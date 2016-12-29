package org.hisp.dhis.asha.facilitator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowUpdateASHAFacilitatorFormAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    // -------------------------------------------------------------------------
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    private OrganisationUnit organisationUnit;
   
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private Facilitator facilitator;
    
    public Facilitator getFacilitator()
    {
        return facilitator;
    }

    private List<Patient> facilitatorPatients = new ArrayList<Patient>();
    
    public List<Patient> getFacilitatorPatients()
    {
        return facilitatorPatients;
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
        if ( id != null )
        {
            facilitator = facilitatorService.getFacilitator( id );
            
            facilitatorPatients = new ArrayList<Patient>( facilitator.getPatients() );
            
            facilitators = new ArrayList<Facilitator> ( facilitatorService.getFacilitatorByOrganisationUnit( facilitator.getOrganisationUnit() ) );
            
            if ( facilitators != null && facilitators.size() > 0 )
            {
                for( OrganisationUnit orgUnit : facilitator.getOrganisationUnit().getChildren() )
                {
                    List<Patient> tempPatient = new ArrayList<Patient>( patientService.getPatients( orgUnit, null, null ) );
                    
                    patients.addAll( tempPatient );
                }
                
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
                
            }
            
        }
        
        return SUCCESS;
    }
}


