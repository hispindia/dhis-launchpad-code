package org.hisp.dhis.asha.payment;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadASHAMonthlyPaymentDetailsFormAction implements Action
{ 
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//4.0
    
    private final String OPTION_SET_MONTHLY_PAYMENT_DATAELEMENT = "Monthly Payment DataElement";
    
    public static final String MODE_OF_PAYMENT_DATAELEMENT_ID = "Mode of Payment Dataelement Id";//174.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------    
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) 
    {
        this.organisationUnitService = organisationUnitService;
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
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
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
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private int orgUnitId;
    
    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
    
    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }   
    
    private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
    private List<OrganisationUnit > programSources = new ArrayList<OrganisationUnit>();
    
    private List<Patient> ashaList = new ArrayList<Patient>();
    
    public List<Patient> getAshaList()
    {
        return ashaList;
    }
    
    private Map<Integer, String> mapPatientOrgunit = new HashMap<Integer, String>();

    public Map<Integer, String> getMapPatientOrgunit()
    {
        return mapPatientOrgunit;
    }
    
    private List<String> modeOfPaymentOptions;
    
    public List<String> getModeOfPaymentOptions()
    {
        return modeOfPaymentOptions;
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
    
    public Map<String, String> patientDataValueMap;

    public Map<String, String> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }
    
    private Period period;

    public Period getPeriod()
    {
        return period;
    }
    
    private List<DataElement> paymentDataElementList;
    
    public List<DataElement> getPaymentDataElementList()
    {
        return paymentDataElementList;
    }
    
    private Map<String, String> patientAttributeValueMap = new HashMap<String, String>();
    
    public Map<String, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // --------- ----------------------------------------------------------------

    public String execute() throws Exception
    {
        period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        // OrgUnit Related Info
        organisationUnit = new OrganisationUnit();
        organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId ); 
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
        
        Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_ID );
        
        program = programService.getProgram( (int) programConstant.getValue() );
        
        programSources = new ArrayList<OrganisationUnit>( program.getOrganisationUnits() );
        
        if( program != null &&  programSources != null && programSources.size() > 0 )
        {
            orgUnitList.retainAll( programSources );
        }    
        
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            List<Patient> patientList = new ArrayList<Patient>( patientService.getPatients( orgUnit, null,null ) );
            
            if( patientList != null && patientList.size() > 0 ) 
            {
                //System.out.println( orgUnit.getName()  + " : Patient List Size is : " + patientList.size() );
                ashaList.addAll( patientList );
            }
        }
        
        //System.out.println(  " Final ASHA List Size is : " + ashaList.size() );
        
        modeOfPaymentOptions = new ArrayList<String>();
        
        Constant modeOfPaymentDataElementConstant = constantService.getConstantByName( MODE_OF_PAYMENT_DATAELEMENT_ID );
        DataElement modeOfPaymentDataElement = dataElementService.getDataElement( (int) modeOfPaymentDataElementConstant.getValue() );
        
        if ( modeOfPaymentDataElement.getOptionSet() != null )
        {
            modeOfPaymentOptions =  new ArrayList<String>( modeOfPaymentDataElement.getOptionSet().getOptions() );
        }
        
        Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers(Patient.class, ashaList ) );
        
        String patientIdsByComma = getCommaDelimitedString( patientIds );
        
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_MONTHLY_PAYMENT_DATAELEMENT );
        
        paymentDataElementList = new ArrayList<DataElement>();
        for( String optionName : optionSet.getOptions() )
        {
            DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( optionName ) );
            paymentDataElementList.add( dataElement );
        }
        
        DataElement paymentStatusDataElement = dataElementService.getDataElement( 508 );
        
        paymentDataElementList.add( paymentStatusDataElement );
        
        Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers( DataElement.class, paymentDataElementList ) );
        String dataElementIdsByComma = getCommaDelimitedString( dataElementIds );
        
        patientAttributeValueMap = new HashMap<String, String>();
        if( ashaList != null && ashaList.size() > 0 )
        {
            for ( Patient patient : ashaList )
            {
                //patient.getOrganisationUnit().getParent().getParent().getParent().getName();
                
                //mapPatientOrgunit.put( patient.getId(), patient.getOrganisationUnit().getParent().getParent().getParent().getName() );
                //mapPatientOrgunit.put( patient.getId(), getHierarchyOrgunit( patient.getOrganisationUnit() ) );
                
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
            }
        }
       
        
        Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID );
        
        // program and programStage Related information
        program = programService.getProgram( (int) programConstant.getValue() );

        programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        
        patientDataValueMap = new HashMap<String, String>();
        
        if( patientIds != null &&  patientIds.size() > 0 )
        {
            if( program != null &&  programStage != null )
            {
                patientDataValueMap = ashaService.getPatientDataValuesByExecutionDate( patientIdsByComma, program.getId(), programStage.getId(), period.getStartDateString(), dataElementIdsByComma  );
            }
            
        }
        
        return SUCCESS;
    }
    
    
    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------
    //&& organisationUnitService.getLevelOfOrganisationUnit( orgunit.getId() ) >=3
    /*
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " / " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }
        
        hierarchyOrgunit = hierarchyOrgunit.substring( hierarchyOrgunit.indexOf( "/" ) + 1 );
        
        return hierarchyOrgunit;
    }    
    */
    
}
