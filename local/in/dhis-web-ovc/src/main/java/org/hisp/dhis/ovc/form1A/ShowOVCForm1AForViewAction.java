package org.hisp.dhis.ovc.form1A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.ovc.registration.GetRegistrationFormAction;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowOVCForm1AForViewAction
    implements Action
{
    public static final String OVC_MONTHLY_VISIT_PROGRAM = "OVC Monthly Visit Program";// 433.0

    public static final String OVC_MONTHLY_VISIT_PROGRAM_STAGE = "OVC Monthly Visit Program Stage";// 708.0

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
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

    private String selPeriod;

    public String getSelPeriod()
    {
        return selPeriod;
    }

    public void setSelPeriod( String selPeriod )
    {
        this.selPeriod = selPeriod;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    public Map<Integer, String> patientDataValueMap;

    public Map<Integer, String> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }

    private List<Employee> employeeListCHV;

    public List<Employee> getEmployeeListCHV()
    {
        return employeeListCHV;
    }

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    private Map<Integer, String> identiferMap;

    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    private String systemIdentifier;

    public String getSystemIdentifier()
    {
        return systemIdentifier;
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

    private Collection<ProgramStageDataElement> programStageDataElements;

    public Collection<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }

    private Map<Integer, DataElement> dataElementMap;

    public Map<Integer, DataElement> getDataElementMap()
    {
        return dataElementMap;
    }

    private Period period;

    public Period getPeriod()
    {
        return period;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        patient = patientService.getPatient( id );

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

        patientDataValueMap = new HashMap<Integer, String>();

        patient = patientService.getPatient( id );

        employeeListCHV = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnitAndJobTitle( patient
            .getOrganisationUnit(), GetRegistrationFormAction.OVC_EMP_JOB_TITLE_CHV ) );

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
        // Get PatientIdentifierType data
        // -------------------------------------------------------------------------

        identiferMap = new HashMap<Integer, String>();

        PatientIdentifierType idType = null;

        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            idType = identifier.getIdentifierType();

            if ( idType != null )
            {
                identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
            }
            else
            {
                systemIdentifier = identifier.getIdentifier();
            }
        }

        Constant programConstant = constantService.getConstantByName( OVC_MONTHLY_VISIT_PROGRAM );
        Constant programStageConstant = constantService.getConstantByName( OVC_MONTHLY_VISIT_PROGRAM_STAGE );

        // program and programStage Related information
        program = programService.getProgram( (int) programConstant.getValue() );

        programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );

        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );

        // Program stage DataElements
        dataElementMap = new HashMap<Integer, DataElement>();

        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                dataElementMap.put( programStageDataElement.getDataElement().getId(), programStageDataElement
                    .getDataElement() );
            }
        }

        period = PeriodType.createPeriodExternalId( selPeriod );

        period.setName( format.formatPeriod( period ) );

        Integer programInstanceId = ovcService.getProgramInstanceId( patient.getId(), program.getId() );

        Integer programStageInstanceId = ovcService.getProgramStageInstanceId( programInstanceId, programStage.getId(),
            period.getStartDateString() );

        patientDataValueMap = ovcService.getDataValueFromPatientDataValue( programStageInstanceId );

        return SUCCESS;
    }
}
