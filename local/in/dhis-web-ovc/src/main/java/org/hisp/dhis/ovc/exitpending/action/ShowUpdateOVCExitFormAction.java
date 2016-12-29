package org.hisp.dhis.ovc.exitpending.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.ovc.registration.GetRegistrationFormAction;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowUpdateOVCExitFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
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
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
 
    public String execute()
    {
        //System.out.println( " Inside update form action " + id );
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
       
        employeeListSupervisior = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnitAndJobTitle( organisationUnit, GetRegistrationFormAction.OVC_EMP_JOB_TITLE_SUPERVISIOR ) );
        
        //locations = new ArrayList<Location> ( locationService.getLocationsByParentOrganisationUnit( organisationUnit ) );
        
        locations = new ArrayList<Location> ( locationService.getActiveLocationsByParentOrganisationUnit( organisationUnit ) );
        
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

        patient = patientService.getPatient( id );
        
        
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
                identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
            }
            else
            {
                systemIdentifier = identifier.getIdentifier();
            }
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

