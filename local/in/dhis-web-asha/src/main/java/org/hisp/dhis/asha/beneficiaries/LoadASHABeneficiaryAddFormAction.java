package org.hisp.dhis.asha.beneficiaries;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientdataentrylock.PatientDataEntryLock;
import org.hisp.dhis.patientdataentrylock.PatientDataEntryLockService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */


public class LoadASHABeneficiaryAddFormAction implements Action
{
    public static final String ASHA_SERVICE_GROUP_SET = "ASHA Service Group Set";//1.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientDataEntryLockService patientDataEntryLockService;
   
    public void setPatientDataEntryLockService( PatientDataEntryLockService patientDataEntryLockService )
    {
        this.patientDataEntryLockService = patientDataEntryLockService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
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
    
    public List<DataElementGroup> dataElementGroupList = new ArrayList<DataElementGroup>();
    
    public List<DataElementGroup> getDataElementGroupList()
    {
        return dataElementGroupList;
    }
    
    private Period period;
    
    public Period getPeriod()
    {
        return period;
    }
    
    private int id;
    
    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
    
    private List<Location> locations = new ArrayList<Location>();
    
    public List<Location> getLocations()
    {
        return locations;
    }
    
    private String status;
    
    public String getStatus()
    {
        return status;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        status = "NONE";
        
        Patient patient = patientService.getPatient( id );
        
        locations = new ArrayList<Location> ( locationService.getActiveLocationsByParentOrganisationUnit( patient.getOrganisationUnit() ) );
        
        period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        Constant serviceGroupSet = constantService.getConstantByName( ASHA_SERVICE_GROUP_SET );
        
        DataElementGroupSet dataElementGroupSet = dataElementService.getDataElementGroupSet( (int) serviceGroupSet.getValue());
        
        dataElementGroupList = new ArrayList<DataElementGroup>( dataElementGroupSet.getMembers() );
        
       
        PatientDataEntryLock patientDataEntryLock = patientDataEntryLockService.getPatientDataEntryLock( patient.getOrganisationUnit(), period, patient );
        
        if( patientDataEntryLock != null && patientDataEntryLock.isLockStatus() )
        {
            status = i18n.getString( "Data Entry Done" );

            return SUCCESS;
        }
        
 
        
        
        /*
        Calendar calendar = Calendar.getInstance();
        
        calendar.setTime( period.getStartDate() );
        
        int monthMaxDays = calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
        
        System.out.println(  " monthMaxDays -- "+ monthMaxDays );
        */
        return SUCCESS;
    }

}


