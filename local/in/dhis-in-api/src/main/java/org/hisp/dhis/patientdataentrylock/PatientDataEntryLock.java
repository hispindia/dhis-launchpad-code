package org.hisp.dhis.patientdataentrylock;

import java.io.Serializable;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;

public class PatientDataEntryLock implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
    */
    private static final long serialVersionUID = 884114994005945275L;
    
    /**
     * The unique identifier
     */
    private Integer id;
    
    private OrganisationUnit organisationUnit;
    
    private Period period;
    
    private Patient patient;
    
    private boolean lockStatus;
    
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------

    public PatientDataEntryLock()
    {
        
    }
    
    public PatientDataEntryLock( OrganisationUnit organisationUnit, Period period, Patient patient )
    {
        this.organisationUnit = organisationUnit;
        this.period = period;
        this.patient = patient;
    }
    
    /*
    public PatientDataEntryLock( OrganisationUnit organisationUnit, Period period, Patient patient, boolean lock )
    {
        this.organisationUnit = organisationUnit;
        this.period = period;
        this.patient = patient;
        this.lock = lock;
    }
    */
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    
    @Override
    
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((period == null) ? 0 : period.hashCode());
        result = prime * result + ((patient == null) ? 0 : patient.hashCode());
        
        return result;
    }
    
    
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

        if ( !(o instanceof PatientDataEntryLock ) )
        {
            return false;
        }

        final PatientDataEntryLock other = ( PatientDataEntryLock ) o;

        return patient.equals( other.getPatient() );
    }
    
    
    public String getName()
    {
        if ( organisationUnit == null )
        {
            return patient.getFullName() + " (" + period.getName() + ")";
        }

        return patient.getFullName() + " (" + organisationUnit.getName() + ", " + period.getName() + ")";
    }
    
    
    //--------------------------------------------------------------------------
    // Getters and Setters
    //--------------------------------------------------------------------------
    
    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public void setPatient( Patient patient )
    {
        this.patient = patient;
    }

    public boolean isLockStatus()
    {
        return lockStatus;
    }

    public void setLockStatus( boolean lockStatus )
    {
        this.lockStatus = lockStatus;
    }
           
}
