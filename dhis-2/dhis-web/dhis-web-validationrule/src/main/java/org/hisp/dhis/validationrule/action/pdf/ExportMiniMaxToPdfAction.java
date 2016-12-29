package org.hisp.dhis.validationrule.action.pdf;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.common.ServiceProvider;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ExportService;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;

import com.opensymphony.xwork2.Action;

public class ExportMiniMaxToPdfAction implements Action
{
    private static final Log log = LogFactory.getLog( ExportMiniMaxToPdfAction.class );

    private static final String EXPORT_FORMAT_PDF = "PDF";

    private static final String TYPE_MINIMAXVALIDATION_RULE = "miniMaxValidationRule";

    private static final String FILENAME_MINIMAXVALIDATION_RULE = "miniMaxValidationRule.zip";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ServiceProvider<ExportService> serviceProvider;

    public void setServiceProvider( ServiceProvider<ExportService> serviceProvider )
    {
        this.serviceProvider = serviceProvider;
    }

    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<MinMaxDataElement> minMaxDataElementList;

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String curKey;

    public void setCurKey( String curKey )
    {
        this.curKey = curKey;
    }
    
   /* private Integer id;

    public void setCurKey( Integer id )
    {
        this.id = id;
    }*/

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( type != null )
        {
            ExportParams params = new ExportParams();

            if ( type.equals( TYPE_MINIMAXVALIDATION_RULE ) )
            {
                if ( isNotBlank( curKey ) ) // Filter on key only if set
                {
                	//minMaxDataElementList = new ArrayList<MiniMaxDataElement>( minMaxDataElementService.getMinMaxDataElements(arg0, arg1).getMinMaxDataElement(arg0).getValidationRulesByName( curKey ) );
                	//minMaxDataElementService.getMinMaxDataElement(id).getDataElement().getName();
                	minMaxDataElementList = new ArrayList<MinMaxDataElement>( minMaxDataElementService.getAllMinMaxDataElements() );
                }
                else
                {
                	minMaxDataElementList = new ArrayList<MinMaxDataElement>( minMaxDataElementService.getAllMinMaxDataElements() );
                }

                if ( (minMaxDataElementList != null) && ! minMaxDataElementList.isEmpty() )
                {
                    params.setMinMaxDataElementObjects( minMaxDataElementList );
                }
                else
                {
                    params.setValidationRuleObjects( null );
                }

                fileName = FILENAME_MINIMAXVALIDATION_RULE;

                log.info( "Exporting to PDF for object type: " + TYPE_MINIMAXVALIDATION_RULE );
            }

            params.setIncludeDataValues( false );
            params.setI18n( i18n );
            params.setFormat( format );

            ExportService exportService = serviceProvider.provide( EXPORT_FORMAT_PDF );

            inputStream = exportService.exportData( params );
        }

        return SUCCESS;
    }
}
