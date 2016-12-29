package org.hisp.dhis.alert.action;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ExportToExcelAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

	@Autowired
    private DataSetService dataSetService;

    
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private InputStream inputStream;

    public void setInputStream( InputStream inputStream )
    {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private String htmlCode;
    
    public void setHtmlCode( String htmlCode )
    {
        this.htmlCode = htmlCode;
    }    
    
    private String dataSetId;

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public String getDataSetId()
    {
        return dataSetId;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {                                        
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );

        fileName = dataSet.getShortName() + "_" + startDate + "_" + endDate + ".xls";
        
        if( htmlCode != null )
        {
            inputStream = new BufferedInputStream( new ByteArrayInputStream( htmlCode.getBytes("UTF-8") ) );
        }
        
        return SUCCESS;
    }

}
