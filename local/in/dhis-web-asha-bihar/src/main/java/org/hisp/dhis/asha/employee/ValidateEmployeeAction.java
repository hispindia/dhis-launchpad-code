package org.hisp.dhis.asha.employee;

import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ValidateEmployeeAction     implements Action
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
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }

    private String birthDate;
    
    public void setBirthDate( String birthDate )
    {
        this.birthDate = birthDate;
    }
    
    private Integer employeeId;
    
    public void setEmployeeId( Integer employeeId )
    {
        this.employeeId = employeeId;
    }

    private Integer orgUnitId; 
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    /*
    private String employeeCode;

    public void setEmployeeCode( String employeeCode )
    {
        this.employeeCode = employeeCode;
    }
    */
    
    public String execute() throws Exception
    {
        // ---------------------------------------------------------------------
        // Employee Validation with empCode and orgUnitId
        // ---------------------------------------------------------------------
        
        //System.out.println( " Inside Validate Employee "   + " Nmae :" + name  + "  -- orgUnitId : " + orgUnitId  + "  -- birth Date : " + birthDate );
        
        //System.out.println( " Inside Validate Employee "   + " employeeId :" + employeeId );
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        Employee employee = employeeService.getEmployeeByNameDateOfBirthAndOrganisationUnit( name, format.parseDate( birthDate ), organisationUnit );
        
        if ( employee != null )
        {
            if ( employeeId == null || ( employeeId != null && employee.getId().intValue() != employeeId.intValue() ) )
            {
                message = "Employee Already Exists, Please Specify Another Name or Date Of Birth";

                return INPUT;
            }
        }
        
        return SUCCESS;
    }
}

