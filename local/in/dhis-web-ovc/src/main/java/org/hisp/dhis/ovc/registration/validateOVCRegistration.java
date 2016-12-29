package org.hisp.dhis.ovc.registration;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class validateOVCRegistration implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    
    private String firstName;
    
    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }
    
    private String middleName;
    
    public void setMiddleName( String middleName )
    {
        this.middleName = middleName;
    }

    private String lastName;
    
    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }
   
    private String gender;
    
    public void setGender( String gender )
    {
        this.gender = gender;
    }


    private String dateOfBirth;
    
    public void setDateOfBirth( String dateOfBirth )
    {
        this.dateOfBirth = dateOfBirth;
    }

    
    private Collection<Patient> patients;
    
    public Collection<Patient> getPatients()
    {
        return patients;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    private String message;

    public String getMessage()
    {
        return message;
    }
    
    private SimpleDateFormat simpleDateFormat;
    
    private Integer ovcId;
    
    public void setOvcId( Integer ovcId )
    {
        this.ovcId = ovcId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        boolean flagDuplicate = false;
        
        Date dob = format.parseDate( String.valueOf( dateOfBirth ) );
        
        patients = patientService.getPatients( firstName, middleName, lastName, format.parseDate( dateOfBirth ), gender );

        if ( patients != null && patients.size() > 0 )
        {
            //message = i18n.getString( "child_duplicate" ) + "\n" + firstName +" " + middleName + " " + lastName + " " + gender + " " + simpleDateFormat.format( dob );
            
            patients = patientService.getPatients( firstName, middleName, lastName, format.parseDate( dateOfBirth ), gender );

            if ( patients != null && patients.size() > 0 )
            {
                //message = i18n.getString( "patient_duplicate" );

                //boolean flagDuplicate = false;
                for ( Patient p : patients )
                {
                    if ( ovcId == null || ( ovcId != null && p.getId().intValue() != ovcId.intValue()) )
                    {
                        flagDuplicate = true;
                        /*
                        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( p );

                        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
                        {
                            patientAttributeValueMap.put( p.getId() + "_" + patientAttributeValue.getPatientAttribute().getId(),patientAttributeValue.getValue() );
                        }
                        */
                    }
                }

                if ( flagDuplicate )
                {
                    message = i18n.getString( "child_duplicate" );
                    
                    message +=  "\n Name :" + firstName +" " + middleName + " " + lastName;
                    
                    message +=  "\n Gender :"  + gender ;
                    
                    message +=  "\n Date of Birth :" + simpleDateFormat.format( dob );
                    
                    return INPUT;
                }
            }
            
            /*
            
            message = i18n.getString( "child_duplicate" );
            
            message +=  "\n Name :" + firstName +" " + middleName + " " + lastName;
            
            message +=  "\n Gender :"  + gender ;
            
            message +=  "\n Date of Birth :" + simpleDateFormat.format( dob );
            
            return INPUT;
            */
        }
        
        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );
        
        //System.out.println( " Inside Validation action " + ovcId  + " Registration Date " + message + " -Flag - " + flagDuplicate );
        
        return SUCCESS;
    }
}
