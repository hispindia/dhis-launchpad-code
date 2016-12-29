package org.hisp.dhis.asha.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */


public class UpdateASHAAttributesAction implements Action
{ 
    private final String SKILLS = "Skills";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private PatientAttributeService patientAttributeService;
    
    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    public PatientAttributeGroupService getPatientAttributeGroupService()
    {
        return patientAttributeGroupService;
    }
    
    private PatientAttributeOptionService patientAttributeOptionService;
    
    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
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
    
    private int attributeGroupId;
    
    public void setAttributeGroupId( int attributeGroupId )
    {
        this.attributeGroupId = attributeGroupId;
    }
    
    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }

    private PatientAttributeGroup attributeGroup;
    
    public PatientAttributeGroup getAttributeGroup()
    {
        return attributeGroup;
    }
    
    private List<PatientAttributeGroup> attributeGroups;

    public List<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }
    
    private List<PatientAttribute> attributes;
    
    public List<PatientAttribute> getAttributes()
    {
        return attributes;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        attributeGroups = new ArrayList<PatientAttributeGroup>();
        
        attributes = new ArrayList<PatientAttribute>();
        
        patient = patientService.getPatient( id );
        
        //System.out.println( "Attribute Group Id is : "  + attributeGroupId );

        
        // -----------------------------------------------------------------------------
        // Prepare Patient Attributes
        // -----------------------------------------------------------------------------
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        String value = null;
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroup( attributeGroupId );
        
        OptionSet optionSet = optionService.getOptionSetByName( SKILLS );
        
        if ( attributeGroup == null && optionSet != null )
        {
            for( String optionName : optionSet.getOptions() )
            {
                attributeGroups.add( patientAttributeGroupService.getPatientAttributeGroup( Integer.parseInt( optionName ) ) );
            }
            
            for ( PatientAttributeGroup attributeGroup : attributeGroups )
            {
                List<PatientAttribute> attributeGroupMemberList = new ArrayList<PatientAttribute>( attributeGroup.getAttributes() );
                   
                attributes.addAll( attributeGroupMemberList );
            }
            
            //System.out.println( " Attributes Size is : "  + attributes.size() );
        }
        
        else
        {
            attributes = new ArrayList<PatientAttribute>( attributeGroup.getAttributes() );
            
            //System.out.println( " Attributes Size is : "  + attributes.size() );
            
        }
        
        
        
        //List<PatientAttributeValue> valuesForSave = new ArrayList<PatientAttributeValue>();
        //List<PatientAttributeValue> valuesForUpdate = new ArrayList<PatientAttributeValue>();
        //Collection<PatientAttributeValue> valuesForDelete = null;
        
        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();
        
        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            //System.out.println( " Inside Update " );
            
            //patient.getAttributes().clear();
            
            //valuesForDelete = patientAttributeValueService.getPatientAttributeValues( patient );

            for ( PatientAttribute attribute : attributes )
            {
                value = request.getParameter( AddASHAAction.PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, attribute );
                    
                    //attributeValue.setPatient( patient );
                    
                    if ( !patient.getAttributes().contains( attribute ) )
                    {
                        patient.getAttributes().add( attribute );
                    }

                    if ( attributeValue == null )
                    {
                        attributeValue = new PatientAttributeValue();
                        attributeValue.setPatient( patient );
                        attributeValue.setPatientAttribute( attribute );
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                        
                        patientAttributeValueService.savePatientAttributeValue( attributeValue );
                        
                        //valuesForSave.add( attributeValue );
                        
                    }
                    else
                    {
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }
                        
                        patientAttributeValueService.updatePatientAttributeValue( attributeValue );
                        
                       // valuesForUpdate.add( attributeValue );
                        //valuesForDelete.remove( attributeValue );
                    }
                }
            }
        }

        //patientService.updatePatient( patient, null, null, valuesForSave, valuesForUpdate, valuesForDelete );

        return SUCCESS;
    }
    
}
