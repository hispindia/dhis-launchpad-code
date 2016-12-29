package org.hisp.dhis.ovc.report.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportDesign;
import org.hisp.dhis.customreports.CustomReportFilter;
import org.hisp.dhis.customreports.CustomReportService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowUpdateReportFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private CustomReportService customReportService;
    
    public void setCustomReportService( CustomReportService customReportService )
    {
        this.customReportService = customReportService;
    }
    
   
    private PatientAttributeService patientAttributeService;
    
    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer reportId;
    
    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }
    

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<CustomReportDesign> customReportDesigns = new ArrayList<CustomReportDesign>();
    
    public List<CustomReportDesign> getCustomReportDesigns()
    {
        return customReportDesigns;
    }

    private List<CustomReportFilter> customReportFilters = new ArrayList<CustomReportFilter>();
    
    public List<CustomReportFilter> getCustomReportFilters()
    {
        return customReportFilters;
    }
    
    private CustomReport customReport;
    
    public CustomReport getCustomReport()
    {
        return customReport;
    }
    
    private List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();
    
    public List<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }
    
    private List<PatientAttribute> reoprtPatientAttributes = new ArrayList<PatientAttribute>();
    
    public List<PatientAttribute> getReoprtPatientAttributes()
    {
        return reoprtPatientAttributes;
    }

    private List<String> reoprtPersonProperties = new ArrayList<String>();
    
    public List<String> getReoprtPersonProperties()
    {
        return reoprtPersonProperties;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        reoprtPatientAttributes = new ArrayList<PatientAttribute>();
        reoprtPersonProperties = new ArrayList<String>();
        
        if( reportId != null )
        {       
            System.out.println(" Inside Update Report Form " );
            
            customReport = customReportService.getCustomReportById( reportId );
            
            customReportDesigns = new ArrayList<CustomReportDesign>( customReport.getCustomReportDesigns() );
            
            customReportFilters = new ArrayList<CustomReportFilter>( customReport.getCustomReportFilters() );
            
            System.out.println(" Custom Report is : " + customReport.getName() );
            
        }
        
        patientAttributes = new ArrayList<PatientAttribute>( patientAttributeService.getAllPatientAttributes() );
        Collections.sort( patientAttributes, new IdentifiableObjectNameComparator() );
        
        for( CustomReportDesign customReportDesign : customReportDesigns )
        {
            if( customReportDesign.getCustomType().equalsIgnoreCase( "PP" ) )
            {
                reoprtPersonProperties.add( customReportDesign.getCustomTypeValue() );
            }
            
            else if( customReportDesign.getCustomType().equalsIgnoreCase( "PA" ) )
            {
                PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( Integer.parseInt( customReportDesign.getCustomTypeValue() ) );
                reoprtPatientAttributes.add( patientAttribute );
                
                
                patientAttributes.removeAll( reoprtPatientAttributes );
                
                
                
            }
            
            System.out.println( " Custom Report Design Type : " + customReportDesign.getCustomType() + " Custom Report Design Type Value : " + customReportDesign.getCustomTypeValue() );
        }
        
        for( String reoprtPersonProperty : reoprtPersonProperties )
        {
            System.out.println( reoprtPersonProperty );
        }
        
        
        
        return SUCCESS;
    }
    
 
}
