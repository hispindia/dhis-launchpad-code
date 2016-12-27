package org.hisp.dhis.dxf2.sm.api;

import java.util.Collection;

import org.hisp.dhis.indicator.Indicator;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface IndicatorSynchStatusService
{ 
    String ID = IndicatorSynchStatusService.class.getName();
    
    void addIndicatorSynchStatus( IndicatorSynchStatus indicatorSynchStatus );
    
    void updateIndicatorSynchStatus( IndicatorSynchStatus indicatorSynchStatus );
    
    void deleteIndicatorSynchStatus( IndicatorSynchStatus indicatorSynchStatus );
    
    IndicatorSynchStatus getStatusByInstanceAndIndicator( SynchInstance instance, Indicator indicator );
    
    Collection<IndicatorSynchStatus> getStatusByInstance( SynchInstance instance );
    
    Collection<Indicator> getNewIndicators();
    
    Collection<Indicator> getUpdatedIndicators();
    
    Collection<IndicatorSynchStatus> getUpdatedIndicatorSyncStatus();

    Collection<Indicator> getApprovedIndicators();
    
    Collection<IndicatorSynchStatus> getSynchStausByIndicator( Indicator indicator );
    
    Collection<IndicatorSynchStatus> getSynchStausByIndicators(Collection<Indicator> indicators);
    
    Collection<IndicatorSynchStatus> getAllIndicatorSynchStatus();
    
    Collection<IndicatorSynchStatus> getPendingIndicatorSyncStatus( SynchInstance instance );
    
    Collection<Indicator> getIndicatorByInstance( SynchInstance instance );
    
    Collection<Indicator> getApprovedIndicatorByInstance( SynchInstance instance );

}
