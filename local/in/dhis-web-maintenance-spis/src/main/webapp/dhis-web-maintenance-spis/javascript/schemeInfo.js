


//-----------------------------------------------------------------------------
// Update Scheme Information
//-----------------------------------------------------------------------------

function showUpdateSchemeInfoForm( context ) 
{
  location.href = 'showUpdateSchemeInfoForm.action?selectedProgramId=' + context.id;
}










//-----------------------------------------------------------------------------
//Load Facility Data Entry Form
//-----------------------------------------------------------------------------

function loadSchemeAttributesForm()
{
	
	$( '#schemeAttributesFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );
	
	var selectedProgramId = $( '#selProgramId' ).val();
	
	if ( selectedProgramId == "-1" )
	{
		$( '#schemeAttributesFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
		jQuery('#schemeAttributesFormDiv').load('loadSchemeDetailsEntryForm.action',
			{
				selectedProgramId:selectedProgramId
			}, function()
			{
				showById('schemeAttributesFormDiv');
				
			});
	}

}

/*
function selectAllById( listId ) 
{
    $('#' + listId).find('option').attr('selected', true);
}
*/

