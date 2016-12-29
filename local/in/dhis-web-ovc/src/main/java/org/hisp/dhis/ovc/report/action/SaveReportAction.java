package org.hisp.dhis.ovc.report.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.customreports.CustomReport;
import org.hisp.dhis.customreports.CustomReportDesign;
import org.hisp.dhis.customreports.CustomReportDesignService;
import org.hisp.dhis.customreports.CustomReportFilter;
import org.hisp.dhis.customreports.CustomReportFilterService;
import org.hisp.dhis.customreports.CustomReportService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
*/

public class SaveReportAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PatientAttributeService patientAttributeService;
    
    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private CustomReportService customReportService;
    
    public void setCustomReportService( CustomReportService customReportService )
    {
        this.customReportService = customReportService;
    }
    
    private CustomReportDesignService customReportDesignService;
    
    public void setCustomReportDesignService( CustomReportDesignService customReportDesignService )
    {
        this.customReportDesignService = customReportDesignService;
    }

    private CustomReportFilterService customReportFilterService;
    
    public void setCustomReportFilterService( CustomReportFilterService customReportFilterService )
    {
        this.customReportFilterService = customReportFilterService;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------
/*
    private List<String> selectedPersonProperties = new ArrayList<String>();
    
    public void setSelectedPersonProperties( List<String> selectedPersonProperties )
    {
        this.selectedPersonProperties = selectedPersonProperties;
    }

    private List<Integer> selectedPatientAttributes = new ArrayList<Integer>();
    
    public void setSelectedPatientAttributes( List<Integer> selectedPatientAttributes )
    {
        this.selectedPatientAttributes = selectedPatientAttributes;
    }
*/    
    private List<String> searchTexts = new ArrayList<String>();
    
    public void setSearchTexts( List<String> searchTexts )
    {
        this.searchTexts = searchTexts;
    }
    
    
    private String reportName;
    
    public void setReportName( String reportName )
    {
        this.reportName = reportName;
    }
    
    private Boolean reportAvailabeForAll;
    
    public void setReportAvailabeForAll( Boolean reportAvailabeForAll )
    {
        this.reportAvailabeForAll = reportAvailabeForAll;
    }
    
    private List<String> displaysSlectedPersonPropertiesAndAttributes = new ArrayList<String>();
    
    public void setDisplaysSlectedPersonPropertiesAndAttributes( List<String> displaysSlectedPersonPropertiesAndAttributes )
    {
        this.displaysSlectedPersonPropertiesAndAttributes = displaysSlectedPersonPropertiesAndAttributes;
    }
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        User user = currentUserService.getCurrentUser();
        
        //CustomReport customReport = new CustomReport();
        
        List<CustomReportDesign> customReportDesignList = new ArrayList<CustomReportDesign>();
        
        List<CustomReportFilter> customReportFilterList = new ArrayList<CustomReportFilter>();
        
        //CustomReportDesign customReportDesign = new CustomReportDesign();
        
        Integer sortOrder = 1;
        if( displaysSlectedPersonPropertiesAndAttributes != null )
        {
            for( String personPropertyAndAttribute : displaysSlectedPersonPropertiesAndAttributes )
            {
                
                
                String[] properties = personPropertyAndAttribute.split( "_" );
                
                if ( properties[0].equals( "PP" ) )
                {
                    //System.out.println(  properties[0] +  " --Order No : " + sortOrder + " -- " + personPropertyAndAttribute  ); 
                     
                    CustomReportDesign customReportDesign = new CustomReportDesign();
                    
                    customReportDesign.setCustomType( properties[0] );
                    customReportDesign.setCustomTypeValue( properties[1] );
                    customReportDesign.setSortOrder( sortOrder );
                    
                    int id = customReportDesignService.addCustomReportDesign( customReportDesign );
                    
                    CustomReportDesign reportDesign = customReportDesignService.getCustomReportDesignById( id );
                    customReportDesignList.add( reportDesign );
                }
                
                else if ( properties[0].equals( "PA" ) )
                {
                    //System.out.println(  properties[0] +  " -- Order No : " + sortOrder + " -- " + personPropertyAndAttribute  ); 
                    
                    CustomReportDesign customReportDesign = new CustomReportDesign();
                    
                    PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( Integer.parseInt( properties[1] ) );
                    customReportDesign.setCustomType( properties[0] );
                    customReportDesign.setCustomTypeValue( Integer.toString( patientAttribute.getId() )  );
                    customReportDesign.setSortOrder( sortOrder );
                    
                    int id = customReportDesignService.addCustomReportDesign( customReportDesign );
                    
                    CustomReportDesign reportDesign = customReportDesignService.getCustomReportDesignById( id );
                    customReportDesignList.add( reportDesign );
                }
                
                
                
                sortOrder++;
            }
        }       
        
        //System.out.println( reportName );
        
        //System.out.println( reportAvailabeForAll );
                
        /*
        Integer sortOrder1 = 1;
        if( selectedPersonProperties != null )
        {
            for( String personProperty : selectedPersonProperties)
            {
                CustomReportDesign customReportDesign = new CustomReportDesign();
                
                customReportDesign.setCustomType( "PP" );
                customReportDesign.setCustomTypeValue( personProperty );
                customReportDesign.setSortOrder( sortOrder1 );
                
                int id = customReportDesignService.addCustomReportDesign( customReportDesign );
                
                CustomReportDesign reportDesign = customReportDesignService.getCustomReportDesignById( id );
                customReportDesignList.add( reportDesign );
                
                sortOrder1++;
            }
        }
        
        
        if( selectedPatientAttributes != null )
        {
            for ( int i = 0; i < this.selectedPatientAttributes.size(); i++ )
            {
                CustomReportDesign customReportDesign = new CustomReportDesign();
                
                PatientAttribute patientAttribute = patientAttributeService.getPatientAttribute( selectedPatientAttributes.get( i ) );
                customReportDesign.setCustomType( "PA" );
                customReportDesign.setCustomTypeValue( patientAttribute.getUid() );
                customReportDesign.setSortOrder( sortOrder1 );
                
                int id = customReportDesignService.addCustomReportDesign( customReportDesign );
                
                CustomReportDesign reportDesign = customReportDesignService.getCustomReportDesignById( id );
                customReportDesignList.add( reportDesign );
                
                sortOrder1++;
            }
        }
        */
        
        if ( searchTexts.size() > 0 )
        {
            for( String searchText : searchTexts )
            {
                String[] keys = searchText.split( "_" );
                //System.out.println( "SearchText : " + searchText  + " : " + keys.length );
                
                CustomReportFilter customReportFilter = new CustomReportFilter();
                 
                //String id = keys[1];
                //String value = "";
                
                //if ( keys.length == 2 )
                //{
                    if ( keys[0].equals( Patient.PREFIX_IDENTIFIER_TYPE ) )
                    {
                        customReportFilter.setFilterType( "PP" );
                        customReportFilter.setFilterOperator( "like" );
                        customReportFilter.setFilterLeftString( keys[0] );
                        customReportFilter.setFilterRightString( keys[1] );
                        
                        customReportFilter.setSearchTexts( searchText );
                        
                        int id = customReportFilterService.addCustomReportFilter( customReportFilter );
                        
                        CustomReportFilter reportFilter = customReportFilterService.getCustomReportFilterById( id );
                        customReportFilterList.add( reportFilter );
                        
                    }
                    
                    //System.out.println( keys[0] + " : " + keys[1] + " : " + keys[2]  );
                //}
                
                //else if ( keys.length == 3 )
                //{
                    else if ( keys[0].equals( Patient.PREFIX_FIXED_ATTRIBUTE ) )
                    {
                        //CustomReportFilter customReportFilter = new CustomReportFilter();
                        
                        
                        if( keys[1].equalsIgnoreCase( "age" ) || keys[1].equalsIgnoreCase( "birthDate" ) )
                        {
                            customReportFilter.setFilterType( "PP" );
                            customReportFilter.setFilterOperator( "" );
                            customReportFilter.setFilterLeftString( keys[1] );
                            customReportFilter.setFilterRightString( keys[2] );
                            
                            //String operator = keys[2];
                            //customReportFilter.setFilterOperator( operator.charAt( 0 ) );
                            
                            customReportFilter.setSearchTexts( searchText );
                            
                            int id = customReportFilterService.addCustomReportFilter( customReportFilter );
                            
                            CustomReportFilter reportFilter = customReportFilterService.getCustomReportFilterById( id );
                            customReportFilterList.add( reportFilter );
                        }
                        
                        else
                        {
                            customReportFilter.setFilterType( "PP" );
                            customReportFilter.setFilterOperator( "=" );
                            customReportFilter.setFilterLeftString( keys[1] );
                            customReportFilter.setFilterRightString( keys[2] );
                            
                            customReportFilter.setSearchTexts( searchText );
                            
                            int id = customReportFilterService.addCustomReportFilter( customReportFilter );
                            
                            CustomReportFilter reportFilter = customReportFilterService.getCustomReportFilterById( id );
                            customReportFilterList.add( reportFilter );
                        }
                    }
                    
                    else if ( keys[0].equals( Patient.PREFIX_PATIENT_ATTRIBUTE ) )
                    {
                        //CustomReportFilter customReportFilter = new CustomReportFilter();
                        
                        customReportFilter.setFilterType( "PA" );
                        customReportFilter.setFilterOperator( "=" );
                        customReportFilter.setFilterLeftString( keys[1] );
                        customReportFilter.setFilterRightString( keys[2] );
                        
                        customReportFilter.setSearchTexts( searchText );
                        
                        int id = customReportFilterService.addCustomReportFilter( customReportFilter );
                        
                        CustomReportFilter reportFilter = customReportFilterService.getCustomReportFilterById( id );
                        customReportFilterList.add( reportFilter );
                    }
                    
                    //System.out.println( keys[0] + " : " + keys[1] + " : " + keys[2]  );
                //}
                
                //else if ( keys.length == 4 )
                //{
                    /*
                    else if ( keys[0].equals( Patient.PREFIX_FIXED_ATTRIBUTE ) )
                    {
                        //CustomReportFilter customReportFilter = new CustomReportFilter();
                        
                        customReportFilter.setFilterType( "PP" );
                        customReportFilter.setFilterOperator( keys[2] );
                        customReportFilter.setFilterLeftString( keys[1] );
                        customReportFilter.setFilterRightString( keys[3] );
                        
                        customReportFilter.setSearchTexts( searchText );
                        
                        int id = customReportFilterService.addCustomReportFilter( customReportFilter );
                        
                        CustomReportFilter reportFilter = customReportFilterService.getCustomReportFilterById( id );
                        customReportFilterList.add( reportFilter );
                        //customReportFilterService.addCustomReportFilter( customReportFilter );
                    }
                    */
                    
                    //System.out.println( keys[0] + " : " + keys[1] + " : " + keys[2] + " : " + keys[3] );
                //}
            }
               //customReportFilterService.addCustomReportFilter( customReportFilter );
        }
        
        
        CustomReport customReport = new CustomReport();
        
        customReport.setName( reportName );
        customReport.setDescription( reportName );
        customReport.setReportAvailable( reportAvailabeForAll );
        customReport.setReportType( "tracker" );
        customReport.setUser( user );
        customReport.setCustomReportDesigns( customReportDesignList );
        customReport.setCustomReportFilters( customReportFilterList );
        
        int customReportId = customReportService.addCustomReport( customReport );
        
        System.out.println( "Custom Report Id : " + customReportId );
        
        /*
        if( selectedPersonProperties != null )
        {
            for( String personProperty : selectedPersonProperties)
            {
                System.out.println( personProperty );
            }
        }
        
       
        if( selectedPatientAttributes != null )
        {
            List<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();
            for ( int i = 0; i < this.selectedPatientAttributes.size(); i++ )
            {
                PatientAttribute PatientAttribute = patientAttributeService.getPatientAttribute( selectedPatientAttributes.get( i ) );
                
                patientAttributes.add( PatientAttribute );
                
            }
            
            for( PatientAttribute patientAttribute : patientAttributes )
            {
                System.out.println( "Attribute Id : "  + patientAttribute.getId() + " Attribute Name is : " + patientAttribute.getName());
            }
            
        }
        
       */
        
        return SUCCESS;
    }
}

