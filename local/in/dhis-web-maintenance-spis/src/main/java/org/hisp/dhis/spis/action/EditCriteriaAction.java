package org.hisp.dhis.spis.action;

import java.util.Calendar;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.spis.Criteria;
import org.hisp.dhis.spis.CriteriaService;
import org.hisp.dhis.spis.CriteriaValue;
import org.hisp.dhis.spis.CriteriaValueService;
import org.hisp.dhis.spis.ProgramCriterias;
import org.hisp.dhis.spis.ProgramCriteriasService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class EditCriteriaAction
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

    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private TrackedEntityAttributeService trackedEntityAttributeService;

    public void setTrackedEntityAttributeService( TrackedEntityAttributeService trackedEntityAttributeService )
    {
        this.trackedEntityAttributeService = trackedEntityAttributeService;
    }

    private CriteriaValueService criteriaValueService;

    public void setCriteriaValueService( CriteriaValueService criteriaValueService )
    {
        this.criteriaValueService = criteriaValueService;
    }

    private ProgramCriteriasService programCriteriasService;

    public void setProgramCriteriasService( ProgramCriteriasService programCriteriasService )
    {
        this.programCriteriasService = programCriteriasService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private Integer id;
    
    public void setId(Integer id) {
		this.id = id;
	}
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
  
	public String execute() throws Exception
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        
        // Save in Criteria
        int count = Integer.parseInt( request.getParameter( "count" ) );
        Criteria criteria = criteriaService.getCriteriaById(id);
        criteria.setName( request.getParameter( "name" ) );
        criteria.setCode( request.getParameter( "code" ) );
        criteria.setDescription( request.getParameter( "description" ) );
        criteria.setCreated( Calendar.getInstance().getTime() );
        
        criteria.setLastUpdated(  Calendar.getInstance().getTime() );
        
        criteria.setUser( currentUserService.getCurrentUser() );
        
        
        int criteriaId = criteriaService.addCriteria( criteria );
        
        criteria = criteriaService.getCriteriaById( criteriaId );
        
        //delete all previous criteria values
        Collection<CriteriaValue> criteriaValues = criteriaValueService.getCriteriaValues(criteria);
        for (CriteriaValue cv : criteriaValues){
        	criteriaValueService.deleteCriteriaValue(cv);
        }
        
        // Save in Criteria value
        for ( int i = 1; i < count; i++ )
        {
            String teaUid = request.getParameter( "tea" + i );
            TrackedEntityAttribute tea = trackedEntityAttributeService.getTrackedEntityAttribute( teaUid );

            String operator = request.getParameter( "operator" + i );
            String validationValue = request.getParameter( "validationvalue" + i );
            CriteriaValue criteriaValue = new CriteriaValue();
            criteriaValue.setCriteria( criteria );
            criteriaValue.setTea( tea );
            criteriaValue.setOperator( operator );
            criteriaValue.setValidationValue( validationValue );
            criteriaValueService.addCriteriaValue( criteriaValue );
        }

        //delete all previous program criterias
        Collection<ProgramCriterias> programcriterias = programCriteriasService.getProgramCriterias(criteria);
        for (ProgramCriterias pc : programcriterias){
        	programCriteriasService.deleteProgramCriterias(pc);
        }
        
        
        // Save Program and Criteria
        String[] programArray = request.getParameterValues( "selectedProgramIds" );
        for ( int i = 0; i < programArray.length; i++ )
        {
            Program program = programService.getProgram( Integer.parseInt( programArray[i] ) );
            ProgramCriterias programCriterias = new ProgramCriterias();
            programCriterias.setCriteria( criteria );
            programCriterias.setProgram( program );
            programCriteriasService.save( programCriterias );
        }

        return SUCCESS;
    }

}