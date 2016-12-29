package org.hisp.dhis.ovc.employee;

import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;

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
    /*
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private int empId;
    
    public void setEmpId( int empId )
    {
        this.empId = empId;
    }
    
    /*
    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    */
    
    private String surName;

    public void setSurName( String surName )
    {
        this.surName = surName;
    }

    private String firstName;

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    private String lastName;

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
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

    private String jobTitle;

    public void setJobTitle( String jobTitle )
    {
        this.jobTitle = jobTitle;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute()
    {
        Employee employee = employeeService.getEmployeeById( empId );
        
        //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        //Employee employee = new Employee();

        employee.setSurname( surName );
        employee.setFirstName( firstName );
        employee.setLastName( lastName );
        employee.setGender( gender );
        employee.setCode( employeeCode );
        employee.setPhoneNumber( phoneNumber );
        employee.setEmail( email );
        employee.setJobTitle( jobTitle );

        employee.setOrganisationUnit( employee.getOrganisationUnit() );

        employeeService.updateEmployee( employee );
        
        return SUCCESS;
    }
}
