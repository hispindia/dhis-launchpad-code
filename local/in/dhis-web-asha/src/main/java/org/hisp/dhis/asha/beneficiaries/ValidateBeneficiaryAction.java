package org.hisp.dhis.asha.beneficiaries;

import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class ValidateBeneficiaryAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BeneficiaryService beneficiaryService;
    
    public void setBeneficiaryService( BeneficiaryService beneficiaryService )
    {
        this.beneficiaryService = beneficiaryService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }
    
    private String identifier;
    
    public void setIdentifier( String identifier )
    {
        this.identifier = identifier;
    }
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }       
    
    private Integer dataElementId;
    
    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private Integer beneficiaryId;
    
    public void setBeneficiaryId(Integer beneficiaryId) 
    {
		this.beneficiaryId = beneficiaryId;
    }

    public String execute() throws Exception
    {
        // ---------------------------------------------------------------------
        // Beneficiary Validation with locationCode and orgUnitId
        // ---------------------------------------------------------------------
        
        //System.out.println( " Inside Validate Beneficiary "   + " identifier :" + identifier  + "  -- dataElementId : " + dataElementId + "  -- selectedPeriodId : " + selectedPeriodId );
        
    	Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
    	
    	DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
    	Beneficiary beneficiary = beneficiaryService.getBeneficiary( identifier, dataElement, period);
        
        if ( beneficiary != null )
        {
            if ( beneficiaryId == null || ( beneficiaryId != null && beneficiary.getId() != beneficiaryId.intValue() ) )
            {
                message = "Service has been already given to the beneficiary ,please confirm";

                return INPUT;
            }
        }
        
        return SUCCESS;
    }
}

