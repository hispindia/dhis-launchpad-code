package org.hisp.dhis.ovc.school;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolDetails;
import org.hisp.dhis.school.SchoolDetailsService;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class RemoveSchoolAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    private SchoolDetailsService schoolDetailsService;
    
    public void setSchoolDetailsService( SchoolDetailsService schoolDetailsService )
    {
        this.schoolDetailsService = schoolDetailsService;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute()
    {
        School school = schoolService.getSchool( id );
        
        List<SchoolDetails> schoolDetails = new ArrayList<SchoolDetails>( schoolDetailsService.getSchoolDetails( school ) ) ;
        
        try
        {
            if( school != null && schoolDetails != null && schoolDetails.size()  > 0 )
            {
                schoolDetailsService.deleteSchoolDetails( school );
                schoolService.deleteSchool( school );
                
            }
            
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


