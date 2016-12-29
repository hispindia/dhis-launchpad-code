/**
 * 
 */
package org.hisp.dhis.dataverify;

import org.hisp.dhis.common.GenericStore;

/**
 * @author BHARATH
 *
 */
public interface DataVerificationStore extends GenericStore<DataVerification>
{
    String ID = DataVerificationStore.class.getName();

}
