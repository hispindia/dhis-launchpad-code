package org.hisp.dhis.caseentry.action.caseentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.caseentry.state.Service;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadLatestAnonymousDataAction implements Action
{
    public static final String PROGRAM_CONSTANT = "PROGRAM_CONSTANT";//1

    public static final String PROGRAM_STAGE_CONSTANT = "PROGRAM_STAGE_CONSTANT";// 1
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    
    private Service service;
    
    public void setService( Service service )
    {
        this.service = service;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<ProgramStageDataElement> programStageDataElements;

    public Collection<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }
    
    private Map<Integer, DataElement> dataElementMap;

    public Map<Integer, DataElement> getDataElementMap()
    {
        return dataElementMap;
    }   
    
    private List<Integer> programStageInstanceIds = new ArrayList<Integer>();
    
    public List<Integer> getProgramStageInstanceIds()
    {
        return programStageInstanceIds;
    }

    public Map<String, String> patientDataValueMap;

    public Map<String, String> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {

        Constant programConstant = constantService.getConstantByName( PROGRAM_CONSTANT );
        Constant programStageConstant = constantService.getConstantByName( PROGRAM_STAGE_CONSTANT );
        
        Program program = programService.getProgram( (int) programConstant.getValue() );

        ProgramStage programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        
        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );

        // Program stage DataElements
        dataElementMap = new HashMap<Integer, DataElement>();
        
        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                dataElementMap.put( programStageDataElement.getDataElement().getId(), programStageDataElement.getDataElement() );
            }
        }
        
        programStageInstanceIds = new ArrayList<Integer>();
        patientDataValueMap = new HashMap<String, String>();
        
        if( program != null &&  programStage != null )
        {
            programStageInstanceIds = new ArrayList<Integer>( service.getLatestProgramStageInstanceIds() );
            patientDataValueMap = service.getLatestDataValueFromPatientDataValue( program.getId(), programStage.getId() );
        }
        
        return SUCCESS;
    }
}

