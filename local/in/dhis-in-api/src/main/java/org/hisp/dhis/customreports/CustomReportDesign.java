package org.hisp.dhis.customreports;

import java.io.Serializable;

/**
 * @author Mithilesh Kumar Thakur
 */

public class CustomReportDesign implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
    */
    
    private static final long serialVersionUID = 884114994005945275L;
    
    
    private Integer id;
    
    private String customType;
    
    private String customTypeValue;
    
    //private CustomReport customReport;
    
    
    private Integer sortOrder;
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
    
    public CustomReportDesign()
    {

    }

    public CustomReportDesign( String customType, String customTypeValue, Integer sortOrder )
    {
        this.customType = customType;
        this.customTypeValue = customTypeValue;
        this.sortOrder = sortOrder;
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

        if ( !(o instanceof CustomReportDesign) )
        {
            return false;
        }

        final CustomReportDesign other = (CustomReportDesign) o;
        
        return customReport.getId() == other.getCustomReport().getId();
        
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

    public String getCustomType()
    {
        return customType;
    }

    public void setCustomType( String customType )
    {
        this.customType = customType;
    }

    public String getCustomTypeValue()
    {
        return customTypeValue;
    }

    public void setCustomTypeValue( String customTypeValue )
    {
        this.customTypeValue = customTypeValue;
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
    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }    
       
}
