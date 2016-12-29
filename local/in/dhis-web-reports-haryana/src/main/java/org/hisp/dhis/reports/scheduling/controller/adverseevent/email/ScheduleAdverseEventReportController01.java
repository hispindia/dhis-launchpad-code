package org.hisp.dhis.reports.scheduling.controller.adverseevent.email;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

public class ScheduleAdverseEventReportController01 implements Action
{
    private static final Log log=LogFactory.getLog(ScheduleAdverseEventReportController01.class);
    
    private static final String DHIS2_HOME = "DHIS2_HOME";
    
    private static final int ADVERSE_EVENT_REPORT_ID=213;
    
    private static final int ORGANISATION_UNIT_LEVEL_DISTRICT=3;
    
	private static final String SCHEDULING_POLICY_ID="2";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

	private SystemSettingManager systemSettingManager;
	
	public SystemSettingManager getSystemSettingManager() 
    {
		return systemSettingManager;
	}
    
    public void setSystemSettingManager(SystemSettingManager systemSettingManager) 
    {
    	this.systemSettingManager = systemSettingManager;
    }
	
    private UserService userService;
    
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }
	        
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
       
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
        
    // -------------------------------------------------------------------------
    // Properties
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

    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private List<String> dataValueList;

    public List<String> getDataValueList()
    {
        return dataValueList;
    }

    private List<String> services;

    public List<String> getServices()
    {
        return services;
    }

    private List<String> slNos;

    public List<String> getSlNos()
    {
        return slNos;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private SimpleDateFormat monthFormat;

    public SimpleDateFormat getMonthFormat()
    {
        return monthFormat;
    }

    private SimpleDateFormat yearFormat;

    public SimpleDateFormat getYearFormat()
    {
        return yearFormat;
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
	
    private List<String> deCodeType;

    private List<String> serviceType;

    private String reportFileNameTB;
    
    private String reportModelTB;
    	
    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private List<Integer> rowMergeList;

    private List<Integer> colMergeList;
    
    private List<String> dataTypeList;

    private Date sDate;

    private Date eDate;

    private OrganisationUnit currentOrgUnit;

    private String raFolderName;
    
    private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
                        
    private String EMAIL_RECEIVER=null;
    
    private String EMAIL_SUBJECT=null;
    
    private String BODY=null;
    
    private String ATTACHMENT_PATH=null;
        
    private static String USER_NAME;  
	
    private static String PASSWORD; 
        
    private OrganisationUnit districtOrgUnit = new OrganisationUnit();
    
    private List<OrganisationUnit> orgUnitListDistWithChild;
    
    private List<Period> tempPeriodList;
    
    private Collection<Integer> tempPeriodIds;
    
    private OrganisationUnit ougHararchy;
    
    private File outputReportFile;
    
    private List<Report_inDesign> reportDesignList;
    
    private List<Integer> dataElmentIds;
    
    private String inputTemplatePath;
    
    private String outputReportPath;    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // Initialization
        Report_in selReportObj = reportService.getReport( ADVERSE_EVENT_REPORT_ID );
    	if(selReportObj.isScheduled() && selReportObj.getSchedulingPolicyId().equalsIgnoreCase(SCHEDULING_POLICY_ID)){
        	//if(selReportObj.isScheduled()){
        raFolderName=reportService.getRAFolderName();
        mathTool=new MathTool();
        services=new ArrayList<String>();
        slNos=new ArrayList<String>();
        deCodeType=new ArrayList<String>();
        serviceType=new ArrayList<String>();
        simpleDateFormat=new SimpleDateFormat( "MMM-yyyy" );
        monthFormat=new SimpleDateFormat( "MMMM" );
        yearFormat=new SimpleDateFormat( "yyyy" );

        // Getting Report Details       
        String deCodesXMLFileName = "";
        deCodesXMLFileName=selReportObj.getXmlTemplateName();
        reportModelTB=selReportObj.getModel();
        reportFileNameTB=selReportObj.getExcelTemplateName();        
        sheetList=new ArrayList<Integer>();
        rowList=new ArrayList<Integer>();
        colList=new ArrayList<Integer>();
        rowMergeList=new ArrayList<Integer>();
        colMergeList=new ArrayList<Integer>();
        dataTypeList=new ArrayList<String>();

        // Period Info       
        MonthlyPeriodType monthlyPeriodType=new MonthlyPeriodType();
        ArrayList<Period> monthlyPeriods=new ArrayList<Period>( periodService.getPeriodsByPeriodType(monthlyPeriodType));
        selectedPeriod=new ArrayList<Period>(monthlyPeriods).get( monthlyPeriods.size() - 2 );
        sDate=selectedPeriod.getStartDate();
        eDate=selectedPeriod.getEndDate();
        simpleDateFormat=new SimpleDateFormat( "MMM-yyyy" );       
        tempPeriodList=new ArrayList<Period>(periodService.getIntersectingPeriods(sDate, eDate));        
        tempPeriodIds=new ArrayList<Integer>(getIdentifiers(Period.class, tempPeriodList));       
        reportDesignList=reportService.getReportDesign(deCodesXMLFileName);	        
        Collection<Integer> dataElementIds = dataElementService.getDataElementIdsByGroupId(
        		dataElementService.getDataElementGroup(reportService.getDataElementIds(
        				reportDesignList).get(0).intValue()).getId());
        inputTemplatePath=System.getenv( DHIS2_HOME ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;          
        outputReportPath=System.getenv( DHIS2_HOME ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir=new File(outputReportPath);
        if(!newdir.exists()){
            newdir.mkdirs();
        }
        
        // OrgUnit Info       
        orgUnitList=new ArrayList<OrganisationUnit>(organisationUnitService.getOrganisationUnitsAtLevel(ORGANISATION_UNIT_LEVEL_DISTRICT));  
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        ougHararchy = new OrganisationUnit();
        Iterator<OrganisationUnit> ougItr=orgUnitList.iterator();
        int recordCount = 0;
        while(ougItr.hasNext()){     
        	districtOrgUnit = ougItr.next();
            System.out.println( selReportObj.getName() + " Generation Start Time is : " + new Date() + " District : " + districtOrgUnit.getName() );
            Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
            outputReportPath = System.getenv( DHIS2_HOME ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER + File.separator + selReportObj.getName() + "_" + districtOrgUnit.getName() + "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
            WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
            orgUnitListDistWithChild=new ArrayList<OrganisationUnit>();
	        List<OrganisationUnit> orgUnitListDistWithChild =new ArrayList<OrganisationUnit>(organisationUnitService.getOrganisationUnitWithChildren(districtOrgUnit.getId())); 	       	        	        	
	        recordCount = 0;
	        Iterator<OrganisationUnit> ougDistChildItr=orgUnitListDistWithChild.iterator();
	        while(ougDistChildItr.hasNext()){        		        	
		        currentOrgUnit=ougDistChildItr.next();	
		        Iterator<Integer> deItr = dataElementIds.iterator();
		        while(deItr.hasNext()){		        	
			        Integer dei = deItr.next();
		            String value = getDataValue( dei.intValue(), selectedPeriod, currentOrgUnit );
		            if(value == null || value =="" || value ==" " || Integer.parseInt(value)==0){
		            	continue;
		            }		            
			        List<String> deCodesList = getDECodes( deCodesXMLFileName );				        
			        Iterator<String> deCodesListItr = deCodesList.iterator();
			        int count1 = 0;
			        while (deCodesListItr.hasNext()){
		            	String deCodeString = (String) deCodesListItr.next();		
		            	String deType = (String) deCodeType.get(count1);
		            	String sType = (String) serviceType.get(count1);
		            	String tempStr = "";		
		            	Calendar tempStartDate = Calendar.getInstance();
		            	Calendar tempEndDate = Calendar.getInstance();
		            	List<Calendar> calendarList = new ArrayList<Calendar>( getStartingEndingPeriods( deType ) );
		            	if (calendarList == null || calendarList.isEmpty())
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
		                if ( deCodeString.equalsIgnoreCase( "ORGUNIT-HARARCHY" ) )
		                {
	                	     tempStr = null;
	                	     ougHararchy = currentOrgUnit;
		                     while(ougHararchy.getParent()!=null && (ougHararchy.getLevel() >= 2))
		                     {
		                	if(tempStr != null)
		                	{
		                	    tempStr = ougHararchy.getParent().getName() + " > " + tempStr;
		                	    ougHararchy = ougHararchy.getParent();
		                	}else{
		                		tempStr = ougHararchy.getParent().getName();
			                	ougHararchy = ougHararchy.getParent();
		                	}
		                    }
		                }
		                else if ( deCodeString.equalsIgnoreCase( "NAME-DE" ) )
		                {
		                    tempStr = dataElementService.getDataElement(dei.intValue()).getName();
		                }
		                else if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
		                {
		                    tempStr = currentOrgUnit.getParent().getName();
		                }		                
		                else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
		                {
		                    tempStr = districtOrgUnit.getName();
		                }		                
		                else if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
		                {
		                    tempStr = format.formatDate( sDate ) + " - " + format.formatDate( eDate );
		                }
		                else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
		                {
		                    tempStr = monthFormat.format( sDate );
		                }
		                else if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
		                {
		                    tempStr = yearFormat.format( sDate );
		                }
		                else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
		                {
		                    tempStr = "" + (recordCount + 1);
		                }
		                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
		                {
		                    tempStr = " ";
		                }
		                else 
		                {
		                	if( sType.equalsIgnoreCase( "dynamicdataelementrepeat" ) )
	                        {		
		                    	tempStr = value;			                    		
	                        }
		                }
		                
		                int tempRowNo = rowList.get(count1);
		                int tempRowNo1 = tempRowNo;
		                int tempColNo = colList.get(count1);
		                int sheetNo = sheetList.get(count1);
		                int tempMergeCol = colMergeList.get(count1);
		                int tempMergeRow = rowMergeList.get(count1 );
		
		                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
		                if ( tempStr == null || tempStr.trim().equals( "" ) )
		                {		
			            	recordCount++;
		                	continue;
		                }
		                else
		                {						                    		
		                    if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-DATAELEMENT" ) )
		                    {
		                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
		                            || deCodeString.equalsIgnoreCase( "FACILITYPP" ) 
		                            || deCodeString.equalsIgnoreCase( "FACILITYPPP" )
		                            || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
		                        {		
		                        }
		                        else if ( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
		                            || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) 
		                            || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
		                            || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
		                        {		
		                        }		                        
		                        else
		                        {
		                            tempRowNo += recordCount ;
		                        }
		
		                        WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo1 );		
		                        CellFormat cellFormat = cell.getCellFormat();		
		                        WritableCellFormat wCellformat = new WritableCellFormat();		
		                        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
		                        wCellformat.setWrap( true );
		                        wCellformat.setAlignment( Alignment.LEFT );
		                        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE ); 		
		                        if ( cellFormat != null )
		                        {
		                            if ( tempMergeCol > 0 || tempMergeRow > 0 )
		                            {
		                                sheet0.mergeCells( tempColNo, tempRowNo, tempColNo + tempMergeCol, tempRowNo + tempMergeRow );
		                            }		                            
		                            try
		                            {
		                                sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), cellFormat ) );
		                            }
		                            catch( Exception e )
		                            {
		                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, cellFormat ) );
		                            }
		                        }
		                        else
		                        {
		                            if ( tempMergeCol > 0 || tempMergeRow > 0 )
		                            {
		                                sheet0.mergeCells( tempColNo, tempRowNo, tempColNo + tempMergeCol, tempRowNo + tempMergeRow );
		                            }
		                            
		                            try
		                            {
		                                sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
		                            }
		                            catch( Exception e )
		                            {
		                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
		                            }
		                        }
		                    }
		                 }
		                 count1++;
		            }// inner while loop end
			        recordCount++;
		        }// outer while loop end
	        }
        
	        // System.out.println( "recordCount : " + recordCount );

	        outputReportWorkbook.write();
	        outputReportWorkbook.close();
            System.out.println( selReportObj.getName() + " Generation End Time is : " + new Date() + " District : " + districtOrgUnit.getName() );
	        HOST_NAME=systemSettingManager.getEmailHostName().trim();
	    	SMTP_PORT=systemSettingManager.getEmailPort();
	    	USER_NAME=systemSettingManager.getEmailUsername().trim();
	    	PASSWORD=systemSettingManager.getEmailPassword();
	    	EMAIL_SUBJECT="Find Attachment Contain Report : "+selReportObj.getName()
					+", Organisatonunit : "+districtOrgUnit.getName()
					+", Period : "+selectedPeriod.getName()
					+", Report Generated At : "+new Date();	        
    		BODY="DHIS2 Generated Adverse Report. Please Do Not reply.";

	    	ATTACHMENT_PATH = outputReportPath;
	    	Collection<OrganisationUnit> orgUnits = new ArrayList<OrganisationUnit>();
	    	orgUnits.add(districtOrgUnit);
	    	orgUnits.add(districtOrgUnit.getParent());	    	
	    	List<User> users = new ArrayList<User>(userService.getUsersByOrganisationUnits(orgUnits));
    		System.out.println("Number of (district + State) Users : "+users.size() );
    		
	    	List<User> usersWitEmailId = new ArrayList<User>();
	    	for(User user:users){
	    		if(user.getEmail() != null){
	    			usersWitEmailId.add( user );
	    		}
	    	}
	    	
    		System.out.println("Number of (district + State) Users With Email Ids: "+usersWitEmailId.size() );
	    	for(User user:usersWitEmailId){
	    		EMAIL_RECEIVER=user.getEmail();	
	    		System.out.println("HOST_NAME :"+HOST_NAME); 
	    				System.out.println("SMTP_PORT :"+SMTP_PORT); 
	    						System.out.println("USER_NAME :"+USER_NAME); 
	    								System.out.println("EMAIL_SUBJECT :"+EMAIL_SUBJECT); 
	    										System.out.println("BODY :"+BODY); 
	    												System.out.println("ATTACHMENT_PATH :"+ATTACHMENT_PATH); 
	    														System.out.println("EMAIL_RECEIVER :"+EMAIL_RECEIVER); 
	    																System.out.println("Records count in attached report :"+recordCount); 
				if(selReportObj.isEmailable()
						&& recordCount > 0
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
				    			+" User : " +user.getUsername()
				    			+", Generated report : "+selReportObj.getName()
				    			+", Organisatonunit : "+districtOrgUnit.getName()
								+", Period : "+selectedPeriod.getName()
								+", Report Generated At : "+new Date());
				        sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
				    	System.out.println("Email sent with attached reports is successfull.");
				}
	    	}
		    outputReportFile = new File( outputReportPath );
	        outputReportFile.delete();
        } try {
		//cleanUp();        	
		//System.gc();    	
		} catch (Throwable e) {
		  e.printStackTrace();
	    }    
    	}
        return SUCCESS;
    }
    
    protected void cleanUp () throws Throwable{
            if (orgUnitListDistWithChild!=null){
        	orgUnitListDistWithChild=null;
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
	    if (tempPeriodList!=null){
	    	tempPeriodList=null;
	    }
	    if (tempPeriodIds!=null){
	    	tempPeriodIds=null;
	    }	    
	    if (orgUnitList!=null){
	    	orgUnitList=null;
	    }
	    if (ougHararchy!=null){
	    	ougHararchy=null;
	    }	    
	    if (selectedPeriod!=null){
	    	selectedPeriod=null;
	    }
	    if (outputReportFile!=null){
	    	outputReportFile=null;
	    }
	    if (reportDesignList!=null){
	    	reportDesignList=null;
	    }
	    if (dataElmentIds!=null){
	    	dataElmentIds=null;
	    }
	    if (inputTemplatePath!=null){
	    	inputTemplatePath=null;
	    }
	    if (outputReportPath!=null){
	    	outputReportPath=null;
	    }
	    inputStream.close();
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
    			email.setFrom(EMAIL_USERNAME);
    			email.addTo(EMAIL_RECEIVER);
    			email.setMsg(EMAIL_SUBJECT);
    			email.setSubject(BODY);
    			email.attach(attachment);
    			email.send();
    		} catch (EmailException e){
    			e.printStackTrace();
    		}
        }    
    
    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public List<Calendar> getStartingEndingPeriods( String deType )
    {
        List<Calendar> calendarList = new ArrayList<Calendar>();

        Calendar tempStartDate = Calendar.getInstance();
        Calendar tempEndDate = Calendar.getInstance();

        Period previousPeriod = new Period();
        previousPeriod = getPreviousPeriod();

        if ( deType.equalsIgnoreCase( "ccmcy" ) )
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            if ( tempStartDate.get( Calendar.MONTH ) < Calendar.APRIL )
            {
                tempStartDate.roll( Calendar.YEAR, -1 );
            }
            tempStartDate.set( Calendar.MONTH, Calendar.APRIL );
            tempEndDate.setTime( selectedPeriod.getEndDate() );
        }
        else if ( deType.equalsIgnoreCase( "cmpy" ) )
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );

            tempStartDate.roll( Calendar.YEAR, -1 );
            tempEndDate.roll( Calendar.YEAR, -1 );
        }

        else if ( deType.equalsIgnoreCase( "pmcy" ) )
        {
            tempStartDate.setTime( previousPeriod.getStartDate() );
            tempEndDate.setTime( previousPeriod.getEndDate() );
        }

        else
        {
            tempStartDate.setTime( selectedPeriod.getStartDate() );
            tempEndDate.setTime( selectedPeriod.getEndDate() );
        }

        calendarList.add( tempStartDate );
        calendarList.add( tempEndDate );

        return calendarList;
    }

    public Period getPreviousPeriod()
    {
        Period period = new Period();
        Calendar tempDate = Calendar.getInstance();
        tempDate.setTime( selectedPeriod.getStartDate() );
        if ( tempDate.get( Calendar.MONTH ) == Calendar.JANUARY )
        {
            tempDate.set( Calendar.MONTH, Calendar.DECEMBER );
            tempDate.roll( Calendar.YEAR, -1 );

        }
        else
        {
            tempDate.roll( Calendar.MONTH, -1 );
        }
        PeriodType periodType = getPeriodTypeObject( "monthly" );
        period = getPeriodByMonth( tempDate.get( Calendar.MONTH ), tempDate.get( Calendar.YEAR ), periodType );

        return period;
    }

    public Period getPeriodByMonth( int month, int year, PeriodType periodType )
    {
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        Calendar cal = Calendar.getInstance();
        cal.set( year, month, 1, 0, 0, 0 );
        Date firstDay = new Date( cal.getTimeInMillis() );

        if ( periodType.getName().equals( "Monthly" ) )
        {
            cal.set( year, month, 1, 0, 0, 0 );
            if ( year % 4 == 0 )
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] + 1 );
            }
            else
            {
                cal.set( Calendar.DAY_OF_MONTH, monthDays[month] );
            }
        }
        else if ( periodType.getName().equals( "Yearly" ) )
        {
            cal.set( year, Calendar.DECEMBER, 31 );
        }

        Date lastDay = new Date( cal.getTimeInMillis() );

        Period newPeriod = new Period();

        newPeriod.setStartDate( firstDay );
        newPeriod.setEndDate( lastDay );
        newPeriod.setPeriodType( periodType );

        return newPeriod;
    }

    public PeriodType getPeriodTypeObject( String periodTypeName )
    {
        Collection<PeriodType> periodTypes = periodService.getAllPeriodTypes();
        PeriodType periodType = null;
        Iterator<PeriodType> iter = periodTypes.iterator();
        while ( iter.hasNext() )
        {
            PeriodType tempPeriodType = (PeriodType) iter.next();
            if ( tempPeriodType.getName().toLowerCase().trim().equals( periodTypeName ) )
            {
                periodType = tempPeriodType;
                break;
            }
        }
        if ( periodType == null )
        {
            System.out.println( "No Such PeriodType" );
            return null;
        }
        return periodType;
    }

    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( DHIS2_HOME );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                // System.out.println( "There is no DECodes related XML file in
                // the user home" );
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Element deCodeElement = (Element) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();
                deCodes.add( ((Node) textDECodeList.item( 0 )).getNodeValue().trim() );
                serviceType.add( deCodeElement.getAttribute( "stype" ) );
                deCodeType.add( deCodeElement.getAttribute( "type" ) );
                sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );
                rowMergeList.add( new Integer( deCodeElement.getAttribute( "rowmerge" ) ) );
                colMergeList.add( new Integer( deCodeElement.getAttribute( "colmerge" ) ) );
                try
                {
                    dataTypeList.add( deCodeElement.getAttribute( "datatype" ) );
                }
                catch( Exception e )
                {
                    dataTypeList.add( "text" );
                }

            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        return deCodes;
    }// getDECodes end
    
	public String getDataValue( int dataElmentId, Period period, OrganisationUnit organisationUnit )
    {
        Statement st1 = null;
        ResultSet rs1 = null;

        String query = "";
        try
        {            
                query = "SELECT value FROM datavalue WHERE sourceid = " + organisationUnit.getId()
                    + " AND periodid = " + period.getId() + " AND dataelementid = " + dataElmentId;
                
                String tempStr = "";

                SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );

                if ( sqlResultSet.next() )
                {
                    tempStr = sqlResultSet.getString( 1 );
                }
               
            return tempStr;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }
        finally
        {
            try
            {
                if ( st1 != null )
                    st1.close();

                if ( rs1 != null )
                    rs1.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end
    }

}