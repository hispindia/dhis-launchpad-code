package org.hisp.dhis.dxf2.sm.api;

import java.util.Date;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.dataelement.DataElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author BHARATH IndicatorSynchStatus
 */
@JacksonXmlRootElement( localName = "dataElementSynchStatuss", namespace = DxfNamespaces.DXF_2_0 )
public class DataElementSynchStatus extends BaseIdentifiableObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public static String SYNCH_STATUS_SUBMITTED = "submitted";
    public static String SYNCH_STATUS_APPROVED = "approved";
    public static String SYNCH_STATUS_ACCEPTED = "accepted";
    
    public static String DATAELEMENT_STATUS_NEW = "New";
    public static String DATAELEMENT_STATUS_UPDATE = "Update";
    
    private DataElement dataElement;
    
    private SynchInstance instance;

    private Boolean rememberApproveStatus;

    private Date lastUpdated;

    private Boolean approveStatus;

    private Date approvedDate;

    private Boolean acceptStatus;

    private Date acceptedDate;
    
    private String status;
    
    private String deStatus;
    
    /*
    private Date deccLastupdated;
    
    private Date osLastupdated;
    */
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementSynchStatus()
    {
    }

    public DataElementSynchStatus( DataElement dataElement, SynchInstance instance, Boolean rememberApproveStatus,
        Date lastUpdated, Boolean approveStatus, Date approvedDate, Boolean acceptStatus, Date acceptedDate, String status, String deStatus )
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
        this.deStatus = deStatus;
    }
    
    /*
    public DataElementSynchStatus( DataElement dataElement, SynchInstance instance, Boolean rememberApproveStatus,
        Date lastUpdated, Boolean approveStatus, Date approvedDate, Boolean acceptStatus, Date acceptedDate, String status, String deStatus, Date deccLastupdated, Date osLastupdated )
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
        this.deStatus = deStatus;
        this.deccLastupdated = deccLastupdated;
        this.osLastupdated = osLastupdated;
    }
    */
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }
    
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }


    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public SynchInstance getInstance()
    {
        return instance;
    }

    public void setInstance( SynchInstance instance )
    {
        this.instance = instance;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Boolean getRememberApproveStatus()
    {
        return rememberApproveStatus;
    }

    public void setRememberApproveStatus( Boolean rememberApproveStatus )
    {
        this.rememberApproveStatus = rememberApproveStatus;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Boolean getApproveStatus()
    {
        return approveStatus;
    }

    public void setApproveStatus( Boolean approveStatus )
    {
        this.approveStatus = approveStatus;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Date getApprovedDate()
    {
        return approvedDate;
    }

    public void setApprovedDate( Date approvedDate )
    {
        this.approvedDate = approvedDate;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Boolean getAcceptStatus()
    {
        return acceptStatus;
    }

    public void setAcceptStatus( Boolean acceptStatus )
    {
        this.acceptStatus = acceptStatus;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public Date getAcceptedDate()
    {
        return acceptedDate;
    }

    public void setAcceptedDate( Date acceptedDate )
    {
        this.acceptedDate = acceptedDate;
    }
    
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public String getDeStatus()
    {
        return deStatus;
    }

    public void setDeStatus( String deStatus )
    {
        this.deStatus = deStatus;
    }
    
    /*
    public Date getDeccLastupdated()
    {
        return deccLastupdated;
    }

    public void setDeccLastupdated( Date deccLastupdated )
    {
        this.deccLastupdated = deccLastupdated;
    }

    public Date getOsLastupdated()
    {
        return osLastupdated;
    }

    public void setOsLastupdated( Date osLastupdated )
    {
        this.osLastupdated = osLastupdated;
    }
    */
}
