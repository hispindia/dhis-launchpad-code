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

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.adapter.OrganisationUnitXmlAdapter;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Nguyen Hong Duc
 * @version $Id: User.java 5554 2008-08-20 09:18:38Z abyot $
 */
@XmlRootElement( name = "user", namespace = Dxf2Namespace.NAMESPACE )
@XmlAccessorType( value = XmlAccessType.NONE )
public class User
    extends BaseIdentifiableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 859837727604102353L;

    /**
     * Required.
     */
    private String surname;

    private String firstName;

    /**
     * Optional.
     */
    private String email;

    private String phoneNumber;

    private UserCredentials userCredentials;

    /**
     * All OrgUnits where the user could belong
     * <p/>
     * TODO This should have been put in UserCredentials
     */
    private Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

    /**
     * Set of the dynamic attributes values that belong to this User.
     */
    private Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public String toString()
    {
        return "[" + surname + " " + firstName + "]";
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        
        int result = 1;
        
        result = prime * result + ( ( email == null) ? 0 : email.hashCode() );
        result = prime * result + ( ( firstName == null) ? 0 : firstName.hashCode() );
        result = prime * result + ( ( phoneNumber == null) ? 0 : phoneNumber.hashCode() );
        result = prime * result + ( ( surname == null) ? 0 : surname.hashCode() );
        
        return result;
    }

    // TODO fix, users might very well have the same name, should use credentials
    
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
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        User other = (User) object;

        if ( firstName == null )
        {
            if ( other.firstName != null )
            {
                return false;
            }
        }
        else if ( !firstName.equals( other.firstName ) )
        {
            return false;
        }

        if ( surname == null )
        {
            if ( other.surname != null )
            {
                return false;
            }
        }
        else if ( !surname.equals( other.surname ) )
        {
            return false;
        }
        
        if ( email == null )
        {
            if ( other.email != null )
            {
                return false;
            }
        }
        else if ( !email.equals( other.email ) )
        {
            return false;
        }
        
        if ( phoneNumber == null )
        {
            if ( other.phoneNumber != null )
            {
                return false;
            }
        }
        else if ( !phoneNumber.equals( other.phoneNumber ) )
        {
            return false;
        }
        
        return true;
    }

    public void addOrganisationUnit( OrganisationUnit unit )
    {
        organisationUnits.add( unit );
        unit.getUsers().add( this );
    }

    public void removeOrganisationUnit( OrganisationUnit unit )
    {
        organisationUnits.remove( unit );
        unit.getUsers().remove( this );
    }

    public void updateOrganisationUnits( Set<OrganisationUnit> updates )
    {
        for ( OrganisationUnit unit : new HashSet<OrganisationUnit>( organisationUnits ) )
        {
            if ( !updates.contains( unit ) )
            {
                removeOrganisationUnit( unit );
            }
        }

        for ( OrganisationUnit unit : updates )
        {
            addOrganisationUnit( unit );
        }
    }

    /**
     * Returns the concatenated first name and surname.
     */
    @Override
    public String getName()
    {
        return firstName + " " + surname;
    }

    /**
     * Returns the first of the organisation units associated with the user.
     * Null is returned if the user has no organisation units. Which
     * organisation unit to return is undefined if the user has multiple
     * organisation units.
     *
     * @return an organisation unit associated with the user.
     */
    public OrganisationUnit getOrganisationUnit()
    {
        return CollectionUtils.isEmpty( organisationUnits ) ? null : organisationUnits.iterator().next();
    }

    public boolean hasOrganisationUnit()
    {
        return !CollectionUtils.isEmpty( organisationUnits );
    }

    public String getOrganisationUnitsName()
    {
        return IdentifiableObjectUtils.join( organisationUnits );
    }

    public String getUsername()
    {
        return userCredentials != null ? userCredentials.getUsername() : null;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @XmlElement
    @JsonProperty
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    @XmlElement
    @JsonProperty
    public String getSurname()
    {
        return surname;
    }

    public void setSurname( String surname )
    {
        this.surname = surname;
    }

    @XmlElement
    @JsonProperty
    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    @XmlElement
    @JsonProperty
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    public UserCredentials getUserCredentials()
    {
        return userCredentials;
    }

    public void setUserCredentials( UserCredentials userCredentials )
    {
        this.userCredentials = userCredentials;
    }

    @XmlElementWrapper( name = "organisationUnits" )
    @XmlElement( name = "organisationUnit" )
    @XmlJavaTypeAdapter( OrganisationUnitXmlAdapter.class )
    @JsonProperty
    @JsonSerialize( contentAs = BaseIdentifiableObject.class )
    public Collection<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( Set<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    @XmlElementWrapper( name = "attributes" )
    @XmlElement( name = "attribute" )
    @JsonProperty( value = "attributes" )
    public Set<AttributeValue> getAttributeValues()
    {
        return attributeValues;
    }

    public void setAttributeValues( Set<AttributeValue> attributeValues )
    {
        this.attributeValues = attributeValues;
    }
}
