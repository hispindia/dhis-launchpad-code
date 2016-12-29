package org.hisp.dhis.asha.training;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowASHATrainingFormAction implements Action
{ 
    public static final String ASHA_TRAINING_ATTRIBUTE = "ASHA Training Status";
    private final String TRANING = "training";
    
    public static final String ASHA_TRAINING_PROGRAM = "ASHA Training Program";// 2.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private ProgramInstanceService programInstanceService;
    
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output / Getter and Setter 
    // -------------------------------------------------------------------------
    
    private int id;
    
    public int getId()
    {
        return id;
    }


    public void setId( int id )
    {
        this.id = id;
    }

    private String attributeGroupName;
    
    public String getAttributeGroupName()
    {
        return attributeGroupName;
    }

    public void setAttributeGroupName( String attributeGroupName )
    {
        this.attributeGroupName = attributeGroupName;
    }
    
    private Integer attributeGroupId;
    
    public Integer getAttributeGroupId()
    {
        return attributeGroupId;
    }

    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }

    private String systemIdentifier;
    
    public String getSystemIdentifier()
    {
        return systemIdentifier;
    }
    
    private Map<Integer, String> identiferMap;
    
    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    private PatientAttributeGroup attributeGroup;
    
    public PatientAttributeGroup getAttributeGroup()
    {
        return attributeGroup;
    }
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    private Integer programInstanceId;
    
    public Integer getProgramInstanceId()
    {
        return programInstanceId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        patient = patientService.getPatient( id );
        
        attributeGroup = new PatientAttributeGroup();
        
        // Attribute Group
        
        if ( attributeGroupName.equalsIgnoreCase( TRANING ))
        {
            
            attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_TRAINING_ATTRIBUTE );
        }
        
        // -------------------------------------------------------------------------
        // Get identifier
        // -------------------------------------------------------------------------
        
        PatientIdentifierType idType = null;
        identiferMap = new HashMap<Integer, String>();
        
        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            idType = identifier.getIdentifierType();

            if ( idType != null )
            {
                identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
            }
            else
            {
                systemIdentifier = identifier.getIdentifier();
            }
        }
        
        
        // Enroll ASHA in Training Program
        Constant trainingProgramConstant = constantService.getConstantByName( ASHA_TRAINING_PROGRAM );
        
        program = programService.getProgram( (int) trainingProgramConstant.getValue() );

        programInstanceId = ashaService.getProgramInstanceId( patient.getId(), program.getId() );
        
        if( programInstanceId == null )
        {
            Patient createdPatient = patientService.getPatient( patient.getId() );
            
            Date programEnrollDate = new Date();
            
            int programType = program.getType();
            ProgramInstance programInstance = null;
            
            if ( programType == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
            {
                programInstance = new ProgramInstance();
                programInstance.setEnrollmentDate(  programEnrollDate  );
                programInstance.setDateOfIncident(  programEnrollDate  );
                programInstance.setProgram( program );
                programInstance.setCompleted( false );

                programInstance.setPatient( createdPatient );
                createdPatient.getPrograms().add( program );
                patientService.updatePatient( createdPatient );

                programInstanceId = programInstanceService.addProgramInstance( programInstance );
                
            }
        }

        //System.out.println( " Program Instance Id is : " + programInstanceId );
        
        
        return SUCCESS;
    }
    
}
