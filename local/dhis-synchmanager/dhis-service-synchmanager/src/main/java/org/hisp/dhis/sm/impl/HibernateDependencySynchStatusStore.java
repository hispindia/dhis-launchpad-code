package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatus;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatusStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class HibernateDependencySynchStatusStore implements DependencySynchStatusStore
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
    // DependencySynchStatus
    // -------------------------------------------------------------------------
    
    @Override
    public void addDependencySynchStatus( DependencySynchStatus dependencySynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( dependencySynchStatus );
    }

    @Override
    public void updateDependencySynchStatus( DependencySynchStatus dependencySynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( dependencySynchStatus );
    }

    @Override
    public void deleteDependencySynchStatus( DependencySynchStatus dependencySynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dependencySynchStatus );
    }

    @Override
    public DependencySynchStatus getDependencySynchStatuByUID( SynchInstance instance, String metaDataTypeUID, String dependencyTypeUID )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DependencySynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.eq( "metaDataTypeUID", metaDataTypeUID ) );
        criteria.add( Restrictions.eq( "dependencyTypeUID", dependencyTypeUID ) );
        
        return (DependencySynchStatus) criteria.uniqueResult();
    }    
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<DependencySynchStatus> getDependencySynchStatusByMetaDataTypeUID( SynchInstance instance, String metaDataTypeUID )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DependencySynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.eq( "metaDataTypeUID", metaDataTypeUID ) );

        return criteria.list();
    }

}

