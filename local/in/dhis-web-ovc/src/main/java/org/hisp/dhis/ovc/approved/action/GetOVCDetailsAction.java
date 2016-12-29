package org.hisp.dhis.ovc.approved.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ovc.util.OVCService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author BHARATH
 */

public class GetOVCDetailsAction  implements Action
{
    public static final String OVC_ID = "OVC_ID";//1085.0
    
    public static final String OVC_PROGRAM_ID = "OVC_PROGRAM_ID";// 236.0 
    public static final String SERVICE_MONITORING_FORM1A_PROGRAM_STAGE_ID = "SERVICE_MONITORING_FORM1A_PROGRAM_STAGE_ID";// 242.0
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
    
    private PatientIdentifierTypeService patientIdentifierTypeService;
    
    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private OVCService ovcService;
    
    public void setOvcService( OVCService ovcService )
    {
        this.ovcService = ovcService;
    }
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    private Integer ovcId;

    public void setOvcId( Integer ovcId )
    {
        this.ovcId = ovcId;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }
    
    private Map<Integer, String> identiferMap;
    
    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }
    
    private String systemIdentifier;
    
    public String getSystemIdentifier()
    {
        return systemIdentifier;
    }

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }
    
    private Map<String, String> visitDateMap = new HashMap<String, String>();
    
    public Map<String, String> getVisitDateMap()
    {
        return visitDateMap;
    }
    
    private List<Period> periods = new ArrayList<Period>();
    
    public List<Period> getPeriods()
    {
        return periods;
    }
    
    private List<Integer> years = new ArrayList<Integer>();
    
    public List<Integer> getYears()
    {
        return years;
    }
    
    private List<String> months = new ArrayList<String>();
    
    public List<String> getMonths()
    {
        return months;
    }
    
    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }
    
    private String tempOVCId;
    
    public String getTempOVCId()
    {
        return tempOVCId;
    }
    
    
    private Map<Integer, List<Period>> quarterlyPeriodTypeMap = new HashMap<Integer, List<Period>>();
    
    public Map<Integer, List<Period>> getQuarterlyPeriodTypeMap()
    {
        return quarterlyPeriodTypeMap;
    }



    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        patient = patientService.getPatient( ovcId );
        organisationUnit = patient.getOrganisationUnit();

        // -------------------------------------------------------------------------
        // Get PatientIdentifierType data
        // -------------------------------------------------------------------------
        
        identiferMap = new HashMap<Integer, String>();
        
        //String abc = "";
        
        PatientIdentifierType idType = null;
        
        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            idType = identifier.getIdentifierType();

            if ( idType != null )
            {
                //System.out.println( "Inside if -- " + identifier.getIdentifierType().getId()  + " : " + identifier.getIdentifier() );
                
                identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
                //abc = identifier.getIdentifier();
            }
            else
            {
                systemIdentifier = identifier.getIdentifier();
                //System.out.println( "Inside else System Id -- " + systemIdentifier );
                //abc = identifier.getIdentifier();
            }
        }
        
        //System.out.println( "Id is -- " + abc ); 
        
        Constant patientIdentifierTypeConstant = constantService.getConstantByName( OVC_ID );
        
        PatientIdentifierType identifierType = patientIdentifierTypeService.getPatientIdentifierType( (int) patientIdentifierTypeConstant.getValue() );
        
        if ( organisationUnit.getCode() != null && identifierType != null )
        {
            PatientIdentifier ovcIdIdentifier = patientIdentifierService.getPatientIdentifier( identifierType, patient );
            
            if( ovcIdIdentifier != null )
            {
                tempOVCId =  "OVC Id : " + ovcIdIdentifier.getIdentifier() ;
            }
            else
            {
                tempOVCId =  "System Generated Id: " + systemIdentifier ; 
            }
            
        }
        else
        {
            tempOVCId =  "System Generated Id: " + systemIdentifier ;
        }
        
        
        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------
        
        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService.getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute().getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getValue() );
            }
        }        
        
        
        // -------------------------------------------------------------------------
        // Get Visit Dates
        // -------------------------------------------------------------------------

        String approvedDateStr = patientAttributeValueMap.get( 405 );
        Date approvedDate = new Date();
        if( approvedDateStr == null )
        {
            approvedDate = patient.getRegistrationDate();
        }
        else
        {
            approvedDate = format.parseDate( approvedDateStr );
        }
        
        //years.add( 2013 );
        
        
        SimpleDateFormat yearFormat = new SimpleDateFormat( "yyyy" );
        int startYear = Integer.parseInt(  yearFormat.format( approvedDate ) );
        int endYear =  Integer.parseInt(  yearFormat.format(  new Date() ) );
        
        for( int i = startYear; i <= endYear; i++ )
        {
            years.add( i );
        }
       
        
        String periodType = QuarterlyPeriodType.NAME;
        
        //periodType = periodType != null && !periodType.isEmpty() ? periodType : QuarterlyPeriodType.NAME;

        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodType );

        Calendar cal = PeriodType.createCalendarInstance();
        
        for( Integer year : years )
        {
            cal.set( Calendar.YEAR, year );
            //cal.add( Calendar.YEAR, year );
            
            periods = _periodType.generatePeriods( cal.getTime() );
            
            for ( Period period : periods )
            {
                period.setName( format.formatPeriod( period ) );
                
            }
            
            Iterator<Period> periodIterator = periods.iterator();
            while( periodIterator.hasNext() )
            {
                Period p1 = periodIterator.next();
                if( p1.getEndDate().before( approvedDate ) )
                {
                    periodIterator.remove();
                }
                
                /*
                if( registrationDate.after(  p1.getStartDate() ) && registrationDate.before( p1.getEndDate() ) )
                {
                    
                }
                else
                {
                    periodIterator.remove();
                }
                */
                
            }
            
            quarterlyPeriodTypeMap.put( year, periods );
        }
        
       /*
        for( Integer year : years )
        {
            List<Period> tempPeriods = new ArrayList<Period>( quarterlyPeriodTypeMap.get( year ) );

            for ( Period period : tempPeriods )
            {
                System.out.println( year + " -- " + period.getId()  + " : " + period.getName() + " -- "+ period.getExternalId() );
            }
        }
        */
        
        
        
        Constant programConstant = constantService.getConstantByName( OVC_PROGRAM_ID );
        Constant programStageConstant = constantService.getConstantByName( SERVICE_MONITORING_FORM1A_PROGRAM_STAGE_ID );
        
        int form1AProgramId = 236;
        
        if ( programConstant != null )
        {
            form1AProgramId = (int) programConstant.getValue();
        }

        int form1AProgramStageId = 242;
        if ( programStageConstant != null )
        {
            form1AProgramStageId = (int) programStageConstant.getValue();
        }
        
       
        visitDateMap.putAll( ovcService.getQuarterlyVisitDateMap( quarterlyPeriodTypeMap, form1AProgramId, form1AProgramStageId, patient.getId() ) );
        
        
        /*
        months.add( "-01-01" );months.add( "-02-01" );
        months.add( "-03-01" );months.add( "-04-01" );
        months.add( "-05-01" );months.add( "-06-01" );
        months.add( "-07-01" );months.add( "-08-01" );
        months.add( "-09-01" );months.add( "-10-01" );
        months.add( "-11-01" );months.add( "-12-01" );
        
        periods = ovcService.getMontlyPeriods( approvedDate, new Date() );
        
        */
        
        /*
        for( Period period : periods )
        {
            System.out.println( period.getName() + " -- "+ period.getDescription() + " -- "+ period.getId() );
        }
        */
        
        
        //visitDateMap.putAll( ovcService.getVisitDateMap( periods, patient.getId() ) );
        
        return SUCCESS;
    }
}
