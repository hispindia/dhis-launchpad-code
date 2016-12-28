package org.hisp.dhis.sm.action;

import org.hisp.dhis.sm.api.InstanceBusinessRules;
import org.hisp.dhis.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.sm.api.SynchInstance;
import org.hisp.dhis.sm.api.SynchInstanceService;

import com.opensymphony.xwork2.Action;

public class SetBusinessRulesAction implements Action
{

    private InstanceBusinessRulesService instanceBusinessRulesService;

    public void setInstanceBusinessRulesService( InstanceBusinessRulesService instanceBusinessRulesService )
    {
        this.instanceBusinessRulesService = instanceBusinessRulesService;
    }
    
    private SynchInstanceService synchInstanceService;
    
    public void setSynchInstanceService( SynchInstanceService synchInstanceService )
    {
        this.synchInstanceService = synchInstanceService;
    }

    private Integer instanceId;

    public void setInstanceId( Integer instanceId )
    {
        this.instanceId = instanceId;
    }

    private String userGroupForApproval;
    
    public void setUserGroupForApproval( String userGroupForApproval )
    {
        this.userGroupForApproval = userGroupForApproval;
    }
    
    private String userGroupForAcceptance;
    
    public void setUserGroupForAcceptance( String userGroupForAcceptance )
    {
        this.userGroupForAcceptance = userGroupForAcceptance;
    }

    private String synchType;
    
    public String getSynchType()
    {
        return synchType;
    }

    public String execute()
    {
        SynchInstance instance = synchInstanceService.getInstance( instanceId );

        synchType = instance.getSynchType();
        
        InstanceBusinessRules businessRules = instanceBusinessRulesService.getInstanceRulesByInstance( instance );
        
        if( businessRules == null )
        {
            businessRules = new InstanceBusinessRules();
            businessRules.setInstance( instance );
            businessRules.setApprovalUserGroupUid( userGroupForApproval );
            businessRules.setAcceptanceUserGroupUid( userGroupForAcceptance );
            
            instanceBusinessRulesService.addInstanceRules( businessRules );
        }
        else
        {
            businessRules.setApprovalUserGroupUid( userGroupForApproval );
            businessRules.setAcceptanceUserGroupUid( userGroupForAcceptance );

            instanceBusinessRulesService.updateInstanceRules( businessRules );
        }
        
        return SUCCESS;
    }
    
}
