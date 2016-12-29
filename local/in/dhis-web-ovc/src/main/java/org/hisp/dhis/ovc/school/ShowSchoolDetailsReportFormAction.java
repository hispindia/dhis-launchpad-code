package org.hisp.dhis.ovc.school;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowSchoolDetailsReportFormAction implements Action
{
  
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------


    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }
    
    private School school;
    
    public School getSchool()
    {
        return school;
    }
    
    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        school = schoolService.getSchool( id );
        
        organisationUnit  = school.getOrganisationUnit();
        
        String periodTypeName = QuarterlyPeriodType.NAME;
        /*
        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );
        
        Calendar cal = PeriodType.createCalendarInstance();
        
        periods = _periodType.generatePeriods( cal.getTime() );
        */
        
        //FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );

        //Collections.reverse( periods );
        
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );
        
        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );
        
        Calendar cal = PeriodType.createCalendarInstance();
        
        periods = _periodType.generatePeriods( cal.getTime() );
                
        /*
        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );
        
        
        periods.addAll( periodService.getPeriodsByPeriodType( periodType ) );
        
        if( periods.size() == 0 )
        {
            CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );
            
            Calendar cal = PeriodType.createCalendarInstance();
            
            periods = _periodType.generatePeriods( cal.getTime() );
        }
        */
        
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }
        
        return SUCCESS;
    }
}