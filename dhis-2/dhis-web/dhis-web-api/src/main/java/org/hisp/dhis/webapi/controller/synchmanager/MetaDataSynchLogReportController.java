package org.hisp.dhis.webapi.controller.synchmanager;

import static org.hisp.dhis.webapi.utils.ContextUtils.CONTENT_TYPE_XML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dxf2.common.JacksonUtils;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatus;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatus;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLog;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLogService;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatus;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatus;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Mithilesh Kumar Thakur
 */

@Controller
@RequestMapping( method = RequestMethod.GET )
public class MetaDataSynchLogReportController
{
    public static final String RESOURCE_PATH = "/MetaDataSynchLogReport";
    
    @Autowired
    private SynchInstanceService synchInstanceService;
    
    @Autowired
    private MetaDataSynchLogService metaDataSynchLogService;
    
    @Autowired
    private DataElementSynchStatusService dataElementSynchStatusService;
    
    @Autowired
    private IndicatorSynchStatusService indicatorSynchStatusService;
    
    @Autowired
    private OrganisationUnitSynchStatusService organisationUnitSynchStatusService;    
 
    @Autowired
    private ValidationRuleSynchStatusService validationRuleSynchStatusService;   
    
    @Autowired
    private ContextUtils contextUtils;
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    public I18nFormat getFormat()
    {
        return format;
    }
    
