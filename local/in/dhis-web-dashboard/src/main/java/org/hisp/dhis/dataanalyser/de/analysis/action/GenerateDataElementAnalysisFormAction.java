package org.hisp.dhis.dataanalyser.de.analysis.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.reports.ReportOption;
import org.hisp.dhis.reports.ReportService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateDataElementAnalysisFormAction.javaSep 5, 2012 12:00:16 PM	
 */

public class GenerateDataElementAnalysisFormAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private List<DataElement> dataElementList;
    
    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }
    
    private List<OrganisationUnitGroupSet> organisationUnitGroupSetList;
    
    public List<OrganisationUnitGroupSet> getOrganisationUnitGroupSetList()
    {
        return organisationUnitGroupSetList;
    }
    
    private List<ReportOption> reportOptionList;
    
    public List<ReportOption> getReportOptionList()
    {
        return reportOptionList;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        // Get Dataelement list
        reportOptionList = new ArrayList<ReportOption>( getReportOptions() );
        
        // DataElement List
        dataElementList = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        
        Iterator<DataElement> alldeIterator = dataElementList.iterator();
        while ( alldeIterator.hasNext() )
        {
            DataElement dataElement = alldeIterator.next();
            if ( !dataElement.getDomainType().equalsIgnoreCase( DataElement.DOMAIN_TYPE_AGGREGATE ) )
            {
                alldeIterator.remove();
            }
        }
        Collections.sort( dataElementList, IdentifiableObjectNameComparator.INSTANCE );
        
        // OrgunitGroupSet List        
        organisationUnitGroupSetList = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getAllOrganisationUnitGroupSets() );
        Collections.sort( organisationUnitGroupSetList, IdentifiableObjectNameComparator.INSTANCE );
        
        return SUCCESS;
    }
    
    
    private List<ReportOption> getReportOptions( )
    {
        String raFolderName = reportService.getRAFolderName();
        
        List<ReportOption> reportOptionList = new ArrayList<ReportOption>();
        
        String newpath = "";
        try
        {
            newpath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "ExpressionListForOULevelReport.xml";
        }
        catch ( NullPointerException npe )
        {
            System.out.println("DHIS_HOME is not set");
            return null;
        }
        
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( newpath ) );
            if ( doc == null )
            {
                System.out.println( "There is no MAP XML file in the DHIS2 Home" );
                return null;
            }

            NodeList listOfOption = doc.getElementsByTagName( "option" );
            int totalOptions = listOfOption.getLength();

            for( int s = 0; s < totalOptions; s++ )
            {
                Element element = (Element) listOfOption.item( s );
                String optiontext = element.getAttribute( "optiontext" );
                String optionvalue = element.getAttribute( "optionvalue" );

                optionvalue += "@@@" + optiontext;
                if( optiontext != null && optionvalue != null )
                {
                    ReportOption reportOption = new ReportOption( optiontext, optionvalue );
                    reportOptionList.add( reportOption );
                }
            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
            return null;
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
            return null;
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
            return null;
        }
        
        return reportOptionList;
    }

    
}
