package org.hisp.dhis.asha.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
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

public class ShowAddASHAAttributesFormAction implements Action
{
    public static final String ASHA_TRAINING_ATTRIBUTE = "ASHA Training Status";
    
    public static final String ASHA_SKILL_ATTRIBUTE = "Skill";
    
    public static final String ASHA_IEC_ATTRIBUTE = "IEC Material and Drug Kit Information";
    
    public static final String ASHA_PERFORMANCE_ATTRIBUTE = "Performance";
    
    private final String TRANING = "training";

    private final String SKILLS = "Skills";
    
    private final String IEC = "iec";
    
    private final String PERFORMANCE = "performance";
    
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
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
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
    
    private List<PatientAttributeGroup> attributeGroups;

    public List<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }
    
    private Map<Integer, Collection<PatientAttribute>> attributeGroupsMap = new HashMap<Integer, Collection<PatientAttribute>>();

    public Map<Integer, Collection<PatientAttribute>> getAttributeGroupsMap()
    {
        return attributeGroupsMap;
    }
    
    private Map<Integer, PatientAttribute> patientAttributeMap;
    
    public Map<Integer, PatientAttribute> getPatientAttributeMap()
    {
        return patientAttributeMap;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        
        patient = patientService.getPatient( id );
        
        attributeGroup = new PatientAttributeGroup();
        
        attributeGroups = new ArrayList<PatientAttributeGroup>();
        
        // Attribute Group
        
        if ( attributeGroupName.equalsIgnoreCase( TRANING ))
        {
            
            attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_TRAINING_ATTRIBUTE );
        }
        
        
        else if ( attributeGroupName.equalsIgnoreCase( SKILLS ) )
        {
            //attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_SKILL_ATTRIBUTE );
            
            OptionSet optionSet = optionService.getOptionSetByName( SKILLS );
            
            for( String optionName : optionSet.getOptions() )
            {
                attributeGroups.add( patientAttributeGroupService.getPatientAttributeGroup( Integer.parseInt( optionName ) ) );
            }
            
            //attributeGroups = new ArrayList<PatientAttributeGroup>( patientAttributeGroupService.getAllPatientAttributeGroups() );
            
            //Collections.sort( attributeGroups, new PatientAttributeGroupSortOrderComparator() );
            
            
            for ( PatientAttributeGroup attributeGroup : attributeGroups )
            {
                List<PatientAttribute> attributes = patientAttributeGroupService.getPatientAttributes( attributeGroup );

                if ( attributes.size() > 0 )
                {
                    attributeGroupsMap.put( attributeGroup.getId(), attributes );
                    //System.out.println( attributeGroup.getId() + " : " + attributeGroup.getName() + " : " + attributes.size() );
                }
            }
        }
        
        else if ( attributeGroupName.equalsIgnoreCase( IEC ) )
        {
            attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_IEC_ATTRIBUTE );
        }
        
        else if ( attributeGroupName.equalsIgnoreCase( PERFORMANCE ) )
        {
            attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_PERFORMANCE_ATTRIBUTE );
        }
        
        
        patientAttributeMap = new HashMap<Integer, PatientAttribute>();
        
        List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
        //List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( attributeGroup.getAttributes() );
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeMap.put( patientAttribute.getId(), patientAttribute );
        }
        
        
        
        
        
        
        
        
        //attributeGroupId = attributeGroup.getId();
        
        
        //attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( attributeGroupName );
        
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
