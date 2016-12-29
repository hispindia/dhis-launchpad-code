package org.hisp.dhis.alert.util;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 10/10/13
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataInventoryStatusInfo {

    private int serialNumber;

    private String groupSetName;

    private String groupName;

    private String dataElementID;

    private String dataElementName;

    private String category;

    private String frequency;

    private String dataSet;

    private String fromDate;

    private String toDate;

    private String totalRecords;

    private String source;

    private String unit;

    public String getDataElementID() {
        return dataElementID;
    }

    public void setDataElementID(String dataElementID) {
        this.dataElementID = dataElementID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getGroupSetName() {
        return groupSetName;
    }

    public void setGroupSetName(String groupSetName) {
        this.groupSetName = groupSetName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDataElementName() {
        return dataElementName;
    }

    public void setDataElementName(String dataElementName) {
        this.dataElementName = dataElementName;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
