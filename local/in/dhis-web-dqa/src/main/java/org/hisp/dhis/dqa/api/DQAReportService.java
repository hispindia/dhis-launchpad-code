package org.hisp.dhis.dqa.api;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

public interface DQAReportService
{
    public static final int MONTHENDDAYS[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    public void setServiceDEs(Collection<DataElement> serviceDEs);
	public void setServiceDataPeriodType(PeriodType serviceDataPeriodType);
	public void setAnnualStartDate(Date annualStartDate);
	public void setAnnualEndDate(Date annualEndDate);
    Map<OrganisationUnit, Integer> getOutlierList( List<OrganisationUnit> organisationUnitList,
        List<Period> periodList, DataElement dataElement );

    int getNoOfExtremeOutliers( OrganisationUnit organisationUnit, Date yearStartDate, Indicator indicator );

    double getStandardDeviation( List<Double> values );

    double getMean( List<Double> values );

    int getNoOfExtremeOutliers( OrganisationUnit organisationUnit, List<Period> periods, Indicator indicator );

    public double calculateThroughExpression(Indicator indicator,Date startDate,Date endDate, OrganisationUnit organisationUnit ) throws Exception;
    	
    Collection<OrganisationUnit> getOrgUnitListForIndicatorRatio( Integer level1, Integer level2, Date startDate,
        Date endDate, Indicator indicator );

    List<OrganisationUnit> getOrganisationUnitsHavingAtLeastOneExtremeOutlier(
        List<OrganisationUnit> organisationUnits, List<Period> periods, Indicator indicator );

    Collection<OrganisationUnit> getOrgUnitListForIndicatorRatioForSurvey( Integer level1, Integer level2,
        Date startDate, Date endDate, Indicator indicator );

    double getCurrentToAverageOFPreviousXYearsIndicatorRatio( OrganisationUnit organisationUnit, Indicator indicator,
        Date startDate, Date endDate, int years );

    int getCountOfIncompleteMonthlyDataForAYear( OrganisationUnit organisationUnit, Date yearStartDate,
        Indicator indicator );

    int getCountOfIncompleteMonthlyDataForAYear( Collection<OrganisationUnit> organisationUnits, Date yearStartDate,
            Indicator indicator );
	
    HashSet<OrganisationUnit> getOrgUnitsHavingIncompleteMonthlyDataBeyondThresholdPercentageForAYear(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Indicator indicator, int thresholdPercentage );

    int getNoOfExtremeOutliers( Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Indicator indicator );

    HashSet<OrganisationUnit> getOrganisationUnitsHavingExtremeOutlierGreaterThanThresholdPercentage(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Collection<Indicator> indicators,
        int thresholdPercentage );

    HashSet<OrganisationUnit> getOrganisationUnitsHavingModerateOutlierGreaterThanThresholdPercentage(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Collection<Indicator> indicators,
        int thresholdPercentage );

    HashSet<OrganisationUnit> getOrganisationUnitsHavingAtLeastOneExtremeOutlier(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Indicator indicator );

    double getIndicatorValue( Indicator indicator, Date reportStartDate, Date reportEndDate,
        OrganisationUnit rootOrganisationUnit );

    int getNoOfModerateOutliers( Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Indicator indicator );

    HashSet<OrganisationUnit> getOrganisationUnitsHavingAtLeastOneModerateOutlier(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Indicator indicator );

    HashSet<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentageOverTime(
        OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
        Date endDate, Indicator indicator, int thresholdPercentage, int years );
    public double getCurrentToAverageOFPreviousXYearsIndicatorRatio( OrganisationUnit organisationUnit,
            Indicator indicator, Date startDate, Date endDate,PeriodType periodType, int years );
    
    public HashSet<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentageOverTime(
            OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate, Date endDate,PeriodType periodType, Indicator indicator,
            int thresholdPercentage ,int years
            );
  
       
    HashSet<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentage(
        OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
        Date endDate, Indicator indicator, int thresholdPercentage );
        
    HashSet<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentage(
            OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits,Collection<Period> periods, Indicator indicator, int thresholdPercentage );
    public double getIndicatorAggregatedNumeratorValueByDenominatorDataValue(OrganisationUnit organisationUnit,Indicator indicator,Date startDate,Date endDate);
    
    public double getIndicatorAggregatedNumeratorValueByDenominatorDataValue(Set<OrganisationUnit> organisationUnits,Indicator indicator,Date startDate,Date endDate);
    	      	 
    public List<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage(OrganisationUnit rootOrganisationUnit,Collection<OrganisationUnit> orgUnitForAnalysis,Date startDate, Date endDate,Indicator indicator,int thresholdPercentage);
    public List<OrganisationUnitGroup> getOrgUnitGroupsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage(OrganisationUnit rootOrganisationUnit,Set<OrganisationUnitGroup> organisationUnitGroups,Date startDate, Date endDate,Indicator indicator,int thresholdPercentage);
    	  	   
	boolean generatechart2b(OrganisationUnit rootOrganisationUnit,
			Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
			Date endDate,PeriodType periodType, Indicator indicator, int years,String chartName);

	boolean generatechart2c(OrganisationUnit rootOrganisationUnit,
			Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
			Date endDate,PeriodType periodType, Indicator indicator, String chartName);

	public boolean generatechart2d(  
			List<Double> values,List<String> ouNames,String chartName);

	    public List<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage2(OrganisationUnit rootOrganisationUnit,Collection<OrganisationUnit> orgUnitForAnalysis,Date startDate, Date endDate,Indicator indicator,int thresholdPercentage);
	     
    public double getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(OrganisationUnit organisationUnit,Indicator indicator,Date startDate,Date endDate);
    	 
	boolean generatechart3a(OrganisationUnit rootOrganisationUnit,
			Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
			Date endDate, Indicator indicator, String chartName);

	boolean generatechart3b(OrganisationUnit rootOrganisationUnit,
			Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
			Date endDate, Indicator indicator, String chartName);

	boolean generatechart4(OrganisationUnit rootOrganisationUnit,
			Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
			Date endDate, Indicator indicator, DataElement standardErrorDE,
			String chartName);

	
	public List<OrganisationUnitGroup> getOrgUnitGroupsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage2(OrganisationUnit rootOrganisationUnit,Set<OrganisationUnitGroup> organisationUnitGroups,Date startDate, Date endDate,Indicator indicator,int thresholdPercentage);
    	
    public double getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(Set<OrganisationUnit> organisationUnits,Indicator indicator,Date startDate,Date endDate);
	boolean generatechart3bGroup(OrganisationUnit rootOrganisationUnit,
			Collection<OrganisationUnitGroup> analysisOrgUnitGroups,
			Date startDate, Date endDate, Indicator indicator, String chartName);
    	  
	public boolean generatechart3aGroup(  
			OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnitGroup> analysisOrgUnitGroups, Date startDate, Date endDate, Indicator indicator,String chartName);
	public boolean generatechart4Group(  
			OrganisationUnit rootOrganisationUnit, Set<OrganisationUnitGroup> analysisOrgUnitGroups, Date startDate, Date endDate, Indicator indicator,DataElement standardErrorDE,String chartName) ;
 
}
