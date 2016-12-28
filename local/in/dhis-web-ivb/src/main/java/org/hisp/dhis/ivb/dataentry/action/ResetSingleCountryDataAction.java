package org.hisp.dhis.ivb.dataentry.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueAudit;
import org.hisp.dhis.datavalue.DataValueAuditService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;

import com.opensymphony.xwork2.Action;


/**
 * @author BHARATH
 */
public class ResetSingleCountryDataAction implements Action
{

    private static final Log log = LogFactory.getLog( ResetSingleCountryDataAction.class );

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
    
    private DataValueAuditService dataValueAuditService;
    
    public void setDataValueAuditService( DataValueAuditService dataValueAuditService )
    {
        this.dataValueAuditService = dataValueAuditService;
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

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    private int statusCode = 0;

    public int getStatusCode()
    {
        return statusCode;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
    
    private String comment;

    public void setComment( String comment )
    {
        this.comment = comment;
    }

    private String dataElementId;

    public void setDataElementId( String dataElementId )
    {
        this.dataElementId = dataElementId;
    }
    
    public String getDataElementId()
    {
        return dataElementId;
    }
    
    public String getOptionComboId()
    {
        return optionComboId;
    }
    
    private int organisationUnitId;

    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    public int getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    private String optionComboId;

    public void setOptionComboId( String optionComboId )
    {
        this.optionComboId = optionComboId;
    }

    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }
    public String getPeriodId()
    {
        return periodId;
    }
    
    private String conflict;
    
    public String getConflict()
    {
        return conflict;
    }

    public void setConflict( String conflict )
    {
        this.conflict = conflict;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {

        Period period = PeriodType.getPeriodFromIsoString( periodId );
        
        if ( period == null )
        {
            return logError( "Illegal period identifier: " + periodId );
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        if ( organisationUnit == null )
        {
            return logError( "Invalid organisation unit identifier: " + organisationUnitId );
        }

        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );

        if ( dataElement == null )
        {
            return logError( "Invalid data element identifier: " + dataElementId );
        }

        DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo( Integer.parseInt( optionComboId ) );

        if ( optionCombo == null )
        {
            return logError( "Invalid category option combo identifier: " + optionComboId );
        }

        String storedBy = currentUserService.getCurrentUsername();

        Date now = new Date();

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        if( value == null )
        {
            value = "";
        }        

        if ( value != null )
        {
            value = value.trim();
        }

        // ---------------------------------------------------------------------
        // Update data
        // ---------------------------------------------------------------------
        
        DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, optionCombo );
       
        if ( dataValue == null && value != null)
        {
            
                dataValue = new DataValue( dataElement, period, organisationUnit, optionCombo, optionCombo, value.trim(), storedBy, now, comment.trim()  );
                dataValue.setStatus(1);
                dataValueService.addDataValue( dataValue );
                
                DataValue dataValue1 = dataValueService.getDataValue( dataElement, period, organisationUnit, optionCombo );
                DataValueAudit dataValueAudit = new DataValueAudit( dataValue1, dataValue1.getValue(), dataValue1.getStoredBy(), dataValue1.getLastUpdated(), dataValue1.getComment(), DataValueAudit.DVA_CT_HISOTRY, DataValueAudit.DVA_STATUS_ACTIVE );                
                dataValueAuditService.addDataValueAudit( dataValueAudit );                      
        }
        else
        {
            if( !(value.trim().equalsIgnoreCase( dataValue.getValue() )) )
            {                       
                if(conflict.equalsIgnoreCase( "conflict" ) && !(dataValue.getStoredBy().equalsIgnoreCase( storedBy )) && !(dataValue.getValue().equalsIgnoreCase( value.trim() )) )
                {
                    dataValue.setFollowup( true );
                }
                else
                {
                    dataValue.setFollowup( false ); 
                }
                
                dataValue.setValue( value.trim() );
                dataValue.setComment( comment.trim() );
                dataValue.setLastUpdated( now );
                dataValue.setStoredBy( storedBy );
                dataValue.setStatus(1);                
                
                DataValueAudit dataValueAudit = dataValueAuditService.getDataValueAuditByLastUpdated_StoredBy( dataElement, organisationUnit, now, storedBy, 1, DataValueAudit.DVA_CT_HISOTRY );
                
                if( dataValueAudit == null )
                {
                    dataValueAudit = new DataValueAudit( dataValue, dataValue.getValue(), dataValue.getStoredBy(), dataValue.getLastUpdated(), dataValue.getComment(), DataValueAudit.DVA_CT_HISOTRY, DataValueAudit.DVA_STATUS_ACTIVE );     
                    dataValueAuditService.addDataValueAudit( dataValueAudit );  
                }
                else
                {
					dataValueAudit.setOrganisationUnit( dataValue.getSource() );
					dataValueAudit.setCategoryOptionCombo( dataValue.getCategoryOptionCombo() );
					dataValueAudit.setPeriod( dataValue.getPeriod() );
					dataValueAudit.setDataElement( dataValue.getDataElement() );
					
                    //dataValueAudit.setDataValue( dataValue );
                    dataValueAudit.setValue( dataValue.getValue() ); 
                    dataValueAudit.setComment( dataValue.getComment() );
                    dataValueAudit.setModifiedBy( storedBy );
                    dataValueAudit.setTimestamp( now );
                    dataValueAuditService.updateDataValueAudit( dataValueAudit );
                }
                dataValueService.updateDataValue( dataValue );
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
