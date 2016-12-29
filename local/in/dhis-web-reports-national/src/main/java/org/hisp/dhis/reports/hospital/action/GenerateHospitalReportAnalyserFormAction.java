package org.hisp.dhis.reports.hospital.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateHospitalReportAnalyserFormAction implements Action
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
    // Getter & Setter
    // -------------------------------------------------------------------------

    private String periodTypeName;
    
    public String getPeriodTypeName()
    {
        return periodTypeName;
    }

    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }  
   
    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }
    
    private SimpleDateFormat simpleDateFormat;
    
    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        reportTypeName = ReportType.RT_HOSPITAL_REPORT;
        
        periodTypeName = MonthlyPeriodType.NAME;
        
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );

        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );
        
        Iterator<Period> periodIterator = periods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
        }

        Collections.sort( periods, new PeriodComparator() );
        
        periodNameList = new ArrayList<String>();
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        for ( Period p1 : periods )
        {
            periodNameList.add( simpleDateFormat.format( p1.getStartDate() ) );
        }
        
        return SUCCESS;
    }
}


