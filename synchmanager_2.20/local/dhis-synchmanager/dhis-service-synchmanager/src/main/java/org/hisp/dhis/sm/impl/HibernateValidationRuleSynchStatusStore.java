package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatus;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusStore;
import org.hisp.dhis.validation.ValidationRule;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class HibernateValidationRuleSynchStatusStore implements ValidationRuleSynchStatusStore
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
    public void addValidationRuleSynchStatus( ValidationRuleSynchStatus validationRuleSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( validationRuleSynchStatus );
    }

    @Override
    public void updateValidationRuleSynchStatus( ValidationRuleSynchStatus validationRuleSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( validationRuleSynchStatus );
    }

    @Override
    public void deleteValidationRuleSynchStatus( ValidationRuleSynchStatus validationRuleSynchStatus )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( validationRuleSynchStatus );
    }
    
    @Override
    public ValidationRuleSynchStatus getStatusByInstanceAndValidationRule( SynchInstance instance, ValidationRule validationRule )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ValidationRuleSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.eq( "validationRule", validationRule ) );

        return (ValidationRuleSynchStatus) criteria.uniqueResult();
    }    
    

    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<ValidationRuleSynchStatus> getStatusByInstance( SynchInstance instance )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( ValidationRuleSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );

        return criteria.list();
    }
    

    @SuppressWarnings( "unchecked" )
    public Collection<ValidationRule> getNewValidationRules()
    {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT validationRule FROM ValidationRule validationRule where validationRule.id not in ( SELECT validationRuleSynchStatus.validationRule.id from ValidationRuleSynchStatus validationRuleSynchStatus )" ).list();
    }    
    
    @SuppressWarnings( "unchecked" )
    public Collection<ValidationRule> getUpdatedValidationRules()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createQuery( "SELECT validationRule FROM ValidationRule validationRule, ValidationRuleSynchStatus validationRuleSynchStatus " +
                                " WHERE validationRule.lastUpdated >= validationRuleSynchStatus.lastUpdated" ).list();
    }    
    
    @SuppressWarnings( "unchecked" )
    public  Collection<ValidationRuleSynchStatus> getUpdatedValidationRuleSyncStatus()
    {
        Session session = sessionFactory.getCurrentSession();
        
        /*return session.createQuery( "SELECT validationRuleSynchStatus FROM ValidationRuleSynchStatus validationRuleSynchStatus , ValidationRule validationRule " +
                                "  WHERE validationRule.lastUpdated >= validationRuleSynchStatus.lastUpdated"  ).list();*/
        
        return session.createQuery( "SELECT validationRuleSynchStatus FROM ValidationRuleSynchStatus validationRuleSynchStatus , ValidationRule validationRule " +
                "  WHERE validationRuleSynchStatus.lastUpdated <= validationRule.lastUpdated "
                + "and validationRuleSynchStatus.validationRule.id = validationRule.id"  ).list();
    }        
    
    @SuppressWarnings( "unchecked" )
    public Collection<ValidationRule> getApprovedValidationRules()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery( "SELECT validationRule FROM ValidationRule validationRule, ValidationRuleSynchStatus validationRuleSynchStatus " +
            " WHERE validationRule.uid = validationRuleSynchStatus.validationRule.uid and validationRuleSynchStatus.status = '"+ ValidationRuleSynchStatus.SYNCH_STATUS_APPROVED+"'" ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<ValidationRuleSynchStatus> getSynchStausByValidationRule( ValidationRule validationRule )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( ValidationRuleSynchStatus.class );
        
        criteria.add( Restrictions.eq( "validationRule", validationRule ) );
        
        return criteria.list();
        
    }    
    
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<ValidationRuleSynchStatus> getSynchStausByValidationRules( Collection<ValidationRule> validationRules )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( ValidationRuleSynchStatus.class );
        
        if( validationRules!= null && validationRules.size() > 0 )
        {
            criteria.add( Restrictions.in( "validationRule", validationRules ) );
        }
        
        return criteria.list();
        
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<ValidationRuleSynchStatus> getAllValidationRuleSynchStatus()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( ValidationRuleSynchStatus.class ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<ValidationRuleSynchStatus> getPendingValidationRuleSyncStatus( SynchInstance instance )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( ValidationRuleSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add(( Restrictions.not( Restrictions.like( "status", "accepted" ) ) ) );
        criteria.add(( Restrictions.not( Restrictions.like( "status", "submitted" ) ) ) );

        return criteria.list();
    }       
    
    @SuppressWarnings( "unchecked" )
    public Collection<ValidationRule> getValidationRuleByInstance( SynchInstance instance )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( ValidationRuleSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );

        return criteria.list();
        */
        
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT validationRule FROM ValidationRule validationRule where validationRule.id in ( SELECT validationRuleSynchStatus.validationRule.id from ValidationRuleSynchStatus validationRuleSynchStatus  WHERE validationRuleSynchStatus.instance.id ="+instance.getId()+" )" ).list();
        
        
    } 
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<ValidationRule> getApprovedValidationRuleByInstance( SynchInstance instance )
    {
        /*
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( ValidationRuleSynchStatus.class );
        criteria.add( Restrictions.eq( "instance", instance ) );
        criteria.add( Restrictions.like( "status", "approved" )  );

        return criteria.list();
        */
        
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery( "SELECT validationRule FROM ValidationRule validationRule where validationRule.id in ( SELECT validationRuleSynchStatus.validationRule.id from ValidationRuleSynchStatus validationRuleSynchStatus WHERE validationRuleSynchStatus.instance.id ="+instance.getId()+" AND validationRuleSynchStatus.status = '"+ValidationRuleSynchStatus.SYNCH_STATUS_APPROVED+"' )" ).list();
        
    }
    
    
}
