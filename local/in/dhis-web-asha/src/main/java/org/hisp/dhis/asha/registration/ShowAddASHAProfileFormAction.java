package org.hisp.dhis.asha.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.location.Location;
import org.hisp.dhis.location.LocationService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.program.Program;

import com.opensymphony.xwork2.Action;


/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowAddASHAProfileFormAction implements Action
{
    public static final String ASHA_PROGRAM = "ASHA Program";
    public static final String ASHA_PROFILE_ATTRIBUTE = "ASHA Profile";
    public static final String PREFIX_IDENTIFIER = "iden";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitSelectionManager selectionManager;
    
    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    /*
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    */
    private PatientAttributeGroupService patientAttributeGroupService;

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }
    
    private LocationService locationService;
    
    public void setLocationService( LocationService locationService )
    {
        this.locationService = locationService;
    }
    
    /*
    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    */
    
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    /*
    private Integer orgUnitId;
    
    public void setOrgUnitId( Integer orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    */
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
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    private Map<Integer, PatientAttribute> patientAttributeMap;
    
    public Map<Integer, PatientAttribute> getPatientAttributeMap()
    {
        return patientAttributeMap;
    }
    
    private List<Location> locations = new ArrayList<Location>();
    
    public List<Location> getLocations()
    {
        return locations;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    
    public String execute()
    {
        //organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        /*
        program = programService.getProgramByName( ASHA_PROGRAM );
        
        if ( program.getOrganisationUnits().contains( organisationUnit ) )
        {
            
        }
        */
        
        locations = new ArrayList<Location> ( locationService.getActiveLocationsByParentOrganisationUnit( organisationUnit ) );
        
        attributeGroup = patientAttributeGroupService.getPatientAttributeGroupByName( ASHA_PROFILE_ATTRIBUTE );
        
        
        patientAttributeMap = new HashMap<Integer, PatientAttribute>();
        
        //List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
        List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>( attributeGroup.getAttributes() );
        for( PatientAttribute patientAttribute : patientAttributes )
        {
            patientAttributeMap.put( patientAttribute.getId(), patientAttribute );
        }
        
        
        
        /*
        System.out.println( " Attribute Size is "   + attributeGroup.getAttributes().size() );
        
        for ( PatientAttribute attribute : attributeGroup.getAttributes() )
        {
            System.out.println( " Attribute Id is "   + attribute.getId()   + "  -- Attribute Name is : " + attribute.getName() );
        }
        */
        return SUCCESS;
    }
    
    
}
