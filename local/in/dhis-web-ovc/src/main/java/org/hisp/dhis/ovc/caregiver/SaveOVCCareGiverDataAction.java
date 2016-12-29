package org.hisp.dhis.ovc.caregiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.ovc.util.OVCService;
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
public class SaveOVCCareGiverDataAction implements Action
{

    public static final String PREFIX_DATAELEMENT = "deps";

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

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private OVCService ovcService;

    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }

    /*
     * private ConstantService constantService;
     * 
     * public void setConstantService( ConstantService constantService ) {
     * this.constantService = constantService; }
     */
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
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

    /*
     * private Integer programStageInstanceId;
     * 
     * public void setProgramStageInstanceId( Integer programStageInstanceId ) {
     * this.programStageInstanceId = programStageInstanceId; }
     */

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private Integer ovcID;

    public void setOvcID( Integer ovcID )
    {
        this.ovcID = ovcID;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    private Integer programID;

    public void setProgramID( Integer programID )
    {
        this.programID = programID;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );

        patient = patientService.getPatient( ovcID );
        // -----------------------------------------------------------------------------
        // Prepare Patient DataValues
        // -----------------------------------------------------------------------------

        // Constant programConstant = constantService.getConstantByName(
        // OVC_MONTHLY_VISIT_PROGRAM );

        // program = programService.getProgram( (int) programConstant.getValue()
        // );

        program = programService.getProgram( programID );

        ProgramStage programStage = programStageService.getProgramStage( programStageId );

        Integer programInstanceId = ovcService.getProgramInstanceId( patient.getId(), program.getId() );

        Integer programStageInstanceId = null;

        if ( programInstanceId != null )
        {
            programStageInstanceId = ovcService.getProgramStageInstanceId( programInstanceId, programStage.getId(),
                period.getStartDateString() );
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

        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance( programStageInstanceId );

        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        String storedBy = currentUserService.getCurrentUsername();

        List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage
            .getProgramStageDataElements() );

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

                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,
                    programStageDataElement.getDataElement() );

                if ( patientDataValue == null )
                {
                    if ( value != null && StringUtils.isNotBlank( value ) )
                    {
                        boolean providedElsewhere = false;

                        patientDataValue = new PatientDataValue( programStageInstance, programStageDataElement
                            .getDataElement(), new Date(), value );
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

        return SUCCESS;
    }

}
