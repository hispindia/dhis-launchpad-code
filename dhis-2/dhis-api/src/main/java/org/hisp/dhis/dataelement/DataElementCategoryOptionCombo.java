package org.hisp.dhis.dataelement;

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

import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.CategoryComboXmlAdapter;
import org.hisp.dhis.common.adapter.CategoryOptionXmlAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

/**
 * @author Abyot Aselefew
 */
@XmlRootElement( name = "categoryOptionCombo", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElementCategoryOptionCombo extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 7759083342982353468L;

    public static final String DEFAULT_NAME = "default";

    public static final String DEFAULT_TOSTRING = "(default)";

    /**
     * The category combo.
     */
    private DataElementCategoryCombo categoryCombo;

    /**
     * The category options.
     */
    private List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementCategoryOptionCombo()
    {
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;

        int result = 1;

        result = prime * result + ((categoryCombo == null) ? 0 : categoryCombo.hashCode());

        result = prime * result + ((categoryOptions == null) ? 0 : categoryOptions.hashCode());

        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null )
        {
            return false;
        }

        if ( !(object instanceof DataElementCategoryOptionCombo) )
        {
            return false;
        }

        final DataElementCategoryOptionCombo other = (DataElementCategoryOptionCombo) object;

        if ( categoryCombo == null )
        {
            if ( other.categoryCombo != null )
            {
                return false;
            }
        }
        else if ( !categoryCombo.equals( other.categoryCombo ) )
        {
            return false;
        }

        if ( categoryOptions == null )
        {
            if ( other.categoryOptions != null )
            {
                return false;
            }
        }
        else if ( !categoryOptions.equals( other.categoryOptions ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder( "[" + categoryCombo + ", [" );

        Iterator<DataElementCategoryOption> iterator = categoryOptions.iterator();

        while ( iterator.hasNext() )
        {
            builder.append( iterator.next().toString() );

            if ( iterator.hasNext() )
            {
                builder.append( ", " );
            }
        }

        return builder.append( "]]" ).toString();
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Tests whether two objects compare on a name basis. The default equals
     * method becomes unusable in the case of detached objects in conjunction
     * with persistence frameworks that put proxys on associated objects and
     * collections, since it tests the class type which will differ between the
     * proxy and the raw type.
     *
     * @param object the object to test for equality.
     * @return true if objects are equal, false otherwise.
     */
    public boolean equalsOnName( DataElementCategoryOptionCombo object )
    {
        if ( this == object )
        {
            return true;
        }

        if ( object == null || object.getCategoryCombo() == null || object.getCategoryOptions() == null )
        {
            return false;
        }

        if ( !categoryCombo.getName().equals( object.getCategoryCombo().getName() ) )
        {
            return false;
        }

        if ( categoryOptions.size() != object.getCategoryOptions().size() )
        {
            return false;
        }

        final Set<String> names1 = new HashSet<String>();
        final Set<String> names2 = new HashSet<String>();

        for ( DataElementCategoryOption option : categoryOptions )
        {
            names1.add( option.getName() );
        }

        for ( DataElementCategoryOption option : object.getCategoryOptions() )
        {
            names2.add( option.getName() );
        }

        return names1.equals( names2 );
    }

    /**
     * Tests if this object equals to an object in the given Collection on a
     * name basis.
     *
     * @param categoryOptionCombos the Collection.
     * @return true if the Collection contains this object, false otherwise.
     */
    public DataElementCategoryOptionCombo get( Collection<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        for ( DataElementCategoryOptionCombo combo : categoryOptionCombos )
        {
            if ( combo.equalsOnName( this ) )
            {
                return combo;
            }
        }

        return null;
    }

    public boolean isDefault()
    {
        return categoryCombo != null && categoryCombo.getName().equals( DEFAULT_NAME );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        StringBuilder name = new StringBuilder();

        if ( categoryOptions != null && categoryOptions.size() > 0 )
        {
            Iterator<DataElementCategoryOption> iterator = categoryOptions.iterator();

            name.append( "(" ).append( iterator.next().getDisplayName() );

            while ( iterator.hasNext() )
            {
                name.append( ", " ).append( iterator.next().getDisplayName() );
            }

            name.append( ")" );
        }

        return name.toString();
    }

    @Override
    public void setName( String name )
    {
        throw new UnsupportedOperationException( "Cannot set name on DataElementCategoryOptionCombo: " + name );
    }

    @Override
    public String getShortName()
    {
        return getName();
    }

    @Override
    public void setShortName( String shortName )
    {
        throw new UnsupportedOperationException( "Cannot set shortName on DataElementCategoryOptionCombo: " + shortName );
    }

    @Override
    public String getAlternativeName()
    {
        return getName();
    }

    @Override
    public void setAlternativeName( String alternativeName )
    {
        throw new UnsupportedOperationException( "Cannot set alternativename on DataElementCategoryOptionCombo: "
            + alternativeName );
    }

    @XmlElement
    @XmlJavaTypeAdapter( CategoryComboXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public DataElementCategoryCombo getCategoryCombo()
    {
        return categoryCombo;
    }

    public void setCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        this.categoryCombo = categoryCombo;
    }

    @XmlElementWrapper( name = "categoryOptions" )
    @XmlJavaTypeAdapter( CategoryOptionXmlAdapter.class )
    @XmlElement( name = "categoryOption" )
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public List<DataElementCategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions( List<DataElementCategoryOption> categoryOptions )
    {
        this.categoryOptions = categoryOptions;
    }

    public String toJSON()
    {
        StringBuilder result = new StringBuilder();
        result.append( "{" );
        result.append( "\"id\":" + this.getId() + "\"" );
        result.append( ",\"name\":" + StringEscapeUtils.escapeJavaScript( this.getName() ) + "\"" );
        result.append( ",\"isDefault\":" + this.isDefault() + "\"" );
        result.append( "}" );
        return result.toString();
    }
}
