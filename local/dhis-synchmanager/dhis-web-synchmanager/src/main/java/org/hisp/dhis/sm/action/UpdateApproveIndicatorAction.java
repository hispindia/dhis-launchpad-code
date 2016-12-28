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
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatus;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatusService;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatus;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRules;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.sm.impl.SynchManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 * 
 */

public class UpdateApproveIndicatorAction
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
    private IndicatorService indicatorService;

    @Autowired
    private SynchInstanceService synchInstanceService;

    @Autowired
    private IndicatorSynchStatusService indicatorSynchStatusService;

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

    private Collection<IndicatorSynchStatus> AllIndicatorsSycnStatus;

    public void setAllIndicatorsSycnStatus( Collection<IndicatorSynchStatus> allIndicatorsSycnStatus )
    {
        AllIndicatorsSycnStatus = allIndicatorsSycnStatus;
    }

    Collection<Integer> instancesLeft;

    public void setInstancesLeft( Collection<Integer> instancesLeft )
    {
        this.instancesLeft = instancesLeft;
    }

    private Indicator indicatorObject;

    public void setIndicatorObject( Indicator indicatorObject )
    {
        this.indicatorObject = indicatorObject;
    }

    private String searchIndId;

    public void setSearchIndId( String searchIndId )
    {
        this.searchIndId = searchIndId;
    }

    Date lastUpdated = new Date();

    StringBuilder updateMessage = new StringBuilder();

    StringBuilder notificationmessage = new StringBuilder();

    Map<SynchInstance, StringBuilder> Notification = new HashMap<SynchInstance, StringBuilder>();

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        try
        {

            HttpServletRequest request = ServletActionContext.getRequest();

            Set<Indicator> newIndicators = new HashSet<Indicator>( indicatorSynchStatusService.getNewIndicators() );
            Collection<IndicatorSynchStatus> updatedIndicators = new ArrayList<IndicatorSynchStatus>(
                indicatorSynchStatusService.getUpdatedIndicatorSyncStatus() );
            Collection<IndicatorSynchStatus> allIndsynchStatus = new ArrayList<IndicatorSynchStatus>(
                indicatorSynchStatusService.getAllIndicatorSynchStatus() );

            AllIndicatorsSycnStatus = new ArrayList<IndicatorSynchStatus>();

            if ( searchIndId != null ) // for individual search
            {
                indicatorObject = indicatorService.getIndicator( Integer.parseInt( searchIndId ) );
                AllIndicatorsSycnStatus
                    .addAll( indicatorSynchStatusService.getSynchStausByIndicator( indicatorObject ) );

                if ( AllIndicatorsSycnStatus.size() > 0 && AllIndicatorsSycnStatus != null ) // updated
                                                                                             // case
                {

                    for ( IndicatorSynchStatus indicatorSynchStatus : AllIndicatorsSycnStatus )
                    {
                        String upapproveNeeded = request.getParameter( "upappNeeded_"
                            + indicatorSynchStatus.getIndicator().getId() );
                        String upapproveStatus = request.getParameter( "upapproveStatus_"
                            + indicatorSynchStatus.getIndicator().getId() );

                        updateIndicators( indicatorSynchStatus, upapproveNeeded, upapproveStatus );
                        System.out.println( "indicator Updated...." );

                    }
                }
                else
                // fresh data element new case
                {

                    String approveNeeded = request.getParameter( "appNeeded_" + indicatorObject.getId() );
                    String approveStatus = request.getParameter( "approveStatus_" + indicatorObject.getId() );
                    String[] instanceIds = request.getParameterValues( "instances_" + indicatorObject.getId() );

                    addNewIndicators( indicatorObject, approveNeeded, approveStatus, instanceIds );
                    System.out.println( "indicator Addedd..." );
                }

                // for last row update

                if ( instancesLeft.size() > 0 && instancesLeft != null )
                {

                    String approveNeeded = request.getParameter( "appNeeded_" + indicatorObject.getId() );
                    String approveStatus = request.getParameter( "approveStatus_" + indicatorObject.getId() );
                    String[] instanceIds = request.getParameterValues( "instances_" + indicatorObject.getId() );

                    addNewIndicators( indicatorObject, approveNeeded, approveStatus, instanceIds );
                }

            }
            else
            // for group option
            {
                // update tab updates
                for ( IndicatorSynchStatus indicatorSynchStatus : updatedIndicators )
                {

                    String upapproveNeeded = request.getParameter( "upappNeeded_"
                        + indicatorSynchStatus.getIndicator().getId() );
                    String upapproveStatus = request.getParameter( "upapproveStatus_"
                        + indicatorSynchStatus.getIndicator().getId() );

                    updateIndicators( indicatorSynchStatus, upapproveNeeded, upapproveStatus );
                }
                // new tab added
                for ( Indicator ind : newIndicators )
                {
                    String approveNeeded = request.getParameter( "appNeeded_" + ind.getId() );
                    String approveStatus = request.getParameter( "approveStatus_" + ind.getId() );
                    String instanceIds[] = request.getParameterValues( "instances_" + ind.getId() );

                    if ( instanceIds == null )
                    {
                        continue;
                    }
                    addNewIndicators( ind, approveNeeded, approveStatus, instanceIds );

                }

                // synched tab added or updated
                for ( IndicatorSynchStatus indicatorSynchStatus : allIndsynchStatus )
                {

                    String approveNeeded = request.getParameter( "syncappNeeded_"
                        + indicatorSynchStatus.getIndicator().getId() );
                    String approveStatus = request.getParameter( "syncapproveStatus_"
                        + indicatorSynchStatus.getIndicator().getId() );
                    String instanceIds[] = request.getParameterValues( "syncinstances_"
                        + indicatorSynchStatus.getIndicator().getId() );

                    if ( instanceIds != null && instanceIds.length > 0 )
                    {

                        for ( String instanceId : instanceIds )
                        {

                            SynchInstance instance = synchInstanceService.getInstance( Integer.parseInt( instanceId ) );

                            IndicatorSynchStatus indSynchStatus = indicatorSynchStatusService
                                .getStatusByInstanceAndIndicator( instance, indicatorSynchStatus.getIndicator() );

                            if ( indSynchStatus == null )
                            {
                                // new
                                String[] newInstance = new String[1];
                                newInstance[0] = instanceId;

                                addNewIndicators( indicatorSynchStatus.getIndicator(), approveNeeded, approveStatus,
                                    newInstance );
                            }
                            else
                            {
                                // update
                                updateIndicators( indicatorSynchStatus, approveNeeded, approveStatus );
                            }

                        }
                    }

                }
            }

            // Notification Message sending code
            for ( SynchInstance instance : Notification.keySet() )
            {

                InstanceBusinessRules bRule = buissnessRuleService.getInstanceRulesByInstance( instance );
                String userGroupId = bRule.getAcceptanceUserGroupUid();

                notificationmessage
                    .append( "The follwing Inidicator is/are approved and waitng for your acceptance : \n" );
                notificationmessage.append( Notification.get( instance ).toString() );

                String NoitificationMessage = "<message xmlns=\"http://dhis2.org/schema/dxf/2.0\">"
                    + "<subject>Notification Message To Accept Approved Indicator</subject>" + "<text> <![CDATA["
                    + notificationmessage.toString() + "]]> </text> " + "<userGroups> " + "<userGroup id=\""
                    + userGroupId + "\"/> </userGroups> </message>";

                synchManager.postMessage( instance.getUrl() + "/messageConversations", NoitificationMessage, instance );

                notificationmessage.setLength( 0 );

            }

            for ( SynchInstance instance : Notification.keySet() )
            {
                updateMessage.append( "The follwing Indicator is/are approved for the instance :" + "<b>"
                    + instance.getName() + "</b><br>" );
                updateMessage.append( Notification.get( instance ).toString() );
            }

            statusMessage = updateMessage.toString().replace( "\n", "<br>" );

            if ( statusMessage.equals( null ) )
            {
                statusMessage = "No Indicator is approved/updated";
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            statusMessage = "Some Error occured in saving data please contact admin";
        }

        return SUCCESS;
    }

    private void addNewIndicators( Indicator ind, String approveNeeded, String approveStatus, String[] instanceIds )
    {

        for ( String instanceId : instanceIds )
        {
            SynchInstance instance = synchInstanceService.getInstance( Integer.parseInt( instanceId ) );

            IndicatorSynchStatus indSynchStatus = indicatorSynchStatusService.getStatusByInstanceAndIndicator(
                instance, ind );

            if ( indSynchStatus == null )
            {
                indSynchStatus = new IndicatorSynchStatus();

                indSynchStatus.setIndicator( ind );
                indSynchStatus.setInstance( instance );

                if ( approveNeeded != null )
                {
                    indSynchStatus.setRememberApproveStatus( true );
                }
                else
                {
                    indSynchStatus.setRememberApproveStatus( false );
                }

                if ( indSynchStatus.getAcceptStatus() != null && indSynchStatus.getAcceptStatus() )
                {
                    indSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_UPDATE );
                }
                else
                {
                    indSynchStatus.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_NEW );
                }

                if ( approveStatus != null )
                {
                    indSynchStatus.setApproveStatus( true );
                    indSynchStatus.setApprovedDate( lastUpdated );
                    indSynchStatus.setStatus( IndicatorSynchStatus.SYNCH_STATUS_APPROVED );
                    // message.append("\n The Indicator "+ind.getName()+" is approved for Instanse "+instance.getName()+" successfully...<br>");
                }
                else
                {
                    indSynchStatus.setApproveStatus( false );
                    indSynchStatus.setApprovedDate( null );
                    indSynchStatus.setStatus( IndicatorSynchStatus.SYNCH_STATUS_SUBMITTED );
                }

                indSynchStatus.setLastUpdated( lastUpdated );

                indicatorSynchStatusService.addIndicatorSynchStatus( indSynchStatus );

                if ( !Notification.containsKey( instance ) )
                {
                    Notification.put( instance, new StringBuilder( ind.getName() + "\n" ) );
                }
                else
                {
                    Notification.get( instance ).append( ind.getName() + "\n" );
                }

            }
            
            // for dependency indicator dataElement
            String deUIDExpressionForIndicators = "+" + ind.getNumerator() + "+" + ind.getDenominator();
            
            List<DataElement> indicatorDataElementList = new ArrayList<>( getDataElementsInExpression( deUIDExpressionForIndicators ) );
            
            if( indicatorDataElementList != null && indicatorDataElementList.size() > 0 )
            {
                for( DataElement indDe : indicatorDataElementList )
                {
                    if( indDe.getOptionSet() != null )
                    {
                        DependencySynchStatus dependencySynchStatusOS = dependencySynchStatusService.getDependencySynchStatuByUID(  instance, indDe.getUid(), indDe.getOptionSet().getUid() );
                        
                        if( dependencySynchStatusOS == null )
                        {
                            dependencySynchStatusOS = new DependencySynchStatus();
                            
                            dependencySynchStatusOS.setInstance( instance );
                            dependencySynchStatusOS.setMetaDataType( DependencySynchStatus.METADATA_TYPE_DATAELEMENT );
                            dependencySynchStatusOS.setMetaDataTypeUID( indDe.getUid() );
                            dependencySynchStatusOS.setDependencyType( DependencySynchStatus.METADATA_DEPENDENCY_TYPE_OPTION_SET );
                            dependencySynchStatusOS.setDependencyTypeUID( indDe.getOptionSet().getUid() );
                            dependencySynchStatusOS.setDependencyTypeLastupdated( indDe.getOptionSet().getLastUpdated() );

                            dependencySynchStatusService.addDependencySynchStatus( dependencySynchStatusOS );
                        }
                        
                    }
                    
                    if( indDe.getCategoryCombo() != null )
                    {
                        DependencySynchStatus dependencySynchStatusDCC = dependencySynchStatusService.getDependencySynchStatuByUID(  instance, indDe.getUid(), indDe.getCategoryCombo().getUid() );
                        
                        if( dependencySynchStatusDCC == null )
                        {
                            dependencySynchStatusDCC =  new DependencySynchStatus();
                            
                            dependencySynchStatusDCC.setInstance( instance );
                            dependencySynchStatusDCC.setMetaDataType( DependencySynchStatus.METADATA_TYPE_DATAELEMENT );
                            dependencySynchStatusDCC.setMetaDataTypeUID( indDe.getUid() );
                            dependencySynchStatusDCC
                                .setDependencyType( DependencySynchStatus.METADATA_DEPENDENCY_TYPE_DATAELEMENT_CATEGORY_COMBO );
                            dependencySynchStatusDCC.setDependencyTypeUID( indDe.getCategoryCombo().getUid() );
                            dependencySynchStatusDCC.setDependencyTypeLastupdated( indDe.getCategoryCombo().getLastUpdated() );

                            dependencySynchStatusService.addDependencySynchStatus( dependencySynchStatusDCC );
                        }
                        
                    }
                    
                }
                
            }

        }
    }

    private void updateIndicators( IndicatorSynchStatus indsync, String upapproveNeeded, String upapproveStatus )
    {

        if ( upapproveNeeded != null )
        {
            indsync.setRememberApproveStatus( true );
        }
        else
        {
            indsync.setRememberApproveStatus( false );
        }

        if ( indsync.getAcceptStatus() != null && indsync.getAcceptStatus() )
        {
            indsync.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_UPDATE );
        }
        else
        {
            indsync.setIndicatorStatus( IndicatorSynchStatus.INDICATOR_STATUS_NEW );
        }

        if ( upapproveStatus != null )
        {
            indsync.setApproveStatus( true );
            indsync.setApprovedDate( lastUpdated );
            indsync.setStatus( IndicatorSynchStatus.SYNCH_STATUS_APPROVED );
            // message.append("\n The Indicator "+Indsync.getIndicator().getName()+" is approved for Instanse id "+Indsync.getInstance().getName()
            // +" successfully...<br>");
        }
        else
        {
            indsync.setApproveStatus( false );
            indsync.setApprovedDate( null );
            indsync.setStatus( IndicatorSynchStatus.SYNCH_STATUS_SUBMITTED );
        }

        indsync.setLastUpdated( lastUpdated );

        indicatorSynchStatusService.updateIndicatorSynchStatus( indsync );
        
        // for dependency indicator dataElement
        String deUIDExpressionForIndicators = "+" + indsync.getIndicator().getNumerator() + "+" + indsync.getIndicator().getDenominator();
        
        List<DataElement> indicatorDataElementList = new ArrayList<>( getDataElementsInExpression( deUIDExpressionForIndicators ) );
        
        if( indicatorDataElementList != null && indicatorDataElementList.size() > 0 )
        {
            for( DataElement indDe : indicatorDataElementList )
            {
                if ( indDe.getOptionSet() != null   )
                {
                    DependencySynchStatus dependencySynchStatusIndOS = dependencySynchStatusService.getDependencySynchStatuByUID(  indsync.getInstance(), indDe.getUid(), indDe.getOptionSet().getUid() );
                    
                    if ( dependencySynchStatusIndOS != null )
                    {
                        dependencySynchStatusIndOS.setDependencyTypeLastupdated( indDe.getOptionSet().getLastUpdated() );
                        dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusIndOS );
                    }
                }
                
                if ( indDe.getCategoryCombo() != null   )
                {
                    DependencySynchStatus dependencySynchStatusIndDCC = dependencySynchStatusService.getDependencySynchStatuByUID( indsync.getInstance(), indDe.getUid(), indDe.getCategoryCombo().getUid() );
                
                    if (  dependencySynchStatusIndDCC != null )
                    {
                        dependencySynchStatusIndDCC.setDependencyTypeLastupdated( indDe.getCategoryCombo().getLastUpdated() );
                        dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusIndDCC );
                    }
                }
            }
            
        }
         
        
         if ( !Notification.containsKey( indsync.getInstance() ) ) {
         Notification.put( indsync.getInstance(), new StringBuilder(
         indsync.getIndicator().getName() + "\n" ) ); } else {
         Notification.get( indsync.getInstance() ).append(
         indsync.getIndicator().getName() + "\n" ); }
         
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
