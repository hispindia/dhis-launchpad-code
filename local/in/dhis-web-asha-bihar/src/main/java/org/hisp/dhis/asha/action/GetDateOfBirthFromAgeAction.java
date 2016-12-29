package org.hisp.dhis.asha.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetDateOfBirthFromAgeAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer age;
   
    public void setAge( Integer age )
    {
        this.age = age;
    }

    private String dateOfBirth;
    
    public String getDateOfBirth()
    {
        return dateOfBirth;
    }
    
    private SimpleDateFormat simpleDateFormat;
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------


    public String execute()
    {
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.clear( Calendar.MILLISECOND );
        todayCalendar.clear( Calendar.SECOND );
        todayCalendar.clear( Calendar.MINUTE );
        todayCalendar.set( Calendar.HOUR_OF_DAY, 0 );
        
        if ( age != null )
        {
            todayCalendar.add( Calendar.YEAR, -1 * age );
        }
        
        
        //dateOfBirth = todayCalendar.getTime().toString();
        
        dateOfBirth = simpleDateFormat.format( todayCalendar.getTime() );
        
        //System.out.println( "Date Of Birth of ASHA : " + dateOfBirth );

        return SUCCESS;
    }
}