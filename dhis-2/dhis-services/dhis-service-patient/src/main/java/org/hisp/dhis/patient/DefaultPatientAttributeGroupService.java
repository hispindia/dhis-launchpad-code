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
package org.hisp.dhis.patient;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
@Transactional
public class DefaultPatientAttributeGroupService
    implements PatientAttributeGroupService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<PatientAttributeGroup> patientAttributeGroupStore;

    public void setPatientAttributeGroupStore(
        GenericIdentifiableObjectStore<PatientAttributeGroup> patientAttributeGroupStore )
    {
        this.patientAttributeGroupStore = patientAttributeGroupStore;
    }

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public int savePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup )
    {
        return patientAttributeGroupStore.save( patientAttributeGroup );
    }

    public void deletePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup )
    {
        patientAttributeGroupStore.delete( patientAttributeGroup );
    }

    public void updatePatientAttributeGroup( PatientAttributeGroup patientAttributeGroup )
    {
        patientAttributeGroupStore.update( patientAttributeGroup );
    }

    public PatientAttributeGroup getPatientAttributeGroup( int id )
    {
        return patientAttributeGroupStore.get( id );
    }

    public PatientAttributeGroup getPatientAttributeGroupByName( String name )
    {
        return patientAttributeGroupStore.getByName( name );
    }

    public Collection<PatientAttributeGroup> getAllPatientAttributeGroups()
    {
        return patientAttributeGroupStore.getAll();
    }
}
