package org.hisp.dhis.reports.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.Action;

public class ShowImageAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

	private String imagePath;

	public void setImagePath(String imagePath) 
	{
		this.imagePath = imagePath;
	}
	
	private byte[] bimage;
    
    public byte[] getBimage()
    {
        return bimage;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {

        File file = new File( imagePath );
        bimage = new byte[(int) file.length()];
        
        try 
        {
            FileInputStream fileInputStream = new FileInputStream( file );
            //convert file into array of bytes
            fileInputStream.read( bimage );
            fileInputStream.close();
       } 
       catch (Exception e) 
       {
            //e.printStackTrace();
       }
        
       try 
       {    	   
           HttpServletResponse response = ServletActionContext.getResponse();
           response.reset();
           response.setContentType("multipart/form-data"); 

           OutputStream out = response.getOutputStream();
    	   //OutputStream out = null;
           out.write( bimage );
           out.flush();
           out.close();
       }
       catch (IOException e) 
       {
           //e.printStackTrace();
       }
                
       return SUCCESS;
    }
	
}
