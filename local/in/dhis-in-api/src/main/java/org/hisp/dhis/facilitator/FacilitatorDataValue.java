package org.hisp.dhis.facilitator;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;

/**
 * @author Mithilesh Kumar Thakur
 */
public class FacilitatorDataValue implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
    */
    
    private static final long serialVersionUID = 6269303850789110610L;
    
    private Facilitator facilitator;
    
    private Patient patient;
    
    private Period period;
    
    private DataElement dataElement;
    
    private DataElementCategoryOptionCombo optionCombo;
    
    private String value;

    private String storedBy;

    private Date lastUpdated;

    private String comment;
    
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public FacilitatorDataValue()
    {
    }

    public FacilitatorDataValue( Facilitator facilitator, Patient patient, Period period, DataElement dataElement )
    {
        this.facilitator = facilitator;
        this.patient = patient;
        this.period = period;
        this.dataElement = dataElement;
    }

    public FacilitatorDataValue( Facilitator facilitator, Patient patient, Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo )
    {
        this.facilitator = facilitator;
        this.patient = patient;
        this.period = period;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
    }    
    
    public FacilitatorDataValue( Facilitator facilitator, Patient patient, Period period, DataElement dataElement, String value )
    {
        this.facilitator = facilitator;
        this.patient = patient;
        this.period = period;
        this.dataElement = dataElement;
        this.value = value;
    }
    
    public FacilitatorDataValue( Facilitator facilitator, Patient patient, Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, String value )
    {
        this.facilitator = facilitator;
        this.patient = patient;
        this.period = period;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.value = value;
    }        

    public FacilitatorDataValue( Facilitator facilitator, Patient patient, Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, String value, String storedBy, Date lastUpdated, String comment )
    {
        
        this.facilitator = facilitator;
        this.patient = patient;
        this.period = period;
        this.dataElement = dataElement;
        this.optionCombo = optionCombo;
        this.value = value;
        this.storedBy = storedBy;
        this.lastUpdated = lastUpdated;
        this.comment = comment;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof DataValue) )
        {
            return false;
        }

        final FacilitatorDataValue other = (FacilitatorDataValue) o;

        return dataElement.equals( other.getDataElement() ) && optionCombo.equals( other.getOptionCombo() ) && period.equals( other.getPeriod() ) && patient.equals( other.getPatient() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        
        result = result * prime + optionCombo.hashCode();
        result = result * prime + period.hashCode();
        result = result * prime + dataElement.hashCode();
        result = result * prime + patient.hashCode();

        return result;
    }

 
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public Facilitator getFacilitator()
    {
        return facilitator;
    }

    public void setFacilitator( Facilitator facilitator )
    {
        this.facilitator = facilitator;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }
    
    public DataElementCategoryOptionCombo getOptionCombo()
    {
        return optionCombo;
    }

    public void setOptionCombo( DataElementCategoryOptionCombo optionCombo )
    {
        this.optionCombo = optionCombo;
    }
    
    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment( String comment )
    {
        this.comment = comment;
    }
    
    
    
}
