/**

 * 
 */

window.onload=function(){
	jQuery('#criteriaDetailsDiv').dialog({autoOpen: false});	
}


//-----------------------------------------------------------------------------
//Show Add Criteria Form
//-----------------------------------------------------------------------------

function addCriteria() {
	location.href = 'showAddCriteria.action?';
}

//-----------------------------------------------------------------------------
// Show Criteria Details
//-----------------------------------------------------------------------------

/*
function showCriteriaDetails(context) {

	jQuery.getJSON('getCriteria.action', {
		id : context.id
	}, function(json) {
		setInnerHTML('nameField', json.lookup.name);
		setInnerHTML('codeField', json.lookup.code);
		setInnerHTML('descriptionField', json.lookup.description);
		showDetails();
	});
}
*/

function showCriteriaDetails(context)
{

	jQuery('#criteriaDetailsDiv').dialog('destroy').remove();
	jQuery('<div id="criteriaDetailsDiv">' ).load( 'getCriteria.action?id='+ context.id ).dialog({
		title:  "Criteria Details" ,
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 750,
		height: 450
	});
	
}

function closeWindow()
{
	jQuery('#criteriaDetailsDiv').dialog('destroy').remove();
}






//-----------------------------------------------------------------------------
// Delete Criteria
//-----------------------------------------------------------------------------

function removeCriteria(context) 
{
	removeItem( context.id, context.name, i18n_confirm_delete, 'removeCriteria.action');
}

//-----------------------------
//Edit Criteria
//-----------------------------
function editCriteriaForm(context)
{
	location.href = 'showEditCriteria.action?id='+context.id;
}
