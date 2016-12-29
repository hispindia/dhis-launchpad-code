package org.hisp.dhis.ovc.report.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ShowAddReportFormAction implements Action
{       
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientAttributeService patientAttributeService;
    
    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------


    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();
    
    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }
    
    private List<OrganisationUnitGroup> orgUnitGroups = new ArrayList<OrganisationUnitGroup>();
    
    public List<OrganisationUnitGroup> getOrgUnitGroups()
    {
        return orgUnitGroups;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

 
    public String execute()
    {
        patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
        
        Collections.sort( patientAttributes, new IdentifiableObjectNameComparator() );
        
        orgUnitGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() );            
        Collections.sort( orgUnitGroups, new IdentifiableObjectNameComparator() );
        
        return SUCCESS;
    }
}