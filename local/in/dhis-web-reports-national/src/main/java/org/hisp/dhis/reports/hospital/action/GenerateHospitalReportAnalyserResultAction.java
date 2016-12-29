package org.hisp.dhis.reports.hospital.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.MathUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateHospitalReportAnalyserResultAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }    
    
    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }
    
    private String periodTypeId;
    
    public void setPeriodTypeId( String periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }
    
    private OrganisationUnit selectedOrgUnit;

    private List<OrganisationUnit> orgUnitList;

    private SimpleDateFormat simpleDateFormat;
    
    //private SimpleDateFormat monthFormat;

    private String reportFileNameTB;

    private String reportModelTB;

    private Date sDate;

    private Date eDate;

    private String raFolderName;
    
    private Period selectedPeriod;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        // Initialization
        
        raFolderName = reportService.getRAFolderName();
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        //monthFormat = new SimpleDateFormat( "MMMM" );
        // Getting Report Details       
        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        
        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+ " Report Generation Start Time is : " + new Date() );
        
        if ( reportModelTB.equalsIgnoreCase( "STATIC" ) || reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }
        
        // Period Related Info
        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );
        
        Calendar calendar = Calendar.getInstance();
        
        calendar.setTime( sDate );
        
        int monthMaxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeId );
        
        
        List<Period> periodList = new ArrayList<Period>();
        //periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, sDate, eDate ) );
        
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );        
        String periodIdsByComma = getCommaDelimitedString( periodIds );
        
        //System.out.println( "Data Element IN : "  + new Date() );
        
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        
        //System.out.println( "Data Element OUT  : "  + new Date() );
        
        //System.out.println( "ORG UNIT  IN  : "  + new Date() );
        String organisationUnitIdsByComma = "-1";
        Iterator<Report_inDesign> deCountIterator = reportDesignList.iterator();
        while ( deCountIterator.hasNext() )
        {
            Report_inDesign report_inDesign = (Report_inDesign) deCountIterator.next();
            
            String ouid = report_inDesign.getOuid();
            
            if( ouid != null && !ouid.equalsIgnoreCase( "" ) )
            {
                organisationUnitIdsByComma += "," + ouid;
            }
            //int orgUnitId = Integer.parseInt( ouid );
        }
        //System.out.println( "ORG UNIT  OUT  : "  + new Date() );
        
        //System.out.println( "Calculate Method in : " + new Date() );
        Map<String, String> aggDeMap = new HashMap<String, String>();
        
        aggDeMap.putAll( reportService.getResultDataFromDataValueTable( organisationUnitIdsByComma, dataElmentIdsByComma, periodIdsByComma ) );
        
        //System.out.println( "calculate Method Out : " + new Date());
        
        //System.out.println(  " dataElmentIdsByComma : " + dataElmentIdsByComma  );
        
        //System.out.println(  " periodIdsByComma : " + periodIdsByComma  );
        
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );
        
        Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
        while ( reportDesignIterator.hasNext() )
        {
            Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

            String sType = report_inDesign.getStype();
            String ouid = report_inDesign.getOuid();
                
            String deCodeString = report_inDesign.getExpression();
            String tempStr = "";
                
            if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )                    
            {
                tempStr = selectedOrgUnit.getName();
            }
            else if ( deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
            {
                tempStr = simpleDateFormat.format( sDate );
            }
            else if ( deCodeString.equalsIgnoreCase( "NA" ) )
            {
                tempStr = " ";
            }
            else
            {
                if ( sType.equalsIgnoreCase( "organisationunit" ) )
                {
                    //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouid ) );
                    //tempStr = reportService.getIndividualResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), orgUnit, reportModelTB );
                    deCodeString = deCodeString.replaceAll( "NO-OF-MONTH-DAYS", ""+monthMaxDays );
                    tempStr = getAggVal( deCodeString, aggDeMap, ouid );
                    //System.out.println( deCodeString +", "+ aggDeMap.size() +", "+ ouid );
                }
                else if( sType.equalsIgnoreCase( "organisationunit_text" ) )
                {
                    String deIdsByComma = "-1";
                    
                    for( int i = 0; i < deCodeString.split( "," ).length; i++ )
                    {
                        deIdsByComma += "," + deCodeString.split( "," )[i];
                    }
                    
                    tempStr = reportService.getTextDataFromDataValueTable( ouid, deIdsByComma, periodIdsByComma );
                    
                    //System.out.println( tempStr + "--" + tempStr.length() );
                    
                    if( tempStr != null && tempStr.length() > 0 )
                    {
                        tempStr = tempStr.replace( tempStr.substring( tempStr.length()-1 ), ""); 
                    }
                }
            }
                
            int tempRowNo = report_inDesign.getRowno();
            int tempColNo = report_inDesign.getColno();
            int sheetNo = report_inDesign.getSheetno();
            WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
            try
            {
                sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
            }
            catch( Exception e )
            {
                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
            }
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+ " Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        return SUCCESS;
    }

    
    private String getAggVal( String expression, Map<String, String> aggDeMap, String ouid )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                
                String keyString = replaceString + ":" + ouid;
                
                replaceString = aggDeMap.get( keyString );
                
                if( replaceString == null )
                {
                    replaceString = "0";
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
            
            double d = 0.0;
            
            try
            {
                //System.out.println( "buffer.toString() : " + buffer.toString() );
                d = MathUtils.calculateExpression( buffer.toString() );
                //System.out.println( "Result : " + d );
                
                if( d == -1.0 )
                {
                    d = 0.0;
                    resultValue = "";
                    //System.out.println( "Result value inside -1.0 : "  + d );
                }
                
                if( Double.isNaN(d) )
                {
                    d = 0.0;
                    resultValue = "";
                    //System.out.println( "Result value inside NaN : "  +  d );
                }
                
                /*
                else if( d == Double.NaN )
                {
                    d = 0.0;
                    resultValue = "";
                    System.out.println( "Result value inside NaN : " + d );
                }
                */
            }
            catch ( Exception e )
            {
                d = 0.0;
                resultValue = "";
            }
            
            resultValue = "" + (double) d;
            
            //System.out.println( "Final Result value is  : "  + d );
            
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
               
    // for format the cell
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }
    
    // Returns the OrgUnitTree for which Root is the orgUnit
    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
        Collections.sort( children, new IdentifiableObjectNameComparator() );
        //Collections.sort( children, new OrganisationUnitNameComparator() );

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }
    // getChildOrgUnitTree end
}
