package org.hisp.dhis.ovc.employee;

import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ValidateEmployeeAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    private Integer employeeId;
    
    public void setEmployeeId( Integer employeeId )
    {
        this.employeeId = employeeId;
    }

    private String employeeCode;

    public void setEmployeeCode( String employeeCode )
    {
        this.employeeCode = employeeCode;
    }

    private Integer orgUnitId; 
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Employee Validation with empCode and orgUnitId
        // ---------------------------------------------------------------------
        
        //System.out.println( " Inside Validate Employee "   + " employeeCode :" + employeeCode  + "  -- orgUnitId : " + orgUnitId );
        
        //System.out.println( " Inside Validate Employee "   + " employeeId :" + employeeId );
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        Employee employee = employeeService.getEmployeeByByOrganisationUnitAndCode( organisationUnit, employeeCode );
        
        if ( employee != null )
        {
            if ( employeeId == null || ( employeeId != null && employee.getId().intValue() != employeeId.intValue() ) )
            {
                message = "Employee Already Exists, Please Specify Another Code";

                return INPUT;
            }
            
        }
        
        return SUCCESS;
    }
}
