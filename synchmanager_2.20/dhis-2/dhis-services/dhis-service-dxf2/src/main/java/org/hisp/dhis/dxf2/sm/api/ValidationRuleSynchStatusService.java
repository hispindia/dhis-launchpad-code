package org.hisp.dhis.dxf2.sm.api;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.validation.ValidationRule;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface ValidationRuleSynchStatusService
{
    String ID = ValidationRuleSynchStatusService.class.getName();
    
    void addValidationRuleSynchStatus( ValidationRuleSynchStatus validationRuleSynchStatus );
    
    void updateValidationRuleSynchStatus( ValidationRuleSynchStatus validationRuleSynchStatus );
    
    void deleteValidationRuleSynchStatus( ValidationRuleSynchStatus validationRuleSynchStatus );
    
    ValidationRuleSynchStatus getStatusByInstanceAndValidationRule( SynchInstance instance, ValidationRule validationRule );
    
    Collection<ValidationRuleSynchStatus> getStatusByInstance( SynchInstance instance );
    
    Collection<ValidationRule> getNewValidationRules();
    
    Collection<ValidationRule> getUpdatedValidationRules();
    
    Collection<ValidationRuleSynchStatus> getUpdatedValidationRuleSyncStatus();

    Collection<ValidationRule> getApprovedValidationRules();
    
    Collection<ValidationRuleSynchStatus> getSynchStausByValidationRule( ValidationRule validationRule );
    
    Collection<ValidationRuleSynchStatus> getSynchStausByValidationRules( Collection<ValidationRule> validationRules );
    
    Collection<ValidationRuleSynchStatus> getAllValidationRuleSynchStatus();
    
    List<ValidationRule> getValidationRuleList();
    
    Collection<ValidationRuleSynchStatus> getPendingValidationRuleSyncStatus( SynchInstance instance );
    
    Collection<ValidationRule> getValidationRuleByInstance( SynchInstance instance );
    
    Collection<ValidationRule> getApprovedValidationRuleByInstance( SynchInstance instance );
}
