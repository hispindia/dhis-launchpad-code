package org.hisp.dhis.school;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;


/**
 * @author Mithilesh Kumar Thakur
 */
public interface SchoolService
{
    String ID = SchoolService.class.getName();
    
    // -------------------------------------------------------------------------
    // School
    // -------------------------------------------------------------------------

    /**
     * Adds an School 
     *
     * @param school the School to add.
     * @return a generated unique id of the added School.
     */
    public int addSchool( School school );

    /**
     * Updates an School.
     *
     * @param school the School to update.
     */
    void updateSchool( School school );

    /**
     * Deletes  School
     * deleted.
     *
     * @param School the School to delete.
     */
    void deleteSchool( School school );
    
    /**
     * Returns an School.
     *
     * @param id the id of the School to return.
     * @return the School with the given id, or null if no match.
     */
    School getSchool( int id );
    
    School getSchoolByName( String name );
    
    School getSchoolByOrganisationUnitAndName( OrganisationUnit organisationUnit,String name );

    Collection<School> getAllSchools();
    
    Collection<School> getSchoolByOrganisationUnit( OrganisationUnit organisationUnit );
    
    Collection<School> getSchoolByOVC( Patient patient );
    
    void searchSchoolsByName( List<School> schools, String key );
    
    public int addSchoolWithDetails( School school, List<SchoolDetails> schoolDetailValues  );
    
    public void updateSchoolWithDetails( School school, List<SchoolDetails> valuesForSave, List<SchoolDetails> valuesForUpdate, Collection<SchoolDetails> valuesForDelete );
    
    
}
