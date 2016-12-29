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
package org.hisp.dhis.patientdataentrylock.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdataentrylock.PatientDataEntryLock;
import org.hisp.dhis.patientdataentrylock.PatientDataEntryLockStore;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class HiberatePatientDataEntryLockStore implements PatientDataEntryLockStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
   
    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
    
    /*
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    */
    
    // -------------------------------------------------------------------------
    // PatientDataEntryLock
    // -------------------------------------------------------------------------
    
    @Override
    public void addLock( PatientDataEntryLock patientDataEntryLock )
    {
        patientDataEntryLock.setPeriod( periodStore.reloadForceAddPeriod( patientDataEntryLock.getPeriod() ) );
    	
    	Session session = sessionFactory.getCurrentSession();

        session.save( patientDataEntryLock );
    }
    
    @Override
    public void updateLock( PatientDataEntryLock patientDataEntryLock )
    {
        patientDataEntryLock.setPeriod( periodStore.reloadForceAddPeriod( patientDataEntryLock.getPeriod() ) );
        
        Session session = sessionFactory.getCurrentSession();

        session.update( patientDataEntryLock );
        
    }
    
    @Override
    public void deleteLock( PatientDataEntryLock patientDataEntryLock )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( patientDataEntryLock );
    }
    
    @Override
    public PatientDataEntryLock getPatientDataEntryLock( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return ( PatientDataEntryLock ) session.get( PatientDataEntryLock.class, id );
    }
    
    
    public PatientDataEntryLock getPatientDataEntryLock( OrganisationUnit organisationUnit, Period period, Patient patient )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }
       
        Criteria criteria = session.createCriteria( PatientDataEntryLock.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "patient", patient ) );

        return ( PatientDataEntryLock ) criteria.uniqueResult();
        
    }    
    

    public PatientDataEntryLock getLockedPatientDataEntry( OrganisationUnit organisationUnit, Period period, Patient patient )
    {
        Session session = sessionFactory.getCurrentSession();
        
        
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }
        
        Criteria criteria = session.createCriteria( PatientDataEntryLock.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "patient", patient ) );
        
        criteria.add( Restrictions.eq( "lockStatus", true ) );
        
        return ( PatientDataEntryLock ) criteria.uniqueResult();
        
    }        
    
   
    @SuppressWarnings( "unchecked" )
    public Collection<PatientDataEntryLock> getAllPatientDataEntryLock()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createCriteria( PatientDataEntryLock.class ).list();
    }
    
 
}
