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

import org.hisp.dhis.asha.comparator.ASHANameComparator;
import org.hisp.dhis.asha.util.ASHAService;
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
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetASHAFacilitatorFormat1ReportResultAction implements Action
{
    public static final String PATIENT_ATTRIBUTE_VILLAGE = "Village Attribute";//6.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
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
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    public Integer getId()
    {
        return id;
    } 
    
    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private int orgUnitId;
    
    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
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
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
    
    private Facilitator facilitator;
    
    private List<Section> sections;
    
    private List<Patient> patients; 
    
    private List<DataElement> dataElements;
    
    public Map<String, String> facilitatorDataValueMap;
    
    public Map<String, String> patientAttributeValueMap;
    
    String patientIdsByComma;
    
    private OrganisationUnit selectedOrgUnit;
    
    private String monthYear;
    
    private SimpleDateFormat simpleDateFormat;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        facilitator = facilitatorService.getFacilitator( id );
        
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        Period period = periodService.getPeriodByExternalId( selectedPeriodId );
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        sections = new ArrayList<Section>( dataSet.getSections() );
        
        patients = new ArrayList<Patient>();
        
        patients = new ArrayList<Patient>( facilitator.getPatients() );
        
        facilitatorDataValueMap = new HashMap<String, String>();
        
        facilitatorDataValueMap = new HashMap<String, String>( ashaService.getfacilitatorDataValues( facilitator.getId(), period.getId(), dataSet.getId() ) );
        
        Section section = sections.get( 0 );
        
        dataElements = new ArrayList<DataElement>();
        
        dataElements = new ArrayList<DataElement>( section.getDataElements() );
        
        Collections.sort( patients, new ASHANameComparator() );
        
        patientIdsByComma = "-1";
        
        if( patients != null && patients.size() > 0 )
        {
            Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers( Patient.class, patients ) );
            patientIdsByComma = getCommaDelimitedString( patientIds );
        }
        
        Constant constant = constantService.getConstantByName( PATIENT_ATTRIBUTE_VILLAGE );
        
        int attributeId = (int) constant.getValue();
        
        patientAttributeValueMap = new HashMap<String, String>();
        
        patientAttributeValueMap = new HashMap<String, String>( ashaService.getPatientAttributeValues( patientIdsByComma, attributeId ) );
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "FacilitatorFormat1", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        
        int rowStart = 0;
        int colStart = 0;
        
        sheet0.mergeCells( colStart , rowStart, colStart + patients.size()+2, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "ASHA Facilitator Format 1 REPORT", getCellFormat1() ) );
        
        rowStart++;
        
        sheet0.addCell( new Label( colStart, rowStart, "Name of Facility", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+1, rowStart, selectedOrgUnit.getName(), getCellFormat3() ) );
        
        sheet0.addCell( new Label( colStart+2, rowStart, "Name of Facilitator", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+3, rowStart, facilitator.getName(), getCellFormat3() ) );
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthYear = simpleDateFormat.format( period.getStartDate() );
        
        
        sheet0.addCell( new Label( colStart + patients.size()+1, rowStart, "Month/Year", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + patients.size()+2, rowStart, monthYear, getCellFormat3() ) );
        
        
        rowStart++;
        rowStart++;
        
        sheet0.addCell( new Label( colStart, rowStart, "Sl.No", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+1, rowStart, "Indicators", getCellFormat1() ) );
        
        colStart = 2;
        for ( Patient patient : patients )
        {
            String Village =" ";
            
            if( patientAttributeValueMap.get( patient.getId() + ":" + attributeId ) != null)
            {
                Village =  patientAttributeValueMap.get( patient.getId() + ":" + attributeId );
            }
            
            sheet0.addCell( new Label( colStart, rowStart, patient.getFullName() + "--" + Village , getCellFormat1() ) );
            
            colStart++;
        }
        
        sheet0.addCell( new Label( colStart, rowStart, "Total Active ASHA" , getCellFormat1() ) );
        
        rowStart++;
        colStart = 0;
        int slNo = 1;
        
        //System.out.println( colStart  + " Col Start " );
        Map<Integer, Integer> patientCountMap = new HashMap<Integer,Integer>();
        for( DataElement dataElement : dataElements )
        {
            colStart = 0;
            sheet0.addCell( new Number( colStart, rowStart, slNo, getCellFormat1() ) );
            sheet0.addCell( new Label( colStart+1, rowStart, dataElement.getFormNameFallback(), getCellFormat2() ) );
            colStart += 2;
            
            int yesCount = 0;
            
            //colStart = 2;
            for ( Patient patient : patients )
            {
                if ( facilitatorDataValueMap.get( dataElement.getId() + ":" + patient.getId() ) != null && facilitatorDataValueMap.get( dataElement.getId() + ":" + patient.getId() ).equalsIgnoreCase( "Yes" ))
                {
                    yesCount++;
                    Integer patientCount = patientCountMap.get( patient.getId() );
                    if( patientCount == null )
                    {
                        patientCount = 1;
                        patientCountMap.put( patient.getId(), patientCount );
                    }
                    else
                    {
                        patientCountMap.put( patient.getId(), patientCount+1 );
                    }
                }
                
                sheet0.addCell( new Label( colStart, rowStart, facilitatorDataValueMap.get( dataElement.getId() + ":" + patient.getId() ), getCellFormat3() ) );
                
                colStart++;
            }
            
            sheet0.addCell( new Number( colStart, rowStart, yesCount, getCellFormat1() ) );
            
            
            slNo++;
            rowStart++;
        }
        
        /*
        for( Integer patientId : patientCountMap.keySet() )
        {
            System.out.println( patientId + " : " + patientCountMap.get( patientId ) );
        }
        */
        
        colStart = 0;
        sheet0.mergeCells( colStart , rowStart, colStart + 1, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "Total Active ASHA", getCellFormat1() ) );
        
        int totalCount = 0;
        colStart = 2;
        for ( Patient patient : patients )
        {
            Integer patientCount = patientCountMap.get( patient.getId() );
            if( patientCount == null ) patientCount = 0;
            
            totalCount += patientCount;
            
            sheet0.addCell( new Number( colStart, rowStart, patientCount, getCellFormat1() ) );
            colStart++;
        }
        
        sheet0.addCell( new Number( colStart, rowStart, totalCount, getCellFormat1() ) );
        
        
        
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "FacilitatorFormat1 .xls";
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
