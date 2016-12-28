package org.hisp.dhis.spis;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class DefaultCriteriaService
    implements CriteriaService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CriteriaStore criteriaStore;

    public void setCriteriaStore( CriteriaStore criteriaStore )
    {
        this.criteriaStore = criteriaStore;
    }

    // -------------------------------------------------------------------------
    // CriteriaService implementation
    // -------------------------------------------------------------------------    
    
    @Override
    public int addCriteria( Criteria criteria )
    {
        return criteriaStore.addCriteria( criteria );
    }

    @Override
    public void updateCriteria( Criteria criteria )
    {
        criteriaStore.updateCriteria( criteria );
    }
    
    @Override
    public void deleteCriteria( Criteria criteria )
    {
        criteriaStore.deleteCriteria( criteria );
    }
    
    @Override
    public Criteria getCriteriaById( int Id )
    {

        return criteriaStore.getCriteriaById( Id );
    }
    
    @Override
    public Collection<Criteria> getAllCriteria()
    {
        // TODO Auto-generated method stub
        //return criteriaStore.getAll();
        return criteriaStore.getAllCriteria();
        
    }

}