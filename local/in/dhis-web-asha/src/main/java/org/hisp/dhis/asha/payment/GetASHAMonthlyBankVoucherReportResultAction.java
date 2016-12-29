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



import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.asha.util.ReportCell;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetASHAMonthlyBankVoucherReportResultAction implements Action
{
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//4.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
   
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
   
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }

    private ProgramService programService;
    
    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    /*
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    */
    
    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
   
    /*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    
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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception 
    {
        String raFolderName = "ra_haryana_asha";
        
        System.out.println(  " Report Generation Start Time is : " + new Date() );
        
        SimpleDateFormat simpleDate = new SimpleDateFormat( "dd-MM-yyyy" );
        
        Period period = periodService.getPeriodByExternalId( selectedPeriodId );
        
        // OrgUnit Related Info
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId ); 
        
        String voucherNo = "";
        
        /*
        if( organisationUnit.getCode() != null )
        {
            voucherNo = organisationUnit.getCode();
        }
        else
        {
            voucherNo = organisationUnit.getName();
        }
        */
        
        voucherNo = organisationUnit.getId()+"/";
        
        
        //System.out.println(  " Voucher No : " + voucherNo );
        
        String suffix = selectedPeriodId.split( "-" )[0] + selectedPeriodId.split( "-" )[1];
        suffix = suffix.split( "_" )[1];
        
        //System.out.println(  " suffix : " + suffix );
        
        voucherNo = voucherNo + suffix;
        
        //System.out.println(  " Voucher No : " + voucherNo );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
        
        Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_ID );
        
        Program program = programService.getProgram( (int) programConstant.getValue() );
        
        Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID );
        
        // program and programStage Related information
        program = programService.getProgram( (int) programConstant.getValue() );

        ProgramStage programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        
        List<OrganisationUnit > programSources = new ArrayList<OrganisationUnit>();
        
        programSources = new ArrayList<OrganisationUnit>( program.getOrganisationUnits() );
        
        if( program != null &&  programSources != null && programSources.size() > 0 )
        {
            orgUnitList.retainAll( programSources );
        }    
        
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        
        String organisationUnitIdsByComma = "-1";
        Collection<Integer> organisationUnitIds = new ArrayList<Integer>();
        if( orgUnitList != null && orgUnitList.size() > 0 )
        {
            organisationUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitList ) );
            organisationUnitIdsByComma = getCommaDelimitedString( organisationUnitIds );
        }
        
        
        //String paymentStatusdataElementIdsByComma = "508";
        String paymentStatusdataElementIdsByComma = "507";
        //List<Patient> ashaList = new ArrayList<Patient>( ashaService.getPaymentDoneAAHAList( organisationUnitIdsByComma, paymentStatusdataElementIdsByComma, period.getStartDateString(), program.getId(), programStage.getId() ) );
        List<Patient> ashaList = new ArrayList<Patient>( ashaService.getPaymentDoneASHAListApproveByAA( organisationUnitIdsByComma, paymentStatusdataElementIdsByComma, period.getStartDateString(), program.getId(), programStage.getId() ) );
        
        
        /*
        List<Patient> ashaList = new ArrayList<Patient>();
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            List<Patient> patientList = new ArrayList<Patient>( patientService.getPatients( orgUnit, null,null ) );
            
            if( patientList != null && patientList.size() > 0 ) 
            {
                //System.out.println( orgUnit.getName()  + " : Patient List Size is : " + patientList.size() );
                ashaList.addAll( patientList );
            }
        }
        */
        
        String patientIdsByComma = "-1";
        Collection<Integer> patientIds = new ArrayList<Integer>();
        if( ashaList != null && ashaList.size() > 0 )
        {
            patientIds = new ArrayList<Integer>( getIdentifiers( Patient.class, ashaList ) );
            patientIdsByComma = getCommaDelimitedString( patientIds );
        }
        
        Map<String, String> patientAttributeValueMap = new HashMap<String, String>();
        patientAttributeValueMap = new HashMap<String, String>( ashaService.getPatientAttributeValues( patientIdsByComma ) );
       
        Map<String, String> patientDataValueMap = new HashMap<String, String>();
        
        String dataElementIdsByComma = "507";
        
        if( patientIds != null &&  patientIds.size() > 0 )
        {
            if( program != null &&  programStage != null )
            {
                patientDataValueMap = ashaService.getPatientDataValuesByExecutionDate( patientIdsByComma, program.getId(), programStage.getId(), period.getStartDateString(), dataElementIdsByComma  );
            }
            
        }
        
        String xmlFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xml" + File.separator + "BankVoucher.xml";
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xls" + File.separator + "BankVoucher.xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        List<ReportCell> fixedcells = new ArrayList<ReportCell>( ashaService.getReportCells( xmlFilePath, "fixedcells" ) );
        List<ReportCell> dynamiccells = new ArrayList<ReportCell>( ashaService.getReportCells( xmlFilePath, "dynamiccells" ) );
        
        
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        
        outputReportWorkbook.setProtected( true );
        
        WritableSheet sheet = outputReportWorkbook.getSheet( 0 );
        
        sheet.getSettings().setProtected( true );
        sheet.getSettings().setPassword( "ashaHaryana" );
        
        
        File outputReportFile = null;
        
        for( ReportCell fixedcell : fixedcells )
        {
            String tempStr = "";
            
            //String deCodeString = fixedcell.getExpression();
            
            if( fixedcell.getDatatype().equalsIgnoreCase( "SYSNUMBER" ) )
            {
                tempStr = "Payment Advice No. :" + voucherNo;
            }
            
            else if( fixedcell.getDatatype().equalsIgnoreCase( "PRINTDATE" ) )
            {
                tempStr = "Print Date : "+ simpleDate.format( new Date() );
            }

            sheet.addCell( new Label( fixedcell.getCol(), fixedcell.getRow(), tempStr, getCellFormat1() ) );
        } 
            
        
        // for dynamic cell
        
        int slNo = 1;
        int rowCount = 0;
        int rowStart = 0;
        int temp = 0;
        int tempvalue = 0;
        
        for( Patient asha : ashaList )
        {
            for( ReportCell dynamiccell : dynamiccells )
            {
                String value = "";
                
                if( dynamiccell.getDatatype().equalsIgnoreCase( "SLNO" ) )
                {
                    value = "" + slNo;
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "CHC" ) )
                {
                    value = asha.getOrganisationUnit().getParent().getParent().getName();
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "PHC" ) )
                {
                    value = asha.getOrganisationUnit().getParent().getName();
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "SC" ) )
                {
                    value = asha.getOrganisationUnit().getName();
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "PATIENTATTRIBUTE" ) )
                {
                    value = patientAttributeValueMap.get( asha.getId() +":" + Integer.parseInt( dynamiccell.getService() )  );
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "ASHAFULLNAME" ) )
                {
                    value = asha.getFullName();
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "DATAELEMENT" ) )
                {
                    value = patientDataValueMap.get( asha.getId() +":" + Integer.parseInt( dynamiccell.getService() ) );
              
                    try
                    {
                        tempvalue = Integer.parseInt( value );
                    }
                    catch ( Exception e )
                    {
                        tempvalue = 0;
                       
                    }
                    
                    temp += tempvalue;
                    
                    //System.out.println(  " : temp  : " + temp );
                }
                    
                try
                {
                    //sheet.addCell( new Number( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, Integer.parseInt( value ), getCellFormat2() ) );
                    
                    sheet.addCell( new Number( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, Integer.parseInt( value ), getCellFormat2() ) );
                }
                catch ( Exception e )
                {
                    sheet.addCell( new Label( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, value, getCellFormat2() ) ); 
                }
            }    
                //sheet.addCell( new jxl.write.Number( c, r, val, st ) );
                
                //sheet.addCell( new Label( dynamiccell.getColno(), dynamiccell.getRowno()+rowCount, value, getCellFormat2() ) );
            rowCount++;
            slNo++;
            rowStart = rowCount;
    
        } 
        
        //System.out.println(  " rowCount : " + rowCount  +  " rowStart : " + rowStart );
        
        // for total amount
        sheet.mergeCells( 1 , rowStart + 12, 10, rowStart + 12 );
        sheet.addCell( new Label( 1,rowStart + 12, "Total", getCellFormat3() ) ); 
        sheet.addCell( new Number( 11,rowStart + 12, temp, getCellFormat2() ) );
        
        // for another information
        
        //System.out.println(  " 2 rowCount : " + rowCount  +  " 2 rowStart : " + rowStart );
        
        sheet.mergeCells( 1, rowStart + 14, 5, rowStart + 14 );
        sheet.addCell( new Label( 1, rowStart + 14, "Please do the needful and confirm", getCellFormat1() ) );
        
        
        sheet.mergeCells( 7 , rowStart + 14, 11, rowStart + 14 );
        sheet.addCell( new Label( 7, rowStart + 14, " ", getCellFormat1() ) );        
        
 
        sheet.mergeCells( 1 , rowStart + 19, 5, rowStart + 19 );
        sheet.addCell( new Label( 1, rowStart + 19, "(Authorized signatory)", getCellFormat1() ) );
        
        
        sheet.mergeCells( 7 , rowStart + 19, 11, rowStart + 19 );
        sheet.addCell( new Label( 7, rowStart + 19, "(Authorized signatory)", getCellFormat1() ) );         
        
        
        sheet.mergeCells( 1 , rowStart + 20, 5, rowStart + 20 );
        sheet.addCell( new Label( 1, rowStart + 20, "Name - .........................................................", getCellFormat1() ) );
        
        
        sheet.mergeCells( 7 , rowStart + 21, 11, rowStart + 21 );
        sheet.addCell( new Label( 7, rowStart + 21, "Name - .........................................................", getCellFormat1() ) );    
        
        
        sheet.mergeCells( 1 , rowStart + 22, 5, rowStart + 22 );
        sheet.addCell( new Label( 1, rowStart + 22, "Designation - ...................................................", getCellFormat1() ) );
        
        
        sheet.mergeCells( 7 , rowStart + 23, 11, rowStart + 23 );
        sheet.addCell( new Label( 7, rowStart + 23, "Designation - ...................................................", getCellFormat1() ) );        
        
        
        sheet.mergeCells( 1 , rowStart + 24, 5, rowStart + 24 );
        sheet.addCell( new Label( 1, rowStart + 24, "Mobile No. - ......................................................", getCellFormat1() ) );
        
        
        sheet.mergeCells( 7 , rowStart + 25, 11, rowStart + 25 );
        sheet.addCell( new Label( 7, rowStart + 25, "Mobile No. - ......................................................", getCellFormat1() ) );         
        
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();
        
      
        fileName = "BankAdviceReport" + ".xls";
        outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        
        //PdfSaveOptions options = new PdfSaveOptions(SaveFormat.PDF);
        //outputReportWorkbook.save( outputReportFile + fileName +".pdf", SaveFormat.PDF);
        
        outputReportFile.deleteOnExit();
   
        System.out.println(  " Report Generation End Time is : " + new Date() );
        
        return SUCCESS;
    }
    
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.NO_BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        wCellformat.setBorder( Border.NONE, BorderLineStyle.NONE );
        wCellformat.setAlignment( Alignment.LEFT );
        //wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( false );
        //wCellformat.setShrinkToFit( true );
        return wCellformat;
    }
    
    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setShrinkToFit( true );
        wCellformat.setWrap( true );
    
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
    }
}

