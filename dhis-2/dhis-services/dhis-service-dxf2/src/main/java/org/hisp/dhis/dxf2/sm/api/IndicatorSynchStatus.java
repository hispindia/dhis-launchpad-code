package org.hisp.dhis.dxf2.sm.api;

import java.util.Date;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.indicator.Indicator;

/**
 * @author Mithilesh Kumar Thakur
 */
public class IndicatorSynchStatus extends BaseIdentifiableObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public static String SYNCH_STATUS_SUBMITTED = "submitted";
    public static String SYNCH_STATUS_APPROVED = "approved";
    public static String SYNCH_STATUS_ACCEPTED = "accepted";
    
    public static String INDICATOR_STATUS_NEW = "New";
    public static String INDICATOR_STATUS_UPDATE = "Update";
    
    private Indicator indicator;
    
    private SynchInstance instance;

    private Boolean rememberApproveStatus;

    private Date lastUpdated;

    private Boolean approveStatus;

    private Date approvedDate;

    private Boolean acceptStatus;

    private Date acceptedDate;
    
    private String status;
    
    private String indicatorStatus;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public IndicatorSynchStatus()
    {
    }

    public IndicatorSynchStatus( Indicator indicator, SynchInstance instance, Boolean rememberApproveStatus,
        Date lastUpdated, Boolean approveStatus, Date approvedDate, Boolean acceptStatus, Date acceptedDate, String status, String indicatorStatus )
    {
        this.indicator = indicator;
        this.instance = instance;
        this.rememberApproveStatus = rememberApproveStatus;
        this.lastUpdated = lastUpdated;
        this.approvedDate = approvedDate;
        this.approveStatus = approveStatus;
        this.acceptStatus = acceptStatus;
        this.acceptedDate = acceptedDate;
        this.status = status;
        this.indicatorStatus = indicatorStatus;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public Indicator getIndicator()
    {
        return indicator;
    }

    public void setIndicator( Indicator indicator )
    {
        this.indicator = indicator;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
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
    public String getIndicatorStatus()
    {
        return indicatorStatus;
    }

    public void setIndicatorStatus( String indicatorStatus )
    {
        this.indicatorStatus = indicatorStatus;
    }

    
}

