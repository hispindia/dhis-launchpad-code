package org.hisp.dhis.ovc.form1A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowOVCForm1AAction_old
    implements Action
{
    public static final String OVC_MONTHLY_VISIT_PROGRAM = "OVC Monthly Visit Program";// 433.0

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
        
        /*
        String periodTypeName = MonthlyPeriodType.NAME;

        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );

        Calendar cal = PeriodType.createCalendarInstance();

        periods = _periodType.generatePeriods( cal.getTime() );

        // periods = new ArrayList<Period>(
        // periodService.getPeriodsByPeriodType( periodType ) );

        FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );

        // Collections.reverse( periods );

        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }
        */
        
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

        // periods.retainAll( tempPeriods );

        Collections.reverse( periods );

        // Collections.sort( periods );

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

        Constant programConstant = constantService.getConstantByName( OVC_MONTHLY_VISIT_PROGRAM );
        int programId = 433;
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

        // System.out.println( " Program Instance Id is : " + programInstanceId
        // );

        return SUCCESS;
    }
}