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

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.excelimport.util.DynamicImportSheet;
import org.hisp.dhis.excelimport.util.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DynamicExcelImportFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
    
    private List<DynamicImportSheet> excelImportSheetList;

    public List<DynamicImportSheet> getExcelImportSheetList() {
        return excelImportSheetList;
    }

    public void setExcelImportSheetList(List<DynamicImportSheet> excelImportSheetList) {
        this.excelImportSheetList = excelImportSheetList;
    }

    private String raFolderName;


    public void getExcelImportSheetList( String reportListFileName )
    {
        String fileName = reportListFileName;

        String excelImportFolderName = "dynamicImport";

        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + raFolderName + File.separator + excelImportFolderName + File.separator + fileName;

        try
        {
            String newPath = System.getenv( "DHIS2_HOME" );
            if ( newPath != null )
            {
                path = newPath + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + fileName;
            }
        }
        catch ( NullPointerException npe )
        {
            System.out.println("DHIS2_HOME IS NOT SET");
        }

        String rScriptName;
        String displayName;
        String mappingXML;

        int count = 0;

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );

            if ( doc == null )
            {
                System.out.println( "* ERROR: XML MISSING!!" );
                return;
            }

            NodeList listOfReports = doc.getElementsByTagName( "BDImportSheet" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;

                    NodeList nodeList = reportElement.getElementsByTagName( "rScriptName" );
                    Element element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    rScriptName = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "displayName" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    displayName = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    nodeList = reportElement.getElementsByTagName( "mappingXML" );
                    element = (Element) nodeList.item( 0 );
                    nodeList = element.getChildNodes();
                    mappingXML = ((Node) nodeList.item( 0 )).getNodeValue().trim();

                    DynamicImportSheet bdImportSheet = new DynamicImportSheet(rScriptName, displayName, mappingXML);

                    excelImportSheetList.add(count,bdImportSheet);

                    count++;
                }
            }
        }
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
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        raFolderName = reportService.getRAFolderName();
        
        excelImportSheetList = new ArrayList<DynamicImportSheet>();
        
        getExcelImportSheetList( "DynamicDataImport.xml" );
        
        return SUCCESS;
    }

}
