package org.hisp.dhis.ovc.util;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Mithilesh Kumar Thakur
 */
public class OVCAggregationService
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
    private DataElementCategoryService dataElementCategoryService;
    
    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    */
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }
    
    // -------------------------------------------------------------------------
    // Support Methods Defination
    // -------------------------------------------------------------------------   
    
    //No.of Caregivers who required at least one Health & Nutrition Suport
    public Map<String, Integer> calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneHealthAndNutritionSuport( Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, String dataElementIdsByComma, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( "DATAELEMENTID_BY_COMMA", dataElementIdsByComma );
            
            query = query.replace( "CURRENT_PERIOD_STARTDATE", simpleDateFormat.format( period.getStartDate() ) );
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+optionCombo.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }
    
    
    //No. of Caregivers who received at least one Economic strengthening service support
    /*
    public Map<String, Integer> calculateNoOfFemaleCaregiversWhoRequiredAtLeastOneEconomicAndStrengtheningServiceSuport( Period period, DataElement dataElement, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( "CURRENT_PERIOD_STARTDATE", simpleDateFormat.format( period.getStartDate() ) );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }    
    
    */
    
    
    //Number of new OVC Registered
    public Map<String, Integer>numberOfOVCRegistered( Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, String gender, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( "GENDER", gender );
            
            query = query.replace( "CURRENT_PERIOD_STARTDATE", simpleDateFormat.format( period.getStartDate() ) );
            
            query = query.replace( "CURRENT_PERIOD_ENDDATE", simpleDateFormat.format( period.getEndDate() ) );
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+optionCombo.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }    


    //Number of active/Inactive ( and attribute value based method) OVC
    public Map<String, Integer>numberOfOVC( Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, String gender, String status, String patientAttributeId, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( "GENDER", gender );
            
            query = query.replace( "STATUS", status );
            
            query = query.replace( "PAID", patientAttributeId );
            
            query = query.replace( "CURRENT_PERIOD_STARTDATE", simpleDateFormat.format( period.getStartDate() ) );
            
            query = query.replace( "CURRENT_PERIOD_ENDDATE", simpleDateFormat.format( period.getEndDate() ) );
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+optionCombo.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }    
    
    
    
    //Number of OVC NOT in School
    public Map<String, Integer>numberOfOVCNotInSchool( Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, String gender, String patientAttributeId, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( "GENDER", gender );
            
            query = query.replace( "PAID", patientAttributeId );
            
            query = query.replace( "CURRENT_PERIOD_STARTDATE", simpleDateFormat.format( period.getStartDate() ) );
            
            query = query.replace( "CURRENT_PERIOD_ENDDATE", simpleDateFormat.format( period.getEndDate() ) );
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+optionCombo.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }    
       

    //Number of OVC NOT in School
    public Map<String, Integer>numberOfOVCByAgeGroup( Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, String gender, String age,  Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( "GENDER", gender );
            
            query = query.replace( "CONDITION", age );
            
            query = query.replace( "CURRENT_PERIOD_STARTDATE", simpleDateFormat.format( period.getStartDate() ) );
            
            query = query.replace( "CURRENT_PERIOD_ENDDATE", simpleDateFormat.format( period.getEndDate() ) );
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+optionCombo.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }    
    
    
    //STF form1A
    public Map<String, Integer> calculateNoOfOVCInSTFWhoRequiredAtLeastOneServiceAndAssessment( Period period, String gender, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, String dataElementIdsByComma, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            
            query = query.replace( "GENDER", gender );
            
            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( "DATAELEMENTID_BY_COMMA", dataElementIdsByComma );
            
            query = query.replace( "CURRENT_PERIOD_STARTDATE", simpleDateFormat.format( period.getStartDate() ) );
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+optionCombo.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }
        

    
    //Multiple service
    public Map<String, Integer> calculateNoOfOVCRecivedMultipleService( Period period, String gender, String age, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            
            query = query.replace( "GENDER", gender );
            
            query = query.replace( "CONDITION", age );
            
            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( "CURRENT_PERIOD_STARTDATE", simpleDateFormat.format( period.getStartDate() ) );
            
            query = query.replace( "CURRENT_PERIOD_ENDDATE", simpleDateFormat.format( period.getEndDate() ) );
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+optionCombo.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }
    
    
 
    //Multiple service
    public Map<String, Integer> calculateNoOfOVCNotRecivedAnyService( Period period, String gender, String age, DataElement dataElement, DataElementCategoryOptionCombo optionCombo, Set<OrganisationUnit> orgUnits, String query )
    {
        Map<String, Integer> aggregationResultMap = new HashMap<String, Integer>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        try
        {
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            
            query = query.replace( "GENDER", gender );
            
            query = query.replace( "CONDITION", age );
            
            query = query.replace( "ORGUNITID_BY_COMMA", orgUnitIdsByComma );
            
            query = query.replace( "CURRENT_PERIOD_STARTDATE", simpleDateFormat.format( period.getStartDate() ) );
            
            query = query.replace( "CURRENT_PERIOD_ENDDATE", simpleDateFormat.format( period.getEndDate() ) );
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer countValue = rs.getInt( 2 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+optionCombo.getId(), countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println( "Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // Method for Importing calculated data in data value table
    
    public String importAggregatedData( Map<String, Integer> aggregationResultMap, Period period )
    {
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }
        
        /*
        DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo( Integer.parseInt(optionComboId) );

        if ( optionCombo == null )
        {
            return null;
        }
        */
        
        String importStatus = "";

        Integer updateCount = 0;
        Integer insertCount = 0;

        String storedBy = currentUserService.getCurrentUsername();
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        long t;
        Date d = new Date();
        t = d.getTime();
        java.sql.Date lastUpdatedDate = new java.sql.Date( t );

        String query = "";
        int insertFlag = 1;
        //String insertQuery = "INSERT INTO datavalue ( dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated, attributeoptioncomboid ) VALUES ";
        
        String insertQuery = "INSERT INTO datavalue ( dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";
        
        try
        {
            int count = 1;
            for ( String cellKey : aggregationResultMap.keySet() )
            {
                // Orgunit
                String[] oneRow = cellKey.split( ":" );
                Integer orgUnitId = Integer.parseInt( oneRow[0] );
                Integer deId = Integer.parseInt( oneRow[1] );
                Integer deCOCId = Integer.parseInt( oneRow[2] );
                
                //Integer deCOCId = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo().getId();
                
                Integer periodId = storedPeriod.getId();
                String value = aggregationResultMap.get( cellKey ) + "";

                query = "SELECT value FROM datavalue WHERE dataelementid = " + deId + " AND categoryoptioncomboid = " + deCOCId + " AND periodid = " + periodId + " AND sourceid = " + orgUnitId;
                SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
                if ( sqlResultSet1 != null && sqlResultSet1.next() )
                {
                    String updateQuery = "UPDATE datavalue SET value = '" + value + "', storedby = '" + storedBy + "',lastupdated='" + lastUpdatedDate + "' WHERE dataelementid = " + deId + " AND periodid = "
                        + periodId + " AND sourceid = " + orgUnitId + " AND categoryoptioncomboid = " + deCOCId;

                    jdbcTemplate.update( updateQuery );
                    updateCount++;
                }
                else
                {
                    if ( value != null && !value.trim().equals( "" ) )
                    {
                        //insertQuery += "( " + deId + ", " + periodId + ", " + orgUnitId + ", " + deCOCId + ", '" + value + "', '" + storedBy + "', '" + lastUpdatedDate + "'," + deCOCId + "), ";
                        
                        insertQuery += "( " + deId + ", " + periodId + ", " + orgUnitId + ", " + deCOCId + ", '" + value + "', '" + storedBy + "', '" + lastUpdatedDate + "' ), ";
                        insertFlag = 2;
                        insertCount++;
                    }
                }

                if ( count == 1000 )
                {
                    count = 1;

                    if ( insertFlag != 1 )
                    {
                        insertQuery = insertQuery.substring( 0, insertQuery.length() - 2 );
                        jdbcTemplate.update( insertQuery );
                    }

                    insertFlag = 1;

                    insertQuery = "INSERT INTO datavalue ( dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";
                }

                count++;
            }

            if ( insertFlag != 1 )
            {
                insertQuery = insertQuery.substring( 0, insertQuery.length() - 2 );
                jdbcTemplate.update( insertQuery );
            }

            importStatus = "Successfully populated aggregated data for the period : " + storedPeriod.getStartDateString() + " To " + simpleDateFormat.format( storedPeriod.getEndDate() );
            importStatus += "<br/> Total new records : " + insertCount;
            importStatus += "<br/> Total updated records : " + updateCount;

        }
        catch ( Exception e )
        {
            importStatus = "Exception occured while import, please check log for more details" + e.getMessage();
        }

        return importStatus;
    }
    

}
