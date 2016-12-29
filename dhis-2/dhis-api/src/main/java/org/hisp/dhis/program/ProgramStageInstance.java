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
import java.util.Date;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class ProgramStageInstance
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6239130884678145713L;

    public static final String COLOR_RED = "#fb4754";

    public static final String COLOR_YELLOW = "#f9f95a";
    
    public static final String COLOR_GREEN = "#8ffe8f";
    
    public static final String COLOR_LIGHTRED = "#fb6bfb";

    private int id;

    private ProgramInstance programInstance;

    private ProgramStage programStage;

    private int stageInProgram;

    private Date dueDate;

    private Date executionDate;
    
    private boolean completed = false;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ProgramStageInstance()
    {
    }

    public ProgramStageInstance( ProgramInstance programInstance, ProgramStage programStage )
    {
        this.programInstance = programInstance;
        this.programStage = programStage;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

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

        if ( !(o instanceof ProgramStageInstance) )
        {
            return false;
        }

        final ProgramStageInstance other = (ProgramStageInstance) o;

        return programInstance.equals( other.getProgramInstance() ) && programStage.equals( other.getProgramStage() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + programInstance.hashCode();
        result = result * prime + programStage.hashCode();
        result = result * prime + dueDate.hashCode();
        result = result * prime + ( ( executionDate == null) ? 0 : executionDate.hashCode() );
        result = result * prime + stageInProgram;

        return result;
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

    /**
     * @return the programInstance
     */
    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    /**
     * @param programInstance the programInstance to set
     */
    public void setProgramInstance( ProgramInstance programInstance )
    {
        this.programInstance = programInstance;
    }

    /**
     * @return the programStage
     */
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    /**
     * @param programStage the programStage to set
     */
    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    /**
     * @return the dueDate
     */
    public Date getDueDate()
    {
        return dueDate;
    }

    /**
     * @param dueDate the dueDate to set
     */
    public void setDueDate( Date dueDate )
    {
        this.dueDate = dueDate;
    }

    /**
     * @return the executionDate
     */
    public Date getExecutionDate()
    {
        return executionDate;
    }

    /**
     * @param executionDate the executionDate to set
     */
    public void setExecutionDate( Date executionDate )
    {
        this.executionDate = executionDate;
    }

    /**
     * @param stageInProgram the stageInProgram to set
     */
    public void setStageInProgram( int stageInProgram )
    {
        this.stageInProgram = stageInProgram;
    }

    /**
     * @return the stageInProgram
     */
    public int getStageInProgram()
    {
        return stageInProgram;
    }
    
    /**
     * @return the completed
     */
    public boolean isCompleted()
    {
        return completed;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

}
