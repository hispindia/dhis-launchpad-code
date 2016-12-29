/**
 * 
 */
package org.hisp.dhis.alert.viewdata.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataverify.DataVerification;
import org.hisp.dhis.dataverify.DataVerificationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;

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
    
    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private DataVerificationService dataVerificationService;
    
    public void setDataVerificationService( DataVerificationService dataVerificationService )
    {
        this.dataVerificationService = dataVerificationService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
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
    
    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        /*
        System.out.println( dataSetId );
        System.out.println( startDate );
        System.out.println( endDate );
        System.out.println( orgUnitIds );
        System.out.println( headingMessage );
        System.out.println( verifyStatus );
        System.out.println( remarks );
        */
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitIds );
        
        String storedBy = currentUserService.getCurrentUsername();
        
        Date now = new Date();
        
        DataVerification dataVerification = new DataVerification();
        
        dataVerification.setFromDate( startDate );
        dataVerification.setToDate( endDate );
        dataVerification.setVerificationStatus( verifyStatus );
        dataVerification.setDataSet( dataSet );
        dataVerification.setOrganisationUnit( organisationUnit );
        dataVerification.setStoredBy( storedBy );
        dataVerification.setRemarks( remarks );
        dataVerification.setLastUpdated( dateFormat.format( now ) );
        
        dataVerificationService.addDataVerification( dataVerification );
        
        return SUCCESS;
    }
}
