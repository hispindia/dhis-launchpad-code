package org.hisp.dhis.asha.payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveASHAMonthlyPaymentDetailsAction implements Action
{
   
    
    private final String OPTION_SET_MONTHLY_PAYMENT_DATAELEMENT = "Monthly Payment DataElement";
    
    public static final String MODE_OF_PAYMENT_DATAELEMENT_ID = "Mode of Payment Dataelement Id";//174.0
    
    public static final String PREFIX_DATAELEMENT = "deps";
    
    public static final String PREFIX_CHECKBOX = "checkbox";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) 
    {
        this.organisationUnitService = organisationUnitService;
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

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }
    
    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private PatientDataValueService patientDataValueService;
    
    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
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
    
    private ProgramInstanceService programInstanceService;
    
    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }
    
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }    
    
    private int organisationUnitId;
    
    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private int programId;
    
    public void setProgramId( int programId )
    {
        this.programId = programId;
    }

    private int programStageId;
    
    public void setProgramStageId( int programStageId )
    {
        this.programStageId = programStageId;
    }
   
    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }   
    
    private List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
    private List<OrganisationUnit > programSources = new ArrayList<OrganisationUnit>();
    
    private List<Patient> ashaList = new ArrayList<Patient>();
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
       
        // OrgUnit Related Info
        organisationUnit = new OrganisationUnit();
        organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId ); 
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
        
        Program program = programService.getProgram( programId );
        
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
        
        //System.out.println( organisationUnit.getName()  + " : Period is : " + period.getExternalId() );
        
        // -----------------------------------------------------------------------------
        // Prepare Patient DataValues
        // -----------------------------------------------------------------------------
        
        //HttpServletRequest request = ServletActionContext.getRequest();
        
        //String value = null;
        
        String storedBy = currentUserService.getCurrentUsername();
        
        ProgramStage programStage = programStageService.getProgramStage( programStageId );
        
       
        OptionSet optionSet = optionService.getOptionSetByName( OPTION_SET_MONTHLY_PAYMENT_DATAELEMENT );
        
        for( Patient patient : ashaList )
        {
            //System.out.println( patient.getFullName() );
            
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
            
            Integer programStageInstanceId = null;

            if ( programInstanceId != null )
            {
                programStageInstanceId = ashaService.getProgramStageInstanceId( programInstanceId, programStage.getId(), period.getStartDateString() );
            }

            ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );
            
            if ( programStageInstanceId == null )
            {
                ProgramStageInstance tempProgramStageInstance = new ProgramStageInstance();
                tempProgramStageInstance.setProgramInstance( programInstance );
                tempProgramStageInstance.setProgramStage( programStage );
                tempProgramStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );
                
                tempProgramStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                tempProgramStageInstance.setDueDate( format.parseDate( period.getStartDateString() ) );

                programStageInstanceId = programStageInstanceService.addProgramStageInstance( tempProgramStageInstance );
                
            }

            ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
            
            HttpServletRequest request = ServletActionContext.getRequest();

            String value = null;
            
            
            if( optionSet != null )
            {
                if ( programStageInstance.getExecutionDate() == null )
                {
                    programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }
                
                String tempCheckBoxStatus = request.getParameter( PREFIX_CHECKBOX + + patient.getId() + ":" + 508 );
                
                DataElement paymentStatusDataElement = dataElementService.getDataElement( 508 );
                
                if( tempCheckBoxStatus != null && tempCheckBoxStatus.equalsIgnoreCase( "true" ) )
                {
                    PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, paymentStatusDataElement );
                    
                    if ( patientDataValue == null )
                    {
                        if ( tempCheckBoxStatus != null && StringUtils.isNotBlank( tempCheckBoxStatus ) )
                        {
                            boolean providedElsewhere = false;

                            patientDataValue = new PatientDataValue( programStageInstance, paymentStatusDataElement, new Date(), tempCheckBoxStatus );
                            patientDataValue.setProvidedElsewhere( providedElsewhere );
                            patientDataValue.setValue( tempCheckBoxStatus );
                            patientDataValue.setStoredBy( storedBy );
                            patientDataValue.setTimestamp( new Date() );
                            patientDataValueService.savePatientDataValue( patientDataValue );
                        }
                    }
                    else
                    {
                        patientDataValue.setValue( tempCheckBoxStatus );
                        patientDataValue.setStoredBy( storedBy );
                        patientDataValue.setTimestamp( new Date() );
                        patientDataValueService.updatePatientDataValue( patientDataValue );
                    }
                    
                    for( String optionName : optionSet.getOptions() )
                    {
                        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( optionName ) );
                        
                        value = request.getParameter( PREFIX_DATAELEMENT + patient.getId() + ":" + dataElement.getId() );
                        
                        //String checkBoxStatus = request.getParameter( PREFIX_CHECKBOX + + patient.getId() + ":" + dataElement.getId() );
                        
                        patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, dataElement );                            
                            
                        //System.out.println(  PREFIX_DATAELEMENT + patient.getId() + ":" + dataElement.getId() +" : " + value );
                                                        
                        if ( patientDataValue == null )
                            {
                                if ( value != null && StringUtils.isNotBlank( value ) )
                                {
                                    boolean providedElsewhere = false;

                                    patientDataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );
                                    patientDataValue.setProvidedElsewhere( providedElsewhere );
                                    patientDataValue.setValue( value );
                                    patientDataValue.setStoredBy( storedBy );
                                    patientDataValue.setTimestamp( new Date() );
                                    patientDataValueService.savePatientDataValue( patientDataValue );
                                }
                            }
                            else
                            {
                                patientDataValue.setValue( value );
                                patientDataValue.setStoredBy( storedBy );
                                patientDataValue.setTimestamp( new Date() );
                                patientDataValueService.updatePatientDataValue( patientDataValue );
                            }
                    }
                }
            }
            
        }
        
        return SUCCESS;
    }
    
}


