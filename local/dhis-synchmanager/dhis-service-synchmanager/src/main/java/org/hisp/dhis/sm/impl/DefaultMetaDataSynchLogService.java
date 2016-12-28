package org.hisp.dhis.sm.impl;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLog;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLogService;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLogStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultMetaDataSynchLogService implements MetaDataSynchLogService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private MetaDataSynchLogStore metaDataSynchLogStore;

    public void setMetaDataSynchLogStore( MetaDataSynchLogStore metaDataSynchLogStore )
    {
        this.metaDataSynchLogStore = metaDataSynchLogStore;
    }

    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------
    @Override
    public void addMetaDataSynchLog( MetaDataSynchLog metaDataSynchLog )
    {
        metaDataSynchLogStore.addMetaDataSynchLog( metaDataSynchLog );        
    }

    @Override
    public void updateMetaDataSynchLog( MetaDataSynchLog metaDataSynchLog )
    {
        metaDataSynchLogStore.updateMetaDataSynchLog( metaDataSynchLog );        
    }

    @Override
    public void deleteMetaDataSynchLog( MetaDataSynchLog metaDataSynchLog )
    {
        metaDataSynchLogStore.deleteMetaDataSynchLog( metaDataSynchLog );        
    }

    @Override
    public Collection<MetaDataSynchLog> getMetaDataSynchLog( SynchInstance instance, Date logDate )
    {
        return metaDataSynchLogStore.getMetaDataSynchLog( instance, logDate );
    }

    @Override
    public Collection<MetaDataSynchLog> getMetaDataSynchLogBetweenDates( SynchInstance instance, Date logStartDate, Date logEndDate )
    {
        return metaDataSynchLogStore.getMetaDataSynchLogBetweenDates( instance, logStartDate, logEndDate );
    }    
    
}
