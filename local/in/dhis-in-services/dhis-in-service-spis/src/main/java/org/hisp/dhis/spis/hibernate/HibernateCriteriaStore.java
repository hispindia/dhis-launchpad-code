package org.hisp.dhis.spis.hibernate;

import java.util.Collection;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.spis.Criteria;
import org.hisp.dhis.spis.CriteriaStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class HibernateCriteriaStore
    extends HibernateIdentifiableObjectStore<Criteria>
    implements CriteriaStore
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
    // Collections of Criterias
    // -------------------------------------------------------------------------
    
    @Override
    public int addCriteria( Criteria criteria )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( criteria );
    }
    
    @Override
    public void updateCriteria( Criteria criteria )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( criteria );
    }    
    
    @Override
    public void deleteCriteria( Criteria criteria )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( criteria );
    }
    
    @Override
    public Criteria getCriteriaById( int Id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Criteria) session.get( Criteria.class, Id );
    }    
    
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<Criteria> getAllCriteria()
    {
        Session session = sessionFactory.getCurrentSession();

        org.hibernate.Criteria criteria = session.createCriteria( Criteria.class );

        return criteria.list();
    }

}