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
package org.hisp.dhis.beneficiary.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class HiberateBeneficiaryStore implements BeneficiaryStore
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
    // -------------------------------------------------------------------------
    // Beneficiary
    // -------------------------------------------------------------------------
    
    @Override
    public int addBeneficiary( Beneficiary beneficiary )
    {
    	beneficiary.setPeriod( periodStore.reloadForceAddPeriod( beneficiary.getPeriod() ) );
    	
    	Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( beneficiary );
    }
    
    @Override
    public void updateBeneficiary( Beneficiary beneficiary )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( beneficiary );
        
    }
    
    @Override
    public void deleteBeneficiary( Beneficiary beneficiary )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( beneficiary );
    }
    
    @Override
    public Beneficiary getBeneficiaryById( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Beneficiary) session.get( Beneficiary.class, id );
    }
    
    public Beneficiary getBeneficiary( String identifier, DataElement dataElement, Period period )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }
        
        Criteria criteria = session.createCriteria( Beneficiary.class );
        criteria.add( Restrictions.eq( "identifier", identifier ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return (Beneficiary) criteria.uniqueResult();
        
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Beneficiary> getAllBeneficiary()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createCriteria( Beneficiary.class ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Beneficiary> getAllBeneficiaryByASHA( Patient patient )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Beneficiary.class );
        criteria.add( Restrictions.eq( "patient", patient ) );

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Beneficiary> getAllBeneficiaryByASHAAndPeriod( Patient patient, Period period )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }
        
        Criteria criteria = session.createCriteria( Beneficiary.class );
        criteria.add( Restrictions.eq( "patient", patient ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.addOrder( Order.asc( "name" ) );
        
        
        return criteria.list();
    }
    
    public int getCountByServicePeriodAndASHA( Patient patient, Period period, DataElement dataElement )
    {
        
        Session session = sessionFactory.getCurrentSession();
        
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return 0;
        }
        
        Criteria criteria = session.createCriteria( Beneficiary.class );
        criteria.add( Restrictions.eq( "patient", patient ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        
        Number rs = (Number) criteria.setProjection( Projections.rowCount() ).uniqueResult();
        
        return rs != null ? rs.intValue() : 0;
    }
   
}
