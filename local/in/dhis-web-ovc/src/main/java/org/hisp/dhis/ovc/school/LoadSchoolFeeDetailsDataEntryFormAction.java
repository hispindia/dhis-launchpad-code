package org.hisp.dhis.ovc.school;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadSchoolFeeDetailsDataEntryFormAction implements Action
{
    public static final String OVC_ID = "OVC_ID";//929.0
    
    public static final String OVC_SCHOOL_MANAGEMENT_PROGRAM = "OVC_SCHOOL_MANAGEMENT_PROGRAM";//1700.0

    public static final String OVC_SCHOOL_MANAGEMENT_PROGRAM_STAGE = "OVC_SCHOOL_MANAGEMENT_PROGRAM_STAGE";// 1703.0

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
    
    private OVCService ovcService;

    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }
    
    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }
    
    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
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
   
    private School school;
    
    public School getSchool()
    {
        return school;
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

    public Map<String, String> patientDataValueMap;

    public Map<String, String> getPatientDataValueMap()
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
    
    private List<Patient> ovcList = new ArrayList<Patient>();
    
    public List<Patient> getOvcList()
    {
        return ovcList;
    }
    
    private Period period;

    public Period getPeriod()
    {
        return period;
    }
    
    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private Map<String, String> patientAttributeValueMap = new HashMap<String, String>();
    
    public Map<String, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
    
    private Map<Integer, String> ovcIDMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getOvcIDMap()
    {
        return ovcIDMap;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        patientDataValueMap = new HashMap<String, String>();
        
        school = schoolService.getSchool( id );
        
        period = PeriodType.createPeriodExternalId( selectedPeriodId );

        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String periodLastDate = dateFormat.format( period.getEndDate() );
        
        period.setName( format.formatPeriod( period ) );
        
        ovcList = new ArrayList<Patient>( ovcService.getPatients( school.getId(), periodLastDate ) );
        /*
        for( Patient patient : ovcList )
        {
            patient.getGender().getFullName()
        }
        */
        
        Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers(Patient.class, ovcList ) );
        
        String patientIdsByComma = getCommaDelimitedString( patientIds );
        
        Constant patientIdentifierTypeConstant = constantService.getConstantByName( OVC_ID );
        
        PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( (int) patientIdentifierTypeConstant.getValue() );
        
        
        
        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------
        
        for ( Patient patient : ovcList )
        {
            Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( patient );

            for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
            {
                if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute().getValueType() ) )
                {
                    patientAttributeValueMap.put( patient.getId() +":" + patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getPatientAttributeOption().getName() );
                }
                else
                {
                    patientAttributeValueMap.put( patient.getId() + ":" + patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getValue() );
                }
            }
            
            String ovcId = "";
            
            if ( school.getOrganisationUnit().getCode() != null && identifierType != null )
            {
                PatientIdentifier ovcIdIdentifier = patientIdentifierService.getPatientIdentifier( identifierType, patient );
                
                if( ovcIdIdentifier != null )
                {
                    ovcId =   ovcIdIdentifier.getIdentifier() ;
                }
               
                ovcIDMap.put( patient.getId(), ovcId );
            }
            else
            {
                PatientIdentifierType idType = null;
                
                for ( PatientIdentifier identifier : patient.getIdentifiers() )
                {
                    idType = identifier.getIdentifierType();

                    if ( idType != null )
                    {
                        //identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
                    }
                    else
                    {
                        ovcId = identifier.getIdentifier();
                    }
                }
                ovcIDMap.put( patient.getId(), ovcId );
            }
            
            //ovcIDMap.put( ""+patient.getId(), ovcId );
            
        }
        
     
        /*
        for( Patient patient : ovcList )
        {
            System.out.println(  patient.getFullName() + " --  " + ovcIDMap.get( patient.getId()  ) );
        }
        */
        
        Constant programConstant = constantService.getConstantByName( OVC_SCHOOL_MANAGEMENT_PROGRAM );
        Constant programStageConstant = constantService.getConstantByName( OVC_SCHOOL_MANAGEMENT_PROGRAM_STAGE );
        
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
                dataElementMap.put( programStageDataElement.getDataElement().getId(), programStageDataElement.getDataElement() );
            }
        }
        
        if( patientIds != null &&  patientIds.size() > 0 )
        {
            if( program != null &&  programStage != null )
            {
                patientDataValueMap = ovcService.getPatientDataValuesByExecutionDate( patientIdsByComma, program.getId(), programStage.getId(), period.getStartDateString()  );
            }
            
        }
       
        return SUCCESS;
    }
}
