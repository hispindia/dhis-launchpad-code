package org.hisp.dhis.asha.facilitator;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateASHAFacilitatorFormat4ReportResultAction implements Action
{
    public static final String ASHA_FACILITATOR_DATASET = "ASHA Facilitator DataSet";//5.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) 
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    /*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private int selectedPeriodId;
    
    public void setSelectedPeriodId( int selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }
    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    private Period selectedStartPeriod;
    
    private SimpleDateFormat simpleDateFormat;

    
    private DataSet dataSet;
    
    private List<Section> sections;
    
    private List<DataElement> dataElements;
    
    
    private List<Facilitator> facilitators = new ArrayList<Facilitator>();
    
    public List<Facilitator> getFacilitators()
    {
        return facilitators;
    }
    
    private OrganisationUnit organisationUnit;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        //facilitator = facilitatorService.getFacilitator( facilitatorId );
        
        // Period Info
        selectedStartPeriod = periodService.getPeriod( selectedPeriodId );
        
        
        
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        
       // List<Facilitator> facilitatorList = new ArrayList<Facilitator>();
        
        for( OrganisationUnit orgUnit : organisationUnit.getChildren() )
        {
            List<Facilitator> facilitatorList = new ArrayList<Facilitator> ( facilitatorService.getFacilitatorByOrganisationUnit( orgUnit ) );
            
            if( facilitatorList != null )
            {
                facilitators.addAll( facilitatorList );
            }
        }
        
        Collections.sort( facilitators, new IdentifiableObjectNameComparator() );
        
        /*
        for( Facilitator facilitator : facilitators )
        {
            System.out.println( facilitator.getId() + " : " + facilitator.getName() );
            
           
        }
        */
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        /* Monthly Period Type */
        
       
        Collection<Integer> facilitatorIds = new ArrayList<Integer>( getIdentifiers( Facilitator.class, facilitators ) );       
        String facilitatorIdsByComma = getCommaDelimitedString( facilitatorIds );
        
        Constant dataSetConstant = constantService.getConstantByName( ASHA_FACILITATOR_DATASET );
        
        dataSet = dataSetService.getDataSet( (int) dataSetConstant.getValue() );
        
        sections = new ArrayList<Section>( dataSet.getSections() );
        
        Section section = sections.get( 0 );
        
        dataElements = new ArrayList<DataElement>( section.getDataElements() );
        
        Map<String, Integer> yesCountMap = new HashMap<String, Integer>( ashaService.getYesCountSumForFacilitator( facilitatorIdsByComma, ""+selectedStartPeriod.getId(), dataSet.getId() ) );
        
        
        // xls related information
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "FacilitatorFormat4", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        
        int rowStart = 0;
        int colStart = 0;
        
        sheet0.mergeCells( colStart , rowStart, colStart + facilitators.size()+2, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "ASHA Facilitator Format 4 REPORT", getCellFormat1() ) );
        
        rowStart++;
        
        rowStart++;
        
        sheet0.addCell( new Label( colStart, rowStart, "Name of Facility", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+1, rowStart, organisationUnit.getName(), getCellFormat3() ) );
        
        sheet0.addCell( new Label( colStart+2, rowStart, "Month/Year" , getCellFormat1() ) );
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        String monthYear = simpleDateFormat.format( selectedStartPeriod.getStartDate() );
        
        sheet0.addCell( new Label( colStart+3, rowStart, monthYear, getCellFormat3() ) );
        
      
        rowStart++;
        rowStart++;
        
        
        sheet0.addCell( new Label( colStart, rowStart, "Sl.No", getCellFormat1() ) );
        
        sheet0.addCell( new Label( colStart+1, rowStart, "", getCellFormat1() ) );
        
        colStart = 2;
        for ( Facilitator facilitator : facilitators )
        {
            sheet0.addCell( new Label( colStart, rowStart, facilitator.getName() , getCellFormat1() ) );
            colStart++;
        }
        
        sheet0.addCell( new Label( colStart, rowStart, organisationUnit.getName() +" Total" , getCellFormat1() ) );
        
        rowStart++;
        
        colStart = 0;
        int slNo = 1;
        
        //Map<Integer, Integer> periodCountMap = new HashMap<Integer,Integer>();
        //Map<Integer, Integer> totalAverageMap = new HashMap<Integer,Integer>();
        
        //Integer totalAverageValue = 0;
        
        for( DataElement dataElement : dataElements )
        {
            colStart = 0;
            sheet0.addCell( new Number( colStart, rowStart, slNo, getCellFormat1() ) );
            sheet0.addCell( new Label( colStart+1, rowStart, dataElement.getFormNameFallback(), getCellFormat2() ) );
            colStart += 2;
            
            //int yesCount = 0;
            int totalValue = 0;
            //colStart = 2;
            for ( Facilitator facilitator : facilitators )
            {

                String Key = facilitator.getId() + ":" + dataElement.getId() + ":" + selectedStartPeriod.getId();
                
                //String value = "";
                
                if( yesCountMap.get( Key ) !=null )
                {
                    totalValue += yesCountMap.get( Key );
                    
                    sheet0.addCell( new Number( colStart, rowStart, yesCountMap.get( Key ), getCellFormat3() ) );
                    
                }
                else
                {
                    sheet0.addCell( new Label( colStart, rowStart, "", getCellFormat3() ) );
                }
                
                //Integer value = yesCountMap.get( Key );
                
                //sheet0.addCell( new Label( colStart, rowStart, value, getCellFormat3() ) );
                
                colStart++;
            }
             
            sheet0.addCell( new Number( colStart, rowStart, totalValue, getCellFormat1() ) );
            
            slNo++;
            rowStart++;
        }
        
        rowStart++;
        
        colStart = 0;
        
        sheet0.mergeCells( colStart , rowStart, colStart + 1, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "Total ASHA", getCellFormat1() ) );
        
        colStart = 2;
        int totalASHACount = 0;
        
        for ( Facilitator facilitator : facilitators )
        {
            totalASHACount += facilitator.getPatients().size();
            sheet0.addCell( new Number( colStart, rowStart, facilitator.getPatients().size(), getCellFormat1() ) );
            
            colStart++;
            
            sheet0.addCell( new Number( colStart, rowStart, totalASHACount, getCellFormat1() ) );
        }
        
        
        
        //sheet0.addCell( new Number( colStart, rowStart, totalAverageValue, getCellFormat1() ) );
        
        
        
        
        /*
        for( Facilitator facilitator : facilitators )
        {
            for( Period p : periodList )
            {
                for( DataElement de : dataElements )
                {
                    String Key = facilitator.getId() + ":" + de.getId() + ":" + p.getId();
                    
                    Integer value = yesCountMap.get( Key );
                    
                    System.out.println( Key + " : " + value );
                }
            }
        }
        */
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "FacilitatorFormat4.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
        
        
        
        return SUCCESS;
    }
    
    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        //wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( false );
        //wCellformat.setShrinkToFit( true );
        return wCellformat;
    } // end getCellFormat1() function
    
    
    public WritableCellFormat getCellFormat2()throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        //wCellformat.setShrinkToFit( true );
        wCellformat.setWrap( false );
    
        return wCellformat;
    }
    
    public WritableCellFormat getCellFormat3()throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        //wCellformat.setShrinkToFit( true );
        wCellformat.setWrap( false );
    
        return wCellformat;
    }    
    
    
}


