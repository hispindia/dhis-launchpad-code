package org.hisp.dhis.ovc.report.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportDesign;
import org.hisp.dhis.customreports.CustomReportFilter;
import org.hisp.dhis.customreports.CustomReportService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

public class GenerateNameBasedReportAction implements Action
{
    public static final String OVC_ID = "OVC_ID";//929.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    /*
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    */
    private CustomReportService customReportService;
    
    public void setCustomReportService( CustomReportService customReportService )
    {
        this.customReportService = customReportService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientAttributeService patientAttributeService;
    
    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    /*
    private OVCService ovcService;
    
    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }
    */
    
   private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
    
    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }
    
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer reportId;
    
    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }
    /*
    private Integer rootOrgUnitId;
    
    public void setRootOrgUnitId( Integer rootOrgUnitId )
    {
        this.rootOrgUnitId = rootOrgUnitId;
    }
    */
    private Integer orgUnitGroupId;
    
    public void setOrgUnitGroupId( Integer orgUnitGroupId )
    {
        this.orgUnitGroupId = orgUnitGroupId;
    }
    
    private String startDate;
    
    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }
    
    private String endDate;
    
    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<String, List<String>> resultGrid = new HashMap<String, List<String>>();
    
    public Map<String, List<String>> getResultGrid()
    {
        return resultGrid;
    }

    private List<String> tableHeader = new ArrayList<String>();
    
    public List<String> getTableHeader()
    {
        return tableHeader;
    }

    private String reportTitle;
    
    public String getReportTitle()
    {
        return reportTitle;
    }
    
    private List<String> displaysSlectedPersonPropertiesAndAttributesForPreview = new ArrayList<String>();
    
    public void setDisplaysSlectedPersonPropertiesAndAttributesForPreview(
        List<String> displaysSlectedPersonPropertiesAndAttributesForPreview )
    {
        this.displaysSlectedPersonPropertiesAndAttributesForPreview = displaysSlectedPersonPropertiesAndAttributesForPreview;
    }

    private List<CustomReportDesign> customReportDesigns = new ArrayList<CustomReportDesign>();
    
    private List<CustomReportFilter> customReportFilters = new ArrayList<CustomReportFilter>();
    
    private List<Patient> patients = new ArrayList<Patient>();
    
    public List<Patient> getPatients()
    {
        return patients;
    }
    
    private List<String> searchTexts = new ArrayList<String>();
    
    public void setSearchTexts( List<String> searchTexts )
    {
        this.searchTexts = searchTexts;
    }
    
    private List<String> savedSearchTexts = new ArrayList<String>();
    
    private Date sDate;
    
    private Date eDate;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        //rootOrgUnitId = 133;
        //reportId = 844;
        
        //orgUnitGroupId = 0;
        
        //OrganisationUnit rootOrgunit = organisationUnitService.getOrganisationUnit( rootOrgUnitId );
        
        OrganisationUnit rootOrgunit = selectionManager.getSelectedOrganisationUnit();
        
        /*
        Set<OrganisationUnit> tempOrgUnitList = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( tempOrgUnitList );
        
        List<OrganisationUnit> selectionTreeManager = selectionTreeManager.getReloadedSelectedOrganisationUnits();
        */
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( rootOrgunit.getId() ) );
        
        //List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( rootOrgunit.getId() ) );
       
        //System.out.println( " OrgUnitGroup Id : " + orgUnitGroupId + " -- " + rootOrgunit.getName() );
        
        if ( orgUnitGroupId != null && orgUnitGroupId != 0 )
        {
            OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );
            
            if( orgUnitGroup != null )
            {
                orgUnitList.retainAll( orgUnitGroup.getMembers() );
            }
        }
        
        //Map<Integer, String> mapPatientSystemIdentifier = new HashMap<Integer, String>();
        //mapPatientSystemIdentifier = new HashMap<Integer, String>( ovcService.getPatientIdentifierId() );
        
        //System.out.println("Orgunitlist size: " + orgUnitList.size() );
        
        //sDate = new Date();
        //eDate = new Date();
        
        if( startDate != null && endDate != null )
        {
            sDate = format.parseDate( startDate );
            eDate = format.parseDate( endDate );
        }
        /*
        else
        {
            sDate = format.parseDate( "2013-01-01" );
            eDate = new Date();
            
        }
        */
        
        //System.out.println(" Start Date is : " + sDate );
        //System.out.println(" End Date is : " + eDate );
        
        //Date sDate = format.parseDate( startDate );
        
        //Date eDate = format.parseDate( endDate );
        
        SimpleDateFormat standardDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        customReportDesigns = new ArrayList<CustomReportDesign>();
        
        // for preview report
        
        Integer sortOrder = 1;
        if( displaysSlectedPersonPropertiesAndAttributesForPreview != null )
        {   
            //System.out.println(" Inside Preview Report " );
            reportTitle = "Report Preview";
            
            for( String personPropertyAndAttribute : displaysSlectedPersonPropertiesAndAttributesForPreview )
            {
                String[] properties = personPropertyAndAttribute.split( "_" );
                
                if ( properties[0].equals( "PP" ) )
                {
                    CustomReportDesign customReportDesign = new CustomReportDesign();
                    
                    customReportDesign.setCustomType( properties[0] );
                    customReportDesign.setCustomTypeValue( properties[1] );
                    customReportDesign.setSortOrder( sortOrder );
                    
                    customReportDesigns.add( customReportDesign );
                }
                
                else if ( properties[0].equals( "PA" ) )
                {
                    CustomReportDesign customReportDesign = new CustomReportDesign();
                    
                    PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( Integer.parseInt( properties[1] ) );
                    customReportDesign.setCustomType( properties[0] );
                    customReportDesign.setCustomTypeValue( Integer.toString( patientAttribute.getId() )  );
                    customReportDesign.setSortOrder( sortOrder );
                    
                    customReportDesigns.add( customReportDesign );
                }
                sortOrder++;
            }
        }               
        
        patients = new ArrayList<Patient>();
        
        if( searchTexts == null || searchTexts.size() <= 0 )
        {
            //System.out.println(" Inside Preview Report Not Filter" );
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                patients.addAll( patientService.getPatients( orgUnit, null, null ) );
            }
        }
        else
        {
            //System.out.println(" Inside Preview Report Filter" );
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                patients.addAll( patientService.searchPatients( searchTexts, orgUnit, null, null, null, null ) );
            }
        }
        
        // for report already saved
        if( reportId != null )
        {       
            //System.out.println(" Inside Saved Report " );
            
            //System.out.println(" Inside Saved Report : " + reportId );
            
            
            CustomReport customReport = customReportService.getCustomReportById( reportId );
            
            customReportDesigns = new ArrayList<CustomReportDesign>( customReport.getCustomReportDesigns() );
            
            //List<CustomReportFilter> customReportFilters = new ArrayList<CustomReportFilter>( customReport.getCustomReportFilters() );
            
            customReportFilters = new ArrayList<CustomReportFilter>( customReport.getCustomReportFilters() );
            
            reportTitle = customReport.getName();
            
            //List<String> savedSearchTexts = new ArrayList<String>();
            
            savedSearchTexts = new ArrayList<String>();
            
            for( CustomReportFilter customReportFilter : customReportFilters )
            {
                savedSearchTexts.add( customReportFilter.getSearchTexts() );
            }
            
            patients = new ArrayList<Patient>();
            
            //List<Patient> patients = new ArrayList<Patient>();
            if( savedSearchTexts == null || savedSearchTexts.size() <= 0 )
            {
                //total = patientService.countGetPatientsByOrgUnit( organisationUnit );
                for( OrganisationUnit orgUnit : orgUnitList )
                {
                    patients.addAll( patientService.getPatients( orgUnit, null, null ) );
                }
            }
            else
            {
                //int total = patientService.countSearchPatients( searchTexts, rootOrgunit );
                for( OrganisationUnit orgUnit : orgUnitList )
                {
                    patients.addAll( patientService.searchPatients( savedSearchTexts, orgUnit, null, null, null, null ) );
                }
                //patients = new ArrayList<Patient>( patientService.searchPatients( searchTexts, rootOrgunit, null, null ) );
            }
            
        }
        
        tableHeader.add( "HIERARCHY" );
        for( CustomReportDesign customReportDesign : customReportDesigns )
        {
            if( customReportDesign.getCustomType().equalsIgnoreCase( "PP" ) )
            {
                if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "firstname" ) )
                {
                    tableHeader.add( "FIRST NAME" );
                }
                else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "middlename" ) )
                {
                    tableHeader.add( "MIDDLE NAME" );
                }
                else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "lastname" ) )
                {
                    tableHeader.add( "LAST NAME" );
                }
                else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "fullname" ) )
                {
                    tableHeader.add( "FULL NAME" );
                }
                else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "identifier" ) )
                {
                    tableHeader.add( "IDENTIFIER" );
                }
                else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "age" ) )
                {
                    tableHeader.add( "AGE" );
                }
                else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "birthdate" ) )
                {
                    tableHeader.add( "DATE OF BIRTH" );
                }
                else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "registrationdate" ) )
                {
                    tableHeader.add( "REGISTRATION DATE" );
                }
                else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "gender" ) )
                {
                    tableHeader.add( "GENDER" );
                }
            }
            else if( customReportDesign.getCustomType().equalsIgnoreCase( "PA" ) )
            {
                PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( Integer.parseInt( customReportDesign.getCustomTypeValue() ) );
                
                tableHeader.add( patientAttribute.getName() );
            }                        
        }
        
        
        Constant patientIdentifierTypeConstant = constantService.getConstantByName( OVC_ID );
        PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( (int) patientIdentifierTypeConstant.getValue() );
        
        
        int rowCount = 1;
        
        int patientCount = 0;
        
        //System.out.println(" Patient List is : " + patients.size() );
        
        Iterator<Patient> patientIterator = patients.iterator();
        
        while( patientIterator.hasNext() )
        {
            Patient patient = patientIterator.next();
            
            if ( ( patient.getRegistrationDate().compareTo( sDate ) >= 0 ) && ( patient.getRegistrationDate().compareTo( eDate ) <= 0 ) )
            {
                //patientIterator.remove( );
                patientCount++;
                
                //System.out.println(" Patient Id : " + patient.getId() + " -- Patient Name : " + patient.getFullName() );
                
                List<String> oneRow = new ArrayList<String>();
                
                oneRow.add( getHierarchyOrgunit( patient.getOrganisationUnit() ) );
                
                for( CustomReportDesign customReportDesign : customReportDesigns )
                {
                    String cellValue = "";
                    if( customReportDesign.getCustomType().equalsIgnoreCase( "PP" ) )
                    {
                        if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "firstname" ) )
                        {
                            cellValue = patient.getFirstName();
                            
                        }
                        else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "middlename" ) )
                        {
                            cellValue = patient.getMiddleName();
                        }
                        else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "lastname" ) )
                        {
                            cellValue = patient.getLastName();
                        }
                        else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "fullname" ) )
                        {
                            cellValue = patient.getFullName();
                        }
                        else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "identifier" ) )
                        {
                            //cellValue = patientIdentifierService.getPatientIdentifier( patient ).getIdentifier();
                            
                            //cellValue = mapPatientSystemIdentifier.get( patient.getId() );
                            
                            for ( PatientIdentifier identifier : patient.getIdentifiers() )
                            {
                                identifierType = identifier.getIdentifierType();

                                if ( patient.getOrganisationUnit().getCode() != null && identifierType != null )
                                {
                                    PatientIdentifier ovcIdIdentifier = patientIdentifierService.getPatientIdentifier( identifierType, patient );
                                    cellValue = ovcIdIdentifier.getIdentifier();
                                }
                                else
                                {
                                    cellValue = identifier.getIdentifier();
                                }                                
                            }
                                                        
                        }
                        else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "age" ) )
                        {
                            cellValue = patient.getAge();
                        }
                        else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "birthdate" ) )
                        {
                            cellValue = standardDateFormat.format( patient.getBirthDate() );
                        }
                        else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "gender" ) )
                        {
                            cellValue = patient.getGender();
                        }
                        
                        else if( customReportDesign.getCustomTypeValue().equalsIgnoreCase( "registrationdate" ) )
                        {
                            cellValue = standardDateFormat.format( patient.getRegistrationDate() );
                        }
                        
                    }
                    else if( customReportDesign.getCustomType().equalsIgnoreCase( "PA" ) )
                    {
                        PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( Integer.parseInt( customReportDesign.getCustomTypeValue() ) );
                        PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, patientAttribute );
                        
                        if( patientAttributeValue != null )
                        {
                            cellValue = patientAttributeValue.getValue();
                        }
                    }
                    
                    if( cellValue == null ) cellValue = "";
                    oneRow.add( cellValue );
                }
                
                resultGrid.put( "row"+rowCount, oneRow );
                
                rowCount++;
            }
        }
        
        System.out.println(" Final Patient List is : " + patientCount );
        
        return SUCCESS;
    }
    
    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " / " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
}
