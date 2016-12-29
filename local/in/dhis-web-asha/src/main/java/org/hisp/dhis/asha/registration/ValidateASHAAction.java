package org.hisp.dhis.asha.registration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.validation.ValidationCriteria;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ValidateASHAAction implements Action
{
    
    public static final String ASHA_DUPLICATE = "duplicate";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    /*
    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
    */
    private PatientIdentifierTypeService identifierTypeService;
    
    public void setIdentifierTypeService( PatientIdentifierTypeService identifierTypeService )
    {
        this.identifierTypeService = identifierTypeService;
    }

    private ProgramService programService;
    
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }



    // -------------------------------------------------------------------------
    // Input/ Output and    Getter/Setter
    // -------------------------------------------------------------------------
    /*
    private Integer representativeId;
    
    public void setRepresentativeId( Integer representativeId )
    {
        this.representativeId = representativeId;
    }

    private Integer relationshipTypeId;
    
    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }
    */
    
    
    private Integer programId;
    
    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }


    private String fullName;
    
    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }
    
    private String birthDate;
    
    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }
    
    private Integer age;
    
    public void setAge( Integer age )
    {
        this.age = age;
    }







    
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    
    private boolean checkedDuplicate;
    
    public void setCheckedDuplicate( boolean checkedDuplicate )
    {
        this.checkedDuplicate = checkedDuplicate;
    }
    
    private String gender;
    
    public void setGender( String gender )
    {
        this.gender = gender;
    }
    
    /*
    private boolean underAge;
    
    public void setUnderAge( boolean underAge )
    {
        this.underAge = underAge;
    }
    */

    private char ageType;
    
    public void setAgeType( char ageType )
    {
        this.ageType = ageType;
    }

    
    private String message;
    
    public String getMessage()
    {
        return message;
    }

    private I18n i18n;
    
    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private Map<String, String> patientAttributeValueMap = new HashMap<String, String>();
    
    public Map<String, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    private PatientIdentifier patientIdentifier;
    
    public PatientIdentifier getPatientIdentifier()
    {
        return patientIdentifier;
    }

    private Collection<Patient> patients;
    
    public Collection<Patient> getPatients()
    {
        return patients;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        //System.out.println( " Inside Validate Action verified : "    );
        
        fullName = fullName.trim();

        int startIndex = fullName.indexOf( ' ' );
        int endIndex = fullName.lastIndexOf( ' ' );

        String firstName = fullName.toString();
        String middleName = "";
        String lastName = "";

        if ( fullName.indexOf( ' ' ) != -1 )
        {
            firstName = fullName.substring( 0, startIndex );
            if ( startIndex == endIndex )
            {
                middleName = "";
                lastName = fullName.substring( startIndex + 1, fullName.length() );
            }
            else
            {
                middleName = fullName.substring( startIndex + 1, endIndex );
                lastName = fullName.substring( endIndex + 1, fullName.length() );
            }
        }

        
        
        
        if ( !checkedDuplicate )
        {
            patients = patientService.getPatients( firstName, middleName, lastName, format.parseDate( birthDate ),
                gender );

            if ( patients != null && patients.size() > 0 )
            {
                message = i18n.getString( "patient_duplicate" );

                boolean flagDuplicate = false;
                for ( Patient p : patients )
                {
                    if ( id == null || (id != null && p.getId().intValue() != id.intValue()) )
                    {
                        flagDuplicate = true;
                        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
                            .getPatientAttributeValues( p );

                        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
                        {
                            patientAttributeValueMap
                                .put( p.getId() + "_" + patientAttributeValue.getPatientAttribute().getId(),
                                    patientAttributeValue.getValue() );
                        }
                    }
                }

                if ( flagDuplicate )
                {
                    return ASHA_DUPLICATE;
                }
            }
        }

        // ---------------------------------------------------------------------
        // Check Under age information
        // ---------------------------------------------------------------------
        
        
        /*
        if ( underAge )
        {
            if ( representativeId == null )
            {
                message = i18n.getString( "please_choose_representative_for_this_under_age_patient" );
                return INPUT;
            }
            if ( relationshipTypeId == null )
            {
                message = i18n.getString( "please_choose_relationshipType_for_this_under_age_patient" );
                return INPUT;
            }
        }
       */
        
        Patient p = new Patient();
        p.setGender( gender );

        if ( birthDate != null )
        {
            birthDate = birthDate.trim();
            p.setBirthDate( format.parseDate( birthDate ) );

        }
        else
        {
            p.setBirthDateFromAge( age.intValue(), ageType );
        }
        
        //HttpServletRequest request = ServletActionContext.getRequest();

        Collection<PatientIdentifierType> identifiers = identifierTypeService.getAllPatientIdentifierTypes();

        if ( identifiers != null && identifiers.size() > 0 )
        {
            //String value = null;
            String idDuplicate = "";
            
            /*
            for ( PatientIdentifierType idType : identifiers )
            {
                if ( !underAge || ( underAge && !idType.isRelated()) )
                {
                    value = request.getParameter( ShowAddASHAProfileFormAction.PREFIX_IDENTIFIER + idType.getId() );

                    if ( StringUtils.isNotBlank( value ) )
                    {
                        PatientIdentifier identifier = patientIdentifierService.get( idType, value );

                        if ( identifier != null
                            && (id == null || identifier.getPatient().getId().intValue() != id.intValue()) )
                        {
                            idDuplicate += idType.getName() + ", ";
                        }
                    }
                }
            }
            */ 
            if ( StringUtils.isNotBlank( idDuplicate ) )
            {
                idDuplicate = StringUtils.substringBeforeLast( idDuplicate, "," );
                message = i18n.getString( "identifier_duplicate" ) + ": " + idDuplicate;
                return INPUT;
            }
        }

        // ---------------------------------------------------------------------
        // Check Enrollment for adding patient single event with registration
        // ---------------------------------------------------------------------

        if ( programId != null )
        {
            Program program = programService.getProgram( programId );
            ValidationCriteria criteria = program.isValid( p );

            if ( criteria != null )
            {
                message = i18n.getString( "patient_could_not_be_enrolled_due_to_following_enrollment_criteria" ) + ": "
                    + i18n.getString( criteria.getProperty() );

                switch ( criteria.getOperator() )
                {
                case ValidationCriteria.OPERATOR_EQUAL_TO:
                    message += " = ";
                    break;
                case ValidationCriteria.OPERATOR_GREATER_THAN:
                    message += " > ";
                    break;
                default:
                    message += " < ";
                    break;
                }

                if ( criteria.getProperty() == "birthDate" )
                {
                    message += " " + format.formatValue( criteria.getValue() );
                }
                else
                {
                    message += " " + criteria.getValue().toString();
                }

                return INPUT;
            }
        }

        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }


}