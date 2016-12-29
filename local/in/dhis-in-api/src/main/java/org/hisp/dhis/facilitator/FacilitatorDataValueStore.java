package org.hisp.dhis.facilitator;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.period.Period;


/**
 * @author Mithilesh Kumar Thakur
 */
public interface FacilitatorDataValueStore
{
    String ID = FacilitatorDataValueStore.class.getName();
    
    
    // -------------------------------------------------------------------------
    // Basic FacilitatorDataValue
    // -------------------------------------------------------------------------

    /**
     * Adds a FacilitatorDataValue.
     * 
     * @param facilitatorDataValue the FacilitatorDataValue to add.
     */
    void addFacilitatorDataValue( FacilitatorDataValue facilitatorDataValue );

    /**
     * Updates a FacilitatorDataValue.
     * 
     * @param facilitatorDataValue the FacilitatorDataValue to update.
     */
    void updateFacilitatorDataValue( FacilitatorDataValue facilitatorDataValue );

    /**
     * Deletes a FacilitatorDataValue.
     * 
     * @param facilitatorDataValue the FacilitatorDataValue to delete.
     */
    void deleteFacilitatorDataValue( FacilitatorDataValue facilitatorDataValue );
    
   
    int deleteFacilitatorDataValuesByFacilitator( Facilitator facilitator );
    
 
    int deleteFacilitatorDataValuesByDataElement( DataElement dataElement );
    
 
    FacilitatorDataValue getFacilitatorDataValue( Facilitator facilitator, Patient patient, Period period, DataElement dataElement, DataElementCategoryOptionCombo optionCombo );
    
    
    // -------------------------------------------------------------------------
    // Collections of FacilitatorDataValue
    // -------------------------------------------------------------------------

    /**
     * Returns all FacilitatorDataValues.
     * 
     * @return a collection of all FacilitatorDataValues.
     */
    Collection<FacilitatorDataValue> getAllFacilitatorDataValues();
    
   
    Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, Period period );
    
    Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, DataElement dataElement );
    
    Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, Period period, Collection<DataElement> dataElements );
    
    Collection<FacilitatorDataValue> getFacilitatorDataValues( Facilitator facilitator, Period period, Collection<DataElement> dataElements, Collection<DataElementCategoryOptionCombo> optionCombos );
    
}
