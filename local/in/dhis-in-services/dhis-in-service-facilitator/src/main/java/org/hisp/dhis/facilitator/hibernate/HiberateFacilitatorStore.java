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
package org.hisp.dhis.facilitator.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 */

public class HiberateFacilitatorStore extends HibernateIdentifiableObjectStore<Facilitator> implements FacilitatorStore
{
   
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Facilitator
    // -------------------------------------------------------------------------
    
    public Facilitator getFacilitator( int id )
    {
        return (Facilitator) sessionFactory.getCurrentSession().get( Facilitator.class, id );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Facilitator> getFacilitatorByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        return getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ) ).list();
    }
    
    
    @SuppressWarnings( "unchecked" )
    public Collection<Facilitator> getAllFacilitatorOrderByCodeDesc()
    {
        Criteria criteria = getCriteria();
        return  criteria.addOrder( Order.desc("code")) .list();
    }
    
    @SuppressWarnings( "unchecked" )
    public  Collection<Facilitator> getAllFacilitatorByOrgUnitAndOrderByCodeDesc( OrganisationUnit organisationUnit )
    {
        Criteria criteria = getCriteria( Restrictions.eq( "organisationUnit", organisationUnit ) );
        
        return criteria.addOrder( Order.desc("code")) .list();
        
        //return criteria.list();
    }
    
    
    public Facilitator getActiveFacilitator( OrganisationUnit organisationUnit )
    {
        Criteria criteria = getCriteria();
        
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "active", true ) );
        
        return (Facilitator) criteria.uniqueResult();
        
    }
    
    
}
