package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatus;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 *
 */
public class ApproveIndicatorStatusAction implements Action {

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	@Autowired
	private InstanceBusinessRulesService instanceBusinessRulesService;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private IndicatorService indicatorService;

	private IndicatorSynchStatusService indicatorSynchStatusService;

	public void setIndicatorSynchStatusService(
			IndicatorSynchStatusService indicatorSynchStatusService) {
		this.indicatorSynchStatusService = indicatorSynchStatusService;
	}


	// ------------------------------------------------------------------------
	// Setters & Getters
	// -------------------------------------------------------------------------

	Collection<SynchInstance> synchInstances = new ArrayList<SynchInstance>();

	public Collection<SynchInstance> getSynchInstances() {
		return synchInstances;
	}

	public void setSynchInstances(Collection<SynchInstance> synchInstances) {
		this.synchInstances = synchInstances;
	}

	private Collection<Indicator> newIndicators = new ArrayList<Indicator>();
	public Collection<Indicator> getNewIndicators() {
		return newIndicators;
	}

	private Collection<IndicatorSynchStatus> updatedIndicatorsSyncStatus;

	public Collection<IndicatorSynchStatus> getUpdatedIndicatorsSyncStatus() {
		return updatedIndicatorsSyncStatus;
	}

	private Collection<IndicatorSynchStatus> allIndicatorSynchStatus;

	private Map<Indicator, List<IndicatorSynchStatus> > indInstanceMap;

	public Collection<IndicatorSynchStatus> getAllIndicatorSynchStatus() {
		return allIndicatorSynchStatus;
	}

	public Map<Indicator, List<IndicatorSynchStatus>> getIndInstanceMap() {
		return indInstanceMap;
	}

	private Map<Indicator, List<SynchInstance>> indInstanceLeftMap;

	public Map<Indicator, List<SynchInstance>> getIndInstanceLeftMap() {
		return indInstanceLeftMap;
	}

	private int IndicatorGroupID;
	public int getIndicatorGroupID() {
		return IndicatorGroupID;
	}

	public void setIndicatorGroupID(int indicatorGroupID) {
		IndicatorGroupID = indicatorGroupID;
	}

	private Collection<Indicator> temp1 ,temp2 ;
	private Collection<IndicatorSynchStatus> temp3;

	// ------------------------------------------------------------------------
	// Action implementation 
	// -------------------------------------------------------------------------
	@Override
	public String execute() throws Exception {
		try {
			newIndicators = new ArrayList<Indicator>();
			updatedIndicatorsSyncStatus = new ArrayList<IndicatorSynchStatus>();

			allIndicatorSynchStatus = new ArrayList<IndicatorSynchStatus>();
			indInstanceMap = new HashMap<Indicator, List<IndicatorSynchStatus>>();
			indInstanceLeftMap = new HashMap<Indicator, List<SynchInstance>>();

			synchInstances.addAll( instanceBusinessRulesService.getInstancesForApprovalUser( currentUserService.getCurrentUser() ) );

			if(IndicatorGroupID == 0){

				newIndicators.addAll( indicatorSynchStatusService.getNewIndicators() );
				updatedIndicatorsSyncStatus.addAll(indicatorSynchStatusService.getUpdatedIndicatorSyncStatus());
			}
			else
			{

				temp1 = new ArrayList<Indicator>();
				temp2 = new ArrayList<Indicator>();
				temp3 = new ArrayList<IndicatorSynchStatus>();

				temp1.addAll(indicatorSynchStatusService.getNewIndicators());

				temp3.addAll(indicatorSynchStatusService.getUpdatedIndicatorSyncStatus());

				IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup(IndicatorGroupID);

				temp2.addAll(indicatorGroup.getMembers());

				for (IndicatorSynchStatus indsync : temp3) {
					if(temp2.contains(indsync.getIndicator())){
						updatedIndicatorsSyncStatus.add(indsync);
					}
				}

				for(Indicator ind : temp2){
					if(temp1.contains(ind))
						newIndicators.add(ind);
				}
			} 

			allIndicatorSynchStatus.addAll(indicatorSynchStatusService.getAllIndicatorSynchStatus());

			for (IndicatorSynchStatus indsync : allIndicatorSynchStatus) {

				List<IndicatorSynchStatus> instance = new ArrayList<IndicatorSynchStatus>();
				List<SynchInstance> instanceLeft = new ArrayList<SynchInstance>(synchInstances);

				if(!indInstanceMap.containsKey(indsync.getIndicator())){
					instance.add(indsync);
					instanceLeft.remove(indsync.getInstance());

					indInstanceMap.put(indsync.getIndicator(), instance );
					indInstanceLeftMap.put(indsync.getIndicator(), instanceLeft);
				}
				else
				{
					indInstanceMap.get(indsync.getIndicator()).add(indsync);
					indInstanceLeftMap.get(indsync.getIndicator()).remove(indsync.getInstance());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;  //To change body of implemented methods use File | Settings | File Templates.

	}

}
