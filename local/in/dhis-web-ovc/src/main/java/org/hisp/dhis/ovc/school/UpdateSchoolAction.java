package org.hisp.dhis.ovc.school;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeOption;
import org.hisp.dhis.patient.PatientAttributeOptionService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolDetails;
import org.hisp.dhis.school.SchoolDetailsService;
import org.hisp.dhis.school.SchoolService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UpdateSchoolAction implements Action
{
    public static final String SCHOOL_DETAILS_ATTRIBUTE_GROUP_ID = "School Details Attribute Group Id";// 1202
    public static final String PREFIX_ATTRIBUTE = "attr";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private SchoolDetailsService schoolDetailsService;
    
    public void setSchoolDetailsService( SchoolDetailsService schoolDetailsService )
    {
        this.schoolDetailsService = schoolDetailsService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    private Integer schoolId;
    
    public void setSchoolId( Integer schoolId )
    {
        this.schoolId = schoolId;
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
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        
        School school = schoolService.getSchool( schoolId );
        
        school.setName( name );
        
        school.setName( name );
        school.setDescription( description );
        school.setOrganisationUnit( school.getOrganisationUnit() );
        
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
        
        //List<SchoolDetails> schoolDetailValues = new ArrayList<SchoolDetails>();
        
        // System.out.println( " Attribute Size is "   + attributes.size() );
         
        schoolDetailsService.getSchoolDetails( school );
       
        List<SchoolDetails> valuesForSave = new ArrayList<SchoolDetails>();
        List<SchoolDetails> valuesForUpdate = new ArrayList<SchoolDetails>();
        Collection<SchoolDetails> valuesForDelete = null;

        SchoolDetails schoolDetailValue = null;

        if ( patientAttributes != null && patientAttributes.size() > 0 )
        {
            
            valuesForDelete = schoolDetailsService.getSchoolDetails( school );

            for ( PatientAttribute patientAttribute : patientAttributes )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE + patientAttribute.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                   
                    schoolDetailValue = schoolDetailsService.getSchoolDetail( school, patientAttribute );
                    
                    if ( schoolDetailValue == null )
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
                        valuesForSave.add( schoolDetailValue );
                    }
                    else
                    {
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
                        valuesForUpdate.add( schoolDetailValue );
                        valuesForDelete.remove( schoolDetailValue );
                    }
                }
            }
        }

        schoolService.updateSchoolWithDetails( school, valuesForSave, valuesForUpdate, valuesForDelete );
        
        return SUCCESS;
    }
}