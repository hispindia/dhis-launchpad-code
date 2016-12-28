package org.hisp.dhis.dxf2.sm.api;

import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;

public class DataElementSynch
{
    private DataElement dataElement;
    
    private SynchInstance instance;
    
    private Boolean rememberAppStatus;
    
    private Date lastUpdated;
    
    private Boolean approveStatus;
    
    private Date approvedDate;
    
    private Boolean acceptStatus;
    
    private Date acceptedDate;
}
