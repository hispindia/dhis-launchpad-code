package org.hisp.dhis.dqa.impl;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.Collection;

import org.hisp.dhis.dqa.api.*;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultDQADimensionService implements DQADimensionService
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DQADimensionStore dqaDimensionStore;

    public void setDqaDimensionStore( DQADimensionStore dqaDimensionStore )
    {
        this.dqaDimensionStore = dqaDimensionStore;
    }
    
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    
    // -------------------------------------------------------------------------
    // DQADimension
    // -------------------------------------------------------------------------

    public int addDQADimension( DQADimension dqaDimension )
    {
        return dqaDimensionStore.save( dqaDimension );
    }

    public void updateDQADimension( DQADimension dqaDimension )
    {
        dqaDimensionStore.update( dqaDimension );
    }

    public void deleteDQADimension( DQADimension dqaDimension )
    {
        dqaDimensionStore.delete( dqaDimension );
    }

    public DQADimension getDQADimension( int id )
    {
        return i18n( i18nService, dqaDimensionStore.get( id ) );
    }

    public DQADimension getDQADimension( String name )
    {
        return i18n( i18nService, dqaDimensionStore.getByName( name ) );
    }
    
    public Collection<DQADimension> getAllDQADimensions()
    {
        return i18n( i18nService, dqaDimensionStore.getAll() );
    }
    
    public void generateDefaultDQADimension()
    {
        DQADimension dqaDimension = new DQADimension(DQADimension.DQA_DIMENSION_TYPE_DEFAULT );

        addDQADimension( dqaDimension );
    }
    
}
