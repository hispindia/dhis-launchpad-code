package org.hisp.dhis.asha.facilitator;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetFacilitatorASHAListAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------

    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
    
    public String getSelectedPeriodId()
    {
        return selectedPeriodId;
    }

    private Integer facilitatorId;
    
    public void setFacilitatorId( Integer facilitatorId )
    {
        this.facilitatorId = facilitatorId;
    }
    
    private List<Patient> patientList = new ArrayList<Patient>();
    
    public List<Patient> getPatientList()
    {
        return patientList;
    }

    private Facilitator facilitator;
    
    public Facilitator getFacilitator()
    {
        return facilitator;
    }
    
    private List<Patient> patients = new ArrayList<Patient>();
    
    public List<Patient> getPatients()
    {
        return patients;
    }

    
    // -------------------------------------------------------------------------
    // Action implementation
    // --------- ----------------------------------------------------------------

    public String execute()
    {
       
        facilitator = facilitatorService.getFacilitator( facilitatorId );
        
        //Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        Period period = periodService.getPeriodByExternalId( selectedPeriodId );
        
     
        List <Integer> tempPatientList = new ArrayList<Integer>( ashaService.getMonthlyFacilitatorASHAList( facilitator.getId(), period.getId() ) );
        
        patientList = new ArrayList<Patient>();
        
        for( Integer patientId : tempPatientList )
        {
            Patient asha = patientService.getPatient( patientId );
            
            //facilitator.getPatients().remove( asha );
            
            patientList.add( asha );
        }
        
        //facilitator.getPatients();
        
        patients = new ArrayList<Patient>( facilitator.getPatients() );
        
        patients.removeAll( patientList );
        
        
        /*
        System.out.println( " Remaining Patient List -- "  + patients.size() + "--" + selectedPeriodId );
        
        for( Patient asha : patients )
        {
            System.out.println( asha.getFullName() + " -- " + facilitator.getName() );
        }
        */
        
        
        return SUCCESS;
    }

}
