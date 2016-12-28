package org.hisp.dhis.sm.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dxf2.metadata.ImportService;
import org.hisp.dhis.dxf2.metadata.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportTypeSummary;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.render.RenderService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.sm.util.MetaDataValidationCheck;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class TestMetaData implements Action
{
    @Autowired
    private RenderService renderService;

    @Autowired
    private MetaDataValidationCheck metaDataValidationCheck;
    
    @Autowired
    private CurrentUserService currentUserService;
    
    @Autowired
    private ImportService importService;

    
    public String execute() throws Exception        
	{
    	List<String> organisationUnitUIDList = new ArrayList<String>();
    	String organisationUnitUIDs = "QV1L9mvpcGY";
        
        if( organisationUnitUIDs != null && organisationUnitUIDs.length() > 0 )
        {
            String[] organisationUnitUIDArray = organisationUnitUIDs.split( "," );
            
            for ( String organisationUnitUid : organisationUnitUIDArray )
            {
                organisationUnitUIDList.add( organisationUnitUid );
            }
        }
        
		BufferedReader br = new BufferedReader( new FileReader( "C:\\metaData.xml" ) );
		
		String str="";
		String sCurrentLine;
		while ((sCurrentLine = br.readLine()) != null) 
		{
			str += sCurrentLine;
		}
		
		//System.out.println( str );
		 br.close();
		
		InputStream stream = new ByteArrayInputStream( str.getBytes() );
		MetaData metaData = null;
		metaData = renderService.fromXml(stream, MetaData.class);

		/*
        System.out.println( "**************OULIST FROM METADATA START*****************" );
        for( OrganisationUnit orgUnit : metaData.getOrganisationUnits() )
        {
        	System.out.println( orgUnit.getShortName() );
        }
        System.out.println( "********************OULIST FROM METADATA END****************************" );
        */
		
		MetaData synchMetaData = metaDataValidationCheck.getMetaDataOrganisationUnit(organisationUnitUIDList, metaData );
        //getMetaDataOrganisationUnit( organisationUnitUIDList, metaData );

		String importDetails;
		importDetails = "<table class=\"listTable\"><tr><th>Type</th><th>Import Count</th></tr>";

        String userUid = currentUserService.getCurrentUser().getUid();
        
        ImportSummary importSummary = importService.importMetaData( userUid, synchMetaData );
        
        //importSummary = importService.importMetaData( userUid, metaData );

        List<ImportTypeSummary> importTypeSummaryList = importSummary.getImportTypeSummaries();

        for(ImportTypeSummary importTypeSummary: importTypeSummaryList)
        {
              importTypeSummary.getType();
              importTypeSummary.getImportCount();

            importDetails = importDetails.concat("<tr><td>"+importTypeSummary.getType()+"</td><td>"+importTypeSummary.getImportCount()+"</td></tr>");
        }

        
        importDetails = importDetails.concat("</table>");

		return SUCCESS;
	}
    
    public void getMetaDataOrganisationUnit( List<String> organisationUnitUIDList, MetaData metaData )
    {
    	
        List<OrganisationUnit> organisationUnits  = metaData.getOrganisationUnits();
        
        Map<String, OrganisationUnit> orgUnitUIDMap = new HashMap<String, OrganisationUnit>();        
        for ( OrganisationUnit organisationUnit : organisationUnits )
        {
        	orgUnitUIDMap.put(organisationUnit.getUid(), organisationUnit );
        }
        
        //Set<OrganisationUnit> filteredOrganisationUnits = new HashSet<OrganisationUnit>();
       
        for ( String organisationUnitUID : organisationUnitUIDList )
        {
        	System.out.println( "\n**************** " + organisationUnitUID + " ***********************" );
        	
        	OrganisationUnit orgUnit = orgUnitUIDMap.get( organisationUnitUID );
        	
        	if( orgUnit != null )
        	{
        		System.out.print( orgUnit.getShortName() + " -> " );
        		//filteredOrganisationUnits.add( orgUnit );
        		
        		while( orgUnit.getParent() != null )
        		{
        			OrganisationUnit orgUnitParent = orgUnitUIDMap.get( orgUnit.getParent().getUid() );
        			
        			if( orgUnitParent != null )
        			{
        				//filteredOrganisationUnits.add( orgUnitParent );
        				orgUnit = orgUnitParent;
        				System.out.print( orgUnitParent.getShortName() + " -> " );
        			}
        			else
        			{
        				System.out.print( "Breaking from while........." );
        				break;
        			}
        			
        		}
        	}
        	            
        }
        
    }    
    

}
