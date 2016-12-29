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
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

public class GenerateASHABudgetUtilizationStatusReportResultAction implements Action
{
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//4.0
    
    private final String OPTION_SET_PAYMENT_STATUS_REPORT = "PaymentStatusReport";
    
    private final String OPTION_SET_BUDGET_UTILIZATION_DATAELEMENT_IDS = "BudgetUtilization";
    
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
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
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
    
    private SimpleDateFormat simpleDateFormat;
    
    private String monthYear;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception 
    {
        String raFolderName = "ra_haryana_asha";
        
        System.out.println(  " Report Generation Start Time is : " + new Date() );
        
        //SimpleDateFormat simpleDate = new SimpleDateFormat( "dd-MM-yyyy" );
        
        Period period = periodService.getPeriodByExternalId( selectedPeriodId );
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        monthYear = simpleDateFormat.format( period.getStartDate() );
        
        
        // OrgUnit Related Info
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId ); 
        
 
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
        
        /*
        String organisationUnitIdsByComma = "-1";
        Collection<Integer> organisationUnitIds = new ArrayList<Integer>();
        if( orgUnitList != null && orgUnitList.size() > 0 )
        {
            organisationUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnitList ) );
            organisationUnitIdsByComma = getCommaDelimitedString( organisationUnitIds );
        }
        */
        
