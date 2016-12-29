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
package org.hisp.dhis.customreport.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportStore;
import org.hisp.dhis.user.User;

/**
 * @author Mithilesh Kumar Thakur
 */

public class HiberateCustomReportStore implements CustomReportStore
{
   
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // CustomReport
    // -------------------------------------------------------------------------
    @Override
    public int addCustomReport( CustomReport customReport )
    {
        Session session = sessionFactory.getCurrentSession();
        
        return (Integer) session.save( customReport );
        //session.save( customReport );
    }
    
    @Override
    public void updateCustomReport( CustomReport customReport )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( customReport );
    }
    
    @Override
    public void deleteCustomReport( CustomReport customReport )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( customReport );
    }

 
    @Override
    public CustomReport getCustomReportById( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (CustomReport) session.get( CustomReport.class, id );
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReport> getAllCustomReports()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createCriteria( CustomReport.class ).list();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReport> getAllCustomReportsByReportType( String reportType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomReport.class );
        criteria.add( Restrictions.eq( "reportType", reportType ) );

        return criteria.list();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReport> getAllCustomReportsByUser( User user )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomReport.class );
        criteria.add( Restrictions.eq( "user", user ) );

        return criteria.list();
    }
    
    

}
