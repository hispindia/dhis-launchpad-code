package org.hisp.dhis.alert.viewdata.action;


import com.opensymphony.xwork2.Action;

public class VerifyDataFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

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

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private String orgUnitIds;
    
    public String getOrgUnitIds()
    {
        return orgUnitIds;
    }

    public void setOrgUnitIds( String orgUnitIds )
    {
        this.orgUnitIds = orgUnitIds;
    }

    private String headingMessage;
    
    public String getHeadingMessage()
    {
        return headingMessage;
    }

    public void setHeadingMessage( String headingMessage )
    {
        this.headingMessage = headingMessage;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        System.out.println("Inside VerifyDataFormAction");
        
        return SUCCESS;
    }
}
