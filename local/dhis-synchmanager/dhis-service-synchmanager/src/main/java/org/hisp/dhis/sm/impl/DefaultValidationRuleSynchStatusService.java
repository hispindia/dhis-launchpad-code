package org.hisp.dhis.sm.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatus;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusStore;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultValidationRuleSynchStatusService implements ValidationRuleSynchStatusService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ValidationRuleSynchStatusStore validationRuleSynchStatusStore;
    
    public void setValidationRuleSynchStatusStore( ValidationRuleSynchStatusStore validationRuleSynchStatusStore )
    {
        this.validationRuleSynchStatusStore = validationRuleSynchStatusStore;
    }
    
    private ValidationRuleService validationRuleService;
    
    public void setValidationRuleService( ValidationRuleService validationRuleService )
    {
        this.validationRuleService = validationRuleService;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
    // -------------------------------------------------------------------------
    // implementation
    // -------------------------------------------------------------------------



    @Override
    public void addValidationRuleSynchStatus( ValidationRuleSynchStatus validationRuleSynchStatus )
    {
        validationRuleSynchStatusStore.addValidationRuleSynchStatus( validationRuleSynchStatus );
    }

    @Override
    public void updateValidationRuleSynchStatus( ValidationRuleSynchStatus validationRuleSynchStatus )
    {
        validationRuleSynchStatusStore.updateValidationRuleSynchStatus( validationRuleSynchStatus );
    }

    @Override
    public void deleteValidationRuleSynchStatus( ValidationRuleSynchStatus validationRuleSynchStatus )
    {
        validationRuleSynchStatusStore.deleteValidationRuleSynchStatus( validationRuleSynchStatus );
    }

    @Override
    public ValidationRuleSynchStatus getStatusByInstanceAndValidationRule( SynchInstance instance, ValidationRule validationRule )
    {
        return validationRuleSynchStatusStore.getStatusByInstanceAndValidationRule( instance, validationRule );
    }

    @Override
    public Collection<ValidationRuleSynchStatus> getStatusByInstance( SynchInstance instance )
    {
        return validationRuleSynchStatusStore.getStatusByInstance( instance );
    }
    
    public Collection<ValidationRule> getNewValidationRules()
    {
        return validationRuleSynchStatusStore.getNewValidationRules();
    }
    
    public Collection<ValidationRule> getUpdatedValidationRules()
    {
        return validationRuleSynchStatusStore.getUpdatedValidationRules();
    }

    public Collection<ValidationRuleSynchStatus> getUpdatedValidationRuleSyncStatus()
    {
        return validationRuleSynchStatusStore.getUpdatedValidationRuleSyncStatus();
    }    
    
    public Collection<ValidationRule> getApprovedValidationRules()
    {
        return validationRuleSynchStatusStore.getApprovedValidationRules();
    }

    @Override
    public Collection<ValidationRuleSynchStatus> getSynchStausByValidationRule( ValidationRule validationRule )
    {
        return validationRuleSynchStatusStore.getSynchStausByValidationRule( validationRule );
    }    
    
    @Override
    public Collection<ValidationRuleSynchStatus> getSynchStausByValidationRules( Collection<ValidationRule> validationRules )
    {
        return validationRuleSynchStatusStore.getSynchStausByValidationRules( validationRules );
    }
    
    public Collection<ValidationRuleSynchStatus> getAllValidationRuleSynchStatus()
    {
        return validationRuleSynchStatusStore.getAllValidationRuleSynchStatus();
    }
    
    // get all validation rule
    public List<ValidationRule> getValidationRuleList()
    {
        List<ValidationRule> validationRuleList = new ArrayList<ValidationRule>();

        try
        {
            String query =  " SELECT validationruleid FROM validationrule "; 
                     
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
//            System.out.println("Inside validation rule ");
               while ( rs.next() )
                {
                    Integer validationRuleId = rs.getInt( 1 );
                    
                    if ( validationRuleId != null )
                    {
                        ValidationRule validationRule = new ValidationRule();
                        
                        validationRule = validationRuleService.getValidationRule( validationRuleId );
                        validationRuleList.add( validationRule );
                    }
                    
                }
           
            return validationRuleList;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal ValidationRule id", e );
        }
    }    
 
    @Override
    public Collection<ValidationRuleSynchStatus> getPendingValidationRuleSyncStatus( SynchInstance instance )
    {
        return validationRuleSynchStatusStore.getPendingValidationRuleSyncStatus( instance );
    }    

    @Override
    public Collection<ValidationRule> getValidationRuleByInstance( SynchInstance instance )
    {
        return validationRuleSynchStatusStore.getValidationRuleByInstance( instance );
    }        
    
    @Override
    public Collection<ValidationRule> getApprovedValidationRuleByInstance( SynchInstance instance )
    {
        return validationRuleSynchStatusStore.getApprovedValidationRuleByInstance( instance );
    }        
}

