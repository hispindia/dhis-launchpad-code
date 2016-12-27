package org.hisp.dhis.sm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceStore;

/**
 * @author BHARATH
 */
public class HibernateSynchInstanceStore
    extends HibernateIdentifiableObjectStore<SynchInstance>
    implements SynchInstanceStore
{

    @Override
    @SuppressWarnings( "unchecked" )
    public Set<SynchInstance> getInstancesByType( String synchType )
    {
        return new HashSet<SynchInstance>( getCriteria( Restrictions.eq( "synchType", synchType ) ).list() );
    }
    
    @Override
    public SynchInstance getInstanceByUrl( String instanceUrl )
    {
        return (SynchInstance) getCriteria( Restrictions.eq( "url", instanceUrl ) ).uniqueResult();
    }
    
    @Override
    public SynchInstance getInstanceByName( String name )
    {
        return (SynchInstance) getCriteria( Restrictions.eq( "name", name ) ).uniqueResult();
    }
    
    @Override
    public SynchInstance getInstanceByNameAndSynchType( String name, String  synchType)
    {

        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "name", name ) );
        criteria.add( Restrictions.eq( "synchType", synchType ) );

        return (SynchInstance) criteria.uniqueResult();
    }
    
    @Override
    public SynchInstance getInstanceByUrlAndSynchType( String url, String  synchType)
    {

        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "url", url ) );
        criteria.add( Restrictions.eq( "synchType", synchType ) );

        return (SynchInstance) criteria.uniqueResult();
    }
    
    @Override
    @SuppressWarnings( "unchecked" )
    public Collection<SynchInstance> getInstancesByInstanceType( String type )
    {
        return new ArrayList<SynchInstance>( getCriteria( Restrictions.eq( "synchType", type ) ).list() );
    }
    
    
}
