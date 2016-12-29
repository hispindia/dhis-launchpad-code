package org.hisp.dhis.reports.fortnightly.action;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.reports.util.DFSReport;

import com.opensymphony.xwork2.Action;

public class SaveFortnigthCommentsAction implements Action
{
    //private static final int FORTNIGHTLY_REPORT_TEXT_BOX_COUNT = 26;
    //private static final String PREFIX_FORTNIGHT = "FR_TEXT_";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private String statusMessage;
    
    public String getStatusMessage()
    {
        return statusMessage;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        try
        {
            HttpServletRequest request = ServletActionContext.getRequest();
                   
            for( int i = 1; i <= DFSReport.FORTNIGHTLY_REPORT_TEXT_BOX_COUNT; i++ )
            {
                String value = request.getParameter( DFSReport.PREFIX_FORTNIGHT + i );
                Configuration_IN config_in = configurationService.getConfigurationByKey( DFSReport.PREFIX_FORTNIGHT + i );
                if( config_in == null )
                {
                    config_in = new Configuration_IN( DFSReport.PREFIX_FORTNIGHT + i, value );
                    configurationService.addConfiguration( config_in );
                }
                else
                {
                    config_in.setValue( value );
                    configurationService.updateConfiguration( config_in );
                }
            }
            
            statusMessage = "Comments are successfully saved.";
        }
        catch( Exception e )
        {
            statusMessage = "Some exception occured while saving comments, Plese contact admin";
        }
        
        return SUCCESS;
    }
}
