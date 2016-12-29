package org.hisp.dhis.ovc.registration;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetOVCRegistrationDetailsAction  implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    /*
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    private Integer ovcId;

    public void setOvcId( Integer ovcId )
    {
        this.ovcId = ovcId;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private List<Grid> grids;

    public List<Grid> getGrids()
    {
        return grids;
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
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        patient = patientService.getPatient( ovcId );

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
        
        //grids = programInstanceService.getProgramInstanceReport( patient, i18n, format );

        if ( type == null )
        {
            return SUCCESS;
        }

        return type;
    }
}
