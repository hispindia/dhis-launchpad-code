package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatus;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.OrganisationUnitSynchStatusStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultOrganisationUnitSynchStatusService implements OrganisationUnitSynchStatusService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitSynchStatusStore organisationUnitSynchStatusStore;

    public void setOrganisationUnitSynchStatusStore( OrganisationUnitSynchStatusStore organisationUnitSynchStatusStore )
    {
        this.organisationUnitSynchStatusStore = organisationUnitSynchStatusStore;
    }
    
    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------

    @Override
    public void addOrganisationUnitSynchStatus( OrganisationUnitSynchStatus organisationUnitSynchStatus )
    {
        organisationUnitSynchStatusStore.addOrganisationUnitSynchStatus( organisationUnitSynchStatus );
    }

    @Override
    public void updateOrganisationUnitSynchStatus( OrganisationUnitSynchStatus organisationUnitSynchStatus )
    {
        organisationUnitSynchStatusStore.updateOrganisationUnitSynchStatus( organisationUnitSynchStatus );
    }

    @Override
    public void deleteOrganisationUnitSynchStatus( OrganisationUnitSynchStatus organisationUnitSynchStatus )
    {
        organisationUnitSynchStatusStore.deleteOrganisationUnitSynchStatus( organisationUnitSynchStatus );
    }

    @Override
    public OrganisationUnitSynchStatus getStatusByInstanceAndOrganisationUnit( SynchInstance instance, OrganisationUnit organisationUnit )
    {
        return organisationUnitSynchStatusStore.getStatusByInstanceAndOrganisationUnit( instance, organisationUnit );
    }

    @Override
    public Collection<OrganisationUnitSynchStatus> getStatusByInstance( SynchInstance instance )
    {
        return organisationUnitSynchStatusStore.getStatusByInstance( instance );
    }
    
    public Collection<OrganisationUnit> getNewOrganisationUnits()
    {
        return organisationUnitSynchStatusStore.getNewOrganisationUnits();
    }
    
    public Collection<OrganisationUnit> getUpdatedOrganisationUnits()
    {
        return organisationUnitSynchStatusStore.getUpdatedOrganisationUnits();
    }

    public Collection<OrganisationUnitSynchStatus> getUpdatedOrganisationUnitSyncStatus()
    {
        return organisationUnitSynchStatusStore.getUpdatedOrganisationUnitSyncStatus();
    }    
    
    public Collection<OrganisationUnit> getApprovedOrganisationUnits()
    {
        return organisationUnitSynchStatusStore.getApprovedOrganisationUnits();
    }

    @Override
    public Collection<OrganisationUnitSynchStatus> getSynchStausByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        return organisationUnitSynchStatusStore.getSynchStausByOrganisationUnit( organisationUnit );
    }    
    
    @Override
    public Collection<OrganisationUnitSynchStatus> getSynchStausByOrganisationUnits( Collection<OrganisationUnit> organisationUnits )
    {
        return organisationUnitSynchStatusStore.getSynchStausByOrganisationUnits( organisationUnits );
    }
    
    public Collection<OrganisationUnitSynchStatus> getAllOrganisationUnitSynchStatus()
    {
        return organisationUnitSynchStatusStore.getAllOrganisationUnitSynchStatus();
    }
    
    public Collection<OrganisationUnitSynchStatus> getPendingOrganisationUnitSyncStatus( SynchInstance instance )
    {
        return organisationUnitSynchStatusStore.getPendingOrganisationUnitSyncStatus( instance );
    }   
    
    public Collection<OrganisationUnit> getOrganisationUnitByInstance( SynchInstance instance )
    {
        return organisationUnitSynchStatusStore.getOrganisationUnitByInstance( instance );
    }
    
    public Collection<OrganisationUnit> getApprovedOrganisationUnitByInstance( SynchInstance instance )
    {
        return organisationUnitSynchStatusStore.getApprovedOrganisationUnitByInstance( instance );
    }     
    
}

