package org.hisp.dhis.asha.beneficiaries;

import org.hisp.dhis.beneficiary.Beneficiary;
import org.hisp.dhis.beneficiary.BeneficiaryService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class UpdateASHABeneficiaryAction
    implements Action
{
    public static final String ASHA_SERVICE_GROUP_SET = "ASHA Service Group Set";// 1.0

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

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input / OUTPUT / Getter/Setter
    // -------------------------------------------------------------------------
    
    private Integer beneficiaryId;

    public void setBeneficiaryId( Integer beneficiaryId )
    {
        this.beneficiaryId = beneficiaryId;
    }
        
    private String name1;

    public void setName1( String name1 )
    {
        this.name1 = name1;
    }

    private String fatherName1;
    
    public void setFatherName1( String fatherName1 )
    {
        this.fatherName1 = fatherName1;
    }

    private String gender1;
    
    public void setGender1( String gender1 )
    {
        this.gender1 = gender1;
    }

    private String village1;

    public void setVillage1( String village1 )
    {
        this.village1 = village1;
    }

    private String identifier1;

    public void setIdentifier1( String identifier1 )
    {
        this.identifier1 = identifier1;
    }

    private Integer dataElementGroupId1;
    
    public void setDataElementGroupId1( Integer dataElementGroupId1 )
    {
        this.dataElementGroupId1 = dataElementGroupId1;
    }

    private Integer dataElementId1;
    
    public void setDataElementId1( Integer dataElementId1 )
    {
        this.dataElementId1 = dataElementId1;
    }

    private String price1;
    
    public void setPrice1( String price1 )
    {
        this.price1 = price1;
    }

    
    private String registrationDate1;

    public void setRegistrationDate1( String registrationDate1 )
    {
        this.registrationDate1 = registrationDate1;
    }
    
    private String serviceGivenDate1;
    
    public void setServiceGivenDate1( String serviceGivenDate1 )
    {
        this.serviceGivenDate1 = serviceGivenDate1;
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


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Beneficiary beneficiary = beneficiaryService.getBeneficiaryById( beneficiaryId );
        
        Period period = beneficiary.getPeriod();
        
        beneficiary.setName( name1 );
        beneficiary.setFatherName( fatherName1 );
        beneficiary.setGender( gender1 );
        beneficiary.setVillage( village1 );
        beneficiary.setIdentifier( identifier1 );

        beneficiary.setPeriod( period );

        DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( dataElementGroupId1 );

        beneficiary.setDataElementGroup( dataElementGroup );

        DataElement dataElement = dataElementService.getDataElement( dataElementId1 );

        beneficiary.setDataElement( dataElement );

        beneficiary.setPrice( price1 );

        beneficiary.setRegistrationDate( format.parseDate( registrationDate1 ) );
        
        beneficiary.setServiceGivenDate( format.parseDate( serviceGivenDate1 )  );
        
        patient = patientService.getPatient( ashaId );

        beneficiary.setPatient( patient );

        beneficiaryService.updateBeneficiary( beneficiary );
        
        return SUCCESS;
    }

}
