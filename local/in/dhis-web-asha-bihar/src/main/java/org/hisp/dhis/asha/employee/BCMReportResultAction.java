package org.hisp.dhis.asha.employee;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.asha.util.ReportCell;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class BCMReportResultAction implements Action
{
    public static final String BLOCK_GROUP_ID = "Block Group";//29.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }
    
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
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
    
    
    /*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private int orgUnitId;
    
    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    /*
    private List<Employee> employeeList;

    public List<Employee> getEmployeeList()
    {
        return employeeList;
    }
    */
    
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
    // Implementation Action
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        //employeeList = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnitOrderByDesignationAsc( organisationUnit ) );
        
        List<OrganisationUnit> blockList = new ArrayList<OrganisationUnit>( organisationUnit.getChildren() );
        
        Constant blockGroupIdConstant = constantService.getConstantByName( BLOCK_GROUP_ID );
        
        OrganisationUnitGroup orgUnitGroup = new OrganisationUnitGroup();
        
        orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( (int) blockGroupIdConstant.getValue() );
        
        List<OrganisationUnit> groupMember = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
        
        blockList.retainAll( groupMember );
        
        Collections.sort( blockList );
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String raFolderName = "ra_bihar_asha";
        
        String xmlFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xml" + File.separator + "BCM.xml";
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xls" + File.separator + "BCM.xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        
        System.out.println( " Report Generation Start Time is : " + new Date() );
        
        List<ReportCell> headerCells = new ArrayList<ReportCell>( ashaService.getReportCells( xmlFilePath, "headercell" ) );
        List<ReportCell> reportCells = new ArrayList<ReportCell>( ashaService.getReportCells( xmlFilePath, "reportcell" ) );

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        WritableSheet sheet = outputReportWorkbook.getSheet( 0 );
        
        File outputReportFile = null;
        
        for( ReportCell headerCell : headerCells )
        {
            String tempStr = "";
            
            if( headerCell.getDatatype().equalsIgnoreCase( "ORGUNIT-NAME" ) )
            {
                tempStr = organisationUnit.getName();
            }
            
            sheet.addCell( new Label( headerCell.getCol(), headerCell.getRow(), tempStr, ashaService.getCellFormat4() ) );
        }
       
        
        int rowCount = 0;
        int slNo = 1;
        
        for( OrganisationUnit orgUnit : blockList )
        {
            List<Employee> employeeList = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnitOrderByNameAsc( orgUnit ) );
            
            for( Employee employee : employeeList )
            {
                for( ReportCell reportCell : reportCells )
                {
                    String tempStr = "";
                    
                    if( reportCell.getDatatype().equalsIgnoreCase( "SLNO" ) )
                    {
                        tempStr = "" + slNo;
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "BLOCK-NAME" ) )
                    {
                        tempStr = orgUnit.getName();
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "NAME" ) )
                    {
                        tempStr = employee.getName();
                    }
                    /*
                    else if( reportCell.getDatatype().equalsIgnoreCase( "DESIGNATION" ) )
                    {
                        tempStr = employee.getDesignation();
                    }
                    */
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "CONTACTNUMBER" ) )
                    {
                        tempStr = employee.getPhoneNumber();
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "EMAIL" ) )
                    {
                        tempStr = employee.getEmail();
                    }
                    else if( reportCell.getDatatype().equalsIgnoreCase( "DOB" ) )
                    {
                        tempStr = dateFormat.format( employee.getBirthDate() );
                    }
                    else if( reportCell.getDatatype().equalsIgnoreCase( "AGE" ) )
                    {
                        tempStr = ""+employeeService.getAgeFromDateOfBirth( employee.getBirthDate() );
                    }
                    else if( reportCell.getDatatype().equalsIgnoreCase( "EDUCATIONLEVEL" ) )
                    {
                        tempStr = employee.getEducationLevel();
                    }
                    else if( reportCell.getDatatype().equalsIgnoreCase( "CATEGORY" ) )
                    {
                        tempStr = employee.getCategory();
                    }
                    else if( reportCell.getDatatype().equalsIgnoreCase( "MARITALSTATUS" ) )
                    {
                        tempStr = employee.getMaritalStatus();
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "JOININGDATE" ) )
                    {
                        tempStr = dateFormat.format( employee.getJoiningDate() );
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "ORIENTATIONRECEIVED" ) )
                    {
                        if( employee.isOrientationReceived() )
                        {
                            tempStr = "Yes";
                        }
                        else
                        {
                            tempStr = "No";
                        }
                    }
                    
                    try
                    {
                        sheet.addCell( new Number( reportCell.getCol(), reportCell.getRow()+rowCount, Double.parseDouble( tempStr ), ashaService.getCellFormat5() ) );
                    }
                    catch ( Exception e )
                    {
                        sheet.addCell( new Label( reportCell.getCol(), reportCell.getRow()+rowCount, tempStr, ashaService.getCellFormat5() ) ); 
                    }
                }
                
                slNo++;
                rowCount++;
            }
        }
        

        
        outputReportWorkbook.write();
        outputReportWorkbook.close();
        fileName = "BCM_"+ organisationUnit.getName()+".xls";
        outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
       
        System.out.println( " Report Generation End Time is : " + new Date() );
        
        return SUCCESS;
    }
}
