package org.hisp.dhis.dqa.api;

import java.util.Collection;

public interface DQAParameterService
{
    String ID = DQAParameterService.class.getName();
    
    // -------------------------------------------------------------------------
    // DQAParameter
    // -------------------------------------------------------------------------

    /**
     * Adds a DQAParameter.
     *
     * @param dqaParameter The DQAParameter to add.
     * @return The generated unique identifier for this DQAParameter.
     */
    int addDQAParameter( DQAParameter dqaParameter );

    /**
     * Updates a DQAParameter.
     *
     * @param dqaParameter The DQAParameter to update.
     */
    void updateDQAParameter( DQAParameter dqaParameter );

    /**
     * Deletes a DQADimension.
     *
     * @param dqaDimension The DQADimension to delete.
     */
    void deleteDQAParameter( DQAParameter dqaParameter );

    /**
     * Get a DQAParameter
     *
     * @param id The unique identifier for the DataSet to get.
     * @return The DQAParameter with the given id or null if it does not exist.
     */
    DQAParameter getDQAParameter( int id );

    /**
     * Returns the DQAParameter with the given UID.
     *
     * @param name the Name.
     * @return the DQAParameter with the given Name, or null if no match.
     */
    DQAParameter getDQAParameterByName( String name );
    
    /**
     * Returns all DQAParameters.
     *
     * @return a collection of all DQAParameters, or an empty collection if there
     *         are no DQAParameters.
     */
    Collection<DQAParameter> getAllDQAParameters();
    
    
    
    
    
}

