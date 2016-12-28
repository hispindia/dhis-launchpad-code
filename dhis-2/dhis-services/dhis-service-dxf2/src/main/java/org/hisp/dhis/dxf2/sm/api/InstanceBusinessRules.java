package org.hisp.dhis.dxf2.sm.api;

import org.hisp.dhis.common.BaseIdentifiableObject;

/**
 * @author BHARATH
 */
public class InstanceBusinessRules extends BaseIdentifiableObject
{

    private String approvalUserGroupUid;
    
    private String acceptanceUserGroupUid;
    
    private SynchInstance instance;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public InstanceBusinessRules()
    {
        
    }
    
    public InstanceBusinessRules( String approvalUserGroupUid, String acceptanceUserGroupUid, SynchInstance instance )
    {
        this.approvalUserGroupUid = approvalUserGroupUid;
        
        this.acceptanceUserGroupUid = acceptanceUserGroupUid;
        
        this.instance = instance;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getApprovalUserGroupUid()
    {
        return approvalUserGroupUid;
    }

    public void setApprovalUserGroupUid( String approvalUserGroupUid )
    {
        this.approvalUserGroupUid = approvalUserGroupUid;
    }

    public String getAcceptanceUserGroupUid()
    {
        return acceptanceUserGroupUid;
    }

    public void setAcceptanceUserGroupUid( String acceptanceUserGroupUid )
    {
        this.acceptanceUserGroupUid = acceptanceUserGroupUid;
    }

    public SynchInstance getInstance()
    {
        return instance;
    }

    public void setInstance( SynchInstance instance )
    {
        this.instance = instance;
    }
    
    
}
