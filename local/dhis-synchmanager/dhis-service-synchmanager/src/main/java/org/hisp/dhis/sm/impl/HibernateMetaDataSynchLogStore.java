package org.hisp.dhis.sm.impl;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLog;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLogStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;

public class HibernateMetaDataSynchLogStore implements MetaDataSynchLogStore
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
    // MetaDataSynchLog
    // -------------------------------------------------------------------------
    @Override
    public void addMetaDataSynchLog( MetaDataSynchLog metaDataSynchLog )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( metaDataSynchLog );
    }

    @Override
    public void updateMetaDataSynchLog( MetaDataSynchLog metaDataSynchLog )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( metaDataSynchLog );
    }

    @Override
    public void deleteMetaDataSynchLog( MetaDataSynchLog metaDataSynchLog )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( metaDataSynchLog );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<MetaDataSynchLog> getMetaDataSynchLog( SynchInstance instance, Date logDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( MetaDataSynchLog.class );
        criteria.add( Restrictions.eq( "synchInstance", instance ) );
        criteria.add( Restrictions.eq( "synchDate", logDate ) );
        
        return criteria.list();
    }    
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<MetaDataSynchLog> getMetaDataSynchLogBetweenDates( SynchInstance instance, Date logStartDate, Date logEndDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( MetaDataSynchLog.class );
        criteria.add( Restrictions.eq( "synchInstance", instance ) );
        //criteria.add( Restrictions.eq( "synchDate", logDate ) );
        
        criteria.add(Restrictions.ge("synchDate", logStartDate ));
        criteria.add(Restrictions.le("synchDate", logEndDate ));
        
        //criteria.add(Restrictions.between("synchDate", logStartDate, logEndDate ));
        
        //criteria.add(Restrictions.between("auditDate", sDate, eDate));
        
        return criteria.list();
    }
    
}
