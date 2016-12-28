package org.hisp.dhis.spis.action.scheme;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.system.util.AttributeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class UpdateSchemeInfoAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private AttributeService attributeService;

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------
    
    private Integer selProgramId;
    
    public void setSelProgramId( Integer selProgramId )
    {
        this.selProgramId = selProgramId;
    }

    private Program program;
    
    public Program getProgram()
    {
        return program;
    }

    private List<String> jsonAttributeValues = new ArrayList<>();

    public void setJsonAttributeValues( List<String> jsonAttributeValues )
    {
        this.jsonAttributeValues = jsonAttributeValues;
    }

    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
 
    public String execute() throws Exception
    {
        // program related info
        program = programService.getProgram( selProgramId );
        
        //System.out.println( " selectedProgramId -- " + selProgramId + " program -- " + program.getName() ) ;
        
        program.increaseVersion();

        if ( jsonAttributeValues != null )
        {
            AttributeUtils.updateAttributeValuesFromJson( program.getAttributeValues(), jsonAttributeValues, attributeService );
        }

        programService.updateProgram( program );
        
        return SUCCESS;
    }
}

