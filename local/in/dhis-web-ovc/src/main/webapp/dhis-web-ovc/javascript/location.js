
//-----------------------------------------------------------------------------
//Location Details
//-----------------------------------------------------------------------------

function showLocationDetails( locationId )
{
	$('#detailsInfo').load("getLocationDetails.action", 
		{
			locationId:locationId
		}
		, function( ){
			
		}).dialog({
			title: i18n_ovc_location_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 400,
			height: 400
		});
}

//-----------------------------------------------------------------------------
//Remove Location
//-----------------------------------------------------------------------------

function removeLocation( locationId, locationName )
{
	removeItem( locationId, locationName, i18n_confirm_to_delete_location, 'removeLocation.action' );
}



//----------------------------------------------------------------------
//Validation for Location Add & Update
//----------------------------------------------------------------------

function validateAddLocation() 
{	

	var locationName = document.getElementById('name').value;
	
	//alert( locationName );
	
	var orgUnitId = document.getElementById('orgUnitId').value;
	
	//alert( orgUnitId );
	
	$.post("validateLocation.action",
		{
			locationName : locationName,
			orgUnitId : orgUnitId
		},
		function (data)
		{
			addLocationValidationCompleted(data);
		},'xml');

	return false;
}

function addLocationValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		document.forms['addLocationForm'].submit();
	} 
	else if ( type == 'input' ) 
	{
		setMessage( messageElement.firstChild.nodeValue );
	}
}


//update validation Location
function validateUpdateLocation() 
{	
	var locationId = document.getElementById('locationId').value;
	var locationName = document.getElementById('name').value;
	var orgUnitId = document.getElementById('orgUnitId').value;
	
	//alert( locationName + " -- " + locationId +  " -- " + orgUnitId );
	
	$.post("validateLocation.action",
		{
			locationId : locationId,
			locationName : locationName,
			orgUnitId : orgUnitId
		},
		function (data)
		{
			updateLocationValidationCompleted(data);
		},'xml');

	return false;
}

function updateLocationValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		document.forms['updateLocationForm'].submit();
	} 
	else if ( type == 'input' ) 
	{
		setMessage( messageElement.firstChild.nodeValue );
	}
}
