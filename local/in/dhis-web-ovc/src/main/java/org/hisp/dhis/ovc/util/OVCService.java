package org.hisp.dhis.ovc.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.period.Period;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class OVCService
{

    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    
    // -------------------------------------------------------------------------
    // Support Methods Defination
    // -------------------------------------------------------------------------    

    public List<Period> getMontlyPeriods( Date startDate, Date endDate )
    {
        List<Period> periods = new ArrayList<Period>();
        
        SimpleDateFormat monthFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        //Date sDate = format.parseDate( startDate );
        //Date eDate = format.parseDate( endDate );

        Calendar cal = Calendar.getInstance();
        cal.setTime( endDate );
        cal.set(  Calendar.DATE, 1 );
        endDate = cal.getTime();
        
        cal = null;
        cal = Calendar.getInstance();
        cal.setTime( startDate );
        cal.set(  Calendar.DATE, 1 );
        
        // for external Period Id
        Date sDate = startDate;
        Calendar sCal = Calendar.getInstance();
        sCal.setTime( sDate );
        sCal.set(  Calendar.DATE, 1 );
        sDate = cal.getTime();
        
        String tempStartDate = dateFormat.format( sDate );
        
        int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        
        int curMonth = Integer.parseInt(  tempStartDate.split( "-" )[1] );
        int curYear = Integer.parseInt(  tempStartDate.split( "-" )[0] );
        
        int curMonthDays = monthDays[ curMonth ];
        
        if( curMonth == 2 && curYear%4 == 0 )
        {
            curMonthDays++;
        }
        
        String eDate =  curYear + "-" + tempStartDate.split( "-" )[1] + "-" + curMonthDays;
        
        while( cal.getTime().before( endDate ) )
        {
            Period period = new Period();
            String externalId = "Monthly_" + dateFormat.format( sDate ) + "_" + eDate;
            period.setDescription( externalId );
            period.setStartDate( cal.getTime() );
            period.setName( monthFormat.format( cal.getTime() ) );            
            cal.add( Calendar.MONTH, 1 );
            periods.add( period );
        }
        
        return periods;
    }
   
    /*
    SELECT MAX( identifier ) FROM patientidentifier INNER JOIN patient ON patient.patientid = patientidentifier.patientid
    WHERE patient.organisationunitid = 133 AND patientidentifiertypeid = 918;    
    */
    
    public String getMaxOVCId( Integer orgUnitId, Integer identifierTypeId )
    {
        String ovcId = null;
        
        try
        {
            String query = "SELECT MAX( identifier ) FROM patientidentifier INNER JOIN patient ON patient.patientid = patientidentifier.patientid " +
            		    "WHERE patient.organisationunitid = " + orgUnitId + " AND patientidentifiertypeid = " + identifierTypeId;

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                ovcId = rs.getString( 1 );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return ovcId;
    }
    
    
    
    // get ProgramInstanceId from patientId and programId
    public Integer getProgramInstanceId( Integer patientId, Integer programId )
    {
        Integer programInstanceId = null;
        
        try
        {
            String query = "SELECT programinstanceid FROM programinstance WHERE patientid = " + patientId + " AND "
                + " programid = " + programId;

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                programInstanceId = rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return programInstanceId;
    }
    
    
    // get ProgramStageInstanceId from programInstanceId  programStageId and executiondate
    public Integer getProgramStageInstanceId( Integer programInstanceId, Integer programStageId, String executiondate )
    {
        Integer programStageInstanceId = null;
        
        try
        {
            String query = "SELECT programstageinstanceid FROM programstageinstance WHERE programinstanceid = " + programInstanceId + " AND "
                + " programstageid = " + programStageId + " AND executiondate = '" + executiondate +"'";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                programStageInstanceId = rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return programStageInstanceId;
    }    
    
    // get DataValueFromPatientDataValue from programStageInstanceId
    public Map<Integer, String> getDataValueFromPatientDataValue( Integer programStageInstanceId )
    {
        Map<Integer, String> patientDataValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT dataelementid, value FROM patientdatavalue WHERE programstageinstanceid = " + programStageInstanceId;
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer dataelementid = rs.getInt( 1 );
                String dataValue = rs.getString( 2 );
                
                if ( dataValue != null )
                {
                    patientDataValueMap.put( dataelementid, dataValue );
                }
            }

            return patientDataValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    

    //--------------------------------------------------------------------------------
    // Get Patient List Sort by Patient Attribute
    //--------------------------------------------------------------------------------
    /*
    public List<Patient> getPatientListSortByAttribute( Integer patientAttributeId )
    {
        List<Patient> patientList = new ArrayList<Patient>();
        
        try
        {
            String query = "SELECT patentid, CONCAT(firstname,' ',middlename,' ',lastname), phoneNumber FROM patient "+
                            " WHERE organisationunitid = "+;  
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Patient patient = new Patient();

                Integer patientId = rs.getInt( 1 );
                String patientAttributeValue = rs.getString( 3 );
                
                patientList.add( patient );
            }

            return patientList;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
    */

    //--------------------------------------------------------------------------------
    // Get Patient Attribute Values by Patient Ids
    //--------------------------------------------------------------------------------
    public Map<String, String> getPatientAttributeValues( String patientIdsByComma )
    {
        Map<String, String> patientAttributeValueMap = new HashMap<String, String>();

        try
        {
            String query = "SELECT patientid, patientattributeid, value FROM patientattributevalue " +
                            "WHERE patientid IN ( "+ patientIdsByComma +")";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer patientId = rs.getInt( 1 );
                Integer patientAttributeId = rs.getInt( 2 );                
                String patientAttributeValue = rs.getString( 3 );
                
                if ( patientAttributeValue != null )
                {
                    patientAttributeValueMap.put( patientId+":"+patientAttributeId, patientAttributeValue );
                }
            }

            return patientAttributeValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    

    
    //--------------------------------------------------------------------------------
    // Get Patient Data Values by Patient Ids
    //--------------------------------------------------------------------------------
    public Map<String, String> getPatientDataValues( String patientIdsByComma, List<Period> periods, Integer programId, Integer prograStageId )
    {
        Map<String, String> patientDataValueMap = new HashMap<String, String>();

        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
            for( Period period : periods )
            {
                String executionDate = simpleDateFormat.format( period.getStartDate() );
                
                String query = "SELECT programinstance.patientid, dataelementid, value FROM patientdatavalue " +
                                " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                                " INNER JOIN programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                                " WHERE " + 
                                    " programinstance.programid = " + programId +" AND " + 
                                    " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                    " programinstance.patientid IN (" + patientIdsByComma + ") AND " + 
                                    " programstageinstance.executiondate = '"+ executionDate +"'";
              
                SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
    
                while ( rs.next() )
                {
                    Integer patientId = rs.getInt( 1 );
                    Integer dataElementId = rs.getInt( 2 );                
                    String patientDataValue = rs.getString( 3 );
                    
                    if ( patientDataValue != null )
                    {
                        patientDataValueMap.put( patientId+":"+period.getId()+":"+prograStageId+":"+dataElementId, patientDataValue );
                    }
                }
            }
            
            return patientDataValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
    
    
    //--------------------------------------------------------------------------------
    // Get Patient Data Values by Patient Ids
    //--------------------------------------------------------------------------------
    public Map<Integer, String> getPatientLastVisitDates( String patientIdsByComma, Integer programId, Integer prograStageId )
    {
        Map<Integer, String> patientLastVisitDateMap = new HashMap<Integer, String>();

        try
        {
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                
            String query = "SELECT programinstance.patientid, MAX( executiondate ) FROM programstageinstance " +   
                            " INNER JOIN programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                            " WHERE " +  
                                " programinstance.programid = " + programId +" AND " + 
                                " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                " programinstance.patientid IN (" + patientIdsByComma + ") " +
                                " GROUP BY programinstance.patientid";  
              
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
    
            while ( rs.next() )
            {
                Integer patientId = rs.getInt( 1 );
                String executionDate = rs.getString( 2 );
                
                if ( executionDate != null )
                {
                    int monthDays[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
                    
                    int curMonth = Integer.parseInt(  executionDate.split( "-" )[1] );
                    int curYear = Integer.parseInt(  executionDate.split( "-" )[0] );
                    int curMonthDays = monthDays[ curMonth ];
                    
                    if( curMonth == 2 && curYear%4 == 0 )
                    {
                        curMonthDays++;
                    }
                    executionDate = executionDate.split( "-" )[0] + "-" + executionDate.split( "-" )[1] + "-" + curMonthDays;
                    patientLastVisitDateMap.put( patientId, executionDate );
                }
            }
            
            return patientLastVisitDateMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception in getPatientLastVisitDates", e );
        }
    }    

    
    public Map<String, String> getVisitDateMap( List<Period> periods, Integer patientId )
    {
        Map<String, String> visitDateMap = new HashMap<String, String>();
        
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            for( Period period : periods )
            {
                String executionDate = simpleDateFormat.format( period.getStartDate() );
                
                String query = "SELECT COUNT( executiondate ) FROM programstageinstance, programinstance " +
                                " WHERE " + 
                                    " programstageinstance.programinstanceid = programinstance.programinstanceid AND " +
                                    " programinstance.patientid = " + patientId + " AND " +
                                    " executiondate = '" + executionDate + "'";
              
                SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
    
                if ( rs.next() )
                {
                    Integer count = rs.getInt( 1 );
                    if ( count == 0 )
                    {
                        visitDateMap.put( executionDate, "red" );
                    }
                    else
                    {
                        visitDateMap.put( executionDate, "green" );
                    }
                    
                }
                else
                {
                    visitDateMap.put( executionDate, "red" );
                }
            }
            
            return visitDateMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }
    
    
}
