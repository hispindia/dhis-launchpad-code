package org.hisp.dhis.dqa.impl;

import java.util.Collection;
import org.hisp.dhis.dqa.api.*;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultDQAReportCardDataValueService implements DQAReportCardDataValueService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DQAReportCardDataValueStore dqaReportCardDataValueStore;
    
    public void setDqaReportCardDataValueStore( DQAReportCardDataValueStore dqaReportCardDataValueStore )
    {
        this.dqaReportCardDataValueStore = dqaReportCardDataValueStore;
    }

    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------

    public void addDQAReportCardDataValue( DQAReportCardDataValue dqaReportCardDataValue )
    {
        dqaReportCardDataValueStore.addDQAReportCardDataValue( dqaReportCardDataValue );
    }
    

    public void updateDQAReportCardDataValue( DQAReportCardDataValue dqaReportCardDataValue )
    {
        dqaReportCardDataValueStore.updateDQAReportCardDataValue( dqaReportCardDataValue );
    }

    public void deleteDQAReportCardDataValue( DQAReportCardDataValue dqaReportCardDataValue )
    {
        dqaReportCardDataValueStore.deleteDQAReportCardDataValue( dqaReportCardDataValue );
    }

    public int deleteDataValuesByDQAParameter( String dqaParameter )
    {
        return dqaReportCardDataValueStore.deleteDataValuesByDQAParameter( dqaParameter );
    }
    
    public DQAReportCardDataValue getDQAReportCardDataValue( String level1, String level2, String year, String dqaParameter )
    {
        return dqaReportCardDataValueStore.getDQAReportCardDataValue( level1, level2, year, dqaParameter );
    }
    
    // -------------------------------------------------------------------------
    // Collections of DQAReportCardDataValues
    // -------------------------------------------------------------------------
  
    public Collection<DQAReportCardDataValue> getAllDQAReportCardDataValues()
    {
        return dqaReportCardDataValueStore.getAllDQAReportCardDataValues();
    }

    public Collection<DQAReportCardDataValue> getAllDQAReportCardDataValues( String dqaParameter )
    {
        return dqaReportCardDataValueStore.getAllDQAReportCardDataValues( dqaParameter );
    }
    
    public Collection<DQAReportCardDataValue> getAllDQAReportCardDataValuesByLevelPeriod( String level1, String level2, String year )
    {
        return dqaReportCardDataValueStore.getAllDQAReportCardDataValuesByLevelPeriod( level1, level2, year );
    }
    
    
    
}
