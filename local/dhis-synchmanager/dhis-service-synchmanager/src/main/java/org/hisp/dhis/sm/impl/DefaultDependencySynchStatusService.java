package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hisp.dhis.dxf2.sm.api.DependencySynchStatus;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatusService;
import org.hisp.dhis.dxf2.sm.api.DependencySynchStatusStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultDependencySynchStatusService
implements DependencySynchStatusService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DependencySynchStatusStore dependencySynchStatusStore;

    public void setDependencySynchStatusStore( DependencySynchStatusStore dependencySynchStatusStore )
    {
        this.dependencySynchStatusStore = dependencySynchStatusStore;
    }

    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------

    @Override
    public void addDependencySynchStatus( DependencySynchStatus dependencySynchStatus )
    {
        dependencySynchStatusStore.addDependencySynchStatus( dependencySynchStatus );
    }

    @Override
    public void updateDependencySynchStatus( DependencySynchStatus dependencySynchStatus )
    {
        dependencySynchStatusStore.updateDependencySynchStatus( dependencySynchStatus );
    }

    @Override
    public void deleteDependencySynchStatus( DependencySynchStatus dependencySynchStatus )
    {
        dependencySynchStatusStore.deleteDependencySynchStatus( dependencySynchStatus );
    }
    
    @Override
    public DependencySynchStatus getDependencySynchStatuByUID( SynchInstance instance, String metaDataTypeUID, String dependencyTypeUID )
    {
        return  dependencySynchStatusStore.getDependencySynchStatuByUID( instance, metaDataTypeUID, dependencyTypeUID  );
    }
    
    @Override
    public Collection<DependencySynchStatus> getDependencySynchStatusByMetaDataTypeUID( SynchInstance instance, String metaDataTypeUID )
    {
        return dependencySynchStatusStore.getDependencySynchStatusByMetaDataTypeUID( instance, metaDataTypeUID );
    }
    
}

