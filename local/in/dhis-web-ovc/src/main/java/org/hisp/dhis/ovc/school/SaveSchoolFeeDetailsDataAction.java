package org.hisp.dhis.ovc.school;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.Patient;
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
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveSchoolFeeDetailsDataAction implements Action
{
    
    public static final String OVC_SCHOOL_MANAGEMENT_PROGRAM = "OVC_SCHOOL_MANAGEMENT_PROGRAM";//1700.0

    public static final String OVC_SCHOOL_MANAGEMENT_PROGRAM_STAGE = "OVC_SCHOOL_MANAGEMENT_PROGRAM_STAGE";// 1703.0
    
    public static final String PREFIX_DATAELEMENT = "deps";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private OVCService ovcService;

    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
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
    
    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
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
    
    /*
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
    
    private Integer schoolId;

    public void setSchoolId( Integer schoolId )
    {
        this.schoolId = schoolId;
    }


    private String message;

    public String getMessage()
    {
        return message;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        //System.out.println( " Inside Save School Details data Entry form : " );
        
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        School school = schoolService.getSchool( schoolId );
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String periodLastDate = dateFormat.format( period.getEndDate() );
        
        period.setName( format.formatPeriod( period ) );
        
        List<Patient> ovcList = new ArrayList<Patient>( ovcService.getPatients( school.getId(), periodLastDate ) );
        
        String storedBy = currentUserService.getCurrentUsername();
        
        //User user = currentUserService.getCurrentUser();
        
        /*
        OrganisationUnit currentOrgUnitUser = user.getOrganisationUnit();
        
        List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnitUser.getId() ) );
        
        List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
        String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );
        */

        
        //System.out.println( " isAddHouseHoldData : " + isAddHouseHoldData + "-- isUpdateHouseHoldData "  + isUpdateHouseHoldData );
        // -----------------------------------------------------------------------------
        // Prepare Patient DataValues
        // -----------------------------------------------------------------------------

        Constant programConstant = constantService.getConstantByName( OVC_SCHOOL_MANAGEMENT_PROGRAM );
        Constant programStageConstant = constantService.getConstantByName( OVC_SCHOOL_MANAGEMENT_PROGRAM_STAGE );
        
        // program and programStage Related information
        Program program = programService.getProgram( (int) programConstant.getValue() );

        ProgramStage programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        
        
        for( Patient patient : ovcList )
        {
            //System.out.println( " Inside ovcList " + patient.getFullName() );
            
            Integer programInstanceId = ovcService.getProgramInstanceId( patient.getId(), program.getId() );
            
            Integer programStageInstanceId = null;

            if ( programInstanceId != null )
            {
                programStageInstanceId = ovcService.getProgramStageInstanceId( programInstanceId, programStage.getId(), period.getStartDateString() );
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

            String value = null;

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
                    value = request.getParameter( PREFIX_DATAELEMENT + patient.getId() + ":" + programStageDataElement.getDataElement().getId() );

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
        
        return SUCCESS;
    }
    
}
