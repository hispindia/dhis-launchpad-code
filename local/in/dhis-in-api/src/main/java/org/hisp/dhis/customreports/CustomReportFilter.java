package org.hisp.dhis.customreports;

import java.io.Serializable;

/**
 * @author Mithilesh Kumar Thakur
 */

public class CustomReportFilter implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
    */
    
    private static final long serialVersionUID = 884114994005945275L;
    
    private Integer id;
    
    // person property, person attribute, person identifier,programstage dataelement,
    private String filterType;
    
    // operator are < > == like
    private String filterOperator;
    
    private String filterLeftString;
    
    private String filterRightString;
    
    private String searchTexts;
    

    //private CustomReport customReport;
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------  

    public CustomReportFilter()
    {

    }

    public CustomReportFilter( String filterType, String filterOperator, String filterLeftString, String filterRightString )
    {
        this.filterType = filterType;
        this.filterOperator = filterOperator;
        this.filterLeftString = filterLeftString;
        this.filterRightString = filterRightString;
        
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------
    /*
    @Override
    public int hashCode()
    {
        return customReport.hashCode();
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

        if ( !(o instanceof CustomReportFilter) )
        {
            return false;
        }

        final CustomReportFilter other = (CustomReportFilter) o;
        
        return customReport.getId() == other.getCustomReport().getId();
        //return id.equals( other.getId() );
    }
   */     
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public Integer getId()
    {
        return id;
    }


    public void setId( Integer id )
    {
        this.id = id;
    }


    public String getFilterType()
    {
        return filterType;
    }


    public void setFilterType( String filterType )
    {
        this.filterType = filterType;
    }


    public String getFilterOperator()
    {
        return filterOperator;
    }


    public void setFilterOperator( String filterOperator )
    {
        this.filterOperator = filterOperator;
    }


    public String getFilterLeftString()
    {
        return filterLeftString;
    }


    public void setFilterLeftString( String filterLeftString )
    {
        this.filterLeftString = filterLeftString;
    }


    public String getFilterRightString()
    {
        return filterRightString;
    }

    public void setFilterRightString( String filterRightString )
    {
        this.filterRightString = filterRightString;
    }

    public String getSearchTexts()
    {
        return searchTexts;
    }

    public void setSearchTexts( String searchTexts )
    {
        this.searchTexts = searchTexts;
    }
    
    
    
    
    /*
    public CustomReport getCustomReport()
    {
        return customReport;
    }


    public void setCustomReport( CustomReport customReport )
    {
        this.customReport = customReport;
    }
    */
}
