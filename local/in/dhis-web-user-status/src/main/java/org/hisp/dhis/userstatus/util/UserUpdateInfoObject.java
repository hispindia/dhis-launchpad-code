package org.hisp.dhis.userstatus.util;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 7/12/13
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserUpdateInfoObject {

    public int serialNumber;

    public String dataSetName;

    public String updatedOn;

    public String DataEnteredFor;

    public String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getDataEnteredFor() {
        return DataEnteredFor;
    }

    public void setDataEnteredFor(String dataEnteredFor) {
        DataEnteredFor = dataEnteredFor;
    }
}
