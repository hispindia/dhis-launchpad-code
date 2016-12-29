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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.CategoryOptionXmlAdapter;
import org.hisp.dhis.concept.Concept;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * A Category is a dimension of a data element. DataElements can have sets of
 * dimensions (known as CategoryCombos). An Example of a Category might be
 * "Sex". The Category could have two (or more) CategoryOptions such as "Male"
 * and "Female".
 *
 * @author Abyot Asalefew
 */
@XmlRootElement( name = "category", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class DataElementCategory extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6797241235048185629L;

    public static final String DEFAULT_NAME = "default";

    private Concept concept;

    private List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataElementCategory()
    {
    }

    public DataElementCategory( String name )
    {
        this.name = name;
    }

    public DataElementCategory( String name, List<DataElementCategoryOption> categoryOptions )
    {
        this.name = name;
        this.categoryOptions = categoryOptions;
    }

    public DataElementCategory( String name, Concept concept, List<DataElementCategoryOption> categoryOptions )
    {
        this.name = name;
        this.concept = concept;
        this.categoryOptions = categoryOptions;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public DataElementCategoryOption getCategoryOption( DataElementCategoryOptionCombo categoryOptionCombo )
    {
        for ( DataElementCategoryOption categoryOption : categoryOptions )
        {
            if ( categoryOption.getCategoryOptionCombos().contains( categoryOptionCombo ) )
            {
                return categoryOption;
            }
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
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

        if ( !(object instanceof DataElementCategory) )
        {
            return false;
        }

        final DataElementCategory other = (DataElementCategory) object;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // ------------------------------------------------------------------------
    // Getters and setters
    // ------------------------------------------------------------------------

    @XmlElementWrapper( name = "categoryOptions" )
    @XmlJavaTypeAdapter( CategoryOptionXmlAdapter.class )
    @XmlElement( name = "categoryOption" )
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public List<DataElementCategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }

    public void setCategoryOptions( List<DataElementCategoryOption> categoryOptions )
    {
        this.categoryOptions = categoryOptions;
    }

    /**
     * TODO Null problem here.. should investigate
     */

/*    @XmlElement
    @XmlJavaTypeAdapter( BaseIdentifiableObjectXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( using = JsonIdentifiableObjectSerializer.class )
*/
    public Concept getConcept()
    {
        return concept;
    }

    public void setConcept( Concept concept )
    {
        this.concept = concept;
    }
}
