package org.hisp.dhis.facilitator;

import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;

/**
 * @author Mithilesh Kumar Thakur
 */
public class Facilitator extends BaseNameableObject
{
    private static final long serialVersionUID = 1228298379303894619L;
    
    
    private int id;
    
    private String name;
    
    private String gender;
    
    private String contactNumber;
    
    private String address;
    
    private boolean active;
    
    private OrganisationUnit organisationUnit;
    
    private Set<Patient> patients = new HashSet<Patient>();
    
    private String code;
    
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    public Facilitator()
    {
        
    }
    
    public Facilitator( String name, String gender,String contactNumber, String address, String code, OrganisationUnit organisationUnit )
    {
        this.name = name;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.code = code;
        this.organisationUnit = organisationUnit;
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
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

        if ( !(o instanceof Facilitator) )
        {
            return false;
        }

        final Facilitator other = (Facilitator) o;

        return name.equals( other.getName() );
    }

 
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
     

    public int getId()
    {
        return id;
    }

    public void setId( int id )
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

    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public String getContactNumber()
    {
        return contactNumber;
    }

    public void setContactNumber( String contactNumber )
    {
        this.contactNumber = contactNumber;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress( String address )
    {
        this.address = address;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public Set<Patient> getPatients()
    {
        return patients;
    }

    public void setPatients( Set<Patient> patients )
    {
        this.patients = patients;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }
    
}
