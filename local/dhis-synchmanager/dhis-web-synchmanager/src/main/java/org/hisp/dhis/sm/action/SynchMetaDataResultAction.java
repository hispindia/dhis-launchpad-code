package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportTypeSummary;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLog;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.sm.impl.SynchManager;
import org.hisp.dhis.sm.util.MetaDataSynchLogWrapper;
import org.hisp.dhis.sm.util.MetaDataValidationCheck;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * Created by gaurav on 16/8/14.
 */

public class SynchMetaDataResultAction
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

        try
        {

            String userUid = currentUserService.getCurrentUser().getUid();

            // List<ValidationRule> validationRuleList = new
            // ArrayList<ValidationRule> (
            // validationRuleSynchStatusService.getValidationRuleList() );

            // System.out.println( " validationRuleList Size-- " +
            // validationRuleList.size() );

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

            System.out.println( "Selected OrganisationUnit UIDs : " + orgUnitUIDs );
            System.out.println( "Selected indicator UIDs : " + indicatorUIDs );
            System.out.println( "Selected dataElements UIDs : " + dataElementsUIDs );
            System.out.println( "Selected validationRule UIDs : " + validationRuleUIDs );
            
            Set<SynchInstance> instances = synchInstanceService.getInstancesByType( "meta-data" );
            
            String newUrl = instances.iterator().next().getUrl() + "/MetaDataSynchManager.xml?DE=" + dataElementsUIDs +"&IND=" + indicatorUIDs + "&ORGUNIT=" + orgUnitUIDs + "&VR=" + validationRuleUIDs;
            
            
            
            // ----TO DO-----//

            // String url = instances.iterator().next().getUrl() +
            // "/metadata.xml?assumeTrue=false&dataElements=true&categoryOptionCombos=true&categoryCombos=true&categoryOptions=true&optionSets=true&attributes=true";
            // String url = instances.iterator().next().getUrl() +
            // "/metadata.xml?assumeTrue=false&indicatorTypes=true&indicators=true&dataElements=true&categoryOptionCombos=true&categoryCombos=true&categoryOptions=true&optionSets=true&attributes=true";

            // for indicator

            // /metadata.xml?assumeTrue=false&validationRuleGroups=true&organisationUnitGroupSets=true&organisationUnitGroups=true&indicatorGroupSets=true&indicatorGroups=true&dataElementGroupSets=true&dataElementGroups=true&validationRules=true&&organisationUnits=true&indicatorTypes=true&indicators=true&dataElements=true&categoryOptionCombos=true&categoryCombos=true&categoryOptions=true&optionSets=true&attributes=true

            // String url = instances.iterator().next().getUrl() +
            // "/metadata.xml?assumeTrue=false&organisationUnits=true&indicatorTypes=true&indicators=true&dataElements=true&categoryOptionCombos=true&categoryCombos=true&categoryOptions=true&optionSets=true&attributes=true";
            // url for meta data including validationRules orgUnits
            // indicators,tndicatorTypes and dataElements categoryCombination
            // optionset and dataElements.
            String url = instances.iterator().next().getUrl()
                + "/metadata.xml?assumeTrue=false&validationRuleGroups=true&organisationUnitGroupSets=true&organisationUnitGroups=true&indicatorGroupSets=true&indicatorGroups=true&dataElementGroupSets=true&dataElementGroups=true&validationRules=true&&organisationUnits=true&indicatorTypes=true&indicators=true&dataElements=true&categoryOptionCombos=true&categoryCombos=true&categoryOptions=true&optionSets=true&attributes=true";

            MetaData metaData = synchManager.getMetaData( instances.iterator().next(), url, null );

            /*
             * System.out.println(
             * "**************OULIST FROM METADATA START*****************" );
             * for( OrganisationUnit orgUnit : metaData.getOrganisationUnits() )
             * { System.out.println( orgUnit.getShortName() ); }
             * System.out.println(
             * "********************OULIST FROM METADATA END****************************"
             * );
             */

            MetaDataValidationCheck metaDataValidationCheck = new MetaDataValidationCheck();

            // MetaData dataElementMetaData =
            // metaDataValidationCheck.getMetaDataDataElement( deUIDList,
            // metaData);

            // System.out.println( "dddddddddd -- " +
            // metaData.getDataElements().size() );
            // System.out.println( "cccccccccc -- " +
            // metaData.getCategoryCombos().size() );
            // System.out.println( "oooooooooo -- " +
            // metaData.getOptionSets().size() );
            // System.out.println( "iiiiiiiiii -- " +
            // metaData.getIndicators().size() );
            // System.out.println( "orrrrrrrrr -- " +
            // metaData.getOrganisationUnits().size() );

            // MetaData synchMetaDataDE = metaDataValidationCheck.getMetaData(
            // metaData, deUIDList, indicatorUIDList, organisationUnitUIDList );

            // System.out.println( "META DETA -- " +
            // metaData.getDataElements().size() );

            MetaData synchMetaDataDE = metaDataValidationCheck.getMetaDataDataElement( deUIDList, metaData );

            // System.out.println( "META DETA -- " +
            // metaData.getIndicators().size() );

            MetaData synchMetaDataI = metaDataValidationCheck.getMetaDataIndicator( indicatorUIDList, metaData );

            MetaData synchMetaDataOU = metaDataValidationCheck.getMetaDataOrganisationUnit( organisationUnitUIDList,
                metaData );

            MetaData synchMetaDataValidationRule = metaDataValidationCheck.getMetaDataValidationRule(
                validationRuleUIDList, metaData );

            Set<DataElement> otherDes = new HashSet<DataElement>();
            Set<DataElementCategoryCombo> otherCategoryCombos = new HashSet<DataElementCategoryCombo>();
            Set<OptionSet> otherOptionSets = new HashSet<OptionSet>();

            otherDes.addAll( synchMetaDataDE.getDataElements() );
            // indicator related dataElements
            otherDes.addAll( synchMetaDataI.getDataElements() );
            // validationRule related dataElements
            otherDes.addAll( synchMetaDataValidationRule.getDataElements() );

            otherCategoryCombos.addAll( synchMetaDataDE.getCategoryCombos() );
            // indicator related CategoryCombos
            otherCategoryCombos.addAll( synchMetaDataI.getCategoryCombos() );
            // validationRule related CategoryCombos
            otherCategoryCombos.addAll( synchMetaDataValidationRule.getCategoryCombos() );

            otherOptionSets.addAll( synchMetaDataDE.getOptionSets() );
            // indicator related OptionSets
            otherOptionSets.addAll( synchMetaDataI.getOptionSets() );
            // validationRule related OptionSets
            otherOptionSets.addAll( synchMetaDataValidationRule.getOptionSets() );

            // set indicator related and validation rule related data element
            // category combination and option set
            metaData.setDataElements( new ArrayList<>( otherDes ) );
            metaData.setCategoryCombos( new ArrayList<>( otherCategoryCombos ) );
            metaData.setOptionSets( new ArrayList<>( otherOptionSets ) );

            metaData.setDataElementGroups( new ArrayList<>( synchMetaDataDE.getDataElementGroups() ) );
            metaData.setDataElementGroupSets( new ArrayList<>( synchMetaDataDE.getDataElementGroupSets() ) );

            // set indicators and indicator types
            metaData.setIndicators( new ArrayList<>( synchMetaDataI.getIndicators() ) );
            metaData.setIndicatorTypes( new ArrayList<>( synchMetaDataI.getIndicatorTypes() ) );
            metaData.setIndicatorGroups( new ArrayList<>( synchMetaDataI.getIndicatorGroups() ) );
            metaData.setIndicatorGroupSets( new ArrayList<>( synchMetaDataI.getIndicatorGroupSets() ) );

            // set OrganisationUnits
            metaData.setOrganisationUnits( new ArrayList<>( synchMetaDataOU.getOrganisationUnits() ) );
            metaData.setOrganisationUnitGroups( new ArrayList<>( synchMetaDataOU.getOrganisationUnitGroups() ) );
            metaData.setOrganisationUnitGroupSets( new ArrayList<>( synchMetaDataOU.getOrganisationUnitGroupSets() ) );

            // set ValidationRules
            metaData.setValidationRules( new ArrayList<>( synchMetaDataValidationRule.getValidationRules() ) );
            metaData.setValidationRuleGroups( new ArrayList<>( synchMetaDataValidationRule.getValidationRuleGroups() ) );

            /*
             * dataElements = metaData.getDataElements();
             * 
             * List<DataElementCategoryCombo> deCCs =
             * metaData.getCategoryCombos(); List<OptionSet> optionSets =
             * metaData.getOptionSets(); List<Attribute> attributes =
             * metaData.getAttributes();
             * 
             * Set<DataElement> filteredDataElements = new HashSet<>();
             * Set<DataElementCategoryCombo> filteredDECCs = new HashSet<>();
             * Set<OptionSet> filteredOptionSets = new HashSet<>();
             * Set<Attribute> filteredAttributes = new HashSet<>();
             * 
             * for ( String dataElementUID : dataElementUIDList ) {
             * deUIDList.add( dataElementUID );
             * 
             * for ( DataElement dataElement : dataElements ) { if (
             * dataElement.getUid().equals( dataElementUID ) ) {
             * filteredDataElements.add( dataElement );
             * 
             * for( DataElementCategoryCombo decc : deCCs ) { if(
             * decc.getUid().equals( dataElement.getCategoryCombo().getUid() ) )
             * { filteredDECCs.add( decc ); break; } }
             * 
             * for( OptionSet os : optionSets ) { if( os.getUid().equals(
             * dataElement.getOptionSet().getUid() ) ) { filteredOptionSets.add(
             * os ); break; } }
             * 
             * break; } } }
             * 
             * metaData.setDataElements( new ArrayList<> ( filteredDataElements
             * ) ); metaData.setCategoryCombos( new ArrayList<>( filteredDECCs )
             * ); metaData.setOptionSets( new ArrayList<>( filteredOptionSets )
             * );
             */

            importDetails = "<table class=\"listTable\"><tr><th>Type</th><th>Import Count</th></tr>";

            // importSummary = importService.importMetaData( userUid, metaData
            // );

            // importSummary = importService.importMetaData( userUid,
            // synchMetaDataValidationRule );

            System.out.println( "Size :" + metaData.getValidationRules().size() );

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

            // thisUrl = "http://192.168.0.102:8090/dhis/api";

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

            /*
             * if( indicatorssUIDs !=null && indicatorssUIDs.length() > 0 ) {
             * String[] indicatorUIDArray = indicatorssUIDs.split( "," );
             * 
             * List<String> indicatorUIDList = new ArrayList<String>();
             * 
             * for ( String indicatorUid : indicatorUIDArray ) {
             * indicatorUIDList.add( indicatorUid ); }
             * 
             * MetaData indicatorMetaData =
             * metaDataValidationCheck.getMetaDataIndicator( indicatorUIDList,
             * metaData);
             * 
             * importSummary = importService.importMetaData( userUid,
             * indicatorMetaData );
             * 
             * // for indicator MetaData metaDataIndicator = new MetaData();
             * 
             * List<MetaDataSynchLog> metaDataSynchIndicatorLogs = new
             * ArrayList<MetaDataSynchLog>();
             * 
             * MetaDataSynchLogWrapper metaDataSynchIndicatorLogWrapper = new
             * MetaDataSynchLogWrapper();
             * 
             * MetaDataSynchLog metaDataSynchIndicatorLog =
             * metaDataSynchIndicatorLogWrapper.getMetaDataSynchLog(
             * importSummary, "Indicator", indicatorUIDList );
             * 
             * metaDataSynchIndicatorLog.setUrl( thisUrl );
             * 
             * metaDataSynchIndicatorLogs.add( metaDataSynchIndicatorLog );
             * 
             * metaDataIndicator.setMetaDataSynchLogs(
             * metaDataSynchIndicatorLogs );
             * 
             * synchManager.postMetaData( metaDataIndicator,
             * instances.iterator().next().getUrl()+"/MetaDataSynchLog.json",
             * instances.iterator().next() );
             * 
             * }
             */

        }
        catch ( Exception e )
        {
            e.printStackTrace();

        }

        return SUCCESS;
    }
}
