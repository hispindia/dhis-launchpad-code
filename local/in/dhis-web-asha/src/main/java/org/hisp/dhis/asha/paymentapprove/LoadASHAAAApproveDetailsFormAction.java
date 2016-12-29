package org.hisp.dhis.asha.paymentapprove;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
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
public class LoadASHAAAApproveDetailsFormAction implements Action
{
    //public static final String ASHA_AMOUNT_DATA_SET = "Amount"; // 2.0

    //public static final String ASHA_ACTIVITY_PROGRAM = "ASHA Activity Program";// 1.0

    //public static final String ASHA_ACTIVITY_PROGRAM_STAGE = "ASHA Activity Program Stage";// 1.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID = "ASHA_ACTIVITY_DETAILS_PROGRAM_STAGE_ID";//4.0
    
    public static final String ASHA_ACTIVITY_DETAILS_GROUP_ID = "ASHA_ACTIVITY_DETAILS_GROUP_ID";//12.0
    
    public static final String VILLAGE_ATTRIBUTE_ID = "Village Attribute";//6.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    /*
    private BeneficiaryService beneficiaryService;

    public void setBeneficiaryService( BeneficiaryService beneficiaryService )
    {
        this.beneficiaryService = beneficiaryService;
    }
    */
    
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
    
    /*
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    */
    
    /*
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
    
    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
     
    */
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
    
    /*
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
    */
    
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

    
    private SimpleDateFormat monthFormat = new SimpleDateFormat();
    
    public SimpleDateFormat getMonthFormat()
    {
        return monthFormat;
    }
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat();
    
    public SimpleDateFormat getDateFormat()
    {
        return dateFormat;
    }    
    
    private Period currentPeriod;
    
    public Period getCurrentPeriod()
    {
        return currentPeriod;
    }    
    
    private Collection<Beneficiary> pendingBeneficiaryList = new ArrayList<Beneficiary>();
    
    public Collection<Beneficiary> getPendingBeneficiaryList()
    {
        return pendingBeneficiaryList;
    }
    
    private Map<Integer, Integer> totalPendingAmountMap;
    
    public Map<Integer, Integer> getTotalPendingAmountMap()
    {
        return totalPendingAmountMap;
    }
    
    private Map<Integer, String> pendingPeriodNameMap;
    
    public Map<Integer, String> getPendingPeriodNameMap()
    {
        return pendingPeriodNameMap;
    }

    private Integer totalPendingAmount;
    
    public Integer getTotalPendingAmount()
    {
        return totalPendingAmount;
    }
    
    private Integer activityDetailsprogramInstanceId;
    
    public Integer getActivityDetailsprogramInstanceId()
    {
        return activityDetailsprogramInstanceId;
    }

    private Integer activityDetailsProgramStageInstanceId;
    
    public Integer getActivityDetailsProgramStageInstanceId()
    {
        return activityDetailsProgramStageInstanceId;
    }
    
    public Map<Integer, String> activityDetailsAARemarkDataValueMap;
    
    public Map<Integer, String> getActivityDetailsAARemarkDataValueMap()
    {
        return activityDetailsAARemarkDataValueMap;
    }
    
    public Map<Integer, String> amountDataValueMap;
    
    public Map<Integer, String> getAmountDataValueMap()
    {
        return amountDataValueMap;
    }
    
    private Map<Date, Map<Integer,String>> pendingPatientDataValueMapByPeriod;
    
    public Map<Date, Map<Integer, String>> getPendingPatientDataValueMapByPeriod()
    {
        return pendingPatientDataValueMapByPeriod;
    }
    
    private Map<Date, Map<Integer,String>> pendingActivityDetailsDataValueMapByPeriod;
    
    public Map<Date, Map<Integer, String>> getPendingActivityDetailsDataValueMapByPeriod()
    {
        return pendingActivityDetailsDataValueMapByPeriod;
    }
    
    private Map<Date, Map<Integer,String>> pendingPatientAARemarkDataValueMapByPeriod;
    
    public Map<Date, Map<Integer, String>> getPendingPatientAARemarkDataValueMapByPeriod()
    {
        return pendingPatientAARemarkDataValueMapByPeriod;
    }
    
    private List<Date> pendingPeriodList = new ArrayList<Date>();
    
    public List<Date> getPendingPeriodList()
    {
        return pendingPeriodList;
    }
    
    public Map<Integer, String> tempActivityDetailsDataValueMap;
    
    public Map<Integer, String> getTempActivityDetailsDataValueMap()
    {
        return tempActivityDetailsDataValueMap;
    }
    
