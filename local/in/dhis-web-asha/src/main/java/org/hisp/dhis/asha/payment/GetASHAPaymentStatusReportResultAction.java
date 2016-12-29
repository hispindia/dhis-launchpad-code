package org.hisp.dhis.asha.payment;

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
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetASHAPaymentStatusReportResultAction implements Action
{
    public static final String ASHA_ACTIVITY = "ASHA Activity";
    public static final String ASHA_ACTIVITY_PROGRAM_STAGE = "ASHA Activity Program Stage";//1.0
    private final String OPTION_SET_PAYMENT = "Payment";
    
    public static final String PHC_GROUP = "PHC Group";//25.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) 
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private int orgUnitId;
    
    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
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
    
    private OrganisationUnit selectedOrgUnit;
    
    private Program program;
    
    private List<OrganisationUnit > programSources = new ArrayList<OrganisationUnit>();
    
    private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
    
    private Period period ;
    
    private List<DataElement> paymentDataElementList = new ArrayList<DataElement>();
    
    private String dataElementIdsByComma;
    
    private SimpleDateFormat simpleDateFormat;
    
    private String monthYear;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // --------- ----------------------------------------------------------------


    public String execute() throws Exception
    {
        
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        organisationUnitService.getOrganisationUnitLevel( orgUnitId );
        
        int level = organisationUnitService.getLevelOfOrganisationUnit( orgUnitId );
        
        //System.out.println( level );
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
        
        // program Related info
        
        program = programService.getProgramByName( ASHA_ACTIVITY );
        programSources = new ArrayList<OrganisationUnit>( program.getOrganisationUnits() );
        
        if( program != null &&  programSources != null && programSources.size() > 0 )
        {
            orgUnitList.retainAll( programSources );
        }    
        
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        
        // period related info
        
        period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        // data element related information
        
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_PAYMENT );
        
        paymentDataElementList = new ArrayList<DataElement>();
        
        for( String optionName : optionSet.getOptions() )
        {
            DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( optionName ) );
            paymentDataElementList.add( dataElement );
        }
        
        dataElementIdsByComma = "-1";
        Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers(DataElement.class, paymentDataElementList ) );
        dataElementIdsByComma = getCommaDelimitedString( dataElementIds );
        
        dataElementIdsByComma += "," +159;
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        monthYear = simpleDateFormat.format( period.getStartDate() );
        
        System.out.println( selectedOrgUnit.getName() + " :  Report Generation Start Time is : " + new Date() );
        
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "ASHAPaymentStatus", 0 );
        
        sheet0.getSettings().setProtected( true );
        sheet0.getSettings().setPassword( "ashaHaryana" );
        
        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );
        
        int rowStart = 0;
        int colStart = 0;
        
        sheet0.mergeCells( colStart , rowStart, colStart + 12, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "ASHA PAYMENT STATUS REPORT", getCellFormat1() ) );
        
        rowStart++;
        
        sheet0.mergeCells( colStart , rowStart, colStart + 1, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "Selected Facility Name", getCellFormat1() ) );
        
        sheet0.mergeCells( colStart+2 , rowStart, colStart + 3, rowStart );
        sheet0.addCell( new Label( colStart+2, rowStart, selectedOrgUnit.getName(), getCellFormat2() ) );
        
        
        sheet0.addCell( new Label( colStart+11, rowStart, "Month", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+12, rowStart, monthYear, getCellFormat2() ) );
        
        
        
        rowStart++;
        sheet0.addCell( new Label( colStart, rowStart, "Sl.No", getCellFormat1() ) );
        
        //sheet0.setColumnView( colStart + 1 , 40 );
        //sheet0.addCell( new Label( colStart + 1, rowStart, "Hierarchy Organisation Unit", getCellFormat1() ) );
        
        if( level == 2 || level == 1 )
        {
            sheet0.addCell( new Label( colStart + 1, rowStart, "District", getCellFormat1() ) );
        }
        
        else if ( level == 3 )
        {
            sheet0.addCell( new Label( colStart + 1, rowStart, "Block", getCellFormat1() ) );
        }
        
        else if ( level == 4 )
        {
            sheet0.addCell( new Label( colStart + 1, rowStart, "PHC", getCellFormat1() ) );
        }
        
        else if ( level == 5 || level == 6 )
        {
            sheet0.addCell( new Label( colStart + 1, rowStart, "PHC/SC", getCellFormat1() ) );
        }
        
        
        //sheet0.setColumnView( colStart + 2 , 15 );
        sheet0.addCell( new Label( colStart + 2, rowStart, "Facility Name", getCellFormat1() ) );
        
        //sheet0.setColumnView( colStart + 3 , 18 );
        sheet0.addCell( new Label( colStart + 3, rowStart, "ASHA Name", getCellFormat1() ) );
        
        sheet0.addCell( new Label( colStart + 4, rowStart, "Age", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 5, rowStart, "Phone No", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 6, rowStart, "Village", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 7, rowStart, "Payment Due", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 8, rowStart, "Payable Amount", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 9, rowStart, "Total Payable Amount", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 10, rowStart, "Amount Paid", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 11, rowStart, "Cheque/Voucher Number", getCellFormat1() ) );
        
        //sheet0.setColumnView( colStart + 3 , 13 );
        sheet0.addCell( new Label( colStart + 12, rowStart, "Comments", getCellFormat1() ) );
        
        //rowStart++;
        rowStart++;
        
        int slNo = 1;
        
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            List<Patient> patientList = new ArrayList<Patient>( patientService.getPatients( orgUnit, null,null ) );
            
            if( patientList == null || patientList.size() == 0 ) 
            {
                //System.out.println( orgUnit.getName()  + " : Patient List is empty ");
                continue;
            }
            
            
            Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers( Patient.class, patientList ) );
            String patientIdsByComma = getCommaDelimitedString( patientIds );
            
             Map<String, String> patientAttributeValueMap = new HashMap<String, String>( ashaService.getPatientAttributeValues( patientIdsByComma ) );
             Map<String, String> patientDataValueMap = new HashMap<String, String>( ashaService.getPatientDataValues( patientIdsByComma, dataElementIdsByComma, period.getStartDateString(), 1, 1 ));
             
             Collections.sort( patientList, new ASHANameComparator() );
             
             for ( Patient patient : patientList )
             {
                 String Village ="";
                 Village =  patientAttributeValueMap.get( patient.getId() + ":" + 6 );
                 
                 sheet0.addCell( new Number( colStart, rowStart, slNo, getCellFormat1() ) );
                 
                 //getHierarchyOrgunit( orgUnit );
                 
                 //sheet0.addCell( new Label( colStart + 1, rowStart, getHierarchyOrgunit( orgUnit ), getCellFormat2() ) );
                 
                 if( level == 2 || level == 1 )
                 {
                     sheet0.addCell( new Label( colStart + 1, rowStart, orgUnit.getParent().getParent().getParent().getName(), getCellFormat2() ) );
                 }
                 
                 else if ( level == 3 )
                 {
                     sheet0.addCell( new Label( colStart + 1, rowStart, orgUnit.getParent().getParent().getName(), getCellFormat2() ) );
                 }
                 
                 else if ( level == 4 )
                 {
                     sheet0.addCell( new Label( colStart + 1, rowStart, orgUnit.getParent().getName(), getCellFormat2() ) );
                 }
                 
                 else if ( level == 5 || level == 6 )
                 {
                     sheet0.addCell( new Label( colStart + 1, rowStart, orgUnit.getName(), getCellFormat2() ) );
                 }
                 
                 sheet0.addCell( new Label( colStart + 2, rowStart, orgUnit.getName(), getCellFormat2() ) );
                 
                 sheet0.addCell( new Label( colStart + 3, rowStart, patient.getFullName(), getCellFormat2() ) );
                 sheet0.addCell( new Label( colStart + 4, rowStart, patient.getAge(),getCellFormat2() ) );
                 sheet0.addCell( new Label( colStart + 5, rowStart, patient.getPhoneNumber(), getCellFormat2() ) );
                 sheet0.addCell( new Label( colStart + 6, rowStart, Village, getCellFormat2() ) );
                 
                 Double paymentDue = 0.0;
                
                 String tempPaymentDue = patientDataValueMap.get( patient.getId()+":"+ 159 );
                 if( tempPaymentDue != null )
                 {
                     try
                     {
                         paymentDue = Double.parseDouble( tempPaymentDue );
                     }
                     catch ( Exception e )
                     {
                         
                     }
                 }
                 
                 sheet0.addCell( new Number( colStart + 7, rowStart, paymentDue, getCellFormat2() ) );
                 
                 Double payableAmount = 0.0;
                 String tempPayableAmount =  patientDataValueMap.get( patient.getId()+":"+ 160 );
                 if( tempPayableAmount != null )
                 {
                     try
                     {
                         payableAmount = Double.parseDouble( tempPayableAmount );
                     }
                     catch ( Exception e )
                     {
                         
                     }
                 }
                 
                 sheet0.addCell( new Number( colStart + 8, rowStart, payableAmount, getCellFormat2() ) );
                 sheet0.addCell( new Number( colStart + 9, rowStart, paymentDue + payableAmount , getCellFormat2() ) );
                 
                 Double amountPaid = 0.0;
                 String tempAmountPaid =  patientDataValueMap.get( patient.getId()+":"+ 161 );
                 if( tempAmountPaid != null )
                 {
                     try
                     {
                         amountPaid = Double.parseDouble( tempAmountPaid );
                     }
                     catch ( Exception e )
                     {
                         
                     }
                 }
                 
                 sheet0.addCell( new Number( colStart + 10, rowStart, amountPaid, getCellFormat2() ) );
                 
                 sheet0.addCell( new Label( colStart + 11, rowStart,  patientDataValueMap.get( patient.getId()+":"+ 162 ), getCellFormat2() ) );
                 sheet0.addCell( new Label( colStart + 12, rowStart,  patientDataValueMap.get( patient.getId()+":"+ 163 ), getCellFormat2() ) );
                 
                 slNo++;
                 rowStart++;
             }
             
             rowStart++;
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "ASHAPaymentStatus.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
        
        System.out.println( selectedOrgUnit.getName() + " :  Report Generation End Time is : " + new Date() );
        
        return SUCCESS;
    }
    
    /*
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        //String hierarchyOrgunit = orgunit.getName();
        String hierarchyOrgunit = "";
       
        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + "/" + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }
        
        hierarchyOrgunit = hierarchyOrgunit.substring( hierarchyOrgunit.indexOf( "/" ) + 1 );
        
        return hierarchyOrgunit;
    }
    
    */
    
    
    // Excel sheet format function
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        //wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( true );
        wCellformat.setShrinkToFit( true );
        return wCellformat;
    } // end getCellFormat1() function
    
    
    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setShrinkToFit( true );
        wCellformat.setWrap( true );
    
        return wCellformat;
    }
    
    public WritableCellFormat getCellFormat3() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.NO_BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        //WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.TOP );
        //wCellformat.setBackground( Colour.GRAY_25 );
        //wCellformat.setWrap( true );
        //wCellformat.setShrinkToFit( true );
        return wCellformat;
    } // end getCellFormat1() function
    
}
