package org.hisp.dhis.asha.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reports.ReportService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ASHAService
{
    public static final String ASHA_AMOUNT_DATA_SET = "Amount"; // 2.0
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
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
    
    
    // get ProgramInstanceId from patientId and programId
    public String getServiceAmount( Integer dataElementId )
    {
        String serviceAmount = "";
        
        Constant amountDataSet = constantService.getConstantByName( ASHA_AMOUNT_DATA_SET );
        
        // Data set  Information
        DataSet dataSet = dataSetService.getDataSet( (int) amountDataSet.getValue() );
        
        // OrganisationUnit  Information
        
        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );
        
        OrganisationUnit organisationUnit = dataSetSource.get( 0 );
        
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        List<AttributeValue> attributeValues = new ArrayList<AttributeValue>( dataElement.getAttributeValues() );
        
        AttributeValue attributeValue = attributeValues.get( 0 );
        
        DataElement aggDataElement = dataElementService.getDataElement( Integer.parseInt( attributeValue.getValue() ) );
        
        if ( aggDataElement != null )
        {
            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
            
            DataValue dataValue = new DataValue();
            
            dataValue = reportService.getLatestDataValue( aggDataElement, optionCombo, organisationUnit );
            
            serviceAmount = "";
            
            if ( dataValue != null )
            {
                serviceAmount = dataValue.getValue();
                
            }
        }
        
        return serviceAmount;
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

    
    
    // get DataValueFromPatientDataValue from programStageInstanceId,and String patientIdsByComma
    public Map<Integer, String> getDataValueFromPatientDataValue( Integer programStageInstanceId, String dataElementIdsByComma )
    {
        Map<Integer, String> patientDataValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT dataelementid, value FROM patientdatavalue WHERE programstageinstanceid = " + programStageInstanceId 
                            +" AND dataelementid IN (" + dataElementIdsByComma + ") ";
          
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
    
    // get Sum of Data value from PatientDataValue of dataElement till last to execution date
    public String getSumOfDataValueFromPatientDataValue( Integer patientId, String executionDate, Integer dataElementId )
    {
        String value = null;

        try
        {
            String query = "SELECT SUM(VALUE) FROM patientdatavalue " + 
            " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
            " INNER JOIN programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
            " WHERE  patientdatavalue.dataelementid = " + dataElementId + " AND programinstance.patientid = " + patientId + " AND " + 
            " programstageinstance.executiondate <= '"+ executionDate +"'";
            
            //System.out.println( " query is --: " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                value = rs.getString( 1 );
            }

            return value; 
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }        
   

 /*
    
    "SELECT SUM(VALUE) FROM `patientdatavalue` 
INNER JOIN  `programstageinstance` ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid 
INNER JOIN `programinstance` ON programinstance.programinstanceid = programstageinstance.programinstanceid
WHERE programstageinstance.`executiondate` <= '2013-04-01' AND patientdatavalue.`dataelementid` = 159 AND programinstance.`patientid` = 46;
"
*/    
    
    
    
    
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
                            "WHERE patientid IN ( "+ patientIdsByComma +" )";
          
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
            throw new RuntimeException( "Illegal Patient id", e );
        }
    }


    
    
    
    //--------------------------------------------------------------------------------
    // Get Patient Attribute Values by Patient Ids and AttributeId
    //--------------------------------------------------------------------------------
    public Map<String, String> getPatientAttributeValues( String patientIdsByComma, Integer attributeId )
    {
        Map<String, String> patientAttributeValueMap = new HashMap<String, String>();

        try
        {
            String query = "SELECT patientid, patientattributeid, value FROM patientattributevalue " +
                            "WHERE  patientattributeid = " + attributeId +" AND patientid IN ( "+ patientIdsByComma +")";
          
           
            //System.out.println( " PatientAttribute Value QUERY ======  :" + query );
            
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
            throw new RuntimeException( "Illegal Patient Ids or Attribute Id", e );
        }
    }


    //--------------------------------------------------------------------------------
    // Get Patient Attribute Values by Patient Id and AttributeIds
    //--------------------------------------------------------------------------------
    public Map<Integer, String> getPatientAttributeValues( Integer patientId, String attributeIdsByComma )
    {
        Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT patientid, patientattributeid, value FROM patientattributevalue " +
                            "WHERE  patientid  = " + patientId +" AND patientattributeid IN ( "+ attributeIdsByComma +")";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                //Integer patientID = rs.getInt( 1 );
                Integer patientAttributeId = rs.getInt( 2 );                
                String patientAttributeValue = rs.getString( 3 );
                
                if ( patientAttributeValue != null )
                {
                    patientAttributeValueMap.put( patientAttributeId, patientAttributeValue );
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
    // Get Patient Data Values by Patient Ids ASHA Master Chart Report
    //--------------------------------------------------------------------------------
    public Map<String, String> getPatientDataValues( String patientIdsByComma, List<Period> periods, Integer programId, Integer prograStageId )
    {
        Map<String, String> patientDataValueMap = new HashMap<String, String>();

        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
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
              
                
                //System.out.println( " query is --: " + query );  
                
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
    // Get Patient Data Values by Patient Ids,DataElements Ids and Execution Date
    //--------------------------------------------------------------------------------
    public Map<String, String> getPatientDataValues( String patientIdsByComma, String dataElementIdsByComma, String executionDate,Integer programId, Integer prograStageId )
    {
        Map<String, String> patientDataValueMap = new HashMap<String, String>();

        try
        {
                
            String query = "SELECT programinstance.patientid, dataelementid, value FROM patientdatavalue " +
                            " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                            " INNER JOIN programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                            " WHERE " + 
                                " programinstance.programid = " + programId +" AND " + 
                                " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                " programinstance.patientid IN (" + patientIdsByComma + ") AND " + 
                                " patientdatavalue.dataelementid IN ( " + dataElementIdsByComma + " ) AND " + 
                                " programstageinstance.executiondate = '"+ executionDate +"'";
              
              
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientId = rs.getInt( 1 );
                    Integer dataElementId = rs.getInt( 2 );                
                    String patientDataValue = rs.getString( 3 );
                    
                    if ( patientDataValue != null )
                    {
                        patientDataValueMap.put( patientId+":"+dataElementId, patientDataValue );
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
    // Get Patient Data Values by Patient Ids,DataElements Ids and Execution Date
    //--------------------------------------------------------------------------------
    public Map<String, String> getASHAFacilitatorDataValues( Integer facilitatorId, Integer periodId )
    {
        Map<String, String> ashaFacilitatorDataValueMap = new HashMap<String, String>();

        try
        {
            String query = "SELECT patientid, dataelementid, value FROM facilitatordatavalue  WHERE facilitatorid = " + facilitatorId + " AND periodid = " + periodId +" " ;
            
            //System.out.println( " SQUERY ======  :" + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientid = rs.getInt( 1 );
                    Integer dataelementid = rs.getInt( 2 );                
                    String facilitatorDataValue = rs.getString( 3 );
                    
                    if ( facilitatorDataValue != null )
                    {
                        ashaFacilitatorDataValueMap.put( dataelementid + ":" + patientid, facilitatorDataValue );
                    }
                }
                
                //System.out.println( " Size of Facilitator Data Value Map inside service is ======  :" + ashaFacilitatorDataValueMap.size() ); 
                
            return ashaFacilitatorDataValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
    
    
    
   
 
    //--------------------------------------------------------------------------------
    // Get Facilitator Data Values by Facilitator Id,Period Id and dataSet id 
    //--------------------------------------------------------------------------------
    public Map<String, String> getfacilitatorDataValues( Integer facilitatorId, Integer periodId, Integer dataSetId )
    {
        Map<String, String> ashaFacilitatorDataValueMap = new HashMap<String, String>();
     
        try
        {
            String query = "SELECT fv.facilitatorid ,fv.patientid, fv.dataelementid, fv.value FROM facilitatordatavalue fv " +
                           "INNER JOIN datasetmembers dsm ON fv.dataelementid=dsm.dataelementid " +
                           "WHERE fv.facilitatorid = " + facilitatorId + " AND fv.periodid = " + periodId + " AND dsm.datasetid = " + dataSetId + "  " ;
            
            //System.out.println( " SQUERY ======  :" + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientid = rs.getInt( 2 );
                    Integer dataelementid = rs.getInt( 3 );                
                    String facilitatorDataValue = rs.getString( 4 );
                    
                    if ( facilitatorDataValue != null )
                    {
                        ashaFacilitatorDataValueMap.put( dataelementid + ":" + patientid, facilitatorDataValue );
                    }
                }
                
            return ashaFacilitatorDataValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal FacilitatorId id or Period Id or Data Set Id", e );
        }
    }    
        
    
    
  
    
    
    
    public Map<Integer, String> getASHAFacilitatorDataValues( Integer facilitatorId, Integer patientId, Integer periodId )
    {
        Map<Integer, String> ashaFacilitatorDataValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT dataelementid, value FROM facilitatordatavalue  WHERE facilitatorid = " + facilitatorId + " AND patientid = " + patientId  + " AND periodid = " + periodId +" " ;
            
            //System.out.println( " SQUERY ======  :" + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer dataelementid = rs.getInt( 1 );                
                    String facilitatorDataValue = rs.getString( 2 );
                    
                    if ( facilitatorDataValue != null )
                    {
                        ashaFacilitatorDataValueMap.put( dataelementid, facilitatorDataValue );
                    }
                }
                
                //System.out.println( " Size of Facilitator Data Value Map inside service is ======  :" + ashaFacilitatorDataValueMap.size() ); 
                
            return ashaFacilitatorDataValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
    
    public Map<Integer, String> getASHAPerformanceScore( Integer facilitatorId, Integer periodId, Integer dataSetId )
    {
        Map<Integer, String> ashaFacilitatorPerformanceScoreValueMap = new HashMap<Integer, String>();

        try
        {
            
            String query = "SELECT asd.patientid,asd1.YesnNo AS 'Yes+No' ,asd.Yes FROM ( " +
                            "SELECT fv.patientid, COUNT(fv.value) AS 'Yes' FROM facilitatordatavalue fv " +
                            "INNER JOIN datasetmembers dsm ON fv.dataelementid=dsm.dataelementid " + 
                            "WHERE fv.facilitatorid = " + facilitatorId + "  AND fv.periodid = " + periodId + " AND dsm.datasetid = " + dataSetId + " AND fv.value  LIKE 'Yes' " +
                            "GROUP BY fv.patientid)asd " +
                            "INNER JOIN  (" +
                            "SELECT fv.patientid, COUNT(fv.value) AS 'YesnNo' FROM facilitatordatavalue fv " +
                            "INNER JOIN datasetmembers dsm ON fv.dataelementid=dsm.dataelementid " +
                            "WHERE fv.facilitatorid = " + facilitatorId + " AND fv.periodid = " + periodId +
                            " AND dsm.datasetid = " + dataSetId + " AND fv.value IN ('Yes','No') " +
                            "GROUP BY fv.patientid )asd1 ON asd.patientid=asd1.patientid";
            
            // String query = "SELECT dataelementid, value FROM facilitatordatavalue  WHERE facilitatorid = " + facilitatorId + " AND patientid = " + patientId  + " AND periodid = " + periodId +" " ;
            
            //System.out.println( " Performance QUERY ======  :" + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientid = rs.getInt( 1 );                
                    String yesno = rs.getString( 2 );
                    String yes = rs.getString( 3 );
                    
                    String performanceScore = yes +"/"+yesno;
                    
                    //System.out.println( patientid + " -- " + yesno +  " -- " + yes + " -- " +  performanceScore); 
                    
                    if ( ( yesno != null && yes != null ) && performanceScore != null )
                    {
                        ashaFacilitatorPerformanceScoreValueMap.put( patientid, performanceScore );
                    }
                }
                
                //System.out.println( " Size of Facilitator Data Value Map inside service is ======  :" + ashaFacilitatorDataValueMap.size() ); 
                
            return ashaFacilitatorPerformanceScoreValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
        
 
    
    
    public List<Integer> getMonthlyFacilitatorASHAList( Integer facilitatorId, Integer periodId )
    {
        List<Integer> patientList = new ArrayList<Integer>();

        try
        {
            String query = "SELECT DISTINCT(patientid) FROM facilitatordatavalue WHERE facilitatorid = " + facilitatorId + " AND periodid = " + periodId +" " ;
               
     
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientid = rs.getInt( 1 );                
                    
                    if ( patientid != null )
                    {
                        patientList.add( patientid );
                    }
                }
                
                //System.out.println( " Size of Facilitator Data Value Map inside service is ======  :" + ashaFacilitatorDataValueMap.size() ); 
                
            return patientList;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
        
        
    
    
    
    
    
    
    
    //--------------------------------------------------------------------------------
    // Get Patient Data COUNT by Patient OrgunitIds
    //--------------------------------------------------------------------------------
    public Set<Integer> getPatientListByDataCount( String orgUnitIdsByComma, String dataElementIdsByComma, String executionDate, Integer prograStageId )
    {
        Set<Integer> patientIds = new HashSet<Integer>();

        try
        {
            String query = "SELECT programinstance.patientid, SUM(value) FROM patientdatavalue " +
                            " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " +  
                            " INNER JOIN programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                            " INNER JOIN patient ON patient.patientid = programinstance.patientid " + 
                            " WHERE " +  
                                " patient.organisationunitid IN ( "+ orgUnitIdsByComma +" ) AND " + 
                                " patientdatavalue.dataelementid IN ( " + dataElementIdsByComma + " ) AND " + 
                                " programstageinstance.programstageid = "+ prograStageId + " AND " +  
                                " programstageinstance.executiondate = '"+ executionDate + "'" +
                                " GROUP BY programinstance.patientid"; 
                          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer patientId = rs.getInt( 1 );
                Integer patientValueCount = rs.getInt( 2 );
                
                if ( patientValueCount != null && patientValueCount > 0 )
                {
                    patientIds.add( patientId );
                }
            }
            
            return patientIds;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }
    
    // Get patient/ASHA List  By PerformanceScore
    public Set<Integer> getASHAListByPerformanceScore( Integer periodId, Integer dataSetId, String orgUnitIdsByComma )
    {
        Set<Integer> patientIds = new HashSet<Integer>();

        try
        {
            String query = "SELECT asd.patientid,asd1.YesnNo AS 'Yes+No' ,asd.Yes FROM ( " +
                            "SELECT fv.patientid, COUNT(fv.value) AS 'Yes' FROM facilitatordatavalue fv " +
                            "INNER JOIN datasetmembers dsm ON fv.dataelementid=dsm.dataelementid " +
                            "INNER JOIN patient p ON p.patientid = fv.patientid " +
                            "WHERE fv.periodid = " + periodId + " AND p.organisationunitid IN ( " + orgUnitIdsByComma + " ) " +
                            "AND dsm.datasetid = " + dataSetId + " AND fv.value  LIKE 'Yes' " +
                            "GROUP BY fv.patientid)asd " +
                            "INNER JOIN  (" +
                            "SELECT fv.patientid, COUNT(fv.value) AS 'YesnNo' FROM facilitatordatavalue fv " +
                            "INNER JOIN datasetmembers dsm ON fv.dataelementid=dsm.dataelementid " +
                            "INNER JOIN patient p ON p.patientid = fv.patientid " +
                            "WHERE fv.periodid = " + periodId + " AND p.organisationunitid IN ( " + orgUnitIdsByComma + " ) " +
                            "AND dsm.datasetid = " + dataSetId + " AND fv.value IN ('Yes','No') " +
                            "GROUP BY fv.patientid )asd1 ON asd.patientid=asd1.patientid";
            
            // String query = "SELECT dataelementid, value FROM facilitatordatavalue  WHERE facilitatorid = " + facilitatorId + " AND patientid = " + patientId  + " AND periodid = " + periodId +" " ;
            
            //System.out.println( " Performance QUERY ======  :" + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientId = rs.getInt( 1 );                
                    Integer yesno = rs.getInt( 2 );
                    Integer yes = rs.getInt( 3 );
                    
                    //String performanceScore = yes +"/"+yesno;
                    
                    //System.out.println( patientid + " -- " + yesno +  " -- " + yes + " -- " +  performanceScore); 
                    
                    if ( ( yesno != null && yes != null ) && ( yes == yesno ) )
                    {
                        //System.out.println( patientId + " -- " + yesno +  " -- " + yes );
                        patientIds.add( patientId );
                    }
                }
                
                //System.out.println( " Size of Facilitator Data Value Map inside service is ======  :" + ashaFacilitatorDataValueMap.size() ); 
                
            return patientIds;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
            
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    public Set<Integer> getPatientListByOrgunit( String orgUnitIdsByComma )
    {
        Set<Integer> patientIds = new HashSet<Integer>();

        try
        {
            String query = "SELECT patientid FROM patient " +
                            " WHERE " +  
                                " patient.organisationunitid IN ( "+ orgUnitIdsByComma +" )"; 
                          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer patientId = rs.getInt( 1 );
                
                patientIds.add( patientId );
            }
            
            return patientIds;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
    
    
    
    //--------------------------------------------------------------------------------
    // Get Latest Patient Data from PatientDataValue
    //--------------------------------------------------------------------------------
    public Map<Integer, List<String>> getLatestPatientData( Integer patientId, Integer programId, Integer programStageId, String executionDate )
    {
        //System.out.println(" Inside query");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Map<Integer, List<String>> patientDataValueMap = new HashMap<Integer,  List<String>>();

        try
        {
            String query = "SELECT dataelementid, VALUE, executiondate FROM patientdatavalue INNER JOIN programstageinstance ON patientdatavalue.programstageinstanceid = programstageinstance.programstageinstanceid " +
                             " WHERE CONCAT(dataelementid,',',executiondate) IN (SELECT CONCAT(pdv.dataelementid,',',MAX(psi.executiondate)) FROM patientdatavalue pdv " +
                             " INNER JOIN programstageinstance psi ON pdv.programstageinstanceid = psi.programstageinstanceid " +
                             " INNER JOIN programinstance pi ON  pi.programinstanceid = psi.programinstanceid " + 
                             " WHERE pi.patientid = "+ patientId + " AND pi.programid = "+ programId + " AND psi.programstageid = "+ programStageId + " AND psi.executiondate <= '"+ executionDate + "'" +
                             " GROUP BY dataelementid ) ORDER BY dataelementid"; 
             
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                String value  = rs.getString( 2 );
                Date dateValue = rs.getDate( 3 );
                
                //Date sDate = format.parseDate( rs.getString( 3 ) );
                
                String date  = simpleDateFormat.format( dateValue );
                
                if ( dataElementId != null && value != null && date != null )
                {
                    List<String> dataValue = new ArrayList<String>();
                    
                    dataValue.add( value );
                    dataValue.add( date );
                    
                    patientDataValueMap.put( dataElementId, dataValue );
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
    // Get Tariff Data Values by OrgUnit Id, DataSet Id, Start Date and End Date
    //--------------------------------------------------------------------------------
    public Map<Integer, Integer> getPerformanceIncentiveDataValues( Integer organisationUnitId, Integer dataSetId, Date startDate, Date endDate )
    {
        Map<Integer, Integer> performanceIncentiveDataValueMap = new HashMap<Integer, Integer>();
        
        try
        {
            String query = "SELECT dataelementid, value FROM tariffdatavalue  WHERE organisationunitid = " + organisationUnitId + " AND datasetid = " + dataSetId  +" AND " +   
                            " DATE(startdate) = '"+ startDate +"'" + " AND DATE(enddate) = '" + endDate + "'" ;
                
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
            while ( rs.next() )
            {
                Integer dataelementid = rs.getInt( 1 );                
                
                Integer dataValue = (int)rs.getDouble( 2 );
                
                if ( dataValue != null )
                {
                    performanceIncentiveDataValueMap.put( dataelementid , dataValue );
                }
            }
                
            return performanceIncentiveDataValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
    
        
    
    
    
    
    
    
    
    
    
    
    
    
    
    //--------------------------------------------------------------------------------
    // Get REPORT CELL from XML
    //--------------------------------------------------------------------------------
    public List<ReportCell> getReportCells( String xmlFilePath, String tagName )
    {
        List<ReportCell> reportCells = new ArrayList<ReportCell>();

        try 
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( xmlFilePath ) );
            if ( doc == null )
            {
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( tagName );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Element deCodeElement = ( Element ) listOfDECodes.item( s );
                
                String datatype = deCodeElement.getAttribute( "datatype" );
                String service = deCodeElement.getAttribute( "service" );
                Integer row = Integer.parseInt( deCodeElement.getAttribute( "row" ) );
                Integer col = Integer.parseInt( deCodeElement.getAttribute( "col" ) );
                
                ReportCell reportCell = new ReportCell( datatype, service, row, col );
                reportCells.add( reportCell );
                
            }// end of for loop with s var

        }// try block end
        catch ( SAXParseException err )
        {
        } 
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ( ( x == null ) ? e : x ).printStackTrace();
        } 
        catch ( Throwable t )
        {
            t.printStackTrace();
        }

        return reportCells;
    }

    public String getOrgunitBranch( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = "";

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " -> " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
    
    //--------------------------------------------------------------------------------
    // JEXCEL CELL FORMATS
    //--------------------------------------------------------------------------------
    
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );                        
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_50 );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );                        
        
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat3() throws Exception
    {
        //WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.NO_BOLD );
        //WritableCellFormat wCellformat = new WritableCellFormat( arialBold );                        
        
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

}
