package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatus;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.IndicatorSynchStatusStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultIndicatorSynchStatusService implements IndicatorSynchStatusService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private IndicatorSynchStatusStore indicatorSynchStatusStore;

    public void setIndicatorSynchStatusStore( IndicatorSynchStatusStore indicatorSynchStatusStore )
    {
        this.indicatorSynchStatusStore = indicatorSynchStatusStore;
    }
    
    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------

    @Override
    public void addIndicatorSynchStatus( IndicatorSynchStatus indicatorSynchStatus )
    {
        indicatorSynchStatusStore.addIndicatorSynchStatus( indicatorSynchStatus );
    }

    @Override
    public void updateIndicatorSynchStatus( IndicatorSynchStatus indicatorSynchStatus )
    {
        indicatorSynchStatusStore.updateIndicatorSynchStatus( indicatorSynchStatus );
    }

    @Override
    public void deleteIndicatorSynchStatus( IndicatorSynchStatus indicatorSynchStatus )
    {
        indicatorSynchStatusStore.deleteIndicatorSynchStatus( indicatorSynchStatus );
    }

    @Override
    public IndicatorSynchStatus getStatusByInstanceAndIndicator( SynchInstance instance, Indicator indicator )
    {
        return indicatorSynchStatusStore.getStatusByInstanceAndIndicator( instance, indicator );
    }

    @Override
    public Collection<IndicatorSynchStatus> getStatusByInstance( SynchInstance instance )
    {
        return indicatorSynchStatusStore.getStatusByInstance( instance );
    }
    
    public Collection<Indicator> getNewIndicators()
    {
        return indicatorSynchStatusStore.getNewIndicators();
    }
    
    public Collection<Indicator> getUpdatedIndicators()
    {
        return indicatorSynchStatusStore.getUpdatedIndicators();
    }

    public Collection<IndicatorSynchStatus> getUpdatedIndicatorSyncStatus()
    {
        return indicatorSynchStatusStore.getUpdatedIndicatorSyncStatus();
    }    
    
    
    public Collection<Indicator> getApprovedIndicators()
    {
        return indicatorSynchStatusStore.getApprovedIndicators();
    }

    @Override
    public Collection<IndicatorSynchStatus> getSynchStausByIndicator( Indicator indicator )
    {
        return indicatorSynchStatusStore.getSynchStausByIndicator( indicator );
    }    
    
    @Override
    public Collection<IndicatorSynchStatus> getSynchStausByIndicators(Collection<Indicator> indicators )
    {
        return indicatorSynchStatusStore.getSynchStausByIndicators( indicators );
    }
    
    public Collection<IndicatorSynchStatus> getAllIndicatorSynchStatus()
    {
        return indicatorSynchStatusStore.getAllIndicatorSynchStatus();
    } 
    
    public Collection<IndicatorSynchStatus> getPendingIndicatorSyncStatus( SynchInstance instance )
    {
        return indicatorSynchStatusStore.getPendingIndicatorSyncStatus( instance );
    }    
    
    public Collection<Indicator> getIndicatorByInstance( SynchInstance instance )
    {
        return indicatorSynchStatusStore.getIndicatorByInstance( instance );
    }
    
    public Collection<Indicator> getApprovedIndicatorByInstance( SynchInstance instance )
    {
        return indicatorSynchStatusStore.getApprovedIndicatorByInstance( instance );
    }
    
}

