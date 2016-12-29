package org.hisp.dhis.api.controller;

import static org.hisp.dhis.api.utils.ContextUtils.CONTENT_TYPE_CSV;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Mithilesh Kumar Thakur
 */

@Controller
@RequestMapping( method = RequestMethod.GET )
public class MaharashtraTestController
{
    public static final String RESOURCE_PATH = "/MaharashtraData";
    
    @Autowired
    private DataElementService dataElementService;
    
    
    @Autowired
    private ContextUtils contextUtils;
    
    @RequestMapping( value = MaharashtraTestController.RESOURCE_PATH + ".csv", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportXml( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        Set<DataElement> dataElements = new HashSet<DataElement>();
        
        response.setContentType( CONTENT_TYPE_CSV );
        
        WebOptions options = new WebOptions( parameters );
        MetaData metaData = new MetaData();
        
        if ( options.getOptions().containsKey( "ActiveDE" ) )
        {
            String test =  options.getOptions().get("ActiveDE");
            
            if( test.equalsIgnoreCase( "abc" ))
            {
                dataElements = new HashSet<DataElement>( dataElementService.getAllActiveDataElements() );
                
                System.out.println( " Active DE List Size " + dataElements.size() );
            }
        }
        
        else
        {
            dataElements = new HashSet<DataElement>( dataElementService.getAllDataElements() );
            System.out.println( " All DE List Size " + dataElements.size() );
        }
        
        
        metaData.setDataElements( new ArrayList<DataElement>( dataElements ) );
        
        System.out.println( " Final DE is " + metaData );
        
        for( DataElement dataElement : metaData.getDataElements() )
        {
            System.out.println( " Final DE id is : " + dataElement.getId() + " Name is : " + dataElement.getName() );
        }
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, CacheStrategy.NO_CACHE, "metaData.csv", true );
        
        
        
        
        Class<?> viewClass = JacksonUtils.getViewClass( options.getViewClass( "export" ) );
        
        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, viewClass );
        
        
        
        
        
    }

}
