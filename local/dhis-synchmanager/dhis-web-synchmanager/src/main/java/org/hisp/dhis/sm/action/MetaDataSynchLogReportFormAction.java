package org.hisp.dhis.sm.action;

import java.util.Set;
import java.util.TreeSet;

import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class MetaDataSynchLogReportFormAction implements Action
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

    Set<SynchInstance> instances = new TreeSet<SynchInstance>();
    
    public Set<SynchInstance> getInstances()
    {
        return instances;
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
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        instances.addAll( synchInstanceService.getInstancesByType( synchType ) );
        
        return SUCCESS;
    }
}

