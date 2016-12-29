package org.hisp.dhis.ovc.caregiver;

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
import org.hisp.dhis.organisationunit.OrganisationUnit;
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
public class LoadOVCCareGiverDataEntryFormAction implements Action
{

    //public static final String OVC_MONTHLY_VISIT_PROGRAM = "OVC Monthly Visit Program";// 433.0

    //public static final String OVC_MONTHLY_VISIT_PROGRAM_STAGE = "OVC Monthly Visit Program Stage";// 708.0
    
    public static final String OVC_CAREGIVER_PROGRAM_ID = "OVC_CAREGIVER_PROGRAM_ID";// 1965.0 
    public static final String OVC_CAREGIVER_PROGRAM_STAGE_ID = "OVC_CAREGIVER_PROGRAM_STAGE_ID";// 1966.0

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

    /*
     * private ProgramInstanceService programInstanceService;
     * 
     * public void setProgramInstanceService( ProgramInstanceService
     * programInstanceService ) { this.programInstanceService =
     * programInstanceService; }
     * 
     * private ProgramStageInstanceService programStageInstanceService;
     * 
     * public void setProgramStageInstanceService( ProgramStageInstanceService
     * programStageInstanceService ) { this.programStageInstanceService =
     * programStageInstanceService; }
     */
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

    private Integer programInstanceId;

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
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

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Integer programStageInstanceId;

    public Integer getProgramStageInstanceId()
    {
        return programStageInstanceId;
    }

    public Map<Integer, String> patientDataValueMap;

    public Map<Integer, String> getPatientDataValueMap()
    {
        return patientDataValueMap;
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

    private Period period;

    public Period getPeriod()
    {
        return period;
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
    
    private Employee employee = new Employee();
    
    public Employee getEmployee()
    {
        return employee;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute()
    {
        period = PeriodType.createPeriodExternalId( selectedPeriodId );

        period.setName( format.formatPeriod( period ) );


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
        
        
        // CHV Details
        
        employee = employeeService.getEmployeeByCode( patientAttributeValueMap.get( 43 ) );
       
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

        // System.out.println( patient.getOrganisationUnit().getName() );

        Constant programConstant = constantService.getConstantByName( OVC_CAREGIVER_PROGRAM_ID );
        Constant programStageConstant = constantService.getConstantByName( OVC_CAREGIVER_PROGRAM_STAGE_ID );
        
        int programId = 1965;
        if ( programConstant != null )
        {
            programId = (int) programConstant.getValue();
        }

        int programStageId = 1966;
        if ( programStageConstant != null )
        {
            programStageId = (int) programStageConstant.getValue();
        }

        // program and programStage Related information
        program = programService.getProgram( programId );

        programStage = programStageService.getProgramStage( programStageId );

        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );

        // Program stage DataElements
        dataElementMap = new HashMap<Integer, DataElement>();

        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                dataElementMap.put( programStageDataElement.getDataElement().getId(), programStageDataElement.getDataElement() );
            }
        }

       
        if ( programInstanceId != null )
        {
            programStageInstanceId = ovcService.getProgramStageInstanceId( programInstanceId, programStage.getId(), period.getStartDateString() );
        }
        
        patientDataValueMap = ovcService.getDataValueFromPatientDataValue( programStageInstanceId );

        return SUCCESS;
    }

}