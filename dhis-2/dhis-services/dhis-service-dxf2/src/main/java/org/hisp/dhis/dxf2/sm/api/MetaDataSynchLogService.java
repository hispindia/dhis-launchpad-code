package org.hisp.dhis.dxf2.sm.api;

import java.util.Collection;
import java.util.Date;

public interface MetaDataSynchLogService
{
    String ID = MetaDataSynchLogService.class.getName();
    
    void addMetaDataSynchLog( MetaDataSynchLog metaDataSynchLog );
    
    void updateMetaDataSynchLog( MetaDataSynchLog metaDataSynchLog );
    
    void deleteMetaDataSynchLog( MetaDataSynchLog metaDataSynchLog );
    
    Collection<MetaDataSynchLog> getAllMetaDataSynchLog();
    
    Collection<MetaDataSynchLog> getMetaDataSynchLog( SynchInstance instance );
    
    Collection<MetaDataSynchLog> getMetaDataSynchLog( SynchInstance instance, Date logStartDate );
    
    Collection<MetaDataSynchLog> getMetaDataSynchLogBetweenDates( SynchInstance instance, Date logStartDate, Date logEndDate );
    
    Collection<MetaDataSynchLog> getMetaDataSynchLogBetweenDates( SynchInstance instance, String logStartDate, String logEndDate );
} 
