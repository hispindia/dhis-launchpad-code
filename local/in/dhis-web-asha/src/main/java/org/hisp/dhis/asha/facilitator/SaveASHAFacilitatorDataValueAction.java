package org.hisp.dhis.asha.facilitator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.asha.registration.AddASHAAction;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorDataValue;
import org.hisp.dhis.facilitator.FacilitatorDataValueService;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveASHAFacilitatorDataValueAction implements Action
{
    private static final Log log = LogFactory.getLog( SaveASHAFacilitatorDataValueAction.class );
    
    public static final String PREFIX_DATAELEMENT = "dataelement";
    public static final String ASHA_FACILITATOR_ATTRIBUTE = "ASHA Facilitator";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    
    private FacilitatorDataValueService facilitatorDataValueService;
    
    public void setFacilitatorDataValueService( FacilitatorDataValueService facilitatorDataValueService )
    {
        this.facilitatorDataValueService = facilitatorDataValueService;
    }
    
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
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private Integer dataSetId;
    
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    private Integer facilitatorId;
    
    public void setFacilitatorId( Integer facilitatorId )
    {
        this.facilitatorId = facilitatorId;
    }

    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    private Integer selectedASHAId;
    
    public void setSelectedASHAId( Integer selectedASHAId )
    {
        this.selectedASHAId = selectedASHAId;
    }

    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private int statusCode = 0;

    public int getStatusCode()
    {
        return statusCode;
    }  
    
    private Facilitator facilitator;
    
    public Facilitator getFacilitator()
    {
        return facilitator;
    }
    
    private List<Patient> patients = new ArrayList<Patient>();
    
    public List<Patient> getPatients()
    {
        return patients;
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
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        facilitator = facilitatorService.getFacilitator( facilitatorId );
        
        patients = new ArrayList<Patient>( facilitator.getPatients() );
        
        patient = patientService.getPatient( selectedASHAId );
        
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        if ( period == null )
        {
            return logError( "Illegal period identifier: " + selectedPeriodId );
        }
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
       
        List<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        String storedBy = currentUserService.getCurrentUsername();
        
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }
        
        Date now = new Date();
       
        // ---------------------------------------------------------------------
        // Add / Update data
        // ---------------------------------------------------------------------
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        //String value = null;
        
        // Save value for Single ASHA Linked with Single facilitator
        
        if ( patient != null && facilitator != null )
        {
            if ( dataElements != null && dataElements.size() > 0 )
            {
                for ( DataElement dataElement : dataElements )
                {
                    DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                                            
                    String value = request.getParameter( PREFIX_DATAELEMENT + dataElement.getId() );
                    
                    if ( value != null && value.trim().length() == 0 )
                    {
                        value = null;
                    }

                    if ( value != null )
                    {
                        value = value.trim();
                    }
                   
                    FacilitatorDataValue facilitatorDataValue = facilitatorDataValueService.getFacilitatorDataValue( facilitator, patient, period, dataElement, optionCombo );   
                    
                    if ( facilitatorDataValue == null )
                    {
                        if ( value != null )
                        {
                            facilitatorDataValue = new FacilitatorDataValue( facilitator, patient, period, dataElement, optionCombo, value, storedBy, now, null );
                            
                            facilitatorDataValueService.addFacilitatorDataValue( facilitatorDataValue );
                        }
                    }
                    else
                    {
                        facilitatorDataValue.setValue( value );
                        facilitatorDataValue.setLastUpdated( now );
                        facilitatorDataValue.setStoredBy( storedBy );

                        facilitatorDataValueService.updateFacilitatorDataValue( facilitatorDataValue );
                    }
                }
            }
        }
        
        
        
        // Save value for Multiple ASHAs Linked with Single facilitator
        
        /*
        if ( patients != null && patients.size() > 0 )
        {
            for( Patient patient : patients )
            {
                if ( dataElements != null && dataElements.size() > 0 )
                {
                    for ( DataElement dataElement : dataElements )
                    {
                        DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                                                
                        String value = request.getParameter( PREFIX_DATAELEMENT + dataElement.getId()+ ":" +  patient.getId() );
                        
                        if ( value != null && value.trim().length() == 0 )
                        {
                            value = null;
                        }

                        if ( value != null )
                        {
                            value = value.trim();
                        }
                       
                        FacilitatorDataValue facilitatorDataValue = facilitatorDataValueService.getFacilitatorDataValue( facilitator, patient, period, dataElement, optionCombo );   
                        
                        if ( facilitatorDataValue == null )
                        {
                            if ( value != null )
                            {
                                facilitatorDataValue = new FacilitatorDataValue( facilitator, patient, period, dataElement, optionCombo, value, storedBy, now, null );
                                
                                facilitatorDataValueService.addFacilitatorDataValue( facilitatorDataValue );
                            }
                        }
                        else
                        {
                            facilitatorDataValue.setValue( value );
                            facilitatorDataValue.setLastUpdated( now );
                            facilitatorDataValue.setStoredBy( storedBy );

                            facilitatorDataValueService.updateFacilitatorDataValue( facilitatorDataValue );
                        }
                    }
                         
                }
                
            }
        }
        */
        
        
        // --------------------------------------------------------------------------------------------------------
        // Save Patient Attributes
        // -----------------------------------------------------------------------------------------------------
        
        String attrValue = null;
        
        attributeGroup = new PatientAttributeGroup();
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_FACILITATOR_ATTRIBUTE );
        
        Collection<PatientAttribute> attributes = attributeGroup.getAttributes();
        
        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            patient.getAttributes().clear();
            
            for ( PatientAttribute attribute : attributes )
            {
                attrValue = request.getParameter( AddASHAAction.PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( attrValue ) )
                {
                    attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, attribute );

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
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( attrValue, 0 ) );
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
                            attributeValue.setValue( attrValue.trim() );
                        }
                        
                        patientAttributeValueService.savePatientAttributeValue( attributeValue );
                    }
                    else
                    {
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( attrValue, 0 ) );
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
                            attributeValue.setValue( attrValue.trim() );
                        }
                        
                        patientAttributeValueService.updatePatientAttributeValue( attributeValue );
                    }
                }
            }
        }
        
        return SUCCESS;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String logError( String message )
    {
        return logError( message, 1 );
    }

    private String logError( String message, int statusCode )
    {
        log.info( message );

        this.statusCode = statusCode;

        return SUCCESS;
    }
    
}

