package org.hisp.dhis.facilitator.hibernate;

import java.util.Collection;
import java.util.Collections;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorDataValue;
import org.hisp.dhis.facilitator.FacilitatorDataValueStore;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;

/**
 * @author Mithilesh Kumar Thakur
 */
public class HibernateFacilitatorDataValueStore implements FacilitatorDataValueStore
{ 
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
    
    /*
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    */
    
    // -------------------------------------------------------------------------
    // Basic FacilitatorDataValue
    // -------------------------------------------------------------------------

    public void addFacilitatorDataValue( FacilitatorDataValue facilitatorDataValue )
    {
        facilitatorDataValue.setPeriod( periodStore.reloadForceAddPeriod( facilitatorDataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.save( facilitatorDataValue );
    }

    public void updateFacilitatorDataValue( FacilitatorDataValue facilitatorDataValue )
    {
        facilitatorDataValue.setPeriod( periodStore.reloadForceAddPeriod( facilitatorDataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.update( facilitatorDataValue );
    }

    public void deleteFacilitatorDataValue( FacilitatorDataValue facilitatorDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( facilitatorDataValue );
    }

    public int deleteFacilitatorDataValuesByFacilitator( Facilitator facilitator )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete FacilitatorDataValue where facilitator = :facilitator" );
        query.setEntity( "facilitator", facilitator );

        return query.executeUpdate();
    }

    public int deleteFacilitatorDataValuesByDataElement( DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete FacilitatorDataValue where dataElement = :dataElement" );
        query.setEntity( "dataElement", dataElement );

        return query.executeUpdate();
    }

    public FacilitatorDataValue getFacilitatorDataValue( Facilitator facilitator, Patient patient, Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Criteria criteria = session.createCriteria( FacilitatorDataValue.class );
        criteria.add( Restrictions.eq( "facilitator", facilitator ) );
        criteria.add( Restrictions.eq( "patient", patient ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );

        return (FacilitatorDataValue) criteria.uniqueResult();
    }
    
    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public Collection<FacilitatorDataValue> getAllFacilitatorDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( FacilitatorDataValue.class );

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, Period period )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( FacilitatorDataValue.class );
        criteria.add( Restrictions.eq( "facilitator", facilitator ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return criteria.list();
    }    
    
    @SuppressWarnings( "unchecked" )
    public Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( FacilitatorDataValue.class );
        criteria.add( Restrictions.eq( "facilitator", facilitator ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, Period period, Collection<DataElement> dataElements )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null || dataElements == null || dataElements.isEmpty() )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( FacilitatorDataValue.class );
        criteria.add( Restrictions.eq( "facilitator", facilitator ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );

        return criteria.list();
    }

    
    @SuppressWarnings( "unchecked" )
    public Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, Period period, Collection<DataElement> dataElements, Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null || dataElements == null || dataElements.isEmpty() || optionCombos == null || optionCombos.isEmpty() )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( FacilitatorDataValue.class );
        criteria.add( Restrictions.eq( "facilitator", facilitator ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );
        criteria.add( Restrictions.in( "optionCombo", optionCombos ) );

        return criteria.list();
    }
      
}
