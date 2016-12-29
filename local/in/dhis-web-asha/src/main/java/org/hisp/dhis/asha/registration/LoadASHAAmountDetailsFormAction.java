package org.hisp.dhis.asha.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.beneficiary.BeneficiaryService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.i18n.I18nFormat;
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
import org.hisp.dhis.reports.ReportService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class LoadASHAAmountDetailsFormAction implements Action
{
    
    public static final String ASHA_AMOUNT_DATA_SET = "Amount"; // 2.0
    public static final String ASHA_ACTIVITY_PROGRAM = "ASHA Activity Program";//1.0
    public static final String ASHA_ACTIVITY_PROGRAM_STAGE = "ASHA Activity Program Stage";//1.0
    
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
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private BeneficiaryService beneficiaryService;

    public void setBeneficiaryService( BeneficiaryService beneficiaryService )
    {
        this.beneficiaryService = beneficiaryService;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private String ashaId;
    
    
    
    public void setAshaId(String ashaId) {
		this.ashaId = ashaId;
	}

	private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }        
    
    /*
    private Integer programInstanceId;
    
    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }
    */
    
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
    
    public Map<Integer, String> dataValueMap;
    
    public Map<Integer, String> getDataValueMap()
    {
        return dataValueMap;
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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute()
    {
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        //System.out.println( period.getStartDateString() + " -- "+ period.getEndDate().toString() );
        
        //period.getStartDate();
        
        dataValueMap = new HashMap<Integer, String>();
        
        patientDataValueMap = new HashMap<Integer, String>();
        
        //System.out.println("ashaid="+ashaId + " selPeriod=" + selectedPeriodId + "PService=" + patientService);
       
        patient = patientService.getPatient( Integer.parseInt(ashaId ));
        
        //System.out.println(  patient.getOrganisationUnit().getName() );
        
       
        Constant amountDataSet = constantService.getConstantByName( ASHA_AMOUNT_DATA_SET );
        Constant programConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM );
        Constant programStageConstant = constantService.getConstantByName( ASHA_ACTIVITY_PROGRAM_STAGE );
        
        //
        program = programService.getProgram( (int) programConstant.getValue() );
        
       Integer programInstanceId = ashaService.getProgramInstanceId( patient.getId(), program.getId() );
        
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
        //
        
        // Data set  Information
        
        dataSet = dataSetService.getDataSet( (int) amountDataSet.getValue() );
        
        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );
        
        organisationUnit = dataSetSource.get( 0 );
        
        //PeriodType periodType = dataSet.getPeriodType();
        
        List<DataElement> dataElementList  = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        for( DataElement dataElement : dataElementList )
        {
            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
            
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
        
        programStageDataElements =  new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        
        // DataElements
        dataElementMap = new HashMap<Integer, DataElement>();
        
        //List<DataElement> dataElements =  new ArrayList<DataElement>( dataElementService.getDataElementsByDomainType( DataElement.DOMAIN_TYPE_PATIENT ) );
        
        if( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                dataElementMap.put( programStageDataElement.getDataElement().getId(), programStageDataElement.getDataElement() );
            }
        }
        
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
        
        /*
        for( DataElement dataElement : dataElementList )
        {
            System.out.println( dataElement.getId() + " -- "+ dataElement.getName()  + " -- Value is : " + dataValueMap.get( dataElement.getId() ) );
                  
        }
        */
        
        patientDataValueMap = new HashMap<Integer, String>();
  
        if( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            for( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                int count = beneficiaryService.getCountByServicePeriodAndASHA( patient, period, programStageDataElement.getDataElement() );
                
                patientDataValueMap.put( programStageDataElement.getDataElement().getId() , ""+count);
                
                //System.out.println( " DataElement  : " +  programStageDataElement.getDataElement().getName() + "-- Count " + count );
            }
        }
        
        /*
        for( DataElement de : dataElementList )
        {
            int count = beneficiaryService.getCountByServicePeriodAndASHA( patient, period, de );
            patientDataValueMap.put( de.getId() , ""+count);
            //System.out.println( " De  : " + de.getName() + "-- count " + count );
        }
        */
        
        return SUCCESS;
    }
    
}
