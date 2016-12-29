package org.hisp.dhis.reports.quarterly.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import com.opensymphony.xwork2.Action;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.reports.util.DFSReport;
import org.hisp.dhis.reports.util.FPMUReportManager;
import org.hisp.dhis.system.util.MathUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Samta Bajpai
 * @version $Id$
 */
public class GenerateQuarterlyReportAction
        implements Action {
    private static final String R_QUARTERLY_SCRIPT_FILE = "quarterly_report.R";


    protected JasperPrint jasperPrint;

    protected JasperReport jr;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private FPMUReportManager fpmuReportManager;

    public void setFpmuReportManager(FPMUReportManager fpmuReportManager) {
        this.fpmuReportManager = fpmuReportManager;
    }

    private ConstantService constantService;

    public void setConstantService(ConstantService constantService) {
        this.constantService = constantService;
    }

    private I18nFormat format;

    public void setFormat(I18nFormat format) {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private String reportYear;

    public void setReportYear(String reportYear) {
        this.reportYear = reportYear;
    }

    public String reportQuarter;

    public void setReportQuarter(String reportQuarter) {
        this.reportQuarter = reportQuarter;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        HashMap<String, Object> hash = new HashMap<String, Object>();
        SimpleDateFormat standardDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, String> resultMap = new HashMap<String, String>();

        Constant constant = constantService.getConstantByName(DFSReport.CONSTANT_MAX_DAYS_FOR_LAST_AVAILABLE_DATE);
        Double maxDaysForLastAvailableDate = -30.0;
        if (constant == null) {
        }
        else {
            maxDaysForLastAvailableDate = constant.getValue();
        }

        //----------------------------------------------------------------------
        // Report Published Date
        //----------------------------------------------------------------------

        String quarterMonth = null;
        String footerDate = null;
        String selectedQuarterStartDate = "";
        String selectedQuarterEndDate = "";
        String currentFiscalYear = "";
        String currentQuarterMonth1Start = "";
        String currentQuarterMonth1End = "";
        String currentQuarterMonth2Start = "";
        String currentQuarterMonth2End = "";
        String currentQuarterMonth3Start = "";
        String currentQuarterMonth3End = "";
        String currentFiscalYearQuarterText = "";

        if (reportQuarter.trim().equalsIgnoreCase("q1")) {
            quarterMonth = "January-March";
            footerDate = "31 March, " + reportYear;
            selectedQuarterStartDate = reportYear + "-" + "01-01";
            selectedQuarterEndDate = reportYear + "-" + "03-31";

            currentFiscalYear = (Integer.parseInt(reportYear) - 1) + "-" + reportYear;
            currentQuarterMonth1Start = reportYear + "-01-01";
            currentQuarterMonth1End = reportYear + "-01-31";
            currentQuarterMonth2Start = reportYear + "-02-01";
            if (Integer.parseInt(reportYear) % 100 == 0) {
                currentQuarterMonth2End = reportYear + "-02-29";
            } else {
                currentQuarterMonth2End = reportYear + "-02-28";
            }
            currentQuarterMonth3Start = reportYear + "-03-01";
            currentQuarterMonth3End = reportYear + "-03-31";
            currentFiscalYearQuarterText = "Q3";
        } else if (reportQuarter.trim().equalsIgnoreCase("q2")) {
            quarterMonth = "April-June";
            footerDate = "30 June, " + reportYear;
            selectedQuarterStartDate = reportYear + "-" + "04-01";
            selectedQuarterEndDate = reportYear + "-" + "06-30";

            currentFiscalYear = (Integer.parseInt(reportYear) - 1) + "-" + reportYear;
            currentQuarterMonth1Start = reportYear + "-04-01";
            currentQuarterMonth1End = reportYear + "-04-30";
            currentQuarterMonth2Start = reportYear + "-05-01";
            currentQuarterMonth2End = reportYear + "-05-31";
            currentQuarterMonth3Start = reportYear + "-06-01";
            currentQuarterMonth3End = reportYear + "-06-30";
            currentFiscalYearQuarterText = "Q4";
        } else if (reportQuarter.trim().equalsIgnoreCase("q3")) {
            quarterMonth = "July-September";
            footerDate = "30 September, " + reportYear;
            selectedQuarterStartDate = reportYear + "-" + "07-01";
            selectedQuarterEndDate = reportYear + "-" + "09-30";

            currentFiscalYear = reportYear + "-" + (Integer.parseInt(reportYear) + 1);
            currentQuarterMonth1Start = reportYear + "-07-01";
            currentQuarterMonth1End = reportYear + "-07-31";
            currentQuarterMonth2Start = reportYear + "-08-01";
            currentQuarterMonth2End = reportYear + "-08-31";
            currentQuarterMonth3Start = reportYear + "-09-01";
            currentQuarterMonth3End = reportYear + "-09-30";
            currentFiscalYearQuarterText = "Q1";
        } else if (reportQuarter.trim().equalsIgnoreCase("q4")) {
            quarterMonth = "October-December";
            footerDate = "31 December, " + reportYear;
            selectedQuarterStartDate = reportYear + "-" + "10-01";
            selectedQuarterEndDate = reportYear + "-" + "12-31";

            currentFiscalYear = reportYear + "-" + (Integer.parseInt(reportYear) + 1);
            currentQuarterMonth1Start = reportYear + "-10-01";
            currentQuarterMonth1End = reportYear + "-10-31";
            currentQuarterMonth2Start = reportYear + "-11-01";
            currentQuarterMonth2End = reportYear + "-11-30";
            currentQuarterMonth3Start = reportYear + "-12-01";
            currentQuarterMonth3End = reportYear + "-12-31";
            currentFiscalYearQuarterText = "Q2";
        }

        hash.put("date", quarterMonth + "," + reportYear);

        resultMap.put("footerdate", footerDate);

        //--------------------------------------------------------------------
        // Initializing R chart paths in resultMap
        //--------------------------------------------------------------------
        String chartOutputPath = System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "chartoutput" + File.separator;
        resultMap.put("quarterlychart1path", chartOutputPath + "quarterlychart1_" + quarterMonth + "_" + reportYear + ".png");
        resultMap.put("quarterlychart2path", chartOutputPath + "quarterlychart2_" + quarterMonth + "_" + reportYear + ".png");
        resultMap.put("quarterlychart3path", chartOutputPath + "quarterlychart3_" + quarterMonth + "_" + reportYear + ".png");
        resultMap.put("quarterlychart4path", chartOutputPath + "quarterlychart4_" + quarterMonth + "_" + reportYear + ".png");
        resultMap.put("quarterlychart5path", chartOutputPath + "quarterlychart5_" + quarterMonth + "_" + reportYear + ".png");
        resultMap.put("quarterlychart6path", chartOutputPath + "quarterlychart6_" + quarterMonth + "_" + reportYear + ".png");
        resultMap.put("quarterlychart7path", chartOutputPath + "quarterlychart7_" + quarterMonth + "_" + reportYear + ".png");
        resultMap.put("quarterlychart8path", chartOutputPath + "quarterlychart8_" + quarterMonth + "_" + reportYear + ".png");
        resultMap.put("quarterlychart9path", chartOutputPath + "quarterlychart9_" + quarterMonth + "_" + reportYear + ".png");
        resultMap.put("quarterlychart10path", chartOutputPath + "quarterlychart10_" + quarterMonth + "_" + reportYear + ".png");

        //--------------------------------------------------------------------
        // Calling R Script
        //--------------------------------------------------------------------
        String quarterlyReportChartsPath = "";
        String rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" + File.separator + R_QUARTERLY_SCRIPT_FILE + " " + currentQuarterMonth3End + " " + chartOutputPath;
        System.out.println("rScriptPath : " + rScriptPath);

        try {
            Runtime rt = Runtime.getRuntime();
            Process pr;
            pr = rt.exec(rScriptPath);
            int exitVal = pr.waitFor();

            System.out.println("Exit Val : " + exitVal + " -- " + rScriptPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator +  R_QUARTERLY_SCRIPT_FILE_B + " " + currentQuarterMonth3End + " " + chartOutputPath;
        try 
        {
            Runtime rt = Runtime.getRuntime();
            Process pr;
            pr = rt.exec( rScriptPath );
            int exitVal = pr.waitFor();
            
            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
        }
        catch( Exception e ) 
        {
        	e.printStackTrace();
        }

        rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator + R_QUARTERLY_SCRIPT_FILE_C + " " + currentQuarterMonth3End + " " + chartOutputPath;
        try 
        {
            Runtime rt = Runtime.getRuntime();
            Process pr;
            pr = rt.exec( rScriptPath );
            int exitVal = pr.waitFor();
            
            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
        }
        catch( Exception e ) 
        {
        	e.printStackTrace();
        }

        rScriptPath = "Rscript " + System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "rscript" +  File.separator + R_QUARTERLY_SCRIPT_FILE_D + " " + currentQuarterMonth3End + " " + chartOutputPath;
        try 
        {
            Runtime rt = Runtime.getRuntime();
            Process pr;
            pr = rt.exec( rScriptPath );
            int exitVal = pr.waitFor();
            
            System.out.println( "Exit Val : " + exitVal + " -- " + rScriptPath );
        }
        catch( Exception e ) 
        {
        	e.printStackTrace();
        }
*/

        //----------------------------------------------------------------------
        // Calculation of table cell data based on xml file mapping
        //----------------------------------------------------------------------
        List<DFSReport> reportDesignList = new ArrayList<DFSReport>(fpmuReportManager.getDFSReportDesign("Quarterly_Report.xml"));

        for (DFSReport dsfReport : reportDesignList) {
            String period = dsfReport.getPeriod();
            String dataElementExp = dsfReport.getDataelement();
            String name = dsfReport.getName();
            String celltype = dsfReport.getCelltype();
            String aggtype = dsfReport.getAggtype();
            String orgUnitId = "0";
            String result = "";
            String dataType = dsfReport.getDatatype();

            if (dsfReport.getOrgunit().equalsIgnoreCase("na")) {
                orgUnitId = "0";
            } else {
                orgUnitId = dsfReport.getOrgunit();
            }

            if (celltype.equalsIgnoreCase(DFSReport.CELL_TYPE_DATE)) {
                if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_START_DATE)) {
                    result = Integer.parseInt(currentFiscalYear.split("-")[0]) + "-07-01";
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_QUARTER_START_DATE)) {
                    result = currentQuarterMonth1Start;
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_QUARTER_END_DATE)) {
                    result = currentQuarterMonth3End;
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-07-01";
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-06-30";
                } else if (period.equalsIgnoreCase(DFSReport.LAST_2ND_FISCAL_YEAR_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[0]) - 2) + "-07-01";
                } else if (period.equalsIgnoreCase(DFSReport.LAST_2ND_FISCAL_YEAR_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 2) + "-06-30";
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_1ST_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-07-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-07-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_1ST_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-07-31";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-07-31");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_2ND_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-08-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-08-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_2ND_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-08-31";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-08-31");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_3RD_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-09-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-09-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_3RD_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-09-30";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-09-30");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_4TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-10-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-10-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_4TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-10-31";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-10-31");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_5TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-11-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-11-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_5TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-11-30";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-11-30");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_6TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-12-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-12-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_6TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-12-31";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-12-31");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_7TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-01-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-01-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_7TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-01-31";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-01-31");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_8TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-02-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-02-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_8TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-02-29";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-02-29");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_9TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-03-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-03-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_9TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-03-31";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-03-31");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_10TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-04-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-04-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_10TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-04-30";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-04-30");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_11TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-05-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-05-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_11TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-05-31";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-05-31");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_12TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-06-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-06-01");
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_12TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-06-30";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-06-30");
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_1ST_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-07-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-07-01");
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_1ST_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-07-31";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-07-31");
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_2ND_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-08-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-08-01");
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_2ND_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-08-31";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-08-31");
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_3RD_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-09-01";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-09-01");
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_3RD_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-09-30";
                    resultMap.put(name + "t3", (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-09-30");
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_4TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-10-01";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-10-01";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_4TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-10-31";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-10-31";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )) + "-10-31" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_5TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-11-01";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-11-01";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )) + "-11-01" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_5TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-11-30";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-11-30";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )) + "-11-30" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_6TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-12-01";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-12-01";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )) + "-12-01" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_6TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1])) + "-12-31";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-12-31";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )) + "-12-31" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_7TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-01-01";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-01-01";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-01-01" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_7TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-01-31";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-01-31";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-01-31" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_8TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-02-01";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-02-01";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-02-01" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_8TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-02-29";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-02-29";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-02-29" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_9TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-03-01";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-03-01";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-03-01" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_9TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-03-31";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-03-31";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-03-31" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_10TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-04-01";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-04-01";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-04-01" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_10TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-04-30";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-04-30";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-04-30" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_11TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-05-01";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-05-01";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-05-01" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_11TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-05-31";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-05-31";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-05-31" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_12TH_MONTH_START_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-06-01";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-06-01";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-06-01" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_12TH_MONTH_END_DATE)) {
                    result = (Integer.parseInt(currentFiscalYear.split("-")[1]) + 1) + "-06-30";
                    String t1 = (Integer.parseInt(currentFiscalYear.split("-")[0]) + 1) + "-06-30";
                    if (t1.compareTo(currentQuarterMonth3End) > 0) {
                        resultMap.put(name + "t3", "YYYY-MM-DD");
                    } else {
                        resultMap.put(name + "t3", t1);
                    }
                    //resultMap.put( name+"t3", (Integer.parseInt( currentFiscalYear.split("-")[0] )+1) + "-06-30" );
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_QUARTER_1ST_MONTH_START_DATE)) {
                    result = currentQuarterMonth1Start;
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_QUARTER_1ST_MONTH_END_DATE)) {
                    result = currentQuarterMonth1End;
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_QUARTER_3RD_MONTH_START_DATE)) {
                    result = currentQuarterMonth3Start;
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_QUARTER_3RD_MONTH_END_DATE)) {
                    result = currentQuarterMonth3End;
                } else if (period.equalsIgnoreCase(DFSReport.LAST_YAEAR_CURRENT_QUARTER_1ST_MONTH_START_DATE)) {
                    result = Integer.parseInt(currentQuarterMonth1Start.split("-")[0]) - 1 + "-" + currentQuarterMonth1Start.split("-")[1] + "-" + currentQuarterMonth1Start.split("-")[2];
                } else if (period.equalsIgnoreCase(DFSReport.LAST_YAEAR_CURRENT_QUARTER_1ST_MONTH_END_DATE)) {
                    result = Integer.parseInt(currentQuarterMonth1End.split("-")[0]) - 1 + "-" + currentQuarterMonth1End.split("-")[1] + "-" + currentQuarterMonth1End.split("-")[2];
                } else if (period.equalsIgnoreCase(DFSReport.LAST_YAEAR_CURRENT_QUARTER_3RD_MONTH_START_DATE)) {
                    result = Integer.parseInt(currentQuarterMonth3Start.split("-")[0]) - 1 + "-" + currentQuarterMonth3Start.split("-")[1] + "-" + currentQuarterMonth3Start.split("-")[2];
                } else if (period.equalsIgnoreCase(DFSReport.LAST_YAEAR_CURRENT_QUARTER_3RD_MONTH_END_DATE)) {
                    result = Integer.parseInt(currentQuarterMonth3End.split("-")[0]) - 1 + "-" + currentQuarterMonth3End.split("-")[1] + "-" + currentQuarterMonth3End.split("-")[2];
                } else if (period.equalsIgnoreCase(DFSReport.LAST_YAEAR_CURRENT_QUARTER_NEXT_MONTH_START_DATE)) {
                    if (Integer.parseInt(currentQuarterMonth3End.split("-")[1]) == 12) {
                        result = Integer.parseInt(currentQuarterMonth3End.split("-")[0]) + "-01-01";
                    } else {
                        result = Integer.parseInt(currentQuarterMonth3End.split("-")[0]) - 1 + "-" + (Integer.parseInt(currentQuarterMonth3End.split("-")[1]) + 1) + "-01";
                    }
                } else if (period.equalsIgnoreCase(DFSReport.LAST_YAEAR_CURRENT_QUARTER_NEXT_MONTH_END_DATE)) {
                    if (Integer.parseInt(currentQuarterMonth3End.split("-")[1]) == 12) {
                        result = Integer.parseInt(currentQuarterMonth3End.split("-")[0]) + "-01-31";
                    } else {
                        int monthDays[] = {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
                        result = Integer.parseInt(currentQuarterMonth3End.split("-")[0]) - 1 + "-" + (Integer.parseInt(currentQuarterMonth3End.split("-")[1]) + 1) + "-" + monthDays[Integer.parseInt(currentQuarterMonth3End.split("-")[1]) + 1];
                    }
                } else if (period.equalsIgnoreCase(DFSReport.LAST_2ND_YAEAR_CURRENT_QUARTER_NEXT_MONTH_START_DATE)) {
                    if (Integer.parseInt(currentQuarterMonth3End.split("-")[1]) == 12) {
                        result = Integer.parseInt(currentQuarterMonth3End.split("-")[0]) - 1 + "-01-01";
                    } else {
                        result = Integer.parseInt(currentQuarterMonth3End.split("-")[0]) - 2 + "-" + (Integer.parseInt(currentQuarterMonth3End.split("-")[1]) + 1) + "-01";
                    }
                } else if (period.equalsIgnoreCase(DFSReport.LAST_2ND_YAEAR_CURRENT_QUARTER_NEXT_MONTH_END_DATE)) {
                    if (Integer.parseInt(currentQuarterMonth3End.split("-")[1]) == 12) {
                        result = Integer.parseInt(currentQuarterMonth3End.split("-")[0]) - 1 + "-01-31";
                    } else {
                        int monthDays[] = {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
                        result = Integer.parseInt(currentQuarterMonth3End.split("-")[0]) - 2 + "-" + (Integer.parseInt(currentQuarterMonth3End.split("-")[1]) + 1) + "-" + monthDays[Integer.parseInt(currentQuarterMonth3End.split("-")[1]) + 1];
                    }
                }
            } else if (celltype.equalsIgnoreCase(DFSReport.CELL_TYPE_FORMULA_USE_FOR_STARTDATE_AND_ENDDATE)) {
                if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    String dataElements[] = dataElementExp.split("--");
                    String startDate = resultMap.get(dataElements[0]);
                    if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
                        result = " ";
                    } else {
                        String endDate = resultMap.get(dataElements[1]);

                        if (endDate == null || endDate.trim().equals("") || endDate.equalsIgnoreCase("YYYY-MM-DD")) {
                            result = " ";
                        } else {
                            result = fpmuReportManager.getIntegerResultValue(dataElements[2], startDate, endDate, orgUnitId, aggtype, celltype);
                        }
                    }
                }
            } else if (celltype.equalsIgnoreCase(DFSReport.CELL_TYPE_FORMULA_USE_FOR_STARTDATE_AND_ENDDATE_PROJECTION)) {
                if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    String dataElements[] = dataElementExp.split("--");
                    String startDate = resultMap.get(dataElements[0]);
                    if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
                        result = " ";
                    } else {
                        String endDate = resultMap.get(dataElements[1]);

                        if (endDate == null || endDate.trim().equals("") || endDate.equalsIgnoreCase("YYYY-MM-DD")) {
                            result = " ";
                        } else {
                            if (endDate.compareTo(currentQuarterMonth3End) <= 0) {
                                System.out.print("Actual : ");
                                result = fpmuReportManager.getIntegerResultValue(dataElements[4], startDate, endDate, orgUnitId, aggtype, celltype);
                            } else {
                                startDate = resultMap.get(dataElements[3]);
                                endDate = resultMap.get(dataElements[2]);
                                System.out.print("Projection : " + startDate + " " + endDate + dataElements[5] + " ");

                                result = fpmuReportManager.getIntegerResultValue(dataElements[5], startDate, endDate, orgUnitId, DFSReport.AGG_TYPE_ONE_PERIOD, celltype);
                            }
                        }
                    }
                }
            } else if (celltype.equalsIgnoreCase(DFSReport.CELL_TYPE_FORMULATEXT) || celltype.equalsIgnoreCase(DFSReport.CELL_TYPE_FORMULANUMBER)) {
                result = getIntegerResultValueForFormula(dataElementExp, celltype, resultMap);
            } else {
                if (period.equalsIgnoreCase(DFSReport.LAST_3RD_FISCAL_YEAR)) {
                    String startDate = (Integer.parseInt(currentFiscalYear.split("-")[0]) - 3) + "-07-01";
                    String endDate = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 3) + "-06-30";

                    System.out.println(startDate + " -- " + endDate);
                    result = fpmuReportManager.getIntegerResultValue(dataElementExp, startDate, endDate, orgUnitId, aggtype, celltype);
                } else if (period.equalsIgnoreCase(DFSReport.LAST_2ND_FISCAL_YEAR)) {
                    String startDate = (Integer.parseInt(currentFiscalYear.split("-")[0]) - 2) + "-07-01";
                    String endDate = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 2) + "-06-30";

                    System.out.println(startDate + " -- " + endDate);
                    result = fpmuReportManager.getIntegerResultValue(dataElementExp, startDate, endDate, orgUnitId, aggtype, celltype);
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR)) {
                    String startDate = (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-07-01";
                    String endDate = (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1) + "-06-30";

                    System.out.println(startDate + " -- " + endDate);
                    result = fpmuReportManager.getIntegerResultValue(dataElementExp, startDate, endDate, orgUnitId, aggtype, celltype);
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_QUARTER_MONTH1)) {
                    String startDate = currentQuarterMonth1Start;
                    String endDate = currentQuarterMonth1End;

                    System.out.println(startDate + " -- " + endDate);
                    result = fpmuReportManager.getIntegerResultValue(dataElementExp, startDate, endDate, orgUnitId, aggtype, celltype);
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_QUARTER_MONTH2)) {
                    String startDate = currentQuarterMonth2Start;
                    String endDate = currentQuarterMonth2End;

                    System.out.println(startDate + " -- " + endDate);
                    result = fpmuReportManager.getIntegerResultValue(dataElementExp, startDate, endDate, orgUnitId, aggtype, celltype);
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_QUARTER_MONTH3)) {
                    String startDate = currentQuarterMonth3Start;
                    String endDate = currentQuarterMonth3End;

                    System.out.println(startDate + " -- " + endDate);
                    result = fpmuReportManager.getIntegerResultValue(dataElementExp, startDate, endDate, orgUnitId, aggtype, celltype);
                }
            }

            if (dataType.equalsIgnoreCase(DFSReport.DATA_TYPE_NUMBER) && (result == null || result.trim().equals("") || result.isEmpty())) {
                resultMap.put(name, "0.0");
            } else {
                resultMap.put(name, result);
            }

            System.out.println(name + " --- " + result);
        }

        //----------------------------------------------------------------------
        // Report Generated Date
        //----------------------------------------------------------------------
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String reportGeneratedDate = dateTimeFormat.format(new Date());
        hash.put("reportGeneratedDate", reportGeneratedDate);

        hash.put("resultMap", resultMap);

        //----------------------------------------------------------------------
        // Initializing result map for date formatting purpose
        //----------------------------------------------------------------------
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM,yy");
        SimpleDateFormat onlyMonthFormat = new SimpleDateFormat("MMM");
        SimpleDateFormat ddmmyyyyFormat = new SimpleDateFormat("dd/MM/yyyy");
        String temp = "";
        //----------------------------------------------------------------------
        // TABLE - 1
        //----------------------------------------------------------------------        
        //Table 1 current fiscal year - 3 date
        String tempVar = (Integer.parseInt(currentFiscalYear.split("-")[0]) - 3) + "-" + (Integer.parseInt(currentFiscalYear.split("-")[1]) - 3);
        resultMap.put("table1heading1", tempVar);

        //Table 1 current fiscal year - 2 date
        tempVar = (Integer.parseInt(currentFiscalYear.split("-")[0]) - 2) + "-" + (Integer.parseInt(currentFiscalYear.split("-")[1]) - 2);
        resultMap.put("table1heading2", tempVar);

        //Table 1 current fiscal year - 1 date
        tempVar = (Integer.parseInt(currentFiscalYear.split("-")[0]) - 1) + "-" + (Integer.parseInt(currentFiscalYear.split("-")[1]) - 1);
        resultMap.put("table1heading3", tempVar);

        //Table 1 current fiscal year date
        tempVar = (Integer.parseInt(currentFiscalYear.split("-")[0])) + "-" + (Integer.parseInt(currentFiscalYear.split("-")[1]));
        resultMap.put("table1heading4", tempVar);

        //Table 1 actual by month quarter (current fiscal quarter)
        tempVar = currentFiscalYearQuarterText;
        resultMap.put("table1heading5", tempVar);

        //Table 1 current quarter month1
        temp = currentQuarterMonth1Start;
        tempVar = onlyMonthFormat.format(standardDateFormat.parse(temp));
        resultMap.put("table1heading6", tempVar);

        //Table 1 current quarter month2
        temp = currentQuarterMonth2Start;
        tempVar = onlyMonthFormat.format(standardDateFormat.parse(temp));
        resultMap.put("table1heading7", tempVar);

        //Table 1 current quarter month3
        temp = currentQuarterMonth3Start;
        tempVar = onlyMonthFormat.format(standardDateFormat.parse(temp));
        resultMap.put("table1heading8", tempVar);

        //Table 1 current fiscal year quarter 1 actual/projected
        temp = resultMap.get("reportcell13");
        if (temp.compareTo(currentQuarterMonth3End) <= 0) {
            resultMap.put("table1heading9", "Actual");
        } else {
            resultMap.put("table1heading9", "Projected");
        }

        //Table 1 current fiscal year quarter 2 actual/projected
        temp = resultMap.get("reportcell19a");
        if (temp.compareTo(currentQuarterMonth3End) <= 0) {
            resultMap.put("table1heading10", "Actual");
        } else {
            resultMap.put("table1heading10", "Projected");
        }

        //Table 1 current fiscal year quarter 3 actual/projected
        temp = resultMap.get("reportcell24");
        if (temp.compareTo(currentQuarterMonth3End) <= 0) {
            resultMap.put("table1heading11", "Actual");
        } else {
            resultMap.put("table1heading11", "Projected");
        }

        //Table 1 current fiscal year quarter 4 actual/projected
        temp = resultMap.get("reportcell30");
        if (temp.compareTo(currentQuarterMonth3End) <= 0) {
            resultMap.put("table1heading12", "Actual");
        } else {
            resultMap.put("table1heading12", "Projected");
        }

        //----------------------------------------------------------------------
        // TABLE - 2
        //----------------------------------------------------------------------        


        //----------------------------------------------------------------------
        // TABLE - 4
        //----------------------------------------------------------------------
        //Table 4 current quarter start date and end date (ex:- Jul,12 to Sep,12 )
        temp = resultMap.get("reportcell55");
        tempVar = monthFormat.format(standardDateFormat.parse(temp));
        temp = resultMap.get("reportcell57");
        tempVar = tempVar + " to " + monthFormat.format(standardDateFormat.parse(temp));
        resultMap.put("table4heading1", tempVar);

        //Table 4 last year current quarter start date and end date (ex:- Jul,11 to Sep,11 )
        temp = resultMap.get("reportcell59");
        tempVar = monthFormat.format(standardDateFormat.parse(temp));
        temp = resultMap.get("reportcell61");
        tempVar = tempVar + " to " + monthFormat.format(standardDateFormat.parse(temp));
        resultMap.put("table4heading2", tempVar);

        //Table 4 last year current quarter next month start date and end date (ex:- Oct,11 to Sep,12 )
        temp = resultMap.get("reportcell63");
        tempVar = monthFormat.format(standardDateFormat.parse(temp));
        temp = resultMap.get("reportcell57");
        tempVar = tempVar + " to " + monthFormat.format(standardDateFormat.parse(temp));
        resultMap.put("table4heading3", tempVar);

        //Table 4 last second year current quarter next month start date and end date (ex:- Oct,10 to Sep,11 )
        temp = resultMap.get("reportcell65");
        tempVar = monthFormat.format(standardDateFormat.parse(temp));
        temp = resultMap.get("reportcell61");
        tempVar = tempVar + " to " + monthFormat.format(standardDateFormat.parse(temp));
        resultMap.put("table4heading4", tempVar);

        //----------------------------------------------------------------------
        // Jasper code for generating report in doc format
        //----------------------------------------------------------------------

        String jrxmlFilePath = System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator + "quarterlyReport.jrxml";

        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlFilePath);
        jasperPrint = JasperFillManager.fillReport(jasperReport, hash, new JREmptyDataSource());

        ServletOutputStream outputStream = response.getOutputStream();

        JRExporter exporter = null;


        response.setContentType("application/msword");

        response.setHeader("Content-Disposition", "inline; fileName=\"Quarterly_" + quarterMonth + "_" + reportYear + ".doc\"");

        exporter = new JRDocxExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);

        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);

        try {
            exporter.exportReport();
        } catch (JRException e) {
            throw new ServletException(e);
        }

        return SUCCESS;
    }


    public boolean isDouble( String input )
    {
        try
        {
            Double.parseDouble( input );
            return true;
        }
        catch( Exception e)
        {
            return false;
        }
    }


    private String getIntegerResultValueForFormula(String formula, String cellType, Map<String, String> resultMap) {
        try {
            Pattern pattern = Pattern.compile("(\\[(.+?)\\])");

            Matcher matcher = pattern.matcher(formula);
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while (matcher.find()) {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll("[\\[\\]]", "");

                String dataValue = null;

                dataValue = resultMap.get(replaceString);
                if (dataValue == null) {
                    replaceString = " ";
                } else {
                    replaceString = String.valueOf(dataValue);
                }

                matcher.appendReplacement(buffer, replaceString);

                resultValue = replaceString;
            }

            matcher.appendTail(buffer);

            if (cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_FORMULANUMBER)) {
                double d = 0.0;
                try {
                    d = MathUtils.calculateExpression(buffer.toString());
                    d = Math.round(d * Math.pow(10, 2)) / Math.pow(10, 2);
                } catch (Exception e) {
                    d = 0.0;
                    resultValue = "";
                }

                resultValue = "" + d;
            } else {
                resultValue = buffer.toString();
            }

            if (resultValue.equalsIgnoreCase("")) {
                resultValue = " ";
            }

            return resultValue;
        } catch (Exception e) {
            throw new RuntimeException("Exception in getIntegerResultValue method: ", e);
        }
    }


}

