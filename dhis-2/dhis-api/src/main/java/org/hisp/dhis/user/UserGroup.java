package org.hisp.dhis.user;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.adapter.UserXmlAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement( name = "userGroup", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class UserGroup
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 347909584755616508L;

    /**
     * Set of related users
     */
    private Set<User> members = new HashSet<User>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------     

    public UserGroup()
    {

    }

    public UserGroup( String name )
    {
        this.name = name;
    }

    public UserGroup( String name, Set<User> members )
    {
        this.name = name;
        this.members = members;
    }


    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------     

    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        else if ( object == null )
        {
            return false;
        }
        else if ( !(object instanceof UserGroup) )
        {
            return false;
        }

        final UserGroup userGroup = (UserGroup) object;

        return name.equals( userGroup.getName() );
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @XmlElementWrapper( name = "users" )
    @XmlElement( name = "user" )
    @XmlJavaTypeAdapter( UserXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Set<User> getMembers()
    {
        return members;
    }

    public void setMembers( Set<User> members )
    {
        this.members = members;
    }
}
