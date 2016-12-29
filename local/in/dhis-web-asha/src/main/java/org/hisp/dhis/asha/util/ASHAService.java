package org.hisp.dhis.asha.util;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
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
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reports.ReportService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private BeneficiaryService beneficiaryService;

    public void setBeneficiaryService( BeneficiaryService beneficiaryService )
    {
        this.beneficiaryService = beneficiaryService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
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
    
    // get ProgramStageInstanceId from programInstanceId  programStageId and executiondate
    public Integer getProgramStageInstanceIdByPatient( Integer patientId, Integer programStageId, String executiondate )
    {
        Integer programStageInstanceId = null;
        
        try
        {
            String query = "SELECT psi.programstageinstanceid FROM programinstance ps INNER JOIN programstageinstance psi ON ps.programinstanceid = psi.programinstanceid "
                + "  WHERE ps.patientid = " + patientId + " AND programstageid = " + programStageId + " AND executiondate = '" + executiondate +"'";


            
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

    
    // get DataValueFromPatientDataValue from programStageInstanceId
    public Map<Integer, String> getDataValueFromPatientDataValueTable( Integer programStageInstanceId, String dataElementIdsByComma )
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
    
    
    // get DataValueFromPatientDataValue from programStageInstanceId
    public Map<Integer, String> getASHAActivityDataValueFromPatientDataValue( Integer programStageInstanceId )
    {
        Map<Integer, String> patientDataValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT dataelementid, value, moapprove, moremark, aaapprove, aaremark FROM patientdatavalue WHERE programstageinstanceid = " + programStageInstanceId;
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer dataelementid = rs.getInt( 1 );
                String dataValue = rs.getString( 2 ) + ":" + rs.getString( 3 ) +":" + rs.getString( 4 ) + ":" + rs.getString( 5 ) +":" + rs.getString( 6 ) ;
                
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
    
 
    
    // get ASHAActivity DataValue From PatientDataValue Not Approve ByMo InCurrentMonth from programStageInstanceId
    public Map<Integer, String> getASHAActivityDataValueFromPatientDataValueNotApproveByMoInCurrentMonth( Integer programStageInstanceId )
    {
        Map<Integer, String> patientDataValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT dataelementid, value, moapprove, moremark, aaapprove, aaremark FROM patientdatavalue WHERE programstageinstanceid = " + programStageInstanceId + " AND " +  
                            " moapprove != 3 ";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer dataelementid = rs.getInt( 1 );
                String dataValue = rs.getString( 2 ) + ":" + rs.getString( 3 ) +":" + rs.getString( 4 ) + ":" + rs.getString( 5 ) +":" + rs.getString( 6 ) ;
                
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
    
 
    
    
    
    
    
    
    
    // get ASHA Activity MORemark DataValue From Patient Data Value Not ApproveBy MO InCurrentMonth
    public Map<Integer, String> getASHAActivityMORemarkDataValueFromPatientDataValueNotApproveByMOInCurrentMonth( Integer programStageInstanceId )
    {
        Map<Integer, String> patientDataValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT dataelementid, value, moapprove, moremark FROM patientdatavalue WHERE programstageinstanceid = " + programStageInstanceId + " AND " +  
                " moapprove != 3 ";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer dataelementid = rs.getInt( 1 );
                String dataValue =  rs.getString( 4 ) ;
                
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
       
    
   
    // get DataValueFromPatientDataValue from programStageInstanceId
    public Map<Integer, String> getASHAActivityMORemarkDataValueFromPatientDataValue( Integer programStageInstanceId )
    {
        Map<Integer, String> patientDataValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT dataelementid, value, moapprove, moremark FROM patientdatavalue WHERE programstageinstanceid = " + programStageInstanceId;
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer dataelementid = rs.getInt( 1 );
                String dataValue =  rs.getString( 4 ) ;
                
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
    
    
    // get DataValueFromPatientDataValue from programStageInstanceId
    public Map<Integer, String> getASHAActivityAARemarkDataValueFromPatientDataValue( Integer programStageInstanceId )
    {
        Map<Integer, String> patientDataValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT dataelementid, value, aaapprove, aaremark FROM patientdatavalue WHERE programstageinstanceid = " + programStageInstanceId;
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer dataelementid = rs.getInt( 1 );
                String dataValue =  rs.getString( 4 ) ;
                
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
   

    
    
    //--------------------------------------------------------------------------------
    // Get ASHA List who payment is done in a particular month
    //--------------------------------------------------------------------------------
    
    
    public List<Patient> getPaymentDoneAAHAList( String organisationUnitIdsByComma, String dataElementIdsByComma, String executionDate,Integer programId, Integer prograStageId )
    {
        List<Patient> patientList = new ArrayList<Patient>();

        try
        {
            /*    
            String query = "SELECT programinstance.patientid, dataelementid, value FROM patientdatavalue " +
                            " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                            " INNER JOIN programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                            " WHERE " + 
                                " programinstance.programid = " + programId +" AND " + 
                                " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                " programinstance.patientid IN (" + patientIdsByComma + ") AND " + 
                                " patientdatavalue.dataelementid IN ( " + dataElementIdsByComma + " ) AND " + 
                                " programstageinstance.executiondate = '"+ executionDate +"'";
              
            */  

            String query =     " SELECT asd.patientid, asd.programstageinstanceid, pdv1.dataelementid, pdv1.value FROM  ( " +
                                " SELECT programinstance.patientid,dataelementid, patientdatavalue.programstageinstanceid FROM patientdatavalue " +
                                " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid  " +
                                " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " +
                                " INNER JOIN patient ON patient.patientid = programinstance.patientid " + 
                                " WHERE programinstance.programid = " + programId +"  AND programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                " patient.organisationunitid IN (" + organisationUnitIdsByComma + ") AND " + 
                                " patientdatavalue.dataelementid IN ( " + dataElementIdsByComma + " ) AND " + 
                                " programstageinstance.executiondate = '"+ executionDate +"' " + " AND VALUE LIKE 'true' " +
                                " )asd INNER JOIN patientdatavalue pdv1 ON pdv1.programstageinstanceid=asd.programstageinstanceid " +
                                " AND pdv1.dataelementid IN ( " + dataElementIdsByComma + " ) "; 
                                            
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientId = rs.getInt( 1 );
                    //Integer dataElementId = rs.getInt( 3 );                
                    String patientDataValue = rs.getString( 4 );
                    
                    if ( patientId != null && patientDataValue != null && patientDataValue.equalsIgnoreCase( "true" ) )
                    {
                        Patient patient = new Patient();
                        
                        patient = patientService.getPatient( patientId );
                        patientList.add( patient );
                    }
                    
                }
           
            return patientList;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
    
    
    
    //--------------------------------------------------------------------------------
    // Get ASHA List who payment is done in a particular month
    //--------------------------------------------------------------------------------
    public List<Patient> getPaymentDoneASHAListApproveByAA( String organisationUnitIdsByComma, String dataElementIdsByComma, String executionDate,Integer programId, Integer prograStageId )
    {
        List<Patient> patientList = new ArrayList<Patient>();

        try
        {
            /*    
            String query = "SELECT programinstance.patientid, dataelementid, value FROM patientdatavalue " +
                            " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                            " INNER JOIN programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                            " WHERE " + 
                                " programinstance.programid = " + programId +" AND " + 
                                " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                " programinstance.patientid IN (" + patientIdsByComma + ") AND " + 
                                " patientdatavalue.dataelementid IN ( " + dataElementIdsByComma + " ) AND " + 
                                " programstageinstance.executiondate = '"+ executionDate +"'";
              
            */  

            String query =     " SELECT asd.patientid, asd.programstageinstanceid, pdv1.dataelementid, pdv1.value FROM  ( " +
                                " SELECT programinstance.patientid,dataelementid, patientdatavalue.programstageinstanceid FROM patientdatavalue " +
                                " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid  " +
                                " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " +
                                " INNER JOIN patient ON patient.patientid = programinstance.patientid " + 
                                " WHERE programinstance.programid = " + programId +"  AND programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                " patient.organisationunitid IN (" + organisationUnitIdsByComma + ") AND " + 
                                " patientdatavalue.dataelementid IN ( " + dataElementIdsByComma + " ) AND " + 
                                " programstageinstance.executiondate = '"+ executionDate +"' " + " AND VALUE  > 0 " +
                                " )asd INNER JOIN patientdatavalue pdv1 ON pdv1.programstageinstanceid=asd.programstageinstanceid " +
                                " AND pdv1.dataelementid IN ( " + dataElementIdsByComma + " ) "; 
                                            
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientId = rs.getInt( 1 );
                    //Integer dataElementId = rs.getInt( 3 );                
                    //String patientDataValue = rs.getString( 4 );
                    Integer patientDataValue = rs.getInt( 4 );
                    if ( patientId != null && patientDataValue != null && patientDataValue > 0 )
                    {
                        Patient patient = new Patient();
                        
                        patient = patientService.getPatient( patientId );
                        patientList.add( patient );
                    }
                    
                }
           
            return patientList;
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
    // Get Patient Attribute Value by Patient Id and AttributeId
    //--------------------------------------------------------------------------------
    public String  getPatientAttributeValue( Integer patientId, Integer attributeId )
    {
        String attributeValue = new String();

        try
        {
            String query = "SELECT value FROM patientattributevalue " +
                            "WHERE  patientid  = " + patientId +" AND patientattributeid = "+ attributeId + " ";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                //Integer patientID = rs.getInt( 1 );
                                
                String patientAttributeValue = rs.getString( 1 );
                
                if ( patientAttributeValue != null )
                {
                    attributeValue = patientAttributeValue ;
                }
            }

            return attributeValue;
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
    // Get Pending Patient Data Values 
    //--------------------------------------------------------------------------------
    public Map<Date, Map<Integer, String>> getMOPendingPatientDataValues( Integer patientId, Integer programId, Integer prograStageId , Integer status, String tempExecutionDate  )
    {
        Map<Date, Map<Integer,String>> pendingPatientDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>();

        try
        {
                String query = "SELECT programinstance.patientid, programstageinstance.executiondate, dataelementid, VALUE, moapprove, moremark, aaapprove, aaremark FROM patientdatavalue " +
                                " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                                " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                                " WHERE " + 
                                    " programinstance.programid = " + programId +" AND " + 
                                    " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                    " programinstance.patientid = " + patientId + " AND " +
                                    " programstageinstance.executiondate < '"+ tempExecutionDate +"' AND " + 
                                    " moapprove = " + status + " ORDER BY programstageinstance.executiondate DESC ";
              
               
                //System.out.println( " query is --: " + query );
                
                //SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yyyy");
                
                SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                //System.out.println( "1." );
    
                while ( rs.next() )
                {
                    //String executionDate = rs.getString( 2 ).substring( 0, 10 );
                    Date executionDate = rs.getDate( 2 );
                    Integer dataElementId = rs.getInt( 3 );                
                    //String patientDataValue = rs.getString( 4 );
                    
                    String patientDataValue = rs.getString( 4 ) + ":" + rs.getString( 5 ) +":" + rs.getString( 6 ) ;
                    
                    //System.out.println( "2." );
                    
                    //System.out.println( "ExecutionDate : "  + executionDate );
                    
                    if ( patientDataValue != null && executionDate != null )
                    {
                        //String monthP = monthFormat.format( executionDate );
                        
                        //System.out.println( "3." );
                        
                        Map<Integer, String> deMap = pendingPatientDataValueMapByPeriod.get( executionDate );
                        
                        if ( deMap == null )
                        {
                            deMap = new HashMap<Integer, String>();
                        }
                        
                        //System.out.println( "4." );
                        deMap.put( dataElementId, patientDataValue );
                        pendingPatientDataValueMapByPeriod.put( executionDate, deMap );
                    }
                    
                }
                
                return pendingPatientDataValueMapByPeriod;
            
        }
        catch ( Exception e )
        {
            //System.out.println( "Exception in getMOPendingPatientDataValues : " + e.getMessage() );
            throw new RuntimeException( "Exception in getMOPendingPatientDataValues ", e );
        }
    }    

    
    public Map<Date, Map<Integer, String>> getMONotApprovePatientDataValues( Integer patientId, Integer programId, Integer prograStageId , String tempExecutionDate  )
    {
        Map<Date, Map<Integer,String>> pendingPatientDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>();

        try
        {
                String query = "SELECT programinstance.patientid, programstageinstance.executiondate, dataelementid, VALUE, moapprove, moremark, aaapprove, aaremark FROM patientdatavalue " +
                                " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                                " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                                " WHERE " + 
                                    " programinstance.programid = " + programId +" AND " + 
                                    " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                    " programinstance.patientid = " + patientId + " AND " +
                                    " programstageinstance.executiondate < '"+ tempExecutionDate +"' AND " + 
                                    " moapprove != 3 ORDER BY programstageinstance.executiondate DESC ";
              
               
                //System.out.println( " query is --: " + query );
                
                //SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yyyy");
                
                SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                //System.out.println( "1." );
    
                while ( rs.next() )
                {
                    //String executionDate = rs.getString( 2 ).substring( 0, 10 );
                    Date executionDate = rs.getDate( 2 );
                    Integer dataElementId = rs.getInt( 3 );                
                    //String patientDataValue = rs.getString( 4 );
                    
                    String patientDataValue = rs.getString( 4 ) + ":" + rs.getString( 5 ) +":" + rs.getString( 6 ) + ":" + rs.getString( 7 ) +":" + rs.getString( 8 ) ;
                    
                    //System.out.println( "2." );
                    
                    //System.out.println( "ExecutionDate : "  + executionDate );
                    
                    if ( patientDataValue != null && executionDate != null )
                    {
                        //String monthP = monthFormat.format( executionDate );
                        
                        //System.out.println( "3." );
                        
                        Map<Integer, String> deMap = pendingPatientDataValueMapByPeriod.get( executionDate );
                        
                        if ( deMap == null )
                        {
                            deMap = new HashMap<Integer, String>();
                        }
                        
                        //System.out.println( "4." );
                        deMap.put( dataElementId, patientDataValue );
                        pendingPatientDataValueMapByPeriod.put( executionDate, deMap );
                    }
                    
                }
                
                return pendingPatientDataValueMapByPeriod;
            
        }
        catch ( Exception e )
        {
            //System.out.println( "Exception in getMOPendingPatientDataValues : " + e.getMessage() );
            throw new RuntimeException( "Exception in getMOPendingPatientDataValues ", e );
        }
    }        
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public Map<Date, Map<Integer, String>> getMOApprovePendingPatientDataValues( Integer patientId, Integer programId, Integer prograStageId , String tempExecutionDate  )
    {
        Map<Date, Map<Integer,String>> pendingPatientDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>();

        try
        {
                String query = "SELECT programinstance.patientid, programstageinstance.executiondate, dataelementid, VALUE, moapprove, moremark, aaapprove, aaremark FROM patientdatavalue " +
                                " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                                " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                                " WHERE " + 
                                    " programinstance.programid = " + programId +" AND " + 
                                    " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                    " programinstance.patientid = " + patientId + " AND " +
                                    " programstageinstance.executiondate < '"+ tempExecutionDate +"' AND " + 
                                    " moapprove = 3 AND aaapprove != 3 ORDER BY programstageinstance.executiondate DESC ";
              
               
                //System.out.println( " query is --: " + query );
                
                //SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yyyy");
                
                SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                //System.out.println( "1." );
    
                while ( rs.next() )
                {
                    //String executionDate = rs.getString( 2 ).substring( 0, 10 );
                    Date executionDate = rs.getDate( 2 );
                    Integer dataElementId = rs.getInt( 3 );                
                    //String patientDataValue = rs.getString( 4 );
                    
                    String patientDataValue = rs.getString( 4 ) + ":" + rs.getString( 5 ) +":" + rs.getString( 6 ) + ":" + rs.getString( 7 ) +":" + rs.getString( 8 ) ;
                    
                    //System.out.println( "2." );
                    
                    //System.out.println( "ExecutionDate : "  + executionDate );
                    
                    if ( patientDataValue != null && executionDate != null )
                    {
                        //String monthP = monthFormat.format( executionDate );
                        
                        //System.out.println( "3." );
                        
                        Map<Integer, String> deMap = pendingPatientDataValueMapByPeriod.get( executionDate );
                        
                        if ( deMap == null )
                        {
                            deMap = new HashMap<Integer, String>();
                        }
                        
                        //System.out.println( "4." );
                        deMap.put( dataElementId, patientDataValue );
                        pendingPatientDataValueMapByPeriod.put( executionDate, deMap );
                    }
                    
                }
                
                return pendingPatientDataValueMapByPeriod;
            
        }
        catch ( Exception e )
        {
            //System.out.println( "Exception in getMOPendingPatientDataValues : " + e.getMessage() );
            throw new RuntimeException( "Exception in getMOPendingPatientDataValues ", e );
        }
    }    

    
    public Map<Date, Map<Integer, String>> getMOPendingRemarkPatientDataValues( Integer patientId, Integer programId, Integer prograStageId , Integer status, String tempExecutionDate  )
    {
        Map<Date, Map<Integer,String>> pendingPatientDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>();

        try
        {
                String query = "SELECT programinstance.patientid, programstageinstance.executiondate, dataelementid, VALUE, moapprove, moremark, aaapprove, aaremark FROM patientdatavalue " +
                                " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                                " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                                " WHERE " + 
                                    " programinstance.programid = " + programId +" AND " + 
                                    " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                    " programinstance.patientid = " + patientId + " AND " +
                                    " programstageinstance.executiondate < '"+ tempExecutionDate +"' AND " + 
                                    " moapprove = " + status + " ORDER BY programstageinstance.executiondate DESC ";
              
               
                //System.out.println( " query is --: " + query );
                
                //SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yyyy");
                
                SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                //System.out.println( "1." );
    
                while ( rs.next() )
                {
                    //String executionDate = rs.getString( 2 ).substring( 0, 10 );
                    Date executionDate = rs.getDate( 2 );
                    Integer dataElementId = rs.getInt( 3 );                
                    //String patientDataValue = rs.getString( 4 );
                    
                    String patientDataValue = rs.getString( 6 ) ;
                    
                    //System.out.println( "2." );
                    
                    //System.out.println( "ExecutionDate : "  + executionDate );
                    
                    if ( patientDataValue != null && executionDate != null )
                    {
                        //String monthP = monthFormat.format( executionDate );
                        
                        //System.out.println( "3." );
                        
                        Map<Integer, String> deMap = pendingPatientDataValueMapByPeriod.get( executionDate );
                        
                        if ( deMap == null )
                        {
                            deMap = new HashMap<Integer, String>();
                        }
                        
                        //System.out.println( "4." );
                        deMap.put( dataElementId, patientDataValue );
                        pendingPatientDataValueMapByPeriod.put( executionDate, deMap );
                    }
                    
                }
                
                return pendingPatientDataValueMapByPeriod;
            
        }
        catch ( Exception e )
        {
            //System.out.println( "Exception in getMOPendingPatientDataValues : " + e.getMessage() );
            throw new RuntimeException( "Exception in getMOPendingPatientDataValues ", e );
        }
    }    
    
    
    public Map<Date, Map<Integer, String>> getAAPendingRemarkPatientDataValues( Integer patientId, Integer programId, Integer prograStageId, String tempExecutionDate  )
    {
        Map<Date, Map<Integer,String>> pendingPatientDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>();

        try
        {
                String query = "SELECT programinstance.patientid, programstageinstance.executiondate, dataelementid, VALUE, moapprove, moremark, aaapprove, aaremark FROM patientdatavalue " +
                                " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                                " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                                " WHERE " + 
                                    " programinstance.programid = " + programId +" AND " + 
                                    " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                    " programinstance.patientid = " + patientId + " AND " +
                                    " programstageinstance.executiondate < '"+ tempExecutionDate +"' " + 
                                    " ORDER BY programstageinstance.executiondate DESC ";
              
               
                //System.out.println( " query is --: " + query );
                
                //SimpleDateFormat monthFormat = new SimpleDateFormat("MMM-yyyy");
                
                SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                //System.out.println( "1." );
    
                while ( rs.next() )
                {
                    //String executionDate = rs.getString( 2 ).substring( 0, 10 );
                    Date executionDate = rs.getDate( 2 );
                    Integer dataElementId = rs.getInt( 3 );                
                    //String patientDataValue = rs.getString( 4 );
                    
                    String patientDataValue = rs.getString( 8 ) ;
                    
                    //System.out.println( "2." );
                    
                    //System.out.println( "ExecutionDate : "  + executionDate );
                    
                    if ( patientDataValue != null && executionDate != null )
                    {
                        //String monthP = monthFormat.format( executionDate );
                        
                        //System.out.println( "3." );
                        
                        Map<Integer, String> deMap = pendingPatientDataValueMapByPeriod.get( executionDate );
                        
                        if ( deMap == null )
                        {
                            deMap = new HashMap<Integer, String>();
                        }
                        
                        //System.out.println( "4." );
                        deMap.put( dataElementId, patientDataValue );
                        pendingPatientDataValueMapByPeriod.put( executionDate, deMap );
                    }
                    
                }
                
                return pendingPatientDataValueMapByPeriod;
            
        }
        catch ( Exception e )
        {
            //System.out.println( "Exception in getMOPendingPatientDataValues : " + e.getMessage() );
            throw new RuntimeException( "Exception in getMOPendingPatientDataValues ", e );
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
        
        
    // getYesCountSumForFacilitator
    public  Map<String, Integer> getYesCountSumForFacilitator( String facilitatorIdsByComma, String periodIdsByComma, Integer dataSetId )
    {
        Map<String, Integer> yesCountSumForFacilitatorValueMap = new HashMap<String, Integer>();

        try
        {
           // String query = "SELECT DISTINCT(patientid) FROM facilitatordatavalue WHERE facilitatorid = " + facilitatorId + " AND periodid = " + periodId +" " ;
               
            String query =  "SELECT asd.facilitatorid ,asd.dataelementid , asd.periodid , SUM(asd.value) AS 'value' FROM ( " +
                            " SELECT fv.facilitatorid ,fv.patientid, fv.dataelementid, fv.periodid, CASE  " +
                            " WHEN fv.value LIKE 'Yes' THEN 1  WHEN fv.value LIKE 'No' THEN 0 END AS 'value' " +
                            " FROM facilitatordatavalue fv INNER JOIN datasetmembers dsm ON fv.dataelementid=dsm.dataelementid " +
                            " WHERE fv.facilitatorid IN ( "+ facilitatorIdsByComma +" ) AND fv.periodid IN ( "+ periodIdsByComma +" ) " +
                            " AND dsm.datasetid = "+ dataSetId + " AND fv.value NOT LIKE 'NA' " + 
                            " )asd GROUP BY asd.facilitatorid ,asd.dataelementid,asd.periodid ";
            
            //System.out.println( " query ======  :" + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer facilitatorId = rs.getInt( 1 );
                    Integer dataelementId = rs.getInt( 2 );
                    Integer periodId = rs.getInt( 3 );
                    Integer value = rs.getInt( 4 );
                    
                    if ( value != null )
                    {
                        yesCountSumForFacilitatorValueMap.put( facilitatorId + ":" + dataelementId + ":" + periodId , value );
                    }
                }
                
                //System.out.println( " Size of Facilitator Data Value Map inside service is ======  :" + ashaFacilitatorDataValueMap.size() ); 
                
            return yesCountSumForFacilitatorValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal facilitator id ", e );
        }
    } 
    
    
    // getNoCountSumForFacilitator
    public  Map<String, Integer> getNoCountSumForFacilitator( String facilitatorIdsByComma, String periodIdsByComma, Integer dataSetId )
    {
        Map<String, Integer> noCountSumForFacilitatorValueMap = new HashMap<String, Integer>();

        try
        {
           // String query = "SELECT DISTINCT(patientid) FROM facilitatordatavalue WHERE facilitatorid = " + facilitatorId + " AND periodid = " + periodId +" " ;
               
            String query =  "SELECT asd.facilitatorid ,asd.dataelementid , asd.periodid , SUM(asd.value) AS 'value' FROM ( " +
                            " SELECT fv.facilitatorid ,fv.patientid, fv.dataelementid, fv.periodid, CASE  " +
                            " WHEN fv.value LIKE 'No' THEN 1  WHEN fv.value LIKE 'Yes' THEN 0 END AS 'value' " +
                            " FROM facilitatordatavalue fv INNER JOIN datasetmembers dsm ON fv.dataelementid=dsm.dataelementid " +
                            " WHERE fv.facilitatorid IN ( "+ facilitatorIdsByComma +" ) AND fv.periodid IN ( "+ periodIdsByComma +" ) " +
                            " AND dsm.datasetid = "+ dataSetId + " AND fv.value NOT LIKE 'NA' " + 
                            " )asd GROUP BY asd.facilitatorid ,asd.dataelementid,asd.periodid ";
            
            //System.out.println( " query ======  :" + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer facilitatorId = rs.getInt( 1 );
                    Integer dataelementId = rs.getInt( 2 );
                    Integer periodId = rs.getInt( 3 );
                    Integer value = rs.getInt( 4 );
                    
                    if ( value != null )
                    {
                        noCountSumForFacilitatorValueMap.put( facilitatorId + ":" + dataelementId + ":" + periodId , value );
                    }
                }
                
                //System.out.println( " Size of Facilitator Data Value Map inside service is ======  :" + ashaFacilitatorDataValueMap.size() ); 
                
            return noCountSumForFacilitatorValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal facilitator id ", e );
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
    
    
    
    // getPatientYesCountByDataElement
    public  Map<Integer, Integer> getPatientYesCountByDataElement( Integer periodId, Integer dataSetId, String orgUnitIdsByComma )
    {
        Map<Integer, Integer> yesPatientCountMap = new HashMap<Integer, Integer>();

        try
        {
           // String query = "SELECT DISTINCT(patientid) FROM facilitatordatavalue WHERE facilitatorid = " + facilitatorId + " AND periodid = " + periodId +" " ;
               
            String query = "SELECT fv.dataelementid, COUNT(fv.patientid) AS 'patientCount', COUNT(fv.value) AS 'yesCount' FROM facilitatordatavalue fv " +
                            " INNER JOIN datasetmembers dsm ON fv.dataelementid=dsm.dataelementid " +
                            " INNER JOIN patient p ON p.patientid = fv.patientid " +
                            " WHERE fv.periodid = "+ periodId + " AND p.organisationunitid IN ( "+ orgUnitIdsByComma +"  ) " +
                            " AND dsm.datasetid = "+ dataSetId + " AND fv.value  LIKE 'Yes' " +
                            " GROUP BY fv.dataelementid " ;
            
            
            //System.out.println( " query ======  :" + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
            while ( rs.next() )
            {
                Integer dataelementId = rs.getInt( 1 );
                Integer patientCount = rs.getInt( 2 );
                Integer yesCount = rs.getInt( 3 );
                
                if ( patientCount != null && yesCount != null )
                {
                    yesPatientCountMap.put( dataelementId  , patientCount );
                }
            }
                
                //System.out.println( " Size of Facilitator Data Value Map inside service is ======  :" + ashaFacilitatorDataValueMap.size() ); 
                
            return yesPatientCountMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal period Id dataSet Id ", e );
        }
    }     
    
    
    
    // ASHA Grade Percentage Map
    public Map<OrganisationUnit, Integer> ashaGradePercentageMap( List<OrganisationUnit> orgUnitList, Integer periodId, Integer dataSetId )
    {
        Map<OrganisationUnit, Integer> patientGradePercentageMap = new HashMap<OrganisationUnit, Integer>();
        
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            List<OrganisationUnit> tempOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
            Collection<Integer> tempOrgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, tempOrgUnitList ) );
            String orgUnitIdsByComma = getCommaDelimitedString( tempOrgUnitIds );
            
            Set<Integer> performancePatientInCHC = new HashSet<Integer>( getASHAListByPerformanceScore( periodId, dataSetId, orgUnitIdsByComma  ) );
            
            //Integer patientCount = 0;
            Set<Integer> totalPatientInCHC = new HashSet<Integer>( getPatientListByOrgunit( orgUnitIdsByComma ) );
            
            //System.out.println( orgUnit.getName() + " Temp Patient Count -- " + tempPatientIds.size() + "  Patient Count -- " + patientIds.size()  );
              
            try
            {
                double gradePercentage = ( (double) performancePatientInCHC.size() / (double) totalPatientInCHC.size() ) * 100.0;
                
                gradePercentage = Math.round( gradePercentage * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 ); 
                
                patientGradePercentageMap.put( orgUnit, (int) gradePercentage );
                //System.out.println( orgUnit.getName() + " Patient Count -- " + performancePatientInCHC.size() + " Patient Size -- " + totalPatientInCHC.size() +  " Grade Percentage in try -- " + gradePercentage );
                
            }
            catch( Exception e )
            {
                patientGradePercentageMap.put( orgUnit, 0 );
                //System.out.println( orgUnit.getName() + " Patient Count -- " + performancePatientInCHC.size() + " Patient Size -- " + totalPatientInCHC.size() +  " In catch ");
            }
        }
        
        return patientGradePercentageMap;
    }
    
    // ASHA Grade Percentage Map
    public Map<OrganisationUnit, String> organisationUnitGradeMap( List<OrganisationUnit> orgUnitList, Integer periodId, Integer dataSetId )
    {
        Map<OrganisationUnit, String> orgUnitGradeMap = new HashMap<OrganisationUnit, String>();
        
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            List<OrganisationUnit> tempOrgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
            Collection<Integer> tempOrgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, tempOrgUnitList ) );
            String orgUnitIdsByComma = getCommaDelimitedString( tempOrgUnitIds );
            
            Set<Integer> performancePatientInCHC = new HashSet<Integer>( getASHAListByPerformanceScore( periodId, dataSetId, orgUnitIdsByComma  ) );
            
            //Integer patientCount = 0;
            Set<Integer> totalPatientInCHC = new HashSet<Integer>( getPatientListByOrgunit( orgUnitIdsByComma ) );
            
            //System.out.println( orgUnit.getName() + " Temp Patient Count -- " + tempPatientIds.size() + "  Patient Count -- " + patientIds.size()  );
              
            try
            {
                double gradePercentage = ( (double) performancePatientInCHC.size() / (double) totalPatientInCHC.size() ) * 100.0;
                
                gradePercentage = Math.round( gradePercentage * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 ); 
                
                if( gradePercentage >= 76 )
                {
                    orgUnitGradeMap.put( orgUnit, "A" );
                }
                
                else if( gradePercentage >= 51 && gradePercentage <= 75 )
                {
                    orgUnitGradeMap.put( orgUnit, "B" );
                }
                else if( gradePercentage >= 26 && gradePercentage <= 50 )
                {
                    orgUnitGradeMap.put( orgUnit, "C" );
                }
                else
                {
                    orgUnitGradeMap.put( orgUnit, "D" );
                }
                
            }
            catch( Exception e )
            {
                orgUnitGradeMap.put( orgUnit, "D" );
            }
        }
        
        return orgUnitGradeMap;
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
                
                NodeList textDECodeList = deCodeElement.getChildNodes();
                
                String expression = ((Node) textDECodeList.item( 0 )).getNodeValue().trim();
                
                String datatype = deCodeElement.getAttribute( "datatype" );
                String service = deCodeElement.getAttribute( "service" );
                Integer row = Integer.parseInt( deCodeElement.getAttribute( "row" ) );
                Integer col = Integer.parseInt( deCodeElement.getAttribute( "col" ) );
                
                ReportCell reportCell = new ReportCell( datatype, service, row, col, expression );
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
    
    
    // get TotalAmountInMonthFromBeneficiary
    public Double getTotalAmountInMonthFromBeneficiary( Integer patientId, Integer periodId )
    {
        Double totalAmountInMonth = null;
        
        try
        {
            String query = "SELECT SUM( price ) FROM beneficiary WHERE patientid = " + patientId + " AND "
                + " periodid = " + periodId + " AND dataelementid != 110 ";

            //System.out.println( " Query -- " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                Double value = rs.getDouble( 1 ) ;
                
                if ( value != null )
                {
                    totalAmountInMonth = value ;
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        //System.out.println( " Total Amount In Month -- " + totalAmountInMonth  + " State total Amount -- " + totalAmountInMonth/2 );
        
        return totalAmountInMonth;
    }
    
    
    // get TotalAmountInMonthFromBeneficiary
    public Double getTotalAmountInMonthFromBeneficiaryApprovedByMO( Integer patientId, Integer periodId )
    {
        Double totalAmountInMonth = null;
        
        try
        {
            String query = "SELECT SUM( price ) FROM beneficiary WHERE patientid = " + patientId + " AND "
                + " periodid = " + periodId + " AND moapprove = 3 ";

            //System.out.println( " Query -- " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                Double value = rs.getDouble( 1 ) ;
                
                if ( value != null )
                {
                    totalAmountInMonth = value ;
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        //System.out.println( " Total Amount In Month -- " + totalAmountInMonth  + " State total Amount -- " + totalAmountInMonth/2 );
        
        return totalAmountInMonth;
    }    
    
    
    // get TotalAmountInMonthFromBeneficiary
    public Double getTotalAmountInMonthFromBeneficiaryApprovedByAA( Integer patientId, Integer periodId )
    {
        Double totalAmountInMonth = null;
        
        try
        {
            String query = "SELECT SUM( price ) FROM beneficiary WHERE patientid = " + patientId + " AND "
                + " periodid = " + periodId + " AND aaapprove = 3 ";

            //System.out.println( " Query -- " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                Double value = rs.getDouble( 1 ) ;
                
                if ( value != null )
                {
                    totalAmountInMonth = value ;
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        //System.out.println( " Total Amount In Month -- " + totalAmountInMonth  + " State total Amount -- " + totalAmountInMonth/2 );
        
        return totalAmountInMonth;
    }    
       
    
    
    // get TotalAmountInMonthFromBeneficiary
    public Double getTotalApproveAmountInMonth( Integer patientId, Integer periodId )
    {
        Double totalAmountInMonth = null;
        
        try
        {
            String query = "SELECT SUM( price ) FROM beneficiary WHERE patientid = " + patientId + " AND "
                + " periodid = " + periodId + " AND moapprove = 3 AND aaapprove = 3 ";

            //System.out.println( " Query -- " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                Double value = rs.getDouble( 1 ) ;
                
                if ( value != null )
                {
                    totalAmountInMonth = value ;
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        //System.out.println( " Total Amount In Month -- " + totalAmountInMonth  + " State total Amount -- " + totalAmountInMonth/2 );
        
        return totalAmountInMonth;
    }
    
    // get all Beneficiary Not approve by MO
    public Collection<Beneficiary> getAllBeneficiaryNotApproveByMOInCurrentMonth( Patient patient, Period period )
    {
        List<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
    
        try
        {
            String query = "SELECT ben.beneficiaryid, ben.name FROM beneficiary ben " +
                " WHERE ben.patientid = "+ patient.getId() + " AND ben.periodid =  "+ period.getId() +  
                    " AND ben.moapprove != 3 " +  
                    " ORDER BY ben.name ASC "; 
            
            //System.out.println( " query -- " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer beneficiaryId = rs.getInt( 1 );                
                    
                    if ( beneficiaryId != null )
                    {
                        Beneficiary beneficiary = beneficiaryService.getBeneficiaryById( beneficiaryId );
                        beneficiaryList.add( beneficiary );
                    }
                }
                
            return beneficiaryList;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }    
    
    
    
    
    
    
    
    
    
    public Collection<Beneficiary> getAllBeneficiaryPendingByMO( Patient patient, Integer status, String date )
    {
        List<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();

        try
        {   
            /*
            String query = "SELECT ben.beneficiaryid,p.startdate FROM beneficiary ben " +
                " INNER JOIN period p ON ben.periodid = p.periodid " +  
                " WHERE ben.patientid = "+ patient.getId() + " AND "  +  
                    " ben.moapprove = "+ status + " AND " +  
                    " p.startdate < '"+ date + "'" + " ORDER BY p.startdate DESC "; 
            */
            
            String query = "SELECT ben.beneficiaryid,p.startdate FROM beneficiary ben " +
                " INNER JOIN period p ON ben.periodid = p.periodid " +  
                " WHERE ben.patientid = "+ patient.getId() + " AND "  +  
                    " ben.moapprove != 3 AND " +  
                    " p.startdate < '"+ date + "'" + " ORDER BY p.startdate DESC "; 
            
            
            
            
            //System.out.println( " query -- " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer beneficiaryId = rs.getInt( 1 );                
                    
                    if ( beneficiaryId != null )
                    {
                        Beneficiary beneficiary = beneficiaryService.getBeneficiaryById( beneficiaryId );
                        beneficiaryList.add( beneficiary );
                    }
                }
                
            return beneficiaryList;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }
    

    
    public Collection<Beneficiary> getAllBeneficiaryPendingByAAApproveByMO( Patient patient, String date )
    {
        List<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
    
        try
        {
            String query = "SELECT ben.beneficiaryid,p.startdate FROM beneficiary ben " +
                " INNER JOIN period p ON ben.periodid = p.periodid " +  
                " WHERE ben.patientid = "+ patient.getId() + " AND "  +  
                    " ben.aaapprove != 3 AND ben.moapprove = 3 AND " +  
                    " p.startdate < '"+ date + "'" + " ORDER BY p.startdate DESC "; 
            
            //System.out.println( " query -- " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer beneficiaryId = rs.getInt( 1 );                
                    
                    if ( beneficiaryId != null )
                    {
                        Beneficiary beneficiary = beneficiaryService.getBeneficiaryById( beneficiaryId );
                        beneficiaryList.add( beneficiary );
                    }
                }
                
            return beneficiaryList;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }

    public Collection<Beneficiary> getAllBeneficiaryPendingByAAApproveByMOCurrentMonth( Patient patient, Period period )
    {
        List<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();
    
        try
        {
            String query = "SELECT ben.beneficiaryid, ben.name FROM beneficiary ben " +
                " WHERE ben.patientid = "+ patient.getId() + " AND ben.periodid =  "+ period.getId() +  
                    " AND ben.aaapprove != 3 AND ben.moapprove = 3 " +  
                    " ORDER BY ben.name ASC "; 
            
            //System.out.println( " query -- " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer beneficiaryId = rs.getInt( 1 );                
                    
                    if ( beneficiaryId != null )
                    {
                        Beneficiary beneficiary = beneficiaryService.getBeneficiaryById( beneficiaryId );
                        beneficiaryList.add( beneficiary );
                    }
                }
                
            return beneficiaryList;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }
   
    
    
    
    
    // get Organisation unit Level 
    public Map<Integer, Integer> getOrgunitLevelMap()
    {
        Map<Integer, Integer> orgUnitLevelMap = new HashMap<Integer, Integer>();
        try
        {
            String query = "SELECT organisationunitid,level FROM _orgunitstructure";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer level = rs.getInt( 2 );

                orgUnitLevelMap.put( orgUnitId, level );
            }

            return orgUnitLevelMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }
    
    
    public Map<String, String> getPatientDataValuesByExecutionDate( String patientIdsByComma, Integer programId, Integer prograStageId, String executionDate, String dataElementIdsByComma )
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
                                " patientdatavalue.dataelementid IN (" + dataElementIdsByComma + ") AND " + 
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
    
    // Budget Utilization Amount SUM of dataElement for Any Other
    public Map<Integer, String> getSumAmountFromPatientDataValuesByExecutionDate( String patientIdsByComma, Integer programId, Integer prograStageId, String executionDate, String dataElementIdsByComma )
    {
        Map<Integer, String> patientDataValueMap = new HashMap<Integer, String>();

        try
        {
          
            String query = "SELECT programinstance.patientid, SUM(VALUE) FROM patientdatavalue " +
                            " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                            " INNER JOIN programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                            " WHERE " + 
                                " programinstance.programid = " + programId +" AND " + 
                                " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                " programinstance.patientid IN (" + patientIdsByComma + ") AND " +
                                " patientdatavalue.dataelementid IN (" + dataElementIdsByComma + ") AND " + 
                                " programstageinstance.executiondate = '"+ executionDate +"' GROUP BY programinstance.patientid";
              
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
 
            while ( rs.next() )
            {
                Integer patientId = rs.getInt( 1 );
                String patientDataValue = rs.getString( 2 );
                
                if ( patientDataValue != null )
                {
                    patientDataValueMap.put( patientId, patientDataValue );
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
    // Get get ASHA Service Wise Final(AA) Approve Amount
    //--------------------------------------------------------------------------------
    public Map<String, Double> getASHAServiceWiseFinalApproveAmount( Integer periodId )
    {
        Map<String, Double> ashaServiceApproveAmountMap = new HashMap<String, Double>();
        /*
        " SELECT patientid, dataelementgroupid, SUM( IFNULL(price,0) ) FROM beneficiary " +
        " WHERE periodid= " + periodId + " AND moapprove = 3 AND aaapprove = 3 " + 
        " GROUP BY patientid ,dataelementgroupid ";
        */
        try
        {
            /*    
            String query = " SELECT patientid, dataelementgroupid, SUM( price ) FROM beneficiary " +
                           " WHERE periodid= " + periodId + " AND moapprove = 3 AND aaapprove = 3 " + 
                           " GROUP BY patientid ,dataelementgroupid "; 
                              
             */
            
            
            String query = " SELECT patientid, dataelementgroupid, SUM( price ) FROM beneficiary " +
                " WHERE periodid= " + periodId + 
                " GROUP BY patientid ,dataelementgroupid "; 
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientId = rs.getInt( 1 );
                    Integer dataelementgroupid = rs.getInt( 2 );                
                    Double amount = rs.getDouble( 3 );
                    //String amount = rs.getString( 3 );
                    if ( amount != null )
                    {
                        ashaServiceApproveAmountMap.put( patientId+":"+ dataelementgroupid, amount );
                    }
                }
            
            return ashaServiceApproveAmountMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal Period Id ", e );
        }
    }    
    
    

    
    //--------------------------------------------------------------------------------
    // Get get ASHA Service Wise Final(AA) Approve Amount
    //--------------------------------------------------------------------------------
    public Map<String, Double> getASHAFinalServiceWiseApproveAmount( Integer periodId )
    {
        Map<String, Double> ashaServiceApproveAmountMap = new HashMap<String, Double>();
        /*
        " SELECT patientid, dataelementgroupid, SUM( IFNULL(price,0) ) FROM beneficiary " +
        " WHERE periodid= " + periodId + " AND moapprove = 3 AND aaapprove = 3 " + 
        " GROUP BY patientid ,dataelementgroupid ";
        */
        try
        {
            /*    
            String query = " SELECT patientid, dataelementgroupid, SUM( price ) FROM beneficiary " +
                           " WHERE periodid= " + periodId + " AND moapprove = 3 AND aaapprove = 3 " + 
                           " GROUP BY patientid ,dataelementgroupid "; 
                              
             
            
            SELECT patientid, dataelementid, SUM( price ) FROM beneficiary 
            WHERE periodid= 45 AND moapprove = 3 AND aaapprove = 3
            GROUP BY patientid ,dataelementid
            */
            
            
            String query = " SELECT patientid, dataelementid, SUM( price ) FROM beneficiary " +
                " WHERE periodid= " + periodId + " AND moapprove = 3 AND aaapprove = 3 " + 
                " GROUP BY patientid ,dataelementid "; 
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                
                while ( rs.next() )
                {
                    Integer patientId = rs.getInt( 1 );
                    Integer dataelementId = rs.getInt( 2 );                
                    Double amount = rs.getDouble( 3 );
                    //String amount = rs.getString( 3 );
                    if ( amount != null )
                    {
                        ashaServiceApproveAmountMap.put( patientId+":"+ dataelementId, amount );
                    }
                }
            
            return ashaServiceApproveAmountMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal Period Id ", e );
        }
    }    
        
    
    // Activites values
    public Map<String, String> getApprovedDataFromPatientDataValuesByExecutionDate( String patientIdsByComma, Integer programId, Integer prograStageId, String executionDate, String dataElementIdsByComma )
    {
        Map<String, String> patientDataValueMap = new HashMap<String, String>();

        try
        {
          
            String query = "SELECT programinstance.patientid, patientdatavalue.dataelementid,patientdatavalue.VALUE FROM patientdatavalue " +
                            " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                            " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                            " WHERE " + 
                                " programinstance.programid = " + programId +" AND  patientdatavalue.moapprove =3 AND patientdatavalue.aaapprove = 3 AND  " + 
                                " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                " programinstance.patientid IN (" + patientIdsByComma + ") AND " +
                                " patientdatavalue.dataelementid IN (" + dataElementIdsByComma + ") AND " + 
                                " programstageinstance.executiondate = '"+ executionDate +"'";
             
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
 
            while ( rs.next() )
            {
                Integer patientId = rs.getInt( 1 );
                Integer dataelementId = rs.getInt( 2 );  
                String patientDataValue = rs.getString( 3 );
                
                if ( patientDataValue != null )
                {
                    patientDataValueMap.put( patientId+":"+ dataelementId, patientDataValue );
                }
            }
            
            
            return patientDataValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }        
    
    // Activites value
    public Map<String, String> getActivitesDataFromPatientDataValuesByExecutionDate( String patientIdsByComma, Integer programId, Integer prograStageId, String executionDate, String dataElementIdsByComma )
    {
        Map<String, String> patientDataValueMap = new HashMap<String, String>();

        try
        {
          
            String query = "SELECT programinstance.patientid, patientdatavalue.dataelementid,patientdatavalue.VALUE FROM patientdatavalue " +
                            " INNER JOIN programstageinstance ON programstageinstance.programstageinstanceid = patientdatavalue.programstageinstanceid " + 
                            " INNER JOIN programinstance ON programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                            " WHERE " + 
                                " programinstance.programid = " + programId +" AND  patientdatavalue.moapprove =3 AND patientdatavalue.aaapprove = 3 AND  " + 
                                " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                                " programinstance.patientid IN (" + patientIdsByComma + ") AND " +
                                " patientdatavalue.dataelementid IN (" + dataElementIdsByComma + ") AND " + 
                                " programstageinstance.executiondate = '"+ executionDate +"'";
             
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
 
            while ( rs.next() )
            {
                Integer patientId = rs.getInt( 1 );
                Integer dataelementId = rs.getInt( 2 );  
                String patientDataValue = rs.getString( 3 );
                
                if ( patientDataValue != null )
                {
                    patientDataValueMap.put( patientId+":"+ dataelementId, patientDataValue );
                }
            }
            
            
            return patientDataValueMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id", e );
        }
    }   
    
    
    
    
    
}

