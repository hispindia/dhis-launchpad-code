package org.hisp.dhis.ovc.school;

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
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.ovc.util.ReportCell;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateSchoolDetailsReoprtAction implements Action
{
    public static final String OVC_ID = "OVC_ID";//929.0
    
    public static final String SCHOOL_DETAILS_ATTRIBUTE_GROUP_ID = "School Details Attribute Group Id";// 1202
    
    public static final String OVC_SCHOOL_MANAGEMENT_PROGRAM = "OVC_SCHOOL_MANAGEMENT_PROGRAM";//1700.0

    public static final String OVC_SCHOOL_MANAGEMENT_PROGRAM_STAGE = "OVC_SCHOOL_MANAGEMENT_PROGRAM_STAGE";// 1703.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    /*
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    */
    
    private OVCService ovcService;

    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
   private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }
    
   private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
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
    
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------

    private int schoolId;
    
    public void setSchoolId( int schoolId )
    {
        this.schoolId = schoolId;
    }
   
    private String startPeriod;
    
    public void setStartPeriod( String startPeriod )
    {
        this.startPeriod = startPeriod;
    }

    /*
    private int organisationUnitId;
    
    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    */
    
    private School school;
    
    private OrganisationUnit organisationUnit;
    
    private Period period;
    
    private List<Patient> ovcList = new ArrayList<Patient>();
    
    private Map<String, String> patientAttributeValueMap = new HashMap<String, String>();
    
    private Map<Integer, String> ovcIDMap = new HashMap<Integer, String>();
    
    private Program program;
    
    private ProgramStage programStage;
    
    //private Collection<ProgramStageDataElement> programStageDataElements;
    
    public Map<String, String> patientDataValueMap;
    
    private PatientAttributeGroup attributeGroup;
    
    //private Map<Integer, PatientAttribute> patientAttributeMap;
    
    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();
    
    private Map<Integer, String> schoolDetailsValueMap = new HashMap<Integer, String>();
    
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
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        school = schoolService.getSchool( schoolId );
        
        System.out.println( school.getName() + " :  Report Generation Start Time is : " + new Date() );
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        organisationUnit  = school.getOrganisationUnit();
        
        period = PeriodType.createPeriodExternalId( startPeriod );
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String periodLastDate = dateFormat.format( period.getEndDate() );
        
        period.setName( format.formatPeriod( period ) );
        
        ovcList = new ArrayList<Patient>( ovcService.getPatients( school.getId(), periodLastDate ) );
        
        Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers(Patient.class, ovcList ) );
        
        String patientIdsByComma = getCommaDelimitedString( patientIds );
        
        Constant patientIdentifierTypeConstant = constantService.getConstantByName( OVC_ID );
        
        PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( (int) patientIdentifierTypeConstant.getValue() );
        
        Constant attributeConstant = constantService.getConstantByName( SCHOOL_DETAILS_ATTRIBUTE_GROUP_ID );
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroup( (int) attributeConstant.getValue() );
        
        //patientAttributeMap = new HashMap<Integer, PatientAttribute>();
        
        //patientAttributes = new ArrayList<PatientAttribute>();
        
        patientAttributes = new ArrayList<PatientAttribute>( attributeGroup.getAttributes() );
        
        /*
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeMap.put( patientAttribute.getId(), patientAttribute );
        }
        */
        
        Collection<Integer> patientAttributeIds = new ArrayList<Integer>( getIdentifiers( PatientAttribute.class, patientAttributes ) );
        
        String patientAttributeIdsByComma = getCommaDelimitedString( patientAttributeIds );
        
        if( patientAttributeIds != null &&  patientAttributeIds.size() > 0 )
        {
            schoolDetailsValueMap = ovcService.getSchoolDetailValues( school.getId(), patientAttributeIdsByComma );
        }
        
        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------
        
        for ( Patient patient : ovcList )
        {
            Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( patient );

            for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
            {
                if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute().getValueType() ) )
                {
                    patientAttributeValueMap.put( patient.getId() +":" + patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getPatientAttributeOption().getName() );
                }
                else
                {
                    patientAttributeValueMap.put( patient.getId() + ":" + patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getValue() );
                }
            }
            
            String ovcId = "";
            
            if ( school.getOrganisationUnit().getCode() != null && identifierType != null )
            {
                PatientIdentifier ovcIdIdentifier = patientIdentifierService.getPatientIdentifier( identifierType, patient );
                
                if( ovcIdIdentifier != null )
                {
                    ovcId =   ovcIdIdentifier.getIdentifier() ;
                }
               
                ovcIDMap.put( patient.getId(), ovcId );
            }
            else
            {
                PatientIdentifierType idType = null;
                
                for ( PatientIdentifier identifier : patient.getIdentifiers() )
                {
                    idType = identifier.getIdentifierType();

                    if ( idType != null )
                    {
                        //identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
                    }
                    else
                    {
                        ovcId = identifier.getIdentifier();
                    }
                }
                ovcIDMap.put( patient.getId(), ovcId );
            }
        }
        
        Constant programConstant = constantService.getConstantByName( OVC_SCHOOL_MANAGEMENT_PROGRAM );
        Constant programStageConstant = constantService.getConstantByName( OVC_SCHOOL_MANAGEMENT_PROGRAM_STAGE );
        
        // program and programStage Related information
        program = programService.getProgram( (int) programConstant.getValue() );

        programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        
        //programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );

        if( patientIds != null &&  patientIds.size() > 0 )
        {
            if( program != null &&  programStage != null )
            {
                patientDataValueMap = ovcService.getPatientDataValuesByExecutionDate( patientIdsByComma, program.getId(), programStage.getId(), period.getStartDateString()  );
            }
        }
       
        String raFolderName = "ra_kenya";
        
        String xmlFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xml" + File.separator + "OVC_School_fees_application_form.xml";
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xls" + File.separator + "OVC_School_fees_application_form.xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        
        List<ReportCell> fixedcells = new ArrayList<ReportCell>( ovcService.getReportCells( xmlFilePath, "fixedcells" ) );
        List<ReportCell> dynamiccells = new ArrayList<ReportCell>( ovcService.getReportCells( xmlFilePath, "dynamiccells" ) );
        
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        
        //System.out.println( " inputTemplatePath : " + inputTemplatePath );
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        WritableSheet sheet = outputReportWorkbook.getSheet( 0 );
        
        File outputReportFile = null;
        
        //int slNo = 1;
        int rowStart = 0;
        
        double temp = 0.0;
        double tempvalue = 0.0;
        
        //try
        //{
            for( ReportCell fixedcell : fixedcells )
            {
                String tempStr = "";
                
                //String deCodeString = fixedcell.getExpression();
                
                if( fixedcell.getDatatype().equalsIgnoreCase( "CSO-NAME" ) )
                {
                    tempStr = organisationUnit.getName();
                }
                
                if( fixedcell.getDatatype().equalsIgnoreCase( "CSO-CONTACT-PERSON-ONE" ) )
                {
                    tempStr = organisationUnit.getContactPerson();
                }
                
                if( fixedcell.getDatatype().equalsIgnoreCase( "CSO-CONTACT-PERSON-TWO" ) )
                {
                    tempStr = "";
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "REGION" ) )
                {
                    if( organisationUnit.getParent().getParent().getParent().getParent() != null )
                    {
                        tempStr = organisationUnit.getParent().getParent().getParent().getParent().getName();
                    }
                    else
                    {
                        tempStr = organisationUnit.getParent().getParent().getParent().getName();
                    }
                    
                }
                else if( fixedcell.getDatatype().equalsIgnoreCase( "COUNTY" ) )
                {
                    tempStr = organisationUnit.getParent().getParent().getParent().getName();
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "SUB-COUNTY" ) )
                {
                    tempStr = organisationUnit.getParent().getParent().getName();
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "DIVISION" ) )
                {
                    tempStr = "";
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "DATE" ) )
                {
                    tempStr = simpleDateFormat.format( new Date() );
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "SCHOOL-NAME" ) )
                {
                    tempStr = school.getName();
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "PATIENT-ATTRIBUTE" ) )
                {
                    tempStr = schoolDetailsValueMap.get( Integer.parseInt( fixedcell.getExpression() ) );
                }
                
                
                /*
                else if( fixedcell.getDatatype().equalsIgnoreCase( "CSO-CHAIR-PERSON-NAME-2" ) )
                {
                    tempStr = schoolDetailsValueMap.get( Integer.parseInt( fixedcell.getExpression() ) );
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "E-MAIL" ) )
                {
                    tempStr = schoolDetailsValueMap.get( Integer.parseInt( fixedcell.getExpression() ) );
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "TEL-NO" ) )
                {
                    tempStr = schoolDetailsValueMap.get( Integer.parseInt( fixedcell.getExpression() ) );
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "PRINICIPAL-NAME" ) )
                {
                    tempStr = schoolDetailsValueMap.get( Integer.parseInt( fixedcell.getExpression() ) );
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "BANK" ) )
                {
                    tempStr = schoolDetailsValueMap.get( Integer.parseInt( fixedcell.getExpression() ) );
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "BRANCH" ) )
                {
                    tempStr = schoolDetailsValueMap.get( Integer.parseInt( fixedcell.getExpression() ) );
                }
                
                else if( fixedcell.getDatatype().equalsIgnoreCase( "BRANCH" ) )
                {
                    tempStr = schoolDetailsValueMap.get( Integer.parseInt( fixedcell.getExpression() ) );
                }
                */
                
                sheet.addCell( new Label( fixedcell.getColno(), fixedcell.getRowno(), tempStr, getCellFormat1() ) );
            } 
                
            
            
            // for dynamic cell
                
                int slNo = 1;
                int rowCount = 0;
                
                for( Patient ovc : ovcList )
                {
                    for( ReportCell dynamiccell : dynamiccells )
                    {
                        String value = "";
                        
                        if( dynamiccell.getDatatype().equalsIgnoreCase( "SL-NO" ) )
                        {
                            value = "" + slNo;
                        }
                        
                        else if( dynamiccell.getDatatype().equalsIgnoreCase( "OVC-FULL-NAME" ) )
                        {
                            value = ovc.getFullName();
                        }
                        
                        else if( dynamiccell.getDatatype().equalsIgnoreCase( "GENDER" ) )
                        {
                            value = ovc.getGender();
                        }
                        
                        else if( dynamiccell.getDatatype().equalsIgnoreCase( "PATIENT-ATTRIBUTE" ) )
                        {
                            value = patientAttributeValueMap.get( ovc.getId() +":" + Integer.parseInt( dynamiccell.getExpression() )  );
                        }
                        
                        else if( dynamiccell.getDatatype().equalsIgnoreCase( "OVC-CODE" ) )
                        {
                            value = ovcIDMap.get( ovc.getId() );
                        }
                        
                        else if( dynamiccell.getDatatype().equalsIgnoreCase( "DATAELEMENT" ) )
                        {
                            value = patientDataValueMap.get( ovc.getId() +":" + Integer.parseInt( dynamiccell.getService() ) );
                            
                            //System.out.println( dynamiccell.getExpression() );
                            if( dynamiccell.getExpression().equalsIgnoreCase( "AWARDED" ) )
                            {
                                try
                                {
                                    tempvalue = Double.valueOf( value );
                                }
                                catch ( Exception e )
                                {
                                    tempvalue = 0.0;
                                   
                                }
                                
                                temp += tempvalue;
                                //System.out.println(  " : temp  : " + temp );
                            }
                            
                        }
                        
                        try
                        {
                            sheet.addCell( new Number( dynamiccell.getColno(), dynamiccell.getRowno()+rowCount, Double.parseDouble( value ), getCellFormat2() ) );
                        }
                        catch ( Exception e )
                        {
                            sheet.addCell( new Label( dynamiccell.getColno(), dynamiccell.getRowno()+rowCount, value, getCellFormat2() ) ); 
                        }
                        
                        //sheet.addCell( new jxl.write.Number( c, r, val, st ) );
                        
                        
                        //sheet.addCell( new Label( dynamiccell.getColno(), dynamiccell.getRowno()+rowCount, value, getCellFormat2() ) );   
                    }
                    
                    rowCount++;
                    slNo++;
                    rowStart = rowCount;
                }
           
            rowStart = 26 + rowStart;
            
            //System.out.println(  " : rowStart  : " + rowStart );
            
            
            //System.out.println(  " : Total Awarded Final Value is  : " + temp );
            
            //rowStart++;
            //rowStart++;
            //rowStart++;
            
            
            int colStart = 0;
            
            sheet.addCell( new Label( colStart+1, rowStart, "Part V", getCellFormat3() ) );
            
            sheet.mergeCells( colStart+2 , rowStart, colStart + 8, rowStart );
            sheet.addCell( new Label( colStart+2, rowStart, "APHIAPlus Western Kenya County: County Team Approvals Only", getCellFormat3() ) );
            
            rowStart++;
            
            sheet.addCell( new Label( colStart+2, rowStart, "Actual Total fees/Levy amount Due to the School: Kshs:", getCellFormat4() ) );
            sheet.addCell( new Number( colStart+3, rowStart, (int) temp , getCellFormat2() ) );
            
            sheet.addCell( new Label( colStart+4, rowStart, "Total No. of OVC", getCellFormat4() ) );
            sheet.addCell( new Number( colStart+5, rowStart, ovcList.size(), getCellFormat2() ) );
            sheet.addCell( new Label( colStart+6, rowStart, "", getCellFormat1() ) );
            sheet.addCell( new Label( colStart+7, rowStart, "", getCellFormat1() ) );
            sheet.addCell( new Label( colStart+8, rowStart, "", getCellFormat1() ) );
            
            rowStart++;
            
            sheet.addCell( new Label( colStart+1, rowStart, "", getCellFormat1() ) );
            sheet.addCell( new Label( colStart+2, rowStart, "Reviewed By: County SDH Officer Name:", getCellFormat4() ) );
            sheet.addCell( new Label( colStart+3, rowStart, "" , getCellFormat1() ) );
            
            sheet.addCell( new Label( colStart+4, rowStart, "Sign", getCellFormat4() ) );
            sheet.addCell( new Label( colStart+5, rowStart, "", getCellFormat1() ) );
            
            sheet.addCell( new Label( colStart+6, rowStart, "Date", getCellFormat4() ) );
            sheet.addCell( new Label( colStart+7, rowStart, "", getCellFormat1() ) );
            sheet.addCell( new Label( colStart+8, rowStart, "", getCellFormat1() ) );
            
            
            rowStart++;
            
            sheet.addCell( new Label( colStart+1, rowStart, "", getCellFormat1() ) );
            sheet.addCell( new Label( colStart+2, rowStart, "Approved By: County cordinator Name:", getCellFormat4() ) );
            sheet.addCell( new Label( colStart+3, rowStart, "" , getCellFormat1() ) );
            
            sheet.addCell( new Label( colStart+4, rowStart, "Sign", getCellFormat4() ) );
            sheet.addCell( new Label( colStart+5, rowStart, "", getCellFormat1() ) );
            
            sheet.addCell( new Label( colStart+6, rowStart, "Date", getCellFormat4() ) );
            sheet.addCell( new Label( colStart+7, rowStart, "", getCellFormat1() ) );
            sheet.addCell( new Label( colStart+8, rowStart, "", getCellFormat1() ) );
            
            
            outputReportWorkbook.write();
            outputReportWorkbook.close();
            
            //fileName = "OVC_School_fees_application_form_"+selOrgUnit.getShortName()+".xls";
            fileName = "OVC_School_fees_application_form" + ".xls";
            outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
            
            outputReportFile.deleteOnExit();
        //}
        
        /*
        catch( Exception e )
        {
            System.out.println( "Exception Occured in GenerateSchoolDetailsReoprtAction : "+ e.getMessage() );
            e.printStackTrace();
        }
        
        finally
        {
            outputReportWorkbook.close();
            outputReportFile.deleteOnExit();
        }
        */
        
        System.out.println( school.getName() + " :  Report Generation End Time is : " + new Date() );
        
        return SUCCESS;
    }
    
    
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setVerticalAlignment( VerticalAlignment.TOP );
        wCellformat.setShrinkToFit( true );
        wCellformat.setWrap( true );
    
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
    
    public WritableCellFormat getCellFormat3()
        throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( false );
        //wCellformat.setShrinkToFit( true );
        return wCellformat;
    } // e   
    
    
    public WritableCellFormat getCellFormat4()
        throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setVerticalAlignment( VerticalAlignment.TOP );
        //wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setWrap( true );
        //wCellformat.setShrinkToFit( true );
        return wCellformat;
    } // e   
    
}
