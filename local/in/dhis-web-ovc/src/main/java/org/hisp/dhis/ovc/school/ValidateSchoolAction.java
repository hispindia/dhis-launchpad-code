package org.hisp.dhis.ovc.school;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ValidateSchoolAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    private String schoolName;
    
    public void setSchoolName( String schoolName )
    {
        this.schoolName = schoolName;
    }
    
    private Integer orgUnitId; 
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private Integer schoolId;
    
    public void setSchoolId( Integer schoolId )
    {
        this.schoolId = schoolId;
    }
    
    public String execute() throws Exception
    {
        // ---------------------------------------------------------------------
        // School Validation with School Name and orgUnitId
        // ---------------------------------------------------------------------
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        School school = schoolService.getSchoolByOrganisationUnitAndName( organisationUnit, schoolName );
        
        if ( school != null )
        {
            if ( schoolId == null || ( schoolId != null && school.getId() != schoolId.intValue() ) )
            {
                message = "School Already Exists, Please Specify Another Name";

                return INPUT;
            }
        }
        
        return SUCCESS;
    }
}

