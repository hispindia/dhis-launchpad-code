package org.hisp.dhis.customreport;

import java.util.Collection;

import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportDesign;
import org.hisp.dhis.customreports.CustomReportDesignService;
import org.hisp.dhis.customreports.CustomReportDesignStore;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultCustomReportDesignService implements CustomReportDesignService
{ 
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CustomReportDesignStore customReportDesignStore;

    public void setCustomReportDesignStore( CustomReportDesignStore customReportDesignStore )
    {
        this.customReportDesignStore = customReportDesignStore;
    }

    // -------------------------------------------------------------------------
    // CustomReportDesign
    // -------------------------------------------------------------------------
    @Transactional
    @Override
    public int addCustomReportDesign( CustomReportDesign customReportDesign )
    {
        return customReportDesignStore.addCustomReportDesign( customReportDesign );
    }
    
    @Transactional
    @Override
    public void updateCustomReportDesign( CustomReportDesign customReportDesign )
    {
        customReportDesignStore.updateCustomReportDesign( customReportDesign );
    }
    
    @Transactional
    @Override
    public void deleteCustomReportDesign( CustomReportDesign customReportDesign )
    {
        customReportDesignStore.deleteCustomReportDesign( customReportDesign );
    }
    
    @Transactional
    @Override
    public CustomReportDesign getCustomReportDesignById( int id )
    {
        return customReportDesignStore.getCustomReportDesignById( id );
    }
    
    public Collection<CustomReportDesign> getAllCustomReportDesign()
    {
        return customReportDesignStore.getAllCustomReportDesign();
    }
        
    public Collection<CustomReportDesign> getAllCustomReportDesignByCustomReport( CustomReport customReport )
    {
        return customReportDesignStore.getAllCustomReportDesignByCustomReport( customReport );
    }
    
    public Collection<CustomReportDesign> getAllCustomReportDesignByCustomType( String customType )
    {
        return customReportDesignStore.getAllCustomReportDesignByCustomType( customType );
    }    
    
    public Collection<CustomReportDesign> getAllCustomReportDesignByCustomReportAndCustomType( CustomReport customReport, String customType )
    {
        return customReportDesignStore.getAllCustomReportDesignByCustomReportAndCustomType( customReport, customType );
    }        
    
}
