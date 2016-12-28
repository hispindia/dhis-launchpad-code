package org.hisp.dhis.sm.data.action;

import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowDataSynchInstanceFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private SynchInstanceService synchInstanceService;

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer dataSynchInstanceId;
    
    public void setDataSynchInstanceId( Integer dataSynchInstanceId )
    {
        this.dataSynchInstanceId = dataSynchInstanceId;
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

    private SynchInstance dataSynchInstance;
    
    public SynchInstance getDataSynchInstance()
    {
        return dataSynchInstance;
    }

    private String encryptPassword;
    
    public String getEncryptPassword()
    {
        return encryptPassword;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if( dataSynchInstanceId != null || update )
        {
            dataSynchInstance = synchInstanceService.getInstance( dataSynchInstanceId );
            encryptPassword = dataSynchInstance.getDecryptPassword( dataSynchInstance.getPassword() );
        }
        
        return SUCCESS;
    }
}

