package org.hisp.dhis.dataanalyser.de.analysis.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.system.util.MathUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateDataElementAnalysisResultAction.javaSep 5, 2012 3:22:14 PM	
 */

public class GenerateDataElementAnalysisResultAction implements Action
{
    private final String DATAELEMENTVALUERANGE = "DataElementValueRange";
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
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
   
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private List<String> selectedDataElements = new ArrayList<String>();
    
    public void setSelectedDataElements( List<String> selectedDataElements )
    {
        this.selectedDataElements = selectedDataElements;
    }
    
    private Integer orgUnitGroupSet;
    
    public void setOrgUnitGroupSet( Integer orgUnitGroupSet )
    {
        this.orgUnitGroupSet = orgUnitGroupSet;
    }
    
    private String startDate;
    
    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }
    
    private String endDate;
    
    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }
    
    private int ouIDTB;
    
    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    
    private String dataElementIdsByComma;
    
    private String periodIdsByComma;
    
    private String orgUnitIdsByComma;
    
    private Date sDate;

    private Date eDate;
    
    private OrganisationUnit selectedOrgUnit;
    
    private List<DataElement> dataElementList;
    
    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }

    private List<String> dataElementNameList;
    
    public List<String> getDataElementNameList()
    {
        return dataElementNameList;
    }

    private List<String> optionNameList;
    
    public List<String> getOptionNameList()
    {
        return optionNameList;
    }

    private List<OrganisationUnit> orgUnitList;
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private Map<Integer, String> orgunitHierarchyMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getOrgunitHierarchyMap()
    {
        return orgunitHierarchyMap;
    }
    
    private Map<OrganisationUnit, List<Integer>> ouMapDataElementValue;
    
    public Map<OrganisationUnit, List<Integer>> getOuMapDataElementValue()
    {
        return ouMapDataElementValue;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        /* Initialization */
        dataElementList = new ArrayList<DataElement>();
        dataElementNameList = new ArrayList<String>();
        optionNameList = new ArrayList<String>();
        orgUnitList = new ArrayList<OrganisationUnit>();
        ouMapDataElementValue = new HashMap<OrganisationUnit, List<Integer>>();
        
        // Period Info
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );
        
        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );        
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );        
        periodIdsByComma = getCommaDelimitedString( periodIds );
        
        //System.out.println( "-- period Ids ByComma  = "  + periodIdsByComma );
        
        // Data elements Info
        
