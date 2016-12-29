package org.hisp.dhis.dataaccess.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.alert.util.SearchResultObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
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
 * Date: 3/12/13
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetDataValuesAction implements Action {
    private String dataElementID;

    public String getDataElementID() {
        return dataElementID;
    }

    public void setDataElementID(String dataElementID) {
        this.dataElementID = dataElementID;
    }

    public Collection<SearchResultObject> resultList = new ArrayList<SearchResultObject>();

    public Collection<SearchResultObject> getResultList() {
        return resultList;
    }

    public void setResultList(Collection<SearchResultObject> resultList) {
        this.resultList = resultList;
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

    private String description;

    private String lastUpdated;

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String execute() throws Exception {

        System.out.println("********************************************************************");
        System.out.println(dataElementID);
        System.out.println("********************************************************************");

        if(jdbcTemplate!=null)
        {

            String inventoryQueryString = "SELECT de.name,ou.name,dv.value,dv.periodid,p.startdate,p.enddate,ccn.categoryoptioncomboname,pt.name\n" +
                    "FROM datavalue dv\n" +
                    "INNER JOIN dataelement de ON de.dataelementid=dv.dataelementid\n"+
                    "INNER JOIN period p ON p.periodid=dv.periodid\n" +
                    "INNER JOIN periodtype pt ON p.periodtypeid=pt.periodtypeid\n" +
                    "INNER JOIN organisationunit ou ON ou.organisationunitid=dv.sourceid\n"+
                    "INNER JOIN _categoryoptioncomboname ccn on ccn.categoryoptioncomboid=dv.categoryoptioncomboid"+
                    " WHERE dv.dataelementid IN ("+dataElementID+");";

            System.out.println("---------------------------------------------------------------------------");

            System.out.println(inventoryQueryString);

            System.out.println("---------------------------------------------------------------------------");

            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

            Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

            if(columnCount==8)
            {
                while(sqlRowSet.next())
                {

                    String dataElement = sqlRowSet.getString(1);
                    String orgUnit = sqlRowSet.getString(2);
                    String value = sqlRowSet.getString(3);
                    String startDate = sqlRowSet.getString(5);
                    String endDate = sqlRowSet.getString(6);
                    String category = sqlRowSet.getString(7);
                    String periodType = sqlRowSet.getString(8);

                    System.out.println(dataElement+":"+orgUnit+":"+value+":"+startDate+":"+endDate+":"+category+":"+periodType);

                    if(category.equalsIgnoreCase("(default)"))
                    {
                        category="-";
                    }
                    else
                    {
                        category = category.replace("(","");
                        category = category.replace(")","");
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

                    SearchResultObject resultObject = new SearchResultObject();

                    resultObject.setDataElement(dataElement);
                    resultObject.setOrgUnit(orgUnit);
                    resultObject.setPeriod(startDate);
                    resultObject.setValue(value);
                    resultObject.setCategory(category);

                    resultList.add(resultObject);

                    ++count;

                }
            }

        }

        return SUCCESS;
    }

}
