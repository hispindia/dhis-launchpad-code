package org.hisp.dhis.spis.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;
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

public class RemoveCriteriaAction implements Action
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
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        try
        {  
            Criteria criteria = criteriaService.getCriteriaById( id );
            
            if( criteria != null )
            {
                List<CriteriaValue> criteriaValues = new ArrayList<CriteriaValue>();
                criteriaValues = new ArrayList<CriteriaValue>( criteriaValueService.getCriteriaValues( criteria ) );
                
                if ( criteriaValues != null && criteriaValues.size() > 0 )
                {
                    for( CriteriaValue criteriaValue : criteriaValues )
                    {
                        criteriaValueService.deleteCriteriaValue( criteriaValue );
                    }
                }
                
                List<ProgramCriterias> programCriterias = new ArrayList<ProgramCriterias>();
                programCriterias = new ArrayList<ProgramCriterias>( programCriteriasService.getProgramCriterias( criteria ) );
                
                if ( programCriterias != null && programCriterias.size() > 0 )
                {
                    for( ProgramCriterias programCriteria : programCriterias )
                    {
                        programCriteriasService.deleteProgramCriterias( programCriteria );
                    }
                }
                
                criteriaService.deleteCriteria( criteria );
            }
            
            message = i18n.getString( "delete_success" );
        }
        
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();

                return ERROR;
            }
        }

        return SUCCESS;
    }    
    
}
