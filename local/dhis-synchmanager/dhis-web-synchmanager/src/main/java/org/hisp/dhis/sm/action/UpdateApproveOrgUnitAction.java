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
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRules;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatus;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.sm.impl.SynchManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 * 
 */

public class UpdateApproveOrgUnitAction
implements Action
{
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	@Autowired
	private OrganisationUnitService organisationUnitService;

	@Autowired
	private SynchInstanceService synchInstanceService;

	@Autowired
	private OrganisationUnitSynchStatusService organisationUnitSynchStatusService;

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

	Collection<Integer> instancesLeft;

	public void setInstancesLeft(Collection<Integer> instancesLeft) {
		this.instancesLeft = instancesLeft;
	}

	private Collection<OrganisationUnitSynchStatus> AllOrgUnitSycnStatus;
	private OrganisationUnit orgUnitObject;
	private String searchOrgId;

	public void setAllOrgUnitSycnStatus(
			Collection<OrganisationUnitSynchStatus> allOrgUnitSycnStatus) {
		AllOrgUnitSycnStatus = allOrgUnitSycnStatus;
	}

	public void setOrgUnitObject(OrganisationUnit orgUnitObject) {
		this.orgUnitObject = orgUnitObject;
	}

	public void setSearchOrgId(String searchOrgId) {
		this.searchOrgId = searchOrgId;
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

			Set<OrganisationUnit> newOrgUnits = new HashSet<OrganisationUnit>(
					organisationUnitSynchStatusService.getNewOrganisationUnits() );
			Collection<OrganisationUnitSynchStatus> updatedOrgUnits = new ArrayList<OrganisationUnitSynchStatus>(
					organisationUnitSynchStatusService.getUpdatedOrganisationUnitSyncStatus() );
			Collection<OrganisationUnitSynchStatus> allorgSyncStatus = new ArrayList<OrganisationUnitSynchStatus>(
					organisationUnitSynchStatusService.getAllOrganisationUnitSynchStatus() );

			AllOrgUnitSycnStatus = new  ArrayList<OrganisationUnitSynchStatus>();

			if(searchOrgId != null){
				orgUnitObject = organisationUnitService.getOrganisationUnit(Integer.parseInt(searchOrgId))	;	
			}

			if(searchOrgId != null)
			{
				AllOrgUnitSycnStatus.addAll(organisationUnitSynchStatusService.getSynchStausByOrganisationUnit(orgUnitObject));
				System.out.println("AllOrgUnitSycnStatus size :"+AllOrgUnitSycnStatus.size());

				if(AllOrgUnitSycnStatus.size() >0 && AllOrgUnitSycnStatus != null) //updated case
				{
					System.out.println("Inside orgunit if condition");
					for (OrganisationUnitSynchStatus orgUnitSynchStatus : AllOrgUnitSycnStatus) 
					{
						String upapproveNeeded = request.getParameter( "upappNeeded_" + orgUnitSynchStatus.getOrganisationUnit().getId() );
						String upapproveStatus = request.getParameter( "upapproveStatus_" + orgUnitSynchStatus.getOrganisationUnit().getId() );

						updateOrgUnits( orgUnitSynchStatus, upapproveNeeded,upapproveStatus);
						System.out.println(" orgunit Updated....");

					}
				}
				else // fresh data element new case
				{
					System.out.println("Else part of orgunit condition  : "+orgUnitObject.getId());

					String approveNeeded = request.getParameter( "appNeeded_" + orgUnitObject.getId() );
					String approveStatus = request.getParameter( "approveStatus_" + orgUnitObject.getId() );
					String[] instanceIds = request.getParameterValues( "instances_" + orgUnitObject.getId() );

					addNewOrgUnits(orgUnitObject, approveNeeded, approveStatus, instanceIds);
					System.out.println(" orgunit Addedd...");
				}

				//for last row update
				System.out.println("instancesLeft size in ORG : "+instancesLeft.size());
				if(instancesLeft.size() > 0 && instancesLeft != null){
					System.out.println("Inside orgunit instance left list");

					String approveNeeded = request.getParameter( "appNeeded_" + orgUnitObject.getId() );
					String approveStatus = request.getParameter( "approveStatus_" + orgUnitObject.getId() );
					String[] instanceIds = request.getParameterValues( "instances_" + orgUnitObject.getId() );

					addNewOrgUnits(orgUnitObject, approveNeeded, approveStatus, instanceIds);
					System.out.println(" orgunit Addedd...");
				}

			}
			else
			{	//for update tab add
				for (OrganisationUnitSynchStatus orgUnitSynchStatus : updatedOrgUnits) {

					String upapproveNeeded = request.getParameter( "upappNeeded_" + orgUnitSynchStatus.getOrganisationUnit().getId() );
					String upapproveStatus = request.getParameter( "upapproveStatus_" + orgUnitSynchStatus.getOrganisationUnit().getId() );

					updateOrgUnits( orgUnitSynchStatus, upapproveNeeded,upapproveStatus);
				}

				//for new tab
				for (OrganisationUnit orgUnit : newOrgUnits) {
					String approveNeeded = request.getParameter( "appNeeded_" + orgUnit.getId() );
					String approveStatus = request.getParameter( "approveStatus_" + orgUnit.getId() );
					String instanceIds[] = request.getParameterValues( "instances_" + orgUnit.getId() );

					if(instanceIds == null){
						continue;
					}
					addNewOrgUnits(orgUnit,approveNeeded,approveStatus,instanceIds );
				}

				//for synched tab

				for (OrganisationUnitSynchStatus orgSynchStatus : allorgSyncStatus) {

					String approveNeeded = request.getParameter( "syncappNeeded_" + orgSynchStatus.getOrganisationUnit().getId() );
					String approveStatus = request.getParameter( "syncapproveStatus_" + orgSynchStatus.getOrganisationUnit().getId() );
					String instanceIds[] = request.getParameterValues( "syncinstances_" + orgSynchStatus.getOrganisationUnit().getId() );

					if(instanceIds != null && instanceIds.length > 0){

						for (String instanceId : instanceIds) {

							SynchInstance instance = synchInstanceService.getInstance( Integer.parseInt( instanceId ) );

							OrganisationUnitSynchStatus orgUnitSynchStatus = organisationUnitSynchStatusService
									.getStatusByInstanceAndOrganisationUnit(instance, orgSynchStatus.getOrganisationUnit());

							if ( orgUnitSynchStatus == null )
							{
								//new
								String[] newInstance = new String[1];
								newInstance[0]=instanceId;

								addNewOrgUnits(orgSynchStatus.getOrganisationUnit(),approveNeeded,approveStatus,newInstance);
							}
							else
							{
								//update
								updateOrgUnits(orgSynchStatus,approveNeeded,approveStatus);
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
				.append( "The follwing Organisation Unit is/are approved and waitng for your acceptance : \n" );
				notificationmessage.append( Notification.get( instance ).toString() );

				String NoitificationMessage = "<message xmlns=\"http://dhis2.org/schema/dxf/2.0\">"
						+ "<subject>Notification Message To Accept Approved Organisation Unit</subject>"
						+ "<text> <![CDATA[" + notificationmessage.toString() + "]]> </text> " + "<userGroups> "
						+ "<userGroup id=\"" + userGroupId + "\"/> </userGroups> </message>";

				synchManager.postMessage( instance.getUrl() + "/messageConversations", NoitificationMessage, instance );

				notificationmessage.setLength( 0 );
			}

			for ( SynchInstance instance : Notification.keySet() )
			{
				updateMessage.append( "The follwing Organisation Unit is/are approved for the instance : " + "<b>"
						+ instance.getName() + "</b><br>" );
				updateMessage.append( Notification.get( instance ).toString() );
			}

			statusMessage = updateMessage.toString().replace( "\n", "<br>" );

			if ( statusMessage.equals( "" ) )
			{
				statusMessage = "No Organisation unit is approved/updated";
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			statusMessage = "Some Error occured in saving data please contact admin";
		}
		return SUCCESS;
	}

	private void addNewOrgUnits(OrganisationUnit orgUnit,
			String approveNeeded, String approveStatus, String[] instanceIds) {

		for ( String instanceId : instanceIds )
		{
			SynchInstance instance = synchInstanceService.getInstance( Integer.parseInt( instanceId ) );

			OrganisationUnitSynchStatus orgUnitSynchStatus = organisationUnitSynchStatusService
					.getStatusByInstanceAndOrganisationUnit( instance, orgUnit );

			if ( orgUnitSynchStatus == null )
			{
				orgUnitSynchStatus = new OrganisationUnitSynchStatus();

				orgUnitSynchStatus.setOrganisationUnit( orgUnit );
				orgUnitSynchStatus.setInstance( instance );

				if ( approveNeeded != null )
				{
					orgUnitSynchStatus.setRememberApproveStatus( true );
				}
				else
				{
					orgUnitSynchStatus.setRememberApproveStatus( false );
				}

				if( orgUnitSynchStatus.getAcceptStatus() != null && orgUnitSynchStatus.getAcceptStatus() )
				{
					orgUnitSynchStatus.setOrganisationUnitStatus( OrganisationUnitSynchStatus.ORGANISATIONUNIT_STATUS_UPDATE );
				}
				else
				{
					orgUnitSynchStatus.setOrganisationUnitStatus( OrganisationUnitSynchStatus.ORGANISATIONUNIT_STATUS_NEW );
				}


				if ( approveStatus != null )
				{
					orgUnitSynchStatus.setApproveStatus( true );
					orgUnitSynchStatus.setApprovedDate( lastUpdated );
					orgUnitSynchStatus.setStatus( OrganisationUnitSynchStatus.SYNCH_STATUS_APPROVED );
				}
				else
				{
					orgUnitSynchStatus.setApproveStatus( false );
					orgUnitSynchStatus.setApprovedDate( null );
					orgUnitSynchStatus.setStatus( OrganisationUnitSynchStatus.SYNCH_STATUS_SUBMITTED );
				}

				orgUnitSynchStatus.setLastUpdated( lastUpdated );

				organisationUnitSynchStatusService.addOrganisationUnitSynchStatus( orgUnitSynchStatus );
				if ( !Notification.containsKey( instance ) )
				{
					Notification.put( instance, new StringBuilder( orgUnit.getName() + "\n" ) );
				}
				else
				{
					Notification.get( instance ).append( orgUnit.getName() + "\n" );
				}
			}
		}
	}

	private void updateOrgUnits(OrganisationUnitSynchStatus orgUnitsync,
			String upapproveNeeded, String upapproveStatus) {

		if ( upapproveNeeded != null )
		{
			orgUnitsync.setRememberApproveStatus( true );
		}
		else
		{
			orgUnitsync.setRememberApproveStatus( false );
		}

		if( orgUnitsync.getAcceptStatus() != null && orgUnitsync.getAcceptStatus() )
		{
			orgUnitsync.setOrganisationUnitStatus( OrganisationUnitSynchStatus.ORGANISATIONUNIT_STATUS_UPDATE );
		}
		else
		{
			orgUnitsync.setOrganisationUnitStatus( OrganisationUnitSynchStatus.ORGANISATIONUNIT_STATUS_NEW);
		}

		if ( upapproveStatus != null )
		{
			orgUnitsync.setApproveStatus( true );
			orgUnitsync.setApprovedDate( lastUpdated );
			orgUnitsync.setStatus( OrganisationUnitSynchStatus.SYNCH_STATUS_APPROVED );
		}
		else
		{
			orgUnitsync.setApproveStatus( false );
			orgUnitsync.setApprovedDate( null );
			orgUnitsync.setStatus( OrganisationUnitSynchStatus.SYNCH_STATUS_SUBMITTED );
		}

		orgUnitsync.setLastUpdated( lastUpdated );

		organisationUnitSynchStatusService.updateOrganisationUnitSynchStatus( orgUnitsync );
		
		/*if ( !Notification.containsKey( orgUnitsync.getInstance() ) )
		{
			Notification.put( orgUnitsync.getInstance(), new StringBuilder( orgUnitsync.getOrganisationUnit()
					.getName() + "\n" ) );
		}
		else
		{
			Notification.get( orgUnitsync.getInstance() ).append(
					orgUnitsync.getOrganisationUnit().getName() + "\n" );
		}*/
	}

}
