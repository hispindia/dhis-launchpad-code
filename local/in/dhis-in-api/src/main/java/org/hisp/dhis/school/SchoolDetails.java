package org.hisp.dhis.school;

import java.io.Serializable;

import org.hisp.dhis.patient.PatientAttribute;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SchoolDetails implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -4469496681709547707L;
    
    private School school;
    
    private PatientAttribute patientAttribute;

    private String value;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public SchoolDetails()
    {
    }

    public SchoolDetails( School school, PatientAttribute patientAttribute )
    {
        this.school = school;
        this.patientAttribute = patientAttribute;
    }

    public SchoolDetails( School school, PatientAttribute patientAttribute, String value )
    {
        this.school = school;
        this.patientAttribute = patientAttribute;
        this.value = value;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof SchoolDetails) )
        {
            return false;
        }

        final SchoolDetails other = (SchoolDetails) o;

        return school.equals( other.getSchool() )  && patientAttribute.equals( other.getPatientAttribute() ) ;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + school.hashCode();
        result = result * prime + patientAttribute.hashCode();
        

        return result;
    }
       
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------    

    public School getSchool()
    {
        return school;
    }

    public void setSchool( School school )
    {
        this.school = school;
    }

    public PatientAttribute getPatientAttribute()
    {
        return patientAttribute;
    }

    public void setPatientAttribute( PatientAttribute patientAttribute )
    {
        this.patientAttribute = patientAttribute;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
    
}
