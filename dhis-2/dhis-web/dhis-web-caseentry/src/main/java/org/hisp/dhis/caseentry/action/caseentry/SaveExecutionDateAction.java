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

package org.hisp.dhis.caseentry.action.caseentry;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SaveExecutionDateAction
    implements Action
{
    private static final Log LOG = LogFactory.getLog( SaveExecutionDateAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String executionDate;

    public void setExecutionDate( String executionDate )
    {
        this.executionDate = executionDate;
    }

    private Integer programStageId;

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Date dateValue = format.parseDate( executionDate );

        if ( dateValue != null )
        {
            // Get program-stage-instance of the patient
            ProgramStageInstance programStageInstance = selectedStateManager.getSelectedProgramStageInstance();

            // If the program-stage-instance of the patient not exists,
            // create a program-instance and program-stage-instance for
            // single-event program
            if ( programStageInstance == null )
            {
                Patient patient = selectedStateManager.getSelectedPatient();
                ProgramStage programStage = programStageService.getProgramStage( programStageId );
                Program program = programStage.getProgram();

                if ( programStage.getProgram().getSingleEvent() && !programStage.getProgram().getAnonymous() )
                {
                    // Add a new program-instance
                    ProgramInstance programInstance = new ProgramInstance();
                    programInstance.setEnrollmentDate( dateValue );
                    programInstance.setDateOfIncident( dateValue );
                    programInstance.setProgram( program );
                    programInstance.setCompleted( false );

                    programInstance.setPatient( patient );
                    patient.getPrograms().add( program );
                    patientService.updatePatient( patient );

                    programInstanceService.addProgramInstance( programInstance );

                    // Add a new program-stage-instance
                    programStageInstance = new ProgramStageInstance();
                    programStageInstance.setProgramInstance( programInstance );
                    programStageInstance.setProgramStage( programStage );
                    programStageInstance.setStageInProgram( programStage.getStageInProgram() );
                    programStageInstance.setDueDate( dateValue );
                    programStageInstance.setExecutionDate( dateValue );

                    programStageInstanceService.addProgramStageInstance( programStageInstance );

                    selectedStateManager.setSelectedProgramInstance( programInstance );
                    selectedStateManager.setSelectedProgramStageInstance( programStageInstance );
                }
            }
            else
            {
                programStageInstance.setExecutionDate( dateValue );

                if ( programStageInstance.getProgramInstance().getProgram().getSingleEvent() )
                {
                    programStageInstance.setDueDate( dateValue );
                }

                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }

            LOG.debug( "Updating Execution Date, value added/changed" );

            message = programStageInstance.getId() + "";

            return SUCCESS;
        }

        return INPUT;
    }
}
