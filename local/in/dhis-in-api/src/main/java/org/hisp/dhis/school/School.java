package org.hisp.dhis.school;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;

/**
 * @author Mithilesh Kumar Thakur
 */
public class School extends BaseNameableObject
{
    private static final long serialVersionUID = 1228298379303894619L;
    
    private int id;
    
    private String name;
    
    private String description;
    
    private OrganisationUnit organisationUnit;
    
    private String storedBy;

    private Date lastUpdated;
    
    private Set<Patient> patients = new HashSet<Patient>();
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public School()
    {
        
    }

    public School( String name )
    {
        this.name = name;
    }
    
    public School( String name, OrganisationUnit organisationUnit )
    {
        this.name = name;
        this.organisationUnit = organisationUnit;
    }
   
    public School( String name, String description, OrganisationUnit organisationUnit  )
    {
        this.name = name;
        this.description = description;
        this.organisationUnit = organisationUnit;
    }
   
    public School( String name, String description, OrganisationUnit organisationUnit, String storedBy, Date lastUpdated  )
    {
        this.name = name;
        this.description = description;
        this.organisationUnit = organisationUnit;
        this.storedBy = storedBy;
        this.lastUpdated = lastUpdated;
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

        if ( !(o instanceof School) )
        {
            return false;
        }

        final School other = (School) o;

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

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
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
    
    public Set<Patient> getPatients()
    {
        return patients;
    }

    public void setPatients( Set<Patient> patients )
    {
        this.patients = patients;
    }
    
}
