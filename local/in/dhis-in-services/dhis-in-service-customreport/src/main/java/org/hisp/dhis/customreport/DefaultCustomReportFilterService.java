package org.hisp.dhis.customreport;

import java.util.Collection;

import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportFilter;
import org.hisp.dhis.customreports.CustomReportFilterService;
import org.hisp.dhis.customreports.CustomReportFilterStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultCustomReportFilterService implements CustomReportFilterService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CustomReportFilterStore customReportFilterStore;

    public void setCustomReportFilterStore( CustomReportFilterStore customReportFilterStore )
    {
        this.customReportFilterStore = customReportFilterStore;
    }

    // -------------------------------------------------------------------------
    // CustomReportFilter
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public int addCustomReportFilter( CustomReportFilter customReportFilter )
    {
        return customReportFilterStore.addCustomReportFilter( customReportFilter );
    }
    
    @Transactional
    @Override
    public  void updateCustomReportFilter( CustomReportFilter customReportFilter )
    {
        customReportFilterStore.updateCustomReportFilter( customReportFilter );
    }
    
    @Transactional
    @Override
    public void deleteCustomReportFilter( CustomReportFilter customReportFilter )
    {
        customReportFilterStore.deleteCustomReportFilter( customReportFilter );
    }
    
    @Transactional
    @Override
    public CustomReportFilter getCustomReportFilterById( int id )
    {
        return customReportFilterStore.getCustomReportFilterById( id );
    }
    
    @Transactional
    @Override
    public Collection<CustomReportFilter> getAllCustomReportFilter()
    {
        return customReportFilterStore.getAllCustomReportFilter();
    }
    
    
    @Transactional
    @Override
    public Collection<CustomReportFilter> getAllCustomReportFilterByCustomReport( CustomReport customReport )
    {
        return customReportFilterStore.getAllCustomReportFilterByCustomReport( customReport );
    }
    
    @Transactional
    @Override
    public Collection<CustomReportFilter> getAllCustomReportFilterByFilterType( String filterType )
    {
        return customReportFilterStore.getAllCustomReportFilterByFilterType( filterType );
    }    
    
    @Transactional
    @Override
    public Collection<CustomReportFilter> getAllCustomReportFilterByCustomReportAndFilterType( CustomReport customReport, String filterType )
    {
        return customReportFilterStore.getAllCustomReportFilterByCustomReportAndFilterType( customReport, filterType );
    }        
        
}
