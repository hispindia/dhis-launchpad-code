package org.hisp.dhis.school;

import java.util.Collection;

import org.hisp.dhis.common.GenericNameableObjectStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface SchoolStore extends GenericNameableObjectStore<School>
{
    String ID = SchoolStore.class.getName();
    
    // -------------------------------------------------------------------------
    // School
    // -------------------------------------------------------------------------

    School getSchool( int id );
    
    School getSchoolByName( String name );
    
    School getSchoolByOrganisationUnitAndName( OrganisationUnit organisationUnit, String name );

    Collection<School> getSchoolByOrganisationUnit( OrganisationUnit organisationUnit );
    
    Collection<School> getSchoolByOVC( Patient patient );
    
}
