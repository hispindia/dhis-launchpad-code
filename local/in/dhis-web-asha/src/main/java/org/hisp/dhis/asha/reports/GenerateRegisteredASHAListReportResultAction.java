package org.hisp.dhis.asha.reports;

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
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

public class GenerateRegisteredASHAListReportResultAction implements Action
{
    //public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//4.0
    
    //public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//4.0
    
    //private final String OPTION_SET_PAYMENT_STATUS_REPORT = "PaymentStatusReport";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    /*
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    */
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
    
    /*
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
    */
    
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
    
    private Integer selectedOrgUnitId;
    
    public void setSelectedOrgUnitId( Integer selectedOrgUnitId )
    {
        this.selectedOrgUnitId = selectedOrgUnitId;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception 
    {
        String raFolderName = "ra_haryana_asha";
        
        System.out.println(  " Report Generation Start Time is : " + new Date() );
        
        //SimpleDateFormat simpleDate = new SimpleDateFormat( "dd-MM-yyyy" );
        
        //Period period = periodService.getPeriodByExternalId( selectedPeriodId );
        
        //simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        SimpleDateFormat simpleDate = new SimpleDateFormat( "dd-MM-yyyy" );
        
        
        // OrgUnit Related Info
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit = organisationUnitService.getOrganisationUnit( selectedOrgUnitId ); 
        
 
        //System.out.println(  " Voucher No : " + voucherNo );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
        
        Constant constant = constantService.getConstantByName( "ASHA Activity Program" );
        
        int selProgramId = (int) constant.getValue();
        
        Program selProgram = programService.getProgram( selProgramId );
        
        List<OrganisationUnit > programSources = new ArrayList<OrganisationUnit>();
        
        programSources = new ArrayList<OrganisationUnit>( selProgram.getOrganisationUnits() );
        
        if( selProgram != null &&  programSources != null && programSources.size() > 0 )
        {
            orgUnitList.retainAll( programSources );
        }    
        
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        
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
        
        String systemId = "";
        Map<Integer, String> ashaSystemIDMap = new HashMap<Integer, String>();
            
        for( Patient asha : ashaList )
        {
            PatientIdentifierType idType = null;
            
            for ( PatientIdentifier identifier : asha.getIdentifiers() )
            {
                idType = identifier.getIdentifierType();

                if ( idType != null )
                {
                    //identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
                }
                else
                {
                    systemId = identifier.getIdentifier();
                }
            }
            ashaSystemIDMap.put( asha.getId(), systemId );
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
       
        
        String xmlFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xml" + File.separator + "ASHAMasterChart.xml";
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xls" + File.separator + "ASHAMasterChart.xls";
        
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
        
        //heet.getSettings().setProtected( true );
        //sheet.getSettings().setPassword( "ashaHaryana" );
        
        File outputReportFile = null;
        
        for( ReportCell fixedcell : fixedcells )
        {
            String tempStr = "";
            
            //String deCodeString = fixedcell.getExpression();
            
            if( fixedcell.getDatatype().equalsIgnoreCase( "FACILITY" ) )
            {
                tempStr = organisationUnit.getName();
            }
            
            else if( fixedcell.getDatatype().equalsIgnoreCase( "PRINTDATE" ) )
            {
                tempStr = simpleDate.format( new Date() );
            }

            sheet.addCell( new Label( fixedcell.getCol(), fixedcell.getRow(), tempStr, getCellFormat1() ) );
        } 
            
        
        // for dynamic cell
        
        int slNo = 1;
        int rowCount = 0;
        //int rowStart = 0;
        
       
        
        for( Patient asha : ashaList )
        {
            for( ReportCell dynamiccell : dynamiccells )
            {
                String value = "";
                
                if( dynamiccell.getDatatype().equalsIgnoreCase( "SLNO" ) )
                {
                    value = "" + slNo;
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "DISTRICT" ) )
                {
                    value = asha.getOrganisationUnit().getParent().getParent().getParent().getName();
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
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "ASHAID" ) )
                {
                    value = ""+asha.getId();
                }
 
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "SYSTEMID" ) )
                {
                    value = ashaSystemIDMap.get( asha.getId() );
                    
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "ASHAFULLNAME" ) )
                {
                    value = asha.getFullName();
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "DOB" ) )
                {
                    value =  simpleDate.format( asha.getBirthDate() );
                    //value =  simpleDate.format( asha.getBirthDate() ) + "(" +  asha.getAge() + ")";
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "ASHAFULLNAME" ) )
                {
                    value = asha.getFullName();
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "CUG" ) )
                {
                    if( asha.getPhoneNumber() != null )
                    {
                        value = asha.getPhoneNumber();
                    }
                    else
                    {
                        value = "";
                    }
                    
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "PATIENTATTRIBUTE" ) )
                {
                    value = patientAttributeValueMap.get( asha.getId() +":" + Integer.parseInt( dynamiccell.getService() )  );
                }
                
                else if( dynamiccell.getDatatype().equalsIgnoreCase( "PATIENTATTRIBUTE_DATE" ) )
                {
                    Date tempDate = format.parseDate( patientAttributeValueMap.get( asha.getId() +":" + Integer.parseInt( dynamiccell.getService() )  ) );
                    
                    if( tempDate != null )
                    {
                        value = simpleDate.format( tempDate );
                    }
                    
                    value = "";
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
            //rowStart = rowCount;
    
        } 
        
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();
        
        fileName = "ASHAMasterChartReport" + ".xls";
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



