package org.hisp.dhis.location;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface LocationService
{
    String ID = LocationService.class.getName();
    
    // -------------------------------------------------------------------------
    // Location
    // -------------------------------------------------------------------------

    /**
     * Adds an Location 
     *
     * @param location the Location to add.
     * @return a generated unique id of the added Location.
     */
    public int addLocation( Location location );

    /**
     * Updates an Location.
     *
     * @param location the Location to update.
     */
    void updateLocation( Location location );

 
    /**
     * Deletes  Location
     * deleted.
     *
     * @param location the Location to delete.
     */
    void deleteLocation( Location location );
        

    /**
     * Returns an Location.
     *
     * @param id the id of the Location to return.
     * @return the Location with the given id, or null if no match.
     */
    Location getLocation( int id );
    
    //Location getLocationByCode( String code );
    
    Location getLocationByParentOrganisationUnitAndName( OrganisationUnit parentOrganisationUnit,String name );

    Collection<Location> getAllLocations();
    
    Collection<Location> getAllLocationsOrderByCodeDesc();
    
    Collection<Location> getAllLocationsByParentOrgUnitAndOrderByCodeDesc( OrganisationUnit parentOrganisationUnit );
    
    Collection<Location> getLocationsByParentOrganisationUnit( OrganisationUnit parentOrganisationUnit );
    
    void searchLocationsByName( List<Location> locations, String key );
    
    Collection<Location> getActiveLocationsByParentOrganisationUnit( OrganisationUnit parentOrganisationUnit );
    
}
