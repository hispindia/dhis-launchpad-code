package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatus;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;
/**
 * @author Ganesh
 *
 */

public class ApproveOrgUnitStatusAction implements Action {
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	@Autowired
	private InstanceBusinessRulesService instanceBusinessRulesService;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private OrganisationUnitService organisationUnitService;
	@Autowired
	private OrganisationUnitGroupService organisationUnitGroupService;

	private OrganisationUnitSynchStatusService organisationUnitSynchStatusService;

	public void setOrganisationUnitSynchStatusService(
			OrganisationUnitSynchStatusService organisationUnitSynchStatusService) {
		this.organisationUnitSynchStatusService = organisationUnitSynchStatusService;
	}

	// ------------------------------------------------------------------------
	// Setters & Getters
	// -------------------------------------------------------------------------


	Collection<SynchInstance> synchInstances = new ArrayList<SynchInstance>();

	public Collection<SynchInstance> getSynchInstances() {
		return synchInstances;
	}

	private Collection<OrganisationUnit> newOrgUnits;

	public Collection<OrganisationUnit> getNewOrgUnits() {
		return newOrgUnits;
	}

	private Collection<OrganisationUnitSynchStatus> updatedOrgUnitSyncStatus;

	public Collection<OrganisationUnitSynchStatus> getUpdatedOrgUnitSyncStatus() {
		return updatedOrgUnitSyncStatus;
	}

	private Collection<OrganisationUnit> temp1 ,temp2 ;
	private Collection<OrganisationUnitSynchStatus> temp3;

	private int OrgUnitGroupId;

	public int getOrgUnitGroupId() {
		return OrgUnitGroupId;
	}

	public void setOrgUnitGroupId(int orgUnitGroupId) {
		OrgUnitGroupId = orgUnitGroupId;
	}

	private Collection<OrganisationUnitSynchStatus> allOrgUnitSyncStaus;

	private Map<OrganisationUnit, List<OrganisationUnitSynchStatus> > orgInstancemap;



	public Collection<OrganisationUnitSynchStatus> getAllOrgUnitSyncStaus() {
		return allOrgUnitSyncStaus;
	}

	public Map<OrganisationUnit, List<OrganisationUnitSynchStatus>> getOrgInstancemap() {
		return orgInstancemap;
	}

	private Map<OrganisationUnit, List<SynchInstance>> orgInstanceLeftMap;

	public Map<OrganisationUnit, List<SynchInstance>> getOrgInstanceLeftMap() {
		return orgInstanceLeftMap;
	}

	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------

	@Override
	public String execute() throws Exception {

		try {
			newOrgUnits = new ArrayList<OrganisationUnit>();
			updatedOrgUnitSyncStatus = new ArrayList<OrganisationUnitSynchStatus>();

			allOrgUnitSyncStaus = new ArrayList<OrganisationUnitSynchStatus>();
			orgInstancemap = new HashMap<OrganisationUnit, List<OrganisationUnitSynchStatus>>();
			orgInstanceLeftMap = new HashMap<OrganisationUnit, List<SynchInstance>>(); 

			synchInstances.addAll( instanceBusinessRulesService.getInstancesForApprovalUser( currentUserService.getCurrentUser() ) );

			if(OrgUnitGroupId == 0){
				newOrgUnits.addAll(organisationUnitSynchStatusService.getNewOrganisationUnits());
				updatedOrgUnitSyncStatus.addAll(organisationUnitSynchStatusService.getUpdatedOrganisationUnitSyncStatus());
			}
			else
			{
				temp1 = new ArrayList<OrganisationUnit>();
				temp2 = new ArrayList<OrganisationUnit>();
				temp3 = new ArrayList<OrganisationUnitSynchStatus>();

				temp1.addAll(organisationUnitSynchStatusService.getNewOrganisationUnits());

				temp3.addAll(organisationUnitSynchStatusService.getUpdatedOrganisationUnitSyncStatus());


				organisationUnitGroupService.getOrganisationUnitGroup(OrgUnitGroupId);
				OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup(OrgUnitGroupId);

				temp2.addAll(orgUnitGroup.getMembers());

				for (OrganisationUnitSynchStatus Orgsync : temp3) {
					if(temp2.contains(Orgsync.getOrganisationUnit())){
						updatedOrgUnitSyncStatus.add(Orgsync);
					}
				}

				for(OrganisationUnit orgUnit : temp2){
					if(temp1.contains(orgUnit))
						newOrgUnits.add(orgUnit);
				}

			}

			allOrgUnitSyncStaus.addAll(organisationUnitSynchStatusService.getAllOrganisationUnitSynchStatus());

			for (OrganisationUnitSynchStatus orgsync : allOrgUnitSyncStaus) {

				List<OrganisationUnitSynchStatus> instance = new ArrayList<OrganisationUnitSynchStatus>();
				List<SynchInstance> instanceLeft = new ArrayList<SynchInstance>(synchInstances);

				if(!orgInstancemap.containsKey(orgsync.getOrganisationUnit())){
					instance.add(orgsync);
					instanceLeft.remove(orgsync.getInstance());

					orgInstancemap.put(orgsync.getOrganisationUnit(), instance );
					orgInstanceLeftMap.put(orgsync.getOrganisationUnit(), instanceLeft);
				}
				else
				{
					orgInstancemap.get(orgsync.getOrganisationUnit()).add(orgsync);
					orgInstanceLeftMap.get(orgsync.getOrganisationUnit()).remove(orgsync.getInstance());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return SUCCESS;
	}

}
