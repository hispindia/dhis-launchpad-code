package org.hisp.dhis.reports.reportmanagement.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportModel;
import org.hisp.dhis.reports.ReportType;
import org.hisp.dhis.schedule.SchedulingPolicy;
import org.hisp.dhis.schedule.SchedulingPolicyService;

import com.opensymphony.xwork2.Action;

public class AddReportFormAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SchedulingPolicyService schedulingPolicyService;
    
    public void setSchedulingPolicyService( SchedulingPolicyService schedulingPolicyService )
    {
        this.schedulingPolicyService = schedulingPolicyService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    private List<String> reportTypes;

    public List<String> getReportTypes()
    {
        return reportTypes;
    }

    private List<String> reportModels;

    public List<String> getReportModels()
    {
        return reportModels;
    }

    private List<OrganisationUnitGroup> orgUnitGroups;
    
    public List<OrganisationUnitGroup> getOrgUnitGroups()
    {
        return orgUnitGroups;
    }
    
    private List<SchedulingPolicy> schedulingPolicies;
    
    public List<SchedulingPolicy> getSchedulingPolicies()
    {
        return schedulingPolicies;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {

        periodTypes = new ArrayList<PeriodType>( periodService.getAllPeriodTypes() );
        
        orgUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() );
        Collections.sort( orgUnitGroups, IdentifiableObjectNameComparator.INSTANCE );
        
        reportTypes = ReportType.getReportTypes();

        reportModels = ReportModel.getReportModels();
        
        schedulingPolicies = new ArrayList<SchedulingPolicy>( schedulingPolicyService.getAllSchedulingPolicies() );
        
        return SUCCESS;
    }

}


