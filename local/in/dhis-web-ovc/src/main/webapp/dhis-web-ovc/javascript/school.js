

//-----------------------------------------------------------------------------
// School Details
//-----------------------------------------------------------------------------

function showSchoolDetails( schoolId )
{
	$('#detailsSchoolInfo').load("getSchoolDetails.action", 
		{
			id:schoolId
		}
		, function( ){
			
		}).dialog({
			title: i18n_ovc_school_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 400,
			height: 400
		});
}

//-----------------------------------------------------------------------------
// Remove School
//-----------------------------------------------------------------------------

function removeSchool( schoolId, schoolName )
{
	removeItem( schoolId, schoolName, i18n_confirm_to_delete_school, 'removeSchool.action' );
}





//----------------------------------------------------------------------
//Validation for School Add & Update
//----------------------------------------------------------------------

function validateAddSchool() 
{	

	var schoolName = document.getElementById('name').value;
	
	var orgUnitId = document.getElementById('orgUnitId').value;
	
	//alert( orgUnitId );
	
	$.post("validateSchool.action",
		{
			schoolName : schoolName,
			orgUnitId : orgUnitId
		},
		function (data)
		{
			addSchoolValidationCompleted(data);
		},'xml');

	return false;
}

function addSchoolValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		document.forms['addSchoolForm'].submit();
	} 
	else if ( type == 'input' ) 
	{
		setMessage( messageElement.firstChild.nodeValue );
	}
}


//update validation School
function validateUpdateSchool() 
{	
	var schoolId = document.getElementById('schoolId').value;
	var schoolName = document.getElementById('name').value;
	var orgUnitId = document.getElementById('orgUnitId').value;
	
	//alert( locationName + " -- " + locationId +  " -- " + orgUnitId );
	
	$.post("validateSchool.action",
		{
			schoolId : schoolId,
			schoolName : schoolName,
			orgUnitId : orgUnitId
		},
		function (data)
		{
			updateSchoolValidationCompleted(data);
		},'xml');

	return false;
}

function updateSchoolValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		document.forms['updateSchoolForm'].submit();
	} 
	else if ( type == 'input' ) 
	{
		setMessage( messageElement.firstChild.nodeValue );
	}
}

//next and pre periods
function getAvailablePeriodsTemp( availablePeriodsId, selectedPeriodsId, year )
{	
	$( '#schoolDetailsDataEntryFormDiv' ).html( '' );

	var availableList = document.getElementById( availablePeriodsId );
	var selectedList = document.getElementById( selectedPeriodsId );
	
	clearList( selectedList );
	
	addOptionToList( selectedList, '-1', '[Please Select]' );
	
	$.getJSON( "getAvailableNextPrePeriods.action", {
		"year": year },
		function( json ) {
			
			for ( i in json.periods ) {
	    		//addOptionToList( list, i, json.periods[i].name );
	    		addOptionToList( selectedList, json.periods[i].externalId, json.periods[i].name );
	    	}
			
		} );
}





