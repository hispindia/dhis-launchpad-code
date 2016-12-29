package org.hisp.dhis.ovc.school;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetSchoolListAction extends ActionPagingSupport<School>
{       
    public static final String CBO_GROUP_ID = "CBO_GROUP_ID";//436.0
    
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
    
    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private List<School> schools = new ArrayList<School>();
   
    public List<School> getSchools()
    {
        return schools;
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
        
        Constant cboGroupConstant = constantService.getConstantByName( CBO_GROUP_ID );
        
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( (int) cboGroupConstant.getValue() );
        
        if ( ( organisationUnit == null ) || ( !organisationUnitGroup.getMembers().contains( organisationUnit ) ) )
        {
            status = i18n.getString( "please_select_cbo" );

            return SUCCESS;
        }

        
        schools = new ArrayList<School> ( schoolService.getSchoolByOrganisationUnit( organisationUnit ) );
        
        Collections.sort( schools, new IdentifiableObjectNameComparator() );
        
        if ( isNotBlank( key ) )
        {
            schoolService.searchSchoolsByName( schools, key );
        }
        
        this.paging = createPaging( schools.size() );
        schools = getBlockElement( schools, paging.getStartPos(), paging.getPageSize() );
        
        return SUCCESS;
    }
}
