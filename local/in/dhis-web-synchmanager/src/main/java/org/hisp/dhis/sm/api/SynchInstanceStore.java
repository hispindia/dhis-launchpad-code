package org.hisp.dhis.sm.api;

import java.util.Set;

import org.hisp.dhis.common.GenericNameableObjectStore;

/**
 * @author BHARATH
 */
public interface SynchInstanceStore extends GenericNameableObjectStore<SynchInstance>
{
    String ID = SynchInstanceStore.class.getName();

    Set<SynchInstance> getInstancesByType( String synchType );
}
