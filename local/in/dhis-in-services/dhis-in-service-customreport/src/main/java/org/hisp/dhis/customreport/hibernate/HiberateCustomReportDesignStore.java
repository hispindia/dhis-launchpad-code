package org.hisp.dhis.customreport.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportDesign;
import org.hisp.dhis.customreports.CustomReportDesignStore;

/**
 * @author Mithilesh Kumar Thakur
 */

public class HiberateCustomReportDesignStore implements CustomReportDesignStore
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
    // CustomReportDesign
    // -------------------------------------------------------------------------
    @Override
    public int addCustomReportDesign( CustomReportDesign customReportDesign )
    {
        Session session = sessionFactory.getCurrentSession();
        
        return (Integer) session.save( customReportDesign );
        //session.save( customReportDesign );
    }
    @Override
    public void updateCustomReportDesign( CustomReportDesign customReportDesign )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( customReportDesign );
    }
    @Override
    public void deleteCustomReportDesign( CustomReportDesign customReportDesign )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( customReportDesign );
    }
    

    @Override
    public CustomReportDesign getCustomReportDesignById( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (CustomReportDesign) session.get( CustomReportDesign.class, id );
    }    
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReportDesign> getAllCustomReportDesign()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createCriteria( CustomReportDesign.class ).list();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReportDesign> getAllCustomReportDesignByCustomReport( CustomReport customReport )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomReportDesign.class );
        criteria.add( Restrictions.eq( "customReport", customReport ) );

        return criteria.list();
    }    

    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReportDesign> getAllCustomReportDesignByCustomType( String customType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomReportDesign.class );
        criteria.add( Restrictions.eq( "customType", customType ) );

        return criteria.list();
    }        
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReportDesign> getAllCustomReportDesignByCustomReportAndCustomType( CustomReport customReport, String customType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomReportDesign.class );
        criteria.add( Restrictions.eq( "customReport", customReport ) );
        criteria.add( Restrictions.eq( "customType", customType ) );

        return criteria.list();
    }            
    
}
