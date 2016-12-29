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
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Samta Bajpai
 * @version $Id$
 */

public class GetClosingBalancAction
    implements Action
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

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private String OpeningBalId;

    public void setOpeningBalId( String openingBalId )
    {
        OpeningBalId = openingBalId;
    }

    private String ClosingBalId;

    public void setClosingBalId( String closingBalId )
    {
        ClosingBalId = closingBalId;
    }

    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    private String organisationUnitId;

    public void setOrganisationUnitId( String organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private String closingBalance;

    public void setClosingBalance( String closingBalance )
    {
        this.closingBalance = closingBalance;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // Period period = PeriodType.createPeriodExternalId( periodId );
        // System.out.println("Next Period is :"+periodId);
        // String[] dataElementId=ClosingBalId.split("-");

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( Integer
            .parseInt( organisationUnitId.trim() ) );

        String storedBy = currentUserService.getCurrentUsername();

        // DataElement dataElement = dataElementService.getDataElement(
        // Integer.parseInt(dataElementId[0].trim()) );
        // DataElementCategoryOptionCombo optionCombo =
        // categoryService.getDataElementCategoryOptionCombo(
        // Integer.parseInt(dataElementId[1].trim()) );

        Date now = new Date();

        // DataValue dataValue = dataValueService.getDataValue(
        // organisationUnit, dataElement, period, optionCombo );

        /*
         * if ( dataValue == null ) { if ( closingBalance != null ) { dataValue
         * = new DataValue( dataElement, period, organisationUnit,
         * closingBalance, storedBy, now, null, optionCombo );
         * dataValueService.addDataValue( dataValue ); } } else {
         * dataValue.setValue( closingBalance ); dataValue.setTimestamp( now );
         * dataValue.setStoredBy( storedBy ); dataValueService.updateDataValue(
         * dataValue ); }
         */
        String[] calulatePeriod = periodId.split( "_" );
        String nextPeriod = null;
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        if ( calulatePeriod[0].equalsIgnoreCase( "forteen" ) )
        {
            String[] firstperiod = calulatePeriod[1].split( "-" );
            if ( Integer.parseInt( firstperiod[0] ) % 4 == 0 )
            {
                monthDays[1] = 29;
            }
            if ( Integer.parseInt( firstperiod[1] ) > 1 || Integer.parseInt( firstperiod[1] ) < 12 )
            {
                if ( (Integer.parseInt( firstperiod[2] ) == 1 || Integer.parseInt( firstperiod[2] ) < 15) )
                {
                    int date = Integer.parseInt( firstperiod[2] ) + 15;
                    nextPeriod = calulatePeriod[0] + "_" + firstperiod[0] + "-" + firstperiod[1] + "-" + date + "_"
                        + firstperiod[0] + "-" + firstperiod[1] + "-"
                        + monthDays[Integer.parseInt( firstperiod[1] ) - 1];
                }
                else if ( Integer.parseInt( firstperiod[2] ) >= 15 )
                {
                    int month = Integer.parseInt( firstperiod[1] ) + 1;
                    nextPeriod = calulatePeriod[0] + "_" + firstperiod[0] + "-" + month + "-01_" + firstperiod[0] + "-"
                        + month + "-15";
                }
            }
            else if ( Integer.parseInt( firstperiod[1] ) == 12 )
            {
                if ( (Integer.parseInt( firstperiod[2] ) == 1 || Integer.parseInt( firstperiod[2] ) < 15) )
                {
                    int date = Integer.parseInt( firstperiod[2] ) + 15;
                    nextPeriod = calulatePeriod[0] + "_" + firstperiod[0] + "-" + firstperiod[1] + "-" + date + "_"
                        + firstperiod[0] + "-" + firstperiod[1] + "-"
                        + monthDays[Integer.parseInt( firstperiod[1] ) - 1];
                }
                else if ( Integer.parseInt( firstperiod[2] ) >= 15 )
                {
                    int month = 1;
                    int year = Integer.parseInt( firstperiod[0] ) + 1;
                    nextPeriod = calulatePeriod[0] + "_" + year + "-" + month + "-01_" + year + "-" + month + "-15";
                }
            }
        }
        if ( calulatePeriod[0].equalsIgnoreCase( "monthly" ) )
        {
            String[] splitperiod = calulatePeriod[1].split( "-" );
            if ( Integer.parseInt( splitperiod[1] ) > 1 || Integer.parseInt( splitperiod[1] ) < 12 )
            {
                int month = Integer.parseInt( splitperiod[1] ) + 1;
                nextPeriod = calulatePeriod[0] + "_" + splitperiod[0] + "-" + month + "-" + splitperiod[2];
            }
            else if ( Integer.parseInt( splitperiod[1] ) == 12 )
            {
                int year = Integer.parseInt( splitperiod[2] ) + 1;
                nextPeriod = calulatePeriod[0] + "_" + splitperiod[0] + "-" + 1 + "-" + year;
            }
        }
        if ( calulatePeriod[0].equalsIgnoreCase( "weekly" ) )
        {
            String[] splitperiod = calulatePeriod[1].split( "-" );
            if ( Integer.parseInt( splitperiod[2] ) <= Integer
                .parseInt( monthDays[Integer.parseInt( splitperiod[1] ) - 1] + "" ) - 7 )
            {
                int day = Integer.parseInt( splitperiod[2] ) + 7;
                nextPeriod = calulatePeriod[0] + "_" + splitperiod[0] + "-" + splitperiod[1] + "-" + day;
            }
            else if ( Integer.parseInt( splitperiod[2] ) > Integer.parseInt( monthDays[Integer
                .parseInt( splitperiod[1] )] + "" ) - 7 )
            {
                int month = Integer.parseInt( splitperiod[1] ) + 1;
                nextPeriod = calulatePeriod[0] + "_" + splitperiod[0] + "-" + month + "-" + 7;
            }
        }
        if ( calulatePeriod[0].equalsIgnoreCase( "yearly" ) )
        {
            String[] splitperiod = calulatePeriod[1].split( "-" );
            int year = Integer.parseInt( splitperiod[0] ) + 1;
            nextPeriod = calulatePeriod[0] + "_" + year + "-" + splitperiod[1] + "-" + splitperiod[2];
        }

        Period nextperiodId = PeriodType.createPeriodExternalId( nextPeriod );

        String[] nextdataElementId = OpeningBalId.split( "-" );
        DataElement nextdataElement = dataElementService
            .getDataElement( Integer.parseInt( nextdataElementId[0].trim() ) );
        DataElementCategoryOptionCombo nextoptionCombo = categoryService.getDataElementCategoryOptionCombo( Integer
            .parseInt( nextdataElementId[1].trim() ) );

        DataValue nextdataValue = dataValueService.getDataValue( organisationUnit, nextdataElement, nextperiodId,
            nextoptionCombo );

        if ( nextdataValue == null )
        {
            nextdataValue = new DataValue( nextdataElement, nextperiodId, organisationUnit, closingBalance, storedBy,
                now, null, nextoptionCombo );
            dataValueService.addDataValue( nextdataValue );
        }
        else
        {
            nextdataValue.setValue( closingBalance );
            nextdataValue.setTimestamp( now );
            nextdataValue.setStoredBy( storedBy );
            dataValueService.updateDataValue( nextdataValue );
        }

        return SUCCESS;
    }
}
