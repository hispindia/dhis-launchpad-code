package org.hisp.dhis.ovc.caregiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowOVCCareGiverFormAction implements Action
{
    public static final String OVC_CAREGIVER_PROGRAM_ID = "OVC_CAREGIVER_PROGRAM_ID";// 1965.0 
    
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

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private OVCService ovcService;

    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------

    private String selPeriod;

    public String getSelPeriod()
    {
        return selPeriod;
    }

    public void setSelPeriod( String selPeriod )
    {
        this.selPeriod = selPeriod;
    }

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

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    private Integer programInstanceId;

    public Integer getProgramInstanceId()
    {
        return programInstanceId;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
/*
    private List<Period> tempPeriods = new ArrayList<Period>();

    public List<Period> getTempPeriods()
    {
        return tempPeriods;
    }
*/
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()

    {
        patient = patientService.getPatient( id );
        
        String periodTypeName = QuarterlyPeriodType.NAME;
        
        /*
        
        */
        
        //FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );

        //Collections.reverse( periods );
        
        
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );
        
        //periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );
        
        periods.addAll( periodService.getPeriodsByPeriodType( periodType ) );
        
        if( periods.size() == 0 )
        {
            CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );
            
            Calendar cal = PeriodType.createCalendarInstance();
            
            periods = _periodType.generatePeriods( cal.getTime() );
        }
        
        /*
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
            System.out.println( " First period : " + period.getName() + ": " + period.getStartDateString() + " : "+ period.getId() );
        }
        */
        
        
        //Collections.reverse( periods );
        
        Date registrationDate = new Date();
        
        registrationDate = patient.getRegistrationDate();
        
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
      
        if ( registrationDate == null )
        {
            registrationDate = new Date();
        }
        else
        {
            registrationDate = patient.getRegistrationDate();
        }

        String approvedDateStr = patientAttributeValueMap.get( 405 );

        Date approvedDate = new Date();
        if ( approvedDateStr == null )
        {
            approvedDate = patient.getRegistrationDate();
        }
        else
        {
            approvedDate = format.parseDate( approvedDateStr );
        }
        
        
        Iterator<Period> periodIterator = periods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            if(  p1.getStartDate().before( approvedDate ) )
            {
                periodIterator.remove();
            }
            
            /*
            if( ( p1.getStartDate().before( approvedDate ) ) && ( p1.getStartDate().compareTo( new Date() ) > 0 ) )
            {
                periodIterator.remove();
            }
            */
            
            /*
            if( registrationDate.after(  p1.getStartDate() ) && registrationDate.before( p1.getEndDate() ) )
            {
                
            }
            else
            {
                periodIterator.remove();
            }
            */
            
        }
        
        /*
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
            System.out.println( " Second period : " + period.getName() + ": " + period.getStartDateString() + " : "+ period.getId() );
        }
        */
        
        Iterator<Period> periodIterator1 = periods.iterator();
        while( periodIterator1.hasNext() )
        {
            Period p2 = periodIterator1.next();
            if(  p2.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator1.remove();
            }
        }
        
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
            //System.out.println( " Final period : " + period.getName() + ": " + period.getStartDateString() + " : "+ period.getId() );
        }
        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute()
                .getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getValue() );
            }
        }

        // -------------------------------------------------------------------------
        // Get Visit Dates
        // -------------------------------------------------------------------------
        
      /*
        String approvedDateStr = patientAttributeValueMap.get( 405 );

        Date approvedDate = new Date();
        if ( approvedDateStr == null )
        {
            approvedDate = patient.getRegistrationDate();
        }
        else
        {
            approvedDate = format.parseDate( approvedDateStr );
        }

        periods = ovcService.getMontlyPeriods( approvedDate, new Date() );

        Collections.reverse( periods );

        if ( selPeriod != null )
        {
            int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

            int curMonth = Integer.parseInt( selPeriod.split( "-" )[1] );
            int curYear = Integer.parseInt( selPeriod.split( "-" )[0] );
            int curMonthDays = monthDays[curMonth];

            if ( curMonth == 2 && curYear % 4 == 0 )
            {
                curMonthDays++;
            }
            selPeriod = "Monthly_" + selPeriod + "_" + curYear + "-" + selPeriod.split( "-" )[1] + "-" + curMonthDays;
        }

       */
        
        Constant programConstant = constantService.getConstantByName( OVC_CAREGIVER_PROGRAM_ID );
        
        int programId = 1965;
        if ( programConstant != null )
        {
            programId = (int) programConstant.getValue();
        }

        program = programService.getProgram( programId );

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

        // System.out.println( " Program Instance Id is : " + programInstanceId );

        return SUCCESS;
    }
}
