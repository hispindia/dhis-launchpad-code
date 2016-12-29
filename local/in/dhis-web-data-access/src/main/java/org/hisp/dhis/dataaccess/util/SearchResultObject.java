package org.hisp.dhis.alert.util;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 22/11/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchResultObject {

    public String dataElement;

    public String orgUnit;

    public String period;

    public String value;

    public String category;

    public String getDataElement() {
        return dataElement;
    }

    public void setDataElement(String dataElement) {
        this.dataElement = dataElement;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
