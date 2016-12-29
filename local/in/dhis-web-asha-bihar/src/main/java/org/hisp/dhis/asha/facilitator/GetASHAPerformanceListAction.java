package org.hisp.dhis.asha.facilitator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.asha.comparator.ASHANameComparator;
import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
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
public class GetASHAPerformanceListAction implements Action
{

    public static final String PATIENT_ATTRIBUTE_VILLAGE = "Village Attribute";//6.0
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
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    
    /*
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    */
    
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

    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer facilitatorId;
    
    public void setFacilitatorId( Integer facilitatorId )
    {
        this.facilitatorId = facilitatorId;
    }
    
    public Map<Integer, String> facilitatorPerformanceScoreValueMap;
    
    public Map<Integer, String> getFacilitatorPerformanceScoreValueMap()
    {
        return facilitatorPerformanceScoreValueMap;
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
    
    /*
    public Map<String, String> patientAttributeValueMap;
    
    public Map<String, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
    
    private int attributeId;
    
    public int getAttributeId()
    {
        return attributeId;
    }
    */
    
    //String patientIdsByComma;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // --------- ----------------------------------------------------------------

    public String execute()
    {
        facilitatorPerformanceScoreValueMap = new HashMap<Integer, String>();
        
        //patientAttributeValueMap = new HashMap<String, String>();
        
        facilitator = facilitatorService.getFacilitator( facilitatorId );
        
        //Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        Period period = periodService.getPeriodByExternalId( selectedPeriodId );
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        facilitatorPerformanceScoreValueMap = new HashMap<Integer, String>( ashaService.getASHAPerformanceScore( facilitator.getId(), period.getId(), dataSet.getId()  ) );
        
        //patientIdsByComma = "-1";
        
        for( Integer patientId : facilitatorPerformanceScoreValueMap.keySet() )
        {
            Patient asha = patientService.getPatient( patientId );
            
            //patientIdsByComma += "," + asha.getId();
            
            patientList.add( asha );
        }
        
        Collections.sort( patientList, new ASHANameComparator() );
        
        /*
        Collection<Integer> patientIds = new ArrayList<Integer>( getIdentifiers( Patient.class, patientList ) );
        String patientIdsByComma = getCommaDelimitedString( patientIds );
        
        Constant constant = constantService.getConstantByName( PATIENT_ATTRIBUTE_VILLAGE );
        
        attributeId = (int) constant.getValue();
        
        patientAttributeValueMap = new HashMap<String, String>( ashaService.getPatientAttributeValues( patientIdsByComma, attributeId ) );
        */
        
        /*
        System.out.println( " Size of facilitatorPerformanceScoreValueMap -- "  + facilitatorPerformanceScoreValueMap.size() );
       
        
        System.out.println( " Size of performance ASHA List -- "  + patientList.size() );
        
        for( Patient asha : patientList )
        {
            System.out.println( asha.getFullName() + " -- " + facilitator.getName()  + " --Score --  " + facilitatorPerformanceScoreValueMap.get( asha.getId() ) );
        }
        */
        
        
        return SUCCESS;
    }

}
