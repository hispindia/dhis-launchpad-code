package org.hisp.dhis.option;

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.common.BaseIdentifiableObject;

/**
 * @author Lars Helge Overland
 */
public class OptionSet
    extends BaseIdentifiableObject
{
    private static final Pattern OPTION_PATTERN = Pattern.compile( "\\[(.*)\\]" );
    
    private List<String> options = new ArrayList<String>();

    public OptionSet()
    {
    }
    
    public OptionSet( String name )
    {
        this.name = name;
    }
    

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

        if ( !(o instanceof OptionSet) )
        {
            return false;
        }

        final OptionSet other = (OptionSet) o;

        return name.equals( other.getName() );
    }

    public List<String> getOptions()
    {
        return options;
    }
    
    public void setOptions( List<String> options )
    {
        this.options = options;
    }

    public static String optionEncode( String option )
    {
        return option != null ? ( "[" + option.replaceAll( " ", "_" ) + "]" ) : null;
    }
    
    public static String optionDecode( String option )
    {
        Matcher matcher = OPTION_PATTERN.matcher( option );
        return matcher.find() && matcher.groupCount() > 0 ? matcher.group( 1 ).replaceAll( "_", " " ) : null;
    }
}
