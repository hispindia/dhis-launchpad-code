package org.hisp.dhis.asha.beneficiaries;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
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
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.reports.ReportService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetASHABeneficiaryReportResultAction implements Action
{
    public static final String ASHA_AMOUNT_DATA_SET = "Amount"; // 2.0

    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_GROUP_ID = "ASHA_ACTIVITY_DETAILS_GROUP_ID";//12.0
    
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

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
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

    private ASHAService ashaService;

    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
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

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    
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
    

    // -------------------------------------------------------------------------
    // Action implementation
    // --------- ----------------------------------------------------------------

    public String execute() throws Exception
    {
        Patient patient = patientService.getPatient( id );

        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        //System.out.println( patient.getFullName()  + "--" + period.getStartDateString() );

        System.out.println( " Report Generation Start Time " + new Date() );
        
        beneficiaryList = new ArrayList<Beneficiary>( beneficiaryService.getAllBeneficiaryByASHAAndPeriod( patient, period ) );

        
        SimpleDateFormat simpleMonthYearFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        
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
        
        String completeHierarchy = patient.getOrganisationUnit().getName() + "/" + patient.getOrganisationUnit().getParent().getName()
                                   + "/" + patient.getOrganisationUnit().getParent().getParent().getName() + "/" + patient.getOrganisationUnit().getParent().getParent().getParent().getName();
        
        sheet0.addCell( new Label( colStart, rowStart, "Name", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+1, rowStart, patient.getFullName(), getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+4, rowStart, "Location", getCellFormat1() ) );
        //sheet0.addCell( new Label( colStart+5, rowStart, patient.getOrganisationUnit().getName(), getCellFormat1() ) );
        sheet0.addCell( new Label( colStart+5, rowStart, completeHierarchy, getCellFormat1() ) );
        
        
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
        sheet0.addCell( new Label( colStart + 8, rowStart, "Incentive Amount", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 9, rowStart, "State Incentive Amount", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 10, rowStart, "Total", getCellFormat1() ) );
        
        rowStart++;
        int slNo = 1;
        
        
        //double totalAmount = 0.0;
        int totalAmountInMonth = 0;
        int stateTotalAmountInMonth = 0;
        int totalAmount = 0;
        
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
                
                int countryTotal = 0;
                int stateAmount = 0;
                
                if( beneficiary.getDataElement().getId() == 110 )
                {
                    countryTotal = 0;
                    stateAmount = Integer.parseInt(  beneficiary.getPrice() );
                    stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));                    
                }
                
                else
                {       
                    /*
                    countryTotal = Integer.parseInt(  beneficiary.getPrice() );
                    stateAmount = Integer.parseInt(  beneficiary.getPrice() ) / 2 ;
                    stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                    */
                    
                    countryTotal = Integer.parseInt(  beneficiary.getPrice() );
                    double temp = Double.parseDouble( beneficiary.getPrice() ) / 2 ;
                    temp = ( Math.round( temp ) ) ;
                    //stateAmount = Integer.parseInt(  beneficiary.getPrice() ) / 2 ;
                    //stateAmount = (int) ( Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                    stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                    
                }
                
                totalAmountInMonth += countryTotal;
                stateTotalAmountInMonth += stateAmount;
                
                int total = countryTotal + stateAmount;
                
                sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
                sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
                sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
                
            }
            
            slNo++;
            rowStart++;
        }
        
        totalAmount = totalAmountInMonth + stateTotalAmountInMonth;
        
        sheet0.mergeCells( colStart , rowStart, colStart + 7, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "Total Beneficiary Amount", getCellFormat1() ) );
        sheet0.addCell( new Number( colStart + 8, rowStart, totalAmountInMonth, getCellFormat2() ) );
        sheet0.addCell( new Number( colStart + 9, rowStart, stateTotalAmountInMonth, getCellFormat2() ) );
        sheet0.addCell( new Number( colStart + 10, rowStart, totalAmount, getCellFormat2() ) );
        
        rowStart++;
        
        // ASHA Activity Related information
        Constant amountDataSet = constantService.getConstantByName( ASHA_AMOUNT_DATA_SET );
        

        Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_ID );
        Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID );
        
        Program program = programService.getProgram( (int) programConstant.getValue() );

        Integer programInstanceId = ashaService.getProgramInstanceId( patient.getId(), program.getId() );

        if ( programInstanceId == null )
        {
            Patient createdPatient = patientService.getPatient( patient.getId() );

            Date programEnrollDate = new Date();

            int programType = program.getType();
            ProgramInstance programInstance = null;

            if ( programType == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
            {
                programInstance = new ProgramInstance();
                programInstance.setEnrollmentDate( programEnrollDate );
                programInstance.setDateOfIncident( programEnrollDate );
                programInstance.setProgram( program );
                programInstance.setCompleted( false );

                programInstance.setPatient( createdPatient );
                createdPatient.getPrograms().add( program );
                patientService.updatePatient( createdPatient );

                programInstanceId = programInstanceService.addProgramInstance( programInstance );

            }
        }
       

        // Data set Information

        DataSet dataSet = dataSetService.getDataSet( (int) amountDataSet.getValue() );

        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );

        OrganisationUnit organisationUnit = dataSetSource.get( 0 );

        // PeriodType periodType = dataSet.getPeriodType();

        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        Map<Integer, String> dataValueMap = new HashMap<Integer, String>();
        
        for ( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();

            DataValue dataValue = new DataValue();

            dataValue = reportService.getLatestDataValue( dataElement, optionCombo, organisationUnit );

            String value = "";

            if ( dataValue != null )
            {
                value = dataValue.getValue();

                dataValueMap.put( dataElement.getId(), value );
            }

        }

        // program and programStage Related information
        program = programService.getProgram( (int) programConstant.getValue() );
        
        Constant ashaActivityDetailsDataElementGroupConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_GROUP_ID );
        
        DataElementGroup ashaActivityDetailsDataElementGroup = dataElementService.getDataElementGroup( (int) ashaActivityDetailsDataElementGroupConstant.getValue() );
        
        List<DataElement> ashaActivityDetailsDataElements = new ArrayList<DataElement>( ashaActivityDetailsDataElementGroup.getMembers() );
        
        String dataElementIdsByComma = "-1";
        if( ashaActivityDetailsDataElements != null && ashaActivityDetailsDataElements.size() > 0 )
        {
            Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers( DataElement.class, ashaActivityDetailsDataElements ) );
            dataElementIdsByComma = getCommaDelimitedString( dataElementIds );
        }
        
        ProgramStage programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );

        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );
        
        Integer programStageInstanceId = null;
        
        if ( programInstanceId != null )
        {
            programStageInstanceId = ashaService.getProgramStageInstanceId( programInstanceId, programStage.getId(),
                period.getStartDateString() );
        }

        if ( programStageInstanceId == null )
        {
            ProgramStageInstance programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );
            programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );

            programStageInstance.setDueDate( format.parseDate( period.getStartDateString() ) );

            programStageInstanceId = programStageInstanceService.addProgramStageInstance( programStageInstance );
        }

        
        //Map<Integer, String> activityDetailsDataValueMap = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValue( programStageInstanceId ) );
        
        Map<Integer, String> activityDetailsDataValueMap = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValue( programStageInstanceId, dataElementIdsByComma ) );
        
        rowStart++;
       
        int goiTotalAmountInMonthASHAActivity = 0;
        int stateTotalAmountInMonthASHAActivity = 0;
        int totalAmountInMonthASHAActivity = 0;
        
        
        sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "ASHA Activity", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 4, rowStart, "Date of Session", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 5, rowStart, "Place of Session", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 6, rowStart, "Kind of Session", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 7, rowStart, "Number of Child", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 8, rowStart, "Incentive Amount", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 9, rowStart, "State Budget Fixed Incentive Amount", getCellFormat1() ) );
        sheet0.addCell( new Label( colStart + 10, rowStart, "Total", getCellFormat1() ) );
        
        rowStart++;
        
        if(  activityDetailsDataValueMap.get( 418 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Mobilization of Community for RI", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 418 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 419 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, " ", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, activityDetailsDataValueMap.get( 434 ), getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 20 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 20 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 20 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 )); 
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 20 ) );
                double temp = Double.parseDouble( dataValueMap.get( 20 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }
        
        
        if(  activityDetailsDataValueMap.get( 470 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Other Kind of Session 1", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 470 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 480 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, activityDetailsDataValueMap.get( 466 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, activityDetailsDataValueMap.get( 474 ), getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 479 ) != null)
            {   
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 479 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 479 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 )); 
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 479 ) );
                double temp = Double.parseDouble( dataValueMap.get( 479 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }
        
       
        if(  activityDetailsDataValueMap.get( 471 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Other Kind of Session 2", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 471 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 481 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, activityDetailsDataValueMap.get( 467 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, activityDetailsDataValueMap.get( 475 ), getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 479 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 479 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 479 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 479 ) );
                double temp = Double.parseDouble( dataValueMap.get( 479 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }        
        
        if(  activityDetailsDataValueMap.get( 472 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Other Kind of Session 3", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 472 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 482 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, activityDetailsDataValueMap.get( 468 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, activityDetailsDataValueMap.get( 476 ), getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 479 ) != null)
            {   
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 479 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 479 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
               */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 479 ) );
                double temp = Double.parseDouble( dataValueMap.get( 479 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }       
        
        
        if(  activityDetailsDataValueMap.get( 473 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Other Kind of Session 4", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 473 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 483 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, activityDetailsDataValueMap.get( 469 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, activityDetailsDataValueMap.get( 477 ), getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 479 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 479 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 479 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 479 ) );
                double temp = Double.parseDouble( dataValueMap.get( 479 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }       
        
        //meeting       
        //Community Mobilization for VHND Celebration
        if(  activityDetailsDataValueMap.get( 406 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Community Mobilization for VHND Celebration", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 406 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 407 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, "", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, "", getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 29 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 29 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 29 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 29 ) );
                double temp = Double.parseDouble( dataValueMap.get( 29 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                 
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }       
        
        //Convening Monthly Meeting of VHSNC
        if(  activityDetailsDataValueMap.get( 432 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Convening Monthly Meeting of VHSNC", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 432 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 433 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, "", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, "", getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 30 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 30 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 30 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 30 ) );
                double temp = Double.parseDouble( dataValueMap.get( 30 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }        
        
        
        //Monthly Meetings at PHC
        if(  activityDetailsDataValueMap.get( 411 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Monthly Meetings at PHC", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 411 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 412 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, "", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, "", getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 34 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 34 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 34 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 34 ) );
                double temp = Double.parseDouble( dataValueMap.get( 34 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }        
               
        //Convenient monthly meeting of adolescent girl
        if(  activityDetailsDataValueMap.get( 430 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Convenient monthly meeting of adolescent girl", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 430 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 431 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, "", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, "", getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 119 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 119 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 119 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 119 ) );
                double temp = Double.parseDouble( dataValueMap.get( 119 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
                
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }       
        
        //Other Meeting 1
        if(  activityDetailsDataValueMap.get( 461 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Other Meeting 1", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 461 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 458 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, activityDetailsDataValueMap.get( 455 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, "", getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 465 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 465 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 465 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 465 ) );
                double temp = Double.parseDouble( dataValueMap.get( 465 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }       
        
        //Other Meeting 2
        if(  activityDetailsDataValueMap.get( 462 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Other Meeting 2", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 462 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 459 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, activityDetailsDataValueMap.get( 456 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, "", getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 465 ) != null)
            {   
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 465 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 465 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 465 ) );
                double temp = Double.parseDouble( dataValueMap.get( 465 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }        
        
        //Other Meeting 3
        if(  activityDetailsDataValueMap.get( 463 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Other Meeting 3", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 463 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 460 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, activityDetailsDataValueMap.get( 457 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, "", getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 465 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 465 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 465 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */

                countryTotal = Integer.parseInt(  dataValueMap.get( 465 ) );
                double temp = Double.parseDouble( dataValueMap.get( 465 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }             
        
        
        //Water Samples Taken Community/School/ AWC to be taken for Testing
        if(  activityDetailsDataValueMap.get( 408 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Water Samples Taken Community/School/ AWC to be taken for Testing", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 408 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 410 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, " " , getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, activityDetailsDataValueMap.get( 409 ), getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 32 ) != null && activityDetailsDataValueMap.get( 409 ) != null )
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 32 ) ) * Integer.parseInt( activityDetailsDataValueMap.get( 409 ) ) ;
                stateAmount =  countryTotal / 2 ;
                
                //stateAmount = Integer.parseInt( dataValueMap.get( 32 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 32 ) ) * Integer.parseInt( activityDetailsDataValueMap.get( 409 ) ) ;
                double temp =  countryTotal / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                

            }
            
          
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }             
        
        
        //Conducting Testing of Salt Sample under NIDDCP
        if(  activityDetailsDataValueMap.get( 427 ) != null &&   Integer.parseInt( activityDetailsDataValueMap.get( 428 ) ) >= 50 )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Conducting Testing of Salt Sample under NIDDCP", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 427 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 429 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, " " , getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, activityDetailsDataValueMap.get( 428 ), getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 35 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 35 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 35 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 35 ) );
                double temp = Double.parseDouble( dataValueMap.get( 35 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
            }
            
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }             
               
        //TA/DA for Attending Training Programmes
        if(  activityDetailsDataValueMap.get( 413 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "TA/DA for Attending Training Programmes", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 413 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 414 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, " " , getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, activityDetailsDataValueMap.get( 437 ), getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 395 ) != null && activityDetailsDataValueMap.get( 437 ) != null )
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 395 ) ) * Integer.parseInt( activityDetailsDataValueMap.get( 437 ) );
                stateAmount = countryTotal / 2 ;
                
                //stateAmount = Integer.parseInt( dataValueMap.get( 395 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 395 ) ) * Integer.parseInt( activityDetailsDataValueMap.get( 437 ) );
                double temp =  countryTotal / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
            }
            
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }       
        
        //TA/DA for Attending Quarterly Award Ceremony
        if(  activityDetailsDataValueMap.get( 415 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "TA/DA for Attending Quarterly Award Ceremony", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 415 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, activityDetailsDataValueMap.get( 416 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, " " , getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, " ", getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( dataValueMap.get( 387 ) != null)
            {
                /*
                countryTotal = Integer.parseInt(  dataValueMap.get( 387 ) );
                stateAmount = Integer.parseInt( dataValueMap.get( 387 ) ) / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = Integer.parseInt(  dataValueMap.get( 387 ) );
                double temp = Double.parseDouble( dataValueMap.get( 387 ) ) / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
            }
            
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }           
        
        
        //ASHA Award
        if(  activityDetailsDataValueMap.get( 436 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, activityDetailsDataValueMap.get( 436 ), getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, simpleDateFormat.format( format.parseDate( activityDetailsDataValueMap.get( 417 ) ) ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, " ", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, " " , getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, " ", getCellFormat2() ) );
            
            int awardAmount = 0;
            int countryTotal = 0;
            int stateAmount = 0;
            
            if( activityDetailsDataValueMap.get( 436 ).equalsIgnoreCase( "Award-I" ) )
            {
                awardAmount = Integer.parseInt(  dataValueMap.get( 123 ) );
            }
            else if( activityDetailsDataValueMap.get( 436 ).equalsIgnoreCase( "Award-II" ) )
            {
                awardAmount = Integer.parseInt(  dataValueMap.get( 125 ) );
            }
            else if( activityDetailsDataValueMap.get( 436 ).equalsIgnoreCase( "Award-III" ) )
            {
                awardAmount = Integer.parseInt(  dataValueMap.get( 127 ) );
            }
            
            
            if( activityDetailsDataValueMap.get( 436 ) != null )
            {
                /*
                countryTotal = awardAmount;
                stateAmount = awardAmount / 2 ;
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                */
                
                countryTotal = awardAmount;
                double temp = awardAmount / 2 ;
                temp = ( Math.round( temp ) ) ;
               
                stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
            }
            
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Number( colStart + 8, rowStart, countryTotal, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, stateAmount, getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 10, rowStart, total, getCellFormat2() ) );
            
            rowStart++;
        }       
        
        
        //Reason for ASHA Inactive
        if(  activityDetailsDataValueMap.get( 438 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "Reason for ASHA Inactive", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, activityDetailsDataValueMap.get( 438 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, " ", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, " " , getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, " ", getCellFormat2() ) );
            
            
            sheet0.addCell( new Label( colStart + 8, rowStart, "", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 9, rowStart, "", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 10, rowStart, "", getCellFormat2() ) );
            
            rowStart++;
        }       
       
        
        //State Activity Incentive Amount
        if(  beneficiaryList.size() > 0 ||  activityDetailsDataValueMap.size() > 0 || activityDetailsDataValueMap.get( 438 ) != null )
        {
            sheet0.mergeCells( colStart , rowStart, colStart + 3, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "State Budget Fixed Incentive Amount", getCellFormat2() ) );
            
            sheet0.addCell( new Label( colStart + 4, rowStart, activityDetailsDataValueMap.get( 438 ), getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, " ", getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, " " , getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, " ", getCellFormat2() ) );
            
            int countryTotal = 0;
            int stateAmount = 500;
           
            goiTotalAmountInMonthASHAActivity += countryTotal;
            stateTotalAmountInMonthASHAActivity += stateAmount;
            
            int total = countryTotal + stateAmount;
            
            sheet0.addCell( new Label( colStart + 8, rowStart, "", getCellFormat2() ) );
            sheet0.addCell( new Number( colStart + 9, rowStart, total, getCellFormat2() ) );
            sheet0.addCell( new Label( colStart + 10, rowStart, "", getCellFormat2() ) );
            
            rowStart++;
        }       
        
        totalAmountInMonthASHAActivity = goiTotalAmountInMonthASHAActivity + stateTotalAmountInMonthASHAActivity;
        
        sheet0.mergeCells( colStart , rowStart, colStart + 7, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "Total ASHA Activity Amount", getCellFormat1() ) );
        sheet0.addCell( new Number( colStart + 8, rowStart, goiTotalAmountInMonthASHAActivity, getCellFormat2() ) );
        sheet0.addCell( new Number( colStart + 9, rowStart, stateTotalAmountInMonthASHAActivity, getCellFormat2() ) );
        sheet0.addCell( new Number( colStart + 10, rowStart, totalAmountInMonthASHAActivity, getCellFormat2() ) );
        
        rowStart++;
        sheet0.mergeCells( colStart , rowStart, colStart + 9, rowStart );
        sheet0.addCell( new Label( colStart, rowStart, "Grand Total Amount", getCellFormat1() ) );
        
        sheet0.addCell( new Number( colStart + 10, rowStart, totalAmount + totalAmountInMonthASHAActivity, getCellFormat2() ) );
        
                
        //System.out.println( " GOI ASHA Activity : " + goiTotalAmountInMonthASHAActivity + " GOI ASHA Activity : " + stateTotalAmountInMonthASHAActivity + " Total ASHA Activity : " + totalAmountInMonthASHAActivity );
        
        //System.out.println( " Grand Total Amount : " + ( totalAmount + totalAmountInMonthASHAActivity) );
        
        //System.out.println( " Beneficiary List : " + beneficiaryList.size() );
        //System.out.println( " DataElement Ids By Comma : " + dataElementIdsByComma );
        //System.out.println( " Activity Detail sDataValue Map : " + activityDetailsDataValueMap.size() + " : " + dataElementIdsByComma.length() );
        //System.out.println( " ActivityDetailsDataValueMap.get( 438 ) Value : " + activityDetailsDataValueMap.get( 438 ) );
        
        
        
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
        
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "ASHABeneficiaryList.xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
        
        System.out.println( " Report Generation End Time " + new Date() );
        
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
