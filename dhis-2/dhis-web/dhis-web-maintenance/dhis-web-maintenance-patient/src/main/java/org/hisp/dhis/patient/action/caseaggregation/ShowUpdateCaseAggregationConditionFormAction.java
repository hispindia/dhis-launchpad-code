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

package org.hisp.dhis.patient.action.caseaggregation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.comparator.PatientAttributeComparator;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.comparator.ProgramNameComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ ShowUpdateCaseAggregationForm.java May 26, 2011 11:43:19 AM $
 * 
 */
public class ShowUpdateCaseAggregationConditionFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CaseAggregationConditionService aggregationConditionService;

    public DataElementService dataElementService;

    public ProgramService programService;

    private PatientAttributeService patientAttributeService;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer id;

    private CaseAggregationCondition caseAggregation;

    private String description;

    private List<PatientAttribute> patientAttributes;

    private List<DataElementGroup> dataElementGroups;

    private List<DataElement> dataElements;

    private List<Program> programs;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getDescription()
    {
        return description;
    }

    public CaseAggregationCondition getCaseAggregation()
    {
        return caseAggregation;
    }

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    public List<Program> getPrograms()
    {
        return programs;
    }

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        dataElementGroups = new ArrayList<DataElementGroup>( dataElementService.getAllDataElementGroups() );
        Collections.sort( dataElementGroups, IdentifiableObjectNameComparator.INSTANCE );

        programs = new ArrayList<Program>( programService.getAllPrograms() );
        Collections.sort( programs, new ProgramNameComparator() );

        patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
        Collections.sort( patientAttributes, new PatientAttributeComparator() );

        caseAggregation = aggregationConditionService.getCaseAggregationCondition( id );
        description = aggregationConditionService.getConditionDescription( caseAggregation.getAggregationExpression() );
        
        return SUCCESS;
    }
}