
function loadASHADrugKitIECDataEntryForm()
{
	$( '#loadASHADrugKitIECDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	var id = $( '#id' ).val();
	var programId = $( '#programId' ).val();
	var programInstanceId = $( '#programInstanceId' ).val();
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}

	else
	{
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#loadASHADrugKitIECDataEntryFormDiv').load('loadASHADrugKitIECDataEntryForm.action',
			{
				id:id,
				selectedPeriodId:selectedPeriodId,
				programId:programId,
				programInstanceId:programInstanceId
			}, function()
			{
				showById('loadASHADrugKitIECDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
			});
	}
}





function saveASHADrugKitIECDataEntryForm()
{
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	$.ajax({
      type: "POST",
      url: 'saveASHADrugKitIECDetails.action',
      data: getASHADrugKitIECParamsForDiv('ASHADrugKitIECDataEntryForm'),
      success: function( json ) {
		callAction( 'selectASHA' );
		
		//window.location.href='getASHAFacilitatorList.action';
		//loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId )
      }
     });
}

function getASHADrugKitIECParamsForDiv( formDataDiv )
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



function validateReceivedStatus( receivedId, replenishmentId, dateDe )
{	
	
	var receiveObject = document.getElementById( receivedId );
	var receiveValue = receiveObject.options[ receiveObject.selectedIndex ].value;
	
	
	var replenishmentObject = document.getElementById( replenishmentId );
	var replenishmentValue = replenishmentObject.options[ replenishmentObject.selectedIndex ].value;
	
	
	if( replenishmentValue ==null || replenishmentValue == '' )
	{
		document.getElementById( replenishmentId ).disabled = true;
	}
	
	if( dateDe ==null || dateDe == '' )
	{
		document.getElementById( dateDe ).disabled = true;
	}
	
	
	
	if( receiveValue == "true" || receiveValue == "Yes" )
	{
		document.getElementById( replenishmentId ).disabled = false;
	} 
	
	else
	{
		document.getElementById( replenishmentId ).value = "";
		document.getElementById( replenishmentId ).disabled = true;
		
		document.getElementById( dateDe ).value = "";
		document.getElementById( dateDe ).disabled = true;
		
	}  
	
}




function validateReplenishmentStatus( receivedId, replenishmentId, dateDe )
{	
	
	var replenishmentObject = document.getElementById( replenishmentId );
	var replenishmentValue = replenishmentObject.options[ replenishmentObject.selectedIndex ].value;
	
	
	if( replenishmentValue ==null || replenishmentValue == '' )
	{
		document.getElementById( replenishmentId ).disabled = true;
	}
	
	if( dateDe ==null || dateDe == '' )
	{
		document.getElementById( dateDe ).disabled = true;
	}
	
	
	if( replenishmentValue == "true" || replenishmentValue == "Yes" )
	{
		document.getElementById( dateDe ).disabled = false;
	} 
	
	else
	{
		document.getElementById( dateDe ).value = "";
		document.getElementById( dateDe ).disabled = true;
		
	}  
	
}


//All DrugKit Calculation
function calculateClosingBalance( openingBalance, replenished, consumed, unusable, closingBalance , nextPeriodDe )
{	
	
	var closeBal = 0;
	
	var sumValue = 0;
	
	var diffValue = 0;
	
	
	if($("#"+openingBalance).val()==null || $("#"+openingBalance).val()=='')
	{
		$("#"+openingBalance).val('0');
	}
	
	if($("#"+replenished).val()==null || $("#"+replenished).val()=='')
	{
		$("#"+replenished).val('0');
	}
	
	if($("#"+consumed).val()==null || $("#"+consumed).val()=='')
	{
		$("#"+consumed).val('0');
	}	
	

	if($("#"+unusable).val()==null || $("#"+unusable).val()=='')
	{
		$("#"+unusable).val('0');
	}	
		
	
	sumValue = parseInt( document.getElementById( openingBalance ).value ) + parseInt( document.getElementById( replenished ).value );
	diffValue = parseInt( document.getElementById( consumed ).value ) + parseInt( document.getElementById( unusable ).value );
	
	closeBal =  parseInt( sumValue ) - parseInt( diffValue );
	
	//closeBal = parseInt( document.getElementById( openingBalance ).value ) + parseInt( document.getElementById( replenished ).value ) - parseInt( document.getElementById( consumed ).value ) - parseInt( document.getElementById( unusable ).value );
	
	
	//$("#"+closingBalance).val((parseInt($("#"+openingBalance).val())+parseInt($("#"+replenished).val()))-( parseInt($("#"+consumed).val()) + parseInt($("#"+unusable).val()) )+"");
	
	document.getElementById( closingBalance ).value = closeBal;
	
	if( document.getElementById( nextPeriodDe ) != null )
	{
		document.getElementById( nextPeriodDe ).value = closeBal;
	}
		
}


