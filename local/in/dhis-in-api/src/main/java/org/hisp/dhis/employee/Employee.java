package org.hisp.dhis.employee;

import java.io.Serializable;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author BHARATH
 */

public class Employee  implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
    */
    private static final long serialVersionUID = 884114994005945275L;

    public static final String MALE = "M";
    public static final String FEMALE = "F";
   
    
    private Integer id;
    
    private String surname;
    
    private String firstName;
    
    private String lastName;
    
    private String gender;
    
    private String email;

    private String phoneNumber;

    private String jobTitle;

    private String code;
    
    private OrganisationUnit organisationUnit;
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    public Employee()
    {
        
    }
    
    public Employee( String surname, String firstName, String lastName, String gender, String email, String phoneNumber, String jobTitle, String code, OrganisationUnit organisationUnit )
    {
        this.surname = surname;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.jobTitle = jobTitle;
        this.code = code;
        this.organisationUnit = organisationUnit;
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
        result = prime * result + ((surname == null) ? 0 : surname.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
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

    public String getSurname()
    {
        return surname;
    }

    public void setSurname( String surname )
    {
        this.surname = surname;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }
    
    public String getGender()
    {
        return gender;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
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

    public String getJobTitle()
    {
        return jobTitle;
    }

    public void setJobTitle( String jobTitle )
    {
        this.jobTitle = jobTitle;
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
    
    public String getFullName()
    {
        boolean space = false;
        String name = "";

        if ( firstName != null && firstName.length() != 0 )
        {
            name = firstName;
            space = true;
        }

        if ( lastName != null && lastName.length() != 0 )
        {
            if ( space )
            {
                name += " ";
            }

            name += lastName;
            space = true;
        }

        if ( surname != null && surname.length() != 0 )
        {
            if ( space )
            {
                name += " ";
            }

            name += surname;
        }

        return name;
    }
    
    
    
    
    
    
    
}
