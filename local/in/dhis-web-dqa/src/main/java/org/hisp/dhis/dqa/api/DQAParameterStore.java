package org.hisp.dhis.dqa.api;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface DQAParameterStore extends GenericNameableObjectStore<DQAParameter>
{       
    String ID = DQAParameterStore.class.getName();
    
    // -------------------------------------------------------------------------
    // DQAParameter
    // -------------------------------------------------------------------------
    
}