package org.hisp.dhis.datainventorystatus.action;

/**
 * User: gaurav
 * Date: 19/9/13
 * Time: 8:59 PM
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.JDBCException;

import com.opensymphony.xwork2.Action;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;


public class JSONDataAction{

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String string1 = "A";
    private String[] stringarray1 = {"A1","B1"};
    private int number1 = 123456789;
    private int[] numberarray1 = {1,2,3,4,5,6,7,8,9};
    private List<String> lists = new ArrayList<String>();
    private Map<String, String> maps = new HashMap<String, String>();

    //no getter method, will not include in the JSON
    private String string2 = "B";

    public String execute() {
        return Action.SUCCESS;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public String[] getStringarray1() {
        return stringarray1;
    }

    public void setStringarray1(String[] stringarray1) {
        this.stringarray1 = stringarray1;
    }

    public int getNumber1() {
        return number1;
    }

    public void setNumber1(int number1) {
        this.number1 = number1;
    }

    public int[] getNumberarray1() {
        return numberarray1;
    }

    public void setNumberarray1(int[] numberarray1) {
        this.numberarray1 = numberarray1;
    }

    public List<String> getLists() {
        return lists;
    }

    public void setLists(List<String> lists) {
        this.lists = lists;
    }

    public Map<String, String> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, String> maps) {
        this.maps = maps;
    }

    public JSONDataAction(){

        //DatabaseInfo dataBaseInfo = databaseInfoProvider.getDatabaseInfo();

        try
        {
            String query = "SELECT INV_MD.GroupSetName, INV_MD.GroupName, INV_MD.Dataelement, INV_MD.Frequency, INV_MD.DataSet, INV_MD.FromDate, INV_MD.ToDate, INV_MD.TotalRecords\n" +
                        "FROM\n" +
                        "(\n" +
                        "SELECT DEGS.name AS 'GroupSetName', DEG.name AS 'GroupName', DE.name AS 'Dataelement', DPT.name AS 'Frequency', DS.name AS 'DataSet', MIN(DP.startdate) AS 'FromDate', MAX(DP.enddate) AS 'ToDate', COUNT(DV.value) AS 'TotalRecords'\n" +
                        "FROM dataelement DE\n" +
                        "LEFT JOIN datavalue DV ON DE.dataelementid=DV.dataelementid\n" +
                        "LEFT JOIN datasetmembers DSM ON DSM.dataelementid=DE.dataelementid\n" +
                        "INNER JOIN dataset DS ON DS.datasetid = DSM.datasetid AND DS.datasetid IN (1,2,3,4)\n" +
                        "LEFT JOIN period DP ON DP.periodid=DV.periodid\n" +
                        "LEFT JOIN dataelementgroupmembers DEGM ON DEGM.dataelementid = DE.dataelementid\n" +
                        "LEFT JOIN dataelementgroup DEG ON DEG.dataelementgroupid=DEGM.dataelementgroupid\n" +
                        "LEFT JOIN dataelementgroupsetmembers DEGSM ON DEGSM.dataelementgroupid=DEGM.dataelementgroupid\n" +
                        "LEFT JOIN dataelementgroupset DEGS ON DEGS.dataelementgroupsetid=DEGSM.dataelementgroupsetid\n" +
                        "LEFT JOIN datasetsource DSS ON DSS.datasetid=DS.datasetid\n" +
                        "LEFT JOIN organisationunit OU ON OU.organisationunitid=DSS.sourceid\n" +
                        "LEFT JOIN periodtype DPT ON DPT.periodtypeid=DP.periodtypeid\n" +
                        "LEFT JOIN dataelementattributevalues DEAV ON DEAV.dataelementid=DE.dataelementid\n" +
                        "LEFT JOIN attributevalue DAV ON DAV.attributevalueid=DEAV.attributevalueid\n" +
                        "LEFT JOIN attribute DA ON DAV.attributeid=DA.attributeid\n" +
                        "GROUP BY DE.dataelementid) INV_MD";

            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet( query );

            while ( sqlRowSet.next() )
            {
                System.out.println(sqlRowSet);
            }
        }
        catch (JDBCException jdbcException)
        {

        }

        lists.add("list1");
        lists.add("list2");
        lists.add("list3");
        lists.add("list4");
        lists.add("list5");

        maps.put("key1", "value1");
        maps.put("key2", "value2");
        maps.put("key3", "value3");
        maps.put("key4", "value4");
        maps.put("key5", "value5");
    }

}