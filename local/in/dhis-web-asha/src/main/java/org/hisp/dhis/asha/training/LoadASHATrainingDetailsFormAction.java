package org.hisp.dhis.asha.training;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadASHATrainingDetailsFormAction implements Action
{
    public static final String ASHA_TRAINING_PROGRAM_STAGE = "ASHA Training Program Stage";//2.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

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
    
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private int id;
    
    public void setId( int id )
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }
    
    private String selectedPeriod;
    
    public void setSelectedPeriod( String selectedPeriod )
    {
        this.selectedPeriod = selectedPeriod;
    }

    public String getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private Integer programId;
    
    public Integer getProgramId()
    {
        return programId;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private Integer programInstanceId;
    
    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }
    
    public Integer getProgramInstanceId()
    {
        return programInstanceId;
    }

    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
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
    
    private Integer programStageInstanceId;
    
    public Integer getProgramStageInstanceId()
    {
        return programStageInstanceId;
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
    
    /*
    public Map<Integer, String> patientDataValueMap;
    
    public Map<Integer, String> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }
    */
    
    public Map<Integer, List<String>> patientDataValueMap;
    
    public Map<Integer, List<String>> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        programStageDataElements = new ArrayList<ProgramStageDataElement>();
        dataElementMap = new HashMap<Integer, DataElement>();
        patientDataValueMap = new HashMap<Integer, List<String>>();
        
        patient = patientService.getPatient( id );
        
        if ( selectedPeriod != null )
        {
            selectedPeriod = selectedPeriod.trim();
        }
        
        program = programService.getProgram( programId );
        
        Constant trainingProgramStageConstant = constantService.getConstantByName( ASHA_TRAINING_PROGRAM_STAGE );
        
        programStage = programStageService.getProgramStage( (int) trainingProgramStageConstant.getValue() );
        
        programStageDataElements =  new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        
        // Program stage DataElements

        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                dataElementMap.put( programStageDataElement.getDataElement().getId(), programStageDataElement
                    .getDataElement() );
            }
        }
        
        
        if ( programInstanceId != null )
        {
            programStageInstanceId = ashaService.getProgramStageInstanceId( programInstanceId, programStage.getId(), selectedPeriod );
        }
        
        //patientDataValueMap = ashaService.getDataValueFromPatientDataValue( programStageInstanceId );
        
        patientDataValueMap = ashaService.getLatestPatientData( patient.getId(), program.getId(), programStage.getId(), selectedPeriod );
        
        /*
        for( Integer dataElementId : patientDataValueMap.keySet() )
        {
            List<String> value = new ArrayList<String>( patientDataValueMap.get( dataElementId ));
            
            System.out.println( " DataElement Id  -- "  + dataElementId + " -- Value is -- " + value.get( 0 )  + " -- Date is " + value.get( 1 ) );
            
        }
        */
        
        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------
        
        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute().getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getValue() );
            }
        }
        
        
        
        return SUCCESS;
    }
}
