// -----------------------------------------------------------------------------
// Show Indicator Group Set details
// -----------------------------------------------------------------------------

function showIndicatorGroupSetDetails( id )
{
	jQuery.post( '../dhis-web-commons-ajax-json/getIndicatorGroupSet.action',
		{ id: id }, function( json ) {
		document.getElementById('nameField').textContent = json.indicatorGroupSet.name;
		//setInnerHTML( 'nameField', json.indicatorGroupSet.name );
		setInnerHTML( 'memberCountField', json.indicatorGroupSet.memberCount );

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Delete Indicator Group Set
// -----------------------------------------------------------------------------

function deleteIndicatorGroupSet( groupSetId, groupSetName )
{
    removeItem( groupSetId, groupSetName, i18n_confirm_delete, "deleteIndicatorGroupSet.action" );
}
