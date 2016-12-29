package org.hisp.dhis.reports.auto.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class GeneratePreGeneratedReportAction 
	implements Action
{        
    private static final Log log=LogFactory.getLog(GeneratePreGeneratedReportAction.class);

	private final String RAFOLDERNAME="ra_haryana";
	
	private final String OUTPUT="output";
	
    private final String OUTPUTREPORTFOLDER="reports";
	
	private final String OUTPUTREPORTFOLDER_SC="CAPTURED_DATA_SC_AND_EQUIVALENT_FACILITIES_REPORTS";
	
	private final String OUTPUTREPORTFOLDER_PHC="CAPTURED_DATA_PHC_AND_EQUIVALENT_FACILILITIES_REPORTS";

	private final String OUTPUTREPORTFOLDER_CHC="CAPTURED_DATA_CHC_AND_EQUIVALENT_HOSPITALS_REPORTS";

	private final String OUTPUTREPORTFOLDER_SDH="CAPTURED_DATA_SDH_AND_EQUIVALENT_HOSPITALS_REPORTS";
	
	private final String OUTPUTREPORTFOLDER_DH="CAPTURED_DATA_DH_AND_EQUIVALENT_HOSPITALS_REPORTS";
	
	private static String USER_NAME;  
	
    private static String PASSWORD; 
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    public SystemSettingManager systemSettingManager;
    
    public SystemSettingManager getSystemSettingManager() 
    {
		return systemSettingManager;
	}

	public void setSystemSettingManager(SystemSettingManager systemSettingManager) 
	{
		this.systemSettingManager = systemSettingManager;
	}
	
    private CurrentUserService currentUserService ;
    
    public void setCurrentUserService(CurrentUserService currentUserService) 
    {
		this.currentUserService = currentUserService;
	}
    
    private ReportService reportService;

    public void setReportService(ReportService reportService)
    {
        this.reportService=reportService;
    }

    private PeriodService periodService;

    public void setPeriodService(PeriodService periodService)
    {
        this.periodService=periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService)
    {
        this.organisationUnitService=organisationUnitService;
    }
        
    private I18nFormat format;

    public void setFormat(I18nFormat format)
    {
        this.format=format;
    }
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
       
    private InputStream inputStream;

    public void setInputStream(InputStream inputStream) 
    {
		this.inputStream=inputStream;
	}

	public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }
   
    private int ouIDTB;

    public int getOuIDTB() 
    {
		return ouIDTB;
	}

	public void setOuIDTB(int ouIDTB)
    {
        this.ouIDTB=ouIDTB;
    }

    private int availablePeriod; 

    public int getAvailablePeriod() 
    {
		return availablePeriod;
	}

	public void setAvailablePeriod(int availablePeriod)
    {
        this.availablePeriod=availablePeriod;
    }
    
    private String downloadPeriod;
    
    public String getDownloadPeriod() 
    {
		return downloadPeriod;
	}

	public void setDownloadPeriod(String downloadPeriod) 
	{
		this.downloadPeriod=downloadPeriod;
	}
   
    private String reportFileNameTB;

    public String getReportFileNameTB() 
    {
		return reportFileNameTB;
	}

	public void setReportFileNameTB(String reportFileNameTB) 
	{
		this.reportFileNameTB=reportFileNameTB;
	}
	
	private int reportList;
	
    public int getReportList() 
    {
		return reportList;
	}

	public void setReportList(int reportList) 
	{
		this.reportList=reportList;
	}

	private Period selectedPeriod;

    private SimpleDateFormat simpleDateFormat;

    private SimpleDateFormat dateFormat;
    
    private SimpleDateFormat monthFormat;
    
    private SimpleDateFormat yearFormat;

    private SimpleDateFormat simpleMonthFormat;

    private String raFolderName;
       
    private OrganisationUnit selOrgUnit;
    
    private String outputReportFolderPath;
           		   
	private Report_in selReportObj;
	        
    private String zipOutPutFilePath;
    
    private String pathOfZipFile=null;
                
    private String EMAIL_RECEIVER=null;
    
    private String EMAIL_SUBJECT=null;
    
    private String BODY=null;
    
    private String ATTACHMENT_PATH=null;
    
    private boolean emailAttachmentCB;
    
    public boolean isEmailAttachmentCB() 
    {
		return emailAttachmentCB;
	}

	public void setEmailAttachmentCB(boolean emailAttachmentCB) 
	{
		this.emailAttachmentCB = emailAttachmentCB;
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
    	
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------	 	

	public String execute()
        throws Exception
    {
        // Initialization
	 	raFolderName=RAFOLDERNAME;	 	 
        simpleDateFormat=new SimpleDateFormat("MMM-yyyy");
        dateFormat=new SimpleDateFormat("dd");
        monthFormat=new SimpleDateFormat("MMMM" );
        yearFormat=new SimpleDateFormat("yyyy");
        simpleMonthFormat=new SimpleDateFormat("MMM");
        selectedPeriod=new Period();
        selOrgUnit=new OrganisationUnit();        
        outputReportFolderPath=null;               		   
    	selReportObj=new Report_in(); 
    	zipOutPutFilePath=null;    
        selReportObj=reportService.getReport(reportList); 
    	reportFileNameTB=selReportObj.getName(); 
        selOrgUnit=organisationUnitService.getOrganisationUnit(ouIDTB);   
        selectedPeriod=periodService.getPeriod(availablePeriod);
        zipOutPutFilePath=System.getenv("DHIS2_HOME")+File.separator+raFolderName+File.separator+OUTPUT;	
                        
        HOST_NAME=systemSettingManager.getEmailHostName();
    	SMTP_PORT=systemSettingManager.getEmailPort();
    	USER_NAME=systemSettingManager.getEmailUsername();
    	PASSWORD=systemSettingManager.getEmailPassword();
    	EMAIL_SUBJECT="Find Attachment Contain Reports : "+selReportObj.getName()
				+" Organisatonunit : "+selOrgUnit.getName()
				+" Period : "+selectedPeriod.getName()
				+" Report Generated At : "+new Date();	     	
		BODY="THIS IS HARYANA DISTRICT HEALTH INFORMATION SOFTWARE SYSTEM'S SELF LOADED ATUTOMATIC GENERATED EMAIL. HENCE NO NEED TO REPLY.";
    	/*BODY="Please find attachment contains reports emailed by NHM DHIS2.\n"
    			  + "Thanks.\n"
    			  + "---------------------------------------------------------------------------------------------------\n"
		          + "Disclaimer: This e-mail & attachment(s) within it are for sole use of intended recipient(s) & may \n" 
		          + "contain confidential & privileged information. If you are not the intended recipient, please intimate \n" 
		          + "the sender by replying to this email & destroy all copies & the original message. Any unauthorized \n"
		          + "review, use, disclosure, dissemination, forwarding, printing or copying of this email or any action \n" 
		          + "taken in reliance on this e-mail is strictly prohibited & unlawful. The recipient acknowledges that \n" 
		          + "state , its governing organization units, associated organization unit or persons authorized by it, \n"
		          + "are unable to exercise control, ensure, guarantee the integrity of/over the contents of the information \n" 
		          + "contained in e-mail transmissions & further acknowledges that any views expressed in this message are \n" 
		          + "those of the individual sender & no binding nature of the message shall be implied or assumed unless \n" 
		          + "the sender does so expressly with due authority of the NRHM Haryana state. \n"	
		  		  + "---------------------------------------------------------------------------------------------------\n";*/    	
    	EMAIL_RECEIVER=currentUserService.getCurrentUser().getEmail();   	  	
        System.out.println(selOrgUnit.getName()+" : "+selectedPeriod.getName()+" : "+ selReportObj.getName()+" : Report Generation Start Time is : "+new Date());	            

        if((selOrgUnit.getName().startsWith("SC") 
			|| selOrgUnit.getName().endsWith("SC")) 
			&& (selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting SC and equivalent Facilities"))){	        
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_SC 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format(selectedPeriod.getStartDate()).trim() 
	        		+ File.separator + selOrgUnit.getParent().getParent().getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim() 
	        		+ File.separator + selReportObj.getName().trim();
	        
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
        }
    	  	    	
    	else if((selOrgUnit.getName().startsWith("PHC") 
            || selOrgUnit.getName().endsWith("PHC")) 
			&& (selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting SC and equivalent Facilities"))){	        
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_SC 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate()).trim() 
	        		+ File.separator + selOrgUnit.getParent().getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim();

			pathOfZipFile=exportFolder(outputReportFolderPath);
	    	ATTACHMENT_PATH=pathOfZipFile;
			System.out.println("pathOfZipFile:"+pathOfZipFile);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
        }
        
    	else if((selOrgUnit.getName().startsWith("CHC") 
    		 || selOrgUnit.getName().endsWith("CHC")) 
    		 && (selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting SC and equivalent Facilities"))){	               
	        outputReportFolderPath=System.getenv( "DHIS2_HOME" ) 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_SC 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format(selectedPeriod.getStartDate()).trim() 
	        		+ File.separator + selOrgUnit.getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim();
	        	       
			pathOfZipFile=exportFolder(outputReportFolderPath);
	    	ATTACHMENT_PATH=pathOfZipFile;
			System.out.println("pathOfZipFile:"+pathOfZipFile);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
        }
        
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId()) == 3) 
    		&& selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting SC and equivalent Facilities")){	        
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_SC 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate()).trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim(); 

			pathOfZipFile=exportFolder(outputReportFolderPath);
			System.out.println("pathOfZipFile:"+pathOfZipFile);

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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
	    }
    	
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId())==2) 
			&& selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting SC and equivalent Facilities")){	        
			outputReportFolderPath=System.getenv("DHIS2_HOME") 
					+ File.separator + raFolderName 
					+ File.separator + OUTPUTREPORTFOLDER 
					+ File.separator + OUTPUTREPORTFOLDER_SC 
					+ File.separator + downloadPeriod.trim() 
					+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate()).trim()
					+ File.separator + selOrgUnit.getShortName().trim(); 
			
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
		}
            	  	
    	else if((selOrgUnit.getName().startsWith("PHC") 
			|| selOrgUnit.getName().endsWith("PHC") 
			|| selOrgUnit.getName().endsWith("PHC")) 
			&& (selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting PHC and equivalent Facilities"))){	        
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_PHC 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate()).trim()
	        		+ File.separator + selOrgUnit.getParent().getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim();
	        	        
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
        }
            	
    	else if((selOrgUnit.getName().startsWith("CHC") 
    		|| selOrgUnit.getName().endsWith("CHC")) 
			&& selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting PHC and equivalent Facilities")){	               
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_PHC 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate()).trim() 
	        		+ File.separator + selOrgUnit.getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim(); 
	        	       
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
        }
           	
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId())==3) 
    		&& selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting PHC and equivalent Facilities")){	        
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_PHC 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate()).trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim();
	        	       
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
	    }
    	
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId())==2) 
			&& selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting PHC and equivalent Facilities")){	        
			outputReportFolderPath=System.getenv("DHIS2_HOME") 
					+ File.separator + raFolderName 
					+ File.separator + OUTPUTREPORTFOLDER 
					+ File.separator + OUTPUTREPORTFOLDER_PHC 
					+ File.separator + downloadPeriod.trim() 
					+ File.separator + simpleDateFormat.format(selectedPeriod.getStartDate()).trim() 
					+ File.separator + selOrgUnit.getShortName().trim();
			
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
		}
          	    	
    	else if((selOrgUnit.getName().startsWith("CHC") 
			|| selOrgUnit.getName().endsWith("CHC")) 
			&& (selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting CHC and equivalent Facilities"))){	               
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_CHC 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate() ).trim() 
	        		+ File.separator + selOrgUnit.getParent().getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim(); 
	        	      
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
        }
            	
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId())==3) 
    		&& selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting CHC and equivalent Facilities")){	        
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_CHC 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format(selectedPeriod.getStartDate()).trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim(); 
	        	      
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
	    }
    	
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId())==2) 
			&& selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting CHC and equivalent Facilities")){	        
			outputReportFolderPath=System.getenv("DHIS2_HOME") 
					+ File.separator + raFolderName 
					+ File.separator + OUTPUTREPORTFOLDER 
					+ File.separator + OUTPUTREPORTFOLDER_CHC 
					+ File.separator + downloadPeriod.trim() 
					+ File.separator + simpleDateFormat.format(selectedPeriod.getStartDate()).trim() 
					+ File.separator + selOrgUnit.getShortName().trim(); 
			  
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
		}
         	   	
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId())==3) 
    		&& selReportObj.getName().trim().equalsIgnoreCase("BULK LineListing SDH and equivalent facilities Report")){	        
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_SDH 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate()).trim()
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim(); 
	     
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
	    }
    	
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId())==2) 
			&& selReportObj.getName().trim().equalsIgnoreCase("BULK LineListing SDH and equivalent facilities Report")){	        
			outputReportFolderPath=System.getenv("DHIS2_HOME") 
					+ File.separator + raFolderName 
					+ File.separator + OUTPUTREPORTFOLDER 
					+ File.separator + OUTPUTREPORTFOLDER_SDH 
					+ File.separator + downloadPeriod.trim() 
					+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate()).trim() 
					+ File.separator + selOrgUnit.getShortName().trim(); 
			
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
		}
           	
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId())==3) 
        	&& selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting DH/GH and equivalent Facilities")){	        
	        outputReportFolderPath=System.getenv("DHIS2_HOME") 
	        		+ File.separator + raFolderName 
	        		+ File.separator + OUTPUTREPORTFOLDER 
	        		+ File.separator + OUTPUTREPORTFOLDER_DH 
	        		+ File.separator + downloadPeriod.trim() 
	        		+ File.separator + simpleDateFormat.format( selectedPeriod.getStartDate()).trim() 
	        		+ File.separator + selOrgUnit.getParent().getShortName().trim() 
	        		+ File.separator + selOrgUnit.getShortName().trim(); 
	               
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
	    }   
        
    	else if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId())==2) 
			&& selReportObj.getName().trim().equalsIgnoreCase("Bulk Linelisting DH/GH and equivalent Facilities")){	        
			outputReportFolderPath=System.getenv("DHIS2_HOME") 
					+ File.separator + raFolderName 
					+ File.separator + OUTPUTREPORTFOLDER 
					+ File.separator + OUTPUTREPORTFOLDER_DH 
					+ File.separator + downloadPeriod.trim() 
					+ File.separator + simpleDateFormat.format(selectedPeriod.getStartDate()).trim() 
					+ File.separator + selOrgUnit.getShortName().trim(); 
			
			pathOfZipFile=exportFolder(outputReportFolderPath);
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
												    			+", Organisatonunit : "+selOrgUnit.getName()
																+", Period : "+selectedPeriod.getName()
																+", Report Generated At : "+new Date());
			        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
			    	System.out.println("Email sent with attached reports is successfull.");
		    }
	    	inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
	    	File outputReportFile = new File( pathOfZipFile );
	        outputReportFile.delete();
		}   
    	
    	else{
            log.error("Selected organisation unit with selected period, selected bulk report for selected download period:"+downloadPeriod.trim()+" is not available.");
            return INPUT;
    	}
        
        System.out.println(selOrgUnit.getName()+" : "+selectedPeriod.getName()+" : "+ selReportObj.getName()+" : Report Generation End Time is : "+new Date());
                 	          
        try {
			//cleanUp();        	
	    	//System.gc();    	
		} catch (Throwable e) {
			e.printStackTrace();
		}        
        return SUCCESS;            
    }
     
    // -------------------------------------------------------------------------
    //  Other supporting methods
    // -------------------------------------------------------------------------
	
	protected void cleanUp () throws Throwable{
        if (selReportObj!=null){
        	selReportObj=null;
	    }
    	if (inputStream!=null){
    		inputStream=null;
	    }    	
	    if (simpleDateFormat!=null){
	    	simpleDateFormat=null;
	    }
	    if (monthFormat!=null){
	    	monthFormat=null;
	    }
	    if (yearFormat!=null){
	    	yearFormat=null;
	    }
	    if (dateFormat!=null){
	    	dateFormat=null;
	    }
	    if (simpleMonthFormat!=null){
	        simpleMonthFormat=null;
	    }	    
	    if (selOrgUnit!=null){
	        selOrgUnit=null;
	    }
	    if (outputReportFolderPath!=null){
	        outputReportFolderPath=null;
	    }	    
	    if (selectedPeriod!=null){
	    	selectedPeriod=null;
	    }	 
	    inputStream.close();
    }        
    
    public boolean clearXfolder(String folderPath){
        try{
            File dir=new File(folderPath);
            String[] files=dir.list();        
            for (String file : files){
                file=folderPath + File.separator + file;
                File tempFile=new File(file);
                tempFile.delete();
            }              
            return true;
        }
        catch(Exception e){
            log.error(e);
            return false;
        }        
    }
    
    public String exportFolder(String outputReportFolderPath) throws IOException, IllegalArgumentException{                                   
        String zipFilePath=null;        
        ZipOutputStream out=null;
        File in=null;
        fileName=reportFileNameTB.replace(".xls","")+"_"+selOrgUnit.getShortName()+"_"+simpleDateFormat.format(selectedPeriod.getStartDate())+".zip";
        zipFilePath=zipOutPutFilePath+File.separator+fileName; 
        
        try{	
            try{
		          File zipFile=new File(zipFilePath);      
		          if(!zipFile.exists()){		            	
		            	in=new File(outputReportFolderPath);
		            	out=new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));  
		            	addDirectoryForCompressing(in,out);
		            	}else{		           	            	
		            	   return zipFilePath;
		          }
            	}catch(Exception e){		        		           
		              log.error("Exception trying to close output stream", e);
		              log.error(e);          
		        }finally{                                           
                     out.flush();
                     out.close();
                } 
            }catch(Exception e){              
            	log.error(e);          
        }                         
        return zipFilePath;
    }              
    
    public void addDirectoryForCompressing(File dirObj, ZipOutputStream out){
        try{
            File[] files=dirObj.listFiles();
            //System.out.println("files.length:"+files.length);
            byte[] tmpBuf=new byte[1024];               
            for(int i=0;i<files.length;i++){            	
              if(files[i].isDirectory()){
            	addDirectoryForCompressing(files[i],out); 
            	continue; 
              }              
              //files[i].getName().equalsIgnoreCase(selOrgUnit.getName());
              String relativePath=null;
              String filePath=files[i].getAbsolutePath();
              String[] spFilePath=filePath.split(Pattern.quote(File.separator));
              for(int j=0;j<spFilePath.length;j++){
            	  System.out.println("spFilePath[j]:"+spFilePath[j]);
            	  if(spFilePath[j].contains(selOrgUnit.getName())){
            		  for(int k=j;k<spFilePath.length;k++){
            			  relativePath+=File.separator+spFilePath[k];
            		  }
            	  }
              }
              System.out.println("filePath:"+filePath);
              System.out.println("relativePath:"+relativePath);
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
          log.error("Should handle these file stream better", e);
        } 
    }

    public void sendEmail(String EMAIL_USERNAME, String EMAIL_PASSWORD, 
    		String HOST_NAME, Integer SMTP_PORT, String EMAIL_RECEIVER, 
    		String EMAIL_SUBJECT, String BODY, String ATTACHMENT_PATH) 
    {
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
		try {
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

