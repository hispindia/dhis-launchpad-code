package org.hisp.dhis.asha.reports;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ASHAFormat7ReporResultAction implements Action
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
    
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private int orgUnitId;
    
    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private int blockId;
    
    public void setBlockId( int blockId )
    {
        this.blockId = blockId;
    }

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
        
        OrganisationUnit district = organisationUnitService.getOrganisationUnit( orgUnitId );
        OrganisationUnit block = organisationUnitService.getOrganisationUnit( blockId );
        
        Constant constant = constantService.getConstantByName( "ASHA Activity Program" );
        
        int selProgramId = (int) constant.getValue();
        
        Program selProgram = programService.getProgram( selProgramId );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( block.getId() )  );

        List<OrganisationUnit> programOrgUnits = new ArrayList<OrganisationUnit>( selProgram.getOrganisationUnits() );
        
        orgUnitList.retainAll( programOrgUnits );
        
        
        String raFolderName = "ra_bihar_asha";
        
        String xmlFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xml" + File.separator + "ASHABlocklevelBaseRegister.xml";
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "xls" + File.separator + "ASHABlocklevelBaseRegister.xls";
        
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
            
            if( headerCell.getDatatype().equalsIgnoreCase( "DISTRICT-NAME" ) )
            {
                tempStr = district.getName();
            }
            
            else if( headerCell.getDatatype().equalsIgnoreCase( "BLOCK-NAME" ) )
            {
                tempStr = block.getName();
            }
            
            sheet.addCell( new Label( headerCell.getCol(), headerCell.getRow(), tempStr, ashaService.getCellFormat4() ) );
        }
        
        
        
        int rowCount = 0;
        int slNo = 1;
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            List<Patient> patientList = new ArrayList<Patient>( patientService.getPatients( orgUnit, null, null ) );
            if( patientList == null || patientList.size() == 0 ) 
            {
                System.out.println( orgUnit.getName() + " --  Patient List is empty ");
                continue;
            }
            
            Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers( Patient.class, patientList ) );
            String patientIdsByComma = getCommaDelimitedString( patientIds );
            Map<String, String> patientAttributeValueMap = new HashMap<String, String>( ashaService.getPatientAttributeValues( patientIdsByComma ) );
            
            for( Patient patient : patientList )
            {
                for( ReportCell reportCell : reportCells )
                {
                    String tempStr = "";
                    if( reportCell.getDatatype().equalsIgnoreCase( "SLNO" ) )
                    {
                        tempStr = "" + slNo;
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "SUBCENTER-NAME" ) )
                    {
                        tempStr = orgUnit.getName();
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "NAME" ) )
                    {
                        tempStr = patient.getFullName();
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "AGE" ) )
                    {
                        tempStr = patient.getAge();
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "PA" ) )
                    {
                        String paId = reportCell.getService();
                        
                        tempStr = patientAttributeValueMap.get( patient.getId()+ ":"+ paId );
                        
                        //System.out.println( paId + "  : " + tempStr );
                        
                        if( tempStr != null && tempStr.equalsIgnoreCase( "true" ) )
                        {
                            tempStr = "Yes";
                        }
                        else if( tempStr != null && tempStr.equalsIgnoreCase( "false" ) )
                        {
                            tempStr = "No";
                        }
                    }
                    
                    else if( reportCell.getDatatype().equalsIgnoreCase( "BANK-ACCOUNT-NUMBER" ) )
                    {
                        String paId = reportCell.getService();
                        
                        tempStr = patientAttributeValueMap.get( patient.getId()+ ":"+ paId );
                        
                    }
                    
                    if ( reportCell.getDatatype().equalsIgnoreCase( "BANK-ACCOUNT-NUMBER" ) )
                    {
                        /*
                        if( tempStr == null )
                        {
                            tempStr = "";
                        }
                        */
                        
                        sheet.addCell( new Label( reportCell.getCol(), reportCell.getRow()+rowCount, tempStr , ashaService.getCellFormat5() ) );
                    }
                    
                    else
                    {
                        try
                        {
                            sheet.addCell( new Number( reportCell.getCol(), reportCell.getRow()+rowCount, Double.parseDouble( tempStr ), ashaService.getCellFormat5() ) );
                        }
                        catch ( Exception e )
                        {
                            sheet.addCell( new Label( reportCell.getCol(), reportCell.getRow()+rowCount, tempStr, ashaService.getCellFormat5() ) ); 
                        }
                    }
                    
                }
                
                slNo++;
                rowCount++;
            }
            
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();
        fileName = "ASHA_BLOCK_LEVEL_BASE_REGISTER_"+ block.getShortName()+".xls";
        outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        outputReportFile.deleteOnExit();
        
        System.out.println( " Report Generation End Time is : " + new Date() );
        
        return SUCCESS;
    }
}