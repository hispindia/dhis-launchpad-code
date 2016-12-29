package org.hisp.dhis.location;

import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 */

public class Location extends BaseNameableObject
{
    private static final long serialVersionUID = 1228298379303894619L;

    private int id;
    
    private String name;

    private String shortName;
    
    private String code;

    private String contactPerson;

    private String address;

    private String email;

    private String phoneNumber;
    
    private OrganisationUnit parentOrganisationUnit;
    
    private boolean active;
    
    
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public Location()
    {
        
    }

    public Location( String name )
    {
        this.name = name;
    }
    
    public Location( String name, String code, OrganisationUnit parentOrganisationUnit  )
    {
        this.name = name;
        this.code = code;
        this.parentOrganisationUnit = parentOrganisationUnit;
    }
    
    
    public Location( String name, String shortName, String code )
    {
        this.name = name;
        this.shortName = shortName;
        this.code = code;
    }
    
    public Location( String name, String shortName, OrganisationUnit parentOrganisationUnit, String code )
    {
        this.name = name;
        this.shortName = shortName;
        this.parentOrganisationUnit = parentOrganisationUnit;
        this.code = code;
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

        if ( !(o instanceof Location) )
        {
            return false;
        }

        final Location other = (Location) o;

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

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson( String contactPerson )
    {
        this.contactPerson = contactPerson;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress( String address )
    {
        this.address = address;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    public OrganisationUnit getParentOrganisationUnit()
    {
        return parentOrganisationUnit;
    }

    public void setParentOrganisationUnit( OrganisationUnit parentOrganisationUnit )
    {
        this.parentOrganisationUnit = parentOrganisationUnit;
    }    
    
    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }
    
}
