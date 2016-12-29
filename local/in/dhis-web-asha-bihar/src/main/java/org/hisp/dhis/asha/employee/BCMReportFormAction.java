package org.hisp.dhis.asha.employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class BCMReportFormAction implements Action
{
    public static final String DISTRICT_GROUP_ID = "DISTRICT_GROUP_ID";//1.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
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
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private List<OrganisationUnit> orgUnitGroupMembers =  new ArrayList<OrganisationUnit>();
    
    public List<OrganisationUnit> getOrgUnitGroupMembers()
    {
        return orgUnitGroupMembers;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------



    public String execute() throws Exception
    {
        Constant districtGroupIdConstant = constantService.getConstantByName( DISTRICT_GROUP_ID );
        
        OrganisationUnitGroup orgUnitGroup = new OrganisationUnitGroup();
        
        orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( (int) districtGroupIdConstant.getValue() );
        
        orgUnitGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
        
        Collections.sort( orgUnitGroupMembers );
        
        return SUCCESS;
    }
}
