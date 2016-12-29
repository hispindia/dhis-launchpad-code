package org.hisp.dhis.ovc.aggregationreport.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.ovc.util.OVCAggregationService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class RunAggregationQueryAction implements Action
{
    private final String OPTION_SET_HEALTH_NUTRITION = "Caregiver STF Health & Nutrition Agg";
    
    private final String OPTION_SET_ECONOMIC = "Caregiver STF Economic strengthening Agg";
    
    private final String OPTION_SET_SHELETER_CARE = "Caregiver STF Shelter & Care Agg";
    
    private final String OPTION_SET_PROTECTION_SERVICE = "Caregiver STF Protection Service Agg";
    
    private final String OPTION_SET_HEALTH_NUTRITION_ASSESSMENT = "Caregiver STF Health & Nutrition Assessment Agg";
    
    private final String OPTION_SET_ECONOMIC_ASSESSMENT = "Caregiver STF Economic Strengthening Assessment Agg";
    
    // for STF form1A
    private final String OPTION_SET_OVC_STF_HEALTH_NUTRITION = "OVC STF Health & Nutrition Agg";
    
    private final String OPTION_SET_OVC_STF_ECONOMIC = "OVC STF Economic strengthening Agg";
    
    private final String OPTION_SET_OVC_STF_SHELETER_CARE = "OVC STF Shelter and Care Agg";
    
    private final String OPTION_SET_OVC_STF_PROTECTION_SERVICE = "OVC STF Protection Services Agg";
    
    private final String OPTION_SET_OVC_STF_EDUCATION = "OVC STF Educational Agg";
    
    private final String OPTION_SET_OVC_STF_PSYCHOSOCIAL_SERVICE = "OVC STF Psychosocial Services Agg";
    
    private final String OPTION_SET_OVC_STF_PSYCHOSOCIAL_ASSESSMENT = "OVC STF Health and Nutrition Assessment Agg";
    
    private final String OPTION_SET_OVC_STF_ECONOMIC_ASSESSMENT = "OVC STF Economic strength Assessment Agg";
    
    private final String OPTION_SET_OVC_STF_SHELETER_CARE_ASSESSMENT = "OVC STF Shelter and Care Assessment Agg";
    
    private final String OPTION_SET_OVC_STF_EDUCATION_ASSESSMENT = "OVC STF Educational Assessment Agg";
    
    private final String OPTION_SET_OVC_STF_PROTECTION_ASSESSMENT = "OVC STF Protection Assessment Agg";
   
    private final String OPTION_SET_OVC_STF_PSY_ASSESSMENT = "OVC STF Psychosocial Assessment Agg";
    
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SelectionTreeManager selectionTreeManager;
    
    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private CaseAggregationConditionService aggregationConditionService;
    
    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }
    
    private OVCAggregationService ovcAggregationService;
    
    public void setOvcAggregationService( OVCAggregationService ovcAggregationService )
    {
        this.ovcAggregationService = ovcAggregationService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
        
    private String importStatus = "";
    
    public String getImportStatus()
    {
        return importStatus;
    }
    
    private String multiService;
    
    public void setMultiService( String multiService )
    {
        this.multiService = multiService;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {
        
        System.out.println( " Aggregation Start Time " + new Date() );
        
        //System.out.println( " multiService checkbox value " + multiService );
        
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
    
        Set<OrganisationUnit> tempOrgUnitList = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
        
        Set<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();
        
        for ( OrganisationUnit org : tempOrgUnitList )
        {
            orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( org.getId() )  ) ;
        }
        
        //System.out.println( " Size of orgUnitList First -- " + orgUnitList.size() );
    
        List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( "CBOs" ) );
    
        OrganisationUnitGroup ouGroup = ouGroups.get( 0 );
    
        if ( ouGroup != null )
        {
            orgUnitList.retainAll( ouGroup.getMembers() );
        }
        
        //System.out.println( " Size of orgUnitList -- " + orgUnitList.size() );
        
        Period period = new Period();
        period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        if( period == null )
        {
            period = periodService.reloadPeriod( period );
        }
        
        
        //System.out.println( " Period -- " + period.getId() );
        
        Set<CaseAggregationCondition> conditions = new HashSet<CaseAggregationCondition>( aggregationConditionService.getAllCaseAggregationCondition() );
        
        if( orgUnitList != null && orgUnitList.size() > 0 )
        {
            for ( CaseAggregationCondition condition : conditions )
            {
                DataElement dataElement = condition.getAggregationDataElement();
                
                DataElementCategoryOptionCombo optionCombo = condition.getOptionCombo();
                
                if( multiService != null  && multiService.equalsIgnoreCase( "on" ) )
                {
                    //System.out.println( " in side multi service" );
                    
                    // Number of OVC's who received service 1 or 2 below 1 years Male
                    if ( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_LESS1_M" ) )
                    {
                        String gender = "M";
                        
                        String age = " < 1 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 1 or 2 below 1 years Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_LESS1_F" ) )
                    {
                        String gender = "F";
                        
                        String age = " < 1 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 1 or 2 between 1-4 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_1TO4_M" ) )
                    {
                        String gender = "M";
                        String age = " between 1 and 4 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 1 or 2 between 1-4 years Female
                    else if( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_1TO4_F" ) )
                    {
                        String gender = "F";
                        String age = " between 1 and 4 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who received service 1 or 2 between 5-9 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_5TO9_M" ) )
                    {
                        String gender = "M";
                        String age = " between 5 and 9 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 1 or 2 between 5-9 years Female
                    else if( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_5TO9_F" ) )
                    {
                        String gender = "F";
                        String age = " between 5 and 9 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 1 or 2 between 10-14 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_10TO14_M" ) )
                    {
                        String gender = "M";
                        String age = " between 10 and 14 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 1 or 2 between 10-14 years Female
                    else if( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_10TO14_F" ) )
                    {
                        String gender = "F";
                        String age = " between 10 and 14 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                    
                    
                    // Number of OVC's who received service 1 or 2 between 15-17 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_15TO17_M" ) )
                    {
                        String gender = "M";
                        String age = " between 15 and 17 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 1 or 2 between 15-17 years Female
                    else if( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_15TO17_F" ) )
                    {
                        String gender = "F";
                        String age = " between 15 and 17 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                       
                    
                    
                    // Number of OVC's who received service 1 or 2 Above 18 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_ABOVE18_M" ) )
                    {
                        String gender = "M";
                        String age = " >= 18 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 1 or 2 Above 18 years Female
                    else if( condition.getOperator().equals( "COUNT_DE_OVC_1OR2SERVICE_AGE_ABOVE18_F" ) )
                    {
                        String gender = "F";
                        String age = " >= 18 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                    
                    
                    // Number of OVC's who received service 3 and more below 1 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_LESS1_M" ) )
                    {
                        String gender = "M";
                        
                        String age = " < 1 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 3 and more below 1 years Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_LESS1_F" ) )
                    {
                        String gender = "F";
                        
                        String age = " < 1 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 3 and more between 1-4 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_1TO4_M" ) )
                    {
                        String gender = "M";
                        
                        String age = " between 1 and 4 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 3 and more between 1-4 years Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_1TO4_F" ) )
                    {
                        String gender = "F";
                        
                        String age = " between 1 and 4 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 3 and more between 5-9 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_5TO9_M" ) )
                    {
                        String gender = "M";
                        String age = " between 5 and 9 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 3 and more between 5-9 years Female
                    else if( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_5TO9_F" ) )
                    {
                        String gender = "F";
                        String age = " between 5 and 9 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 3 and more between 10-14 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_10TO14_M" ) )
                    {
                        String gender = "M";
                        String age = " between 10 and 14 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 3 and more between 10-14 years Female
                    else if( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_10TO14_F" ) )
                    {
                        String gender = "F";
                        String age = " between 10 and 14 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                   
                    
                    // Number of OVC's who received service 3 and more between 15-17 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_15TO17_M" ) )
                    {
                        String gender = "M";
                        String age = " between 15 and 17 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 3 and more between 15-17 years Female
                    else if( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_15TO17_F" ) )
                    {
                        String gender = "F";
                        String age = " between 15 and 17 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                     
                    
                    
                    // Number of OVC's who received service 3 and more between Above 18 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_ABOVE_18_M" ) )
                    {
                        String gender = "M";
                        String age = " >= 18 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who received service 3 and more between Above 18 years Female
                    else if( condition.getOperator().equals( "COUNT_DE_OVC_3_AND_MORE_SERVICE_AGE_ABOVE_18_F" ) )
                    {
                        String gender = "F";
                        String age = " >= 18 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCRecivedMultipleService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                        
                    
                    
                    // Not Received any service
                    
                    // Number of OVC's who not received any service below 1 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_LESS1_M" ) )
                    {
                        String gender = "M";
                        
                        String age = " < 1 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who not received any service below 1 years Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_LESS1_F" ) )
                    {
                        String gender = "F";
                        
                        String age = " < 1 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who not received any service between 1-4 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_1TO4_M" ) )
                    {
                        String gender = "M";
                        
                        String age = " between 1 and 4 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who not received any service between 1-4 years Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_1TO4_F" ) )
                    {
                        String gender = "F";
                        
                        String age = " between 1 and 4 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who not received any service between 5-9 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_5TO9_M" ) )
                    {
                        String gender = "M";
                        
                        String age = " between 5 and 9 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who not received any service between 5-9 years Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_5TO9_F" ) )
                    {
                        String gender = "F";
                        
                        String age = " between 5 and 9 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                    
                    
                    
                    // Number of OVC's who not received any service between 10-14 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_10T14_M" ) )
                    {
                        String gender = "M";
                        
                        String age = " between 10 and 14 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who not received any service between 10-14 years Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_10T14_F" ) )
                    {
                        String gender = "F";
                        
                        String age = " between 10 and 14 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                    
                    
                    // Number of OVC's who not received any service between 15-17 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_15TO17_M" ) )
                    {
                        String gender = "M";
                        
                        String age = " between 15 and 17 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who not received any service between 15-17 years Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_15TO17_F" ) )
                    {
                        String gender = "F";
                        
                        String age = " between 15 and 17 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                    
                    
                    // Number of OVC's who not received any service Above 18 years Male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_ABOVE18_M" ) )
                    {
                        String gender = "M";
                        
                        String age = " >= 18 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC's who not received any service Above 18 years Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_NOT_ANY_SERVICE_AGE_ABOVE18_F" ) )
                    {
                        String gender = "F";
                        
                        String age = " >= 18 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCNotRecivedAnyService( period, gender, age, dataElement, optionCombo, orgUnitList, condition.getAggregationExpression() ) );
                    }                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                }
                
                
                else
                {
                    //System.out.println( " out side multi service" );
                    
                    // STF Form1A Report
                    //No.of OVC who received at least one Health & Nutrition support male
                    if ( condition.getOperator().equals( "COUNT_DE_OVC_HN_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionHealthNutritionSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_HEALTH_NUTRITION );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutritionSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No.of OVC who received at least one Health & Nutrition support Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_HN_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionHealthNutritionSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_HEALTH_NUTRITION );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutritionSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                    

                    //No.of OVC who received atleast one Economic Strengthening  support male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_ECO_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionECONOMICSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_ECONOMIC );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionECONOMICSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No.of OVC who received atleast one Economic Strengthening  support Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_ECO_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionECONOMICSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_ECONOMIC );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionECONOMICSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                                   
                    //No. of OVC who received at least one Shelter & Care service support male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_SHLT_CARE_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionSHLTSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_SHELETER_CARE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionSHLTSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No. of OVC who received at least one Shelter & Care service support Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_SHLT_CARE_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionSHLTSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_SHELETER_CARE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionSHLTSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    //No. of OVC who received at least one Protection Care service support male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_PROTECTION_SERVICE_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionPROTECTIONSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PROTECTION_SERVICE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionPROTECTIONSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No. of OVC who received at least one Protection Care service support Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_PROTECTION_SERVICE_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionPROTECTIONSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PROTECTION_SERVICE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionPROTECTIONSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                                
                    
                    //No. of OVC who received atleast one educational service support male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_EDU_SERVICE_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionEDUSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_EDUCATION );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionEDUSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No. of OVC who received atleast one educational service support Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_EDU_SERVICE_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionEDUSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_EDUCATION );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionEDUSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                                
                                    
                    //No. of OVC who received atleast one Psychosocial service support male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_PSYC_SERVICE_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionPSYCSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PSYCHOSOCIAL_SERVICE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionPSYCSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No. of OVC who received atleast one Psychosocial service support Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_PSYC_SERVICE_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionPSYCSTF = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PSYCHOSOCIAL_SERVICE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionPSYCSTF.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                    
                    
                    //No.of OVC who required at least one Health & Nutrition support (assessment) male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_HN_ASSESSMENT_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionPSYCSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PSYCHOSOCIAL_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionPSYCSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No.of OVC who required at least one Health & Nutrition support (assessment) Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_HN_ASSESSMENT_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionPSYCSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PSYCHOSOCIAL_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionPSYCSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                  
                  
                    //No.of OVC who required atleast one Economic Strengthening  support (assessment) male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_ECO_ASSESSMENT_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionECOCSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_ECONOMIC_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionECOCSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No.of OVC who required atleast one Economic Strengthening  support (assessment) Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_ECO_ASSESSMENT_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionECOCSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_ECONOMIC_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionECOCSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                  
                                    
                    //No. of OVC who require at least one  Shelter and Care support (assessment) male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_SHELTER_ASSESSMENT_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionSHLTSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_SHELETER_CARE_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionSHLTSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No. of OVC who require at least one  Shelter and Care support (assessment) Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_SHELTER_ASSESSMENT_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionSHLTSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_SHELETER_CARE_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionSHLTSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                    
                    //No. of OVC who require atleast one Educational support (assessment) male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_EDU_ASSESSMENT_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionEDUSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_EDUCATION_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionEDUSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No. of OVC who require atleast one Educational support (assessment) Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_EDU_ASSESSMENT_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionEDUSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_EDUCATION_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionEDUSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    //No. of OVC who require atleast one Protection support (assessment) male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_PROT_ASSESSMENT_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionProtectionSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PROTECTION_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionProtectionSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No. of OVC who require atleast one Protection support (assessment) Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_PROT_ASSESSMENT_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionProtectionSTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PROTECTION_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionProtectionSTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                  
                    
                    //No. of OVC who require atleast one Psychosocial Support (assessment) male
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_PSYC_ASSESSMENT_M" ) )
                    {
                        String gender = "M";
                        
                        OptionSet optionPsySTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PSY_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionPsySTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    //No. of OVC who require at least one Psychosocial Support (assessment) Female
                    else if ( condition.getOperator().equals( "COUNT_DE_OVC_PSYC_ASSESSMENT_F" ) )
                    {
                        String gender = "F";
                        
                        OptionSet optionPsySTFAssesment = optionService.getOptionSetByName( OPTION_SET_OVC_STF_PSY_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionPsySTFAssesment.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( period, gender, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                  
                    
                    
                    
                    // Care giver Health and Nutrition Services Male
                    
                    else if ( condition.getOperator().equals( "COUNT_DE_HN_M" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_HEALTH_NUTRITION );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Care giver Health and Nutrition Services Female
                    else if ( condition.getOperator().equals( "COUNT_DE_HN_F" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_HEALTH_NUTRITION );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                        
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Care giver Economics strengthening Services Male
                    else if( condition.getOperator().equals( "COUNT_DE_ECO_M" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_ECONOMIC );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Care giver Economics strengthening Services Female
                    else if( condition.getOperator().equals( "COUNT_DE_ECO_F" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_ECONOMIC );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    
                    // No. of Care givers who received at least one Shelter & Care service support Male
                    else if( condition.getOperator().equals( "COUNT_DE_SHLT_CARE_M" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_SHELETER_CARE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                    
                    // No. of Care givers who received at least one Shelter & Care service support Female
                    else if( condition.getOperator().equals( "COUNT_DE_SHLT_CARE_F" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_SHELETER_CARE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    // No. of Caregivers who received at least one Protection Care service support Male
                    else if( condition.getOperator().equals( "COUNT_DE_PROTECTION_SERVICE_M" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_PROTECTION_SERVICE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                    
                    // No. of Caregivers who received at least one Protection Care service support Male
                    else if( condition.getOperator().equals( "COUNT_DE_PROTECTION_SERVICE_F" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_PROTECTION_SERVICE );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    
                    // No.of Caregivers who required at least one Health & Nutrition Support Male
                    else if( condition.getOperator().equals( "COUNT_DE_HN_ASSESSMENT_M" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_HEALTH_NUTRITION_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                    
                    
                    // No.of Caregivers who required at least one Health & Nutrition Support Female
                    else if( condition.getOperator().equals( "COUNT_DE_HN_ASSESSMENT_F" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_HEALTH_NUTRITION_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    
                    // No.of Care givers who required at least one Economic Strengthening  Support Male
                    else if( condition.getOperator().equals( "COUNT_DE_ECO_ASSESSMENT_M" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_ECONOMIC_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }              
                    
                    // No.of Care givers who required at least one Economic Strengthening  Support Female
                    else if( condition.getOperator().equals( "COUNT_DE_ECO_ASSESSMENT_F" ) )
                    {
                        
                        OptionSet optionHealthNutrition = optionService.getOptionSetByName( OPTION_SET_ECONOMIC_ASSESSMENT );
                        
                        String dataElementIdsByComma = "-1";
                        
                        for( String optionName : optionHealthNutrition.getOptions() )
                        {
                            dataElementIdsByComma += "," + optionName;
                        }
                       
                        aggregationResultMap.putAll( ovcAggregationService.calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( period, dataElement, optionCombo, dataElementIdsByComma, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    
                    // Number of new OVC Registered Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_REG_M" ) )
                    {
                        String gender = "M";
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCRegistered( period, dataElement, optionCombo, gender, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of new OVC Registered Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_REG_F" ) )
                    {
                        String gender = "F";
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCRegistered( period, dataElement, optionCombo, gender, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    // Number of active OVC Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_ACTIVE_M" ) )
                    {
                        String gender = "M";
                        String status = "Active";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of active OVC Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_ACTIVE_F" ) )
                    {
                        String gender = "F";
                        String status = "Active";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of In active OVC Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_INACTIVE_M" ) )
                    {
                        String gender = "M";
                        String status = "Inactive";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of In active OVC Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_INACTIVE_F" ) )
                    {
                        String gender = "F";
                        String status = "Inactive";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    // Number of Rejected OVC Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_REJECTED_M" ) )
                    {
                        String gender = "M";
                        String status = "Rejected";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of Rejected OVC Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_REJECTED_M" ) )
                    {
                        String gender = "F";
                        String status = "Rejected";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC in Pre-School Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_PRE_SCHOOL_M" ) )
                    {
                        String gender = "M";
                        String value = "Pre-Primary";
                        String paId = "44";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC in Pre-School Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_PRE_SCHOOL_F" ) )
                    {
                        String gender = "F";
                        String value = "Pre-Primary";
                        String paId = "44";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC in primary school Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_PRIMARY_SCHOOL_M" ) )
                    {
                        String gender = "M";
                        String value = "Primary";
                        String paId = "44";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC in primary school Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_PRIMARY_SCHOOL_F" ) )
                    {
                        String gender = "F";
                        String value = "Primary";
                        String paId = "44";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                    
                    // Number of OVC in secondary Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_SECONDARY_SCHOOL_M" ) )
                    {
                        String gender = "M";
                        String value = "Secondary";
                        String paId = "44";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    //  Number of OVC in secondary Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_SECONDARY_SCHOOL_F" ) )
                    {
                        String gender = "F";
                        String value = "Secondary";
                        String paId = "44";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }
                    
                    // Number of OVC Tertiary Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_TERTIARY_SCHOOL_M" ) )
                    {
                        String gender = "M";
                        String value = "Tertiary/Vocational";
                        String paId = "44";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    //  Number of OVC Tertiary Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_TERTIARY_SCHOOL_F" ) )
                    {
                        String gender = "F";
                        String value = "Tertiary/Vocational";
                        String paId = "44";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC NOT in School Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_NOT_SCHOOL_M"  ) )
                    {
                        String gender = "M";
                        String paId = "51";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCNotInSchool( period, dataElement, optionCombo, gender, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    //  Number of OVC NOT in School Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_NOT_SCHOOL_F" ) )
                    {
                        String gender = "F";
                        String paId = "51";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCNotInSchool( period, dataElement, optionCombo, gender, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC Died Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_DIED_M"  ) )
                    {
                        String gender = "M";
                        String value = "Died";
                        String paId = "450";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    //  Number of OVC Died Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_DIED_F" ) )
                    {
                        String gender = "F";
                        String value = "Died";
                        String paId = "450";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, value, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    
                    // Number of OVC exited Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_EXITED_M" ) )
                    {
                        String gender = "M";
                        String status = "Inactive";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC exited Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_EXITED_F" ) )
                    {
                        String gender = "F";
                        String status = "Inactive";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                                 

                    // Number of OVC Exit request rejected Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_EXIT_REJECT_M" ) )
                    {
                        String gender = "M";
                        String status = "Active";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC Exit request rejected Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_EXIT_REJECT_F" ) )
                    {
                        String gender = "F";
                        String status = "Active";
                        String paId = "301";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having HIV Positive Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_HIV_POSITIVE_M" ) )
                    {
                        String gender = "M";
                        String status = "Positive";
                        String paId = "52";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having HIV Positive Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_HIV_POSITIVE_F" ) )
                    {
                        String gender = "F";
                        String status = "Positive";
                        String paId = "52";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    
                    // Number of Mothers HIV Positive Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_MOTHER_HIV_POSITIVE_M" ) )
                    {
                        String gender = "M";
                        String status = "Positive";
                        String paId = "77";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of Mothers HIV Positive Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_MOTHER_HIV_POSITIVE_F" ) )
                    {
                        String gender = "F";
                        String status = "Positive";
                        String paId = "77";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having 1st Priority need-Food/Nutrition Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_FN_M" ) )
                    {
                        String gender = "M";
                        String status = "Food/Nutrition";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having 1st Priority need-Food/Nutrition Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_FN_F" ) )
                    {
                        String gender = "F";
                        String status = "Food/Nutrition";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    // Number of OVC having 1st Priority need-Education Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_EDUCATION_M" ) )
                    {
                        String gender = "M";
                        String status = "Education";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having 1st Priority need-Education Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_EDUCATION_F" ) )
                    {
                        String gender = "F";
                        String status = "Education";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having 1st Priority need-child protection Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_CHILD_PROTECTION_M" ) )
                    {
                        String gender = "M";
                        String status = "Child Protection";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having 1st Priority need-child protection Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_CHILD_PROTECTION_F" ) )
                    {
                        String gender = "F";
                        String status = "Child Protection";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    
                    // Number of OVC having 1st Priority need-shelter/Care Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_SC_M" ) )
                    {
                        String gender = "M";
                        String status = "Shelter/Care";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having 1st Priority need-shelter/Care Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_SC_F" ) )
                    {
                        String gender = "F";
                        String status = "Shelter/Care";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                 
                    // Number of OVC having 1st Priority need-Health Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_HEALTH_M" ) )
                    {
                        String gender = "M";
                        String status = "Health";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having 1st Priority need-Health Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_HEALTH_F" ) )
                    {
                        String gender = "F";
                        String status = "Health";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    
                    // Number of OVC having 1st Priority need-Psychosocial Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_PSY_M" ) )
                    {
                        String gender = "M";
                        String status = "Psychosocial";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC having 1st Priority need-Psychosocial Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_FP_PSY_F" ) )
                    {
                        String gender = "F";
                        String status = "Psychosocial";
                        String paId = "583";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVC( period, dataElement, optionCombo, gender, status, paId, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                    // Number of OVC's who are below 1 years Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_LESS1_M" ) )
                    {
                        String gender = "M";
                        String age = "< 1 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are below 1 years Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_LESS1_F" ) )
                    {
                        String gender = "F";
                        String age = " < 1 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }               
                    
                    // Number of OVC's who are between 1-4 years Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_1TO4_M" ) )
                    {
                        String gender = "M";
                        String age = " between 1 and 4 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are between 1-4 years Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_1TO4_F" ) )
                    {
                        String gender = "F";
                        String age = " between 1 and 4 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are between 5-9 years Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_5TO9_M" ) )
                    {
                        String gender = "M";
                        String age = " between 5 and 9 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are between 5-9 years Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_5TO9_F" ) )
                    {
                        String gender = "F";
                        String age = " between 5 and 9 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are between 10-14 years Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_10TO14_M" ) )
                    {
                        String gender = "M";
                        String age = " between 10 and 14 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are between 10-14 years Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_10TO14_F" ) )
                    {
                        String gender = "F";
                        String age = " between 10 and 14 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are between 15-17 years Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_15TO17_M" ) )
                    {
                        String gender = "M";
                        String age = " between 15 and 17 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are between 15-17 years Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_15TO17_F" ) )
                    {
                        String gender = "F";
                        String age = " between 15 and 17 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are 18 and above years Male
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_ABOVE18_M" ) )
                    {
                        String gender = "M";
                        String age = " >= 18 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                
                    
                    // Number of OVC's who are 18 and above years Female
                    else if( condition.getOperator().equals( "COUNT_TOTAL_AGE_ABOVE18_F" ) )
                    {
                        String gender = "F";
                        String age = " >= 18 ";
                        
                        aggregationResultMap.putAll( ovcAggregationService.numberOfOVCByAgeGroup( period, dataElement, optionCombo, gender, age, orgUnitList, condition.getAggregationExpression() ) );
                    }                 
                    
                }
                
                dataElements.add( dataElement );
            }
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            

            /*
            for( String key : aggregationResultMap.keySet() )
            {
                System.out.println( key + " -- " + aggregationResultMap.get(  key ) );
            }
            */
            
            importStatus = ovcAggregationService.importAggregatedData( aggregationResultMap, period );
        }
        
        
        
        
        
        
        
        
        
        else
        {
            importStatus = "Please Select Correct Organisation Unit";
        }
        
        
        System.out.println( " Aggregation End Time " + new Date() );
        
        
        return SUCCESS;
    }

}
