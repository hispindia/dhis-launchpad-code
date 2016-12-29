package org.hisp.dhis.asha.payment;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class LoadASHAPaymentDetailsFormAction implements Action
{
    
    public static final String ASHA_ACTIVITY_PROGRAM = "ASHA Activity Program";//1.0
    public static final String ASHA_ACTIVITY_PROGRAM_STAGE = "ASHA Activity Program Stage";//1.0
    public static final String PAYMENT_DUE_DATAELEMENT = "Payment Due";//159.0
    private final String OPTION_SET_PAYMENT = "Payment";
    public static final String MODE_OF_PAYMENT_DATAELEMENT_ID = "Mode of Payment Dataelement Id";//174.0
    
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
    
    private ProgramInstanceService programInstanceService;
    
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    private DataElementService dataElementService;
    
    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
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
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
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
    
    private List<DataElement> paymentDataElementList;
    
    public List<DataElement> getPaymentDataElementList()
    {
        return paymentDataElementList;
    }
    
    private String paymentDueValue;
    
    public String getPaymentDueValue()
    {
        return paymentDueValue;
    }
    
    private List<String> modeOfPaymentOptions;
    
    public List<String> getModeOfPaymentOptions()
    {
        return modeOfPaymentOptions;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        patientDataValueMap = new HashMap<Integer, String>();
        
        paymentDataElementList = new ArrayList<DataElement>();
        
        modeOfPaymentOptions = new ArrayList<String>();
        
        patient = patientService.getPatient( id );
        
        Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM );
        Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM_STAGE );
        
        program = programService.getProgram( (int) programConstant.getValue() );
        
        programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );
        
        programStageDataElements =  new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        
        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );
        
        if ( programInstanceId != null )
        {
            programStageInstanceId = ashaService.getProgramStageInstanceId( programInstanceId, programStage.getId(), period.getStartDateString() );
        }
        
        if ( programStageInstanceId == null )
        {
            ProgramStageInstance programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );
            programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
            
            programStageInstance.setDueDate( format.parseDate( period.getStartDateString() ) );
            
            programStageInstanceId = programStageInstanceService.addProgramStageInstance( programStageInstance );
        }
        
        
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_PAYMENT );
        
        for( String optionName : optionSet.getOptions() )
        {
            DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( optionName ) );
            paymentDataElementList.add( dataElement );
        }
        
        Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers(DataElement.class, paymentDataElementList ) );
        String dataElementIdsByComma = getCommaDelimitedString( dataElementIds );
        
        Constant modeOfPaymentDataElementConstant = constantService.getConstantByName( MODE_OF_PAYMENT_DATAELEMENT_ID );
        DataElement modeOfPaymentDataElement = dataElementService.getDataElement( (int) modeOfPaymentDataElementConstant.getValue() );
        
        if ( modeOfPaymentDataElement.getOptionSet() != null )
        {
            modeOfPaymentOptions =  new ArrayList<String>( modeOfPaymentDataElement.getOptionSet().getOptions() );
        }
        
        /*
        for( String option : modeOfPaymentOptions )
        {
            System.out.println( " option -- "+ option );
                  
        }
        
        //patientDataValueMap = ashaService.getDataValueFromPatientDataValue( programStageInstanceId );
        /*
        for( DataElement dataElement : paymentDataElementList )
        {
            System.out.println( dataElement.getId() + " -- "+ dataElement.getName() );
                  
        }
        */
        
        Constant paymentDueConstant = constantService.getConstantByName( PAYMENT_DUE_DATAELEMENT );
        
        //DataElement paymentDueDataElement = dataElementService.getDataElement( (int) paymentDueConstant.getValue() );
        
        dataElementIdsByComma += "," + (int) paymentDueConstant.getValue();
        
        patientDataValueMap = ashaService.getDataValueFromPatientDataValue( programStageInstanceId, dataElementIdsByComma );
        
        
        paymentDueValue = patientDataValueMap.get( (int) paymentDueConstant.getValue() );
        
        
        Calendar previousExecution = Calendar.getInstance();
        
        previousExecution.setTime( period.getStartDate() );
        
        previousExecution.add( Calendar.MONTH , -1  );
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String previousExecutionDate = simpleDateFormat.format( previousExecution.getTime() );
        
        if( paymentDueValue == null )
        {
            //System.out.println(  " Inside Payment Data Value null" );
            
            paymentDueValue = ashaService.getSumOfDataValueFromPatientDataValue( patient.getId(), previousExecutionDate, (int) paymentDueConstant.getValue() );
        }
        
        //System.out.println(  " DataElementIds ByComma -- "+ dataElementIdsByComma );
       
        
        //System.out.println( " payment Due Value is --: " + paymentDueValue +  " -- Size of Patient Data Value Map is  : " + patientDataValueMap.size() );
        
        
        /*
        for( Integer key : patientDataValueMap.keySet() )
        {
            System.out.println( " Key is --: " + key +  " -- Value is : " + patientDataValueMap.get( key ) );
                  
        }
        */
        
        
       /*
        for( ProgramStageDataElement programStageDataElement : programStageDataElements )
        {
            System.out.println( programStageDataElement.getDataElement().getId() +  " -- Value is : " + patientDataValueMap.get( programStageDataElement.getDataElement().getId() ) );
                  
        }
        */
        
        
        /*
        else if ( period.equalsIgnoreCase( DFSReport.CURRENT_FORTNIGHT1_START_DATE ) )
        {
            //result = startDate.split( "-" )[0] + "-" + startDate.split( "-" )[1] + "-01";
            Calendar reportDateCal = Calendar.getInstance();
            reportDateCal.setTime( format.parseDate( reportDate ) );
            String tempS = "YYYY-MM-DD";
            for( Calendar cal : weekList )
            {
                System.out.println( reportDateCal.getTime() + " ***** " + cal.getTime() );
                if( reportDateCal.before( cal ) )
                {
                    System.out.println( "Two Weeks from Report Date are : "+reportDateCal.getTime() + " ***** " + cal.getTime() );
                    Calendar tempCal = Calendar.getInstance();
                    tempCal.setTime( cal.getTime() );

                    tempCal.add( Calendar.DATE , -7  );
                    //System.out.print( "W1 " +tempCal.get(  Calendar.YEAR ) + "-" + tempCal.get(  Calendar.MONTH ) + "-" + tempCal.get(  Calendar.DATE ) );
                    tempCal.add( Calendar.DATE , -7  );
                    tempS = standardDateFormat.format( tempCal.getTime() );
                    //System.out.print( " W2 " +tempCal.get(  Calendar.YEAR ) + "-" + tempCal.get(  Calendar.MONTH ) + "-" + tempCal.get(  Calendar.DATE ) );
                    break;
                }
            }
            
            result = tempS;
        }
        */
        
        
        
        return SUCCESS;
    }


    
}