    private SimpleDateFormat simpleDateFormat;
    
    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }
    
    private String village = "";
    
    public String getVillage()
    {
        return village;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // --------- ----------------------------------------------------------------

    public String execute() throws Exception
    {
        patient = patientService.getPatient( id );
        
        String periodTypeName = MonthlyPeriodType.NAME;
        
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );
        
        List<Period> periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );  

        if( periods.size() == 0 )
        {
            CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );
            
            Calendar cal = PeriodType.createCalendarInstance();
            
            periods = _periodType.generatePeriods( cal.getTime() );
        }
        
        Collections.reverse( periods );

        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }
        
        Iterator<Period> periodIterator = periods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
            
        }
        
        
        // village attribute value
        Constant villageAttributeConstant = constantService.getConstantByName( VILLAGE_ATTRIBUTE_ID );
        
        Integer villageAttributeId =  (int) villageAttributeConstant.getValue() ;
        
        village = ashaService.getPatientAttributeValue( patient.getId(), villageAttributeId );
        
        
        
        monthFormat = new SimpleDateFormat("MMM-yyyy");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        
        currentPeriod = periods.get( 0 );
        
       
        dataValueMap = new HashMap<Integer, String>();

        patientDataValueMap = new HashMap<Integer, String>();
        tempPatientDataValueMap = new HashMap<Integer, String>();
        
        //beneficiaryList = new ArrayList<Beneficiary>( beneficiaryService.getAllBeneficiaryByASHAAndPeriod( patient, period ) );
        
        //beneficiaryList = new ArrayList<Beneficiary>( beneficiaryService.getAllBeneficiaryStatusByMO( patient, currentPeriod, 3 ) );
        
        beneficiaryList = new ArrayList<Beneficiary>( ashaService.getAllBeneficiaryPendingByAAApproveByMOCurrentMonth( patient, currentPeriod ) );
        
        //Double totalCountryAmount = null;
        
        stateAmountMap = new HashMap<Integer, Integer>();
        totalAmountMap = new HashMap<Integer, Integer>();
        
        totalAmountInMonth = 0;
        stateTotalAmountInMonth = 0;
        
        for ( Beneficiary beneficiary : beneficiaryList )
        {
            if ( beneficiary.getPrice() != null )
            {   
                
                int countryTotal = 0;
                int stateAmount = 0;
                
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
                   
                    stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                }
                
                totalAmountInMonth += countryTotal;
                stateTotalAmountInMonth += stateAmount;
              
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
                int total = countryTotal + stateAmount;
                
                stateAmountMap.put( beneficiary.getId(), stateAmount );
                totalAmountMap.put( beneficiary.getId(), total );
                
            }
        }
        

        SimpleDateFormat simpleMonthYearFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        monthYear = simpleMonthYearFormat.format( currentPeriod.getStartDate() );
        
        pendingBeneficiaryList = new ArrayList<Beneficiary>( ashaService.getAllBeneficiaryPendingByAAApproveByMO( patient,currentPeriod.getStartDateString() ) );
        
        totalPendingAmountMap =  new HashMap<Integer, Integer>();
        
        pendingPeriodNameMap = new HashMap<Integer, String>();
        
        totalPendingAmount = 0;
        
        for ( Beneficiary pendingBeneficiary : pendingBeneficiaryList )
        {
            if ( pendingBeneficiary.getPrice() != null )
            {   
                //int countryPendingTotal = Integer.parseInt(  pendingBeneficiary.getPrice() );
                
                //int statePendingAmount = Integer.parseInt(  pendingBeneficiary.getPrice() ) / 2 ;
                
                //statePendingAmount = (int) (Math.round( statePendingAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
                int countryPendingTotal = 0;
                int statePendingAmount = 0;
                
                //int countryTotal = Integer.parseInt(  beneficiary.getPrice() );
                
                if( pendingBeneficiary.getDataElement().getId() == 110 )
                {
                    countryPendingTotal = 0;
                    statePendingAmount = Integer.parseInt(  pendingBeneficiary.getPrice() );
                    statePendingAmount = (int) (Math.round( statePendingAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                }
                
                else
                {
                    /*
                    countryPendingTotal = Integer.parseInt(  pendingBeneficiary.getPrice() );
                    statePendingAmount = Integer.parseInt(  pendingBeneficiary.getPrice() ) / 2 ;
                    statePendingAmount = (int) (Math.round( statePendingAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                    */
                    
                    countryPendingTotal = Integer.parseInt(  pendingBeneficiary.getPrice() );
                    double temp = Double.parseDouble( pendingBeneficiary.getPrice() ) / 2 ;
                    temp = ( Math.round( temp ) ) ;
                   
                    statePendingAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                }
                
                int totalPending = countryPendingTotal + statePendingAmount;
                
                totalPendingAmount = totalPendingAmount + totalPending;
                
                totalPendingAmountMap.put( pendingBeneficiary.getId(), totalPending );
                
                pendingPeriodNameMap.put( pendingBeneficiary.getId(), simpleMonthYearFormat.format( pendingBeneficiary.getPeriod().getStartDate() ) );
                
            }
        }
        
 
        //totalAmountInMonth = null;
        //totalAmountInMonth = totalCountryAmount.intValue();
        
        
        //totalAmountInMonth = ashaService.getTotalAmountInMonthFromBeneficiaryApprovedByMO( patient.getId(), period.getId() ).intValue();
        
        //stateTotalAmountInMonth = null;
        
        //stateTotalAmountInMonth = (int) ( ashaService.getTotalAmountInMonthFromBeneficiaryApprovedByMO( patient.getId(), period.getId())/2);     
        
        //totalAmount = totalAmountInMonth + stateTotalAmountInMonth;
        
        
        //double d = 4.57767;  System.out.println(Math.round(d)); 
        
        //System.out.println( " Total Amount In Month -- " + totalAmountInMonth  + " State total Amount -- " + stateTotalAmountInMonth );
        

        //Constant amountDataSet = constantService.getConstantByName( ASHA_AMOUNT_DATA_SET );
        //Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM );
        //Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM_STAGE );

        //
        
        
        activityDetailsDataValueMap = new HashMap<Integer, String>();
        
        activityDetailsAARemarkDataValueMap = new HashMap<Integer, String>();
        
        pendingPatientAARemarkDataValueMapByPeriod = new HashMap<Date, Map<Integer,String>>();
        
        amountDataValueMap = new HashMap<Integer, String>();
        
        Constant ashaActivityDetailsrogramConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_PROGRAM_ID );
        
        Program activityDetailsprogram = programService.getProgram( (int) ashaActivityDetailsrogramConstant.getValue() );
        
        activityDetailsprogramInstanceId = ashaService.getProgramInstanceId( patient.getId(), activityDetailsprogram.getId() );
        
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
            activityDetailsProgramStageInstanceId = ashaService.getProgramStageInstanceId( activityDetailsprogramInstanceId, activityDetailsProgramStage.getId(), currentPeriod.getStartDateString() );
            
            ProgramInstance activityDetailsProgramInstance = programInstanceService.getProgramInstance( activityDetailsprogramInstanceId );
            
            if ( activityDetailsProgramStageInstanceId == null )
            {
                ProgramStageInstance tempProgramStageInstance = new ProgramStageInstance();
                tempProgramStageInstance.setProgramInstance( activityDetailsProgramInstance );
                tempProgramStageInstance.setProgramStage( activityDetailsProgramStage );
                tempProgramStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );
                
                tempProgramStageInstance.setExecutionDate( format.parseDate( currentPeriod.getStartDateString() ) );
                tempProgramStageInstance.setDueDate( format.parseDate( currentPeriod.getStartDateString() ) );

                activityDetailsProgramStageInstanceId = programStageInstanceService.addProgramStageInstance( tempProgramStageInstance );
                
            }
            
            
            //activityDetailsDataValueMap = ashaService.getDataValueFromPatientDataValue( activityDetailsProgramStageInstanceId );
            
            amountDataValueMap =  ashaService.getDataValueFromPatientDataValue( activityDetailsProgramStageInstanceId );
            
            activityDetailsDataValueMap = ashaService.getASHAActivityDataValueFromPatientDataValue( activityDetailsProgramStageInstanceId );
            activityDetailsAARemarkDataValueMap = ashaService.getASHAActivityAARemarkDataValueFromPatientDataValue( activityDetailsProgramStageInstanceId );
        }
        
        
        /*
        Constant ashaActivityDetailsDataElementGroupConstant = constantService.getConstantByName( ASHA_ACTIVITY_DETAILS_GROUP_ID );
        
        DataElementGroup ashaActivityDetailsDataElementGroup = dataElementService.getDataElementGroup( (int) ashaActivityDetailsDataElementGroupConstant.getValue() );
        
        List<DataElement> ashaActivityDetailsDataElements = new ArrayList<DataElement>( ashaActivityDetailsDataElementGroup.getMembers() );
        
        
        String dataElementIdsByComma = "-1";
        if( ashaActivityDetailsDataElements != null && ashaActivityDetailsDataElements.size() > 0 )
        {
            Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers( DataElement.class, ashaActivityDetailsDataElements ) );
            dataElementIdsByComma = getCommaDelimitedString( dataElementIds );
        }
        
        tempActivityDetailsDataValueMap = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValue( programStageInstanceId, dataElementIdsByComma ) );
        */
        
        
        
        // pending details
        
        pendingPatientDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>( ashaService.getMOApprovePendingPatientDataValues( patient.getId(), activityDetailsprogram.getId(), activityDetailsProgramStage.getId(), currentPeriod.getStartDateString()  ) );
        
        pendingActivityDetailsDataValueMapByPeriod =  new TreeMap<Date, Map<Integer,String>>( ashaService.getMOPendingPatientDataValues( patient.getId(), activityDetailsprogram.getId(), activityDetailsProgramStage.getId(), 0, currentPeriod.getStartDateString() ) );
        
        
        pendingPatientAARemarkDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>( ashaService.getAAPendingRemarkPatientDataValues( patient.getId(), activityDetailsprogram.getId(), activityDetailsProgramStage.getId(), currentPeriod.getStartDateString()  ) );
        
        
        pendingPeriodList = new ArrayList<Date>( pendingPatientDataValueMapByPeriod.keySet() );
        
        Collections.reverse( pendingPeriodList );
        
        
        return SUCCESS;
    }

}
