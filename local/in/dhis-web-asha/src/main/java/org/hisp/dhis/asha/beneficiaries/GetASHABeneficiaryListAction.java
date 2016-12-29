package org.hisp.dhis.asha.beneficiaries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetASHABeneficiaryListAction
    implements Action
{

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

    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
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
    
    // -------------------------------------------------------------------------
    // Action implementation
    // --------- ----------------------------------------------------------------

    

    public String execute()
    {
        Patient patient = patientService.getPatient( id );

        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        //System.out.println( "  patient  : " + patient.getFullName() );
        
        //System.out.println( "  period.getStartDateString()  : " + period.getStartDateString() );
        
        beneficiaryList = new ArrayList<Beneficiary>( beneficiaryService.getAllBeneficiaryByASHAAndPeriod( patient, period ) );

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
        
        return SUCCESS;
    }

}
