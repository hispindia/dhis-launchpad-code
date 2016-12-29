
function loadASHATrainingDataEntryForm()
{
	$( '#loadASHATrainingDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	
	var id = $( '#id' ).val();
	
	var programId = $( '#programId' ).val();
	
	var programInstanceId = $( '#programInstanceId' ).val();
	
	var selectedPeriod = $( '#selectedPeriod' ).val();
	
	if ( selectedPeriod == "" )
	{
		$( '#loadASHATrainingDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#loadASHATrainingDataEntryFormDiv').load('loadASHATrainingDetailsForm.action',
			{
				id:id,
				selectedPeriod:selectedPeriod,
				programId:programId,
				programInstanceId:programInstanceId
			}, function()
			{
				showById('loadASHATrainingDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
			});
	}
}


function saveASHATrainingDataEntryForm()
{
	$.ajax({
      type: "POST",
      url: 'saveASHATrainingDetails.action',
      data: getASHATrainingParamsForDiv('ASHATrainingDataEntryForm'),
      success: function( json ) {
		callAction( 'selectASHA' );
      }
     });
}

function getASHATrainingParamsForDiv( formDataDiv )
{
	var params = '';
	var dateOperator = '';
	jQuery("#" + formDataDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}
			
			
			else if( elementId =='dateOperator' )
			{
				dateOperator = jQuery(this).val();
			}
			else if( $(this).attr('type') != 'button' )
			{
				var value = "";
				if( jQuery(this).val()!= null && jQuery(this).val() != '' )
				{
					value = htmlEncode(jQuery(this).val());
				}
				if( dateOperator != '' )
				{
					value = dateOperator + "'" + value + "'";
					dateOperator = "";
				}
				params += elementId + "="+ value + "&";
			}
		});
		
	return params;
}




//Module 1
function selectModule1Training()
{
	
	if( document.getElementById('checkbox181').checked )
	{
		document.getElementById( "deps181" ).disabled = false;
	} 
	else
	{
		document.getElementById( "deps181" ).value = "";
		document.getElementById( "deps181" ).disabled = true;
	}  
	
}


//All Training
function selectTrainingStatus( checkBoxId, checkBoxIdforDateDe, dataElementID, dataElementIDDate, attributeID, attributeIDDate, updationDate )
{	
	var updationDate = document.getElementById( updationDate ).value;
	
	
	if( document.getElementById( checkBoxId ).checked )
	{
		var dataElementObject = document.getElementById( dataElementID );
		var dataElementValue = dataElementObject.options[ dataElementObject.selectedIndex ].value;
		
		
		document.getElementById( dataElementID ).disabled = false;
		
		document.getElementById( dataElementIDDate ).value = updationDate;
		
		document.getElementById( attributeID ).value = dataElementValue;
		document.getElementById( attributeIDDate ).value = updationDate;
		
		document.getElementById( checkBoxIdforDateDe ).value = "true";
		
		
	} 
	else
	{
		document.getElementById( dataElementID ).value = "";
		document.getElementById( dataElementID ).disabled = true;
		document.getElementById( checkBoxIdforDateDe ).value = "false";
		
		document.getElementById( dataElementIDDate ).value = "";
		
		document.getElementById( attributeID ).value = "";
		document.getElementById( attributeIDDate ).value = "";
		
	}  
	
}







// Any Other Training Status
function selectAnyOtherTrainingStatus( checkBoxId, checkBoxIdforDateDe, checkBoxIdforNameDe, dataElementID, dataElementIDDate, dataElementIDName, attributeID, attributeIDDate, attributeIDName, updationDate )
{	
	var updationDate = document.getElementById( updationDate ).value;
	
	
	if( document.getElementById( checkBoxId ).checked )
	{
		var dataElementObject = document.getElementById( dataElementID );
		var dataElementValue = dataElementObject.options[ dataElementObject.selectedIndex ].value;
		
		
		document.getElementById( dataElementID ).disabled = false;
		
		document.getElementById( dataElementIDName ).disabled = false;
		
		document.getElementById( dataElementIDDate ).value = updationDate;
		
		document.getElementById( attributeID ).value = dataElementValue;
		document.getElementById( attributeIDDate ).value = updationDate;
		document.getElementById( attributeIDName ).value = document.getElementById( dataElementIDName ).value;
		
		
		document.getElementById( checkBoxIdforDateDe ).value = "true";
		
		document.getElementById( checkBoxIdforNameDe ).value = "true";
		
		
	} 
	else
	{
		document.getElementById( dataElementID ).value = "";
		document.getElementById( dataElementID ).disabled = true;
		document.getElementById( checkBoxIdforDateDe ).value = "false";
		
		document.getElementById( dataElementIDName ).value = "";
		document.getElementById( dataElementIDName ).disabled = true;
		document.getElementById( checkBoxIdforNameDe ).value = "false";
		
		
		document.getElementById( dataElementIDDate ).value = "";
		
		document.getElementById( attributeID ).value = "";
		document.getElementById( attributeIDDate ).value = "";
		document.getElementById( attributeIDName ).value = "";
		
	}  
	
}

