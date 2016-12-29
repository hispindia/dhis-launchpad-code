package org.hisp.dhis.reports.util;

import org.hisp.dhis.system.util.MathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FPMUReportManager 
{
    private static final String NULL_REPLACEMENT = "0";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /*
    private I18nFormat format = new org.hisp.dhis.i18n.I18nFormat();
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    // -------------------------------------------------------------------------
    // Support Methods
    // -------------------------------------------------------------------------

    public List<DFSReport> getDFSReportDesign( String designXMLFile )
    {
        String path = System.getenv( "DHIS2_HOME" )+ File.separator + "fpmureports" + File.separator + designXMLFile;
        
        List<DFSReport> reportDesignList = new ArrayList<DFSReport>();
        
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "Error: XML File Not Found at Report Home!!" );
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
                    String datatype = reportElement.getAttribute( "datatype" );

                    DFSReport dfsReport = new DFSReport( period, orgunit, dataelement, name, celltype, aggtype, datatype );
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
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( dataElementExp );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString.length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                Integer dataElementId = Integer.parseInt( replaceString );
                Integer optionComboId = Integer.parseInt( optionComboIdStr );

                String dataValue = null;
                
                if( aggType.equalsIgnoreCase( DFSReport.AGG_TYPE_ONE_PERIOD ) )
                {
                    dataValue = getDataValueForLatestAvailableDate(dataElementId, optionComboId, orgUnitId, startDate, endDate, cellType);
                }
                else if( aggType.equalsIgnoreCase( DFSReport.AGG_TYPE_PERIOD_AGG ) )
                {
                    dataValue = getAggDataBetweenStartAndEndDate( dataElementId, optionComboId, orgUnitId, startDate, endDate );
                }
                else if( aggType.equalsIgnoreCase( DFSReport.AGG_TYPE_ONE_PERIOD_MULTIPLE_ORGUNITS ) )
                {
                    dataValue = getOrgunitAggregatedDataForLatestAvailabeDate( dataElementId, optionComboId, orgUnitId, startDate, endDate );
                }
                else if( aggType.equalsIgnoreCase( DFSReport.AGG_TYPE_MULTIPLE_PERIOD_MULTIPLE_ORGUNITS ) )
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

            if( cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_DATA ) 
                || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_TARGETDATA ) 
                || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_STARTDATE ) 
                || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_ENDDATE )
                || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_FOR_STARTDATE_AND_ENDDATE )
                || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_FOR_STARTDATE_AND_ENDDATE_PROJECTION ))
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                	if( dataElementExp.equals("([265.1]/1000)") )
                	{
                		System.out.println( "**************** Before : " + buffer.toString() );
                		System.out.println( "**************** After : " + d );
                		
                	}

                    //d = Math.round( d * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
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

            return resultValue;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception in getResultValue method: ", e );
        }
    }

    public String getIntegerResultValue( String dataElementExp, String startDate, String endDate, String orgUnitId, String aggType, String cellType )
    {

            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( dataElementExp );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

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

                if( aggType.equalsIgnoreCase( DFSReport.AGG_TYPE_ONE_PERIOD ) )
                {
                    dataValue = getDataValueForLatestAvailableDate(dataElementId, optionComboId, orgUnitId, startDate, endDate, cellType);
                }
                else if( aggType.equalsIgnoreCase( DFSReport.AGG_TYPE_PERIOD_AGG ) )
                {
                    dataValue = getAggDataBetweenStartAndEndDate( dataElementId, optionComboId, orgUnitId, startDate, endDate );
                }
                else if( aggType.equalsIgnoreCase( DFSReport.AGG_TYPE_ONE_PERIOD_MULTIPLE_ORGUNITS ) )
                {
                    dataValue = getOrgunitAggregatedDataForLatestAvailabeDate( dataElementId, optionComboId, orgUnitId, startDate, endDate );
                }
                else if( aggType.equalsIgnoreCase( DFSReport.AGG_TYPE_MULTIPLE_PERIOD_MULTIPLE_ORGUNITS ) )
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

            if( cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_DATA )
                    || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_TARGETDATA )
                    || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_STARTDATE )
                    || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_ENDDATE )
                    || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_FOR_STARTDATE_AND_ENDDATE )
                    || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_FOR_STARTDATE_AND_ENDDATE_PROJECTION ))
            {
                double d = 0.0;
                try
                {
                    d = MathUtils.calculateExpression( buffer.toString() );
                    //d = Math.round( d * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
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
            if(resultValue.contains(".") && resultValue.split("\\.").length==2)
            {
                String[] resultValueSplit = resultValue.split("\\.");
                resultValue = resultValueSplit[0];
            }

            return resultValue;
    }

    public Map<Integer, Map<Integer, String>> getLineListingDataForProgramStage(String programStageId, String orgUnitId, String reportingDate)
    {
        Map<Integer, Map<Integer, String>> lineListDataValueMap = new HashMap<Integer, Map<Integer, String>>();
        
        try
        {
            String query = "SELECT patientdatavalue.programstageinstanceid, dataelementid, value FROM patientdatavalue "+
                                " INNER JOIN programstageinstance ON patientdatavalue.programstageinstanceid = programstageinstance.programstageinstanceid " +
                                " WHERE " +
                                    " programstageinstance.executiondate = '"+ reportingDate +"' AND " + 
                                    " programstageinstance.organisationunitid = " + orgUnitId + " AND " + 
                                    " programstageinstance.programstageid = "+programStageId;


            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer programStageInstanceId = rs.getInt( 1 );
                Integer dataElementId = rs.getInt( 2 );
                String value = rs.getString( 3 );
                
                Map<Integer, String> tempMap = lineListDataValueMap.get( programStageInstanceId );
                if( tempMap == null )
                {
                    tempMap = new HashMap<Integer, String>();
                }
                tempMap.put( dataElementId, value );
                
                lineListDataValueMap.put(programStageInstanceId, tempMap);
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        return lineListDataValueMap;
    }
    
    public String getLatestAvailableDateForLineListingData(String orgUnitId, String startDate, String endDate)
    {
        String resultDate = "YYYY-MM-DD";
        
        try
        {
            String query = "SELECT executiondate FROM programstageinstance " + 
                                " WHERE organisationunitid = "+ orgUnitId +" AND " +
                                " executiondate <= '"+ startDate +"' AND " +
                                " executiondate >= '"+ endDate +"' " +
                                " ORDER BY executiondate DESC LIMIT 1";

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
        
        return resultDate;
    }

    public String getLatestAvailableDateFromMultipleDes(String dataElementIdsByComma, String orgUnitId, String startDate, String endDate)
    {
        String resultDate = "YYYY-MM-DD";

        try
        {
            String query = "SELECT startdate FROM datavalue " + 
            	  				" INNER JOIN period ON period.periodid = datavalue.periodid " + 
            	  				" WHERE " + 
            	  					" dataelementid in ( " + dataElementIdsByComma +" ) AND " +        
            	  					" sourceid = " + orgUnitId + " AND " + 
            	  					" period.startdate <= '"+ startDate +"' "; 
            	        
				            if( endDate != null )
				            {
				                query += " AND period.startdate >= '"+ endDate +"' ";
				            }
				            query += " ORDER BY startdate DESC LIMIT 1";

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
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                resultDate = dateFormat.format( tempDate );
            }
            catch( Exception e )
            {
                System.out.println("Error: Exception while converting Date !!");
            }
        }
        return resultDate;
    }

    

    public String getLatestAvailableDate(Integer dataElementId, Integer optionComboId, String orgUnitId, String startDate, String endDate)
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                resultDate = dateFormat.format( tempDate );
            }
            catch( Exception e )
            {
                System.out.println("Error: Exception while converting Date!!");
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

    public String getDataValueForLatestAvailableDate(Integer dataElementId, Integer optionComboId, String orgUnitId, String startDate, String endDate, String cellType)
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

           while ( rs.next() )
           {
               if( cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_DATEDATA ) 
                   || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_TARGETDATEDATA ))
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
                           System.out.println("Error: Exception while converting Date!!");
                       }
                       
                   }
               }
               else if( cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_STARTDATE_TEXTDATA ) )
               {
            	   result = rs.getString( 1 );
               }
               else
               {
                   try
                   {
                	   dataValue = rs.getDouble( 1 );
                   }
                   catch( Exception e )
                   {
                	   dataValue = 0.0;
                   }
               }
           }
            
           if( !cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_DATEDATA ) && !cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_TARGETDATEDATA ) && !cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_FORMULA_USE_STARTDATE_TEXTDATA ) )
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
               if( cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_DATEDATA ) 
                   || cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_TARGETDATEDATA ))
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
                           System.out.println("Error: Exception while converting Date!!");
                       }
                   }
               }
               else
               {
                   dataValue = rs.getDouble( 1 );
               }
           }
            
           if( !cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_DATEDATA ) && !cellType.equalsIgnoreCase( DFSReport.CELL_TYPE_TARGETDATEDATA ) )
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
    
    public List<String> getMinAndMaxYear()
    {
    	List<String> yearsList=new ArrayList<String>();
	String startDate = null;
	String endDate = null;
	try
        {
	    String query = "SELECT MIN(startdate), MAX(startdate) FROM period";
	    SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
	    while ( rs.next() )
	    {
		startDate = rs.getString( 1 );
		endDate = rs.getString( 2 );	               
	    }

	    int startYear = Integer.parseInt(startDate.trim().split("-")[0]);
	    int endYear = Integer.parseInt(endDate.trim().split("-")[0]);
			
	    Calendar cal = Calendar.getInstance();
            cal.setTime( new Date() );
            int currentYear = cal.get( Calendar.YEAR );
            
            for( int i = endYear; i >= startYear; i-- )
            {
                if( i <= currentYear )
                    yearsList.add( i+"" );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
	return yearsList;
    }

    public Map<String, String> getAvailableRawData( Integer orgUnitId, Integer periodId, String dataElementIdsByComma )
    {
        Map<String, String> availableRawDataMap = new HashMap<String, String>();
        
        try
        {
            String query = "SELECT dataelementid, categoryoptioncomboid, value FROM datavalue " + 
                                " WHERE " +
                                    " sourceid = "+ orgUnitId +" AND " +
                                    " periodid = "+ periodId +" AND " +
                                    " dataelementid IN ("+ dataElementIdsByComma +")";    
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                availableRawDataMap.put( rs.getInt(1)+":"+rs.getInt(2), rs.getString(3) );
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }
        
        return availableRawDataMap;
    }
 //Last updated   
    public String getLastUpdatedDateFromMultipleDes(String dataElementIdsByComma, String startDate, String endDate )
    {
        String resultDate = "YYYY-MM-DD";

        try
        {
            String query = "SELECT enddate FROM datavalue " + 
            	  				" INNER JOIN period ON period.periodid = datavalue.periodid " + 
            	  				" WHERE " + 
            	  					" dataelementid in ( " + dataElementIdsByComma +" ) AND "
            	  							+ "period.enddate <= '"+ endDate +"'"
            	  									+ "ORDER BY enddate DESC LIMIT 1";
            	  			
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                resultDate = dateFormat.format( tempDate );
            }
            catch( Exception e )
            {
                System.out.println("Error: Exception while converting Date !!");
            }
        }
        return resultDate;
    }

    
    

}
