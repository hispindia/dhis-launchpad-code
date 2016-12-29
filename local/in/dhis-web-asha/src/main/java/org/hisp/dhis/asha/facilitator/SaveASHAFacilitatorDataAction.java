package org.hisp.dhis.asha.facilitator;

import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveASHAFacilitatorDataAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }

    // -------------------------------------------------------------------------
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------
    
    private Integer facilitatorId;
    
    public void setFacilitatorId( Integer facilitatorId )
    {
        this.facilitatorId = facilitatorId;
    }
    
    
    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    private String contactNumber;
   
    public void setContactNumber( String contactNumber )
    {
        this.contactNumber = contactNumber;
    }
    
    private String address;
    
    public void setAddress( String address )
    {
        this.address = address;
    }
    
    private String gender;

    public void setGender( String gender )
    {
        this.gender = gender;
    }
    
    private Boolean active;
    
    public void setActive( Boolean active )
    {
        this.active = active;
    }
    
    private String message;
    
    public String getMessage()
    {
        return message;
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
        if( facilitatorId != null )
        {
            System.out.println( "Inside Update Facilitator" );
            
            Facilitator facilitator = facilitatorService.getFacilitator( facilitatorId );
            
            facilitator.setName( name );
            facilitator.setContactNumber( contactNumber );
            facilitator.setAddress( address );
            facilitator.setGender( gender );
            
            active = ( active == null) ? false : true;
            
            facilitator.setActive( active );
            
            facilitator.setOrganisationUnit( facilitator.getOrganisationUnit() );
            
            facilitatorService.updateFacilitator( facilitator );
            
            organisationUnit = facilitator.getOrganisationUnit();
            
            message =  "ASHA FACILITATOR DATA SUCCESSFULLY UPDATED";
        }
        
        else
        {
            System.out.println( "Inside Add Facilitator" );
            
            organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
            
            Facilitator facilitator = new Facilitator();
            
            facilitator.setName( name );
            facilitator.setContactNumber( contactNumber );
            facilitator.setAddress( address );
            facilitator.setGender( gender );
            
            active = ( active == null) ? false : true;
            
            facilitator.setActive( active );
            
            facilitator.setOrganisationUnit( organisationUnit );
            
            facilitatorService.addFacilitator( facilitator );
            
            message =  "ASHA FACILITATOR SUCCESSFULLY REGISTERED";
        }
        
        return SUCCESS;
    }
}
