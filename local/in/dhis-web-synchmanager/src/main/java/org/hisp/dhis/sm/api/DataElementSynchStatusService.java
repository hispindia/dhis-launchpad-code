package org.hisp.dhis.sm.api;

import org.hisp.dhis.dataelement.DataElement;

import java.util.Collection;

/**
 * @author BHARATH
 */
public interface DataElementSynchStatusService
{
    String ID = DataElementSynchStatusService.class.getName();
    
    void addDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus );
    
    void updateDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus );
    
    void deleteDataElementSynchStatus( DataElementSynchStatus dataElementSynchStatus );
    
    DataElementSynchStatus getStatusByInstanceAndDataElement( SynchInstance instance, DataElement dataElement );
    
    Collection<DataElementSynchStatus> getStatusByInstance( SynchInstance instance );
    
    Collection<DataElement> getUpdatedDataElements();

    Collection<DataElement> getNewDataElements();
		
}
