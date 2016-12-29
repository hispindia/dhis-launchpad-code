package org.hisp.dhis.dqa.impl;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.stream.events.StartDocument;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dqa.api.*;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.FinancialJulyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

import static org.hisp.dhis.system.util.MathUtils.calculateExpression;


public class DefaultDQAReportService
    implements DQAReportService
{

    private static final String PERIODTYPENAME = "Monthly";
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService(AggregatedDataValueService aggregatedDataValueService) 
    {
		this.aggregatedDataValueService = aggregatedDataValueService;
	}

	private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataValueService dataValueService;
    
    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private ExpressionService expressionService;
    
    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
private AnalyticsService analyticsService;
	
	public void setAnalyticsService(AnalyticsService analyticsService) {
		this.analyticsService = analyticsService;
	}
    
	 private ConstantService constantService;

	    public void setConstantService( ConstantService constantService )
	    {
	        this.constantService = constantService;
	    }
	    
	    
	    private DataElementService dataElementService;
	    
	    public void setDataElementService( DataElementService dataElementService )
	    {
	        this.dataElementService = dataElementService;
	    }
    // PARAMAETERS
    Collection<DataElement> serviceDEs;
    public void setServiceDEs(Collection<DataElement> serviceDEs) {
		this.serviceDEs = serviceDEs;
	}
    PeriodType serviceDataPeriodType;
	public void setServiceDataPeriodType(PeriodType serviceDataPeriodType) {
		this.serviceDataPeriodType = serviceDataPeriodType;
	}
	PeriodType annualPeriodType = new YearlyPeriodType();
	Date annualStartDate;
	public void setAnnualStartDate(Date annualStartDate) {
		this.annualStartDate = annualStartDate;
	}

	public void setAnnualEndDate(Date annualEndDate) {
		this.annualEndDate = annualEndDate;
	}

	Date annualEndDate;
	DecimalFormat df=new DecimalFormat("0.00");
    DataQueryParams dataQueryParameter;
	
    // -------------------------------------------------------------------------
    // Methods Implementation
    // -------------------------------------------------------------------------
    
    @Override
    public Map<OrganisationUnit, Integer> getOutlierList( List<OrganisationUnit> organisationUnitList,
        List<Period> period, DataElement dataElement )
    {
        // TODO Auto-generated method stub
        Map<Period, Double> periodWiseValueMap = new HashMap<Period, Double>();
        Map<OrganisationUnit, Object> ouWisePeriodWiseValueMap = new HashMap<OrganisationUnit, Object>();
        Map<OrganisationUnit, Integer> orgUnitWiseNoOfOutliers = new HashMap<OrganisationUnit, Integer>();

        Iterator ouListIterator = organisationUnitList.iterator();

        while ( ouListIterator.hasNext() )
        {
            OrganisationUnit ou = (OrganisationUnit) ouListIterator.next();
            Iterator periodIterator = period.iterator();
            while ( periodIterator.hasNext() )
            {
                Period per = (Period) periodIterator.next();
                Double value = aggregationService.getAggregatedDataValue( dataElement, null, per.getStartDate(),
                    per.getEndDate(), ou );
                periodWiseValueMap.put( per, value );
                ouWisePeriodWiseValueMap.put( ou, periodWiseValueMap );
            }
        }

        ouListIterator = organisationUnitList.iterator();

        while ( ouListIterator.hasNext() )
        {
            OrganisationUnit ou = (OrganisationUnit) ouListIterator.next();
            HashMap<Period, Double> periodWiseValuesMap = (HashMap<Period, Double>) ouWisePeriodWiseValueMap.get( ou );
            List<Double> values;

            values = new ArrayList<Double>( periodWiseValuesMap.values() );

      //      System.out.println( "standard dev = " + getStandardDeviation( values ) );

            double sd = getStandardDeviation( values );
            double mean = getMean( values );

            Map<OrganisationUnit, Double> orgUnitWiseSD = new HashMap<OrganisationUnit, Double>();
            Map<OrganisationUnit, Double> orgUnitWiseMean = new HashMap<OrganisationUnit, Double>();

            orgUnitWiseSD.put( ou, sd );
            orgUnitWiseMean.put( ou, mean );

            int noOfOutliers = getNoOfOutliers( values, sd, mean );
            orgUnitWiseNoOfOutliers.put( ou, noOfOutliers );
        }
        return orgUnitWiseNoOfOutliers;

    }

    public int getNoOfOutliers( List<Double> values, double sd, double mean )
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getNoOfExtremeOutliers( OrganisationUnit organisationUnit, Date yearStartDate, Indicator indicator )
    {
    	PeriodType monthlyPeriodType = periodService.getPeriodTypeByName( PERIODTYPENAME );
    	Double value = null;
        Collection<Date> endDates = getMonthWiseEndDatesForAYear( yearStartDate );
        
        List<Double> values = new ArrayList<Double>();
        int outlierCount = 0;
        Calendar cal = Calendar.getInstance();

        Iterator<Date> endDatesIterator = endDates.iterator();
        while ( endDatesIterator.hasNext() )
        {
            Date endDate = endDatesIterator.next();
            cal.setTime( endDate );
            cal.set( Calendar.DATE, 1 );
            Date startDate = cal.getTime();
            Collection<Period> periods = periodService.getPeriodsBetweenDates( monthlyPeriodType, startDate, endDate );
            dataQueryParameter = new DataQueryParams();
            dataQueryParameter.setIndicator(indicator);	
            dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
            dataQueryParameter.setFilterOrganisationUnit( organisationUnit );
            Grid grid = analyticsService.getAggregatedDataValues( dataQueryParameter );
            // value = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,organisationUnit );
            if (grid != null && !grid.getVisibleRows().isEmpty() )
            {
                value = (Double) grid.getValue(0, 2);
            }            
            if ( value!=null && value > 0 )
            {
                values.add( value );
            }
        }

        double sd = getStandardDeviation( values );
        
        double mean = getMean( values );

        Iterator<Double> valueIterator = values.iterator();
        while ( valueIterator.hasNext() )
        {
            double val = (Double) valueIterator.next();
            if ( !isWithin3StandardDeviationsFromMean( val, mean, sd ) )
            {
                outlierCount++;
            }
        }
        
        System.out.println("orgunit="+organisationUnit.getDisplayName() + "outlier="+outlierCount);
        return outlierCount;
    }

//    public int getNoOfModerateOutliers1( OrganisationUnit organisationUnit, Date yearStartDate, Indicator indicator )
//    {
//
//        Collection<Date> endDates = getMonthWiseEndDatesForAYear( yearStartDate );
//        Iterator<Date> endDatesIterator = endDates.iterator();
//        List<Double> values = new ArrayList<Double>();
//        int outlierCount = 0;
//        Calendar cal = Calendar.getInstance();
//        while ( endDatesIterator.hasNext() )
//        {
//            Date endDate = endDatesIterator.next();
//            cal.setTime( endDate );
//            cal.set( Calendar.DATE, 1 );
//            Date startDate = cal.getTime();
//
//            Double value = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,
//                organisationUnit );
//            values.add( value );
//        }
//
//        double sd = getStandardDeviation( values );
//        double mean = getMean( values );
//
//        Iterator<Double> valueIterator = values.iterator();
//        while ( valueIterator.hasNext() )
//        {
//            double val = (Double) valueIterator.next();
//            if ( !isBetween2And3StandardDeviationsFromMean( val, mean, sd ) )
//            {
//                outlierCount++;
//            }
//        }
//        return outlierCount;
//    }

    public int getNoOfExtremeOutliers( Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Indicator indicator )
    {
        int count = 0;
        Iterator<OrganisationUnit> organisationUnitsIterator = organisationUnits.iterator();
        while ( organisationUnitsIterator.hasNext() )
        {
            OrganisationUnit ou = organisationUnitsIterator.next();
            count = count + getNoOfExtremeOutliers( ou, yearStartDate, indicator );
        }
        
        return count;
    }

    public int getNoOfModerateOutliers( Collection<OrganisationUnit> organisationUnits, Date yearStartDate,
        Indicator indicator )
    {
        int count = 0;
        Iterator<OrganisationUnit> organisationUnitsIterator = organisationUnits.iterator();

        while ( organisationUnitsIterator.hasNext() )
        {
            OrganisationUnit ou = organisationUnitsIterator.next();
            count = count + getNoOfModerateOutliers( ou, yearStartDate, indicator );
            //System.out.println("org= " + ou.getDisplayName() + " count= " +count +"indicator= " + indicator.getDisplayName() + "startdate" + yearStartDate);
        }
        
        return count;
    }

    public int getNoOfModerateOutliers( OrganisationUnit organisationUnit, Date yearStartDate, Indicator indicator )
    {
    	 PeriodType monthlyPeriodType = periodService.getPeriodTypeByName(PERIODTYPENAME);
    	  Double value = null;
        Collection<Date> endDates = getMonthWiseEndDatesForAYear( yearStartDate );
        Iterator<Date> endDatesIterator = endDates.iterator();
        List<Double> values = new ArrayList<Double>();
        int outlierCount = 0;
        Calendar cal = Calendar.getInstance();
        while ( endDatesIterator.hasNext() )
        {
            Date endDate = endDatesIterator.next();
            cal.setTime( endDate );
            cal.set( Calendar.DATE, 1 );
            Date startDate = cal.getTime();
            Collection<Period> periods = periodService.getPeriodsBetweenDates(monthlyPeriodType,startDate, endDate);
            dataQueryParameter = new DataQueryParams();
            dataQueryParameter.setIndicator(indicator);
            dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
            dataQueryParameter.setFilterOrganisationUnit(organisationUnit);
            Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
            // value = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,organisationUnit );
            if (grid!=null && !grid.getVisibleRows().isEmpty()){
             value = (Double) grid.getValue(0, 2);
            }
              if ( value > 0 )
                values.add( value );
        }

        double sd = getStandardDeviation( values );
        double mean = getMean( values );
        // System.out.println("value = " +values + "sd = "+sd+ "mean = "+mean);
        Iterator<Double> valueIterator = values.iterator();
        while ( valueIterator.hasNext() )
        {
            double val = (Double) valueIterator.next();
            if ( !isBetween2And3StandardDeviationsFromMean( val, mean, sd ) )
            {
                outlierCount++;
            }
        }
        System.out.println("orgunit=" +organisationUnit.getDisplayName() + " indicator=" + indicator.getDisplayName() + " outlierCount=" + outlierCount);
        return outlierCount;
    }

    public boolean isWithin3StandardDeviationsFromMean( double val, double mean, double sd )
    {
        // (> + or - 3 standard deviations)
        if ( val < mean - (3 * sd) || val > mean + (3 * sd) )
        {
            return false;
        }
        
        return true;
    }

    public boolean isBetween2And3StandardDeviationsFromMean( double val, double mean, double sd )
    {

        // (between + or - 2 & 3 standard deviations)
        if ( val < mean - (2 * sd) && val > mean - (3 * sd) || val > mean + (2 * sd) && val < mean + (3 * sd) )
        {
            return false;
        }

        return true;
    }

    public HashSet<OrganisationUnit> getOrganisationUnitsHavingAtLeastOneExtremeOutlier(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Indicator indicator )
    {

        Iterator<OrganisationUnit> organisationUnitIterator = organisationUnits.iterator();
        HashSet<OrganisationUnit> OrganisatioUnitListWithAtleastOneExtremeOutlier = new HashSet<OrganisationUnit>();

        while ( organisationUnitIterator.hasNext() )
        {
            OrganisationUnit ou = (OrganisationUnit) organisationUnitIterator.next();
            if ( getNoOfExtremeOutliers( ou, yearStartDate, indicator ) > 0 )
            {
                OrganisatioUnitListWithAtleastOneExtremeOutlier.add( ou );
            }
        }

        return OrganisatioUnitListWithAtleastOneExtremeOutlier;
    }

    public HashSet<OrganisationUnit> getOrganisationUnitsHavingAtLeastOneModerateOutlier(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Indicator indicator )
    {

        Iterator<OrganisationUnit> organisationUnitIterator = organisationUnits.iterator();
        HashSet<OrganisationUnit> OrganisatioUnitListWithAtleastOneModerateOutlier = new HashSet<OrganisationUnit>();

        while ( organisationUnitIterator.hasNext() )
        {
            OrganisationUnit ou = (OrganisationUnit) organisationUnitIterator.next();
            if ( getNoOfModerateOutliers( ou, yearStartDate, indicator ) > 0 )
            {
                OrganisatioUnitListWithAtleastOneModerateOutlier.add( ou );
            }
        }

        return OrganisatioUnitListWithAtleastOneModerateOutlier;
    }

    public HashSet<OrganisationUnit> getOrganisationUnitsHavingExtremeOutlierGreaterThanThresholdPercentage(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Collection<Indicator> indicators,
        int thresholdPercentage )
    {

        Iterator<OrganisationUnit> organisationUnitIterator = organisationUnits.iterator();
        Iterator<Indicator> indicatorsIterator = indicators.iterator();

        HashSet<OrganisationUnit> organisatioUnitList = new HashSet<OrganisationUnit>();
        int noOfOutliers = 0;
        while ( organisationUnitIterator.hasNext() )
        {
            noOfOutliers = 0;
            OrganisationUnit ou = (OrganisationUnit) organisationUnitIterator.next();
            for ( Indicator indicator : indicators )
            {            
                noOfOutliers = noOfOutliers + getNoOfExtremeOutliers( ou, yearStartDate, indicator );
              
            }
      System.out.println("ou="+ou.getDisplayName() +" Extreme outlier -> no of outliers="+noOfOutliers +  " threshold=" + thresholdPercentage + " yearstartdate=" + yearStartDate);
            
      		if (noOfOutliers > 0 )
            {
                if ( !isDuplicate(organisatioUnitList,ou) )
                organisatioUnitList.add( ou );
            }
        }

        return organisatioUnitList;
    }

    public HashSet<OrganisationUnit> getOrganisationUnitsHavingModerateOutlierGreaterThanThresholdPercentage(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Collection<Indicator> indicators,
        int thresholdPercentage )
    {

        Iterator<OrganisationUnit> organisationUnitIterator = organisationUnits.iterator();
        Iterator<Indicator> indicatorsIterator = indicators.iterator();
        HashSet<OrganisationUnit> organisatioUnitList = new HashSet<OrganisationUnit>();
        double noOfOutliers = 0;
        while ( organisationUnitIterator.hasNext() )
        {     
            noOfOutliers = 0;
            OrganisationUnit ou = (OrganisationUnit) organisationUnitIterator.next();

            for(Indicator indicator : indicators)
            {
          
                int count = getNoOfModerateOutliers( ou, yearStartDate, indicator );
                 noOfOutliers = noOfOutliers + count;     
            
            }
            //System.out.println("ou="+ou.getDisplayName() +" Moderate outlier -> no of outliers="+noOfOutliers +  " threshold=" + thresholdPercentage + " yearstartdate=" + yearStartDate);
            if ( ((noOfOutliers /( 12 * indicators.size()))) * 100 > thresholdPercentage )
            {
                if ( !isDuplicate(organisatioUnitList,ou) )
                organisatioUnitList.add( ou );
            }
        }

        return organisatioUnitList;
    }

    public double getStandardDeviation( List<Double> values )
    {
        // TODO Auto-generated method stub

        Iterator<Double> iterator = values.iterator();
        double mean = getMean( values );
        double sum = 0;

        while ( iterator.hasNext() )
        {
            double val = (Double) iterator.next();
            sum = sum + ((val - mean) * (val - mean));
        }

        return Math.sqrt( sum / (values.size() - 1) );
    }

    @Override
    public double getMean( List<Double> values )
    {
        // TODO Auto-generated method stub
        Iterator<Double> iterator = values.iterator();
        double sum = 0;
        while ( iterator.hasNext() )
        {
            double val = (Double) iterator.next();
            sum = sum + val;
        }

        return sum / values.size();
    }

    public double getCurrentToAverageOFPreviousXYearsIndicatorRatio( OrganisationUnit organisationUnit,
        Indicator indicator, Date startDate, Date endDate, int years )
    {
        Date currentStartDate = startDate;
        Date currentEndDate = endDate;
        Date prevYearStartDate, prevYearEndDate;
        int count = years;
        double total = 0;
        Double currentVal = 0.0;
        int noData = 0;
        
        PeriodType periodType = periodService.getPeriodTypeByName("Yearly");
        Period period = periodService.getPeriod(startDate, endDate, periodType);
        //currentVal = aggregationService.getAggregatedNumeratorValue( indicator, currentStartDate, currentEndDate, organisationUnit );
        System.out.println("period="+period);
        if (aggregatedDataValueService.getAggregatedValue(indicator, period, organisationUnit)!=null)
        currentVal = aggregatedDataValueService.getAggregatedValue(indicator, period, organisationUnit);
        
    
        while ( count > 0 )
        {
            prevYearStartDate = getPreviousYear( currentStartDate );
            prevYearEndDate = getPreviousYear( currentEndDate );
            currentStartDate = prevYearStartDate;
            currentEndDate = prevYearEndDate;
            Double value = 0.0;
            period = periodService.getPeriod(prevYearStartDate, prevYearEndDate, periodType);
     
           //  Double value = aggregationService.getAggregatedNumeratorValue( indicator, currentStartDate, currentEndDate, organisationUnit );
            if (aggregatedDataValueService.getAggregatedValue(indicator, period, organisationUnit)!=null && aggregatedDataValueService.getAggregatedValue(indicator, period, organisationUnit)>0){
           value = aggregatedDataValueService.getAggregatedValue(indicator, period, organisationUnit);
            }else {
            	noData++;
            }
            total = total + value;
       
            count--;
        }
        total = total / (years-noData);
       
        return currentVal / total;
    }
    public double getCurrentToAverageOFPreviousXYearsIndicatorRatio( OrganisationUnit organisationUnit, Indicator indicator, Date startDate, Date endDate,PeriodType periodType, int years )
    {
    	Date currentStartDate = startDate;
    	Date currentEndDate = endDate;
    	Date prevYearStartDate, prevYearEndDate;
    	int count = years;
    	double total = 0;
    	Double currentVal = 0.0;
    	int noData = 0;
    	
		currentVal = calculateThroughExpression(indicator, startDate, endDate, organisationUnit);
		System.out.println("current val="+currentVal);
		while ( count > 0 )
		{
			prevYearStartDate = getPreviousYear( currentStartDate );
		    prevYearEndDate = getPreviousYear( currentEndDate );
		    currentStartDate = prevYearStartDate;
		    currentEndDate = prevYearEndDate;
		    Double value = 0.0;
		  
		    try
		    {  
		    	value = calculateThroughExpression(indicator, prevYearStartDate, prevYearEndDate, organisationUnit);
		        System.out.println("value="+value);
		    }
		    catch(Exception e)
		    {
		    	value=null;
		    }
		    
		    if (value!=null)
		    {
		    	total = total + value;
		    }
		    else
		    {
		    	noData++;
		    }
		   
		    count--;
		}

		total = total / ( years - noData );
		System.out.println("total="+total);
		return currentVal / total;
    }

    private Date getPreviousYear( Date date )
    {
        Calendar prevDate = Calendar.getInstance();
        prevDate.setTime( date );
        prevDate.add( Calendar.YEAR, -1 );

        return prevDate.getTime();
    }

    public Collection<OrganisationUnit> getOrgUnitListForIndicatorRatio( Integer level1, Integer level2,
        Date startDate, Date endDate, Indicator indicator )
    {
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();

        List<OrganisationUnit> orgUnitListAtLavel1 = new ArrayList<OrganisationUnit>(
            organisationUnitService.getOrganisationUnitsAtLevel( level1 ) );

        List<OrganisationUnit> orgUnitListAtLavel2 = new ArrayList<OrganisationUnit>(
            organisationUnitService.getOrganisationUnitsAtLevel( level2 ) );

        OrganisationUnit orgUnitAtLavel1 = orgUnitListAtLavel1.get( 0 );

        OrganisationUnit OrganisationUnitAtNational = organisationUnitService.getOrganisationUnit( orgUnitAtLavel1
            .getId() );
        ;

        Double aggregatedIndicatorValueAtLavel1 = 0.0;

        aggregatedIndicatorValueAtLavel1 = aggregationService.getAggregatedIndicatorValue( indicator, startDate,
            endDate, OrganisationUnitAtNational );

        for ( OrganisationUnit orgUnit : orgUnitListAtLavel2 )
        {
            Double aggregatedIndicatorValueAtLavel2 = 0.0;

            aggregatedIndicatorValueAtLavel2 = aggregationService.getAggregatedIndicatorValue( indicator, startDate,
                endDate, orgUnit );
            Double value = 0.0;

            value = aggregatedIndicatorValueAtLavel2 / aggregatedIndicatorValueAtLavel1;

            if ( !(value >= 33 && value <= -33) )
            {
                orgUnitList.add( orgUnit );
            }
        }

        return orgUnitList;
    }

    public double getIndicatorValue(Indicator indicator,Date reportStartDate,
        Date reportEndDate,OrganisationUnit rootOrganisationUnit ){
        
        Double aggregatedIndicatorValueAtRootOrgUnit = 0.0;
        try{
        double aggregatedNumeratorIndicatorValueAtRootOrgUnit = aggregationService.getAggregatedNumeratorValue( indicator, reportStartDate,
            reportEndDate, rootOrganisationUnit );
        Collection<DataElement> des = expressionService.getDataElementsInExpression( indicator.getDenominator());
        printListMembers( des );
        Collection<DataValue> denominatorIndicatorValuesAtRootOrgUnit = dataValueService.getDataValues( rootOrganisationUnit, des.iterator().next() );
        //System.out.println("denominatorIndVal = " + denominatorIndicatorValuesAtRootOrgUnit.size() + "DE = " + des);
        int aggregatedDenominatorIndicatorValueAtRootOrgUnit = Integer.parseInt( denominatorIndicatorValuesAtRootOrgUnit.iterator().next().getValue());
        
        aggregatedIndicatorValueAtRootOrgUnit = aggregatedNumeratorIndicatorValueAtRootOrgUnit/aggregatedDenominatorIndicatorValueAtRootOrgUnit;
        }catch(Exception e){
            aggregatedIndicatorValueAtRootOrgUnit = 0.0;   
        }
        return aggregatedIndicatorValueAtRootOrgUnit;
        
    }
    private void printListMembers(Collection<DataElement> des){
        Iterator<DataElement> collectionIterator = des.iterator();
        while(collectionIterator.hasNext()){
            Object obj = collectionIterator.next();
            //System.out.println("obj = " + obj.toString());
        }
        
    }
    public HashSet<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentage(  OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate, Date endDate, Indicator indicator,
        int thresholdPercentage
      )
    {   
        HashSet<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();
        Collection<OrganisationUnit> orgUnitListAtLavel = analysisOrgUnits;

        Double aggregatedIndicatorValueAtRootOrgUnit = 0.0;
         
//        aggregatedIndicatorValueAtRootOrgUnit = aggregationService.getAggregatedIndicatorValue(  indicator, startDate,
//            endDate, rootOrganisationUnit );
       aggregatedIndicatorValueAtRootOrgUnit=calculateThroughExpression(indicator, startDate, endDate, rootOrganisationUnit);
          
        if ( aggregatedIndicatorValueAtRootOrgUnit != null )
        {
            for ( OrganisationUnit orgUnit : orgUnitListAtLavel )
            {
                Double aggregatedIndicatorValueAtLavel2 = 0.0;

                aggregatedIndicatorValueAtLavel2 =aggregationService.getAggregatedIndicatorValue( indicator,
                    startDate, endDate, orgUnit );
                aggregatedIndicatorValueAtLavel2=calculateThroughExpression(indicator, startDate, endDate, orgUnit);
                Double value = 0.0;

                //System.out.println("aggregatedlevel2" + aggregatedIndicatorValueAtLavel2 +"  root = "+aggregatedIndicatorValueAtRootOrgUnit);
                value = ((aggregatedIndicatorValueAtRootOrgUnit-aggregatedIndicatorValueAtLavel2) / aggregatedIndicatorValueAtRootOrgUnit)*100;
               
                if ( (value >= thresholdPercentage || value <= -thresholdPercentage) )
                {
                    if ( !isDuplicate(orgUnitList,orgUnit) )
                    {
                        orgUnitList.add( orgUnit );
                    }
                }
            }
        }
        return orgUnitList;
     }

    public HashSet<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentage(  OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Collection<Period> periods, Indicator indicator,
            int thresholdPercentage
          )
        {   
            HashSet<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();
            Collection<OrganisationUnit> orgUnitListAtLavel = analysisOrgUnits;

            Double aggregatedIndicatorValueAtRootOrgUnit = 0.0;
            
            System.out.println("periods="+periods);
            dataQueryParameter = new DataQueryParams();
            dataQueryParameter.setIndicator(indicator);
            dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
            dataQueryParameter.setFilterOrganisationUnit(rootOrganisationUnit);
            Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);

//            aggregatedIndicatorValueAtRootOrgUnit = aggregationService.getAggregatedIndicatorValue(  indicator, startDate,
//                endDate, rootOrganisationUnit );
            aggregatedIndicatorValueAtRootOrgUnit=(Double) grid.getValue(0, 2);
              
            if ( aggregatedIndicatorValueAtRootOrgUnit != null )
            {
                for ( OrganisationUnit orgUnit : orgUnitListAtLavel )
                {
                    Double aggregatedIndicatorValueAtLavel2 = 0.0;
                    dataQueryParameter.setFilterOrganisationUnit(orgUnit);
//                    aggregatedIndicatorValueAtLavel2 =aggregationService.getAggregatedIndicatorValue( indicator,
//                        startDate, endDate, orgUnit );
                    aggregatedIndicatorValueAtLavel2 = (Double) grid.getValue(0, 2);
                    Double value = 0.0;

                    //System.out.println("aggregatedlevel2" + aggregatedIndicatorValueAtLavel2 +"  root = "+aggregatedIndicatorValueAtRootOrgUnit);
                    value = ((aggregatedIndicatorValueAtRootOrgUnit-aggregatedIndicatorValueAtLavel2) / aggregatedIndicatorValueAtRootOrgUnit)*100;
                   
                    if ( (value >= thresholdPercentage || value <= -thresholdPercentage) )
                    {
                        if ( !isDuplicate(orgUnitList,orgUnit) )
                        {
                            orgUnitList.add( orgUnit );
                        }
                    }
                }
            }
            return orgUnitList;
         }

    
    public HashSet<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentageOverTime(
        OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate, Date endDate, Indicator indicator,
        int thresholdPercentage ,int years
        )
    {  
        HashSet<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();
        Double aggregatedIndicatorValueAtRootOrgUnit = 0.0;
         
        aggregatedIndicatorValueAtRootOrgUnit = getCurrentToAverageOFPreviousXYearsIndicatorRatio(  rootOrganisationUnit,indicator, startDate,
            endDate,years );
       
          
        if ( aggregatedIndicatorValueAtRootOrgUnit != null )
        {
            for ( OrganisationUnit orgUnit : analysisOrgUnits )
            {
                Double aggregatedIndicatorValueAtLavel2 = 0.0;

                aggregatedIndicatorValueAtLavel2 =getCurrentToAverageOFPreviousXYearsIndicatorRatio(  orgUnit,indicator, startDate,
                    endDate,years );
        //        System.out.println("orgunit = " +orgUnit.getDisplayName()+"root= " + aggregatedIndicatorValueAtRootOrgUnit + "oorg val = " + aggregatedIndicatorValueAtLavel2);
                Double value = 0.0;

               value = ((aggregatedIndicatorValueAtRootOrgUnit-aggregatedIndicatorValueAtLavel2 )/ aggregatedIndicatorValueAtRootOrgUnit)*100;
       //        System.out.println("% val = " + value); 
               if ( (value >= thresholdPercentage || value <= -thresholdPercentage) )
                {
                    if ( !isDuplicate(orgUnitList,orgUnit) )
                    {
                        orgUnitList.add( orgUnit );

                    }
                }
            }
        }
        return orgUnitList;
  }

    public HashSet<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentageOverTime(
            OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate, Date endDate,PeriodType periodType, Indicator indicator,
            int thresholdPercentage ,int years
            )
        {  
            HashSet<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();
            Double aggregatedIndicatorValueAtRootOrgUnit = 0.0;
             
            aggregatedIndicatorValueAtRootOrgUnit = getCurrentToAverageOFPreviousXYearsIndicatorRatio(  rootOrganisationUnit,indicator, startDate, endDate, periodType, years );
              
            if ( aggregatedIndicatorValueAtRootOrgUnit != null )
            {
                for ( OrganisationUnit orgUnit : analysisOrgUnits )
                {
                    Double aggregatedIndicatorValueAtLavel2 = 0.0;

                    aggregatedIndicatorValueAtLavel2 =getCurrentToAverageOFPreviousXYearsIndicatorRatio(  orgUnit,indicator, startDate, endDate,periodType,years );
            
                    System.out.println("+++Orgunit = " +orgUnit.getName()+" RootVal: " + aggregatedIndicatorValueAtRootOrgUnit + " OUVal = " + aggregatedIndicatorValueAtLavel2);
                    
                    Double value = 0.0;

                    value = ((aggregatedIndicatorValueAtRootOrgUnit-aggregatedIndicatorValueAtLavel2 )/ aggregatedIndicatorValueAtRootOrgUnit)*100;
                    
                    System.out.println("Orgunit : " + orgUnit.getName() + " % val = " + value + " Threshhold = " + thresholdPercentage); 
                    if ( ( Math.round( value ) >= thresholdPercentage || Math.round( value ) <= -thresholdPercentage) )
                    {
                        if ( !isDuplicate(orgUnitList,orgUnit) )
                        {
                            orgUnitList.add( orgUnit );

                        }
                    }
                }
            }
            return orgUnitList;
      }

    private boolean isDuplicate( HashSet<OrganisationUnit> orgUnitList, OrganisationUnit orgUnit )
    {
        // TODO Auto-generated method stub
        
        Iterator orgUnitIterator = orgUnitList.iterator();
        while(orgUnitIterator.hasNext()){
            OrganisationUnit ou = (OrganisationUnit) orgUnitIterator.next();
            if (orgUnit.equals( ou )){

            	return true;
            }
        }
        
        return false;
    }

    public Collection<OrganisationUnit> getOrgUnitListForIndicatorRatioForSurvey( Integer level1, Integer level2,
        Date startDate, Date endDate, Indicator indicator )
    {
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();

        List<OrganisationUnit> orgUnitListAtLavel1 = new ArrayList<OrganisationUnit>(
            organisationUnitService.getOrganisationUnitsAtLevel( level1 ) );

        List<OrganisationUnit> orgUnitListAtLavel2 = new ArrayList<OrganisationUnit>(
            organisationUnitService.getOrganisationUnitsAtLevel( level2 ) );

        OrganisationUnit orgUnitAtLavel1 = orgUnitListAtLavel1.get( 0 );

        OrganisationUnit OrganisationUnitAtNational = organisationUnitService.getOrganisationUnit( orgUnitAtLavel1
            .getId() );
     
        Double aggregatedIndicatorValueAtLavel1 = 0.0;

        aggregatedIndicatorValueAtLavel1 = aggregationService.getAggregatedIndicatorValue( indicator, startDate,
            endDate, OrganisationUnitAtNational );

        for ( OrganisationUnit orgUnit : orgUnitListAtLavel2 )
        {
            Double aggregatedIndicatorValueAtLavel2 = 0.0;

            aggregatedIndicatorValueAtLavel2 = aggregationService.getAggregatedIndicatorValue( indicator, startDate,
                endDate, orgUnit );

            Double value = 0.0;

            value = aggregatedIndicatorValueAtLavel2 / aggregatedIndicatorValueAtLavel1;

            if ( value > 33 )
            {
                orgUnitList.add( orgUnit );
            }
        }

        return orgUnitList;
    }

    public int getCountOfIncompleteMonthlyDataForAYear( OrganisationUnit organisationUnit, Date yearStartDate,
        Indicator indicator )
    {
    	 PeriodType monthlyPeriodType = periodService.getPeriodTypeByName(PERIODTYPENAME);
        Collection<Date> endDates = getMonthWiseEndDatesForAYear( yearStartDate );
        Iterator<Date> endDatesIterator = endDates.iterator();
        int count = 0;
        Calendar cal = Calendar.getInstance();
        while ( endDatesIterator.hasNext() )
        {
        	Double result = null;
            Date endDate = endDatesIterator.next();
            cal.setTime( endDate );
            cal.set( Calendar.DATE, 1 );
            Date startDate = cal.getTime();
            System.out.println("startdate="+startDate+"enddate="+endDate);
            
            Collection<Period> periods = periodService.getPeriodsBetweenDates(monthlyPeriodType,startDate, endDate);
            dataQueryParameter = new DataQueryParams();
            dataQueryParameter.setIndicator(indicator);
            dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
            dataQueryParameter.setFilterOrganisationUnit(organisationUnit);
            Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
            // value = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,organisationUnit );
            if (grid!=null && !grid.getVisibleRows().isEmpty())
            	 result = (Double) grid.getValue(0, 2);
//            double result = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate,
//                organisationUnit );
            //System.out.println("orgunit = " + organisationUnit.getDisplayName()+"result = " + result + "startdate = " + startDate + "enddate = " + endDate );
            if ( result==null || result <= 0 )
            {
                count++;
            }
        }
      
        return count;

    }

    public int getCountOfIncompleteMonthlyDataForAYear( Collection<OrganisationUnit> organisationUnits, Date yearStartDate,
            Indicator indicator ){
		int result = 0;
    	
		for (OrganisationUnit orgUnit : organisationUnits){
			result += getCountOfIncompleteMonthlyDataForAYear( orgUnit, yearStartDate, indicator );
		}
    	
    	return result;
    	
    }
    public HashSet<OrganisationUnit> getOrgUnitsHavingIncompleteMonthlyDataBeyondThresholdPercentageForAYear(
        Collection<OrganisationUnit> organisationUnits, Date yearStartDate, Indicator indicator, int thresholdPercentage )
    {
    	Iterator<OrganisationUnit> orgUnitIterator = organisationUnits.iterator();
        HashSet<OrganisationUnit> incompleteDataOrgUnits = new HashSet<OrganisationUnit>();
        while ( orgUnitIterator.hasNext() )
        {
            OrganisationUnit ou = orgUnitIterator.next();
            int value = getCountOfIncompleteMonthlyDataForAYear( ou, yearStartDate, indicator );
            if ( (value / 12) * 100 >= 20 )
            {
                incompleteDataOrgUnits.add( ou );
            }
        }

        return incompleteDataOrgUnits;

    }
    
