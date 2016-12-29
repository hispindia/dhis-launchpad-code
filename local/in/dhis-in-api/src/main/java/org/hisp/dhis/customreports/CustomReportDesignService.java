package org.hisp.dhis.customreports;

import java.util.Collection;

/**
 * @author Mithilesh Kumar Thakur
 */

public interface CustomReportDesignService
{
    String ID = CustomReportDesignService.class.getName();
    
    int addCustomReportDesign( CustomReportDesign customReportDesign );
    
    void updateCustomReportDesign( CustomReportDesign customReportDesign );

    void deleteCustomReportDesign( CustomReportDesign customReportDesign );
    
    CustomReportDesign getCustomReportDesignById( int id );
    
    
    Collection<CustomReportDesign> getAllCustomReportDesign();
    
    Collection<CustomReportDesign> getAllCustomReportDesignByCustomReport( CustomReport customReport );
    
    Collection<CustomReportDesign> getAllCustomReportDesignByCustomType( String customType );
    
    Collection<CustomReportDesign> getAllCustomReportDesignByCustomReportAndCustomType( CustomReport customReport, String customType );
    
}
