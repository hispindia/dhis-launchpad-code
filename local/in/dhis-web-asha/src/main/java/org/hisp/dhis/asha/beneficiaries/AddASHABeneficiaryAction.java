package org.hisp.dhis.asha.beneficiaries;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.asha.util.ASHAService;
import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class AddASHABeneficiaryAction implements Action
{
	private static final Log log = LogFactory.getLog( AddASHABeneficiaryAction.class );
	public static final String ASHA_SERVICE_GROUP_SET = "ASHA Service Group Set";//1.0
    
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
    
    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }
    
    private ASHAService ashaService;
    
    public void setAshaService( ASHAService ashaService )
    {
        this.ashaService = ashaService;
    }
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    
    private String fatherName;
    
    public void setFatherName( String fatherName )
    {
        this.fatherName = fatherName;
    }
    
    private String gender;
    
    public void setGender( String gender )
    {
        this.gender = gender;
    }
    
    private String village;
    
    public void setVillage( String village )
    {
        this.village = village;
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
    
    /*
    private Integer dataElementGroupId;
    
    public void setDataElementGroupId( Integer dataElementGroupId )
    {
        this.dataElementGroupId = dataElementGroupId;
    }
    
    private Integer dataElementId;
    
    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private String price;
    
    public void setPrice( String price )
    {
        this.price = price;
    }
    */
    private String registrationDate;
    
    public void setRegistrationDate( String registrationDate )
    {
        this.registrationDate = registrationDate;
    }
    
    private Integer ashaId;
    
    public void setAshaId( Integer ashaId )
    {
        this.ashaId = ashaId;
    }
    
    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }
    
    private int statusCode = 0;

    public int getStatusCode()
    {
        return statusCode;
    }
    
    private List<String> selecteddataElementsList = new ArrayList<String>();
    
    public void setSelecteddataElementsList( List<String> selecteddataElementsList )
    {
        this.selecteddataElementsList = selecteddataElementsList;
    }
    
    private String serviceGivenDate;
    
    public void setServiceGivenDate( String serviceGivenDate )
    {
        this.serviceGivenDate = serviceGivenDate;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        //Beneficiary beneficiary = new Beneficiary();
        
        Period period = PeriodType.createPeriodExternalId( selectedPeriodId );
        
        if ( period == null )
        {
            return logError( "Illegal period identifier: " + selectedPeriodId );
        }
        
        /*
        beneficiary.setName( name );
        beneficiary.setFatherName( fatherName );
        beneficiary.setGender( gender );
        beneficiary.setVillage( village );
        beneficiary.setIdentifier( identifier );
        
        beneficiary.setPeriod( period );
        
        DataElementGroup  dataElementGroup = dataElementService.getDataElementGroup( dataElementGroupId );
        
        beneficiary.setDataElementGroup( dataElementGroup );
        
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        beneficiary.setDataElement( dataElement );
        
        beneficiary.setPrice( price );
        
        beneficiary.setRegistrationDate( format.parseDate( registrationDate )  );
        
        patient = patientService.getPatient( ashaId );
        
        beneficiary.setPatient( patient );
        
        beneficiaryService.addBeneficiary( beneficiary );
        */
        
        //System.out.println(   " -- Service Given Date : " + serviceGivenDate  );
        
        //System.out.println(   " -- Service Given Date : " + format.parseDate( serviceGivenDate )  );
        
        Integer record = 1;
        
        if( selecteddataElementsList != null )
        {
            //System.out.println(   " -- Size of Record : " + selecteddataElementsList.size()  ); 
            
            for( String dataElementGroupAndDataElement : selecteddataElementsList )
            {
                Beneficiary beneficiary = new Beneficiary();
                
                beneficiary.setName( name );
                beneficiary.setFatherName( fatherName );
                beneficiary.setGender( gender );
                beneficiary.setVillage( village );
                beneficiary.setIdentifier( identifier );
                
                beneficiary.setPeriod( period );
                
                String[] deGroupAndDe = dataElementGroupAndDataElement.split( ":" );
                
                String dataElementGroupId =  deGroupAndDe[0];
                String dataElementId =  deGroupAndDe[1];
                
                DataElementGroup  deGroup = dataElementService.getDataElementGroup( Integer.parseInt( dataElementGroupId ) );
                
                beneficiary.setDataElementGroup( deGroup );
                
                DataElement de = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );
                
                beneficiary.setDataElement( de );
                
                beneficiary.setPrice( ashaService.getServiceAmount( de.getId() ) );
                
                beneficiary.setRegistrationDate( format.parseDate( registrationDate )  );
                
                beneficiary.setServiceGivenDate( format.parseDate( serviceGivenDate )  );
                
                patient = patientService.getPatient( ashaId );
                
                beneficiary.setPatient( patient );
                
                beneficiaryService.addBeneficiary( beneficiary );
                
                //System.out.println(   " -- Record Added : " + record  );
                
                //System.out.println(   " -- Service Given Date : " + format.parseDate( serviceGivenDate )  );
                
                record++;
                
            }
        }       
        
        return SUCCESS;
    }
    
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String logError( String message )
    {
        return logError( message, 1 );
    }

    private String logError( String message, int statusCode )
    {
        log.info( message );

        this.statusCode = statusCode;

        return SUCCESS;
    }
}


