package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatus;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatusStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class HibernateOrganisationUnitSynchStatusStore implements OrganisationUnitSynchStatusStore
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
    // DataElementSynchStatus
    // -------------------------------------------------------------------------
    
    @Override
    public void addOrganisationUnitSynchStatus( OrganisationUnitSynchStatus organisationUnitSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( organisationUnitSynchStatus );
    }

    @Override
    public void updateOrganisationUnitSynchStatus( OrganisationUnitSynchStatus organisationUnitSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( organisationUnitSynchStatus );
    }

    @Override
    public void deleteOrganisationUnitSynchStatus( OrganisationUnitSynchStatus organisationUnitSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( organisationUnitSynchStatus );
    }
    
    @Override
    public OrganisationUnitSynchStatus getStatusByInstanceAndOrganisationUnit( SynchInstance instance, OrganisationUnit organisationUnit )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( OrganisationUnitSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );

        return (OrganisationUnitSynchStatus) criteria.uniqueResult();
    }    
    

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnitSynchStatus> getStatusByInstance( SynchInstance instance )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( OrganisationUnitSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );

        return criteria.list();
    }
    

    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> getNewOrganisationUnits()
    {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT organisationUnit FROM OrganisationUnit organisationUnit where organisationUnit.id not in ( SELECT organisationunitstatus.organisationUnit.id from OrganisationUnitSynchStatus organisationunitstatus )" ).list();
    }    
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> getUpdatedOrganisationUnits()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createQuery( "SELECT organisationUnit FROM OrganisationUnit organisationUnit, OrganisationUnitSynchStatus organisationunitstatus " +
                                " WHERE organisationUnit.lastUpdated >= organisationunitstatus.lastUpdated" ).list();
    }    
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnitSynchStatus> getUpdatedOrganisationUnitSyncStatus()
    {
        Session session = sessionFactory.getCurrentSession();
        
      /*  return session.createQuery( "SELECT organisationunitstatus FROM OrganisationUnitSynchStatus organisationunitstatus , OrganisationUnit organisationUnit " +
                                "  WHERE organisationUnit.lastUpdated >= organisationunitstatus.lastUpdated"  ).list();*/
        
        return session.createQuery( "SELECT organisationunitstatus FROM OrganisationUnitSynchStatus organisationunitstatus , OrganisationUnit organisationUnit " +
                "  WHERE organisationunitstatus.lastUpdated <= organisationUnit.lastUpdated "
                + "and organisationunitstatus.organisationUnit.id = organisationUnit.id"  ).list();
    }        
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> getApprovedOrganisationUnits()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "SELECT organisationUnit FROM OrganisationUnit organisationUnit, OrganisationUnitSynchStatus organisationunitstatus " +
            " WHERE organisationUnit.uid = organisationunitstatus.organisationUnit.uid and organisationunitstatus.status = '"+ OrganisationUnitSynchStatus.SYNCH_STATUS_APPROVED+"'" ).list();
    }
    

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<OrganisationUnitSynchStatus> getSynchStausByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( OrganisationUnitSynchStatus.class );
        
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        
        return criteria.list();
        
    }    
    
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<OrganisationUnitSynchStatus> getSynchStausByOrganisationUnits( Collection<OrganisationUnit> organisationUnits )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( OrganisationUnitSynchStatus.class );
        
        if( organisationUnits!= null && organisationUnits.size() > 0 )
        {
            criteria.add( Restrictions.in( "organisationUnit", organisationUnits ) );
        }
        
        return criteria.list();
        
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnitSynchStatus> getAllOrganisationUnitSynchStatus()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( OrganisationUnitSynchStatus.class ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnitSynchStatus> getPendingOrganisationUnitSyncStatus( SynchInstance instance )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( OrganisationUnitSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add(( Restrictions.not( Restrictions.like( "status", "accepted" ) ) ) );
        criteria.add(( Restrictions.not( Restrictions.like( "status", "submitted" ) ) ) );

        return criteria.list();
    }       
    

    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> getOrganisationUnitByInstance( SynchInstance instance )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( OrganisationUnitSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );

        return criteria.list();
        */
        
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT organisationUnit FROM OrganisationUnit organisationUnit where organisationUnit.id in ( SELECT organisationunitstatus.organisationUnit.id from OrganisationUnitSynchStatus organisationunitstatus WHERE organisationunitstatus.instance.id ="+instance.getId()+" )" ).list();
        
        
        
        
    }           
    
    @SuppressWarnings( "unchecked" )
    public Collection<OrganisationUnit> getApprovedOrganisationUnitByInstance( SynchInstance instance )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( OrganisationUnitSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.like( "status", "approved" )  );
        
        return criteria.list();
        */
        
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT organisationUnit FROM OrganisationUnit organisationUnit where organisationUnit.id in ( SELECT organisationunitstatus.organisationUnit.id from OrganisationUnitSynchStatus organisationunitstatus WHERE organisationunitstatus.instance.id ="+instance.getId()+" AND organisationunitstatus.status = '"+OrganisationUnitSynchStatus.SYNCH_STATUS_APPROVED+"' )" ).list();
        
        
        
        
        
    }           
    
}
