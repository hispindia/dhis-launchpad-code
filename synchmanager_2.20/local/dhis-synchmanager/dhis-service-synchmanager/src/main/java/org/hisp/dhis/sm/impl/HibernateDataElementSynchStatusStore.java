package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatus;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BHARATH
 */
@Transactional
public class HibernateDataElementSynchStatusStore
    implements DataElementSynchStatusStore
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
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getApprovedDataElements( )
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "SELECT de FROM DataElement de, DataElementSynchStatus dess " +
            " WHERE de.uid = dess.dataElement.uid and dess.status = '"+DataElementSynchStatus.SYNCH_STATUS_APPROVED+"'" ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getUpdatedDataElements()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createQuery( "SELECT de FROM DataElement de, DataElementSynchStatus dess " +
                                " WHERE de.lastUpdated >= dess.lastUpdated" ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<DataElementSynchStatus> getUpdatedDataElementSyncStatus()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createQuery( "SELECT dess FROM DataElementSynchStatus dess , DataElement de " +
                                " WHERE dess.lastUpdated <= de.lastUpdated and dess.dataElement.id = de.id" ).list();
    }    
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getNewDataElements()
    {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "select de FROM DataElement de where de.id not in ( select dess.dataElement.id from DataElementSynchStatus dess )" ).list();
    }

    @Override
    public DataElementSynchStatus getStatusByInstanceAndDataElement( SynchInstance instance, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElementSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        
        return (DataElementSynchStatus) criteria.uniqueResult();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<DataElementSynchStatus> getStatusByInstance( SynchInstance instance )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElementSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );

        return criteria.list();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<DataElementSynchStatus> getStatusByDataElement( DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DataElementSynchStatus.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }
    
    @Override
    public void addDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( dataElementSynchStatus );
    }

    @Override
    public void updateDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( dataElementSynchStatus );
    }

    @Override
    public void deleteDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataElementSynchStatus );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<DataElementSynchStatus> getSynchStausByDataElements( Collection<DataElement> dataElements) 
    {
       // Session session = sessionFactory.getCurrentSession();
        //return null;
        
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( DataElementSynchStatus.class );
        
        if( dataElements!= null && dataElements.size() > 0 )
        {
            criteria.add( Restrictions.in( "dataElement", dataElements ) );
            
        }
        
        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataElementSynchStatus> getAllDataElementSynchStatus()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( DataElementSynchStatus.class ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataElementSynchStatus> getPendingDataElementSyncStatus( SynchInstance instance )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( DataElementSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add(( Restrictions.not( Restrictions.like( "status", "accepted" ) ) ) );
        criteria.add(( Restrictions.not( Restrictions.like( "status", "submitted" ) ) ) );

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getDataElementListByInstance( SynchInstance instance )
    {
       /* Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( DataElementSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );

        return criteria.list();*/
        
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT de FROM DataElement de where de.id in ( SELECT dess.dataElement.id from DataElementSynchStatus dess WHERE dess.instance.id ="+instance.getId()+" )" ).list();
        
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DataElement> getApprovedDataElementListByInstance( SynchInstance instance )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( DataElementSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.like( "status", "approved" )  );
        return criteria.list();
        */
        
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT de FROM DataElement de where de.id in ( SELECT dess.dataElement.id from DataElementSynchStatus dess WHERE dess.instance.id ="+instance.getId()+" AND dess.status = '"+DataElementSynchStatus.SYNCH_STATUS_APPROVED+"' )" ).list();
        
    }    
    
    
   
}
