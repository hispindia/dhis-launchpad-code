/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.beneficiary;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class DefaultBeneficiaryService implements BeneficiaryService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BeneficiaryStore beneficiaryStore;
    
    public void setBeneficiaryStore( BeneficiaryStore beneficiaryStore )
    {
        this.beneficiaryStore = beneficiaryStore;
    }
    
    // -------------------------------------------------------------------------
    // Beneficiary
    // -------------------------------------------------------------------------
    
    public int addBeneficiary( Beneficiary beneficiary )
    {
        return beneficiaryStore.addBeneficiary( beneficiary );
    }

    public void updateBeneficiary( Beneficiary beneficiary )
    {
        beneficiaryStore.updateBeneficiary( beneficiary );
    }
    
    public void deleteBeneficiary( Beneficiary beneficiary )
    {
        beneficiaryStore.deleteBeneficiary( beneficiary );
    }

    public Beneficiary getBeneficiaryById( int id )
    {
        return beneficiaryStore.getBeneficiaryById( id );
    }
    
    public Beneficiary getBeneficiary( String identifier, DataElement dataElement, Period period )
    {
        return beneficiaryStore.getBeneficiary( identifier, dataElement, period );
    }

    public Collection<Beneficiary> getAllBeneficiary()
    {
        return beneficiaryStore.getAllBeneficiary();
    }
    
    public Collection<Beneficiary> getAllBeneficiaryByASHA( Patient patient )
    {
        return beneficiaryStore.getAllBeneficiaryByASHA( patient );
    }
    
    public Collection<Beneficiary> getAllBeneficiaryByASHAAndPeriod( Patient patient, Period period )
    {
        return beneficiaryStore.getAllBeneficiaryByASHAAndPeriod( patient, period  );
    }
    
    public int getCountByServicePeriodAndASHA( Patient patient, Period period, DataElement dataElement )
    {
        return beneficiaryStore.getCountByServicePeriodAndASHA( patient, period, dataElement );
    }
}
