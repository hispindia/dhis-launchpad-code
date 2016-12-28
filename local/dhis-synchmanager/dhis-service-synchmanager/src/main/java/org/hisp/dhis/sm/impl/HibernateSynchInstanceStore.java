package org.hisp.dhis.sm.impl;

import java.util.HashSet;
import java.util.Set;

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
    
    public SynchInstance getInstanceByUrl( String instanceUrl )
    {
        return (SynchInstance) getCriteria( Restrictions.eq( "url", instanceUrl ) ).uniqueResult();
    }
}
