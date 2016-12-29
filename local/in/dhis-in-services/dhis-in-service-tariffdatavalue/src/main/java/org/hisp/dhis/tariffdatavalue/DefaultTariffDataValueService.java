package org.hisp.dhis.tariffdatavalue;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultTariffDataValueService implements TariffDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TariffDataValueStore tariffDataValueStore;

    public void setTariffDataValueStore( TariffDataValueStore tariffDataValueStore )
    {
        this.tariffDataValueStore = tariffDataValueStore;
    }

    // -------------------------------------------------------------------------
    // TariffDataValue
    // -------------------------------------------------------------------------

    
    public void addTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.addTariffDataValue( tariffDataValue );;
    }

   
    public void updateTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.updateTariffDataValue( tariffDataValue );
    }

   
    public void deleteTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.deleteTariffDataValue( tariffDataValue );
    }

 
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        return tariffDataValueStore.getTariffDataValue( organisationUnit, dataElement, dataSet, startDate, endDate );
    }

   
    public Collection<TariffDataValue> getAllTariffDataValues()
    {
        return tariffDataValueStore.getAllTariffDataValues();
    }

   
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, dataSet );
    }

   
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, dataElement );
    }

}
