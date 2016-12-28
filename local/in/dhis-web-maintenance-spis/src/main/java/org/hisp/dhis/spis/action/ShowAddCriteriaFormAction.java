package org.hisp.dhis.spis.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeGroup;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ShowAddCriteriaFormAction
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
    // Input / Output
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        Constant trackedEntityAttributeGroupIdConstant = constantService.getConstantByName( CRITERIA_ATTRIBUTE_GROUP_ID );
        
        TrackedEntityAttributeGroup trackedEntityAttributeGroup =  trackedEntityAttributeGroupService.getTrackedEntityAttributeGroup((int) trackedEntityAttributeGroupIdConstant.getValue() );
        
        teas = new ArrayList<TrackedEntityAttribute>( trackedEntityAttributeGroup.getAttributes() );
       
        //teas = trackedEntityAttributeService.getAllTrackedEntityAttributes();
                
        availablePrograms = programService.getAllPrograms();

        return SUCCESS;
    }
}