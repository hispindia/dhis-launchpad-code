package org.hisp.dhis.dxf2.sm.api;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.user.User;

import sun.security.jca.GetInstance.Instance;

/**
 * @author BHARATH
 */
public interface InstanceBusinessRulesStore extends GenericNameableObjectStore<InstanceBusinessRules>
{
    String ID = InstanceBusinessRulesStore.class.getName();
    
    InstanceBusinessRules getInstanceRulesByInstance( SynchInstance instance );
    
    Collection<SynchInstance> getInstancesForApprovalUser( User user );
}
