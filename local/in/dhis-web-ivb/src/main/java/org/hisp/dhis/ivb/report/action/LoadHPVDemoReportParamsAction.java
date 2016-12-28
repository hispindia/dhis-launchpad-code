package org.hisp.dhis.ivb.report.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.lookup.Lookup;
import org.hisp.dhis.lookup.LookupService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author BHARATH
 */
public class LoadHPVDemoReportParamsAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private LookupService lookupService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SelectionTreeManager selectionTreeManager;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Collection<OrganisationUnit> selectedUnits = new HashSet<OrganisationUnit>();

    public Collection<OrganisationUnit> getSelectedUnits()
    {
        return selectedUnits;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    
    @Override
    public String execute() throws Exception
    {
        Lookup lookup = lookupService.getLookupByName( Lookup.HPVDEMO_REPORT_PARAMS_ORGUNITGROUP );
        
        String hpvdemo_orgunitgroups = lookup.getValue();
        
        for( String strOrgunitGroupId : hpvdemo_orgunitgroups.split( ":" ) )
        {
            selectedUnits.addAll( organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( strOrgunitGroupId ) ).getMembers() );
        }
        
        Set<OrganisationUnit> currentUserOrgUnits = new HashSet<OrganisationUnit>( currentUserService.getCurrentUser().getDataViewOrganisationUnits() );
        selectionTreeManager.setRootOrganisationUnits( currentUserOrgUnits );

        selectionTreeManager.clearSelectedOrganisationUnits();
        selectionTreeManager.setSelectedOrganisationUnits( selectedUnits );

        return SUCCESS;
    }
}
