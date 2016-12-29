package org.hisp.dhis.reports.auto.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class GenerateLLBulkReportAnalyserFormAction implements Action
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
    // Constants
    // -------------------------------------------------------------------------

    private final int ALL = 0;

    public int getALL()
    {
        return ALL;
    }
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Collection<PeriodType> periodTypes;

    public Collection<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    private String reportTypeName;

    public void setReportTypeName(String reportTypeName) 
    {
		this.reportTypeName = reportTypeName;
	}

	public String getReportTypeName()
    {
        return reportTypeName;
    }
    
    private List<String> downloadPeriodList = new ArrayList<String>();
	
 	public List<String> getDownloadPeriodList() 
 	{
 		return downloadPeriodList;
 	}

 	public void setDownloadPeriodList( List<String> downloadPeriodList ) 
 	{
 		this.downloadPeriodList = downloadPeriodList;
 	}
 	
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        reportTypeName = ReportType.RT_LINELIST_BULK_REPORT;      
        periodTypes = periodService.getAllPeriodTypes();

        // Filtering Period types other than Monthly, Quarterly and Yearly
        Iterator<PeriodType> periodTypeIterator = periodTypes.iterator();
        while ( periodTypeIterator.hasNext() )
        {
            PeriodType type = periodTypeIterator.next();
            if ( type.getName().equalsIgnoreCase( "Monthly" ) || type.getName().equalsIgnoreCase( "quarterly" )
                || type.getName().equalsIgnoreCase( "yearly" ) || type.getName().equalsIgnoreCase("weekly") || type.getName().equalsIgnoreCase("Daily") )
            {
            }
            else
            {
                periodTypeIterator.remove();
            }
        }
              
        return SUCCESS;
    }	
}


    

