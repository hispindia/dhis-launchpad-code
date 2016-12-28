package org.hisp.dhis.spis.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.spis.Criteria;
import org.hisp.dhis.spis.CriteriaService;
import org.hisp.dhis.spis.CriteriaValue;
import org.hisp.dhis.spis.CriteriaValueService;
import org.hisp.dhis.spis.ProgramCriterias;
import org.hisp.dhis.spis.ProgramCriteriasService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetCriteriaAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private CriteriaService criteriaService;
    
    @Autowired
    private CriteriaValueService criteriaValueService;

    @Autowired
    private ProgramCriteriasService programCriteriasService;
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Criteria criteria;
    
    public Criteria getCriteria()
    {
        return criteria;
    }
    
    private List<CriteriaValue> criteriaValues = new ArrayList<CriteriaValue>();
    
    public List<CriteriaValue> getCriteriaValues()
    {
        return criteriaValues;
    }

    private List<ProgramCriterias> programCriterias = new ArrayList<ProgramCriterias>();
    
    public List<ProgramCriterias> getProgramCriterias()
    {
        return programCriterias;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        
        criteria = criteriaService.getCriteriaById( id );
        
        if( criteria != null )
        {
            criteriaValues = new ArrayList<CriteriaValue>( criteriaValueService.getCriteriaValues( criteria ) );
           
            programCriterias = new ArrayList<ProgramCriterias>( programCriteriasService.getProgramCriterias( criteria ) );
            
        }
        
        /*
        for ( CriteriaValue crValue : criteriaValues )
        {
            System.out.println( "CR TEA --" + crValue.getTea().getName() );
            System.out.println( " CR OP " + crValue.getOperator()  );
            System.out.println( " CR Value " + crValue.getValidationValue() );
        }
        
        for ( ProgramCriterias pr : programCriterias )
        {
            System.out.println( "Program --" + pr.getProgram().getName() );
        }
        */
        
        return SUCCESS;
    }

}

