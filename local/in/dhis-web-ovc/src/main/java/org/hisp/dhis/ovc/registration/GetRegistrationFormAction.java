package org.hisp.dhis.ovc.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetRegistrationFormAction implements Action
{
    public static final String OVC_MONTHLY_VISIT = "OVC Monthly Visit";
    public static final String OVC_PROG_CONSTANT = "OVC_PROGRAM_ID";
    public static final String OVC_PROG_STAGE_CONSTANT = "OVC_REGISTRATION_STAGE_ID";
    public static final String OVC_EMP_JOB_TITLE_CHV = "CHV";
    public static final String OVC_EMP_JOB_TITLE_SUPERVISIOR = "Supervisior";
    
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    /*
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    */
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    /*
    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    */
    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }
    
    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }
    
    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    /*
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private List<Employee> employeeListCHV;
    
    public List<Employee> getEmployeeListCHV()
    {
        return employeeListCHV;
    }
    
    private List<Employee> employeeListSupervisior;
    
    public List<Employee> getEmployeeListSupervisior()
    {
        return employeeListSupervisior;
    }

    private String status;
    
    public String getStatus()
    {
        return status;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    /*
    private int orgUnitId;
    
    public int getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    */
    
    private Map<Integer, PatientAttribute> patientAttributeMap;
    
    public Map<Integer, PatientAttribute> getPatientAttributeMap()
    {
        return patientAttributeMap;
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
    
    private List<Location> locations = new ArrayList<Location>();
    
    public List<Location> getLocations()
    {
        return locations;
    }
    
    private List<School> schools = new ArrayList<School>();
    
    public List<School> getSchools()
    {
        return schools;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    
    public String execute()
    {
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        //organisationUnit.getParent().getParent().getParent().getName()
        
        //System.out.println( "County : "  + organisationUnit.getParent().getParent().getParent().getName() );
        
        //System.out.println( "District : "  + organisationUnit.getParent().getParent().getName() );
        
        //System.out.println( "Division : "  + organisationUnit.getParent().getName() );
        
        status = i18n.getString( "none" );
        
        program = programService.getProgramByName( OVC_MONTHLY_VISIT );
        
        if ( ( organisationUnit == null ) || ( !program.getOrganisationUnits().contains( organisationUnit ) ) )
        {
            
            status = i18n.getString( "please_select_cbo" );

            return SUCCESS;
        }

        //orgUnitId = organisationUnit.getId();
        
        
        employeeListCHV = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnitAndJobTitle( organisationUnit, OVC_EMP_JOB_TITLE_CHV ) );
        
        employeeListSupervisior = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnitAndJobTitle( organisationUnit, OVC_EMP_JOB_TITLE_SUPERVISIOR ) );
        
        //locations = new ArrayList<Location> ( locationService.getLocationsByParentOrganisationUnit( organisationUnit ) );
        
        locations = new ArrayList<Location> ( locationService.getActiveLocationsByParentOrganisationUnit( organisationUnit ) );
        
        schools = new ArrayList<School> ( schoolService.getSchoolByOrganisationUnit( organisationUnit ) );
        
        //organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        //Constant programConstant = constantService.getConstantByName( OVC_PROG_CONSTANT );
        //Constant programStageConstant = constantService.getConstantByName( OVC_PROG_STAGE_CONSTANT );
        
        // -----------------------------------------------------------------------------
        // Prepare Patient Attributes
        // -----------------------------------------------------------------------------
        patientAttributeMap = new HashMap<Integer, PatientAttribute>();
        
        List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );          
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeMap.put( patientAttribute.getId(), patientAttribute );
        }
       
        // Program and Program Stage

        /*
        program = programService.getProgram( (int) programConstant.getValue() );
        
        programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        
        programStageDataElements =  new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        //programStageDataElements = programStage.getProgramStageDataElements();
        
        
        // DataElements
        dataElementMap = new HashMap<Integer, DataElement>();
        
        //List<DataElement> dataElements =  new ArrayList<DataElement>( dataElementService.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_PATIENT ) );
        
        if( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                dataElementMap.put( programStageDataElement.getDataElement().getId(), programStageDataElement.getDataElement() );
            }
        }
        */
        
        //System.out.println( "Program Name " + program.getName() + " Program Stage Name is " + programStage.getName() );
        
        //System.out.println( " DataElement Size is  " + programStageDataElements.size() );
        
        //System.out.println( "Size of Patient Attributes " + patientAttributes.size() + " DataElement Size is  " + programStageDataElements.size() );
        
        return SUCCESS;
    }
}
