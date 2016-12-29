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
package org.hisp.dhis.school;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class DefaultSchoolService implements SchoolService
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SchoolStore schoolStore;
    
    public void setSchoolStore( SchoolStore schoolStore )
    {
        this.schoolStore = schoolStore;
    }
    
    private SchoolDetailsService schoolDetailsService;
    
    public void setSchoolDetailsService( SchoolDetailsService schoolDetailsService )
    {
        this.schoolDetailsService = schoolDetailsService;
    }
    
    // -------------------------------------------------------------------------
    // Location
    // -------------------------------------------------------------------------

    @Override
    public int addSchool( School school )
    {
        return schoolStore.save( school );
    }
    
    @Override
    public void updateSchool( School school )
    {
        schoolStore.update( school );
    }
    
    @Override
    public void deleteSchool( School school )
    {
        schoolStore.delete( school );
    }

    @Override
    public School getSchool( int id )
    {
        return schoolStore.getSchool( id );
    }
    
    @Override
    public School getSchoolByName( String name )
    {
        return schoolStore.getSchoolByName( name );
    }
    
    @Override
    public School getSchoolByOrganisationUnitAndName( OrganisationUnit organisationUnit,String name )
    {
        return schoolStore.getSchoolByOrganisationUnitAndName( organisationUnit, name );
    }
    
    public Collection<School> getAllSchools()
    {
        return schoolStore.getAll();
    }
    
    @Override
    public Collection<School> getSchoolByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        return schoolStore.getSchoolByOrganisationUnit( organisationUnit );
    }
    
    public Collection<School> getSchoolByOVC( Patient patient )
    {
        return schoolStore.getSchoolByOVC( patient );
    }
       
    
    public void searchSchoolsByName( List<School> schools, String key )
    {
        Iterator<School> iterator = schools.iterator();

        while ( iterator.hasNext() )
        {
            if ( !iterator.next().getName().toLowerCase().contains( key.toLowerCase() ) )
            {
                iterator.remove();
            }
        }
    }
    
    
    public int addSchoolWithDetails( School school, List<SchoolDetails> schoolDetailValues   )
    {
        int schoolId = addSchool( school );
        
        if( schoolDetailValues != null && schoolDetailValues.size() > 0 )
        {
            for ( SchoolDetails value : schoolDetailValues )
            {
                schoolDetailsService.saveSchoolDetails( value );
            }
        }
        
        return schoolId;

    }
    
    
    public void updateSchoolWithDetails( School school, List<SchoolDetails> valuesForSave, List<SchoolDetails> valuesForUpdate, Collection<SchoolDetails> valuesForDelete )
    {
        schoolStore.update( school );
        
        if( valuesForSave != null && valuesForSave.size() > 0 )
        {
            for ( SchoolDetails valueForSave : valuesForSave )
            {
                schoolDetailsService.saveSchoolDetails( valueForSave );
            }
        }
        
        if( valuesForUpdate != null && valuesForUpdate.size() > 0 )
        {
            for ( SchoolDetails valueForUpdate : valuesForUpdate )
            {
                schoolDetailsService.updateSchoolDetails( valueForUpdate );
            }
        }
        
        if( valuesForDelete != null && valuesForDelete.size() > 0 )
        {
            for ( SchoolDetails valueForDelete : valuesForDelete )
            {
                schoolDetailsService.deleteSchoolDetails( valueForDelete );
            }
        }
        
    }
   
}
