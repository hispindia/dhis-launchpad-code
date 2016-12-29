package org.hisp.dhis.dqa.api;

import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.DxfNamespaces;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "DQAParameter", namespace = DxfNamespaces.DXF_2_0)
public class DQAParameter  extends BaseNameableObject
{
    //Dimension - 1 Completeness Of Reporting
    public static final String COMPLETENESS_ADMIN_UNIT_REPORTING_SUB_NATIONAL = "completeness_admin_unit_reporting_sub_national";
    public static final String MINIMUM_PERCENTAGE_REPORTING_RATE = "minimum_percentage_reporting_rate";
    public static final String PERCENTAGE_COMPLETENESS_INDICATOR_ZERO_MISSING = "percentage_completeness_indicator_zero_missing";
    //Dimension - 2 Internal Consistency Of Reported Data
    public static final String RANGE_STANDARD_DEVIATION = "range_standard_deviation";
    public static final String PREVIOUS_YEAR_COMPARE = "previous_year_compare";
    public static final String MINIMUM_DIFFERENCE_BETWEEN_INDICATORS = "minimum_difference_between_indicators";
    public static final String DEFAULT_PERCENTAGE_COMPARISON = "default_percentage_comparison";
    // Dimension - 3 External Consistency Of Reported Data
    public static final String MINIMUM_PERCENTAGE_SURVEY_PREGNANT = "minimum_percentage_survey_pregnant";
    public static final String MINIMUM_PERCENTAGE_SURVEY_CHILDREN_LESS_1_YEAR = "minimum_percentage_survey_children_less_1_year";
    
    // Dimension - 4 External Consistency Of Coverage Rates
    
    public static final String PERCENTAGE_DIFFERENCE_ANC1 = "percentage_difference_anc1";
    public static final String PERCENTAGE_DIFFERENCE_DPT1 = "percentage_difference_dpt1";
    public static final String PERCENTAGE_DIFFERENCE_DELIVERIES = "percentage_difference_deliveries";
    
    // Other Paramters
    public static final String STARTING_MONTH_FINANCIAL_YEAR = "starting_month_financial_year";
    public static final String LEVEL_AGGREGATION_MOST_RECENT_POPULATION_SURVEY = "level_aggregation_most_recent_population_survey";
    
    // Indicator Dimension - 1 Completeness Of Reporting
    
    public static final String EXPECTED_MONTHLY_REPORTS_INDICATOR = "expected_monthly_reports_indicator";
    public static final String EXPECTED_MONTHLY_FACILITY_REPORTS_INDICATOR = "expected_monthly_facility_reports_indicator";
    public static final String MONTHLY_REPORTS_NOT_ZERO_MISSING_AVERAGE_INDICATORS = "monthly_reports_not_zero_missing_average_indicators";
    public static final String MONTHLY_REPORTS_NOT_ZERO_MISSING_ANC1_INDICATOR = "monthly_reports_not_zero_missing_anc1_indicator";
    public static final String MONTHLY_REPORTS_NOT_ZERO_MISSING_DELIVERIES_INDICATOR = "monthly_reports_not_zero_missing_deliveries_indicator";
    public static final String MONTHLY_REPORTS_NOT_ZERO_MISSING_DPT3_INDICATOR = "monthly_reports_not_zero_missing_dpt3_indicator";
    public static final String MONTHLY_REPORTS_NOT_ZERO_MISSING_OPD_INDICATOR = "monthly_reports_not_zero_missing_opd_indicator";
    
    // Indicator Dimension - 2 Internal Consistency Of Reported Data
    public static final String MONTHLY_EXTREME_OUTLIERS_AVERAGE_INDICATORS = "monthly_extreme_outliers_average_indicators";
    public static final String MONTHLY_EXTREME_OUTLIERS_ANC1_INDICATOR = "monthly_extreme_outliers_anc1_indicator";
    public static final String MONTHLY_EXTREME_OUTLIERS_DELIVERIES_INDICATOR = "monthly_extreme_outliers_deliveries_indicator";
    public static final String MONTHLY_EXTREME_OUTLIERS_DPT3_INDICATOR = "monthly_extreme_outliers_dpt3_indicator";
    public static final String MONTHLY_EXTREME_OUTLIERS_OPD_INDICATOR = "monthly_extreme_outliers_opd_indicator";
    public static final String MONTHLY_MODERATE_OUTLIERS_AVERAGE_INDICATORS = "monthly_moderate_outliers_average_indicators";
    public static final String MONTHLY_MODERATE_OUTLIERS_ANC1_INDICATOR = "monthly_moderate_outliers_anc1_indicator";
    
