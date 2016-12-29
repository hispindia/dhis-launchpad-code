package org.hisp.dhis.caseentry.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Mithilesh Kumar Thakur
 */
public class Service 
{
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // -------------------------------------------------------------------------
    // Support Methods Defination
    // -------------------------------------------------------------------------
    
    
    // get ProgramInstanceId from patientId and programId
    public Integer getProgramInstanceId( Integer programId )
    {
        Integer programInstanceId = null;
        
        try
        {
            String query = "SELECT programinstanceid FROM programinstance WHERE programid = " + programId + " ";
                

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
    
    
    
    
    
    
    
    
    
    // get Latest ProgramStageInstanceIds List
    public List<Integer> getLatestProgramStageInstanceIds()
    {
        List<Integer> programStageInstanceIds = new ArrayList<Integer>();

        try
        {
            String query = "SELECT programstageinstanceid FROM programstageinstance " +
                            " WHERE executiondate IN( SELECT MAX(executiondate)FROM programstageinstance )" +
                            " ORDER BY programstageinstanceid DESC " ;
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer programStageInstanceId = rs.getInt( 1 );
                         
                    if ( programStageInstanceId != null )
                    {
                        programStageInstanceIds.add( programStageInstanceId );
                    }
                    
                }
           
            return programStageInstanceIds;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal Program Stage Instance Id", e );
        }
    }    
    
    // get Latest DataValue FromPatientDataValue
    public Map<String, String> getLatestDataValueFromPatientDataValue( Integer programId, Integer programStageId )
    {
        Map<String, String> patientDataValueMap = new HashMap<String, String>();

        try
        {
            String query = "SELECT programstageinstance.executiondate, patientdatavalue.programstageinstanceid, dataelementid, VALUE FROM patientdatavalue " +
                            " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid  " +
                            " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " +
                            " WHERE programinstance.programid = " + programId + " AND programstageinstance.programstageid = " + programStageId + " AND " +
                            " programstageinstance.executiondate = ( SELECT MAX(executiondate)FROM programstageinstance ) " +
                            " ORDER BY patientdatavalue.programstageinstanceid DESC ";
            
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer programStageInstanceId = rs.getInt( 2 );
                Integer dataelementId = rs.getInt( 3 );
                String dataValue = rs.getString( 4 );
                
                if ( dataValue != null )
                {
                    patientDataValueMap.put( programStageInstanceId +":" + dataelementId, dataValue );
                    
                }
            }

            return patientDataValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal Program Stage Instance Id", e );
        }
    }            
    
}
