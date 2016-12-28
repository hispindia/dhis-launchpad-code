package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatus;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatus;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatusService;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatus;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRules;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.sm.impl.SynchManager;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 * 
 */

public class UpdateApproveDEStatusAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private UserService userService;

    @Autowired
    private SynchInstanceService synchInstanceService;

    @Autowired
    private DataElementSynchStatusService dataElementSynchStatusService;

    private DependencySynchStatusService dependencySynchStatusService;

    public void setDependencySynchStatusService( DependencySynchStatusService dependencySynchStatusService )
    {
        this.dependencySynchStatusService = dependencySynchStatusService;
    }

    private InstanceBusinessRulesService buissnessRuleService;

    public void setBuissnessRuleService( InstanceBusinessRulesService buissnessRuleService )
    {
        this.buissnessRuleService = buissnessRuleService;
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

    Collection<DataElementSynchStatus> AllDataElementsSycnStatus;

    Collection<Integer> instancesLeft;

    public void setAllDataElementsSycnStatus( Collection<DataElementSynchStatus> allDataElementsSycnStatus )
    {
        AllDataElementsSycnStatus = allDataElementsSycnStatus;
    }

    public void setInstancesLeft( Collection<Integer> instancesLeft )
    {
        this.instancesLeft = instancesLeft;
    }

    private String searchDeId;

    public void setSearchDeId( String searchDeId )
    {
        this.searchDeId = searchDeId;
    }

    DataElement deObj;

    public void setDeObj( DataElement deObj )
    {
        this.deObj = deObj;
    }

    Date lastUpdated = new Date();

    private StringBuilder updateMessage = new StringBuilder(); // displayed on
                                                               // success page

    private StringBuilder notificationmessage = new StringBuilder(); // sent as
                                                                     // message

    private Map<SynchInstance, StringBuilder> MessageMap = new HashMap<SynchInstance, StringBuilder>();

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        try
        {

            HttpServletRequest request = ServletActionContext.getRequest();

            Set<DataElement> newdataElements = new HashSet<DataElement>(
                dataElementSynchStatusService.getNewDataElements() );
            Collection<DataElementSynchStatus> updateDataElements = new ArrayList<DataElementSynchStatus>(
                dataElementSynchStatusService.getUpdatedDataElementSyncStatus() );
            Collection<DataElementSynchStatus> allsynchStatus = new ArrayList<DataElementSynchStatus>(
                dataElementSynchStatusService.getAllDataElementSynchStatus() );

            AllDataElementsSycnStatus = new ArrayList<DataElementSynchStatus>();

            if ( searchDeId != null ) // for individual search new/update
            {
                deObj = dataElementService.getDataElement( Integer.parseInt( searchDeId ) );
                AllDataElementsSycnStatus.addAll( dataElementSynchStatusService.getStatusByDataElement( deObj ) );

                if ( AllDataElementsSycnStatus.size() > 0 && AllDataElementsSycnStatus != null ) // updated
                {
                    for ( DataElementSynchStatus dataElementSynchStatus : AllDataElementsSycnStatus )
                    {
                        String upapproveNeeded = request.getParameter( "upappNeeded_"+ dataElementSynchStatus.getDataElement().getId() );
                        String upapproveStatus = request.getParameter( "upapproveStatus_"+ dataElementSynchStatus.getDataElement().getId() );

                        if(upapproveStatus != null){
                            updateDataElements( dataElementSynchStatus, upapproveNeeded, upapproveStatus );
                        }
                    }
                }
                else
                // fresh data element new case
                {
                    String approveNeeded = request.getParameter( "appNeeded_" + deObj.getId() );
                    String approveStatus = request.getParameter( "approveStatus_" + deObj.getId() );
                    String[] instanceIds = request.getParameterValues( "instances_" + deObj.getId() );

                    if(approveStatus != null){
                        addNewDataElements( deObj, approveNeeded, approveStatus, instanceIds );
                    }
                }

                // for last row update

                if ( instancesLeft.size() > 0 && instancesLeft != null )
                {

                    String approveNeeded = request.getParameter( "appNeeded_" + deObj.getId() );
                    String approveStatus = request.getParameter( "approveStatus_" + deObj.getId() );
                    String[] instanceIds = request.getParameterValues( "instances_" + deObj.getId() );

                    if(approveStatus != null){
                        addNewDataElements( deObj, approveNeeded, approveStatus, instanceIds );
                    }
                }

            }
            else
            // for group search
            { // for update tab
                String[] allInstanceIds = null;
                allInstanceIds = request.getParameterValues( "deAllInstaces" );

                for ( DataElementSynchStatus dataElementSynchStatus : updateDataElements )
                {
                    String upapproveNeeded = request.getParameter( "upappNeeded_"+ dataElementSynchStatus.getDataElement().getId() );
                    String upapproveStatus = request.getParameter( "upapproveStatus_"+ dataElementSynchStatus.getDataElement().getId() );

                    if(upapproveStatus != null){
                        updateDataElements( dataElementSynchStatus, upapproveNeeded, upapproveStatus );
                    }
                }
                // for new tab
                for ( DataElement de : newdataElements ) {
                    String approveNeeded = request.getParameter( "appNeeded_" + de.getId());
                    String approveStatus = request.getParameter( "approveStatus_" + de.getId() );

                    String instanceIds[] = null;

                    if( allInstanceIds != null )
                    {
                        instanceIds = allInstanceIds;
                    }
                    else
                    {
                        instanceIds = request.getParameterValues( "instances_" + de.getId() );
                    }

                  //   System.out.println(de.getName() + "---" + instanceIds);

                    if ( instanceIds == null )
                    {
                        continue;
                    }
                    if(approveStatus != null){
                        addNewDataElements( de, approveNeeded, approveStatus, instanceIds );
                    }
                }

                // for synched tab


                String[] sync_allInstanceIds = null;
                sync_allInstanceIds = request.getParameterValues( "Sync_deAllInstaces" );

                for ( DataElementSynchStatus dataElementsynchStatus : allsynchStatus ) {

                    String approveNeeded = request.getParameter("syncappNeeded_"
                            + dataElementsynchStatus.getDataElement().getId());
                    String approveStatus = request.getParameter("syncapproveStatus_"
                            + dataElementsynchStatus.getDataElement().getId());

                    String instanceIds[] = null;

                    if( allInstanceIds != null )
                    {
                        instanceIds = sync_allInstanceIds;
                    }
                    else
                    {
                        instanceIds = request.getParameterValues( "syncinstances_"
                                + dataElementsynchStatus.getDataElement().getId() );
                    }

                   /* String instanceIds[] = request.getParameterValues( "syncinstances_"
                        + dataElementsynchStatus.getDataElement().getId() );*/

                    // System.out.println("--------------------Synched tab test --------------------------------");

                    if ( instanceIds != null && instanceIds.length > 0 )
                    {

                        for ( String instanceId : instanceIds )
                        {

                            SynchInstance instance = synchInstanceService.getInstance( Integer.parseInt( instanceId ) );

                            DataElementSynchStatus deSynchStatus = dataElementSynchStatusService
                                .getStatusByInstanceAndDataElement( instance, dataElementsynchStatus.getDataElement() );

                            if ( deSynchStatus == null )
                            {
                                // new
                                String[] newInstance = new String[1];
                                newInstance[0] = instanceId;
                                if(approveStatus != null ){
                                    addNewDataElements( dataElementsynchStatus.getDataElement(), approveNeeded,
                                            approveStatus, newInstance );
                                }
                            }
                            else
                            {
                                // update
                                if(approveStatus != null ){
                                    updateDataElements( dataElementsynchStatus, approveNeeded, approveStatus );
                                }
                            }
                        }
                    }
                }

            }

            // Notification Message sending code
            for ( SynchInstance instance : MessageMap.keySet() )
            {
                InstanceBusinessRules bRule = buissnessRuleService.getInstanceRulesByInstance( instance );
                String userGroupId = bRule.getAcceptanceUserGroupUid();

                notificationmessage
                    .append( "The follwing dataElement is/are approved and waitng for your acceptance : \n" );
                notificationmessage.append( MessageMap.get( instance ).toString() );

                String NoitificationMessage = "<message xmlns=\"http://dhis2.org/schema/dxf/2.0\">"
                    + "<subject>Notification Message To Accept Approved DataElement</subject>" + "<text> <![CDATA["
                    + notificationmessage.toString() + "]]> </text> " + "<userGroups> " + "<userGroup id=\""
                    + userGroupId + "\"/> </userGroups> </message>"; // user id

                synchManager.postMessage( instance.getUrl() + "/messageConversations", NoitificationMessage, instance );

                notificationmessage.setLength( 0 );
            }

            // success message creation

            for ( SynchInstance instance : MessageMap.keySet() )
            {
                updateMessage.append( "The follwing Data Elemenet is/are approved for the instance : " + "<b>"
                    + instance.getName() + "</b><br>" );
                updateMessage.append( MessageMap.get( instance ).toString() );
            }

            // validation message content
            statusMessage = updateMessage.toString().replace( "\n", "<br>" );

            if ( statusMessage.equals( "" ) )
            {
                statusMessage = "No DataElement is approved/updated";
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
            statusMessage = "Some Error occured in saving please contact admin";
        }

        return SUCCESS;
    }

    private void addNewDataElements( DataElement de, String approveNeeded, String approveStatus, String[] instanceIds )
    {
        for ( String instanceId : instanceIds )
        {
            SynchInstance instance = synchInstanceService.getInstance( Integer.parseInt( instanceId ) );

            DataElementSynchStatus deSynchStatus = dataElementSynchStatusService.getStatusByInstanceAndDataElement(
                instance, de );

            if ( deSynchStatus == null )
            {
                deSynchStatus = new DataElementSynchStatus();

                deSynchStatus.setDataElement( de );
                deSynchStatus.setInstance( instance );

                if ( approveNeeded != null && approveNeeded.equalsIgnoreCase("on") )
                {
                    deSynchStatus.setRememberApproveStatus( true );
                }
                else
                {
                    deSynchStatus.setRememberApproveStatus( false );
                }

                if ( deSynchStatus.getAcceptStatus() != null && deSynchStatus.getAcceptStatus() )
                {
                    deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_UPDATE );
                }
                else
                {
                    deSynchStatus.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_NEW );
                }

                if ( approveStatus != null && approveStatus.equalsIgnoreCase("on"))
                {
                    deSynchStatus.setApproveStatus( true );
                    deSynchStatus.setApprovedDate( lastUpdated );
                    deSynchStatus.setStatus( DataElementSynchStatus.SYNCH_STATUS_APPROVED );
                }
                else
                {
                    deSynchStatus.setApproveStatus( false );
                    deSynchStatus.setApprovedDate( null );
                    deSynchStatus.setStatus( DataElementSynchStatus.SYNCH_STATUS_SUBMITTED );
                }

                deSynchStatus.setLastUpdated( lastUpdated );

                dataElementSynchStatusService.addDataElementSynchStatus( deSynchStatus );

                if ( !MessageMap.containsKey( instance ) )
                {
                    MessageMap.put( instance, new StringBuilder( de.getName() + "\n" ) );
                }
                else
                {
                    MessageMap.get( instance ).append( de.getName() + "\n" );
                }
            }
            // Dependency De new synchstatus
            if ( de.getOptionSet() != null )
            {
                DependencySynchStatus dependencySynchStatusOS = dependencySynchStatusService
                    .getDependencySynchStatuByUID( instance, de.getUid(), de.getOptionSet().getUid() );

                if ( dependencySynchStatusOS == null )
                {
                    dependencySynchStatusOS = new DependencySynchStatus();

                    dependencySynchStatusOS.setInstance( instance );
                    dependencySynchStatusOS.setMetaDataType( DependencySynchStatus.METADATA_TYPE_DATAELEMENT );
                    dependencySynchStatusOS.setMetaDataTypeUID( de.getUid() );
                    dependencySynchStatusOS
                        .setDependencyType( DependencySynchStatus.METADATA_DEPENDENCY_TYPE_OPTION_SET );
                    dependencySynchStatusOS.setDependencyTypeUID( de.getOptionSet().getUid() );
                    dependencySynchStatusOS.setDependencyTypeLastupdated( de.getOptionSet().getLastUpdated() );

                    dependencySynchStatusService.addDependencySynchStatus( dependencySynchStatusOS );
                }

            }

            if ( de.getCategoryCombo() != null )
            {
                DependencySynchStatus dependencySynchStatusDCC = dependencySynchStatusService
                    .getDependencySynchStatuByUID( instance, de.getUid(), de.getCategoryCombo().getUid() );

                if ( dependencySynchStatusDCC == null )
                {
                    dependencySynchStatusDCC = new DependencySynchStatus();

                    dependencySynchStatusDCC.setInstance( instance );
                    dependencySynchStatusDCC.setMetaDataType( DependencySynchStatus.METADATA_TYPE_DATAELEMENT );
                    dependencySynchStatusDCC.setMetaDataTypeUID( de.getUid() );
                    dependencySynchStatusDCC
                        .setDependencyType( DependencySynchStatus.METADATA_DEPENDENCY_TYPE_DATAELEMENT_CATEGORY_COMBO );
                    dependencySynchStatusDCC.setDependencyTypeUID( de.getCategoryCombo().getUid() );
                    dependencySynchStatusDCC.setDependencyTypeLastupdated( de.getCategoryCombo().getLastUpdated() );

                    dependencySynchStatusService.addDependencySynchStatus( dependencySynchStatusDCC );
                }

            }

        }

    }

    private void updateDataElements( DataElementSynchStatus desync, String upapproveNeeded, String upapproveStatus )
    {

        if ( upapproveNeeded != null )
        {
            desync.setRememberApproveStatus( true );
        }
        else
        {
            desync.setRememberApproveStatus( false );
        }

        if ( desync.getAcceptStatus() != null && desync.getAcceptStatus() )
        {
            desync.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_UPDATE );
        }
        else
        {
            desync.setDeStatus( DataElementSynchStatus.DATAELEMENT_STATUS_NEW );
        }
        
        /*
        if ( upapproveStatus != null )
        {
            if ( desync.getAcceptStatus() != null && desync.getAcceptStatus() )
            {

            }
            else
            {
                desync.setApproveStatus( true );
                desync.setApprovedDate( lastUpdated );
                desync.setStatus( DataElementSynchStatus.SYNCH_STATUS_APPROVED );
            }
        }
        else
        {
            desync.setApproveStatus( false );
            desync.setApprovedDate( null );
            desync.setStatus( DataElementSynchStatus.SYNCH_STATUS_SUBMITTED );
        }
        */
        
        if ( upapproveStatus != null )
        {
            desync.setApproveStatus( true );
            desync.setApprovedDate( lastUpdated );
            desync.setStatus( IndicatorSynchStatus.SYNCH_STATUS_APPROVED );
        }
        else
        {
            desync.setApproveStatus( false );
            desync.setApprovedDate( null );
            desync.setStatus( IndicatorSynchStatus.SYNCH_STATUS_SUBMITTED );
        }
        
        desync.setLastUpdated( lastUpdated );

        dataElementSynchStatusService.updateDataElementSynchStatus( desync );

        // Dependancy De update syncstatus

        if ( desync.getDataElement().getOptionSet() != null )
        {
            DependencySynchStatus dependencySynchStatusOS = dependencySynchStatusService
                .getDependencySynchStatuByUID( desync.getInstance(), desync.getDataElement().getUid(), desync
                    .getDataElement().getOptionSet().getUid() );

            if ( dependencySynchStatusOS != null )
            {
                dependencySynchStatusOS.setDependencyTypeLastupdated( desync.getDataElement().getOptionSet()
                    .getLastUpdated() );
                dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusOS );
            }
        }

        if ( desync.getDataElement().getCategoryCombo() != null )
        {
            DependencySynchStatus dependencySynchStatusDCC = dependencySynchStatusService.getDependencySynchStatuByUID(
                desync.getInstance(), desync.getDataElement().getUid(), desync.getDataElement().getCategoryCombo()
                    .getUid() );

            if ( dependencySynchStatusDCC != null )
            {
                dependencySynchStatusDCC.setDependencyTypeLastupdated( desync.getDataElement().getCategoryCombo()
                    .getLastUpdated() );
                dependencySynchStatusService.updateDependencySynchStatus( dependencySynchStatusDCC );
            }
        }

        if ( !MessageMap.containsKey( desync.getInstance() ) )
        {
            MessageMap.put( desync.getInstance(), new StringBuilder( desync.getDataElement().getName() + "\n" ) );
        }
        else
        {
            MessageMap.get( desync.getInstance() ).append( desync.getDataElement().getName() + "\n" );
        }

    }
}
