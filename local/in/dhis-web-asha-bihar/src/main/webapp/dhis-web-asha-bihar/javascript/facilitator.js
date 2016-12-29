jQuery( document ).ready( function()
{
	validation( 'addASHAFacilitatorForm', function( form ){ 
		form.submit();
	}, function(){
		$("#selectedASHAList").find("option").attr("selected", "selected");
		
	});
	
		
});


jQuery( document ).ready( function()
{
	validation( 'updateASHAFacilitatorForm', function( form ){ 
		form.submit();
	}, function(){
		$("#selectedASHAList").find("option").attr("selected", "selected");
		
	});
	
		
});


//-----------------------------------------------------------------------------
//Remove Facilitator
//-----------------------------------------------------------------------------

function removeASHAFacilitator( facilitatorId, facilitatorName )
{
	removeItem( facilitatorId, facilitatorName, i18n_confirm_to_delete_facilitator, 'removeFacilitator.action' );
}