// Validate IECMaterial Received Status
function validateIECMaterialReceivedStatus( itemReceivedId, quanitityId, issueDateDe )
{	
	
	var itemObject = document.getElementById( itemReceivedId );
	var itemValue = itemObject.options[ itemObject.selectedIndex ].value;
	
	
	if( itemValue == "true" || itemValue == "Yes" )
	{
		document.getElementById( quanitityId ).disabled = false;
		document.getElementById( issueDateDe ).disabled = false;
	} 
	
	else
	{
		document.getElementById( quanitityId ).value = "";
		document.getElementById( quanitityId ).disabled = true;
		
		document.getElementById( issueDateDe ).value = "";
		document.getElementById( issueDateDe ).disabled = true;
		
	}  
	
}


//Validate Item Received Status
function validateItemReceived( itemReceivedId, workingStatusId, issuedDeId , reIssueDateId )
{	
	
	var itemReceivedObject = document.getElementById( itemReceivedId );
	var itemReceivedValue = itemReceivedObject.options[ itemReceivedObject.selectedIndex ].value;
	
	if( itemReceivedValue == "true" || itemReceivedValue == "Yes" )
	{
		document.getElementById( workingStatusId ).disabled = false;
		
		//document.getElementById( workingStatusId ).disabled = false;
		//document.getElementById( issuedDeId ).disabled = false;
	} 
	
	else
	{
		document.getElementById( workingStatusId ).value = "";
		document.getElementById( workingStatusId ).disabled = true;
		
		document.getElementById( issuedDeId ).value = "";
		document.getElementById( issuedDeId ).disabled = true;
		
		document.getElementById( reIssueDateId ).value = "";
		document.getElementById( reIssueDateId ).disabled = true;
		
	}  
	
}



//Validate Item Working  Status
function validateItemWorkingStatus( itemReceivedId, workingStatusId, issuedDeId , reIssueDateId )
{	
	
	var itemReceivedObject = document.getElementById( itemReceivedId );
	var itemReceivedValue = itemReceivedObject.options[ itemReceivedObject.selectedIndex ].value;
	
	var itemWorkingObject = document.getElementById( workingStatusId );
	var itemWorkingValue = itemWorkingObject.options[ itemWorkingObject.selectedIndex ].value;
	
	
	
	if( ( itemReceivedValue == "Yes" || itemReceivedValue == "true" ) && ( itemWorkingValue == "Non Functional" )   )
	{
		document.getElementById( issuedDeId ).disabled = false;
		
		//document.getElementById( workingStatusId ).disabled = false;
		//document.getElementById( issuedDeId ).disabled = false;
	} 
	
	else
	{
		
		document.getElementById( issuedDeId ).value = "";
		document.getElementById( issuedDeId ).disabled = true;
		
		document.getElementById( reIssueDateId ).value = "";
		document.getElementById( reIssueDateId ).disabled = true;
		
	}  
	
}



//Validate Item Functional Status
function validateItemFunctionalStatus( itemReceivedId, workingStatusId, issuedDeId , reIssueDateId )
{	
	
	var itemReceivedObject = document.getElementById( itemReceivedId );
	var itemReceivedValue = itemReceivedObject.options[ itemReceivedObject.selectedIndex ].value;
	
	var itemWorkingObject = document.getElementById( workingStatusId );
	var itemWorkingValue = itemWorkingObject.options[ itemWorkingObject.selectedIndex ].value;
	
	var itemFunctionalObject = document.getElementById( issuedDeId );
	var itemFunctionalValue = itemFunctionalObject.options[ itemFunctionalObject.selectedIndex ].value;
	
	if( ( itemReceivedValue == "Yes" || itemReceivedValue == "true" )  && ( itemWorkingValue == "Non Functional" ) && ( itemFunctionalValue == "Yes" || itemFunctionalValue == "true" )  )
	{
		document.getElementById( reIssueDateId ).disabled = false;
		
		//document.getElementById( workingStatusId ).disabled = false;
		//document.getElementById( issuedDeId ).disabled = false;
	} 
	
	else
	{
		document.getElementById( reIssueDateId ).value = "";
		document.getElementById( reIssueDateId ).disabled = true;
		
	}  
	
}


