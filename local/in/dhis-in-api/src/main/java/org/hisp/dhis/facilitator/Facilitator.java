package org.hisp.dhis.facilitator;

import java.util.Date;
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
    
    private String code;
    
    private boolean active;
    
    private String email;
    
    private Date birthDate;
    
    private String educationLevel;
    
    private String category;
    
    private String designation;
    
    private String maritalStatus;
    
    private Date joiningDate;
    
    private String panchayatName;
   
    private String experience;
    
    private boolean orientationReceived;
    
    private OrganisationUnit organisationUnit;
    
    private Set<Patient> patients = new HashSet<Patient>();
    
    private String storedBy;

    private Date lastupdated;

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
    
    
    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate( Date birthDate )
    {
        this.birthDate = birthDate;
    }

    public String getEducationLevel()
    {
        return educationLevel;
    }

    public void setEducationLevel( String educationLevel )
    {
        this.educationLevel = educationLevel;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory( String category )
    {
        this.category = category;
    }

    public String getDesignation()
    {
        return designation;
    }

    public void setDesignation( String designation )
    {
        this.designation = designation;
    }

    public String getMaritalStatus()
    {
        return maritalStatus;
    }

    public void setMaritalStatus( String maritalStatus )
    {
        this.maritalStatus = maritalStatus;
    }

    public Date getJoiningDate()
    {
        return joiningDate;
    }

    public void setJoiningDate( Date joiningDate )
    {
        this.joiningDate = joiningDate;
    }
    
    public String getPanchayatName()
    {
        return panchayatName;
    }

    public void setPanchayatName( String panchayatName )
    {
        this.panchayatName = panchayatName;
    }
    
    public String getExperience()
    {
        return experience;
    }

    public void setExperience( String experience )
    {
        this.experience = experience;
    }

    public boolean isOrientationReceived()
    {
        return orientationReceived;
    }

    public void setOrientationReceived( boolean orientationReceived )
    {
        this.orientationReceived = orientationReceived;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getLastupdated()
    {
        return lastupdated;
    }

    public void setLastupdated( Date lastupdated )
    {
        this.lastupdated = lastupdated;
    }

}
