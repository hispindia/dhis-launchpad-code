package org.hisp.dhis.sm.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    /*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    public I18nFormat getFormat()
    {
        return format;
    }
    */
    
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
    public Collection<MetaDataSynchLog> getAllMetaDataSynchLog()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( MetaDataSynchLog.class );
        return criteria.list();
    }    
        
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<MetaDataSynchLog> getMetaDataSynchLog( SynchInstance instance )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( MetaDataSynchLog.class );
        criteria.add( Restrictions.eq( "synchInstance", instance ) );
        
        return criteria.list();
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
        //System.out.println( "Inside getMetaDataSynchLogBetweenDates hibernate start date " + logStartDate + " -- " + logEndDate  + " -- " + instance.getId() );
        
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( MetaDataSynchLog.class );
        criteria.add( Restrictions.eq( "synchInstance", instance ) );
        //criteria.add( Restrictions.eq( "synchDate", logDate ) );
        
        criteria.add(Restrictions.ge("synchDate", logStartDate ));
        criteria.add(Restrictions.le("synchDate", logEndDate ));
        
        //criteria.add(Restrictions.between("synchDate", logStartDate, logEndDate ));
        
        //criteria.add(Restrictions.between("auditDate", sDate, eDate));
        
        //System.out.println( "Inside getMetaDataSynchLogBetweenDates  List Size is " + criteria.list().size() );
        
        return criteria.list();
    }
    
    @Override
    
    public Collection<MetaDataSynchLog> getMetaDataSynchLogBetweenDates( SynchInstance instance, String logStartDate, String logEndDate )
    {
        //System.out.println( "Inside getMetaDataSynchLogBetweenDates hibernate start date " + logStartDate + " -- " + logEndDate  + " -- " + instance.getId() );
        
        List<MetaDataSynchLog> metaDataSynchLogList = new ArrayList<MetaDataSynchLog>();
        
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            Date startDate = dateFormat.parse( logStartDate );
            Date endDate = dateFormat.parse( logEndDate );
            
            metaDataSynchLogList = new ArrayList<MetaDataSynchLog>( getMetaDataSynchLogBetweenDates( instance, startDate, endDate ) );
            
            //System.out.println( "Inside getMetaDataSynchLogBetweenDates  List Size is " + metaDataSynchLogList.size() );
            
            return metaDataSynchLogList;
            
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
        
        
    }
    
}
