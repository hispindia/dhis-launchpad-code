package org.hisp.dhis.minmax;

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

import java.io.Serializable;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Brajesh Murari
 * 
 */

public class MinMaxValidationResult implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -4118317796752965294L;

    private DataElement dataElement;
    
    private OrganisationUnit source;

    private Period period;

    private int min;

    private Double value;
    
    private int max;

    private boolean generated;
    
    private MinMaxDataElement minMaxDataElement;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------     

    public MinMaxValidationResult()
    {
    }

    public MinMaxValidationResult( DataElement dataElement, OrganisationUnit source, Period period, int min, 
    		Double value, int max, boolean generated, MinMaxDataElement minMaxDataElement )
    {
    	this.dataElement = dataElement;
        this.source = source;
        this.period = period;
        this.min = min;
        this.value = value;
        this.max = max;
        this.generated = generated;
        this.minMaxDataElement = minMaxDataElement;
    }

    // -------------------------------------------------------------------------
    // Equals, hashCode and toString
    // -------------------------------------------------------------------------     

    @Override
    public int hashCode()
    {
        final int PRIME = 31;

        int result = 1;

        result = PRIME * result + ((dataElement == null) ? 0 : dataElement.hashCode());
        result = PRIME * result + ((period == null) ? 0 : period.hashCode());
        result = PRIME * result + ((source == null) ? 0 : source.hashCode());
        result = PRIME * result + ((minMaxDataElement == null) ? 0 : minMaxDataElement.hashCode());

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

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final MinMaxValidationResult other = (MinMaxValidationResult) object;

        if ( period == null )
        {
            if ( other.period != null )
            {
                return false;
            }
        }
        else if ( !period.equals( other.period ) )
        {
            return false;
        }

        if ( source == null )
        {
            if ( other.source != null )
            {
                return false;
            }
        }
        else if ( !source.equals( other.source ) )
        {
            return false;
        }

        if ( minMaxDataElement == null )
        {
            if ( other.minMaxDataElement != null )
            {
                return false;
            }
        }
        else if ( !minMaxDataElement.equals( other.minMaxDataElement ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return dataElement + " - " + source + " - " + period + " - " + min + " - " + max + " - " + generated;
    }

    // -------------------------------------------------------------------------
    // Set and get methods
    // -------------------------------------------------------------------------     

    public DataElement getDataElement() 
    {
		return dataElement;
	}

	public void setDataElement(DataElement dataElement) 
	{
		this.dataElement = dataElement;
	}

	public OrganisationUnit getSource() 
	{
		return source;
	}

	public void setSource(OrganisationUnit source) 
	{
		this.source = source;
	}

	public Period getPeriod() 
	{
		return period;
	}

	public void setPeriod(Period period) 
	{
		this.period = period;
	}

	public int getMin() 
	{
		return min;
	}

	public void setMin(int min) 
	{
		this.min = min;
	}

	public Double getValue() 
	{
		return value;
	}

	public void setValue(Double value) 
	{
		this.value = value;
	}

	public int getMax() 
	{
		return max;
	}

	public void setMax(int max) 
	{
		this.max = max;
	}

	public boolean isGenerated() 
	{
		return generated;
	}

	public void setGenerated(boolean generated) 
	{
		this.generated = generated;
	}

	public MinMaxDataElement getMinMaxDataElement() 
	{
		return minMaxDataElement;
	}

	public void setMinMaxDataElement(MinMaxDataElement minMaxDataElement) 
	{
		this.minMaxDataElement = minMaxDataElement;
	}

}
