package org.hisp.dhis.ovc.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class OVCService
{
    public static final String OVC_ID = "OVC_ID";//929.0
    
    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
    
    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
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

 
    //--------------------------------------------------------------------------------
    // Get Patient Data Values by Patient Ids for quartely period
    //--------------------------------------------------------------------------------
    public Map<Integer, String> getPatientsLastVisitDate( String patientIdsByComma, Integer programId, Integer prograStageId )
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
        
    
    public Map<String, String> getQuarterlyVisitDateMap( Map<Integer, List<Period>> quarterlyPeriodTypeMap, Integer programId, Integer prograStageId, Integer patientId )
    {
        Map<String, String> visitDateMap = new HashMap<String, String>();
        
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
            
            for (Integer year : quarterlyPeriodTypeMap.keySet() )
            {
                List<Period> periods = new ArrayList<Period>( quarterlyPeriodTypeMap.get( year ) );
                
                for( Period period : periods )
                {
                    String executionDate = simpleDateFormat.format( period.getStartDate() );
                    
                    /*
                    String query = "SELECT COUNT( executiondate ) FROM programstageinstance, programinstance " +
                                    " WHERE " + 
                                        " programstageinstance.programinstanceid = programinstance.programinstanceid AND " +
                                        " programinstance.patientid = " + patientId + " AND " +
                                        " executiondate = '" + executionDate + "'";
                  
                    */
                    
                    String query = "SELECT COUNT( executiondate ) FROM programstageinstance " +   
                        " INNER JOIN programinstance on programinstance.programinstanceid = programstageinstance.programinstanceid " + 
                        " WHERE " +  
                            " programinstance.programid = " + programId +" AND " + 
                            " programstageinstance.programstageid = "+ prograStageId +" AND " + 
                            " programinstance.patientid = " + patientId + " AND " +
                            " executiondate = '" + executionDate + "'";
                            
                    SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
        
                    //System.out.println( rs.getRow() );
                    
                    if ( rs.next() )
                    {
                        Integer count = rs.getInt( 1 );
                        if ( count == 0 )
                        {
                            visitDateMap.put( ""+period.getExternalId(), "red" );
                        }
                        else
                        {
                            visitDateMap.put( ""+period.getExternalId(), "green" );
                        }
                        
                    }
                    else
                    {
                        visitDateMap.put( ""+period.getExternalId(), "red" );
                    }
                }
            }
            
            return visitDateMap;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal Period id", e );
        }
    }    
    
    
    
    //--------------------------------------------------------------------------------
    // Get School Details values  by School Id and PatientAttributeIds
    //--------------------------------------------------------------------------------
    public Map<Integer, String> getSchoolDetailValues( Integer schoolId, String patientAttributeIdsByComma )
    {
        Map<Integer, String> schoolDetailsValueMap = new HashMap<Integer, String>();

        try
        {
            String query = "SELECT schoolid, patientattributeid, value FROM schooldetails " +
                            "WHERE  schoolid  = " + schoolId +" AND patientattributeid IN ( "+ patientAttributeIdsByComma +")";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                //Integer schoolID = rs.getInt( 1 );
                Integer patientAttributeId = rs.getInt( 2 );                
                String value = rs.getString( 3 );
                
                if ( value != null )
                {
                    schoolDetailsValueMap.put( patientAttributeId, value );
                }
            }

            return schoolDetailsValueMap;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal Patient Attribute Ids ", e );
        }
    }
    
    
    
    //--------------------------------------------------------------------------------
    // Get School Data values  by School Id and PatientAttributeId
    //--------------------------------------------------------------------------------
    public String getSchoolDataValue( Integer schoolId, Integer patientAttributeId )
    {
        String schoolValue = "";

        try
        {
            String query = "SELECT schoolid, patientattributeid, value FROM schooldetails " +
                            "WHERE  schoolid  = " + schoolId + " AND patientattributeid = " + patientAttributeId + " ";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                //Integer schoolID = rs.getInt( 1 );
                //Integer attributeId = rs.getInt( 2 );                
                String value = rs.getString( 3 );
                
                if ( value != null )
                {
                    schoolValue = value;
                }
            }

            return schoolValue;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal School Id or Patient Attribute Id ", e );
        }
    }
        
    
    // get School Object list by OVC Id
    public List<School> getSchoolByOVC( Integer ovcId )
    {
        List<School> schools = new ArrayList<School>();
        
        try
        {
            String query = "SELECT schoolid, patientid FROM school_ovc " +
                            "WHERE  patientid  = " + ovcId  + " ";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer schoolId = rs.getInt( 1 );
                //Integer patientId = rs.getInt( 2 );                
                
                if ( schoolId != null )
                {
                    School school = schoolService.getSchool( schoolId );
                    schools.add( school );
                }
            }

            return schools;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal School Id or Patient Attribute Id ", e );
        }
        
    }
    
    
    // get Patient List Object By Registration Date less than or equal selected date
    public List<Patient> getPatients( Integer schoolId, String  registrationdate )
    {
        List<Patient> patients = new ArrayList<Patient>();
        
        try
        {
            String query = "SELECT  school_ovc.patientid, school_ovc.schoolid FROM school_ovc "
                           + "INNER JOIN patient ON patient.patientid = school_ovc.patientid "
                           + "WHERE school_ovc.schoolid  = " + schoolId  + " and  patient.registrationdate <= '" + registrationdate + "'";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer patientId = rs.getInt( 1 );
                //Integer schoolId = rs.getInt( 2 );                
                
                if ( patientId != null )
                {
                    Patient patient = patientService.getPatient( patientId );
                    patients.add( patient );
                }
            }

            return patients;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal Registration Date ", e );
        }
        
    }
    
    //--------------------------------------------------------------------------------
    // Get Patient Data Values by Patient Ids and Execution Date
    //--------------------------------------------------------------------------------
    public Map<String, String> getPatientDataValuesByExecutionDate( String patientIdsByComma, Integer programId, Integer prograStageId, String executionDate )
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
    // Get Patient IdentifierId map
    //--------------------------------------------------------------------------------
    public Map<Integer, String> getPatientIdentifierId()
    {
        Map<Integer, String> mapPatientSystemIdentifier = new HashMap<Integer, String>();
        Constant patientIdentifierTypeConstant = constantService.getConstantByName( OVC_ID );
        PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( (int) patientIdentifierTypeConstant.getValue() );

        try
        {
            Collection<Patient> patients = new ArrayList<Patient>();
            patients = new ArrayList<Patient>(  patientService.getAllPatients() );
            
            if( patients != null && patients.size() > 0 )
            {
                for( Patient ovc : patients )
                {
                    String systemOVCId = "";

                    for ( PatientIdentifier identifier : ovc.getIdentifiers() )
                    {
                        identifierType = identifier.getIdentifierType();

                        if ( ovc.getOrganisationUnit().getCode() != null && identifierType != null )
                        {
                            PatientIdentifier ovcIdIdentifier = patientIdentifierService.getPatientIdentifier( identifierType, ovc );
                            systemOVCId = ovcIdIdentifier.getIdentifier();
                        }
                        else
                        {
                            systemOVCId = identifier.getIdentifier();
                        }
                        
                        //mapPatientSystemIdentifier.put( ovc.getId(), systemOVCId );
                    }
                    mapPatientSystemIdentifier.put( ovc.getId(), systemOVCId );
                }
            }        
            
            return mapPatientSystemIdentifier;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal Patient id", e );
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
                Integer sheetno = new Integer( deCodeElement.getAttribute( "sheetno" ) );
                Integer rowno = Integer.parseInt( deCodeElement.getAttribute( "rowno" ) );
                Integer colno = Integer.parseInt( deCodeElement.getAttribute( "colno" ) );
                
                ReportCell reportCell = new ReportCell( datatype, service, sheetno, rowno, colno, expression );
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
      
}
