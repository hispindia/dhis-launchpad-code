package org.hisp.dhis.customreports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.user.User;

/**
 * @author Mithilesh Kumar Thakur
 */

public class CustomReport implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
    */
    
    private static final long serialVersionUID = 884114994005945275L;
    
    private Integer id;
    
    private String name;
    
    private String description;
    
    // Report type either name based or aggregate
    private String reportType;
    
    private User user;
    
    private boolean reportAvailable;

    private List<CustomReportDesign> customReportDesigns = new ArrayList<CustomReportDesign>();
    
    private List<CustomReportFilter> customReportFilters = new ArrayList<CustomReportFilter>();
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
    
    public CustomReport()
    {

    }

    public CustomReport( String name, String reportType, User user, boolean reportAvailable )
    {
        this.name = name;
        this.reportType = reportType;
        this.user = user;
        this.reportAvailable = reportAvailable;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
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

        if ( !(o instanceof CustomReport) )
        {
            return false;
        }

        final CustomReport other = (CustomReport) o;

        return name.equals( other.getName() );
    }
        
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

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getReportType()
    {
        return reportType;
    }

    public void setReportType( String reportType )
    {
        this.reportType = reportType;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }

    public boolean isReportAvailable()
    {
        return reportAvailable;
    }

    public void setReportAvailable( boolean reportAvailable )
    {
        this.reportAvailable = reportAvailable;
    }
        
    public List<CustomReportDesign> getCustomReportDesigns()
    {
        return customReportDesigns;
    }

    public void setCustomReportDesigns( List<CustomReportDesign> customReportDesigns )
    {
        this.customReportDesigns = customReportDesigns;
    }

    public List<CustomReportFilter> getCustomReportFilters()
    {
        return customReportFilters;
    }

    public void setCustomReportFilters( List<CustomReportFilter> customReportFilters )
    {
        this.customReportFilters = customReportFilters;
    }    
    
}
