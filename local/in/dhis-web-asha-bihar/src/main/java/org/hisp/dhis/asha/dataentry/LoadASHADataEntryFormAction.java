package org.hisp.dhis.asha.dataentry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadASHADataEntryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
    
    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
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
    
    public Map<Integer, String> ashaDataValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getAshaDataValueMap()
    {
        return ashaDataValueMap;
    }

    private Patient patient;
    
    public Patient getPatient()
    {
        return patient;
    }
    
    private Period period;
    
    public Period getPeriod()
    {
        return period;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute()
    {
        patient = patientService.getPatient( id );
        
        period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        Collections.sort( dataElements, dataElementComparator );
        
        ashaDataValueMap = new HashMap<Integer, String>();
        
        for( DataElement dataElement : dataElements )
        {
            DataElementCategoryOptionCombo decoc = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
            
            //DataElementCategoryCombo dataElementCategoryCombo = dataElement.getCategoryCombo();
            
            DataValue dataValue = new DataValue();
            
            dataValue = dataValueService.getDataValue( patient.getOrganisationUnit(), dataElement, period, decoc );
            
            String value = "";
            
            if ( dataValue != null )
            {
                value = dataValue.getValue();
            }
            
            // System.out.println( organisationUnit.getId() +" -- " + period.getId() + " -- " + dataElement.getName()  + " -- " + value );
            
            //String key = patient.getOrganisationUnit().getId()+ ":" +  period.getId()  + ":" + dataElement.getId();
            
            ashaDataValueMap.put( dataElement.getId(), value );
        }
        /*
        for( Integer key : ashaDataValueMap.keySet() )
        {
            System.out.println( " Key is -- " + key + " -- Value is  " + ashaDataValueMap.get( key ) );
        }
        */
        
        return SUCCESS;
    }

}
