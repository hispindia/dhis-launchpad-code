package org.hisp.dhis.dxf2.sm.api;

import java.util.Collection;
import java.util.Set;

import org.hisp.dhis.common.GenericNameableObjectStore;

/**
 * @author BHARATH
 */
public interface SynchInstanceStore extends GenericNameableObjectStore<SynchInstance>
{
    String ID = SynchInstanceStore.class.getName();

    Set<SynchInstance> getInstancesByType( String synchType );
    
    SynchInstance getInstanceByName( String name );
    
    SynchInstance getInstanceByUrl( String instanceUrl );
    
    SynchInstance getInstanceByNameAndSynchType( String name, String  synchType);
    
    SynchInstance getInstanceByUrlAndSynchType( String url, String  synchType);
    
    Collection<SynchInstance> getInstancesByInstanceType( String type );
    
}
