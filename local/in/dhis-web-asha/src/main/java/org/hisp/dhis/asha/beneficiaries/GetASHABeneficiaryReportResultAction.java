package org.hisp.dhis.asha.beneficiaries;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetASHABeneficiaryReportResultAction implements Action
{
    public static final String ASHA_ACTIVITY_PROGRAM = "ASHA Activity Program";//1.0
    public static final String ASHA_ACTIVITY_PROGRAM_STAGE = "ASHA Activity Program Stage";//1.0
    private final String OPTION_SET_PAYMENT = "Payment";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BeneficiaryService beneficiaryService;

    public void setBeneficiaryService( BeneficiaryService beneficiaryService )
    {
        this.beneficiaryService = beneficiaryService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    /*
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    */
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
    
    /*private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    */
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
    
    public String getSelectedPeriodId()
    {
        return selectedPeriodId;
    }

    private Collection<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();

    public Collection<Beneficiary> getBeneficiaryList()
    {
        return beneficiaryList;
    }

    private String monthYear;
    
    public String getMonthYear()
    {
        return monthYear;
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
    
    private Collection<Patient> patients = new ArrayList<Patient>();
    
    public Collection<Patient> getPatients()
    {
        return patients;
    }
    
    private List<DataElement> paymentDataElementList;
    
    public List<DataElement> getPaymentDataElementList()
    {
        return paymentDataElementList;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // --------- ----------------------------------------------------------------

    public String execute() throws Exception
    {
        Patient patient = patientService.getPatient( id );

        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        /*
       
        patients = new ArrayList<Patient>( patientService.getPatients( patient.getOrganisationUnit(), null,null ) );
        
        
        Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers(Patient.class, patients ) );
        
        String patientIdsByComma = getCommaDelimitedString( patientIds );
        */
        
        //Map<String, String> patientAttributeValueMap = new HashMap<String, String>( ashaService.getPatientAttributeValues( patientIdsByComma ) );
        
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_PAYMENT );
        
        paymentDataElementList = new ArrayList<DataElement>();
        
        for( String optionName : optionSet.getOptions() )
        {
            DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( optionName ) );
            paymentDataElementList.add( dataElement );
        }
        
        /*
        Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers(DataElement.class, paymentDataElementList ) );
        
        String dataElementIdsByComma = getCommaDelimitedString( dataElementIds );
        
        dataElementIdsByComma += "," +159;
        */
        
        //Map<String, String> patientDataValueMap = new HashMap<String, String>( ashaService.getPatientDataValues( patientIdsByComma, dataElementIdsByComma, period.getStartDateString(), 1, 1 ));
        
        /*
        Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM );
        Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM_STAGE );
        
        Program program = programService.getProgram( (int) programConstant.getValue() );
        
        ProgramStage programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        */
        
        //System.out.println( patient.getFullName()  + "--" + period.getStartDateString() );

        //System.out.println( " Size of Beneficiary List --" + beneficiaryList.size() );
        
        beneficiaryList = new ArrayList<Beneficiary>( beneficiaryService.getAllBeneficiaryByASHAAndPeriod( patient,
            period ) );

        
        SimpleDateFormat simpleMonthYearFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );;
        
        monthYear = simpleMonthYearFormat.format( period.getStartDate() );
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );

        WritableSheet sheet0 = outputReportWorkbook.createSheet( "ASHABeneficiaryList", 0 );

        // Cell Format
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setWrap( false );

        
        int rowStart = 0;
        int colStart = 0;
        
        sheet0.mergeCells( colStart , rowStart, colStart + 8, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "ASHA SELF APPRASIAL FORM", getCellFormat1() ) );
        
        rowStart++;
        
        sheet0.addCell( new Label( colStart, rowStart, "Name", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+1, rowStart, patient.getFullName(), getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+4, rowStart, "Location", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+5, rowStart, patient.getOrganisationUnit().getName(), getCellFormat1() ) );
        
        
        sheet0.addCell( new Label( colStart+6, rowStart, "Month", getCellFormat1() ) );
        //sheet0.addCell( new Label( colStart+7, rowStart, monthYear, getCellFormat1() ) );
        sheet0.mergeCells( colStart+7 , rowStart, colStart + 8, rowStart );
        sheet0.addCell( new Label( colStart+7, rowStart, monthYear, getCellFormat1() ) );
        
        rowStart++;
        rowStart++;
        
        sheet0.addCell( new Label( colStart, rowStart, "Sl.No", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 1, rowStart, "Name of Beneficiary", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 2, rowStart, "Gender", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 3, rowStart, "Father's/Husband's Name", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 4, rowStart, "Beneficiary Identifier", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 5, rowStart, "Category", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 6, rowStart, "Services", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 7, rowStart, "Service Given Date", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 8, rowStart, "Price", getCellFormat1() ) );
        
        rowStart++;
        int slNo = 1;
        
        
        double totalAmount = 0.0;
        
        for ( Beneficiary beneficiary : beneficiaryList )
        {
            sheet0.addCell( new Number( colStart, rowStart, slNo, getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 1, rowStart, beneficiary.getName(), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 2, rowStart, beneficiary.getGender(), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 3, rowStart, beneficiary.getFatherName(), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 4, rowStart, beneficiary.getIdentifier(), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, beneficiary.getDataElementGroup().getName(), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, beneficiary.getDataElement().getName(), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, simpleDateFormat.format( beneficiary.getServiceGivenDate() ), getCellFormat2() ) );
            
            if ( beneficiary.getPrice() != null )
            {
                totalAmount += Double.parseDouble( beneficiary.getPrice() );
                //System.out.println( beneficiary.getPrice() + " -- Total Amount  " + totalAmount );
            }
            
            sheet0.addCell( new Number( colStart + 8, rowStart, Double.parseDouble( beneficiary.getPrice()), getCellFormat2() ) );
            
            slNo++;
            rowStart++;
        }
        
        sheet0.mergeCells( colStart , rowStart, colStart + 7, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "Total Amount", getCellFormat1() ) );
        sheet0.addCell( new Number( colStart + 8, rowStart, totalAmount, getCellFormat2() ) );
        
        rowStart++;
        rowStart++;
        
        sheet0.addCell( new Number( colStart , rowStart, 1 , getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 1, rowStart, "Verified activities of ASHA from Sr. No. 1 to ________.", getCellFormat3() ) );
        
        sheet0.addCell( new Number( colStart , rowStart+1, 2 , getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 1, rowStart+1, "Signature of the BAC/ DAC.", getCellFormat3() ) );
        
        sheet0.addCell( new Number( colStart , rowStart+2, 3 , getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 1, rowStart+2, "Signature of the Account Assistant", getCellFormat3() ) );
        
        sheet0.addCell( new Number( colStart , rowStart+3, 4 , getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 1, rowStart+3, "Signature of the MO I/c", getCellFormat3() ) );
        
        /*
        rowStart++;
        rowStart++;
        
        
        int colStart1 = 0;
        for ( Patient patient1 : patients )
        {
            String Village ="";
            Village =  patientAttributeValueMap.get( patient1.getId()+":"+6 );
            
            sheet0.addCell( new Number( colStart1, rowStart, slNo, getCellFormat1() ) );
            sheet0.addCell( new Label( colStart1 + 1, rowStart, patient1.getFullName(), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart1 + 2, rowStart, ""+patient1.getAge() ) );
            sheet0.addCell( new Label( colStart1 + 3, rowStart, patient1.getPhoneNumber(), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart1 + 4, rowStart, Village, getCellFormat2() ) );
            
            
            Double paymentDue = 0.0;
            paymentDue = Double.parseDouble( patientDataValueMap.get( patient1.getId()+":"+ 159 ));
            if( paymentDue != null )
            {
                paymentDue = Double.parseDouble( patientDataValueMap.get( patient1.getId()+":"+ 159 ));
            }
            
            System.out.println( " Payment Due  : " + paymentDue );
            
            Integer payableAmount = 0;
            payableAmount = Integer.parseInt( patientDataValueMap.get( patient1.getId()+":"+ 160 ));
            if( payableAmount != null )
            {
                payableAmount = Integer.parseInt( patientDataValueMap.get( patient1.getId()+":"+ 160 ));
            }
            
            Integer totalPayableAmount = 0;
            
            totalPayableAmount = Integer.parseInt( patientDataValueMap.get( patient1.getId()+":"+ 159 )) + Integer.parseInt( patientDataValueMap.get( patient1.getId()+":"+ 160 ));
            
            if( totalPayableAmount != null )
            {
                totalPayableAmount = Integer.parseInt( patientDataValueMap.get( patient1.getId()+":"+ 159 )) + Integer.parseInt( patientDataValueMap.get( patient1.getId()+":"+ 160 ));
            }
            
            Integer amountPaid = 0;
            amountPaid = Integer.parseInt( patientDataValueMap.get( patient1.getId()+":"+ 161 ));
            if( amountPaid != null )
            {
                amountPaid = Integer.parseInt( patientDataValueMap.get( patient1.getId()+":"+ 161 ));
            }
            
            
            //String paymentDue = "";
            
            //paymentDue =  patientDataValueMap.get( patient1.getId()+":"+159 );
            
            
            sheet0.addCell( new Number( colStart1 + 5, rowStart, paymentDue, getCellFormat2() ) );
            
            sheet0.addCell( new Number( colStart1 + 6, rowStart, payableAmount, getCellFormat2() ) );
            
            sheet0.addCell( new Number( colStart1 + 7, rowStart, totalPayableAmount , getCellFormat2() ) );
            sheet0.addCell( new Number( colStart1 + 8, rowStart, amountPaid, getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart1 + 9, rowStart,  patientDataValueMap.get( patient1.getId()+":"+ 162 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart1 + 10, rowStart,  patientDataValueMap.get( patient1.getId()+":"+ 163 ), getCellFormat2() ) );
           
            
            
            int newCol = 0;
            
            newCol = colStart1 + 6;
            
            System.out.println( colStart1  + "--" + newCol );
            
            int i = 0;
            for( DataElement dataElement : paymentDataElementList )
            {
                int col = newCol+i;
                sheet0.addCell( new Label( col, rowStart, patientDataValueMap.get( patient1.getId()+":"+ dataElement.getId() ), getCellFormat2() ) );
                
                System.out.println( rowStart  + " -- " +col  + "-- " + patientDataValueMap.get( patient1.getId()+":"+ dataElement.getId() ) );
                
                i++;
            }
           
            
            slNo++;
            rowStart++;
            
        }
        */
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "ASHABeneficiaryList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
    
        return SUCCESS;
    }
    // Excel sheet format function
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
    
    public WritableCellFormat getCellFormat3() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        //wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( false );
        //wCellformat.setShrinkToFit( true );
        return wCellformat;
    } // end getCellFormat1() function
    
    
    
    
    
}
