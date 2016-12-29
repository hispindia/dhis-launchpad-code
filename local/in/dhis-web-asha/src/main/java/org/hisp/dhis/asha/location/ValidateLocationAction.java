package org.hisp.dhis.asha.location;

import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */


public class ValidateLocationAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    private String locationName;
    
    public void setLocationName( String locationName )
    {
        this.locationName = locationName;
    }

    private Integer orgUnitId; 
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private Integer locationId;
   
    public void setLocationId( Integer locationId )
    {
        this.locationId = locationId;
    }


    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Location Validation with locationCode and orgUnitId
        // ---------------------------------------------------------------------
        
        //System.out.println( " Inside Validate Location "   + " Location Name :" + locationName  + "  -- orgUnitId : " + orgUnitId );
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        Location location = locationService.getLocationByParentOrganisationUnitAndName( organisationUnit, locationName );
        
        if ( location != null )
        {
            if ( locationId == null || ( locationId != null && location.getId() != locationId.intValue() ) )
            {
                message = "Village Already Exists, Please Specify Another Name";

                return INPUT;
            }
        }
        
        return SUCCESS;
    }
}
