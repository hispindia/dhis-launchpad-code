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
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetASHAPaymentStatusReportResultAction_1 implements Action
{
    public static final String ASHA_ACTIVITY_PROGRAM = "ASHA Activity Program";//1.0
    public static final String ASHA_ACTIVITY_PROGRAM_STAGE = "ASHA Activity Program Stage";//1.0
    private final String OPTION_SET_PAYMENT = "Payment";
    
    public static final String PHC_GROUP = "PHC Group";//25.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) 
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }    
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
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
    
    public String getSelectedPeriodId()
    {
        return selectedPeriodId;
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
    
    /*
    private Collection<Patient> patients = new ArrayList<Patient>();
    
    public Collection<Patient> getPatients()
    {
        return patients;
    }
    */
    private List<Patient> patients = new ArrayList<Patient>();
   
    public List<Patient> getPatients()
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
        OrganisationUnit selectedOrgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        Constant phcGroupConstant = constantService.getConstantByName( PHC_GROUP );
        
        OrganisationUnitGroup organisationUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( (int) phcGroupConstant.getValue() );
        
        
        
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_PAYMENT );
        
        paymentDataElementList = new ArrayList<DataElement>();
        
        for( String optionName : optionSet.getOptions() )
        {
            DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( optionName ) );
            paymentDataElementList.add( dataElement );
        }
        
        Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers(DataElement.class, paymentDataElementList ) );
        String dataElementIdsByComma = getCommaDelimitedString( dataElementIds );
        
        dataElementIdsByComma += "," +159;
        
        //System.out.println( " dataElementIdsByComma  : " +dataElementIdsByComma + "-- patientIdsByComma " + patientIdsByComma  );
        
        //System.out.println( "  period.getStartDateString()  : " + period.getStartDateString() );
        
       
        
        /*
        Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM );
        Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM_STAGE );
        
        Program program = programService.getProgram( (int) programConstant.getValue() );
        
        ProgramStage programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        */
       
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        //SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );;
        
        monthYear = simpleDateFormat.format( period.getStartDate() );
        
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
        
        if (  !organisationUnitGroup.getMembers().contains( selectedOrgUnit )  )
        {
            
            patients = new ArrayList<Patient>( patientService.getPatients( selectedOrgUnit, null,null ) );
            
            Collections.sort( patients, new ASHANameComparator() );
            
            //Collections.sort( patients, new IdentifiableObjectNameComparator() );
            
            Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers(Patient.class, patients ) );
            String patientIdsByComma = getCommaDelimitedString( patientIds );
            
            Map<String, String> patientAttributeValueMap = new HashMap<String, String>( ashaService.getPatientAttributeValues( patientIdsByComma ) );
            
            Map<String, String> patientDataValueMap = new HashMap<String, String>( ashaService.getPatientDataValues( patientIdsByComma, dataElementIdsByComma, period.getStartDateString(), 1, 1 ));
            
            int rowStart = 0;
            int colStart = 0;
            
            sheet0.mergeCells( colStart , rowStart, colStart + 10, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "ASHA PAYMENT STATUS REPORT", getCellFormat1() ) );
            
            rowStart++;
            
            sheet0.addCell( new Label( colStart, rowStart, "Sub Centre Name", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart+1, rowStart, selectedOrgUnit.getName(), getCellFormat2() ) );
                    
            sheet0.addCell( new Label( colStart+9, rowStart, "Month", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart+10, rowStart, monthYear, getCellFormat2() ) );
            
            rowStart++;
            sheet0.addCell( new Label( colStart, rowStart, "Sl.No", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 1, rowStart, "Name", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 2, rowStart, "Age", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 3, rowStart, "Phone No", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 4, rowStart, "Village", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, "Payment Due", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, "Payable Amount", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, "Total Payable Amount", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 8, rowStart, "Amount Paid", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 9, rowStart, "Cheque/Voucher Number", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 10, rowStart, "Comments", getCellFormat1() ) );
            
            rowStart++;
            rowStart++;
            int slNo = 1;
            for ( Patient patient : patients )
            {
                String Village ="";
                Village =  patientAttributeValueMap.get( patient.getId() + ":" + 6 );
                
                sheet0.addCell( new Number( colStart, rowStart, slNo, getCellFormat1() ) );
                sheet0.addCell( new Label( colStart + 1, rowStart, patient.getFullName(), getCellFormat2() ) );
                sheet0.addCell( new Label( colStart + 2, rowStart, patient.getAge(),getCellFormat2() ) );
                sheet0.addCell( new Label( colStart + 3, rowStart, patient.getPhoneNumber(), getCellFormat2() ) );
                sheet0.addCell( new Label( colStart + 4, rowStart, Village, getCellFormat2() ) );
                
                //String paymentDue = "";
                
                //paymentDue =  patientDataValueMap.get( patient.getId()+":"+159 );
                
                
                Double paymentDue = 0.0;
                //System.out.println( " Payment Due  : " + paymentDue );
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
                    //paymentDue = Double.parseDouble( patientDataValueMap.get( patient.getId()+":"+ 159 ));
                }
                
                //System.out.println( " Payment Due  : " + paymentDue );
                
                sheet0.addCell( new Number( colStart + 5, rowStart, paymentDue, getCellFormat2() ) );
                
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
                    
                    //payableAmount = Double.parseDouble( patientDataValueMap.get( patient.getId()+":"+ 160 ));
                }
                
                sheet0.addCell( new Number( colStart + 6, rowStart, payableAmount, getCellFormat2() ) );
                /*
                Double totalPayableAmount = 0.0;
                
                String tempTotalPayableAmount = patientDataValueMap.get( patient.getId()+":"+ 159 ) + Double.parseDouble( patientDataValueMap.get( patient.getId()+":"+ 160 ));
                
                if( totalPayableAmount != null )
                {
                    totalPayableAmount = Double.parseDouble( patientDataValueMap.get( patient.getId()+":"+ 159 )) + Integer.parseInt( patientDataValueMap.get( patient.getId()+":"+ 160 ));
                }
                */
                sheet0.addCell( new Number( colStart + 7, rowStart, paymentDue + payableAmount , getCellFormat2() ) );
                
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
                    
                    //amountPaid = Double.parseDouble( patientDataValueMap.get( patient.getId()+":"+ 161 ));
                }
                
                sheet0.addCell( new Number( colStart + 8, rowStart, amountPaid, getCellFormat2() ) );
                
                sheet0.addCell( new Label( colStart + 9, rowStart,  patientDataValueMap.get( patient.getId()+":"+ 162 ), getCellFormat2() ) );
                sheet0.addCell( new Label( colStart + 10, rowStart,  patientDataValueMap.get( patient.getId()+":"+ 163 ), getCellFormat2() ) );
                
                /*
                int newCol = 0;
                
                newCol = colStart1 + 6;
                
                System.out.println( colStart1  + "--" + newCol );
                
                int i = 0;
                for( DataElement dataElement : paymentDataElementList )
                {
                    int col = newCol+i;
                    sheet0.addCell( new Label( col, rowStart, patientDataValueMap.get( patient.getId()+":"+ dataElement.getId() ), getCellFormat2() ) );
                    
                    System.out.println( rowStart  + " -- " +col  + "-- " + patientDataValueMap.get( patient.getId()+":"+ dataElement.getId() ) );
                    
                    i++;
                }
                */
                
                slNo++;
                rowStart++;
            }
           
        }
        
        //list for selected PHC
        
        else
        {
            int rowStart = 0;
            int colStart = 0;
            
            sheet0.mergeCells( colStart , rowStart, colStart + 11, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "ASHA PAYMENT STATUS REPORT", getCellFormat1() ) );
            
            rowStart++;
            
            sheet0.mergeCells( colStart , rowStart, colStart + 1, rowStart );
            sheet0.addCell( new Label( colStart, rowStart, "PHC Name", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart+2, rowStart, selectedOrgUnit.getName(), getCellFormat2() ) );
                    
            sheet0.addCell( new Label( colStart+10, rowStart, "Month", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart+11, rowStart, monthYear, getCellFormat2() ) );
            
            rowStart++;
            sheet0.addCell( new Label( colStart, rowStart, "Sl.No", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 1, rowStart, "Sub Centre Name", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 2, rowStart, "ASHA Name", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 3, rowStart, "Age", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 4, rowStart, "Phone No", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 5, rowStart, "Village", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 6, rowStart, "Payment Due", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 7, rowStart, "Payable Amount", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 8, rowStart, "Total Payable Amount", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 9, rowStart, "Amount Paid", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 10, rowStart, "Cheque/Voucher Number", getCellFormat1() ) );
            sheet0.addCell( new Label( colStart + 11, rowStart, "Comments", getCellFormat1() ) );
            
            rowStart++;
            rowStart++;
            int slNo = 1;
            
            //OrganisationUnit parentOrgUnit = selectedOrgUnit.getParent();
            
            
            
            List<OrganisationUnit> subCentreList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren());
            
            Collections.sort( subCentreList, new IdentifiableObjectNameComparator() );
            
            for( OrganisationUnit subCentre : subCentreList )
            {
                List<Patient> tempPatientList = new ArrayList<Patient>( patientService.getPatients( subCentre, null,null ) );
                
                Collection<Integer> tempPatientIds = new ArrayList<Integer>( getIdentifiers( Patient.class, tempPatientList ) );
                String tempPatientIdsByComma = getCommaDelimitedString( tempPatientIds );
                
                Map<String, String> tempPatientAttributeValueMap = new HashMap<String, String>( ashaService.getPatientAttributeValues( tempPatientIdsByComma ) );
                
                Map<String, String> tempPatientDataValueMap = new HashMap<String, String>( ashaService.getPatientDataValues( tempPatientIdsByComma, dataElementIdsByComma, period.getStartDateString(), 1, 1 ));
                
                Collections.sort( tempPatientList, new ASHANameComparator() );
                
                for ( Patient patient : tempPatientList )
                {
                    String Village ="";
                    Village =  tempPatientAttributeValueMap.get( patient.getId() + ":" + 6 );
                    
                    sheet0.addCell( new Number( colStart, rowStart, slNo, getCellFormat1() ) );
                    
                    sheet0.addCell( new Label( colStart + 1, rowStart, subCentre.getName(), getCellFormat2() ) );
                    
                    sheet0.addCell( new Label( colStart + 2, rowStart, patient.getFullName(), getCellFormat2() ) );
                    sheet0.addCell( new Label( colStart + 3, rowStart, patient.getAge(),getCellFormat2() ) );
                    sheet0.addCell( new Label( colStart + 4, rowStart, patient.getPhoneNumber(), getCellFormat2() ) );
                    sheet0.addCell( new Label( colStart + 5, rowStart, Village, getCellFormat2() ) );
                    
                    Double paymentDue = 0.0;
                   
                    String tempPaymentDue = tempPatientDataValueMap.get( patient.getId()+":"+ 159 );
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
                    
                    sheet0.addCell( new Number( colStart + 6, rowStart, paymentDue, getCellFormat2() ) );
                    
                    Double payableAmount = 0.0;
                    String tempPayableAmount =  tempPatientDataValueMap.get( patient.getId()+":"+ 160 );
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
                    
                    sheet0.addCell( new Number( colStart + 7, rowStart, payableAmount, getCellFormat2() ) );
                    
                    sheet0.addCell( new Number( colStart + 8, rowStart, paymentDue + payableAmount , getCellFormat2() ) );
                    
                    Double amountPaid = 0.0;
                    String tempAmountPaid =  tempPatientDataValueMap.get( patient.getId()+":"+ 161 );
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
                    
                    sheet0.addCell( new Number( colStart + 9, rowStart, amountPaid, getCellFormat2() ) );
                    
                    sheet0.addCell( new Label( colStart + 10, rowStart,  tempPatientDataValueMap.get( patient.getId()+":"+ 162 ), getCellFormat2() ) );
                    sheet0.addCell( new Label( colStart + 11, rowStart,  tempPatientDataValueMap.get( patient.getId()+":"+ 163 ), getCellFormat2() ) );
                    
                    slNo++;
                    rowStart++;
                }
                
                rowStart++;
            }
            
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "ASHAPaymentStatus.xls";
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
        wCellformat.setWrap( true );
        wCellformat.setShrinkToFit( true );
        return wCellformat;
    } // end getCellFormat1() function
    
    
    public WritableCellFormat getCellFormat2()throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setShrinkToFit( true );
        wCellformat.setWrap( true );
    
        return wCellformat;
    }
}
