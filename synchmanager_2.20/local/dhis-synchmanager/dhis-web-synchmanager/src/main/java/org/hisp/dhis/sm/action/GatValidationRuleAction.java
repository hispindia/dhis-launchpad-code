package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.dxf2.sm.api.InstanceBusinessRulesService;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatus;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Ganesh
 * 
 */

public class GatValidationRuleAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private InstanceBusinessRulesService instanceBusinessRulesService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private ValidationRuleService validationRuleService;

    private ValidationRuleSynchStatusService validationRuleSynchStatusService;

    // ------------------------------------------------------------------------
    // Setters & Getters
    // -------------------------------------------------------------------------

    public void setValidationRuleSynchStatusService( ValidationRuleSynchStatusService validationRuleSynchStatusService )
    {
        this.validationRuleSynchStatusService = validationRuleSynchStatusService;
    }

    private int validationRuleId;

    public void setValidationRuleId( int validationRuleId )
    {
        this.validationRuleId = validationRuleId;
    }

    private ValidationRule validationRuleObject;

    public ValidationRule getValidationRuleObject()
    {
        return validationRuleObject;
    }

    public void setValidationRuleObject( ValidationRule validationRuleObject )
    {
        this.validationRuleObject = validationRuleObject;
    }

    Collection<SynchInstance> synchInstances = new ArrayList<SynchInstance>();

    public Collection<SynchInstance> getSynchInstances()
    {
        return synchInstances;
    }

    public void setSynchInstances( Collection<SynchInstance> synchInstances )
    {
        this.synchInstances = synchInstances;
    }

    private Collection<ValidationRuleSynchStatus> AllValidationRules;

    public Collection<ValidationRuleSynchStatus> getAllValidationRules()
    {
        return AllValidationRules;
    }

    public void setAllValidationRules( Collection<ValidationRuleSynchStatus> allValidationRules )
    {
        AllValidationRules = allValidationRules;
    }

    Collection<SynchInstance> instancesLeft;

    public Collection<SynchInstance> getInstancesLeft()
    {
        return instancesLeft;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    @Override
    public String execute()
        throws Exception
    {

        validationRuleObject = validationRuleService.getValidationRule( validationRuleId );

        AllValidationRules = new ArrayList<ValidationRuleSynchStatus>();
        AllValidationRules.addAll( validationRuleSynchStatusService
            .getSynchStausByValidationRule( validationRuleObject ) );

        synchInstances.addAll( instanceBusinessRulesService.getInstancesForApprovalUser( currentUserService
            .getCurrentUser() ) );

        instancesLeft = new ArrayList<SynchInstance>();
        instancesLeft.addAll( synchInstances );

        for ( ValidationRuleSynchStatus validationRuleSynchStatus : AllValidationRules )
        {
            instancesLeft.remove( validationRuleSynchStatus.getInstance() );
        }

        return SUCCESS;
    }
}
