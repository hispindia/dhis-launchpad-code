package org.hisp.dhis.alert.action;

/**
 * User: gaurav
 * Date: 19/9/13
 * Time: 8:59 PM
 */

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.alert.util.DataInventoryStatusInfo;
import org.hisp.dhis.alert.util.IndicatorInventoryStatusInfo;
import org.hisp.dhis.indicator.IndicatorService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.DataSet;

import java.util.ArrayList;
import java.util.Collection;


public class DataInventoryStatusAction implements Action{

    private int dataSetID;

    public int getDataSetID() {
        return dataSetID;
    }

    private String criteriaType;

    public String getCriteriaType() {
        return criteriaType;
    }

    private int groupID;

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public void setCriteriaType(String criteriaType) {
        this.criteriaType = criteriaType;
    }

    private DataSet dataSet;

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }

    public void setDataSetID(int dataSetID) {
        this.dataSetID = dataSetID;
    }

    private String dataSetDescription;

    public String getDataSetDescription() {
        return dataSetDescription;
    }

    public void setDataSetDescription(String dataSetDescription) {
        this.dataSetDescription = dataSetDescription;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private DataSetService dataSetService;

    public DataSetService getDataSetService() {
        return dataSetService;
    }

    public void setDataSetService(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }

    private int totalDataElements;

    public int getTotalDataElements() {
        return totalDataElements;
    }

    public void setTotalDataElements(int totalDataElements) {
        this.totalDataElements = totalDataElements;
    }

    private ArrayList<DataInventoryStatusInfo> inventoryList = new ArrayList<DataInventoryStatusInfo>();

    public ArrayList<DataInventoryStatusInfo> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(ArrayList<DataInventoryStatusInfo> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public Collection<IndicatorInventoryStatusInfo> indicatorInventoryList = new ArrayList<IndicatorInventoryStatusInfo>();

    public Collection<IndicatorInventoryStatusInfo> getIndicatorInventoryList() {
        return indicatorInventoryList;
    }

    public void setIndicatorInventoryList(Collection<IndicatorInventoryStatusInfo> indicatorInventoryList) {
        this.indicatorInventoryList = indicatorInventoryList;
    }

    @Override
    public String execute() throws Exception {

        System.out.println("DS-ID : "+dataSetID);

        System.out.println("Criteria : "+criteriaType);

        System.out.println("G-ID : "+groupID);

        totalDataElements = 0;

        if(jdbcTemplate!=null  && criteriaType.equalsIgnoreCase("datasets"))
        {

            String inventoryQueryString = "SELECT INV_MD.GroupSetName, INV_MD.GroupName, INV_MD.dataelementid AS 'DE ID', INV_MD.Dataelement, INV_Category.Category, INV_MD.Frequency, INV_MD.DataSet, INV_MD.FromDate, INV_MD.ToDate, INV_MD.TotalRecords, INV_Source.Source, INV_Unit.Unit\n" +
                    "FROM\n" +
                    "(\n" +
                    "SELECT DE.dataelementid AS dataelementid,DEGS.name AS 'GroupSetName', DEG.name AS 'GroupName', DE.name AS 'Dataelement', DPT.name AS 'Frequency', DS.name AS 'DataSet', MIN(DP.startdate) AS 'FromDate', MAX(DP.enddate) AS 'ToDate', COUNT(DV.value) AS 'TotalRecords', DV.categoryoptioncomboid\n" +
                    "FROM dataelement DE\n" +
                    "LEFT JOIN datavalue DV ON DE.dataelementid=DV.dataelementid\n" +
                    "LEFT JOIN datasetmembers DSM ON DSM.dataelementid=DV.dataelementid\n" +
                    "LEFT JOIN dataset DS ON DS.datasetid = DSM.datasetid\n" +
                    "LEFT JOIN period DP ON DP.periodid=DV.periodid\n" +
                    "LEFT JOIN periodtype DPT ON DPT.periodtypeid=DP.periodtypeid\n" +
                    "LEFT JOIN dataelementgroupmembers DEGM ON DEGM.dataelementid = DV.dataelementid\n" +
                    "LEFT JOIN dataelementgroup DEG ON DEG.dataelementgroupid=DEGM.dataelementgroupid\n" +
                    "LEFT JOIN dataelementgroupsetmembers DEGSM ON DEGSM.dataelementgroupid=DEGM.dataelementgroupid\n" +
                    "LEFT JOIN dataelementgroupset DEGS ON DEGS.dataelementgroupsetid=DEGSM.dataelementgroupsetid\n" +
                    "LEFT JOIN organisationunit OU ON OU.organisationunitid=DV.sourceid\n" +
                    "WHERE DS.datasetid="+ dataSetID +
                     "\n" +
                    "GROUP BY DV.dataelementid, DV.categoryoptioncomboid\n" +
                    ") INV_MD\n" +
                    "LEFT JOIN\n" +
                    "(\n" +
                    "SELECT DISTINCT(DE.dataelementid), DE.name AS 'Dataelement', CCN.categoryoptioncomboname AS 'Category'\n" +
                    "FROM dataelement DE\n" +
                    "LEFT JOIN _dataelementcategoryoptioncombo DECC ON DECC.dataelementuid=DE.uid\n" +
                    "LEFT JOIN categoryoptioncombo CC ON CC.uid=DECC.categoryoptioncombouid\n" +
                    "LEFT JOIN _categoryoptioncomboname CCN ON CCN.categoryoptioncomboid=CC.categoryoptioncomboid\n" +
                    "GROUP BY DE.name, CCN.categoryoptioncomboname\n" +
                    ") INV_Category ON INV_MD.dataelement=INV_Category.Dataelement\n" +
                    "LEFT JOIN\n" +
                    "(\n" +
                    "SELECT DISTINCT(DE.dataelementid), DE.name AS 'Dataelement', CASE WHEN AV.attributeid=2 THEN AV.value END AS 'Source'\n" +
                    "FROM dataelement DE\n" +
                    "LEFT JOIN dataelementattributevalues DEAV ON DE.dataelementid=DEAV.dataelementid\n" +
                    "LEFT JOIN attributevalue AV ON AV.attributevalueid=DEAV.attributevalueid\n" +
                    ") INV_Source ON INV_MD.Dataelement=INV_Source.Dataelement\n" +
                    "LEFT JOIN\n" +
                    "(\n" +
                    "SELECT DISTINCT(DE.dataelementid), DE.name AS 'Dataelement', AV.value AS 'Unit'\n" +
                    "FROM dataelement DE\n" +
                    "INNER JOIN dataelementattributevalues DEAV ON DE.dataelementid=DEAV.dataelementid\n" +
                    "INNER JOIN attributevalue AV ON AV.attributevalueid=DEAV.attributevalueid\n" +
                    "WHERE AV.attributeid=1\n" +
                    ") INV_Unit ON INV_MD.Dataelement=INV_Unit.Dataelement\n" +
                    "GROUP BY INV_MD.dataelementid, INV_Category.Category\n" +
                    "ORDER BY INV_MD.dataelementid";

            System.out.println("=====================================================================================");
            System.out.println(inventoryQueryString);
            System.out.println("=====================================================================================");

            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

            Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

            int i = 1;

            while(sqlRowSet.next())
            {
                ++totalDataElements;

                DataInventoryStatusInfo dataInventoryStatusInfo = new DataInventoryStatusInfo();

                dataInventoryStatusInfo.setSerialNumber(i);

                if(sqlRowSet.getString(1)!=null)
                {
                    dataInventoryStatusInfo.setGroupSetName(sqlRowSet.getString(1));
                }
                else
                {
                    dataInventoryStatusInfo.setGroupSetName("-");
                }

                if(sqlRowSet.getString(2)!=null)
                {
                    dataInventoryStatusInfo.setGroupName(sqlRowSet.getString(2));
                }
                else
                {
                    dataInventoryStatusInfo.setGroupName("-");
                }

                if(sqlRowSet.getString(3)!=null)
                {
                    dataInventoryStatusInfo.setDataElementID(sqlRowSet.getString(3));
                }
                else
                {
                    dataInventoryStatusInfo.setDataElementID("-");
                }

                if(sqlRowSet.getString(4)!=null)
                {
                    dataInventoryStatusInfo.setDataElementName(sqlRowSet.getString(4));
                }
                else
                {
                    dataInventoryStatusInfo.setDataElementName("-");
                }

                if(sqlRowSet.getString(5)!=null)
                {
                    String category = sqlRowSet.getString(5).replaceAll("[\\(\\)]", "");
                    dataInventoryStatusInfo.setCategory(category);
                }
                else
                {
                    dataInventoryStatusInfo.setCategory("-");
                }

                if(sqlRowSet.getString(6)!=null)
                {
                    dataInventoryStatusInfo.setFrequency(sqlRowSet.getString(6));
                }
                else
                {
                    dataInventoryStatusInfo.setFrequency("-");
                }

                if(sqlRowSet.getString(7)!=null)
                {
                    dataInventoryStatusInfo.setDataSet(sqlRowSet.getString(7));
                }
                else
                {
                    dataInventoryStatusInfo.setDataSet("-");
                }

                if(sqlRowSet.getString(8)!=null)
                {
                    dataInventoryStatusInfo.setFromDate(sqlRowSet.getString(8));
                }
                else
                {
                    dataInventoryStatusInfo.setFromDate("-");
                }

                if(sqlRowSet.getString(9)!=null)
                {
                    dataInventoryStatusInfo.setToDate(sqlRowSet.getString(9));
                }
                else
                {
                    dataInventoryStatusInfo.setToDate("-");
                }

                if(sqlRowSet.getString(10)!=null)
                {
                    dataInventoryStatusInfo.setTotalRecords(sqlRowSet.getString(10));
                }
                else
                {
                    dataInventoryStatusInfo.setTotalRecords("-");
                }

                if(sqlRowSet.getString(11)!=null)
                {
                    dataInventoryStatusInfo.setSource(sqlRowSet.getString(11));
                }
                else
                {
                    dataInventoryStatusInfo.setSource("-");
                }

                if(sqlRowSet.getString(12)!=null)
                {
                    dataInventoryStatusInfo.setUnit(sqlRowSet.getString(12));
                }
                else
                {
                    dataInventoryStatusInfo.setUnit("-");
                }

                ++i;

                inventoryList.add(dataInventoryStatusInfo);


            }


        }

        else if(jdbcTemplate!=null  && criteriaType.equalsIgnoreCase("groups"))
        {

            String inventoryQueryString = "SELECT INV_MD.GroupSetName, INV_MD.GroupName, INV_MD.dataelementid AS 'DE ID', INV_MD.Dataelement, INV_Category.Category, INV_MD.Frequency, INV_MD.DataSet, INV_MD.FromDate, INV_MD.ToDate, INV_MD.TotalRecords, INV_Source.Source, INV_Unit.Unit\n" +
                    "FROM\n" +
                    "(\n" +
                    "SELECT DE.dataelementid AS dataelementid,DEGS.name AS 'GroupSetName', DEG.name AS 'GroupName', DE.name AS 'Dataelement', DPT.name AS 'Frequency', DS.name AS 'DataSet', MIN(DP.startdate) AS 'FromDate', MAX(DP.enddate) AS 'ToDate', COUNT(DV.value) AS 'TotalRecords', DV.categoryoptioncomboid\n" +
                    "FROM dataelement DE\n" +
                    "LEFT JOIN datavalue DV ON DE.dataelementid=DV.dataelementid\n" +
                    "LEFT JOIN datasetmembers DSM ON DSM.dataelementid=DV.dataelementid\n" +
                    "LEFT JOIN dataset DS ON DS.datasetid = DSM.datasetid\n" +
                    "LEFT JOIN period DP ON DP.periodid=DV.periodid\n" +
                    "LEFT JOIN periodtype DPT ON DPT.periodtypeid=DP.periodtypeid\n" +
                    "LEFT JOIN dataelementgroupmembers DEGM ON DEGM.dataelementid = DV.dataelementid\n" +
                    "LEFT JOIN dataelementgroup DEG ON DEG.dataelementgroupid=DEGM.dataelementgroupid\n" +
                    "LEFT JOIN dataelementgroupsetmembers DEGSM ON DEGSM.dataelementgroupid=DEGM.dataelementgroupid\n" +
                    "LEFT JOIN dataelementgroupset DEGS ON DEGS.dataelementgroupsetid=DEGSM.dataelementgroupsetid\n" +
                    "LEFT JOIN organisationunit OU ON OU.organisationunitid=DV.sourceid\n" +
                    "WHERE DEG.dataelementgroupid="+ groupID +"\n" +
                    "GROUP BY DV.dataelementid, DV.categoryoptioncomboid\n" +
                    ") INV_MD\n" +
                    "LEFT JOIN\n" +
                    "(\n" +
                    "SELECT DISTINCT(DE.dataelementid), DE.name AS 'Dataelement', CCN.categoryoptioncomboname AS 'Category'\n" +
                    "FROM dataelement DE\n" +
                    "LEFT JOIN _dataelementcategoryoptioncombo DECC ON DECC.dataelementuid=DE.uid\n" +
                    "LEFT JOIN categoryoptioncombo CC ON CC.uid=DECC.categoryoptioncombouid\n" +
                    "LEFT JOIN _categoryoptioncomboname CCN ON CCN.categoryoptioncomboid=CC.categoryoptioncomboid\n" +
                    "GROUP BY DE.name, CCN.categoryoptioncomboname\n" +
                    ") INV_Category ON INV_MD.dataelement=INV_Category.Dataelement\n" +
                    "LEFT JOIN\n" +
                    "(\n" +
                    "SELECT DISTINCT(DE.dataelementid), DE.name AS 'Dataelement', CASE WHEN AV.attributeid=2 THEN AV.value END AS 'Source'\n" +
                    "FROM dataelement DE\n" +
                    "LEFT JOIN dataelementattributevalues DEAV ON DE.dataelementid=DEAV.dataelementid\n" +
                    "LEFT JOIN attributevalue AV ON AV.attributevalueid=DEAV.attributevalueid\n" +
                    ") INV_Source ON INV_MD.Dataelement=INV_Source.Dataelement\n" +
                    "LEFT JOIN\n" +
                    "(\n" +
                    "SELECT DISTINCT(DE.dataelementid), DE.name AS 'Dataelement', AV.value AS 'Unit'\n" +
                    "FROM dataelement DE\n" +
                    "INNER JOIN dataelementattributevalues DEAV ON DE.dataelementid=DEAV.dataelementid\n" +
                    "INNER JOIN attributevalue AV ON AV.attributevalueid=DEAV.attributevalueid\n" +
                    "WHERE AV.attributeid=1\n" +
                    ") INV_Unit ON INV_MD.Dataelement=INV_Unit.Dataelement\n" +
                    "GROUP BY INV_MD.dataelementid, INV_Category.Category\n" +
                    "ORDER BY INV_MD.dataelementid";


            System.out.println("=====================================================================================");
            System.out.println(inventoryQueryString);
            System.out.println("=====================================================================================");


            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

            Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

            int i = 1;

            while(sqlRowSet.next())
            {
                ++totalDataElements;

                DataInventoryStatusInfo dataInventoryStatusInfo = new DataInventoryStatusInfo();

                dataInventoryStatusInfo.setSerialNumber(i);

                if(sqlRowSet.getString(1)!=null)
                {
                    dataInventoryStatusInfo.setGroupSetName(sqlRowSet.getString(1));
                }
                else
                {
                    dataInventoryStatusInfo.setGroupSetName("-");
                }

                if(sqlRowSet.getString(2)!=null)
                {
                    dataInventoryStatusInfo.setGroupName(sqlRowSet.getString(2));
                }
                else
                {
                    dataInventoryStatusInfo.setGroupName("-");
                }

                if(sqlRowSet.getString(3)!=null)
                {
                    dataInventoryStatusInfo.setDataElementID(sqlRowSet.getString(3));
                }
                else
                {
                    dataInventoryStatusInfo.setDataElementID("-");
                }

                if(sqlRowSet.getString(4)!=null)
                {
                    dataInventoryStatusInfo.setDataElementName(sqlRowSet.getString(4));
                }
                else
                {
                    dataInventoryStatusInfo.setDataElementName("-");
                }

                if(sqlRowSet.getString(5)!=null)
                {
                    String category = sqlRowSet.getString(5).replaceAll("[\\(\\)]", "");
                    dataInventoryStatusInfo.setCategory(category);
                }
                else
                {
                    dataInventoryStatusInfo.setCategory("-");
                }

                if(sqlRowSet.getString(6)!=null)
                {
                    dataInventoryStatusInfo.setFrequency(sqlRowSet.getString(6));
                }
                else
                {
                    dataInventoryStatusInfo.setFrequency("-");
                }

                if(sqlRowSet.getString(7)!=null)
                {
                    dataInventoryStatusInfo.setDataSet(sqlRowSet.getString(7));
                }
                else
                {
                    dataInventoryStatusInfo.setDataSet("-");
                }

                if(sqlRowSet.getString(8)!=null)
                {
                    dataInventoryStatusInfo.setFromDate(sqlRowSet.getString(8));
                }
                else
                {
                    dataInventoryStatusInfo.setFromDate("-");
                }

                if(sqlRowSet.getString(9)!=null)
                {
                    dataInventoryStatusInfo.setToDate(sqlRowSet.getString(9));
                }
                else
                {
                    dataInventoryStatusInfo.setToDate("-");
                }

                if(sqlRowSet.getString(10)!=null)
                {
                    dataInventoryStatusInfo.setTotalRecords(sqlRowSet.getString(10));
                }
                else
                {
                    dataInventoryStatusInfo.setTotalRecords("-");
                }

                if(sqlRowSet.getString(11)!=null)
                {
                    dataInventoryStatusInfo.setSource(sqlRowSet.getString(11));
                }
                else
                {
                    dataInventoryStatusInfo.setSource("-");
                }

                if(sqlRowSet.getString(12)!=null)
                {
                    dataInventoryStatusInfo.setUnit(sqlRowSet.getString(12));
                }
                else
                {
                    dataInventoryStatusInfo.setUnit("-");
                }

                ++i;

                inventoryList.add(dataInventoryStatusInfo);


            }


        }

        else if(jdbcTemplate!=null  && criteriaType.equalsIgnoreCase("all"))
        {

            String inventoryQueryString = "SELECT *\n" +
                    "FROM " + "_mdinventory;";

            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

            Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

            int i = 1;

            while(sqlRowSet.next())
            {
                ++totalDataElements;

                DataInventoryStatusInfo dataInventoryStatusInfo = new DataInventoryStatusInfo();

                dataInventoryStatusInfo.setSerialNumber(i);

                if(sqlRowSet.getString(1)!=null)
                {
                    dataInventoryStatusInfo.setGroupSetName(sqlRowSet.getString(1));
                }
                else
                {
                    dataInventoryStatusInfo.setGroupSetName("-");
                }

                if(sqlRowSet.getString(2)!=null)
                {
                    dataInventoryStatusInfo.setGroupName(sqlRowSet.getString(2));
                }
                else
                {
                    dataInventoryStatusInfo.setGroupName("-");
                }

                if(sqlRowSet.getString(3)!=null)
                {
                    dataInventoryStatusInfo.setDataElementID(sqlRowSet.getString(3));
                }
                else
                {
                    dataInventoryStatusInfo.setDataElementID("-");
                }

                if(sqlRowSet.getString(4)!=null)
                {
                    dataInventoryStatusInfo.setDataElementName(sqlRowSet.getString(4));
                }
                else
                {
                    dataInventoryStatusInfo.setDataElementName("-");
                }

                if(sqlRowSet.getString(5)!=null)
                {
                    String category = sqlRowSet.getString(5).replaceAll("[\\(\\)]", "");
                    dataInventoryStatusInfo.setCategory(category);
                }
                else
                {
                    dataInventoryStatusInfo.setCategory("-");
                }

                if(sqlRowSet.getString(6)!=null)
                {
                    dataInventoryStatusInfo.setFrequency(sqlRowSet.getString(6));
                }
                else
                {
                    dataInventoryStatusInfo.setFrequency("-");
                }

                if(sqlRowSet.getString(7)!=null)
                {
                    dataInventoryStatusInfo.setDataSet(sqlRowSet.getString(7));
                }
                else
                {
                    dataInventoryStatusInfo.setDataSet("-");
                }

                if(sqlRowSet.getString(8)!=null)
                {
                    dataInventoryStatusInfo.setFromDate(sqlRowSet.getString(8));
                }
                else
                {
                    dataInventoryStatusInfo.setFromDate("-");
                }

                if(sqlRowSet.getString(9)!=null)
                {
                    dataInventoryStatusInfo.setToDate(sqlRowSet.getString(9));
                }
                else
                {
                    dataInventoryStatusInfo.setToDate("-");
                }

                if(sqlRowSet.getString(10)!=null)
                {
                    dataInventoryStatusInfo.setTotalRecords(sqlRowSet.getString(10));
                }
                else
                {
                    dataInventoryStatusInfo.setTotalRecords("-");
                }

                if(sqlRowSet.getString(11)!=null)
                {
                    dataInventoryStatusInfo.setSource(sqlRowSet.getString(11));
                }
                else
                {
                    dataInventoryStatusInfo.setSource("-");
                }

                if(sqlRowSet.getString(12)!=null)
                {
                    dataInventoryStatusInfo.setUnit(sqlRowSet.getString(12));
                }
                else
                {
                    dataInventoryStatusInfo.setUnit("-");
                }

                ++i;

                inventoryList.add(dataInventoryStatusInfo);


            }

        }

        else if(jdbcTemplate!=null  && criteriaType.equalsIgnoreCase("allin"))
        {

            String inventoryQueryString = "SELECT din.indicatorid as 'ID', din.name as 'Name', ding.name as 'Group', dings.name as 'Group Set', dint.name as 'Type', du.username as 'Created By' " +
                    "FROM indicator din " +
                    "LEFT JOIN indicatorgroupmembers dingm ON dingm.indicatorid=din.indicatorid " +
                    "LEFT JOIN indicatortype dint ON dint.indicatortypeid=din.indicatortypeid " +
                    "LEFT JOIN users du ON din.userid=du.userid " +
                    "LEFT JOIN indicatorgroup ding ON ding.indicatorgroupid=dingm.indicatorgroupid " +
                    "LEFT JOIN indicatorgroupsetmembers dingsm ON dingsm.indicatorgroupid=ding.indicatorgroupid " +
                    "LEFT JOIN indicatorgroupset dings ON dings.indicatorgroupsetid=dingsm.indicatorgroupsetid ; ";

            System.out.println("=====================================================================================");
            System.out.println(inventoryQueryString);
            System.out.println("=====================================================================================");


            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

            Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

            int i = 1;

            while(sqlRowSet.next())
            {
                ++totalDataElements;

                IndicatorInventoryStatusInfo indicatorInventoryStatusInfo = new IndicatorInventoryStatusInfo();

                indicatorInventoryStatusInfo.setSerialNumber(i);

                if(sqlRowSet.getString(1)!=null)
                {
                    indicatorInventoryStatusInfo.setIndicatorID(sqlRowSet.getString(1));
                }
                else
                {
                    indicatorInventoryStatusInfo.setIndicatorID("-");
                }

                if(sqlRowSet.getString(2)!=null)
                {
                    indicatorInventoryStatusInfo.setIndicatorName(sqlRowSet.getString(2));
                }
                else
                {
                    indicatorInventoryStatusInfo.setIndicatorName("-");
                }

                if(sqlRowSet.getString(3)!=null)
                {
                    indicatorInventoryStatusInfo.setIndicatorGroup(sqlRowSet.getString(3));
                }
                else
                {
                    indicatorInventoryStatusInfo.setIndicatorGroup("-");
                }

                if(sqlRowSet.getString(4)!=null)
                {
                    indicatorInventoryStatusInfo.setIndicatorGroupSet(sqlRowSet.getString(4));
                }
                else
                {
                    indicatorInventoryStatusInfo.setIndicatorGroupSet("-");
                }

                if(sqlRowSet.getString(5)!=null)
                {
                    indicatorInventoryStatusInfo.setIndicatorType(sqlRowSet.getString(5));
                }
                else
                {
                    indicatorInventoryStatusInfo.setIndicatorType("-");
                }

                if(sqlRowSet.getString(6)!=null)
                {
                    indicatorInventoryStatusInfo.setUserName(sqlRowSet.getString(6));
                }
                else
                {
                    indicatorInventoryStatusInfo.setUserName("-");
                }

                ++i;

                indicatorInventoryList.add(indicatorInventoryStatusInfo);

            }

        }

        return SUCCESS;

    }
}