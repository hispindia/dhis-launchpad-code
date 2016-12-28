package org.hisp.dhis.spis.action.scheme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.system.util.AttributeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowUpdateSchemeFormAction implements Action
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
    
    private Integer selectedProgramId;
    
    public void setSelectedProgramId( Integer selectedProgramId )
    {
        this.selectedProgramId = selectedProgramId;
    }
    
    private Program program;
    
    public Program getProgram()
    {
        return program;
    }

    private List<Attribute> attributes;
    
    public List<Attribute> getAttributes()
    {
        return attributes;
    }
    
    /*
    private List<Program> programs = new ArrayList<Program>();
    
    public List<Program> getPrograms()
    {
        return programs;
    }
    */
    
    private Map<Integer, String> attributeValues = new HashMap<>();

    public Map<Integer, String> getAttributeValues()
    {
        return attributeValues;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        //programs = new ArrayList<Program>( programService.getAllPrograms() );
        
        //Collections.sort( programs, IdentifiableObjectNameComparator.INSTANCE );
        
        program = programService.getProgram( selectedProgramId );
        
        //  attributes related info
        attributes = new ArrayList<Attribute>( attributeService.getProgramAttributes() );
                
        Collections.sort( attributes, IdentifiableObjectNameComparator.INSTANCE );
        
        attributeValues = AttributeUtils.getAttributeValueMap( program.getAttributeValues() );
        
        return SUCCESS;
    }
}
