package org.hisp.dhis.alert.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.alert.util.DataInventoryStatusInfo;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.datavalue.DefaultDataValueAuditService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 18/11/13
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */

public class SearchStartEndDateAction implements Action {

    private DataValueService dataValueService;

    public DataValueService getDataValueService() {
        return dataValueService;
    }

    public void setDataValueService(DataValueService dataValueService) {
        this.dataValueService = dataValueService;
    }

    private int dataElementID;

    public int getDataElementID() {
        return dataElementID;
    }

    public void setDataElementID(int dataElementID) {
        this.dataElementID = dataElementID;
    }

    private String startDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    private String endDate;

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    private String periodType;

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String DataElementName;

    public String getDataElementName() {
        return DataElementName;
    }

    public void setDataElementName(String dataElementName) {
        DataElementName = dataElementName;
    }

    private DataElementService dataElementService;

    public DataElementService getDataElementService() {
        return dataElementService;
    }

    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }

    public Collection<DataElement> allDataElements = new ArrayList<DataElement>();

    public Collection<DataElement> getAllDataElements() {
        return allDataElements;
    }

    public void setAllDataElements(Collection<DataElement> allDataElements) {
        this.allDataElements = allDataElements;
    }

    @Override
    public String execute() throws Exception {

        allDataElements = dataElementService.getAllDataElements();

        if(jdbcTemplate!=null)
        {

            String inventoryQueryString = "select DPT.name as 'periodtype' ,(DP.startdate), max(DP.startdate)\n" +
                    "from datavalue DV\n" +
                    "inner join period DP on DV.periodid=DP.periodid\n" +
                    "inner join periodtype DPT on DPT.periodtypeid=DP.periodtypeid\n" +
                    "where DV.dataelementid="+dataElementID+"\n" +
                    "group by DV.dataelementid;\n";

            System.out.println("---------------------------------------------------------------------------");

            System.out.println(inventoryQueryString);

            System.out.println("---------------------------------------------------------------------------");

            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

            Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

            if(columnCount==3)
            {
                while(sqlRowSet.next())
                {
                periodType = sqlRowSet.getString(1);
                startDate = "\""+sqlRowSet.getString(2)+"\"";
                endDate = "\""+sqlRowSet.getString(3)+"\"";
                System.out.println(periodType +":"+ startDate +"||"+ endDate);
                }
            }
        }

        return SUCCESS;
    }
}
