package org.hisp.dhis.asha.facilitator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UpdateASHAFacilitatorAction implements Action
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
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------
    
    private Integer facilitatorId;
    
    public void setFacilitatorId( Integer facilitatorId )
    {
        this.facilitatorId = facilitatorId;
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
    
    private String email;

    public void setEmail( String email )
    {
        this.email = email;
    }

    private String panchayatName;
    
    public void setPanchayatName( String panchayatName )
    {
        this.panchayatName = panchayatName;
    }

    private String birthDate;
    
    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    private String educationLevel;
    
    public void setEducationLevel( String educationLevel )
    {
        this.educationLevel = educationLevel;
    }
    
    private String category;

    public void setCategory( String category )
    {
        this.category = category;
    }

    private String maritalStatus;
    
    public void setMaritalStatus( String maritalStatus )
    {
        this.maritalStatus = maritalStatus;
    }
    
    private String joiningDate;
    
    public void setJoiningDate( String joiningDate )
    {
        this.joiningDate = joiningDate;
    }
       
    private Boolean orientationReceived;
    
    public void setOrientationReceived( Boolean orientationReceived )
    {
        this.orientationReceived = orientationReceived;
    }   
    
   
    private Boolean active;
    
    public void setActive( Boolean active )
    {
        this.active = active;
    }
    
    private String message;
    
    public String getMessage()
    {
        return message;
    }
    
    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
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
        if( facilitatorId != null )
        {
            Facilitator facilitator = facilitatorService.getFacilitator( facilitatorId );
            
            facilitator.setName( name );
            facilitator.setGender( gender );
            facilitator.setAddress( address );
            facilitator.setContactNumber( contactNumber );
            facilitator.setEmail( email );
            facilitator.setPanchayatName( panchayatName );
            facilitator.setBirthDate( format.parseDate( birthDate ) );
            facilitator.setEducationLevel( educationLevel );
            facilitator.setCategory( category );
            facilitator.setMaritalStatus( maritalStatus );
            facilitator.setJoiningDate( format.parseDate( joiningDate ) );
            
            facilitator.setDesignation( "ASHA Facilitator" );
            
            active = ( active == null) ? false : true;
            
            facilitator.setOrientationReceived( orientationReceived );
            
            facilitator.setActive( active );
            
            facilitator.setOrganisationUnit( facilitator.getOrganisationUnit() );
            
            String storedBy = currentUserService.getCurrentUsername();

            Date now = new Date();

            if ( storedBy == null )
            {
                storedBy = "[unknown]";
            }

            facilitator.setStoredBy( storedBy );

            facilitator.setLastupdated( now );
            
            
            if( facilitator != null)
            {
                facilitator.getPatients().clear();
            }
            
            Set<Patient> patients = new HashSet<Patient>();
            
            for ( int i = 0; i < this.selectedASHAList.size(); i++ )
            {
                Patient patient = patientService.getPatient( selectedASHAList.get( i ) );
                patients.add( patient );
            }
            
            facilitator.setPatients( patients );
            
            facilitatorService.updateFacilitator( facilitator );
            
        }
 
        
        return SUCCESS;
    }
}
