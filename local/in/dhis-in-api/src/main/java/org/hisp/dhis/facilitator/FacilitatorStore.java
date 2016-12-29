package org.hisp.dhis.facilitator;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface FacilitatorStore extends GenericNameableObjectStore<Facilitator>
{
    String ID = FacilitatorStore.class.getName();
    
    // -------------------------------------------------------------------------
    // Facilitator
    // -------------------------------------------------------------------------

    Facilitator getFacilitator( int id );
    
    Collection<Facilitator> getAllFacilitatorOrderByCodeDesc();
    
    Collection<Facilitator> getFacilitatorByOrganisationUnit( OrganisationUnit organisationUnit );
    
    Collection<Facilitator> getAllFacilitatorByOrgUnitAndOrderByCodeDesc( OrganisationUnit organisationUnit );
    
    Facilitator getActiveFacilitator( OrganisationUnit organisationUnit );
    
}