        //String paymentStatusdataElementIdsByComma = "508";
        //String paymentStatusdataElementIdsByComma = "507";
        //List<Patient> ashaList = new ArrayList<Patient>( ashaService.getPaymentDoneAAHAList( organisationUnitIdsByComma, paymentStatusdataElementIdsByComma, period.getStartDateString(), program.getId(), programStage.getId() ) );
        //List<Patient> ashaList = new ArrayList<Patient>( ashaService.getPaymentDoneASHAListApproveByAA( organisationUnitIdsByComma, paymentStatusdataElementIdsByComma, period.getStartDateString(), program.getId(), programStage.getId() ) );
        
        
        
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
        Map<Integer, String> budgetUtilizationPatientDataValueMap = new HashMap<Integer, String>();
        
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_PAYMENT_STATUS_REPORT );
        
        String dataElementIdsByComma = "-1";
        
        for( String optionName : optionSet.getOptions() )
        {
            dataElementIdsByComma += "," + optionName;
        }
        
        OptionSet optionBudgetUtilizationSet = optionService.getOptionSetByName( OPTION_SET_BUDGET_UTILIZATION_DATAELEMENT_IDS );
        
        String budgetUtilizationDataElementIdsByComma = "-1";
        
        for( String optionName : optionBudgetUtilizationSet.getOptions() )
        {
            budgetUtilizationDataElementIdsByComma += "," + optionName;
        }
        
       
        //System.out.println(  " : dataElementIdsByComma : " + dataElementIdsByComma );
       
        if( patientIds != null &&  patientIds.size() > 0 )
        {
            if( program != null &&  programStage != null )
            {
                patientDataValueMap = ashaService.getPatientDataValuesByExecutionDate( patientIdsByComma, program.getId(), programStage.getId(), period.getStartDateString(), dataElementIdsByComma  );
                
                budgetUtilizationPatientDataValueMap = ashaService.getSumAmountFromPatientDataValuesByExecutionDate( patientIdsByComma, program.getId(), programStage.getId(), period.getStartDateString(), budgetUtilizationDataElementIdsByComma  );
            }
            
        }
        
        Map<String, Double> ashaServiceApproveAmountMap = new HashMap<String, Double>();
        
        ashaServiceApproveAmountMap = ashaService.getASHAServiceWiseFinalApproveAmount( period.getId() );
        
        
        String xmlFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xml" + File.separator + "ASHABudgetUtilizationStatusReport.xml";
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xls" + File.separator + "ASHABudgetUtilizationStatusReport.xls";
        
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
        WritableSheet sheet = outputReportWorkbook.getSheet( 0 );
        
        sheet.getSettings().setProtected( true );
        sheet.getSettings().setPassword( "ashaHaryana" );
        
        File outputReportFile = null;
        
        for( ReportCell fixedcell : fixedcells )
        {
            String tempStr = "";
            
            //String deCodeString = fixedcell.getExpression();
            
            if( fixedcell.getDatatype().equalsIgnoreCase( "FACILITY" ) )
            {
                tempStr = organisationUnit.getName();
            }
            
            else if( fixedcell.getDatatype().equalsIgnoreCase( "PERIOD" ) )
            {
                tempStr = monthYear;
            }

            sheet.addCell( new Label( fixedcell.getCol(), fixedcell.getRow(), tempStr, getCellFormat1() ) );
        } 
            
        
        // for dynamic cell
        
        int slNo = 1;
        int rowCount = 0;
        int rowStart = 0;
        
        //int tempTotalAmount = 0;
        //int totalAmount = 0;
        
        for( Patient asha : ashaList )
        {
            int tempTotalAmount = 0;
            int totalAmount = 0;
            
            for( ReportCell dynamiccell : dynamiccells )
            {
                String value = "";
                String totalApproveAmount = "";
                
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
                    
                    if( !dynamiccell.getService().equalsIgnoreCase( "0" ) )
                    {
                        String countryTotal = ""+ashaServiceApproveAmountMap.get( asha.getId() +":" + Integer.parseInt( dynamiccell.getService() ) );
                        
                        //System.out.println(  " ASHA ID  : " + asha.getId() + " DE Group ID  : " + Integer.parseInt( dynamiccell.getService() ) );
                        
                        if( countryTotal != null && !countryTotal.equalsIgnoreCase( "null" ) )
                        {
                            //value = ""+Integer.parseInt( value );
                            
                            double contryTot = Double.parseDouble( countryTotal ) ;
                            contryTot = ( Math.round( contryTot ) ) ;
                            
                            double temp = Double.parseDouble( countryTotal )/2 ;
                            
                            temp = ( Math.round( temp ) ) ;
                            
                            Integer stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                            
                            int groupWiseTotal = (int) ( contryTot + stateAmount );
                            
                            value = ""+groupWiseTotal;
                            
                            /*
                            try
                            {
                                tempTotalAmount = Integer.parseInt( value );
                            }
                            catch ( Exception e )
                            {
                                tempTotalAmount = 0;
                               
                            }
                            
                            totalAmount += tempTotalAmount;
                            */
                            
                            //System.out.println(  " : Total Amount  : " + totalAmount );
                        }
                    }
                 
                    else if( dynamiccell.getService().equalsIgnoreCase( "0" ) )
                    {
                        String anyOthervalue = budgetUtilizationPatientDataValueMap.get( asha.getId() );
                        
                        if( anyOthervalue != null && !anyOthervalue.equalsIgnoreCase( "null" ) )
                        {
                            double anyOthVal = Double.parseDouble( anyOthervalue ) ;
                            anyOthVal = ( Math.round( anyOthVal ) ) ;
                            
                            int anyOtherTotal = (int) ( anyOthVal );
                            
                            value = ""+anyOtherTotal;
                            
                            //System.out.println(  " ASHA ID  : " + asha.getId() + " DATAELEMENT_ANYOTHER Value : " + value );
                        }
                        
                    }
                    
                    
                    try
                    {
                        tempTotalAmount = Integer.parseInt( value );
                    }
                    catch ( Exception e )
                    {
                        tempTotalAmount = 0;
                    }
                    
                    totalAmount += tempTotalAmount;
                    
                }
                
                //System.out.println(  " : Final Total Amount  : " + totalAmount );
                
                try
                {
                    
                    sheet.addCell( new Number( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, Integer.parseInt( value ), getCellFormat2() ) );
                }
                catch ( Exception e )
                {
                    sheet.addCell( new Label( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, value, getCellFormat2() ) ); 
                }
                
            
                if( dynamiccell.getDatatype().equalsIgnoreCase( "DATAELEMENT" ) )
                {       
                    if( !dynamiccell.getService().equalsIgnoreCase( "0" ) )
                    {
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
                    
                    else if( dynamiccell.getService().equalsIgnoreCase( "0" ) )
                    {
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
                    
                    
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "TOTAL" ) )
                {
                    try
                    {
                        
                        sheet.addCell( new Number( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, totalAmount , getCellFormat2() ) );
                    }
                    catch ( Exception e )
                    {
                        //sheet.addCell( new Label( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, totalAmount, getCellFormat2() ) ); 
                    } 
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "TOTALAPPROVEAMOUNT" ) )
                {
                    totalApproveAmount = ""+patientDataValueMap.get( asha.getId() +":" + 507 );
                    
                    if( totalApproveAmount != null && !totalApproveAmount.equalsIgnoreCase( "null" ) )
                    {
                        try
                        {
                            
                            sheet.addCell( new Number( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, Integer.parseInt( totalApproveAmount ) , getCellFormat2() ) );
                        }
                        catch ( Exception e )
                        {
                            sheet.addCell( new Label( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, totalApproveAmount, getCellFormat2() ) ); 
                        } 
                    }
                }                
                
            }   
            
            
            //sheet.addCell( new jxl.write.Number( c, r, val, st ) );
                
            //sheet.addCell( new Label( dynamiccell.getColno(), dynamiccell.getRowno()+rowCount, value, getCellFormat2() ) );
            rowCount++;
            slNo++;
            rowStart = rowCount;
    
        } 
        
  
        outputReportWorkbook.write();
        outputReportWorkbook.close();
        
      
        fileName = "ASHABudgetUtilizationReport" + ".xls";
        outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        
        outputReportFile.deleteOnExit();
   
        
        System.out.println(  " Report Generation End Time is : " + new Date() );
        
        return SUCCESS;
    }
    
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
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



