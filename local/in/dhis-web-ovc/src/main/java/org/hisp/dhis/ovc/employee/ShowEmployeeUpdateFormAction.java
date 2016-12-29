package org.hisp.dhis.ovc.employee;

import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowEmployeeUpdateFormAction implements Action
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

    private Integer empId;
    
    public void setEmpId( Integer empId )
    {
        this.empId = empId;
    }

    private Employee employee;
    
    public Employee getEmployee()
    {
        return employee;
    }
    
    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }


    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
 
    public String execute()
    {
        //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        employee = employeeService.getEmployeeById( empId );
        
        organisationUnit = employee.getOrganisationUnit();
        
        return SUCCESS;
    }
}