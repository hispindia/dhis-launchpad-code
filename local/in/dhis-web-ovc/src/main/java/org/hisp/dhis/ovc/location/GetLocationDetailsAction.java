package org.hisp.dhis.ovc.location;

import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetLocationDetailsAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer locationId;
    
    public void setLocationId( Integer locationId )
    {
        this.locationId = locationId;
    }

    private Location location;
    
    public Location getLocation()
    {
        return location;
    }
    
    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }


    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
 
    public String execute()
    {
        location = locationService.getLocation( locationId );
        
        organisationUnit = location.getParentOrganisationUnit();
        
        return SUCCESS;
    }
}