//    public List<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage(OrganisationUnit rootOrganisationUnit,Collection<OrganisationUnit> orgUnitForAnalysis,Date startDate, Date endDate,Indicator indicator,int thresholdPercentage){
//    	
//    	List<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();    	
//    	for(OrganisationUnit orgUnit : orgUnitForAnalysis){
//    		double ratio = getIndicatorAggregatedNumeratorValueByDenominatorDataValue(orgUnit, indicator, startDate, endDate);
//    		double tempVal=0;
//    		if (ratio >= 1){
//    			tempVal = ratio - 1;
//    		}else {
//    			tempVal = 1 - ratio;
//    		}
//    		tempVal = tempVal*100;
//    		System.out.println("org unit=" + orgUnit.getDisplayName() + "ratio = " + ratio +"tempVal ="+ tempVal);
//    		
//    		if ( tempVal > thresholdPercentage){
//    		orgUnits.add(orgUnit);
//    		}
//    	}
//    	
//		return orgUnits;
//    }
  public List<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage(OrganisationUnit rootOrganisationUnit,Collection<OrganisationUnit> orgUnitForAnalysis,Date startDate, Date endDate,Indicator indicator,int thresholdPercentage){
    	
    	List<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();    	
    	for(OrganisationUnit orgUnit : orgUnitForAnalysis){
    		double ratio = getIndicatorAggregatedNumeratorValueByDenominatorDataValue(orgUnit, indicator, startDate, endDate);
    		double tempVal=0;
    		if (ratio >= 1){
    			tempVal = ratio - 1;
    		}else {
    			tempVal = 1 - ratio;
    		}
    		tempVal = tempVal*100;
    		System.out.println("org unit=" + orgUnit.getDisplayName() + "ratio = " + ratio +"tempVal ="+ tempVal);
    		
    		if ( tempVal > thresholdPercentage){
    		orgUnits.add(orgUnit);
    		}
    	}
    	
		return orgUnits;
    }
    
  
  public List<OrganisationUnitGroup> getOrgUnitGroupsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage(OrganisationUnit rootOrganisationUnit,Set<OrganisationUnitGroup> organisationUnitGroups,Date startDate, Date endDate,Indicator indicator,int thresholdPercentage){
  	
	  List<OrganisationUnitGroup> orgUnitGroups = new ArrayList<OrganisationUnitGroup>();
  	for (OrganisationUnitGroup organisationUnitGroup : organisationUnitGroups){
  		
  		double ratio = getIndicatorAggregatedNumeratorValueByDenominatorDataValue(organisationUnitGroup.getMembers(), indicator, startDate, endDate);
  		double tempVal=0;
  		if (ratio >= 1){
  			tempVal = ratio - 1;
  		}else {
  			tempVal = 1 - ratio;
  		}
  		tempVal = tempVal*100;
  		System.out.println("org unitgroup=" + organisationUnitGroup.getDisplayName() + "ratio = " + ratio +"tempVal ="+ tempVal);
  		
  		if ( tempVal > thresholdPercentage){
  			orgUnitGroups.add(organisationUnitGroup);
  		}
  	}
  	
		return orgUnitGroups;
  }
    public List<OrganisationUnit> getOrgUnitsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage2(OrganisationUnit rootOrganisationUnit,Collection<OrganisationUnit> orgUnitForAnalysis,Date startDate, Date endDate,Indicator indicator,int thresholdPercentage){
    	
    	List<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();    	
    	for(OrganisationUnit orgUnit : orgUnitForAnalysis){
    		double ratio = getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(orgUnit, indicator, startDate, endDate);
    		
    		double tempVal=0;
    		if (ratio >= 1){
    			tempVal = ratio - 1;
    		}else {
    			tempVal = 1 - ratio;
    		}
    		tempVal = tempVal*100;
    		System.out.println("org unit=" + orgUnit.getDisplayName() + "ratio = " + ratio +"tempVal ="+ tempVal);
    		
    		if ( tempVal > thresholdPercentage){
    		orgUnits.add(orgUnit);
    		}
    	}
    	
    	
		return orgUnits;
    }
    
    	public List<OrganisationUnitGroup> getOrgUnitGroupsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage2(OrganisationUnit rootOrganisationUnit,Set<OrganisationUnitGroup> organisationUnitGroups,Date startDate, Date endDate,Indicator indicator,int thresholdPercentage){
    	
    	List<OrganisationUnitGroup> orgUnits = new ArrayList<OrganisationUnitGroup>();
    	for (OrganisationUnitGroup organsationUnitGroup : organisationUnitGroups){
   
    		double ratio = getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(organsationUnitGroup.getMembers(), indicator, startDate, endDate);
    		
    		double tempVal=0;
    		if (ratio >= 1){
    			tempVal = ratio - 1;
    		}else {
    			tempVal = 1 - ratio;
    		}
    		tempVal = tempVal*100;
    		System.out.println("org unitgroup=" + organsationUnitGroup.getDisplayName() + "ratio = " + ratio +"tempVal ="+ tempVal);
    		
    		if ( tempVal > thresholdPercentage){
    		orgUnits.add(organsationUnitGroup);
    		}
    	    
    	}
		return orgUnits;
    } 
