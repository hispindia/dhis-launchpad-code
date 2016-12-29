/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hisp.dhis.mobile.action;


import com.opensymphony.xwork2.Action;
import java.util.Calendar;
import java.util.Date;
import org.hisp.dhis.mobile.SmsService;
import org.hisp.dhis.mobile.api.MobileImportService;

/**
 *
 * @author harsh
 */

public class HttpReceiveSmsAction implements Action
{
String data;
String result="hmmm";

MobileImportService mobileImportService;
 SmsService smsService;

    public void setSmsService( SmsService smsService )
    {
        this.smsService = smsService;
    }
    public MobileImportService getMobileImportService()
    {
        return mobileImportService;
    }

    public String getResult()
    {
        return result;
    }
    public void setData( String data )
    {
        this.data = data;
        this.data="2#46*3?2009-04-01$1|2|2";
    }

    @Override
    public String execute() throws Exception
    {
        
        String phone="123456789";
        Date date=Calendar.getInstance().getTime();
        System.out.println("this should get printed."+data);
        result="its done"+data;
     //  chuong here we create xml but first we need to extract the data from what is posted 
     //  smsService.createXMLFile(phone,date,data);         
     return SUCCESS;
    }
    
}
