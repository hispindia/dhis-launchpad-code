package org.hisp.dhis.ovc.report.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportDesign;
import org.hisp.dhis.customreports.CustomReportDesignService;
import org.hisp.dhis.customreports.CustomReportFilter;
import org.hisp.dhis.customreports.CustomReportFilterService;
import org.hisp.dhis.customreports.CustomReportService;
import org.hisp.dhis.i18n.I18n;
import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class RemoveNameBasedReportAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CustomReportService customReportService;
    
    public void setCustomReportService( CustomReportService customReportService )
    {
        this.customReportService = customReportService;
    }
    
    private CustomReportDesignService customReportDesignService;
    
    public void setCustomReportDesignService( CustomReportDesignService customReportDesignService )
    {
        this.customReportDesignService = customReportDesignService;
    }
    
    private CustomReportFilterService customReportFilterService;
    
    public void setCustomReportFilterService( CustomReportFilterService customReportFilterService )
    {
        this.customReportFilterService = customReportFilterService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        try
        {
            CustomReport customReport = customReportService.getCustomReportById( id );
            
            if( customReport != null )
            {
                List<CustomReportDesign> customReportDesigns = new ArrayList<CustomReportDesign>( customReport.getCustomReportDesigns() );
                
                if( customReportDesigns != null && customReportDesigns.size() > 0 )
                {
                    customReport.getCustomReportDesigns().clear();
                    for( CustomReportDesign customReportDesignForDelete : customReportDesigns )
                    {
                        customReportDesignService.deleteCustomReportDesign( customReportDesignForDelete );
                    }
                }
                
                List<CustomReportFilter> customReportFilters = new ArrayList<CustomReportFilter>( customReport.getCustomReportFilters() );
                
                if( customReportFilters != null && customReportFilters.size() > 0 )
                {       
                    customReport.getCustomReportFilters().clear();
                    for( CustomReportFilter customReportFilterForDelete : customReportFilters )
                    {
                        customReportFilterService.deleteCustomReportFilter( customReportFilterForDelete );
                    }
                }
            }
            
            customReportService.deleteCustomReport( customReport );
            
        }
        
        catch ( DataIntegrityViolationException ex )
        {
            message = i18n.getString( "object_not_deleted_associated_by_objects" );

            return ERROR;
        }
        
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();

                return ERROR;
            }
        }
        
        return SUCCESS;
    }
}

