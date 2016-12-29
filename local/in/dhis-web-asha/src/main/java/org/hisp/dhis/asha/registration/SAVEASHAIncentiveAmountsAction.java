package org.hisp.dhis.asha.registration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
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

public class SAVEASHAIncentiveAmountsAction implements Action
{
    public static final String PREFIX_DATAELEMENT = "deps";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private Integer programStageId;
    
    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }
    
    private Integer programStageInstanceId;
    
    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        // -----------------------------------------------------------------------------
        // Prepare Patient DataValues
        // -----------------------------------------------------------------------------
        //System.out.println("here");
        ProgramStage programStage = programStageService.getProgramStage( programStageId );
        
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( programStageInstanceId );
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        String value = null;
        
        String storedBy = currentUserService.getCurrentUsername();
         
        List<ProgramStageDataElement> programStageDataElements =  new ArrayList<ProgramStageDataElement>( programStage.getProgramStageDataElements() );
        
        if ( programStageDataElements != null && programStageDataElements.size() > 0 )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                programStageInstance.setExecutionDate( format.parseDate( period.getStartDateString() ) );
                programStageInstanceService.updateProgramStageInstance( programStageInstance );
            }
            
            for ( ProgramStageDataElement programStageDataElement : programStageDataElements )
            {
                value = request.getParameter( PREFIX_DATAELEMENT + programStageDataElement.getDataElement().getId() );
                
                PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, programStageDataElement.getDataElement());
                
                if( patientDataValue == null  )
                {
                    if ( value != null && StringUtils.isNotBlank( value ) )
                    {
                        boolean providedElsewhere = false;

                        patientDataValue = new PatientDataValue( programStageInstance, programStageDataElement.getDataElement(), new Date(), value );
                        patientDataValue.setProvidedElsewhere( providedElsewhere );
                        patientDataValue.setValue(value);
                        patientDataValue.setStoredBy( storedBy );
                        patientDataValue.setTimestamp( new Date() );
                        patientDataValueService.savePatientDataValue( patientDataValue );
                        //System.out.println("patient datavalue saved for" + PREFIX_DATAELEMENT+programStageDataElement.getDataElement().getId() +"value=" +value);
                    }
                }
                else
                {
                    patientDataValue.setValue(value);
                    patientDataValue.setStoredBy( storedBy );
                    patientDataValue.setTimestamp( new Date() );
                    patientDataValueService.updatePatientDataValue( patientDataValue );
                    //System.out.println("patient datavalue updated for" + PREFIX_DATAELEMENT+programStageDataElement.getDataElement().getId() +"value=" +value);
                    
                }
              
            }
        }
        
        return SUCCESS;
    }
    
}

