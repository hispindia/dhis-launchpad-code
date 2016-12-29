package org.hisp.dhis.customreports;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.user.User;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface CustomReportService
{
    String ID = CustomReportService.class.getName();
    
    int addCustomReport( CustomReport customReport );
    
    void updateCustomReport( CustomReport customReport );

    void deleteCustomReport( CustomReport customReport );
    
    CustomReport getCustomReportById( int id );
    
    Collection<CustomReport> getAllCustomReports();
    
    Collection<CustomReport> getAllCustomReportsByReportType( String reportType );
    
    Collection<CustomReport> getAllCustomReportsByUser( User user );
    
    Collection<CustomReport> getAllAvailableCustomReports();
    
    void searchReportsByName( List<CustomReport> customReports, String key );
    
}
