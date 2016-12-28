package org.hisp.dhis.rbf.api;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

public interface BankDetailsStore
{
    String ID = BankDetailsStore.class.getName();
    
    void addBankDetails( BankDetails bankDetails );
    
    void updateBankDetails( BankDetails bankDetails );
    
    void deleteBankDetails( BankDetails bankDetails );
        
    //BankDetails getBankDetails( OrganisationUnit organisationUnit, DataSet dataSet );
    
    BankDetails getBankDetails( OrganisationUnit organisationUnit, OrganisationUnitGroup organisationUnitGroup );
    
    Collection<BankDetails> getAllBankDetails();
    
    Collection<BankDetails> getBankDetails( OrganisationUnit organisationUnit );

}
