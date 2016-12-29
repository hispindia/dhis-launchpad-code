/**
 * 
 */
package org.hisp.dhis.dataverify;

import java.util.Collection;


/**
 * @author BHARATH
 *
 */
public interface DataVerificationService
{
    String ID = DataVerificationService.class.getName();

    int addDataVerification( DataVerification dataVerification );
    
    void updateDataVerification( DataVerification dataVerification );
    
    void deleteDataVerification( DataVerification dataVerification );
    
    DataVerification getDataVerification( int id );
    
    Collection<DataVerification> getAllDataVerifications();
}
