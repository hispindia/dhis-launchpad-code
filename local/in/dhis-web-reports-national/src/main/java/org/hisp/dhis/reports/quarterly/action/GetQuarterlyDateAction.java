package org.hisp.dhis.reports.quarterly.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.reports.util.FPMUReportManager;

import com.opensymphony.xwork2.Action;

public class GetQuarterlyDateAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private FPMUReportManager fpmuReportManager;

    public void setFpmuReportManager( FPMUReportManager fpmuReportManager )
    {
        this.fpmuReportManager = fpmuReportManager;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    public List<String> yearsList = new ArrayList<String>();

    public void setYearsList( List<String> yearsList )
    {
        this.yearsList = yearsList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        List<String> years = fpmuReportManager.getMinAndMaxYear();
        for ( String year : years )
        {
            yearsList.add( year + "-" + (Integer.parseInt( year ) + 1) );
        }

        return SUCCESS;
    }

}
