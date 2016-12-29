package org.hisp.dhis.ovc.location;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
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
    
    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }
    
    
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
    
    private String locationCode = "";
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------


    public String execute()
    {
        organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
        if ( organisationUnit.getCode() != null )
        {
            //System.out.println( " Parent OrgUnit Code is  : " + organisationUnit.getCode() );
            
            locations = new ArrayList<Location> ( locationService.getAllLocationsByParentOrgUnitAndOrderByCodeDesc( organisationUnit ) );
            
            //System.out.println( " Size of Location is  : " + locations.size() );
           
            if ( locations != null && locations.size() > 0 )
            {
               /*
                for( Location location : locations )
                {
                    System.out.println(   location.getName() + " -- " + location.getCode() );
                }
                */
                
                Location location = locations.get( 0 );
                
                locationCode = location.getCode();
                
                //System.out.println( " Last Location Code is  : " + locationCode );
                
                String[] compositeCode = locationCode.split( "_" );
                
                //String parentCode = compositeCode[0];
                
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
                
                //code = organisationUnit.getCode() + "_" + "000" + finalCount;
                
                //locCode.substring( 3,locCode.length() );
                
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
       
        
        //System.out.println( " Present Location Code is  : " + locationCode + " -- Final Location Code is " + code );
        
        return SUCCESS;
    }
}