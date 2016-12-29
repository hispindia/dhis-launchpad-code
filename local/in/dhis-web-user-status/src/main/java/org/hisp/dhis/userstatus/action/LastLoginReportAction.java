package org.hisp.dhis.userstatus.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.userstatus.util.UserInfoObject;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: gaurav
 * Date: 5/12/13
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class LastLoginReportAction implements Action {

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<UserInfoObject> infoList = new ArrayList<UserInfoObject>();

    public Collection<UserInfoObject> getInfoList() {
        return infoList;
    }

    public void setInfoList(Collection<UserInfoObject> infoList) {
        this.infoList = infoList;
    }


    public int totalItems;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    @Override
    public String execute() throws Exception {


        if(jdbcTemplate!=null)
        {

            String inventoryQueryString = "SELECT du.userid AS 'ID', du.username AS 'Username', dui.firstName AS 'FirstName', GROUP_CONCAT(dur.name) AS 'Roles', dui.surname AS 'LastName', dui.email AS 'Email', DATE(du.lastLogin) AS 'Last Login', TIME_FORMAT(TIME(du.lastLogin), '%T %p') AS 'Last Login Time' " +
                    "FROM users du " +
                    "INNER JOIN userinfo dui ON dui.userinfoid=du.userid " +
                    "LEFT JOIN userrolemembers durm ON durm.userid=du.userid " +
                    "LEFT JOIN userrole dur ON dur.userroleid=durm.userroleid " +
                    "GROUP BY du.userid; ";

            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(inventoryQueryString);

            Integer columnCount = sqlRowSet.getMetaData().getColumnCount();

            for(int colIndex= 1 ; colIndex <= columnCount ; ++colIndex)
            {
                System.out.print(sqlRowSet.getMetaData().getColumnName(colIndex)+"  ");
            }

            int i = 1;

            while(sqlRowSet.next())
            {
                UserInfoObject userInfoObject = new UserInfoObject();

                userInfoObject.setSerialNumber(i);

                if(sqlRowSet.getString(1)!=null)
                {
                    userInfoObject.setID(sqlRowSet.getString(1));
                }
                else
                {
                    userInfoObject.setID("-");
                }

                if(sqlRowSet.getString(2)!=null)
                {
                    userInfoObject.setUserName(sqlRowSet.getString(2));
                }
                else
                {
                    userInfoObject.setUserName("-");
                }

                if(sqlRowSet.getString(3)!=null)
                {
                    userInfoObject.setFirstName(sqlRowSet.getString(3));
                }
                else
                {
                    userInfoObject.setFirstName("-");
                }

                if(sqlRowSet.getString(4)!=null)
                {
                    userInfoObject.setRoles(sqlRowSet.getString(4));
                }
                else
                {
                    userInfoObject.setRoles("-");
                }

                if(sqlRowSet.getString(5)!=null)
                {
                    userInfoObject.setLastName(sqlRowSet.getString(5));
                }
                else
                {
                    userInfoObject.setLastName("-");
                }

                if(sqlRowSet.getString(6)!=null)
                {
                    userInfoObject.setEmail(sqlRowSet.getString(6));
                }
                else
                {
                    userInfoObject.setEmail("-");
                }

                if(sqlRowSet.getString(7)!= null)
                {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    Date date = dateFormat.parse(sqlRowSet.getString(7));

                    Calendar calendar = Calendar.getInstance();

                    Date currDate = new Date();

                    calendar.setTime(currDate);

                    calendar.add(Calendar.WEEK_OF_YEAR,-1);

                    Date weekOld = calendar.getTime();

                    if(date.before(weekOld))
                    {
                        userInfoObject.setLastLogin("<p style='color:red'>"+sqlRowSet.getString(7)+"</p>");
                        userInfoObject.setLastLoginTime("<p style='color:red'>"+sqlRowSet.getString(8)+"</p>");
                    }
                    else
                    {
                        userInfoObject.setLastLogin(sqlRowSet.getString(7));
                        userInfoObject.setLastLoginTime(sqlRowSet.getString(8));
                    }

                }
                else
                {
                    userInfoObject.setLastLogin("-");
                    userInfoObject.setLastLoginTime("-");
                }

                ++i;

                infoList.add(userInfoObject);

                System.out.println(userInfoObject.getSerialNumber()+":"+userInfoObject.getUserName()+":"+userInfoObject.getFirstName()+":"+userInfoObject.getLastName()+":"+userInfoObject.getEmail()+":"+userInfoObject.getLastLogin());

            }

            totalItems = i;

        }

        return SUCCESS;
    }
}
