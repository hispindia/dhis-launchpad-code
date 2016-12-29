package org.hisp.dhis.asha.drugkitiec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadASHADrugKitIECDetailsFormAction implements Action
{
    public static final String ASHA_DRUG_KIT_IEC_HBPNC_PROGRAM_STAGE = "ASHA Drug IEC HBPNC Details Program Stage";//3.0
    
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
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
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
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
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
    
    public Map<Integer, String> patientDataValueMap;
    
    public Map<Integer, String> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }
    
    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
    
    private Period period;
    
    public Period getPeriod()
    {
        return period;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        programStageDataElements = new ArrayList<ProgramStageDataElement>();
        dataElementMap = new HashMap<Integer, DataElement>();
        patientDataValueMap = new HashMap<Integer, String>();
        
        patient = patientService.getPatient( id );
        
        period = periodService.getPeriodByExternalId( selectedPeriodId );
        
        program = programService.getProgram( programId );
        
        Constant drugKitIECProgramStageConstant = constantService.getConstantByName( ASHA_DRUG_KIT_IEC_HBPNC_PROGRAM_STAGE );
        
        programStage = programStageService.getProgramStage( (int) drugKitIECProgramStageConstant.getValue() );
        
        programStageDataElements =  new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        //Collections.sort( programStageDataElements, dataElementComparator );
        
        // Program stage DataElements

        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                dataElementMap.put( programStageDataElement.getDataElement().getId(), programStageDataElement.getDataElement() );
            }
        }
        
        if ( programInstanceId != null )
        {
            programStageInstanceId = ashaService.getProgramStageInstanceId( programInstanceId, programStage.getId(), period.getStartDateString() );
        }
        
        patientDataValueMap = ashaService.getDataValueFromPatientDataValue( programStageInstanceId );
        
        return SUCCESS;
    }
}

