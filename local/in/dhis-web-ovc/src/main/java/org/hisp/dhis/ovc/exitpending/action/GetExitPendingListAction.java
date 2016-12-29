package org.hisp.dhis.ovc.exitpending.action;

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
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetExitPendingListAction extends ActionPagingSupport<Patient>
{
    public static final String OVC_MONTHLY_VISIT = "OVC Monthly Visit";
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
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
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
    
    
    private Map<String, String> patientAttributeValueMap = new HashMap<String, String>();
    
    public Map<String, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
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
        
        status = i18n.getString( "none" );
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        program = programService.getProgramByName( OVC_MONTHLY_VISIT );
        
        if ( ( organisationUnit == null ) || ( !program.getOrganisationUnits().contains( organisationUnit ) ) )
        {
            
            status = i18n.getString( "please_select_cbo" );

            return SUCCESS;
        }

        /*      
        List<String> searchTexts = new ArrayList<String>();
        
        searchTexts.add( "attr_301_exitpending" );

        total = patientService.countSearchPatients( searchTexts, organisationUnit );
        
        this.paging = createPaging( total );
        
        patients = patientService.searchPatients( searchTexts, organisationUnit, paging.getStartPos(), paging.getPageSize() );
        
        */
        
        
     // List all patients
        if ( listAll )
        {
            List<String> searchTexts = new ArrayList<String>();
            
            searchTexts.add( "attr_301_exitpending" );

            total = patientService.countSearchPatients( searchTexts, organisationUnit, null );
            
            this.paging = createPaging( total );
            
            patients = patientService.searchPatients( searchTexts, organisationUnit, null, null, paging.getStartPos(), paging.getPageSize() );
        }
        
        else if ( searchTexts.size() > 0 )
        {
            organisationUnit = (searchBySelectedOrgunit) ? organisationUnit : null;
            
            searchTexts.add( "attr_301_exitpending" );
            
            //System.out.println( " Inside Filter" );
            
            //System.out.println( "Search By Selected Orgunit : " + searchBySelectedOrgunit + " -- Organisation Unit  " + organisationUnit );
            
            total = patientService.countSearchPatients( searchTexts, organisationUnit, null );
            this.paging = createPaging( total );
            patients = patientService.searchPatients( searchTexts, organisationUnit, null, null, paging.getStartPos(), paging.getPageSize() );
            
            
            if ( !searchBySelectedOrgunit )
            {
                for ( Patient patient : patients )
                {
                    mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );
                }
            }       
        }
        
        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------
        
        for ( Patient patient : patients )
        {
            Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( patient );

            for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
            {
                if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute().getValueType() ) )
                {
                    patientAttributeValueMap.put( patient.getId() +":" + patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getPatientAttributeOption().getName() );
                }
                else
                {
                    patientAttributeValueMap.put( patient.getId() +":" + patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getValue() );
                }
            }
        }
        
                
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

