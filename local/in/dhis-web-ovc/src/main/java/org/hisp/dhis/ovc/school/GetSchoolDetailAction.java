package org.hisp.dhis.ovc.school;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetSchoolDetailAction implements Action
{
    
    public static final String SCHOOL_LEVEL_CONSTANT = "SCHOOL_LEVEL";// 1219
    public static final String CLASS_LEVEL_CONSTANT = "CLASS_LEVEL";// 1194
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private OVCService ovcService;
    
    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }
    /*
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    */
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

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
    
    private School school;
    
    public School getSchool()
    {
        return school;
    }
    
    private String schoolLevel;
    
    public String getSchoolLevel()
    {
        return schoolLevel;
    }

    private String classLevel;
    
    public String getClassLevel()
    {
        return classLevel;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
 

    public String execute() throws Exception
    {
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        //OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        //school = schoolService.getSchool( id );
       
        school = schoolService.getSchoolByOrganisationUnitAndName( orgUnit, schoolName );
        
        Constant schoolLevelAttributeConstant = constantService.getConstantByName( SCHOOL_LEVEL_CONSTANT );
        schoolLevel = "";
        schoolLevel = ovcService.getSchoolDataValue( school.getId(), (int) schoolLevelAttributeConstant.getValue() );
        
        Constant classLevelAttributeConstant = constantService.getConstantByName( CLASS_LEVEL_CONSTANT );
        classLevel = "";
        classLevel = ovcService.getSchoolDataValue( school.getId(), (int) classLevelAttributeConstant.getValue() );
        
        return SUCCESS;
    }
}