/*        dataElementIdsByComma = "-1";
        Iterator<String> deIterator = selectedDataElements.iterator();
        while ( deIterator.hasNext() )
        {
            int tempDeID = Integer.parseInt( (String) deIterator.next() );
            DataElement dataElement = dataElementService.getDataElement( tempDeID );
            dataElementIdsByComma += ","+dataElement.getId();
            dataElementList.add( dataElement );
        }
*/      
        dataElementIdsByComma = getDataelementIdsFromSelectedList();
        
        OptionSet optionSet = optionService.getOptionSetByName( DATAELEMENTVALUERANGE );
        for( String optionName : optionSet.getOptions() )
        {
            optionNameList.add( optionName );
        }
        
        /*
        for( String option : optionNameList )
        {
            System.out.println( "-- Opetion Name is   = "  + option );
            
            if( option.contains( "<" ))
            {
                String[] tempFirst = option.split( "<" );
                System.out.println( tempFirst[0] +  " -- "  + tempFirst[1] );
                String first = tempFirst[0];
                String second = tempFirst[1];
                
            }
            if( option.contains( "-" ))
            {
                String[] tempFirst = option.split( "-" );
                System.out.println( tempFirst[0] +  " -- "  + tempFirst[1] );
                String first = tempFirst[0];
                String second = tempFirst[1];
                
            }
            if( option.contains( ">" ))
            {
                String[] tempFirst = option.split( ">" );
                System.out.println( tempFirst[0] +  " -- "  + tempFirst[1] );
                String first = tempFirst[0];
                String second = tempFirst[1];
                
            }            
        }
        */
        
        // OrgUnit Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        System.out.println( selectedOrgUnit.getName()+ " : Report Generation Start Time is : " + new Date() );
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( ouIDTB ) );
        if ( orgUnitGroupSet != 0 )
        {
            OrganisationUnitGroupSet organisationUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( orgUnitGroupSet );
            if ( organisationUnitGroupSet != null )
            {
                orgUnitList.retainAll( organisationUnitGroupSet.getOrganisationUnits() );
            }
        }
        orgUnitIdsByComma = "-1";
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            orgunitHierarchyMap.put( orgUnit.getId(), getHierarchyOrgunit( orgUnit ) );
            orgUnitIdsByComma += "," + orgUnit.getId();
        }
        
        System.out.println( orgUnitIdsByComma );
        System.out.println( dataElementIdsByComma );
        System.out.println( periodIdsByComma );
        System.out.println( selectedDataElements.size() );
        
        Map<String, String> aggDataMap = new HashMap<String, String>( reportService.getDataFromDataValueTableByPeriodAgg( orgUnitIdsByComma, dataElementIdsByComma, periodIdsByComma ) );
        
        /* Calculation Part */
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            List<Integer> deCounts = new ArrayList<Integer>();
            for( String selDeExp : selectedDataElements )
            {
                String[] selDeExpParts = selDeExp.split( "@@@" );
                String formula = selDeExpParts[0];
                String tempStr = getAggVal( formula, orgUnit.getId(), aggDataMap );
                
                for( String option : optionNameList )
                {
                    if( option.trim().contains( "<" ) )
                    {
                        String[] tempFirst = option.trim().split( "<" );                                
                        if ( tempStr != null && Double.parseDouble( tempStr ) < Double.parseDouble ( tempFirst[1].trim() ) )
                        {
                            deCounts.add( 1 );
                        }
                        else
                        {
                            deCounts.add( 0 );
                        }
                    }
                    else if( option.trim().contains( "to" ))
                    {
                        String[] tempFirst = option.trim().split( "to" );
                        if ( tempStr != null && ( Double.parseDouble( tempStr ) >= Double.parseDouble ( tempFirst[0].trim() )) &&  ( Double.parseDouble( tempStr ) <= Double.parseDouble ( tempFirst[1].trim() ) ) )
                        {
                            deCounts.add( 1 );
                        }
                        else
                        {
                            deCounts.add( 0 );
                        }                                
                    }
                    else if( option.trim().contains( ">" ))
                    {
                        String[] tempFirst = option.trim().split( ">" );
                        if ( tempStr != null && Double.parseDouble( tempStr ) > Double.parseDouble (tempFirst[1].trim()) )
                        {
                            deCounts.add( 1 );
                        }
                        else
                        {
                            deCounts.add( 0 );
                        }
                    }
                    else
                    {
                        deCounts.add( -1 );
                    }
                }
            }
            ouMapDataElementValue.put( orgUnit, deCounts );
        }
        
        System.out.println( selectedOrgUnit.getName()+ " : Report Generation End Time is : " + new Date() );
        
        return SUCCESS;
    }
    
    //--------------------------------------------------------------------------
    // supportive methods
    //--------------------------------------------------------------------------
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = "";
        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + "/" + hierarchyOrgunit;
            orgunit = orgunit.getParent();
        }
        return hierarchyOrgunit;
    }
    
    public String  getDataelementIdsFromSelectedList( )
    {
        String dataElmentIdsByComma = "-1";
        
        try
        {
            for( String selDeExp : selectedDataElements )
            {
                System.out.println( selDeExp );
                String[] selDeExpParts = selDeExp.split( "@@@" );
                String formula = selDeExpParts[0];
                dataElementNameList.add( selDeExpParts[1] );
                
                try
                {
                    Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

                    Matcher matcher = pattern.matcher( formula );
                    StringBuffer buffer = new StringBuffer();

                    while ( matcher.find() )
                    {
                        String replaceString = matcher.group();

                        replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                        replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                        int dataElementId = Integer.parseInt( replaceString );
                        dataElmentIdsByComma += "," + dataElementId;
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                    }
                }
                catch( Exception e )
                {
                    e.printStackTrace();
                }
                
            }// end of for loop
        }// try block end
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return dataElmentIdsByComma;
    }
 
    // getting data value using Map
    private String getAggVal( String expression, Integer orgUnitID, Map<String, String> aggDeMap )
    {
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString.length() );
                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );
                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );
                
                replaceString = aggDeMap.get( orgUnitID +":"+dataElementId +":"+ optionComboId );
                if( replaceString == null )
                {
                    replaceString = "0";
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
            
            double d = 0.0;
            try
            {
                d = MathUtils.calculateExpression( buffer.toString() );
            }
            catch ( Exception e )
            {
                d = 0.0;
                resultValue = "";
            }
            
            resultValue = "" + (double) d;

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
}

