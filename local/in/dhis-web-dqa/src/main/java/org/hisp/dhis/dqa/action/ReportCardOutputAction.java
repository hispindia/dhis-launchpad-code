package org.hisp.dhis.dqa.action;

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

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.dqa.api.DQAParameter;
import org.hisp.dhis.dqa.api.DQAParameterService;
import org.hisp.dhis.dqa.api.DQAReportCardDataValue;
import org.hisp.dhis.dqa.api.DQAReportCardDataValueService;
import org.hisp.dhis.dqa.api.DQAReportService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.FinancialJulyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author BHARATH
 */
public class ReportCardOutputAction
    implements Action
{

    private static final String OUTPUT_TYPE_PDF = "pdf";

    private static final String OUTPUT_TYPE_HTML = "html";

    private static final String OUTPUT_TYPE_EXCEL = "excel";

    private static final String OUTPUT_TYPE_WORD = "word";

    private static final String NATIONAL_SCORE = "_NATIONAL_SCORE";

    private static final String SUB_NATIONAL_SCORE_NO_OF_OUS = "_SUB_NATIONAL_SCORE_NO_OF_OUS";

    private static final String SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS = "_SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS";

    private static final String SUB_NATIONAL_SCORE_LIST_OF_OUS = "_SUB_NATIONAL_SCORE_LIST_OF_OUS";

    private static final String COMPLETENESS_ADMIN_UNIT_REPORTING_SUB_NATIONAL = "completeness_admin_unit_reporting_sub_national";

    private static final String MINIMUM_PERCENTAGE_REPORTING_RATE = "minimum_percentage_reporting_rate";

    private static final String PERCENTAGE_COMPLETENESS_INDICATOR_ZERO_MISSING = "percentage_completeness_indicator_zero_missing";

    private static final String RANGE_STANDARD_DEVIATION = "range_standard_deviation";

    private static final String PREVIOUS_YEAR_COMPARE = "previous_year_compare";

    private static final String MINIMUM_DIFFERENCE_BETWEEN_INDICATORS = "minimum_difference_between_indicators";

    private static final String DEFAULT_PERCENTAGE_COMPARISON = "default_percentage_comparison";

    private static final String MINIMUM_PERCENTAGE_SURVEY_PREGNANT = "minimum_percentage_survey_pregnant";

    private static final String MINIMUM_PERCENTAGE_SURVEY_CHILDREN_LESS_1_YEAR = "minimum_percentage_survey_children_less_1_year";

    private static final String PERCENTAGE_DIFFERENCE_ANC1 = "percentage_difference_anc1";

    private static final String PERCENTAGE_DIFFERENCE_DPT1 = "percentage_difference_dpt1";

    private static final String PERCENTAGE_DIFFERENCE_DELIVERIES = "percentage_difference_deliveries";

    private static final String STARTING_MONTH_FINANCIAL_YEAR = "starting_month_financial_year";

    private static final String LEVEL_AGGREGATION_MOST_RECENT_POPULATION_SURVEY = "LEVEL_AGGREGATION_MOST_RECENT_POPULATION_SURVEY";

    private static final String LEVEL2_NAME = "LEVEL2_NAME";
    
    private static final String LEVEL2_NAME1 = "LEVEL2_NAME1";
	
	private static final String ROOT_LEVEL = "root_level";

    private static final String YEAR = "YEAR";

    private static final int MONTHENDDAYS[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    
    private static final String SAVE_AND_GENERATE = "saveAndGenerate";
    private static final String GENERATE_FROM_SAVED = "generateFromSaved";
    
    private static final String ANNUALPERIODTYPENAME = "Yearly";
    // private static final String MONTHNAMES[] =
    // {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    // protected JasperReport jr;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private DQAReportService dqaReportService;

    public void setDqaReportService( DQAReportService dqaReportService )
    {
        this.dqaReportService = dqaReportService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DQAParameterService dqaParameterService;

    public void setDqaParameterService( DQAParameterService dqaParameterService )
    {
        this.dqaParameterService = dqaParameterService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private DQAReportCardDataValueService dqaReportCardDataValueService;
    
    public void setDqaReportCardDataValueService( DQAReportCardDataValueService dqaReportCardDataValueService )
    {
        this.dqaReportCardDataValueService = dqaReportCardDataValueService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private ExpressionService expressionService;
    
    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private DataElementService dataElementService;
   
    public void setDataElementService(DataElementService dataElementService) {
		this.dataElementService = dataElementService;
	}

    private CompleteDataSetRegistrationService completeDataSetRegistrationService;
    
	public void setCompleteDataSetRegistrationService(
			CompleteDataSetRegistrationService completeDataSetRegistrationService) {
		this.completeDataSetRegistrationService = completeDataSetRegistrationService;
	}

	private AnalyticsService analyticsService;
	
	public void setAnalyticsService(AnalyticsService analyticsService) {
		this.analyticsService = analyticsService;
	}

	private OrganisationUnitGroupService organisationUnitGroupService;
		
	public void setOrganisationUnitGroupService(
			OrganisationUnitGroupService organisationUnitGroupService) {
		this.organisationUnitGroupService = organisationUnitGroupService;
	}

	// -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    private String outputType;

    public void setOutputType( String outputType )
    {
        this.outputType = outputType;
    }

    private String orgUnitLevel1;

    public void setOrgUnitLevel1( String orgUnitLevel1 )
    {
        this.orgUnitLevel1 = orgUnitLevel1;
    }

    public void setOrgUnitLevel2( String orgUnitLevel2 )
    {
        this.orgUnitLevel2 = orgUnitLevel2;
    }

    public void setYearlyPeriodId( String yearlyPeriodId )
    {
        this.yearlyPeriodId = yearlyPeriodId;
    }

    private String orgUnitLevel2;

    private String yearlyPeriodId;

    int thresholdPercentage = 33;
    
    private String report_type;
    
    public void setReport_type( String report_type )
    {
        this.report_type = report_type;
    }
    
    private String chartReportOption;
    
    public void setChartReportOption(String chartReportOption) {
		this.chartReportOption = chartReportOption;
	}

	private String nationalLevel;
    private String analysisLevel;
    private String year;
    
    public List<DQAReportCardDataValue> dqaReportCardDataValueList;
   
    public List<DQAReportCardDataValue> getDqaReportCardDataValueList()
    {
        return dqaReportCardDataValueList;
    }
    
    public Map<String, String> resultMap;
    
    public Map<String, String> getResultMap()
    {
        return resultMap;
    }

    DecimalFormat df=new DecimalFormat("0.0");
    DecimalFormat dfTwoDecimalPlace=new DecimalFormat("0.00");
    
   DataQueryParams dataQueryParameter;
   
 
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
   
    public String execute()
        throws Exception
    {
        HashMap<String, Object> hash = new HashMap<String, Object>();

        if ( outputType == null || outputType.trim().equals( "" ) )
        {
            outputType = "pdf";
        }
        PeriodType annualPeriodType = periodService.getPeriodTypeByName(ANNUALPERIODTYPENAME);
        PeriodType servicePeriodType = null;
        Collection<DataElement> serviceDEs = new ArrayList<DataElement>() ;
        DQAParameter dqaParameterFinancialYear = dqaParameterService
            .getDQAParameterByName( STARTING_MONTH_FINANCIAL_YEAR );
        int month = Integer.parseInt(dqaParameterFinancialYear.getValue());
        
        if (month == 7){
        	// financial april
        	servicePeriodType= new FinancialJulyPeriodType();
        	
        }else if (month == 1){
        	servicePeriodType =new YearlyPeriodType();
        }
        dqaReportService.setServiceDataPeriodType(servicePeriodType);
        
        System.out.println( "month = " + month );
        Date reportStartDate = getStartDate( Integer.parseInt( yearlyPeriodId ),  month  - 1 );
        System.out.println( reportStartDate );
        Date reportEndDate = getEndDate( Integer.parseInt( yearlyPeriodId ),  month  + 10 );
        System.out.println( reportEndDate );
        
        dqaReportService.setAnnualStartDate(getStartDate(Integer.parseInt( yearlyPeriodId ), 0));
        dqaReportService.setAnnualEndDate(getEndDate(Integer.parseInt( yearlyPeriodId ), 11));
        
        //HashMap<String, String> resultMap = new HashMap<String, String>();
        
        resultMap = new HashMap<String, String>();
        
        
        // todo
        String dqaFieldName = "";

		DQAParameter rootLevelDQAParam = dqaParameterService.getDQAParameterByName( ROOT_LEVEL );
        orgUnitLevel1 = rootLevelDQAParam.getValue();

        int level1 = Integer.parseInt( orgUnitLevel1 );
        int level2 = Integer.parseInt( orgUnitLevel2 );
        Calendar cal = Calendar.getInstance();
        cal.setTime( reportStartDate );
        String level1Name = organisationUnitService.getOrganisationUnitLevelByLevel( level1 ).getDisplayName();
        String level2Name = organisationUnitService.getOrganisationUnitLevelByLevel( level2 ).getDisplayName();
        resultMap.put( LEVEL2_NAME, level2Name.toLowerCase() );
        resultMap.put(LEVEL2_NAME1, level2Name);
        System.out.println(resultMap.get(LEVEL2_NAME) + "|" + resultMap.get(LEVEL2_NAME1));
        System.out.println( LEVEL2_NAME + " = " + level2Name );
        System.out.println(level1Name);
        resultMap.put( YEAR, "" + cal.get( Calendar.YEAR ) );
        
       
        System.out.println( YEAR + " = " + cal.get( Calendar.YEAR ) );
       
        ArrayList<OrganisationUnit> orgUnitsAtAnalysisLevel1 = (ArrayList<OrganisationUnit>) organisationUnitService
            .getOrganisationUnitsAtLevel( level1 );
        OrganisationUnit rootOrganisationUnit = orgUnitsAtAnalysisLevel1.get( 0 );
        Collection<OrganisationUnit> orgUnitsAtAnalysisLevel2 = organisationUnitService.getOrganisationUnitsAtLevel( level2 );
        // todo
        
        //for saving reportCardDataValue Table
        nationalLevel = level1Name;
        analysisLevel = level2Name;
        year = ""+cal.get( Calendar.YEAR );
        
        String serviceDEsString = dqaParameterService.getDQAParameterByName("all_service_des").getValue();        
        serviceDEs = getDataElementsFromCommaSeparatedList(serviceDEsString);
        dqaReportService.setServiceDEs(serviceDEs);
        
        for (DataElement de:serviceDEs)
        {
        	System.out.println("de="+de);
        }
        
        int ouGroupSetId = Integer.parseInt(dqaParameterService.getDQAParameterByName("ou_group_set").getValue());                
         OrganisationUnitGroupSet organisationUnitGroupSet=organisationUnitGroupService.getOrganisationUnitGroupSet(ouGroupSetId);
      
         System.out.println("orgunitgroupset="+organisationUnitGroupSet);
        // -------------------------------------------------------------------------
        // Calculate and Fill Result Map
        // -------------------------------------------------------------------------

        
        System.out.println( " Report Card Generated Start Time : "  + new Date() );
        
        dqaReportCardDataValueList = new ArrayList<DQAReportCardDataValue>( dqaReportCardDataValueService.getAllDQAReportCardDataValuesByLevelPeriod( nationalLevel, analysisLevel, year ) );
        
        if( report_type.equalsIgnoreCase( GENERATE_FROM_SAVED ))
        {
            System.out.println( " Report Card Generated From Table Start Time : "  + new Date() );
            
            if( dqaReportCardDataValueList != null && dqaReportCardDataValueList.size() > 0 )
            {
                resultMap = new HashMap<String, String>();
               
                for( DQAReportCardDataValue dqaReportCardDataValue : dqaReportCardDataValueList )
                { 
                	System.out.println(dqaReportCardDataValue.getDqaParameter() + "----"+dqaReportCardDataValue.getValue());
                    resultMap.put( dqaReportCardDataValue.getDqaParameter(), dqaReportCardDataValue.getValue() );
                }
            }
        }
        
        else
        {
            Collection<DQAParameter> dqaParameters = dqaParameterService.getAllDQAParameters();
            Iterator<DQAParameter> dqaParametersIterator = dqaParameters.iterator();

            while ( dqaParametersIterator.hasNext() )
            {
            	DQAParameter dqaP = dqaParametersIterator.next();

                dqaFieldName = dqaP.getName();
                if (dqaFieldName.equalsIgnoreCase( " " ))
                {
                	
                }
                
                else if ( dqaFieldName.equalsIgnoreCase( "expected_monthly_facility_reports_indicators" ) )
                {
            	   DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                   int dataSetId = Integer.parseInt( dqaParameter.getValue() );
                   DataSet dataSet = dataSetService.getDataSet(dataSetId);
                   Collection<DataSet> dataSets = new ArrayList<DataSet>();
                   dataSets.add(dataSet);
                   PeriodType periodType = periodService.getPeriodTypeByName("Monthly");
                   Collection<Period> periods = periodService.getPeriodsBetweenDates(periodType, reportStartDate, reportEndDate );        
                   System.out.println("dataSet = " + dataSet.getDisplayName()+"datasets = " + dataSets.size() + "periods = " + periods);
                   //Collection<CompleteDataSetRegistration> completeDataSetRegistrationList = completeDataSetRegistrationService.getCompleteDataSetRegistrations(dataSets, orgUnitsAtAnalysisLevel2, periods);
                   //System.out.println("completeDataSetRegistration size= " + completeDataSetRegistrationList.size());
                   thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D1.1b").getValue());
                       
                   double result = 0;
                      
                   // have 1 dataset, check if even 1 dataelement is filled, if yes then that is reported
                   
                   double nationalCount = 0;
                   Collection<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();                   
                   for (OrganisationUnit orgUnit : orgUnitsAtAnalysisLevel2)
                   {
                	   double count = 0;
                	   for (Period period : periods)
                	   {
                		   Collection <DataValue> dataValues = dataValueService.getDataValues( orgUnit, period, dataSet.getDataElements() );
                		   if (dataValues.size() == 0)
                		   {
                			   count++;
                    	   }
                	   }
                	   nationalCount+=count;
                       if (((periods.size()-count)/periods.size())*100 < thresholdPercentage)
                       {
                    	   orgUnits.add(orgUnit);
                       }
                   }
                   
                   double totalReports = (orgUnitsAtAnalysisLevel2.size()*periods.size());
                   result =((totalReports - nationalCount )/totalReports )*100;
                   
                   int resultNoOfOus = orgUnits.size();
                   String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                   double resultPercentageOfOus = (orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;
                  
                   resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + df.format(result) +"%");
                   resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                   resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                   resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );
                
                   System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + result );
                   System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                   System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                   System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                }                
                else if ( dqaFieldName.equalsIgnoreCase( "monthly_reports_not_zero_missing_average_indicators" ) )
                {
                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    String indicatorIds = dqaParameter.getValue();
                    thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D1.1c").getValue());
                    
                    //System.out.println(dqaFieldName + "-----indicators = "+indicatorIds);
                    Collection<Indicator> indicators = getIndicatorsFromCommaSeparatedList( indicatorIds );
                  
                    
                    Iterator<Indicator> indicatorsIterator = indicators.iterator();
                    Collection<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();
                    double  result = 0;
                    while ( indicatorsIterator.hasNext() )
                    {
                        Indicator indicator = indicatorsIterator.next();
                        double value = dqaReportService.getCountOfIncompleteMonthlyDataForAYear( orgUnitsAtAnalysisLevel2, reportStartDate, indicator );

                        value =  (100 - ((double)value / (12*orgUnitsAtAnalysisLevel2.size())) * 100);
                        result += value;
                      
                        orgUnits.addAll( dqaReportService.getOrgUnitsHavingIncompleteMonthlyDataBeyondThresholdPercentageForAYear( orgUnitsAtAnalysisLevel2, reportStartDate, indicator, thresholdPercentage ));         
                    }
                    
                    result =result / indicators.size();
                    
                    int resultNoOfOus = orgUnits.size();
               
                    double resultPercentageOfOus = Math.round(((double) orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100);
                     
                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + df.format( result ) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                  
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + result );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                }
                else if ( 
    		 			dqaFieldName.equalsIgnoreCase( "monthly_reports_not_zero_missing_anc1_indicator" )
    		 			|| dqaFieldName.equalsIgnoreCase( "monthly_reports_not_zero_missing_deliveries_indicator" )
    		 			|| dqaFieldName.equalsIgnoreCase( "monthly_reports_not_zero_missing_dpt3_indicator" )
    		 			|| dqaFieldName.equalsIgnoreCase( "monthly_reports_not_zero_missing_opd_indicator" ) 
            		 )
                {

                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                    Indicator indicator = indicatorService.getIndicator( indicatorId );
                    int thresholdPercentage = 20;
                    double result = dqaReportService.getCountOfIncompleteMonthlyDataForAYear( orgUnitsAtAnalysisLevel2,
                        reportStartDate, indicator );
                    
                    System.out.println("-----result = " + result);
                    result =  (100 - ((double)result / (12*orgUnitsAtAnalysisLevel2.size())) * 100);

                    Collection<OrganisationUnit> orgUnits = dqaReportService
                        .getOrgUnitsHavingIncompleteMonthlyDataBeyondThresholdPercentageForAYear( orgUnitsAtAnalysisLevel2,
                            reportStartDate, indicator, thresholdPercentage );

                    int resultNoOfOus = orgUnits.size();
                    String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    double resultPercentageOfOus = orgUnits.size() / orgUnitsAtAnalysisLevel2.size() * 100;

                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + df.format(result) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );

                 
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + result );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
      
                }
               
                else if ( dqaFieldName.equalsIgnoreCase( "monthly_extreme_outliers_average_indicators" ) )
        	    {

                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    String indicatorIds = dqaParameter.getValue();
                    Collection<Indicator> indicators = getIndicatorsFromCommaSeparatedList( indicatorIds ); // todo
                    Iterator<Indicator> indicatorsIterator = indicators.iterator();

                    int thresholdPercentage = 5;
                    double result = 0;

                    while ( indicatorsIterator.hasNext() )
                    {
                        Indicator indicator = indicatorsIterator.next();
                        result = result + dqaReportService.getNoOfExtremeOutliers( orgUnitsAtAnalysisLevel2, reportStartDate, indicator );
                    }
                    double temp=result;
                    result = (result / (indicators.size() * 12 * orgUnitsAtAnalysisLevel2.size())) * 100;

                    Collection<OrganisationUnit> orgUnits = dqaReportService.getOrganisationUnitsHavingExtremeOutlierGreaterThanThresholdPercentage( orgUnitsAtAnalysisLevel2, reportStartDate, indicators, thresholdPercentage );

                    int resultNoOfOus = orgUnits.size();
                    String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    double resultPercentageOfOus = ((double)orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;

                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + df.format(result) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );

                    System.out.println("-------->>>>>>>>>>>>>>"+temp);
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + result );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );

                }
                 
               else if ( dqaFieldName.equalsIgnoreCase( "monthly_extreme_outliers_anc1_indicator" )
                   || dqaFieldName.equalsIgnoreCase( "monthly_extreme_outliers_deliveries_indicator" )
                    || dqaFieldName.equalsIgnoreCase( "monthly_extreme_outliers_dpt3_indicator" )
                    || dqaFieldName.equalsIgnoreCase( "monthly_extreme_outliers_opd_indicator" ) 
            		)
                {
                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                    Indicator indicator = indicatorService.getIndicator( indicatorId );

                    double result = dqaReportService.getNoOfExtremeOutliers( orgUnitsAtAnalysisLevel2, reportStartDate,
                        indicator );
                   
                  
                    result = (result / (12 * orgUnitsAtAnalysisLevel2.size())) * 100;

                    Collection<OrganisationUnit> orgUnits = dqaReportService
                        .getOrganisationUnitsHavingAtLeastOneExtremeOutlier( orgUnitsAtAnalysisLevel2, reportStartDate,
                            indicator );
                    int resultNoOfOus = orgUnits.size();
                    String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    double resultPercentageOfOus = ((double)orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;

                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + df.format(result) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );

                  
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + df.format(result ));
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                 
                }
          	else if ( dqaFieldName.equalsIgnoreCase( "monthly_moderate_outliers_average_indicators" ) )
                {

                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    String indicatorIds = dqaParameter.getValue();
                    Collection<Indicator> indicators = getIndicatorsFromCommaSeparatedList( indicatorIds ); // todo
                    Iterator<Indicator> indicatorsIterator = indicators.iterator();

                    int thresholdPercentage1 = 5;
                    double result = 0;

                    while ( indicatorsIterator.hasNext() )
                    {
                        Indicator indicator = indicatorsIterator.next();
                        result = result
                            + dqaReportService.getNoOfModerateOutliers( orgUnitsAtAnalysisLevel2, reportStartDate,
                                indicator );
                    }
                    System.out.println("result = " + result);
                    result = (result / (indicators.size() * 12 * orgUnitsAtAnalysisLevel2.size())) * 100;
                    
                    Collection<OrganisationUnit> orgUnits = dqaReportService
                        .getOrganisationUnitsHavingModerateOutlierGreaterThanThresholdPercentage( orgUnitsAtAnalysisLevel2,
                            reportStartDate, indicators, thresholdPercentage1 );

                    int resultNoOfOus = orgUnits.size();
                    String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    double resultPercentageOfOus = ((double)orgUnits.size()) / orgUnitsAtAnalysisLevel2.size() * 100;

                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + df.format( result ) +"%" );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%" );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );

                   
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + result );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                    
                }
          	  else if ( dqaFieldName.equalsIgnoreCase( "monthly_moderate_outliers_anc1_indicator" )
          			  || dqaFieldName.equalsIgnoreCase( "monthly_moderate_outliers_deliveries_indicator" )
          			  || dqaFieldName.equalsIgnoreCase( "monthly_moderate_outliers_dpt3_indicator" )
          			  ||  dqaFieldName.equalsIgnoreCase( "monthly_moderate_outliers_opd_indicator" )   
                     )
                {
                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                    Indicator indicator = indicatorService.getIndicator( indicatorId );

                    double result = dqaReportService.getNoOfModerateOutliers( orgUnitsAtAnalysisLevel2, reportStartDate,
                        indicator );

                    System.out.println("result = " + result);
                    result = (result / (12 * orgUnitsAtAnalysisLevel2.size())) * 100;

                    Collection<OrganisationUnit> orgUnits = dqaReportService
                        .getOrganisationUnitsHavingAtLeastOneModerateOutlier( orgUnitsAtAnalysisLevel2, reportStartDate,
                            indicator );
                    
                    int resultNoOfOus = orgUnits.size();
                    String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    double resultPercentageOfOus = ((double)orgUnits.size()) / orgUnitsAtAnalysisLevel2.size() * 100;
               
                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + df.format( result ) +"%" );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%" );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );
  
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + result );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                   
                }
          	 	  else if ( dqaFieldName.equalsIgnoreCase( "events_current_divided_preceding_3_years_average_indicators" ) )
              {                  
          		  double result = 0;

                  DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                  String indicatorIds = dqaParameter.getValue();
                  thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D2.2b.1").getValue());
                  int years = Integer.parseInt( dqaParameterService.getDQAParameterByName("D2.2b.2").getValue());
                   
                  HashSet<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>();
                  Collection<Indicator> indicators = getIndicatorsFromCommaSeparatedList( indicatorIds ); // todo
                  Iterator<Indicator> indicatorsIterator = indicators.iterator();
                  while ( indicatorsIterator.hasNext() )
                  {
                	  Indicator indicator = indicatorsIterator.next();
                      // cal indicator value for all indicators
                      result = result + dqaReportService.getCurrentToAverageOFPreviousXYearsIndicatorRatio( rootOrganisationUnit, indicator, reportStartDate, reportEndDate, annualPeriodType, years );

                      Collection<OrganisationUnit> tempOus = dqaReportService.getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentageOverTime( rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate,annualPeriodType, indicator, thresholdPercentage, years );
                             
                      orgUnits.addAll( tempOus );
                    }
                    // cal average of all indicators
                    result = result / indicators.size();
                    int resultNoOfOus = orgUnits.size();
                    String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    double resultPercentageOfOus = ((double)orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;

                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" +  dfTwoDecimalPlace.format(result)  );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );
     
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + result );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
              }
              
          	  else if (
    		  		dqaFieldName.equalsIgnoreCase( "deviation_anc1_from_average_3_previous_years_indicator" )
               		  ||  dqaFieldName.equalsIgnoreCase( "deviation_deliveries_from_average_3_previous_years_indicator" ) 
               		  || dqaFieldName.equalsIgnoreCase( "deviation_dpt1_from_average_3_previous_years_indicator" )
               		  || dqaFieldName.equalsIgnoreCase( "deviation_opd_from_average_3_previous_years_indicator" ) 
                    )
                {
                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                    Indicator indicator = indicatorService.getIndicator( indicatorId );
                    
                    String chartName = "";
                    if (dqaFieldName.equalsIgnoreCase("deviation_anc1_from_average_3_previous_years_indicator"))
                    {
                    	chartName = "ANC";
                    }
                    else if (dqaFieldName.equalsIgnoreCase("deviation_deliveries_from_average_3_previous_years_indicator"))
                    {
                    	chartName = "Delivery";
                    }
                    else if (dqaFieldName.equalsIgnoreCase("deviation_dpt1_from_average_3_previous_years_indicator"))
                    {
                    	chartName = "DPT3";
                    }
                    else if (dqaFieldName.equalsIgnoreCase("deviation_opd_from_average_3_previous_years_indicator"))
                    {
                    	chartName = "OPD";
                    }
                    
                    thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D2.2b.1").getValue());
                    int years = Integer.parseInt( dqaParameterService.getDQAParameterByName("D2.2b.2").getValue());
                   
                    double result = 0;
                    System.out.println("------>>>" + dqaFieldName);
                    result = dqaReportService.getCurrentToAverageOFPreviousXYearsIndicatorRatio( rootOrganisationUnit, indicator, reportStartDate, reportEndDate, annualPeriodType,years );
                    
                    
                    List<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>( dqaReportService.getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentageOverTime( rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate,annualPeriodType, indicator, thresholdPercentage, years ) );
                    //Collection<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();     
                    int resultNoOfOus = orgUnits.size();
                    //Collections.sort(orgUnits, new IdentifiableObjectNameComparator() );
                    String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    double resultPercentageOfOus = ((double)orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;
         
                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + dfTwoDecimalPlace.format( result) );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus)+"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );
               
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + result );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus +"%");
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                  
                   
                    dqaReportService.generatechart2b(rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate,annualPeriodType, indicator, years,chartName);
                    String path = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator + "dqaChart_" + chartName+ ".png";
           	       	resultMap.put("chart2b" + chartName , path);           	    
                }   
                
     		else if ( dqaFieldName.equalsIgnoreCase( "no_of_dpt1_to_anc1_indicator" ) )
                {
                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                    Indicator indicator = indicatorService.getIndicator( indicatorId );
                    thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D2.2c").getValue());
                    Collection<Period> periods = periodService.getPeriodsBetweenDates(annualPeriodType,reportStartDate, reportEndDate);
                    
                    String chartName = "dpt1ToAnc1";
                    Collection<OrganisationUnit> orgUnits = dqaReportService
                        .getOrgUnitsHavingIndicatorRatioDifferenceAboveThresholdPercentage( rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate,reportEndDate, indicator, thresholdPercentage );
                  
                    int resultNoOfOus = orgUnits.size();
                    String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    double resultPercentageOfOus = ((double)orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;

                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );
             
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                 
                    dqaReportService.generatechart2c(rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate,annualPeriodType, indicator, chartName);
                    String path = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator + "dqaChart_" + chartName + ".png";
           	       	resultMap.put("chart2c" , path);                
                }
          else if ( dqaFieldName.equalsIgnoreCase( "no_of_dpt3_divided_by_dpt1_indicator" ) )
                {
                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                    thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D2.2d").getValue());
                    
                    Indicator indicator = indicatorService.getIndicator( indicatorId );
                    List<Double> indicatorValueList = new ArrayList<Double>();
                    List<String> ouNameList = new ArrayList<String>();
                    TreeMap<Double,String> valueOrgUnitTreeMap =  new TreeMap<Double, String>();
                    String chartName = dqaFieldName;
                    
                    Double result = 0.0;
                    //result = aggregationService.getAggregatedIndicatorValue( indicator, reportStartDate,reportEndDate, rootOrganisationUnit );
                          
                    result=dqaReportService.calculateThroughExpression(indicator, reportStartDate, reportEndDate, rootOrganisationUnit);
                    indicatorValueList.add(result);
                    ouNameList.add(rootOrganisationUnit.getDisplayName());
               
                    
                    if ( result == null ) result = -1.0;
                   
                    Iterator<OrganisationUnit> analysisLevelOrgUnitsIterator = orgUnitsAtAnalysisLevel2.iterator();
                    Collection<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();
                    while ( analysisLevelOrgUnitsIterator.hasNext() )
                    {
                        OrganisationUnit ou = analysisLevelOrgUnitsIterator.next();
                      
                        Double indicatorValue = 0.0;
                                            
                       // indicatorValue = aggregationService.getAggregatedIndicatorValue( indicator, reportStartDate,reportEndDate, ou );
                        //indicatorValue=(Double) grid.getValue(0, 2);
                        indicatorValue=dqaReportService.calculateThroughExpression(indicator, reportStartDate, reportEndDate, ou);
                        valueOrgUnitTreeMap.put(indicatorValue, ou.getDisplayName());
                                              
                        if( indicatorValue != null )
                        {
                            if ( (1 - (1 / indicatorValue)) * 100 > thresholdPercentage )
                            {
                                orgUnits.add( ou );
                            }
                        }
                    }
                   
                    indicatorValueList.addAll(valueOrgUnitTreeMap.descendingMap().keySet());
                    ouNameList.addAll(valueOrgUnitTreeMap.descendingMap().values());
                    
                    int resultNoOfOus = orgUnits.size();
                    String commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    double resultPercentageOfOus = ((double)orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;
                  
                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" +  dfTwoDecimalPlace.format(result)  );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );
                    
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + result );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                    
                    dqaReportService.generatechart2d(indicatorValueList, ouNameList, chartName);
                    String path = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator + "dqaChart_" + chartName + ".png";
           	       	resultMap.put("chart2d" , path);
                }
                
          else if ( dqaFieldName.equalsIgnoreCase( "population_projection_live_births_indicator" ) )
                {
                   DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                   
                   int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                   
                   Indicator indicator = indicatorService.getIndicator( indicatorId );
                   
                   //System.out.println( " indicator - >" + indicator.getName() + "rootou->" + rootOrganisationUnit.getName() + "-- Start date " + reportStartDate + "-- End Date : " + reportEndDate );
                   
                   Double aggIndicatorValue = 0.0;
                   Collection<Period> periods = periodService.getPeriodsBetweenDates(annualPeriodType,reportStartDate, reportEndDate);
                   System.out.println("periods="+periods);
                   
                  // aggIndicatorValue = aggregationService.getAggregatedIndicatorValue( indicator, reportStartDate, reportEndDate, rootOrganisationUnit );
                 //  aggIndicatorValue = (Double) grid.getValue(0, 2);
                   aggIndicatorValue = dqaReportService.calculateThroughExpression(indicator, reportStartDate, reportEndDate, rootOrganisationUnit);
                   if ( aggIndicatorValue == null ) aggIndicatorValue = 0.0;
                   
                   //System.out.println( " aggIndicatorValue - >" + aggIndicatorValue );
                   
                   aggIndicatorValue = Math.round( aggIndicatorValue * Math.pow( 10, 2 ) )/ Math.pow( 10, 2 );
                   resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + aggIndicatorValue );
                   System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + aggIndicatorValue );
               }
               
          else if ( dqaFieldName.equalsIgnoreCase( "dpt3_facility_reports_by_coverage_rate_survey_indicator" ) 
		   	|| dqaFieldName.equalsIgnoreCase( "delivery_facility_reports_by_coverage_rate_survey_indicator" )
                    || dqaFieldName.equalsIgnoreCase( "anc1_facility_reports_by_coverage_rate_survey_indicator" ) 
		   			)
                {
                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    
                    int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                    
                    Indicator indicator = indicatorService.getIndicator( indicatorId );
                    DataElement standardErrorDE=null ;
                    
                    String chartName = dqaFieldName;
                    if (dqaFieldName.equalsIgnoreCase("dpt3_facility_reports_by_coverage_rate_survey_indicator")){
                    	chartName = "DTP3";
                    	int DEId=Integer.parseInt(dqaParameterService.getDQAParameterByName("dpt3_facility_reports_by_coverage_rate_survey_indicator_SE").getValue());
                    	standardErrorDE= dataElementService.getDataElement(DEId);
                    	 thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D4.4c").getValue());
                         
                    }else if (dqaFieldName.equalsIgnoreCase("delivery_facility_reports_by_coverage_rate_survey_indicator")){
                    	chartName = "Deliveries";
                    	int DEId=Integer.parseInt(dqaParameterService.getDQAParameterByName("delivery_facility_reports_by_coverage_rate_survey_indicator_SE").getValue());
                        
                    	standardErrorDE= dataElementService.getDataElement(DEId);
                    	 thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D4.4b").getValue());
                         
                    }else if (dqaFieldName.equalsIgnoreCase("anc1_facility_reports_by_coverage_rate_survey_indicator")){
                    	chartName = "ANC1";
                    	int DEId=Integer.parseInt(dqaParameterService.getDQAParameterByName("anc1_facility_reports_by_coverage_rate_survey_indicator_SE").getValue());
                        
                    	standardErrorDE= dataElementService.getDataElement(DEId);
                    	 thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D4.4c").getValue());
                         
                    }
                    
                 
                    //System.out.println( " indicator - >" + indicator.getName() + "rootou->" + rootOrganisationUnit.getName() + "-- Start date " + reportStartDate + "-- End Date : " + reportEndDate );
                    
                    Double aggResultDataValue = 0.0;
                    
                    aggResultDataValue = dqaReportService.getIndicatorAggregatedNumeratorValueByDenominatorDataValue(rootOrganisationUnit, indicator, reportStartDate, reportEndDate);
                    
                    System.out.println("aggResultDataValue - anc1 by anc coverage" + aggResultDataValue);
                    
                    //aggResultDataValue = Math.round( aggResultDataValue * Math.pow( 10, 2 ) )/ Math.pow( 10, 2 );
                    
                    int resultNoOfOus ;
                    String commaSeparatedOuNames = null;
                    double resultPercentageOfOus ;
                    
                    if (organisationUnitGroupSet==null){
                 	   Collection<OrganisationUnit> orgUnits = dqaReportService.getOrgUnitsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage(rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate, indicator, thresholdPercentage);
                                       
                     resultNoOfOus = orgUnits.size();
                     commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                     resultPercentageOfOus = ((double)orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;
                    }else{
                 	   Collection<OrganisationUnitGroup> orgUnitGroups = dqaReportService.getOrgUnitGroupsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage(rootOrganisationUnit, organisationUnitGroupSet.getOrganisationUnitGroups(), reportStartDate, reportEndDate, indicator, thresholdPercentage);
                         
                    	   resultNoOfOus = orgUnitGroups.size();
                        commaSeparatedOuNames = getCommaSeparatedNamesFromOUGroups( orgUnitGroups );
                        resultPercentageOfOus = ((double)orgUnitGroups.size() / organisationUnitGroupSet.getOrganisationUnitGroups().size()) * 100;
                    }
    
                    //System.out.println( " aggResultDataValue - >" + aggResultDataValue );
                    
                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + dfTwoDecimalPlace.format(aggResultDataValue  ));
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );
                    
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + aggResultDataValue );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                    
                    if (organisationUnitGroupSet==null){
                    dqaReportService.generatechart4(rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate, indicator,standardErrorDE, chartName);
                    }else{
                    	dqaReportService.generatechart4Group(rootOrganisationUnit, organisationUnitGroupSet.getOrganisationUnitGroups(), reportStartDate, reportEndDate, indicator,standardErrorDE, chartName);                        
                    }
                    String path = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator +"dqaChart4_" + chartName + ".png";
           	       	resultMap.put("chart4"+chartName , path);
                }
                 else if (  dqaFieldName.equalsIgnoreCase( "dpt1_total_by_dpt1_coverage_rate_survey_indicator" )  )
                {
	   				String chartName = dqaFieldName;
                    DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                    
                    int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                    thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D3.3b.1").getValue());
                     
                    Indicator indicator = indicatorService.getIndicator( indicatorId );
                    
                    System.out.println( " indicator - >" + indicator.getName() + "rootou->" + rootOrganisationUnit.getName() + "-- Start date " + reportStartDate + "-- End Date : " + reportEndDate );
                    
                    Double aggResultDataValue = dqaReportService.getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(rootOrganisationUnit, indicator, reportStartDate, reportEndDate);
                    
                    aggResultDataValue = Math.round( aggResultDataValue * Math.pow( 10, 2 ) )/ Math.pow( 10, 2 );
                    
                    System.out.println( " aggResultDataValue - >" + aggResultDataValue );
                    
                    int resultNoOfOus ;
                    String commaSeparatedOuNames = null;
                    double resultPercentageOfOus ;
                    
                    if (organisationUnitGroupSet==null){
                 	   Collection<OrganisationUnit> orgUnits = dqaReportService.getOrgUnitsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage2(rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate, indicator, thresholdPercentage);
                                       
                     resultNoOfOus = orgUnits.size();
                     commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                     resultPercentageOfOus = ((double)orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;
                    }else{
                 	   Collection<OrganisationUnitGroup> orgUnitGroups = dqaReportService.getOrgUnitGroupsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage2(rootOrganisationUnit, organisationUnitGroupSet.getOrganisationUnitGroups(), reportStartDate, reportEndDate, indicator, thresholdPercentage);
                         
                    	   resultNoOfOus = orgUnitGroups.size();
                        commaSeparatedOuNames = getCommaSeparatedNamesFromOUGroups( orgUnitGroups );
                        resultPercentageOfOus = ((double)orgUnitGroups.size() / organisationUnitGroupSet.getOrganisationUnitGroups().size()) * 100;
                    }
                    resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + aggResultDataValue );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                    resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );
               
                    System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + aggResultDataValue );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                    System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                    
                    if (organisationUnitGroupSet==null){
                    dqaReportService.generatechart3a(rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate, indicator, chartName);
                    }else{
                    	 dqaReportService.generatechart3aGroup(rootOrganisationUnit, organisationUnitGroupSet.getOrganisationUnitGroups(), reportStartDate, reportEndDate, indicator, chartName);                         
                    }
                    String path = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator + "dqaChart3_" + chartName + ".png";
          	       	resultMap.put("chart3" + chartName, path);
               
                }
      		else if (   dqaFieldName.equalsIgnoreCase( "anc1_total_by_anc1_coverage_rate_survey_indicator" ) )
               {
	   				String chartName = dqaFieldName;
                   DQAParameter dqaParameter = dqaParameterService.getDQAParameterByName( dqaFieldName );
                   
                   int indicatorId = Integer.parseInt( dqaParameter.getValue() );
                   thresholdPercentage = Integer.parseInt( dqaParameterService.getDQAParameterByName("D3.3b.2").getValue());
                   
                   Indicator indicator = indicatorService.getIndicator( indicatorId );
                   
                   System.out.println( " indicator - >" + indicator.getName() + "rootou->" + rootOrganisationUnit.getName() + "-- Start date " + reportStartDate + "-- End Date : " + reportEndDate );
                   
                   Double aggResultDataValue = dqaReportService.getIndicatorAggregatedNumeratorValueMultipliedByDenominatorDataValue(rootOrganisationUnit, indicator, reportStartDate, reportEndDate);
                   System.out.println("aggResultDataValue - anc1 by anc coverage" + aggResultDataValue);
                  // aggResultDataValue = Math.round( aggResultDataValue * Math.pow( 10, 2 ) )/ Math.pow( 10, 2 );
                   
                   System.out.println( " aggResultDataValue - >" + aggResultDataValue );
                                     
                   int resultNoOfOus ;
                   String commaSeparatedOuNames = null;
                   double resultPercentageOfOus ;
                   
                   if (organisationUnitGroupSet==null){
                	   Collection<OrganisationUnit> orgUnits = dqaReportService.getOrgUnitsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage2(rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate, indicator, thresholdPercentage);
                                      
                    resultNoOfOus = orgUnits.size();
                    commaSeparatedOuNames = getCommaSeparatedNames( orgUnits );
                    resultPercentageOfOus = ((double)orgUnits.size() / orgUnitsAtAnalysisLevel2.size()) * 100;
                   }else{
                	   Collection<OrganisationUnitGroup> orgUnitGroups = dqaReportService.getOrgUnitGroupsHavingIndicatorRatioDifferenceGreaterThanThresholdPercentage2(rootOrganisationUnit, organisationUnitGroupSet.getOrganisationUnitGroups(), reportStartDate, reportEndDate, indicator, thresholdPercentage);
                        
                   	   resultNoOfOus = orgUnitGroups.size();
                       commaSeparatedOuNames = getCommaSeparatedNamesFromOUGroups( orgUnitGroups );
                       resultPercentageOfOus = ((double)orgUnitGroups.size() / organisationUnitGroupSet.getOrganisationUnitGroups().size()) * 100;
                   }
                   
                   resultMap.put( dqaFieldName.toUpperCase() + NATIONAL_SCORE, "" + dfTwoDecimalPlace.format(aggResultDataValue ));
                   resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_NO_OF_OUS, "" + resultNoOfOus );
                   resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS, "" + df.format(resultPercentageOfOus) +"%");
                   resultMap.put( dqaFieldName.toUpperCase()  + SUB_NATIONAL_SCORE_LIST_OF_OUS, "" + commaSeparatedOuNames );
             
                   System.out.println( dqaFieldName + NATIONAL_SCORE + " = " + aggResultDataValue );
                   System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_NO_OF_OUS + " = " + resultNoOfOus );
                   System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_PERCENTAGE_OF_OUS + " = " + resultPercentageOfOus );
                   System.out.println( dqaFieldName + SUB_NATIONAL_SCORE_LIST_OF_OUS + " = " + commaSeparatedOuNames );
                   
                   if (organisationUnitGroupSet==null){
                   dqaReportService.generatechart3b(rootOrganisationUnit, orgUnitsAtAnalysisLevel2, reportStartDate, reportEndDate, indicator, chartName);
                   }else{
                	   dqaReportService.generatechart3bGroup(rootOrganisationUnit, organisationUnitGroupSet.getOrganisationUnitGroups(), reportStartDate, reportEndDate, indicator, chartName);
                         
                   }
                   String path = System.getenv("DHIS2_HOME") + File.separator + "dqa" + File.separator + "chartoutput" + File.separator + "dqaChart3_" + chartName + ".png";
        	       	resultMap.put("chart3" + chartName, path);
               
             }
            
     		
           }     
            if( report_type.equalsIgnoreCase( SAVE_AND_GENERATE ))
            {       
                //System.out.println( "Saving in Report Card Data Value Table Start Time : "  + new Date() );
                
                Date now = new Date();
                
                String storedBy = currentUserService.getCurrentUsername();
                
                if ( storedBy == null )
                {
                    storedBy = "[unknown]";
                }
                //int updateCount = 0;
                //int addCount = 0;
                for( String dqaReportCardParaMeter : resultMap.keySet() )
                {
                    nationalLevel = level1Name;
                    analysisLevel =  level2Name;
                    year = ""+cal.get( Calendar.YEAR );
                    String parameterName = dqaReportCardParaMeter;
                    String value = resultMap.get( dqaReportCardParaMeter );
                    
                    //System.out.println( " Key is  "  + dqaReportCardParaMeter  );
                    //System.out.println( " Value is   "  + resultMap.get( dqaReportCardParaMeter )  );
                  

                              DQAReportCardDataValue dqaReportCardDataValue = dqaReportCardDataValueService.getDQAReportCardDataValue( nationalLevel, analysisLevel, year, parameterName );

                    if ( dqaReportCardDataValue == null )
                    {
                        if ( value != null )
                        {
                            //addCount++;
                           // System.out.println( " Inside add  "  + dqaReportCardParaMeter + "- value is : "  + resultMap.get( dqaReportCardParaMeter ) );
                            dqaReportCardDataValue = new DQAReportCardDataValue( nationalLevel, analysisLevel, year, parameterName, value, storedBy, now );
                            dqaReportCardDataValueService.addDQAReportCardDataValue( dqaReportCardDataValue );
                        }
                    }
                    else
                    {
                        //updateCount++;
                        //System.out.println( " Inside Update  Parameter is  "  + dqaReportCardParaMeter + "- value is : "  + resultMap.get( dqaReportCardParaMeter ) );
                        dqaReportCardDataValue.setValue( value );
                        dqaReportCardDataValue.setTimestamp( now );
                        dqaReportCardDataValue.setStoredBy( storedBy );

                        dqaReportCardDataValueService.updateDQAReportCardDataValue( dqaReportCardDataValue );
                    }
                }
                //System.out.println( "update Count is  : "  + updateCount );
                //System.out.println( "add Count is  : "  + addCount );
            }            
        }
        
       String titlePicturePath = System.getenv( "DHIS2_HOME" ) + File.separator + "dqa" + File.separator +"pic" + File.separator + "title.png";
       String title2PicturePath = System.getenv( "DHIS2_HOME" ) + File.separator + "dqa" + File.separator +"pic" + File.separator + "title2.png";
       String logoPicturePath = System.getenv( "DHIS2_HOME" ) + File.separator + "dqa" + File.separator +"pic" + File.separator + "who_logo.png";
       String definition1PicturePath = System.getenv( "DHIS2_HOME" ) + File.separator + "dqa" + File.separator +"pic" + File.separator + "definition1.png";
       String definition2PicturePath = System.getenv( "DHIS2_HOME" ) + File.separator + "dqa" + File.separator +"pic" + File.separator + "definition2.png";
       String definition3PicturePath = System.getenv( "DHIS2_HOME" ) + File.separator + "dqa" + File.separator +"pic" + File.separator + "definition3.png";
       String mainReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + "dqa" + File.separator + "dqa_main_report.jasper";
           
        
        resultMap.put("LEVEL1_NAME", rootOrganisationUnit.getDisplayName());
        resultMap.put("TITLE_PICTURE", titlePicturePath);
        resultMap.put("TITLE_PICTURE2", title2PicturePath);
        resultMap.put("logo", logoPicturePath);
        resultMap.put("definition1", definition1PicturePath);
        resultMap.put("definition2", definition2PicturePath);
        resultMap.put("definition3", definition3PicturePath);
        resultMap.put("dqa_main_report", mainReportPath);  
        
        // -------------------------------------------------------------------------
        // Jasper Report Generation
        // -------------------------------------------------------------------------
        hash.put( "resultMap", resultMap );
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + "dqa" + File.separator;
        
       System.out.println("dhis2_home= " + System.getenv("DHIS2_HOME") ); 
        
       String dqaJrxmlFileName="";
       String outputFileName= "DQA_ReportCard_";
       
    	   dqaJrxmlFileName = "DQA_ReportCard.jrxml"; 
    	   outputFileName = outputFileName + "Report";
       
       
       
        HttpServletResponse response = ServletActionContext.getResponse();

        JasperReport jasperReport = JasperCompileManager.compileReport( path + dqaJrxmlFileName );

        JasperPrint jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );

        ServletOutputStream ouputStream = response.getOutputStream();

        JRExporter exporter = null;

        if ( outputType.equalsIgnoreCase( OUTPUT_TYPE_PDF ) )
        {
            response.setContentType( "application/pdf" );
            response.setHeader( "Content-Disposition", "inline; fileName=\""+outputFileName+".pdf\"" );
            exporter = new JRPdfExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
            exporter.setParameter( JRExporterParameter.CHARACTER_ENCODING, "UTF-8" );
        }
        else if ( outputType.equalsIgnoreCase( OUTPUT_TYPE_WORD ) )
        {
            response.setContentType( "application/msword" );
            response.setHeader( "Content-Disposition", "inline; fileName=\"DQA_ReportCard_.doc\"" );
            exporter = new JRDocxExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
            exporter.setParameter( JRDocxExporterParameter.CHARACTER_ENCODING, "UTF-8" );
        }
        else if ( outputType.equalsIgnoreCase( OUTPUT_TYPE_HTML ) )
        {
            exporter = new JRHtmlExporter();
            exporter.setParameter( JRHtmlExporterParameter.OUTPUT_STREAM, false );
            exporter.setParameter( JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, new Boolean( false ) );
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
            exporter.setParameter( JRExporterParameter.CHARACTER_ENCODING, "UTF-8" );
        }
        else if ( outputType.equalsIgnoreCase( OUTPUT_TYPE_EXCEL ) )
        {
        	
            //System.out.println( " OUTPUT_TYPE_EXCEL  : "  + outputType );
           // response.setContentType( "application/vnd.ms-excel" );
        	 response.setContentType( 	"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"  );
           
        	response.setHeader( "Content-Disposition", "inline; fileName=\"DQA_ReportCard.xlsx\"" );
            exporter = new JRXlsxExporter();
            exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
            exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );
            exporter.setParameter( JRExporterParameter.CHARACTER_ENCODING, "UTF-8" );
          //  exporter.setParameter( JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE );
            
            /*
            response.setContentType("application/xls");
            response.setHeader("Content-Disposition","inline; fileName=\"DailyFoodSituationReport_"+reportDate+".xls\"");
            exporter = new JRXlsExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM,ouputStream);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING,"UTF-8");
            */
            
        }

        try
        {
            exporter.exportReport();
        }
        catch ( JRException e )
        {
            throw new ServletException( e );
        }
        
        System.out.println( " Report Card Generated End Time : "  + new Date() );
        
        return SUCCESS;
    }

    private Collection<DataElement> getDEListFromIndicatorNumerator(
			Collection<Indicator> indicators) {
    	Collection<DataElement> deList = new ArrayList<DataElement>();
		for(Indicator indicator : indicators){			
    	 Set<DataElementOperand> operands = expressionService.getOperandsInExpression( indicator.getNumerator() );
    	 	for ( DataElementOperand operand : operands ){
    	 		DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
    	 		deList.add(dataElement);
    	 	}
		}
		return deList;
	}

	private Collection<Indicator> getIndicatorsFromCommaSeparatedList( String indicatorIds )
    {
        // TODO Auto-generated method stub
        String subStr[] = indicatorIds.split( "," );
        List<Indicator> indicators = new ArrayList<Indicator>();
        int count = 0;
        while ( count < subStr.length )
        {
            int id = Integer.parseInt( subStr[count] );
            indicators.add( indicatorService.getIndicator( id ) );
            count++;
        }

        return indicators;
    }

	private Collection<DataElement> getDataElementsFromCommaSeparatedList( String dataElementIds )
    {
        // TODO Auto-generated method stub
        String subStr[] = dataElementIds.split( "," );
        List<DataElement> des = new ArrayList<DataElement>();
        int count = 0;
        while ( count < subStr.length )
        {
            int id = Integer.parseInt( subStr[count] );
            des.add( dataElementService.getDataElement( id ) );
            count++;
        }

        return des;
    }
    private Date getStartDate( int year, int month )
    {
        // TODO Auto-generated method stub
        int day = 1;

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, day ,0,0,0);
     
        return cal.getTime();
    }

    private Date getEndDate( int year, int month )
    {
    	if ( month > 12 ){
    		year++;
    	}
    	month=month%12;
        // TODO Auto-generated method stub
        int day = MONTHENDDAYS[month];

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
        Calendar cal = Calendar.getInstance();
        cal.set( year, month, day ,23,59,59);

        return cal.getTime();
    }

    public static boolean isLeapYear( int year )
    {
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, year );
        return cal.getActualMaximum( Calendar.DAY_OF_YEAR ) > 365;
    }

    private String getCommaSeparatedNames( Collection<OrganisationUnit> orgUnitsParameter )
    {
        // TODO Auto-generated method stub
    	List<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>(orgUnitsParameter);
    	Collections.sort(orgUnits, new IdentifiableObjectNameComparator() );
        String commaSeparatedNames = "";
        Iterator<OrganisationUnit> orgUnitIterator = orgUnits.iterator();
        while ( orgUnitIterator.hasNext() )
        {
            OrganisationUnit ou = orgUnitIterator.next();
            commaSeparatedNames = commaSeparatedNames + ou.getDisplayName() + ",";
        }
        if ( !commaSeparatedNames.equalsIgnoreCase( "" ) )
        {
            commaSeparatedNames = commaSeparatedNames.substring( 0, commaSeparatedNames.length() - 1 );
        }

        return commaSeparatedNames;
    }

    private String getCommaSeparatedNamesFromOUGroups( Collection<OrganisationUnitGroup> orgUnitGroups )
    {
        // TODO Auto-generated method stub
        String commaSeparatedNames = "";
        Iterator<OrganisationUnitGroup> orgUnitIterator = orgUnitGroups.iterator();
        while ( orgUnitIterator.hasNext() )
        {
            OrganisationUnitGroup ou = orgUnitIterator.next();
            commaSeparatedNames = commaSeparatedNames + ou.getDisplayName() + ",";
        }
        if ( !commaSeparatedNames.equalsIgnoreCase( "" ) )
        {
            commaSeparatedNames = commaSeparatedNames.substring( 0, commaSeparatedNames.length() - 1 );
        }

        return commaSeparatedNames;
    }
}
