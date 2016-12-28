package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hibernate.Session;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.sm.api.DataElementSynchStatus;
import org.hisp.dhis.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.sm.api.DataElementSynchStatusStore;
import org.hisp.dhis.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BHARATH
 */
@Transactional
public class DefaultDataElementSynchStatusService implements DataElementSynchStatusService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataElementSynchStatusStore dataElementSynchStatusStore;

    public void setDataElementSynchStatusStore( DataElementSynchStatusStore dataElementSynchStatusStore )
    {
        this.dataElementSynchStatusStore = dataElementSynchStatusStore;
    }

    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------

    @Override
    public void addDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus )
    {
        dataElementSynchStatusStore.addDataElementSynchStatus( dataElementSynchStatus );
    }

    @Override
    public void updateDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus )
    {
        dataElementSynchStatusStore.updateDataElementSynchStatus( dataElementSynchStatus );
    }

    @Override
    public void deleteDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus )
    {
        dataElementSynchStatusStore.deleteDataElementSynchStatus( dataElementSynchStatus );
    }

    @Override
    public DataElementSynchStatus getStatusByInstanceAndDataElement( SynchInstance instance, DataElement dataElement )
    {
        return dataElementSynchStatusStore.getStatusByInstanceAndDataElement( instance, dataElement );
    }

    @Override
    public Collection<DataElementSynchStatus> getStatusByInstance( SynchInstance instance )
    {
        return dataElementSynchStatusStore.getStatusByInstance( instance );
    }
    
    public Collection<DataElement> getUpdatedDataElements()
    {
        return dataElementSynchStatusStore.getUpdatedDataElements();
    }

    public Collection<DataElement> getNewDataElements()
    {
        return dataElementSynchStatusStore.getNewDataElements();
    }
    
}
