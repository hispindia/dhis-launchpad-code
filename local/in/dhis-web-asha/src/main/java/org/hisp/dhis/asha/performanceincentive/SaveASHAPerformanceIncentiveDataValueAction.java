package org.hisp.dhis.asha.performanceincentive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.tariffdatavalue.TariffDataValue;
import org.hisp.dhis.tariffdatavalue.TariffDataValueService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveASHAPerformanceIncentiveDataValueAction implements Action
{
    private static final Log log = LogFactory.getLog( SaveASHAPerformanceIncentiveDataValueAction.class );
    
    public static final String PREFIX_DATAELEMENT = "dataelement";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private TariffDataValueService tariffDataValueService;
    
    public void setTariffDataValueService( TariffDataValueService tariffDataValueService )
    {
        this.tariffDataValueService = tariffDataValueService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private int dataSetId;
    
    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    private int orgUnitId;
    
    public void setOrgUnitId( int orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }


    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private int statusCode = 0;

    public int getStatusCode()
    {
        return statusCode;
    }  
    

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        
        OrganisationUnit organisationUnit =  organisationUnitService.getOrganisationUnit( orgUnitId );
       
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        if ( period == null )
        {
            return logError( "Illegal period identifier: " + selectedPeriodId );
        }
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
       
        List<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        String storedBy = currentUserService.getCurrentUsername();
        
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }
        
        Date now = new Date();
       
        // ---------------------------------------------------------------------
        // Add / Update data value
        // ---------------------------------------------------------------------
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        //String value = null;
        
        // Save value for Single ASHA Linked with Single facilitator
        
        //System.out.println( "inside Save performance data value");
        
        if ( dataElements != null && dataElements.size() > 0 )
        {
            for ( DataElement dataElement : dataElements )
            {

                String value = request.getParameter( PREFIX_DATAELEMENT + dataElement.getId() );
                
                if ( value != null && value.trim().length() == 0 )
                {
                    value = null;
                }

                if ( value != null )
                {
                    value = value.trim();
                }
               
                TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue( organisationUnit, dataElement, dataSet, period.getStartDate(), period.getEndDate() );
                
                if ( tariffDataValue == null )
                {
                    if ( value != null )
                    {
                        tariffDataValue = new TariffDataValue( organisationUnit, dataElement, dataSet, period.getStartDate(), period.getEndDate(), Double.parseDouble( value ), now, storedBy, null );
                        
                        tariffDataValueService.addTariffDataValue( tariffDataValue );
                    }
                }
                else
                {
                    tariffDataValue.setOrganisationUnit( organisationUnit );
                    tariffDataValue.setDataSet( dataSet );
                    tariffDataValue.setDataElement( dataElement );
                    tariffDataValue.setStartDate( period.getStartDate() );
                    tariffDataValue.setEndDate( period.getEndDate() );
                    tariffDataValue.setValue( Double.parseDouble( value ) );
                    tariffDataValue.setTimestamp( now );
                    tariffDataValue.setStoredBy( storedBy );
                    
                    tariffDataValueService.updateTariffDataValue( tariffDataValue );
                }
            }
        }
 
        
        return SUCCESS;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String logError( String message )
    {
        return logError( message, 1 );
    }

    private String logError( String message, int statusCode )
    {
        log.info( message );

        this.statusCode = statusCode;

        return SUCCESS;
    }
    
}

