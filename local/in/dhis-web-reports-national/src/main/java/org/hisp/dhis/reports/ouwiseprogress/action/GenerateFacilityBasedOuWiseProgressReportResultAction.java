package org.hisp.dhis.reports.ouwiseprogress.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import jxl.write.Formula;
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
public class GenerateFacilityBasedOuWiseProgressReportResultAction implements Action
{
    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
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
    
    private int availablePeriodsto;
    
    public void setAvailablePeriodsto( int availablePeriodsto )
    {
        this.availablePeriodsto = availablePeriodsto;
    }

    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }

    private Period selectedPeriod;
    
    private Period selectedEndPeriod;

    private SimpleDateFormat simpleDateFormat;

    private Date sDate;

    private Date eDate;

    private String raFolderName;

    private List<Period> periodList = new ArrayList<Period>();
    private SimpleDateFormat yearFormat;
    private SimpleDateFormat dateTimeFormat;
    
    private OrganisationUnit selectedOrgUnit;

    private List<OrganisationUnit> orgUnitList;

    private String reportFileNameTB;

    private String reportModelTB;
    
    private PeriodType selPeriodType;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        
        // Initialization
        
        
        raFolderName = reportService.getRAFolderName();
        simpleDateFormat = new SimpleDateFormat( "MMM-yy" );
        
        yearFormat = new SimpleDateFormat( "yyyy" );
        SimpleDateFormat dayFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        dateTimeFormat = new SimpleDateFormat( "EEEE, dd MMMM yyyy HH:mm:ss zzzz" );
        // Getting Report Details       
        String deCodesXMLFileName = "";
        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );
        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        
        //int selectedOrgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( ouIDTB );

        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );
        
        // OrgUnit Related Information
        if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
        {            
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            
            //Hardcoded to level 2 to make report fast for state level
            /*
            if( selectedOrgUnitLevel != 2 )
            {
                orgUnitList.add( selectedOrgUnit );
            }
            */
        }
        
        // Period Info
        selectedPeriod = periodService.getPeriod( availablePeriods );
        selectedEndPeriod = periodService.getPeriod( availablePeriodsto );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedEndPeriod.getEndDate() ) );
        
        selPeriodType = selReportObj.getPeriodType();
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( selPeriodType, sDate, eDate ) );
        
        //List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );        
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );        
        String periodIdsByComma = getCommaDelimitedString( periodIds );
        
        
        
        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        
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
        
        
        int slno = 1;
        int orgUnitCount = 0;
        Iterator<OrganisationUnit> organisationUnitIterator = orgUnitList.iterator();
        
        while ( organisationUnitIterator.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) organisationUnitIterator.next();
            
            List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
            String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );
            
            Map<String, String> aggDeMap = new HashMap<String, String>();
            if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( currentOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
                
            }
            else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( ""+currentOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }

            //int count1 = 0;
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while ( reportDesignIterator.hasNext() )
            {
                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";
                
                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )                    
                {
                    tempStr = selectedOrgUnit.getName();
                }
                else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    tempStr = currentOrgUnit.getName();
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    tempStr = selectedOrgUnit.getParent().getName();
                    
                }
                else if( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    tempStr = selectedOrgUnit.getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "DATE-FROM" ) )
                {
                    tempStr = dayFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "DATE-TO" ) )
                {
                    tempStr = dayFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-FROM" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-TO" ) )
                {
                    tempStr = simpleDateFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "CURRENTDATETIME" ) )
                {
                    tempStr = dateTimeFormat.format( new Date() );
                }
                
                else if ( deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
                {
                    tempStr = yearFormat.format( sDate );
                }
                
                else if( deCodeString.equalsIgnoreCase( "SLNo" ) )
                {
                    tempStr = "" + slno;
                } 
                
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
                
                else
                {
                    if ( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) ) 
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                        }
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                        }
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                        }
                        
                        //System.out.println( " deCodeString " + deCodeString  + " -tempSt " + tempStr + "-" + currentOrgUnit.getName()  );
                    }
                    
                    else if ( sType.equalsIgnoreCase( "formula" ) )
                    {
                        tempStr = deCodeString;
                    }
                } 
                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
                if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    
                    if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" )
                        || deCodeString.equalsIgnoreCase( "DATE-FROM" ) || deCodeString.equalsIgnoreCase( "DATE-TO" )  || deCodeString.equalsIgnoreCase( "MONTH-FROM" ) 
                        || deCodeString.equalsIgnoreCase( "MONTH-TO" ) || deCodeString.equalsIgnoreCase( "CURRENTDATETIME" )  || deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
                    {
                    }
                    else
                    {
                        tempRowNo += orgUnitCount;
                    }
                    
                    try
                    {
                        if( sType.equalsIgnoreCase( "formula" ) )
                        {
                            tempStr = tempStr.replace( "?", "" + ( tempRowNo + 1 ) );
                            
                            sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                        else
                        {
                            sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                        }
                    }
                    catch( Exception e )
                    {
                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat2() ) );
                    }
                    
                    //System.out.println( "SType : " + sType + " OU Name : " + currentOrgUnit.getName() +  " DECode : " + deCodeString + "   TempStr : " + tempStr + " -- Row No " + tempRowNo  + " -- Col No " + tempColNo  );

                }
                
                //count1++;
            }// reportDesignIterator while loop end
            
            slno++;
            orgUnitCount++;
        }// organisationUnitIterator while loop end
        
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName() +" Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();       
        
        return SUCCESS;
    }
    
    
    
    private String getAggVal( String expression, Map<String, String> aggDeMap )
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

                replaceString = aggDeMap.get( replaceString );
                
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
                d = MathUtils.calculateExpression( buffer.toString() );
            }
            catch ( Exception e )
            {
                d = 0.0;
                resultValue = "";
            }
            
            resultValue = "" + (double) d;

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
    
    
    // for format the cell
    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }
}
