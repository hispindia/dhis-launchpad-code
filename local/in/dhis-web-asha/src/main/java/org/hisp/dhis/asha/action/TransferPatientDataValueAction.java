package org.hisp.dhis.asha.action;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
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
public class TransferPatientDataValueAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    /*
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    */
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
   
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
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
    
    private PatientDataValueService patientDataValueService;
    
    public void setPatientDataValueService( PatientDataValueService patientDataValueService )
    {
        this.patientDataValueService = patientDataValueService;
    }
    
    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
   /*
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    */
    
    /*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }
    
    private Integer selectedOrgUnitId;
    
    public void setSelectedOrgUnitId( Integer selectedOrgUnitId )
    {
        this.selectedOrgUnitId = selectedOrgUnitId;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private String resultStatus;

    public String getResultStatus()
    {
        return resultStatus;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception 
    {
        
        System.out.println(  " Transfer Data Start Time is : " + new Date() );
        
        resultStatus = " ";
        
        // OrgUnit Related Info
        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit = organisationUnitService.getOrganisationUnit( selectedOrgUnitId );         
        
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
        
        Constant constant = constantService.getConstantByName( "ASHA Activity Program" );
        
        int selProgramId = (int) constant.getValue();
        
        Program selProgram = programService.getProgram( selProgramId );
        
        List<OrganisationUnit > programSources = new ArrayList<OrganisationUnit>();
        
        programSources = new ArrayList<OrganisationUnit>( selProgram.getOrganisationUnits() );
        
        if( selProgram != null &&  programSources != null && programSources.size() > 0 )
        {
            orgUnitList.retainAll( programSources );
        }    
        
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        
        List<Patient> ashaList = new ArrayList<Patient>();
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            List<Patient> patientList = new ArrayList<Patient>( patientService.getPatients( orgUnit, null,null ) );
            
            if( patientList != null && patientList.size() > 0 ) 
            {
                //System.out.println( orgUnit.getName()  + " : Patient List Size is : " + patientList.size() );
                ashaList.addAll( patientList );
            }
        }
        
        String storedBy = currentUserService.getCurrentUsername();
        
        List<Integer> dataElementIds = new ArrayList<Integer>();
        dataElementIds.add( 502 );
        dataElementIds.add( 503 );
        
        String dataElementIdsByComma = "502" + "," + "503";
        
        int slNo = 1;
        for( Patient patient : ashaList )
        {
            Integer programStageInstanceId = ashaService.getProgramStageInstanceIdByPatient( patient.getId(), 4, "2014-06-01" );
            
            ProgramStage programStage = programStageService.getProgramStage( 4 );
            
            if( programStageInstanceId != null )
            {
                Map<Integer, String> patientDataValueMap = new HashMap<Integer, String>();
                
                patientDataValueMap = new HashMap<Integer, String>( ashaService.getDataValueFromPatientDataValueTable( programStageInstanceId, dataElementIdsByComma ) );
                
                Integer programInstanceId = ashaService.getProgramInstanceId( patient.getId(), 4 );
                
                Integer newProgramStageInstanceId = ashaService.getProgramStageInstanceIdByPatient( patient.getId(), 4, "2014-07-01" );
                
                ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );
                
                if ( newProgramStageInstanceId == null )
                {
                    ProgramStageInstance tempProgramStageInstance = new ProgramStageInstance();
                    tempProgramStageInstance.setProgramInstance( programInstance );
                    tempProgramStageInstance.setProgramStage( programStage );
                    tempProgramStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );
                    
                    tempProgramStageInstance.setExecutionDate( format.parseDate( "2014-07-01" ) );
                    tempProgramStageInstance.setDueDate( format.parseDate( "2014-07-01" ) );

                    newProgramStageInstanceId = programStageInstanceService.addProgramStageInstance( tempProgramStageInstance );
                    
                }
                
                ProgramStageInstance newProgramStageInstance = programStageInstanceService.getProgramStageInstance( newProgramStageInstanceId );
                
                for( Integer dataElementId : dataElementIds )
                {
                    DataElement dataElement = dataElementService.getDataElement( dataElementId );
                    
                    String value = patientDataValueMap.get( dataElementId );
                    
                    PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( newProgramStageInstance, dataElement );
                    
                    if ( patientDataValue == null )
                    {
                        if ( value != null && StringUtils.isNotBlank( value ) )
                        {
                            boolean providedElsewhere = false;

                            patientDataValue = new PatientDataValue( newProgramStageInstance, dataElement, new Date(), value );
                            patientDataValue.setProvidedElsewhere( providedElsewhere );
                            patientDataValue.setValue( value );
                            patientDataValue.setStoredBy( storedBy );
                            patientDataValue.setTimestamp( new Date() );
                            
                            patientDataValue.setMOApprove( 0 );
                            patientDataValue.setAAApprove( 0 );
                            
                            patientDataValueService.savePatientDataValue( patientDataValue );
                        }
                    }
                    else
                    {
                        patientDataValue.setValue( value );
                        patientDataValue.setStoredBy( storedBy );
                        patientDataValue.setTimestamp( new Date() );
                        
                        patientDataValue.setMOApprove( 0 );
                        patientDataValue.setAAApprove( 0 );
                        
                        patientDataValueService.updatePatientDataValue( patientDataValue );
                    }
                    
                    System.out.println( "Sl.No -" + slNo +  "-ID and Name : " + patient.getId() + "--"+ patient.getFullName() +   " DEID  : " + dataElement.getId() +  " Value  : " + value +  " Transfered   " );
                }
              
            }
            
            slNo++;
            
        }
        
        resultStatus += " Data Successfully Transfered " + " and No of ASHA : " + ashaList.size() ;
    
        System.out.println(  " Data Transfered End Time is : " + new Date() );
        
        return SUCCESS;
    }
    

}