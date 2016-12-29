package org.hisp.dhis.reports.util;

public class FortnightReport 
{
	public static final String LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR = "LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR";
    public static final String LAST_AVAILABLE_DATE_FOR_PREVIOUS_FISCAL_YEAR = "LAST_AVAILABLE_DATE_FOR_PREVIOUS_FISCAL_YEAR";
    
    public static final String LAST_FORTNIGHT_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR = "LAST_FORTNIGHT_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR";
    public static final String LAST_MONTH_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR = "LAST_MONTH_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR";
    public static final String CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR = "CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR";
    public static final String CUMULATIVE_FOR_LAST_FISCAL_YEAR = "CUMULATIVE_FOR_LAST_FISCAL_YEAR";
    public static final String CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_LAST_FISCAL_YEAR = "CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_LAST_FISCAL_YEAR";
    public static final String MONTHWISE_CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR = "MONTHWISE_CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR";
    public static final String DAYWISE_FOR_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR = "DAYWISE_FOR_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR";
    
    public static final String CELL_TYPE_DATA = "data";
    public static final String CELL_TYPE_DATE = "date";
    public static final String CELL_TYPE_DATEDATA = "datedata";
    public static final String CELL_TYPE_TARGETDATEDATA = "targetdatedata";
    public static final String CELL_TYPE_TARGETDATA = "targetdata";
    public static final String CELL_TYPE_FORMULATEXT = "formula_text";
    public static final String CELL_TYPE_FORMULANUMBER = "formula_number";
    public static final String CELL_TYPE_FORMULA_USE_STARTDATE = "formula_use_for_startdate";
    public static final String CELL_TYPE_FORMULA_USE_ENDDATE = "formula_use_for_enddate";
    public static final String CELL_TYPE_LASTFISCALYEARDATE = "lastficalyeardate";
    public static final String CELL_TYPE_LINELISITNGDATA = "linelistingdata";
    public static final String CELL_TYPE_DATEANDDATA = "dateanddata";
    public static final String RICE_RETAIL = "riceretail";
    public static final String RICE_WHOLESALE = "ricewholesale";
    public static final String ATTA_RETAIL = "attaretail";
    public static final String ATTA_WHOLESALE = "attawholesale";
    public static final String AGG_TYPE_PERIOD_AGG = "PERIOD_AGGREGATION";
    public static final String AGG_TYPE_ONE_PERIOD = "ONE_PERIOD";
    public static final String AGG_TYPE_ONE_PERIOD_MULTIPLE_ORGUNITS = "ONE_PERIOD_MULTIPLE_ORGUNIT_AGGREGATION";
    public static final String AGG_TYPE_MULTIPLE_PERIOD_MULTIPLE_ORGUNITS = "MULTIPLE_PERIOD_MULTIPLE_ORGUNIT_AGGREGATION";

    public static final String CONSTANT_MAX_DAYS_FOR_LAST_AVAILABLE_DATE = "CONSTANT_MAX_DAYS_FOR_LAST_AVAILABLE_DATE";
    public static final String CONSTANT_FORTNIGHREPORT_CHARTS_PATH = "CONSTANT_FORTNIGHREPORT_CHARTS_PATH";
    public static final String CONSTANT_RSCRIPT_PATH = "CONSTANT_RSCRIPT_PATH";
    
    public static final String DATA_TYPE_NUMBER = "number";
    
    String period;

    String orgunit;

    String dataelement;
    
    String name;

    String celltype;
    
    String aggtype;
    
    String rowname;
    
    String datatype;
	    
	    // -------------------------------------------------------------------------
	    // Constructor
	    // -------------------------------------------------------------------------

	    public FortnightReport()
	    {

	    }

	    public FortnightReport( String period, String orgunit, String dataelement, String name, String celltype, String aggtype,String rowname, String  datatype )
	    {
	        this.period = period;
	        this.orgunit = orgunit;
	        this.dataelement = dataelement;
	        this.name = name;
	        this.celltype = celltype;
	        this.aggtype = aggtype;
	        this.rowname = rowname;
	        this.datatype = datatype;
	    }

	    // -------------------------------------------------------------------------
	    // Getter & Setter
	    // -------------------------------------------------------------------------

	    
	    
	    public String getPeriod()
	    {
	        return period;
	    }

	    public String getDatatype() {
			return datatype;
		}

		public void setDatatype(String datatype) {
			this.datatype = datatype;
		}

		public void setPeriod( String period )
	    {
	        this.period = period;
	    }

	    public String getOrgunit()
	    {
	        return orgunit;
	    }

	    public void setOrgunit( String orgunit )
	    {
	        this.orgunit = orgunit;
	    }

	    public String getDataelement()
	    {
	        return dataelement;
	    }

	    public void setDataelement( String dataelement )
	    {
	        this.dataelement = dataelement;
	    }

	    public String getName()
	    {
	        return name;
	    }

	    public void setName( String name )
	    {
	        this.name = name;
	    }

	    public String getCelltype()
	    {
	        return celltype;
	    }

	    public void setCelltype( String celltype )
	    {
	        this.celltype = celltype;
	    }

	    public String getAggtype()
	    {
	        return aggtype;
	    }

	    public void setAggtype( String aggtype )
	    {
	        this.aggtype = aggtype;
	    }

		public String getRowname() {
			return rowname;
		}

		public void setRowname(String rowname) {
			this.rowname = rowname;
		}
	    
}
