package org.hisp.dhis.sm.action;

import org.hisp.dhis.sm.api.SynchInstance;
import org.hisp.dhis.sm.api.SynchInstanceService;

import com.opensymphony.xwork2.Action;

public class GetInstanceAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependecies
    // -------------------------------------------------------------------------

    private SynchInstanceService synchInstanceService;
    
    public void setSynchInstanceService( SynchInstanceService synchInstanceService )
    {
        this.synchInstanceService = synchInstanceService;
    }
    
    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private SynchInstance instance;
    
    public SynchInstance getInstance()
    {
        return instance;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    public String execute()
    {
        instance = synchInstanceService.getInstance( id );
        
        return SUCCESS;
    }
}
