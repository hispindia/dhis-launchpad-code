package org.hisp.dhis.sm.data.action;

import java.util.Collection;

import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowDataSynchConfigureFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private PeriodService periodService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------


    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<PeriodType> periodTypes;

    public Collection<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        periodTypes = periodService.getAllPeriodTypes();
        
        
        return SUCCESS;
    }
}
