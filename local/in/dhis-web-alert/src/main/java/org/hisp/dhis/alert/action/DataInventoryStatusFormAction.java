package org.hisp.dhis.alert.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Gaurav
 * Date: 11/10/13
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataInventoryStatusFormAction implements Action {

    private DataElementService dataElementService;

    public DataElementService getDataElementService() {
        return dataElementService;
    }

    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }

    private Collection<DataElementGroup> groupArrayList = new ArrayList<DataElementGroup>();

    public Collection<DataElementGroup> getGroupArrayList() {
        return groupArrayList;
    }

    public void setGroupSetArrayList(Collection<DataElementGroupSet> groupSetArrayList) {
        this.groupArrayList = groupArrayList;
    }

    private DataSetService dataSetService;

    public DataSetService getDataSetService() {
        return dataSetService;
    }

    private Collection<DataSet> dataSetArrayList = new ArrayList<DataSet>();

    public Collection<DataSet> getDataSetArrayList() {
        return dataSetArrayList;
    }

    public void setDataSetArrayList(ArrayList<DataSet> dataSetArrayList) {
        this.dataSetArrayList = dataSetArrayList;
    }

    public void setDataSetService(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }

    private DataElementGroupSet dataElementGroupSet;

    public DataElementGroupSet getDataElementGroupSet() {
        return dataElementGroupSet;
    }

    public void setDataElementGroupSet(DataElementGroupSet dataElementGroupSet) {
        this.dataElementGroupSet = dataElementGroupSet;
    }

    private int groupSetID;

    public int getGroupSetID() {
        return groupSetID;
    }

    public void setGroupSetID(int groupSetID) {
        this.groupSetID = groupSetID;
    }

    @Override
    public String execute() throws Exception {

        dataSetArrayList = dataSetService.getAllDataSets();

        groupArrayList = dataElementService.getAllDataElementGroups();



        return SUCCESS;
    }


}
