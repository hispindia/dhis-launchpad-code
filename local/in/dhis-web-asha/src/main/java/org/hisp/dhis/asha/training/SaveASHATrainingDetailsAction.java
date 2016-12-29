package org.hisp.dhis.asha.training;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveASHATrainingDetailsAction implements Action
{
    public static final String PREFIX_DATAELEMENT = "deps";
    public static final String PREFIX_CHECKBOX = "checkbox";
    public static final String PREFIX_ATTRIBUTE = "pa";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
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
    
    private PatientAttributeService patientAttributeService;
    
    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private PatientAttributeOptionService patientAttributeOptionService;
    
    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
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
    
    private String selectedPeriod;
    
    public void setSelectedPeriod( String selectedPeriod )
    {
        this.selectedPeriod = selectedPeriod;
    }

    private Integer programId;
    
    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    /*
    private Integer programInstanceId;
    
    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }
    */
    
    private Integer programStageId;
    
    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }
    
    /*
    private Integer programStageInstanceId;
    
    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
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
    
 
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    


    public String execute() throws Exception
    {
        
        patient = patientService.getPatient( id );
        
        if ( selectedPeriod != null )
        {
            selectedPeriod = selectedPeriod.trim();
        }
        
        program = programService.getProgram( programId );
        
        ProgramStage programStage = programStageService.getProgramStage( programStageId );
        
        
        Integer programInstanceId = ashaService.getProgramInstanceId( patient.getId(), program.getId() );

        Integer programStageInstanceId = null;

        if ( programInstanceId != null )
        {
            programStageInstanceId = ashaService.getProgramStageInstanceId( programInstanceId, programStage.getId(), selectedPeriod );
        }

        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );

        if ( programStageInstanceId == null )
        {
            ProgramStageInstance tempProgramStageInstance = new ProgramStageInstance();
            tempProgramStageInstance.setProgramInstance( programInstance );
            tempProgramStageInstance.setProgramStage( programStage );
            tempProgramStageInstance.setOrganisationUnit( patient.getOrganisationUnit() );
            tempProgramStageInstance.setExecutionDate( format.parseDate( selectedPeriod ) );
            tempProgramStageInstance.setDueDate( format.parseDate( selectedPeriod ) );

            programStageInstanceId = programStageInstanceService.addProgramStageInstance( tempProgramStageInstance );
        }
        
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
        
        HttpServletRequest request = ServletActionContext.getRequest();

        //String value = null;

        String storedBy = currentUserService.getCurrentUsername();

        List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        
        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                programStageInstance.setExecutionDate( format.parseDate( selectedPeriod ) );
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }

            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                String checkBoxStatus = request.getParameter( PREFIX_CHECKBOX + programStageDataElement.getDataElement().getId() );
                
                //
                
                //System.out.println( " checkBoxStatus -- "+ checkBoxStatus );
                
                if( checkBoxStatus != null && checkBoxStatus.equalsIgnoreCase( "true" ) )
                {
                    //System.out.println( " checkBoxStatus -- "+ checkBoxStatus );
                    
                    String value = request.getParameter( PREFIX_DATAELEMENT + programStageDataElement.getDataElement().getId() );
                    
                    //System.out.println( " checkBoxStatus -- "+ checkBoxStatus + "--" + programStageDataElement.getDataElement().getId()  + " " + value );
                    
                    PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance,programStageDataElement.getDataElement() );

                    if ( patientDataValue == null )
                    {
                        if ( value != null && StringUtils.isNotBlank( value ) )
                        {
                            boolean providedElsewhere = false;

                            patientDataValue = new PatientDataValue( programStageInstance, programStageDataElement.getDataElement(), new Date(), value );
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

        
        // update Patient Attributes
        
        Collection<PatientAttribute> attributes = patientAttributeService.getAllPatientAttributes();
        
        PatientAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            //System.out.println( " Inside Update " );
            
            //patient.getAttributes().clear();
            
            //valuesForDelete = patientAttributeValueService.getPatientAttributeValues( patient );
            
          
            for ( PatientAttribute attribute : attributes )
            {
                String paValue = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( paValue ) )
                {
                    attributeValue = patientAttributeValueService.getPatientAttributeValue( patient, attribute );
                    
                    //attributeValue.setPatient( patient );
                    
                    if ( !patient.getAttributes().contains( attribute ) )
                    {
                        patient.getAttributes().add( attribute );
                    }

                    if ( attributeValue == null )
                    {
                        attributeValue = new PatientAttributeValue();
                        attributeValue.setPatient( patient );
                        attributeValue.setPatientAttribute( attribute );
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt(
                                paValue, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( paValue.trim() );
                        }
                        
                        patientAttributeValueService.savePatientAttributeValue( attributeValue );
                        
                        //valuesForSave.add( attributeValue );
                        
                    }
                    else
                    {
                        if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( paValue, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setPatientAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                            else
                            {
                                // This option was deleted ???
                            }
                        }
                        else
                        {
                            attributeValue.setValue( paValue.trim() );
                        }
                        
                        patientAttributeValueService.updatePatientAttributeValue( attributeValue );
                        
                        // valuesForUpdate.add( attributeValue );
                        //valuesForDelete.remove( attributeValue );
                    }
                }
            }
        }        
        
        return SUCCESS;
    }
    
}
