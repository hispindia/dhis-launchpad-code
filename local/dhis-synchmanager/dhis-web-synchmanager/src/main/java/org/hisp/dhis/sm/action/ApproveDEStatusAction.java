package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatus;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 *
 */

public class ApproveDEStatusAction
implements Action
{
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	@Autowired
	private InstanceBusinessRulesService instanceBusinessRulesService;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private DataElementService dataElementService;

	private DataElementSynchStatusService dataElementSynchStatusService;
	public void setDataElementSynchStatusService( DataElementSynchStatusService dataElementSynchStatusService )
	{
		this.dataElementSynchStatusService = dataElementSynchStatusService;
	}

	// ------------------------------------------------------------------------
	// Setters & Getters
	// -------------------------------------------------------------------------

	Collection<SynchInstance> synchInstances = new ArrayList<SynchInstance>();

	public Collection<SynchInstance> getSynchInstances() {
		return synchInstances;
	}

	private Collection<DataElement> newDataElements = new ArrayList<DataElement>();

	public Collection<DataElement> getNewDataElements() {
		return newDataElements;
	}

	private Collection<DataElementSynchStatus> updatedDataElementsSyncStatus;

	public Collection<DataElementSynchStatus> getUpdatedDataElementsSyncStatus()
	{
		return updatedDataElementsSyncStatus;
	}

	private Collection<DataElementSynchStatus> allDataElementSynchStatus;

	public Collection<DataElementSynchStatus> getAllDataElementSynchStatus() {
		return allDataElementSynchStatus;
	}

	private Map<DataElement, List<DataElementSynchStatus> > deInstanceMap;

	public Map<DataElement, List<DataElementSynchStatus>> getDeInstanceMap() {
		return deInstanceMap;
	}

	private Map<DataElement, List<SynchInstance>> instancesLeftMap;

	public Map<DataElement, List<SynchInstance>> getInstancesLeftMap() {
		return instancesLeftMap;
	}

	private Collection<DataElement> temp1 ,temp2 ;
	private Collection<DataElementSynchStatus> temp3;

	private int groupId;

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------


	public String execute() throws Exception 
	{

		try {
			newDataElements = new ArrayList<DataElement>();
			updatedDataElementsSyncStatus = new ArrayList<DataElementSynchStatus>();

			allDataElementSynchStatus = new ArrayList<DataElementSynchStatus>();
			deInstanceMap = new HashMap<DataElement, List<DataElementSynchStatus>>();
			instancesLeftMap = new HashMap<DataElement, List<SynchInstance>>();

			synchInstances.addAll( instanceBusinessRulesService.getInstancesForApprovalUser( currentUserService.getCurrentUser() ) );

			if(groupId == 0){

				newDataElements.addAll( dataElementSynchStatusService.getNewDataElements() );
				updatedDataElementsSyncStatus.addAll(dataElementSynchStatusService.getUpdatedDataElementSyncStatus());
			}
			else 
			{
				temp1 = new ArrayList<DataElement>();
				temp2 = new ArrayList<DataElement>();
				temp3 = new ArrayList<DataElementSynchStatus>();

				temp1.addAll(dataElementSynchStatusService.getNewDataElements());

				temp3.addAll(dataElementSynchStatusService.getUpdatedDataElementSyncStatus());

				temp2.addAll(dataElementService.getDataElementsByGroupId(groupId));

				for (DataElementSynchStatus desync : temp3) {
					if(temp2.contains(desync.getDataElement())){
						updatedDataElementsSyncStatus.add(desync);
					}
				}

				for(DataElement de : temp2){
					if(temp1.contains(de))
						newDataElements.add(de);
				}
			}

			allDataElementSynchStatus.addAll(dataElementSynchStatusService.getAllDataElementSynchStatus());

			for (DataElementSynchStatus desync : allDataElementSynchStatus) {

				List<DataElementSynchStatus> instance = new ArrayList<DataElementSynchStatus>();
				List<SynchInstance> instanceLeft = new ArrayList<SynchInstance>(synchInstances);

				if(!deInstanceMap.containsKey(desync.getDataElement())){
					instance.add(desync);
					instanceLeft.remove(desync.getInstance());

					deInstanceMap.put(desync.getDataElement(), instance );
					instancesLeftMap.put(desync.getDataElement(), instanceLeft);
				}
				else
				{
					deInstanceMap.get(desync.getDataElement()).add(desync);
					instancesLeftMap.get(desync.getDataElement()).remove(desync.getInstance());
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();

		}



		return SUCCESS;  //To change body of implemented methods use File | Settings | File Templates.
	}	
}
