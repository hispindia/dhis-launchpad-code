package org.hisp.dhis.asha.beneficiaries;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowUpdateASHABeneficiaryFormAction
    implements Action
{
    
    public static final String ASHA_SERVICE_GROUP_SET = "ASHA Service Group Set";//1.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BeneficiaryService beneficiaryService;

    public void setBeneficiaryService( BeneficiaryService beneficiaryService )
    {
        this.beneficiaryService = beneficiaryService;
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
    
    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer beneficiaryId;

    public void setBeneficiaryId( Integer beneficiaryId )
    {
        this.beneficiaryId = beneficiaryId;
    }

    /*
     * private Integer periodId;
     * 
     * public void setPeriodId(Integer periodId) { this.periodId = periodId; }
     * 
     * private Integer ashaId;
     * 
     * public void setAshaId(Integer ashaId) { this.ashaId = ashaId; }
     */

    private Beneficiary beneficiary;

    public Beneficiary getBeneficiary()
    {
        return beneficiary;
    }

    private Period period;

    public Period getPeriod()
    {
        return period;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }
    
    private List<DataElementGroup> dataElementGroupList = new ArrayList<DataElementGroup>();
    
    public List<DataElementGroup> getDataElementGroupList()
    {
        return dataElementGroupList;
    }
    
    private DataElement dataElement;
    
    public DataElement getDataElement()
    {
        return dataElement;
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
    
    private List<DataElement> dataElementList = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }
    
    private List<Location> locations = new ArrayList<Location>();
    
    public List<Location> getLocations()
    {
        return locations;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute()
        throws Exception
    {
        beneficiary = beneficiaryService.getBeneficiaryById( beneficiaryId );
        
        //Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
       // beneficiary.getDataElementGroup().getId().getPrice().getRegistrationDate().getIdentifier().getVillage().getGender()// beneficiary.getFatherName()
        period = beneficiary.getPeriod();

        patient = beneficiary.getPatient();
        
        dataElement = beneficiary.getDataElement();
        
        Constant serviceGroupSet = constantService.getConstantByName( ASHA_SERVICE_GROUP_SET );
        
        DataElementGroupSet dataElementGroupSet = dataElementService.getDataElementGroupSet( (int) serviceGroupSet.getValue());
        
        dataElementGroupList = new ArrayList<DataElementGroup>( dataElementGroupSet.getMembers() );
        
        dataElementList = new ArrayList<DataElement>( dataElementService.getDataElementGroup( beneficiary.getDataElementGroup().getId() ).getMembers() );
        
        locations = new ArrayList<Location> ( locationService.getActiveLocationsByParentOrganisationUnit( patient.getOrganisationUnit() ) );
        
        
        /*
        for( DataElement de : dataElementList )
        {
            int count = beneficiaryService.getCountByServicePeriodAndASHA( patient, period, de );
            
            System.out.println( " De  : " + de.getName() + "-- count " + count );
        }
        */
        
        return SUCCESS;
    }
}
