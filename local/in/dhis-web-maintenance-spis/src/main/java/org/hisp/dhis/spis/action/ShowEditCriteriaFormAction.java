package org.hisp.dhis.spis.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.spis.Criteria;
import org.hisp.dhis.spis.CriteriaService;
import org.hisp.dhis.spis.CriteriaValue;
import org.hisp.dhis.spis.CriteriaValueService;
import org.hisp.dhis.spis.ProgramCriterias;
import org.hisp.dhis.spis.ProgramCriteriasService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeGroup;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ShowEditCriteriaFormAction
    implements Action
{
    public static final String CRITERIA_ATTRIBUTE_GROUP_ID = "CRITERIA_ATTRIBUTE_GROUP_ID";//1106.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    /*
    private TrackedEntityAttributeService trackedEntityAttributeService;

    public void setTrackedEntityAttributeService( TrackedEntityAttributeService trackedEntityAttributeService )
    {
        this.trackedEntityAttributeService = trackedEntityAttributeService;
    }
    */
    
    @Autowired
    private CriteriaService criteriaService;
    @Autowired
    private CriteriaValueService criteriaValueService;

    @Autowired
    private ProgramCriteriasService programCriteriasService;
   
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    @Autowired
    private ConstantService constantService;
    
    @Autowired
    private TrackedEntityAttributeGroupService trackedEntityAttributeGroupService;
    
    
    
    /*
    private ProgramCriteriasService programCriteriasService;

    public void setProgramCriteriasService( ProgramCriteriasService programCriteriasService )
    {
        this.programCriteriasService = programCriteriasService;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public Integer getId() {
		return id;
	}

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
   
    private List<TrackedEntityAttribute> teas = new ArrayList<TrackedEntityAttribute>();
    
    public List<TrackedEntityAttribute> getTeas()
    {
        return teas;
    }

    private Collection<Program> selectedPrograms;

    public Collection<Program> getSelectedPrograms()
    {
        return selectedPrograms;
    }

    private Collection<Program> availablePrograms;

    public Collection<Program> getAvailablePrograms()
    {
        return availablePrograms;
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
    
    public String execute() throws Exception
    {
    	
    	 criteria = criteriaService.getCriteriaById( id );
         
         if( criteria != null )
         {
             criteriaValues = new ArrayList<CriteriaValue>( criteriaValueService.getCriteriaValues( criteria ) );
            
             programCriterias = new ArrayList<ProgramCriterias>( programCriteriasService.getProgramCriterias( criteria ) );
             
         }
        Constant trackedEntityAttributeGroupIdConstant = constantService.getConstantByName( CRITERIA_ATTRIBUTE_GROUP_ID );
        
        TrackedEntityAttributeGroup trackedEntityAttributeGroup =  trackedEntityAttributeGroupService.getTrackedEntityAttributeGroup((int) trackedEntityAttributeGroupIdConstant.getValue() );
        
        teas = new ArrayList<TrackedEntityAttribute>( trackedEntityAttributeGroup.getAttributes() );
       
        //teas = trackedEntityAttributeService.getAllTrackedEntityAttributes();
                
        availablePrograms = programService.getAllPrograms();
        selectedPrograms = programCriteriasService.getPrograms(criteria);
        availablePrograms.removeAll(selectedPrograms);

        return SUCCESS;
    }
}