package org.hisp.dhis.ovc.report.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.ovc.report.comparator.ReportNameComparator;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ReportListAction extends ActionPagingSupport<CustomReport>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CustomReportService customReportService;
    
    public void setCustomReportService( CustomReportService customReportService )
    {
        this.customReportService = customReportService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    // -------------------------------------------------------------------------
    // Input/output getter/setter
    // -------------------------------------------------------------------------
    
    private List<CustomReport> reportList = new ArrayList<CustomReport>();
    
    public List<CustomReport> getReportList()
    {
        return reportList;
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
    
    private List<OrganisationUnitGroup> orgUnitGroups = new ArrayList<OrganisationUnitGroup>();
    
    public List<OrganisationUnitGroup> getOrgUnitGroups()
    {
        return orgUnitGroups;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    
    String reportType = "tracker";
    public String execute()
    {
        reportList = new ArrayList<CustomReport>( customReportService.getAllCustomReportsByReportType( reportType ) );
        
        Collections.sort( reportList, new ReportNameComparator() );
        
        if ( isNotBlank( key ) )
        {
            customReportService.searchReportsByName( reportList, key );
        }
        
        this.paging = createPaging( reportList.size() );
        reportList = getBlockElement( reportList, paging.getStartPos(), paging.getPageSize() );
        
        /*
        for( CustomReport customReport : reportList )
        {
            System.out.println( "Report name is : " + customReport.getName() );
        }
        */
        
        orgUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() );            
        Collections.sort( orgUnitGroups, new IdentifiableObjectNameComparator() );
        
        return SUCCESS;
    }
    
}
