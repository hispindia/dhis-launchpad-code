package org.hisp.dhis.userstatus.util;


import org.apache.struts2.views.xslt.StringAdapter;
import org.h2.message.TraceSystem;
import org.hibernate.ejb.criteria.expression.function.CurrentDateFunction;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.config.ConfigurationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 9/12/13
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConstructStatusMailContentAction {

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private ConfigurationService configurationService;

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }




    private int userID;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private DataSetService dataSetService;

    public DataSetService getDataSetService() {
        return dataSetService;
    }

    public void setDataSetService(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }


    private String dateCreated;

    private String userName;

    private String UID;

    private String dataSetsAssigned;

    private String userRoles;

    private String OrgUnits;

    public Collection<UserUpdateInfoObject> infoList = new ArrayList<UserUpdateInfoObject>();

    public Collection<UserUpdateInfoObject> getInfoList() {
        return infoList;
    }

    public void setInfoList(Collection<UserUpdateInfoObject> infoList) {
        this.infoList = infoList;
    }

    private ArrayList<DataSet> dataSetArrayList = new ArrayList<DataSet>();

    public ArrayList<DataSet> getDataSetArrayList() {
        return dataSetArrayList;
    }

    public void setDataSetArrayList(ArrayList<DataSet> dataSetArrayList) {
        this.dataSetArrayList = dataSetArrayList;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getDataSetsAssigned() {
        return dataSetsAssigned;
    }

    public void setDataSetsAssigned(String dataSetsAssigned) {
        this.dataSetsAssigned = dataSetsAssigned;
    }

    public String getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(String userRoles) {
        this.userRoles = userRoles;
    }

    public String getOrgUnits() {
        return OrgUnits;
    }

    public void setOrgUnits(String orgUnits) {
        OrgUnits = orgUnits;
    }

    private String lastSendMailKey;

    public void setLastSendMailKey(String lastSendMailKey) {
        this.lastSendMailKey = lastSendMailKey;
    }




    public void constructMessage() throws Exception {
        infoList = new ArrayList<UserUpdateInfoObject>();

        SimpleDateFormat standardDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        String newDate =  standardDateFormat.format( new Date() );
        String  lastSendMailKey="lastSendMailKey";
        Configuration_IN conFigObj = configurationService.getConfigurationByKey( lastSendMailKey );

        if (jdbcTemplate != null) {
            String inventoryQueryString;

            if( conFigObj == null )
            {
                Configuration_IN abc = new Configuration_IN();

            inventoryQueryString = "SELECT asd.dsname,asd1.storedby,asd1.Updated_On,asd.startdate,asd.enddate,asd.name\n" +
                    "FROM\n" +
                    "(\n" +
                    "SELECT ds.name 'dsname', dv.storedby, dv.dataelementid, MAX(dv.lastupdated) AS 'Updated_On', MAX(dp.startdate) AS 'StartDate', MAX(dp.enddate) AS 'EndDate', dpt.name\n" +
                    "FROM dataelement de\n" +
                    "INNER JOIN datasetmembers dsm ON de.dataelementid=dsm.dataelementid\n" +
                    "INNER JOIN dataset ds ON ds.datasetid= dsm.datasetid\n" +
                    "INNER JOIN datavalue dv ON dv.dataelementid=de.dataelementid\n" +
                    "LEFT JOIN period dp ON dp.periodid=dv.periodid\n" +
                    "LEFT JOIN periodtype dpt ON dpt.periodtypeid=dp.periodtypeid\n" +
                    "WHERE dv.storedby IS NOT NULL\n" +
                    "GROUP BY ds.datasetid\n" +
                    ")asd\n" +
                    "INNER JOIN\n" +
                    "(\n" +
                    "SELECT ds.name AS 'dsname', dv.storedby, dv.dataelementid, MAX(dv.lastupdated) AS 'Updated_On', MAX(dp.startdate) AS 'StartDate', MAX(dp.enddate) AS 'EndDate', dpt.name\n" +
                    "FROM dataelement de\n" +
                    "INNER JOIN datasetmembers dsm ON de.dataelementid=dsm.dataelementid\n" +
                    "INNER JOIN dataset ds ON ds.datasetid= dsm.datasetid\n" +
                    "INNER JOIN datavalue dv ON dv.dataelementid=de.dataelementid\n" +
                    "LEFT JOIN period dp ON dp.periodid=dv.periodid\n" +
                    "LEFT JOIN periodtype dpt ON dpt.periodtypeid=dp.periodtypeid\n" +
                    "WHERE dv.storedby IS NOT NULL\n" +
                    "GROUP BY ds.datasetid,dv.storedby\n" +
                    ")asd1 ON asd1.dsname=asd.dsname AND asd1.Updated_On=asd.Updated_On\n" +
                    "where asd1.Updated_On >= " + "'"+ newDate  + "'"+
                    ";";

                abc.setKey( lastSendMailKey);
                abc.setValue( newDate );
                int ppp = configurationService.addConfiguration( abc );
            }

            else
            {
                String lastValue = conFigObj.getValue() ;

                inventoryQueryString = "SELECT asd.dsname,asd1.storedby,asd1.Updated_On,asd.startdate,asd.enddate,asd.name\n" +
                        "FROM\n" +
                        "(\n" +
                        "SELECT ds.name 'dsname', dv.storedby, dv.dataelementid, MAX(dv.lastupdated) AS 'Updated_On', MAX(dp.startdate) AS 'StartDate', MAX(dp.enddate) AS 'EndDate', dpt.name\n" +
                        "FROM dataelement de\n" +
                        "INNER JOIN datasetmembers dsm ON de.dataelementid=dsm.dataelementid\n" +
                        "INNER JOIN dataset ds ON ds.datasetid= dsm.datasetid\n" +
                        "INNER JOIN datavalue dv ON dv.dataelementid=de.dataelementid\n" +
                        "LEFT JOIN period dp ON dp.periodid=dv.periodid\n" +
                        "LEFT JOIN periodtype dpt ON dpt.periodtypeid=dp.periodtypeid\n" +
                        "WHERE dv.storedby IS NOT NULL\n" +
                        "GROUP BY ds.datasetid\n" +
                        ")asd\n" +
                        "INNER JOIN\n" +
                        "(\n" +
                        "SELECT ds.name AS 'dsname', dv.storedby, dv.dataelementid, MAX(dv.lastupdated) AS 'Updated_On', MAX(dp.startdate) AS 'StartDate', MAX(dp.enddate) AS 'EndDate', dpt.name\n" +
                        "FROM dataelement de\n" +
                        "INNER JOIN datasetmembers dsm ON de.dataelementid=dsm.dataelementid\n" +
                        "INNER JOIN dataset ds ON ds.datasetid= dsm.datasetid\n" +
                        "INNER JOIN datavalue dv ON dv.dataelementid=de.dataelementid\n" +
                        "LEFT JOIN period dp ON dp.periodid=dv.periodid\n" +
                        "LEFT JOIN periodtype dpt ON dpt.periodtypeid=dp.periodtypeid\n" +
                        "WHERE dv.storedby IS NOT NULL\n" +
                        "GROUP BY ds.datasetid,dv.storedby\n" +
                        ")asd1 ON asd1.dsname=asd.dsname AND asd1.Updated_On=asd.Updated_On\n" +
                        "where asd1.Updated_On >= " + "'" +lastValue+ "'"   +
                        ";";

                conFigObj.setValue( newDate );
                configurationService.updateConfiguration( conFigObj ) ;
            }




            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

            System.out.println("==================================================================================================");

            System.out.println(inventoryQueryString);

            System.out.println("==================================================================================================");

            Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

            int columnNum = 1;
            int total_rows=0;
            if (columnCount == 6) {
                while (sqlRowSet.next()) {
                    total_rows++;
                    String dataSetName = sqlRowSet.getString(1);
                    String storedBy = sqlRowSet.getString(2);
                    String updatedOn = sqlRowSet.getString(3);
                    String startDate = sqlRowSet.getString(4);
                    String endDate = sqlRowSet.getString(5);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    String periodType = sqlRowSet.getString(6);

                    Date sDate = dateFormat.parse(startDate);
                    Calendar sCalendar = Calendar.getInstance();
                    sCalendar.setTime(sDate);

                    Date eDate = dateFormat.parse(endDate);
                    Calendar eCalendar = Calendar.getInstance();
                    eCalendar.setTime(eDate);

                    if (periodType.equalsIgnoreCase("FinancialJuly")) {
                        startDate = new SimpleDateFormat("MMM").format(sCalendar.getTime()) + " "
                                + sCalendar.get(Calendar.YEAR) + " - "
                                + new SimpleDateFormat("MMM").format(eCalendar.getTime()) + " "
                                + eCalendar.get(Calendar.YEAR);
                    } else if (periodType.equalsIgnoreCase("Monthly")) {
                        startDate = new SimpleDateFormat("MMM").format(sCalendar.getTime()) + " "
                                + sCalendar.get(Calendar.YEAR);
                    } else if (periodType.equalsIgnoreCase("Yearly")) {
                        startDate = String.valueOf(sCalendar.get(Calendar.YEAR));
                    }

                    System.out.println(dataSetName + ":" + updatedOn + ":" + startDate);

                    UserUpdateInfoObject userUpdateInfoObject = new UserUpdateInfoObject();

                    userUpdateInfoObject.setSerialNumber(columnNum);

                    ++columnNum;

                    userUpdateInfoObject.setDataSetName(dataSetName);
                    userUpdateInfoObject.setUpdatedOn(updatedOn);
                    userUpdateInfoObject.setDataEnteredFor(startDate);
                    userUpdateInfoObject.setUserName(storedBy);

                    infoList.add(userUpdateInfoObject);

                }


            }


            List<String> receiverAddresses  = new ArrayList<String>();

            if (jdbcTemplate != null) {

                String QueryString = "SELECT dui.email AS 'Mail'\n" +
                        "FROM users du\n" +
                        "INNER JOIN userinfo dui ON dui.userinfoid=du.userid\n" +
                        "INNER JOIN userrolemembers durm ON durm.userid=du.userid\n" +
                        "INNER JOIN userrole dur ON dur.userroleid=durm.userroleid\n" +
                        "WHERE dur.name='Receive Alerts';";

                System.out.println("---------------------------------------------------------------------------");

                System.out.println(QueryString);

                System.out.println("---------------------------------------------------------------------------");

                sqlRowSet = jdbcTemplate.queryForRowSet(QueryString);

                columnCount = sqlRowSet.getMetaData().getColumnCount();

                if (columnCount == 1) {
                    while (sqlRowSet.next()) {

                        String mailAddress = sqlRowSet.getString(1);

                        receiverAddresses.add(mailAddress);

                    }
                }
            }

            String mailMessage;

            if(total_rows>0) {
            mailMessage = "<html>\n";

            //----------------------------------ADD HEADER TO THE STATUS REPORT MAIL---------------------------//

            mailMessage = mailMessage + "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" \"http://www.w3.org/TR/REC-html40/loose.dtd\">\n" +
                    "<html>\n" +
                    "<body style=\"font-family: georgia; font-size: 12px;\">\n" +
                    "<style type=\"text/css\">\n" +
                    ".bordered tr:hover { background: #fbf8e9 !important; -o-transition: all 0.1s ease-in-out !important; -webkit-transition: all 0.1s ease-in-out !important; -moz-transition: all 0.1s ease-in-out !important; -ms-transition: all 0.1s ease-in-out !important; transition: all 0.1s ease-in-out !important; }\n" +
                    ".fitler-table .quick:hover { text-decoration: underline !important; }\n" +
                    "></style>\n" +
                    "<h3 style=\"font-family: georgia;\">Data Update Report</h3>\n" +
                    "<br><table style=\"*border-collapse: collapse; border-spacing: 0; width: 100%; -moz-border-radius: 6px; -webkit-border-radius: 6px; border-radius: 6px; -webkit-box-shadow: 0 1px 1px #ccc; -moz-box-shadow: 0 1px 1px #ccc; box-shadow: 0 1px 1px #ccc; border: 1px solid #ccc;\">\n" +
                    "<thead><tr>\n" +
                    "<th style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: none; border-top-color: #ccc; border-top-style: none; border-top-width: 1px; text-align: left; -webkit-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; -moz-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; text-shadow: 0 1px 0 rgba(255, 255, 255, .5); -moz-border-radius: 6px 0 0 0; -webkit-border-radius: 6px 0 0 0; border-radius: 6px 0 0 0; background-image: linear-gradient(top, #ebf3fc, #dce9f9); background-color: #dce9f9; padding: 10px;\" align=\"left\" bgcolor=\"#dce9f9\">S. No</th>\n" +
                    "                <th style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: solid; border-top-color: #ccc; border-top-style: none; border-top-width: 1px; text-align: left; -webkit-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; -moz-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; text-shadow: 0 1px 0 rgba(255, 255, 255, .5); background-image: linear-gradient(top, #ebf3fc, #dce9f9); background-color: #dce9f9; padding: 10px;\" align=\"left\" bgcolor=\"#dce9f9\">Dataset</th>\n" +
                    "                <th style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: solid; border-top-color: #ccc; border-top-style: none; border-top-width: 1px; text-align: left; -webkit-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; -moz-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; text-shadow: 0 1px 0 rgba(255, 255, 255, .5); background-image: linear-gradient(top, #ebf3fc, #dce9f9); background-color: #dce9f9; padding: 10px;\" align=\"left\" bgcolor=\"#dce9f9\">Stored By</th>\n" +
                    "                <th style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: solid; border-top-color: #ccc; border-top-style: none; border-top-width: 1px; text-align: left; -webkit-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; -moz-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; text-shadow: 0 1px 0 rgba(255, 255, 255, .5); background-image: linear-gradient(top, #ebf3fc, #dce9f9); background-color: #dce9f9; padding: 10px;\" align=\"left\" bgcolor=\"#dce9f9\">Updated On(yyyy-mm-dd)</th>\n" +
                    "                <th style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: solid; border-top-color: #ccc; border-top-style: none; border-top-width: 1px; text-align: left; -webkit-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; -moz-box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; box-shadow: 0 1px 0 rgba(255, 255, 255, .8) inset; text-shadow: 0 1px 0 rgba(255, 255, 255, .5); -moz-border-radius: 0 6px 0 0; -webkit-border-radius: 0 6px 0 0; border-radius: 0 6px 0 0; background-image: linear-gradient(top, #ebf3fc, #dce9f9); background-color: #dce9f9; padding: 10px;\" align=\"left\" bgcolor=\"#dce9f9\">Data Entered For</th>\n" +
                    "            </tr></thead>\n" +
                    "<tbody>";

            //----------------------------------ADD BODY ELEMENTS TO THE STATUS REPORT MAIL---------------------------//

            mailMessage = mailMessage + "";

            for(UserUpdateInfoObject updateInfoObject: infoList)
            {
                mailMessage = mailMessage
                        +"                 <tr>"
                        +"                    <td style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: none; border-top-color: #ccc; border-top-style: solid; border-top-width: 1px; text-align: left; padding: 10px;\" align=\"left\">"+updateInfoObject.serialNumber+"</td>\n"
                        +"                    <td style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: none; border-top-color: #ccc; border-top-style: solid; border-top-width: 1px; text-align: left; padding: 10px;\" align=\"left\">"+updateInfoObject.dataSetName+"</td>\n"
                        +"                    <td style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: none; border-top-color: #ccc; border-top-style: solid; border-top-width: 1px; text-align: left; padding: 10px;\" align=\"left\">"+updateInfoObject.userName+"</td>\n"
                        +"                    <td style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: none; border-top-color: #ccc; border-top-style: solid; border-top-width: 1px; text-align: left; padding: 10px;\" align=\"left\">"+updateInfoObject.updatedOn+"</td>\n"
                        +"                    <td style=\"border-left-color: #ccc; border-left-width: 1px; border-left-style: none; border-top-color: #ccc; border-top-style: solid; border-top-width: 1px; text-align: left; padding: 10px;\" align=\"left\">"+updateInfoObject.DataEnteredFor+"</td>\n"
                        +"                 </tr>";
            }

            mailMessage = mailMessage
                    +"            <tbody>\n" +
                    "            <tfoot>\n" +
                    "            </tfoot>\n" +
                    "        </table>\n" +
                    "    </div>\n" ;

            }

            else {
                mailMessage = "<html>\n";

                //----------------------------------ADD HEADER TO THE STATUS REPORT MAIL---------------------------//

                mailMessage = mailMessage + "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" \"http://www.w3.org/TR/REC-html40/loose.dtd\">\n" +
                        "<html>\n" +
                        "<body style=\"font-family: georgia; font-size: 12px;\">\n" +
                        "<style type=\"text/css\">\n" +
                        ".bordered tr:hover { background: #fbf8e9 !important; -o-transition: all 0.1s ease-in-out !important; -webkit-transition: all 0.1s ease-in-out !important; -moz-transition: all 0.1s ease-in-out !important; -ms-transition: all 0.1s ease-in-out !important; transition: all 0.1s ease-in-out !important; }\n" +
                        ".fitler-table .quick:hover { text-decoration: underline !important; }\n" +
                        "></style>\n" +
                        "<h3 style=\"font-family: georgia;\">Data Update Report</h3>\n" + "There are no updates since times of last alert";

                //----------------------------------ADD BODY ELEMENTS TO THE STATUS REPORT MAIL---------------------------//

                mailMessage = mailMessage + "";

                mailMessage = mailMessage
                        +  "    </div>\n" ;

            }

            SendDataStatusEmail sendDataStatusEmail = new SendDataStatusEmail();

            for(String address: receiverAddresses)
            {

                System.out.println("NOTE: Sending alert email to "+ address);
                sendDataStatusEmail.sendReport("fsnisalert","fsnisalert@2014","smtp.gmail.com",address,mailMessage,"FSNIS UPDATE ALERT ["+new Date()+"]");
            }

        }
    }

}
