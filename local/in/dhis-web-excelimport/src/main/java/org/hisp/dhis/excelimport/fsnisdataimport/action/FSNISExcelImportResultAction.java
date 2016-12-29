package org.hisp.dhis.excelimport.fsnisdataimport.action;

import com.opensymphony.xwork2.Action;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.excelimport.util.ExcelImport_OUDeCode;
import org.hisp.dhis.excelimport.util.ReportService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FSNISExcelImportResultAction implements Action {


    private Matcher matcher;

    private static final String DATE_PATTERN_SLASH_DDMMYYYY =
            "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";

    private static final String DATE_PATTERN_SLASH_DDMMYY =
            "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d\\d)";

    private static final String DATE_PATTERN_DOT_DDMMYYYY =
            "(0?[1-9]|[12][0-9]|3[01]).(0?[1-9]|1[012]).((19|20)\\d\\d)";

    private boolean dateValidation = true;

    private boolean reportValidation = true;

    private String expectedDateFormat;

    private String reportedFormat;

    //-------------------------------------------------------------------------
    // Dependencies
    //-------------------------------------------------------------------------

    private ReportService reportService;

    private static final String DOWNLOAD = "download";

    private FileInputStream fileInputStream;

    public FileInputStream getFileInputStream() {
        return fileInputStream;
    }

    public void setFileInputStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService(PeriodService periodService) {
        this.periodService = periodService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService(OrganisationUnitGroupService organisationUnitGroupService) {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    private DataSetService dataSetService;

    public void setDataSetService(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }

    private DatabaseInfoProvider databaseInfoProvider;

    public void setDatabaseInfoProvider(DatabaseInfoProvider databaseInfoProvider) {
        this.databaseInfoProvider = databaseInfoProvider;
    }

    private I18nFormat format;

    public void setFormat(I18nFormat format) {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private String checkTemplateName;

    public void setCheckTemplateName(String checkTemplateName) {
        this.checkTemplateName = checkTemplateName;
    }

    public String getCheckTemplateName() {
        return checkTemplateName;
    }

    private String downloadTemplateName;

    public void setDownloadTemplateName(String downloadTemplateName) {
        this.downloadTemplateName = downloadTemplateName;
    }

    private String checkRangeForHeader;

    public void setCheckRangeForHeader(String checkRangeForHeader) {
        this.checkRangeForHeader = checkRangeForHeader;
    }

    private String checkRangeForData;

    public void setCheckRangeForData(String checkRangeForData) {
        this.checkRangeForData = checkRangeForData;
    }

    private Integer sheetNo;

    public void setSheetNo(Integer sheetNo) {
        this.sheetNo = sheetNo;
    }

    private String importSheetId;

    public void setImportSheetId(String importSheetId) {
        this.importSheetId = importSheetId;
    }

    public String getImportSheetId() {
        return importSheetId;
    }

    private String message = "";

    public String getMessage() {
        return message;
    }

    private boolean showDownloadButton = false;

    public boolean isShowDownloadButton() {
        return showDownloadButton;
    }

    private File output;

    public File getOutput() {
        return output;
    }

    private File upload;

    public File getUpload() {
        return upload;
    }

    public void setUpload(File upload) {
        this.upload = upload;
    }

    private String raFolderName;

    private boolean lockStatus;

    public boolean isLockStatus() {
        return lockStatus;
    }

    String selectedPeriodicity;

    public void setSelectedPeriodicity(String selectedPeriodicity) {
        this.selectedPeriodicity = selectedPeriodicity;
    }

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------


    public String download() throws Exception {

        filename = downloadTemplateName.replace("/", "");

        raFolderName = reportService.getRAFolderName();

        String excelTemplatePath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator
                + "excelimport" + File.separator + "template" + File.separator + filename;

        System.out.println("* INFO: Downloaded File Name is " + excelTemplatePath);

        try {
            fileInputStream = new FileInputStream(new File(excelTemplatePath));
        } catch (FileNotFoundException ex) {
            System.out.println(this.getClass().getSimpleName() + ": File in " + excelTemplatePath + " cannot be found.");
            return ERROR;
        }

        return DOWNLOAD;
    }


    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    //-------------------------------------------------------------------------
    // Date String Validation Method
    //-------------------------------------------------------------------------


    public String extractDate(String dateString, String datePattern)
    {
        Pattern pattern = Pattern.compile(datePattern);
        Matcher dateMatcher = pattern.matcher(dateString);

        String extractedDateString = new String();

        while (dateMatcher.find())
        {
            extractedDateString = dateMatcher.group();
        }

        return extractedDateString;
    }


    private void importPortalData(WritableWorkbook importWorkbook) throws Exception {

        List<ExcelImport_OUDeCode> excelImport_ouDeCodeList = new ArrayList<ExcelImport_OUDeCode>();

        final String excelImportFolderName = "excelimport";

        String path = System.getProperty("user.home") + File.separator + "dhis" + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + importSheetId;

        try {
            String newPath = System.getenv("DHIS2_HOME");

            if (newPath != null) {
                path = newPath + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + importSheetId;
            }
        } catch (NullPointerException npe) {
            System.out.println("DHIS2_HOME is not set");
        }

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(path));

            if (doc == null) {
                System.out.println("There is no DECodes related XML file in the DHIS2 Home");
            }

            NodeList periodCells = doc.getElementsByTagName("period-info");

            Element periodCell = (Element) periodCells.item(0);

            Integer periodSheetNo = Integer.parseInt(periodCell.getAttribute("sheetno"));
            Integer periodRowNo = Integer.parseInt(periodCell.getAttribute("rowno"));
            Integer periodColNo = Integer.parseInt(periodCell.getAttribute("colno"));
            String periodFormat = periodCell.getAttribute("format");

            System.out.println("* CHECK SHEET No :" + periodSheetNo);

            Sheet importFileSheet = importWorkbook.getSheet(periodSheetNo);

            String cellContent = importFileSheet.getCell(periodColNo, periodRowNo).getContents().trim();

            cellContent = cellContent.trim();

            System.out.println("* DATE FORMAT: " + cellContent);

            Period selectedPeriod = null;

            if (periodFormat.equalsIgnoreCase("OFFTAKE"))
            {

                String sDateString;

                expectedDateFormat = new String("DD/MM/YY");

                sDateString = extractDate(cellContent,DATE_PATTERN_SLASH_DDMMYYYY);

                if(sDateString == null || sDateString.isEmpty())
                {
                    dateValidation = false;
                    return;
                }
                else
                {
                    System.out.println("* INFO: Date Check [" + sDateString + "]");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    Date convertedStartDate = dateFormat.parse(sDateString);

                    System.out.println("* INFO: Date Cast Check [" + convertedStartDate + "]");

                    PeriodType periodType = periodService.getPeriodTypeByName(selectedPeriodicity);

                    selectedPeriod = getSelectedPeriod(convertedStartDate, periodType);
                }

            }
            else if (periodFormat.equalsIgnoreCase("DAILY-REPORT"))
            {

                String sDateString;

                expectedDateFormat = new String("DD.MM.YYYY");

                sDateString = extractDate(cellContent,DATE_PATTERN_DOT_DDMMYYYY);

                if(sDateString == null || sDateString.isEmpty())
                {
                    dateValidation = false;
                    return;
                }
                else
                {
                    System.out.println("* INFO: Date Check [" + sDateString + "]");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

                    Date convertedStartDate = dateFormat.parse(sDateString);

                    System.out.println("* INFO: Date Cast Check [" + convertedStartDate + "]");

                    PeriodType periodType = periodService.getPeriodTypeByName(selectedPeriodicity);

                    selectedPeriod = getSelectedPeriod(convertedStartDate, periodType);
                }

            }
            else if (periodFormat.equalsIgnoreCase("DHAKA-WRP"))
            {

                String sDateString;

                expectedDateFormat = new String("DD/MM/YYYY");

                sDateString = extractDate(cellContent,DATE_PATTERN_SLASH_DDMMYYYY);

                if(sDateString == null || sDateString.isEmpty())
                {
                    dateValidation = false;
                    return;
                }
                else
                {
                    System.out.println("* INFO: Date Check ["+sDateString+"]");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                    Date convertedStartDate = dateFormat.parse(sDateString);

                    System.out.println("* INFO: Date Cast Check ["+convertedStartDate+"]");

                    PeriodType periodType = periodService.getPeriodTypeByName(selectedPeriodicity);

                    selectedPeriod = getSelectedPeriod(convertedStartDate, periodType);
                }

            }

        }
        catch (SAXParseException err)
        {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        }
        catch (SAXException e)
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }

    }


    public boolean validateReport(Workbook excelImportFile, Workbook excelTemplateFile) {

        boolean validator = true;

        int errorCount = 0;

        int sheetNumber = sheetNo;

        String headerParts[] = checkRangeForHeader.split("-");
        int headerStartRow = Integer.parseInt(headerParts[0].split(",")[0]);
        int headerEndRow = Integer.parseInt(headerParts[1].split(",")[0]);

        int headerStartCol = Integer.parseInt(headerParts[0].split(",")[1]);
        int headerEndCol = Integer.parseInt(headerParts[1].split(",")[1]);

        String dataParts[] = checkRangeForData.split("-");
        int dataStartRow = Integer.parseInt(dataParts[0].split(",")[0]);
        int dataEndRow = Integer.parseInt(dataParts[1].split(",")[0]);

        int dataStartCol = Integer.parseInt(dataParts[0].split(",")[1]);
        int dataEndCol = Integer.parseInt(dataParts[1].split(",")[1]);

        Sheet importFileSheet = excelImportFile.getSheet(sheetNumber);
        Sheet templateFileSheet = excelTemplateFile.getSheet(sheetNumber);

        //if (excelImportFile.getSheet(sheetNumber).getRows() == excelTemplateFile.getSheet(sheetNumber).getRows()) {

        //-------------------------------- Checking Header Cells ---------------------------------------------
        message += "<ul>";

        for (int c = headerStartCol; c <= headerEndCol; c++) {
            for (int r = headerStartRow; r <= headerEndRow; r++) {

                String cellContent = importFileSheet.getCell(c, r).getContents();
                String templateContent = templateFileSheet.getCell(c, r).getContents();

                if (templateContent.equalsIgnoreCase(cellContent) && cellContent.equalsIgnoreCase(templateContent)) {
                    continue;
                }
                else
                {
                    message += "<li><font color=red>VALIDATION ERROR[Cell Content Mismatch at(" + (c + 1) + "," + (r + 1) + ")]" + "<br/>" + "<font color=black>Template Content: " + templateContent + "<br/>" + "Imported File's Content: " + cellContent + "</font></font><br/></li>";
                    validator = false;
                    ++errorCount;
                    if (errorCount > 4) {
                        message += "</br>and more.....";
                        showDownloadButton = true;
                        break;
                    }
                }
            }
        }

        //--------------------------------- Checking Data Cells ----------------------------------------------

        for (int c = dataStartCol; c <= dataEndCol; c++) {
            for (int r = dataStartRow; r <= dataEndRow; r++) {
                String cellContent = importFileSheet.getCell(c, r).getContents();
                String templateContent = templateFileSheet.getCell(c, r).getContents();

                if (templateContent.equalsIgnoreCase(cellContent) && cellContent.equalsIgnoreCase(templateContent)) {
                    continue;
                } else {
                    message += "<li><font color=red>VALIDATION ERROR[Cell Content Mismatch at(" + (c + 1) + "," + (r + 1) + ")]" + "<br/>" + "<font color=black>Template Content: " + templateContent + "<br/>" + "Imported File's Content: " + cellContent + "</font></font><br/></li>";

                    validator = false;
                    ++errorCount;
                    if (errorCount > 4) {
                        message += "</br>and more.....";
                        showDownloadButton = true;
                        break;
                    }
                }
            }
        }

        message += "</ul>";

        //}

        /*
        else
        {
            System.out.println("* FAILURE :Validation failed due to unequal count of Rows.");
            validator = false;
        }
        */

        return validator;
    }


    public Period getSelectedPeriod(Date startDate, PeriodType periodType) throws Exception {


        List<Period> periods = new ArrayList<Period>(periodService.getPeriodsByPeriodType(periodType));
        for (Period period : periods) {
            Date tempDate = period.getStartDate();
            if (tempDate.equals(startDate)) {
                return period;
            }
        }

        Period period = periodType.createPeriod(startDate);
        period = reloadPeriodForceAdd(period);


        return period;
    }

    private final Period reloadPeriod(Period period) {
        return periodService.getPeriod(period.getStartDate(), period.getEndDate(), period.getPeriodType());
    }

    private final Period reloadPeriodForceAdd(Period period) {
        Period storedPeriod = reloadPeriod(period);

        if (storedPeriod == null) {

            periodService.addPeriod(period);
            return period;
        }

        return storedPeriod;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception {

        message += "\n<br><font color=blue>Importing StartTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font>";

        System.out.println(message);

        raFolderName = reportService.getRAFolderName();

        System.out.println("\n==========================================TESTING=============================================");

        System.out.println("TemplateName [" + checkTemplateName + "]");


        String excelTemplatePath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator
                + "excelimport" + File.separator + "template" + File.separator + checkTemplateName;

        String outputReportPath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";

        Workbook excelImportFile = Workbook.getWorkbook(upload);

        WritableWorkbook writableExcelImportFile = Workbook.createWorkbook(new File(outputReportPath), excelImportFile);

        Workbook excelTemplateFile = Workbook.getWorkbook(new File(excelTemplatePath));

        if (validateReport(excelImportFile, excelTemplateFile))
        {
            System.out.println("Uploaded ExcelSheet is matched with Template file.");
            importPortalData(writableExcelImportFile);
            if(dateValidation == false)
            {
                message += "<li><font color=red>The DATE Format Is <b>Not Acceptable</b></font></li>" + "<li><font color=green>Expected DATE Format: <b>"+expectedDateFormat+"</b></font></li>" + "<br><font color=blue>Please Correct This And Try Again</font>";
            }
        }
        else
        {
            reportValidation = false;
            message += "<font color=red>The file you are trying to import is not the correct format</font>";
        }


        excelImportFile.close();
        excelTemplateFile.close();
        writableExcelImportFile.close();


        System.out.println("==========================================DONE-TESTING=============================================\n");

        if(reportValidation && dateValidation)
        {
            System.out.println("Importing has been completed which is started by : " + currentUserService.getCurrentUsername() + " at " + new Date());
            message += "<br><br><font color=blue>Importing EndTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font>";
        }
        return SUCCESS;
    }


}
