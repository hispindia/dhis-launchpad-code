package org.hisp.dhis.asha.registration;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetRegistrationFormAction implements Action
{
    public static final String OVC_PROG_CONSTANT = "OVC_PROGRAM_ID";
    public static final String OVC_PROG_STAGE_CONSTANT = "OVC_REGISTRATION_STAGE_ID";
    
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    /*
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
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
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private int orgUnitId;
    
    public int getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private Map<Integer, PatientAttribute> patientAttributeMap;
    
    public Map<Integer, PatientAttribute> getPatientAttributeMap()
    {
        return patientAttributeMap;
    }
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return programStage;
    }
    
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
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    
    public String execute()
    {
        /*
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        
        Constant programConstant = constantService.getConstantByName( OVC_PROG_CONSTANT );
        Constant programStageConstant = constantService.getConstantByName( OVC_PROG_STAGE_CONSTANT );
        */
        // -----------------------------------------------------------------------------
        // Prepare Patient Attributes
        // -----------------------------------------------------------------------------
        
        
        /*
        
        patientAttributeMap = new HashMap<Integer, PatientAttribute>();
        
        List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );          
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeMap.put( patientAttribute.getId(), patientAttribute );
        }
       */
        // Program and Program Stage
        
        
        
        /*
        program = programService.getProgram( (int) programConstant.getValue() );
        
        programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        
        programStageDataElements =  new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        */
        //programStageDataElements = programStage.getProgramStageDataElements();
        
        
        // DataElements
        
        
        //dataElementMap = new HashMap<Integer, DataElement>();
        
        //List<DataElement> dataElements =  new ArrayList<DataElement>( dataElementService.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_PATIENT ) );
        
        
        /*
        if( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                dataElementMap.put( programStageDataElement.getDataElement().getId(), programStageDataElement.getDataElement() );
            }
        }
        */
        //System.out.println( "Program Name " + program.getName() + " Program Stage Name is " + programStage.getName() );
        
        //System.out.println( " DataElement Size is  " + programStageDataElements.size() );
        
        //System.out.println( "Size of Patient Attributes " + patientAttributes.size() + " DataElement Size is  " + programStageDataElements.size() );
        
        return SUCCESS;
    }
}
