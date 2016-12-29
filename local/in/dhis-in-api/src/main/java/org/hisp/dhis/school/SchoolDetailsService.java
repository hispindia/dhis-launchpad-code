package org.hisp.dhis.school;

import java.util.Collection;

import org.hisp.dhis.patient.PatientAttribute;


/**
 * @author Mithilesh Kumar Thakur
 */
public interface SchoolDetailsService
{
    String ID = SchoolDetailsService.class.getName();
    
    void saveSchoolDetails( SchoolDetails schoolDetails );

    void updateSchoolDetails( SchoolDetails schoolDetails );

    void deleteSchoolDetails( SchoolDetails schoolDetails );

    int deleteSchoolDetails( School school );

    int deleteSchoolDetails( PatientAttribute patientAttribute );
    
    
    SchoolDetails getSchoolDetail( School school, PatientAttribute patientAttribute );

    Collection<SchoolDetails> getSchoolDetails( School school );

    Collection<SchoolDetails> getSchoolDetails( PatientAttribute patientAttribute );

    Collection<SchoolDetails> getSchoolDetails( Collection<School> schools );

    Collection<SchoolDetails> getAllSchoolDetails();

    Collection<SchoolDetails> searchSchoolDetails( PatientAttribute patientAttribute, String searchText );
    
}
