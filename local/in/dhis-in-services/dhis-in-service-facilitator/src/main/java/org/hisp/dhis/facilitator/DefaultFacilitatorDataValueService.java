package org.hisp.dhis.facilitator;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultFacilitatorDataValueService implements FacilitatorDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private FacilitatorDataValueStore facilitatorDataValueStore;

    public void setFacilitatorDataValueStore( FacilitatorDataValueStore facilitatorDataValueStore )
    {
        this.facilitatorDataValueStore = facilitatorDataValueStore;
    }

    // -------------------------------------------------------------------------
    // Basic FacilitatorDataValue
    // -------------------------------------------------------------------------


    public void addFacilitatorDataValue( FacilitatorDataValue facilitatorDataValue )
    {
        facilitatorDataValueStore.addFacilitatorDataValue( facilitatorDataValue );
    }

    public void updateFacilitatorDataValue( FacilitatorDataValue facilitatorDataValue )
    {
        facilitatorDataValueStore.updateFacilitatorDataValue( facilitatorDataValue );
    }

    @Transactional
    public void deleteFacilitatorDataValue( FacilitatorDataValue facilitatorDataValue )
    {
        facilitatorDataValueStore.deleteFacilitatorDataValue( facilitatorDataValue );
    }

    @Transactional
    public int deleteFacilitatorDataValuesByFacilitator( Facilitator facilitator )
    {
        return facilitatorDataValueStore.deleteFacilitatorDataValuesByFacilitator( facilitator );
    }

    @Transactional
    public int deleteFacilitatorDataValuesByDataElement( DataElement dataElement )
    {
        return facilitatorDataValueStore.deleteFacilitatorDataValuesByDataElement( dataElement );
    }

    public FacilitatorDataValue getFacilitatorDataValue( Facilitator facilitator, Patient patient,Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo )
    {
        return facilitatorDataValueStore.getFacilitatorDataValue( facilitator, patient, period, dataElement, optionCombo );
    }

    
    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    public Collection<FacilitatorDataValue> getAllFacilitatorDataValues()
    {
        return facilitatorDataValueStore.getAllFacilitatorDataValues();
    }

    public Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, Period period )
    {
        return facilitatorDataValueStore.getFacilitatorDataValues( facilitator, period );
    }
    
    public Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, DataElement dataElement )
    {
        return facilitatorDataValueStore.getFacilitatorDataValues( facilitator, dataElement );
    }

    public Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, Period period, Collection<DataElement> dataElements )
    {
        return facilitatorDataValueStore.getFacilitatorDataValues( facilitator, period, dataElements );
    }
    
    public Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, Period period, Collection<DataElement> dataElements, Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        return facilitatorDataValueStore.getFacilitatorDataValues( facilitator, period, dataElements, optionCombos );
    }    
    
}
