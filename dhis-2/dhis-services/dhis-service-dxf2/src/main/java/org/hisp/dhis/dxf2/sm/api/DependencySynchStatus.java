package org.hisp.dhis.dxf2.sm.api;

import java.util.Date;

import org.hisp.dhis.common.BaseIdentifiableObject;

/**
 * @author Mithilesh Kumar Thakur
 */
public class DependencySynchStatus extends BaseIdentifiableObject
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public static String METADATA_TYPE_DATAELEMENT = "DataElement";
    public static String METADATA_DEPENDENCY_TYPE_OPTION_SET = "OptionSet";
    public static String METADATA_DEPENDENCY_TYPE_DATAELEMENT_CATEGORY_COMBO = "DataElementCategoryCombo";
    
    private int id;
    
    private SynchInstance instance;
    
    private String metaDataType;
    
    private String metaDataTypeUID;
    
    private String dependencyType;
    
    private String dependencyTypeUID;
    
    private Date dependencyTypeLastupdated;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DependencySynchStatus()
    {
    }

    public DependencySynchStatus( SynchInstance instance, String metaDataType, String metaDataTypeUID, String dependencyType, String dependencyTypeUID, Date dependencyTypeLastupdated )
    {
        this.instance = instance;
        this.metaDataType = metaDataType;
        this.metaDataTypeUID = metaDataTypeUID;
        this.dependencyType = dependencyType;
        this.dependencyTypeUID = dependencyTypeUID;
        this.dependencyTypeLastupdated = dependencyTypeLastupdated;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
    
    public SynchInstance getInstance()
    {
        return instance;
    }

    public void setInstance( SynchInstance instance )
    {
        this.instance = instance;
    }

    public String getMetaDataType()
    {
        return metaDataType;
    }

    public void setMetaDataType( String metaDataType )
    {
        this.metaDataType = metaDataType;
    }

    public String getMetaDataTypeUID()
    {
        return metaDataTypeUID;
    }

    public void setMetaDataTypeUID( String metaDataTypeUID )
    {
        this.metaDataTypeUID = metaDataTypeUID;
    }

    public String getDependencyType()
    {
        return dependencyType;
    }

    public void setDependencyType( String dependencyType )
    {
        this.dependencyType = dependencyType;
    }

    public String getDependencyTypeUID()
    {
        return dependencyTypeUID;
    }

    public void setDependencyTypeUID( String dependencyTypeUID )
    {
        this.dependencyTypeUID = dependencyTypeUID;
    }

    public Date getDependencyTypeLastupdated()
    {
        return dependencyTypeLastupdated;
    }

    public void setDependencyTypeLastupdated( Date dependencyTypeLastupdated )
    {
        this.dependencyTypeLastupdated = dependencyTypeLastupdated;
    }
    
}