//    public double getIndicatorAggregatedNumeratorValueByDenominatorDataValue(OrganisationUnit organisationUnit,Indicator indicator,Date startDate,Date endDate){
//    	double result = 0.0;
//        Double aggIndicatorNumValue = 0.0;
//        
//        
//        aggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator, startDate, endDate, organisationUnit );
//        
//        
//        if ( aggIndicatorNumValue == null ) aggIndicatorNumValue = 0.0;
//        
//        System.out.println( " aggIndicatorValue - >" + aggIndicatorNumValue );
//        
//       // aggIndicatorNumValue = Math.round( aggIndicatorNumValue * Math.pow( 10, 2 ) )/ Math.pow( 10, 2 );
//        
//        //System.out.println( " aggIndicatorNumValue - >" + aggIndicatorNumValue );
//        System.out.println("startdate="+startDate + "endDate = "+endDate);
//        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );        
//        System.out.println("periods size= " + periods.size());
//        
//        List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getDenominator()));
//        
//        DataElement dataElement = des.get( 0 );
//        
//        DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();        
//        //System.out.println( " dataElement id is  - >" + dataElement.getId() + " -- dataElement name is  "  + dataElement.getName());
//        Double aggIndicatorDenumValue = 0.0;
//        
//         for ( Period period : periods )
//        {
//            DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, decoc );
//            try
//            {
//                aggIndicatorDenumValue += Double.parseDouble( dataValue.getValue() );
//            }
//            catch ( Exception e )
//            {
//                
//            }
//        }
//         System.out.println("aggdenomin - >" + aggIndicatorDenumValue);
//         result = aggIndicatorNumValue/aggIndicatorDenumValue;
//    	return result;
//    }
  
    public double getIndicatorAggregatedNumeratorValueByDenominatorDataValue(OrganisationUnit organisationUnit,Indicator indicator,Date startDate,Date endDate){
    	double result = 0.0;
        Double aggIndicatorNumValue = 0.0;
        
        Collection<Period> periods = null;
        
        aggIndicatorNumValue = generateExpression(indicator.getNumerator(), startDate,endDate, organisationUnit);
        
        
        if ( aggIndicatorNumValue == null ) aggIndicatorNumValue = 0.0;
        
        System.out.println( " aggIndicatorValue - >" + aggIndicatorNumValue );
        
               
        List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getDenominator()));
        
        DataElement dataElement = des.get( 0 );
        
        DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();        
        //System.out.println( " dataElement id is  - >" + dataElement.getId() + " -- dataElement name is  "  + dataElement.getName());
        Double aggIndicatorDenumValue = 0.0;
        if (serviceDEs.contains(dataElement)){
			periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
			System.out.println("dataelement="+dataElement+"startdate="+startDate+"enddate="+endDate);	
		}else{
			periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
			System.out.println("dataelement="+dataElement+"startdate="+annualStartDate+"enddate="+annualEndDate);	
		}
         for ( Period period : periods )
        {
            DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, decoc );
            try
            {
                aggIndicatorDenumValue += Double.parseDouble( dataValue.getValue() );
            }
            catch ( Exception e )
            {
                
            }
        }
         System.out.println("aggdenomin - >" + aggIndicatorDenumValue);
         result = aggIndicatorNumValue/aggIndicatorDenumValue;
    	return result;
    }
  
    public double getIndicatorAggregatedNumeratorValueByDenominatorDataValue(Set<OrganisationUnit> organisationUnits,Indicator indicator,Date startDate,Date endDate){
    	double result = 0.0;
        Double aggIndicatorNumValue = 0.0;
        
        Collection<Period> periods = null;
        
        aggIndicatorNumValue = generateExpression(indicator.getNumerator(), startDate,endDate, organisationUnits);
        
        
        if ( aggIndicatorNumValue == null ) aggIndicatorNumValue = 0.0;
        
        System.out.println( " aggIndicatorValue - >" + aggIndicatorNumValue );
        
       // aggIndicatorNumValue = Math.round( aggIndicatorNumValue * Math.pow( 10, 2 ) )/ Math.pow( 10, 2 );
        
        //System.out.println( " aggIndicatorNumValue - >" + aggIndicatorNumValue );
        System.out.println("startdate="+startDate + "endDate = "+endDate);
        periods = periodService.getPeriodsBetweenDates( startDate, endDate );        
        System.out.println("periods size= " + periods.size());
        
        List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getDenominator()));
        
        DataElement dataElement = des.get( 0 );
        
        DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();        
        //System.out.println( " dataElement id is  - >" + dataElement.getId() + " -- dataElement name is  "  + dataElement.getName());
        Double aggIndicatorDenumValue = 0.0;
        
        if (serviceDEs.contains(dataElement)){
			periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
			System.out.println("dataelement="+dataElement+"startdate="+startDate+"enddate="+endDate);	
		}else{
			periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
			System.out.println("dataelement="+dataElement+"startdate="+annualStartDate+"enddate="+annualEndDate);	
		}
         for ( OrganisationUnit organisationUnit : organisationUnits )
        {
        	 for (Period period:periods){
        		 DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, decoc );
        		 System.out.println("period="+period);
        		 try
        		 {
        			 aggIndicatorDenumValue = Double.parseDouble( dataValue.getValue() );
        			 if (aggIndicatorDenumValue!=null)
            			 break;
        		 }        		 
        		 catch ( Exception e )
        		 {
        			 e.printStackTrace();
        		 }
        	 }
        	 if (aggIndicatorDenumValue!=null)
    			 break;
        }
         System.out.println("numer="+aggIndicatorNumValue+"denomi"+aggIndicatorDenumValue);
        
         result = aggIndicatorNumValue/aggIndicatorDenumValue;
    	return result;
    }
  
    
