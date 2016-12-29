package org.hisp.dhis.asha.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;



/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowUpdateASHAProfileFormAction implements Action
{
    public static final String ASHA_PROFILE_ATTRIBUTE = "ASHA Profile";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output / Getter and Setter 
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

    private String attributeGroupName;
    
    public String getAttributeGroupName()
    {
        return attributeGroupName;
    }


    public void setAttributeGroupName( String attributeGroupName )
    {
        this.attributeGroupName = attributeGroupName;
    }
    
    private Integer attributeGroupId;
    
    public Integer getAttributeGroupId()
    {
        return attributeGroupId;
    }

    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }

    private String systemIdentifier;
    
    public String getSystemIdentifier()
    {
        return systemIdentifier;
    }
    
    private Map<Integer, String> identiferMap;
    
    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    private PatientAttributeGroup attributeGroup;
    
    public PatientAttributeGroup getAttributeGroup()
    {
        return attributeGroup;
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
    
    private List<Location> locations = new ArrayList<Location>();
    
    public List<Location> getLocations()
    {
        return locations;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        
        patient = patientService.getPatient( id );
        
        attributeGroup = new PatientAttributeGroup();
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_PROFILE_ATTRIBUTE );
        
        organisationUnit = patient.getOrganisationUnit();
        
        locations = new ArrayList<Location> ( locationService.getActiveLocationsByParentOrganisationUnit( organisationUnit ) );
        
        /*
        System.out.println( " Attribute Size is "   + attributeGroup.getAttributes().size() );
        
        for ( PatientAttribute attribute : attributeGroup.getAttributes() )
        {
            System.out.println( " Attribute Id is "   + attribute.getId()   + "  -- Attribute Name is : " + attribute.getName() );
        }
        */
        // -------------------------------------------------------------------------
        // Get identifier
        // -------------------------------------------------------------------------
        
        PatientIdentifierType idType = null;
        identiferMap = new HashMap<Integer, String>();
        
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
        
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_PROFILE_ATTRIBUTE );
        
        
        patientAttributeMap = new HashMap<Integer, PatientAttribute>();
        
        //List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
        List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( attributeGroup.getAttributes() );
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeMap.put( patientAttribute.getId(), patientAttribute );
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
