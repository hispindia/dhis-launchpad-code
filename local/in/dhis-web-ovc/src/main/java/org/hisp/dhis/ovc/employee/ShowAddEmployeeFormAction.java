package org.hisp.dhis.ovc.employee;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

 /**
 * @author Mithilesh Kumar Thakur
 */

public class ShowAddEmployeeFormAction implements Action
{       
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private int organisationUnitId;
    
    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private String employeeCode;
    
    public String getEmployeeCode()
    {
        return employeeCode;
    }

    private List<Employee> employees = new ArrayList<Employee>();
    
    public List<Employee> getEmployees()
    {
        return employees;
    }
    private String empCode = "";
    
    private Employee employee;
    
    public Employee getEmployee()
    {
        return employee;
    }
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
    {
        organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
        if ( organisationUnit.getCode() != null )
        {
            //employees = new ArrayList<Employee>( employeeService.getAllEmployeesByOrgUnitAndOrderByCodeDesc( organisationUnit ) );
            
            String temEmpCode = employeeService.getMaxEmployeeCodeByOrganisationUnit( organisationUnit );
            
            System.out.println(  " Emp Code is -- " + temEmpCode );
            
            if ( temEmpCode != null && !temEmpCode.equalsIgnoreCase( "" ) )
            {
                //employee = employees.get( 0 );
                
                //System.out.println( employee.getFullName() + " -- " + employee.getCode() );
                
                empCode = temEmpCode;
                
                String[] compositeCode = empCode.split( "-" );
                
                //String orgUnitCode = compositeCode[0];
                
                String preFix = compositeCode[1];
                
                String code = compositeCode[2];
                
                Integer presentCount = Integer.parseInt( code  );
                
                int finalCount = presentCount + 1;
                
                
                if( finalCount <= 9 )
                {
                    employeeCode = organisationUnit.getCode() + "-" + preFix + "-" + "000" + finalCount;
                }
                
                else if( finalCount <= 99 )
                {
                    employeeCode = organisationUnit.getCode() + "-" + preFix + "-" + "00" + finalCount;
                }
                
                else if( finalCount <= 999 )
                {
                    employeeCode = organisationUnit.getCode() + "-" + preFix + "-" + + finalCount;
                }
                else if( finalCount <= 9999 )
                {
                    employeeCode = organisationUnit.getCode() + "-" + preFix + "-" +  finalCount;
                }
                
            }
            
            else
            {
                employeeCode = organisationUnit.getCode() + "-C" + "-" + "0001";
            }
            
        }
        
        else
        {
            employeeCode = "";  
        }
        
        
        System.out.println( " Final Employee Code is -- " + employeeCode );
        
        return SUCCESS;
    }
}