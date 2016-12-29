package org.hisp.dhis.asha.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetChildrenOrgUnitListAction implements Action
{
    public static final String BLOCK_GROUP_ID = "Block Group";//29.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private int orgUnitId;
    
    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private List<OrganisationUnit> organisationUnitList = new ArrayList<OrganisationUnit>();
    
    public List<OrganisationUnit> getOrganisationUnitList()
    {
        return organisationUnitList;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        organisationUnitList = new ArrayList<OrganisationUnit>( organisationUnit.getChildren() );
        
        Constant blockGroupIdConstant = constantService.getConstantByName( BLOCK_GROUP_ID );
        
        OrganisationUnitGroup orgUnitGroup = new OrganisationUnitGroup();
        
        orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( (int) blockGroupIdConstant.getValue() );
        
        List<OrganisationUnit> groupMember = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
        
        organisationUnitList.retainAll( groupMember );
        
        Collections.sort( organisationUnitList );
        
        return SUCCESS;
    }
}
