package org.hisp.dhis.asha.facilitator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowAddASHAFacilitatorFormAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
 
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------

    
    private Integer organisationUnitId;
    
    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private OrganisationUnit organisationUnit;
   
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private Collection<Patient> patients = new ArrayList<Patient>();
    
    public Collection<Patient> getPatients()
    {
        return patients;
    }
    
    private List<Facilitator> facilitators = new ArrayList<Facilitator>();
    
    public List<Facilitator> getFacilitators()
    {
        return facilitators;
    }
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute()
    {
        organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
        facilitators = new ArrayList<Facilitator> ( facilitatorService.getFacilitatorByOrganisationUnit( organisationUnit ) );
        
        if ( facilitators != null && facilitators.size() > 0 )
        {
            for( OrganisationUnit orgUnit : organisationUnit.getChildren() )
            {
                List<Patient> tempPatient = new ArrayList<Patient>( patientService.getPatients( orgUnit, null, null ) );
                
                patients.addAll( tempPatient );
            }
            
            //patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, null, null ) );
            for ( Facilitator facilitator : facilitators )
            {
                patients.removeAll( facilitator.getPatients() );  // Remove ASHA used in other Facilitator
            }
        }
       
        else
        {
            for( OrganisationUnit orgUnit : organisationUnit.getChildren() )
            {
                List<Patient> tempPatient = new ArrayList<Patient>( patientService.getPatients( orgUnit, null, null ) );
                
                patients.addAll( tempPatient );
            }
            
            //patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, null, null ) );
        }
        /*
        System.out.println( "- Size of patients List  is : " + patients.size());
        for( Patient patient : patients )
        {
            System.out.println( " patient id and name is : " + patient.getId() + " -- "+ patient.getFullName() );
        }
        */
        
        return SUCCESS;
    }
}

