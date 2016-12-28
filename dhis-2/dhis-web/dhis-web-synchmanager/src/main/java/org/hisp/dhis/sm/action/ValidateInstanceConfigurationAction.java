package org.hisp.dhis.sm.action;

import org.hisp.dhis.dxf2.sm.api.SynchInstance;
import org.hisp.dhis.dxf2.sm.api.SynchInstanceService;
import org.hisp.dhis.i18n.I18n;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ValidateInstanceConfigurationAction  implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private SynchInstanceService synchInstanceService;

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    /*
    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String synchType;
    
    public String getSynchType()
    {
        return synchType;
    }
    
    
    public void setSynchType( String synchType )
    {
        this.synchType = synchType;
    }
    */
    
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }
    
    private String synchTypeAndId;
    
    public void setSynchTypeAndId( String synchTypeAndId )
    {
        this.synchTypeAndId = synchTypeAndId;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------



    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // ---------------------------------------------------------------------
        // Validating DataElement fields
        // ----------------------------------------------------------------------
        
        if ( name != null && synchTypeAndId != null )
        {
            String[] typeAndId =  synchTypeAndId.split( "##" ) ;
            
            String synchType = typeAndId[0];
            Integer instanceId = Integer.parseInt( typeAndId[1] );
            
            //System.out.println( "Inside Validate name "  + synchType + "--" + instanceId );
            
            SynchInstance instance = synchInstanceService.getInstanceByNameAndSynchType( name, synchType );

            if ( instance != null && ( instanceId == 0 || instance.getId() != instanceId ) )
            {
                message = i18n.getString( "name_in_use" );

                return INPUT;
            }
        }

        if ( url != null && synchTypeAndId != null )
        {
            String[] typeAndId =  synchTypeAndId.split( "##" ) ;
            
            String synchType = typeAndId[0];
            Integer instanceId = Integer.parseInt( typeAndId[1] );
            
            //System.out.println( "Inside Validate url "  + synchType + "--" + instanceId );
            
            SynchInstance instance = synchInstanceService.getInstanceByUrlAndSynchType( url, synchType );

            if ( instance != null && (instanceId == 0 || instance.getId() != instanceId ) )
            {
                message = i18n.getString( "url_in_use" );

                return INPUT;
            }
        }
        
        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }
}
