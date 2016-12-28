package org.hisp.dhis.sm.api;

import java.util.Collection;

import org.hisp.dhis.user.User;

import sun.security.jca.GetInstance.Instance;


/**
 * @author BHARATH
 */
public interface InstanceBusinessRulesService
{
    String ID = InstanceBusinessRulesService.class.getName();
    
    void addInstanceRules( InstanceBusinessRules instanceRules );
    
    void updateInstanceRules( InstanceBusinessRules instanceRules );
    
    void deleteInstanceRules( InstanceBusinessRules instanceRules );
    
    InstanceBusinessRules getInstanceRules( int id );
    
    InstanceBusinessRules getInstanceRulesByInstance( SynchInstance instance );
    
    Collection<SynchInstance> getInstancesForApprovalUser( User user );
}
