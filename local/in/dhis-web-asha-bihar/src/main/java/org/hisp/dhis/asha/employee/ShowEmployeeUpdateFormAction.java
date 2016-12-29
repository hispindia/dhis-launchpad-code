package org.hisp.dhis.asha.employee;

import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowEmployeeUpdateFormAction implements Action
{
    private final String OPTION_SET_STATE_DESIGNATION = "STATE_DESIGNATION";

    private final String OPTION_SET_DIVISION_DESIGNATION = "DIVISION_DESIGNATION";
    
    private final String OPTION_SET_DISTRICT_DESIGNATION = "DISTRICT_DESIGNATION";
    
    private final String BLOCK_DESIGNATION = "BLOCK_DESIGNATION";
    
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }
    
    private OptionService optionService;
    
    public void setOptionService( OptionService optionService )
    {
        this.optionService = optionService;
    }
    
    /*
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    */
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer empId;
    
    public void setEmpId( Integer empId )
    {
        this.empId = empId;
    }

    private Employee employee;
    
    public Employee getEmployee()
    {
        return employee;
    }
    
    private OrganisationUnit organisationUnit;
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private String orientationRecived;
    
    public String getOrientationRecived()
    {
        return orientationRecived;
    }
    
    private int orgUnitLevel;
    
    public void setOrgUnitLevel( int orgUnitLevel )
    {
        this.orgUnitLevel = orgUnitLevel;
    }
    
    private OptionSet optionSet;
    
    public OptionSet getOptionSet()
    {
        return optionSet;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
 
    public String execute()
    {
        //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        optionSet = new OptionSet();

        employee = employeeService.getEmployeeById( empId );
        
        organisationUnit = employee.getOrganisationUnit();
        
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
        
        return SUCCESS;
    }
}
