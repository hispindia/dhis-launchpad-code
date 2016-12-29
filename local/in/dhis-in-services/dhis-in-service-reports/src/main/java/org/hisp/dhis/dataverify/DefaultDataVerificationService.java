/**
 * 
 */
package org.hisp.dhis.dataverify;

import java.util.Collection;

/**
 * @author BHARATH
 *
 */
public class DefaultDataVerificationService implements DataVerificationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataVerificationStore dataVerificationStore;
    
    public void setDataVerificationStore( DataVerificationStore dataVerificationStore )
    {
        this.dataVerificationStore = dataVerificationStore;
    }

    // -------------------------------------------------------------------------
    // DataVerificationService implementation
    // -------------------------------------------------------------------------
    @Override
    public int addDataVerification( DataVerification dataVerification )
    {
        return dataVerificationStore.save( dataVerification );
    }
    
    @Override
    public void updateDataVerification( DataVerification dataVerification )
    {
        dataVerificationStore.update( dataVerification );
    }

    @Override
    public void deleteDataVerification( DataVerification dataVerification )
    {
        dataVerificationStore.delete( dataVerification );
    }

    @Override
    public DataVerification getDataVerification( int id )
    {
        return dataVerificationStore.get( id );
    }
    
    @Override
    public Collection<DataVerification> getAllDataVerifications()
    {
        return dataVerificationStore.getAll();
    }

}
