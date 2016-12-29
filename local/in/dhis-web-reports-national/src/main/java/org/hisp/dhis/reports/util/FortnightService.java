package org.hisp.dhis.reports.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.system.util.MathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FortnightService {
	

	private static final String NULL_REPLACEMENT = "0";
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    public List<FortnightReport> getFortnightReportDesign( String designXMLFile )
    {
        String path = System.getenv( "DHIS2_HOME" )+ File.separator + "fpmureports" + File.separator + designXMLFile;

        //String path = System.getenv( "DHIS2_HOME" )+ File.separator + "fortnightreports" + File.separator + designXMLFile;
        
        List<FortnightReport> reportDesignList = new ArrayList<FortnightReport>();
        
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return null;
            }

            NodeList listOfReports = doc.getElementsByTagName( "reportcell" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;

                    String period = reportElement.getAttribute( "period" );
                    String orgunit = reportElement.getAttribute( "orgunit" );
                    String dataelement = reportElement.getAttribute( "dataelement" );
                    String name = reportElement.getAttribute( "name" );
                    String celltype = reportElement.getAttribute( "celltype" );
                    String aggtype = reportElement.getAttribute( "aggtype" );
                    String rowname = reportElement.getAttribute( "rowname" );
                    String datatype = reportElement.getAttribute( "datatype" );

                    FortnightReport dfsReport = new FortnightReport( period, orgunit, dataelement, name, celltype, aggtype, rowname, datatype );
                    reportDesignList.add( dfsReport );
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

        return reportDesignList;
    }
    public String getResultValue( String dataElementExp, String startDate, String endDate, String orgUnitId, String aggType, String cellType )
    {
        try
        {
            Pattern pattern = Pattern.compile( "\\[\\d+\\.\\d+\\]" );

            Matcher matcher = pattern.matcher( dataElementExp );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

           // System.out.println("Inside Result value upward");
            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString
                    .length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                Integer dataElementId = Integer.parseInt( replaceString );
                Integer optionComboId = Integer.parseInt( optionComboIdStr );

                String dataValue = null;
                
                //System.out.println("Inside Result value");
                if( aggType.equalsIgnoreCase( FortnightReport.AGG_TYPE_ONE_PERIOD ) )
                {
                    dataValue = getDataValueForLatestAvailabeDate( dataElementId, optionComboId, orgUnitId, startDate, endDate, cellType );
                }
               else if( aggType.equalsIgnoreCase( FortnightReport.AGG_TYPE_PERIOD_AGG ) )
                {
                    dataValue = getAggDataBetweenStartAndEndDate( dataElementId, optionComboId, orgUnitId, startDate, endDate );
                }
                else if( aggType.equalsIgnoreCase( FortnightReport.AGG_TYPE_ONE_PERIOD_MULTIPLE_ORGUNITS ) )
                {
                    dataValue = getOrgunitAggregatedDataForLatestAvailabeDate( dataElementId, optionComboId, orgUnitId, startDate, endDate );
                }
                else if( aggType.equalsIgnoreCase( FortnightReport.AGG_TYPE_MULTIPLE_PERIOD_MULTIPLE_ORGUNITS ) )
                {
                    dataValue = geAggDataForMultipleOrgUnitsMultiplePeriods( dataElementId, optionComboId, orgUnitId, startDate, endDate );
                }
               
               if ( dataValue == null )
                {
                    replaceString = NULL_REPLACEMENT;
                }
                else
                {
                    replaceString = String.valueOf( dataValue );
                }
               
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );

            if( cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_DATA ) 
                || cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_TARGETDATA ) 
                || cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_FORMULA_USE_STARTDATE ) 
                || cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_FORMULA_USE_ENDDATE ) )
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                    d = Math.round( d * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                }
                catch ( Exception e )
                {
                    d = 0.0;
                    resultValue = "";
                }
    
                resultValue = "" + d;
            }
            else
            {
                resultValue = buffer.toString();
            }
            
            if ( resultValue.equalsIgnoreCase( "" ) )
            {
                resultValue = " ";
            }
               // resultValue=dataValue; 
            return resultValue;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception in getResultValue method: ", e );
        }
    }

    public Map<Integer, Map<Integer, String>> getLinelistingDataForProgramStage( String programStageId, String orgUnitId, String reportingDate )
    {
        Map<Integer, Map<Integer, String>> linelistDataValueMap = new HashMap<Integer, Map<Integer, String>>();
        
        try
        {
            String query = "SELECT patientdatavalue.programstageinstanceid, dataelementid, value FROM patientdatavalue "+
                                " INNER JOIN programstageinstance ON patientdatavalue.programstageinstanceid = programstageinstance.programstageinstanceid " +
                                " WHERE " +
                                    " programstageinstance.executiondate = '"+ reportingDate +"' AND " + 
                                    " programstageinstance.organisationunitid = " + orgUnitId + " AND " + 
                                    " programstageinstance.programstageid = "+programStageId;

            //System.out.println("QUERY : - " + query); 
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer programStageInstanceId = rs.getInt( 1 );
                Integer dataElementId = rs.getInt( 2 );
                String value = rs.getString( 3 );
                
                Map<Integer, String> tempMap = linelistDataValueMap.get( programStageInstanceId );
                if( tempMap == null )
                {
                    tempMap = new HashMap<Integer, String>();
                }
                tempMap.put( dataElementId, value );
                
                linelistDataValueMap.put( programStageInstanceId, tempMap );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        //System.out.println( "MapSize "+linelistDataValueMap.size());
        return linelistDataValueMap;
    }
    
    public String getLatestAvaialableDateForLinelistingData( String orgUnitId, String startDate, String endDate )
    {
        String resultDate = "YYYY-MM-DD";
        
        try
        {
            String query = "SELECT executiondate FROM programstageinstance " + 
                                " WHERE organisationunitid = "+ orgUnitId +" AND " +
                                " executiondate <= '"+ startDate +"' AND " +
                                " executiondate >= '"+ endDate +"' " +
                                " ORDER BY executiondate DESC LIMIT 1";

            //System.out.println( "QUERY: "+ query );
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                resultDate = rs.getString( 1 );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        /*
        if( resultDate != null ) 
        {
           try
            {
                Date tempDate = format.parse( resultDate );
                SimpleDateFormat dataFromat = new SimpleDateFormat("dd/MM/yyyy");
                resultDate = dataFromat.format( tempDate );
            }
            catch( Exception e )
            {
                System.out.println("Exception while converting Date");
            }
        }
        */
        
        return resultDate;
    }


    public String getLatestAvaialableDate( Integer dataElementId, Integer optionComboId, String orgUnitId, String startDate, String endDate )
    {
        String resultDate = "YYYY-MM-DD";
        
        try
        {
            String query = "SELECT period.startdate FROM datavalue " +
                            " INNER JOIN period ON period.periodid = datavalue.periodid " +
                            " WHERE " +
                                " dataelementid = "+ dataElementId +" AND " +
                                " categoryoptioncomboid = "+ optionComboId +" AND " +
                                " sourceid = "+ orgUnitId + " AND " +
                                " period.startdate <= '"+ startDate +"' ";
            if( endDate != null )
            {
                query += " AND period.startdate >= '"+ endDate +"' ";
            }
            query += " ORDER BY startdate DESC LIMIT 1";

            //System.out.println( "QUERY: "+ query );
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                resultDate = rs.getString( 1 );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        if( resultDate != null ) 
        {
            try
            {
                Date tempDate = format.parse( resultDate );
                SimpleDateFormat dataFromat = new SimpleDateFormat("dd/MM/yyyy");
                resultDate = dataFromat.format( tempDate );
            }
            catch( Exception e )
            {
                System.out.println("Exception while converting Date");
            }
        }
        return resultDate;
    }

    public String getAggDataBetweenStartAndEndDate( Integer dataElementId, Integer optionComboId, String orgUnitId, String startDate, String endDate )
    {
        Double aggDataValue = 0.0;
        
        try
        {
            String query = "SELECT SUM(value) FROM datavalue " +
                            " INNER JOIN period ON period.periodid = datavalue.periodid " +
                            " WHERE " +
                                " dataelementid = "+ dataElementId +" AND " +
                                " categoryoptioncomboid = "+ optionComboId +" AND " +
                                " sourceid = "+ orgUnitId + " AND " +
                                " period.startdate >= '"+ startDate +"'";
            
            if( endDate != null )
            {
                query += " AND period.startdate <= '"+ endDate +"'";
            }

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                aggDataValue = rs.getDouble( 1 );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return aggDataValue.toString();
    }

    public String getDataValueForLatestAvailabeDate( Integer dataElementId, Integer optionComboId, String orgUnitId, String startDate, String endDate, String cellType )
    {
    	Double dataValue = 0.0;
        String result = "";
        
        try
        {
            String query = "SELECT value FROM datavalue " +
            	            " INNER JOIN period ON period.periodid = datavalue.periodid " +
            	            " WHERE " +
            	                " dataelementid = "+ dataElementId +" AND " +
            	                " categoryoptioncomboid = "+ optionComboId +" AND " +
            	                " sourceid = "+ orgUnitId + " AND " +
            	                " period.startdate <= '"+ startDate +"' ";
           if( endDate != null )
           {
               query += " AND period.startdate >= '"+ endDate +"' ";
           }
           query +=" ORDER BY startdate DESC LIMIT 1";

           SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
           //System.out.println( " Query " + query );
           while ( rs.next() )
           {
               if( cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_DATEDATA ) 
                   || cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_TARGETDATEDATA ))
               {
                   result = rs.getString( 1 );
                   if( result != null ) 
                   {
                       try
                       {
                           Date tempDate = format.parse( result );
                           SimpleDateFormat dataFromat = new SimpleDateFormat("dd/MM/yyyy");
                           result = dataFromat.format( tempDate );
                       }
                       catch( Exception e )
                       {
                           System.out.println("Exception while converting Date");
                       }
                       
                   }
               }
               else
               {
                   dataValue = rs.getDouble( 1 );
               }
              
           }
            
           if( !cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_DATEDATA ) && !cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_TARGETDATEDATA ) )
           {
               result = dataValue.toString();
           }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return result;	
    }

    public String geAggDataForMultipleOrgUnitsMultiplePeriods( Integer dataElementId, Integer optionComboId, String orgUnitId, String startDate, String endDate )
    {
        Double aggDataValue = 0.0;
        
        try
        {
            String query = "SELECT SUM(value) FROM datavalue " +
                            " INNER JOIN period ON period.periodid = datavalue.periodid " +
                            " WHERE " +
                                " dataelementid = "+ dataElementId +" AND " +
                                " categoryoptioncomboid = "+ optionComboId +" AND " +
                                " sourceid IN ("+ orgUnitId + " ) AND " +
                                " period.startdate >= '"+ startDate +"'";
            if( endDate != null )
            {
                query += "  AND period.startdate <= '"+ endDate +"'";
            }

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                aggDataValue = rs.getDouble( 1 );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return aggDataValue.toString();
    }

    public String getOrgunitAggregatedDataForLatestAvailabeDate( Integer dataElementId, Integer optionComboId, String orgUnitId, String startDate, String endDate )
    {
        Double dataValue = 0.0;
        
        try
        {
            String query = "SELECT period.startDate, SUM(value) FROM datavalue " +
                            " INNER JOIN period ON period.periodid = datavalue.periodid " + 
                            " WHERE " + 
                                " dataelementid = " + dataElementId + " AND " +
                                " categoryoptioncomboid = " + optionComboId + " AND " + 
                                " sourceid IN ( "+ orgUnitId +") AND " +
                                " period.startdate <= '"+ startDate +"'";
            if( endDate != null )
            {
                query += " AND period.startdate >= '"+ endDate +"' ";
            }
                                
            query += " GROUP BY period.startDate ORDER BY startDate desc limit 1";
                
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                dataValue = rs.getDouble( 2 );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return dataValue.toString();
    }

    public String getDateAndDataValueForLatestAvailabeDate( Integer dataElementId, Integer optionComboId, String orgUnitId, String startDate, String endDate, String cellType )
    {
        Double dataValue = 0.0;
        String result = "";
        
        try
        {
            String query = "SELECT period.startdate, value FROM datavalue " +
                            " INNER JOIN period ON period.periodid = datavalue.periodid " +
                            " WHERE " +
                                " dataelementid = "+ dataElementId +" AND " +
                                " categoryoptioncomboid = "+ optionComboId +" AND " +
                                " sourceid = "+ orgUnitId + " AND " +
                                " period.startdate <= '"+ startDate +"' ";
           if( endDate != null )
           {
               query += " AND period.startdate >= '"+ endDate +"' ";
           }
           query +=" ORDER BY startdate DESC LIMIT 1";

           SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
           while ( rs.next() )
           {
               if( cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_DATEDATA ) 
                   || cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_TARGETDATEDATA ))
               {
                   result = rs.getString( 1 );
                   if( result != null ) 
                   {
                      try
                       {
                           Date tempDate = format.parse( result );
                           SimpleDateFormat dataFromat = new SimpleDateFormat("dd/MM/yyyy");
                           result = dataFromat.format( tempDate );
                       }
                       catch( Exception e )
                       {
                           System.out.println("Exception while converting Date");
                       }
                   }
               }
               else
               {
                   dataValue = rs.getDouble( 1 );
               }
               //System.out.println( result + " :: " + dataValue );
           }
            
           if( !cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_DATEDATA ) && !cellType.equalsIgnoreCase( FortnightReport.CELL_TYPE_TARGETDATEDATA ) )
           {
               result = dataValue.toString();
           }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return result;
    }
}
