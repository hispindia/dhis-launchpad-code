package org.hisp.dhis.ovc.approved.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author BHARATH
 */

public class GetOVCDetailsAction  implements Action
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
    // Getters && Setters
    // -------------------------------------------------------------------------

    private Integer ovcId;

    public void setOvcId( Integer ovcId )
    {
        this.ovcId = ovcId;
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
    
    private Map<String, String> visitDateMap = new HashMap<String, String>();
    
    public Map<String, String> getVisitDateMap()
    {
        return visitDateMap;
    }
    
    private List<Period> periods = new ArrayList<Period>();
    
    public List<Period> getPeriods()
    {
        return periods;
    }
    
    private List<Integer> years = new ArrayList<Integer>();
    
    public List<Integer> getYears()
    {
        return years;
    }
    
    private List<String> months = new ArrayList<String>();
    
    public List<String> getMonths()
    {
        return months;
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
        
        
        // -------------------------------------------------------------------------
        // Get Visit Dates
        // -------------------------------------------------------------------------

        String approvedDateStr = patientAttributeValueMap.get( 405 );
        Date approvedDate = new Date();
        if( approvedDateStr == null )
        {
            approvedDate = patient.getRegistrationDate();
        }
        else
        {
            approvedDate = format.parseDate( approvedDateStr );
        }
        
        SimpleDateFormat yearFormat = new SimpleDateFormat( "yyyy" );
        int startYear = Integer.parseInt(  yearFormat.format( approvedDate ) );
        int endYear =  Integer.parseInt(  yearFormat.format(  new Date() ) );
        
        for( int i = startYear; i <= endYear; i++ )
        {
            years.add( i );
        }
        
        months.add( "-01-01" );months.add( "-02-01" );
        months.add( "-03-01" );months.add( "-04-01" );
        months.add( "-05-01" );months.add( "-06-01" );
        months.add( "-07-01" );months.add( "-08-01" );
        months.add( "-09-01" );months.add( "-10-01" );
        months.add( "-11-01" );months.add( "-12-01" );
        
        periods = ovcService.getMontlyPeriods( approvedDate, new Date() );
        
        /*
        for( Period period : periods )
        {
            System.out.println( period.getName() + " -- "+ period.getDescription() + " -- "+ period.getId() );
        }
        */
        
        
        visitDateMap.putAll( ovcService.getVisitDateMap( periods, patient.getId() ) );
        
        return SUCCESS;
    }
}
