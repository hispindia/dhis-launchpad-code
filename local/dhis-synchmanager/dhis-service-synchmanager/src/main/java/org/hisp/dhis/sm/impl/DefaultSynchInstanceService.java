package org.hisp.dhis.sm.impl;

import java.util.Collection;
import java.util.Set;

import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BHARATH
 */
@Transactional
public class DefaultSynchInstanceService implements SynchInstanceService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private SynchInstanceStore synchInstanceStore;

    public void setSynchInstanceStore( SynchInstanceStore synchInstanceStore )
    {
        this.synchInstanceStore = synchInstanceStore;
    }

    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------
    @Override
    public void addInstance( SynchInstance instance )
    {
        synchInstanceStore.save( instance );
    }

    @Override
    public void updateInstance( SynchInstance instance )
    {
        synchInstanceStore.update( instance );        
    }

    @Override
    public void deleteInstance( SynchInstance instance )
    {
        synchInstanceStore.delete( instance );        
    }

    @Override
    public SynchInstance getInstance( int id )
    {
        return synchInstanceStore.get( id );
    }

    @Override
    public SynchInstance getInstance( String uid )
    {
        return synchInstanceStore.getByUid( uid );
    }

    @Override
    public Set<SynchInstance> getInstancesByType( String synchType )
    {
        return synchInstanceStore.getInstancesByType( synchType );
    }
    
    public SynchInstance getInstanceByUrl( String instanceUrl )
    {
        return synchInstanceStore.getInstanceByUrl( instanceUrl );
    }
    

    @Override
    public SynchInstance getInstanceByName( String name )
    {
        return synchInstanceStore.getInstanceByName( name );
    }
    
    @Override
    public SynchInstance getInstanceByNameAndSynchType( String name, String  synchType)
    {
        return synchInstanceStore.getInstanceByNameAndSynchType( name, synchType );
    }
    
    @Override
    public SynchInstance getInstanceByUrlAndSynchType( String url, String  synchType)
    {
        return synchInstanceStore.getInstanceByUrlAndSynchType( url, synchType);
    }
    
    @Override
    public Collection<SynchInstance> getInstancesByInstanceType( String type )
    {
        return synchInstanceStore.getInstancesByInstanceType( type );
    }    
    
}
