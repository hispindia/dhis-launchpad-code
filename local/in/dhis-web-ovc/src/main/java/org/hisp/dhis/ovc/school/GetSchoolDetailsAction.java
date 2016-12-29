package org.hisp.dhis.ovc.school;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.school.School;
import org.hisp.dhis.school.SchoolService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetSchoolDetailsAction implements Action
{
    public static final String SCHOOL_DETAILS_ATTRIBUTE_GROUP_ID = "School Details Attribute Group Id";// 1202
    
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
    
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private OVCService ovcService;
    
    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    private School school;
    
    public School getSchool()
    {
        return school;
    }

    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private PatientAttributeGroup attributeGroup;
    
    public PatientAttributeGroup getAttributeGroup()
    {
        return attributeGroup;
    }
    
    private Map<Integer, PatientAttribute> patientAttributeMap;
    
    public Map<Integer, PatientAttribute> getPatientAttributeMap()
    {
        return patientAttributeMap;
    }
    
    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();
    
    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }
    
    private Map<Integer, String> schoolDetailsValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getSchoolDetailsValueMap()
    {
        return schoolDetailsValueMap;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
 

    public String execute() throws Exception
    {
        //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        school = schoolService.getSchool( id );
       
        organisationUnit = school.getOrganisationUnit();
        
        Constant attributeConstant = constantService.getConstantByName( SCHOOL_DETAILS_ATTRIBUTE_GROUP_ID );
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroup( (int) attributeConstant.getValue() );
        
        patientAttributeMap = new HashMap<Integer, PatientAttribute>();
        
        patientAttributes = new ArrayList<PatientAttribute>();
        
        patientAttributes = new ArrayList<PatientAttribute>( attributeGroup.getAttributes() );
        
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeMap.put( patientAttribute.getId(), patientAttribute );
        }
        
        Collection<Integer> patientAttributeIds = new ArrayList<Integer>( getIdentifiers( PatientAttribute.class, patientAttributes ) );
        
        String patientAttributeIdsByComma = getCommaDelimitedString( patientAttributeIds );
        
        if( patientAttributeIds != null &&  patientAttributeIds.size() > 0 )
        {
            schoolDetailsValueMap = ovcService.getSchoolDetailValues( school.getId(), patientAttributeIdsByComma );
        }
        
        return SUCCESS;
    }
}
