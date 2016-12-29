package org.hisp.dhis.school.hibernate;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolDetails;
import org.hisp.dhis.school.SchoolDetailsStore;

/**
 * @author Mithilesh Kumar Thakur
 */
public class HiberateSchoolDetailsStore extends HibernateGenericStore<SchoolDetails> implements SchoolDetailsStore
{ 
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    /*
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    */
    
    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------    
    
    public void saveVoid( SchoolDetails schoolDetails )
    {
        sessionFactory.getCurrentSession().save( schoolDetails );
    }
    
    
    public int deleteBySchool( School school )
    {
        Query query = getQuery( "delete from SchoolDetails where school = :school" );
        query.setEntity( "school", school );
        return query.executeUpdate();
    }
    
    public int deleteByPatientAttribute( PatientAttribute patientAttribute )
    {
        Query query = getQuery( "delete from SchoolDetails where patientAttribute = :patientAttribute" );
        query.setEntity( "patientAttribute", patientAttribute );
        return query.executeUpdate();
    }
    
    
    public SchoolDetails get( School school, PatientAttribute patientAttribute )
    {
        return (SchoolDetails) getCriteria( Restrictions.eq( "school", school ),
            Restrictions.eq( "patientAttribute", patientAttribute ) ).uniqueResult();
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<SchoolDetails> get( School school )
    {
        return getCriteria( Restrictions.eq( "school", school ) ).list();
    }
    
    @SuppressWarnings( "unchecked" )
    public  Collection<SchoolDetails> get( PatientAttribute patientAttribute )
    {
        return getCriteria( Restrictions.eq( "patientAttribute", patientAttribute ) ).list();
    }   
    
    
    @SuppressWarnings( "unchecked" )
    public  Collection<SchoolDetails> get( Collection<School> schools )
    {
        return getCriteria( Restrictions.in( "school", schools ) ).list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SchoolDetails> searchByValue( PatientAttribute patientAttribute, String searchText )
    {
        return getCriteria( Restrictions.eq( "patientAttribute", patientAttribute ),
            Restrictions.ilike( "value", "%" + searchText + "%" ) ).list();
    }
   
    @SuppressWarnings( "unchecked" )
    public  Collection<School> getSchool( PatientAttribute patientAttribute, String value )
    {
        return getCriteria(
            Restrictions.and( Restrictions.eq( "patientAttribute", patientAttribute ), Restrictions.eq( "value", value ) ) )
            .setProjection( Projections.property( "school" ) ).list();
    }   
}
