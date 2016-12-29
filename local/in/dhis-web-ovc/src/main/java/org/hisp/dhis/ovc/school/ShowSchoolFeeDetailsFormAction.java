package org.hisp.dhis.ovc.school;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowSchoolFeeDetailsFormAction implements Action
{
    public static final String OVC_SCHOOL_MANAGEMENT_PROGRAM = "OVC_SCHOOL_MANAGEMENT_PROGRAM";//1700.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
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
    
    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
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

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
    
    private School school;
    
    public School getSchool()
    {
        return school;
    }
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    private Integer programInstanceId;

    public Integer getProgramInstanceId()
    {
        return programInstanceId;
    }
    
    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }
    
    private List<Patient> ovcList = new ArrayList<Patient>();
    
    public List<Patient> getOvcList()
    {
        return ovcList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute()
    {
        school = schoolService.getSchool( id );
        
        ovcList = new ArrayList<Patient>( school.getPatients() );
        
        // Period related Information
        String periodTypeName = QuarterlyPeriodType.NAME;
        
        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );
        
        Calendar cal = PeriodType.createCalendarInstance();
        
        periods = _periodType.generatePeriods( cal.getTime() );
        
        //FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );

        //Collections.reverse( periods );

        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }

        
        // Enroll OVC SCHOOL MANAGEMENT PROGRAM
         
        Constant programConstant = constantService.getConstantByName( OVC_SCHOOL_MANAGEMENT_PROGRAM );
        
        program = programService.getProgram( (int) programConstant.getValue() );

        for( Patient patient : ovcList )
        {
            Integer programInstanceId = ovcService.getProgramInstanceId( patient.getId(), program.getId() );
            
            if ( programInstanceId == null )
            {
                Patient createdPatient = patientService.getPatient( patient.getId() );

                Date programEnrollDate = new Date();

                int programType = program.getType();
                ProgramInstance programInstance = null;

                if ( programType == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
                {
                    programInstance = new ProgramInstance();
                    programInstance.setEnrollmentDate( programEnrollDate );
                    programInstance.setDateOfIncident( programEnrollDate );
                    programInstance.setProgram( program );
                    programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );

                    programInstance.setPatient( createdPatient );
                    createdPatient.getPrograms().add( program );
                    patientService.updatePatient( createdPatient );

                    programInstanceId = programInstanceService.addProgramInstance( programInstance );

                }
            }
            
        }
        
        
        /*
        programInstanceId = ovcService.getProgramInstanceId( patient.getId(), program.getId() );

        if ( programInstanceId == null )
        {
            Patient createdPatient = patientService.getPatient( patient.getId() );

            Date programEnrollDate = new Date();

            int programType = program.getType();
            ProgramInstance programInstance = null;

            if ( programType == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
            {
                programInstance = new ProgramInstance();
                programInstance.setEnrollmentDate( programEnrollDate );
                programInstance.setDateOfIncident( programEnrollDate );
                programInstance.setProgram( program );
                programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );

                programInstance.setPatient( createdPatient );
                createdPatient.getPrograms().add( program );
                patientService.updatePatient( createdPatient );

                programInstanceId = programInstanceService.addProgramInstance( programInstance );

            }
        }
        */
        
        return SUCCESS;
        
    }
}
