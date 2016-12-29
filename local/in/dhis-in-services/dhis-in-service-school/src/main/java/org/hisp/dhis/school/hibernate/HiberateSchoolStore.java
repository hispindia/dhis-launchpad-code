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
package org.hisp.dhis.school.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolStore;

/**
 * @author Mithilesh Kumar Thakur
 */

public class HiberateSchoolStore extends HibernateIdentifiableObjectStore<School> implements SchoolStore
{
   
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // School
    // -------------------------------------------------------------------------
    
    public School getSchool( int id )
    {
        return (School) sessionFactory.getCurrentSession().get( School.class, id );
    }
 
    public  School getSchoolByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( School.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (School) criteria.uniqueResult();

    }
    
    
    public School getSchoolByOrganisationUnitAndName( OrganisationUnit organisationUnit, String name )
    {
        return (School) getCriteria( Restrictions.eq( "organisationUnit", organisationUnit )).add( Restrictions.eq( "name", name ) ).uniqueResult();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<School> getSchoolByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        //return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ) ).list();
        
        Criteria criteria = getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ) );
        
        criteria.addOrder( Order.asc("name")) .list();
        
        return criteria.list();
        
        //return  criteria.addOrder( Order.desc("code")) .list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<School> getSchoolByOVC( Patient patient )
    {
        //return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ) ).list();
        /*
        Set<Patient> patients = new HashSet<Patient>();
        
        patients.add( patient );
        
        Criteria criteria = getCriteria( Restrictions.eq( "patients", patients ) );
        
        return criteria.list();
        */
        
        //criteria.addOrder( Order.asc("name")) .list();
        
        
        //return  criteria.addOrder( Order.desc("code")) .list();
        /*
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( School.class );
        criteria.add( Restrictions.eq( "patients", patient ) );
        
        return criteria.list();
        */
        
       
        String hql = "from School s where :patient in elements(s.patients) ";
        Query query = sessionFactory.getCurrentSession().createQuery( hql );
        query.setEntity( "patient", patient );

        return query.list();
       
    }
    
}