//    public double getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(OrganisationUnit organisationUnit,Indicator indicator,Date startDate,Date endDate){
//    	double result = 0.0;
//        Double aggIndicatorNumValue = 0.0;
//        
//        aggIndicatorNumValue = aggregationService.getAggregatedNumeratorValue( indicator, startDate, endDate, organisationUnit );
//        
//        
//        if ( aggIndicatorNumValue == null ) aggIndicatorNumValue = 0.0;
//        
//        //System.out.println( " aggIndicatorValue - >" + aggIndicatorNumValue );
//        
//        //aggIndicatorNumValue = Math.round( aggIndicatorNumValue * Math.pow( 10, 2 ) )/ Math.pow( 10, 2 );
//        
//        //System.out.println( " aggIndicatorNumValue - >" + aggIndicatorNumValue );
//        System.out.println("startdate="+startDate + "endDate = "+endDate);
//        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );        
//        System.out.println("periods size= " + periods.size());
//        
//        List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getDenominator()));
//        
//        DataElement dataElement = des.get( 0 );
//        
//        DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();        
//        //System.out.println( " dataElement id is  - >" + dataElement.getId() + " -- dataElement name is  "  + dataElement.getName());
//        Double aggIndicatorDenumValue = 0.0;
//        
//         for ( Period period : periods )
//        {
//            DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, decoc );
//            try
//            {
//                aggIndicatorDenumValue += Double.parseDouble( dataValue.getValue() );
//            }
//            catch ( Exception e )
//            {
//                
//            }
//        }
//         
//         result = (aggIndicatorNumValue*aggIndicatorDenumValue)/100;
//    	return result;
//    }
    
    public double getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(OrganisationUnit organisationUnit,Indicator indicator,Date startDate,Date endDate){
    	double result = 0.0;
        Double aggIndicatorNumValue = 0.0;
        
        Collection<Period> periods =null;
        
        aggIndicatorNumValue = generateExpression(indicator.getNumerator(), startDate, endDate, organisationUnit);
      
        
        if ( aggIndicatorNumValue == null ) aggIndicatorNumValue = 0.0;
        
        //System.out.println( " aggIndicatorValue - >" + aggIndicatorNumValue );
        
        //aggIndicatorNumValue = Math.round( aggIndicatorNumValue * Math.pow( 10, 2 ) )/ Math.pow( 10, 2 );
        
        //System.out.println( " aggIndicatorNumValue - >" + aggIndicatorNumValue );
        System.out.println("startdate="+startDate + "endDate = "+endDate);
       
        List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getDenominator()));
        
        DataElement dataElement = des.get( 0 );
        
        DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();        
        //System.out.println( " dataElement id is  - >" + dataElement.getId() + " -- dataElement name is  "  + dataElement.getName());
        Double aggIndicatorDenumValue = 0.0;
        
        System.out.println("-------------dataelement="+dataElement+"orga="+organisationUnit);
        
        if (serviceDEs.contains(dataElement)){
			periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
			System.out.println("dataelement="+dataElement+"startdate="+startDate+"enddate="+endDate);	
		}else{
			periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
			System.out.println("dataelement="+dataElement+"startdate="+annualStartDate+"enddate="+annualEndDate);	
		}
         for ( Period period : periods )
        {
            DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, decoc );
            System.out.println("period="+period);
            try
            {
                aggIndicatorDenumValue += Double.parseDouble( dataValue.getValue() );
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }
         System.out.println("numer="+aggIndicatorNumValue+"denomi"+aggIndicatorDenumValue);
         result = (aggIndicatorNumValue*aggIndicatorDenumValue)/100;
         System.out.println("result========"+result);
    	return result;
    }
  
    public double getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(Set<OrganisationUnit> organisationUnits,Indicator indicator,Date startDate,Date endDate ){
    	double result = 0.0;
        Double aggIndicatorNumValue = 0.0;

        Collection<Period> periods =null;
        System.out.println("#################size org units---------------"+organisationUnits.size());
        aggIndicatorNumValue = generateExpression(indicator.getNumerator(), startDate, endDate, organisationUnits);
      
        
        if ( aggIndicatorNumValue == null ) aggIndicatorNumValue = 0.0;
        
        //System.out.println( " aggIndicatorValue - >" + aggIndicatorNumValue );
        
        //aggIndicatorNumValue = Math.round( aggIndicatorNumValue * Math.pow( 10, 2 ) )/ Math.pow( 10, 2 );
        
        //System.out.println( " aggIndicatorNumValue - >" + aggIndicatorNumValue );
        System.out.println("startdate="+startDate + "endDate = "+endDate);
       
        List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getDenominator()));
        
        DataElement dataElement = des.get( 0 );
        
        DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();        
        //System.out.println( " dataElement id is  - >" + dataElement.getId() + " -- dataElement name is  "  + dataElement.getName());
        Double aggIndicatorDenumValue = null;
        
        System.out.println("-------------dataelement="+dataElement);
        
        if (serviceDEs.contains(dataElement)){
			periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
			System.out.println("dataelement="+dataElement+"startdate="+startDate+"enddate="+endDate);	
		}else{
			periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
			System.out.println("dataelement="+dataElement+"startdate="+annualStartDate+"enddate="+annualEndDate);	
		}
         for ( OrganisationUnit organisationUnit : organisationUnits )
        {
        	 for (Period period:periods){
        		 DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, decoc );
        		 System.out.println("period="+period);
        		 try
        		 {
        			 aggIndicatorDenumValue = Double.parseDouble( dataValue.getValue() );
        			 if (aggIndicatorDenumValue!=null)
            			 break;
        		 }        		 
        		 catch ( Exception e )
        		 {
        			 e.printStackTrace();
        		 }
        	 }
        	 if (aggIndicatorDenumValue!=null)
    			 break;
        }
         System.out.println("numer="+aggIndicatorNumValue+"denomi"+aggIndicatorDenumValue);
         result = (aggIndicatorNumValue*aggIndicatorDenumValue)/100;
         System.out.println("result========"+result);
    	return result;
    }
  
    
    public double getIndicatorDenominatorDataValue(Indicator indicator,Date startDate,Date endDate,OrganisationUnit organisationUnit){
    	double result = 0.0;
    	
    	   
           List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getDenominator()));
           Collection<Period> periods = null;
           DataElement dataElement = des.get( 0 );
           if (serviceDEs.contains(dataElement)){
        	   
           }
            periods= periodService.getPeriodsBetweenDates( annualStartDate, annualEndDate );        
           System.out.println("periods size= " + periods.size());
          
           DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();        
           //System.out.println( " dataElement id is  - >" + dataElement.getId() + " -- dataElement name is  "  + dataElement.getName());
           Double aggIndicatorDenumValue = 0.0;
           
            for ( Period period : periods )
           {
               DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period, decoc );
               try
               {
            	   System.out.println("period="+period + "aggIndicatordenumValue="+aggIndicatorDenumValue); 
                   aggIndicatorDenumValue += Double.parseDouble( dataValue.getValue() );
               }
               catch ( Exception e )
               {
                   
               }
           }
          
        result = aggIndicatorDenumValue;
    	return result;
    }
    private Collection<Date> getMonthWiseEndDatesForAYear( Date date )
    {
        int count = 0;
        List<Date> dates = new ArrayList<Date>();
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        int month = cal.get( Calendar.MONTH );
        int year = cal.get( Calendar.YEAR );
        int day = 1;
        System.out.println("month="+month);
        while ( count < 12 )
        {
            day = MONTHENDDAYS[month];
            if ( month == 2 )
            {
                if ( isLeapYear( year ) )
                {
                    day = MONTHENDDAYS[month] + 1;
                }
                else
                {
                    day = MONTHENDDAYS[month];
                }
            }

            cal.set( Calendar.YEAR, year );
            cal.set( Calendar.MONTH, month );
            cal.set( Calendar.DATE, day );
            Date dd = cal.getTime();
            dates.add( dd );
            month = month + 1;
            if ( month >= 12 )
            {
                month = month % 12;
                year = year + 1;
            }
            count++;
        }

        return dates;

    }

