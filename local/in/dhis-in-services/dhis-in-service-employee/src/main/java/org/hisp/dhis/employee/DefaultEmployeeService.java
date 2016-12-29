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
package org.hisp.dhis.employee;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BHARATH
 */

@Transactional
public class DefaultEmployeeService implements EmployeeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EmployeeStore employeeStore;
    
    public void setEmployeeStore( EmployeeStore employeeStore )
    {
        this.employeeStore = employeeStore;
    }
    
    // -------------------------------------------------------------------------
    // Employee
    // -------------------------------------------------------------------------
    
    public void addEmployee( Employee employee )
    {
        employeeStore.addEmployee( employee );
    }

    public void deleteEmployee( Employee employee )
    {
        employeeStore.deleteEmployee( employee );
    }

    public void updateEmployee( Employee employee )
    {
        employeeStore.updateEmployee( employee );
    }
    
    public Employee getEmployeeById( int id )
    {
        return employeeStore.getEmployeeById( id );
    }
    
    public Employee getEmployeeByCode( String code )
    {
        return employeeStore.getEmployeeByCode( code );
    }
    
    public Employee getEmployeeByOrganisationUnitAndCode( OrganisationUnit organisationUnit,String code )
    {
        return employeeStore.getEmployeeByOrganisationUnitAndCode( organisationUnit, code );
    }    
    
    public Employee getEmployeeByNameDateOfBirthAndOrganisationUnit( String name, Date birthDate, OrganisationUnit organisationUnit )
    {
        return employeeStore.getEmployeeByNameDateOfBirthAndOrganisationUnit( name, birthDate, organisationUnit );
    }    
    
    public Collection<Employee> getAllEmployee()
    {
        return employeeStore.getAllEmployee();
    }
    
    public Collection<Employee> getEmployeeByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        return employeeStore.getEmployeeByOrganisationUnit( organisationUnit );
    }
    
    public Collection<Employee> getEmployeeByOrganisationUnitOrderByNameAsc( OrganisationUnit organisationUnit )
    {
        return employeeStore.getEmployeeByOrganisationUnitOrderByNameAsc( organisationUnit );
    }    
    
    public Collection<Employee> getEmployeeByOrganisationUnitOrderByDesignationAsc( OrganisationUnit organisationUnit )
    {
        return employeeStore.getEmployeeByOrganisationUnitOrderByDesignationAsc( organisationUnit );
    }        
    
    public Collection<Employee> getEmployeeByOrganisationUnitAndDesignation( OrganisationUnit organisationUnit, String designation )
    {
        return employeeStore.getEmployeeByOrganisationUnitAndDesignation( organisationUnit, designation );
    }
    
    public Collection<Employee> getAllEmployeesByOrgUnitAndOrderByCodeDesc( OrganisationUnit organisationUnit )
    {
        return employeeStore.getAllEmployeesByOrgUnitAndOrderByCodeDesc( organisationUnit );
    }
    
    public String getMaxEmployeeCodeByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        return employeeStore.getMaxEmployeeCodeByOrganisationUnit( organisationUnit );
    }
    
   
    // Search employee by name
    public void searchEmployeesByName( List<Employee> employees, String key )
    {
        Iterator<Employee> iterator = employees.iterator();

        while ( iterator.hasNext() )
        {
            if ( !iterator.next().getName().toLowerCase().contains( key.toLowerCase() ) )
            {
                iterator.remove();
            }
        }
    }
    
    
    public int getAgeFromDateOfBirth( Date birthDate ) 
    {
        if ( birthDate == null ) 
        {
                return 0;
        }

        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);

        Calendar todayCalendar = Calendar.getInstance();

        int age = todayCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        if ( todayCalendar.get(Calendar.MONTH) < birthCalendar.get(Calendar.MONTH) ) 
        {
            age--;
        } 
        else if (todayCalendar.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) && todayCalendar.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH)) 
        {
            age--;
        }

        
        return age;
    }
    
}
