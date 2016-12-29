package org.hisp.dhis.ovc.school;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolDetails;
import org.hisp.dhis.school.SchoolService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class AddSchoolAction implements Action
{
    public static final String SCHOOL_DETAILS_ATTRIBUTE_GROUP_ID = "School Details Attribute Group Id";// 1202
    public static final String PREFIX_ATTRIBUTE = "attr";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private PatientAttributeOptionService patientAttributeOptionService;
    
    public void setPatientAttributeOptionService( PatientAttributeOptionService patientAttributeOptionService )
    {
        this.patientAttributeOptionService = patientAttributeOptionService;
    }
    
    private SchoolService schoolService;
    
    public void setSchoolService( SchoolService schoolService )
    {
        this.schoolService = schoolService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer orgUnitId;

    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String orgUnitName;

    public String getOrgUnitName()
    {
        return orgUnitName;
    }

    public void setOrgUnitName( String orgUnitName )
    {
        this.orgUnitName = orgUnitName;
    }

    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    private String description;
    
    public void setDescription( String description )
    {
        this.description = description;
    }
    
    private PatientAttributeGroup attributeGroup;
    private List<PatientAttribute> patientAttributes;
    
    private Integer schoolId;
    
    public Integer getSchoolId()
    {
        return schoolId;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        School school = new School( name, orgUnit );
       
        
        school.setName( name );
        school.setDescription( description );
        school.setOrganisationUnit( orgUnit );
        
        String storedBy = currentUserService.getCurrentUsername();

        Date now = new Date();

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }
       
        school.setStoredBy( storedBy );
        school.setLastUpdated( now );
        
        
        Constant attributeConstant = constantService.getConstantByName( SCHOOL_DETAILS_ATTRIBUTE_GROUP_ID );
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroup( (int) attributeConstant.getValue() );
        
        patientAttributes = new ArrayList<PatientAttribute>();
        patientAttributes = new ArrayList<PatientAttribute>( attributeGroup.getAttributes() );
        
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        String value = null;
        
        List<SchoolDetails> schoolDetailValues = new ArrayList<SchoolDetails>();
        
        // System.out.println( " Attribute Size is "   + attributes.size() );
         
        SchoolDetails schoolDetailValue = null;

         if ( patientAttributes != null && patientAttributes.size() > 0 )
         {
             for ( PatientAttribute patientAttribute : patientAttributes )
             {
                 //System.out.println( " Attribute Id is "   + attribute.getId()   + "  -- Attribute Name is : " + attribute.getName() );
                 
                 value = request.getParameter( PREFIX_ATTRIBUTE + patientAttribute.getId() );
                 
                 if ( StringUtils.isNotBlank( value ) )
                 {
                    
                     schoolDetailValue = new SchoolDetails();
                     schoolDetailValue.setSchool( school );
                     schoolDetailValue.setPatientAttribute( patientAttribute );

                     if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttribute.getValueType() ) )
                     {
                         PatientAttributeOption option = patientAttributeOptionService.get( NumberUtils.toInt( value, 0 ) );
                         if ( option != null )
                         {
                             schoolDetailValue.setValue( option.getName() );
                         }
                         else
                         {
                             
                         }
                     }
                     else
                     {
                         schoolDetailValue.setValue( value.trim() );
                     }
                     schoolDetailValues.add( schoolDetailValue );
                 }
             }
         }
  
        
         // -------------------------------------------------------------------------
         // Save School with Details
         // -------------------------------------------------------------------------

         schoolId = schoolService.addSchoolWithDetails( school, schoolDetailValues );
         
        return SUCCESS;
    }
}