//    private double generateExpression( String expression, Collection<Period> periods, OrganisationUnit organisationUnit )
//    {
//        Map<String, Double> constantMap = constantService.getConstantMap();
//        
//        Set<DataElementOperand> operands = expressionService.getOperandsInExpression( expression );
//        
//        Map<DataElementOperand, Double> valueMap = new HashMap<DataElementOperand, Double>();
//        Double value = null;
//        for ( DataElementOperand operand : operands )
//        {
//            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
//            DataElementCategoryOptionCombo optionCombo = !operand.isTotal() ? dataElementCategoryService.getDataElementCategoryOptionCombo( operand.getOptionComboId() ) : null;
//            dataQueryParameter = new DataQueryParams();
//            dataQueryParameter.setDataElement(dataElement);
//            dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
//            dataQueryParameter.setFilterOrganisationUnit(organisationUnit);
//         
//            Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
//            System.out.println("grid="+grid);
//            if (grid!=null && !grid.getVisibleRows().isEmpty()){
//            value = (Double) grid.getValue(0, 2);
//           
//            }
//            valueMap.put( operand, value );            
//        }
//        System.out.println("----->"+expressionService.generateExpression( expression, valueMap, constantMap, null, false ));
//        return calculateExpression(expressionService.generateExpression( expression, valueMap, constantMap, null, false ));
//    }
    
    private double generateExpression( String expression, Date startDate,Date enddate, OrganisationUnit organisationUnit )
    {// period will be calculated on the basis of dataelement.getperiodtype method    	
        Map<String, Double> constantMap = constantService.getConstantMap();
        Collection<Period> periods;
        Set<DataElementOperand> operands = expressionService.getOperandsInExpression( expression );
        
        Map<DataElementOperand, Double> valueMap = new HashMap<DataElementOperand, Double>();
        Double value = null;
        for ( DataElementOperand operand : operands )
        {
            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
            DataElementCategoryOptionCombo optionCombo = !operand.isTotal() ? dataElementCategoryService.getDataElementCategoryOptionCombo( operand.getOptionComboId() ) : null;
            dataQueryParameter = new DataQueryParams();
            dataQueryParameter.setDataElement(dataElement);
            PeriodType periodType;
       
            if (serviceDEs.contains(dataElement)){
				periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, enddate);
				System.out.println("dataelement="+dataElement+"startdate="+startDate+"enddate="+enddate);	
			}else{
				periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
				System.out.println("dataelement="+dataElement+"startdate="+annualStartDate+"enddate="+annualEndDate);	
			}
            
             
           // dataQueryParameter.setFilterPeriods(periods);
            dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
            dataQueryParameter.setFilterOrganisationUnit(organisationUnit);
         
            Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
            System.out.println("grid="+grid);
            if (grid!=null && !grid.getVisibleRows().isEmpty()){
            value = (Double) grid.getValue(0, 2);           
            }
            valueMap.put( operand, value );            
        }
        System.out.println("----->"+expressionService.generateExpression( expression, valueMap, constantMap, null, false ));
        return calculateExpression(expressionService.generateExpression( expression, valueMap, constantMap, null, false ));
    }

    private double generateExpression( String expression, Date startDate,Date enddate, Collection<OrganisationUnit> organisationUnits )
    {// period will be calculated on the basis of dataelement.getperiodtype method    	
        Map<String, Double> constantMap = constantService.getConstantMap();
        Collection<Period> periods;
        Set<DataElementOperand> operands = expressionService.getOperandsInExpression( expression );
        
        Map<DataElementOperand, Double> valueMap = new HashMap<DataElementOperand, Double>();
        Double value = null;
        for ( DataElementOperand operand : operands )
        {
            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
            DataElementCategoryOptionCombo optionCombo = !operand.isTotal() ? dataElementCategoryService.getDataElementCategoryOptionCombo( operand.getOptionComboId() ) : null;
            dataQueryParameter = new DataQueryParams();
            dataQueryParameter.setDataElement(dataElement);
            PeriodType periodType;
       
            if (serviceDEs.contains(dataElement)){
				periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, enddate);
				System.out.println("dataelement="+dataElement+"startdate="+startDate+"enddate="+enddate);	
			}else{
				periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
				System.out.println("dataelement="+dataElement+"startdate="+annualStartDate+"enddate="+annualEndDate);	
			}
            
             
           // dataQueryParameter.setFilterPeriods(periods);
            dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
            for(OrganisationUnit organisationUnit:organisationUnits){
            	dataQueryParameter.setFilterOrganisationUnit(organisationUnit);
         
            	Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
            	System.out.println("grid="+grid);
            	if (grid!=null && !grid.getVisibleRows().isEmpty()){
            		if (value==null){
            		value = (Double) grid.getValue(0, 2);
            		
            		}else{
            			value += (Double) grid.getValue(0, 2);
            		}
            		System.out.println("orgUnit="+organisationUnit+" value="+value);
            	}
            }
            valueMap.put( operand, value ); 
            value=null;
        }
        System.out.println("----->"+expressionService.generateExpression( expression, valueMap, constantMap, null, false ));
        return calculateExpression(expressionService.generateExpression( expression, valueMap, constantMap, null, false ));
    }

    
    public double calculateThroughExpression(Indicator indicator,Date startDate,Date endDate, OrganisationUnit organisationUnit ){
		
    	return generateExpression(indicator.getNumerator(), startDate,endDate, organisationUnit) / generateExpression(indicator.getDenominator(), startDate,endDate, organisationUnit);    	
    }
    
    public static boolean isLeapYear( int year )
    {
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, year );
        return cal.getActualMaximum( Calendar.DAY_OF_YEAR ) > 365;
    }

    @Override
    public int getNoOfExtremeOutliers( OrganisationUnit organisationUnit, List<Period> periods, Indicator indicator )
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<OrganisationUnit> getOrganisationUnitsHavingAtLeastOneExtremeOutlier(
        List<OrganisationUnit> organisationUnits, List<Period> periods, Indicator indicator )
    {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public boolean generatechart2b(  
			OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate, Date endDate,PeriodType periodType, Indicator indicator,
	        int years,String chartName) {
		// get values for all orgunits : mean and current
		List<Double> meanValues = getMeanValueList(analysisOrgUnits,  startDate,  endDate,periodType,  indicator, years);
		List<Double> currentValues = getCurrentValueList(analysisOrgUnits,  startDate,  endDate,periodType,  indicator);
		String commaSeparatedMeanValues = commafyList(meanValues);
		String commaSeparatedCurrentValues = commafyList(currentValues);
		
		double maxMeanValue = Collections.max(meanValues);
		System.out.println("max mean value = " + maxMeanValue);
		
		maxMeanValue = Math.round((maxMeanValue + 500)/1000) *1000;
		double rootRatio = getCurrentToAverageOFPreviousXYearsIndicatorRatio( rootOrganisationUnit,
                indicator, startDate, endDate,periodType, years );
		
		// pass them to r script
		// Rscript chart1.R commaSeparatedMeanvalues commaSeparatedCurrentValues maxMeanValue rootRatio 
		   String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator;
	       String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "rscript" +  File.separator + "dqaChart2b.R" + " \"" +commaSeparatedMeanValues+"\" \""+commaSeparatedCurrentValues +"\" "+ maxMeanValue + " " + rootRatio + " " + chartOutputPath + " " + chartName;
	       
	        System.out.println( "rScriptPath : "+ rScriptPath );
	        //rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_A + " " + currentQuarterMonth3End + " " + chartOutputPath;
	        try 
	        {
	            Runtime rt = Runtime.getRuntime();
	            Process pr;
	            pr = rt.exec( rScriptPath );
	            int exitVal = pr.waitFor();
	            
	            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
	        }
	        catch( Exception e ) 
	        {
	        	e.printStackTrace();
	        }
		return false;
	}
	
	@Override
	public boolean generatechart2c(  
			OrganisationUnit rootOrganisationUnit,Collection<OrganisationUnit> analysisOrgUnits, Date startDate, Date endDate,PeriodType periodType, Indicator indicator,
	        String chartName) {
		// get values for all orgunits : mean and current
		List<Double> de1Values = getIndicatorDenominatorValuesList(analysisOrgUnits, startDate, endDate,periodType, indicator);
		List<Double> de2Values = getIndicatorNumeratorValuesList(analysisOrgUnits, startDate, endDate,periodType, indicator);
		String commaSeparatedDe1Values = commafyList(de1Values);
		String commaSeparatedDe2Values = commafyList(de2Values);
		
		double maxValue = Collections.max(de1Values);
		System.out.println("max value = " + maxValue);
		
		maxValue = Math.round((maxValue + 500)/1000) *1000;
		
		//double rootRatio = aggregationService.getAggregatedIndicatorValue(indicator, startDate, endDate, rootOrganisationUnit);
		//Double rootRatio = (Double) grid.getValue(0, 2);
		Double rootRatio=calculateThroughExpression(indicator, startDate, endDate, rootOrganisationUnit);
		// pass them to r script
		// Rscript chart1.R commaSeparatedMeanvalues commaSeparatedCurrentValues maxMeanValue rootRatio 
		   String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator;
	       String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "rscript" +  File.separator + "dqaChart2c.R" + " \"" +commaSeparatedDe1Values+"\" \""+commaSeparatedDe2Values +"\" "+ maxValue + " " + rootRatio + " " + chartOutputPath + " " + chartName;
	       
	        System.out.println( "rScriptPath : "+ rScriptPath );
	        //rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_A + " " + currentQuarterMonth3End + " " + chartOutputPath;
	        try 
	        {
	            Runtime rt = Runtime.getRuntime();
	            Process pr;
	            pr = rt.exec( rScriptPath );
	            int exitVal = pr.waitFor();
	            
	            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
	        }
	        catch( Exception e ) 
	        {
	        	e.printStackTrace();
	        }
		return false;
	}
	@Override
	public boolean generatechart2d(  
			List<Double> values,List<String> ouNames,String chartName) {
		
		// get values for all orgunits : mean and current
		String commaSeparatedValues = commafyList(values);
		String commaSeparatedOuNames =commafyStringList(ouNames);
		

			// pass them to r script
		// Rscript chart1.R commaSeparatedMeanvalues commaSeparatedCurrentValues maxMeanValue rootRatio 
		   String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator;
	       String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "rscript" +  File.separator + "dqaChart2d.R" + " \"" +commaSeparatedValues+"\" \""+commaSeparatedOuNames +"\" "+  chartOutputPath + " " + chartName;
	       
	        System.out.println( "rScriptPath : "+ rScriptPath );
	        //rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_A + " " + currentQuarterMonth3End + " " + chartOutputPath;
	        try 
	        {
	            Runtime rt = Runtime.getRuntime();
	            Process pr;
	            pr = rt.exec( rScriptPath );
	            int exitVal = pr.waitFor();
	            
	            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
	        }
	        catch( Exception e ) 
	        {
	        	e.printStackTrace();
	        }
		return false;
	}
	
	@Override
	public boolean generatechart4(  
			OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate, Date endDate, Indicator indicator,DataElement standardErrorDE,String chartName) {
 		// get values for all orgunits : mean and current
		List<Double> values = new ArrayList<Double>();
		List<Double> surveyValues = new ArrayList<Double>();
		List<Double> standardError = new ArrayList<Double>();
		List<String> ouNames = new ArrayList<String>();
		TreeMap<Double,OrganisationUnit> valueOrgUnittreeMap = new TreeMap<Double, OrganisationUnit>();
		TreeMap<Double,Double> valueSurveyValueTreeMap = new TreeMap<Double, Double>();
		TreeMap<Double,Double> standardErrorTreeMap = new TreeMap<Double, Double>();
		
		Collection<OrganisationUnit> analysisOrgUnitsLocal = new ArrayList<OrganisationUnit>();
		analysisOrgUnitsLocal.addAll(analysisOrgUnits);
		//analysisOrgUnitsLocal.add(rootOrganisationUnit);
		  PeriodType periodType = periodService.getPeriodTypeByName("Yearly");
		  Period period = periodService.getPeriod(annualStartDate, annualEndDate, periodType);
		  Collection<Period> periods = periodService.getPeriodsBetweenDates(periodType,startDate, endDate);
		  DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();      
			System.out.println("period=" + period);
		//double rootVal  = aggregationService.getAggregatedNumeratorValue(indicator, startDate, endDate, rootOrganisationUnit);
		Double rootVal = generateExpression(indicator.getNumerator(), startDate,endDate, rootOrganisationUnit);
		
		double rootSurveyVal = getIndicatorDenominatorDataValue(indicator, startDate, endDate, rootOrganisationUnit);
		DataValue rootDataValue =  dataValueService.getDataValue(rootOrganisationUnit, standardErrorDE, period,decoc);
		
		double rootStandardError = 0.0;
		if (rootDataValue!=null){
		rootStandardError = Double.parseDouble(rootDataValue.getValue());
		}
		// preappend root values
				values.add(rootVal);
				surveyValues.add(rootSurveyVal);
				standardError.add(rootStandardError);
				ouNames.add(rootOrganisationUnit.getDisplayName());
				
		
		for (OrganisationUnit organisationUnit : analysisOrgUnitsLocal){
 			//double val  = aggregationService.getAggregatedNumeratorValue(indicator, startDate, endDate, organisationUnit);
 			Double val  = generateExpression(indicator.getNumerator(), startDate,endDate, organisationUnit);
			
 			double surveyVal = getIndicatorDenominatorDataValue(indicator, startDate, endDate, organisationUnit);
			DataValue dataValue =  dataValueService.getDataValue(organisationUnit, standardErrorDE, period,decoc);
			
			double SE = 0.0;
			if (dataValue!=null){
			SE = Double.parseDouble(dataValue.getValue());
			}
		
			valueOrgUnittreeMap.put(val, organisationUnit);
			valueSurveyValueTreeMap.put(val, surveyVal);
			standardErrorTreeMap.put(val, SE);
			System.out.println("se value="+SE);
		}
		
		
		values.addAll(valueOrgUnittreeMap.descendingMap().keySet());
		surveyValues.addAll(valueSurveyValueTreeMap.descendingMap().values());	
		standardError.addAll(standardErrorTreeMap.descendingMap().values());
		
		for (OrganisationUnit ou : valueOrgUnittreeMap.descendingMap().values()){
			ouNames.add(ou.getDisplayName());		
		}
		
		String commaSeparatedValues = commafyList(values);
		String commaSeparatedSurveyValues = commafyList(surveyValues);
		String commaSeparatedOuNames =commafyStringList(ouNames);
		String commaSeparatedStandardError = commafyList(standardError);
			// pass them to r script
		// Rscript chart1.R commaSeparatedMeanvalues commaSeparatedCurrentValues maxMeanValue rootRatio 
		   String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator;
	       String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "rscript" +  File.separator + "dqaChart4.R" + " \"" +commaSeparatedValues+"\" \""+ commaSeparatedSurveyValues + "\" \"" +commaSeparatedOuNames +"\" \"" +commaSeparatedStandardError+"\" "+  chartOutputPath + " " + chartName;
	       
	        System.out.println( "rScriptPath : "+ rScriptPath );
	        //rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_A + " " + currentQuarterMonth3End + " " + chartOutputPath;
	        try 
	        {
	            Runtime rt = Runtime.getRuntime();
	            Process pr;
	            pr = rt.exec( rScriptPath );
	            int exitVal = pr.waitFor();
	            
	            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
	        }
	        catch( Exception e ) 
	        {
	        	e.printStackTrace();
	        }
		return false;
	}

	
	public boolean generatechart4Group(  
			OrganisationUnit rootOrganisationUnit, Set<OrganisationUnitGroup> analysisOrgUnitGroups, Date startDate, Date endDate, Indicator indicator,DataElement standardErrorDE,String chartName) {
 		// get values for all orgunits : mean and current
		List<Double> values = new ArrayList<Double>();
		List<Double> surveyValues = new ArrayList<Double>();
		List<Double> standardError = new ArrayList<Double>();
		List<String> ouNames = new ArrayList<String>();
		TreeMap<Double,OrganisationUnitGroup> valueOrgUnitGrouptreeMap = new TreeMap<Double, OrganisationUnitGroup>();
		TreeMap<Double,Double> valueSurveyValueTreeMap = new TreeMap<Double, Double>();
		TreeMap<Double,Double> standardErrorTreeMap = new TreeMap<Double, Double>();
		
		Collection<OrganisationUnitGroup> analysisOrgUnitsLocal = new ArrayList<OrganisationUnitGroup>();
		analysisOrgUnitsLocal.addAll(analysisOrgUnitGroups);
		//analysisOrgUnitsLocal.add(rootOrganisationUnit);
		  PeriodType periodType = null;
		  Collection<Period> periods=null;
		  Double surveyVal =null;
		  DataValue dataValue=null;
		  if (serviceDEs.contains(standardErrorDE)){
			  periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
		  }else{
		   periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
		  }
	
		  DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();      
			System.out.println("periods=" + periods);
		//double rootVal  = aggregationService.getAggregatedNumeratorValue(indicator, startDate, endDate, rootOrganisationUnit);
		Double rootVal = generateExpression(indicator.getNumerator(), startDate,endDate, rootOrganisationUnit);
		
		double rootSurveyVal = getIndicatorDenominatorDataValue(indicator, startDate, endDate, rootOrganisationUnit);
		DataValue rootDataValue =  dataValueService.getDataValue(rootOrganisationUnit, standardErrorDE, periods.iterator().next(),decoc);
		
		double rootStandardError = 0.0;
		if (rootDataValue!=null){
		rootStandardError = Double.parseDouble(rootDataValue.getValue());
		}
		// preappend root values
				values.add(rootVal);
				surveyValues.add(rootSurveyVal);
				standardError.add(rootStandardError);
				ouNames.add(rootOrganisationUnit.getDisplayName());
				
		
		for (OrganisationUnitGroup organisationUnitGroup : analysisOrgUnitsLocal){
 			//double val  = aggregationService.getAggregatedNumeratorValue(indicator, startDate, endDate, organisationUnit);
 			Double val  = generateExpression(indicator.getNumerator(), startDate,endDate, organisationUnitGroup.getMembers());
 			surveyVal=null;
 			dataValue=null;
			for (OrganisationUnit organisationUnit: organisationUnitGroup.getMembers()){
		
				surveyVal = getIndicatorDenominatorDataValue(indicator, startDate, endDate, organisationUnit);
				dataValue =  dataValueService.getDataValue(organisationUnit, standardErrorDE, periods.iterator().next(),decoc);
				if (surveyVal!=null && dataValue!=null){
					break;
				}
			}
			double SE = 0.0;
			if (dataValue!=null){
			SE = Double.parseDouble(dataValue.getValue());
			}
		
			valueOrgUnitGrouptreeMap.put(val, organisationUnitGroup);
			valueSurveyValueTreeMap.put(val, surveyVal);
			standardErrorTreeMap.put(val, SE);
			System.out.println("se value="+SE);
		}
		
		
		values.addAll(valueOrgUnitGrouptreeMap.descendingMap().keySet());
		surveyValues.addAll(valueSurveyValueTreeMap.descendingMap().values());	
		standardError.addAll(standardErrorTreeMap.descendingMap().values());
		
		for (OrganisationUnitGroup ou : valueOrgUnitGrouptreeMap.descendingMap().values()){
			ouNames.add(ou.getDisplayName());		
		}
		
		String commaSeparatedValues = commafyList(values);
		String commaSeparatedSurveyValues = commafyList(surveyValues);
		String commaSeparatedOuNames =commafyStringList(ouNames);
		String commaSeparatedStandardError = commafyList(standardError);
			// pass them to r script
		// Rscript chart1.R commaSeparatedMeanvalues commaSeparatedCurrentValues maxMeanValue rootRatio 
		   String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator;
	       String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "rscript" +  File.separator + "dqaChart4.R" + " \"" +commaSeparatedValues+"\" \""+ commaSeparatedSurveyValues + "\" \"" +commaSeparatedOuNames +"\" \"" +commaSeparatedStandardError+"\" "+  chartOutputPath + " " + chartName;
	       
	        System.out.println( "rScriptPath : "+ rScriptPath );
	        //rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_A + " " + currentQuarterMonth3End + " " + chartOutputPath;
	        try 
	        {
	            Runtime rt = Runtime.getRuntime();
	            Process pr;
	            pr = rt.exec( rScriptPath );
	            int exitVal = pr.waitFor();
	            
	            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
	        }
	        catch( Exception e ) 
	        {
	        	e.printStackTrace();
	        }
		return false;
	}


	@Override
	public boolean generatechart3a(  
			OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate, Date endDate, Indicator indicator,String chartName) {
		
		  List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getNumerator()));
		  List<DataElement> des2 = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getDenominator()));
		  
		  DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();      
		
		List<Double> values = new ArrayList<Double>();
		List<Double> surveyValues = new ArrayList<Double>();
		List<String> ouNames = new ArrayList<String>();
		Double val = 0.0;
		double surveyVal = 0.0;
		
		PeriodType periodType = periodService.getPeriodTypeByName("Yearly");		 
		  Collection<Period> periods;
		  
	//	System.out.println("de size = " + des.size());
	//	System.out.println("de1 = " + des.get(0) + "  de2 = " + des.get(1));
		DataElement de1 = des.get(0);
		
		 dataQueryParameter = new DataQueryParams();         
          
		for (OrganisationUnit organisationUnit : analysisOrgUnits){
			//System.out.println("aggregation = " + aggregationService.toString() + "decoc " + decoc + "startdate = " + startDate + "enddate = " + endDate + "organisationUnit = " + organisationUnit);
			dataQueryParameter.setDataElement(de1); 
			if (serviceDEs.contains(de1)){
				periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
			}else{
				periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
			}
			dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
			dataQueryParameter.setFilterOrganisationUnit(organisationUnit);
//			if (aggregationService.getAggregatedDataValue(des.get(0), decoc, startDate, endDate, organisationUnit)!= null)
//			 val  = aggregationService.getAggregatedDataValue(des.get(0), decoc, startDate, endDate, organisationUnit);
			
			Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
			val = (Double) grid.getValue(0, 2);
			
		//	 System.out.println("here val = " + val);
	
			 double surveyPercentage = getIndicatorDenominatorDataValue(indicator, startDate, endDate, organisationUnit);
			 
			 dataQueryParameter.setDataElement(des.get(1)); 
			 if (serviceDEs.contains(des.get(1))){
					periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
				}else{
					periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
				}
				dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
			 grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
			 surveyVal = (Double) grid.getValue(0, 2);
				double temp=surveyVal;
			 surveyVal=(surveyVal/surveyPercentage)*100;
//			if (aggregationService.getAggregatedDataValue(des.get(1), decoc, startDate, endDate, organisationUnit)!= null)
//			 surveyVal = (aggregationService.getAggregatedDataValue(des.get(1), decoc, startDate, endDate, organisationUnit) / surveyPercentage)*100;
			
			//double temp = aggregationService.getAggregatedDataValue(des.get(1), decoc, startDate, endDate, organisationUnit);
				
			
		System.out.println("org unit = " +organisationUnit.getDisplayName() + " val= "+val+"  survey " +surveyVal + "aggsurvey = " +temp + " surveyPer=" + surveyPercentage);
			values.add(val);
			surveyValues.add(surveyVal);
			ouNames.add(organisationUnit.getDisplayName());
		}
		double ratioRoot= getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(rootOrganisationUnit, indicator, startDate, endDate);
		String commaSeparatedValues = commafyList(values);
		String commaSeparatedSurveyValues = commafyList(surveyValues);
		double maxSurveyValue = Collections.max(surveyValues);
		maxSurveyValue = Math.round((maxSurveyValue + 500)/1000) *1000;
		
			// pass them to r script
		// Rscript chart1.R commaSeparatedMeanvalues commaSeparatedCurrentValues maxMeanValue rootRatio 
		   String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator;
	       String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "rscript" +  File.separator + "dqaChart3a.R" + " \"" +commaSeparatedValues+"\" \""+ commaSeparatedSurveyValues + "\" "  + maxSurveyValue + " " + ratioRoot +" "+  chartOutputPath + " " + chartName;
	       
	        System.out.println( "rScriptPath : "+ rScriptPath );
	        //rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_A + " " + currentQuarterMonth3End + " " + chartOutputPath;
	        try 
	        {
	            Runtime rt = Runtime.getRuntime();
	            Process pr;
	            pr = rt.exec( rScriptPath );
	            int exitVal = pr.waitFor();
	            
	            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
	        }
	        catch( Exception e ) 
	        {
	        	e.printStackTrace();
	        }
		return false;
	}
	
	@Override
	public boolean generatechart3aGroup(  
			OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnitGroup> analysisOrgUnitGroups, Date startDate, Date endDate, Indicator indicator,String chartName) {
		
		  List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getNumerator()));
		  List<DataElement> des2 = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getDenominator()));
		  
		  DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();      
		
		List<Double> values = new ArrayList<Double>();
		List<Double> surveyValues = new ArrayList<Double>();
		List<String> ouNames = new ArrayList<String>();
		Double val = null;
		Double surveyVal = null;
		Period period = null;
		Double surveyPercentage=null;
		  PeriodType periodType = null; 
		  Collection<Period> periods;
	//	System.out.println("de size = " + des.size());
	//	System.out.println("de1 = " + des.get(0) + "  de2 = " + des.get(1));
		DataElement de1 = des.get(0);
		
		 dataQueryParameter = new DataQueryParams();         
          
			for (OrganisationUnitGroup organisationUnitGroup : analysisOrgUnitGroups){
				//System.out.println("de0=" + des.get(0) + "de0" + "decoc = " + de1Combo.getDisplayName() +"startdate = " + startDate.getDate() + "enddate = " + endDate.getDate() + "organi=" + organisationUnit.getDisplayName());
				val=null;
				surveyVal=null;
				surveyPercentage=null;
				for (OrganisationUnit organisationUnit : organisationUnitGroup.getMembers()){
				dataQueryParameter.setFilterOrganisationUnit(organisationUnit);
				 if (serviceDEs.contains(des.get(1))){
						periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
					}else{
						periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
					}
				    dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
				dataQueryParameter.setDataElement(des.get(1));
				
				Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
				if (val==null){
				val =  (Double) grid.getValue(0, 2);
				}else{
					val += (Double) grid.getValue(0, 2);
				}
					System.out.println("periods="+periods + "organisationUnit="+organisationUnit);
				
				dataQueryParameter.setDataElement(des.get(0));
				 if (serviceDEs.contains(des.get(0))){
						periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
					}else{
						periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
					}
				    dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
				grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
				if (surveyVal==null){
					surveyVal =  (Double) grid.getValue(0, 2);
					}else{
						surveyVal += (Double) grid.getValue(0, 2);
					}
				
					if (surveyPercentage==null){
						surveyPercentage = getIndicatorDenominatorDataValue(indicator, startDate, endDate, organisationUnit);
					}
				}
				surveyVal=(surveyVal/surveyPercentage)*100;

				values.add(val);
				surveyValues.add(surveyVal);
				ouNames.add(organisationUnitGroup.getDisplayName());
				System.out.println(" "+organisationUnitGroup.getDisplayName() + "-> value=" + val + "surveyVal=" + surveyVal);
			}

		double ratioRoot= getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(rootOrganisationUnit, indicator, startDate, endDate);
		String commaSeparatedValues = commafyList(values);
		String commaSeparatedSurveyValues = commafyList(surveyValues);
		double maxSurveyValue = Collections.max(surveyValues);
		maxSurveyValue = Math.round((maxSurveyValue + 500)/1000) *1000;
		
			// pass them to r script
		// Rscript chart1.R commaSeparatedMeanvalues commaSeparatedCurrentValues maxMeanValue rootRatio 
		   String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator;
	       String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "rscript" +  File.separator + "dqaChart3a.R" + " \"" +commaSeparatedValues+"\" \""+ commaSeparatedSurveyValues + "\" "  + maxSurveyValue + " " + ratioRoot +" "+  chartOutputPath + " " + chartName;
	       
	        System.out.println( "rScriptPath : "+ rScriptPath );
	        //rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_A + " " + currentQuarterMonth3End + " " + chartOutputPath;
	        try 
	        {
	            Runtime rt = Runtime.getRuntime();
	            Process pr;
	            pr = rt.exec( rScriptPath );
	            int exitVal = pr.waitFor();
	            
	            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
	        }
	        catch( Exception e ) 
	        {
	        	e.printStackTrace();
	        }
		return false;
	}
	@Override
	public boolean generatechart3b(  
			OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnit> analysisOrgUnits, Date startDate, Date endDate, Indicator indicator,String chartName) {
		
		  List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getNumerator()));
		  List<DataElementCategoryOptionCombo> de1ComboList = new ArrayList<DataElementCategoryOptionCombo>(expressionService.getOptionCombosInExpression(indicator.getNumerator())); 
		  DataElementCategoryOptionCombo de1Combo = de1ComboList.get(0);
		  DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();      
		
		List<Double> values = new ArrayList<Double>();
		List<Double> surveyValues = new ArrayList<Double>();
		List<String> ouNames = new ArrayList<String>();
		Double val = 0.0;
		double surveyVal = 0.0;
		Period period = null;
		  PeriodType periodType = null; 
		  Collection<Period> periods;
		  if (serviceDEs.contains(des.get(1))){
				periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
			}else{
				periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
			}
		System.out.println("de1 = " + des.get(0) + "  de2 = " + des.get(1));
		DataElement de1 = des.get(0);
		
		 period = periodService.getPeriod(startDate, endDate, periodType);
		  
		 dataQueryParameter = new DataQueryParams();         
     
		 
		for (OrganisationUnit organisationUnit : analysisOrgUnits){
			//System.out.println("de0=" + des.get(0) + "de0" + "decoc = " + de1Combo.getDisplayName() +"startdate = " + startDate.getDate() + "enddate = " + endDate.getDate() + "organi=" + organisationUnit.getDisplayName());
			dataQueryParameter.setFilterOrganisationUnit(organisationUnit);
			 if (serviceDEs.contains(des.get(1))){
					periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
				}else{
					periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
				}
			    dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
			dataQueryParameter.setDataElement(des.get(1));
			
			Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
			val =  (Double) grid.getValue(0, 2);
			 double surveyPercentage = getIndicatorDenominatorDataValue(indicator, startDate, endDate, organisationUnit);
			System.out.println("periods="+periods + "organisationUnit="+organisationUnit);
			
			dataQueryParameter.setDataElement(des.get(0));
			 if (serviceDEs.contains(des.get(0))){
					periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
				}else{
					periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
				}
			    dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
			grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
			surveyVal =  (Double) grid.getValue(0, 2);
			surveyVal=(surveyVal/surveyPercentage)*100;

			values.add(val);
			surveyValues.add(surveyVal);
			ouNames.add(organisationUnit.getDisplayName());
			System.out.println(" "+organisationUnit.getDisplayName() + "-> value=" + val + "surveyVal=" + surveyVal);
		}
		double ratioRoot= getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(rootOrganisationUnit, indicator, startDate, endDate);
		String commaSeparatedValues = commafyList(values);
		String commaSeparatedSurveyValues = commafyList(surveyValues);
		double maxSurveyValue = Collections.max(surveyValues);
		maxSurveyValue = Math.round((maxSurveyValue + 500)/1000) *1000;
		
		
			// pass them to r script
		// Rscript chart1.R commaSeparatedMeanvalues commaSeparatedCurrentValues maxMeanValue rootRatio 
		   String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator;
	       String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "rscript" +  File.separator + "dqaChart3b.R" + " \"" +commaSeparatedValues+"\" \""+ commaSeparatedSurveyValues + "\" "  + maxSurveyValue + " " + ratioRoot +" "+  chartOutputPath + " " + chartName;
	       
	        System.out.println( "rScriptPath : "+ rScriptPath );
	        //rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_A + " " + currentQuarterMonth3End + " " + chartOutputPath;
	        try 
	        {
	            Runtime rt = Runtime.getRuntime();
	            Process pr;
	            pr = rt.exec( rScriptPath );
	            int exitVal = pr.waitFor();
	            
	            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
	        }
	        catch( Exception e ) 
	        {
	        	e.printStackTrace();
	        }
		return false;
	}
	
	@Override
	public boolean generatechart3bGroup(  
			OrganisationUnit rootOrganisationUnit, Collection<OrganisationUnitGroup> analysisOrgUnitGroups, Date startDate, Date endDate, Indicator indicator,String chartName) {
		
		  List<DataElement> des = new ArrayList<DataElement>( expressionService.getDataElementsInExpression( indicator.getNumerator()));
		  List<DataElementCategoryOptionCombo> de1ComboList = new ArrayList<DataElementCategoryOptionCombo>(expressionService.getOptionCombosInExpression(indicator.getNumerator())); 
		  DataElementCategoryOptionCombo de1Combo = de1ComboList.get(0);
		  DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();      
		
		List<Double> values = new ArrayList<Double>();
		List<Double> surveyValues = new ArrayList<Double>();
		List<String> ouNames = new ArrayList<String>();
		Double val = null;
		Double surveyVal = null;
		Period period = null;
		Double surveyPercentage=null;
		  PeriodType periodType = null; 
		  Collection<Period> periods;
		  if (serviceDEs.contains(des.get(1))){
				periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
			}else{
				periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
			}
		System.out.println("de1 = " + des.get(0) + "  de2 = " + des.get(1));
		DataElement de1 = des.get(0);
		
		 period = periodService.getPeriod(startDate, endDate, periodType);
		  
		 dataQueryParameter = new DataQueryParams();         
     		 
		for (OrganisationUnitGroup organisationUnitGroup : analysisOrgUnitGroups){
			//System.out.println("de0=" + des.get(0) + "de0" + "decoc = " + de1Combo.getDisplayName() +"startdate = " + startDate.getDate() + "enddate = " + endDate.getDate() + "organi=" + organisationUnit.getDisplayName());
			val=null;
			surveyVal=null;
			surveyPercentage=null;
			for (OrganisationUnit organisationUnit : organisationUnitGroup.getMembers()){
			dataQueryParameter.setFilterOrganisationUnit(organisationUnit);
			 if (serviceDEs.contains(des.get(1))){
					periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
				}else{
					periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
				}
			    dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
			dataQueryParameter.setDataElement(des.get(1));
			
			Grid grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
			if (val==null){
			val =  (Double) grid.getValue(0, 2);
			}else{
				val += (Double) grid.getValue(0, 2);
			}
				System.out.println("periods="+periods + "organisationUnit="+organisationUnit);
			
			dataQueryParameter.setDataElement(des.get(0));
			 if (serviceDEs.contains(des.get(0))){
					periods = periodService.getPeriodsBetweenDates(serviceDataPeriodType,startDate, endDate);
				}else{
					periods = periodService.getPeriodsBetweenDates(annualPeriodType,annualStartDate, annualEndDate);
				}
			    dataQueryParameter.setPeriods((List<? extends NameableObject>) periods);
			grid = analyticsService.getAggregatedDataValues(dataQueryParameter);
			if (surveyVal==null){
				surveyVal =  (Double) grid.getValue(0, 2);
				}else{
					surveyVal += (Double) grid.getValue(0, 2);
				}
			
				if (surveyPercentage==null){
					surveyPercentage = getIndicatorDenominatorDataValue(indicator, startDate, endDate, organisationUnit);
				}
			}
			surveyVal=(surveyVal/surveyPercentage)*100;

			values.add(val);
			surveyValues.add(surveyVal);
			ouNames.add(organisationUnitGroup.getDisplayName());
			System.out.println(" "+organisationUnitGroup.getDisplayName() + "-> value=" + val + "surveyVal=" + surveyVal);
		}
		double ratioRoot= getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(rootOrganisationUnit, indicator, startDate, endDate);
		String commaSeparatedValues = commafyList(values);
		String commaSeparatedSurveyValues = commafyList(surveyValues);
		double maxSurveyValue = Collections.max(surveyValues);
		maxSurveyValue = Math.round((maxSurveyValue + 500)/1000) *1000;
		
		
			// pass them to r script
		// Rscript chart1.R commaSeparatedMeanvalues commaSeparatedCurrentValues maxMeanValue rootRatio 
		   String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator;
	       String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "rscript" +  File.separator + "dqaChart3b.R" + " \"" +commaSeparatedValues+"\" \""+ commaSeparatedSurveyValues + "\" "  + maxSurveyValue + " " + ratioRoot +" "+  chartOutputPath + " " + chartName;
	       
	        System.out.println( "rScriptPath : "+ rScriptPath );
	        //rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_A + " " + currentQuarterMonth3End + " " + chartOutputPath;
	        try 
	        {
	            Runtime rt = Runtime.getRuntime();
	            Process pr;
	            pr = rt.exec( rScriptPath );
	            int exitVal = pr.waitFor();
	            
	            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
	        }
	        catch( Exception e ) 
	        {
	        	e.printStackTrace();
	        }
		return false;
	}
	
	

	private String commafyList(List<Double> list){
		String result = "";
		
		for (Double val : list){
			result = result + "," + df.format(val);
		}
			if (!result.equalsIgnoreCase("") ){
				result = result.substring(1, result.length());
			}
		return result;
	}
	private String commafyStringList(List<String> list){
		String result = "";
		
		for (String val : list){
			result = result + "," + (val);
		}
			if (!result.equalsIgnoreCase("") ){
				result = result.substring(1, result.length());
			}
		return result;
	}
	private List<Double> getCurrentValueList(
			Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
			Date endDate,PeriodType periodType, Indicator indicator) {
		
		double currentVal;
		List<Double> result = new ArrayList<Double>();
		
		for (OrganisationUnit organisationUnit : analysisOrgUnits){
		currentVal =calculateThroughExpression(indicator, startDate, endDate, organisationUnit);
		  System.out.println("Current "+organisationUnit.getDisplayName() + " : " + currentVal + " startDate = " +startDate + "enddate = " + endDate + "indicator = " + indicator.getName());
          
        result.add(currentVal);
		}
		return result;
	}

	private List<Double> getMeanValueList(
			Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
			Date endDate,PeriodType periodType, Indicator indicator, int years) {
		List<Double> result = new ArrayList<Double>();
		
		      
		      for ( OrganisationUnit orgUnit : analysisOrgUnits )
	            {
	                Double aggregatedIndicatorValueAtLavel2 = 0.0;
	                 aggregatedIndicatorValueAtLavel2 =getLastXyearAverageIndicatorValue(orgUnit,startDate,endDate,periodType,indicator,years);
	                
	                System.out.println("mean "+orgUnit.getDisplayName() + " : " + aggregatedIndicatorValueAtLavel2 + " startDate = " +startDate + "enddate = " + endDate + "indicator = " + indicator.getName());
	                result.add(aggregatedIndicatorValueAtLavel2);
	              
	            }
		return result;
	}
	private Double getLastXyearAverageIndicatorValue(OrganisationUnit orgUnit,
			Date startDate, Date endDate,PeriodType periodType, Indicator indicator, int years) {
	    
		Date currentStartDate = startDate;
        Date currentEndDate = endDate;
        Date prevYearStartDate, prevYearEndDate;
        int count = years;
        double total = 0;
        int noData = 0;
       
        Collection<Period> periods = null;
      
        while ( count > 0 )
        {
            prevYearStartDate = getPreviousYear( currentStartDate );
            prevYearEndDate = getPreviousYear( currentEndDate );
            currentStartDate = prevYearStartDate;
            currentEndDate = prevYearEndDate;
            Double value = 0.0;
            periods = periodService.getPeriodsBetweenDates(periodType,prevYearStartDate, prevYearEndDate);
            
           try{
            value = calculateThroughExpression(indicator, prevYearStartDate, prevYearEndDate, orgUnit);
           }catch(Exception e){
        	   value=null;
           }
            if (value!=null){
            	total = total + value;
        	}else {
            	noData++;
            }
      
              
       
            count--;
        }
        total = total / (years-noData);
 
		return total;
	}

	private List<Double> getIndicatorDenominatorValuesList(
			Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
			Date endDate,PeriodType periodType, Indicator indicator) {
		Collection<Period> periods = periodService.getPeriodsBetweenDates(periodType,startDate, endDate);
	       
		List<Double> result = new ArrayList<Double>();
		      for ( OrganisationUnit orgUnit : analysisOrgUnits )
	            {
	                Double aggregatedIndicatorValueAtLavel2 = 0.0;
	                
	                //aggregatedIndicatorValueAtLavel2 =aggregationService.getAggregatedDenominatorValue(indicator, startDate, endDate, orgUnit);
	               aggregatedIndicatorValueAtLavel2= generateExpression(indicator.getDenominator(), startDate, endDate, orgUnit);
	                result.add(aggregatedIndicatorValueAtLavel2);          
	            }
	      
		return result;
	}
	private List<Double> getIndicatorNumeratorValuesList(
			Collection<OrganisationUnit> analysisOrgUnits, Date startDate,
			Date endDate,PeriodType periodType, Indicator indicator) {
		Collection<Period> periods = periodService.getPeriodsBetweenDates(periodType,startDate, endDate);
		   
		List<Double> result = new ArrayList<Double>();
		      for ( OrganisationUnit orgUnit : analysisOrgUnits )
	            {
	                Double aggregatedIndicatorValueAtLavel2 = 0.0;
	               // aggregatedIndicatorValueAtLavel2 =aggregationService.getAggregatedNumeratorValue(indicator, startDate, endDate, orgUnit);
	                aggregatedIndicatorValueAtLavel2= generateExpression(indicator.getNumerator(), startDate, endDate, orgUnit);
	                result.add(aggregatedIndicatorValueAtLavel2);          
	            }
	      
		return result;
	}

	
	
}
