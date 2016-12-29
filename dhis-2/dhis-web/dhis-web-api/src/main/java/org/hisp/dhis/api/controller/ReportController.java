package org.hisp.dhis.api.controller;

/*
 * Copyright (c) 2004-2011, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hisp.dhis.api.utils.IdentifiableObjectParams;
import org.hisp.dhis.api.utils.WebLinkPopulator;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.report.ReportService;
import org.hisp.dhis.report.Reports;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.api.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping( value = ReportController.RESOURCE_PATH )
public class ReportController
{
    public static final String RESOURCE_PATH = "/reports";

    @Autowired
    public ReportService reportService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private I18nManager i18nManager;

    // -------------------------------------------------------------------------------------------------------
    // GET
    // -------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.GET )
    public String getReports( IdentifiableObjectParams params, Model model, HttpServletRequest request )
    {
        Reports reports = new Reports();

        if ( params.isPaging() )
        {
            int total = reportService.getReportCount();

            Pager pager = new Pager( params.getPage(), total );
            reports.setPager( pager );

            List<Report> reportList = new ArrayList<Report>(
                reportService.getReportsBetween( pager.getOffset(), pager.getPageSize() ) );

            reports.setReports( reportList );
        }
        else
        {
            reports.setReports( new ArrayList<Report>( reportService.getAllReports() ) );
        }

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( reports );
        }

        model.addAttribute( "model", reports );

        return "reports";
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public String getReport( @PathVariable( "uid" ) String uid, IdentifiableObjectParams params, Model model,
                             HttpServletRequest request )
    {
        Report report = reportService.getReport( uid );

        if ( params.hasLinks() )
        {
            WebLinkPopulator listener = new WebLinkPopulator( request );
            listener.addLinks( report );
        }

        model.addAttribute( "model", report );

        return "report";
    }

    @RequestMapping( value = {"/{uid}/data", "/{uid}/data.pdf"}, method = RequestMethod.GET )
    public void getReportAsPdf( @PathVariable( "uid" ) String uid,
                                @RequestParam( value = "ou", required = false ) String organisationUnitUid,
                                @RequestParam( value = "pe", required = false ) String period, 
                                HttpServletResponse response ) throws Exception
    {
        getReport( uid, organisationUnitUid, period, response, "pdf", ContextUtils.CONTENT_TYPE_PDF, false );
    }

    @RequestMapping( value = "/{uid}/data.xls", method = RequestMethod.GET )
    public void getReportAsXls( @PathVariable( "uid" ) String uid,
                                @RequestParam( value = "ou", required = false ) String organisationUnitUid,
                                @RequestParam( value = "pe", required = false ) String period, 
                                HttpServletResponse response ) throws Exception
    {
        getReport( uid, organisationUnitUid, period, response, "xls", ContextUtils.CONTENT_TYPE_EXCEL, true );
    }

    // -------------------------------------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------------------------------------

    private void getReport( String uid, String organisationUnitUid, String period,
                            HttpServletResponse response, String type, String contentType, boolean attachment ) throws Exception
    {
        Report report = reportService.getReport( uid );

        if ( organisationUnitUid == null && report.hasReportTable() && report.getReportTable().hasReportParams()
            && report.getReportTable().getReportParams().isOrganisationUnitSet() )
        {
            organisationUnitUid = organisationUnitService.getRootOrganisationUnits().iterator().next().getUid();
        }

        Date date = period != null ? DateUtils.getMediumDate( period ) : new Cal().now().subtract( Calendar.MONTH, 1 ).time();

        String filename = CodecUtils.filenameEncode( report.getName() ) + "." + type;
        ContextUtils.configureResponse( response, contentType, true, filename, attachment );

        reportService.renderReport( response.getOutputStream(), uid, date, organisationUnitUid, type,
            i18nManager.getI18nFormat() );
    }
}
