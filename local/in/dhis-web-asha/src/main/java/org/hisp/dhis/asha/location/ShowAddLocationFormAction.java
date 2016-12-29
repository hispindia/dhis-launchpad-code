package org.hisp.dhis.asha.location;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.location.Location;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;


/**
 * @author Mithilesh Kumar Thakur
 */


public class ShowAddLocationFormAction implements Action
{       
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    /*
    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private int organisationUnitId;
    
    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private String code;
    
    public String getCode()
    {
        return code;
    }
    
    private List<Location> locations = new ArrayList<Location>();
    
    public List<Location> getLocations()
    {
        return locations;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------


    public String execute()
    {
        organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
       // for OVC Location
        /*
        if ( organisationUnit.getCode() != null )
        {
            
            locations = new ArrayList<Location> ( locationService.getAllLocationsByParentOrgUnitAndOrderByCodeDesc( organisationUnit ) );
            
            if ( locations != null && locations.size() > 0 )
            {
                
                Location location = locations.get( 0 );
                
                locationCode = location.getCode();
                
                
                String[] compositeCode = locationCode.split( "_" );
                
                
                String locCode = compositeCode[1];
                
                Integer presentCount = Integer.parseInt( locCode  );
                
                int finalCount = presentCount + 1;
                
                if( finalCount <= 9 )
                {
                    code = organisationUnit.getCode() + "_" + "000" + finalCount;
                }
                
                else if( finalCount <= 99 )
                {
                    code = organisationUnit.getCode() + "_" + "00" + finalCount;
                }
                
                else if( finalCount <= 999 )
                {
                    code = organisationUnit.getCode() + "_" + "0" + finalCount;
                }
                else if( finalCount <= 9999 )
                {
                    code = organisationUnit.getCode() + "_" +  finalCount;
                }
                
            }
            
            else
            {
                code = organisationUnit.getCode() + "_" + "0001";
            }
             
        }
        else
        {
            code = "";  
        }
       */
        
        return SUCCESS;
    }
}