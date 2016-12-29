package org.hisp.dhis.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Mithilesh Kumar Thakur
 */

@Controller
@RequestMapping( value = MaharashtraTestControllerNew.RESOURCE_PATH )
public class MaharashtraTestControllerNew extends AbstractCrudController<DataElement>
{
    public static final String RESOURCE_PATH = "/MaharashtraDataNew";
    
    @Autowired
    private DataElementService dataElementService;
    
    protected List<DataElement> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<DataElement> entityList = new ArrayList<DataElement>();
        
        if ( options.getOptions().containsKey( "ActiveDE" ) )
        {
            String test =  options.getOptions().get("ActiveDE");
            
            if( test.equalsIgnoreCase( "abc" ))
            {
                entityList = new ArrayList<DataElement>( dataElementService.getAllActiveDataElements() );
                
                System.out.println( " Active DE List Size " + entityList.size() );
            }
        }
        
        else
        {
            entityList = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
            System.out.println( " All DE List Size " + entityList.size() );
        }
        
        return entityList;
    }
    
    
}
