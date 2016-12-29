/**
 * 
 */
package org.hisp.dhis.ws.service;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hisp.dhis.mobile.api.DefaultMobileImportService;
import org.hisp.dhis.mobile.api.MobileImportService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.ws.DataSMS;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author THAI
 *
 */
public class DataServiceImpl implements DataService
{

    //DefaultMobileImportService mobileImportService = new DefaultMobileImportService();
    DefaultMobileImportService mobileImportService = new DefaultMobileImportService();

    
    
    public DataSMS sendData( DataSMS data ) throws DataException
    {
        // TODO Auto-generated method stub
        String myReturnValue, thirdPartyData;
        thirdPartyData = data.getMyData();
        myReturnValue = thirdPartyData + " @return message@Thai Chuong - Thai Chuong - Thai Chuong - Thai Chuong";

        importdata( thirdPartyData );

        data.setMyData( myReturnValue );
        System.out.println( "Web service processe in server done!" );
        return data;
    }

    /*
    public void initialize()
    {
    }
    */

    private void importdata( String Data )
    {
       

        Period period = null;
        String formid = null, date, smsData, periodType, query, updateInsertBuildQueryResponse;
        int periodId = 0, sourceId = 0, dataelementid = 0, comboid = 0;
        String dhis2Home = System.getenv( "DHIS2_HOME" );
        // get de, coid from csv file
       
        String[] thirdPartyData = Data.split( ":" );
         String messageWithHeader = thirdPartyData[0];
        String phoneNumber = thirdPartyData[1];
        String timeStamp = thirdPartyData[2];
        Properties props = new Properties();
        try
        {

            String csvFilePath = dhis2Home + File.separator + "mi" + File.separator + "formIDLayout.csv";
            props.load( new FileReader( csvFilePath ) );
       
        } catch ( IOException ex )
        {
            ex.printStackTrace();
            Logger.getLogger( DataServiceImpl.class.getName() ).log( Level.SEVERE, null, ex );
        }
      
        //
        String insertQuery = "INSERT INTO datavalue (dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated ) VALUES ";
        String[] splitDummyId = messageWithHeader.split( "#" );

        String[] splitFormId = mysplit(splitDummyId[1], "*" );
        formid = splitFormId[0];
        String[] splitDataSetTypeId = mysplit(splitFormId[1],"?" );
        periodType = splitDataSetTypeId[0];
        String[] splitDate = mysplit(splitDataSetTypeId[1], "$" );
        date = splitDate[0];
        //get date and organisationunit   
        try
        {
System.out.println("at line 94"+date+periodType);           
            //period = mobileImportService.getPeriodInfo( "'"+date+"'", periodType );
if (mobileImportService==null){
System.out.println("mis is null ..............");
}
            periodId=mobileImportService.queryForPeriod( date, periodType);
           // periodId = period.getId();
            OrganisationUnit source = mobileImportService.getOrganisationUnitByPhone( phoneNumber );
            sourceId = source.getId();
            System.out.println("at line 99");
        } catch ( Exception ex )
        {
            ex.printStackTrace();
            Logger.getLogger( DataServiceImpl.class.getName() ).log( Level.SEVERE, null, ex );
        }
        smsData = splitDate[1];

        String IdString = props.getProperty( formid );
System.out.println( "at line 80...."+IdString );
        String[] elementIds = mysplit(IdString, "," );

        
        
        String[] smsDataParsed = smsData.split( "|" );

        String datavalue = smsDataParsed[0];
        // check if value is null

        /*
         * for each datavalue in the sms extract the deid coid from csv file
         * 
         */
        System.out.println("at line 116");
        for ( int i = 1; i < elementIds.length; i++ )
        {
            String[] dataelementidComboId = elementIds[i].split( "." );
            dataelementid = Integer.parseInt( dataelementidComboId[0] );
            comboid = Integer.parseInt( dataelementidComboId[1] );
System.out.println("at line 122");
            updateInsertBuildQueryResponse = mobileImportService.updateInsertBuildQueryForExternalClient( dataelementid, comboid, periodId, sourceId, datavalue, phoneNumber, timeStamp );
            if ( updateInsertBuildQueryResponse.equalsIgnoreCase( "update" ) )
            {
                System.out.println("at line 126");
                System.out.print( "update" );
            } else
            {
                if ( updateInsertBuildQueryResponse.equalsIgnoreCase( "error" ) )
                {System.out.println("at line 131");

                    System.out.println( "neither update nor insert" );
                } else
                {System.out.println("at line 135");

                    insertQuery += updateInsertBuildQueryResponse;
                    System.out.println( "added insert q" );
                }
            }


        }

    }
       private String[] mysplit(String original, String separator) {
       Vector nodes = new Vector();

       // Parse nodes into vector
       int index = original.indexOf(separator);
       while (index >= 0) {
           nodes.addElement(original.substring(0, index));
           original = original.substring(index + separator.length());
           index = original.indexOf(separator);
       }
       // Get the last node
       nodes.addElement(original);

       // Create splitted string array
       String[] result = new String[nodes.size()];
       if (nodes.size() > 0) {
           for (int loop = 0; loop < nodes.size(); loop++) {
               result[loop] = (String) nodes.elementAt(loop);
           }
       }
       return result;
       }
}
