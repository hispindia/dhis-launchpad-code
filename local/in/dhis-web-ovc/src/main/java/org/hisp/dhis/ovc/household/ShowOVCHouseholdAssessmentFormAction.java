package org.hisp.dhis.ovc.household;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
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
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;


/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowOVCHouseholdAssessmentFormAction implements Action
{
    public static final String OVC_HOUSEHOLD_ASSESSMENT_PROGRAM = "OVC Household Assessment Program";//820.0
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
    
    private int id;

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
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
    
    /*
    private String selHalfYearlyPeriod;

    public String getSelHalfYearlyPeriod()
    {
        return selHalfYearlyPeriod;
    }

    public void setSelHalfYearlyPeriod( String selHalfYearlyPeriod )
    {
        this.selHalfYearlyPeriod = selHalfYearlyPeriod;
    }
    */
    
    boolean isAddHouseHoldData = false;
    boolean isUpdateHouseHoldData = false;
    
    /*
    private boolean isAddHouseHoldData;
    
    public boolean isAddHouseHoldData()
    {
        return isAddHouseHoldData;
    }
    
    private boolean isUpdateHouseHoldData;
    
    public boolean isUpdateHouseHoldData()
    {
        return isUpdateHouseHoldData;
    }
    */
    
    private String addPrivilege;
    
    public String getAddPrivilege()
    {
        return addPrivilege;
    }
    
    private String updatePrivilege;
    
    public String getUpdatePrivilege()
    {
        return updatePrivilege;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        
        patient = patientService.getPatient( id );
        
        Date registrationDate = new Date();
        
        registrationDate = patient.getRegistrationDate();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String regDate = simpleDateFormat.format( patient.getRegistrationDate() );
        
        int year = Integer.parseInt( regDate.split( "-" )[0] );
        
        if ( registrationDate == null )
        {
            registrationDate = new Date();
        }
        else
        {
            registrationDate = patient.getRegistrationDate();
        }
        
        
        String firstHalfYearlyStartDate = year +"-01-01";
        Date h1StartDate = format.parseDate( firstHalfYearlyStartDate );
        
        String firstHalfYearlyEndDate = year +"-06-30";
        Date h1EndDate = format.parseDate( firstHalfYearlyEndDate );
        
        String secondHalfYearlyStartDate = year +"-07-01";
        Date h2StartDate = format.parseDate( secondHalfYearlyStartDate );
        
        String secondHalfYearlyEndDate = year +"-12-31";
        Date h2EndDate = format.parseDate( secondHalfYearlyEndDate );
        
        
        Period regDateHYPeriod = new Period();
        
        if( registrationDate.after(  h1StartDate ) && registrationDate.before( h1EndDate ) )
        {
            regDateHYPeriod.setStartDate( h1StartDate );
            regDateHYPeriod.setEndDate( h1EndDate );
        }
        else
        {
            regDateHYPeriod.setStartDate( h2StartDate );
            regDateHYPeriod.setEndDate( h2EndDate );
        }        
        
        
        String periodTypeName = SixMonthlyPeriodType.NAME;

        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );

        Calendar cal = PeriodType.createCalendarInstance();

        periods = _periodType.generatePeriods( cal.getTime() );

        // periods = new ArrayList<Period>(
        // periodService.getPeriodsByPeriodType( periodType ) );

        //FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );

        // Collections.reverse( periods );

       // System.out.println( " Registration Date is : " + registrationDate  );
        
        /*
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
            
            System.out.println( "--Before Iterator--" );
            
            System.out.println( " Period Start Date  is : " + period.getStartDate() + " Period End Date  is : " + period.getEndDate() + " -- Period Name is " + period.getName() );
        }
        */
        
        
        //System.out.println( " regDate HYPeriod Start Date  is : " + regDateHYPeriod.getStartDate() + " regDateHYPeriod End Date  is : " + regDateHYPeriod.getEndDate()  );
        
        Iterator<Period> periodIterator = periods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( regDateHYPeriod.getStartDate() ) < 0  )
            {
                //System.out.println( " inside Remove 1 : " + p1.getStartDate() + " --- " + regDateHYPeriod.getStartDate() );
                periodIterator.remove();
            }
            
            else if ( p1.getEndDate().compareTo(  regDateHYPeriod.getEndDate() ) > 0 )
            {
                //System.out.println( " inside Remove 2 : " + p1.getEndDate() + " --- " + regDateHYPeriod.getEndDate() );
                periodIterator.remove();
            }
            
            
            //System.out.println( " inside not Remove : " + p1.getStartDate() + " --- " + p1.getEndDate() );
            
            
            /*
            
            if ( ( p1.getStartDate().before( regDateHYPeriod.getStartDate() ) ) )
            {
                periodIterator.remove( );
            }
            else if ( ( p1.getEndDate().after( new Date() ) ) )
            {
                periodIterator.remove( );
            }
            */
            
            /*
            if ( !( ( p1.getStartDate().compareTo( registrationDate ) >= 0 ) && ( p1.getEndDate().compareTo( registrationDate ) < 0 ) )  )
            {
                periodIterator.remove( );
            }
            */
        }
        
        
        
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
            
            //System.out.println( "--After Iterator--" );
            
            //System.out.println( " Period Start Date  is : " + period.getStartDate() + " Period End Date  is : " + period.getEndDate() + " -- Period Name is " + period.getName() );
        }
        
        
        //selHalfYearlyPeriod = "SixMonthly_" + simpleDateFormat.format( regDateHYPeriod.getStartDate() )+ "_" + simpleDateFormat.format( regDateHYPeriod.getEndDate() );
        
        /*
        if ( selHalfYearlyPeriod != null )
        {
            String sDate = simpleDateFormat.format( regDateHYPeriod.getStartDate() );
            String eDate = simpleDateFormat.format( regDateHYPeriod.getEndDate() );
            
            selHalfYearlyPeriod = "SixMonthly_" + sDate + "_" + eDate;
            
            //System.out.println( selHalfYearlyPeriod );
        }
        */
        
        // Enroll OVC Household Assessment Program
         
        Constant programConstant = constantService.getConstantByName( OVC_HOUSEHOLD_ASSESSMENT_PROGRAM );
        
        program = programService.getProgram( (int) programConstant.getValue() );

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
        
        User user = currentUserService.getCurrentUser();
        
        addPrivilege = "no";
        updatePrivilege = "no";
        for( String authority : user.getUserCredentials().getAllAuthorities() )
        {
            if( authority.equalsIgnoreCase( "F_OVC_HOUSEHOLD_ASSESSMENT_ADD" ))
            {
                isAddHouseHoldData = true;
                addPrivilege = "yes";
            }
            
            if( authority.equalsIgnoreCase( "F_OVC_HOUSEHOLD_ASSESSMENT_UPDATE" ))
            {
                isUpdateHouseHoldData = true;
                updatePrivilege = "yes";
            }
            
        }
        
        
        //System.out.println( " isAddHouseHoldData : " + isAddHouseHoldData + "-- isUpdateHouseHoldData "  + isUpdateHouseHoldData );
        
        return SUCCESS;
        
    }
}
