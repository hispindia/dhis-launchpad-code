package org.hisp.dhis.reports.dailyfood.action;

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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.reports.util.DFSReport;
import org.hisp.dhis.reports.util.FPMUReportManager;
import org.hisp.dhis.system.util.MathUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Samta Bajpai
 * @version $Id$
 */
public class GenerateDailyFoodReportAction
        implements Action {

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

    private ConfigurationService configurationService;

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    private I18nFormat format;

    public void setFormat(I18nFormat format) {
        this.format = format;
    }

    private org.hisp.dhis.i18n.I18n i18n;

    public void setI18n(I18n i18n) {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private String outputType;

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    private String reportDate;

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    private String fontName = "";

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    private String reportOutputMode;

    public void setReportOutputMode(String reportOutputMode) {
        this.reportOutputMode = reportOutputMode;
    }

    private Boolean writeupStatus;

    public void setWriteupStatus(Boolean writeupStatus) {
        this.writeupStatus = writeupStatus;
    }

    Map<String, String> outputResultMap = new HashMap<String, String>();

    public Map<String, String> getOutputResultMap() {
        return outputResultMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    String path = System.getenv("DHIS2_HOME") + File.separator + "fpmureports" + File.separator;

    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("^-?\\d+\\.?\\d*$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public String execute() throws Exception {

        HttpServletResponse response = ServletActionContext.getResponse();
        HttpServletRequest request = ServletActionContext.getRequest();
        String fileName = null;

        HashMap<String, Object> hash = new HashMap<String, Object>();

        SimpleDateFormat standardDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<DFSReport> reportDesignList = new ArrayList<DFSReport>(fpmuReportManager.getDFSReportDesign("Daily_Food_Situation.xml"));
        Map<String, String> resultMap = new HashMap<String, String>();

        Constant constant = constantService.getConstantByName(DFSReport.CONSTANT_MAX_DAYS_FOR_LAST_AVAILABLE_DATE);
        Double maxDaysForLastAvailableDate = -30.0;
        if (constant == null) {

        } else {
            maxDaysForLastAvailableDate = constant.getValue();
        }

        for (DFSReport dsfReport : reportDesignList) {
            String period = dsfReport.getPeriod();
            String startDate = reportDate;
            String dataElementExp = dsfReport.getDataelement();
            String name = dsfReport.getName();
            String cellType = dsfReport.getCelltype();
            String aggType = dsfReport.getAggtype();
            String dataType = dsfReport.getDatatype();
            String orgUnitId;
            String result = "";

            if (dsfReport.getOrgunit().equalsIgnoreCase("na")) {
                orgUnitId = "0";
            } else {
                orgUnitId = dsfReport.getOrgunit();
            }

            if (cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_DATE_MULTIPLEDES)) {
                if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    Date rDate = format.parseDate(reportDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(rDate);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                        cal.add(Calendar.YEAR, -1);
                    }
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);
                    String endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getLatestAvailableDateFromMultipleDes(dataElementExp, orgUnitId, startDate, endDate);
                } 
                else if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_PREVIOUS_FISCAL_YEAR)) 
                {
                    Date rDate = format.parseDate(reportDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(rDate);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                        cal.add(Calendar.YEAR, -1);
                    }
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);
                    String endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getLatestAvailableDateFromMultipleDes(dataElementExp, orgUnitId, startDate, endDate);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date DSUpdateDate = null;
                    if( result != null && !result.trim().equals("") && !result.trim().equalsIgnoreCase("YYYY-MM-DD") )
                    {
                    	DSUpdateDate = dateFormat.parse( result );
                    }
                    else
                    {
                    	DSUpdateDate = rDate;
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DSUpdateDate);

                    calendar.roll(Calendar.YEAR, -1);

                    Date sDate = calendar.getTime();
                    cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    cal.add(Calendar.DATE, maxDaysForLastAvailableDate.intValue());
                    endDate = standardDateFormat.format(cal.getTime());
                    startDate = format.formatDate(sDate);

                    result = fpmuReportManager.getLatestAvailableDateFromMultipleDes(dataElementExp, orgUnitId, startDate, endDate);

                } else if (period.equalsIgnoreCase(DFSReport.LAST_WEEK_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {


                    Date rDate = format.parseDate(reportDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(rDate);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                        cal.add(Calendar.YEAR, -1);
                    }
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);
                    String endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getLatestAvailableDateFromMultipleDes(dataElementExp, orgUnitId, startDate, endDate);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    Date DSUpdateDate = null;
                    if( result != null && !result.trim().equals("") && !result.trim().equalsIgnoreCase("YYYY-MM-DD") )
                    {
                    	DSUpdateDate = dateFormat.parse( result );
                    }
                    else
                    {
                    	DSUpdateDate = rDate;
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(DSUpdateDate);

                    calendar.add(Calendar.DAY_OF_YEAR, -7);

                    Date sDate = calendar.getTime();
                    endDate = standardDateFormat.format(cal.getTime());
                    startDate = format.formatDate(sDate);
                    result = fpmuReportManager.getLatestAvailableDateFromMultipleDes(dataElementExp, orgUnitId, startDate, endDate);

                }
            } else if (cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_DATE)) {
                if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    Integer dataElementId = Integer.parseInt(dataElementExp.split("\\.")[0]);
                    Integer optionComboId = Integer.parseInt(dataElementExp.split("\\.")[1]);

                    Date rDate = format.parseDate(reportDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(rDate);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                        cal.add(Calendar.YEAR, -1);
                    }
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);
                    String endDate = standardDateFormat.format(cal.getTime());
                    result = fpmuReportManager.getLatestAvailableDate(dataElementId, optionComboId, orgUnitId, startDate, endDate);

                } else if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_PREVIOUS_FISCAL_YEAR)) {
                    Integer dataElementId = Integer.parseInt(dataElementExp.split("\\.")[0]);
                    Integer optionComboId = Integer.parseInt(dataElementExp.split("\\.")[1]);

                    int year = Integer.parseInt(reportDate.split("-")[0]);
                    year--;
                    startDate = year + "-" + reportDate.split("-")[1] + "-" + reportDate.split("-")[2];

                    Date sDate = format.parseDate(startDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    cal.add(Calendar.DATE, maxDaysForLastAvailableDate.intValue());
                    String endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getLatestAvailableDate(dataElementId, optionComboId, orgUnitId, startDate, endDate);
                } else if (period.equalsIgnoreCase(DFSReport.LAST_WEEK_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    Integer dataElementId = Integer.parseInt(dataElementExp.split("\\.")[0]);
                    Integer optionComboId = Integer.parseInt(dataElementExp.split("\\.")[1]);

                    Date sDate = format.parseDate(startDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    cal.add(Calendar.DATE, -7);
                    startDate = standardDateFormat.format(cal.getTime());
                    cal.add(Calendar.DATE, maxDaysForLastAvailableDate.intValue());
                    String endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getLatestAvailableDate(dataElementId, optionComboId, orgUnitId, startDate, endDate);
                }
            } else if (cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_TARGETDATEDATA) || cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_TARGETDATA)) {
                if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    String endDate = null;
                    result = fpmuReportManager.getResultValue(dataElementExp, startDate, endDate, orgUnitId, aggType, cellType);
                }
            } else if (cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_FORMULATEXT) || cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_FORMULANUMBER)) {
                result = getResultValueForFormula(dataElementExp, cellType, resultMap);
            } else if (cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_DATEANDDATA)) {
                result = getResultValueForFormula(dataElementExp, cellType, resultMap);
            } 
            else if(cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_FOR_STARTDATE_AND_ENDDATE) ) 
            {
                if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) 
                {
                    String dataElements[] = dataElementExp.split("--");
                    startDate = resultMap.get(dataElements[0]);
                    if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) 
                    {
                        result = " ";
                    } 
                    else 
                    {
                        String endDate = resultMap.get(dataElements[1]);

                        if (endDate == null || endDate.trim().equals("") || endDate.equalsIgnoreCase("YYYY-MM-DD")) 
                        {
                            result = " ";
                        } 
                        else 
                        {
                            SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date sDate = ddMMYYYYFormat.parse(startDate);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(sDate);
                            startDate = standardDateFormat.format(cal.getTime());

                            sDate = ddMMYYYYFormat.parse(endDate);
                            cal.setTime(sDate);
                            endDate = standardDateFormat.format(cal.getTime());

                            result = fpmuReportManager.getResultValue(dataElements[2], startDate, endDate, orgUnitId, aggType, cellType);
                        }
                    }
                } 
                else if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_PREVIOUS_FISCAL_YEAR)) {

                    String dataElements[] = dataElementExp.split("--");
                    startDate = resultMap.get(dataElements[0]);
                    if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
                        result = " ";
                    } else {
                        String endDate = resultMap.get(dataElements[1]);

                        if (endDate == null || endDate.trim().equals("") || endDate.equalsIgnoreCase("YYYY-MM-DD")) {
                            result = " ";
                        } else {
                            SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date sDate = ddMMYYYYFormat.parse(startDate);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(sDate);
                            startDate = standardDateFormat.format(cal.getTime());

                            sDate = ddMMYYYYFormat.parse(endDate);
                            cal.setTime(sDate);
                            endDate = standardDateFormat.format(cal.getTime());

                            result = fpmuReportManager.getResultValue(dataElements[2], startDate, endDate, orgUnitId, aggType, cellType);
                        }
                    }
                } else if (period.equalsIgnoreCase(DFSReport.SEASON_BASED_ON_LAST_AVAILABLE_DATE)) {

                    String dataElements[] = dataElementExp.split("--");
                    startDate = resultMap.get(dataElements[0]).split("--")[0];
                    if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
                        result = " ";
                    } else {
                        String endDate = resultMap.get(dataElements[0]).split("--")[1];

                        if (endDate == null || endDate.trim().equals("") || endDate.equalsIgnoreCase("YYYY-MM-DD")) {
                            result = " ";
                        } else {
                            SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date sDate = ddMMYYYYFormat.parse(startDate);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(sDate);
                            startDate = standardDateFormat.format(cal.getTime());

                            sDate = ddMMYYYYFormat.parse(endDate);
                            cal.setTime(sDate);
                            endDate = standardDateFormat.format(cal.getTime());
                            result = fpmuReportManager.getResultValue(dataElements[1], startDate, endDate, orgUnitId, aggType, cellType);
                        }
                    }
                } else if (period.equalsIgnoreCase(DFSReport.LAST_FISCAL_YEAR_SEASON_BASED_ON_LAST_AVAILABLE_DATE)) {
                    String dataElements[] = dataElementExp.split("--");
                    startDate = resultMap.get(dataElements[0]);
                    if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
                        result = " ";
                    } else {
                        String endDate = resultMap.get(dataElements[1]);

                        if (endDate == null || endDate.trim().equals("") || endDate.equalsIgnoreCase("YYYY-MM-DD")) {
                            result = " ";
                        } else {
                            SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date sDate = ddMMYYYYFormat.parse(startDate);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(sDate);
                            startDate = standardDateFormat.format(cal.getTime());

                            sDate = ddMMYYYYFormat.parse(endDate);
                            cal.setTime(sDate);
                            endDate = standardDateFormat.format(cal.getTime());

                            String currentFiscalYearStartDate = "";
                            String currentFiscalYearEndDate = "";

                            if (Integer.parseInt(reportDate.split("-")[1]) > 7) {
                                currentFiscalYearStartDate = Integer.parseInt(reportDate.split("-")[0]) + "-07-01";
                                currentFiscalYearEndDate = (Integer.parseInt(reportDate.split("-")[0]) + 1) + "-06-30";
                            } else {
                                currentFiscalYearStartDate = (Integer.parseInt(reportDate.split("-")[0]) - 1) + "-07-01";
                                currentFiscalYearEndDate = Integer.parseInt(reportDate.split("-")[0]) + "-06-30";
                            }

                            if (startDate.compareTo(currentFiscalYearStartDate) >= 0) {
                                startDate = "YYYY-MM-DD";
                                endDate = "YYYY-MM-DD";
                            } else {
                                startDate = resultMap.get(dataElements[0]);
                                endDate = "30/06/" + Integer.parseInt(currentFiscalYearStartDate.split("-")[0]);
                            }

                            if (dataType.equalsIgnoreCase(DFSReport.DATA_TYPE_NUMBER)) {
                                result = fpmuReportManager.getResultValue(dataElements[2], startDate, endDate, orgUnitId, aggType, cellType);
                            } else {
                                result = startDate + "--" + endDate;
                            }
                        }
                    }
                } else if (period.equalsIgnoreCase(DFSReport.CURRENT_FISCAL_YEAR_SEASON_BASED_ON_LAST_AVAILABLE_DATE)) {
                    String dataElements[] = dataElementExp.split("--");
                    startDate = resultMap.get(dataElements[0]);
                    if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
                        result = " ";
                    } else {
                        String endDate = resultMap.get(dataElements[1]);

                        if (endDate == null || endDate.trim().equals("") || endDate.equalsIgnoreCase("YYYY-MM-DD")) {
                            result = " ";
                        } else {
                            SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date sDate = ddMMYYYYFormat.parse(startDate);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(sDate);
                            startDate = standardDateFormat.format(cal.getTime());

                            sDate = ddMMYYYYFormat.parse(endDate);
                            cal.setTime(sDate);
                            endDate = standardDateFormat.format(cal.getTime());

                            String currentFiscalYearStartDate = "";
                            String currentFiscalYearEndDate = "";

                            if (Integer.parseInt(reportDate.split("-")[1]) > 7) {
                                currentFiscalYearStartDate = Integer.parseInt(reportDate.split("-")[0]) + "-07-01";
                                currentFiscalYearEndDate = (Integer.parseInt(reportDate.split("-")[0]) + 1) + "-06-30";
                            } else {
                                currentFiscalYearStartDate = (Integer.parseInt(reportDate.split("-")[0]) - 1) + "-07-01";
                                currentFiscalYearEndDate = Integer.parseInt(reportDate.split("-")[0]) + "-06-30";
                            }

                            if (endDate.compareTo(currentFiscalYearEndDate) <= 0) {
                                if (startDate.compareTo(currentFiscalYearStartDate) >= 0) {
                                    startDate = resultMap.get(dataElements[0]);
                                    endDate = resultMap.get(dataElements[1]);
                                } else if (endDate.compareTo(currentFiscalYearStartDate) <= 0) {
                                    startDate = "YYYY-MM-DD";
                                    endDate = "YYYY-MM-DD";
                                } else {
                                    startDate = "01/07/" + Integer.parseInt(currentFiscalYearStartDate.split("-")[0]);
                                    endDate = resultMap.get(dataElements[1]);
                                }
                            } else {
                                startDate = "01/07/" + Integer.parseInt(currentFiscalYearStartDate.split("-")[0]);
                                endDate = resultMap.get(dataElements[1]);
                            }

                            if (dataType.equalsIgnoreCase(DFSReport.DATA_TYPE_NUMBER)) {
                                result = fpmuReportManager.getResultValue(dataElements[2], startDate, endDate, orgUnitId, aggType, cellType);
                            } else {
                                result = startDate + "--" + endDate;
                            }
                        }
                    }
                }
            } else if (cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_FORMULA_USE_STARTDATE)) {
                Date sDate = format.parseDate(startDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(sDate);
                if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                    cal.add(Calendar.YEAR, -1);
                }
                cal.set(Calendar.MONTH, Calendar.JUNE);
                cal.set(Calendar.DATE, 30);
                String endDate = standardDateFormat.format(cal.getTime());

                String dataElements[] = dataElementExp.split("--");
                startDate = resultMap.get(dataElements[0]);

                if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
                    result = " ";
                } else {
                    SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat("dd/MM/yyyy");
                    sDate = ddMMYYYYFormat.parse(startDate);
                    cal.setTime(sDate);
                    startDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getResultValue(dataElements[1], startDate, endDate, orgUnitId, aggType, cellType);
                }
            } else if (cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_FORMULA_USE_ENDDATE)) {
                String dataElements[] = dataElementExp.split("--");

                Date sDate = format.parseDate(startDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(sDate);
                if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                    cal.add(Calendar.YEAR, -1);
                }
                cal.set(Calendar.MONTH, Calendar.JULY);
                cal.set(Calendar.DATE, 1);
                startDate = standardDateFormat.format(cal.getTime());

                String endDate = resultMap.get(dataElements[0]);
                if (endDate == null || endDate.trim().equals("") || endDate.equalsIgnoreCase("YYYY-MM-DD")) {
                    result = " ";
                } else {
                    SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat("dd/MM/yyyy");
                    sDate = ddMMYYYYFormat.parse(endDate);
                    cal.setTime(sDate);
                    endDate = standardDateFormat.format(cal.getTime());

                    Integer endDateMonth = Integer.parseInt(endDate.split("-")[2]);

                    if (endDateMonth < 7) {
                        result = " ";
                    } else {

                        result = fpmuReportManager.getResultValue(dataElements[1], startDate, endDate, orgUnitId, aggType, cellType);
                    }
                }

            } else if (cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_LASTFISCALYEARDATE)) {
                Date sDate = format.parseDate(startDate);
                Calendar cal = Calendar.getInstance();
                cal.setTime(sDate);
                int startYear = cal.get(Calendar.YEAR);
                int endYear = cal.get(Calendar.YEAR);

                if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                    startYear -= 2;
                    endYear--;
                } else {
                    startYear--;
                }

                result = startYear + "-" + endYear;
            } else {
                if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    Date rDate = format.parseDate(reportDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(rDate);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                        cal.add(Calendar.YEAR, -1);
                    }
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);
                    String endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getResultValue(dataElementExp, startDate, endDate, orgUnitId, aggType, cellType);

                } else if (period.equalsIgnoreCase(DFSReport.LAST_AVAILABLE_DATE_FOR_PREVIOUS_FISCAL_YEAR)) {
                    int year = Integer.parseInt(reportDate.split("-")[0]);
                    year--;
                    startDate = year + "-" + reportDate.split("-")[1] + "-" + reportDate.split("-")[2];

                    Date sDate = format.parseDate(startDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    cal.add(Calendar.DATE, maxDaysForLastAvailableDate.intValue());
                    String endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getResultValue(dataElementExp, startDate, endDate, orgUnitId, aggType, cellType);
                } else if (period.equalsIgnoreCase(DFSReport.LAST_WEEK_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    Date sDate = format.parseDate(startDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    cal.add(Calendar.DATE, -7);
                    startDate = standardDateFormat.format(cal.getTime());
                    cal.add(Calendar.DATE, maxDaysForLastAvailableDate.intValue());
                    String endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getResultValue(dataElementExp, startDate, endDate, orgUnitId, aggType, cellType);
                } else if (period.equalsIgnoreCase(DFSReport.CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    String endDate = startDate;

                    Date sDate = format.parseDate(startDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                        cal.add(Calendar.YEAR, -1);
                    }
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);

                    startDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getResultValue(dataElementExp, startDate, endDate, orgUnitId, aggType, cellType);
                } else if (period.equalsIgnoreCase(DFSReport.CUMULATIVE_FOR_LAST_FISCAL_YEAR)) {
                    Date sDate = format.parseDate(startDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    int startYear = cal.get(Calendar.YEAR);
                    int endYear = cal.get(Calendar.YEAR);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                        startYear -= 2;
                        endYear--;
                    } else {
                        startYear--;
                    }

                    cal.set(Calendar.YEAR, startYear);
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);
                    startDate = standardDateFormat.format(cal.getTime());

                    String endDate = startDate;
                    cal.set(Calendar.YEAR, endYear);
                    cal.set(Calendar.MONTH, Calendar.JUNE);
                    cal.set(Calendar.DATE, 30);
                    endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getResultValue(dataElementExp, startDate, endDate, orgUnitId, aggType, cellType);
                } else if (period.equalsIgnoreCase(DFSReport.CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_LAST_FISCAL_YEAR)) {
                    String endDate = startDate;

                    Date sDate = format.parseDate(startDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    int startYear = cal.get(Calendar.YEAR);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                        startYear -= 2;
                    } else {
                        startYear--;
                    }
                    cal.set(Calendar.YEAR, startYear);
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);
                    startDate = standardDateFormat.format(cal.getTime());

                    sDate = format.parseDate(endDate);
                    cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    cal.add(Calendar.YEAR, -1);
                    endDate = standardDateFormat.format(cal.getTime());

                    result = fpmuReportManager.getResultValue(dataElementExp, startDate, endDate, orgUnitId, aggType, cellType);
                } 
                else if (period.equalsIgnoreCase(DFSReport.MONTHWISE_CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR)) {
                    String dataElements1[] = dataElementExp.split("--");
                    String dataElements2[] = dataElements1[0].split(":");
                    String dataElements3[] = dataElements1[1].split(":");
                    int monthDays[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yy");

                    String endDate = startDate;

                    Date sDate = format.parseDate(startDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);

                    int month = cal.get(Calendar.MONTH);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) 
                    {
                        cal.add(Calendar.YEAR, -1);
                    }
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);
                    String fiscalYearStart = standardDateFormat.format(cal.getTime());
                    int dataValue = 1;
                    int date = 1;
                    System.out.println( cal.get(Calendar.MONTH) + " : " + month );
                    while (cal.get(Calendar.MONTH) != month) 
                    {
                        startDate = standardDateFormat.format(cal.getTime());

                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(cal.getTime());
                        cal1.set(Calendar.DATE, monthDays[cal1.get(Calendar.MONTH)]);

                        endDate = standardDateFormat.format(cal1.getTime());

                        int count1 = 1;
                        for (String deExp : dataElements2) 
                        {
                            result = fpmuReportManager.getResultValue(deExp, startDate, endDate, orgUnitId, DFSReport.AGG_TYPE_PERIOD_AGG, cellType);

                            if (dataType.equalsIgnoreCase(DFSReport.DATA_TYPE_NUMBER) && (result == null || result.trim().equals("") || result.isEmpty())) {
                                resultMap.put("datavalue" + dataValue, "(0.0)");
                                //System.out.println("---NULL BLOCK---");
                            } 
                            else 
                            {
                                try 
                                {
                                    if (count1 >= 5) 
                                    {
                                        resultMap.put("datavalue" + dataValue, result);
                                        //System.out.println("---BLOCK A 1---");
                                    } 
                                    else 
                                    {
                                        Double tempD = Double.parseDouble(result) * 100.0;
                                        resultMap.put("datavalue" + dataValue, tempD + "");
                                        //System.out.println("---BLOCK A 2---");
                                    }
                                } 
                                catch (Exception e) 
                                {
                                    resultMap.put("datavalue" + dataValue, result);
                                }
                            }

                            count1++;

                            System.out.println( "******** datavalue"+dataValue + " -- " + result );
                            dataValue++;
                        }
                        simpleDateFormat.format(cal.getTime());
                        resultMap.put("date" + date, simpleDateFormat.format(cal.getTime()));
                        cal.add(Calendar.MONTH, 1);
                        date++;
                    }

                    //-----------This is for calculating data from selected date's month start till selected date

                    startDate = standardDateFormat.format(cal.getTime());
                    endDate = reportDate;
                    int count1 = 1;
                    for (String deExp : dataElements2) 
                    {
                        result = fpmuReportManager.getResultValue(deExp, startDate, endDate, orgUnitId, DFSReport.AGG_TYPE_PERIOD_AGG, cellType);

                        if (dataType.equalsIgnoreCase(DFSReport.DATA_TYPE_NUMBER) && (result == null || result.trim().equals("") || result.isEmpty())) 
                        {
                            resultMap.put("datavalue" + dataValue, "(0.0)");
                            System.out.println("---NULL BLOCK---");
                        } 
                        else 
                        {
                            try 
                            {
                                if (count1 >= 5) 
                                {
                                    resultMap.put("datavalue" + dataValue, result);
                                    System.out.println("---BLOCK B 1---");
                                } 
                                else 
                                {
                                    Double tempD = Double.parseDouble(result) * 100.0;
                                    resultMap.put("datavalue" + dataValue, tempD + "");
                                    System.out.println("---BLOCK B 2---");
                                }
                            } 
                            catch (Exception e) 
                            {
                                resultMap.put("datavalue" + dataValue, result);
                            }
                        }

                        count1++;

                        dataValue++;
                    }
                    simpleDateFormat.format(cal.getTime());
                    resultMap.put("date" + date, simpleDateFormat.format(cal.getTime()));
                    date++;

                    //-----------------This is for calculating data from selected date's fiscal year start till selected date

                    startDate = fiscalYearStart;
                    endDate = reportDate;
                    int total = 1;
                    count1 = 1;
                    for (String deExp : dataElements2) {
                        result = fpmuReportManager.getResultValue(deExp, startDate, endDate, orgUnitId, DFSReport.AGG_TYPE_PERIOD_AGG, cellType);

                        if (dataType.equalsIgnoreCase(DFSReport.DATA_TYPE_NUMBER) && (result == null || result.trim().equals("") || result.isEmpty())) {
                            resultMap.put("total" + total, "0.00");
                        } else {
                            try {
                                if (count1 >= 5) {
                                    resultMap.put("total" + total, result);
                                } else {
                                    Double tempD = Double.parseDouble(result) * 100.0;
                                    resultMap.put("total" + total, tempD + "");
                                }
                            } catch (Exception e) {
                                resultMap.put("total" + total, result);
                            }

                        }
                        count1++;
                        total++;
                    }

                    //----------------This is for getting start date for last available data for current fiscal year for selected date's month

                    startDate = reportDate;
                    cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    cal.add(Calendar.DATE, maxDaysForLastAvailableDate.intValue());
                    endDate = standardDateFormat.format(cal.getTime());
                    result = fpmuReportManager.getResultValue(dataElements3[0], startDate, endDate, orgUnitId, DFSReport.AGG_TYPE_ONE_PERIOD, DFSReport.CELL_TYPE_DATEDATA);

                    simpleDateFormat.format(cal.getTime());

                    Date tempDate = format.parseDate(endDate);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    endDate = dateFormat.format(tempDate);

                    resultMap.put("startDate", endDate);


                    //-----------------This is for getting end date for last available data for current fiscal year for selected date's month

                    startDate = reportDate;
                    cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    cal.add(Calendar.DATE, maxDaysForLastAvailableDate.intValue());
                    endDate = standardDateFormat.format(cal.getTime());
                    result = fpmuReportManager.getResultValue(dataElements3[1], startDate, endDate, orgUnitId, DFSReport.AGG_TYPE_ONE_PERIOD, DFSReport.CELL_TYPE_DATEDATA);

                    Date enDate = format.parseDate(startDate);
                    startDate = dateFormat.format(enDate);
                    resultMap.put("endDate", startDate);
                } else if (period.equalsIgnoreCase(DFSReport.DAYWISE_FOR_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR) && cellType.equalsIgnoreCase(DFSReport.CELL_TYPE_LINELISITNGDATA)) {
                    //--------------This is for getting latest execution/reporting date, so that we can get all program-stage instances for that date

                    startDate = reportDate;
                    Date sDate = format.parseDate(startDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(sDate);
                    cal.add(Calendar.DATE, maxDaysForLastAvailableDate.intValue());
                    String endDate = standardDateFormat.format(cal.getTime());
                    String reportingDate = fpmuReportManager.getLatestAvailableDateForLineListingData(orgUnitId, startDate, endDate);

                    if (reportingDate.equalsIgnoreCase("YYYY-MM-DD")) {
                        resultMap.put("reportDate", reportingDate);
                    } else {
                        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");

                        Date reportDate = dateTimeFormat.parse(reportingDate);
                        dateTimeFormat.applyPattern("dd/MM/yyyy");
                        String table5dateFormat = dateTimeFormat.format(reportDate);

                        resultMap.put("reportDate", table5dateFormat);
                    }

                    String dataElements1[] = dataElementExp.split("--");
                    String dataElements2[] = dataElements1[1].split(":");

                    String programStageId = dataElements1[0];

                    Map<Integer, Map<Integer, String>> lineListDataValueMap = fpmuReportManager.getLineListingDataForProgramStage(programStageId, orgUnitId, reportingDate);

                    Set<Integer> lineListDataValueKeys = new HashSet<Integer>(lineListDataValueMap.keySet());
                    double total1 = 0;
                    double total2 = 0;
                    double total3 = 0;
                    int dataValueCount = 1;
                    for (Integer psiId : lineListDataValueKeys) {
                        Map<Integer, String> deDataValueMap = lineListDataValueMap.get(psiId);
                        for (String deId : dataElements2) {
                            String value = deDataValueMap.get(Integer.parseInt(deId));
                            if (value != null) {
                                value = value.replaceAll(",", "");

                                String[] dateString = value.split("-");
                                if (dateString.length == 3) {
                                    value = dateString[2].concat("/").concat(dateString[1]).concat("/").concat(dateString[0]);
                                } else if (isNumeric(value)) {
                                    value = String.valueOf((Double.parseDouble(value) / 1000));
                                }
                                resultMap.put("table5d" + dataValueCount, value);
                                dataValueCount++;
                            }
                        }

                        try {
                            total1 = total1 + Double.parseDouble(resultMap.get("table5d" + (dataValueCount - 3)));
                        } catch (Exception e) {

                        }

                        try {
                            total2 = total2 + Double.parseDouble(resultMap.get("table5d" + (dataValueCount - 2)));
                        } catch (Exception e) {

                        }

                        try {
                            total3 = total3 + Double.parseDouble(resultMap.get("table5d" + (dataValueCount - 1)));
                        } catch (Exception e) {

                        }
                    }
                    resultMap.put("table5total1", (total1) + "");
                    resultMap.put("table5total2", (total2) + "");
                    resultMap.put("table5total3", (total3) + "");
                }
            }

            if (dataType.equalsIgnoreCase(DFSReport.DATA_TYPE_NUMBER) && (result == null || result.trim().equals("") || result.isEmpty())) 
            {
                resultMap.put(name, "0.0");
            } 
            else if (isNumber(result)) 
            {
                double numResult = Double.parseDouble(result);
                //String resultString = String.format("%.2f", numResult);

                //resultMap.put(name, resultString);
                resultMap.put( name, result );
            } 
            else 
            {
                resultMap.put(name, result);
            }

        }

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String reportGeneratedDate = dateTimeFormat.format(new Date());
        hash.put("reportGeneratedDate", reportGeneratedDate);
        resultMap.put("reportGeneratedDate", reportGeneratedDate);

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String generatedYear = yearFormat.format(new Date());
        hash.put("generatedYear", generatedYear);

        Date tempDate = format.parseDate(reportDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(tempDate);

        String currentFiscalYear;
        String currentFiscalStartYear;
        String currentTable9SeasonEndYear;
        String prevFiscalStartYear;
        String prevYearTable9SeasonEndYear;

        String prevFiscalYear;
        if (Integer.parseInt(reportDate.split("-")[1]) >= 7) {
            currentFiscalYear = Integer.parseInt(reportDate.split("-")[0]) + "-" + (Integer.parseInt(reportDate.split("-")[0]) + 1);
            prevFiscalYear = (Integer.parseInt(reportDate.split("-")[0]) - 1) + "-" + Integer.parseInt(reportDate.split("-")[0]);
        } else {
            currentFiscalYear = (Integer.parseInt(reportDate.split("-")[0]) - 1) + "-" + (Integer.parseInt(reportDate.split("-")[0]));
            prevFiscalYear = (Integer.parseInt(reportDate.split("-")[0]) - 2) + "-" + (Integer.parseInt(reportDate.split("-")[0]) - 1);
        }

        resultMap.put("curfiscalyear", currentFiscalYear);
        resultMap.put("prevfiscalyear", prevFiscalYear);


        resultMap.put("localHeading1", i18n.getString("local_heading1"));


        String tempReportDate = reportDate;
        tempDate = format.parseDate(reportDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        tempReportDate = dateFormat.format(tempDate);


        //------------Calculate table9RowOneStartDate------------------//

        String table9RowOneEndDate = resultMap.get("table9date1.2");

        if( table9RowOneEndDate != null && !table9RowOneEndDate.trim().equals("") ) 
        {
        	SimpleDateFormat dateFormatFor9 = new SimpleDateFormat("dd/MM/yyyy");
        	
        	if( table9RowOneEndDate.trim().equalsIgnoreCase("YYYY-MM-DD") )
        	{
        		//SimpleDateFormat sdf = new SimpleDateFormat( "YYYY-MM-DD" );
        		//Date tempD = sdf.parse( reportDate );
        		table9RowOneEndDate = reportDate.split("-")[2]+"/"+reportDate.split("-")[1]+"/"+reportDate.split("-")[0];
        		System.out.println( "inside table9 block: " + reportDate  + " --- " + table9RowOneEndDate);
        	}
        	
            Date table9EndDate = dateFormatFor9.parse(table9RowOneEndDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(table9EndDate);
            Date table9StartDate;
            if (calendar.get(Calendar.DAY_OF_WEEK) < 5) 
            {
                calendar.add(Calendar.WEEK_OF_MONTH, -1);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            } 
            else if (calendar.get(Calendar.DAY_OF_MONTH) < 7) 
            {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
            } 
            else 
            {
                calendar.add(Calendar.DATE, -6);
            }

            table9StartDate = calendar.getTime();


            System.out.println("---------------------------------------+++++" + table9RowOneEndDate);

            calendar.setTime( table9EndDate );

            if (calendar.get(Calendar.MONTH) >= 6) 
            {
                currentFiscalStartYear = String.valueOf(calendar.get(Calendar.YEAR));
                currentTable9SeasonEndYear = String.valueOf(calendar.get(Calendar.YEAR));
                prevFiscalStartYear = String.valueOf(calendar.get(Calendar.YEAR) - 1);
                prevYearTable9SeasonEndYear = String.valueOf(calendar.get(Calendar.YEAR) - 1);
            } 
            else 
            {
                currentFiscalStartYear = String.valueOf(calendar.get(Calendar.YEAR) - 1);
                currentTable9SeasonEndYear = String.valueOf(calendar.get(Calendar.YEAR));
                prevFiscalStartYear = String.valueOf(calendar.get(Calendar.YEAR) - 2);
                prevYearTable9SeasonEndYear = String.valueOf(calendar.get(Calendar.YEAR) - 1);
            }

            System.out.println("* CHECK(START DATE): " + table9RowOneEndDate);

            resultMap.put("table9date1.1", dateFormatFor9.format(table9StartDate));

            String table9RowThreeEndDateSplit[] = table9RowOneEndDate.split("/");

            String table9RowThreeEndDate = new String();

            if (table9RowThreeEndDateSplit.length == 3) {
                table9RowThreeEndDate = table9RowThreeEndDateSplit[0] + "/" + table9RowThreeEndDateSplit[1] + "/" + prevYearTable9SeasonEndYear;
            }

            currentFiscalStartYear = "1/07/"+currentFiscalStartYear;
            prevFiscalStartYear = "1/07/"+prevFiscalStartYear;

            resultMap.put("curfiscalstartyear", currentFiscalStartYear);
            resultMap.put("prevfiscalstartyear", prevFiscalStartYear);
            resultMap.put("curfiscalendyear", currentTable9SeasonEndYear);
            resultMap.put("prevfiscalendyear", prevYearTable9SeasonEndYear);

            resultMap.put("table9date3.1", table9RowThreeEndDate);

        } 
        else 
        {
            resultMap.put("curfiscalstartyear", "");
            resultMap.put("prevfiscalstartyear", "");
            resultMap.put("curfiscalendyear", "");
            resultMap.put("prevfiscalendyear", "");

            resultMap.put("table9date3.1", "");
        }

        //---------------------------Table 8 Start and End Dates---------------------//

        String table8EndDate = resultMap.get("table8date1");

        if (table8EndDate != null && !table8EndDate.trim().equals("") && !table8EndDate.trim().equalsIgnoreCase("YYYY-MM-DD") ) 
        {
            SimpleDateFormat table8DateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date table8LastDate = table8DateFormat.parse(table8EndDate);


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(table8LastDate);

            if (calendar.get(Calendar.MONTH) >= 6) 
            {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, 6);
            } 
            else 
            {
                calendar.roll(Calendar.YEAR, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, 6);
            }

            Date table8StartDate = calendar.getTime();

            String table8FiscalStart = table8DateFormat.format(table8StartDate);

            resultMap.put("table8StartDate", table8FiscalStart);

        }


        Map<String, String> finalResultMap = new HashMap<String, String>();

        System.out.println("\n\n");
        System.out.println("=================================================================================");
        for (Map.Entry entry : resultMap.entrySet()) {

            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            if( isNumeric(value) ) 
            {
            	String[] valueParts = value.split("\\.");
            	
            	if( valueParts != null && valueParts.length >= 1 )
            	{
            		value = valueParts[0];
            		
            		if( valueParts.length >= 2 )
            		{
            			if( valueParts[1].length() >= 2 )
            				value += "." + valueParts[1].substring(0, 2);
            			else if( valueParts[1].length() == 1 )
            				value += "." + valueParts[1]+"0";
            		}
            	}
            	
                //DecimalFormat doubleFormat = new DecimalFormat("0.0");
                
                
                //value = String.valueOf( doubleFormat.format( Double.parseDouble( value ) ) );
            }

            finalResultMap.put(key, value);
        }
        System.out.println("=================================================================================");
        System.out.println("\n\n");

        System.out.println("\n\n");
        System.out.println("=================================================================================");
        for (Map.Entry entry : finalResultMap.entrySet()) {
            System.out.print("key,val: ");
            System.out.println(entry.getKey() + ", " + entry.getValue() + " :: " + resultMap.get( entry.getKey() ) );
        }
        System.out.println("=================================================================================");
        System.out.println("\n\n");

        // --------------------------------------------------------------------
        // Populating writeup comments in resultMap
        // --------------------------------------------------------------------

        for (int i = 1; i <= DFSReport.DAILY_REPORT_TEXT_BOX_COUNT; i++) {
            Configuration_IN config_in = configurationService.getConfigurationByKey(DFSReport.PREFIX_DAILY + i);
            if (config_in == null || writeupStatus == null) {
                finalResultMap.put(DFSReport.PREFIX_DAILY + i, " ");
            } else {
                finalResultMap.put(DFSReport.PREFIX_DAILY + i, config_in.getValue());
            }
        }

        hash.put("date", tempReportDate);

        hash.put("resultMap", finalResultMap);

        outputResultMap = finalResultMap;

        if (reportOutputMode.equalsIgnoreCase("COMMENTOUTPUT")) {
            return "COMMENTOUTPUT";
        }

        if (fontName.equalsIgnoreCase("Bangla")) {
            fileName = "dailyFoodSituationReport_bangla.jrxml";
        } else if (fontName.isEmpty() || fontName.equals("")) {
            fileName = "dailyFoodSituationReport.jrxml";
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(path + fileName);

        jasperPrint = JasperFillManager.fillReport(jasperReport, hash, new JREmptyDataSource());

        ServletOutputStream outputStream = response.getOutputStream();
        JRExporter exporter = null;

        if ("pdf".equalsIgnoreCase(outputType)) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; fileName=\"DailyFoodSituationReport_" + reportDate + ".pdf\"");
            exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
        } else if ("rtf".equalsIgnoreCase(outputType) && fontName.equalsIgnoreCase("Bangla")) {
            response.setContentType("application/rtf");
            response.setHeader("Content-Disposition", "inline; fileName=\"DailyFoodSituationReport_" + reportDate + ".rtf\"");
            exporter = new JRRtfExporter();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
        } else if ("rtf".equalsIgnoreCase(outputType)) {
            response.setContentType("application/rtf");
            response.setHeader("Content-Disposition", "inline; fileName=\"DailyFoodSituationReport_" + reportDate + ".rtf\"");
            exporter = new JRRtfExporter();

            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
        } else if ("html".equalsIgnoreCase(outputType)) {
            exporter = new JRHtmlExporter();
            exporter.setParameter(JRHtmlExporterParameter.OUTPUT_STREAM, false);
            exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, new Boolean(false));
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
        } else if ("xls".equalsIgnoreCase(outputType)) {
            response.setContentType("application/xls");
            response.setHeader("Content-Disposition", "inline; fileName=\"DailyFoodSituationReport_" + reportDate + ".xls\"");
            exporter = new JRXlsExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
        } else if ("csv".equalsIgnoreCase(outputType)) {
            response.setContentType("application/csv");
            response.setHeader("Content-Disposition", "inline; fileName=\"DailyFoodSituationReport_" + reportDate + ".csv\"");
            exporter = new JRCsvExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
            exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
        }

        try {
            exporter.exportReport();
        } catch (JRException e) {
            throw new ServletException(e);
        }

        return SUCCESS;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private String getResultValueForFormula(String formula, String cellType, Map<String, String> resultMap) {
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
                    //d = Math.round(d * Math.pow(10, 2)) / Math.pow(10, 2);
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
            throw new RuntimeException("Exception in getResultValue method: ", e);
        }
    }
}
