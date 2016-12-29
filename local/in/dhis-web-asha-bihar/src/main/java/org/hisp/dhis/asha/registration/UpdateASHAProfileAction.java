package org.hisp.dhis.asha.registration;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class UpdateASHAProfileAction implements Action
{
   
    public static final String ASHA_PROFILE_ATTRIBUTE = "ASHA Profile";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    /*
    private PatientAttributeService patientAttributeService;
    
    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    */
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    /*
    private OrganisationUnitSelectionManager selectionManager;
    
    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
   
    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
    */

    private PatientAttributeOptionService patientAttributeOptionService;
    
    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    /*
    private UserService userService;
    
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
    */
    
    // -----------------------------------------------------------------------------
    // Input / Output Getter/Setter
    // -----------------------------------------------------------------------------
    
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
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
    
    /*
    private boolean isDead;
    
    public void setIsDead( boolean isDead )
    {
        this.isDead = isDead;
    }
    
    private String deathDate;

    public void setDeathDate( String deathDate )
    {
        this.deathDate = deathDate;
    }
    */

    
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

    /*
     * 
    private boolean underAge;
    
    public void setUnderAge( boolean underAge )
    {
        this.underAge = underAge;
    }

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
   

    
    
    private Integer healthWorker;
    
    public void setHealthWorkerId( Integer healthWorkerId )
    {
        this.healthWorker = healthWorkerId;
    }
    
     */
    
    private Character dobType;
    
    public void setDobType( Character dobType )
    {
        this.dobType = dobType;
    }
    
    private String registrationDate;
    
    public void setRegistrationDate( String registrationDate )
    {
        this.registrationDate = registrationDate;
    }

    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }
    
    private PatientAttributeGroup attributeGroup;
    
    public PatientAttributeGroup getAttributeGroup()
    {
        return attributeGroup;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        //OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        patient = patientService.getPatient( id );
        
        OrganisationUnit organisationUnit = patient.getOrganisationUnit();

        verified = (verified == null) ? false : verified;

        // ---------------------------------------------------------------------
        // Set FirstName, MiddleName, LastName by FullName
        // ---------------------------------------------------------------------

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
        
       
        
        patient.setPhoneNumber( phoneNumber );
        
        /*
        patient.setIsDead( isDead );
        if ( healthWorker != null )
        {
            patient.setHealthWorker( userService.getUser( healthWorker ) );
        }

        if ( deathDate != null )
        {
            deathDate = deathDate.trim();
            patient.setDeathDate( format.parseDate( deathDate ) );
        }
        
        patient.setUnderAge( underAge );
        */
        
        patient.setOrganisationUnit( organisationUnit );

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

        if ( registrationDate != null )
        {
            patient.setRegistrationDate( format.parseDate( registrationDate ) );
        }
        
        patientService.updatePatient( patient );
        
        // -------------------------------------------------------------------------------------
        // Save PatientIdentifier
        // -------------------------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;
        
        /*
        Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        PatientIdentifier identifier = null;

        if ( identifierTypes != null && identifierTypes.size() > 0 )
        {
            for ( PatientIdentifierType identifierType : identifierTypes )
            {
                value = request.getParameter( AddASHAAction.PREFIX_IDENTIFIER + identifierType.getId() );

                identifier = patientIdentifierService.getPatientIdentifier( identifierType, patient );

                if ( StringUtils.isNotBlank( value ) )
                {
                    value = value.trim();

                    if ( identifier == null )
                    {
                        identifier = new PatientIdentifier();
                        identifier.setIdentifierType( identifierType );
                        identifier.setPatient( patient );
                        identifier.setIdentifier( value );
                        patient.getIdentifiers().add( identifier );
                    }
                    else
                    {
                        identifier.setIdentifier( value );
                        patient.getIdentifiers().add( identifier );
                    }
                }
                else if ( identifier != null )
                {
                    patient.getIdentifiers().remove( identifier );
                }
            }
        }
      */
        
        
        // --------------------------------------------------------------------------------------------------------
        // Save Patient Attributes
        // -----------------------------------------------------------------------------------------------------
        
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_PROFILE_ATTRIBUTE );
        
        //Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();
        
        Collection<PatientAttribute> attributes = attributeGroup.getAttributes();

        //List<PatientAttributeValue> valuesForSave = new ArrayList<PatientAttributeValue>();
       // List<PatientAttributeValue> valuesForUpdate = new ArrayList<PatientAttributeValue>();
        //Collection<PatientAttributeValue> valuesForDelete = null;

        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            patient.getAttributes().clear();
            
            //valuesForDelete = patientAttributeValueService.getPatientAttributeValues( patient );

            for ( PatientAttribute attribute : attributes )
            {
                value = request.getParameter( AddASHAAction.PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, attribute );

                    if ( !patient.getAttributes().contains( attribute ) )
                    {
                        patient.getAttributes().add( attribute );
                    }

                    if ( attributeValue == null )
                    {
                        attributeValue = new PatientAttributeValue();
                        attributeValue.setPatient( patient );
                        attributeValue.setPatientAttribute( attribute );
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                       // valuesForSave.add( attributeValue );
                        patientAttributeValueService.savePatientAttributeValue( attributeValue );
                    }
                    else
                    {
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                        
                        patientAttributeValueService.updatePatientAttributeValue( attributeValue );
                        //valuesForUpdate.add( attributeValue );
                        //valuesForDelete.remove( attributeValue );
                    }
                }
            }
        }

        //patientService.updatePatient( patient, null, null, valuesForSave, valuesForUpdate,valuesForDelete );

        return SUCCESS;
    }
}

