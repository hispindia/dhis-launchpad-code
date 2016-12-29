package org.hisp.dhis.asha.location;

import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class UpdateLocationAction implements Action
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

 
    private String orgUnitName;

    public String getOrgUnitName()
    {
        return orgUnitName;
    }

    public void setOrgUnitName( String orgUnitName )
    {
        this.orgUnitName = orgUnitName;
    }

    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    private String code;
    
    public void setCode( String code )
    {
        this.code = code;
    }
    
    /*
    private String shortName;
    
    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }
 
    
    private String address;
    
    public void setAddress( String address )
    {
        this.address = address;
    }
    
    private String email;
    
    public void setEmail( String email )
    {
        this.email = email;
    }
    private String phoneNumber;
    
    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }
    
    private String contactPerson;
   
    public void setContactPerson( String contactPerson )
    {
        this.contactPerson = contactPerson;
    }
    */
    
    private Boolean active;
    
    public void setActive( Boolean active )
    {
        this.active = active;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute()
    {
        
        Location location = locationService.getLocation( locationId );
        
        location.setName( name );
        
        /*
        location.setShortName( shortName );
        
        location.setContactPerson( contactPerson );
        location.setAddress( address );
        location.setEmail( email );
        location.setPhoneNumber( phoneNumber );
        */
        
        location.setCode( code );
        location.setParentOrganisationUnit( location.getParentOrganisationUnit() );
        
        //System.out.println( " In Update Active value is : " + active );
        
        active = ( active == null) ? false : true;
        location.setActive( active );
        
        locationService.updateLocation( location );
        
        return SUCCESS;
    }
}