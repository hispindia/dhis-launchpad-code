package org.hisp.dhis.sm.util;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.metadata.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportTypeSummary;
import org.hisp.dhis.dxf2.sm.api.MetaDataSynchLog;

public class MetaDataSynchLogWrapper
{

    public MetaDataSynchLog getMetaDataSynchLog( ImportSummary importSummary, String objectType, List<String> metaDataUids )
    {
        MetaDataSynchLog metaDataSynchLog = new MetaDataSynchLog();
        
        List<ImportConflict> conflicts = new ArrayList<>();
        
        List<ImportTypeSummary> importTypeSummaryList = importSummary.getImportTypeSummaries();
        
        for( ImportTypeSummary importTypeSummary : importTypeSummaryList )
        {
            if( importTypeSummary.getType().equals( objectType ) )
            {
                if( importTypeSummary.getConflicts() == null || importTypeSummary.getConflicts().size() == 0 ) 
                {
                    for( String metaDataUid : metaDataUids )
                    {
                        ImportConflict ic = new ImportConflict();
                        ic.setObject( metaDataUid );
                        ic.setValue( "SUCCESS" );
                        conflicts.add( ic );
                    }
                }
            }
        }
        
        metaDataSynchLog.setConflicts( conflicts );
        
        return metaDataSynchLog;
    }
}
