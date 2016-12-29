package org.hisp.dhis.dataanalyser.dslinelisting.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateDataStatusLineListingFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    // -------------------------------------------------------------------------
    // Getter and Setter Input/output
    // -------------------------------------------------------------------------
    
    private List<DataSet> dataSetList;

    public List<DataSet> getDataSetList()
    {
        return dataSetList;
    }
    
    private PeriodType monthlyPeriodType;

    public PeriodType getMonthlyPeriodType()
    {
        return monthlyPeriodType;
    }
    
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
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        
        dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Iterator<DataSet> dataSetListIterator = dataSetList.iterator();
        
        while(dataSetListIterator.hasNext())
        {
            DataSet dataSet = (DataSet) dataSetListIterator.next();
            if ( dataSet.getSources().size() <= 0 )
            {
                dataSetListIterator.remove();
            }
            else
            {
                if ( dataSet.getId() == 9 || dataSet.getId() == 10 )
                {
                    
                } 
                else
                {
                    dataSetListIterator.remove();
                }
            }
                        
        }
        
        Collections.sort( dataSetList, new IdentifiableObjectNameComparator() );
        
        /* Monthly Periods */
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
        return SUCCESS;
    }
}
