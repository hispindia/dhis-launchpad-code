package org.hisp.dhis.location;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface LocationStore extends GenericNameableObjectStore<Location>
{
    String ID = LocationStore.class.getName();
    
    // -------------------------------------------------------------------------
    // Location
    // -------------------------------------------------------------------------

    Location getLocation( int id );
    
    //Location getLocationByCode( String code );
    
    Collection<Location> getAllLocationsOrderByCodeDesc();
    
    Collection<Location> getAllLocationsByParentOrgUnitAndOrderByCodeDesc( OrganisationUnit parentOrganisationUnit );
    
    Location getLocationByParentOrganisationUnitAndName( OrganisationUnit parentOrganisationUnit,String name );

    Collection<Location> getLocationsByParentOrganisationUnit( OrganisationUnit parentOrganisationUnit );
    
}
