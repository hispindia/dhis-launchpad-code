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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateASHAFacilitatorFormat5ReportResultAction implements Action
{
    public static final String ASHA_FACILITATOR_DATASET = "ASHA Facilitator DataSet";//5.0
    public static final String CHC_GROUP_ID = "CHC_GROUP_ID";//6.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    /*
    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    */
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
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
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
    
    
    private OrganisationUnit organisationUnit;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        
        // Period Info
        selectedStartPeriod = periodService.getPeriod( selectedPeriodId );
        
        // OrgUnit and OrgUnit Group Info
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        Constant chcGroupConstant = constantService.getConstantByName( CHC_GROUP_ID );
        
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( (int) chcGroupConstant.getValue() );
        
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        /* Monthly Period Type */
        
        Constant dataSetConstant = constantService.getConstantByName( ASHA_FACILITATOR_DATASET );
        
        dataSet = dataSetService.getDataSet( (int) dataSetConstant.getValue() );
        
        sections = new ArrayList<Section>( dataSet.getSections() );
        
        Section section = sections.get( 0 );
        
        dataElements = new ArrayList<DataElement>( section.getDataElements() );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
        
        List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( organisationUnitGroup.getMembers() );
        
        orgUnitList.retainAll( orgUnitGroupMembers );
        
            
        // xls related information
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "FacilitatorFormat5", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        
        int rowStart = 0;
        int colStart = 0;
        
        sheet0.mergeCells( colStart , rowStart, colStart + 5 , rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "ASHA Facilitator Format 5 REPORT", getCellFormat1() ) );
        
        rowStart++;
        
        //rowStart++;
        
        sheet0.addCell( new Label( colStart, rowStart, "Name of Facility", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+1, rowStart, organisationUnit.getName(), getCellFormat3() ) );
        
        sheet0.addCell( new Label( colStart+2, rowStart, "Month/Year" , getCellFormat1() ) );
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        String monthYear = simpleDateFormat.format( selectedStartPeriod.getStartDate() );
        
        sheet0.addCell( new Label( colStart+3, rowStart, monthYear, getCellFormat3() ) );
        
        rowStart++;
       
        //rowStart++;
       
        colStart = 0;
        
        sheet0.mergeCells( colStart , rowStart, colStart, rowStart + 1 );
        sheet0.addCell( new Label( colStart, rowStart, "Sl.No", getCellFormat1() ) );
        
        sheet0.mergeCells( colStart + 1 , rowStart, colStart + 1, rowStart + 1 );
        sheet0.addCell( new Label( colStart+1, rowStart, "", getCellFormat1() ) );
        
        colStart = 2;
        for ( OrganisationUnit orgUnit : orgUnitList )
        {
            
            sheet0.mergeCells( colStart , rowStart, colStart + 1, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, orgUnit.getName() , getCellFormat4() ) );
            
            colStart++;
            colStart++;
        }
        
        rowStart++;
        colStart = 2;
        for ( @SuppressWarnings( "unused" ) OrganisationUnit orgUnit : orgUnitList )
        {
            sheet0.addCell( new Label( colStart, rowStart, "ASHA %" , getCellFormat4() ) );
            sheet0.setColumnView( colStart + 1 , 12 );
            sheet0.addCell( new Label( colStart+1, rowStart, "Block Grade" , getCellFormat4() ) );
            
            colStart++;
            colStart++;
        }
        
       
        
        rowStart++;
        colStart = 0;
        int slNo = 1;

        for( DataElement dataElement : dataElements )
        {
            colStart = 0;
            sheet0.addCell( new Number( colStart, rowStart, slNo, getCellFormat1() ) );
            sheet0.addCell( new Label( colStart+1, rowStart, dataElement.getFormNameFallback(), getCellFormat2() ) );
            
            colStart += 2;
            
            for ( OrganisationUnit orgUnit : orgUnitList )
            {

                List<OrganisationUnit> tempOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
                Collection<Integer> tempOrgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, tempOrgUnitList ) );
                String orgUnitIdsByComma = getCommaDelimitedString( tempOrgUnitIds );
                
                Map<Integer, Integer>yesPatientCountMap = new HashMap<Integer, Integer>( ashaService.getPatientYesCountByDataElement( selectedStartPeriod.getId(), dataSet.getId(), orgUnitIdsByComma  ) );
                
                //Integer patientCount = 0;
                Set<Integer> totalPatientInCHC = new HashSet<Integer>( ashaService.getPatientListByOrgunit( orgUnitIdsByComma ) );
                
                //System.out.println( orgUnit.getName() + " Temp Patient Count -- " + tempPatientIds.size() + "  Patient Count -- " + patientIds.size()  );
                
                String blockGrade = "";
                
                try
                {
                    double gradePercentage = ( (double) yesPatientCountMap.get( dataElement.getId() ) / (double) totalPatientInCHC.size() ) * 100.0;
                    
                    gradePercentage = Math.round( gradePercentage * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 ); 
                    
                    sheet0.addCell( new Number( colStart, rowStart, gradePercentage, getCellFormat3() ) );
                    
                    if( gradePercentage >= 76 )
                    {
                        blockGrade = "A";
                    }
                    
                    else if( gradePercentage >= 51 && gradePercentage <= 75 )
                    {
                        blockGrade = "B";
                    }
                    
                    else if( gradePercentage >= 26 && gradePercentage <= 50 )
                    {
                        blockGrade = "C";
                    }
                    
                    else
                    {
                        blockGrade = "D";
                    }
                    
                    sheet0.addCell( new Label( colStart+1, rowStart, blockGrade , getCellFormat4() ) );
                    
                    //System.out.println( orgUnit.getName() + " Patient Count -- " + performancePatientInCHC.size() + " Patient Size -- " + totalPatientInCHC.size() +  " Grade Percentage in try -- " + gradePercentage );
                    
                }
                catch( Exception e )
                {
                    sheet0.addCell( new Number( colStart, rowStart, 0, getCellFormat3() ) );
                    sheet0.addCell( new Label( colStart+1, rowStart, "" , getCellFormat4() ) );
                    //System.out.println( orgUnit.getName() + " Patient Count -- " + performancePatientInCHC.size() + " Patient Size -- " + totalPatientInCHC.size() +  " In catch ");
                }
                
                colStart++;
                colStart++;
            }
             
            slNo++;
            rowStart++;
        }
        
        rowStart++;
        
        
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

        fileName = "FacilitatorFormat5.xls";
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
    
    public WritableCellFormat getCellFormat4()
        throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        //wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( false );
        wCellformat.setShrinkToFit( true );
        return wCellformat;
    } 
    
    
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