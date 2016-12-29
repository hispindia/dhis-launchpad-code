package org.hisp.dhis.asha.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class ShowRegisteredASHAListReportForm implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception 
    {
        
        // OrgUnit Related Info
        organisationUnit = new OrganisationUnit();
        organisationUnit = organisationUnitService.getOrganisationUnit( 14 ); 
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnit.getChildren() );
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
       
        return SUCCESS;
    }
}