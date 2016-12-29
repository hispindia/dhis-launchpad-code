package org.hisp.dhis.asha.facilitator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class AddASHAFacilitatorAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
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
    
   
    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    private String contactNumber;
   
    public void setContactNumber( String contactNumber )
    {
        this.contactNumber = contactNumber;
    }
    
    private String address;
    
    public void setAddress( String address )
    {
        this.address = address;
    }
    
    private String gender;

    public void setGender( String gender )
    {
        this.gender = gender;
    }
    
    private Boolean active;
    
    public void setActive( Boolean active )
    {
        this.active = active;
    }
    
    private List<Integer> selectedASHAList = new ArrayList<Integer>();
    
    public void setSelectedASHAList( List<Integer> selectedASHAList )
    {
        this.selectedASHAList = selectedASHAList;
    }


    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute()
    {
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        Facilitator facilitator = new Facilitator();
         
        facilitator.setName( name );
        facilitator.setContactNumber( contactNumber );
        facilitator.setAddress( address );
        facilitator.setGender( gender );
        
        active = ( active == null) ? false : true;
        
        facilitator.setActive( active );
        
        facilitator.setOrganisationUnit( organisationUnit );
        
        
        Set<Patient> patients = new HashSet<Patient>();
        
        for ( int i = 0; i < this.selectedASHAList.size(); i++ )
        {
            Patient patient = patientService.getPatient( selectedASHAList.get( i ) );
            patients.add( patient );
        }
        /*
        System.out.println( "- Size of patients List  is : " + patients.size());
        for( Patient patient : patients )
        {
            System.out.println( " patient id and name is : " + patient.getId() + " -- "+ patient.getFullName() );
        }
        */
        
        facilitator.setPatients( patients );
        
        facilitatorService.addFacilitator( facilitator );
        
       
        return SUCCESS;
    }
}