    public static final String MONTHLY_MODERATE_OUTLIERS_DELIVERIES_INDICATOR = "monthly_moderate_outliers_deliveries_indicator";
    public static final String MONTHLY_MODERATE_OUTLIERS_DPT3_INDICATOR = "monthly_moderate_outliers_dpt3_indicator";
    public static final String MONTHLY_MODERATE_OUTLIERS_OPD_INDICATOR = "monthly_moderate_outliers_opd_indicator";
    public static final String EVENTS_CURRENT_DIVIDED_PRECEDING_3_YEARS_AVERAGE_INDICATORS = "events_current_divided_preceding_3_years_average_indicators";
    public static final String DEVIATION_ANC1_FROM_AVERAGE_3_PREVIOUS_YEARS_INDICATOR = "deviation_anc1_from_average_3_previous_years_indicator";
    public static final String DEVIATION_DELIVERIES_FROM_AVERAGE_3_PREVIOUS_YEARS_INDICATOR = "deviation_deliveries_from_average_3_previous_years_indicator";
    public static final String DEVIATION_DPT1_FROM_AVERAGE_3_PREVIOUS_YEARS_INDICATOR = "deviation_dpt1_from_average_3_previous_years_indicator";
    
    
    public static final String DEVIATION_DPT3_FROM_AVERAGE_3_PREVIOUS_YEARS_INDICATOR = "deviation_dpt3_from_average_3_previous_years_indicator";
    public static final String DEVIATION_OPD_FROM_AVERAGE_3_PREVIOUS_YEARS_INDICATOR = "deviation_opd_from_average_3_previous_years_indicator";
    public static final String NO_OF_DPT1_TO_ANC1_INDICATOR = "no_of_dpt1_to_anc1_indicator";
    public static final String NO_OF_DPT3_DIVIDED_BY_DPT1_INDICATOR = "no_of_dpt3_divided_by_dpt1_indicator";
    
    
    // Indicator Dimension - 3 External Consistency Of Reported Data
    public static final String POPULATION_PROJECTION_LIVE_BIRTHS_INDICATOR = "population_projection_live_births_indicator";
    public static final String ANC1_TOTAL_BY_ANC1_COVERAGE_RATE_SURVEY_INDICATOR = "anc1_total_by_anc1_coverage_rate_survey_indicator";
    public static final String DPT1_TOTAL_BY_DPT1_COVERAGE_RATE_SURVEY_INDICATOR = "dpt1_total_by_dpt1_coverage_rate_survey_indicator";
    
    // Dimension - 4 External Consistency Of Coverage Rates
    public static final String ANC1_FACILITY_REPORTS_BY_COVERAGE_RATE_SURVEY_INDICATOR = "anc1_facility_reports_by_coverage_rate_survey_indicator";
    public static final String DELIVERY_FACILITY_REPORTS_BY_COVERAGE_RATE_SURVEY_INDICATOR = "delivery_facility_reports_by_coverage_rate_survey_indicator";
    public static final String DPT3_FACILITY_REPORTS_BY_COVERAGE_RATE_SURVEY_INDICATOR = "dpt3_facility_reports_by_coverage_rate_survey_indicator";
    
    
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -2466830446144115499L;
    
    //private DQADimension dqaDimension;
    
    private String description;
    
    private String type;
    
    private String value;
    
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    
    
    public DQAParameter()
    {
    }

    public DQAParameter( String name )
    {
        this.name = name;
    }
    
    public DQAParameter( String name, String type )
    {
        this.name = name;
        this.type = type;
    }
    
    
    public DQAParameter( String name, String description, String type, String value )
    {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }
    

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof DQAParameter) )
        {
            return false;
        }

        final DQAParameter other = (DQAParameter) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }


    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    /*
    public DQADimension getDqaDimension()
    {
        return dqaDimension;
    }

    public void setDqaDimension( DQADimension dqaDimension )
    {
        this.dqaDimension = dqaDimension;
    }
    */
    public String getValue()
    {
        return value;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
    
}
