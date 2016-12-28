package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatus;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 * 
 */

public class GetOrgUnitAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private InstanceBusinessRulesService instanceBusinessRulesService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    private OrganisationUnitSynchStatusService organisationSynStatusService;

    // ------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    public void setOrganisationSynStatusService( OrganisationUnitSynchStatusService organisationSynStatusService )
    {
        this.organisationSynStatusService = organisationSynStatusService;
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

    private int orgUnitId;

    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    Collection<OrganisationUnitSynchStatus> AllOrgUnits;

    public Collection<OrganisationUnitSynchStatus> getAllOrgUnits()
    {
        return AllOrgUnits;
    }

    private OrganisationUnit organisationUnitObject;

    public OrganisationUnit getOrganisationUnitObject()
    {
        return organisationUnitObject;
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

        organisationUnitObject = organisationUnitService.getOrganisationUnit( orgUnitId );

        AllOrgUnits = new ArrayList<OrganisationUnitSynchStatus>();
        AllOrgUnits.addAll( organisationSynStatusService.getSynchStausByOrganisationUnit( organisationUnitObject ) );

        synchInstances.addAll( instanceBusinessRulesService.getInstancesForApprovalUser( currentUserService
            .getCurrentUser() ) );

        instancesLeft = new ArrayList<SynchInstance>();
        instancesLeft.addAll( synchInstances );

        for ( OrganisationUnitSynchStatus organisationUnitSynchStatus : AllOrgUnits )
        {
            instancesLeft.remove( organisationUnitSynchStatus.getInstance() );
        }
        return SUCCESS;
    }

}
