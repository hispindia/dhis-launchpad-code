package org.hisp.dhis.dqa.impl.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dqa.api.*;

public class HibernateDQAReportCardDataValueStore implements DQAReportCardDataValueStore
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
    // Basic DataValue
    // -------------------------------------------------------------------------
    
    public void addDQAReportCardDataValue( DQAReportCardDataValue dqaReportCardDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( dqaReportCardDataValue );
    }
    
    
    public void updateDQAReportCardDataValue( DQAReportCardDataValue dqaReportCardDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( dqaReportCardDataValue );
    }
    
 
    public void deleteDQAReportCardDataValue( DQAReportCardDataValue dqaReportCardDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dqaReportCardDataValue );
    }
    

    public int deleteDataValuesByDQAParameter( String dqaParameter )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete DataValue where source = :source" );
        query.setEntity( "dqaParameter", dqaParameter );

        return query.executeUpdate();
    }
    
   
    public DQAReportCardDataValue getDQAReportCardDataValue( String level1, String level2, String year, String dqaParameter )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DQAReportCardDataValue.class );
        criteria.add( Restrictions.eq( "level1", level1 ) );
        criteria.add( Restrictions.eq( "level2", level2 ) );
        criteria.add( Restrictions.eq( "year", year ) );
        criteria.add( Restrictions.eq( "dqaParameter", dqaParameter ) );

        return (DQAReportCardDataValue) criteria.uniqueResult();
    }
    
    // -------------------------------------------------------------------------
    // Collections of DQAReportCardDataValues
    // -------------------------------------------------------------------------
  
    @SuppressWarnings( "unchecked" )
    public Collection<DQAReportCardDataValue> getAllDQAReportCardDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DQAReportCardDataValue.class );

        return criteria.list();
    }
    
  
    @SuppressWarnings( "unchecked" )
    public Collection<DQAReportCardDataValue> getAllDQAReportCardDataValues( String dqaParameter )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DQAReportCardDataValue.class );
        criteria.add( Restrictions.eq( "dqaParameter", dqaParameter ) );

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<DQAReportCardDataValue> getAllDQAReportCardDataValuesByLevelPeriod( String level1, String level2, String year )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( DQAReportCardDataValue.class );
        criteria.add( Restrictions.eq( "level1", level1 ) );
        criteria.add( Restrictions.eq( "level2", level2 ) );
        criteria.add( Restrictions.eq( "year", year ) );

        return criteria.list();
    }
    
    
    

}
