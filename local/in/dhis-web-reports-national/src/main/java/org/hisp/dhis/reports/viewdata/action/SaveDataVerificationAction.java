/**
 * 
 */
package org.hisp.dhis.reports.viewdata.action;

import com.opensymphony.xwork2.Action;

/**
 * @author BHARATH
 *
 */
public class SaveDataVerificationAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String dataSetId;

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private String orgUnitIds;

    public void setOrgUnitIds( String orgUnitIds )
    {
        this.orgUnitIds = orgUnitIds;
    }

    private String headingMessage;

    public void setHeadingMessage( String headingMessage )
    {
        this.headingMessage = headingMessage;
    }

    private String verifyStatus;

    public void setVerifyStatus( String verifyStatus )
    {
        this.verifyStatus = verifyStatus;
    }

    private String remarks;

    public void setRemarks( String remarks )
    {
        this.remarks = remarks;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        System.out.println( dataSetId );
        System.out.println( startDate );
        System.out.println( endDate );
        System.out.println( orgUnitIds );
        System.out.println( headingMessage );
        System.out.println( verifyStatus );
        System.out.println( remarks );
        
        return SUCCESS;
    }
}
