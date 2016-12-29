package org.hisp.dhis.asha.beneficiaries;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.reports.ReportService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadASHABeneficiaryAndActivityAmountDetailsFormAction implements Action
{
    public static final String ASHA_AMOUNT_DATA_SET = "Amount"; // 2.0

    //public static final String ASHA_ACTIVITY_PROGRAM = "ASHA Activity Program";// 1.0

    //public static final String ASHA_ACTIVITY_PROGRAM_STAGE = "ASHA Activity Program Stage";// 1.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_GROUP_ID = "ASHA_ACTIVITY_DETAILS_GROUP_ID";//12.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BeneficiaryService beneficiaryService;

    public void setBeneficiaryService( BeneficiaryService beneficiaryService )
    {
        this.beneficiaryService = beneficiaryService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
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

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
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
    
    public String getSelectedPeriodId()
    {
        return selectedPeriodId;
    }
    
    private int organisationUnitId;
    
    public int getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private Collection<Beneficiary> beneficiaryList = new ArrayList<Beneficiary>();

    public Collection<Beneficiary> getBeneficiaryList()
    {
        return beneficiaryList;
    }

    private String update;
    
    public String getUpdate()
    {
        return update;
    }
    
    private Integer totalAmountInMonth;
    
    public Integer getTotalAmountInMonth()
    {
        return totalAmountInMonth;
    }
    
    public Map<Integer, Integer> stateAmountMap;
    
    public Map<Integer, Integer> getStateAmountMap()
    {
        return stateAmountMap;
    }
    
    private Integer stateTotalAmountInMonth;
    
    public Integer getStateTotalAmountInMonth()
    {
        return stateTotalAmountInMonth;
    }
   
    public Map<Integer, Integer> totalAmountMap;
    
    public Map<Integer, Integer> getTotalAmountMap()
    {
        return totalAmountMap;
    }
    
    private Integer totalAmount;
    
    public Integer getTotalAmount()
    {
        return totalAmount;
    }
    
    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }
    
    private String monthYear;
    
    public String getMonthYear()
    {
        return monthYear;
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

    public Map<Integer, String> dataValueMap;

    public Map<Integer, String> getDataValueMap()
    {
        return dataValueMap;
    }
    
    public Map<Integer, String> patientDataValueMap;

    public Map<Integer, String> getPatientDataValueMap()
    {
        return patientDataValueMap;
    }

    public Map<Integer, String> tempPatientDataValueMap;

    public Map<Integer, String> getTempPatientDataValueMap()
    {
        return tempPatientDataValueMap;
    }
    
    public Map<Integer, String> activityDetailsDataValueMap;
    
    public Map<Integer, String> getActivityDetailsDataValueMap()
    {
        return activityDetailsDataValueMap;
    }

    public Map<Integer, String> tempActivityDetailsDataValueMap;
    
    public Map<Integer, String> getTempActivityDetailsDataValueMap()
    {
        return tempActivityDetailsDataValueMap;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // --------- ----------------------------------------------------------------

    public String execute() throws Exception
    {
        patient = patientService.getPatient( id );
        
        //Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        Period period = periodService.getPeriodByExternalId( selectedPeriodId );
        
        //System.out.println( "  patient  : " + patient.getFullName() );
        
        //System.out.println( "  period.getStartDateString()  : " + period.getStartDateString() );
        
        
        dataValueMap = new HashMap<Integer, String>();

        patientDataValueMap = new HashMap<Integer, String>();
        tempPatientDataValueMap = new HashMap<Integer, String>();
        
        beneficiaryList = new ArrayList<Beneficiary>( beneficiaryService.getAllBeneficiaryByASHAAndPeriod( patient, period ) );
        
        //Double totalCountryAmount = null;
        
        stateAmountMap = new HashMap<Integer, Integer>();
        totalAmountMap = new HashMap<Integer, Integer>();
        
        totalAmountInMonth = 0;
        stateTotalAmountInMonth = 0;
        totalAmount = 0;
        
        for ( Beneficiary beneficiary : beneficiaryList )
        {
            if ( beneficiary.getPrice() != null )
            {   
                int countryTotal = 0;
                int stateAmount = 0;
                
                //int countryTotal = Integer.parseInt(  beneficiary.getPrice() );
                
                if( beneficiary.getDataElement().getId() == 110 )
                {
                    countryTotal = 0;
                    stateAmount = Integer.parseInt(  beneficiary.getPrice() );
                    stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));                    
                }
                
                else
                {
                    /*
                    countryTotal = Integer.parseInt(  beneficiary.getPrice() );
                    stateAmount = Integer.parseInt(  beneficiary.getPrice() ) / 2 ;
                    stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                    */
                    
                    countryTotal = Integer.parseInt(  beneficiary.getPrice() );
                    double temp = Double.parseDouble( beneficiary.getPrice() ) / 2 ;
                    temp = ( Math.round( temp ) ) ;
                    //stateAmount = Integer.parseInt(  beneficiary.getPrice() ) / 2 ;
                    //stateAmount = (int) ( Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                    stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                    
                    
                    
                }
                
                totalAmountInMonth += countryTotal;
                stateTotalAmountInMonth += stateAmount;
                
                
                //totalCountryAmount += Double.parseDouble( beneficiary.getPrice() );
                
                //double stateAmount = Double.parseDouble( beneficiary.getPrice() ) / 2 ;
                
                //int stateAmount = Integer.parseInt(  beneficiary.getPrice() ) / 2 ;
                
                //double d = 4.57767; System.out.println(Math.round(d));
                //stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
               
                int total = countryTotal + stateAmount;
                
                stateAmountMap.put( beneficiary.getId(), stateAmount );
                totalAmountMap.put( beneficiary.getId(), total );
                
                //beneficiary.getPatient().getOrganisationUnit().getId();
                
            }
        }
        
        totalAmount = totalAmountInMonth + stateTotalAmountInMonth;
        
        Calendar current = Calendar.getInstance();
        
        current.setTime( new Date() );
        
        current.get( Calendar.MONTH );
        
        Calendar pre = Calendar.getInstance();
        
        pre.setTime( period.getStartDate() );
        
        pre.get( Calendar.MONTH );
        
        if( current.get( Calendar.MONTH ) == pre.get( Calendar.MONTH ) )
        {
            update = "YES";
            
            //System.out.println( " Current  : " + current.get( Calendar.MONTH ) + "-- Pre " + pre.get( Calendar.MONTH ) + "-- update " + update );
        }
        else
        {
            update = "NO";
            //System.out.println( " Current  : " + current.get( Calendar.MONTH ) + "-- Pre " + pre.get( Calendar.MONTH ) + "-- update " + update );
            
        }
        
        SimpleDateFormat simpleMonthYearFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        monthYear = simpleMonthYearFormat.format( period.getStartDate() );
        
        
        //totalAmountInMonth = null;
        //totalAmountInMonth = totalCountryAmount.intValue();
        
        
        //totalAmountInMonth = ashaService.getTotalAmountInMonthFromBeneficiary( patient.getId(), period.getId() ).intValue();
        
        //stateTotalAmountInMonth = null;
        
        //stateTotalAmountInMonth = (int) ( ashaService.getTotalAmountInMonthFromBeneficiary( patient.getId(), period.getId())/2  + 200 );     
        
        //totalAmount = totalAmountInMonth + stateTotalAmountInMonth;
        
        
        //double d = 4.57767;  System.out.println(Math.round(d)); 
        
        //System.out.println( " Total Amount In Month -- " + totalAmountInMonth  + " State total Amount -- " + stateTotalAmountInMonth );
        

        Constant amountDataSet = constantService.getConstantByName( ASHA_AMOUNT_DATA_SET );
        
        //Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM );
        //Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM_STAGE );

        
        
        //
        
        Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_ID );
        Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID );
        
        program = programService.getProgram( (int) programConstant.getValue() );

        Integer programInstanceId = ashaService.getProgramInstanceId( patient.getId(), program.getId() );

        if ( programInstanceId == null )
        {
            Patient createdPatient = patientService.getPatient( patient.getId() );

            Date programEnrollDate = new Date();

            int programType = program.getType();
            ProgramInstance programInstance = null;

            if ( programType == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
            {
                programInstance = new ProgramInstance();
                programInstance.setEnrollmentDate( programEnrollDate );
                programInstance.setDateOfIncident( programEnrollDate );
                programInstance.setProgram( program );
                programInstance.setCompleted( false );

                programInstance.setPatient( createdPatient );
                createdPatient.getPrograms().add( program );
                patientService.updatePatient( createdPatient );

                programInstanceId = programInstanceService.addProgramInstance( programInstance );

            }
        }
       

        // Data set Information

        dataSet = dataSetService.getDataSet( (int) amountDataSet.getValue() );

        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );

        organisationUnit = dataSetSource.get( 0 );

        // PeriodType periodType = dataSet.getPeriodType();

        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );

        for ( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                .getDefaultDataElementCategoryOptionCombo();

            DataValue dataValue = new DataValue();

            dataValue = reportService.getLatestDataValue( dataElement, optionCombo, organisationUnit );

            String value = "";

            if ( dataValue != null )
            {
                value = dataValue.getValue();

                dataValueMap.put( dataElement.getId(), value );
            }

        }

        // program and programStage Related information
        program = programService.getProgram( (int) programConstant.getValue() );

        programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );

        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );

        // DataElements
        dataElementMap = new HashMap<Integer, DataElement>();

        // List<DataElement> dataElements = new ArrayList<DataElement>(
        // dataElementService.getDataElementsByDomainType(
        // DataElement.DOMAIN_TYPE_PATIENT ) );

        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                dataElementMap.put( programStageDataElement.getDataElement().getId(),
                    programStageDataElement.getDataElement() );
            }
        }

        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );

        if ( programInstanceId != null )
        {
            programStageInstanceId = ashaService.getProgramStageInstanceId( programInstanceId, programStage.getId(),
                period.getStartDateString() );
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

        
        Constant ashaActivityDetailsDataElementGroupConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_GROUP_ID );
        
        DataElementGroup ashaActivityDetailsDataElementGroup = dataElementService.getDataElementGroup( (int) ashaActivityDetailsDataElementGroupConstant.getValue() );
        
        List<DataElement> ashaActivityDetailsDataElements = new ArrayList<DataElement>( ashaActivityDetailsDataElementGroup.getMembers() );
        
        String dataElementIdsByComma = "-1";
        if( ashaActivityDetailsDataElements != null && ashaActivityDetailsDataElements.size() > 0 )
        {
            Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers( DataElement.class, ashaActivityDetailsDataElements ) );
            dataElementIdsByComma = getCommaDelimitedString( dataElementIds );
        }
        
        
        
        activityDetailsDataValueMap = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValue( programStageInstanceId ) );
        
        tempActivityDetailsDataValueMap  = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValue( programStageInstanceId, dataElementIdsByComma ) );
        
        
        /*
         * for( DataElement dataElement : dataElementList ) {
         * System.out.println( dataElement.getId() + " -- "+
         * dataElement.getName() + " -- Value is : " + dataValueMap.get(
         * dataElement.getId() ) );
         * 
         * }
         */

        /*
        patientDataValueMap = new HashMap<Integer, String>();

        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                int count = beneficiaryService.getCountByServicePeriodAndASHA( patient, period,
                    programStageDataElement.getDataElement() );

                patientDataValueMap.put( programStageDataElement.getDataElement().getId(), "" + count );

                // System.out.println( " DataElement  : " + programStageDataElement.getDataElement().getName() + "-- Count " + count );
            }
        }
        
        tempPatientDataValueMap = new HashMap<Integer, String>();
        tempPatientDataValueMap = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValue( programStageInstanceId ) );
        */
        
        
        // ACTIVITY Details 
        
        
        /*
        activityDetailsDataValueMap = new HashMap<Integer, String>();
        
        Constant ashaActivityDetailsrogramConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_ID );
        
        Program activityDetailsprogram = programService.getProgram( (int) ashaActivityDetailsrogramConstant.getValue() );
        
        Integer activityDetailsprogramInstanceId = ashaService.getProgramInstanceId( patient.getId(), activityDetailsprogram.getId() );
        
        if( activityDetailsprogramInstanceId == null )
        {
            Patient createdPatient = patientService.getPatient( patient.getId() );
            
            Date programEnrollDate = new Date();
            
            int programType = activityDetailsprogram.getType();
            ProgramInstance activityDetailsProgramInstance = null;
            
            if ( programType == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
            {
                activityDetailsProgramInstance = new ProgramInstance();
                activityDetailsProgramInstance.setEnrollmentDate(  programEnrollDate  );
                activityDetailsProgramInstance.setDateOfIncident(  programEnrollDate  );
                activityDetailsProgramInstance.setProgram( activityDetailsprogram );
                activityDetailsProgramInstance.setCompleted( false );

                activityDetailsProgramInstance.setPatient( createdPatient );
                createdPatient.getPrograms().add( activityDetailsprogram );
                patientService.updatePatient( createdPatient );

                activityDetailsprogramInstanceId = programInstanceService.addProgramInstance( activityDetailsProgramInstance );
                
            }
        }
        
        Constant ashaActivityDetailsProgramStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID );
        
        ProgramStage activityDetailsProgramStage = programStageService.getProgramStage( (int) ashaActivityDetailsProgramStageConstant.getValue() );
        
        
        if ( activityDetailsprogramInstanceId != null )
        {
            Integer activityDetailsProgramStageInstanceId = ashaService.getProgramStageInstanceId( activityDetailsprogramInstanceId, activityDetailsProgramStage.getId(), period.getStartDateString() );
            
            activityDetailsDataValueMap = ashaService.getDataValueFromPatientDataValue( activityDetailsProgramStageInstanceId );
        }
        */
        
        
        
        return SUCCESS;
    }

}

