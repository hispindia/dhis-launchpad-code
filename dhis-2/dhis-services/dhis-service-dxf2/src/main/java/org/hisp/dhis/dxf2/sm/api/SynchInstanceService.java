package org.hisp.dhis.dxf2.sm.api;

import java.util.Set;

/**
 * @author BHARATH
 */
public interface SynchInstanceService
{
    String ID = SynchInstanceService.class.getName();
    
    void addInstance( SynchInstance instance );
    
    void updateInstance( SynchInstance instance );
    
    void deleteInstance( SynchInstance instance );
    
    SynchInstance getInstance( int id );
    
    SynchInstance getInstance( String uid );
    
    Set<SynchInstance> getInstancesByType( String synchType );
    
    SynchInstance getInstanceByUrl( String instanceUrl );
}
