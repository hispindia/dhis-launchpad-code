package org.hisp.dhis.de.action;

import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetClosingBalancAction implements Action 
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    
    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------
            
    private String closingBalDeId;
    
    public void setClosingBalDeId( String closingBalDeId )
    {
        this.closingBalDeId = closingBalDeId;
    }

    private String periodId;
    
    public void setPeriodId(String periodId) 
    {
        this.periodId = periodId;
    }

    private String organisationUnitId;
    
    public void setOrganisationUnitId(String organisationUnitId) 
    {
        this.organisationUnitId = organisationUnitId;
    }       
    
    private String closingBalance;

    public void setClosingBalance(String closingBalance) 
    {
        this.closingBalance = closingBalance;
    }               

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() 
    {

        //System.out.println( "Inside ClosingBalance");
        
        //System.out.println( closingBalDeId + "-- " + periodId + "-- " + organisationUnitId + "-- " + closingBalance );
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId.trim() );
        
        String storedBy = currentUserService.getCurrentUsername();

        Date now = new Date();

        String[] nextdataElementId = closingBalDeId.split("-");
        DataElement nextdataElement = dataElementService.getDataElement( nextdataElementId[0].trim() );
        DataElementCategoryOptionCombo nextoptionCombo = categoryService.getDataElementCategoryOptionCombo( nextdataElementId[1].trim() );
        
        String nextISOPeriod = null;
        
        String year = periodId.substring( 0, 4 ).trim();
        
        String month = periodId.substring( 4, 6 );
        
        //System.out.println( "year " + year +" -- month " + month );
        
        if( Integer.parseInt( month ) > 1 || Integer.parseInt( month ) < 12 )
        {               
            int nextMonth=Integer.parseInt( month ) + 1;
            
            if( nextMonth >= 1 && nextMonth <= 9 )
            {
                nextISOPeriod = year + "0"+nextMonth;
                System.out.println( " nextISOPeriod In 1 " + nextISOPeriod);
            }
            else if( nextMonth >= 10 && nextMonth <= 12 )
            {
                nextISOPeriod = year + nextMonth;
                //System.out.println( " nextISOPeriod In 2 " + nextISOPeriod);
            }
            
        }
        
        else if( Integer.parseInt( month ) == 12 )
        {
            int nextYear=Integer.parseInt( year ) + 1;
            nextISOPeriod = nextYear + "0"+month;
            
            //System.out.println( " nextISOPeriod In 3 " + nextISOPeriod);
        }
        
        //System.out.println( "Next ISO Period 4 " + nextISOPeriod );
        
        Period nextperiod = new Period();
        
        nextperiod = PeriodType.getPeriodFromIsoString( nextISOPeriod );
        
        nextperiod = periodService.reloadPeriod( nextperiod );
        
        //System.out.println( "Next ISO Period Id " + nextperiod.getId() );
        
        DataElementCategoryOptionCombo defaultAttributeOptionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
        
        DataValue nextdataValue = dataValueService.getDataValue( nextdataElement, nextperiod, organisationUnit, nextoptionCombo );
        
        if ( nextdataValue == null )
        {                       
            nextdataValue = new  DataValue( nextdataElement, nextperiod, organisationUnit, nextoptionCombo, defaultAttributeOptionCombo, closingBalance, storedBy, now, null );
            dataValueService.addDataValue( nextdataValue );
            
            //System.out.println( "Data Added for " + nextperiod.getId() );
        }
        else
        {
            nextdataValue.setValue( closingBalance );
            nextdataValue.setLastUpdated( now );
            nextdataValue.setStoredBy( storedBy );
            dataValueService.updateDataValue( nextdataValue );
            
            //System.out.println( "Data updated " + nextperiod.getId() );
        }
        
        return SUCCESS;
    }
}

