package org.hisp.dhis.dqa.impl;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.Collection;

import org.hisp.dhis.dqa.api.*;
import org.hisp.dhis.i18n.I18nService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultDQAParameterService implements DQAParameterService
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DQAParameterStore dqaParameterStore;
    
    public void setDqaParameterStore( DQAParameterStore dqaParameterStore )
    {
        this.dqaParameterStore = dqaParameterStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    
    
    // -------------------------------------------------------------------------
    // DQAParameter
    // -------------------------------------------------------------------------



    public int addDQAParameter( DQAParameter dqaParameter )
    {
        return dqaParameterStore.save( dqaParameter );
    }

    public void updateDQAParameter( DQAParameter dqaParameter )
    {
        dqaParameterStore.update( dqaParameter );
    }

    public void deleteDQAParameter( DQAParameter dqaParameter )
    {
        dqaParameterStore.delete( dqaParameter );
    }

    public DQAParameter getDQAParameter( int id )
    {
        return i18n( i18nService, dqaParameterStore.get( id ) );
    }

    public DQAParameter getDQAParameterByName( String name )
    {
        return i18n( i18nService, dqaParameterStore.getByName( name ) );
    }
    
    public Collection<DQAParameter> getAllDQAParameters()
    {
        return i18n( i18nService, dqaParameterStore.getAll() );
    }
    
}
