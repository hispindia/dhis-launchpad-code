package org.hisp.dhis.spis;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.program.Program;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */

@Transactional
public class DefaultCriteriaValueService
    implements CriteriaValueService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CriteriaValueStore criteriaValueStore;

    public void setCriteriaValueStore( CriteriaValueStore criteriaValueStore )
    {
        this.criteriaValueStore = criteriaValueStore;
    }

    // -------------------------------------------------------------------------
    // CriteriaValueService implementation
    // -------------------------------------------------------------------------    
    
    @Override
    public int addCriteriaValue( CriteriaValue criteriaValue )
    {
        return criteriaValueStore.addCriteriaValue( criteriaValue );
    }

    @Override
    public void updateCriteriaValue( CriteriaValue criteriaValue )
    {
        criteriaValueStore.updateCriteriaValue( criteriaValue );
    }
    
    @Override
    public void deleteCriteriaValue( CriteriaValue criteriaValue )
    {
        criteriaValueStore.deleteCriteriaValue( criteriaValue );
    }
    
    @Override
    public CriteriaValue getCriteriaValueById( int Id )
    {
        return criteriaValueStore.getCriteriaValueById( Id );
    }
    
    @Override
    public Collection<CriteriaValue> getAllCriteriaValue()
    {
        return criteriaValueStore.getAllCriteriaValue();
    }    
    
    @Override
    public Collection<CriteriaValue> getCriteriaValues( org.hisp.dhis.spis.Criteria ctr )
    {
        return criteriaValueStore.getCriteriaValues( ctr );
    }
    
    @Override
    public CriteriaValue getCriteriaValue(TrackedEntityAttribute tea, String operator, String validationValue )
    {
        return criteriaValueStore.getCriteriaValue( tea, operator, validationValue );
    }
   
    @Override
    public Set<Program> getProgramByCriteriaValue( TrackedEntityAttribute tea, String operator, String validationValue )
    {
        return criteriaValueStore.getProgramByCriteriaValue( tea, operator, validationValue );
    }
    
    @Override
    public Collection<CriteriaValue> getCriteriaValues( Collection<TrackedEntityAttribute> teas )
    {
        return criteriaValueStore.getCriteriaValues( teas );
    } 
    
    @Override
    public Set<Program> getCriteriaValues( Collection<TrackedEntityAttribute> teas, Collection<String> operators, Collection<String> validationValues )
    {
        return criteriaValueStore.getCriteriaValues( teas, operators, validationValues );
    }     

    @Override
    public List<Integer> getProgramIds()
    {
        return criteriaValueStore.getProgramIds();
    }     
    
}