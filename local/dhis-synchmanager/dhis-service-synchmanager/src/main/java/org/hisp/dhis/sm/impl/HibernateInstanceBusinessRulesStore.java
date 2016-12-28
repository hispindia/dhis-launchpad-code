package org.hisp.dhis.sm.impl;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRules;
import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesStore;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.user.User;

/**
 * @author BHARATH
 */

public class HibernateInstanceBusinessRulesStore
    extends HibernateIdentifiableObjectStore<InstanceBusinessRules>
    implements InstanceBusinessRulesStore
{
    @Override
    public InstanceBusinessRules getInstanceRulesByInstance( SynchInstance instance )
    {
        return (InstanceBusinessRules) getCriteria( Restrictions.eq( "instance", instance ) ).uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SynchInstance> getInstancesForApprovalUser( User user )
    {
        /*
        Collection<Instance> instances = new HashSet<Instance>();
        String sql = "select ";
        SqlRowSet rs = jdbcTemplate.queryForRowSet( sql );
        while ( rs.next() )
        {
            
        }
        */
        
        String hql = "select ibr.instance from InstanceBusinessRules ibr, UserGroup ug where ibr.approvalUserGroupUid = ug.uid and :user in elements(ug.members)";
        
        Query query = getQuery( hql );
        query.setEntity( "user", user );

        return (Collection<SynchInstance>) query.list();

    }
}
