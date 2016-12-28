package org.hisp.dhis.sm.data.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class DataSynchConfigureInstancesAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private SynchInstanceService synchInstanceService;

    
    // -------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    private List<SynchInstance> dataSynchInstances =  new ArrayList<SynchInstance>();
    
    public List<SynchInstance> getDataSynchInstances()
    {
        return dataSynchInstances;
    }
    
    /*
    private Set<SynchInstance> dataSynchInstances = new TreeSet<SynchInstance>();
    
    public Set<SynchInstance> getDataSynchInstances()
    {
        return dataSynchInstances;
    }
    */
    
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
        dataSynchInstances.addAll( synchInstanceService.getInstancesByType( synchType ) );
        
        return SUCCESS;
    }
}

