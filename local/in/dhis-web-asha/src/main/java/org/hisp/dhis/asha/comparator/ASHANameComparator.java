package org.hisp.dhis.asha.comparator;

import java.util.Comparator;

import org.hisp.dhis.patient.Patient;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ASHANameComparator implements Comparator<Patient>
{
    public int compare( Patient patient0, Patient patient1 )
    {
        return patient0.getFullName().compareToIgnoreCase( patient1.getFullName() );
    }
}
