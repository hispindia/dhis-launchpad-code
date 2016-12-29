package org.hisp.dhis.customreports;

import java.util.Collection;

import org.hisp.dhis.user.User;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface CustomReportStore
{
    String ID = CustomReportStore.class.getName();
    
    int addCustomReport( CustomReport customReport );
    
    void updateCustomReport( CustomReport customReport );

    void deleteCustomReport( CustomReport customReport );
    
    CustomReport getCustomReportById( int id );
    
    
    Collection<CustomReport> getAllCustomReports();
    
    Collection<CustomReport> getAllCustomReportsByReportType( String reportType );
    
    Collection<CustomReport> getAllCustomReportsByUser( User user );
    
    //Collection<CustomReport> getAllCustomReportsByReportAvailable( boolean reportAvailable );
}
