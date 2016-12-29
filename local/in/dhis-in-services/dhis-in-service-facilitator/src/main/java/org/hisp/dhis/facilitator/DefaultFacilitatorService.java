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
package org.hisp.dhis.facilitator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class DefaultFacilitatorService implements FacilitatorService
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private FacilitatorStore facilitatorStore;
    
    public void setFacilitatorStore( FacilitatorStore facilitatorStore )
    {
        this.facilitatorStore = facilitatorStore;
    }
    
    
    // -------------------------------------------------------------------------
    // Facilitator
    // -------------------------------------------------------------------------
    
    @Override
    public int addFacilitator( Facilitator facilitator )
    {
        return facilitatorStore.save( facilitator );
    }
    
    @Override
    public void updateFacilitator( Facilitator facilitator )
    {
        facilitatorStore.update( facilitator );
    }
    
    @Override
    public void deleteFacilitator( Facilitator facilitator )
    {
        facilitatorStore.delete( facilitator );
    }

    @Override
    public Facilitator getFacilitator( int id )
    {
        return facilitatorStore.getFacilitator( id );
    }
   
    public Collection<Facilitator> getAllFacilitator()
    {
        return facilitatorStore.getAll();
    }    
    
    public Collection<Facilitator> getAllFacilitatorOrderByCodeDesc()
    {
        return facilitatorStore.getAllFacilitatorOrderByCodeDesc();
    }    
    
    public Collection<Facilitator> getAllFacilitatorByOrgUnitAndOrderByCodeDesc( OrganisationUnit organisationUnit )
    {
        return facilitatorStore.getAllFacilitatorByOrgUnitAndOrderByCodeDesc( organisationUnit );
    }
        
    @Override
    public Collection<Facilitator> getFacilitatorByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        return facilitatorStore.getFacilitatorByOrganisationUnit( organisationUnit );
    }
    
    @Override
    public Facilitator getActiveFacilitator( OrganisationUnit organisationUnit )
    {
        return facilitatorStore.getActiveFacilitator( organisationUnit );
    } 
    public Collection<Facilitator> getActiveFacilitatorByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        List<Facilitator> facilitatorList = new ArrayList<Facilitator>();
       
        List<Facilitator> tempFacilitatorList = new ArrayList<Facilitator>( getFacilitatorByOrganisationUnit( organisationUnit ) );
        
        for ( Facilitator facilitator : tempFacilitatorList )
        {
            if ( facilitator.isActive() )
            {
                facilitatorList.add( facilitator );
            }
        }

        return facilitatorList;
    }
    
    
    public void searchFacilitatorsByName( List<Facilitator> facilitators, String key )
    {
        Iterator<Facilitator> iterator = facilitators.iterator();

        while ( iterator.hasNext() )
        {
            if ( !iterator.next().getName().toLowerCase().contains( key.toLowerCase() ) )
            {
                iterator.remove();
            }
        }
    }

}
