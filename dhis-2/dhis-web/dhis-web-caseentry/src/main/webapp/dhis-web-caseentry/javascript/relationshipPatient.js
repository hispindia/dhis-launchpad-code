
//------------------------------------------------------------------------------
// Add Relationship
//------------------------------------------------------------------------------

function showAddRelationship( patientId )
{
	hideById('listRelationshipDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#addRelationshipDiv').load('showAddRelationshipForm.action',
		{
			patientId:patientId
		}, function()
		{
			showById('addRelationshipDiv');
			jQuery('#loaderDiv').hide();
		});
}

// -----------------------------------------------------------------------------
// Add Relationship Patient
// -----------------------------------------------------------------------------

function showAddRelationshipPatient( patientId, isShowPatientList )
{
	hideById( 'selectDiv' );
	hideById( 'searchPatientDiv' );
	hideById( 'listPatientDiv' );
	hideById( 'listRelationshipDiv' );
	setInnerHTML('editPatientDiv', '');
	
	jQuery('#loaderDiv').show();
	jQuery('#addRelationshipDiv').load('showAddRelationshipPatient.action',
		{
			id:patientId
		}, function()
		{
			showById('addRelationshipDiv');
			setFieldValue( 'isShowPatientList', isShowPatientList );
			jQuery('#loaderDiv').hide();
		});
}

function validateAddRelationshipPatient()
{
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addRelationshipPatientCompleted ); 
	request.sendAsPost( getParamsForDiv('addRelationshipDiv') );	
    request.send( "validateAddRelationshipPatient.action" );        

    return false;
}

function addRelationshipPatientCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	removeRelationshipDisabledIdentifier();
    	addRelationshipPatient();
    }
    else if ( type == 'error' )
    {
        showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        showWarningMessage( message );
    }
    else if( type == 'duplicate' )
    {
    	if( !checkedDuplicate )
		{
    		showListPatientDuplicate(messageElement, true);
		}
    }
}

function addRelationshipPatient()
{
	jQuery('#loaderDiv').show();
	$.ajax({
      type: "POST",
      url: 'addRelationshipPatient.action',
      data: getParamsForDiv('addRelationshipDiv'),
      success: function( json ) {
		hideById('addRelationshipDiv');
		showById('selectDiv');
		showById('searchPatientDiv');
		showById('listPatientDiv');
		jQuery('#loaderDiv').hide();
		
		if( getFieldValue( 'isShowPatientList' ) == 'false' )
		{
			showRelationshipList( getFieldValue('id') );
		}else
		{
			loadPatientList();
		}
      }
     });
    return false;
}

//remove value of all the disabled identifier fields
//an identifier field is disabled when its value is inherited from another person ( underAge is true ) 
//we don't save inherited identifiers. Only save the representative id.
function removeRelationshipDisabledIdentifier()
{
	jQuery("#addRelationshipPatientForm :input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}


//------------------------------------------------------------------------------
// Relationship partner
//------------------------------------------------------------------------------

function manageRepresentative( patientId, partnerId )
{		
	$('#relationshipDetails').dialog('destroy').remove();
	$('<div id="relationshipDetails">' ).load( 'getPartner.action', 
		{
			patientId: patientId,
			partnerId: partnerId
		}).dialog({
			title: i18n_relationship_management,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 400,
			height: 300
		});
}

function saveRepresentative( patientId, representativeId, copyAttribute )
{
	$.post( 'saveRepresentative.action', 
		{ 
			patientId:patientId, 
			representativeId: representativeId,
			copyAttribute: copyAttribute	
		}, saveRepresentativeCompleted );
}

function saveRepresentativeCompleted( messageElement )
{
	var type = $(messageElement).find('message').attr('type');
	var message = $(messageElement).find('message').text();
		
	if( type == 'success' )
	{
		jQuery('#relationshipDetails').dialog('close');
	}	
	else if( type == 'error' )
	{
		showErrorMessage( i18n_saving_representative_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		showWarningMessage( message );
	}
}

function removeRepresentative( patientId, representativeId )
{	
	$.post( 'removeRepresentative.action', 
		{ 
			patientId:patientId, 
			representativeId: representativeId 
		}, removeRepresentativeCompleted );
}

function removeRepresentativeCompleted( messageElement )
{
	var type = $(messageElement).find('message').attr('type');
	var message = $(messageElement).find('message').text();
	
	if( type == 'success' )
	{
		$('#relationshipDetails').dialog('close');
	}	
	else if( type == 'error' )
	{
		showErrorMessage( i18n_removing_representative_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		showWarningMessage( message );
	}
}



//-----------------------------------------------------------------------------
// Search Relationship Partner
//-----------------------------------------------------------------------------

function validateSearchPartner()
{	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( searchValidationCompleted );    
	request.sendAsPost(getParamsForDiv('relationshipSelectForm'));
	request.send( 'validateSearchRelationship.action' );        

	return false;
}

function searchValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if( type == 'success' )
	{
		jQuery('#loaderDiv').show();
		jQuery("#relationshipSelectForm :input").each(function()
			{
				jQuery(this).attr('disabled', 'disabled');
			});
			
		$.ajax({
			type: "POST",
			url: 'searchRelationshipPatient.action',
			data: getParamsForDiv('relationshipSelectForm'),
			success: function( json ) {
				clearListById('availablePartnersList');
				for ( i in json.patients ) 
				{
					addOptionById( 'availablePartnersList', json.patients[i].id, json.patients[i].fullName );
				} 
				
				jQuery("#relationshipSelectForm :input").each(function()
					{
						jQuery(this).removeAttr('disabled');
					});
					
				jQuery('#loaderDiv').hide();
			}
		});
		return false;
	}
	else if( type == 'error' )
	{
		showErrorMessage( i18n_searching_patient_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		showWarningMessage( message );
	}
}

function addRelationship() 
{
	var relationshipTypeId = jQuery( '#relationshipSelectForm [id=relationshipTypeId]' ).val();
	var partnerId = jQuery( '#relationshipSelectForm [id=availablePartnersList]' ).val();
	
	var relTypeId = relationshipTypeId.substr( 0, relationshipTypeId.indexOf(':') );
	var relName = relationshipTypeId.substr( relationshipTypeId.indexOf(':') + 1, relationshipTypeId.length );
	
	var url = 'saveRelationship.action?' + 
		'patientId=' + getFieldValue('patientId') + 
		'&partnerId=' + partnerId + 
		'&relationshipTypeId=' + relTypeId +
		'&relationshipName=' + relName ;
	
	jQuery('#loaderDiv').show();
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( addRelationshipCompleted );    
	request.send( url );
	
	return false;
	
}

function addRelationshipCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if( type == 'success' )
	{
		showSuccessMessage( i18n_save_success );
	}	
	else if( type == 'error' )
	{
		showErrorMessage( i18n_adding_relationship_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		showWarningMessage( message );
	}
	jQuery('#loaderDiv').hide();
}

//------------------------------------------------------------------------------
// Remove Relationship
//------------------------------------------------------------------------------

function removeRelationship( relationshipId, patientA, aIsToB, patientB )
{	
	removeItem( relationshipId, patientA + ' is ' + aIsToB + ' to ' + patientB, i18n_confirm_delete_relationship, 'removeRelationship.action' );
}