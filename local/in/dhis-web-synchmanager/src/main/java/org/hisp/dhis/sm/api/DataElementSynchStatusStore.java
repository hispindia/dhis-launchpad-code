package org.hisp.dhis.sm.api;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.dataelement.DataElement;

/**
 * @author BHARATH
 */
public interface DataElementSynchStatusStore
{
    String ID = DataElementSynchStatusStore.class.getName();
    
    void addDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus );
    
    void updateDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus );
    
    void deleteDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus );
    
    DataElementSynchStatus getStatusByInstanceAndDataElement( SynchInstance instance, DataElement dataElement );
    
    Collection<DataElementSynchStatus> getStatusByInstance( SynchInstance instance );
    
    Collection<DataElement> getUpdatedDataElements();

    Collection<DataElement> getNewDataElements();

}
