package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.sm.api.SynchInstance;
import org.hisp.dhis.sm.api.SynchInstanceService;
import org.hisp.dhis.sm.api.SynchManager;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;


public class ApproveDEStatusAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SynchManager synchManager;

    public void setSynchManager( SynchManager synchManager )
    {
        this.synchManager = synchManager;
    }

    private SynchInstanceService synchInstanceService;

    public void setSynchInstanceService( SynchInstanceService synchInstanceService )
    {
        this.synchInstanceService = synchInstanceService;
    }

    private DataElementSynchStatusService dataElementSynchStatusService;

    public void setDataElementSynchStatusService( DataElementSynchStatusService dataElementSynchStatusService )
    {
        this.dataElementSynchStatusService = dataElementSynchStatusService;
    }
    
    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private InstanceBusinessRulesService instanceBusinessRulesService;

    @Autowired
    private CurrentUserService currentUserService;
    // ------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    Collection<SynchInstance> synchInstances = new ArrayList<SynchInstance>();

    public Collection<SynchInstance> getSynchInstances() 
    {
        return synchInstances;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements(List<DataElement> dataElements) 
    {
        this.dataElements = dataElements;
    }

    private List<DataElement> allDataElements = new ArrayList<DataElement>();
    public List<DataElement> getAllDataElements()
    {
        return allDataElements;
    }

    public void setAllDataElements( List<DataElement> allDataElements )
    {
        this.allDataElements = allDataElements;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception 
    {

        //----TO DO-----//

        /*
        dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        int count=dataElements.size();
        System.out.println("Total Size - "+count +"\n");
        for (DataElement dataElement : dataElements) 
        {
            System.out.println(dataElement.getDisplayName() + " : " + dataElement.getLastUpdated());
        }
         */
        
        dataElements = new ArrayList<DataElement>();
        dataElements.addAll( dataElementSynchStatusService.getNewDataElements() );
        dataElements.addAll( dataElementSynchStatusService.getUpdatedDataElements() );
        
        
        synchInstances.addAll( instanceBusinessRulesService.getInstancesForApprovalUser( currentUserService.getCurrentUser() ) );
        //synchInstances.addAll( synchInstanceService.getInstancesByType("meta-data") );
        
        allDataElements.addAll( dataElementService.getAllDataElements());
        
        /*
        for (SynchInstance instance : synchInstances) 
        {
            System.out.println(instance.getDisplayName() + " : " + instance.getLastUpdated());
        }
        */

        return SUCCESS;  //To change body of implemented methods use File | Settings | File Templates.

    }

}
