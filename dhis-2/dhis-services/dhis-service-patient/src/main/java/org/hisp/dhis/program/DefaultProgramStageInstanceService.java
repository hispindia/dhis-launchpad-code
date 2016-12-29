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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.system.grid.ListGrid;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
@Transactional
public class DefaultProgramStageInstanceService
    implements ProgramStageInstanceService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceStore programStageInstanceStore;

    public void setProgramStageInstanceStore( ProgramStageInstanceStore programStageInstanceStore )
    {
        this.programStageInstanceStore = programStageInstanceStore;
    }

    private PatientDataValueService patientDataValueService;

    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public int addProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        return programStageInstanceStore.save( programStageInstance );
    }

    public void deleteProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        programStageInstanceStore.delete( programStageInstance );
    }

    public Collection<ProgramStageInstance> getAllProgramStageInstances()
    {
        return programStageInstanceStore.getAll();
    }

    public ProgramStageInstance getProgramStageInstance( int id )
    {
        return programStageInstanceStore.get( id );
    }

    public ProgramStageInstance getProgramStageInstance( ProgramInstance programInstance, ProgramStage programStage )
    {
        return programStageInstanceStore.get( programInstance, programStage );
    }

    public Collection<ProgramStageInstance> getProgramStageInstances( ProgramStage programStage )
    {
        return programStageInstanceStore.get( programStage );
    }

    public void updateProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        programStageInstanceStore.update( programStageInstance );
    }

    public Map<Integer, String> colorProgramStageInstances( Collection<ProgramStageInstance> programStageInstances )
    {
        Map<Integer, String> colorMap = new HashMap<Integer, String>();

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            if ( programStageInstance.isCompleted() )
            {
                colorMap.put( programStageInstance.getId(), ProgramStageInstance.COLOR_GREEN );
            }
            else if ( programStageInstance.getExecutionDate() != null )
            {
                colorMap.put( programStageInstance.getId(), ProgramStageInstance.COLOR_LIGHTRED );
            }
            else
            {
                // -------------------------------------------------------------
                // If a program stage is not provided even a day after its due
                // date, then that service is alerted red - because we are
                // getting late
                // -------------------------------------------------------------

                Calendar dueDateCalendar = Calendar.getInstance();
                dueDateCalendar.setTime( programStageInstance.getDueDate() );
                dueDateCalendar.add( Calendar.DATE, 1 );

                if ( dueDateCalendar.getTime().before( new Date() ) )
                {
                    colorMap.put( programStageInstance.getId(), ProgramStageInstance.COLOR_RED );
                }
                else
                {
                    colorMap.put( programStageInstance.getId(), ProgramStageInstance.COLOR_YELLOW );
                }
            }
        }

        return colorMap;
    }

    public Collection<ProgramStageInstance> getProgramStageInstances( Collection<ProgramInstance> programInstances )
    {
        return programStageInstanceStore.getProgramStageInstances( programInstances );
    }

    public Collection<ProgramStageInstance> getProgramStageInstances( Date dueDate )
    {
        return programStageInstanceStore.getProgramStageInstances( dueDate );
    }

    public Collection<ProgramStageInstance> getProgramStageInstances( Date dueDate, Boolean completed )
    {
        return programStageInstanceStore.getProgramStageInstances( dueDate, completed );
    }

    public Collection<ProgramStageInstance> getProgramStageInstances( Date startDate, Date endDate )
    {
        return programStageInstanceStore.getProgramStageInstances( startDate, endDate );
    }

    public Collection<ProgramStageInstance> getProgramStageInstances( Date startDate, Date endDate, Boolean completed )
    {
        return programStageInstanceStore.getProgramStageInstances( startDate, endDate, completed );
    }

    public List<ProgramStageInstance> get( OrganisationUnit unit, Date after, Date before, Boolean completed )
    {
        return programStageInstanceStore.get( unit, after, before, completed );
    }

    public List<ProgramStageInstance> getProgramStageInstances( Patient patient, Boolean completed )
    {
        return programStageInstanceStore.getProgramStageInstances( patient, completed );
    }

    public List<ProgramStageInstance> getProgramStageInstances( ProgramInstance programInstance, Date startDate, Date endDate ,
        int min, int max )
    {
        return programStageInstanceStore.getProgramStageInstances( programInstance, startDate, endDate , min, max );
    }

    public int countProgramStageInstances( ProgramInstance programInstance, Date startDate, Date endDate  )
    {
        return programStageInstanceStore.countProgramStageInstances( programInstance, startDate, endDate  );
    }

    public Grid getSingleEventReport( ProgramInstance programInstance, Date startDate, Date endDate , int min, int max,
        I18nFormat format, I18n i18n )
    {
        List<ProgramStageInstance> programStageInstances = getProgramStageInstances( programInstance, startDate, endDate ,
            min, max );

        ProgramStage programStage = programInstance.getProgram().getProgramStages().iterator().next();

        Collection<ProgramStageDataElement> psDataElements = programStage.getProgramStageDataElements();

        Collection<DataElement> dataElements = new HashSet<DataElement>();
        for ( ProgramStageDataElement psDataElement : psDataElements )
        {
            if ( psDataElement.getShowOnReport() )
            {
                dataElements.add( psDataElement.getDataElement() );
            }
        }

        // ---------------------------------------------------------------------
        // Create a grid
        // ---------------------------------------------------------------------

        Grid grid = new ListGrid().setTitle( programInstance.getProgram().getName() );
        grid.setSubtitle( i18n.getString( "from" ) + " " + format.formatDate( startDate ) + " " + i18n.getString( "to" ) + " " + format.formatDate( endDate ) );

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        for ( DataElement dataElement : dataElements )
        {
            grid.addHeader( new GridHeader( dataElement.getName(), false, false ) );
        }

        grid.addHeader( new GridHeader( i18n.getString( "operations" ), false, false ) );

        // ---------------------------------------------------------------------
        // Values
        // ---------------------------------------------------------------------

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            grid.addRow();

            for ( DataElement dataElement : dataElements )
            {
                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,
                    dataElement );

                if ( patientDataValue != null )
                {
                    grid.addValue( patientDataValue.getValue() );
                }
                else
                {
                    grid.addValue( "" );
                }
            }

            grid.addValue( programStageInstance.getId() );
        }

        return grid;
    }
}
