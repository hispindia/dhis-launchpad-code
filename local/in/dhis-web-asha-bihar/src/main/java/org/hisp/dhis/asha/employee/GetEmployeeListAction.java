package org.hisp.dhis.asha.employee;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.employee.Employee;
import org.hisp.dhis.employee.EmployeeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetEmployeeListAction extends ActionPagingSupport<Employee>
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
    private EmployeeService employeeService;

    public void setEmployeeService( EmployeeService employeeService )
    {
        this.employeeService = employeeService;
    }

    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private List<Employee> employeeList;

    public List<Employee> getEmployeeList()
    {
        return employeeList;
    }

    private String statusMessage;

    public String getStatusMessage()
    {
        return statusMessage;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private Integer orgUnitLevel;

    public Integer getOrgUnitLevel()
    {
        return orgUnitLevel;
    }
    
    private Integer total;

    public Integer getTotal()
    {
        return total;
    }
    
    private String key;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }
    
    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        statusMessage = "NONE";

        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        Map<Integer, Integer> orgUnitLevelMap = new HashMap<Integer, Integer>( ashaService.getOrgunitLevelMap() );
        
        orgUnitLevel = orgUnitLevelMap.get( organisationUnit.getId() );

        if ( organisationUnit == null || orgUnitLevel == 1 || orgUnitLevel > 5  )
        {
            statusMessage = "There is no Designation at this Level Please select correct organisationunit";

            return SUCCESS;
        }
        
        // OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(Integer.parseInt(orgUnitId));

        //employeeList = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnit( organisationUnit ) );
        
        employeeList = new ArrayList<Employee>( employeeService.getEmployeeByOrganisationUnitOrderByNameAsc( organisationUnit ) );
        
        if ( isNotBlank( key ) )
        {
            employeeService.searchEmployeesByName( employeeList, key );
        }
        
        this.paging = createPaging( employeeList.size() );
        employeeList = getBlockElement( employeeList, paging.getStartPos(), paging.getPageSize() );
        
        //System.out.println( organisationUnit.getName() + ": " + orgUnitLevel );
        
        return SUCCESS;
    }
}
