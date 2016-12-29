
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showRoleDetails( roleId )
{
    jQuery.post( 'getRole.action', { id: roleId }, function ( json ) {
		document.getElementById('nameField').textContent = json.userRole.name;
		//setInnerHTML( 'nameField', json.userRole.name );
		setInnerHTML( 'membersField', json.userRole.members );
		setInnerHTML( 'dataSetsField', json.userRole.dataSets );

		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove role
// -----------------------------------------------------------------------------

function removeRole(id, role)
{
	removeItem( id, role, i18n_confirm_delete, 'removeRole.action' );
}