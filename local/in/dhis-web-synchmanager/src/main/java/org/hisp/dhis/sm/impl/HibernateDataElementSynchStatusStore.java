package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.sm.api.DataElementSynchStatus;
import org.hisp.dhis.sm.api.DataElementSynchStatusStore;
import org.hisp.dhis.sm.api.SynchInstance;

/**
 * @author BHARATH
 */
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
    public Collection<DataElement> getUpdatedDataElements()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createQuery( "SELECT de FROM DataElement de, DataElementSynchStatus dess " +
                                " WHERE de.lastUpdated >= dess.lastUpdated" ).list();
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
}
