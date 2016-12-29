package org.hisp.dhis.alert.util;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 3/12/13
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class IndicatorInventoryStatusInfo {

    public int serialNumber;

    public String indicatorType;

    public String indicatorGroupSet;

    public String indicatorGroup;

    public String indicatorID;

    public String indicatorName;

    public String userName;

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public String getIndicatorGroupSet() {
        return indicatorGroupSet;
    }

    public void setIndicatorGroupSet(String indicatorGroupSet) {
        this.indicatorGroupSet = indicatorGroupSet;
    }

    public String getIndicatorGroup() {
        return indicatorGroup;
    }

    public void setIndicatorGroup(String indicatorGroup) {
        this.indicatorGroup = indicatorGroup;
    }

    public String getIndicatorID() {
        return indicatorID;
    }

    public void setIndicatorID(String indicatorID) {
        this.indicatorID = indicatorID;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
