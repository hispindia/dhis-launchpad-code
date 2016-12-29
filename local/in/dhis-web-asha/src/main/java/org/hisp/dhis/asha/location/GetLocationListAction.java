package org.hisp.dhis.asha.location;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetLocationListAction  extends ActionPagingSupport<Location>
{
    public static final String ASHA_ACTIVITY = "ASHA Activity";
    
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
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
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
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        status = "NONE";
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
    
        program = programService.getProgramByName( ASHA_ACTIVITY );
        
        if ( ( organisationUnit == null ) || ( !program.getOrganisationUnits().contains( organisationUnit ) ) )
        {
            
            status = i18n.getString( "please_select_sc" );

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
