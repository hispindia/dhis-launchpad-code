package org.hisp.dhis.dqa.api;

import java.util.Collection;

public interface DQAReportCardDataValueStore
{
    String ID = DQAReportCardDataValueStore.class.getName();

    // -------------------------------------------------------------------------
    // Basic DQAReportCardDataValue
    // -------------------------------------------------------------------------

    /**
     * Adds a DQAReportCardDataValue.
     * @param dqaReportCardDataValue the DQAReportCardDataValue to add.
     */
    void addDQAReportCardDataValue( DQAReportCardDataValue dqaReportCardDataValue );

    /**
     * Updates a DQAReportCardDataValue. 
     * @param dqaReportCardDataValue the DQAReportCardDataValue to update.
     */
    void updateDQAReportCardDataValue( DQAReportCardDataValue dqaReportCardDataValue );

    /**
     * Deletes a DQAReportCardDataValue.
     * 
     * @param dqaReportCardDataValue the DQAReportCardDataValue to delete.
     */
    void deleteDQAReportCardDataValue( DQAReportCardDataValue dqaReportCardDataValue );
    
    /**
     * Deletes all DQAReportCardDataValue connected to a DQAParameter.
     * 
     * @param dqaParameter the DQAParameter for which the DQAReportCardDataValues should be deleted.
     * @return the number of deleted DataValues.
     */
    int deleteDataValuesByDQAParameter( String dqaParameter );


    /**
     * Returns a DQAReportCardDataValue.
     * 
     * @param level1 the Level1 of the DQAReportCardDataValue.
     * @param level2 the Level2 of the DQAReportCardDataValue.
     * @param year the Year of the DQAReportCardDataValue.
     * @param dqaParameter the DQAParameter of the DQAReportCardDataValue.
     * @return the DQAReportCardDataValue which corresponds to the given parameters, or null
     *         if no match.
     */
    DQAReportCardDataValue getDQAReportCardDataValue( String level1, String level2, String year, String dqaParameter );

    
    // -------------------------------------------------------------------------
    // Collections of DQAReportCardDataValues
    // -------------------------------------------------------------------------

    /**
     * Returns all DQAReportCardDataValues.
     * 
     * @return a collection of all DQAReportCardDataValues.
     */
    Collection<DQAReportCardDataValue> getAllDQAReportCardDataValues();
    
    /**
     * Returns all DQAReportCardDataValues for a given DQAParameter.
     * 
     * @param dqaParameter the DQAParameter of the DataValues.
     * @return a collection of all DQAReportCardDataValues which match the given DQAParameter
     * or an empty collection if no values match.
     */
    Collection<DQAReportCardDataValue> getAllDQAReportCardDataValues( String dqaParameter );
    
    /**
     * Returns all DQAReportCardDataValues for a given Level1 Level2 Year.
     * 
     * @param dqaParameter the DQAParameter of the DataValues.
     * @return a collection of all DQAReportCardDataValues which match the given Level1 Level2 Year.
     * or an empty collection if no values match.
     */
    Collection<DQAReportCardDataValue> getAllDQAReportCardDataValuesByLevelPeriod( String level1, String level2, String year );

}
