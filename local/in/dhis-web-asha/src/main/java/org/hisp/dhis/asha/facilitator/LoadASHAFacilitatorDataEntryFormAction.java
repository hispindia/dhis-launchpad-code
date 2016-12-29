package org.hisp.dhis.asha.facilitator;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.asha.comparator.ASHANameComparator;
import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadASHAFacilitatorDataEntryFormAction implements Action
{
    public static final String ASHA_FACILITATOR_ATTRIBUTE = "ASHA Facilitator";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    private FacilitatorDataValueService facilitatorDataValueService;
    
    public void setFacilitatorDataValueService( FacilitatorDataValueService facilitatorDataValueService )
    {
        this.facilitatorDataValueService = facilitatorDataValueService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    */
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }
    /*
    private Comparator<ASHANameComparator> ashaNameComparator;
    
    public void setAshaNameComparator( Comparator<ASHANameComparator> ashaNameComparator )
    {
        this.ashaNameComparator = ashaNameComparator;
    }
    */
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private int selectedASHAId;
    
    public void setSelectedASHAId( int selectedASHAId )
    {
        this.selectedASHAId = selectedASHAId;
    }

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    public Integer getId()
    {
        return id;
    } 
    
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
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
    
    public Map<Integer, String> facilitatorDataValueMap;
    
    public Map<Integer, String> getFacilitatorDataValueMap()
    {
        return facilitatorDataValueMap;
    }
    
    private List<Section> sections;

    public List<Section> getSections()
    {
        return sections;
    }
    
    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }
    
    public Map<Integer, String> facilitatorPerformanceScoreValueMap;
    
    public Map<Integer, String> getFacilitatorPerformanceScoreValueMap()
    {
        return facilitatorPerformanceScoreValueMap;
    }

    private List<Patient> patintList = new ArrayList<Patient>();
    
    public List<Patient> getPatintList()
    {
        return patintList;
    }
    
    private PatientAttributeGroup attributeGroup;
    
    public PatientAttributeGroup getAttributeGroup()
    {
        return attributeGroup;
    }
    
    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
    
    private Map<Integer, PatientAttribute> patientAttributeMap;
    
    public Map<Integer, PatientAttribute> getPatientAttributeMap()
    {
        return patientAttributeMap;
    }
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    

    public String execute()
    {
        facilitator = facilitatorService.getFacilitator( id );
        
        facilitatorDataValueMap = new HashMap<Integer, String>();
        
        facilitatorPerformanceScoreValueMap = new HashMap<Integer, String>();
        
        //Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        Period tempPeriod = periodService.getPeriodByExternalId( selectedPeriodId );
        
        dataSet = dataSetService.getDataSet( dataSetId );
        
        sections = new ArrayList<Section>( dataSet.getSections() );
        
        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        Collections.sort( dataElements, dataElementComparator );
        
        //List<Patient> tempPatientList = new ArrayList<Patient>( facilitator.getPatients() );
        
        
        patients = new ArrayList<Patient>( facilitator.getPatients() );
        
        Collections.sort( patients, new ASHANameComparator() );
        
        patient = patientService.getPatient( selectedASHAId );
        
        
        
        //Collections.sort( patients, new IdentifiableObjectNameComparator() );
        
        //System.out.println( " Time Start :" + new Date() );
        
        //facilitatorDataValueMap = new HashMap<String, String>( ashaService.getASHAFacilitatorDataValues( facilitator.getId(), tempPeriod.getId() ) );
        
        
        facilitatorDataValueMap = new HashMap<Integer, String>( ashaService.getASHAFacilitatorDataValues( facilitator.getId(), patient.getId(), tempPeriod.getId() ) );
        
        /*
        facilitatorPerformanceScoreValueMap = new HashMap<Integer, String>( ashaService.getASHAPerformanceScore( facilitator.getId(), tempPeriod.getId(), dataSet.getId()  ) );
        
        
        for( Integer patientId : facilitatorPerformanceScoreValueMap.keySet() )
        {
            Patient asha = patientService.getPatient( patientId );
            patintList.add( asha );
        }
        
        for( Patient patient : patintList )
        {
            System.out.println( patient.getId() + " -- " + patient.getFullName()  + " --Score --  " + facilitatorPerformanceScoreValueMap.get( patient.getId() ) );
        }
        
        System.out.println( " Size of performance ASHA List -- "  + patintList.size() );
        */
        
        /*
        List<FacilitatorDataValue> facilitatorDataValues = new ArrayList<FacilitatorDataValue>( facilitatorDataValueService.getFacilitatorDataValues( facilitator, period )  );
        
        if( facilitatorDataValues != null && facilitatorDataValues.size() > 0 )
        {
            for( FacilitatorDataValue facilitatorDataValue : facilitatorDataValues )
            {   
                String value = "";
                if( facilitatorDataValue != null )
                {
                    String key = facilitatorDataValue.getDataElement().getId()+ ":" +  facilitatorDataValue.getPatient().getId();
                    
                    value = facilitatorDataValue.getValue();
                    
                    facilitatorDataValueMap.put( key, value );
                }
            }
        }
        */
        
        /*
       
        for( Patient patient : patients )
        {
            for( DataElement dataElement : dataElements )
            {
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
                
                FacilitatorDataValue facilitatorDataValue = new FacilitatorDataValue();
                
                facilitatorDataValue = facilitatorDataValueService.getFacilitatorDataValue( facilitator, patient, period, dataElement, optionCombo );                
                            
                String value = "";
                
                if ( facilitatorDataValue != null )
                {
                    value = facilitatorDataValue.getValue();
                }
                
                //System.out.println( patient.getId() +" -- " + dataElement.getName()  + " -- " + value );
                
                String key = dataElement.getId()+ ":" +  patient.getId();
                
                facilitatorDataValueMap.put( key, value );
                
            }
        }
        */
        //System.out.println( " Time End :" + new Date() );
        
        //System.out.println( " Size of Facilitator Data Value Map is ======  :" + facilitatorDataValueMap.size() );
        
        
        // ASHA Attribute Related Information
        
        attributeGroup = new PatientAttributeGroup();
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_FACILITATOR_ATTRIBUTE );
        
        patientAttributeMap = new HashMap<Integer, PatientAttribute>();
        
        List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( attributeGroup.getAttributes() );
        
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeMap.put( patientAttribute.getId(), patientAttribute );
        }
        
        Collection<Integer> patientAttributeIds = new ArrayList<Integer>( getIdentifiers( PatientAttribute.class, patientAttributes ) );
        String patientAttributeIdsByComma = getCommaDelimitedString( patientAttributeIds );
        
        patientAttributeValueMap = new HashMap<Integer, String>();
        
        patientAttributeValueMap = new HashMap<Integer, String>( ashaService.getPatientAttributeValues( patient.getId(), patientAttributeIdsByComma ) );
        
        /*
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            
            System.out.println( "-- Patient Name is : " + patient.getFullName()  + "-- Key is  :" + patientAttribute.getId() + " -- Value is " + patientAttributeValueMap.get( patientAttribute.getId() ) );
        }
        */
        
        return SUCCESS;
    }

}