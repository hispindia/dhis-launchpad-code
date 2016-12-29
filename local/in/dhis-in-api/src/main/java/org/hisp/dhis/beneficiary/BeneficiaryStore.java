package org.hisp.dhis.beneficiary;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;


/**
 * @author Mithilesh Kumar Thakur
 */

public interface BeneficiaryStore
{
    String ID = BeneficiaryStore.class.getName();
    
    int addBeneficiary( Beneficiary beneficiary );
    
    void updateBeneficiary( Beneficiary beneficiary );

    void deleteBeneficiary( Beneficiary beneficiary );
    
    Beneficiary getBeneficiaryById( int id );
    
    Beneficiary getBeneficiary( String identifier, DataElement dataElement, Period period );
    
    Collection<Beneficiary> getAllBeneficiary();
    
    Collection<Beneficiary> getAllBeneficiaryByASHA( Patient patient );
    
    Collection<Beneficiary> getAllBeneficiaryByASHAAndPeriod( Patient patient, Period period );
    
    int getCountByServicePeriodAndASHA( Patient patient, Period period, DataElement dataElement );

}
