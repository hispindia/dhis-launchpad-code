package org.hisp.dhis.ovc.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.ovc.idgen.IdentifierGenerator;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class SaveRegistredDataAction implements Action
{
    public static final String PREFIX_ATTRIBUTE = "attr";
    public static final String OVC_EXIT_MESSAGE_ALERT = "OVC Exit Message Alert";//3.0
    public static final String OVC_ID = "OVC_ID";//929.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private MessageService messageService;
   
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    /*
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    
    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    */
    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
    
    private PatientAttributeOptionService patientAttributeOptionService;
    
    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }
    
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
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private OVCService ovcService;

    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }

    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    /*
    private ProgramInstanceService programInstanceService;
    
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    private ProgramStageInstanceService programStageInstanceService;
    
    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
    */
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
 
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    /*
    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    
    private Integer programStageId;
    
    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }
    */
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
   
   private ProgramInstance programInstance;

   public ProgramInstance getProgramInstance()
   {
       return programInstance;
   }
   
   private Collection<Patient> patients;
   
   private ProgramStageInstance activeProgramStageInstance;

   public ProgramStageInstance getActiveProgramStageInstance()
   {
       return activeProgramStageInstance;
   }
   
   private String message;
   
   public String getMessage()
   {
       return message;
   }
   
   private I18n i18n;
   
   private Patient ovc;
   
   public Patient getOvc()
   {
       return ovc;
   }

   private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();
   
   public Map<Integer, String> getPatientAttributeValueMap()
   {
       return patientAttributeValueMap;
   }
   
   private String attr43;
   
   public void setAttr43( String attr43 )
   {
       this.attr43 = attr43;
   }
   /*
   private String organisationUnit;
   
   public void setOrganisationUnit( String organisationUnit )
   {
       this.organisationUnit = organisationUnit;
   }
   */
   
   String systemOVCId;
   
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
    {
        //System.out.println( " Inside Save Add  action "   + " Registration Date " + registrationDate );
        
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        /*
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        if( organisationUnit == null )
        {
            organisationUnit = selectionManager.getSelectedOrganisationUnit();
        }
        */
        
        systemOVCId = new String();
        
        Patient patient = new Patient();
        
        // ---------------------------------------------------------------------
        // Set personal information for patient
        // ---------------------------------------------------------------------
        
        patient.setFirstName( firstName );
        patient.setMiddleName( middleName );
        patient.setLastName( lastName );

        patient.setGender( gender );
        patient.setIsDead( false );
        patient.setOrganisationUnit( organisationUnit );
        
        patient.setBirthDate( format.parseDate( dateOfBirth ) );
        
        patient.setRegistrationDate( format.parseDate( registrationDate ) );
        
        
        patients = patientService.getPatients( firstName, middleName, lastName, format.parseDate( dateOfBirth ), gender );

        if ( patients != null && patients.size() > 0 )
        {
            //message = i18n.getString( "patient_duplicate" );
            message = i18n.getString( "patient_duplicate" ) + "\n" + firstName +" " + middleName + " " + lastName + " " + gender + " " + format.parseDate( dateOfBirth );
        }
        
        else
        {
            //System.out.println( " Inside Save Add  action "   + " Registration Date " + registrationDate );
            
            HttpServletRequest request = ServletActionContext.getRequest();
            
            String value = null;
            
            // -----------------------------------------------------------------------------
            // Prepare Patient Identifiers
            // -----------------------------------------------------------------------------
            
            /*
            HttpServletRequest request = ServletActionContext.getRequest();

            Collection<PatientIdentifierType> identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

            String value = null;

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
            
            Constant patientIdentifierTypeConstant = constantService.getConstantByName( OVC_ID );
            
            PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( (int) patientIdentifierTypeConstant.getValue() );
            
            if ( organisationUnit.getCode() != null && identifierType != null )
            {
                String generateOVCId = "";
                
                String tempOVCId = ovcService.getMaxOVCId( organisationUnit.getId(), identifierType.getId() );
                
                //System.out.println(  " temp OVC Id -- " + tempOVCId );
                
                if( tempOVCId != null && !tempOVCId.equalsIgnoreCase( "" ) )
                {
                    String[] compositeOVCId = tempOVCId.split( "-" );
                    
                    String preFix = compositeOVCId[1];
                    
                    String id = compositeOVCId[2];
                    
                    Integer presentCount = Integer.parseInt( id  );
                    
                    int finalCount = presentCount + 1;
                    
                    
                    if( finalCount <= 9 )
                    {
                        generateOVCId = organisationUnit.getCode() + "-" + preFix + "-" + "000" + finalCount;
                    }
                    
                    else if( finalCount <= 99 )
                    {
                        generateOVCId = organisationUnit.getCode() + "-" + preFix + "-" + "00" + finalCount;
                    }
                    
                    else if( finalCount <= 999 )
                    {
                        generateOVCId = organisationUnit.getCode() + "-" + preFix + "-" + + finalCount;
                    }
                    else if( finalCount <= 9999 )
                    {
                        generateOVCId = organisationUnit.getCode() + "-" + preFix + "-" +  finalCount;
                    }
                    
                }
                
                else
                {
                    generateOVCId = organisationUnit.getCode() + "-O" + "-" + "0001";
                }
                
                
                
                //System.out.println( " Final OVC ID is -- " + generateOVCId );
                
                PatientIdentifier pIdentifier = null;

                if ( identifierType != null )
                {
                    pIdentifier = new PatientIdentifier();
                    pIdentifier.setIdentifierType( identifierType );
                    pIdentifier.setPatient( patient );
                    pIdentifier.setIdentifier( generateOVCId );
                    patient.getIdentifiers().add( pIdentifier );
                }
                
            }

            // -----------------------------------------------------------------------------
            // Prepare Patient Attributes
            // -----------------------------------------------------------------------------

            Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();

            List<PatientAttributeValue> patientAttributeValues = new ArrayList<PatientAttributeValue>();

            PatientAttributeValue attributeValue = null;

            if ( attributes != null && attributes.size() > 0 )
            {
                for ( PatientAttribute attribute : attributes )
                {
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

            Integer ovcId = patientService.createPatient( patient, null, null, patientAttributeValues );
            
            ovc = patientService.getPatient( ovcId );
            
            // School Related information
            String schoolName = request.getParameter( PREFIX_ATTRIBUTE + 51 );
            
            //System.out.println( " School Name -- " + schoolName + "-- ovc Id is " + ovc.getId() );
            
            School school = schoolService.getSchoolByOrganisationUnitAndName( ovc.getOrganisationUnit(), schoolName );
           
            
            if( school != null )
            {
                school.getPatients().add( ovc );
                schoolService.updateSchool( school );
                
                /*
                Set<Patient> patients = new HashSet<Patient>( school.getPatients() );
                patients.add( ovc );
                
                school.setPatients( patients );
                
                schoolService.updateSchool( school );
                */
            }
            
            
            //message = patientId + "_" + systemGenerateIdentifier.getIdentifier();
            
            
            // -----------------------------------------------------------------------------
            // Enrolling Patient to Program
            // -----------------------------------------------------------------------------
            
            
            /*
            
            Program program = programService.getProgram( programId );
            
            ProgramStage programStage = programStageService.getProgramStage( programStageId );
            
            Patient createdPatient = patientService.getPatient( patientId );
            
            int type = program.getType();
            ProgramInstance programInstance = null;
            if ( type == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
            {
                // -----------------------------------------------------------------------------
                // Add a new program-instance
                // -----------------------------------------------------------------------------
                programInstance = new ProgramInstance();
                programInstance.setEnrollmentDate( createdPatient.getRegistrationDate() );
                programInstance.setDateOfIncident( createdPatient.getRegistrationDate() );
                programInstance.setProgram( program );
                programInstance.setCompleted( false );

                programInstance.setPatient( createdPatient );
                createdPatient.getPrograms().add( program );
                patientService.updatePatient( createdPatient );

                programInstanceService.addProgramInstance( programInstance );

                // -----------------------------------------------------------------------------
                // Add a new program-stage-instance
                // -----------------------------------------------------------------------------
                ProgramStageInstance programStageInstance = new ProgramStageInstance();
                programStageInstance.setProgramInstance( programInstance );
                programStageInstance.setProgramStage( programStage );
                programStageInstance.setDueDate( createdPatient.getRegistrationDate() );
                programStageInstance.setExecutionDate( createdPatient.getRegistrationDate() );
                programStageInstance.setOrganisationUnit( createdPatient.getOrganisationUnit() );

                int psInstanceId = programStageInstanceService.addProgramStageInstance( programStageInstance );
                
            }
            */
            
            // enroll in multiple stages
            
            
            /*
            Patient patientforEnroll = patientService.getPatient( patientId );

            Program program = programService.getProgram( programId );

            Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( patientforEnroll, program, false );
            
            if ( programInstances.iterator().hasNext() )
            {
                programInstance = programInstances.iterator().next();
            }

            if ( programInstance == null )
            {
                programInstance = new ProgramInstance();
                programInstance.setEnrollmentDate( format.parseDate( registrationDate ) );
                programInstance.setDateOfIncident( format.parseDate( registrationDate ) );
                programInstance.setProgram( program );
                programInstance.setPatient( patient );
                programInstance.setCompleted( false );

                programInstanceService.addProgramInstance( programInstance );

                patient.getPrograms().add( program );
                patientService.updatePatient( patient );

                Date dateCreatedEvent = format.parseDate( registrationDate );
                if ( program.getGeneratedByEnrollmentDate() )
                {
                    dateCreatedEvent = format.parseDate( registrationDate );
                }
                
                boolean isFirstStage = false;
                Date currentDate = new Date();
                for ( ProgramStage programStage : program.getProgramStages() )
                {
                    if ( programStage.getAutoGenerateEvent() )
                    {
                        Date dueDate = DateUtils.getDateAfterAddition( dateCreatedEvent, programStage.getMinDaysFromStart() );

                        if ( ! ( program.getIgnoreOverdueEvents() && dueDate.before( currentDate ) ))
                        {
                            ProgramStageInstance programStageInstance = new ProgramStageInstance();
                            programStageInstance.setProgramInstance( programInstance );
                            programStageInstance.setProgramStage( programStage );
                            programStageInstance.setDueDate( dueDate );
                            
                            if ( program.isSingleEvent() )
                            {
                                programStageInstance.setOrganisationUnit( organisationUnit );
                                programStageInstance.setExecutionDate( dueDate );
                            }
                            programStageInstanceService.addProgramStageInstance( programStageInstance );

                            if ( !isFirstStage )
                            {
                                activeProgramStageInstance = programStageInstance;
                                isFirstStage = true;
                            }
                        }
                    }
                }
            }
            else
            {
                programInstance.setEnrollmentDate( format.parseDate( registrationDate ) );
                programInstance.setDateOfIncident( format.parseDate( registrationDate ) );

                programInstanceService.updateProgramInstance( programInstance );

                for ( ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances() )
                {
                    if ( !programStageInstance.isCompleted()
                        || programStageInstance.getStatus() != ProgramStageInstance.SKIPPED_STATUS )
                    {
                        Date dueDate = DateUtils.getDateAfterAddition( format.parseDate( registrationDate ),
                            programStageInstance.getProgramStage().getMinDaysFromStart() );

                        programStageInstance.setDueDate( dueDate );

                        programStageInstanceService.updateProgramStageInstance( programStageInstance );
                    }
                }
            }
            
            */
            
            String fullName = firstName + "  " + middleName + " " + lastName;
           
            //message = patientId + "_" + systemGenerateIdentifier.getIdentifier() +"_"+ fullName +" IS SUCCESSFULLY REGISTERED";
            
            message =  fullName +" IS SUCCESSFULLY REGISTERED and  System Generated Id : " + systemGenerateIdentifier.getIdentifier() ;
            
            //systemOVCId = systemGenerateIdentifier.getIdentifier();
            
            if ( organisationUnit.getCode() != null && identifierType != null )
            {
                PatientIdentifier ovcIdIdentifier = patientIdentifierService.getPatientIdentifier( identifierType, ovc );
                
                message =  fullName +" IS SUCCESSFULLY REGISTERED and  OVC Id : " + ovcIdIdentifier.getIdentifier() ;
                systemOVCId = ovcIdIdentifier.getIdentifier();
            }
            else
            {
                message =  fullName +" IS SUCCESSFULLY REGISTERED and  System Generated Id : " + systemGenerateIdentifier.getIdentifier() ;
                systemOVCId = systemGenerateIdentifier.getIdentifier();
            }
            
            
            //message = patientId + "_" + systemGenerateIdentifier.getIdentifier();
            
            
        }
        
        
        
        /*
        if ( isPatientInDB( firstName, middleName, lastName, format.parseDate( dateOfBirth ),gender ) )
        {
           message = "aa_\"Duplicate Patient - "+firstName +" " + middleName + " " + lastName+"Form No. "+formValue+"\" Patient with same Form No. already exists"  ;
        }
        */
        
        
        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------
        
        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( ovc );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute().getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getValue() );
            }
        }        
        
        
        // Sending message regarding New Registration to County Users
        String subject = "New Registration of OVC ID  " + systemOVCId;
        
        String currentUser = currentUserService.getCurrentUsername();
        
        String approvalUrl = " " + "<a href=\'../dhis-web-ovc/showUpdateOVCRegistrationInApproveForm.action?id=" + ovc.getId() + "&approveInsideDashBoard=yes" + " \'target=\'_blank\'>Approve OVC</a>";
        
        //sources = " "+sources+" " + "<a href=\'"+link+"\' target=\'_blank\'>"+link+"</a>,";
        
        //String chv = patientAttributeValueMap.get( 43 ) ;
        /*
        String text = "New OVC Registration for Approval/Rejection- " +  "<br/>"  + approvalUrl + "<br/>" + "<strong>OVC Name: </strong>" + ovc.getFullName() + "<br/>" + "OVC ID: " + systemOVCId + "<br/>" + "CHV: " + attr43 + "<br/>"  + "CBO: " + ovc.getOrganisationUnit().getName()  
        + "<br/>" + "Requested By: " + currentUser;
        */
        
        String text = "New OVC Registration for Approval/Rejection- " +  "<br/>"  + approvalUrl + "<br/>" + "OVC Name: " + ovc.getFullName() + "<br/>" + "OVC ID: " + systemOVCId + "<br/>" + "CHV: " + attr43 + "<br/>"  + "CBO: " + ovc.getOrganisationUnit().getName()  
                      + "<br/>" + "Requested By: " + currentUser;
        
        String metaData = MessageService.META_USER_AGENT +
        ServletActionContext.getRequest().getHeader( ContextUtils.HEADER_USER_AGENT );
        
        Set<User> users = new HashSet<User>();
        Constant msgAlertConstant = constantService.getConstantByName( OVC_EXIT_MESSAGE_ALERT );
        
        List<OrganisationUnit> orgUnitBranchList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitBranch( patient.getOrganisationUnit().getId() ) );
    
        for ( OrganisationUnit unit :  orgUnitBranchList )
        {   
            if( (int) msgAlertConstant.getValue() == unit.getOrganisationUnitLevel() )
            {
                users.addAll( unit.getUsers() );
                break;
            }
        }
        
        Set<User> recipientUsers = new HashSet<User>();
        
        for ( User user :  users )
        {
            for( String authority : user.getUserCredentials().getAllAuthorities() )
            {
                if( authority.equalsIgnoreCase( "F_OVC_EXIT_MESSAGE" ))
                {
                    recipientUsers.add( user );
                }
            }
        }
        
        messageService.sendMessage( subject, text, metaData, recipientUsers );
        
        /*
        Set<User> users = new HashSet<User>();

        for ( OrganisationUnit unit :  ovc.getOrganisationUnit().getParent().getChildren() )
        {
            users.addAll( unit.getUsers() );
        }
       
        System.out.println( " Size of Users "   + users.size());
        
        int i = 1;
        for ( User user :  users )
        {
            int j = 1;
            for( String authority : user.getUserCredentials().getAllAuthorities() )
            {
                System.out.println( " User : " + i +  " : " + user.getName() + " -- User authority : " + j  + " : " + authority ); 
                
                j++;
            }
            
            i++;
        }
        
        
        
        String metaData = MessageService.META_USER_AGENT +
        ServletActionContext.getRequest().getHeader( ContextUtils.HEADER_USER_AGENT );
        
        String subject = "Alert message";
        String text = "Waiting for Approve";
        
        messageService.sendMessage( subject, text, metaData, users );
        */
        return SUCCESS;
    }
}
