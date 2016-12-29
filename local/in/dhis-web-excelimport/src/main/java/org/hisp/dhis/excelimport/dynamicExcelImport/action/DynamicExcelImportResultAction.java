/*
 * Copyright (c) 2004-2012, University of Oslo
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
package org.hisp.dhis.excelimport.dynamicExcelImport.action;

import au.com.bytecode.opencsv.CSVReader;
import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.excelimport.util.ReportService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicExcelImportResultAction implements Action {


    private Date importDate;

    private final String osTempDirProp = new String("java.io.tmpdir");

    private final String tmpDir = System.getProperty(osTempDirProp);

    private static final String DATE_PATTERN_DDMMYYYY =
            "(0?[1-9]|[12][0-9]|3[01])(0?[1-9]|1[012])((19|20)\\d\\d)";

    private boolean dateValidation = true;

    private boolean reportValidation = true;


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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
        this.organisationUnitService = organisationUnitService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    private DataElementService dataElementService;

    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(DataElementCategoryService dataElementCategoryOptionComboService) {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private DataValueService dataValueService;

    public void setDataValueService(DataValueService dataValueService) {
        this.dataValueService = dataValueService;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private String checkRscriptName;

    public String getCheckMappingXML() {
        return checkMappingXML;
    }

    public String getCheckRscriptName() {
        return checkRscriptName;
    }

    private String checkMappingXML;

    public void setCheckMappingXML(String checkMappingXML) {
        this.checkMappingXML = checkMappingXML;
    }

    public void setCheckRscriptName(String checkRscriptName) {
        this.checkRscriptName = checkRscriptName;
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

    public String extractDate(String dateString, String datePattern)
    {
        Pattern pattern = Pattern.compile(datePattern);
        Matcher dateMatcher = pattern.matcher(dateString);

        String extractedDateString = new String();

        while (dateMatcher.find())
        {
            extractedDateString = dateMatcher.group();

            if(extractedDateString==null)
            {
                dateValidation = false;
            }
        }

        return extractedDateString;
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public Period getSelectedPeriod(Date startDate, PeriodType periodType) throws Exception
    {
        List<Period> periods = new ArrayList<Period>(periodService.getPeriodsByPeriodType(periodType));
        for (Period period : periods)
        {
            Date tempDate = period.getStartDate();
            if (tempDate.equals(startDate))
            {
                return period;
            }
        }

        Period period = periodType.createPeriod(startDate);
        period = reloadPeriodForceAdd(period);
        return period;
    }

    private final Period reloadPeriod(Period period)
    {
        return periodService.getPeriod(period.getStartDate(), period.getEndDate(), period.getPeriodType());
    }

    private final Period reloadPeriodForceAdd(Period period)
    {
        Period storedPeriod = reloadPeriod(period);

        if (storedPeriod == null)
        {

            periodService.addPeriod(period);
            return period;
        }

        return storedPeriod;
    }


    //-------------------------------------------------------------------------
    // Dynamic Excel-Import Implementation
    //-------------------------------------------------------------------------


    private Map<String, String> infoMap = new HashMap<String, String>();

    private Integer seqSize;


    @SuppressWarnings("ConstantConditions")
    public void readXMLMap() {

        raFolderName = reportService.getRAFolderName();

        final String xmlMapFileName = checkMappingXML;

        String xmlMapPath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator
                + "dynamicImport" + File.separator + xmlMapFileName;

        File xmlMappingFile = new File(xmlMapPath);

        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlMappingFile);
            doc.getDocumentElement().normalize();

            NodeList sheetInfoNodes = doc.getElementsByTagName("sheet-info");
            Integer numOfSheetInfoNodes = sheetInfoNodes.getLength();


            for (int num = 0; num < numOfSheetInfoNodes; ++num)
            {
                Element element = (Element) sheetInfoNodes.item(num);

                if (element.getAttribute("type").equalsIgnoreCase("seq_len"))
                {
                    seqSize = Integer.parseInt(element.getAttribute("len"));
                    System.out.println("* INFO: SEQUENCE LENGTH(" + seqSize + ")");
                }
                if (element.getAttribute("type").equalsIgnoreCase("date_cell"))
                {
                    Integer row = Integer.parseInt(element.getAttribute("row"));
                    Integer col = Integer.parseInt(element.getAttribute("col"));
                    System.out.println("* INFO: DATE(" + row + "," + col + ")");
                }
            }


            NodeList mapNodes = doc.getElementsByTagName("dMap");
            numOfSheetInfoNodes = mapNodes.getLength();

            for (int num = 0; num < numOfSheetInfoNodes; ++num)
            {
                Element element = (Element) mapNodes.item(num);

                String excelTag = element.getAttribute("exlTag");
                String sequenceNum = element.getAttribute("seq_num");
                String ouCode = element.getAttribute("ouCode");
                String deID = element.getAttribute("deID");

                excelTag = excelTag.trim();

                System.out.println("* CHECK: [" + excelTag + "]");

                String keyString = excelTag + ":" + sequenceNum;
                String valueString = ouCode + "," + deID;

                infoMap.put(keyString, valueString);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void executeScript(String excelInputPath)
    {
        final String scriptOutputFolderPath = System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator
                + "dynamicImport" + File.separator + "dhisCSVOut";

        File scriptOutputDir = new File(scriptOutputFolderPath);

        if(!scriptOutputDir.exists())
        {
            scriptOutputDir.mkdir();
        }

        final String pathToScript =  System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator
                + "dynamicImport" + File.separator + "scripts" + File.separator + checkRscriptName;

        String rScript = "Rscript"+ " " +pathToScript + " "+ excelInputPath + " " + scriptOutputFolderPath;

        System.out.println("rScript : " + rScript);

        try
        {
            Runtime rt = Runtime.getRuntime();
            Process pr;
            pr = rt.exec(rScript);
            int exitVal = pr.waitFor();

            System.out.println("Exit Val : " + exitVal + " -- " + rScript);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //----------------------Check for CSV output file-------------------//

        File[] csvFiles = scriptOutputDir.listFiles(
                new FilenameFilter()
                {
                    public boolean accept(File dir, String name)
                    {
                        return name.endsWith(".csv");
                    }
                });

        if(csvFiles.length<1)
        {
            System.out.println("* WARNING: Something went WRONG, no CSV file in the script output folder.");
        }

        for (File csvFile : csvFiles)
        {
            System.out.println(csvFile.toString());

            String fileName = csvFile.getName();

            System.out.println("* CHECK(FILENAME):"+fileName);

            String dateContent = extractDate(fileName,DATE_PATTERN_DDMMYYYY);

            System.out.println("* CHECK(DATE-CONTENT):"+dateContent);

            if(dateContent==null || dateContent.isEmpty() || dateContent.length()!=8)
            {
                dateValidation = false;
                return;
            }

            try
            {
                importDate = new SimpleDateFormat("ddMMyyyy", Locale.ENGLISH).parse(dateContent);
                System.out.println("* CHECK(DATE): "+importDate);

            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }

            readCompiledCSV(csvFile);

            csvFile.delete();

        }
    }


    public void readCompiledCSV(File newOutputFile)
    {

        ArrayList<String[]> csvDataList = new ArrayList<String[]>();

        try
        {
            CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(newOutputFile)));
            String[] nextLine;

            while ((nextLine = csvReader.readNext()) != null)
            {
                String[] dataStream = new String[seqSize];
                int i = 0;
                while (i < nextLine.length && i < dataStream.length)
                {
                    dataStream[i] = nextLine[i];
                    ++i;
                }
                csvDataList.add(dataStream);
            }

            String labelTag;

            for (String[] dataEntryStream : csvDataList)
            {
                if (dataEntryStream.length >= seqSize)
                {
                    labelTag = dataEntryStream[1].trim();
                    Integer start = 2, end = seqSize - 2;

                    for (int itemSeq = start; itemSeq < end; ++itemSeq)
                    {
                        String value = dataEntryStream[itemSeq];

                        if (isNumeric(dataEntryStream[itemSeq]))
                        {
                            System.out.println("* CHECK(isNum):"+value);

                            String mapKeyString = labelTag.concat(":").concat(String.valueOf(itemSeq+1));

                            if (infoMap.get(mapKeyString) != null)
                            {
                                System.out.println("* CHECK(isMapped):"+value);

                                String[] mapValueTokens = (infoMap.get(mapKeyString)).split(",");

                                if (mapValueTokens.length == 2)
                                {
                                    System.out.println("* CHECK(isCorrFormat):"+value);

                                    Integer ouCode = Integer.parseInt(mapValueTokens[0]);
                                    String deCode = mapValueTokens[1];

                                    Integer dataelementID;
                                    Integer optionComboID;

                                    dataelementID = Integer.parseInt(deCode.substring(0,deCode.length()-2));
                                    optionComboID = Integer.parseInt(deCode.substring(deCode.length()-1));
                                    Period newPeriod;

                                    PeriodType periodType = new DailyPeriodType();

                                    newPeriod = periodType.createPeriod(importDate);
                                    newPeriod = reloadPeriodForceAdd(newPeriod);

                                    OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(ouCode);

                                    DataElement dataElement = dataElementService.getDataElement(dataelementID);

                                    DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo(optionComboID);

                                    DataValue dvCheck = dataValueService.getDataValue(organisationUnit, dataElement, newPeriod, dataElementCategoryOptionCombo);

                                    if ((dvCheck == null) && (newPeriod != null))
                                    {
                                        System.out.println("* MATCH CHECK(N): DN( " + dataElement.getName() + " )  V( " + value + " )");

                                        DataValue dataValue = new DataValue(dataElement, newPeriod, organisationUnit, value, dataElementCategoryOptionCombo);

                                        dataValueService.addDataValue(dataValue);
                                    }

                                    else if (newPeriod != null)
                                    {
                                        System.out.println("* MATCH CHECK(U): DN( " + dataElement.getName() + " )  V( " + value + " )");

                                        dvCheck.setValue(value);
                                        dvCheck.setPeriod(newPeriod);
                                        dvCheck.setSource(organisationUnit);

                                        dataValueService.updateDataValue(dvCheck);

                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        message += "\n<br><font color=blue>Importing StartTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font>";

        System.out.println(message);

        raFolderName = reportService.getRAFolderName();

        System.out.println("\n==========================================TESTING=============================================");

        System.out.println("* NOTE(ScriptName) [" + checkRscriptName + "]");

        System.out.println("* NOTE(XMLMapName) [" + checkMappingXML + "]");

        String randFileName = UUID.randomUUID().toString() + ".xls";

        File uploadedExcel = new File( System.getenv("DHIS2_HOME") + File.separator + raFolderName + File.separator
                + "dynamicImport" + File.separator + randFileName);

        upload.renameTo(uploadedExcel);

        System.out.println("* CHECK: UPLOADED FILE PATH  >>>> " + uploadedExcel.getAbsolutePath());

        readXMLMap();

        executeScript(uploadedExcel.getAbsolutePath());

        if(uploadedExcel.exists())
        {
            uploadedExcel.delete();
        }

        System.out.println("==========================================DONE-TESTING=============================================\n");

        if (reportValidation && dateValidation)
        {
            System.out.println("Importing has been completed which is started by : " + currentUserService.getCurrentUsername() + " at " + new Date());
            message += "<br><br><font color=green>Data imported to DATE: "+importDate+"</font>";
            message += "<br><br><font color=blue>Importing EndTime : " + new Date() + "  - By " + currentUserService.getCurrentUsername() + "</font>";
        }
        else if(dateValidation == false)
        {
            message += "<br><font color=red>Import failed, please check if you are uploading the correct sheet and the date format in the sheet is correct.<br> Required date format(DD/MM/YYYY).</font>";
        }
        return SUCCESS;
    }


}
