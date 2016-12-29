package org.hisp.dhis.reports.scheduling.controller.programewise;

/*
 * Copyright (c) 2004-2009, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.reports.comparator.Report_inNameComparator;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;

public class ScheduleProgrameWiseReports11 implements com.opensymphony.xwork2.Action 
{	
	private static final Log log=LogFactory.getLog(ScheduleProgrameWiseReports11.class);

    private static final String DHIS2_HOME="DHIS2_HOME";
    
	private final int ROOT_ORGANISATION_UNIT_ID=14;    
    
	private final String RA_FOLDER_NAME="ra_haryana";

	private final String OUT_PUT_REPORT_FOLDER="reports";
		
	private final String REPORT_TYPE="Programwise OrgUnitProgress Reports";
		
	private final String OUT_PUT="output";
	
	private final String SCHEDULING_POLICY_ID="12";
	
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
	private ReportService reportService;

	public void setReportService(ReportService reportService) 
	{
		this.reportService = reportService;
	}

	private PeriodService periodService;

	public void setPeriodService(PeriodService periodService) 
	{
		this.periodService = periodService;
	}

	private OrganisationUnitService organisationUnitService;

	public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) 
	{
		this.organisationUnitService = organisationUnitService;
	}
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private SystemSettingManager systemSettingManager;
    
    public void setSystemSettingManager(SystemSettingManager systemSettingManager) 
    {
		this.systemSettingManager = systemSettingManager;
	}
    
    public SystemSettingManager getSystemSettingManager() 
    {
		return systemSettingManager;
	}
	
	private UserService userService;
    
    public void setUserService( UserService userService )
    {
        this.userService = userService;
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
    
    private String HOST_NAME; 
	
   	public String getHOST_NAME() 
   	{
   		return HOST_NAME;
   	}

   	public void setHOST_NAME(String HOST_NAME) 
   	{
   		this.HOST_NAME = HOST_NAME;
   	}

   	private Integer SMTP_PORT;
   	
   	public Integer getSMTP_PORT() 
   	{
   		return SMTP_PORT;
   	}

   	public void setSMTP_PORT(Integer SMTP_PORT) 
   	{
   		this.SMTP_PORT = SMTP_PORT;
   	}

    private List<Report_in> reportList;

    public List<Report_in> getReportList()
    {
        return reportList;
    }
    
    private List<OrganisationUnit> districtOrgUnitList;

    public List<OrganisationUnit> getDistrictOrgUnitList()
    {
        return districtOrgUnitList;
    }
    
    private List<OrganisationUnit> stateOrgUnitList;

    public List<OrganisationUnit> getStateOrgUnitList() 
    {
		return stateOrgUnitList;
	}
    
	private String zipOutPutFilePath;
    
    private List<OrganisationUnit> orgUnitList;

    private SimpleDateFormat simpleDateFormat;

    private String reportFileNameTB;

    private String reportModelTB;

    private Date sDate;

    private Date eDate;

    private String raFolderName;
    
    private Period selectedPeriod;
    
    private Integer monthCount;              
    	
	private ArrayList<Period> monthlyPeriods;
	
	private List<OrganisationUnit> orgUnitListFromRoot;
	
	private PeriodType periodTypeObj;
	
	private Date presentDate;
	
	private SimpleDateFormat monthFormat;

	private SimpleDateFormat yearFormat;

	private SimpleDateFormat simpleMonthFormat;
	
	private SimpleDateFormat dateFormat;
	
	private String outputReportPath;
	
	private String USER_NAME;  
	
    private String PASSWORD; 
        
    private String pathOfZipFile=null;
                
    private String EMAIL_RECEIVER=null;
    
    private String EMAIL_SUBJECT=null;
    
    private String BODY=null;
    
    private String ATTACHMENT_PATH=null;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        //Initialization        
        raFolderName = reportService.getRAFolderName();
        simpleDateFormat = new SimpleDateFormat("MMM-yyyy");
		dateFormat = new SimpleDateFormat("dd");
		monthFormat = new SimpleDateFormat("MMMM");
		yearFormat = new SimpleDateFormat("yyyy");
		simpleMonthFormat = new SimpleDateFormat("MMM");
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );  
        
		presentDate = new Date();       
        //OrgUnit Related Info
        orgUnitList = new ArrayList<OrganisationUnit>();       
        orgUnitListFromRoot = new ArrayList<OrganisationUnit>(organisationUnitService.getOrganisationUnitWithChildren(ROOT_ORGANISATION_UNIT_ID));       
		for (OrganisationUnit ou : orgUnitListFromRoot) {
			if(organisationUnitService.getLevelOfOrganisationUnit(ou.getId()) == 3){
				orgUnitList.add(ou);
			}
		}		
                            
        //Period Related Info        
		selectedPeriod = new Period();
		MonthlyPeriodType monthlyPeriodType = new MonthlyPeriodType();
		monthlyPeriods = new ArrayList<Period>(periodService.getPeriodsByPeriodType(monthlyPeriodType));	
		selectedPeriod=new ArrayList<Period>(monthlyPeriods).get(monthlyPeriods.size()-2);		
		sDate=selectedPeriod.getStartDate();
        eDate=selectedPeriod.getEndDate();        
        Calendar monthStart = Calendar.getInstance();
        Calendar monthEnd = Calendar.getInstance();        
        monthStart.setTime( sDate );
        monthEnd.setTime( eDate );        
        
        //for January,February,March,April,May,June,July,August,September,October,November,December
        int financialMonthOrder[] = { 10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9 };       
        monthCount = financialMonthOrder[ monthEnd.get( Calendar.MONTH ) ];                          
        
        int slno = 1;
        int orgUnitCount = 0;
		int totalreports = 0;

        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while (it.hasNext()) {
        	OrganisationUnit currentOrgUnit = it.next();
        	
			//Get Report List
	        periodTypeObj = selectedPeriod.getPeriodType();                
	        reportList = new ArrayList<Report_in>(reportService.getReportsByPeriodSourceAndReportType(periodTypeObj, currentOrgUnit, REPORT_TYPE));           
	        Collections.sort(reportList, new Report_inNameComparator());
	        
	        for(Report_in selReportObj:reportList){
	        	if(selReportObj.isScheduled() && selReportObj.getSchedulingPolicyId().equalsIgnoreCase(SCHEDULING_POLICY_ID)){
		        	String deCodesXMLFileName="";
		            deCodesXMLFileName=selReportObj.getXmlTemplateName();
		            reportModelTB=selReportObj.getModel();
		            reportFileNameTB=selReportObj.getExcelTemplateName();	            
		            String selectedReportName=selReportObj.getName();
		            String selectedReport="";
		            for(int i=0;selectedReportName.length()>i;i++){
		            	char character=selectedReportName.charAt(i);          
		            	if(Character.isUpperCase(character)){ 
		            		selectedReport=selectedReport+character;
		                } 
		            	else if(Character.isLowerCase(character)){ 
		            		char currentCharToUpperCase=Character.toUpperCase(character);
		            		selectedReport=selectedReport+currentCharToUpperCase;
		                } 	
		            	else if(Character.isWhitespace(character)){
		            		selectedReport=selectedReport+"_";
		                }
		                else{ 
		                	selectedReport=selectedReport+character;
		            	}	            	
		            }
		            
		            
		            //Getting DataValues
		            String inputTemplatePath=System.getenv("DHIS2_HOME")+File.separator+RA_FOLDER_NAME+File.separator+"template"+File.separator+reportFileNameTB;	           	            	            
		            String outputReportPath=System.getenv(DHIS2_HOME)
							+ File.separator+RA_FOLDER_NAME
							+ File.separator+OUT_PUT_REPORT_FOLDER
							+ File.separator+selectedReport
							+ File.separator+dateFormat.format(presentDate).trim()+"-"+monthFormat.format(presentDate).trim()+"-"+yearFormat.format(presentDate).trim()
							+ File.separator+simpleDateFormat.format(selectedPeriod.getStartDate()).trim()
							//+ File.separator+currentOrgUnit.getParent().getParent().getShortName().trim() 
							+ File.separator+currentOrgUnit.getParent().getShortName()
							+ File.separator+currentOrgUnit.getShortName()
							+ File.separator+selReportObj.getName();
		            
		            File newdir=new File(outputReportPath);
					if (!newdir.exists()) {
						newdir.mkdirs();
					}				
					
					//outputReportPath = outputReportPath + File.separator + reportFileNameTB + ".xls";	
					outputReportPath = outputReportPath + File.separator + reportFileNameTB;				
			        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
			        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );        
			        WritableCellFormat wCellformat = new WritableCellFormat();
			        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
			        wCellformat.setAlignment( Alignment.CENTRE );
			        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
			        wCellformat.setWrap( true );
		        
		            int count1=0;
		            List<Report_inDesign> reportDesignList=reportService.getReportDesign( deCodesXMLFileName );
		            Iterator<Report_inDesign> reportDesignIterator=reportDesignList.iterator();
		            while ( reportDesignIterator.hasNext() )
		            {
		                Report_inDesign report_inDesign=(Report_inDesign) reportDesignIterator.next();
		                String deType=report_inDesign.getPtype();
		                String sType=report_inDesign.getStype();
		                String deCodeString=report_inDesign.getExpression();
		                String tempStr = "";
		                                
		                Calendar tempStartDate=Calendar.getInstance();
		                Calendar tempEndDate=Calendar.getInstance();
		                List<Calendar> calendarList=new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, selectedPeriod ) );
		                                
		                if ( calendarList == null || calendarList.isEmpty() )
		                {
		                    tempStartDate.setTime( selectedPeriod.getStartDate() );
		                    tempEndDate.setTime( selectedPeriod.getEndDate() );
		                    return SUCCESS;
		                }
		                else
		                {
		                    tempStartDate = calendarList.get( 0 );
		                    tempEndDate = calendarList.get( 1 );
		                }
		                                
		                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )                    
		                {
		                    tempStr = currentOrgUnit.getName();
		                }
		                
		                else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
		                {
		                    tempStr = currentOrgUnit.getName();
		                }
		                
		                else if ( deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
		                {
		                    tempStr = simpleDateFormat.format( sDate );
		                }
		                
		                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
		                {
		                    tempStr = " ";
		                }
		               
		                else if( deCodeString.equalsIgnoreCase( "SLNo" ) )
		                {
		                    tempStr = "" + slno;
		                } 
		                
		                else
		                {
		                    if ( sType.equalsIgnoreCase( "dataelement" ) )
		                    {
		                        tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
		                    }
		                    
		                    else if ( sType.equalsIgnoreCase( "orgunitgroupdata" ) )
		                    {
		                        String orgunitGroups = deCodeString.split( "--" )[0];                        
		                        String deExp = deCodeString.split( "--" )[1];
		                                                
		                        List<OrganisationUnit> orgUnitGroupMemberList = new ArrayList<OrganisationUnit>();
		                        for( int i = 0; i < orgunitGroups.split( "," ).length; i++ )
		                        {
		                            OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt ( orgunitGroups.split( "," )[i] )  );
		                            List<OrganisationUnit> orgUnitGroupMembers  = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );                           
		                            orgUnitGroupMemberList.addAll( orgUnitGroupMembers );
		                        }
		                        
		                        List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );                       
		                        childOrgUnitTree.retainAll( orgUnitGroupMemberList );                        
		                        List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
		                        String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );                       
		                        tempStr = reportService.getResultDataValueForOrgUnitGroupMember( deExp, childOrgUnitsByComma, tempStartDate.getTime(), tempEndDate.getTime(), reportModelTB );
		                    }
		                    
		                    else if ( sType.equalsIgnoreCase( "proportionate" ) )
		                    {
		                        tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );                    
		                        if( tempStr != null && !tempStr.trim().equalsIgnoreCase( "" ) )
		                        {
		                            Double proportionateValue = ( Double.parseDouble( tempStr ) / 12 ) * monthCount;                           
		                            proportionateValue = Math.round( proportionateValue * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );                            
		                            tempStr = proportionateValue.toString();
		                        }
		                        else
		                        {
		                            tempStr = "";
		                        }
		                    }
		                    else if ( sType.equalsIgnoreCase( "formula" ) )
		                    {
		                        tempStr = deCodeString;
		                    }                   
		                }
		                
		                int tempRowNo = report_inDesign.getRowno();
		                int tempColNo = report_inDesign.getColno();
		                int sheetNo = report_inDesign.getSheetno();
		                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
		                
		                if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
		                {
		                    if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
		                    {
		                    }
		                    else
		                    {
		                        tempRowNo += orgUnitCount;
		                    }
		                    try
		                    {                      
		                        if( sType.equalsIgnoreCase( "formula" ) )
		                        {
		                            tempStr = tempStr.replace( "?", "" + ( tempRowNo + 1 ) );
		                            if( orgUnitCount == orgUnitList.size()-1 && organisationUnitService.getLevelOfOrganisationUnit(currentOrgUnit.getId()) != 1 )
		                            {
		                                sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );                               
		                            }
		                            else
		                            {
		                                sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, wCellformat ) );
		                            }
		                        }
		                        else
		                        {
		                            if( orgUnitCount == orgUnitList.size()-1 && organisationUnitService.getLevelOfOrganisationUnit(currentOrgUnit.getId()) != 1 )
		                            {
		                                if(deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
		                                {
		                                    continue;
		                                }
		                                else
		                                {
		                                    sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
		                                }                                                               
		                            }
		                            else
		                            {
		                                sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
		                            }
		                        }
		                    }
		                    catch( Exception e )
		                    {
		                        if( orgUnitCount == orgUnitList.size()-1 && organisationUnitService.getLevelOfOrganisationUnit(currentOrgUnit.getId()) != 1 )
		                        {
		                            if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
		                            {
		                                continue;
		                            }
		                            else
		                            {
		                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
		                            }                           
		                        }
		                        else
		                        {
		                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
		                        }
		                    }
		                }
		                count1++;
		            }
	            	outputReportWorkbook.write();
	            	outputReportWorkbook.close();
	            	totalreports++;         	        		
		        
	            	//Email Info       
	        	    zipOutPutFilePath=System.getenv(DHIS2_HOME) + File.separator+RA_FOLDER_NAME+File.separator+OUT_PUT;	                       
	        	    HOST_NAME=systemSettingManager.getEmailHostName();
	        	    SMTP_PORT=systemSettingManager.getEmailPort();
	        	    USER_NAME=systemSettingManager.getEmailUsername();
	        	    PASSWORD=systemSettingManager.getEmailPassword();
	        	    	        	    	        	            
	        	    EMAIL_SUBJECT="Find Attachment Contain Reports : " + selectedReport +" Organisatonunit : "+currentOrgUnit.getName() +" Period : "+selectedPeriod.getName() +" Report Generated At : "+new Date();		    		
	        		BODY="DHIS2 Generated Custom Report. Please Do Not reply.";

	        		List<User> districtAndStateUserList = new ArrayList<User>(userService.getUsersByOrganisationUnit(currentOrgUnit.getId()));
	        	    districtAndStateUserList.addAll(userService.getUsersByOrganisationUnit(currentOrgUnit.getParent().getId()));
	        	    System.out.println("Number of district and state level users:" + districtAndStateUserList.size() );	    		
	        	    List<User> districtAndStateUsersWitEmailId = new ArrayList<User>();		        	
	        	    for(User user:districtAndStateUserList){
	        	        if(user.getEmail() != null){
	        	        			districtAndStateUsersWitEmailId.add(user);
	        	        	}
	        	    }	
	        	        	
	        	    System.out.println("Number of district and state users With Email Ids: "+districtAndStateUsersWitEmailId.size());		    			    		
	        	    for(User user:districtAndStateUsersWitEmailId) {	    			
	        	    		outputReportPath=System.getenv(DHIS2_HOME)
	        							+ File.separator+RA_FOLDER_NAME
	        							+ File.separator+OUT_PUT_REPORT_FOLDER
	        							+ File.separator+selectedReport
	        							+ File.separator+dateFormat.format(presentDate).trim()+"-"+monthFormat.format(presentDate).trim()+"-"+yearFormat.format(presentDate).trim()
	        							+ File.separator+simpleDateFormat.format(selectedPeriod.getStartDate()).trim()
	        							+ File.separator+currentOrgUnit.getParent().getShortName()
	        							+ File.separator+currentOrgUnit.getShortName();		
	        	    			
	        	    EMAIL_RECEIVER=user.getEmail();   	  			
	        		pathOfZipFile=exportFolder(outputReportPath, reportFileNameTB, currentOrgUnit, selectedPeriod, zipOutPutFilePath);
	        			  ATTACHMENT_PATH=pathOfZipFile;
	        			    	System.out.println("HOST_NAME :"+HOST_NAME); 
	        					System.out.println("SMTP_PORT :"+SMTP_PORT); 
	        							System.out.println("USER_NAME :"+USER_NAME); 
	        									System.out.println("EMAIL_SUBJECT :"+EMAIL_SUBJECT); 
	        											System.out.println("BODY :"+BODY); 
	        													System.out.println("ATTACHMENT_PATH :"+ATTACHMENT_PATH); 
	        															System.out.println("EMAIL_RECEIVER :"+EMAIL_RECEIVER); 
	        															if(selReportObj.isEmailable()
	        																	&&(HOST_NAME!=null 
	        														    		&& SMTP_PORT!=null
	        														    		&&(SMTP_PORT.intValue()==587 || SMTP_PORT.intValue()==465 || SMTP_PORT.intValue()==25)
	        														    		&& USER_NAME!=null 
	        														    		&& USER_NAME.contains("@") 
	        														    		&& USER_NAME.contains(".") 
	        														    		&& PASSWORD!=null 
	        														    		&& EMAIL_SUBJECT!=null 
	        														    		&& BODY!=null 
	        														    		&& ATTACHMENT_PATH!=null
	        														    		&& EMAIL_RECEIVER!=null
	        																    && EMAIL_RECEIVER.contains(".") 
	        														    		&& EMAIL_RECEIVER.contains("@"))){
	        																	System.out.println("Sending email with attachment to" 
	        														    			+" User : " +USER_NAME
	        														    			+", Generated report : "+selReportObj.getName()
	        														    			+", Organisatonunit : "+currentOrgUnit.getName()
	        																		+", Period : "+selectedPeriod.getName()
	        																		+", Report Generated At : "+new Date());
	        		   sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
	        		   System.out.println("Email sent with attached reports is successfull. : " + currentOrgUnit.getName()+" : "+selectedPeriod.getName()+" : "+ selReportObj.getName()+" : Report Generation End Time is : "+new Date());
	        		   }        	    			        	    		    		    	
	        		    //inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	        		  File outputReportFile = new File( pathOfZipFile );
	        		  outputReportFile.delete();
	        	    } 	        	
	        	}	        	      	        
				orgUnitCount++;
	            slno++;
	            //System.out.println("Total number of " + selReportObj.getName() + " generated reports ==" + totalreports + " for Organisation Unit :" + currentOrgUnit.getName() + " : Report Generation End Time is : " + new Date() );
	        }	
	        //ii++;
        }                           	    			
	    return SUCCESS;
    }

	public boolean zipDirectory(String dir, String zipfile) throws IOException,IllegalArgumentException {
		try {
			// Check that the directory is a directory, and get its contents
	
			File d = new File(dir);
			if (!d.isDirectory()) {
				System.out.println(dir + " is not a directory");
				return false;
			}
			String[] entries = d.list();
			byte[] buffer = new byte[4096]; // Create a buffer for copying
			int bytesRead;
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					zipfile));
			for (int i = 0; i < entries.length; i++) {
				File f = new File(d, entries[i]);
				if (f.isDirectory()) {
					continue;// Ignore directory
				}
				FileInputStream in = new FileInputStream(f); // Stream to read															
				ZipEntry entry = new ZipEntry(f.getName()); // Make a ZipEntry
				out.putNextEntry(entry); // Store entry
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}
				in.close();
			}
			out.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	public boolean clearXfolder(String folderPath) {
		try {
			File dir = new File(folderPath);
			String[] files = dir.list();
			for (String file : files) {
				file = folderPath + File.separator + file;
				File tempFile = new File(file);
				tempFile.delete();
			}
			return true;
		} catch (Exception e) {
			log.error(e);
			return false;
		}
	}
   
    // for format the cell
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );
        return wCellformat;
    }
            
    public String exportFolder(String outputReportPath, String reportFileNameTB, OrganisationUnit selOrgUnit, Period selectedPeriod, String zipOutPutFilePath) throws IOException, IllegalArgumentException{                                   
            String zipFilePath=null;        
            ZipOutputStream out=null;
            File in=null;
            
            fileName=reportFileNameTB.replace(".xls","")+"_"+selOrgUnit.getShortName()+"_"+simpleDateFormat.format(selectedPeriod.getStartDate())+".zip";        
            zipFilePath=zipOutPutFilePath+File.separator+fileName; 
            
            try{	
                try{
    		          File zipFile=new File(zipFilePath);      
    		          if(!zipFile.exists()){		            	
    		            	in=new File(outputReportPath);
    		            	out=new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));  
    		            	addDirectoryForCompressing(in,selOrgUnit,out);
    		            	}else{		           	            	
    		            	   return zipFilePath;
    		          }
                	}catch(Exception e){		        		           
    		              ((org.apache.commons.logging.Log) log).error("Exception trying to close output stream", e);
    		              ((org.apache.commons.logging.Log) log).error(e);          
    		        }finally{                                           
                         out.flush();
                         out.close();
                    } 
                }catch(Exception e){              
                    ((org.apache.commons.logging.Log) log).error(e);          
            }                         
            return zipFilePath;
        }              
        
        public void addDirectoryForCompressing(File dirObj, OrganisationUnit selOrgUnit, ZipOutputStream out){
            try{
                File[] files=dirObj.listFiles();
                byte[] tmpBuf=new byte[1024];               
                for(int i=0;files.length>i;i++){            	
                  if(files[i].isDirectory()){
                	addDirectoryForCompressing(files[i],selOrgUnit,out); 
                	continue; 
                  }              
                  //files[i].getName().equalsIgnoreCase(selOrgUnit.getName());
                  String relativePath=null;
                  String filePath=files[i].getAbsolutePath();
                  String[] spFilePath=filePath.split(Pattern.quote(File.separator));
                  for(int j=0;j<spFilePath.length;j++){
                	  //System.out.println("spFilePath[j]:"+spFilePath[j]);
                	  if(spFilePath[j].contains(selOrgUnit.getName())){
                		  for(int k=j;k<spFilePath.length;k++){
                			  relativePath+=File.separator+spFilePath[k];
                		  }
                	  }
                  }
                  FileInputStream in=new FileInputStream(filePath);
                  out.putNextEntry(new ZipEntry(filePath));
                  int len;              
                  while((len=in.read(tmpBuf))>0){
                    out.write(tmpBuf, 0, len);
                  }
                  out.closeEntry();
                  in.close();
                }
            }
            catch(Exception e){
              ((org.apache.commons.logging.Log) log).error("Should handle these file stream better", e);
            } 
        }

        public void sendEmail(String EMAIL_USERNAME, String EMAIL_PASSWORD, 
        		String HOST_NAME, Integer SMTP_PORT, String EMAIL_RECEIVER, 
        		String EMAIL_SUBJECT, String BODY, String ATTACHMENT_PATH) throws org.apache.commons.mail.EmailException
        {
        	try {
    		File attachFile=new File(ATTACHMENT_PATH);
    		EmailAttachment attachment=new EmailAttachment();
    		attachment.setPath(attachFile.getPath());
    		attachment.setDisposition(EmailAttachment.ATTACHMENT);
    		attachment.setDescription(attachFile.getName());
    		attachment.setName(attachFile.getName());		
    		MultiPartEmail email = new MultiPartEmail();
    		email.setHostName(HOST_NAME);//"smtp.gmail.com"
    		email.setSmtpPort(SMTP_PORT);//587,465
    		email.setAuthenticator(new DefaultAuthenticator(EMAIL_USERNAME,EMAIL_PASSWORD));
    		email.setSSL(true);
    		
    			email.addTo(EMAIL_RECEIVER);
    			email.setFrom(EMAIL_USERNAME, EMAIL_SUBJECT);
    			email.setSubject(BODY);
    			email.setMsg(EMAIL_SUBJECT);
    			email.attach(attachment);
    			email.send();
    		} catch (EmailException e){
    			e.printStackTrace();
    		}
        }
    }
