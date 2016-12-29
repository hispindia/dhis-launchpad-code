package org.hisp.dhis.asha.facilitator;

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.facilitator.Facilitator;
import org.hisp.dhis.facilitator.FacilitatorService;
import org.hisp.dhis.i18n.I18n;
import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class RemoveASHAFacilitatorAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private FacilitatorService facilitatorService;
    
    public void setFacilitatorService( FacilitatorService facilitatorService )
    {
        this.facilitatorService = facilitatorService;
    }
    

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        try
        {
            Facilitator facilitator = facilitatorService.getFacilitator( id );
            
            if( facilitator != null )
            {
                facilitator.getPatients().clear();
            }
            
            facilitatorService.deleteFacilitator( facilitator );
            
        }
        
        catch ( DataIntegrityViolationException ex )
        {
            message = i18n.getString( "object_not_deleted_associated_by_objects" );

            return ERROR;
        }
        
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();

                return ERROR;
            }
        }
        
        return SUCCESS;
    }
}
