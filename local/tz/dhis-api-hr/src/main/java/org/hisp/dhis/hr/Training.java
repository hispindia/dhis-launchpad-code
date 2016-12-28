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

package org.hisp.dhis.hr;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.common.IdentifiableObject;

/**
 * @author Wilfred Felix Senyoni
 * @version $Id$
 */


public class Training 
extends AbstractNameableObject
	{
	private int id;

    private String name;
    
    private Person person ;

    private String location;
    
    private String sponsor;
    
    private Date startDate;
    
    private Date endDate;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Training()
    {
    }
    
    public Training(String name)
    {
    	this.name = name;
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
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof Training) )
        {
            return false;
        }

        final Training other = (Training) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
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

    public String getName()
    {
        return name;
    }    
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    public Person getPerson()
    {
        return person;
    }    
    
    public void setPerson( Person person )
    {
        this.person = person;
    }
    
    public String getLocation()
    {
        return location;
    }    
    
    public void setLocation( String location )
    {
        this.location = location;
    }
    
    public String getSponsor()
    {
        return sponsor;
    }    
    
    public void setSponsor( String sponsor )
    {
        this.sponsor = sponsor;
    }
    
    public Date getStartDate()
    {
        return startDate;
    }    
    
    public void setStartDate( Date startDate )
    {
        this.startDate = startDate;
    }
    
    public Date getEndDate()
    {
        return endDate;
    }    
    
    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }
}
