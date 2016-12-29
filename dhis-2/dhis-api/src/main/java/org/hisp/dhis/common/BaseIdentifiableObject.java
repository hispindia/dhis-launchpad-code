package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Bob Jolliffe
 */
@XmlRootElement( name = "identifiableObject", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class BaseIdentifiableObject extends BaseLinkableObject
    implements IdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 5532508099213570673L;

    /**
     * The database internal identifier for this Object.
     */
    protected int id;

    /**
     * The Unique Identifier for this Object.
     */
    protected String uid;

    /**
     * The unique code for this Object.
     */
    protected String code;

    /**
     * The name of this Object. Required and unique.
     */
    protected String name;

    /**
     * The date this object was last updated.
     */
    protected Date lastUpdated;
    
    /**
     * The i18n variant of the name. Should not be persisted.
     */
    protected transient String displayName;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public BaseIdentifiableObject()
    {
    }

    public BaseIdentifiableObject( int id, String uid, String name )
    {
        this.id = id;
        this.uid = uid;
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Comparable implementation
    // -------------------------------------------------------------------------

    @Override
    public int compareTo( IdentifiableObject object )
    {
        return name == null ? ( object.getName() == null ? 0 : -1 ) : name.compareTo( object.getName() );
    }
    
    // -------------------------------------------------------------------------
    // Setters and getters
    // -------------------------------------------------------------------------

    @JsonProperty( value = "internalId" )
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @XmlAttribute( name = "id" )
    @JsonProperty( value = "id" )
    public String getUid()
    {
        return uid;
    }

    public void setUid( String uid )
    {
        this.uid = uid;
    }

    @XmlAttribute(required = false)
    @JsonProperty
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @XmlAttribute(required = false)
    @JsonProperty
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @XmlAttribute(required = false)
    @JsonProperty
    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Date lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public String getDisplayName()
    {
        return displayName != null && !displayName.trim().isEmpty() ? displayName : getName();
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Set auto-generated fields on save or update
     */
    public void setAutoFields()
    {
        if ( uid == null )
        {
            setUid( CodeGenerator.generateCode() );
        }

        setLastUpdated( new Date() );
    }

    /**
     * Get a map of uids to internal identifiers
     *
     * @param objects the IdentifiableObjects to put in the map
     * @return the map
     */
    public static Map<String, Integer> getUIDMap( Collection<? extends BaseIdentifiableObject> objects )
    {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for ( IdentifiableObject object : objects )
        {
            String uid = object.getUid();
            int internalId = object.getId();

            map.put( uid, internalId );
        }

        return map;
    }

    /**
     * Get a map of codes to internal identifiers
     *
     * @param objects the NameableObjects to put in the map
     * @return the map
     */
    public static Map<String, Integer> getCodeMap( Collection<? extends BaseNameableObject> objects )
    {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for ( NameableObject object : objects )
        {
            String code = object.getCode();
            int internalId = object.getId();

            map.put( code, internalId );
        }
        return map;
    }
}
