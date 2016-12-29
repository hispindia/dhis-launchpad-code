package org.hisp.dhis.beneficiary;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;

/**
 * @author Mithilesh Kumar Thakur
 */
public class Beneficiary  implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
    */
    private static final long serialVersionUID = 884114994005945275L;

    public static final String MALE = "M";
    public static final String FEMALE = "F";
   
    /**
     * The unique identifier
     */
    private Integer id;
    
    /**
     * Name
     */
    
    private String name;
    
    /**
     * Name fatherName/Husband name
     */
    
    private String fatherName;
    
    /**
     * gender
     */
   
    private String gender;
    
    /**
     * village
     */
   
    private String village;
    
    /**
     * identifier unique Required
     */
   
    private String identifier;  
    
    
    private Period period;
    
    private DataElementGroup dataElementGroup;
    
    private DataElement dataElement;
    
    private String price;
    
    private Date registrationDate;
    
    private Date serviceGivenDate;
   
    private Patient patient;
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
 
    public Beneficiary()
    {
        
    }
    
    public Beneficiary( String name, String fatherName, String gender, String village, String identifier, Period period, DataElementGroup dataElementGroup, DataElement dataElement, Patient patient, String price )
    {
        this.name = name;
        this.fatherName = fatherName;
        this.gender = gender;
        this.village = village;
        this.identifier = identifier;
        this.period = period;
        this.dataElementGroup = dataElementGroup;
        this.dataElement = dataElement;
        this.patient = patient;
        this.price = price;
    }

    public Beneficiary( String name, String fatherName, String gender, String village, String identifier, Period period, DataElementGroup dataElementGroup, DataElement dataElement, Patient patient )
    {
        this.name = name;
        this.fatherName = fatherName;
        this.gender = gender;
        this.village = village;
        this.identifier = identifier;
        this.period = period;
        this.dataElementGroup = dataElementGroup;
        this.dataElement = dataElement;
        this.patient = patient;
    }    
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        
        return result;
    }
    
    /*
    public int hashCode()
    {
        return code.hashCode();
    }
    */
    
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

        if ( !(o instanceof Beneficiary ) )
        {
            return false;
        }

        final Beneficiary other = (Beneficiary) o;

        return identifier.equals( other.getIdentifier() );
    }
    
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
   
    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getFatherName()
    {
        return fatherName;
    }

    public void setFatherName( String fatherName )
    {
        this.fatherName = fatherName;
    }
    
    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }
    
    public String getVillage()
    {
        return village;
    }

    public void setVillage( String village )
    {
        this.village = village;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier( String identifier )
    {
        this.identifier = identifier;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public DataElementGroup getDataElementGroup()
    {
        return dataElementGroup;
    }

    public void setDataElementGroup( DataElementGroup dataElementGroup )
    {
        this.dataElementGroup = dataElementGroup;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }
    

    public String getPrice()
    {
        return price;
    }

    public void setPrice( String price )
    {
        this.price = price;
    }
    
    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    public void setRegistrationDate( Date registrationDate )
    {
        this.registrationDate = registrationDate;
    }
    
    public Date getServiceGivenDate()
    {
        return serviceGivenDate;
    }

    public void setServiceGivenDate( Date serviceGivenDate )
    {
        this.serviceGivenDate = serviceGivenDate;
    }
    
    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }
    
    
}
