
function showInstanceDetails(context) 
{

	jQuery.getJSON('getInstance.action', {
		id : context.id
	}, function(json) {
		setInnerHTML('nameField', json.instance.name );
		setInnerHTML('urlField', json.instance.url );
		setInnerHTML('userField', json.instance.userId );
		//setInnerHTML('pwdField', json.instance.password );
		//setInnerHTML('lastUpdatedField', json.instance.lastUpdated );
		setInnerHTML('idField', json.instance.uid );

		showDetails();
	});
}

function removeInstance(context) 
{
	removeItem( context.id, context.name, i18n_confirm_delete, 'deleteInstance.action' );
}

function showBusinessRulesForm(context) 
{
	location.href = 'showBusinessRulesForm.action?instanceId=' + context.id;
}

function showUpdateInstanceForm(context)
{
	var synchType = $("#synchType").val();
	location.href = 'showInstanceForm.action?update=true&instanceId=' + context.id + '&synchType='+synchType;
}