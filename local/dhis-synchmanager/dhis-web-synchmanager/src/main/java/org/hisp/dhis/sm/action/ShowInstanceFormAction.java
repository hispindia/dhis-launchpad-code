package org.hisp.dhis.sm.action;

import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;

import com.opensymphony.xwork2.Action;

public class ShowInstanceFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private SynchInstanceService synchInstanceService;

    public void setSynchInstanceService( SynchInstanceService synchInstanceService )
    {
        this.synchInstanceService = synchInstanceService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer instanceId;
    
    public void setInstanceId( Integer instanceId )
    {
        this.instanceId = instanceId;
    }

    private boolean update;

    public boolean isUpdate()
    {
        return update;
    }

    public void setUpdate( boolean update )
    {
        this.update = update;
    }

    private String synchType;
    
    public String getSynchType()
    {
        return synchType;
    }

    public void setSynchType( String synchType )
    {
        this.synchType = synchType;
    }

    private SynchInstance instance;
    
    public SynchInstance getInstance()
    {
        return instance;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if( instanceId != null || update )
        {
            instance = synchInstanceService.getInstance( instanceId );
        }
        
        return SUCCESS;
    }
}
