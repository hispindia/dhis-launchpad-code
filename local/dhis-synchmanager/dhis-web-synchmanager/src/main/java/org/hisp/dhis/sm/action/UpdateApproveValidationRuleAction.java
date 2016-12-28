package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatus;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatusService;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRules;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.sm.impl.SynchManager;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatus;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 * 
 */

public class UpdateApproveValidationRuleAction
    implements Action
{

    final String OPERAND_EXPRESSION = "#\\{(\\w+)\\.?(\\w*)\\}";
    final Pattern OPERAND_PATTERN = Pattern.compile( OPERAND_EXPRESSION );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private ValidationRuleService validationRuleService;

    @Autowired
    private SynchInstanceService synchInstanceService;

    @Autowired
    private ValidationRuleSynchStatusService validationRuleSynchStatusService;

    private InstanceBusinessRulesService buissnessRuleService;

    public void setBuissnessRuleService( InstanceBusinessRulesService buissnessRuleService )
    {
        this.buissnessRuleService = buissnessRuleService;
    }
    
    private DependencySynchStatusService dependencySynchStatusService;
    
    public void setDependencySynchStatusService( DependencySynchStatusService dependencySynchStatusService )
    {
        this.dependencySynchStatusService = dependencySynchStatusService;
    }
    
    private SynchManager synchManager;

    public void setSynchManager( SynchManager synchManager )
    {
        this.synchManager = synchManager;
    }

    // ------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------
    private String statusMessage;

    public String getStatusMessage()
    {
        return statusMessage;
    }

    Collection<Integer> instancesLeft;

    public void setInstancesLeft( Collection<Integer> instancesLeft )
    {
        this.instancesLeft = instancesLeft;
    }

    private Collection<ValidationRuleSynchStatus> allValidationRuleSycnStatus;

    private ValidationRule validationRuleObject;

    private String searchVRId;

    public void setAllValidationRuleSycnStatus( Collection<ValidationRuleSynchStatus> allValidationRuleSycnStatus )
    {
        this.allValidationRuleSycnStatus = allValidationRuleSycnStatus;
    }

    public void setValidationRuleObject( ValidationRule validationRuleObject )
    {
        this.validationRuleObject = validationRuleObject;
    }

    public void setSearchVRId( String searchVRId )
    {
        this.searchVRId = searchVRId;
    }

    Date lastUpdated = new Date();

    StringBuilder updateMessage = new StringBuilder();

    StringBuilder notificationmessage = new StringBuilder();

    Map<SynchInstance, StringBuilder> Notification = new HashMap<SynchInstance, StringBuilder>();

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        try
        {
            HttpServletRequest request = ServletActionContext.getRequest();

            Set<ValidationRule> newValidationRule = new HashSet<ValidationRule>(
                validationRuleSynchStatusService.getValidationRuleList() );
            Collection<ValidationRuleSynchStatus> updatedValidationRule = new ArrayList<ValidationRuleSynchStatus>(
                validationRuleSynchStatusService.getUpdatedValidationRuleSyncStatus() );
            Collection<ValidationRuleSynchStatus> allVRSyncStatus = new ArrayList<ValidationRuleSynchStatus>(
                validationRuleSynchStatusService.getAllValidationRuleSynchStatus() );

            allValidationRuleSycnStatus = new ArrayList<ValidationRuleSynchStatus>();

            if ( searchVRId != null )
            {
                validationRuleObject = validationRuleService.getValidationRule( Integer.parseInt( searchVRId ) );
            }

            // System.out.println("dataElement name :"+deObj.getName());

            if ( searchVRId != null )
            {
                allValidationRuleSycnStatus.addAll( validationRuleSynchStatusService
                    .getSynchStausByValidationRule( validationRuleObject ) );

                System.out.println( "allValidationRuleSycnStatus size :" + allValidationRuleSycnStatus.size() );

                if ( allValidationRuleSycnStatus.size() > 0 && allValidationRuleSycnStatus != null ) // updated
                                                                                                     // case
                {
                    System.out.println( "Inside validationRule if condition" );

                    for ( ValidationRuleSynchStatus validationRuleSynchStatus : allValidationRuleSycnStatus )
                    {
                        String upapproveNeeded = request.getParameter( "upappNeeded_"
                            + validationRuleSynchStatus.getValidationRule().getId() );
                        String upapproveStatus = request.getParameter( "upapproveStatus_"
                            + validationRuleSynchStatus.getValidationRule().getId() );

                        // System.out.println("upapproveNeeded = "+upapproveNeeded+"upapproveStatus = "+upapproveStatus);

                        updateValidationRules( validationRuleSynchStatus, upapproveNeeded, upapproveStatus );
                        System.out.println( " validationRule Updated...." );

                    }
                }
                else
                // fresh data element new case
                {
                    System.out.println( "Else part of  validationRule condition  : " + validationRuleObject.getId() );

                    String approveNeeded = request.getParameter( "appNeeded_" + validationRuleObject.getId() );
                    String approveStatus = request.getParameter( "approveStatus_" + validationRuleObject.getId() );
                    String[] instanceIds = request.getParameterValues( "instances_" + validationRuleObject.getId() );

                    addNewValidationRules( validationRuleObject, approveNeeded, approveStatus, instanceIds );
                    System.out.println( " validationRule Addedd..." );
                }

                // for last row update
                System.out.println( "instancesLeft size in VR : " + instancesLeft.size() );

                if ( instancesLeft.size() > 0 && instancesLeft != null )
                {
                    System.out.println( "Inside validationRule instance left list" );

                    String approveNeeded = request.getParameter( "appNeeded_" + validationRuleObject.getId() );
                    String approveStatus = request.getParameter( "approveStatus_" + validationRuleObject.getId() );
                    String[] instanceIds = request.getParameterValues( "instances_" + validationRuleObject.getId() );

                    addNewValidationRules( validationRuleObject, approveNeeded, approveStatus, instanceIds );
                    System.out.println( " validationRule Addedd..." );
                }

            }
            else
            { // for new tab
                for ( ValidationRuleSynchStatus validationRuleSynchStatus : updatedValidationRule )
                {

                    String upapproveNeeded = request.getParameter( "upappNeeded_"
                        + validationRuleSynchStatus.getValidationRule().getId() );
                    String upapproveStatus = request.getParameter( "upapproveStatus_"
                        + validationRuleSynchStatus.getValidationRule().getId() );

                    updateValidationRules( validationRuleSynchStatus, upapproveNeeded, upapproveStatus );
                }
                // for update tab
                for ( ValidationRule validationRule : newValidationRule )
                {
                    String approveNeeded = request.getParameter( "appNeeded_" + validationRule.getId() );
                    String approveStatus = request.getParameter( "approveStatus_" + validationRule.getId() );
                    String instanceIds[] = request.getParameterValues( "instances_" + validationRule.getId() );

                    if ( instanceIds == null )
                    {
                        continue;
                    }
                    addNewValidationRules( validationRule, approveNeeded, approveStatus, instanceIds );
                }

                // for synched tab

                for ( ValidationRuleSynchStatus vrSynchStatus : allVRSyncStatus )
                {

                    String approveNeeded = request.getParameter( "syncappNeeded_"
                        + vrSynchStatus.getValidationRule().getId() );
                    String approveStatus = request.getParameter( "syncapproveStatus_"
                        + vrSynchStatus.getValidationRule().getId() );
                    String instanceIds[] = request.getParameterValues( "syncinstances_"
                        + vrSynchStatus.getValidationRule().getId() );

                    if ( instanceIds != null && instanceIds.length > 0 )
                    {

                        for ( String instanceId : instanceIds )
                        {

                            SynchInstance instance = synchInstanceService.getInstance( Integer.parseInt( instanceId ) );

                            ValidationRuleSynchStatus validationRuleSynchStatus = validationRuleSynchStatusService
                                .getStatusByInstanceAndValidationRule( instance, vrSynchStatus.getValidationRule() );

                            if ( validationRuleSynchStatus == null )
                            {
                                // new
                                String[] newInstance = new String[1];
                                newInstance[0] = instanceId;

                                addNewValidationRules( vrSynchStatus.getValidationRule(), approveNeeded, approveStatus,
                                    newInstance );
                            }
                            else
                            {
                                // update
                                updateValidationRules( vrSynchStatus, approveNeeded, approveStatus );
                            }

                        }
                    }
                }

            }

            for ( SynchInstance instance : Notification.keySet() )
            {

                InstanceBusinessRules bRule = buissnessRuleService.getInstanceRulesByInstance( instance );
                String userGroupId = bRule.getAcceptanceUserGroupUid();

                notificationmessage
                    .append( "The follwing Validation rule Unit is/are approved and waitng for your acceptance : \n" );
                notificationmessage.append( Notification.get( instance ).toString() );

                String NoitificationMessage = "<message xmlns=\"http://dhis2.org/schema/dxf/2.0\">"
                    + "<subject>Notification Message To Accept Approved Validation Rule</subject>" + "<text> <![CDATA["
                    + notificationmessage.toString() + "]]> </text> " + "<userGroups> " + "<userGroup id=\""
                    + userGroupId + "\"/> </userGroups> </message>";

                synchManager.postMessage( instance.getUrl() + "/messageConversations", NoitificationMessage, instance );
                notificationmessage.setLength( 0 );
            }

            for ( SynchInstance instance : Notification.keySet() )
            {
                updateMessage.append( "The follwing Validation Rule is/are approved for the instance : " + "<b>"
                    + instance.getName() + "</b><br>" );
                updateMessage.append( Notification.get( instance ).toString() );
            }

            statusMessage = updateMessage.toString().replace( "\n", "<br>" );

            if ( statusMessage.equals( "" ) )
            {
                statusMessage = "No Validation Rule is approved/updated";
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            statusMessage = "Some Error occured in saving please contact admin";
        }

        return SUCCESS;
    }

    private void addNewValidationRules( ValidationRule VrRule, String approveNeeded, String approveStatus,
        String[] instanceIds )
    {

        for ( String instanceId : instanceIds )
        {
            SynchInstance instance = synchInstanceService.getInstance( Integer.parseInt( instanceId ) );

            ValidationRuleSynchStatus vRSynchStatus = validationRuleSynchStatusService
                .getStatusByInstanceAndValidationRule( instance, VrRule );

            if ( vRSynchStatus == null )
            {
                vRSynchStatus = new ValidationRuleSynchStatus();

                vRSynchStatus.setValidationRule( VrRule );
                vRSynchStatus.setInstance( instance );

                if ( vRSynchStatus.getAcceptStatus() != null && vRSynchStatus.getAcceptStatus() )
                {
                    vRSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_UPDATE );
                }
                else
                {
                    vRSynchStatus.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_NEW );
                }

                if ( approveNeeded != null )
                {
                    vRSynchStatus.setRememberApproveStatus( true );
                }
                else
                {
                    vRSynchStatus.setRememberApproveStatus( false );
                }

                if ( approveStatus != null )
                {
                    vRSynchStatus.setApproveStatus( true );
                    vRSynchStatus.setApprovedDate( lastUpdated );
                    vRSynchStatus.setStatus( ValidationRuleSynchStatus.SYNCH_STATUS_APPROVED );
                    // message.append("\n The Validation Rule "+VrRule.getName()+" is approved for Instanse "+instance.getName()
                    // +" successfully...<br>");
                }
                else
                {
                    vRSynchStatus.setApproveStatus( false );
                    vRSynchStatus.setApprovedDate( null );
                    vRSynchStatus.setStatus( ValidationRuleSynchStatus.SYNCH_STATUS_SUBMITTED );
                }

                vRSynchStatus.setLastUpdated( lastUpdated );

                // vRSynchStatus.setValidationRuleStatus(
                // ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_NEW );

                validationRuleSynchStatusService.addValidationRuleSynchStatus( vRSynchStatus );

                if ( !Notification.containsKey( instance ) )
                {
                    Notification.put( instance, new StringBuilder( VrRule.getName() + "\n" ) );
                }
                else
                {
                    Notification.get( instance ).append( VrRule.getName() + "\n" );
                }
            }
            
            
            // for dependency validation rule dataElement
            String deUIDExpressionForValidationRules = "+" + VrRule.getLeftSide() + "+" + VrRule.getRightSide();
            
            List<DataElement> validationRuleDataElementList = new ArrayList<>( getDataElementsInExpression( deUIDExpressionForValidationRules ) );
            
            if( validationRuleDataElementList != null && validationRuleDataElementList.size() > 0 )
            {
                for( DataElement vrDe : validationRuleDataElementList )
                {
                    if( vrDe.getOptionSet() != null )
                    {
                        DependencySynchStatus dependencySynchStatusOS = dependencySynchStatusService.getDependencySynchStatuByUID(  instance, vrDe.getUid(), vrDe.getOptionSet().getUid() );
                        
                        if( dependencySynchStatusOS == null )
                        {
                            dependencySynchStatusOS = new DependencySynchStatus();
                            
                            dependencySynchStatusOS.setInstance( instance );
                            dependencySynchStatusOS.setMetaDataType( DependencySynchStatus.METADATA_TYPE_DATAELEMENT );
                            dependencySynchStatusOS.setMetaDataTypeUID( vrDe.getUid() );
                            dependencySynchStatusOS.setDependencyType( DependencySynchStatus.METADATA_DEPENDENCY_TYPE_OPTION_SET );
                            dependencySynchStatusOS.setDependencyTypeUID( vrDe.getOptionSet().getUid() );
                            dependencySynchStatusOS.setDependencyTypeLastupdated( vrDe.getOptionSet().getLastUpdated() );

                            dependencySynchStatusService.addDependencySynchStatus( dependencySynchStatusOS );
                        }
                        
                    }
                    
                    if( vrDe.getCategoryCombo() != null )
                    {
                        DependencySynchStatus dependencySynchStatusDCC = dependencySynchStatusService.getDependencySynchStatuByUID(  instance, vrDe.getUid(), vrDe.getCategoryCombo().getUid() );
                        
                        if( dependencySynchStatusDCC == null )
                        {
                            dependencySynchStatusDCC =  new DependencySynchStatus();
                            
                            dependencySynchStatusDCC.setInstance( instance );
                            dependencySynchStatusDCC.setMetaDataType( DependencySynchStatus.METADATA_TYPE_DATAELEMENT );
                            dependencySynchStatusDCC.setMetaDataTypeUID( vrDe.getUid() );
                            dependencySynchStatusDCC
                                .setDependencyType( DependencySynchStatus.METADATA_DEPENDENCY_TYPE_DATAELEMENT_CATEGORY_COMBO );
                            dependencySynchStatusDCC.setDependencyTypeUID( vrDe.getCategoryCombo().getUid() );
                            dependencySynchStatusDCC.setDependencyTypeLastupdated( vrDe.getCategoryCombo().getLastUpdated() );

                            dependencySynchStatusService.addDependencySynchStatus( dependencySynchStatusDCC );
                        }
                        
                    }
                    
                }
                
            }
            
        }
    }

    private void updateValidationRules( ValidationRuleSynchStatus vRsync, String upapproveNeeded, String upapproveStatus )
    {

        if ( upapproveNeeded != null )
        {
            vRsync.setRememberApproveStatus( true );
        }
        else
        {
            vRsync.setRememberApproveStatus( false );
        }

        if ( vRsync.getAcceptStatus() != null && vRsync.getAcceptStatus() )
        {
            vRsync.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_UPDATE );
        }
        else
        {
            vRsync.setValidationRuleStatus( ValidationRuleSynchStatus.VALIDATIONRULE_STATUS_NEW );
        }

        if ( upapproveStatus != null )
        {
            vRsync.setApproveStatus( true );
            vRsync.setApprovedDate( lastUpdated );
            vRsync.setStatus( ValidationRuleSynchStatus.SYNCH_STATUS_APPROVED );
        }
        else
        {
            vRsync.setApproveStatus( false );
            vRsync.setApprovedDate( null );
            vRsync.setStatus( ValidationRuleSynchStatus.SYNCH_STATUS_SUBMITTED );
        }

        vRsync.setLastUpdated( lastUpdated );

        validationRuleSynchStatusService.updateValidationRuleSynchStatus( vRsync );

        
          if ( !Notification.containsKey( vRsync.getInstance() ) ) {
          Notification.put( vRsync.getInstance(), new StringBuilder(
          vRsync.getValidationRule().getName() + "\n" ) ); } else {
          Notification.get( vRsync.getInstance() ).append(
          vRsync.getValidationRule().getName() + "\n" ); }
        
                
        // for dependency validation rule dataElement
        String deUIDExpressionForValidationRules = "+" + vRsync.getValidationRule().getLeftSide() + "+" + vRsync.getValidationRule().getRightSide();
        
        List<DataElement> validationRuleDataElementList = new ArrayList<>( getDataElementsInExpression( deUIDExpressionForValidationRules ) );
        
        if( validationRuleDataElementList != null && validationRuleDataElementList.size() > 0 )
        {
            for( DataElement vrDe : validationRuleDataElementList )
            {
                if ( vrDe.getOptionSet() != null   )
                {
                    DependencySynchStatus dependencySynchStatusIndOS = dependencySynchStatusService.getDependencySynchStatuByUID(  vRsync.getInstance(), vrDe.getUid(), vrDe.getOptionSet().getUid() );
                    
                    if ( dependencySynchStatusIndOS != null )
                    {
                        dependencySynchStatusIndOS.setDependencyTypeLastupdated( vrDe.getOptionSet().getLastUpdated() );
                        dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusIndOS );
                    }
                }
                
                if ( vrDe.getCategoryCombo() != null   )
                {
                    DependencySynchStatus dependencySynchStatusIndDCC = dependencySynchStatusService.getDependencySynchStatuByUID( vRsync.getInstance(), vrDe.getUid(), vrDe.getCategoryCombo().getUid() );
                
                    if (  dependencySynchStatusIndDCC != null )
                    {
                        dependencySynchStatusIndDCC.setDependencyTypeLastupdated( vrDe.getCategoryCombo().getLastUpdated() );
                        dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusIndDCC );
                    }
                }
            }
            
        }
    }
    
    // find dataElement from Expression   
    public Set<DataElement> getDataElementsInExpression( String expression )
    {
        Set<DataElement> dataElementsInExpression = null;

        if ( expression != null )
        {
            dataElementsInExpression = new HashSet<>();

            final Matcher matcher = OPERAND_PATTERN.matcher( expression );

            while ( matcher.find() )
            {
                String deUID =  matcher.group( 1 );
                
                DataElement dataElement = dataElementService.getDataElement( deUID );
                
                dataElementsInExpression.add( dataElement );
                
            }
        }
        return dataElementsInExpression;
    }
    
    
    
    
    
}
