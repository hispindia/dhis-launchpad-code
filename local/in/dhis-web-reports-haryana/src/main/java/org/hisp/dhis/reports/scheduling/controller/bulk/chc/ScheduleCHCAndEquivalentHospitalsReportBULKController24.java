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
package org.hisp.dhis.reports.scheduling.controller.bulk.chc;

/**
 * @author Brajesh Murari
 * @version $Id$
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.write.Blank;
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
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;

public class ScheduleCHCAndEquivalentHospitalsReportBULKController24 implements com.opensymphony.xwork2.Action 
{		
	private static final Log log=LogFactory.getLog(ScheduleCHCAndEquivalentHospitalsReportBULKController24.class);
	
    private static final String DHIS2_HOME="DHIS2_HOME";
    
    private static final int ORGANISATION_UNIT_LEVEL_DISTRICT=3;
        
	private static final int CHC_AND_EQUIVALENT_FACILITY_REPORT_ID=118;

	private static final int ROOT_ORGANISATION_UNIT_ID=14;

	private static final String RA_FOLDER_NAME="ra_haryana";

	private static final String OUT_PUT_REPORT_FOLDER="reports";

	private static final String OUT_PUT="output";
	
	private static final String BULK_LINELISTING_CHC_AND_EQUIVALENT_FACILITIES="Bulk Linelisting CHC and equivalent Facilities";
		
    private static final String OUT_PUT_REPORT_FOLDER_CHC="CAPTURED_DATA_CHC_AND_EQUIVALENT_HOSPITALS_REPORTS";
    
	private final String SCHEDULING_POLICY_ID="25";

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

	// -------------------------------------------------------------------------
	// Properties
	// -------------------------------------------------------------------------

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

	private String reportFileNameTB;

	private ArrayList<Period> monthlyPeriods;

	public ArrayList<Period> getMonthlyPeriods() 
	{
		return monthlyPeriods;
	}

	private SimpleDateFormat simpleDateFormat;

	public SimpleDateFormat getSimpleDateFormat() 
	{
		return simpleDateFormat;
	}

	private SimpleDateFormat dateFormat;

	public SimpleDateFormat getDateFormat() 
	{
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) 
	{
		this.dateFormat = dateFormat;
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
      	
	private String downloadPeriod;
    
    public String getDownloadPeriod() 
    {
		return downloadPeriod;
	}

	public void setDownloadPeriod(String downloadPeriod) 
	{
		this.downloadPeriod=downloadPeriod;
	}
	
	private List<OrganisationUnit> orgUnitListCHC;

	private String reportModelTB;

	private List<OrganisationUnit> orgUnitList;

	private Period selectedPeriod;

	private List<Period> selectedPeriodList;

	private SimpleDateFormat monthFormat;

	private SimpleDateFormat yearFormat;

	private SimpleDateFormat simpleMonthFormat;
	
	private MonthlyPeriodType monthlyPeriodType;

	private Date sDate;

	private Date eDate;

	private OrganisationUnitGroup orgUnitGroup;

	private OrganisationUnit selOrgUnit;

	private String outputReportFolderPath;

	private List<Report_in> selReportObjList;

	private String zipOutPutFilePath;

	private List<OrganisationUnit> orgUnitListSC;

	private List<Period> tempPeriodList;

	private String periodIdsByComma;

	private Collection<Integer> tempPeriodIds;

	private File newdir;

	private Map<String, String> aggDeMap;

	private List<Calendar> calendarList;

	private WritableCellFormat wCellformat;

	private Date presentDate;
	
	private String USER_NAME;  
	
    private String PASSWORD; 
        
    private String pathOfZipFile=null;
                
    private String EMAIL_RECEIVER=null;
    
    private String EMAIL_SUBJECT=null;
    
    private String BODY=null;
    
    private String ATTACHMENT_PATH=null;
    
    private Period selectedSecondLastMonthlyPeriod;

	// -------------------------------------------------------------------------
	// Action Implementation
	// -------------------------------------------------------------------------

	public String execute() throws Exception {
		// Initialization
		Report_in selReportObj = reportService
				.getReport(CHC_AND_EQUIVALENT_FACILITY_REPORT_ID);
    	if(selReportObj.isScheduled() && selReportObj.getSchedulingPolicyId().equalsIgnoreCase(SCHEDULING_POLICY_ID)){
			String deCodesXMLFileName = "";
			presentDate = new Date();
			simpleDateFormat = new SimpleDateFormat("MMM-yyyy");
			monthFormat = new SimpleDateFormat("MMMM");
			yearFormat = new SimpleDateFormat("yyyy");
			dateFormat = new SimpleDateFormat("dd");
			simpleMonthFormat = new SimpleDateFormat("MMM");
			String parentUnit = "";
			orgUnitListCHC = new ArrayList<OrganisationUnit>();
			orgUnitGroup = new OrganisationUnitGroup();
			selOrgUnit = new OrganisationUnit();
			outputReportFolderPath = null;
			selReportObjList = new ArrayList<Report_in>();
			zipOutPutFilePath = System.getenv(DHIS2_HOME) + File.separator
					+ RA_FOLDER_NAME + File.separator + OUT_PUT;
			deCodesXMLFileName = selReportObj.getXmlTemplateName();
			reportModelTB = selReportObj.getModel();
			reportFileNameTB = selReportObj.getExcelTemplateName();
			String inputTemplatePath = System.getenv(DHIS2_HOME)
					+ File.separator + RA_FOLDER_NAME + File.separator
					+ "template" + File.separator + reportFileNameTB;
			if (reportModelTB.equalsIgnoreCase("STATIC")
					|| reportModelTB.equalsIgnoreCase("STATIC-DATAELEMENTS")
					|| reportModelTB.equalsIgnoreCase("STATIC-FINANCIAL")) {
				orgUnitList = new ArrayList<OrganisationUnit>(
						organisationUnitService
								.getOrganisationUnitWithChildren(ROOT_ORGANISATION_UNIT_ID));
			} else {
				return INPUT;
			}
			orgUnitListCHC = new ArrayList<OrganisationUnit>();
			for (OrganisationUnit ou : orgUnitList) {
				if (ou.getName().startsWith("CHC")
						|| ou.getName().endsWith("CHC")) {
					orgUnitListCHC.add(ou);
				}
			}

			/* Monthly Periods */
			selectedPeriod = new Period();
			selectedPeriodList = new ArrayList<Period>();
			monthlyPeriodType = new MonthlyPeriodType();
			monthlyPeriods = new ArrayList<Period>(
					periodService.getPeriodsByPeriodType(monthlyPeriodType));
			/*for (int i = 3; i > 0; i--) {
			selectedPeriodList.add(monthlyPeriods.get(monthlyPeriods.size()
					- i));
			}*/
		
			selectedSecondLastMonthlyPeriod=new ArrayList<Period>(monthlyPeriods).get(monthlyPeriods.size()-2);
			selectedPeriodList.add(selectedSecondLastMonthlyPeriod);

			// Getting DataValues
			List<Report_inDesign> reportDesignList = reportService
					.getReportDesign(deCodesXMLFileName);
			String dataElmentIdsByComma = reportService
					.getDataelementIds(reportDesignList);
			int orgUnitCount = 0;
			int totalreports = 0;
			System.out.println(" ---- Size of OrgUnit List is ---- "
					+ orgUnitListCHC.size() + " : Scheduled "
					+ selReportObj.getName()
					+ " Reports Generation Start Time : " + new Date());
			Iterator<OrganisationUnit> it = orgUnitListCHC.iterator();
			while (it.hasNext()) {
				OrganisationUnit currentOrgUnit = it.next();
				Iterator<Period> pit = selectedPeriodList.iterator();
				while (pit.hasNext()) {
					Period selectedPeriod = pit.next();
					sDate = selectedPeriod.getStartDate();
					eDate = selectedPeriod.getEndDate();
					tempPeriodList = new ArrayList<Period>(
							periodService.getIntersectingPeriods(sDate, eDate));
					tempPeriodIds = new ArrayList<Integer>(getIdentifiers(
							Period.class, tempPeriodList));
					periodIdsByComma = getCommaDelimitedString(tempPeriodIds);
					// System.out.println(currentOrgUnit.getName()+" : "+selectedPeriod.getName()+" : "+
					// selReportObj.getName()+" : Report Generation Start Time is : "+new
					// Date());
					outputReportFolderPath = System.getenv(DHIS2_HOME)
							+ File.separator + RA_FOLDER_NAME
							+ File.separator + OUT_PUT_REPORT_FOLDER
							+ File.separator + OUT_PUT_REPORT_FOLDER_CHC
							+ File.separator + dateFormat.format(presentDate).trim()+"-"+ monthFormat.format(presentDate).trim()+"-"+ yearFormat.format(presentDate).trim()
							+ File.separator + simpleDateFormat.format(selectedPeriod.getStartDate()).trim()
							//+ File.separator + currentOrgUnit.getParent().getParent().getParent().getShortName().trim()
							+ File.separator + currentOrgUnit.getParent().getParent().getShortName()
							+ File.separator + currentOrgUnit.getParent().getShortName()
							+ File.separator + currentOrgUnit.getShortName()
							+ File.separator + selReportObj.getName();

					newdir = new File(outputReportFolderPath);
					if (!newdir.exists()) {
						newdir.mkdirs();
					}

					String outPutFileName = reportFileNameTB;
					String outputReportPath = outputReportFolderPath
							+ File.separator + outPutFileName;
					Workbook templateWorkbook = Workbook.getWorkbook(new File(
							inputTemplatePath));
					WritableWorkbook outputReportWorkbook = Workbook
							.createWorkbook(new File(outputReportPath),
									templateWorkbook);
					aggDeMap = new HashMap<String, String>();
					aggDeMap.putAll(reportService.getAggDataFromDataValueTable(
							"" + currentOrgUnit.getId(), dataElmentIdsByComma,
							periodIdsByComma));

					// int count1 = 0;

					Iterator<Report_inDesign> reportDesignIterator = reportDesignList
							.iterator();
					while (reportDesignIterator.hasNext()) {
						Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator
								.next();

						String deType = report_inDesign.getPtype();
						String sType = report_inDesign.getStype();
						String deCodeString = report_inDesign.getExpression();
						String tempStr = "";

						Calendar tempStartDate = Calendar.getInstance();
						Calendar tempEndDate = Calendar.getInstance();
						calendarList = new ArrayList<Calendar>(
								reportService.getStartingEndingPeriods(deType,
										selectedPeriod));
						if (calendarList == null || calendarList.isEmpty()) {
							tempStartDate
									.setTime(selectedPeriod.getStartDate());
							tempEndDate.setTime(selectedPeriod.getEndDate());
							return SUCCESS;
						} else {
							tempStartDate = calendarList.get(0);
							tempEndDate = calendarList.get(1);
						}

						if (deCodeString.equalsIgnoreCase("FACILITY")) {
							tempStr = currentOrgUnit.getName();
						} else if (deCodeString
								.equalsIgnoreCase("FACILITY-NOREPEAT")) {
							tempStr = parentUnit;
						} else if (deCodeString.equalsIgnoreCase("FACILITYP")) {
							tempStr = currentOrgUnit.getParent().getName();
						} else if (deCodeString.equalsIgnoreCase("FACILITYPP")) {
							tempStr = currentOrgUnit.getParent().getParent()
									.getName();
						} else if (deCodeString.equalsIgnoreCase("FACILITYPPP")) {
							tempStr = currentOrgUnit.getParent().getParent()
									.getParent().getName();
						} else if (deCodeString
								.equalsIgnoreCase("FACILITYPPPP")) {
							tempStr = currentOrgUnit.getParent().getParent()
									.getParent().getParent().getName();
						} else if (deCodeString.equalsIgnoreCase("PERIOD")
								|| deCodeString
										.equalsIgnoreCase("PERIOD-NOREPEAT")) {
							tempStr = simpleDateFormat.format(sDate);
						} else if (deCodeString
								.equalsIgnoreCase("PERIOD-MONTH")) {
							tempStr = monthFormat.format(sDate);
						} else if (deCodeString.equalsIgnoreCase("PERIOD-YEAR")) {
							tempStr = yearFormat.format(sDate);
						} else if (deCodeString
								.equalsIgnoreCase("MONTH-START-SHORT")) {
							tempStr = simpleMonthFormat.format(sDate);
						} else if (deCodeString
								.equalsIgnoreCase("MONTH-END-SHORT")) {
							tempStr = simpleMonthFormat.format(eDate);
						} else if (deCodeString.equalsIgnoreCase("MONTH-START")) {
							tempStr = monthFormat.format(sDate);
						} else if (deCodeString.equalsIgnoreCase("MONTH-END")) {
							tempStr = monthFormat.format(eDate);
						} else if (deCodeString.equalsIgnoreCase("SLNO")) {
							tempStr = "" + (orgUnitCount + 1);
						} else if (deCodeString.equalsIgnoreCase("NA")) {
							tempStr = " ";
						} else {
							if (sType.equalsIgnoreCase("dataelement")) {
								tempStr = getAggVal(deCodeString, aggDeMap);

								if (deCodeString.equalsIgnoreCase("[1.1]")
										|| deCodeString
												.equalsIgnoreCase("[2.1]")
										|| deCodeString
												.equalsIgnoreCase("[153.1]")
										|| deCodeString
												.equalsIgnoreCase("[155.1]")
										|| deCodeString
												.equalsIgnoreCase("[157.1]")
										|| deCodeString
												.equalsIgnoreCase("[158.1]")
										|| deCodeString
												.equalsIgnoreCase("[160.1]")) {
									if (tempStr.equalsIgnoreCase("0.0")) {
										tempStr = "" + 1.0;
									} else if (tempStr.equalsIgnoreCase("1.0")) {
										tempStr = "" + 0.0;
									} else {
									}
								}
							} else if (sType
									.equalsIgnoreCase("dataelement-boolean")) {
								tempStr = reportService.getBooleanDataValue(
										deCodeString, tempStartDate.getTime(),
										tempEndDate.getTime(), currentOrgUnit,
										reportModelTB);
							} else {
								tempStr = reportService
										.getIndividualResultIndicatorValue(
												deCodeString,
												tempStartDate.getTime(),
												tempEndDate.getTime(),
												currentOrgUnit);
							}
						}

						int tempRowNo = report_inDesign.getRowno();
						int tempColNo = report_inDesign.getColno();
						int sheetNo = report_inDesign.getSheetno();
						WritableSheet sheet0 = outputReportWorkbook
								.getSheet(sheetNo);

						if (tempStr == null || tempStr.equals(" ")) {
							tempColNo += orgUnitCount;

							WritableCellFormat wCellformat = new WritableCellFormat();
							wCellformat.setBorder(Border.ALL,
									BorderLineStyle.THIN);
							wCellformat.setWrap(true);
							wCellformat.setAlignment(Alignment.CENTRE);

							sheet0.addCell(new Blank(tempColNo, tempRowNo,
									wCellformat));
						} else {
							if (reportModelTB
									.equalsIgnoreCase("DYNAMIC-ORGUNIT")) {
								if (deCodeString.equalsIgnoreCase("FACILITYP")
										|| deCodeString
												.equalsIgnoreCase("FACILITYPP")
										|| deCodeString
												.equalsIgnoreCase("FACILITYPPP")
										|| deCodeString
												.equalsIgnoreCase("FACILITYPPPP")) {
								} else if (deCodeString
										.equalsIgnoreCase("PERIOD")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-NOREPEAT")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-WEEK")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-MONTH")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-QUARTER")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-YEAR")
										|| deCodeString
												.equalsIgnoreCase("MONTH-START")
										|| deCodeString
												.equalsIgnoreCase("MONTH-END")
										|| deCodeString
												.equalsIgnoreCase("MONTH-START-SHORT")
										|| deCodeString
												.equalsIgnoreCase("MONTH-END-SHORT")
										|| deCodeString
												.equalsIgnoreCase("SIMPLE-QUARTER")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-MONTHS-SHORT")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-MONTHS")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-START-SHORT")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-END-SHORT")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-START")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-END")
										|| deCodeString
												.equalsIgnoreCase("SIMPLE-YEAR")
										|| deCodeString
												.equalsIgnoreCase("YEAR-END")
										|| deCodeString
												.equalsIgnoreCase("YEAR-FROMTO")) {
								} else {
									tempColNo += orgUnitCount;
								}
							} else if (reportModelTB
									.equalsIgnoreCase("dynamicwithrootfacility")) {
								if (deCodeString.equalsIgnoreCase("FACILITYP")
										|| deCodeString
												.equalsIgnoreCase("FACILITY-NOREPEAT")
										|| deCodeString
												.equalsIgnoreCase("FACILITYPP")
										|| deCodeString
												.equalsIgnoreCase("FACILITYPPP")
										|| deCodeString
												.equalsIgnoreCase("FACILITYPPPP")) {
								} else if (deCodeString
										.equalsIgnoreCase("PERIOD")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-NOREPEAT")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-WEEK")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-MONTH")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-QUARTER")
										|| deCodeString
												.equalsIgnoreCase("PERIOD-YEAR")
										|| deCodeString
												.equalsIgnoreCase("MONTH-START")
										|| deCodeString
												.equalsIgnoreCase("MONTH-END")
										|| deCodeString
												.equalsIgnoreCase("MONTH-START-SHORT")
										|| deCodeString
												.equalsIgnoreCase("MONTH-END-SHORT")
										|| deCodeString
												.equalsIgnoreCase("SIMPLE-QUARTER")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-MONTHS-SHORT")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-MONTHS")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-START-SHORT")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-END-SHORT")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-START")
										|| deCodeString
												.equalsIgnoreCase("QUARTER-END")
										|| deCodeString
												.equalsIgnoreCase("SIMPLE-YEAR")
										|| deCodeString
												.equalsIgnoreCase("YEAR-END")
										|| deCodeString
												.equalsIgnoreCase("YEAR-FROMTO")) {
								} else {
									tempRowNo += orgUnitCount;
								}
							}

							WritableCell cell = sheet0.getWritableCell(
									tempColNo, tempRowNo);

							CellFormat cellFormat = cell.getCellFormat();
							WritableCellFormat wCellformat = new WritableCellFormat();
							wCellformat.setBorder(Border.ALL,
									BorderLineStyle.THIN);
							wCellformat.setWrap(true);
							wCellformat.setAlignment(Alignment.CENTRE);

							if (cell.getType() == CellType.LABEL) {
								Label l = (Label) cell;
								l.setString(tempStr);
								l.setCellFormat(cellFormat);
							} else {
								try {
									sheet0.addCell(new Number(tempColNo,
											tempRowNo, Double
													.parseDouble(tempStr),
											wCellformat));
								} catch (Exception e) {
									sheet0.addCell(new Label(tempColNo,
											tempRowNo, tempStr, wCellformat));
								}
							}
						}
						// count1++;
					}// inner while loop end

					outputReportWorkbook.write();
					outputReportWorkbook.close();

					// System.out.println(currentOrgUnit.getName()+" : "+selectedPeriod.getName()+" : "+
					// selReportObj.getName()+" : Report Generation End Time is : "+new
					// Date());

					totalreports++;
				}

				orgUnitCount++;
			}// outer while loop end

			System.out.println("Total number of " + selReportObj.getName() + " generated reports ==" + totalreports);			
			districtOrgUnitList=new ArrayList<OrganisationUnit>(organisationUnitService.getOrganisationUnitsAtLevel(ORGANISATION_UNIT_LEVEL_DISTRICT));  
		    Collections.sort(districtOrgUnitList, new IdentifiableObjectNameComparator());    		        		  
		                     
		    // email Info       
		    HOST_NAME=systemSettingManager.getEmailHostName();
		    SMTP_PORT=systemSettingManager.getEmailPort();
		    USER_NAME=systemSettingManager.getEmailUsername();
		    PASSWORD=systemSettingManager.getEmailPassword();
		   
		    for( OrganisationUnit selOrgUnit:districtOrgUnitList )
	        {
		    	if((organisationUnitService.getLevelOfOrganisationUnit(selOrgUnit.getId()) == 3) 
		    		&& selReportObj.getName().trim().equalsIgnoreCase( BULK_LINELISTING_CHC_AND_EQUIVALENT_FACILITIES )){	
		    		
		    		EMAIL_SUBJECT="Find Attachment Contain Reports : " + selReportObj.getName()+" Organisatonunit : "+selOrgUnit.getName() +" Period : "+selectedSecondLastMonthlyPeriod.getName() +" Report Generated At : "+new Date();		    		
		    		BODY="DHIS2 Generated Bulk Report. Please Do Not reply.";
					List<User> distAndStateUsers = new ArrayList<User>(userService.getUsersByOrganisationUnit(selOrgUnit.getId()));
		    		distAndStateUsers.addAll(userService.getUsersByOrganisationUnit(selOrgUnit.getParent().getId()));
		    		//System.out.println("Number of district level users:" + distAndStateUsers.size());	    		
		        	List<User> districtAndStateUsersWitEmailId = new ArrayList<User>();		        	
		        	for(User user:distAndStateUsers){
		        		if(user.getEmail() != null){
		        			districtAndStateUsersWitEmailId.add( user );
		        		}
		        	}			        	
		    		System.out.println("Number of district and state users With Email Ids: "+districtAndStateUsersWitEmailId.size() );		    			    			    		
		    		for( User user : districtAndStateUsersWitEmailId )
		    		{	    			
		    			outputReportFolderPath=System.getenv(DHIS2_HOME)
								+ File.separator + RA_FOLDER_NAME
								+ File.separator + OUT_PUT_REPORT_FOLDER
								+ File.separator + OUT_PUT_REPORT_FOLDER_CHC
								+ File.separator + dateFormat.format(presentDate).trim()+"-"+ monthFormat.format(presentDate).trim()+"-"+ yearFormat.format(presentDate).trim()
								+ File.separator + simpleDateFormat.format(selectedSecondLastMonthlyPeriod.getStartDate()).trim()
								//+ File.separator + selOrgUnit.getParent().getParent().getParent().getShortName().trim() 
								//+ File.separator + selOrgUnit.getParent().getParent().getShortName().trim() 
								+ File.separator + selOrgUnit.getParent().getShortName()
								+ File.separator + selOrgUnit.getShortName();
								
				    	EMAIL_RECEIVER=user.getEmail();   	  			
						pathOfZipFile=exportFolder(outputReportFolderPath, reportFileNameTB, selOrgUnit, selectedSecondLastMonthlyPeriod);
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
																			+", Period : "+selectedSecondLastMonthlyPeriod.getStartDate()
																			+", Report Generated At : "+new Date());
						  sendEmail(USER_NAME,PASSWORD,HOST_NAME,SMTP_PORT.intValue(),EMAIL_RECEIVER,EMAIL_SUBJECT,BODY,ATTACHMENT_PATH); 
						  System.out.println("Email sent with attached reports is successfull. : " + selOrgUnit.getName()+" : "+selectedSecondLastMonthlyPeriod.getName()+" : "+ selReportObj.getName()+" : Report Generation End Time is : "+new Date());
					    }
		    		}
		    	}	    		    	
			    //inputStream=new BufferedInputStream(new FileInputStream(pathOfZipFile));
			    File outputReportFile = new File( pathOfZipFile );
			    outputReportFile.delete();
		    }	        	       
		}
		try {
			cleanUp();
			System.gc();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	protected void cleanUp() throws Throwable {
		if (presentDate != null) {
			presentDate = null;
		}
		if (orgUnitList != null) {
			orgUnitList = null;
		}
		if (simpleDateFormat != null) {
			simpleDateFormat = null;
		}
		if (monthFormat != null) {
			monthFormat = null;
		}
		if (yearFormat != null) {
			yearFormat = null;
		}
		if (dateFormat != null) {
			dateFormat = null;
		}
		if (simpleMonthFormat != null) {
			simpleMonthFormat = null;
		}
		if (orgUnitListSC != null) {
			orgUnitListSC = null;
		}
		if (orgUnitGroup != null) {
			orgUnitGroup = null;
		}
		if (selOrgUnit != null) {
			selOrgUnit = null;
		}
		if (outputReportFolderPath != null) {
			outputReportFolderPath = null;
		}
		if (selReportObjList != null) {
			selReportObjList = null;
		}
		if (selectedPeriod != null) {
			selectedPeriod = null;
		}
		if (selectedPeriodList != null) {
			selectedPeriodList = null;
		}
		if (monthlyPeriods != null) {
			monthlyPeriods = null;
		}
		if (tempPeriodList != null) {
			tempPeriodList = null;
		}
		if (tempPeriodIds != null) {
			tempPeriodIds = null;
		}
		if (newdir != null) {
			newdir = null;
		}
		if (aggDeMap != null) {
			aggDeMap = null;
		}
		if (calendarList != null) {
			calendarList = null;
		}
		if (wCellformat != null) {
			wCellformat = null;
		}
		super.finalize();
	}

	public boolean zipDirectory(String dir, String zipfile) throws IOException,
			IllegalArgumentException {

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
																// file
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

	// getting data value using Map
	private String getAggVal(String expression, Map<String, String> aggDeMap) {
		try {
			Pattern pattern = Pattern.compile("(\\[\\d+\\.\\d+\\])");

			Matcher matcher = pattern.matcher(expression);
			StringBuffer buffer = new StringBuffer();

			String resultValue = "";

			while (matcher.find()) {
				String replaceString = matcher.group();

				replaceString = replaceString.replaceAll("[\\[\\]]", "");

				replaceString = aggDeMap.get(replaceString);

				if (replaceString == null) {
					replaceString = "0";
				}

				matcher.appendReplacement(buffer, replaceString);

				resultValue = replaceString;
			}

			matcher.appendTail(buffer);

			double d = 0.0;
			try {
				d = MathUtils.calculateExpression(buffer.toString());
			} catch (Exception e) {
				d = 0.0;
				resultValue = "";
			}

			resultValue = "" + (double) d;

			return resultValue;
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Illegal DataElement id", ex);
		}
	}
	   
	public String exportFolder(String outputReportFolderPath, String reportFileNameTB, OrganisationUnit selOrgUnit, Period selectedPeriod) throws IOException, IllegalArgumentException{                                   
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
