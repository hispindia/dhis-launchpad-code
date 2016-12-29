package org.hisp.dhis.asha.paymentapprove;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class SaveASHAMOApproveDetailsAction implements Action
{
    public static final String PREFIX_BENEFICIARY = "approveamount";
    public static final String PREFIX_BENEFICIARY_MO_REMARK = "moremark";
    public static final String PREFIX_BENEFICIARY_PENDING = "pendingapproveamount";
    public static final String PREFIX_BENEFICIARY_PENDING_MO_REMARK = "pendingmoremark";
    
    public static final String PREFIX_ASHA_ACTIVITY = "ashaactivityapproveamount";
    public static final String PREFIX_ASHA_ACTIVITY_MO_REMARK = "ashaactivitymoremark";
    public static final String PREFIX_ASHA_ACTIVITY_PENDING = "pendingashaactivityamount";
    public static final String PREFIX_ASHA_ACTIVITY_PENDING_MO_REMARK = "pendingashaactivitymoremark";
    
    public static final String PREFIX_DATAELEMENT = "deps";
    
    public static final String PAYMENT_APPROVE_DATAELEMENT_GROUP_ID = "PAYMENT_APPROVE_DATAELEMENT_GROUP_ID";//11.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//3.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//3.0
    
    private final String OPTION_SET_PAYABLE_AMOUNT = "Payable Amount";
    
    
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BeneficiaryService beneficiaryService;

    public void setBeneficiaryService( BeneficiaryService beneficiaryService )
    {
        this.beneficiaryService = beneficiaryService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private ASHAService ashaService;

    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
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
    
    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
    
    private PatientDataValueService patientDataValueService;
    
    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    /*
    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    */
    
    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
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
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }        
    
    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }
    
    private int id;

    public void setId( int id )
    {
        this.id = id;
    }
    
    public int getId()
    {
        return id;
    }
    
    private Collection<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();

    public Collection<Beneficiary> getBeneficiaryList()
    {
        return beneficiaryList;
    }
    
    private Integer programStageInstanceId;
    
    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }
    
  
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        patient = patientService.getPatient( id );
        
        //Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        Period period = periodService.getPeriodByExternalId( selectedPeriodId );
        
        //System.out.println( "  patient  : " + patient.getFullName() );
        
        //System.out.println( "  period.getStartDateString()  : " + period.getStartDateString() );
        
        
        beneficiaryList = new ArrayList<Beneficiary>( beneficiaryService.getAllBeneficiaryByASHAAndPeriod( patient, period ) );
        
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        
        //System.out.println( " and Value=" + request.getParameter( PREFIX_BENEFICIARY + 15 ) );
        
        
        String value = null;
        String moRemark = null;
        
        for ( Beneficiary beneficiary : beneficiaryList )
        {
            //value = request.getParameter( PREFIX_BENEFICIARY + beneficiary.getId() + ":" + period.getId() );
            value = request.getParameter( PREFIX_BENEFICIARY + beneficiary.getId() );
            
            moRemark = request.getParameter( PREFIX_BENEFICIARY_MO_REMARK + beneficiary.getId() );
            
            Beneficiary tempBeneficiary = beneficiaryService.getBeneficiaryById( beneficiary.getId() );
            
            //System.out.println("Beneficiary  id " + PREFIX_BENEFICIARY + beneficiary.getId()  +" and Value=" + value);
            
            if ( value != null && StringUtils.isNotBlank( value ) )
            {
                tempBeneficiary.setMOApprove( Integer.parseInt( value ) );
            }
            
            if ( moRemark != null && StringUtils.isNotBlank( moRemark ) )
            {
                tempBeneficiary.setMORemark( moRemark );
            }
            
            beneficiaryService.updateBeneficiary( tempBeneficiary );
        }
        
        
        String pendingValue = null;
        String pendingMORemark = null;
        
        List<Beneficiary> pendingBeneficiaryList = new ArrayList<Beneficiary>( ashaService.getAllBeneficiaryPendingByMO( patient, 2, period.getStartDateString() ) );
        
        for ( Beneficiary pendingBeneficiary : pendingBeneficiaryList )
        {
            pendingValue = request.getParameter( PREFIX_BENEFICIARY_PENDING + pendingBeneficiary.getId()  );
            
            pendingMORemark = request.getParameter( PREFIX_BENEFICIARY_PENDING_MO_REMARK + pendingBeneficiary.getId() );
            
            Beneficiary tempPendingBeneficiary = beneficiaryService.getBeneficiaryById( pendingBeneficiary.getId() );
            
            //System.out.println("Beneficiary  id " + PREFIX_BENEFICIARY_PENDING + pendingBeneficiary.getId()  +" and Value=" + pendingMORemark );
            
            if ( pendingValue != null && StringUtils.isNotBlank( pendingValue ) )
            {
                tempPendingBeneficiary.setMOApprove( Integer.parseInt( pendingValue ) );
            }
            
            if ( pendingMORemark != null && StringUtils.isNotBlank( pendingMORemark ) )
            {
                tempPendingBeneficiary.setMORemark( pendingMORemark );
            }
            
            beneficiaryService.updateBeneficiary( tempPendingBeneficiary );
        }
        
        Constant PaymentApproveDataElementGroupConstant = constantService.getConstantByName( PAYMENT_APPROVE_DATAELEMENT_GROUP_ID );
        
        DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( (int) PaymentApproveDataElementGroupConstant.getValue() );
        
        List<DataElement> paymentApproveDEs = new ArrayList<DataElement>( dataElementGroup.getMembers() );
        
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
        
        String ashaActivityMOStatus = null;
        String ashaActivityMORemark = null;
        //String moApproveAmount = null;
        
        if ( paymentApproveDEs != null && paymentApproveDEs.size() > 0 )
        {
            for ( DataElement de : paymentApproveDEs )
            {
                ashaActivityMOStatus = request.getParameter( PREFIX_ASHA_ACTIVITY + de.getId() + ":" + period.getId() );
                
                ashaActivityMORemark = request.getParameter( PREFIX_ASHA_ACTIVITY_MO_REMARK + de.getId() + ":" + period.getId() );
                
                //System.out.println(  "DataElement Id " + de.getId() + ": " +ashaActivityMOStatus + ":" + ashaActivityMORemark );
                
                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, de );
                
                if( patientDataValue != null  )
                {
                    if ( ashaActivityMOStatus != null && StringUtils.isNotBlank( ashaActivityMOStatus ) )
                    {
                        patientDataValue.setMOApprove( Integer.parseInt( ashaActivityMOStatus ) );
                    }
                    
                    if ( ashaActivityMORemark != null && StringUtils.isNotBlank( ashaActivityMORemark ) )
                    {
                        patientDataValue.setMORemark( ashaActivityMORemark );
                    }
                    
                    patientDataValueService.updatePatientDataValue( patientDataValue );
                }
                
            }
        }
        
        // Update payment dataElements
        String storedBy = currentUserService.getCurrentUsername();
        
        String amount = null;
        
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_PAYABLE_AMOUNT );
        
        if( optionSet != null )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }
            
            for( String optionName : optionSet.getOptions() )
            {
                DataElement paymentDataElement = dataElementService.getDataElement( Integer.parseInt( optionName ) );
                
                amount = request.getParameter( PREFIX_DATAELEMENT + paymentDataElement.getId() );
                
                PatientDataValue paymentPatientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, paymentDataElement );
                
                if( paymentPatientDataValue == null  )
                {
                    if ( amount != null && StringUtils.isNotBlank( amount ) )
                    {
                        boolean providedElsewhere = false;

                        paymentPatientDataValue = new PatientDataValue( programStageInstance, paymentDataElement, new Date(), amount );
                        paymentPatientDataValue.setProvidedElsewhere( providedElsewhere );
                        paymentPatientDataValue.setValue(amount);
                        paymentPatientDataValue.setStoredBy( storedBy );
                        paymentPatientDataValue.setTimestamp( new Date() );
                        patientDataValueService.savePatientDataValue( paymentPatientDataValue );
                    }
                }
                else
                {
                    paymentPatientDataValue.setValue(amount);
                    paymentPatientDataValue.setStoredBy( storedBy );
                    paymentPatientDataValue.setTimestamp( new Date() );
                    patientDataValueService.updatePatientDataValue( paymentPatientDataValue );
                }
            }
            
        }
        
        /*
        DataElement paymentDataElement = dataElementService.getDataElement( 160 );
        
        PatientDataValue paymentPatientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, paymentDataElement );
        
        if( paymentPatientDataValue != null  )
        {
            moApproveAmount = request.getParameter( PREFIX_DATAELEMENT + 160 );
            
            if ( moApproveAmount != null && StringUtils.isNotBlank( moApproveAmount ) )
            {
                paymentPatientDataValue.setValue(moApproveAmount);
            }
            
            patientDataValueService.updatePatientDataValue( paymentPatientDataValue );
        }
        */
        
        // Pending ASHA Activity Details
        
        Constant ashaActivityDetailsrogramConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_ID );
        
        Program activityDetailsprogram = programService.getProgram( (int) ashaActivityDetailsrogramConstant.getValue() );
        
        Integer activityDetailsprogramInstanceId = ashaService.getProgramInstanceId( patient.getId(), activityDetailsprogram.getId() );
        
        Constant ashaActivityDetailsProgramStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID );
        
        ProgramStage activityDetailsProgramStage = programStageService.getProgramStage( (int) ashaActivityDetailsProgramStageConstant.getValue() );
        
        
        //Map<Date, Map<Integer,String>> pendingPatientDataValueMapByPeriod  = new TreeMap<Date, Map<Integer,String>>( ashaService.getMOPendingPatientDataValues( patient.getId(), activityDetailsprogram.getId(), activityDetailsProgramStage.getId(), 2, period.getStartDateString()  ) );
        
        Map<Date, Map<Integer,String>> pendingPatientDataValueMapByPeriod  = new TreeMap<Date, Map<Integer,String>>( ashaService.getMONotApprovePatientDataValues( patient.getId(), activityDetailsprogram.getId(), activityDetailsProgramStage.getId(), period.getStartDateString()  ) );
        
        
        List<Date> pendingPeriodList  = new ArrayList<Date>( pendingPatientDataValueMapByPeriod.keySet() );
        
        Collections.reverse( pendingPeriodList );
        
        SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd");
        
        String ashaPendingActivityMOStatus = null;
        String ashaPendingActivityMORemark = null;
        
        for ( Date date : pendingPeriodList )
        {
            Integer activityDetailsProgramStageInstanceId = ashaService.getProgramStageInstanceId( activityDetailsprogramInstanceId, activityDetailsProgramStage.getId(), dateFormat.format( date ) );
            
            ProgramStageInstance pendingActivityProgramStageInstance = programStageInstanceService.getProgramStageInstance( activityDetailsProgramStageInstanceId );
            
            for ( DataElement dataElement : paymentApproveDEs )
            {
                ashaPendingActivityMOStatus = request.getParameter( PREFIX_ASHA_ACTIVITY_PENDING + dataElement.getId() + ":" + dateFormat.format( date ) );
                
                ashaPendingActivityMORemark = request.getParameter( PREFIX_ASHA_ACTIVITY_PENDING_MO_REMARK + dataElement.getId() + ":" + dateFormat.format( date ) );
                
                //System.out.println(  "DataElement Id " + dataElement.getId() + ": " + ashaPendingActivityMOStatus + ":" + ashaPendingActivityMORemark );
                
                PatientDataValue pendingPatientDataValue = patientDataValueService.getPatientDataValue( pendingActivityProgramStageInstance, dataElement );
                
                if( pendingPatientDataValue != null  )
                {
                    if ( ashaPendingActivityMOStatus != null && StringUtils.isNotBlank( ashaPendingActivityMOStatus ) && pendingPatientDataValue.getMOApprove() != 3 )
                    {
                        pendingPatientDataValue.setMOApprove( Integer.parseInt( ashaPendingActivityMOStatus ) );
                    }
                    
                    if ( ashaPendingActivityMORemark != null && StringUtils.isNotBlank( ashaPendingActivityMORemark ) )
                    {
                        pendingPatientDataValue.setMORemark( ashaPendingActivityMORemark );
                    }
                    
                    patientDataValueService.updatePatientDataValue( pendingPatientDataValue );
                }
                
            }
            
        }
        
        //Integer activityDetailsProgramStageInstanceId;
    
        return SUCCESS;
    }
    
}
