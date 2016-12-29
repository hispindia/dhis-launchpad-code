package org.hisp.dhis.reports.fortnightly.action;

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
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.reports.util.DFSReport;
import org.hisp.dhis.reports.util.FPMUReportManager;
import org.hisp.dhis.reports.util.FortnightReport;
import org.hisp.dhis.reports.util.FortnightService;
import org.hisp.dhis.system.util.MathUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Samta Bajpai
 * @version $Id$
 */
public class GenerateFortnightlyReportAction
    implements Action
{
    protected JasperPrint jasperPrint;

    protected JasperReport jr;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private FortnightService fortReportManager;

    public void setFortReportManager( FortnightService fortReportManager )
    {
        this.fortReportManager = fortReportManager;
    }

    private FPMUReportManager fpmuReportManager;

    public void setFpmuReportManager( FPMUReportManager fpmuReportManager )
    {
        this.fpmuReportManager = fpmuReportManager;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private String outputType;

    public void setOutputType( String outputType )
    {
        this.outputType = outputType;
    }

    private String reportDate;

    public void setReportDate( String reportDate )
    {
        this.reportDate = reportDate;
    }

    private int chartOption;

    public void setChartOption( int chartOption )
    {
        this.chartOption = chartOption;
    }

    private String reportOutputMode;

    public void setReportOutputMode( String reportOutputMode )
    {
        this.reportOutputMode = reportOutputMode;
    }

    Map<String, String> outputResultMap;

    public Map<String, String> getOutputResultMap()
    {
        return outputResultMap;
    }

    private Boolean writeupStatus;

    public void setWriteupStatus( Boolean writeupStatus )
    {
        this.writeupStatus = writeupStatus;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    String path = System.getenv( "DHIS2_HOME" ) + File.separator + "fpmureports" + File.separator;

    public String execute()
        throws Exception
    {
        HttpServletResponse response = ServletActionContext.getResponse();
        String fileName = null;

        HashMap<String, Object> hash = new HashMap<String, Object>();

        SimpleDateFormat standardDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        List<FortnightReport> reportDesignList = new ArrayList<FortnightReport>( fortReportManager
            .getFortnightReportDesign( "Fortnightly_Report.xml" ) );
        Map<String, String> resultMap = new HashMap<String, String>();
        outputResultMap = new HashMap<String, String>();

        // --------------------------------------------------------------------
        // Initializing R chart paths in resultMap
        // --------------------------------------------------------------------

        String chartOutputPath = System.getenv( "DHIS2_HOME" ) + File.separator + "fpmureports" + File.separator + "chartoutput" + File.separator;
        resultMap.put( "fortnightlychart1path", chartOutputPath + "fortnightlychart1_" + reportDate + ".png" );
        resultMap.put( "fortnightlychart2path", chartOutputPath + "fortnightlychart2_" + reportDate + ".png" );
        resultMap.put( "fortnightlychart3path", chartOutputPath + "fortnightlychart3_" + reportDate + ".png" );
        resultMap.put( "fortnightlychart4path", chartOutputPath + "fortnightlychart4_" + reportDate + ".png" );
        resultMap.put( "fortnightlychart5path", chartOutputPath + "fortnightlychart5_" + reportDate + ".png" );
        resultMap.put( "fortnightlychart6path", chartOutputPath + "fortnightlychart6_" + reportDate + ".png" );
        resultMap.put( "fortnightlychart7path", chartOutputPath + "fortnightlychart7_" + reportDate + ".png" );
        resultMap.put( "fortnightlychart8path", chartOutputPath + "fortnightlychart8_" + reportDate + ".png" );

        resultMap.put( "fortnightlychart7legendpath", chartOutputPath + "fortnightlychart7legend.png" );
        
        String arrowImagePath = System.getenv( "DHIS2_HOME" ) + File.separator + "fpmureports" + File.separator + "images" + File.separator;
        resultMap.put( "fortnightlyreduppath", arrowImagePath + "fortnightlyredup.png" );
        resultMap.put( "fortnightlyreddownpath", arrowImagePath + "fortnightlyreddown.png" );
        resultMap.put( "fortnightlygreenuppath", arrowImagePath + "fortnightlygreenup.png" );
        resultMap.put( "fortnightlygreendownpath", arrowImagePath + "fortnightlygreendown.png" );
        resultMap.put( "fortnightlyyellowuppath", arrowImagePath + "fortnightlyyellowup.png" );
        resultMap.put( "fortnightlyyellowdownpath", arrowImagePath + "fortnightlyyellowdown.png" );
        resultMap.put( "fortnightlyyellowrightpath", arrowImagePath + "fortnightlyyellowright.png" );
        
        

        if( chartOption == 1 )
        {
            resultMap.put( "seasonal_crop", "AMAN");
        }
        else if( chartOption == 2 )
        {
            resultMap.put( "seasonal_crop", "BORO");
        }
        else if( chartOption == 3 )
        {
            resultMap.put( "seasonal_crop", "WHEAT");
        }
        else
        {
            resultMap.put( "seasonal_crop", "SEASONAL CROP");
        }
        
        // --------------------------------------------------------------------
        // Populating writeup comments in resultMap
        // --------------------------------------------------------------------
        for ( int i = 1; i <= DFSReport.FORTNIGHTLY_REPORT_TEXT_BOX_COUNT; i++ )
        {
            Configuration_IN config_in = configurationService.getConfigurationByKey( DFSReport.PREFIX_FORTNIGHT + i );
            if ( config_in == null || writeupStatus == null )
            {
                resultMap.put( DFSReport.PREFIX_FORTNIGHT + i, " " );
            }
            else
            {
                resultMap.put( DFSReport.PREFIX_FORTNIGHT + i, config_in.getValue() );
            }
        }

        Constant constant = constantService.getConstantByName( FortnightReport.CONSTANT_MAX_DAYS_FOR_LAST_AVAILABLE_DATE );

        Double maxDaysForLastAvailableDate = -30.0;

        if ( constant != null )
        {
            maxDaysForLastAvailableDate = constant.getValue();
        }

        String fortnightReportChartsPath = "";
        String rScriptPath = "Rscript " + System.getenv( "DHIS2_HOME" ) + File.separator + "fpmureports"
            + File.separator + "rscript" + File.separator + "fortnightly_report.R " + reportDate + " "
            + chartOutputPath + " " + chartOption;


        try
        {
            Runtime rt = Runtime.getRuntime();
            Process pr;

            int exitVal = 1;

            pr = rt.exec( rScriptPath );
            exitVal = pr.waitFor();


            if ( exitVal != 0 )
            {
                try
                {
                    InputStreamReader isr = new InputStreamReader( pr.getErrorStream() );
                    BufferedReader br = new BufferedReader( isr );
                    String line = null;
                    while ( (line = br.readLine()) != null )
                        System.out.println( "ERROR:" + ">" + line );
                }
                catch ( IOException ioe )
                {
                    ioe.printStackTrace();
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        String curYearStart = reportDate.split( "-" )[0] + "-01-01";
        String curYearEnd = reportDate.split( "-" )[0] + "-12-31";     
         
      //  String curFiscYearStart = reportDate.split("-")[0]+"-07-01";
  
        List<Calendar> weekList = new ArrayList<Calendar>( getWeeklyDates( curYearStart, curYearEnd, Calendar.SUNDAY ) );
        
        int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        for ( FortnightReport dsfReport : reportDesignList )
        {
            String period = dsfReport.getPeriod();
            String startDate = reportDate;
            String dataElementExp = dsfReport.getDataelement();
            String name = dsfReport.getName();
            String celltype = dsfReport.getCelltype();
            String aggtype = dsfReport.getAggtype();
            String orgUnitId = "0";
            String result = "";
            String lableName = dsfReport.getRowname();
            String dataType = dsfReport.getDatatype();

            List<String> availableDate = new ArrayList<String>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yy" );

            if ( dsfReport.getOrgunit().equalsIgnoreCase( "na" ) )
            {
                orgUnitId = "0";
            }
            else
            {
                orgUnitId = dsfReport.getOrgunit();
            }
            
          
            if ( celltype.equalsIgnoreCase( DFSReport.CELL_TYPE_DATE_MULTIPLEDES_WITH_STARTDATE_AND_ENDDATE ) )
            {
                if ( period.equalsIgnoreCase( DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    String dataElements[] = dataElementExp.split( "--" );
                    startDate = resultMap.get( dataElements[0] );
                    if ( startDate == null || startDate.trim().equals( "" ) || startDate.equalsIgnoreCase( "YYYY-MM-DD" ) )
                    {
                        result = " ";
                    }
                    else
                    {
                        String endDate = resultMap.get( dataElements[1] );
                        if ( endDate == null || endDate.trim().equals( "" ) || endDate.equalsIgnoreCase( "YYYY-MM-DD" ) )
                        {
                            result = " ";
                        }
                        else
                        {
                            result = fpmuReportManager.getLatestAvailableDateFromMultipleDes(dataElements[2], orgUnitId, startDate, endDate);
                        }
                    }
                }
            }
            else if ( celltype.equalsIgnoreCase( DFSReport.CELL_TYPE_DATE_MULTIPLEDES ) )
            {
                if ( period.equalsIgnoreCase( DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    String dataElements[] = dataElementExp.split( "--" );
                    startDate = resultMap.get( dataElements[0] );
                    if ( startDate == null || startDate.trim().equals( "" ) || startDate.equalsIgnoreCase( "YYYY-MM-DD" ) )
                    {
                        result = " ";
                    }
                    else
                    {

                        result = fpmuReportManager.getLatestAvailableDateFromMultipleDes(dataElements[1], orgUnitId, startDate, null);

                    }
                }
                if ( period.equalsIgnoreCase( DFSReport.LAST_FORTNIGHT_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    String getDate = resultMap.get(dataElementExp);

                    if(getDate!=null && !getDate.isEmpty())
                    {
                        SimpleDateFormat simpleDateFormatTable8 = new SimpleDateFormat("dd/MM/yyyy");

                        Date startLatestFortnight = simpleDateFormatTable8.parse(getDate);

                        Calendar calendar = Calendar.getInstance();

                        calendar.setTime(startLatestFortnight);

                        calendar.add(Calendar.DATE,-14);

                        Date endLatestFortnight = calendar.getTime();

                        String endFortnight = simpleDateFormatTable8.format(endLatestFortnight);
                        result = endFortnight;
                    }
                    else
                    {
                        result = "";
                    }

                }
            }
            //last updated date for data element for figure 7
            else if ( celltype.equalsIgnoreCase( DFSReport.CELL_TYPE_DATE ) )
            {
            	if(period.equalsIgnoreCase(DFSReport.LAST_DATA_UPDATED_DATE))
            	{
            		Date rDate = format.parseDate(reportDate);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(rDate);

                    if (cal.get(Calendar.MONTH) < Calendar.JULY) {
                        cal.add(Calendar.YEAR, -1);
                    }
                    
                    cal.set(Calendar.MONTH, Calendar.JULY);
                    cal.set(Calendar.DATE, 1);
                	                        
                    Date eDate = format.parseDate(reportDate);
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(eDate);
                    
                    int cdate = cal1.get(Calendar.DATE);
                    cal1.set(Calendar.DATE, (cdate-5));
                    
                    System.out.println("Cdate" + cdate);
                    
                    if(cal1.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY)
                    {
                    	cal1.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                    }
                                        	                        
                    String stDate = standardDateFormat.format(cal.getTime());
                    String endDate = standardDateFormat.format(cal1.getTime());	
                    
                    System.out.println("\n\n\n"+stDate +" " + endDate);
          
                    if(chartOption==1){
                		
            				String dataelementids = "255,256,418,452,453";
	                        result = fpmuReportManager.getLastUpdatedDateFromMultipleDes(dataelementids, stDate, endDate );
	                     
	                       // if no data available 
	                        if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
	                            result = " --- ";
	                        }
                		}
                		else if(chartOption==2){
                			
	                        String dataelementids = "257,258,417,450,451";
	                        result = fpmuReportManager.getLastUpdatedDateFromMultipleDes(dataelementids, stDate, endDate );
	                        
	                       // if no data available 
	                        if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
	                            result = " --- ";
	                        }
                		}
                		else if(chartOption==3){
                			
                			String dataelementids = "267,268,420,456,457";
	                        result = fpmuReportManager.getLastUpdatedDateFromMultipleDes(dataelementids, stDate, endDate );
	                          
	                       // if no data available 
	                        if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
	                            result = " --- ";
	                        }
                		}
                		                        
	                        result = fpmuReportManager.getLastUpdatedDateFromMultipleDes(dataElementExp, stDate, endDate );
	                       
	                       // if no data available 
	                        if (startDate == null || startDate.trim().equals("") || startDate.equalsIgnoreCase("YYYY-MM-DD")) {
	                            result = " --- ";
	                        }
                		          	
            	}
            else if ( period.equalsIgnoreCase( DFSReport.CURRENT_FORTNIGHT_START_DATE ) )
                {
                    Calendar reportDateCal = Calendar.getInstance();
                    reportDateCal.setTime( format.parseDate( reportDate ) );
                    String tempS = "YYYY-MM-DD";
                    for( Calendar cal : weekList )
                    {
                        if( reportDateCal.before( cal ) )
                        {

                            Calendar tempCal = Calendar.getInstance();
                            tempCal.setTime( cal.getTime() );

                            tempCal.add( Calendar.DATE , -7  );
                            tempCal.add( Calendar.DATE , -7  );
                            tempS = standardDateFormat.format( tempCal.getTime() );
                            break;
                        }
                    }
                    
                    result = tempS;
                }
                else if ( period.equalsIgnoreCase( DFSReport.CURRENT_FORTNIGHT_END_DATE ) )
                {
                    result = startDate;
                }
                else if ( period.equalsIgnoreCase( DFSReport.CURRENT_FORTNIGHT1_START_DATE ) )
                {
                    //result = startDate.split( "-" )[0] + "-" + startDate.split( "-" )[1] + "-01";
                    Calendar reportDateCal = Calendar.getInstance();
                    reportDateCal.setTime( format.parseDate( reportDate ) );
                    String tempS = "YYYY-MM-DD";
                    for( Calendar cal : weekList )
                    {

                        if( reportDateCal.before( cal ) )
                        {
                            Calendar tempCal = Calendar.getInstance();
                            tempCal.setTime( cal.getTime() );

                            tempCal.add( Calendar.DATE , -7  );
                            tempCal.add( Calendar.DATE , -7  );
                            tempS = standardDateFormat.format( tempCal.getTime() );
                            break;
                        }
                    }
                    
                    result = tempS;
                }
                else if ( period.equalsIgnoreCase( DFSReport.CURRENT_FORTNIGHT1_END_DATE ) )
                {
                    //result = startDate.split( "-" )[0] + "-" + startDate.split( "-" )[1] + "-15";
                    result = startDate;
                }
                else if ( period.equalsIgnoreCase( DFSReport.CURRENT_FORTNIGHT2_START_DATE ) )
                {
                    //result = startDate.split( "-" )[0] + "-" + startDate.split( "-" )[1] + "-16";
                    Calendar reportDateCal = Calendar.getInstance();
                    reportDateCal.setTime( format.parseDate( reportDate ) );
                    String tempS = "YYYY-MM-DD";
                    
                    Calendar tempCal1 = Calendar.getInstance();
                    for( Calendar cal : weekList )
                    {
                        if( reportDateCal.before( cal ) )
                        {
                            tempCal1.setTime( cal.getTime() );
                            break;
                        }
                    }
                    
                    Calendar tempCal2 = Calendar.getInstance();
                    String temp1 = reportDate.split( "-" )[0]+"-"+reportDate.split( "-" )[1]+"-01";
                    tempCal2.setTime( format.parseDate( temp1 ) );
                    while( tempCal2.before( tempCal1 ) )
                    {
                        tempCal1.add( Calendar.DATE , -7  );
                    }
                    tempS = standardDateFormat.format( tempCal1.getTime() );
                    result = tempS;
                }
                else if ( period.equalsIgnoreCase( DFSReport.CURRENT_FORTNIGHT2_END_DATE ) )
                {
                    result = startDate;
                }
                else if ( period.equalsIgnoreCase( DFSReport.CURRENT_REPORT_DATE ) )
                {
                    result = startDate;
                }
                else if ( period.equalsIgnoreCase( DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    Integer dataElementId = Integer.parseInt( dataElementExp.split( "\\." )[0] );
                    Integer optionComboId = Integer.parseInt( dataElementExp.split( "\\." )[1] );

                    Date sDate = format.parseDate( startDate );
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( sDate );
                    cal.add( Calendar.DATE, maxDaysForLastAvailableDate.intValue() );
                    String endDate = standardDateFormat.format( cal.getTime() );

                    result = fpmuReportManager.getLatestAvailableDate(dataElementId, optionComboId, orgUnitId, startDate, endDate);
                }
                else if ( period.equalsIgnoreCase( DFSReport.LAST_WEEK_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    Integer dataElementId = Integer.parseInt( dataElementExp.split( "\\." )[0] );
                    Integer optionComboId = Integer.parseInt( dataElementExp.split( "\\." )[1] );

                    Date sDate = format.parseDate( startDate );
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( sDate );
                    cal.add( Calendar.DATE, -7 );
                    startDate = standardDateFormat.format( cal.getTime() );
                    cal.add( Calendar.DATE, maxDaysForLastAvailableDate.intValue() );
                    String endDate = standardDateFormat.format( cal.getTime() );

                    result = fpmuReportManager.getLatestAvailableDate(dataElementId, optionComboId, orgUnitId, startDate, endDate);
                }
            }
            else if ( celltype.equalsIgnoreCase( DFSReport.CELL_TYPE_DATEDATA ) )
            {
                if ( period.equalsIgnoreCase( DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    Date sDate = format.parseDate( startDate );
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( sDate );
                    cal.add( Calendar.DATE, maxDaysForLastAvailableDate.intValue() );
                    String endDate = standardDateFormat.format( cal.getTime() );

                    result = fpmuReportManager.getResultValue( dataElementExp, startDate, endDate, orgUnitId, aggtype, celltype );
                }
            }
            else if ( celltype.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_STARTDATE )
                || celltype.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_STARTDATE_TEXTDATA ) )
            {
                if ( period.equalsIgnoreCase( DFSReport.CUMULATIVE_FOR_CURRENT_MONTH ) )
                {
                    String dataElements[] = dataElementExp.split( "--" );

                    String getDate = resultMap.get(dataElements[0]);

                    if(getDate!=null && !getDate.isEmpty())
                    {
                        SimpleDateFormat simpleDateFormatTable8 = new SimpleDateFormat("dd/MM/yyyy");

                        Date latestFortnightEndDate = simpleDateFormatTable8.parse(getDate);

                        String endDate = standardDateFormat.format(latestFortnightEndDate);

                        Calendar calendar = Calendar.getInstance();

                        calendar.setTime(latestFortnightEndDate);

                        calendar.add(Calendar.MONTH,-1);

                        Date startOfFortnightMonth = calendar.getTime();

                        String monthStart= standardDateFormat.format(startOfFortnightMonth);

                        resultMap.put("table8monthstart",simpleDateFormatTable8.format(startOfFortnightMonth));

                        result = fpmuReportManager.getResultValue(dataElements[1],monthStart,endDate,orgUnitId,aggtype,celltype);
                    }
                    else
                    {
                        result = "";
                    }
                }
                if ( period.equalsIgnoreCase( DFSReport.LAST_FORTNIGHT_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    String dataElements[] = dataElementExp.split("--");
                    startDate = resultMap.get( dataElements[0] );
                    if ( startDate == null || startDate.trim().equals( "" ) || startDate.equalsIgnoreCase( "YYYY-MM-DD" ) )
                    {
                        result = " ";
                    }
                    else
                    {
                        SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                        Date sDate = ddMMYYYYFormat.parse( startDate );
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( sDate );
                        cal.add( Calendar.DATE, -14 );
                        startDate = standardDateFormat.format( cal.getTime() );
                        cal.add( Calendar.DATE, maxDaysForLastAvailableDate.intValue() );
                        String endDate = standardDateFormat.format( cal.getTime() );

                        result = fpmuReportManager.getResultValue( dataElements[1], startDate, endDate, orgUnitId, aggtype, celltype );
                    }
                }
                else if ( period.equalsIgnoreCase( DFSReport.LAST_MONTH_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    String dataElements[] = dataElementExp.split( "--" );
                    startDate = resultMap.get( dataElements[0] );

                    if ( startDate == null || startDate.trim().equals( "" ) || startDate.equalsIgnoreCase( "YYYY-MM-DD" ) )
                    {
                        result = " ";
                    }
                    else
                    {
                        SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                        Date sDate = ddMMYYYYFormat.parse( startDate );
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( sDate );
                        cal.add( Calendar.DATE, -30 );
                        startDate = standardDateFormat.format( cal.getTime() );
                        cal.add( Calendar.DATE, maxDaysForLastAvailableDate.intValue() );
                        String endDate = standardDateFormat.format( cal.getTime() );

                        result = fpmuReportManager.getResultValue( dataElements[1], startDate, endDate, orgUnitId, aggtype, celltype );
                    }
                }
                else if ( period.equalsIgnoreCase( DFSReport.LAST_AVAILABLE_DATE_FOR_PREVIOUS_FISCAL_YEAR ) )
                {
                    String dataElements[] = dataElementExp.split( "--" );
                    startDate = resultMap.get( dataElements[0] );

                    if ( startDate == null || startDate.trim().equals( "" ) || startDate.equalsIgnoreCase( "YYYY-MM-DD" ) )
                    {
                        result = " ";
                    }
                    else
                    {
                        SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                        Date sDate = ddMMYYYYFormat.parse( startDate );
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( sDate );
                        cal.add( Calendar.YEAR, -1 );
                        startDate = standardDateFormat.format( cal.getTime() );
                        cal.add( Calendar.DATE, maxDaysForLastAvailableDate.intValue() );
                        String endDate = standardDateFormat.format( cal.getTime() );

                        result = fpmuReportManager.getResultValue( dataElements[1], startDate, endDate, orgUnitId, aggtype, celltype );
                    }
                }
                else if ( period.equalsIgnoreCase( DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    String dataElements[] = dataElementExp.split( "--" );
                    startDate = resultMap.get( dataElements[0] );

                    if ( startDate == null || startDate.trim().equals( "" )
                        || startDate.equalsIgnoreCase( "YYYY-MM-DD" ) )
                    {
                        result = " ";
                    }
                    else
                    {
                        SimpleDateFormat ddMMYYYYFormat = new SimpleDateFormat( "dd/MM/yyyy" );
                        Date sDate = ddMMYYYYFormat.parse( startDate );
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( sDate );
                        startDate = standardDateFormat.format( cal.getTime() );
                        cal.add( Calendar.DATE, maxDaysForLastAvailableDate.intValue() );
                        String endDate = standardDateFormat.format( cal.getTime() );

                        result = fpmuReportManager.getResultValue( dataElements[1], startDate, endDate, orgUnitId,
                            aggtype, celltype );
                    }
                }
             }
            else if ( celltype.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULATEXT )
                || celltype.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULANUMBER ) )
            {
                result = getResultValueForFormula( dataElementExp, celltype, resultMap );
            }                           
            else if ( celltype.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_FOR_STARTDATE_AND_ENDDATE ) )
            {
                if ( period.equalsIgnoreCase( DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    String dataElements[] = dataElementExp.split( "--" );
                    startDate = resultMap.get( dataElements[0] );
                    if ( startDate == null || startDate.trim().equals( "" )
                        || startDate.equalsIgnoreCase( "YYYY-MM-DD" ) )
                    {
                        result = " ";
                    }
                    else
                    {

                        SimpleDateFormat preDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        Date sDate = preDateFormat.parse(startDate);
                        String endDate = resultMap.get( dataElements[1] );
                        Date eDate = preDateFormat.parse(endDate);

                        startDate = dateFormat.format(sDate);

                        endDate = dateFormat.format(eDate);

                        if ( endDate == null || endDate.trim().equals( "" ) || endDate.equalsIgnoreCase( "YYYY-MM-DD" ) )
                        {
                            result = " ";
                        }
                        else
                        {
                            result = fpmuReportManager.getResultValue( dataElements[2], startDate, endDate, orgUnitId,
                                aggtype, celltype );
                        }
                    }
                }
            }
            else
            {

            if ( period.equalsIgnoreCase( DFSReport.LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    Date sDate = format.parseDate( startDate );
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( sDate );
                    cal.add( Calendar.DATE, maxDaysForLastAvailableDate.intValue() );
                    String endDate = standardDateFormat.format( cal.getTime() );

                    result = fpmuReportManager.getResultValue( dataElementExp, startDate, endDate, orgUnitId, aggtype,
                        celltype );
                }
                else if ( period
                    .equalsIgnoreCase( DFSReport.CUMULATIVE_UPTO_LAST_AVAILABLE_DATE_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    String endDate = startDate;

                    Date sDate = format.parseDate( startDate );
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( sDate );

                    if ( cal.get( Calendar.MONTH ) < Calendar.JULY )
                    {
                        cal.add( Calendar.YEAR, -1 );
                    }
                    cal.set( Calendar.MONTH, Calendar.JULY );
                    cal.set( Calendar.DATE, 1 );

                    startDate = standardDateFormat.format( cal.getTime() );

                    result = fpmuReportManager.getResultValue( dataElementExp, startDate, endDate, orgUnitId, aggtype,
                        celltype );
                   
                    System.out.println("=========================================================================");
                    System.out.println(dataElementExp+","+startDate+","+endDate+","+result);
                    System.out.println("=========================================================================");

                }
           else if ( period
                        .equalsIgnoreCase( DFSReport.CUMULATIVE_UPTO_PREVIOUS_FORTNIGHT_FOR_CURRENT_FISCAL_YEAR ) )
                {
                    Date eDate = format.parseDate( startDate );
                    Calendar eCalendar = Calendar.getInstance();
                    eCalendar.setTime(eDate);
                    eCalendar.add(Calendar.DATE,-14);

                    String endDate = standardDateFormat.format( eCalendar.getTime() );

                    Date sDate = format.parseDate( startDate );
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( sDate );

                    if ( cal.get( Calendar.MONTH ) < Calendar.JULY )
                    {
                        cal.add( Calendar.YEAR, -1 );
                    }
                    cal.set( Calendar.MONTH, Calendar.JULY );
                    cal.set( Calendar.DATE, 1 );

                    startDate = standardDateFormat.format( cal.getTime() );

                    result = fpmuReportManager.getResultValue( dataElementExp, startDate, endDate, orgUnitId, aggtype,
                            celltype );
                 
                    System.out.println("=========================================================================");
                    System.out.println(dataElementExp+","+startDate+","+endDate+","+result);
                    System.out.println("=========================================================================");
                }
          // Added to get previously fortnightly distribution PFD //     
	           else if ( period
	               .equalsIgnoreCase( DFSReport.CUMULATIVE_UPTO_LAST_FORTHNIGHT_FOR_CURRENT_FISCAL_YEAR ) )
	           {
	           	Date eDate = format.parseDate(startDate);
	           	Calendar cal1 = Calendar.getInstance();
	           	cal1.setTime(eDate);
	           	
	           	cal1.set(Calendar.DATE, -14);                              	
	           	String endDate = standardDateFormat.format(cal1.getTime());
	           	
	               
	           	Date sDate = format.parseDate( startDate );
	               Calendar cal = Calendar.getInstance();
	               cal.setTime( sDate );
	
	               if ( cal.get( Calendar.MONTH ) < Calendar.JULY )
	               {
	                   cal.add( Calendar.YEAR, -1 );
	               }
	               cal.set( Calendar.MONTH, Calendar.JULY );
	               cal.set( Calendar.DATE, 1 );
	
	               startDate = standardDateFormat.format( cal.getTime() );
	
	               result = fpmuReportManager.getResultValue( dataElementExp, startDate, endDate, orgUnitId, aggtype,
	                   celltype );
	              
	               System.out.println("=========================================================================");
	               System.out.println(dataElementExp+","+startDate+","+endDate+","+result);
	               System.out.println("=========================================================================");
	           }
	           
	           else if ( period
	                   .equalsIgnoreCase( DFSReport.CUMULATIVE_UPTO_LAST_OF_LAST_FORTHNIGHT_FOR_CURRENT_FISCAL_YEAR) )
	               {
	               	Date eDate = format.parseDate(startDate);
	               	Calendar cal1 = Calendar.getInstance();
	               	cal1.setTime(eDate);
	               	
	               	cal1.set(Calendar.DATE, -28);                              	
	               	String endDate = standardDateFormat.format(cal1.getTime());
	               	
	                   
	               	Date sDate = format.parseDate( startDate );
	                   Calendar cal = Calendar.getInstance();
	                   cal.setTime( sDate );
	
	                   if ( cal.get( Calendar.MONTH ) < Calendar.JULY )
	                   {
	                       cal.add( Calendar.YEAR, -1 );
	                   }
	                   cal.set( Calendar.MONTH, Calendar.JULY );
	                   cal.set( Calendar.DATE, 1 );
	
	                   startDate = standardDateFormat.format( cal.getTime() );
	
	                   result = fpmuReportManager.getResultValue( dataElementExp, startDate, endDate, orgUnitId, aggtype,
	                       celltype );
	                 
	                   System.out.println("=========================================================================");
	                   System.out.println(dataElementExp+","+startDate+","+endDate+","+result);
	                   System.out.println("=========================================================================");
	               }
                //PFD end//
                
	            else if ( period
                        .equalsIgnoreCase( DFSReport.CUMULATIVE_UPTO_LAST_FORTNIGHT_FOR_PREVIOUS_FISCAL_YEAR ) )
                {
                    Date eDate = format.parseDate( startDate );
                    Calendar eCalendar = Calendar.getInstance();
                    eCalendar.setTime(eDate);
                    eCalendar.add(Calendar.YEAR,-1);

                    String endDate = standardDateFormat.format( eCalendar.getTime() );;

                    Date sDate = format.parseDate( startDate );
                    Calendar cal = Calendar.getInstance();
                    cal.setTime( sDate );

                    cal.add( Calendar.YEAR, -1 );

                    if ( cal.get( Calendar.MONTH ) < Calendar.JULY )
                    {
                        cal.add( Calendar.YEAR, -1 );
                    }
                    cal.set( Calendar.MONTH, Calendar.JULY );
                    cal.set( Calendar.DATE, 1 );

                    startDate = standardDateFormat.format( cal.getTime() );

                    result = fpmuReportManager.getResultValue( dataElementExp, startDate, endDate, orgUnitId, aggtype,
                            celltype );
                   
                    System.out.println("=========================================================================");
                    System.out.println(dataElementExp+","+startDate+","+endDate+","+result);
                    System.out.println("=========================================================================");
                }
            }

            if ( dataType.equalsIgnoreCase( FortnightReport.DATA_TYPE_NUMBER )
                && (result == null || result.trim().equals( "" ) || result.isEmpty()) )
            {
                resultMap.put( name, "0.0" );
            }
            else
            {
                resultMap.put( name, result );
            }

            System.out.println( name + " --- " + result );
        }

        //----------------------------------------------------------------------
        // Initializing result map for date formatting purpose
        //----------------------------------------------------------------------

        SimpleDateFormat monthFormat = new SimpleDateFormat( "MMM-yy" );
        SimpleDateFormat ddmmyyyyFormat = new SimpleDateFormat( "dd/MM/yyyy" );

        String temp = resultMap.get( "table2month1" );
        if ( temp == null || temp.trim().equals( "" ) || temp.equals( "YYYY-MM-DD" ) )
        {
            resultMap.put( "table2month1disp", "DD/MM/YYYY" );
        }
        else
        {
            resultMap.put( "table2month1disp", monthFormat.format( ddmmyyyyFormat.parse( temp ) ) );
        }
        temp = null;
        temp = resultMap.get( "table2month2" );
        if ( temp == null || temp.trim().equals( "" ) || temp.equals( "YYYY-MM-DD" ) )
        {
            resultMap.put( "table2month2disp", "DD/MM/YYYY" );
        }
        else
        {
            resultMap.put( "table2month2disp", monthFormat.format( ddmmyyyyFormat.parse( temp ) ) );
        }
        temp = null;
        temp = resultMap.get( "table2month3" );
        if ( temp == null || temp.trim().equals( "" ) || temp.equals( "YYYY-MM-DD" ) )
        {
            resultMap.put( "table2month3disp", "DD/MM/YYYY" );
        }
        else
        {
            resultMap.put( "table2month3disp", monthFormat.format( ddmmyyyyFormat.parse( temp ) ) );
        }
        temp = null;
        temp = resultMap.get( "table2month4" );
        if ( temp == null || temp.trim().equals( "" ) || temp.equals( "YYYY-MM-DD" ) )
        {
            resultMap.put( "table2month4disp", "DD/MM/YYYY" );
        }
        else
        {
            resultMap.put( "table2month4disp", monthFormat.format( ddmmyyyyFormat.parse( temp ) ) );
        }
        temp = null;
        temp = resultMap.get( "table2month5" );
        if ( temp == null || temp.trim().equals( "" ) || temp.equals( "YYYY-MM-DD" ) )
        {
            resultMap.put( "table2month5disp", "DD/MM/YYYY" );
        }
        else
        {
            resultMap.put( "table2month5disp", monthFormat.format( ddmmyyyyFormat.parse( temp ) ) );
        }

        SimpleDateFormat dayMonthFormat = new SimpleDateFormat( "dd-MMM" );
        temp = null;
        temp = resultMap.get( "table2date1" );
        if ( temp == null || temp.trim().equals( "" ) || temp.equals( "YYYY-MM-DD" ) )
        {
            resultMap.put( "table2date1disp", "DD/MM/YYYY" );
        }
        else
        {
            resultMap.put( "table2date1disp", dayMonthFormat.format( ddmmyyyyFormat.parse( temp ) ) );
        }
        temp = null;
        temp = resultMap.get( "table2date2" );
        if ( temp == null || temp.trim().equals( "" ) || temp.equals( "YYYY-MM-DD" ) )
        {
            resultMap.put( "table2date2disp", "DD/MM/YYYY" );
        }
        else
        {
            resultMap.put( "table2date2disp", dayMonthFormat.format( ddmmyyyyFormat.parse( temp ) ) );
        }

        String monthShortNames[] = { " ", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
        String monthNames[] = { " ", "January", "Februay", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
       
        String temp1 = resultMap.get( "reportcelldata3" );
        if ( temp1 == null || temp1.trim().equals( "" ) || temp1.equals( "YYYY-MM-DD" ) )
        {
            resultMap.put( "table8data1disp", "DD/MM/YYYY" );
        }
        else
        {
            String temp2 = resultMap.get( "reportcelldata7" );
            if ( temp2 == null || temp2.trim().equals( "" ) || temp2.equals( "YYYY-MM-DD" ) )
            {
                resultMap.put( "table8data1disp", "DD/MM/YYYY" );
                resultMap.put( "table8data2disp", "DD/MM/YYYY" );
                resultMap.put( "table8data3disp", "DD/MM/YYYY" );
            }
            else
            {
                String temp3 = temp1.split( "-" )[2];
                
                if ( !temp1.split( "-" )[1].equals( temp2.split( "-" )[1] ) )
                {
                    temp3 += monthShortNames[Integer.parseInt( temp1.split( "-" )[1] )];
                }

                temp3 += "-" + temp2.split( "-" )[2] + monthShortNames[Integer.parseInt( temp2.split( "-" )[1] )];

                resultMap.put( "table8data1disp", temp3 );

                resultMap.put( "table8data3disp", temp2.split( "-" )[2] + monthShortNames[Integer.parseInt( temp2.split( "-" )[1] )] );

                resultMap.put( "table8data2disp", monthNames[Integer.parseInt( temp2.split( "-" )[1] )] );
            }
        }



        //----------------------------------------------------------------------
        // Initializing result map for printing Arrow Images
        //----------------------------------------------------------------------

        temp = resultMap.get( "table1calc1" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc1disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 1 )
            {
                resultMap.put( "table1calc1disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -1 )
            {
                resultMap.put( "table1calc1disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc1disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc2" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc2disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 1 )
            {
                resultMap.put( "table1calc2disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -1 )
            {
                resultMap.put( "table1calc2disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc2disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc3" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc3disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 1 )
            {
                resultMap.put( "table1calc3disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -1 )
            {
                resultMap.put( "table1calc3disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc3disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc4" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc4disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 1 )
            {
                resultMap.put( "table1calc4disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -1 )
            {
                resultMap.put( "table1calc4disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc4disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc5" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc5disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 5 )
            {
                resultMap.put( "table1calc5disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -5 )
            {
                resultMap.put( "table1calc5disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc5disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc6" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc6disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 5 )
            {
                resultMap.put( "table1calc6disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -5 )
            {
                resultMap.put( "table1calc6disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc6disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc7" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc7disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 1 )
            {
                resultMap.put( "table1calc7disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -1 )
            {
                resultMap.put( "table1calc7disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc7disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc8" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc8disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 1 )
            {
                resultMap.put( "table1calc8disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -1 )
            {
                resultMap.put( "table1calc8disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc8disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc9" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc9disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 1 )
            {
                resultMap.put( "table1calc9disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -1 )
            {
                resultMap.put( "table1calc9disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc9disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc10" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc10disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 1 )
            {
                resultMap.put( "table1calc10disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -1 )
            {
                resultMap.put( "table1calc10disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc10disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc11" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc11disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 5 )
            {
                resultMap.put( "table1calc11disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -5 )
            {
                resultMap.put( "table1calc11disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc11disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        temp = resultMap.get( "table1calc12" );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( "table1calc12disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 5 )
            {
                resultMap.put( "table1calc12disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -5 )
            {
                resultMap.put( "table1calc12disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( "table1calc12disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }

        //----------------------------------------TABLE 8 DATE---------------------------------//



        //--------------------------------------- TABLE 6 ARROWS -----------------------------//

        table6And7Arrows( "table6calc1", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc2", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc3", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc4", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc5", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc6", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc7", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc8", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc9", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc10", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc11", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc12", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc13", arrowImagePath, resultMap );
        table6And7Arrows( "table6calc14", arrowImagePath, resultMap );

        //-------------------------------------- TABLE 7 ARROWS ------------------------------//
        table6And7Arrows( "table7calc1", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc2", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc3", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc4", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc5", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc6", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc7", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc8", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc9", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc10", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc11", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc12", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc13", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc14", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc15", arrowImagePath, resultMap );
        table6And7Arrows( "table7calc16", arrowImagePath, resultMap );

        //----------------------------------------------------------------------
        // Report Generated Date
        //----------------------------------------------------------------------
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat( "dd/MM/yyyy HH:mm" );
        String reportGeneratedDate = dateTimeFormat.format( new Date() );
        hash.put( "reportGeneratedDate", reportGeneratedDate );

        //----------------------------------------------------------------------
        // Report Published Date
        //----------------------------------------------------------------------
        Date tempDate = format.parseDate( reportDate );
        SimpleDateFormat dataFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        reportDate = dataFormat.format( tempDate );
        hash.put( "date", reportDate );

        hash.put( "resultMap", resultMap );

        resultMap.put( "date", reportDate );
        resultMap.put( "reportGeneratedDate", reportGeneratedDate );
        outputResultMap = resultMap;


        if ( reportOutputMode.equalsIgnoreCase( "COMMENTOUTPUT" ) )
        {
            return "COMMENTOUTPUT";
        }

        //----------------------------------------------------------------------
        // Jasper Code
        //----------------------------------------------------------------------

        fileName = "fortnightly_Report.jrxml";

        JasperReport jasperReport = JasperCompileManager.compileReport( path + fileName );
        jasperPrint = JasperFillManager.fillReport( jasperReport, hash, new JREmptyDataSource() );

        ServletOutputStream ouputStream = response.getOutputStream();
        JRExporter exporter = null;

        response.setContentType( "application/msword" );
        response.setHeader( "Content-Disposition", "inline; fileName=\"Fortnightly_" + reportDate + ".doc\"" );
        exporter = new JRDocxExporter();
        exporter.setParameter( JRExporterParameter.JASPER_PRINT, jasperPrint );
        exporter.setParameter( JRExporterParameter.OUTPUT_STREAM, ouputStream );

        try
        {
            exporter.exportReport();
        }
        catch ( JRException e )
        {
            throw new ServletException( e );
        }

        return SUCCESS;
    }

    private List<Calendar> getWeeklyDates( String fromDate, String toDate, int weekStartDay )
    {
        List<Calendar> weekList = new ArrayList<Calendar>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Date sDate = null;
        Date eDate = null;
        
        try
        {
            sDate = simpleDateFormat.parse( fromDate );
            eDate = simpleDateFormat.parse( toDate );
        }
        catch( Exception e )
        {
            System.out.println( "Method getWeeklyDates : " + e.getMessage() );
        }

        Calendar startCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        startCal.setTime( sDate );
        endCal.setTime( eDate );

        startCal.setFirstDayOfWeek( weekStartDay );
        endCal.setFirstDayOfWeek( weekStartDay );
        
        while( startCal.get( Calendar.DAY_OF_WEEK ) != weekStartDay )
        {
            startCal.add( Calendar.DATE , -1 );
        }

        while( endCal.get( Calendar.DAY_OF_WEEK ) != weekStartDay )
        {
            endCal.add( Calendar.DATE , -1 );
        }
        
        while( startCal.before(  endCal ) )
        {
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTime( startCal.getTime() );
            weekList.add( tempCal );
            startCal.add( Calendar.DATE , 7 );
        }
        
        return weekList;
    }
    
    private String getResultValueForFormula( String formula, String cellType, Map<String, String> resultMap )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[(.+?)\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                String dataValue = null;

                dataValue = resultMap.get( replaceString );

                if ( dataValue == null )
                {
                    replaceString = " ";
                }
                else
                {
                    replaceString = String.valueOf( dataValue );
                }

                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );

            if ( cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_FORMULANUMBER ) )
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                    d = Math.round( d * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                }
                catch ( Exception e )
                {
                    d = 0.0;
                    resultValue = "";
                }

                resultValue = "" + d;
            }
            else
            {
                resultValue = buffer.toString();
            }

            if ( resultValue.equalsIgnoreCase( "" ) )
            {
                resultValue = " ";
            }

            return resultValue;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception in getResultValue method: ", e );
        }

    }

    private void table1Arrows( String sourceCell, String arrowImagePath, Map<String, String> resultMap )
    {
        String temp = resultMap.get( sourceCell );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );

            if ( tempD > 5 )
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlyredup.png" );
            }
            else if ( tempD < -5 )
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
        }
    }

    private void table6And7Arrows( String sourceCell, String arrowImagePath, Map<String, String> resultMap )
    {
        String temp = resultMap.get( sourceCell );
        if ( temp == null || temp.trim().equals( "" ) || temp.isEmpty() )
        {
            resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlyyellowright.png" );
        }
        else
        {
            Double tempD = Double.parseDouble( temp );
            Long tempL = Math.round( tempD );

            resultMap.put( sourceCell, tempL + "%" );

            if ( tempL == 0 )
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlyyellowright.png" );
            }
            else if ( tempL >= -5 && tempL < 0 )
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlyyellowdown.png" );
            }
            else if ( tempL >= -10 && tempL < -5 )
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlygreendown.png" );
            }
            else if ( tempL < -10 )
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlyreddown.png" );
            }
            else if ( tempL > 0 && tempL <= 5 )
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlyyellowup.png" );
            }
            else if ( tempL > 5 && tempL <= 10 )
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlygreenup.png" );
            }
            else if ( tempL >= 10 )
            {
                resultMap.put( sourceCell + "disp", arrowImagePath + "fortnightlyredup.png" );
            }
        }
    }

    private static java.awt.Image createEmployeeChartImage( final CategoryDataset dataset )
    {
        JFreeChart chart = ChartFactory.createLineChart( "", "", "", dataset, PlotOrientation.VERTICAL, true, true,
            true );
        chart.setBackgroundPaint( Color.white );

        return chart.createBufferedImage( 260, 120 );
    }

}