package org.hisp.dhis.customreports;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface CustomReportFilterService
{
    String ID = CustomReportFilterService.class.getName();
    
    int addCustomReportFilter( CustomReportFilter customReportFilter );
    
    void updateCustomReportFilter( CustomReportFilter customReportFilter );

    void deleteCustomReportFilter( CustomReportFilter customReportFilter );
    
    CustomReportFilter getCustomReportFilterById( int id );
    
    
    
    Collection<CustomReportFilter> getAllCustomReportFilter();
    
    Collection<CustomReportFilter> getAllCustomReportFilterByCustomReport( CustomReport customReport );
    
    Collection<CustomReportFilter> getAllCustomReportFilterByFilterType( String filterType );
    
    Collection<CustomReportFilter> getAllCustomReportFilterByCustomReportAndFilterType( CustomReport customReport, String filterType );    
    
    
}
