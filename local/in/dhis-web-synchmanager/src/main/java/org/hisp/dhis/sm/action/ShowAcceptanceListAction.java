package org.hisp.dhis.sm.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.sm.api.SynchInstance;
import org.hisp.dhis.sm.api.SynchInstanceService;
import org.hisp.dhis.sm.api.SynchManager;

import com.opensymphony.xwork2.Action;

/**
 * User: gaurav Date: 2/8/14 Time: 12:13 PM
 */
public class ShowAcceptanceListAction
    implements Action
{

    private SynchManager synchManager;

    public void setSynchManager( SynchManager synchManager )
    {
        this.synchManager = synchManager;
    }

    private SynchInstanceService synchInstanceService;

    public void setSynchInstanceService( SynchInstanceService synchInstanceService )
    {
        this.synchInstanceService = synchInstanceService;
    }

    private DataElementService dataElementService;

    public DataElementService getDataElementService() 
    {
        return dataElementService;
    }

    public void setDataElementService(DataElementService dataElementService) 
    {
        this.dataElementService = dataElementService;
    }
    /*
    private Integer instanceId;

    public void setInstanceId( Integer instanceId )
    {
        this.instanceId = instanceId;
    }
    */
    
    List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    private List<DataElement> newDataElements = new ArrayList<DataElement>();

    public List<DataElement> getNewDataElements() 
    {
        return newDataElements;
    }

    public void setNewDataElements(List<DataElement> newDataElements) 
    {
        this.newDataElements = newDataElements;
    }

    List<DataElement> updatedDataElements = new ArrayList<DataElement>();

    public List<DataElement> getUpdatedDataElements() {
        return updatedDataElements;
    }

    public void setUpdatedDataElements(List<DataElement> updatedDataElements) 
    {
        this.updatedDataElements = updatedDataElements;
    }

    public String metaDataString;

    public String getMetaDataString() 
    {
        return metaDataString;
    }

    public void setMetaDataString(String metaDataString) 
    {
        this.metaDataString = metaDataString;

    }
    
    private MetaData metaData;
    
    public MetaData getMetaData()
    {
        return metaData;
    }

    @Override

    public String execute() throws Exception 
    {

        Set<SynchInstance> instances = synchInstanceService.getInstancesByType("meta-data");

        //----TO DO-----//
        
        //InputStream stream = new ByteArrayInputStream( metaDataString.getBytes("UTF-8"));
        //metaData = renderService.fromXml(stream, MetaData.class);
        
        //metaData = synchManager.getMetaData( instances.iterator().next(), "dataElements" );
        
        
        
        //dataElements = metaData.getDataElements();
        
        
        
        
        
        metaDataString = synchManager.getMetaDataString(instances.iterator().next(), "dataElements");

        MetaData metaData = synchManager.getMetaData(instances.iterator().next(), "dataElements");

        List<DataElement> dataElements = metaData.getDataElements();

        for ( DataElement dataElement : dataElements )
        {
            System.out.println( dataElement.getDisplayName() + " : " + dataElement.getLastUpdated() );


            for (DataElement dataElement1 : dataElements) 
            {
               if(dataElementService.getDataElement(dataElement.getUid())==null)
               {
                  newDataElements.add( dataElement1 );
               }
               else
               {
                  updatedDataElements.add( dataElement1 );
               }
    
            }

        }
        return SUCCESS;
    }
}