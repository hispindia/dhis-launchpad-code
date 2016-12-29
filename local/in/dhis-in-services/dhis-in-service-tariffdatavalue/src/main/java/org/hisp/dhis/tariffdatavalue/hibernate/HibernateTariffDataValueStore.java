package org.hisp.dhis.tariffdatavalue.hibernate;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.tariffdatavalue.TariffDataValue;
import org.hisp.dhis.tariffdatavalue.TariffDataValueStore;

/**
 * @author Mithilesh Kumar Thakur
 */
public class HibernateTariffDataValueStore implements TariffDataValueStore
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
    // TariffDataValue
    // -------------------------------------------------------------------------
    
   
    public void addTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( tariffDataValue );
    }

   
    public void updateTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( tariffDataValue );
    }

    
    public void deleteTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( tariffDataValue );
    }

    
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );        
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (TariffDataValue) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<TariffDataValue> getAllTariffDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "organisationUnitGroup", dataSet ) );

        return criteria.list();
    }

    
    @SuppressWarnings( "unchecked" )
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

}
