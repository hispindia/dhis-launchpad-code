package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatus;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BHARATH
 */
@Transactional
public class DefaultDataElementSynchStatusService
    implements DataElementSynchStatusService
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

    public Collection<DataElementSynchStatus> getUpdatedDataElementSyncStatus()
    {
        return dataElementSynchStatusStore.getUpdatedDataElementSyncStatus();
    }

    public Collection<DataElement> getNewDataElements()
    {
        return dataElementSynchStatusStore.getNewDataElements();
    }

    public Collection<DataElement> getApprovedDataElements()
    {
        return dataElementSynchStatusStore.getApprovedDataElements();
    }

    @Override
    public Collection<DataElementSynchStatus> getSynchStausByDataElements( Collection<DataElement> dataElements )
    {
        return dataElementSynchStatusStore.getSynchStausByDataElements( dataElements );

        //return null;
    }
    
    public Collection<DataElementSynchStatus> getAllDataElementSynchStatus()
    {
        return dataElementSynchStatusStore.getAllDataElementSynchStatus();
    }    
    
    public Collection<DataElementSynchStatus> getStatusByDataElement( DataElement dataElement )
    {
        return dataElementSynchStatusStore.getStatusByDataElement( dataElement );
    } 
    
    public Collection<DataElementSynchStatus> getPendingDataElementSyncStatus( SynchInstance instance )
    {
        return dataElementSynchStatusStore.getPendingDataElementSyncStatus( instance );
    }    
 
    public Collection<DataElement> getDataElementListByInstance( SynchInstance instance )
    {
        return dataElementSynchStatusStore.getDataElementListByInstance( instance );
    }
    
    public Collection<DataElement> getApprovedDataElementListByInstance( SynchInstance instance )
    {
        return dataElementSynchStatusStore.getApprovedDataElementListByInstance( instance );
    }
    
}
