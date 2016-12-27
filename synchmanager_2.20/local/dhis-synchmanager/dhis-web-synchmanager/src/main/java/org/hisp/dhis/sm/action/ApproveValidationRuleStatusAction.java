package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatus;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 *
 */

public class ApproveValidationRuleStatusAction implements Action {
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	@Autowired
	private InstanceBusinessRulesService instanceBusinessRulesService;

	@Autowired
	private CurrentUserService currentUserService;

	@Autowired
	private ValidationRuleService ruleService;

	private ValidationRuleSynchStatusService ruleSynchStatusService;

	public void setRuleSynchStatusService(
			ValidationRuleSynchStatusService ruleSynchStatusService) {
		this.ruleSynchStatusService = ruleSynchStatusService;
	}


	// ------------------------------------------------------------------------
	// Setters & Getters
	// -------------------------------------------------------------------------


	Collection<SynchInstance> synchInstances = new ArrayList<SynchInstance>();

	public Collection<SynchInstance> getSynchInstances() {
		return synchInstances;
	}

	private Collection<ValidationRule> newValidationRules;

	private Collection<ValidationRuleSynchStatus> updatedValidationRuleSyncStatus;

	public Collection<ValidationRule> getNewValidationRules() {
		return newValidationRules;
	}

	public Collection<ValidationRuleSynchStatus> getUpdatedValidationRuleSyncStatus() {
		return updatedValidationRuleSyncStatus;
	}

	private Collection<ValidationRuleSynchStatus> allValidationRuleSynchStatus;

	public Collection<ValidationRuleSynchStatus> getAllValidationRuleSynchStatus() {
		return allValidationRuleSynchStatus;
	}

	private Map<ValidationRule, List<ValidationRuleSynchStatus>> vRuleInstancemap;
	private Map<ValidationRule, List<SynchInstance>> validationRuleInstanceLeftMap;

	public Map<ValidationRule, List<ValidationRuleSynchStatus>> getvRuleInstancemap() {
		return vRuleInstancemap;
	}

	public Map<ValidationRule, List<SynchInstance>> getValidationRuleInstanceLeftMap() {
		return validationRuleInstanceLeftMap;
	}

	private Collection<ValidationRule> temp1 ,temp2 ;
	private Collection<ValidationRuleSynchStatus> temp3;

	private int ValidationRuleGroupId;

	public void setValidationRuleGroupId(int validationRuleGroupId) {
		ValidationRuleGroupId = validationRuleGroupId;
	}

	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------

	@Override
	public String execute() throws Exception {

		try {
			newValidationRules = new ArrayList<ValidationRule>();
			updatedValidationRuleSyncStatus = new ArrayList<ValidationRuleSynchStatus>();

			allValidationRuleSynchStatus =  new ArrayList<ValidationRuleSynchStatus>();
			vRuleInstancemap = new HashMap<ValidationRule, List<ValidationRuleSynchStatus>>();

			validationRuleInstanceLeftMap = new HashMap<ValidationRule, List<SynchInstance>>();

			synchInstances.addAll( instanceBusinessRulesService.getInstancesForApprovalUser( currentUserService.getCurrentUser() ) );

			if(ValidationRuleGroupId == 0){
				newValidationRules.addAll(ruleSynchStatusService.getNewValidationRules());
				updatedValidationRuleSyncStatus.addAll(ruleSynchStatusService.getUpdatedValidationRuleSyncStatus());
			}
			else 
			{
				temp1 = new ArrayList<ValidationRule>();
				temp2 = new ArrayList<ValidationRule>();
				temp3 = new ArrayList<ValidationRuleSynchStatus>();

				temp1.addAll(ruleSynchStatusService.getNewValidationRules());

				temp3.addAll(ruleSynchStatusService.getUpdatedValidationRuleSyncStatus());

				ValidationRuleGroup validationRuleGroup = ruleService.getValidationRuleGroup( ValidationRuleGroupId );
				temp2.addAll(validationRuleGroup.getMembers());


				for (ValidationRuleSynchStatus VRsync : temp3) {
					if(temp2.contains(VRsync.getValidationRule())){
						updatedValidationRuleSyncStatus.add(VRsync);
					}
				}

				for(ValidationRule validationRule : temp2){
					if(temp1.contains(validationRule))
						newValidationRules.add(validationRule);
				}

			}

			allValidationRuleSynchStatus.addAll(ruleSynchStatusService.getAllValidationRuleSynchStatus());

			for (ValidationRuleSynchStatus VRsync : allValidationRuleSynchStatus) {

				List<ValidationRuleSynchStatus> instance = new ArrayList<ValidationRuleSynchStatus>();
				List<SynchInstance> instanceLeft = new ArrayList<SynchInstance>(synchInstances);

				if(!vRuleInstancemap.containsKey(VRsync.getValidationRule())){
					instance.add(VRsync);
					instanceLeft.remove(VRsync.getInstance());

					vRuleInstancemap.put(VRsync.getValidationRule(), instance );
					validationRuleInstanceLeftMap.put(VRsync.getValidationRule(), instanceLeft);
				}
				else
				{
					vRuleInstancemap.get(VRsync.getValidationRule()).add(VRsync);
					validationRuleInstanceLeftMap.get(VRsync.getValidationRule()).remove(VRsync.getInstance());
				}
			}
			/*
				for (ValidationRule sync : vRuleInstancemap.keySet()) {
					System.out.println("Validation rule :"+sync.getName());

					for (ValidationRuleSynchStatus vrsync : vRuleInstancemap.get(sync)) {
						System.out.println("value :"+vrsync.getInstance().getName());
					}

				}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

}
