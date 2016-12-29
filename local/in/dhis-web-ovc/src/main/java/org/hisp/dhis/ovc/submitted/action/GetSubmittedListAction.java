package org.hisp.dhis.ovc.submitted.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

public class GetSubmittedListAction extends ActionPagingSupport<Patient>
{
    public static final String OVC_MONTHLY_VISIT = "OVC Monthly Visit";
    public static final String OVC_ID = "OVC_ID";//929.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
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

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    /*
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    */
    private String status;
    
    public String getStatus()
    {
        return status;
    }

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }
    
    private Collection<Patient> patients = new ArrayList<Patient>();
    
    public Collection<Patient> getPatients()
    {
        return patients;
    }
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    private boolean listAll;
    
    public boolean isListAll()
    {
        return listAll;
    }

    public void setListAll( boolean listAll )
    {
        this.listAll = listAll;
    }
    
    private List<String> searchTexts = new ArrayList<String>();
    
    public void setSearchTexts( List<String> searchTexts )
    {
        this.searchTexts = searchTexts;
    }
    
    private Boolean searchBySelectedOrgunit;
    
    public void setSearchBySelectedOrgunit( Boolean searchBySelectedOrgunit )
    {
        this.searchBySelectedOrgunit = searchBySelectedOrgunit;
    }
    
    private Map<Integer, String> mapPatientOrgunit = new HashMap<Integer, String>();

    public Map<Integer, String> getMapPatientOrgunit()
    {
        return mapPatientOrgunit;
    }
    
    private Integer submittedTotal;
    
    public Integer getSubmittedTotal()
    {
        return submittedTotal;
    }
    
    private Map<Integer, String> mapPatientSystemIdentifier = new HashMap<Integer, String>();
    
    public Map<Integer, String> getMapPatientSystemIdentifier()
    {
        return mapPatientSystemIdentifier;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        status = i18n.getString( "none" );
        
        //organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        program = programService.getProgramByName( OVC_MONTHLY_VISIT );
        
        if ( ( organisationUnit == null ) || ( !program.getOrganisationUnits().contains( organisationUnit ) ) )
        {
            
            status = i18n.getString( "please_select_cbo" );

            return SUCCESS;
        }

        /*
        total = patientService.countGetPatientsByOrgUnit( organisationUnit );
        
        this.paging = createPaging( total );
        
        patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, paging.getStartPos(), paging.getPageSize() ) );
        */
        
        //System.out.println( "Search By Selected Orgunit : " + searchBySelectedOrgunit + " -- Organisation Unit  " + organisationUnit.getName() );
        
        // List all patients
        if ( listAll )
        {
            /*
            total = patientService.countGetPatientsByOrgUnit( organisationUnit );
            
            this.paging = createPaging( total );
            
            patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, paging.getStartPos(), paging.getPageSize() ) );
            */
            
            
            List<String> searchTexts = new ArrayList<String>();
            
            searchTexts.add( "attr_301_submitted" );

            total = patientService.countSearchPatients( searchTexts, organisationUnit, null );
            
            submittedTotal = patientService.countSearchPatients( searchTexts, organisationUnit, null );
            
            this.paging = createPaging( total );
            
            patients = patientService.searchPatients( searchTexts, organisationUnit, null, null, paging.getStartPos(), paging.getPageSize() );
            
        }
        
        // search patients
        else if ( searchTexts.size() > 0 )
        {
            organisationUnit = (searchBySelectedOrgunit) ? organisationUnit : null;
            
            searchTexts.add( "attr_301_submitted" );
            
            //System.out.println( " Inside Filter" );
            
            //System.out.println( "Search By Selected Orgunit : " + searchBySelectedOrgunit + " -- Organisation Unit  " + organisationUnit );
            
            total = patientService.countSearchPatients( searchTexts, organisationUnit, null );
            submittedTotal = patientService.countSearchPatients( searchTexts, organisationUnit, null );
            
            this.paging = createPaging( total );
            patients = patientService.searchPatients( searchTexts, organisationUnit, null, null, paging.getStartPos(), paging.getPageSize() );
            
            /*
            for( String searchText : searchTexts )
            {
                String[] keys = searchText.split( "_" );
                System.out.println( "SearchText : " + searchText  + " : " + keys.length );
            }
            */
            
            if ( !searchBySelectedOrgunit )
            {
                for ( Patient patient : patients )
                {
                    mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );
                }
            }
           /*
            if ( programIds != null )
            {
                for ( Integer programId : programIds )
                {
                    Program progam = programService.getProgram( programId );
                    identifierTypes.addAll( progam.getPatientIdentifierTypes() );
                }
            }
            */
        }
        
        // map for ovc identifier
        mapPatientSystemIdentifier = new HashMap<Integer, String>();
        Constant patientIdentifierTypeConstant = constantService.getConstantByName( OVC_ID );
        PatientIdentifierType ovcIdentifierType = patientIdentifierTypeService.getPatientIdentifierType( (int) patientIdentifierTypeConstant.getValue() );
        
        String systemOVCId = "";
        
        // map for ovc identifier
        if( patients != null && patients.size() > 0 )
        {
            for( Patient ovc : patients )
            {

                for ( PatientIdentifier identifier : ovc.getIdentifiers() )
                {
                    //identifierType = identifier.getIdentifierType();

                    if ( ovcIdentifierType != null )
                    {
                        PatientIdentifier ovcIdIdentifier = patientIdentifierService.getPatientIdentifier( ovcIdentifierType, ovc );
                        
                        if( ovcIdIdentifier != null )
                        {
                            systemOVCId =  ovcIdIdentifier.getIdentifier() ;
                        }
                        else
                        {
                            systemOVCId =    identifier.getIdentifier(); 
                        }
                        
                    }
                    else
                    {
                        systemOVCId =    identifier.getIdentifier();
                    }
                    
                    //mapPatientSystemIdentifier.put( ovc.getId(), systemOVCId );
                }
                
                mapPatientSystemIdentifier.put( ovc.getId(), systemOVCId );
            }
        }        
        
        
        //System.out.println( "TOTAL : "+ total);
        
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

