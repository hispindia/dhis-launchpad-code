package org.hisp.dhis.dqa.api;

import java.util.Collection;


public interface DQADimensionService
{
    String ID = DQADimensionService.class.getName();
    
    
    // -------------------------------------------------------------------------
    // DQADimension
    // -------------------------------------------------------------------------

    /**
     * Adds a DQADimension.
     *
     * @param dqaDimension The DQADimension to add.
     * @return The generated unique identifier for this DQADimension.
     */
    int addDQADimension( DQADimension dqaDimension );

    /**
     * Updates a DQADimension.
     *
     * @param dqaDimension The DQADimension to update.
     */
    void updateDQADimension( DQADimension dqaDimension );

    /**
     * Deletes a DQADimension.
     *
     * @param dqaDimension The DQADimension to delete.
     */
    void deleteDQADimension( DQADimension dqaDimension );

    /**
     * Get a DQADimension
     *
     * @param id The unique identifier for the DataSet to get.
     * @return The DQADimension with the given id or null if it does not exist.
     */
    DQADimension getDQADimension( int id );

    /**
     * Returns the DQADimension with the given UID.
     *
     * @param name the Name.
     * @return the DQADimension with the given Name, or null if no match.
     */
    DQADimension getDQADimension( String name );
    
    /**
     * Returns all DQADimensions.
     *
     * @return a collection of all DQADimensions, or an empty collection if there
     *         are no DQADimensions.
     */
    Collection<DQADimension> getAllDQADimensions();
    
    void generateDefaultDQADimension();
    
}
