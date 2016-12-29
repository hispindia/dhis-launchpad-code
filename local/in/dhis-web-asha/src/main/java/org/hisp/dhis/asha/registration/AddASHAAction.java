package org.hisp.dhis.asha.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.asha.idgen.IdentifierGenerator;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class AddASHAAction implements Action
{
    public static final String ASHA_ACTIVITY_PROGRAM = "ASHA Activity Program";//1.0
    
    public static final String PREFIX_ATTRIBUTE = "attr";
    
    public static final String ASHA_PROFILE_ATTRIBUTE = "ASHA Profile";
    
    public static final String PREFIX_IDENTIFIER = "iden";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
    
    private OrganisationUnitSelectionManager selectionManager;
    
    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
    /*
    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }
    */
   
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private PatientAttributeOptionService patientAttributeOptionService;
    
    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private ProgramInstanceService programInstanceService;
    
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------

    private String fullName;
    
    public void setFullName( String fullName )
    {
        this.fullName = fullName;
    }
    
    private String gender;

    public void setGender( String gender )
    {
        this.gender = gender;
    }
    
    private String phoneNumber;
    
    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
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

    private Boolean verified;
    
    public void setVerified( Boolean verified )
    {
        this.verified = verified;
    }
   
    private String registrationDate;
    
    public void setRegistrationDate( String registrationDate )
    {
        this.registrationDate = registrationDate;
    }

   

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
    
    private boolean underAge;
    
    public void setUnderAge( boolean underAge )
    {
        this.underAge = underAge;
    }    
    
    private Integer healthWorker;
    
    public void setHealthWorker( Integer healthWorker )
    {
        this.healthWorker = healthWorker;
    }    
    

    private boolean isDead;

    private String deathDate;
    */
    
    private String message;
    
    public String getMessage()
    {
        return message;
    }
    
    
    private PatientAttributeGroup attributeGroup;
    
    public PatientAttributeGroup getAttributeGroup()
    {
        return attributeGroup;
    }
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        //System.out.println( " Inside add Action verified : "  + verified );
        
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        Patient patient = new Patient();

        verified = (verified == null) ? false : verified;

        // ---------------------------------------------------------------------
        // Set FirstName, MiddleName, LastName by FullName
        // ---------------------------------------------------------------------

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

        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );

        // ---------------------------------------------------------------------
        // Set Other information for patient
        // ---------------------------------------------------------------------

        patient.setGender( gender );
        patient.setIsDead( false );
        patient.setPhoneNumber( phoneNumber );
        
        patient.setOrganisationUnit( organisationUnit );
       
        /*
        patient.setUnderAge( underAge );
        
        patient.setIsDead( isDead );
       
        
        if ( deathDate != null )
        {
            deathDate = deathDate.trim();
            patient.setDeathDate( format.parseDate( deathDate ) );
        }
        
        if ( healthWorker != null )
        {
            patient.setHealthWorker( userService.getUser( healthWorker ) );
        }
       */
        
        Character dobType = (verified) ? 'V' : 'D';

        if ( !verified && age != null )
        {
            dobType = 'A';
        }

        if ( dobType == Patient.DOB_TYPE_VERIFIED || dobType == Patient.DOB_TYPE_DECLARED )
        {
            birthDate = birthDate.trim();
            patient.setBirthDate( format.parseDate( birthDate ) );
        }
        else
        {
            patient.setBirthDateFromAge( age.intValue(), Patient.AGE_TYPE_YEAR );
        }

        patient.setDobType( dobType );

        patient.setRegistrationDate( format.parseDate( registrationDate ) );

        // -----------------------------------------------------------------------------
        // Prepare Patient Identifiers
        // -----------------------------------------------------------------------------
        
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        String value = null;
        
        /*
        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        PatientIdentifier pIdentifier = null;

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {

                value = request.getParameter( PREFIX_IDENTIFIER + identifierType.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    pIdentifier = new PatientIdentifier();
                    pIdentifier.setIdentifierType( identifierType );
                    pIdentifier.setPatient( patient );
                    pIdentifier.setIdentifier( value.trim() );
                    patient.getIdentifiers().add( pIdentifier );
                }
            }
        }
      */
        
        // --------------------------------------------------------------------------------
        // Generate system id with this format :
        // (BirthDate)(Gender)(XXXXXX)(checkdigit)
        // PatientIdentifierType will be null
        // --------------------------------------------------------------------------------

        String identifier = IdentifierGenerator.getNewIdentifier( patient.getBirthDate(), patient.getGender() );

        PatientIdentifier systemGenerateIdentifier = patientIdentifierService.get( null, identifier );
        while ( systemGenerateIdentifier != null )
        {
            identifier = IdentifierGenerator.getNewIdentifier( patient.getBirthDate(), patient.getGender() );
            systemGenerateIdentifier = patientIdentifierService.get( null, identifier );
        }

        systemGenerateIdentifier = new PatientIdentifier();
        systemGenerateIdentifier.setIdentifier( identifier );
        systemGenerateIdentifier.setPatient( patient );

        patient.getIdentifiers().add( systemGenerateIdentifier );


        // -----------------------------------------------------------------------------
        // Prepare Patient Attributes
        // -----------------------------------------------------------------------------
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_PROFILE_ATTRIBUTE );
        
        Collection<PatientAttribute> attributes = attributeGroup.getAttributes();
        
        //Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();

        List<PatientAttributeValue> patientAttributeValues = new ArrayList<PatientAttributeValue>();
        
       // System.out.println( " Attribute Size is "   + attributes.size() );
        
        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            for ( PatientAttribute attribute : attributes )
            {
                //System.out.println( " Attribute Id is "   + attribute.getId()   + "  -- Attribute Name is : " + attribute.getName() );
                
                value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    if ( !patient.getAttributes().contains( attribute ) )
                    {
                        patient.getAttributes().add( attribute );
                    }

                    attributeValue = new PatientAttributeValue();
                    attributeValue.setPatient( patient );
                    attributeValue.setPatientAttribute( attribute );

                    if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                    {
                        PatientAttributeOption option = patientAttributeOptionService
                            .get( NumberUtils.toInt( value, 0 ) );
                        if ( option != null )
                        {
                            attributeValue.setPatientAttributeOption( option );
                            attributeValue.setValue( option.getName() );
                        }
                        else
                        {
                            // Someone deleted this option ...
                        }
                    }
                    else
                    {
                        attributeValue.setValue( value.trim() );
                    }
                    patientAttributeValues.add( attributeValue );
                }
            }
        }

        // -------------------------------------------------------------------------
        // Save patient
        // -------------------------------------------------------------------------
        
        Integer ashaId = patientService.createPatient( patient, null, null, patientAttributeValues );

        message = ashaId + "_" + systemGenerateIdentifier.getIdentifier();
       
        // -----------------------------------------------------------------------------
        // Add a new program-instance Enroll ASHA In ASHA Activity Program
        // -----------------------------------------------------------------------------
            
        Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM );
        
        program = programService.getProgram( (int) programConstant.getValue() );
        
        Patient createdPatient = patientService.getPatient( ashaId );
        
        int programType = program.getType();
        ProgramInstance programInstance = null;
        
        if ( programType == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
        {
            
            programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( createdPatient.getRegistrationDate() );
            programInstance.setDateOfIncident( createdPatient.getRegistrationDate() );
            programInstance.setProgram( program );
            
            //programInstance.setCompleted( false );

            programInstance.setPatient( createdPatient );
            createdPatient.getPrograms().add( program );
            patientService.updatePatient( createdPatient );

            programInstanceService.addProgramInstance( programInstance );
            
        }
        
        return SUCCESS;
    }

}

