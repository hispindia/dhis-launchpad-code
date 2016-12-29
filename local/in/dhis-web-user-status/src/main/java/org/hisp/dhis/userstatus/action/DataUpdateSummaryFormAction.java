package org.hisp.dhis.userstatus.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;

import java.security.PrivateKey;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 6/12/13
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class DataUpdateSummaryFormAction implements Action {


    private UserService userService;

    private DataSetService dataSetService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public DataSetService getDataSetService() {
        return dataSetService;
    }

    public void setDataSetService(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }

    private Collection<DataSet> dataSetList = new ArrayList<DataSet>();

    private List<User> userList = new ArrayList<User>();

    public Collection<DataSet> getDataSetList() {
        return dataSetList;
    }

    public void setDataSetList(Collection<DataSet> dataSetList) {
        this.dataSetList = dataSetList;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }


    class ComparatorClass implements Comparator<User> {
        @Override
        public int compare(User userOne, User userTwo) {
            return userOne.getName().compareTo(userTwo.getName());
        }
    }


    @Override
    public String execute() throws Exception {

        dataSetList = dataSetService.getAllDataSets();

        userList = (List<User>) userService.getAllUsers();

        Collections.sort(userList, new ComparatorClass());

        return SUCCESS;
    }


}
