package org.hisp.dhis.school;

import java.util.Collection;

import org.hisp.dhis.common.GenericStore;
import org.hisp.dhis.patient.PatientAttribute;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface SchoolDetailsStore extends GenericStore<SchoolDetails>
{
    String ID = SchoolDetailsStore.class.getName();   
    
    void saveVoid( SchoolDetails schoolDetails );

    int deleteBySchool( School school );
    
    int deleteByPatientAttribute( PatientAttribute patientAttribute );
    
    SchoolDetails get( School school, PatientAttribute patientAttribute );    

    Collection<SchoolDetails> get( School school );
    
    Collection<SchoolDetails> get( PatientAttribute patientAttribute );
    
    Collection<SchoolDetails> get( Collection<School> schools );
    
    Collection<SchoolDetails> searchByValue( PatientAttribute patientAttribute, String searchText );
    
    Collection<School> getSchool( PatientAttribute patientAttribute, String value );
    
}
