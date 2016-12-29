package org.hisp.dhis.reports.scheduling.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;

import com.opensymphony.xwork2.Action;

public class UpdateSchedulAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Collection<Integer> reportList = new ArrayList<Integer>();

    public Collection<Integer> getReportList()
    {
        return reportList;
    }

    public void setReportList( Collection<Integer> reportList )
    {
        this.reportList = reportList;
    }

    private Collection<Integer> selectedReportList = new ArrayList<Integer>();

    public Collection<Integer> getSelectedReportList()
    {
        return selectedReportList;
    }

    public void setSelectedReportList( Collection<Integer> selectedReportList )
    {
        this.selectedReportList = selectedReportList;
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

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Report_in report = new Report_in();
        if ( reportList.size() > 0 )
        {
            for ( Integer id : reportList )
            {
                report = reportService.getReport( id.intValue() );
                report.setScheduled( !true );
                report.setEmailable( !true );
                reportService.updateReport( report );
            }
        }

        // System.out.println("emailAttachmentCB : "+emailAttachmentCB);
        if ( emailAttachmentCB != true )
        {
            if ( selectedReportList.size() > 0 )
            {
                for ( Integer id : selectedReportList )
                {
                    report = reportService.getReport( id.intValue() );
                    report.setScheduled( true );
                    report.setEmailable( false );
                    reportService.updateReport( report );
                }
            }
        }
        else
        {
            if ( selectedReportList.size() > 0 )
            {
                for ( Integer id : selectedReportList )
                {
                    report = reportService.getReport( id.intValue() );
                    report.setScheduled( true );
                    report.setEmailable( true );
                    reportService.updateReport( report );
                }
            }
        }
        return SUCCESS;
    }
}