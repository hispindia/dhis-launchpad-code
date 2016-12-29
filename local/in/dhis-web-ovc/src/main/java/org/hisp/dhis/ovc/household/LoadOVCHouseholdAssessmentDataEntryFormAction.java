package org.hisp.dhis.ovc.household;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class LoadOVCHouseholdAssessmentDataEntryFormAction implements Action
{
    public static final String OVC_HOUSEHOLD_ASSESSMENT_PROGRAM = "OVC Household Assessment Program";// 820.0

    public static final String OVC_HOUSEHOLD_ASSESSMENT_PROGRAM_STAGE = "OVC Household Assessment Program Stage";// 821.0

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
    
    private OVCService ovcService;

    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }
    
    /*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    
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
    /*
    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
    */
    
    private Integer programInstanceId;

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
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

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Integer programStageInstanceId;

    public Integer getProgramStageInstanceId()
    {
        return programStageInstanceId;
    }

    public Map<Integer, String> patientDataValueMap;

    public Map<Integer, String> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }
    
    /*
    private Period period;

    public Period getPeriod()
    {
        return period;
    }
    */
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        /*
        period = PeriodType.createPeriodExternalId( selectedPeriodId );

        period.setName( format.formatPeriod( period ) );
        */
        patientDataValueMap = new HashMap<Integer, String>();

        patient = patientService.getPatient( id );
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String ovcRegistrationDate = dateFormat.format( patient.getRegistrationDate() );
        
        //patient.getRegistrationDate().toString();
        
        //System.out.println( " Period Start date : " + period.getStartDateString()  );
        //System.out.println( " OVC Registration date : " + ovcRegistrationDate );
        
        Constant programConstant = constantService.getConstantByName( OVC_HOUSEHOLD_ASSESSMENT_PROGRAM );
        Constant programStageConstant = constantService.getConstantByName( OVC_HOUSEHOLD_ASSESSMENT_PROGRAM_STAGE );
        
        
        
        // program and programStage Related information
        program = programService.getProgram( (int) programConstant.getValue() );

        programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );

        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );

        // Program stage DataElements
        dataElementMap = new HashMap<Integer, DataElement>();

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
            //programStageInstanceId = ovcService.getProgramStageInstanceId( programInstanceId, programStage.getId(), period.getStartDateString() );
            
            programStageInstanceId = ovcService.getProgramStageInstanceId( programInstanceId, programStage.getId(), ovcRegistrationDate );
        }
        
        patientDataValueMap = ovcService.getDataValueFromPatientDataValue( programStageInstanceId );
       
        return SUCCESS;
    }
}
