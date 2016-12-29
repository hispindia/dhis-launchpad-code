package org.hisp.dhis.asha.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reports.ReportService;

import com.opensymphony.xwork2.Action;

public class GetServicePriceAction implements Action
{
    public static final String ASHA_AMOUNT_DATA_SET = "Amount"; // 2.0
    
    //public static final String ASHA_AMOUNT_DATA_SET = "PERFORMANCE_INCENTIVE_DATA_SET_ID"; // 2.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private Integer dataElementId;
    
    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }
    
    private String servicePrice;
    
    public String getServicePrice()
    {
        return servicePrice;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
    {
        Constant amountDataSet = constantService.getConstantByName( ASHA_AMOUNT_DATA_SET );
        
        // Data set  Information
        
        DataSet dataSet = dataSetService.getDataSet( (int) amountDataSet.getValue() );
        
        // OrganisationUnit  Information
        
        List<OrganisationUnit> dataSetSource = new ArrayList<OrganisationUnit>( dataSet.getSources() );
        
        OrganisationUnit organisationUnit = dataSetSource.get( 0 );
        
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        List<AttributeValue> attributeValues = new ArrayList<AttributeValue>( dataElement.getAttributeValues() );
        
        AttributeValue attributeValue = attributeValues.get( 0 );
        
        DataElement aggDataElement = dataElementService.getDataElement( Integer.parseInt( attributeValue.getValue() ) );
        
        //System.out.println( " Patient DataElement  : " + dataElement.getId() + " ----- " + dataElement.getName() );
        
        //System.out.println( " AttributeValue  : " + attributeValue.getValue() );
        
        //System.out.println( " Agg DataElement  : "  + aggDataElement.getId() + " ----- " + aggDataElement.getName() );
        
        if ( aggDataElement != null )
        {
            DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
            
            DataValue dataValue = new DataValue();
            
            dataValue = reportService.getLatestDataValue( aggDataElement, optionCombo, organisationUnit );
            
            servicePrice = "";
            
            if ( dataValue != null )
            {
                servicePrice = dataValue.getValue();
                
            }
        }
        
        //System.out.println( " Price is  : " + servicePrice );
        return SUCCESS;
    }
}
