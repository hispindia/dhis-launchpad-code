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
package org.hisp.dhis.patientdataentrylock;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class DefaultPatientDataEntryLockService implements PatientDataEntryLockService
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientDataEntryLockStore patientDataEntryLockStore;
    
    public void setPatientDataEntryLockStore( PatientDataEntryLockStore patientDataEntryLockStore )
    {
        this.patientDataEntryLockStore = patientDataEntryLockStore;
    }
    
    
    // -------------------------------------------------------------------------
    // PatientDataEntryLock
    // -------------------------------------------------------------------------
    
    @Override
    public void addLock( PatientDataEntryLock patientDataEntryLock )
    {
        patientDataEntryLockStore.addLock( patientDataEntryLock );
    }
    
    @Override
    public void updateLock( PatientDataEntryLock patientDataEntryLock )
    {
        patientDataEntryLockStore.updateLock( patientDataEntryLock );
    }
    
    @Override
    public void deleteLock( PatientDataEntryLock patientDataEntryLock )
    {
        patientDataEntryLockStore.deleteLock( patientDataEntryLock );
    }

    @Override
    public PatientDataEntryLock getPatientDataEntryLock( int id )
    {
        return patientDataEntryLockStore.getPatientDataEntryLock( id );
    }
   
    public PatientDataEntryLock getPatientDataEntryLock( OrganisationUnit organisationUnit, Period period, Patient patient )
    {
        return patientDataEntryLockStore.getPatientDataEntryLock( organisationUnit, period, patient );
    }    
    
    public PatientDataEntryLock getLockedPatientDataEntry( OrganisationUnit organisationUnit, Period period, Patient patient )
    {
        return patientDataEntryLockStore.getLockedPatientDataEntry( organisationUnit, period, patient );
    }    
    
    public Collection<PatientDataEntryLock> getAllPatientDataEntryLock()
    {
        return patientDataEntryLockStore.getAllPatientDataEntryLock();
    }
    
    /*
    @SuppressWarnings( "unused" )
    private boolean isLocked( PatientDataEntryLock patientDataEntryLock  )
    {
        if ( !patientDataEntryLock.isLock() )
            return false;

        return patientDataEntryLock.isLock();
    }
    */
}
