package org.hisp.dhis.webapi.controller.synchmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.dxf2.sm.api.ValidationRuleSynchStatusService;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ValidationRuleSynchStatusController
{
    public static final String RESOURCE_PATH = "/AccepetanceValidationRule";

    public static final String VALIDATIONRULE_STATUS_NEW = "new";
    public static final String VALIDATIONRULE_STATUS_UPDATED = "update";
    
    @Autowired
    private ValidationRuleService validationRuleService;
    
    @Autowired
    private ValidationRuleSynchStatusService validationRuleSynchStatusService;
 
    @Autowired
    private ContextUtils contextUtils;
    
    @Autowired
    private SynchInstanceService synchInstanceService;
    
    @RequestMapping( value = ValidationRuleSynchStatusController.RESOURCE_PATH + ".xml", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_METADATA_EXPORT')" )
    public void exportXml( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        List<ValidationRule> validationRules = new ArrayList<ValidationRule>();
        
        WebOptions options = new WebOptions( parameters );
        MetaData metaData = new MetaData();
        
        String clientURL = options.getOptions().get("cleintURL");
        
        SynchInstance instance = synchInstanceService.getInstanceByUrl( clientURL );
        
        if ( options.getOptions().containsKey( "status" ))
        {
            String validationRuleStatus = options.getOptions().get( "status" );
            
            if( VALIDATIONRULE_STATUS_UPDATED.equalsIgnoreCase( validationRuleStatus ) )
            {
                validationRules.addAll( validationRuleSynchStatusService.getValidationRuleByInstance( instance ) );
            }
            else
            {
                validationRules.addAll( validationRuleSynchStatusService.getNewValidationRules() );
            }
        }
        else
        {
            validationRules.addAll( validationRuleSynchStatusService.getApprovedValidationRuleByInstance( instance ) );
        }
        
        metaData.setValidationRules( validationRules );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.NO_CACHE, "metaData.xml", true );

        Class<?> viewClass = JacksonUtils.getViewClass( options.getViewClass( "export" ) );
        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, viewClass );
    }

    
}

