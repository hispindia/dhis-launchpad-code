package org.hisp.dhis.sm.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateMetaDataSynchLogReportResultAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SynchInstanceService synchInstanceService;

    public void setSynchInstanceService( SynchInstanceService synchInstanceService )
    {
        this.synchInstanceService = synchInstanceService;
    }
    
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
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    public I18nFormat getFormat()
    {
        return format;
    }
    
    
    // -------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------


    private Integer instanceId;
    
    public void setInstanceId( Integer instanceId )
    {
        this.instanceId = instanceId;
    }

    private String selectedStartDate;
    
    public void setSelectedStartDate( String selectedStartDate )
    {
        this.selectedStartDate = selectedStartDate;
    }

    private String selectedEndDate;
    
    public void setSelectedEndDate( String selectedEndDate )
    {
        this.selectedEndDate = selectedEndDate;
    }

    private List<MetaDataSynchLog> metaDataSynchLogs = new ArrayList<MetaDataSynchLog>();
    
    public List<MetaDataSynchLog> getMetaDataSynchLogs()
    {
        return metaDataSynchLogs;
    }

    private SimpleDateFormat simpleDateFormat;
    
    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }
    
    
    private List<DataElementSynchStatus> dataElementSynchStatus = new ArrayList<DataElementSynchStatus>();
    
    public List<DataElementSynchStatus> getDataElementSynchStatus()
    {
        return dataElementSynchStatus;
    }
    
    private List<IndicatorSynchStatus> indicatorSynchStatus = new ArrayList<IndicatorSynchStatus>();
    
    public List<IndicatorSynchStatus> getIndicatorSynchStatus()
    {
        return indicatorSynchStatus;
    }
    
    private List<OrganisationUnitSynchStatus> organisationUnitSynchStatus = new ArrayList<OrganisationUnitSynchStatus>();
    
    public List<OrganisationUnitSynchStatus> getOrganisationUnitSynchStatus()
    {
        return organisationUnitSynchStatus;
    }
    
    private List<ValidationRuleSynchStatus> validationRuleSynchStatus = new ArrayList<ValidationRuleSynchStatus>();
    
    public List<ValidationRuleSynchStatus> getValidationRuleSynchStatus()
    {
        return validationRuleSynchStatus;
    }

    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        //System.out.println( "inside report");
        
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        SynchInstance synchInstance = synchInstanceService.getInstance( instanceId );
        
        //metaDataSynchLogs = (List<MetaDataSynchLog>) metaDataSynchLogService.getMetaDataSynchLog( synchInstance, format.parseDate( selectedDate )  );
        
        metaDataSynchLogs = (List<MetaDataSynchLog>) metaDataSynchLogService.getMetaDataSynchLogBetweenDates( synchInstance, format.parseDate( selectedStartDate ), format.parseDate( selectedEndDate ) );
        
        dataElementSynchStatus = new ArrayList<DataElementSynchStatus>( dataElementSynchStatusService.getPendingDataElementSyncStatus( synchInstance ) );
        
        indicatorSynchStatus = new ArrayList<IndicatorSynchStatus>( indicatorSynchStatusService.getPendingIndicatorSyncStatus( synchInstance ) );
        
        organisationUnitSynchStatus = new ArrayList<OrganisationUnitSynchStatus>( organisationUnitSynchStatusService.getPendingOrganisationUnitSyncStatus( synchInstance ) );
        
        validationRuleSynchStatus = new ArrayList<ValidationRuleSynchStatus>( validationRuleSynchStatusService.getPendingValidationRuleSyncStatus( synchInstance ) );
        
        
        /*
        for( MetaDataSynchLog metaDataSynchLog : metaDataSynchLogs )
        {
            System.out.println( metaDataSynchLog.getSynchInstance().getName() );
            
            System.out.println( metaDataSynchLog.getSynchDate().toString() );
            
            System.out.println( metaDataSynchLog.getRemarks() );
            
            metaDataSynchLog.getSynchDate();
            
            metaDataSynchLog.getMetaDataType();
            metaDataSynchLog.getStatus();
        }
        
        for( DataElementSynchStatus de : dataElementSynchStatus )
        {
            de.getDataElement().getName();
            de.getApproveStatus();
            
            de.getApprovedDate();
            
        }
        
        for( IndicatorSynchStatus indicator : indicatorSynchStatus )
        {
            indicator.getIndicator().getName();
            indicator.getApprovedDate();
        }        
        
        for( OrganisationUnitSynchStatus orgUnitSynchStatus : organisationUnitSynchStatus )
        {
            orgUnitSynchStatus.getOrganisationUnit().getName();
            orgUnitSynchStatus.getApprovedDate();
        }        
               
        for( ValidationRuleSynchStatus vrStatus : validationRuleSynchStatus )
        {
            vrStatus.getValidationRule().getName();
            vrStatus.getApprovedDate();
            
            vrStatus.getLastUpdated();
        }         
       
        */
        
        return SUCCESS;
    }
}
