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
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.asha.util.ReportCell;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

public class ASHAMasterChartReportAction implements Action 
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
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
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

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
    
    private String startMonth;

    public void setStartMonth( String startMonth )
    {
        this.startMonth = startMonth;
    }

    private String endMonth;
    
    public void setEndMonth( String endMonth )
    {
        this.endMonth = endMonth;
    }

    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception 
    {
        Constant constant = constantService.getConstantByName( "ASHA Activity Program" );
        
        int selProgramId = (int) constant.getValue();
        
        Program selProgram = programService.getProgram( selProgramId );
        
        OrganisationUnit selOrgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnitId )  );

        List<OrganisationUnit> programOrgUnits = new ArrayList<OrganisationUnit>( selProgram.getOrganisationUnits() );
        
        
        
        orgUnitList.retainAll( programOrgUnits );
        
        SimpleDateFormat monthFormat = new SimpleDateFormat( "MMM-yyyy" );
        Date sDate = format.parseDate( startMonth.split( "_" )[1] );
        Date eDate = format.parseDate( endMonth.split( "_" )[2] );
        
        List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( new MonthlyPeriodType(), sDate, eDate ) );
        
        String raFolderName = "ra_haryana_asha";
        
        String xmlFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xml" + File.separator + "ASHA_Master_Chart.xml";
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xls" + File.separator + "ASHA_Master_Chart.xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        List<ReportCell> headerCells = new ArrayList<ReportCell>( ashaService.getReportCells( xmlFilePath, "headercell" ) );
        List<ReportCell> groupCells = new ArrayList<ReportCell>( ashaService.getReportCells( xmlFilePath, "groupcell" ) );
        List<ReportCell> reportCells = new ArrayList<ReportCell>( ashaService.getReportCells( xmlFilePath, "reportcell" ) );
        
        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        WritableSheet sheet = outputReportWorkbook.getSheet( 0 );
        
        File outputReportFile = null;
        
        PatientAttribute sortPatientAttribute = null;
        try
        {
            
            for( ReportCell headerCell : headerCells )
            {
                String tempStr = "";
                if( headerCell.getDatatype().equalsIgnoreCase( "FACILITY-NO-REPEAT" ) )
                {
                    tempStr = selOrgUnit.getName();
                }
                else if( headerCell.getDatatype().equalsIgnoreCase( "PERIOD-NO-REPEAT" ) )
                {
                    tempStr = monthFormat.format( sDate ) + " To " + monthFormat.format( eDate );
                }
                else if( headerCell.getDatatype().equalsIgnoreCase( "SORT-ATTRIBUTE" ) )
                {
                    sortPatientAttribute = patientAttributeService.getPatientAttribute( Integer.parseInt( headerCell.getService() ) );
                    continue;
                }
                
                sheet.addCell( new Label( headerCell.getCol(), headerCell.getRow(), tempStr, getCellFormat2() ) );
            }
            
            int colCount = 0;
            for( ReportCell groupCell : groupCells )
            {
                sheet.mergeCells( groupCell.getCol()+colCount, groupCell.getRow()-2, groupCell.getCol()+colCount+periodList.size()-1, groupCell.getRow()-2 );
                sheet.addCell( new Label( groupCell.getCol()+colCount, groupCell.getRow()-2, groupCell.getDatatype(), getCellFormat1() ) );
                
                for( Period period : periodList )
                {
                    sheet.addCell( new Label( groupCell.getCol()+colCount, groupCell.getRow()-1, monthFormat.format( period.getStartDate() ), getCellFormat1() ) );
                    colCount++;
                }
            }

            
            int rowCount = 0;
            int slNo = 1;
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                List<Patient> patientList = new ArrayList<Patient>( patientService.getPatients( orgUnit, sortPatientAttribute, null, null ) );
                if( patientList == null || patientList.size() == 0 ) 
                {
                    System.out.println( "Patient List is empty ");
                    continue;
                }
                
                Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers(Patient.class, patientList ) );
                String patientIdsByComma = getCommaDelimitedString( patientIds );
                Map<String, String> patientAttributeValueMap = new HashMap<String, String>( ashaService.getPatientAttributeValues( patientIdsByComma ) );
                //System.out.println( "patientAttributeValueMap" + patientAttributeValueMap.size() );
                Map<String, String> patientDataValueMap = new HashMap<String, String>( ashaService.getPatientDataValues( patientIdsByComma, periodList, 1, 1 ));
                
                for( Patient patient : patientList )
                {
                    String orgUnitBranch = ashaService.getOrgunitBranch( orgUnit );
                    colCount = 0;
                    for( ReportCell reportCell : reportCells )
                    {
                        String tempStr = "";
                        if( reportCell.getDatatype().equalsIgnoreCase( "SLNO" ) )
                        {
                            tempStr = "" + slNo;
                        }
                        else if( reportCell.getDatatype().equalsIgnoreCase( "HIERARCHY" ) )
                        {
                            tempStr = "" + orgUnitBranch;
                        }
                        else if( reportCell.getDatatype().equalsIgnoreCase( "FACILITY-REPEAT" ) )
                        {
                            tempStr = orgUnit.getName();
                        }
                        else if( reportCell.getDatatype().equalsIgnoreCase( "PNAME" ) )
                        {
                            tempStr = patient.getFullName();
                        }
                        else if( reportCell.getDatatype().equalsIgnoreCase( "PCONTACTNUMBER" ) )
                        {
                            tempStr = patient.getPhoneNumber();
                        }
                        else if( reportCell.getDatatype().equalsIgnoreCase( "PA:ADDRESS_CONTACTNUMBER" ) )
                        {
                            tempStr = "";
                            String tempArray[] = reportCell.getService().split( "," );
                            for( int i = 0; i < tempArray.length; i++ )
                            {
                                if( patientAttributeValueMap.get( patient.getId()+":"+tempArray[i] ) != null )
                                {
                                    tempStr = tempStr + " " + patientAttributeValueMap.get( patient.getId()+":"+tempArray[i] );
                                }
                            }
                            tempStr = tempStr + " " + patient.getPhoneNumber();
                        }
                        else if( reportCell.getDatatype().equalsIgnoreCase( "PA" ) )
                        {
                            tempStr = "";
                            String tempArray[] = reportCell.getService().split( "," );
                            for( int i = 0; i < tempArray.length; i++ )
                            {
                                if( patientAttributeValueMap.get( patient.getId()+":"+tempArray[i] ) != null )
                                {
                                    tempStr = tempStr + " " + patientAttributeValueMap.get( patient.getId()+":"+tempArray[i] );
                                }
                            }
                        }
                        
                        if( tempStr == null ) tempStr = "";
                        sheet.addCell( new Label( reportCell.getCol()+colCount, reportCell.getRow()+rowCount, tempStr, getCellFormat3() ) );                    
                    }
                    
                    
                    for( ReportCell groupCell : groupCells )
                    {
                        for( Period period : periodList )
                        {
                            String tempStr = patientDataValueMap.get( patient.getId()+":"+period.getId()+":"+groupCell.getService() );
                            //if( tempStr == null ) tempStr = "NA";
                            if( tempStr == null ) tempStr = "";
                            
                            sheet.addCell( new Label( groupCell.getCol()+colCount, groupCell.getRow()+rowCount, tempStr, getCellFormat3() ) );
                            colCount++;
                        }
                    }
                    
                    slNo++;
                    rowCount++;
                }
                
            }
            
            outputReportWorkbook.write();
            outputReportWorkbook.close();
            fileName = "ASHAMasterChart_"+selOrgUnit.getShortName()+".xls";
            outputReportFile = new File( outputReportPath );
            inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
            outputReportFile.deleteOnExit();
        }
        catch( Exception e )
        {
            System.out.println( "Exception Occured in ASHAMasterChartReportAction : "+ e.getMessage() );
            e.printStackTrace();
        }
        finally
        {
            //outputReportWorkbook.close();
            //outputReportFile.deleteOnExit();
        }

        return SUCCESS;
    }

    
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_50 );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );                        
        
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat3() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.NO_BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }
    
}
