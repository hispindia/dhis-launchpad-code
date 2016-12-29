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
import jxl.write.Blank;
import jxl.write.Formula;
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
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
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
import org.hisp.dhis.reports.ReportService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateASHAActivitiesReportResultAction implements Action
{
    public static final String ASHA_AMOUNT_DATA_SET = "Amount"; // 2.0
    
    public static final String PAYMENT_APPROVE_DATAELEMENT_GROUP_ID = "PAYMENT_APPROVE_DATAELEMENT_GROUP_ID";//11.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//4.0
    
    private final String OPTION_SET_ASHA_ACTIVITES = "Activites";
    
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
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
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
        
        
        // Data set Information
        
        Map<Integer, String> dataValueMap = new HashMap<Integer, String>();
        
        Constant amountDataSet = constantService.getConstantByName( ASHA_AMOUNT_DATA_SET );
        DataSet dataSet = dataSetService.getDataSet( (int) amountDataSet.getValue() );

        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );

        OrganisationUnit ou = dataSetSource.get( 0 );
        
        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );

        for ( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();

            DataValue dataValue = new DataValue();

            dataValue = reportService.getLatestDataValue( dataElement, optionCombo, ou );

            String value = "";

            if ( dataValue != null )
            {
                value = dataValue.getValue();

                dataValueMap.put( dataElement.getId(), value );
            }

        }
        
        
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
        
        Map<String, String> activitesDataValueMap = new HashMap<String, String>();
        
        Constant PaymentApproveDataElementGroupConstant = constantService.getConstantByName( PAYMENT_APPROVE_DATAELEMENT_GROUP_ID );
        
        DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( (int) PaymentApproveDataElementGroupConstant.getValue() );
        
        List<DataElement> paymentApproveDEs = new ArrayList<DataElement>( dataElementGroup.getMembers() );
        
        String dataElementIdsByComma = "-1";
        
        for( DataElement de : paymentApproveDEs )
        {
            dataElementIdsByComma += "," + de.getId();
        }
        
        
        OptionSet activitesOptionSet = optionService.getOptionSetByName( OPTION_SET_ASHA_ACTIVITES );
        
        String activitesDataElementIdsByComma = "-1";
        
        for( String optionName : activitesOptionSet.getOptions() )
        {
            activitesDataElementIdsByComma += "," + optionName;
        }
        
        //System.out.println(  " : patientIdsByComma : " + patientIdsByComma );
        //System.out.println(  " : dataElementIdsByComma : " + budgetUtilizationDataElementIdsByComma );
       
        if( patientIds != null &&  patientIds.size() > 0 )
        {
            if( program != null &&  programStage != null )
            {
                patientDataValueMap = ashaService.getApprovedDataFromPatientDataValuesByExecutionDate( patientIdsByComma, program.getId(), programStage.getId(), period.getStartDateString(), dataElementIdsByComma  );
            
                activitesDataValueMap = ashaService.getApprovedDataFromPatientDataValuesByExecutionDate( patientIdsByComma, program.getId(), programStage.getId(), period.getStartDateString(), activitesDataElementIdsByComma  );
            }
            
        }
        
        Map<String, Double> ashaServiceApproveAmountMap = new HashMap<String, Double>();
        
        //ashaServiceApproveAmountMap = ashaService.getASHAServiceWiseFinalApproveAmount( period.getId() );
        
        ashaServiceApproveAmountMap = ashaService.getASHAFinalServiceWiseApproveAmount( period.getId() );
        
        String xmlFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xml" + File.separator + "ASHAActivitiesRreport.xml";
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xls" + File.separator + "ASHAActivitiesRreport.xls";
        
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
            //int tempTotalNHMAmount = 0;
            //int totaNHMlAmount = 0;
            //int totalStateAmount = 0;
            boolean isStateIncentive = false;
            for( ReportCell dynamiccell : dynamiccells )
            {
                //System.out.println(  " ASHA ID  : " + asha.getId() + "--- "  + asha.getFullName() +  " row  : " + dynamiccell.getRow()+rowCount +  " colom  : " + dynamiccell.getCol() );
                
                String value = "";
                //String totalApproveAmount = "";
                
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
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "DATAELEMENT" ) || dynamiccell.getDatatype().equalsIgnoreCase( "DATAELEMENT_P" ) )
                {
                    
                    if( dynamiccell.getExpression().equalsIgnoreCase( "DATAELEMENT" ) )
                    {
                        String nhmAmount = ""+ashaServiceApproveAmountMap.get( asha.getId() +":" + Integer.parseInt( dynamiccell.getService() ) );
                        
                        if( nhmAmount != null && !nhmAmount.equalsIgnoreCase( "null" ) )
                        {
                            isStateIncentive = true;
                            double nhmAmt = Double.parseDouble( nhmAmount ) ;
                            
                            int nhmApproveAmount = (int) ( nhmAmt );
                            
                            value = ""+nhmApproveAmount;
                        }
                        
                    }
                    
                    else if( dynamiccell.getExpression().equalsIgnoreCase( "DATAELEMENT_P" ) )
                    {
                        String dateDe = dynamiccell.getService().split( ":" )[0];
                        String amountDe = dynamiccell.getService().split( ":" )[1];
                        //String validationDe = dynamiccell.getService().split( ":" )[2];
                        
                        //System.out.println(  " ASHA ID  : " + asha.getId() + " DE ID  : " + dateDe + ":" + amountDe + ":" + validationDe );
                        
                        String dateValue = ""+patientDataValueMap.get( asha.getId() +":" + Integer.parseInt( dateDe ) );
                        
                        if( dateValue != null && !dateValue.equalsIgnoreCase( "null" ) )
                        {
                            isStateIncentive = true;
                            double nhmAmt = Double.parseDouble( dataValueMap.get( Integer.parseInt( amountDe ) ) ) ;
                            
                            int nhmApproveAmount = (int) ( nhmAmt );
                            
                            value = ""+nhmApproveAmount;
                        }
                        
                    }
                    
                    else if( dynamiccell.getExpression().equalsIgnoreCase( "DATAELEMENT_WATER_SAMPLE" ) )
                    {
                        String dateDe = dynamiccell.getService().split( ":" )[0];
                        String amountDe = dynamiccell.getService().split( ":" )[1];
                        String noOfWaterSampleDe = dynamiccell.getService().split( ":" )[2];
                        
                        //System.out.println(  " Water ASHA ID  : " + asha.getId() + " DE ID  : " + dateDe + ":" + amountDe + ":" + noOfWaterSampleDe );
                        
                        String dateValue = ""+patientDataValueMap.get( asha.getId() +":" + Integer.parseInt( dateDe ) );
                        
                        //int waterSampleNo = 0;
                        
                        String noOfWaterSample = ""+activitesDataValueMap.get( asha.getId() +":" + Integer.parseInt( noOfWaterSampleDe ) );
                        
                        /*
                        if( noOfWaterSample != null && !noOfWaterSample.equalsIgnoreCase( "null" ) && noOfWaterSample != null && !noOfWaterSample.equalsIgnoreCase( "null" ) )
                        {
                            waterSampleNo = Integer.parseInt( noOfWaterSample );
                        }
                        */
                        
                        if( dateValue != null && !dateValue.equalsIgnoreCase( "null" ) && noOfWaterSample != null && !noOfWaterSample.equalsIgnoreCase( "null" ) )
                        {
                            isStateIncentive = true;
                            double nhmAmt = Double.parseDouble( dataValueMap.get( Integer.parseInt( amountDe ) ) ) ;
                            
                            int nhmApproveAmount = (int) ( nhmAmt ) *  Integer.parseInt( noOfWaterSample );
                            
                            value = ""+nhmApproveAmount;
                        }
                        
                    }
                    
                    else if( dynamiccell.getExpression().equalsIgnoreCase( "DATAELEMENT_TA_DA" ) )
                    {
                        String dateDe = dynamiccell.getService().split( ":" )[0];
                        String amountDe = dynamiccell.getService().split( ":" )[1];
                        String noOfDaysTrainingDe = dynamiccell.getService().split( ":" )[2];
                        
                        //System.out.println(  " TA/DA ASHA ID  : " + asha.getId() + " DE ID  : " + dateDe + ":" + amountDe + ":" + noOfDaysTrainingDe );
                        
                        String dateValue = ""+patientDataValueMap.get( asha.getId() +":" + Integer.parseInt( dateDe ) );
                        
                        String noOfDaysTraining = ""+activitesDataValueMap.get( asha.getId() +":" + Integer.parseInt( noOfDaysTrainingDe ) );
                        
                        if( dateValue != null && !dateValue.equalsIgnoreCase( "null" ) && noOfDaysTraining != null && !noOfDaysTraining.equalsIgnoreCase( "null" ) )
                        {
                            isStateIncentive = true;
                            double nhmAmt = Double.parseDouble( dataValueMap.get( Integer.parseInt( amountDe ) ) ) ;
                            
                            int nhmApproveAmount = (int) ( nhmAmt ) * Integer.parseInt( noOfDaysTraining ) ;
                            
                            value = ""+nhmApproveAmount;
                        }
                        
                    }
                    
                    else if( dynamiccell.getExpression().equalsIgnoreCase( "DATAELEMENT_SALT_SAMPLE" ) )
                    {
                        String dateDe = dynamiccell.getService().split( ":" )[0];
                        String amountDe = dynamiccell.getService().split( ":" )[1];
                        String noOfSaltSampleDe = dynamiccell.getService().split( ":" )[2];
                        
                        //System.out.println(  " Salt ASHA ID  : " + asha.getId() + " DE ID  : " + dateDe + ":" + amountDe + ":" + noOfSaltSampleDe );
                        
                        String dateValue = ""+patientDataValueMap.get( asha.getId() +":" + Integer.parseInt( dateDe ) );
                        
                        String noOfSaltSample = ""+activitesDataValueMap.get( asha.getId() +":" + Integer.parseInt( noOfSaltSampleDe ) );
                        
                        if( dateValue != null && !dateValue.equalsIgnoreCase( "null" ) && noOfSaltSample != null && !noOfSaltSample.equalsIgnoreCase( "null" ) && Integer.parseInt( noOfSaltSample ) >= 50 )
                        {
                            isStateIncentive = true;
                            double nhmAmt = Double.parseDouble( dataValueMap.get( Integer.parseInt( amountDe ) ) ) ;
                            
                            int nhmApproveAmount = (int) ( nhmAmt );
                            
                            value = ""+nhmApproveAmount;
                        }
                        
                    }
                    
                    else if( dynamiccell.getExpression().equalsIgnoreCase( "ASHA_AWARD" ) )
                    {
                        String dateDe = dynamiccell.getService().split( ":" )[0];
                        String amountDe1 = dynamiccell.getService().split( ":" )[1];
                        String amountDe2 = dynamiccell.getService().split( ":" )[2];
                        String amountDe3 = dynamiccell.getService().split( ":" )[3];
                        
                        String awardDe = dynamiccell.getService().split( ":" )[4];
                        
                        //System.out.println(  " Salt ASHA ID  : " + asha.getId() + " DE ID  : " + dateDe + ":" + amountDe + ":" + noOfSaltSampleDe );
                        
                        String dateValue = ""+patientDataValueMap.get( asha.getId() +":" + Integer.parseInt( dateDe ) );
                        
                        String awardValue = ""+activitesDataValueMap.get( asha.getId() +":" + Integer.parseInt( awardDe ) );
                        
                        double nhmAmt = 0.0;
                        
                        if( dateValue != null && !dateValue.equalsIgnoreCase( "null" )  && awardValue != null && !awardValue.equalsIgnoreCase( "null" ))
                        {
                            isStateIncentive = true;
                            
                            if( awardValue.equalsIgnoreCase( "Award-I" ))
                            {
                                nhmAmt = Double.parseDouble( dataValueMap.get( Integer.parseInt( amountDe1 ) ) ) ;
                            }
                            
                            else if( awardValue.equalsIgnoreCase( "Award-II" ))
                            {
                                nhmAmt = Double.parseDouble( dataValueMap.get( Integer.parseInt( amountDe2 ) ) ) ;
                            }
                            
                            else if( awardValue.equalsIgnoreCase( "Award-III" ))
                            {
                                nhmAmt = Double.parseDouble( dataValueMap.get( Integer.parseInt( amountDe3 ) ) ) ;
                            }
                            
                            int nhmApproveAmount = (int) ( nhmAmt );
                            
                            value = ""+nhmApproveAmount;
                        }
                        
                    }                   
                    
                    /* this is done using formula in XML file and add code in print in the sheet
                    try
                    {
                        tempTotalNHMAmount = Integer.parseInt( value );
                    }
                    catch ( Exception e )
                    {
                        tempTotalNHMAmount = 0;
                    }
                    
                    totaNHMlAmount += tempTotalNHMAmount;
                    */
                    
                    //System.out.println(  " ASHA ID  : " + asha.getId() + " DE ID  : " + Integer.parseInt( dynamiccell.getService() ) + " Value  : " + value );
                    
                    
                }
                /*
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "TOTALNHMBUDGET" ) )
                {
                    value = ""+totaNHMlAmount;
                }
                */
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "STATE_INCENTIVE" ) )
                {
                    if( isStateIncentive  )
                    {
                        //double nhmAmt = 1000.0;
                        double nhmAmt = 500.0;
                        int nhmApproveAmount = (int) ( nhmAmt );
                        
                        value = ""+nhmApproveAmount;
                    }
                    
                    //System.out.println(  " ASHA ID  : " + asha.getId() + "--- "  + asha.getFullName() +  " Value  : " + value );
                }
                /*
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "STATEBUDGET" ) )
                {
                    int temp =  totaNHMlAmount/2 ;
                    
                    temp = ( Math.round( temp ) ) ;
                    
                    
                    totalStateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                    
                    value = ""+totalStateAmount;d
                }
                */
                
                else if ( dynamiccell.getDatatype().equalsIgnoreCase( "formula" ) )
                {
                    value = dynamiccell.getExpression().replace( "?", "" + ( dynamiccell.getRow() + rowCount + 1) );
                    
                    //System.out.println(  " ASHA ID  : " + asha.getId() + "--- "  + asha.getFullName() +  " Value  : " + value );
                }
                /*
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "TOTALBUDGET" ) )
                {
                    int grandTotal = (int) ( totaNHMlAmount + totalStateAmount );
                    
                    value = ""+grandTotal;
                }
                */
                
                //System.out.println(  " : Total NHM Amount  : " + totaNHMlAmount );
                
                try
                {
                    if ( value == null || value.equals( "" ) )
                    {
                        sheet.addCell( new Blank( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, getCellFormat2() )  );
                    }
                    
                    else
                    {
                        if ( dynamiccell.getDatatype().equalsIgnoreCase( "formula" ) )
                        {
                            sheet.addCell( new Formula( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, value, getCellFormat2() ) );
                        }
                        else
                        {
                            sheet.addCell( new Number( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, Integer.parseInt( value ), getCellFormat2() ) );
                        }
                    }
                    
                }
                catch ( Exception e )
                {
                    sheet.addCell( new Label( dynamiccell.getCol(), dynamiccell.getRow()+rowCount, value, getCellFormat2() ) ); 
                }
                
                //System.out.println(  " ASHA ID  : " + asha.getId() + "--- "  + asha.getFullName() +  " row  : " + dynamiccell.getRow()+rowCount +  " colom  : " + dynamiccell.getCol() );
            }   
            
            //sheet.addCell( new jxl.write.Number( c, r, val, st ) );
                
            //sheet.addCell( new Label( dynamiccell.getColno(), dynamiccell.getRowno()+rowCount, value, getCellFormat2() ) );
            rowCount++;
            slNo++;
            rowStart = rowCount;
    
        } 
        
  
        outputReportWorkbook.write();
        outputReportWorkbook.close();
        
      
        fileName = "ASHAActivitiesReport" + ".xls";
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

