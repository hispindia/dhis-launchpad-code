package org.hisp.dhis.ovc.location;

import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class AddLocationAction implements Action
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
    // Input/output
    // -------------------------------------------------------------------------

    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
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
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        //Location location = new Location();
        
        Location location = new Location( name, code, orgUnit );
        
        location.setName( name );
        
        //location.setShortName( shortName );
        
        location.setCode( code );
        
        /*
        location.setContactPerson( contactPerson );
        location.setAddress( address );
        location.setEmail( email );
        location.setPhoneNumber( phoneNumber );
        
        */
        
        location.setParentOrganisationUnit( orgUnit );
        
        //System.out.println( " In Add Active value is : " + active );
        
        active = ( active == null) ? false : true;
        location.setActive( active );
        
        locationService.addLocation( location );
        
        return SUCCESS;
    }
}