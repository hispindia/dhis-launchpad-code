package org.hisp.dhis.spis.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.spis.Criteria;
import org.hisp.dhis.spis.CriteriaService;

import com.opensymphony.xwork2.Action;

public class ShowCriteriaFormAction
    implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CriteriaService criteriaService;

    public void setCriteriaService( CriteriaService criteriaService )
    {
        this.criteriaService = criteriaService;
    }
    
    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private List<Criteria> criterias = new ArrayList<Criteria>();
    
    
    public List<Criteria> getCriterias()
    {
        return criterias;
    }

   
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        criterias = new ArrayList<Criteria>( criteriaService.getAllCriteria());
        
        return SUCCESS;
    }
}