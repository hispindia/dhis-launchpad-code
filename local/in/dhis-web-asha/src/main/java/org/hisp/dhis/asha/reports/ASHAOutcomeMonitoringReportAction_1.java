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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.asha.util.ReportCell;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class ASHAOutcomeMonitoringReportAction_1 implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

/*    
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
*/
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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception 
    {
        String raFolderName = "ra_haryana_asha";
        
        SimpleDateFormat monthFormat = new SimpleDateFormat( "MMM-yyyy" );
        String executionDate = startMonth.split( "_" )[1];
        Date sDate = format.parseDate( executionDate );
        
        String xmlFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xml" + File.separator + "ASHA_Outcome_Monitoring.xml";
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xls" + File.separator + "ASHA_Outcome_Monitoring.xls";
        
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

        OrganisationUnit rootOrgUnit = new OrganisationUnit();
        OrganisationUnitGroup categoryOrgUnitGroup = new OrganisationUnitGroup();
        
        try
        {
            for( ReportCell headerCell : headerCells )
            {
                String tempStr = "";
                if( headerCell.getDatatype().equalsIgnoreCase( "ROOT-ORGUNIT-ID" ) )
                {
                    rootOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( headerCell.getService() ) );
                    continue;
                }
                else if( headerCell.getDatatype().equalsIgnoreCase( "CATEGORY-ORGUNIT-GROUP-ID" ) )
                {
                    categoryOrgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( headerCell.getService() ) );
                    continue;
                }
                else if( headerCell.getDatatype().equalsIgnoreCase( "FACILITY-NO-REPEAT" ) )
                {
                    tempStr = rootOrgUnit.getName();
                }
                else if( headerCell.getDatatype().equalsIgnoreCase( "PERIOD-NO-REPEAT" ) )
                {
                    tempStr = monthFormat.format( sDate );
                }
                
                sheet.addCell( new Label( headerCell.getCol(), headerCell.getRow(), tempStr, ashaService.getCellFormat2() ) );
            }
            
            List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( rootOrgUnit.getId() ) );
            
            List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>( categoryOrgUnitGroup.getMembers() );
            
            orgUnitList.retainAll( orgUnitGroupMembers );
            
            Map<String, Set<Integer>> patientIdMap = new HashMap<String, Set<Integer>>();
            Map<OrganisationUnit, Double> patientGradePercentageMap = new HashMap<OrganisationUnit, Double>();
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                List<OrganisationUnit> tempOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
                Collection<Integer> tempOrgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, tempOrgUnitList ) );
                String orgUnitIdsByComma = getCommaDelimitedString( tempOrgUnitIds );

                for( ReportCell groupCell : groupCells )
                {
                    Integer prograStageId =  Integer.parseInt( groupCell.getService().split( ":" )[0] );
                    String dataElementIdsByComma = groupCell.getService().split( ":" )[0];
                    Set<Integer> tempPatientIds = new HashSet<Integer>( ashaService.getPatientListByDataCount( orgUnitIdsByComma, dataElementIdsByComma, executionDate, prograStageId ) );
                    patientIdMap.put( groupCell.getDatatype(), tempPatientIds );
                }

                Integer patientCount = 0;
                Set<Integer> patientIds = new HashSet<Integer>( ashaService.getPatientListByOrgunit( orgUnitIdsByComma ) );
                for( Integer patientId : patientIds )
                {
                    boolean isEligible = true;
                    for( ReportCell groupCell : groupCells )
                    {
                        Set<Integer> tempPatientIds = new HashSet<Integer>( patientIdMap.get( groupCell.getDatatype() ) );
                        if( !tempPatientIds.contains( patientId ) )
                        {
                            isEligible = false;
                            break;
                        }
                    }
                    
                    if( isEligible )
                    {
                        patientCount++;
                    }
                }
                
                try
                {
                    Double gradePercentage = (double) patientCount / (double) patientIds.size();
                    patientGradePercentageMap.put( orgUnit, gradePercentage );
                }
                catch( Exception e )
                {
                    patientGradePercentageMap.put( orgUnit, 0.0 );
                }
            }
            
            int rowCount = 0;
            int slNo = 1;
            for( OrganisationUnit orgUnit : orgUnitList )
            {
                String orgUnitBranch = ashaService.getOrgunitBranch( orgUnit );
                int colCount = 0;
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
                    else if( reportCell.getDatatype().equalsIgnoreCase( "CATEGORY" ) )
                    {
                        tempStr = "" + orgUnit.getName();
                        Double gradePercentage = patientGradePercentageMap.get( orgUnit );
                        if( gradePercentage >= 76 )
                        {
                            sheet.addCell( new Label( reportCell.getCol()+colCount, reportCell.getRow()+rowCount, tempStr, ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+1, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+2, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+3, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                        }
                        else if( gradePercentage >= 51 && gradePercentage <= 75 )
                        {
                            sheet.addCell( new Label( reportCell.getCol()+colCount, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+1, reportCell.getRow()+rowCount, tempStr, ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+2, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+3, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                        }
                        else if( gradePercentage >= 26 && gradePercentage <= 50 )
                        {
                            sheet.addCell( new Label( reportCell.getCol()+colCount, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+1, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+2, reportCell.getRow()+rowCount, tempStr, ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+3, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                        }
                        else
                        {
                            sheet.addCell( new Label( reportCell.getCol()+colCount, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+1, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+2, reportCell.getRow()+rowCount, "", ashaService.getCellFormat3() ) );
                            sheet.addCell( new Label( reportCell.getCol()+colCount+3, reportCell.getRow()+rowCount, tempStr, ashaService.getCellFormat3() ) );
                        }
                        
                        continue;
                    }
                    
                    //if( tempStr == null ) tempStr = "";
                    
                    sheet.addCell( new Label( reportCell.getCol()+colCount, reportCell.getRow()+rowCount, tempStr, ashaService.getCellFormat3() ) );                    
                }
                slNo++;
                rowCount++;
            }
            
            outputReportWorkbook.write();
            outputReportWorkbook.close();
            fileName = "ASHA_Outcome_Monitoring_"+monthFormat.format( sDate )+".xls";
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
}
