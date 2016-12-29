package org.hisp.dhis.userstatus.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.user.UserService;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by gaurav on 9/1/14.
 */
public class UpdateAlertSettingsAction implements Action {

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private int userID;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    private UserService userService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private Configuration_IN configurationIn;



    @Override
    public String execute() throws Exception {

        return null;


    }
}
