package org.hisp.dhis.patientdataentrylock;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;

public interface PatientDataEntryLockService
{
    String ID = PatientDataEntryLockService.class.getName();
    
    // -------------------------------------------------------------------------
    // PatientDataEntryLock
    // -------------------------------------------------------------------------
    
    void addLock( PatientDataEntryLock patientDataEntryLock );

    void updateLock( PatientDataEntryLock patientDataEntryLock );

    void deleteLock( PatientDataEntryLock patientDataEntryLock );
    
    PatientDataEntryLock getPatientDataEntryLock( int id );
    
    PatientDataEntryLock getPatientDataEntryLock( OrganisationUnit organisationUnit, Period period, Patient patient );
    
    PatientDataEntryLock getLockedPatientDataEntry( OrganisationUnit organisationUnit, Period period, Patient patient );
    
    //boolean isLocked( PatientDataEntryLock patientDataEntryLock );
    
    Collection<PatientDataEntryLock> getAllPatientDataEntryLock();
    
}
