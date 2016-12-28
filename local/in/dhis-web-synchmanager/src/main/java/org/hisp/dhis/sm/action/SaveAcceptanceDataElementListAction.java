package org.hisp.dhis.sm.action;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dxf2.metadata.MetaData;

import antlr.collections.List;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveAcceptanceDataElementListAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    
    
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
    
    private List dataElements;
    
    public void setDataElements( List dataElements )
    {
        this.dataElements = dataElements;
    }
    
    private MetaData metaData;
    
    public void setMetaData( MetaData metaData )
    {
        this.metaData = metaData;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception 
    {
        
        for( DataElement de : metaData.getDataElements() )
        {
            System.out.println(  " De Id is : " + de.getId() + " De Name is : " + de.getName() );
        }
        
        
        return SUCCESS;
    }
    
}
