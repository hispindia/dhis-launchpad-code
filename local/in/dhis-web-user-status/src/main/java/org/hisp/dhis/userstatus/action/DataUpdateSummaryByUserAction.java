package org.hisp.dhis.userstatus.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.userstatus.util.UserUpdateInfoObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 6/12/13
 * Time: 12:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataUpdateSummaryByUserAction implements Action {

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

        System.out.println("UserID:" + userID);

        User user = userService.getUser(userID);

        dateCreated = String.valueOf(user.getCreated());

        UID = user.getUid();

        userName = user.getName();

        OrgUnits = user.getOrganisationUnitsName();

        String getUserDataSetsQuery = "SELECT av.value " +
                "FROM attributevalue av " +
                "INNER JOIN attribute da ON da.attributeid=av.attributeid " +
                "INNER JOIN userattributevalues uav ON uav.attributevalueid=av.attributevalueid " +
                "INNER JOIN users du ON du.userid=uav.userinfoid " +
                "WHERE du.userid=" + userID + "\n" +
                "AND da.name='Datasets' " +
                "LIMIT 1; ";

        if (jdbcTemplate != null) {

            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(getUserDataSetsQuery);
            String userDatasets = null;
            int resultCount = 0;

            while (sqlRowSet.next()) {
                userDatasets = sqlRowSet.getString(1);
                System.out.println("User Datasets: " + userDatasets);
                ++resultCount;
            }

            if (resultCount == 1)
            {
                String[] dataSetIDS = userDatasets.split(",");

                int dataSetCount = dataSetIDS.length;
                for (int i = 0; i < dataSetCount; ++i) {
                    dataSetArrayList.add(dataSetService.getDataSet(Integer.parseInt(dataSetIDS[i].trim())));
                }

                dataSetsAssigned = " ";

                for (DataSet dataSet : dataSetArrayList) {
                    if (dataSet != null) {
                        System.out.println("[" + dataSet.getName() + "]");
                        dataSetsAssigned = dataSetsAssigned +"["+dataSet.getName()+"] ";
                    }
                }


                if(jdbcTemplate!=null)
                {

                    String inventoryQueryString = "SELECT ds.name, MAX(dv.lastupdated) AS 'Updated On', MAX(dp.startdate) AS 'StartDate', MAX(dp.enddate) AS 'EndDate' , dpt.name " +
                            "FROM dataelement de " +
                            "INNER JOIN datasetmembers dsm ON de.dataelementid=dsm.dataelementid " +
                            "INNER JOIN dataset ds ON ds.datasetid= dsm.datasetid " +
                            "INNER JOIN datavalue dv ON dv.dataelementid=de.dataelementid " +
                            "INNER JOIN period dp ON dp.periodid=dv.periodid " +
                            "INNER JOIN periodtype dpt ON dpt.periodtypeid=dp.periodtypeid " +
                            "WHERE ds.datasetid IN("+userDatasets+") " +
                            "GROUP BY ds.datasetid; ";

                    System.out.println("---------------------------------------------------------------------------");

                    System.out.println(inventoryQueryString);

                    System.out.println("---------------------------------------------------------------------------");

                    sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

                    Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

                    int columnNum = 1;

                    if(columnCount==5)
                    {
                        while(sqlRowSet.next())
                        {

                            String dataSetName = sqlRowSet.getString(1);
                            String updatedOn = sqlRowSet.getString(2);
                            String startDate = sqlRowSet.getString(3);
                            String endDate = sqlRowSet.getString(4);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                            String periodType = sqlRowSet.getString(5);

                            Date sDate = dateFormat.parse(startDate);
                            Calendar sCalendar = Calendar.getInstance();
                            sCalendar.setTime(sDate);

                            Date eDate = dateFormat.parse(endDate);
                            Calendar eCalendar = Calendar.getInstance();
                            eCalendar.setTime(eDate);

                            if(periodType.equalsIgnoreCase("FinancialJuly"))
                            {
                                startDate = new SimpleDateFormat("MMM").format(sCalendar.getTime())+" "
                                        +sCalendar.get(Calendar.YEAR)+ " - "
                                        + new SimpleDateFormat("MMM").format(eCalendar.getTime())+" "
                                        +eCalendar.get(Calendar.YEAR);
                            }
                            else if(periodType.equalsIgnoreCase("Monthly"))
                            {
                                startDate = new SimpleDateFormat("MMM").format(sCalendar.getTime())+" "
                                        +sCalendar.get(Calendar.YEAR);
                            }
                            else if(periodType.equalsIgnoreCase("Yearly"))
                            {
                                startDate = String.valueOf(sCalendar.get(Calendar.YEAR));
                            }

                            System.out.println(dataSetName+":"+updatedOn+":"+startDate);

                            UserUpdateInfoObject userUpdateInfoObject = new UserUpdateInfoObject();

                            userUpdateInfoObject.setSerialNumber(columnNum);

                            ++columnNum;

                            userUpdateInfoObject.setDataSetName(dataSetName);
                            userUpdateInfoObject.setUpdatedOn(updatedOn);
                            userUpdateInfoObject.setDataEnteredFor(startDate);

                            infoList.add(userUpdateInfoObject);

                        }
                    }
                }
            }
            else
            {
              dataSetsAssigned = "-";
            }
        }

        return SUCCESS;
    }
}
