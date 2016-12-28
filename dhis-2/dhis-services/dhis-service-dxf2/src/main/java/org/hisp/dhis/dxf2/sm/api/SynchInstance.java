package org.hisp.dhis.dxf2.sm.api;

import org.hisp.dhis.common.BaseIdentifiableObject;

/**
 * @author BHARATH
 */
public class SynchInstance extends BaseIdentifiableObject
{
    public static final String SYNCH_TYPE_META_DATA = "meta-data";
    
    public static final String SYNCH_TYPE_DATA = "data";

    private String url;
    
    private String userId;
    
    private String password;
    
    private String synchType;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public SynchInstance()
    {
    }

    public SynchInstance( String name, String url, String userId, String password, String synchType )
    {
        this.name = name;
        this.url = url;
        this.userId = userId;
        this.password = password;
        this.synchType = synchType;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId( String userId )
    {
        this.userId = userId;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword( String password )
    {
        this.password = password;
    }

    public String getSynchType()
    {
        return synchType;
    }

    public void setSynchType( String synchType )
    {
        this.synchType = synchType;
    }
    
}
