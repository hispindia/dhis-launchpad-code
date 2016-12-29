package org.hisp.dhis.asha.paymentapprove;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

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
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdataentrylock.PatientDataEntryLock;
import org.hisp.dhis.patientdataentrylock.PatientDataEntryLockService;
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
public class LoadASHAMOApproveDetailsFormAction implements Action
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
    
    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
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
    
    private PatientDataEntryLockService patientDataEntryLockService;
    
    public void setPatientDataEntryLockService( PatientDataEntryLockService patientDataEntryLockService )
    {
        this.patientDataEntryLockService = patientDataEntryLockService;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public I18nFormat getFormat()
    {
        return format;
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

    /*
    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
    
    public String getSelectedPeriodId()
    {
        return selectedPeriodId;
    }
    */
    
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
    
    private Integer totalMOApproveAmount;
    
    public Integer getTotalMOApproveAmount()
    {
        return totalMOApproveAmount;
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
    
    private Map<Date, Map<Integer,String>> pendingPatientDataValueMapByPeriod;
    
    public Map<Date, Map<Integer, String>> getPendingPatientDataValueMapByPeriod()
    {
        return pendingPatientDataValueMapByPeriod;
    }
    
    /*
    private Integer countSaltSample;
    
    public Integer getCountSaltSample()
    {
        return countSaltSample;
    }
    */
    
    private List<Date> pendingPeriodList = new ArrayList<Date>();
    
    public List<Date> getPendingPeriodList()
    {
        return pendingPeriodList;
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
    
    private Map<Date, Map<Integer,String>> pendingActivityDetailsDataValueMapByPeriod;
    
    public Map<Date, Map<Integer, String>> getPendingActivityDetailsDataValueMapByPeriod()
    {
        return pendingActivityDetailsDataValueMapByPeriod;
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
    
    public Map<Integer, String> activityDetailsMORemarkDataValueMap;
    
    public Map<Integer, String> getActivityDetailsMORemarkDataValueMap()
    {
        return activityDetailsMORemarkDataValueMap;
    }
    
    private Map<Date, Map<Integer,String>> pendingPatientMORemarkDataValueMapByPeriod;
    
    public Map<Date, Map<Integer, String>> getPendingPatientMORemarkDataValueMapByPeriod()
    {
        return pendingPatientMORemarkDataValueMapByPeriod;
    }

    public Map<Integer, String> amountDataValueMap;
    
    public Map<Integer, String> getAmountDataValueMap()
    {
        return amountDataValueMap;
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
    
    private String status;
    
    public String getStatus()
    {
        return status;
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
        status = "NONE";
        
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
        
        SimpleDateFormat simpleMonthYearFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        monthYear = simpleMonthYearFormat.format( currentPeriod.getStartDate() );
        
        
        //Date date = new Date();
        //System.out.println("date = " + dateFormat.format(date));
        // get date one months back
        Calendar calendar = Calendar.getInstance();
        
        calendar.setTime( currentPeriod.getStartDate() );
        
        //System.out.println("current calendar time = " + dateFormat.format( calendar.getTime() ));
        
        calendar.add( Calendar.MONTH, -1 );
        
        //System.out.println("calendar one month back = " + dateFormat.format( calendar.getTime() ) );
        
        //for end date
        Calendar endDate = Calendar.getInstance();
        endDate.setTime( currentPeriod.getEndDate() );
        endDate.add( Calendar.MONTH, -1);
        
        
        endDate.set( Calendar.DAY_OF_MONTH, endDate.getActualMaximum( Calendar.DAY_OF_MONTH) );
        
        //System.out.println( periodType.getName() + "_" + dateFormat.format( calendar.getTime() ) + "_" + dateFormat.format( endDate.getTime() ) );
        
        //System.out.println( periodType.getName() + "_" + dateFormat.format( calendar.getTime() ) + "_" + dateFormat.format( endDate.getTime() ) );
        
        Period previousPeriod = periodService.getPeriodByExternalId( periodType.getName() + "_" + dateFormat.format( calendar.getTime() ) + "_" + dateFormat.format( endDate.getTime() ) );
        
        //System.out.println( periodType.getName() + "_" + dateFormat.format( calendar.getTime() ) + "_" + dateFormat.format( endDate.getTime() ) );
        
        //System.out.println(" Current Period Id = " + currentPeriod.getId() + " Perivous Period Id " + previousPeriod.getId() );
        
        /*
        PatientDataEntryLock previousPatientDataEntryLock = patientDataEntryLockService.getPatientDataEntryLock( patient.getOrganisationUnit(), previousPeriod, patient );
        
        PatientDataEntryLock patientDataEntryLock = patientDataEntryLockService.getPatientDataEntryLock( patient.getOrganisationUnit(), currentPeriod, patient );
        
        
        if( patientDataEntryLock == null && previousPatientDataEntryLock == null )
        {
            //System.out.println( " In side DataEntry not done " );
            
            status = i18n.getString( "Data Entry Not Done" );

            return SUCCESS;
        }
        */
        
        
        //System.out.println( "  Latest Period  : " + currentPeriod.getStartDateString() + " Period Id : "+ currentPeriod.getId() + " Period Name : "+ currentPeriod.getName() + " Period External Id : "+ currentPeriod.getExternalId() );
        
        //Period period = periodService.getPeriodByExternalId( selectedPeriodId );
        
     
        //System.out.println( "  patient  : " + patient.getFullName() );
        
        //System.out.println( "  period.getStartDateString()  : " + period.getStartDateString() );
        
        
        dataValueMap = new HashMap<Integer, String>();

        patientDataValueMap = new HashMap<Integer, String>();
        tempPatientDataValueMap = new HashMap<Integer, String>();
        
        //beneficiaryList = new ArrayList<Beneficiary>( beneficiaryService.getAllBeneficiaryByASHAAndPeriod( patient, currentPeriod ) );
        
        beneficiaryList = new ArrayList<Beneficiary>( ashaService.getAllBeneficiaryNotApproveByMOInCurrentMonth( patient, currentPeriod ) );
        
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
                
                //int countryTotal = Integer.parseInt(  beneficiary.getPrice() );
                
                if( beneficiary.getDataElement().getId() == 110 )
                {
                    countryTotal = 0;
                    stateAmount = Integer.parseInt(  beneficiary.getPrice() );
                    stateAmount = (int) ( Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                }
                
                else
                {
                    countryTotal = Integer.parseInt(  beneficiary.getPrice() );
                    double temp = Double.parseDouble( beneficiary.getPrice() ) / 2 ;
                    temp = ( Math.round( temp ) ) ;
                    //stateAmount = Integer.parseInt(  beneficiary.getPrice() ) / 2 ;
                    //stateAmount = (int) ( Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                    stateAmount = (int) ( Math.round( temp * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                }
                
                totalAmountInMonth += countryTotal;
                stateTotalAmountInMonth += stateAmount;
              
                //int countryTotal = Integer.parseInt(  beneficiary.getPrice() );
                
                //totalCountryAmount += Double.parseDouble( beneficiary.getPrice() );
                
                //double stateAmount = Double.parseDouble( beneficiary.getPrice() ) / 2 ;
                
                //int stateAmount = Integer.parseInt(  beneficiary.getPrice() ) / 2 ;
                
                //double d = 4.57767; System.out.println(Math.round(d));
                
                stateAmount = (int) (Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
                
                int total = countryTotal + stateAmount;
                
                stateAmountMap.put( beneficiary.getId(), stateAmount );
                totalAmountMap.put( beneficiary.getId(), total );
                
            }
            
        }
        
        
        
        //pendingBeneficiaryList = new ArrayList<Beneficiary>( beneficiaryService.getAllBeneficiaryStatusPendingByMO( patient, 2 ) );
        
        pendingBeneficiaryList = new ArrayList<Beneficiary>( ashaService.getAllBeneficiaryPendingByMO( patient, 2, currentPeriod.getStartDateString() ) );
        
               
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
                    //stateAmount = Integer.parseInt(  beneficiary.getPrice() ) / 2 ;
                    //stateAmount = (int) ( Math.round( stateAmount * Math.pow( 10, 0 ) )/ Math.pow( 10, 0 ));
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
        
        
        //totalAmountInMonth = ashaService.getTotalAmountInMonthFromBeneficiary( patient.getId(), currentPeriod.getId() ).intValue();
        
        //stateTotalAmountInMonth = null;
        
        //stateTotalAmountInMonth = (int) ( ashaService.getTotalAmountInMonthFromBeneficiary( patient.getId(), currentPeriod.getId())/2 + 200 );     
        
        totalAmount = totalAmountInMonth + stateTotalAmountInMonth;
        
        
        Integer totalMOApproveAmountInMonth = null;
        //totalAmountInMonth = totalCountryAmount.intValue();
        
        
        totalMOApproveAmountInMonth = ashaService.getTotalAmountInMonthFromBeneficiaryApprovedByMO( patient.getId(), currentPeriod.getId() ).intValue();
        
        Integer stateMOApproveTotalAmountInMonth = null;
        
        stateMOApproveTotalAmountInMonth = (int) ( ashaService.getTotalAmountInMonthFromBeneficiaryApprovedByMO( patient.getId(), currentPeriod.getId())/2);     
        
        totalMOApproveAmount = totalMOApproveAmountInMonth + stateMOApproveTotalAmountInMonth;
        
        
        //double d = 4.57767;  System.out.println(Math.round(d)); 
        
        //System.out.println( " Total Amount In Month -- " + totalAmountInMonth  + " State total Amount -- " + stateTotalAmountInMonth );
        

        //Constant amountDataSet = constantService.getConstantByName( ASHA_AMOUNT_DATA_SET );
        
        //Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM );
        //Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM_STAGE );

        //
        /*
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
        */

        
        // Data set Information
        /*
        dataSet = dataSetService.getDataSet( (int) amountDataSet.getValue() );

        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );

        organisationUnit = dataSetSource.get( 0 );
        */
        
        
        // PeriodType periodType = dataSet.getPeriodType();
        
        /*
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
        
        */
        
        // program and programStage Related information
        /*
        program = programService.getProgram( (int) programConstant.getValue() );

        programStage = programStageService.getProgramStage( (int) programStageConstant.getValue() );

        programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        */
        
        // DataElements
        
        //dataElementMap = new HashMap<Integer, DataElement>();

        // List<DataElement> dataElements = new ArrayList<DataElement>(
        // dataElementService.getDataElementsByDomainType(
        // DataElement.DOMAIN_TYPE_PATIENT ) );
        
        /*
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
                currentPeriod.getStartDateString() );
        }

        if ( programStageInstanceId == null )
        {
            ProgramStageInstance programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );
            programStageInstance.setExecutionDate( format.parseDate( currentPeriod.getStartDateString() ) );

            programStageInstance.setDueDate( format.parseDate( currentPeriod.getStartDateString() ) );

            programStageInstanceId = programStageInstanceService.addProgramStageInstance( programStageInstance );
        }
        */
        
        
        
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
                int count = beneficiaryService.getCountByServicePeriodAndASHA( patient, currentPeriod,
                    programStageDataElement.getDataElement() );

                patientDataValueMap.put( programStageDataElement.getDataElement().getId(), "" + count );

                //System.out.println( " DataElement  : " + programStageDataElement.getDataElement().getName() + "-- Count " + count );
            }
        }
        */
        
        //tempPatientDataValueMap = new HashMap<Integer, String>();
        //tempPatientDataValueMap = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValue( programStageInstanceId ) );
        
        //patientDataValueMap = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValue( programStageInstanceId ) );
        
        // ASHA ACTIVITY Details Program
        
        activityDetailsDataValueMap = new HashMap<Integer, String>();
        
        activityDetailsMORemarkDataValueMap = new HashMap<Integer, String>();
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
            
            //activityDetailsDataValueMap = ashaService.getASHAActivityDataValueFromPatientDataValue( activityDetailsProgramStageInstanceId );
            //activityDetailsMORemarkDataValueMap = ashaService.getASHAActivityMORemarkDataValueFromPatientDataValue( activityDetailsProgramStageInstanceId );
        
            activityDetailsMORemarkDataValueMap = ashaService.getASHAActivityMORemarkDataValueFromPatientDataValueNotApproveByMOInCurrentMonth( activityDetailsProgramStageInstanceId );
            
            
            
            activityDetailsDataValueMap = ashaService.getASHAActivityDataValueFromPatientDataValueNotApproveByMoInCurrentMonth( activityDetailsProgramStageInstanceId );
            
            //activityDetailsDataValueMap = ashaService.getASHAActivityMORemarkDataValueFromPatientDataValueNotApproveByMOInCurrentMonth( activityDetailsProgramStageInstanceId );
            
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
        
        tempActivityDetailsDataValueMap  = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValue( activityDetailsProgramStageInstanceId, dataElementIdsByComma ) );
        
        
        //countSaltSample = Integer.parseInt( activityDetailsDataValueMap.get( 428 ) ) ;
        
        // pending details
        
        
        
        //pendingPatientDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>( ashaService.getMOPendingPatientDataValues( patient.getId(), activityDetailsprogram.getId(), activityDetailsProgramStage.getId(), 2, currentPeriod.getStartDateString()  ) );
        
        pendingPatientDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>( ashaService.getMONotApprovePatientDataValues( patient.getId(), activityDetailsprogram.getId(), activityDetailsProgramStage.getId(), currentPeriod.getStartDateString()  ) );
        
        pendingActivityDetailsDataValueMapByPeriod =  new TreeMap<Date, Map<Integer,String>>( ashaService.getMOPendingPatientDataValues( patient.getId(), activityDetailsprogram.getId(), activityDetailsProgramStage.getId(), 0, currentPeriod.getStartDateString() ) );
        
        
        pendingPatientMORemarkDataValueMapByPeriod = new TreeMap<Date, Map<Integer,String>>( ashaService.getMOPendingRemarkPatientDataValues( patient.getId(), activityDetailsprogram.getId(), activityDetailsProgramStage.getId(), 2, currentPeriod.getStartDateString()  ) );
        
        
        
        pendingPeriodList = new ArrayList<Date>( pendingPatientDataValueMapByPeriod.keySet() );
        
        Collections.reverse( pendingPeriodList );
        
        /*
        for ( Date period : pendingPeriodList )
        {
            System.out.println( " Period  : " + period  );
            
            
            Map<Integer, String> deValueMap = new HashMap<Integer, String>( pendingActivityDetailsDataValueMapByPeriod.get( period ) );
            
            for ( Integer deId : deValueMap.keySet() )
            {
                System.out.println( " De Id  : " + deId  +  " De Value  : " + deValueMap.get( deId ));
            }
        }
        */
        //<!--<td>$!simpleDateFormat.format( format.parseDate( $activityDetailsDataValueMap.get( 418 ).split(":")[0] ) )</td>-->
        return SUCCESS;
    }

}

