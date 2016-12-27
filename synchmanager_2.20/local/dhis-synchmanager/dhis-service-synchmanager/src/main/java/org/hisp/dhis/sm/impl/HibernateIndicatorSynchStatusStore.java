package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatus;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatusStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class HibernateIndicatorSynchStatusStore implements IndicatorSynchStatusStore
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
    public void addIndicatorSynchStatus( IndicatorSynchStatus indicatorSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( indicatorSynchStatus );
    }

    @Override
    public void updateIndicatorSynchStatus( IndicatorSynchStatus indicatorSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( indicatorSynchStatus );
    }

    @Override
    public void deleteIndicatorSynchStatus( IndicatorSynchStatus indicatorSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( indicatorSynchStatus );
    }
    
    @Override
    public IndicatorSynchStatus getStatusByInstanceAndIndicator( SynchInstance instance, Indicator indicator )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( IndicatorSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.eq( "indicator", indicator ) );

        return (IndicatorSynchStatus) criteria.uniqueResult();
    }    
    

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<IndicatorSynchStatus> getStatusByInstance( SynchInstance instance )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( IndicatorSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );

        return criteria.list();
    }
    

    @SuppressWarnings( "unchecked" )
    public Collection<Indicator> getNewIndicators()
    {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT indicator FROM Indicator indicator where indicator.id not in ( SELECT indicatorstatus.indicator.id from IndicatorSynchStatus indicatorstatus )" ).list();
    }    
    
    @SuppressWarnings( "unchecked" )
    public Collection<Indicator> getUpdatedIndicators()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createQuery( "SELECT indicator FROM Indicator indicator, IndicatorSynchStatus indicatorstatus " +
                                " WHERE indicator.lastUpdated >= indicatorstatus.lastUpdated" ).list();
    }    
    
    @SuppressWarnings( "unchecked" )
    public Collection<IndicatorSynchStatus> getUpdatedIndicatorSyncStatus()
    {
        Session session = sessionFactory.getCurrentSession();
        
      /*  return session.createQuery( "SELECT indicatorstatus FROM IndicatorSynchStatus indicatorstatus , Indicator indicator " +
                                "  WHERE indicator.lastUpdated >= indicatorstatus.lastUpdated"  ).list();*/
        
        return session.createQuery( "SELECT indicatorstatus FROM IndicatorSynchStatus indicatorstatus , Indicator indicator " +
                "  WHERE indicatorstatus.lastUpdated <= indicator.lastUpdated "
                + "and indicatorstatus.indicator.id = indicator.id ").list();
    }        
    
    @SuppressWarnings( "unchecked" )
    public Collection<Indicator> getApprovedIndicators()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "SELECT indicator FROM Indicator indicator, IndicatorSynchStatus indicatorstatus " +
            " WHERE indicator.uid = indicatorstatus.indicator.uid and indicatorstatus.status = '"+IndicatorSynchStatus.SYNCH_STATUS_APPROVED+"'" ).list();
    }
    

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<IndicatorSynchStatus> getSynchStausByIndicator( Indicator indicator ) 
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( IndicatorSynchStatus.class );
        
        criteria.add( Restrictions.eq( "indicator", indicator ) );
        
        return criteria.list();
        
    }    
    
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<IndicatorSynchStatus> getSynchStausByIndicators( Collection<Indicator> indicators ) 
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( IndicatorSynchStatus.class );
        
        if( indicators!= null && indicators.size() > 0 )
        {
            criteria.add( Restrictions.in( "indicator", indicators ) );
        }
        
        return criteria.list();
        
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<IndicatorSynchStatus> getAllIndicatorSynchStatus()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( IndicatorSynchStatus.class ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<IndicatorSynchStatus> getPendingIndicatorSyncStatus( SynchInstance instance )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( IndicatorSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add(( Restrictions.not( Restrictions.like( "status", "accepted" ) ) ) );
        criteria.add(( Restrictions.not( Restrictions.like( "status", "submitted" ) ) ) );
        return criteria.list();
    } 
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<Indicator> getIndicatorByInstance( SynchInstance instance )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( IndicatorSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        
        return criteria.list();
        */
        
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT indicator FROM Indicator indicator where indicator.id in ( SELECT indicatorstatus.indicator.id from IndicatorSynchStatus indicatorstatus WHERE indicatorstatus.instance.id ="+instance.getId()+" )" ).list();
        
    } 
    
    @SuppressWarnings( "unchecked" )
    public Collection<Indicator> getApprovedIndicatorByInstance( SynchInstance instance )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( IndicatorSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.like( "status", "approved" )  );
        return criteria.list();
        */
        
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT indicator FROM Indicator indicator where indicator.id in ( SELECT indicatorstatus.indicator.id from IndicatorSynchStatus indicatorstatus WHERE indicatorstatus.instance.id ="+instance.getId()+" AND indicatorstatus.status = '"+ IndicatorSynchStatus.SYNCH_STATUS_APPROVED+"' )" ).list();
        
    } 
    
    
}
