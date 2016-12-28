package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 *
 */

public class SyncStatusFormDesignAction implements Action {

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	@Autowired
	private DataElementService dataElementService;
	@Autowired
	private IndicatorService indicatorService;
	@Autowired
	private OrganisationUnitGroupService groupService;
	@Autowired
	private OrganisationUnitService organisationUnitService ;
	@Autowired
	private ValidationRuleService validationRuleService;

	private ValidationRuleSynchStatusService validationRuleSynchStatusService;

	// ------------------------------------------------------------------------
	// Setters & Getters
	// -------------------------------------------------------------------------

	public void setValidationRuleSynchStatusService(
			ValidationRuleSynchStatusService validationRuleSynchStatusService) {
		this.validationRuleSynchStatusService = validationRuleSynchStatusService;
	}

	private Collection<DataElement> allDataElements = new ArrayList<DataElement>();

	private Collection<DataElementGroup> dataElementGroups = new ArrayList<DataElementGroup>();

	public Collection<DataElement> getAllDataElements() 
	{
		return allDataElements;
	}

	public Collection<DataElementGroup> getDataElementGroups() {
		return dataElementGroups;
	}

	private Collection<Indicator> allIndicators = new ArrayList<Indicator>();

	private Collection<IndicatorGroup> indicatorGroups = new ArrayList<IndicatorGroup>();


	public Collection<Indicator> getAllIndicators() {
		return allIndicators;
	}

	public Collection<IndicatorGroup> getIndicatorGroups() {
		return indicatorGroups;
	}

	private Collection<OrganisationUnit> allOrgUnits = new ArrayList<OrganisationUnit>();

	private Collection<OrganisationUnitGroup> OrgUnutGroups = new ArrayList<OrganisationUnitGroup>();

	public Collection<OrganisationUnit> getAllOrgUnits() {
		return allOrgUnits;
	}

	public Collection<OrganisationUnitGroup> getOrgUnutGroups() {
		return OrgUnutGroups;
	}

	private List<ValidationRule> allValidationRules;// = new ArrayList<ValidationRule>();


	public List<ValidationRule> getAllValidationRules() {
		return allValidationRules;
	}

	private Collection<ValidationRuleGroup> validationRuleGroups = new ArrayList<ValidationRuleGroup>();



	public Collection<ValidationRuleGroup> getValidationRuleGroups() {
		return validationRuleGroups;
	}

	//-------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------


	@Override
	public String execute() throws Exception {

		allDataElements.addAll( dataElementService.getAllDataElements() );

		dataElementGroups.addAll(dataElementService.getAllDataElementGroups());

		allIndicators.addAll(indicatorService.getAllIndicators());

		indicatorGroups.addAll(indicatorService.getAllIndicatorGroups());

		allOrgUnits.addAll(organisationUnitService.getAllOrganisationUnits());

		OrgUnutGroups.addAll(groupService.getAllOrganisationUnitGroups());
		
//		allValidationRules.addAll(validationRuleService.getAllValidationRules());

		allValidationRules = new ArrayList<ValidationRule>( validationRuleSynchStatusService.getValidationRuleList() );

		validationRuleGroups.addAll(validationRuleService.getAllValidationRuleGroups());
		//		
		//		for (ValidationRule vr : allValidationRules) {
		//			System.out.println("rule group ="+vr);
		//		}


		return SUCCESS;
	}

}
