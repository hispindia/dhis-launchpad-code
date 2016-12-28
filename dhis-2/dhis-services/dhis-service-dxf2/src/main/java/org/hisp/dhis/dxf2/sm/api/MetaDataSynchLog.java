package org.hisp.dhis.dxf2.sm.api;

import java.util.Date;
import java.util.List;

import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "metaDataSynchSummary", namespace = DxfNamespaces.DXF_2_0 )
public class MetaDataSynchLog
{
    
    public static final String METADATA_TYPE_DATAELEMENT = "DataElement";
    public static final String METADATA_TYPE_INDICATOR = "Indicator";
    public static final String METADATA_TYPE_ORGUNIT = "OrganisationUnit";
    public static final String METADATA_TYPE_VALIDATIONRULE = "ValidationRule";
    
    public static final String METADATA_STATUS_NEW = "New";
    public static final String METADATA_STATUS_UPDATE = "Update";
    
    private Integer id;
    
    private SynchInstance synchInstance;
    
    private Date synchDate;
    
    private String metaDataType;
    
    private String status;
    
    private String remarks;
    
    private String url;
    
    private List<ImportConflict> conflicts;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public MetaDataSynchLog()
    {
        
    }

    // -------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------
    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public SynchInstance getSynchInstance()
    {
        return synchInstance;
    }

    public void setSynchInstance( SynchInstance synchInstance )
    {
        this.synchInstance = synchInstance;
    }

    public Date getSynchDate()
    {
        return synchDate;
    }

    public void setSynchDate( Date synchDate )
    {
        this.synchDate = synchDate;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public void setRemarks( String remarks )
    {
        this.remarks = remarks;
    }
    
    public String getMetaDataType()
    {
        return metaDataType;
    }

    public void setMetaDataType( String metaDataType )
    {
        this.metaDataType = metaDataType;
    }
    
    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }

    @JsonProperty
    @JacksonXmlElementWrapper( localName = "conflicts", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "conflict", namespace = DxfNamespaces.DXF_2_0 )
    public List<ImportConflict> getConflicts()
    {
        return conflicts;
    }

    public void setConflicts( List<ImportConflict> conflicts )
    {
        this.conflicts = conflicts;
    }
    
    @JsonProperty
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }
    
    @Override
    public String toString()
    {
        return "metaDataSynchSummary{" +
            "url='" + url + '\'' +
            ", conflicts=" + conflicts +
            '}';
    }
}
