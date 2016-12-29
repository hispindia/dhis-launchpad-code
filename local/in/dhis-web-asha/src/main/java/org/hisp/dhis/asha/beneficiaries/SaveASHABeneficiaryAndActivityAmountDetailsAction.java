package org.hisp.dhis.asha.beneficiaries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdataentrylock.PatientDataEntryLock;
import org.hisp.dhis.patientdataentrylock.PatientDataEntryLockService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveASHABeneficiaryAndActivityAmountDetailsAction implements Action
{
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//4.0
    
    public static final String PREFIX_DATAELEMENT = "deps";
    
    public static final String PREFIX_ASHA_ACTIVITY_DATAELEMENT = "depsamount";
    
    public static final String PAYMENT_DATAELEMENT_GROUP_ID = "PAYMENT_DATAELEMENT_GROUP_ID";//10.0
    
    
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
    
    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private PatientDataValueService patientDataValueService;
    
    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private PatientDataEntryLockService patientDataEntryLockService;
    
    public void setPatientDataEntryLockService( PatientDataEntryLockService patientDataEntryLockService )
    {
        this.patientDataEntryLockService = patientDataEntryLockService;
    }
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    /*
    private ASHAService ashaService;

    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    */
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }        
    
    private Integer programStageId;
    
    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }
    
    private Integer programStageInstanceId;
    
    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }
    
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        Patient patient = patientService.getPatient( id );
        
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        Constant PaymentDataElementGroupConstant = constantService.getConstantByName( PAYMENT_DATAELEMENT_GROUP_ID );
        
        DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( (int) PaymentDataElementGroupConstant.getValue() );
        
     
        //Patient patient = patientService.getPatient( id );
        
        // -----------------------------------------------------------------------------
        // Prepare Patient DataValues
        // -----------------------------------------------------------------------------
        //System.out.println("here");
        ProgramStage programStage = programStageService.getProgramStage( programStageId );
        
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        String value = null;
        
        String storedBy = currentUserService.getCurrentUsername();
         
        List<ProgramStageDataElement> programStageDataElements =  new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        List<DataElement> programStageDataElementList = new ArrayList<DataElement>();
        
        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                programStageDataElementList.add( programStageDataElement.getDataElement() );
            }
        }
        
        
        List<DataElement> paymentDEs = new ArrayList<DataElement>( dataElementGroup.getMembers() );
        
        paymentDEs.retainAll( programStageDataElementList );
        

        if ( paymentDEs != null && paymentDEs.size() > 0 )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }
            
            for ( DataElement programStageDataElement : paymentDEs )
            {
                value = request.getParameter( PREFIX_DATAELEMENT + programStageDataElement.getId() );
                
                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, programStageDataElement );
                
                if( patientDataValue == null  )
                {
                    if ( value != null && StringUtils.isNotBlank( value ) )
                    {
                        boolean providedElsewhere = false;
                        
                        int moApprove = 0;
                        int aaApprove = 0;
                            
                        patientDataValue = new PatientDataValue( programStageInstance, programStageDataElement, new Date(), value );
                        patientDataValue.setProvidedElsewhere( providedElsewhere );
                        patientDataValue.setValue(value);
                        patientDataValue.setStoredBy( storedBy );
                        patientDataValue.setTimestamp( new Date() );
                        
                        patientDataValue.setMOApprove( moApprove );
                        patientDataValue.setAAApprove( aaApprove );
                        
                        patientDataValueService.savePatientDataValue( patientDataValue );
                        //System.out.println("patient datavalue saved for" + PREFIX_DATAELEMENT+programStageDataElement.getDataElement().getId() +"value=" +value);
                    }
                }
                else
                {
                    patientDataValue.setValue(value);
                    patientDataValue.setStoredBy( storedBy );
                    patientDataValue.setTimestamp( new Date() );
                    patientDataValueService.updatePatientDataValue( patientDataValue );
                    //System.out.println("patient datavalue updated for" + PREFIX_DATAELEMENT+programStageDataElement.getDataElement().getId() +"value=" +value);
                    
                }
              
            }
        }
        
        
        // Asha Activity Details Program
        
        /*
        Constant ashaActivityDetailsProgramConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_ID );
        
        Program activityDetailsProgram = programService.getProgram( (int) ashaActivityDetailsProgramConstant.getValue() );
        
        Integer activityDetailsprogramInstanceId = ashaService.getProgramInstanceId( patient.getId(), activityDetailsProgram.getId() );
        
        Constant ashaActivityDetailsProgramStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID );
        
        ProgramStage activityDetailsProgramStage = programStageService.getProgramStage( (int) ashaActivityDetailsProgramStageConstant.getValue() );
        
        Integer activityDetailsProgramStageInstanceId = null;
        
        if ( activityDetailsprogramInstanceId != null )
        {
            activityDetailsProgramStageInstanceId = ashaService.getProgramStageInstanceId( activityDetailsprogramInstanceId, activityDetailsProgramStage.getId(), period.getStartDateString() );
            
        }        
        
        ProgramStageInstance activityDetailsProgramStageInstance = programStageInstanceService.getProgramStageInstance( activityDetailsProgramStageInstanceId );
        
        List<ProgramStageDataElement> activityDetailsProgramStageDataElements =  new ArrayList<ProgramStageDataElement>( activityDetailsProgramStage.getProgramStageDataElements() );
        
        if ( activityDetailsProgramStageDataElements != null && activityDetailsProgramStageDataElements.size() > 0 )
        {
            if ( activityDetailsProgramStageInstance.getExecutionDate() == null )
            {
                activityDetailsProgramStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                programStageInstanceService.updateProgramStageInstance( activityDetailsProgramStageInstance );
            }
            
            for ( ProgramStageDataElement programStageDataElement : activityDetailsProgramStageDataElements )
            {
                value = request.getParameter( PREFIX_ASHA_ACTIVITY_DATAELEMENT + programStageDataElement.getDataElement().getId() );
                
                PatientDataValue activityDetailsPatientDataValue = patientDataValueService.getPatientDataValue( activityDetailsProgramStageInstance, programStageDataElement.getDataElement());
                
                if( activityDetailsPatientDataValue == null  )
                {
                    if ( value != null && StringUtils.isNotBlank( value ) )
                    {
                        boolean providedElsewhere = false;
                        
                        int moApprove = 0;
                        int aaApprove = 0;
                            
                        activityDetailsPatientDataValue = new PatientDataValue( activityDetailsProgramStageInstance, programStageDataElement.getDataElement(), new Date(), value );
                        activityDetailsPatientDataValue.setProvidedElsewhere( providedElsewhere );
                        activityDetailsPatientDataValue.setValue(value);
                        activityDetailsPatientDataValue.setStoredBy( storedBy );
                        activityDetailsPatientDataValue.setTimestamp( new Date() );
                        
                        activityDetailsPatientDataValue.setMOApprove( moApprove );
                        activityDetailsPatientDataValue.setAAApprove( aaApprove );
                        
                        patientDataValueService.savePatientDataValue( activityDetailsPatientDataValue );
                        //System.out.println("patient datavalue saved for" + PREFIX_DATAELEMENT+programStageDataElement.getDataElement().getId() +"value=" +value);
                    }
                }
                else
                {
                    activityDetailsPatientDataValue.setValue(value);
                    activityDetailsPatientDataValue.setStoredBy( storedBy );
                    activityDetailsPatientDataValue.setTimestamp( new Date() );
                    patientDataValueService.updatePatientDataValue( activityDetailsPatientDataValue );
                    //System.out.println("patient datavalue updated for" + PREFIX_DATAELEMENT+programStageDataElement.getDataElement().getId() +"value=" +value);
                    
                }
              
            }
        }
        
        */
        
        
        
 // for lock the DataEnty for ASHA ACTIVITY and Beneficiary Details       
        
        /*
        PatientDataEntryLock patientDataEntryLock = new PatientDataEntryLock();
        patientDataEntryLock = new PatientDataEntryLock();
        
        patientDataEntryLock.setOrganisationUnit( patient.getOrganisationUnit() );
        patientDataEntryLock.setPeriod( period );
        patientDataEntryLock.setPatient( patient );
        patientDataEntryLock.setLockStatus( true );
        
        patientDataEntryLockService.addLock( patientDataEntryLock );
        */
        
        /*
        PatientDataEntryLock patientDataEntryLock = new PatientDataEntryLock();
        
        patientDataEntryLock = patientDataEntryLockService.getPatientDataEntryLock( patient.getOrganisationUnit(), period, patient );
        
        if( patientDataEntryLock != null && patientDataEntryLock.isLockStatus() )
        {   
            //System.out.println(" Update Lock" + patient.getOrganisationUnit().getId() +":" + period.getId() +":" + patient.getId());
            
            patientDataEntryLockService.updateLock( patientDataEntryLock );
        }
       
        else
        {   
            //System.out.println(" Add Lock" + patient.getOrganisationUnit().getId() +":" + period.getId() +":" + patient.getId());
            
            patientDataEntryLock = new PatientDataEntryLock();
            
            patientDataEntryLock.setOrganisationUnit( patient.getOrganisationUnit() );
            patientDataEntryLock.setPeriod( period );
            patientDataEntryLock.setPatient( patient );
            patientDataEntryLock.setLockStatus( true );
            
            patientDataEntryLockService.addLock( patientDataEntryLock );
        }
        */
        
                
        return SUCCESS;
    }
    
}
