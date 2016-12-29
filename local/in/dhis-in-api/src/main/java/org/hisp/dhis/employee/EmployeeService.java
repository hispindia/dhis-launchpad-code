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

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author BHARATH
 */
public interface EmployeeService
{
    String ID = EmployeeService.class.getName();
    
    void addEmployee( Employee employee );
    
    void updateEmployee( Employee employee );

    void deleteEmployee( Employee employee );
    
    Employee getEmployeeById( int id );
    
    Employee getEmployeeByCode( String code );
    
    Employee getEmployeeByOrganisationUnitAndCode( OrganisationUnit organisationUnit,String code );
    
    //Employee getEmployeeByOrganisationUnitAndName( OrganisationUnit organisationUnit,String name );
    
    //Employee getEmployeeByNameAndDateOfBirth( String name, Date birthDate );
    
    Employee getEmployeeByNameDateOfBirthAndOrganisationUnit( String name, Date birthDate, OrganisationUnit organisationUnit );
    
    
    Collection<Employee> getAllEmployee();
    
    Collection<Employee> getEmployeeByOrganisationUnit( OrganisationUnit organisationUnit );
    
    Collection<Employee> getEmployeeByOrganisationUnitOrderByNameAsc( OrganisationUnit organisationUnit );
    
    Collection<Employee> getEmployeeByOrganisationUnitOrderByDesignationAsc( OrganisationUnit organisationUnit );
    
    
    Collection<Employee> getEmployeeByOrganisationUnitAndDesignation( OrganisationUnit organisationUnit, String designation );

    Collection<Employee> getAllEmployeesByOrgUnitAndOrderByCodeDesc( OrganisationUnit organisationUnit );
    
    String getMaxEmployeeCodeByOrganisationUnit( OrganisationUnit organisationUnit );
    
    void searchEmployeesByName( List<Employee> employees, String key );
    
    int getAgeFromDateOfBirth( Date birthDate );
    
}

