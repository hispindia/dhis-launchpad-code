package org.hisp.dhis.caseentry.action.caseentry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.caseentry.state.Service;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
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
public class SaveDataInPatientDataValueAction implements Action
{
    public static final String PROGRAM_CONSTANT = "PROGRAM_CONSTANT";//1

    public static final String PROGRAM_STAGE_CONSTANT = "PROGRAM_STAGE_CONSTANT";// 1
    
    public static final String PREFIX_DATAELEMENT = "deps";
    
    public static final String PREFIX_CHECKBOX = "checkbox";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
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
    
    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    
    private Service service;
    
    public void setService( Service service )
    {
        this.service = service;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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
    
    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
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
    // Input/Output
    // -------------------------------------------------------------------------
    
    private String currentDate;
    
    public void setCurrentDate( String currentDate )
    {
        this.currentDate = currentDate;
    }
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        System.out.println( " In side Save Action " );
        
        Date executionDate = format.parseDate( currentDate );
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( 2 );
        
        Constant programConstant = constantService.getConstantByName( PROGRAM_CONSTANT );
        Constant programStageConstant = constantService.getConstantByName( PROGRAM_STAGE_CONSTANT );
        
        Program program = programService.getProgram( (int) programConstant.getValue() );
        
        Integer programInstanceId = service.getProgramInstanceId( program.getId() );
        
        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );
        
        /*
        int type = program.getType();
        
        ProgramInstance programInstance = null;

        if ( type == Program.SINGLE_EVENT_WITH_REGISTRATION )
        {
            // Add a new program-instance
            programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( executionDate );
            programInstance.setDateOfIncident( executionDate );
            programInstance.setProgram( program );
            //programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );

            programInstance.setPatient( null );
           
            programInstanceService.addProgramInstance( programInstance );
        }
        */
        
        String storedBy = currentUserService.getCurrentUsername();
        
        ProgramStage programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        
        List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        List<Integer>programStageInstanceIds = new ArrayList<Integer>( service.getLatestProgramStageInstanceIds() );
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        if( programStageInstanceIds != null && programStageInstanceIds.size() > 0 )
        {
            for( Integer pgInstanceId : programStageInstanceIds )
            {
                String checkBoxStatus = request.getParameter( PREFIX_CHECKBOX +  pgInstanceId );
                
                if( checkBoxStatus != null && checkBoxStatus.equalsIgnoreCase( "true" ) )
                {
                    Integer programStageInstanceId = null;
                    
                    if ( programInstanceId != null )
                    {
                        // Add a new program-stage-instance
                        ProgramStageInstance programStageInstance = new ProgramStageInstance();
                        
                        programStageInstance.setProgramInstance( programInstance );
                        programStageInstance.setProgramStage( programStage );
                        programStageInstance.setDueDate( executionDate );
                        programStageInstance.setExecutionDate( executionDate );
                        programStageInstance.setOrganisationUnit( organisationUnit );

                        programStageInstanceId = programStageInstanceService.addProgramStageInstance( programStageInstance );
                        
                    }
                    
                    ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
                    
                    if ( programStageDataElements != null && programStageDataElements.size() > 0 )
                    {
                        if ( programStageInstance.getExecutionDate() == null )
                        {
                            programStageInstance.setExecutionDate( executionDate );
                            programStageInstanceService.updateProgramStageInstance( programStageInstance );
                        }

                        for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
                        {
                            String value = request.getParameter( PREFIX_DATAELEMENT + pgInstanceId + ":" + programStageDataElement.getDataElement().getId() );

                            PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, programStageDataElement.getDataElement() );

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
                    
                }
                
            }
        }
        
        return SUCCESS;
    }
}
