package org.hisp.dhis.customreport.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportDesign;
import org.hisp.dhis.customreports.CustomReportFilter;
import org.hisp.dhis.customreports.CustomReportFilterStore;

/**
 * @author Mithilesh Kumar Thakur
 */

public class HiberateCustomReportFilterStore implements CustomReportFilterStore
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
    // CustomReportFilter
    // -------------------------------------------------------------------------
    @Override
    public int addCustomReportFilter( CustomReportFilter customReportFilter )
    {
        Session session = sessionFactory.getCurrentSession();
        
        return (Integer) session.save( customReportFilter );
        
        //session.save( customReportFilter );
    }
    @Override
    public void updateCustomReportFilter( CustomReportFilter customReportFilter )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( customReportFilter );
    }
    @Override
    public void deleteCustomReportFilter( CustomReportFilter customReportFilter )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( customReportFilter );
    }    
    
    @Override
    public CustomReportFilter getCustomReportFilterById( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (CustomReportFilter) session.get( CustomReportFilter.class, id );
    }    
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReportFilter> getAllCustomReportFilter()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createCriteria( CustomReportFilter.class ).list();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReportFilter> getAllCustomReportFilterByCustomReport( CustomReport customReport )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomReportFilter.class );
        criteria.add( Restrictions.eq( "customReport", customReport ) );

        return criteria.list();
    }    

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReportFilter> getAllCustomReportFilterByFilterType( String filterType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomReportDesign.class );
        criteria.add( Restrictions.eq( "filterType", filterType ) );

        return criteria.list();
    }        
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<CustomReportFilter> getAllCustomReportFilterByCustomReportAndFilterType( CustomReport customReport, String filterType )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CustomReportDesign.class );
        criteria.add( Restrictions.eq( "customReport", customReport ) );
        criteria.add( Restrictions.eq( "filterType", filterType ) );

        return criteria.list();
    }            
        
    
}    