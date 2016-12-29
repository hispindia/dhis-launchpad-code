package org.hisp.dhis.reports.scheduling.action;

/**
 * @author Brajesh Murari
 * @version $Id$
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.joda.time.DateTime;

import com.opensymphony.xwork2.Action;

public class ReportSchedulingFormAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private LinkedList<Period> monthlyPeriods;

    public LinkedList<Period> getMonthlyPeriods()
    {
        return monthlyPeriods;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private PeriodType monthlyPeriodType;

    public PeriodType getMonthlyPeriodType()
    {
        return monthlyPeriodType;
    }

    private Collection<Report_in> reportList = new ArrayList<Report_in>();

    public Collection<Report_in> getReportList()
    {
        return reportList;
    }

    public void setReportList( Collection<Report_in> reportList )
    {
        this.reportList = reportList;
    }

    private Collection<Report_in> selectedReportList = new ArrayList<Report_in>();

    public Collection<Report_in> getSelectedReportList()
    {
        return selectedReportList;
    }

    public void setSelectedReportList( Collection<Report_in> selectedReportList )
    {
        this.selectedReportList = selectedReportList;
    }

    private LinkedList<OrganisationUnitGroup> orgUnitGroups;

    public LinkedList<OrganisationUnitGroup> getOrgUnitGroups()
    {
        return orgUnitGroups;
    }

    private LinkedList<String> hoursInStringList;

    public LinkedList<String> getHoursInStringList()
    {
        return hoursInStringList;
    }

    public void setHoursInStringList( LinkedList<String> hoursInStringList )
    {
        this.hoursInStringList = hoursInStringList;
    }

    private boolean emailAttachmentCB;

    public boolean isEmailAttachmentCB()
    {
        return emailAttachmentCB;
    }

    public void setEmailAttachmentCB( boolean emailAttachmentCB )
    {
        this.emailAttachmentCB = emailAttachmentCB;
    }

    int currentYear;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        /* Available and Selected Reports List For Scheduling */
        reportList = new ArrayList<Report_in>();
        Collection<Report_in> tempReportList = new ArrayList<Report_in>();
        tempReportList.addAll( reportService.getAllSchedulableReports() );

        if ( tempReportList != null )
        {
            for ( Report_in ri : tempReportList )
            {
                if ( ri.isScheduled() )
                {
                    emailAttachmentCB = ri.isEmailable();
                    selectedReportList.add( ri );
                }
                else
                {
                    reportList.add( ri );
                }
            }
        }

        /* Current Yearly Period */
        currentYear = new DateTime().getYear();

        /* Monthly Periods */
        monthlyPeriodType = new MonthlyPeriodType();
        monthlyPeriods = new LinkedList<Period>( periodService.getPeriodsByPeriodType( monthlyPeriodType ) );
        Date currentDate = new Date();
        String currentDateInString = String.valueOf( currentDate );
        String tmpe[] = currentDateInString.split( " " );
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, Integer.parseInt( tmpe[5] ) - 1 );
        Date twoYearBackThenCurrentDate = cal.getTime();
        Iterator<Period> periodIterator = monthlyPeriods.iterator();
        while ( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            if ( p1.getStartDate().compareTo( currentDate ) > 0
                || p1.getStartDate().compareTo( twoYearBackThenCurrentDate ) < 0 )
            {
                periodIterator.remove();
            }
        }

        Collections.sort( monthlyPeriods, new PeriodComparator() );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        /* Hourly Periods */
        hoursInStringList = new LinkedList<String>();
        for ( int i = 0, j = 0; i < 25; )
        {
            if ( i < 10 )
            {
                String hoursInString = "0" + i + ":" + "00" + "AM";
                hoursInStringList.add( hoursInString );
                i++;
            }
            else if ( i > 9 && i < 13 )
            {
                String hoursInString = "" + i + ":" + "00" + "AM";
                hoursInStringList.add( hoursInString );
                i++;
            }
            else
            {
                if ( j < 10 )
                {
                    String hoursInString = "0" + j + ":" + "00" + "PM";
                    hoursInStringList.add( hoursInString );
                    i++;
                    j++;
                }
                else if ( j > 9 && j < 13 )
                {
                    String hoursInString = "" + j + ":" + "00" + "PM";
                    hoursInStringList.add( hoursInString );
                    i++;
                    j++;
                }
            }
        }
        return SUCCESS;
    }
}
