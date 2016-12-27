package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportTypeSummary;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLog;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.sm.impl.SynchManager;
import org.hisp.dhis.sm.util.MetaDataSynchLogWrapper;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */


public class SynchMetaDataResultAction_New
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private ImportService importService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private ValidationRuleSynchStatusService validationRuleSynchStatusService;

    private SynchManager synchManager;

    public void setSynchManager( SynchManager synchManager )
    {
        this.synchManager = synchManager;
    }

    private SynchInstanceService synchInstanceService;

    public void setSynchInstanceService( SynchInstanceService synchInstanceService )
    {
        this.synchInstanceService = synchInstanceService;
    }

    // ------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    /*
     * private Integer instanceId;
     * 
     * public void setInstanceId( Integer instanceId ) { this.instanceId =
     * instanceId; }
     */

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    public String dataElementsUIDs;

    public String getDataElementsUIDs()
    {
        return dataElementsUIDs;
    }

    public void setDataElementsUIDs( String dataElementsUIDs )
    {
        this.dataElementsUIDs = dataElementsUIDs;
    }

    private String thisUrl;

    public void setThisUrl( String thisUrl )
    {
        this.thisUrl = thisUrl;
    }

    private ImportSummary importSummary;

    public ImportSummary getImportSummary()
    {
        return importSummary;
    }

    private String importDetails;

    public String getImportDetails()
    {
        return importDetails;
    }

    public void setImportDetails( String importDetails )
    {
        this.importDetails = importDetails;
    }

    public String indicatorUIDs;

    public void setIndicatorUIDs( String indicatorUIDs )
    {
        this.indicatorUIDs = indicatorUIDs;
    }

    public String orgUnitUIDs;

    public void setOrgUnitUIDs( String orgUnitUIDs )
    {
        this.orgUnitUIDs = orgUnitUIDs;
    }

    public String validationRuleUIDs;

    public void setValidationRuleUIDs( String validationRuleUIDs )
    {
        this.validationRuleUIDs = validationRuleUIDs;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {

        System.out.println( "This URL is " + thisUrl );
        
        if( thisUrl == null || thisUrl.length() == 0  )
        {
            HttpServletRequest request = ServletActionContext.getRequest();
            
            String thisUrlTemp = request.getRequestURL().toString();
            
            String [] urlFrgmant = thisUrlTemp.split("/");
                    
            System.out.println( "This URL is after setting the url " + urlFrgmant[0]+"/"+urlFrgmant[1]+"/"+urlFrgmant[2]+"/"+urlFrgmant[3]+"/api" );
            
            thisUrl = urlFrgmant[0]+"/"+urlFrgmant[1]+"/"+urlFrgmant[2]+"/"+urlFrgmant[3]+"/api";
        }
        
        try
        {

            String userUid = currentUserService.getCurrentUser().getUid();

            List<String> deUIDList = new ArrayList<String>();
            List<String> indicatorUIDList = new ArrayList<String>();
            List<String> organisationUnitUIDList = new ArrayList<String>();
            List<String> validationRuleUIDList = new ArrayList<String>();
            
            
            // DataElement UID list
            if ( dataElementsUIDs != null && dataElementsUIDs.length() > 0 )
            {
                String[] dataElementUIDList = dataElementsUIDs.split( "," );

                for ( String dataElementUID : dataElementUIDList )
                {
                    deUIDList.add( dataElementUID );
                }

            }
            else
            {
                dataElementsUIDs = "NA";
            }
            // Indicator UID list
            // indicatorsUIDs = "V1k8W2g9C3z";

            if ( indicatorUIDs != null && indicatorUIDs.length() > 0 )
            {
                String[] indicatorUIDArray = indicatorUIDs.split( "," );

                for ( String indicatorUid : indicatorUIDArray )
                {
                    indicatorUIDList.add( indicatorUid );
                }
            }
            else
            {
                indicatorUIDs = "NA";
            }
            // OrganisationUnit UID list
            // organisationUnitUIDs = "QV1L9mvpcGY";

            if ( orgUnitUIDs != null && orgUnitUIDs.length() > 0 )
            {
                String[] organisationUnitUIDArray = orgUnitUIDs.split( "," );

                for ( String organisationUnitUid : organisationUnitUIDArray )
                {
                    organisationUnitUIDList.add( organisationUnitUid );
                }
            }
            else
            {
                orgUnitUIDs = "NA";
            }
            // ValidationRule UID list

            // validationRuleUIDs = "Z2a6N5c2Q6g";

            if ( validationRuleUIDs != null && validationRuleUIDs.length() > 0 )
            {
                String[] validationRuleUIDArray = validationRuleUIDs.split( "," );

                for ( String validationruleUid : validationRuleUIDArray )
                {
                    validationRuleUIDList.add( validationruleUid );
                }
            }
            {
                validationRuleUIDs = "NA";
            }
            
            System.out.println( "Selected OrganisationUnit UIDs : " + orgUnitUIDs );
            System.out.println( "Selected indicator UIDs : " + indicatorUIDs );
            System.out.println( "Selected dataElements UIDs : " + dataElementsUIDs );
            System.out.println( "Selected validationRule UIDs : " + validationRuleUIDs );
            
            Set<SynchInstance> instances = synchInstanceService.getInstancesByType( "meta-data" );
            
            String url = instances.iterator().next().getUrl() + "/MetaDataSynchManager.xml?DE=" + dataElementsUIDs +"&IND=" + indicatorUIDs + "&ORGUNIT=" + orgUnitUIDs + "&VR=" + validationRuleUIDs;
            
            System.out.println( "Posted URL: " + url );
            
            MetaData metaData = new MetaData();
            
            //metaData = synchManager.getMetaDataByPostUrl( instances.iterator().next(), url );
            
            metaData = synchManager.getMetaData( instances.iterator().next(), url, null );
            
            
            System.out.println( "Result Return MetaData: " + metaData );

            importDetails = "<table class=\"listTable\"><tr><th>Type</th><th>Import Count</th></tr>";
  
            importSummary = importService.importMetaData( userUid, metaData );

            List<ImportTypeSummary> importTypeSummaryList = importSummary.getImportTypeSummaries();

            for ( ImportTypeSummary importTypeSummary : importTypeSummaryList )
            {
                importTypeSummary.getType();
                importTypeSummary.getImportCount();

                importDetails = importDetails.concat( "<tr><td>" + importTypeSummary.getType() + "</td><td>"
                    + importTypeSummary.getImportCount() + "</td></tr>" );
            }

            importDetails = importDetails.concat( "</table>" );


            MetaData metaData1 = new MetaData();

            List<MetaDataSynchLog> metaDataSynchLogs = new ArrayList<MetaDataSynchLog>();

            MetaDataSynchLogWrapper metaDataSynchLogWrapper = new MetaDataSynchLogWrapper();

            MetaDataSynchLog metaDataSynchLog1 = metaDataSynchLogWrapper.getMetaDataSynchLog( importSummary,
                MetaDataSynchLog.METADATA_TYPE_DATAELEMENT, deUIDList );

            MetaDataSynchLog metaDataSynchLog2 = metaDataSynchLogWrapper.getMetaDataSynchLog( importSummary,
                MetaDataSynchLog.METADATA_TYPE_INDICATOR, indicatorUIDList );

            MetaDataSynchLog metaDataSynchLog3 = metaDataSynchLogWrapper.getMetaDataSynchLog( importSummary,
                MetaDataSynchLog.METADATA_TYPE_ORGUNIT, organisationUnitUIDList );

            MetaDataSynchLog metaDataSynchLog4 = metaDataSynchLogWrapper.getMetaDataSynchLog( importSummary,
                MetaDataSynchLog.METADATA_TYPE_VALIDATIONRULE, validationRuleUIDList );

            metaDataSynchLog1.setMetaDataType( MetaDataSynchLog.METADATA_TYPE_DATAELEMENT );
            metaDataSynchLog2.setMetaDataType( MetaDataSynchLog.METADATA_TYPE_INDICATOR );
            metaDataSynchLog3.setMetaDataType( MetaDataSynchLog.METADATA_TYPE_ORGUNIT );
            metaDataSynchLog4.setMetaDataType( MetaDataSynchLog.METADATA_TYPE_VALIDATIONRULE );

            metaDataSynchLog1.setUrl( thisUrl );
            metaDataSynchLog2.setUrl( thisUrl );
            metaDataSynchLog3.setUrl( thisUrl );
            metaDataSynchLog4.setUrl( thisUrl );

            metaDataSynchLogs.add( metaDataSynchLog1 );
            metaDataSynchLogs.add( metaDataSynchLog2 );
            metaDataSynchLogs.add( metaDataSynchLog3 );
            metaDataSynchLogs.add( metaDataSynchLog4 );

            metaData1.setMetaDataSynchLogs( metaDataSynchLogs );

            synchManager.postMetaData( metaData1, instances.iterator().next().getUrl() + "/MetaDataSynchLog.json",
                instances.iterator().next() );


        }
        catch ( Exception e )
        {
            e.printStackTrace();

        }

        return SUCCESS;
    }
}
