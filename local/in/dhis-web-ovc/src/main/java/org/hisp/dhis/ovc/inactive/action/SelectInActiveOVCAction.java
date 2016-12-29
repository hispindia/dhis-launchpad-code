package org.hisp.dhis.ovc.inactive.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.comparator.ProgramDisplayNameComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class SelectInActiveOVCAction implements Action
{
    public static final String OVC_MONTHLY_VISIT = "OVC Monthly Visit";
    public static final String OVC_STATUS = "OVC Status";//301.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private PatientService patientService;
    
    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    
    /*
    private Collection<PatientAttribute> patientAttributes;

    public Collection<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }
    */
    
    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();
    
    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }    
    
    
    private List<Program> programs;

    public List<Program> getPrograms()
    {
        return programs;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    
    private int status;

    public int getStatus()
    {
        return status;
    }
    
    private String ovcStatus;
    
    public String getOvcStatus()
    {
        return ovcStatus;
    }
    
    private Program program;

    public Program getProgram()
    {
        return program;
    }
    
    private Integer total;

    public Integer getTotal()
    {
        return total;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        ovcStatus = i18n.getString( "none" );
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        program = programService.getProgramByName( OVC_MONTHLY_VISIT );
        
        if ( ( organisationUnit == null ) || ( !program.getOrganisationUnits().contains( organisationUnit ) ) )
        {
            
            ovcStatus = i18n.getString( "please_select_cbo" );

            return SUCCESS;
        }
        
        List<String> searchTexts = new ArrayList<String>();
        
        searchTexts.add( "attr_301_inactive" );

        total = patientService.countSearchPatients( searchTexts, organisationUnit, null );
        
        //patientAttributes = patientAttributeService.getAllPatientAttributes();
        
        patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
        Collections.sort( patientAttributes, new IdentifiableObjectNameComparator() );
        
        Constant ovcStatusonstant = constantService.getConstantByName( OVC_STATUS );
        
        Iterator<PatientAttribute> attributeIterator = patientAttributes.iterator();
        while( attributeIterator.hasNext() )
        {
            PatientAttribute attribute = attributeIterator.next();
            
            if ( attribute.getId() == (int) ovcStatusonstant.getValue() )
            {
                attributeIterator.remove( );
            }
        }
        
        programs = new ArrayList<Program>(programService.getAllPrograms() );
        programs.removeAll( programService.getPrograms( Program.SINGLE_EVENT_WITHOUT_REGISTRATION ) );
        
        Collections.sort( programs, new ProgramDisplayNameComparator() );

        organisationUnit = selectionManager.getSelectedOrganisationUnit();

        return SUCCESS;
    }
}
