package org.hisp.dhis.employee;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 */

public class Employee implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
    */
    private static final long serialVersionUID = 884114994005945275L;

    public static final String MALE = "M";
    public static final String FEMALE = "F";
   
    
    private Integer id;
    
    private String name;
    
    /*
    private String surname;
    
    private String firstName;
    
    private String lastName;
    */
    
    private String gender;
    
    private String address;
    
    private String phoneNumber;
    
    private String email;
    
    private Date birthDate;
    
    private String educationLevel;
    
    private String category;
    
    private String designation;
    
    private String maritalStatus;
    
    private Date joiningDate;
   
    private String experience;
    
    private boolean orientationReceived;
    
    private boolean active;
    
    private String code;
    
    private OrganisationUnit organisationUnit;
    
    private String storedBy;

    private Date lastupdated;
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    public Employee()
    {
        
    }
    
    public Employee( String name, String gender, String address, String phoneNumber, String email, Date birthDate, String educationLevel, String category, String designation, String maritalStatus, Date joiningDate, String experience, boolean orientationReceived, boolean active, String code, OrganisationUnit organisationUnit, String storedBy, Date lastupdated )
    {
        this.name = name;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.birthDate = birthDate;
        this.educationLevel = educationLevel;
        this.category = category;
        this.designation = designation;
        this.maritalStatus = maritalStatus;
        this.joiningDate = joiningDate;
        this.experience = experience;
        this.orientationReceived = orientationReceived;
        this.active = active;
        this.code = code;
        this.organisationUnit = organisationUnit;
        this.storedBy = storedBy;
        this.lastupdated = lastupdated;
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

        if ( !(o instanceof Employee ) )
        {
            return false;
        }

        final Employee other = (Employee) o;

        return code.equals( other.getCode() );
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

    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress( String address )
    {
        this.address = address;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
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

    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
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

    public Date getLastupdated()
    {
        return lastupdated;
    }

    public void setLastupdated( Date lastupdated )
    {
        this.lastupdated = lastupdated;
    }

}
