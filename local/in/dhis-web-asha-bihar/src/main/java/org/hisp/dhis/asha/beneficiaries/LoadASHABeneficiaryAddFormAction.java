package org.hisp.dhis.asha.beneficiaries;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */


public class LoadASHABeneficiaryAddFormAction implements Action
{
    public static final String ASHA_SERVICE_GROUP_SET = "ASHA Service Group Set";//1.0
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ConstantService constantService;
    
    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    


    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }        
    
    public String getSelectedPeriodId()
    {
        return selectedPeriodId;
    }
    
    public List<DataElementGroup> dataElementGroupList = new ArrayList<DataElementGroup>();
    
    public List<DataElementGroup> getDataElementGroupList()
    {
        return dataElementGroupList;
    }
    
    private Period period;
    
    public Period getPeriod()
    {
        return period;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        Constant serviceGroupSet = constantService.getConstantByName( ASHA_SERVICE_GROUP_SET );
        
        DataElementGroupSet dataElementGroupSet = dataElementService.getDataElementGroupSet( (int) serviceGroupSet.getValue());
        
        dataElementGroupList = new ArrayList<DataElementGroup>( dataElementGroupSet.getMembers() );
        
        /*
        Calendar calendar = Calendar.getInstance();
        
        calendar.setTime( period.getStartDate() );
        
        int monthMaxDays = calendar.getActualMaximum( Calendar.DAY_OF_MONTH );
        
        System.out.println(  " monthMaxDays -- "+ monthMaxDays );
        */
        
        return SUCCESS;
    }

}


