package org.hisp.dhis.ovc.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowUpdateOVCRegistrationFormAction implements Action
{
    public static final String OVC_ID = "OVC_ID";//929.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    /*
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    */
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
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
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
    
    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }
    
    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    
    /*
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    // -------------------------------------------------------------------------
    // Input/output and getter and setter
    // -------------------------------------------------------------------------
    


    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }
    
    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
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

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
    
    private Map<Integer, PatientAttribute> patientAttributeMap;
    
    public Map<Integer, PatientAttribute> getPatientAttributeMap()
    {
        return patientAttributeMap;
    }
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private int orgUnitId;
    
    public int getOrgUnitId()
    {
        return orgUnitId;
    }
    
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
    
    private List<Location> locations = new ArrayList<Location>();
    
    public List<Location> getLocations()
    {
        return locations;
    }
    
    private String ovcId;
    
    public String getOvcId()
    {
        return ovcId;
    }
    
    private String approveInsideDashBoard;
    
    public String getApproveInsideDashBoard()
    {
        return approveInsideDashBoard;
    }

    public void setApproveInsideDashBoard( String approveInsideDashBoard )
    {
        this.approveInsideDashBoard = approveInsideDashBoard;
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
        //System.out.println( " Inside update form action " + id );
        
        patient = patientService.getPatient( id );
        
        organisationUnit = patient.getOrganisationUnit();
        
        //organisationUnit = selectionManager.getSelectedOrganisationUnit();
       
        //orgUnitId = organisationUnit.getId();
        
        employeeListCHV = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnitAndJobTitle( organisationUnit, GetRegistrationFormAction.OVC_EMP_JOB_TITLE_CHV ) );
        
        employeeListSupervisior = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnitAndJobTitle( organisationUnit, GetRegistrationFormAction.OVC_EMP_JOB_TITLE_SUPERVISIOR ) );
        
        //locations = new ArrayList<Location> ( locationService.getLocationsByParentOrganisationUnit( organisationUnit ) );
        
        locations = new ArrayList<Location> ( locationService.getActiveLocationsByParentOrganisationUnit( organisationUnit ) );
        schools = new ArrayList<School> ( schoolService.getSchoolByOrganisationUnit( organisationUnit ) );
        
        // -----------------------------------------------------------------------------
        // Prepare Patient Attributes
        // -----------------------------------------------------------------------------
        patientAttributeMap = new HashMap<Integer, PatientAttribute>();
        
        List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );          
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeMap.put( patientAttribute.getId(), patientAttribute );
        }
   
        // -------------------------------------------------------------------------
        // Get Patient Object
        // -------------------------------------------------------------------------

        
        
        
        //System.out.println( " Patient orgUnit " + patient.getOrganisationUnit().getName() );
        
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
                //identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
            }
            else
            {
                systemIdentifier = identifier.getIdentifier();
            }
        }
        
        
        Constant patientIdentifierTypeConstant = constantService.getConstantByName( OVC_ID );
        
        PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( (int) patientIdentifierTypeConstant.getValue() );
        
        if ( organisationUnit.getCode() != null && identifierType != null )
        {
            PatientIdentifier ovcIdIdentifier = patientIdentifierService.getPatientIdentifier( identifierType, patient );
            
            if( ovcIdIdentifier != null )
            {
                ovcId =  "OVC Id : " + ovcIdIdentifier.getIdentifier() ;
            }
            else
            {
                ovcId =  "System Generated Id: " + systemIdentifier ; 
            }
            
        }
        else
        {
            ovcId =  "System Generated Id: " + systemIdentifier ;
        }
        
        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------
        
        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute().getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getValue() );
            }
        }
        
        return SUCCESS;
    }

}
