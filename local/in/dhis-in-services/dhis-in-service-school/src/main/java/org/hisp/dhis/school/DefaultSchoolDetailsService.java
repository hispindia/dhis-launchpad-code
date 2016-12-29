package org.hisp.dhis.school;

import java.util.Collection;

import org.hisp.dhis.patient.PatientAttribute;


/**
 * @author Mithilesh Kumar Thakur
 */
public class DefaultSchoolDetailsService implements SchoolDetailsService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SchoolDetailsStore schoolDetailsStore;

    public void setSchoolDetailsStore( SchoolDetailsStore schoolDetailsStore )
    {
        this.schoolDetailsStore = schoolDetailsStore;
    }
    
    
    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------
    
    public void saveSchoolDetails( SchoolDetails schoolDetails )
    {
        if ( schoolDetails.getValue() != null )
        {
            schoolDetailsStore.saveVoid( schoolDetails );
        }
    }
    
    public void updateSchoolDetails( SchoolDetails schoolDetails )
    {
        if ( schoolDetails.getValue() == null )
        {
            schoolDetailsStore.delete( schoolDetails );
        }
        else
        {
            schoolDetailsStore.update( schoolDetails );
        }
    }   
    
    public void deleteSchoolDetails( SchoolDetails schoolDetails )
    {
        schoolDetailsStore.delete( schoolDetails );
    }

    public int deleteSchoolDetails( School school )
    {
        return schoolDetailsStore.deleteBySchool( school );
    }

    public int deleteSchoolDetails( PatientAttribute patientAttribute )
    {
        return schoolDetailsStore.deleteByPatientAttribute( patientAttribute );
    }    
    

    public SchoolDetails getSchoolDetail( School school, PatientAttribute patientAttribute )
    {
        return schoolDetailsStore.get( school, patientAttribute );
    }    
    
    public Collection<SchoolDetails> getSchoolDetails( School school )
    {
        return schoolDetailsStore.get( school );
    }   
    
    public Collection<SchoolDetails> getSchoolDetails( PatientAttribute patientAttribute )
    {
        return schoolDetailsStore.get( patientAttribute );
    }

    public Collection<SchoolDetails> getSchoolDetails( Collection<School> schools )
    {
        if ( schools != null && schools.size() > 0 )
        {
            return schoolDetailsStore.get( schools );
        }
            
        return null;
    }    
    
    public Collection<SchoolDetails> getAllSchoolDetails()
    {
        return schoolDetailsStore.getAll();
    }    
    

    public Collection<SchoolDetails> searchSchoolDetails( PatientAttribute patientAttribute, String searchText )
    {
        return schoolDetailsStore.searchByValue( patientAttribute, searchText );
    }
    
}
