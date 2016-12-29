/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.program;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataentryform.DataEntryForm;

/**
 * @author Abyot Asalefew
 */
public class ProgramStage
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6876401001559656214L;

    private int id;

    private String name;

    private String description;

    private int stageInProgram;

    private int minDaysFromStart;    
    
    private Boolean irregular;
    
    private Program program;

    private Set<ProgramStageDataElement> programStageDataElements = new HashSet<ProgramStageDataElement>();

    private DataEntryForm dataEntryForm;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramStage()
    {

    }

    public ProgramStage( String name, Program program )
    {
        this.name = name;
        this.program = program;
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

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final ProgramStage other = (ProgramStage) object;

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

    /**
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId( int id )
    {
        this.id = id;
    }

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }

    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription( String description )
    {
        this.description = description;
    }

    public Boolean getIrregular()
    {
        return irregular;
    }

    public void setIrregular( Boolean irregular )
    {
        this.irregular = irregular;
    }

    /**
     * @return the stageInProgram
     */
    public int getStageInProgram()
    {
        return stageInProgram;
    }

    /**
     * @param stageInProgram the stageInProgram to set
     */
    public void setStageInProgram( int stageInProgram )
    {
        this.stageInProgram = stageInProgram;
    }
    
    /**
     * @return the minDaysFromStart
     */
    public int getMinDaysFromStart()
    {
        return minDaysFromStart;
    }

    /**
     * @param minDaysFromStart the minDaysFromStart to set
     */
    public void setMinDaysFromStart( int minDaysFromStart )
    {
        this.minDaysFromStart = minDaysFromStart;
    }    

    /**
     * @param program the program to set
     */
    public void setProgram( Program program )
    {
        this.program = program;
    }

    /**
     * @return the program
     */
    public Program getProgram()
    {
        return program;
    }
    
    public Set<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }

    public void setProgramStageDataElements( Set<ProgramStageDataElement> programStageDataElements )
    {
        this.programStageDataElements = programStageDataElements;
    }
    

}
