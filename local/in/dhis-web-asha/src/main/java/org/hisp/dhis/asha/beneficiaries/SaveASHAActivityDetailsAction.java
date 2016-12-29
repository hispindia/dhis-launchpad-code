package org.hisp.dhis.asha.beneficiaries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.asha.facilitator.SaveASHAFacilitatorDataValueAction;
import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
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
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveASHAActivityDetailsAction implements Action
{
    private static final Log log = LogFactory.getLog( SaveASHAFacilitatorDataValueAction.class );
    
    public static final String ASHA_ACTIVITY_DETAILS_GROUP_ID = "ASHA_ACTIVITY_DETAILS_GROUP_ID";//12.0
    
    public static final String PREFIX_DATAELEMENT = "deps";
        
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

    private Integer programStageId;
    
    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

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
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        //System.out.println( id + " : " + programId + " : " + programStageId + " : " + selectedPeriodId );
        
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
        
        //System.out.println( id + " : " + programId + " : " + programStageId + " : " + programInstanceId + " : "  + programStageInstanceId );
        
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
        
        HttpServletRequest request = ServletActionContext.getRequest();

        //String value = null;

        String storedBy = currentUserService.getCurrentUsername();
        
        Constant ashaActivityDetailsDataElementGroupConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_GROUP_ID );
        
        DataElementGroup ashaActivityDetailsDataElementGroup = dataElementService.getDataElementGroup( (int) ashaActivityDetailsDataElementGroupConstant.getValue() );
        
        List<DataElement> ashaActivityDetailsDataElements = new ArrayList<DataElement>( ashaActivityDetailsDataElementGroup.getMembers() );
        
        //List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        //if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        if ( ashaActivityDetailsDataElements != null && ashaActivityDetailsDataElements.size() > 0 )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }

            //for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            for ( DataElement dataElement : ashaActivityDetailsDataElements )
            {
                
                //String value = request.getParameter( PREFIX_DATAELEMENT + programStageDataElement.getDataElement().getId() );
                String value = request.getParameter( PREFIX_DATAELEMENT + dataElement.getId() );
                
                //PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,programStageDataElement.getDataElement() );
                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, dataElement );
                
                /*
                if ( patientDataValue != null )
                {
                    System.out.println( " In side Not null DataElement Id : " + dataElement.getId() +" Patient DataValue : " + patientDataValue.getValue() + " : value is : " + value ); 
                }
                */
                
               
                if ( patientDataValue == null )
                {
                    //System.out.println( " In side null DataElement Id : " + dataElement.getId() +" Patient DataValue : null and : value is : " + value ); 
                    
                    if ( value != null && StringUtils.isNotBlank( value ) )
                    {
                        boolean providedElsewhere = false;
                        
                        int moApprove = 0;
                        int aaApprove = 0;
                        
                        //patientDataValue = new PatientDataValue( programStageInstance, programStageDataElement.getDataElement(), new Date(), value );
                        patientDataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );
                        patientDataValue.setProvidedElsewhere( providedElsewhere );
                        patientDataValue.setValue( value );
                        patientDataValue.setStoredBy( storedBy );
                        patientDataValue.setTimestamp( new Date() );
                        
                        patientDataValue.setMOApprove( moApprove );
                        patientDataValue.setAAApprove( aaApprove );
                        
                        patientDataValueService.savePatientDataValue( patientDataValue );
                    }
                    
                    
                }
                else
                {       
                    //System.out.println( " In side Not null DataElement Id : " + dataElement.getId() +" Patient DataValue : " + patientDataValue.getValue() + " : value is : " + value ); 
                    
                    patientDataValue.setValue( value );
                    patientDataValue.setStoredBy( storedBy );
                    patientDataValue.setTimestamp( new Date() );
                    patientDataValueService.updatePatientDataValue( patientDataValue );
                    
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

