package org.hisp.dhis.dxf2.sm.api;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface DependencySynchStatusService
{
    String ID = DependencySynchStatusService.class.getName();
    
    void addDependencySynchStatus( DependencySynchStatus dependencySynchStatus );
    
    void updateDependencySynchStatus( DependencySynchStatus dependencySynchStatus );
    
    void deleteDependencySynchStatus( DependencySynchStatus dependencySynchStatus );
    
    DependencySynchStatus getDependencySynchStatuByUID( SynchInstance instance, String metaDataTypeUID, String dependencyTypeUID );
    
    Collection<DependencySynchStatus> getDependencySynchStatusByMetaDataTypeUID( SynchInstance instance, String metaDataTypeUID );
    
}
