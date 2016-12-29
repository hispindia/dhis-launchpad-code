package org.hisp.dhis.asha.drugkitiec;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.asha.facilitator.SaveASHAFacilitatorDataValueAction;
import org.hisp.dhis.asha.util.ASHAService;
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
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
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
public class SaveASHADrugKitIECDetailsAction implements Action
{
    private static final Log log = LogFactory.getLog( SaveASHAFacilitatorDataValueAction.class );
    
    private final String OPTION_SET_DRUGS = "Drugs";
    
    public static final String PREFIX_DATAELEMENT = "deps";
    public static final String PREFIX_DATAELEMENT_NEXT_PERIOD = "depsnextperiod";
    
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

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
    
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
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
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    private Integer programId;
    
    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    /*
    private Integer programInstanceId;
    
    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }
    */
    
    private Integer programStageId;
    
    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }
    
    /*
    private Integer programStageInstanceId;
    
    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }
    */
    
    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return programStage;
    }
    
    private int statusCode = 0;

    public int getStatusCode()
    {
        return statusCode;
    }  
    
    private Integer nextMonthProgramStageInstanceId;
    
    public Integer getNextMonthProgramStageInstanceId()
    {
        return nextMonthProgramStageInstanceId;
    }
    
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        
        patient = patientService.getPatient( id );
        
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        if ( period == null )
        {
            return logError( "Illegal period identifier: " + selectedPeriodId );
        }
        
        program = programService.getProgram( programId );
        
        ProgramStage programStage = programStageService.getProgramStage( programStageId );
        
        
        Integer programInstanceId = ashaService.getProgramInstanceId( patient.getId(), program.getId() );

        Integer programStageInstanceId = null;

        if ( programInstanceId != null )
        {
            programStageInstanceId = ashaService.getProgramStageInstanceId( programInstanceId, programStage.getId(), period.getStartDateString() );
        }

        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );

        if ( programStageInstanceId == null )
        {
            ProgramStageInstance tempProgramStageInstance = new ProgramStageInstance();
            tempProgramStageInstance.setProgramInstance( programInstance );
            tempProgramStageInstance.setProgramStage( programStage );
            tempProgramStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );
            tempProgramStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
            tempProgramStageInstance.setDueDate( format.parseDate( period.getStartDateString() ) );

            programStageInstanceId = programStageInstanceService.addProgramStageInstance( tempProgramStageInstance );
        }
        
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
        
        HttpServletRequest request = ServletActionContext.getRequest();

        //String value = null;

        String storedBy = currentUserService.getCurrentUsername();

        List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        
        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }

            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                
                String value = request.getParameter( PREFIX_DATAELEMENT + programStageDataElement.getDataElement().getId() );
                
                
                
                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,programStageDataElement.getDataElement() );

                if ( patientDataValue == null )
                {
                    if ( value != null && StringUtils.isNotBlank( value ) )
                    {
                        boolean providedElsewhere = false;

                        patientDataValue = new PatientDataValue( programStageInstance, programStageDataElement.getDataElement(), new Date(), value );
                        patientDataValue.setProvidedElsewhere( providedElsewhere );
                        patientDataValue.setValue( value );
                        patientDataValue.setStoredBy( storedBy );
                        patientDataValue.setTimestamp( new Date() );
                        patientDataValueService.savePatientDataValue( patientDataValue );
                    }
                }
                else
                {
                    patientDataValue.setValue( value );
                    patientDataValue.setStoredBy( storedBy );
                    patientDataValue.setTimestamp( new Date() );
                    patientDataValueService.updatePatientDataValue( patientDataValue );
                }

           
                    
            }
        }

        
        // for NEXT Month
        
        Patient patient = patientService.getPatient( id );
        
        programStage = programStageService.getProgramStage( programStageId );
        
        Calendar nextExecution = Calendar.getInstance();
        
        nextExecution.setTime( period.getStartDate() );
        
        nextExecution.add( Calendar.MONTH , 1  );
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String nextExecutionDate = simpleDateFormat.format( nextExecution.getTime() );      
        
        programInstance = programInstanceService.getProgramInstance( programInstanceId );
        
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
        
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_DRUGS );
        
        if( optionSet != null )
        {
            if ( nextMonthProgramStageInstance.getExecutionDate() == null )
            {
                nextMonthProgramStageInstance.setExecutionDate( format.parseDate( nextExecutionDate ) );
                programStageInstanceService.updateProgramStageInstance( nextMonthProgramStageInstance );
            }
            
            for( String optionName : optionSet.getOptions() )
            {
                DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( optionName ) );
                
                String nextPeriodValue = request.getParameter( PREFIX_DATAELEMENT_NEXT_PERIOD + dataElement.getId() );
                
                PatientDataValue nextPeriodPatientDataValue = patientDataValueService.getPatientDataValue( nextMonthProgramStageInstance, dataElement );
                
                
                if( nextPeriodPatientDataValue == null  )
                {
                    if ( nextPeriodValue != null && StringUtils.isNotBlank( nextPeriodValue ) )
                    {
                        boolean providedElsewhere = false;

                        nextPeriodPatientDataValue = new PatientDataValue( nextMonthProgramStageInstance, dataElement, new Date(), nextPeriodValue );
                        nextPeriodPatientDataValue.setProvidedElsewhere( providedElsewhere );
                        nextPeriodPatientDataValue.setValue(nextPeriodValue);
                        nextPeriodPatientDataValue.setStoredBy( storedBy );
                        nextPeriodPatientDataValue.setTimestamp( new Date() );
                        patientDataValueService.savePatientDataValue( nextPeriodPatientDataValue );
                    }
                }
                else
                {
                    nextPeriodPatientDataValue.setValue(nextPeriodValue);
                    nextPeriodPatientDataValue.setStoredBy( storedBy );
                    nextPeriodPatientDataValue.setTimestamp( new Date() );
                    patientDataValueService.updatePatientDataValue( nextPeriodPatientDataValue );
                }
            }
            
        }
        
        
        
        
        
        
        
        return SUCCESS;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String logError( String message )
    {
        return logError( message, 1 );
    }

    private String logError( String message, int statusCode )
    {
        log.info( message );

        this.statusCode = statusCode;

        return SUCCESS;
    } 
}

