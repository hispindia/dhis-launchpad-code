package org.hisp.dhis.sm.action;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.sm.api.DataElementSynchStatus;
import org.hisp.dhis.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.sm.api.SynchInstance;
import org.hisp.dhis.sm.api.SynchInstanceService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class UpdateApproveDEStatusAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private SynchInstanceService synchInstanceService;
    
    @Autowired
    private DataElementSynchStatusService dataElementSynchStatusService;
    
    // ------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception 
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        
        Set<DataElement> dataElements = new HashSet<DataElement>( dataElementService.getAllDataElements() );
        for( DataElement de : dataElements )
        {
            Date lastUpdated = new Date();
            
            String approveNeeded = request.getParameter( "appNeeded_"+de.getId() );
            String approveStatus = request.getParameter( "approveStatus_"+de.getId() );
            String instanceIds[] = request.getParameterValues( "instances_"+de.getId() );
            
            System.out.println( "instances_"+de.getId() + instanceIds );
            
            if( instanceIds == null )
            {
                continue;
            }
            
            for( String instanceId : instanceIds )
            {
                SynchInstance instance = synchInstanceService.getInstance( Integer.parseInt( instanceId ) );
                
                DataElementSynchStatus deSynchStatus = dataElementSynchStatusService.getStatusByInstanceAndDataElement( instance, de );
                
                if( deSynchStatus == null )
                {
                    deSynchStatus = new DataElementSynchStatus();
                    
                    deSynchStatus.setDataElement( de );
                    deSynchStatus.setInstance( instance );
                    
                    if( approveNeeded != null )
                    {
                        deSynchStatus.setRememberApproveStatus( true );
                    }
                    else
                    {
                        deSynchStatus.setRememberApproveStatus( false );
                    }
                    
                    if( approveStatus != null )
                    {
                        deSynchStatus.setApproveStatus( true );
                        deSynchStatus.setApprovedDate( lastUpdated );
                    }
                    else
                    {
                        deSynchStatus.setApproveStatus( false );
                        deSynchStatus.setApprovedDate( null );
                    }
                    
                    deSynchStatus.setLastUpdated( lastUpdated );
                    
                    dataElementSynchStatusService.addDataElementSynchStatus( deSynchStatus );
                }
                else
                {
                    if( approveNeeded != null )
                    {
                        deSynchStatus.setRememberApproveStatus( true );
                    }
                    else
                    {
                        deSynchStatus.setRememberApproveStatus( false );
                    }
                    
                    if( approveStatus != null )
                    {
                        deSynchStatus.setApproveStatus( true );
                        deSynchStatus.setApprovedDate( lastUpdated );
                    }
                    else
                    {
                        deSynchStatus.setApproveStatus( false );
                        deSynchStatus.setApprovedDate( null );
                    }
                    
                    deSynchStatus.setLastUpdated( lastUpdated );
                    
                    dataElementSynchStatusService.updateDataElementSynchStatus( deSynchStatus );
                }
            }
                         
        }
        
        return SUCCESS;
    }
}

