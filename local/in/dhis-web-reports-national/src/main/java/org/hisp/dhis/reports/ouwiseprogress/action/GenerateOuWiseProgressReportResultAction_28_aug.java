package org.hisp.dhis.reports.ouwiseprogress.action;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.system.util.TextUtils;

import com.opensymphony.xwork2.Action;

public class GenerateOuWiseProgressReportResultAction_28_aug
  implements Action
{
  private final String GENERATEAGGDATA = "generateaggdata";

  private final String USEEXISTINGAGGDATA = "useexistingaggdata";

  private final String USECAPTUREDDATA = "usecaptureddata";
  private ReportService reportService;
  private PeriodService periodService;
  private OrganisationUnitService organisationUnitService;
  private OrganisationUnitGroupService organisationUnitGroupService;
  private DataSetService dataSetService;
  private I18nFormat format;
  private InputStream inputStream;
  private String fileName;
  private String reportList;
  private String startDate;
  private String endDate;
  private int ouIDTB;
  private String aggData;
  private Integer orgUnitGroup;
  private OrganisationUnit selectedOrgUnit;
  private List<OrganisationUnit> orgUnitList;
  private SimpleDateFormat simpleDateFormat;
  private String reportFileNameTB;
  private String reportModelTB;
  private Date sDate;
  private Date eDate;
  private String raFolderName;
  private SimpleDateFormat dateTimeFormat;
  private PeriodType selPeriodType;
  private List<OrganisationUnit> tempOrgUnitList;
  private Map<String, Integer> orgGroupmemberCount;
  private SimpleDateFormat simpleDateFormatMonthYear;

  public void setReportService(ReportService reportService)
  {
    reportService = reportService;
  }

  public void setPeriodService(PeriodService periodService)
  {
    periodService = periodService;
  }

  public void setOrganisationUnitService(OrganisationUnitService organisationUnitService)
  {
    organisationUnitService = organisationUnitService;
  }

  public void setOrganisationUnitGroupService(OrganisationUnitGroupService organisationUnitGroupService)
  {
    organisationUnitGroupService = organisationUnitGroupService;
  }

  public void setDataSetService(DataSetService dataSetService)
  {
    dataSetService = dataSetService;
  }

  public void setFormat(I18nFormat format)
  {
    format = format;
  }

  public InputStream getInputStream()
  {
    return inputStream;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setReportList(String reportList)
  {
    reportList = reportList;
  }

  public void setStartDate(String startDate)
  {
    startDate = startDate;
  }

  public void setEndDate(String endDate)
  {
    endDate = endDate;
  }

  public void setOuIDTB(int ouIDTB)
  {
    ouIDTB = ouIDTB;
  }

  public void setAggData(String aggData)
  {
    aggData = aggData;
  }

  public void setOrgUnitGroup(Integer orgUnitGroup)
  {
    orgUnitGroup = orgUnitGroup;
  }

  public String execute()
    throws Exception
  {
    raFolderName = reportService.getRAFolderName();
    simpleDateFormat = new SimpleDateFormat("MMM-yy");

    simpleDateFormatMonthYear = new SimpleDateFormat("MMM-yyyy");

    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateTimeFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss zzzz");

    String[] colArray = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY", "BZ" };

    String deCodesXMLFileName = "";

    Report_in selReportObj = reportService.getReport(Integer.parseInt(reportList));

    deCodesXMLFileName = selReportObj.getXmlTemplateName();
    reportModelTB = selReportObj.getModel();
    reportFileNameTB = selReportObj.getExcelTemplateName();

    selectedOrgUnit = new OrganisationUnit();
    selectedOrgUnit = organisationUnitService.getOrganisationUnit(ouIDTB);
    int selectedOrgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit(ouIDTB);

    System.out.println(selectedOrgUnit.getName() + " : " + selReportObj.getName() + " : Report Generation Start Time is : " + new Date());

    tempOrgUnitList = new ArrayList(selectedOrgUnit.getChildren());

    if (reportModelTB.equalsIgnoreCase("PROGRESSIVE-ORGUNIT"))
    {
      if (orgUnitGroup.intValue() != 0)
      {
        orgUnitList = getChildOrgUnitTree(selectedOrgUnit);
        OrganisationUnitGroup ouGroup = organisationUnitGroupService.getOrganisationUnitGroup(orgUnitGroup.intValue());

        if (ouGroup != null)
        {
          orgUnitList.retainAll(ouGroup.getMembers());
        }
      }
      else
      {
        orgUnitList = new ArrayList(selectedOrgUnit.getChildren());
      }

      Collections.sort(orgUnitList, new IdentifiableObjectNameComparator());

      if (selectedOrgUnitLevel != 2)
      {
        orgUnitList.add(selectedOrgUnit);
      }

    }

    String inputTemplatePath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;

    String outputReportPath = System.getenv("DHIS2_HOME") + File.separator + "temp";
    File newdir = new File(outputReportPath);
    if (!newdir.exists())
    {
      newdir.mkdirs();
    }
    outputReportPath = outputReportPath + File.separator + UUID.randomUUID().toString() + ".xls";

    Workbook templateWorkbook = Workbook.getWorkbook(new File(inputTemplatePath));

    WritableWorkbook outputReportWorkbook = Workbook.createWorkbook(new File(outputReportPath), templateWorkbook);

    WritableCellFormat wCellformat = new WritableCellFormat();
    wCellformat.setBorder(Border.ALL, BorderLineStyle.THIN);
    wCellformat.setAlignment(Alignment.CENTRE);
    wCellformat.setVerticalAlignment(VerticalAlignment.CENTRE);
    wCellformat.setWrap(true);

    sDate = format.parseDate(startDate);
    eDate = format.parseDate(endDate);

    List periodList = new ArrayList();

    DataSet dataSet = new DataSet();

    String dataSetIDs = selReportObj.getDataSetIds();

    if ((dataSetIDs != null) && (!dataSetIDs.equalsIgnoreCase("")))
    {
      String[] tempDataSetIDs = dataSetIDs.split(",");

      String firstDsId = tempDataSetIDs[0];

      dataSet = dataSetService.getDataSet(Integer.parseInt(firstDsId));

      if (dataSet.getPeriodType().getName().equalsIgnoreCase("Forteen"))
      {
        selPeriodType = periodService.getPeriodTypeByName("Forteen");

        periodList = new ArrayList(periodService.getPeriodsBetweenDates( selPeriodType, sDate, eDate));
      }
      else
      {
        selPeriodType = selReportObj.getPeriodType();
        periodList = new ArrayList(periodService.getPeriodsBetweenDates(selPeriodType, sDate, eDate));
      }

    }
    else
    {
      selPeriodType = selReportObj.getPeriodType();

      periodList = new ArrayList(periodService.getIntersectingPeriods(sDate, eDate));
    }

    Collection periodIds = new ArrayList(ConversionUtils.getIdentifiers(Period.class, periodList));
    String periodIdsByComma = TextUtils.getCommaDelimitedString(periodIds);

    List reportDesignList = reportService.getReportDesign(deCodesXMLFileName);

    String dataElmentIdsByComma = reportService.getDataelementIds(reportDesignList);

    Map aggDeMapForselectedFacility = new HashMap();
    aggDeMapForselectedFacility.putAll(reportService.getAggDataFromDataValueTable("" + selectedOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma));

    int orgUnitCount = 0;
    Iterator it = orgUnitList.iterator();
    while (it.hasNext())
    {
      OrganisationUnit currentOrgUnit = (OrganisationUnit)it.next();
      List childOrgUnitTree = new ArrayList(organisationUnitService.getOrganisationUnitWithChildren(currentOrgUnit.getId()));
      List childOrgUnitTreeIds = new ArrayList(ConversionUtils.getIdentifiers(OrganisationUnit.class, childOrgUnitTree));
      String childOrgUnitsByComma = TextUtils.getCommaDelimitedString(childOrgUnitTreeIds);

      Map aggDeMap = new HashMap();
      if (aggData.equalsIgnoreCase("useexistingaggdata"))
      {
        aggDeMap.putAll(reportService.getResultDataValueFromAggregateTable(Integer.valueOf(currentOrgUnit.getId()), dataElmentIdsByComma, periodIdsByComma));
      }
      else if (aggData.equalsIgnoreCase("generateaggdata"))
      {
        aggDeMap.putAll(reportService.getAggDataFromDataValueTable(childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma));
      }
      else if (aggData.equalsIgnoreCase("usecaptureddata"))
      {
        aggDeMap.putAll(reportService.getAggDataFromDataValueTable("" + currentOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma));
      }

      int count1 = 0;
      Iterator reportDesignIterator = reportDesignList.iterator();
      while (reportDesignIterator.hasNext())
      {
        Report_inDesign report_inDesign = (Report_inDesign)reportDesignIterator.next();

        String sType = report_inDesign.getStype();
        String deCodeString = report_inDesign.getExpression();
        String tempStr = "";
        String tempStrForSelectedFacility = "";
        if (deCodeString.equalsIgnoreCase("FACILITY"))
        {
          tempStr = selectedOrgUnit.getName();
          tempStrForSelectedFacility = selectedOrgUnit.getName();
        }
        else if (deCodeString.equalsIgnoreCase("PROGRESSIVE-ORGUNIT"))
        {
          tempStr = currentOrgUnit.getName();
          tempStrForSelectedFacility = selectedOrgUnit.getName() + "-ONLY";
        }
        else if (deCodeString.equalsIgnoreCase("FACILITYP"))
        {
          tempStr = selectedOrgUnit.getParent().getName();
          tempStrForSelectedFacility = selectedOrgUnit.getParent().getName();
        }
        else if (deCodeString.equalsIgnoreCase("FACILITYPP"))
        {
          tempStr = selectedOrgUnit.getParent().getParent().getName();
          tempStrForSelectedFacility = selectedOrgUnit.getParent().getParent().getName();
        }
        else if (deCodeString.equalsIgnoreCase("DATE-FROM"))
        {
          tempStr = dayFormat.format(sDate);
          tempStrForSelectedFacility = dayFormat.format(sDate);
        }
        else if (deCodeString.equalsIgnoreCase("DATE-TO"))
        {
          tempStr = dayFormat.format(eDate);
          tempStrForSelectedFacility = dayFormat.format(eDate);
        }
        else if (deCodeString.equalsIgnoreCase("MONTH-FROM"))
        {
          tempStr = simpleDateFormat.format(sDate);
          tempStrForSelectedFacility = simpleDateFormat.format(sDate);
        }
        else if (deCodeString.equalsIgnoreCase("MONTH-TO"))
        {
          tempStr = simpleDateFormat.format(eDate);
          tempStrForSelectedFacility = simpleDateFormat.format(eDate);
        }
        else if (deCodeString.equalsIgnoreCase("CURRENTDATETIME"))
        {
          tempStr = dateTimeFormat.format(new Date());
        }
        else if (deCodeString.equalsIgnoreCase("MONTH-YEAR"))
        {
          tempStr = simpleDateFormatMonthYear.format(sDate);
        }
        else if (deCodeString.equalsIgnoreCase("NA"))
        {
          tempStr = " ";
          tempStrForSelectedFacility = " ";
        }
        else if (sType.equalsIgnoreCase("dataelement"))
        {
          if (aggData.equalsIgnoreCase("usecaptureddata"))
          {
            tempStr = getAggVal(deCodeString, aggDeMap);

            tempStrForSelectedFacility = getAggVal(deCodeString, aggDeMapForselectedFacility);
          }
          else if (aggData.equalsIgnoreCase("generateaggdata"))
          {
            tempStr = getAggVal(deCodeString, aggDeMap);
            tempStrForSelectedFacility = getAggVal(deCodeString, aggDeMapForselectedFacility);
          }
          else if (aggData.equalsIgnoreCase("useexistingaggdata"))
          {
            tempStr = getAggVal(deCodeString, aggDeMap);
            tempStrForSelectedFacility = getAggVal(deCodeString, aggDeMapForselectedFacility);
          }

        }
        else if (sType.equalsIgnoreCase("dataelement_orgunitcountbygroup_opd"))
        {
          String deExp = deCodeString.split("---")[0];

          String orgunitGroup = deCodeString.split("---")[1];

          if (aggData.equalsIgnoreCase("generateaggdata"))
          {
            Integer orgUnitGroupCount = reportService.getOrgunitCountByOrgunitGroup(orgunitGroup, Integer.valueOf(currentOrgUnit.getId()));
            String resultDataValue = getAggVal(deExp, aggDeMap);

            tempStr = "" + Double.parseDouble(resultDataValue) / (orgUnitGroupCount.intValue() * 33) * 100.0D;

            Double dataValue = Double.valueOf(0.0D);
            dataValue = Double.valueOf(Math.round(Double.parseDouble(tempStr) * Math.pow(10.0D, 0.0D)) / Math.pow(10.0D, 0.0D));

            tempStr = "" + dataValue;
          }

        }
        else if (sType.equalsIgnoreCase("dataelement_orgunitcountbygroup_ipd"))
        {
          String deExp = deCodeString.split("---")[0];

          String orgunitGroup = deCodeString.split("---")[1];

          if (aggData.equalsIgnoreCase("generateaggdata"))
          {
            Integer orgUnitGroupCount = reportService.getOrgunitCountByOrgunitGroup(orgunitGroup, Integer.valueOf(currentOrgUnit.getId()));
            String resultDataValue = getAggVal(deExp, aggDeMap);

            tempStr = "" + Double.parseDouble(resultDataValue) / (orgUnitGroupCount.intValue() * 112) * 100.0D;

            Double dataValue = Double.valueOf(0.0D);
            dataValue = Double.valueOf(Math.round(Double.parseDouble(tempStr) * Math.pow(10.0D, 0.0D)) / Math.pow(10.0D, 0.0D));

            tempStr = "" + dataValue;
          }

        }
        else if (sType.equalsIgnoreCase("dataelement_orgunitcountbygroup_percentage"))
        {
          String deExp = deCodeString.split("---")[0];

          String orgunitGroup = deCodeString.split("---")[1];

          if (aggData.equalsIgnoreCase("generateaggdata"))
          {
            Integer orgUnitGroupCount = reportService.getOrgunitCountByOrgunitGroup(orgunitGroup, Integer.valueOf(currentOrgUnit.getId()));
            String resultDataValue = getAggVal(deExp, aggDeMap);

            tempStr = "" + Double.parseDouble(resultDataValue) / orgUnitGroupCount.intValue() * 100.0D;

            Double dataValue = Double.valueOf(0.0D);
            dataValue = Double.valueOf(Math.round(Double.parseDouble(tempStr) * Math.pow(10.0D, 0.0D)) / Math.pow(10.0D, 0.0D));

            tempStr = "" + dataValue;
          }

        }
        else if (sType.equalsIgnoreCase("dataelement_orgunitcountbygroup_assessment"))
        {
          String deExp = deCodeString.split("---")[0];

          String orgunitGroup = deCodeString.split("---")[1];

          if (aggData.equalsIgnoreCase("generateaggdata"))
          {
            Integer orgUnitGroupCount = reportService.getOrgunitCountByOrgunitGroup(orgunitGroup, Integer.valueOf(currentOrgUnit.getId()));
            String resultDataValue = getAggVal(deExp, aggDeMap);

            tempStr = "" + Double.parseDouble(resultDataValue) / orgUnitGroupCount.intValue();

            Double dataValue = Double.valueOf(0.0D);
            dataValue = Double.valueOf(Math.round(Double.parseDouble(tempStr) * Math.pow(10.0D, 0.0D)) / Math.pow(10.0D, 0.0D));

            tempStr = "" + dataValue;
          }

        }
        else if (sType.equalsIgnoreCase("dataelement_round"))
        {
          if (aggData.equalsIgnoreCase("generateaggdata"))
          {
            String resultDataValue = getAggVal(deCodeString, aggDeMap);

            tempStr = resultDataValue;

            Double dataValue = Double.valueOf(0.0D);
            dataValue = Double.valueOf(Math.round(Double.parseDouble(tempStr) * Math.pow(10.0D, 0.0D)) / Math.pow(10.0D, 0.0D));

            tempStr = "" + dataValue;
          }

        }
        else if (sType.equalsIgnoreCase("dataelement-count"))
        {
          String deIdsByComma = "-1";

          for (int i = 0; i < deCodeString.split(",").length; i++)
          {
            deIdsByComma = deIdsByComma + "," + deCodeString.split(",")[i];
          }

          if (aggData.equalsIgnoreCase("usecaptureddata"))
          {
            tempStr = "" + reportService.getDataCountFromDataValueTable(new StringBuilder().append("").append(currentOrgUnit.getId()).toString(), deIdsByComma, periodIdsByComma);
          }
          else
          {
            tempStr = "" + reportService.getDataCountFromDataValueTable(childOrgUnitsByComma, deIdsByComma, periodIdsByComma);
          }

        }
        else if (sType.equalsIgnoreCase("formula"))
        {
          tempStr = deCodeString;
        }

        int tempRowNo = report_inDesign.getRowno();
        int tempColNo = report_inDesign.getColno();
        int sheetNo = report_inDesign.getSheetno();
        WritableSheet sheet0 = outputReportWorkbook.getSheet(sheetNo);

        if (reportModelTB.equalsIgnoreCase("PROGRESSIVE-ORGUNIT"))
        {
          if ((!deCodeString.equalsIgnoreCase("FACILITY")) && (!deCodeString.equalsIgnoreCase("FACILITYP")) && (!deCodeString.equalsIgnoreCase("FACILITYPP")) && (!deCodeString.equalsIgnoreCase("MONTH-FROM")) && (!deCodeString.equalsIgnoreCase("MONTH-TO")) && (!deCodeString.equalsIgnoreCase("DATE-FROM")) && (!deCodeString.equalsIgnoreCase("DATE-TO")) && (!deCodeString.equalsIgnoreCase("CURRENTDATETIME")) && (!deCodeString.equalsIgnoreCase("MONTH-YEAR")))
          {
            tempColNo += orgUnitCount;
          }

          try
          {
            if (sType.equalsIgnoreCase("formula"))
            {
              tempStr = tempStr.replace("?", colArray[tempColNo]);
              if ((orgUnitCount == orgUnitList.size() - 1) && (selectedOrgUnitLevel != 2))
              {
                sheet0.addCell(new Formula(tempColNo + 1, tempRowNo, tempStr, getCellFormat1()));
              }
              else
              {
                sheet0.addCell(new Formula(tempColNo, tempRowNo, tempStr, wCellformat));
              }
            }
            else
            {
              if ((orgUnitCount == orgUnitList.size() - 1) && (selectedOrgUnitLevel != 2))
              {
                if ((!deCodeString.equalsIgnoreCase("FACILITY")) && (!deCodeString.equalsIgnoreCase("FACILITYP")) && (!deCodeString.equalsIgnoreCase("FACILITYPP")) && (!deCodeString.equalsIgnoreCase("MONTH-FROM")) && (!deCodeString.equalsIgnoreCase("MONTH-TO")) && (!deCodeString.equalsIgnoreCase("DATE-FROM")) && (!deCodeString.equalsIgnoreCase("DATE-TO")) && (!deCodeString.equalsIgnoreCase("CURRENTDATETIME")) && (deCodeString.equalsIgnoreCase("MONTH-YEAR")))
                {
                  continue;
                }

                sheet0.addCell(new Number(tempColNo, tempRowNo, Double.parseDouble(tempStrForSelectedFacility), getCellFormat2()));
                sheet0.addCell(new Number(tempColNo + 1, tempRowNo, Double.parseDouble(tempStr), getCellFormat1())); 
              }

              sheet0.addCell(new Number(tempColNo, tempRowNo, Double.parseDouble(tempStr), wCellformat));
            }

          }
          catch (Exception e)
          {
            if ((orgUnitCount == orgUnitList.size() - 1) && (selectedOrgUnitLevel != 2))
            {
              if ((!deCodeString.equalsIgnoreCase("FACILITY")) && (!deCodeString.equalsIgnoreCase("FACILITYP")) && (!deCodeString.equalsIgnoreCase("FACILITYPP")) && (!deCodeString.equalsIgnoreCase("MONTH-FROM")) && (!deCodeString.equalsIgnoreCase("MONTH-TO")) && (!deCodeString.equalsIgnoreCase("DATE-FROM")) && (!deCodeString.equalsIgnoreCase("DATE-TO")) && (!deCodeString.equalsIgnoreCase("CURRENTDATETIME")) && (deCodeString.equalsIgnoreCase("MONTH-YEAR")))
              {
                continue;
              }

              sheet0.addCell(new Label(tempColNo, tempRowNo, tempStrForSelectedFacility, getCellFormat2()));
              sheet0.addCell(new Label(tempColNo + 1, tempRowNo, tempStr, getCellFormat1())); 
            }

            sheet0.addCell(new Label(tempColNo, tempRowNo, tempStr, wCellformat));
          }
        }
        else
        {
          label3777: count1++;
        }
      }
      orgUnitCount++;
    }

    if (selectedOrgUnitLevel == 2)
    {
      WritableCellFormat totalCellformat = new WritableCellFormat(getCellFormat1());
      totalCellformat.setBorder(Border.ALL, BorderLineStyle.THIN);
      totalCellformat.setAlignment(Alignment.CENTRE);
      totalCellformat.setVerticalAlignment(VerticalAlignment.CENTRE);
      totalCellformat.setWrap(true);

      Iterator reportDesignIterator = reportDesignList.iterator();
      while (reportDesignIterator.hasNext())
      {
        Report_inDesign reportDesign = (Report_inDesign)reportDesignIterator.next();

        String deCodeString = reportDesign.getExpression();

        if ((!deCodeString.equalsIgnoreCase("FACILITY")) && (!deCodeString.equalsIgnoreCase("FACILITYP")) && (!deCodeString.equalsIgnoreCase("FACILITYPP")) && (!deCodeString.equalsIgnoreCase("MONTH-FROM")) && (!deCodeString.equalsIgnoreCase("MONTH-TO")) && (!deCodeString.equalsIgnoreCase("DATE-FROM")) && (!deCodeString.equalsIgnoreCase("CURRENTDATETIME")) && (!deCodeString.equalsIgnoreCase("DATE-TO")) && (!deCodeString.equalsIgnoreCase("MONTH-YEAR")))
        {
          int tempRowNo = reportDesign.getRowno();
          int tempColNo = reportDesign.getColno();
          int sheetNo = reportDesign.getSheetno();

          String colStart = "" + colArray[tempColNo];
          String colEnd = "" + colArray[(tempColNo + orgUnitCount - 1)];

          String tempFormula = "SUM(" + colStart + (tempRowNo + 1) + ":" + colEnd + (tempRowNo + 1) + ")";

          WritableSheet totalSheet = outputReportWorkbook.getSheet(sheetNo);

          if (deCodeString.equalsIgnoreCase("PROGRESSIVE-ORGUNIT"))
          {
            totalSheet.addCell(new Label(tempColNo + orgUnitCount, tempRowNo, selectedOrgUnit.getName(), totalCellformat));
          }
          else if (deCodeString.equalsIgnoreCase("NA"))
          {
            totalSheet.addCell(new Label(tempColNo + orgUnitCount, tempRowNo, " ", totalCellformat));
          }
          else
          {
            totalSheet.addCell(new Formula(tempColNo + orgUnitCount, tempRowNo, tempFormula, totalCellformat));
          }
        }
      }
    }
    outputReportWorkbook.write();
    outputReportWorkbook.close();

    fileName = reportFileNameTB.replace(".xls", "");
    fileName = (fileName + "_" + selectedOrgUnit.getShortName() + "_");
    fileName = (fileName + "_" + simpleDateFormat.format(sDate) + ".xls");
    File outputReportFile = new File(outputReportPath);
    inputStream = new BufferedInputStream(new FileInputStream(outputReportFile));

    System.out.println(selectedOrgUnit.getName() + " : " + selReportObj.getName() + " Report Generation End Time is : " + new Date());

    outputReportFile.deleteOnExit();

    return "success";
  }

  public WritableCellFormat getCellFormat1() throws Exception
  {
    WritableFont arialBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
    WritableCellFormat wCellformat = new WritableCellFormat(arialBold);

    wCellformat.setBorder(Border.ALL, BorderLineStyle.THIN);
    wCellformat.setAlignment(Alignment.CENTRE);
    wCellformat.setBackground(Colour.GRAY_25);
    wCellformat.setVerticalAlignment(VerticalAlignment.CENTRE);
    wCellformat.setWrap(true);

    return wCellformat;
  }

  public WritableCellFormat getCellFormat2() throws Exception {
    WritableFont arialBold = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD);
    WritableCellFormat wCellformat = new WritableCellFormat(arialBold);

    wCellformat.setBorder(Border.ALL, BorderLineStyle.THIN);
    wCellformat.setAlignment(Alignment.CENTRE);
    wCellformat.setBackground(Colour.ICE_BLUE);
    wCellformat.setVerticalAlignment(VerticalAlignment.CENTRE);
    wCellformat.setWrap(true);

    return wCellformat;
  }

  private String getAggVal(String expression, Map<String, String> aggDeMap)
  {
    try
    {
      Pattern pattern = Pattern.compile("(\\[\\d+\\.\\d+\\])");

      Matcher matcher = pattern.matcher(expression);
      StringBuffer buffer = new StringBuffer();

      String resultValue = "";

      while (matcher.find())
      {
        String replaceString = matcher.group();

        replaceString = replaceString.replaceAll("[\\[\\]]", "");

        replaceString = (String)aggDeMap.get(replaceString);

        if (replaceString == null)
        {
          replaceString = "0";
        }

        matcher.appendReplacement(buffer, replaceString);

        resultValue = replaceString;
      }

      matcher.appendTail(buffer);

      double d = 0.0D;
      try
      {
        d = MathUtils.calculateExpression(buffer.toString());
      }
      catch (Exception e)
      {
        d = 0.0D;
        resultValue = "";
      }

      return "" + d;
    }
    catch (NumberFormatException ex)
    {
      throw new RuntimeException("Illegal DataElement id", ex);
    }
  }

  public List<OrganisationUnit> getChildOrgUnitTree(OrganisationUnit orgUnit)
  {
    List orgUnitTree = new ArrayList();
    orgUnitTree.add(orgUnit);

    List children = new ArrayList(orgUnit.getChildren());
    Collections.sort(children, new IdentifiableObjectNameComparator());

    Iterator childIterator = children.iterator();

    while (childIterator.hasNext())
    {
      OrganisationUnit child = (OrganisationUnit)childIterator.next();
      orgUnitTree.addAll(getChildOrgUnitTree(child));
    }
    return orgUnitTree;
  }
}