package org.hisp.dhis.sm.api;

import java.util.Date;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;

/**
 * @author BHARATH
 */
public class DataElementSynchStatus extends BaseIdentifiableObject
{
    public static String SYNCH_STATUS_SUBMITTED = "submitted";
    public static String SYNCH_STATUS_APPROVED = "approved";
    public static String SYNCH_STATUS_ACCEPTED = "accepted";
    
    private DataElement dataElement;
    
    private SynchInstance instance;

    private Boolean rememberApproveStatus;

    private Date lastUpdated;

    private Boolean approveStatus;

    private Date approvedDate;

    private Boolean acceptStatus;

    private Date acceptedDate;
    
    private String status;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementSynchStatus()
    {
    }

    public DataElementSynchStatus( DataElement dataElement, SynchInstance instance, Boolean rememberApproveStatus,
        Date lastUpdated, Boolean approveStatus, Date approvedDate, Boolean acceptStatus, Date acceptedDate, String status )
    {
        this.dataElement = dataElement;
        this.instance = instance;
        this.rememberApproveStatus = rememberApproveStatus;
        this.lastUpdated = lastUpdated;
        this.approvedDate = approvedDate;
        this.approveStatus = approveStatus;
        this.acceptStatus = acceptStatus;
        this.acceptedDate = acceptedDate;
        this.status = status;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public DataElement getDataElement()
    {
        return dataElement;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public SynchInstance getInstance()
    {
        return instance;
    }

    public void setInstance( SynchInstance instance )
    {
        this.instance = instance;
    }

    public Boolean getRememberApproveStatus()
    {
        return rememberApproveStatus;
    }

    public void setRememberApproveStatus( Boolean rememberApproveStatus )
    {
        this.rememberApproveStatus = rememberApproveStatus;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public Boolean getApproveStatus()
    {
        return approveStatus;
    }

    public void setApproveStatus( Boolean approveStatus )
    {
        this.approveStatus = approveStatus;
    }

    public Date getApprovedDate()
    {
        return approvedDate;
    }

    public void setApprovedDate( Date approvedDate )
    {
        this.approvedDate = approvedDate;
    }

    public Boolean getAcceptStatus()
    {
        return acceptStatus;
    }

    public void setAcceptStatus( Boolean acceptStatus )
    {
        this.acceptStatus = acceptStatus;
    }

    public Date getAcceptedDate()
    {
        return acceptedDate;
    }

    public void setAcceptedDate( Date acceptedDate )
    {
        this.acceptedDate = acceptedDate;
    }

}