    @RequestMapping( value = MetaDataSynchLogReportController.RESOURCE_PATH + ".xml", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportXml( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        
        List<MetaDataSynchLog> metaDataSynchLogs = new ArrayList<MetaDataSynchLog>();
        
        List<DataElementSynchStatus> dataElementSynchStatus = new ArrayList<DataElementSynchStatus>();
        List<IndicatorSynchStatus> indicatorSynchStatus = new ArrayList<IndicatorSynchStatus>();
        List<OrganisationUnitSynchStatus> organisationUnitSynchStatus = new ArrayList<OrganisationUnitSynchStatus>();
        List<ValidationRuleSynchStatus> validationRuleSynchStatus = new ArrayList<ValidationRuleSynchStatus>();
        
        //System.out.println("Inside MetaDataSynchLogReportController 1 " + metaDataSynchLogs.size() + "--" + dataElementSynchStatus.size() + "--" + indicatorSynchStatus.size() + "--" + organisationUnitSynchStatus.size() +"--" + validationRuleSynchStatus.size() );
        
        
        WebOptions options = new WebOptions( parameters );
        MetaData metaData = new MetaData();
        
               
        String sDate = null;
        
        String eDate = null;
        
        String url = null;
        
        response.setContentType( CONTENT_TYPE_XML );
        
        if ( options.getOptions().containsKey( "cleintURL" ) && options.getOptions().containsKey( "startDate" ) && options.getOptions().containsKey( "endDate" ) )
        {
            url = options.getOptions().get("cleintURL");
            
            SynchInstance instance = synchInstanceService.getInstanceByUrl( url );
            
            sDate =  options.getOptions().get( "startDate" ) ; 
            eDate = options.getOptions().get( "endDate" ) ;
            
            //System.out.println( "Inside MetaDataSynchLogReportController 2 1 start date " + sDate + " -- " + eDate  + " -- " + instance.getName() );
            
            //System.out.println( "Inside MetaDataSynchLogReportController 2 1 format start date " + format.parseDate( sDate ) );
            
            //System.out.println( "Inside MetaDataSynchLogReportController 2 1 format end date " + format.parseDate( eDate ) );
            
            //metaDataSynchLogs= new ArrayList<MetaDataSynchLog>( metaDataSynchLogService.getMetaDataSynchLogBetweenDates( instance, format.parseDate( sDate ), format.parseDate( eDate ) ) );
            
            metaDataSynchLogs= new ArrayList<MetaDataSynchLog>( metaDataSynchLogService.getMetaDataSynchLogBetweenDates( instance, sDate ,  eDate ) );
            
            //metaDataSynchLogs= (List<MetaDataSynchLog>) metaDataSynchLogService.getMetaDataSynchLogBetweenDates( instance, format.parseDate( sDate ), format.parseDate( eDate ) );
            
            dataElementSynchStatus = new ArrayList<DataElementSynchStatus>( dataElementSynchStatusService.getPendingDataElementSyncStatus( instance ) );
            indicatorSynchStatus = new ArrayList<IndicatorSynchStatus>( indicatorSynchStatusService.getPendingIndicatorSyncStatus( instance ) );
            organisationUnitSynchStatus = new ArrayList<OrganisationUnitSynchStatus>( organisationUnitSynchStatusService.getPendingOrganisationUnitSyncStatus( instance ) );
            validationRuleSynchStatus = new ArrayList<ValidationRuleSynchStatus>( validationRuleSynchStatusService.getPendingValidationRuleSyncStatus( instance ) );
            
            //System.out.println("Inside MetaDataSynchLogReportController 2 2 " + instance.getName() + "---" +  metaDataSynchLogs.size() + "--" + dataElementSynchStatus.size() + "--" + indicatorSynchStatus.size() + "--" + organisationUnitSynchStatus.size() +"--" + validationRuleSynchStatus.size() );
        }         
        else if ( options.getOptions().containsKey( "cleintURL" ) && options.getOptions().containsKey( "startDate" ) )
        {
            url = options.getOptions().get("cleintURL");
            
            SynchInstance instance = synchInstanceService.getInstanceByUrl( url );
            
            sDate =  options.getOptions().get( "startDate" ) ; 
            
            //System.out.println( "Inside MetaDataSynchLogReportController 3 1 Satrt date " + sDate );
            
            metaDataSynchLogs= new ArrayList<MetaDataSynchLog>( metaDataSynchLogService.getMetaDataSynchLog( instance, format.parseDate( sDate ) ) );
            
            dataElementSynchStatus = new ArrayList<DataElementSynchStatus>( dataElementSynchStatusService.getPendingDataElementSyncStatus( instance ) );
            indicatorSynchStatus = new ArrayList<IndicatorSynchStatus>( indicatorSynchStatusService.getPendingIndicatorSyncStatus( instance ) );
            organisationUnitSynchStatus = new ArrayList<OrganisationUnitSynchStatus>( organisationUnitSynchStatusService.getPendingOrganisationUnitSyncStatus( instance ) );
            validationRuleSynchStatus = new ArrayList<ValidationRuleSynchStatus>( validationRuleSynchStatusService.getPendingValidationRuleSyncStatus( instance ) );
            
            //System.out.println("Inside MetaDataSynchLogReportController 3  2 " + instance.getName() + "---" + metaDataSynchLogs.size() + "--" + dataElementSynchStatus.size() + "--" + indicatorSynchStatus.size() + "--" + organisationUnitSynchStatus.size() +"--" + validationRuleSynchStatus.size() );
        }         
        
        else if ( options.getOptions().containsKey( "cleintURL" ) )
        {
            url = options.getOptions().get("cleintURL");
            
            SynchInstance instance = synchInstanceService.getInstanceByUrl( url );
            
            //System.out.println("Inside MetaDataSynchLogReportController 4 1 Instance name " + instance.getName() );
            
            metaDataSynchLogs= new ArrayList<MetaDataSynchLog>( metaDataSynchLogService.getMetaDataSynchLog( instance ) );
            
            dataElementSynchStatus = new ArrayList<DataElementSynchStatus>( dataElementSynchStatusService.getPendingDataElementSyncStatus( instance ) );
            indicatorSynchStatus = new ArrayList<IndicatorSynchStatus>( indicatorSynchStatusService.getPendingIndicatorSyncStatus( instance ) );
            organisationUnitSynchStatus = new ArrayList<OrganisationUnitSynchStatus>( organisationUnitSynchStatusService.getPendingOrganisationUnitSyncStatus( instance ) );
            validationRuleSynchStatus = new ArrayList<ValidationRuleSynchStatus>( validationRuleSynchStatusService.getPendingValidationRuleSyncStatus( instance ) );
            
            //System.out.println("Inside MetaDataSynchLogReportController 4  2 " + instance.getName() + "---" + metaDataSynchLogs.size() + "--" + dataElementSynchStatus.size() + "--" + indicatorSynchStatus.size() + "--" + organisationUnitSynchStatus.size() +"--" + validationRuleSynchStatus.size() );
        } 
        else
        {               
            metaDataSynchLogs= new ArrayList<MetaDataSynchLog>( metaDataSynchLogService.getAllMetaDataSynchLog() );
            
            //System.out.println("Inside MetaDataSynchLogReportController 5 "  + metaDataSynchLogs.size() + "--" + dataElementSynchStatus.size() + "--" + indicatorSynchStatus.size() + "--" + organisationUnitSynchStatus.size() +"--" + validationRuleSynchStatus.size() );
            
            
        }
        
        metaData.setMetaDataSynchLogs( metaDataSynchLogs );
        metaData.setDataElementSynchStatus( dataElementSynchStatus );
        metaData.setIndicatorSynchStatus( indicatorSynchStatus );
        metaData.setOrganisationUnitSynchStatus( organisationUnitSynchStatus );
        metaData.setValidationRuleSynchStatus( validationRuleSynchStatus );
        
        //System.out.println("Inside MetaDataSynchLogReportController Final "  + metaDataSynchLogs.size() + "--" + dataElementSynchStatus.size() + "--" + indicatorSynchStatus.size() + "--" + organisationUnitSynchStatus.size() +"--" + validationRuleSynchStatus.size() );
        
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.NO_CACHE, "metaData.xml", true );

        Class<?> viewClass = JacksonUtils.getViewClass( options.getViewClass( "export" ) );
        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, viewClass );
                
    }
}
