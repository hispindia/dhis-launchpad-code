package org.hisp.dhis.asha.facilitator;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowASHAFacilitatorFormAction implements Action
{
    
    public static final String PHC_GROUP = "PHC Group";//25.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
 
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }    
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    // -------------------------------------------------------------------------
    // Input/output Getter/Setter
    // -------------------------------------------------------------------------

    
    private OrganisationUnit organisationUnit;
   
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private Facilitator facilitator;
    
    public Facilitator getFacilitator()
    {
        return facilitator;
    }

    private String status;
    
    public String getStatus()
    {
        return status;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute()
    {
        status = "NONE";
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        Constant phcGroupConstant = constantService.getConstantByName( PHC_GROUP );
        
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( (int) phcGroupConstant.getValue() );
        
        if ( ( organisationUnit == null ) || ( !organisationUnitGroup.getMembers().contains( organisationUnit ) ) )
        {
            status = i18n.getString( "please_select_phc" );

            return SUCCESS;
        }
        
        facilitator = facilitatorService.getActiveFacilitator( organisationUnit );
        
        return SUCCESS;
    }
}

