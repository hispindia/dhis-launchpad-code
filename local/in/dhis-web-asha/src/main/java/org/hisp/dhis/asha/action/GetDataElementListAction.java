package org.hisp.dhis.asha.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetDataElementListAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private Integer dataElementGroupId;
    
    public void setDataElementGroupId( Integer dataElementGroupId )
    {
        this.dataElementGroupId = dataElementGroupId;
    }
    
    public List<DataElement> dataElementList = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }
    
    private DataElementGroup  dataElementGroup;
    
    public DataElementGroup getDataElementGroup()
    {
        return dataElementGroup;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    
    public String execute()
    {
        
        dataElementGroup = dataElementService.getDataElementGroup( dataElementGroupId );
      
        if ( dataElementGroup != null )
        {
            dataElementList = new ArrayList<DataElement>( dataElementGroup.getMembers() );
        }
        else
        {
            dataElementList = new ArrayList<DataElement>();
        }
        
        Collections.sort( dataElementList, new IdentifiableObjectNameComparator() );
        
        return SUCCESS;
    }
}