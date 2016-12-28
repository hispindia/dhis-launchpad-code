package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRules;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BHARATH
 */
@Transactional
public class DefaultInstanceBusinessRulesService implements InstanceBusinessRulesService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private InstanceBusinessRulesStore instanceBusinessRulesStore;

    public void setInstanceBusinessRulesStore( InstanceBusinessRulesStore instanceBusinessRulesStore )
    {
        this.instanceBusinessRulesStore = instanceBusinessRulesStore;
    }

    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------
    @Override
    public void addInstanceRules( InstanceBusinessRules instanceBusinessRules )
    {
        instanceBusinessRulesStore.save( instanceBusinessRules );
    }

    @Override
    public void updateInstanceRules( InstanceBusinessRules instanceBusinessRules )
    {
        instanceBusinessRulesStore.update( instanceBusinessRules );        
    }

    @Override
    public void deleteInstanceRules( InstanceBusinessRules instanceBusinessRules )
    {
        instanceBusinessRulesStore.delete( instanceBusinessRules );        
    }

    @Override
    public InstanceBusinessRules getInstanceRules( int id )
    {
        return instanceBusinessRulesStore.get( id );
    }
    
    @Override
    public InstanceBusinessRules getInstanceRulesByInstance( SynchInstance instance )
    {
        return instanceBusinessRulesStore.getInstanceRulesByInstance( instance );
    }
    
    public Collection<SynchInstance> getInstancesForApprovalUser( User user )
    {
        return instanceBusinessRulesStore.getInstancesForApprovalUser( user );
    }
}
