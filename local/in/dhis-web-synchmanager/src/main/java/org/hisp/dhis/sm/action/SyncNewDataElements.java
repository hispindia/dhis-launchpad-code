package org.hisp.dhis.sm.action;

import com.opensymphony.xwork2.Action;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.attribute.AttributeService;
import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.dataelement.*;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.sm.api.SynchInstance;
import org.hisp.dhis.sm.api.SynchInstanceService;
import org.hisp.dhis.sm.api.SynchManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by gaurav on 16/8/14.
 */


public class SyncNewDataElements implements Action {

    private AttributeService attributeService;

    public AttributeService getAttributeService() {
        return attributeService;
    }

    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

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

    private Integer instanceId;

    private DataElementService dataElementService;

    public DataElementService getDataElementService() {
        return dataElementService;
    }

    public void setDataElementService(DataElementService dataElementService) {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public DataElementCategoryService getDataElementCategoryService() {
        return dataElementCategoryService;
    }

    public void setDataElementCategoryService(DataElementCategoryService dataElementCategoryService) {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    public void setInstanceId( Integer instanceId )
    {
        this.instanceId = instanceId;
    }

    List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<DataElement> dataElements) {
        this.dataElements = dataElements;
    }

    public String dataElementsUIDs;

    public String getDataElementsUIDs() {
        return dataElementsUIDs;
    }

    public void setDataElementsUIDs(String dataElementsUIDs) {
        this.dataElementsUIDs = dataElementsUIDs;
    }

    private DataElementCategoryCombo validateDataElementCategoryCombo(DataElement dataElement)
    {
            if(dataElementCategoryService.getDataElementCategoryCombo(dataElement.getCategoryCombo().getUid())!=null )
            {
                return dataElementCategoryService.getDataElementCategoryCombo(dataElement.getCategoryCombo().getUid());
            }
            else if( dataElementCategoryService.getDataElementCategoryComboByName(dataElement.getCategoryCombo().getName())!=null )
            {
                 return dataElementCategoryService.getDataElementCategoryComboByName(dataElement.getCategoryCombo().getName());
            }
        return null;
    }

    private boolean validateDataElementAttributeValue(DataElement dataElement){

        Set<AttributeValue> attributeValueSet = dataElement.getAttributeValues();
        for(AttributeValue attributeValue : attributeValueSet)
        {
            Attribute attribute = attributeValue.getAttribute();
            if(attributeService.getAttribute(attribute.getUid())==null && attributeService.getAttribute(attribute.getName())==null)
            {
                attribute.setDataElementAttribute(true);
                attributeService.addAttribute(attribute);
            }
        }

        return true;
    }


    @Override
    public String execute() throws Exception {

        String[] dataElementUIDList = dataElementsUIDs.split(",");

        Set<SynchInstance> instances = synchInstanceService.getInstancesByType("meta-data");

        //----TO DO-----//

        MetaData metaData = synchManager.getMetaData(instances.iterator().next(), "dataElements");

        dataElements = metaData.getDataElements();

        for(String dataElementUID : dataElementUIDList){
            System.out.println(dataElementUID);
            for (DataElement dataElement : dataElements) {

                if(dataElement.getUid().equals(dataElementUID))
                {
                    DataElementCategoryCombo dataElementCategoryCombo = validateDataElementCategoryCombo(dataElement);
                    if( dataElementCategoryCombo != null )
                    {
                        DataElement newDataElement = new DataElement();

                        newDataElement.setCategoryCombo(dataElementCategoryCombo);
                        newDataElement.setName(dataElement.getName());
                        newDataElement.setNumberType(dataElement.getNumberType());
                        newDataElement.setDomainType(dataElement.getDomainType());
                        newDataElement.setUid(dataElement.getUid());
                        newDataElement.setShortName(dataElement.getShortName());
                        newDataElement.setAggregationOperator(dataElement.getAggregationOperator());
                        newDataElement.setType(dataElement.getType());

                        dataElementService.addDataElement(newDataElement);

                    }
                    else
                    {
                       System.out.println( "Category Combo not found!");
                    }
                }
            }
        }

        System.out.println("SD : "+dataElementsUIDs);

        return SUCCESS;
    }
}
