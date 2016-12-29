package org.hisp.dhis.asha.employee;

import java.util.Date;

import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UpdateEmployeeAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private int empId;
    
    public void setEmpId( int empId )
    {
        this.empId = empId;
    }
    
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    private String gender;

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    private String employeeCode;

    public void setEmployeeCode( String employeeCode )
    {
        this.employeeCode = employeeCode;
    }

    private String designation;

    public void setDesignation( String designation )
    {
        this.designation = designation;
    }
    
    private String address;
    
    public void setAddress( String address )
    {
        this.address = address;
    }

    private String phoneNumber;

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    private String email;

    public void setEmail( String email )
    {
        this.email = email;
    }

    private String birthDate;
    
    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }

    private String educationLevel;
    
    public void setEducationLevel( String educationLevel )
    {
        this.educationLevel = educationLevel;
    }
    
    private String category;

    public void setCategory( String category )
    {
        this.category = category;
    }

    private String maritalStatus;
    
    public void setMaritalStatus( String maritalStatus )
    {
        this.maritalStatus = maritalStatus;
    }
    
    private String joiningDate;
    
    public void setJoiningDate( String joiningDate )
    {
        this.joiningDate = joiningDate;
    }
    
    private String experience;
    
    public void setExperience( String experience )
    {
        this.experience = experience;
    }
    
    private Boolean orientationReceived;
    
    public void setOrientationReceived( Boolean orientationReceived )
    {
        this.orientationReceived = orientationReceived;
    }

    private Boolean active;
    
    public void setActive( Boolean active )
    {
        this.active = active;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute()
    {
        Employee employee = employeeService.getEmployeeById( empId );
        
        //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        //Employee employee = new Employee();

        employee.setName( name );
        employee.setGender( gender );
        employee.setCode( employeeCode );
        employee.setDesignation( designation );
        employee.setAddress( address );
        employee.setPhoneNumber( phoneNumber );
        employee.setEmail( email );
        employee.setBirthDate( format.parseDate( birthDate ) );
        employee.setEducationLevel( educationLevel );
        employee.setCategory( category );
        employee.setMaritalStatus( maritalStatus );
        employee.setJoiningDate( format.parseDate( joiningDate ) );
        employee.setExperience( experience );
        
        if( orientationReceived != null )
        {
            employee.setOrientationReceived( orientationReceived );
        }

        active = ( active == null) ? false : true;
        employee.setActive( active );
        
        employee.setCode( employeeCode );
        employee.setOrganisationUnit( employee.getOrganisationUnit() );
        
        String storedBy = currentUserService.getCurrentUsername();

        Date now = new Date();

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        employee.setStoredBy( storedBy );

        employee.setLastupdated( now );

        employeeService.updateEmployee( employee );
        
        return SUCCESS;
    }
}