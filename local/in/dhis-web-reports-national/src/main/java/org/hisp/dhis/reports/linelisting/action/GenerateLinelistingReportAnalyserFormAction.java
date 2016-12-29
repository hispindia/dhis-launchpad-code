package org.hisp.dhis.reports.linelisting.action;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class GenerateLinelistingReportAnalyserFormAction
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

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private List<Period> monthlyPeriods;

    public List<Period> getMonthlyPeriods()
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
    
    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }
    
    private Collection<PeriodType> periodTypes;

    public Collection<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        reportTypeName = ReportType.RT_LINELIST;

        
        periodTypes = periodService.getAllPeriodTypes();

        Iterator<PeriodType> periodTypeIterator = periodTypes.iterator();
        while ( periodTypeIterator.hasNext() )
        {
            PeriodType type = periodTypeIterator.next();
            
            if( type.getName().equalsIgnoreCase("Monthly") || type.getName().equalsIgnoreCase("quarterly") || type.getName().equalsIgnoreCase("yearly") )
            {
            }
            else
            {
                periodTypeIterator.remove();
            }
        }
        
        /* Monthly Periods */
       
        /*
        monthlyPeriodType = new MonthlyPeriodType();

        monthlyPeriods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( monthlyPeriodType ) );
        Iterator<Period> periodIterator = monthlyPeriods.iterator();
        while ( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();

            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove();
            }

        }
        
        Collections.sort( monthlyPeriods, new PeriodComparator() );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
         */
        return SUCCESS;
    }

}
