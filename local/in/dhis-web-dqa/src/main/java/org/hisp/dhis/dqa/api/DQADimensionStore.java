package org.hisp.dhis.dqa.api;

import org.hisp.dhis.common.GenericNameableObjectStore;

public interface DQADimensionStore extends GenericNameableObjectStore<DQADimension>
{       
    String ID = DQADimensionStore.class.getName();
    
    // -------------------------------------------------------------------------
    // DQADimension
    // -------------------------------------------------------------------------
    
}
