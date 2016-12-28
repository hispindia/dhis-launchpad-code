package org.hisp.dhis.sm.action;

import org.hisp.dhis.sm.api.SynchInstance;
import org.hisp.dhis.sm.api.SynchInstanceService;

import com.opensymphony.xwork2.Action;

public class AddInstanceAction implements Action
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
    // Setters & Getters
    // -------------------------------------------------------------------------
    private String synchType;
    
    public String getSynchType()
    {
        return synchType;
    }

    public void setSynchType( String synchType )
    {
        this.synchType = synchType;
    }
    
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }

    private String userId;

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    private String password;

    public void setPassword( String password )
    {
        this.password = password;
    }
    
    private Integer instanceId;
    
    public void setInstanceId( Integer instanceId )
    {
        this.instanceId = instanceId;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        SynchInstance instance = new SynchInstance();
        
        if( instanceId != null )
        {
            instance = synchInstanceService.getInstance( instanceId );
        }   
        
        instance.setName( name );
        instance.setUrl( url );
        instance.setUserId( userId );
        instance.setPassword( password );
        instance.setSynchType( synchType );
        
        if( instanceId != null )
        {
            synchInstanceService.updateInstance( instance );
        }
        else
        {
            synchInstanceService.addInstance( instance );
        }
        
        return SUCCESS;
    }
}
