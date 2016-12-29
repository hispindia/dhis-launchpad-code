package org.hisp.dhis.asha.employee;

import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowAddEmployeeFormAction implements Action
{       
    
    private final String OPTION_SET_STATE_DESIGNATION = "STATE_DESIGNATION";

    private final String OPTION_SET_DIVISION_DESIGNATION = "DIVISION_DESIGNATION";
    
    private final String OPTION_SET_DISTRICT_DESIGNATION = "DISTRICT_DESIGNATION";
    
    private final String BLOCK_DESIGNATION = "BLOCK_DESIGNATION";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private int organisationUnitId;
    
    public void setOrganisationUnitId( int organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    private int orgUnitLevel;
    
    public void setOrgUnitLevel( int orgUnitLevel )
    {
        this.orgUnitLevel = orgUnitLevel;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private OptionSet optionSet;
    
    public OptionSet getOptionSet()
    {
        return optionSet;
    }
    
    private String orientationRecived;
    
    public String getOrientationRecived()
    {
        return orientationRecived;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        optionSet = new OptionSet();
        
        orientationRecived = "false";
        
        organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
        
        //System.out.println( organisationUnit.getName() + " : " +  orgUnitLevel );
        
        // for state level designation
        if ( orgUnitLevel == 2 )
        {
            optionSet = optionService.getOptionSetByName( OPTION_SET_STATE_DESIGNATION );
        }
        
        // for Division level designation
        
        else if ( orgUnitLevel == 3 )
        {
            optionSet = optionService.getOptionSetByName( OPTION_SET_DIVISION_DESIGNATION );
        }
        
        // for District level designation
        else if ( orgUnitLevel == 4 )
        {
            optionSet = optionService.getOptionSetByName( OPTION_SET_DISTRICT_DESIGNATION );
            orientationRecived = "true";
        }
        
        // for Block level designation
        else if ( orgUnitLevel == 5 )
        {
            optionSet = optionService.getOptionSetByName( BLOCK_DESIGNATION );
            orientationRecived = "true";
        }
        
        /*
        for( String optionName : optionSet.getOptions() )
        {
            System.out.println( orgUnitLevel + " : " +  optionName );
        }
        */
        
        
        return SUCCESS;
    }
}