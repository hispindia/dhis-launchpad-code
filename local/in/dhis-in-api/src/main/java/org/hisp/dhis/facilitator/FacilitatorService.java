package org.hisp.dhis.facilitator;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;


/**
 * @author Mithilesh Kumar Thakur
 */
public interface FacilitatorService
{
    String ID = FacilitatorService.class.getName();
    
    // -------------------------------------------------------------------------
    // Facilitator
    // -------------------------------------------------------------------------

    public int addFacilitator( Facilitator facilitator );

    void updateFacilitator( Facilitator facilitator );

    void deleteFacilitator( Facilitator facilitator );
    
    Facilitator getFacilitator( int id );
        

    Collection<Facilitator> getAllFacilitator();
    
    Collection<Facilitator> getAllFacilitatorOrderByCodeDesc();
    
    Collection<Facilitator> getAllFacilitatorByOrgUnitAndOrderByCodeDesc( OrganisationUnit organisationUnit );
    
    Collection<Facilitator> getFacilitatorByOrganisationUnit( OrganisationUnit organisationUnit );
    
    Collection<Facilitator> getActiveFacilitatorByOrganisationUnit( OrganisationUnit organisationUnit );    
    
    void searchFacilitatorsByName( List<Facilitator> facilitators, String key );
    
    Facilitator getActiveFacilitator( OrganisationUnit organisationUnit );
    
}
