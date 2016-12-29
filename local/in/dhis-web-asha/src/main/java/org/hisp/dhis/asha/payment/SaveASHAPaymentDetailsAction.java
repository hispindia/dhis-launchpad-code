package org.hisp.dhis.asha.payment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class SaveASHAPaymentDetailsAction implements Action
{
    public static final String PREFIX_DATAELEMENT = "deps";
    private final String OPTION_SET_PAYMENT = "Payment";
    public static final String PAYMENT_DUE_DATAELEMENT = "Payment Due";//159.0
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
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private ProgramInstanceService programInstanceService;
    
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
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
    
    private Integer nextMonthProgramStageInstanceId;
    
    public Integer getNextMonthProgramStageInstanceId()
    {
        return nextMonthProgramStageInstanceId;
    }
        
    private Integer programInstanceId;
    
    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }
    
    private int ashaId;
    
    public void setAshaId( int ashaId )
    {
        this.ashaId = ashaId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
       
        // -----------------------------------------------------------------------------
        // Prepare Patient DataValues
        // -----------------------------------------------------------------------------
        
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        String value = null;
        
        String storedBy = currentUserService.getCurrentUsername();
        
        
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_PAYMENT );
        
        if( optionSet != null )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                //programStageInstance.setExecutionDate( format.parseDate( nextExecutionDate ) );
                programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }
            
            for( String optionName : optionSet.getOptions() )
            {
                DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( optionName ) );
                
                value = request.getParameter( PREFIX_DATAELEMENT + dataElement.getId() );
                
                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, dataElement );
                
                if( patientDataValue == null  )
                {
                    if ( value != null && StringUtils.isNotBlank( value ) )
                    {
                        boolean providedElsewhere = false;

                        patientDataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );
                        patientDataValue.setProvidedElsewhere( providedElsewhere );
                        patientDataValue.setValue(value);
                        patientDataValue.setStoredBy( storedBy );
                        patientDataValue.setTimestamp( new Date() );
                        patientDataValueService.savePatientDataValue( patientDataValue );
                    }
                }
                else
                {
                    patientDataValue.setValue(value);
                    patientDataValue.setStoredBy( storedBy );
                    patientDataValue.setTimestamp( new Date() );
                    patientDataValueService.updatePatientDataValue( patientDataValue );
                }
            }
            
        }
        
        
// for NEXT Month
        
        Patient patient = patientService.getPatient( ashaId );
        
        ProgramStage programStage = programStageService.getProgramStage( programStageId );
        
        Calendar nextExecution = Calendar.getInstance();
        
        nextExecution.setTime( period.getStartDate() );
        
        nextExecution.add( Calendar.MONTH , 1  );
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String nextExecutionDate = simpleDateFormat.format( nextExecution.getTime() );
        
        //System.out.println(  " Next Execution Date -- "+ nextExecutionDate );
        
        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );
        
        nextMonthProgramStageInstanceId = ashaService.getProgramStageInstanceId( programInstanceId, programStage.getId(), nextExecutionDate );
        
        if ( nextMonthProgramStageInstanceId == null )
        {
            ProgramStageInstance nextMonthProgramStageInstance = new ProgramStageInstance();
            nextMonthProgramStageInstance.setProgramInstance( programInstance );
            nextMonthProgramStageInstance.setProgramStage( programStage );
            nextMonthProgramStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );
            nextMonthProgramStageInstance.setExecutionDate( format.parseDate( nextExecutionDate ) );
            
            nextMonthProgramStageInstance.setDueDate( format.parseDate( nextExecutionDate ) );
            
            nextMonthProgramStageInstanceId = programStageInstanceService.addProgramStageInstance( nextMonthProgramStageInstance );
        }
        
        ProgramStageInstance nextMonthProgramStageInstance = programStageInstanceService.getProgramStageInstance( nextMonthProgramStageInstanceId );  
        
        Constant paymentDueConstant = constantService.getConstantByName( PAYMENT_DUE_DATAELEMENT );
        
        if( paymentDueConstant != null )
        {
            if ( nextMonthProgramStageInstance.getExecutionDate() == null )
            {
                nextMonthProgramStageInstance.setExecutionDate( format.parseDate( nextExecutionDate ) );
                programStageInstanceService.updateProgramStageInstance( nextMonthProgramStageInstance );
            }
            
            DataElement paymentDueDataElement = dataElementService.getDataElement( (int) paymentDueConstant.getValue() );
            
            value = request.getParameter( PREFIX_DATAELEMENT + paymentDueDataElement.getId() );
            
            PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( nextMonthProgramStageInstance, paymentDueDataElement );
            
            if( patientDataValue == null  )
            {
                if ( value != null && StringUtils.isNotBlank( value ) )
                {
                    boolean providedElsewhere = false;

                    patientDataValue = new PatientDataValue( nextMonthProgramStageInstance, paymentDueDataElement, new Date(), value );
                    patientDataValue.setProvidedElsewhere( providedElsewhere );
                    patientDataValue.setValue(value);
                    patientDataValue.setStoredBy( storedBy );
                    patientDataValue.setTimestamp( new Date() );
                    patientDataValueService.savePatientDataValue( patientDataValue );
                }
            }
            else
            {
                patientDataValue.setValue(value);
                patientDataValue.setStoredBy( storedBy );
                patientDataValue.setTimestamp( new Date() );
                patientDataValueService.updatePatientDataValue( patientDataValue );
            }
        }
        
        /*
        
        List<ProgramStageDataElement> programStageDataElements =  new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }
            
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                value = request.getParameter( PREFIX_DATAELEMENT + programStageDataElement.getDataElement().getId() );
                
                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, programStageDataElement.getDataElement());
                
                if( patientDataValue == null  )
                {
                    if ( value != null && StringUtils.isNotBlank( value ) )
                    {
                        boolean providedElsewhere = false;

                        patientDataValue = new PatientDataValue( programStageInstance, programStageDataElement.getDataElement(), new Date(), value );
                        patientDataValue.setProvidedElsewhere( providedElsewhere );
                        patientDataValue.setValue(value);
                        patientDataValue.setStoredBy( storedBy );
                        patientDataValue.setTimestamp( new Date() );
                        patientDataValueService.savePatientDataValue( patientDataValue );
                    }
                }
                else
                {
                    patientDataValue.setValue(value);
                    patientDataValue.setStoredBy( storedBy );
                    patientDataValue.setTimestamp( new Date() );
                    patientDataValueService.updatePatientDataValue( patientDataValue );
                }
              
            }
        }
        */
        return SUCCESS;
    }
    
}


