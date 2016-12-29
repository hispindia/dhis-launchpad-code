package org.hisp.dhis.ovc.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class UpdateOVCRegistrationAction implements Action
{
    
    public static final String PREFIX_ATTRIBUTE = "attr";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    /*
    @Autowired
    private MessageService messageService;
    */
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    /*
    private OrganisationUnitSelectionManager selectionManager;
    
    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    */
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
     
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    private PatientAttributeOptionService patientAttributeOptionService;
    
    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }

    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    /*
    private OVCService ovcService;

    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }
    */
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
 
    // -------------------------------------------------------------------------
    // Input/output getter/setter
    // -------------------------------------------------------------------------

    private int ovcId;
    
    public void setOvcId( int ovcId )
    {
        this.ovcId = ovcId;
    }
    

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
    
    private String approveInsideDashBoard;
    
    public String getApproveInsideDashBoard()
    {
        return approveInsideDashBoard;
    }

    public void setApproveInsideDashBoard( String approveInsideDashBoard )
    {
        this.approveInsideDashBoard = approveInsideDashBoard;
    }
    
    private Integer selectOrgUnitInsideDashBoard;
    
    public Integer getSelectOrgUnitInsideDashBoard()
    {
        return selectOrgUnitInsideDashBoard;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    
    public String execute()
    {
        //System.out.println( " Inside update  action " + ovcId  + " Registration Date " + registrationDate );
        
        //OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        patient = patientService.getPatient( ovcId );
        
        OrganisationUnit organisationUnit = patient.getOrganisationUnit();
        
        // ---------------------------------------------------------------------
        // Set FirstName, MiddleName, LastName 
        // ---------------------------------------------------------------------

        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );
        
        // ---------------------------------------------------------------------
        // Set Other information for OVC
        // ---------------------------------------------------------------------
        
        patient.setGender( gender );
        patient.setIsDead( false );
        patient.setOrganisationUnit( organisationUnit );
        
        patient.setBirthDate( format.parseDate( dateOfBirth ) );
        
        if ( registrationDate != null )
        {
            patient.setRegistrationDate( format.parseDate( registrationDate ) );
        }
        
        // --------------------------------------------------------------------------------------------------------
        // Save Patient Attributes
        // -----------------------------------------------------------------------------------------------------
        
        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        
        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();

        List<PatientAttributeValue> valuesForSave = new ArrayList<PatientAttributeValue>();
        List<PatientAttributeValue> valuesForUpdate = new ArrayList<PatientAttributeValue>();
        Collection<PatientAttributeValue> valuesForDelete = null;

        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            patient.getAttributes().clear();
            valuesForDelete = patientAttributeValueService.getPatientAttributeValues( patient );

            for ( PatientAttribute attribute : attributes )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );

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
                        valuesForSave.add( attributeValue );
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
                        valuesForUpdate.add( attributeValue );
                        valuesForDelete.remove( attributeValue );
                    }
                }
            }
        }

        patientService.updatePatient( patient, null, null, valuesForSave, valuesForUpdate, valuesForDelete );
        
        // School Related information
        String schoolName = request.getParameter( PREFIX_ATTRIBUTE + 51 );
        
        //System.out.println( " School Name -- " + schoolName + "-- ovc Id is " + patient.getId() );
        
        School school = schoolService.getSchoolByOrganisationUnitAndName( patient.getOrganisationUnit(), schoolName );
        
        List<School> schools = new ArrayList<School>( schoolService.getSchoolByOVC( patient ));
        
        //List<School> schools = new ArrayList<School>( ovcService.getSchoolByOVC( patient.getId() ));
        
        //System.out.println( " School Name -- " + school.getName() + " -- Size of School "+ schools.size() );
        
        if( school != null && schools != null && schools.size() == 1 && schools.contains( school ) )
        {
            
        }
        
        else
        {
            for( School s : schools )
            {
                s.getPatients().remove( patient );
                schoolService.updateSchool( s );
            }
            school.getPatients().add( patient );
            schoolService.updateSchool( school );

        }
        
        /*
        if( school != null )
        {
            Set<Patient> patients = new HashSet<Patient>();
            patients.add( patient );
            
            school.setPatients( patients );
            
            schoolService.updateSchool( school );
        }
        */
        
        
        
        
        
        /*
        Set<User> users = new HashSet<User>();

        for ( OrganisationUnit unit :  patient.getOrganisationUnit().getParent().getChildren())
        {
            users.addAll( unit.getUsers() );
        }
        
        String metaData = MessageService.META_USER_AGENT +
        ServletActionContext.getRequest().getHeader( ContextUtils.HEADER_USER_AGENT );
        
        String subject = "Alert message";
        String text = "Waiting for Approve";
        messageService.sendMessage( subject, text, metaData, users );
        */
        
        
        if( ( approveInsideDashBoard != null ) &&  ( approveInsideDashBoard.equalsIgnoreCase( "yes" ) ) )
        {
            selectOrgUnitInsideDashBoard = patient.getOrganisationUnit().getId();
            
            System.out.println( " Approve Inside DashBoard " + approveInsideDashBoard + " -- Select OrgUnit Inside DashBoard : "  + selectOrgUnitInsideDashBoard );
            return INPUT;
        }
        
        else
        {
            return SUCCESS;
        }
        
        
        
    }

}
