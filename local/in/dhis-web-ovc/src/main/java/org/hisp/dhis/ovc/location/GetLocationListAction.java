package org.hisp.dhis.ovc.location;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetLocationListAction extends ActionPagingSupport<Location>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private List<Location> locations = new ArrayList<Location>();
   
    public List<Location> getLocations()
    {
        return locations;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private String status;
    
    public String getStatus()
    {
        return status;
    }
    
    private Integer total;

    public Integer getTotal()
    {
        return total;
    }
    
    private String key;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        status = "NONE";
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        if( organisationUnit == null )
        {
            status = "Please select organisationunit";
            
            return SUCCESS;
        }
        
        locations = new ArrayList<Location> ( locationService.getLocationsByParentOrganisationUnit( organisationUnit ) );
        
        Collections.sort( locations, new IdentifiableObjectNameComparator() );
        
        if ( isNotBlank( key ) )
        {
            locationService.searchLocationsByName( locations, key );
        }
        
        this.paging = createPaging( locations.size() );
        locations = getBlockElement( locations, paging.getStartPos(), paging.getPageSize() );
        
        return SUCCESS;
    }
}



