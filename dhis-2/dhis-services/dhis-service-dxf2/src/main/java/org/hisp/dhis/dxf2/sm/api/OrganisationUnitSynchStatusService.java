package org.hisp.dhis.dxf2.sm.api;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface OrganisationUnitSynchStatusService
{
    String ID = OrganisationUnitSynchStatusService.class.getName();
    
    void addOrganisationUnitSynchStatus( OrganisationUnitSynchStatus organisationUnitSynchStatus );
    
    void updateOrganisationUnitSynchStatus( OrganisationUnitSynchStatus organisationUnitSynchStatus );
    
    void deleteOrganisationUnitSynchStatus( OrganisationUnitSynchStatus organisationUnitSynchStatus );
    
    OrganisationUnitSynchStatus getStatusByInstanceAndOrganisationUnit( SynchInstance instance, OrganisationUnit organisationUnit );
    
    Collection<OrganisationUnitSynchStatus> getStatusByInstance( SynchInstance instance );
    
    Collection<OrganisationUnit> getNewOrganisationUnits();
    
    Collection<OrganisationUnit> getUpdatedOrganisationUnits();
    
    Collection<OrganisationUnitSynchStatus> getUpdatedOrganisationUnitSyncStatus();

    Collection<OrganisationUnit> getApprovedOrganisationUnits();
    
    Collection<OrganisationUnitSynchStatus> getSynchStausByOrganisationUnit( OrganisationUnit organisationUnit );
    
    Collection<OrganisationUnitSynchStatus> getSynchStausByOrganisationUnits( Collection<OrganisationUnit> organisationUnits );
    
    Collection<OrganisationUnitSynchStatus> getAllOrganisationUnitSynchStatus();
    
    Collection<OrganisationUnitSynchStatus> getPendingOrganisationUnitSyncStatus( SynchInstance instance );
    
    Collection<OrganisationUnit> getOrganisationUnitByInstance( SynchInstance instance );
    
    Collection<OrganisationUnit> getApprovedOrganisationUnitByInstance( SynchInstance instance );
}
