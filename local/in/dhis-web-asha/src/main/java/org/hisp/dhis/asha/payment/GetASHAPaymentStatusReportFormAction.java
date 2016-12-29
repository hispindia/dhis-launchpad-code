package org.hisp.dhis.asha.payment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.system.filter.PastAndCurrentPeriodFilter;
import org.hisp.dhis.system.util.FilterUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetASHAPaymentStatusReportFormAction implements Action
{
    public static final String ASHA_ACTIVITY = "ASHA Activity";
    public static final String PHC_GROUP = "PHC Group";//25.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    /*
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
    */
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    /*
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    */
    // -------------------------------------------------------------------------
    // Input / OUTPUT
    // -------------------------------------------------------------------------
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    private String status;
    
    public String getStatus()
    {
        return status;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        status = "NONE";
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        //Constant phcGroupConstant = constantService.getConstantByName( PHC_GROUP );
        
        //OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( (int) phcGroupConstant.getValue() );
        
        program = programService.getProgramByName( ASHA_ACTIVITY );
        
        /*
        if ( ( organisationUnit == null ) || ( !program.getOrganisationUnits().contains( organisationUnit ) ) || ( !organisationUnitGroup.getMembers().contains( organisationUnit ) )  )
        {
            status = i18n.getString( "please_select_sc_phc" );

            return SUCCESS;
        }
        */
        
        String periodTypeName = MonthlyPeriodType.NAME;
        
        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );
        
        Calendar cal = PeriodType.createCalendarInstance();
        
        periods = _periodType.generatePeriods( cal.getTime() );
        
        FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );

        Collections.reverse( periods );
        
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }

        return SUCCESS;
    }
    
}
