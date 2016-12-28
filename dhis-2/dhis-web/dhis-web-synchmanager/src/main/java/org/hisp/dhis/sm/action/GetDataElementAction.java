package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatus;
import org.hisp.dhis.dxf2.sm.api.DataElementSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 * 
 */

public class GetDataElementAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DataElementService dataElementservice;

    @Autowired
    private InstanceBusinessRulesService instanceBusinessRulesService;

    @Autowired
    private CurrentUserService currentUserService;

    private DataElementSynchStatusService dataElementSynchStatusService;

    public void setDataElementSynchStatusService( DataElementSynchStatusService dataElementSynchStatusService )
    {
        this.dataElementSynchStatusService = dataElementSynchStatusService;
    }

    // ------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------
    private int dataElementId;

    public void setDataElementId( int dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private DataElement dataElementObj;

    public DataElement getDataElementObj()
    {
        return dataElementObj;
    }

    Collection<SynchInstance> synchInstances = new ArrayList<SynchInstance>();

    public Collection<SynchInstance> getSynchInstances()
    {
        return synchInstances;
    }

    public void setSynchInstances( Collection<SynchInstance> synchInstances )
    {
        this.synchInstances = synchInstances;
    }

    Collection<DataElementSynchStatus> AllDataElementsById;

    public Collection<DataElementSynchStatus> getAllDataElementsById()
    {
        return AllDataElementsById;
    }

    Collection<SynchInstance> instancesLeft;

    public Collection<SynchInstance> getInstancesLeft()
    {
        return instancesLeft;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    @Override
    public String execute()
        throws Exception
    {

        try
        {
            dataElementObj = dataElementservice.getDataElement( dataElementId );

            AllDataElementsById = new ArrayList<DataElementSynchStatus>();
            AllDataElementsById.addAll( dataElementSynchStatusService.getStatusByDataElement( dataElementObj ) );

            synchInstances.addAll( instanceBusinessRulesService.getInstancesForApprovalUser( currentUserService
                .getCurrentUser() ) );

            instancesLeft = new ArrayList<SynchInstance>();
            instancesLeft.addAll( synchInstances );

            for ( DataElementSynchStatus desyn : AllDataElementsById )
            {
                instancesLeft.remove( desyn.getInstance() );
            }

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return SUCCESS;
    }

}
