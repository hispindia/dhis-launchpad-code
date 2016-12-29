package org.hisp.dhis.userstatus.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.userstatus.util.UserUpdateInfoObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 7/12/13
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserUpdateSummaryByDataSetsAction implements Action {
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    @Override
    public String execute() throws Exception {


        if (jdbcTemplate != null) {

            String inventoryQueryString = "SELECT asd.dsname,asd1.storedby,asd1.Updated_On,asd.startdate,asd.enddate,asd.name\n" +
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
                    ";";

            System.out.println("---------------------------------------------------------------------------");

            System.out.println(inventoryQueryString);

            System.out.println("---------------------------------------------------------------------------");

            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

            Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

            int columnNum = 1;

            if (columnCount == 6) {
                while (sqlRowSet.next()) {

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

        }

        return SUCCESS;
    }
}
