package org.hisp.dhis.asha.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetRegisteredASHAListAction extends ActionPagingSupport<Patient>
{
    public static final String ASHA_ACTIVITY = "ASHA Activity";
    
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
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

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
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        status = "NONE";
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        program = programService.getProgramByName( ASHA_ACTIVITY );
        
        if ( ( organisationUnit == null ) || ( !program.getOrganisationUnits().contains( organisationUnit ) ) )
        {
            
            status = i18n.getString( "please_select_sc" );

            return SUCCESS;
        }
        
        // List all ASHA
        if ( listAll )
        {

            total = patientService.countSearchPatients( searchTexts, organisationUnit );
            
            this.paging = createPaging( total );
            
            patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, paging.getStartPos(), paging.getPageSize() ) );
            
        }
        
        // search ASHA by searchTexts
        else if ( searchTexts.size() > 0 )
        {
            organisationUnit = (searchBySelectedOrgunit) ? organisationUnit : null;
            
            total = patientService.countSearchPatients( searchTexts, organisationUnit );
            this.paging = createPaging( total );
            patients = patientService.searchPatients( searchTexts, organisationUnit, paging.getStartPos(), paging.getPageSize() );
            
            if ( !searchBySelectedOrgunit )
            {
                for ( Patient patient : patients )
                {
                    mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );
                }
            }
        }
        
        
        //System.out.println( "TOTAL : "+ total);
        
        /*
        total = patientService.countGetPatientsByOrgUnit( organisationUnit );
        
        this.paging = createPaging( total );
        
        patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, paging.getStartPos(), paging.getPageSize() ) );
        */    
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
