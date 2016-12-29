jQuery( document ).ready( function()
{
    jQuery( "#name" ).focus();
    
    validation2( 'updateValidationRuleGroupForm', function( form )
    {
        //form.submit();
        if( jQuery( "#groupMembers" ).children().length > 0 )
        {
        	selectAllById( "groupMembers" );
        	selectAllById( "userRolesToAlert" );
        	form.submit();
        }
        
        else
        {
        	setHeaderDelayMessage( "Please select validation rule" );
        }        
        
        
    }, {
        'beforeValidateHandler' : function()
        {
        	selectAllById( "groupMembers" );
        	selectAllById( "userRolesToAlert" );
        },
        'rules' : getValidationRules( "validationRuleGroup" )
    } );

    checkValueIsExist( "name", "validateValidationRuleGroup.action", {
        id : getFieldValue( 'id' )
    } );
} );
