
function loadOVCHouseHoldAssessmentDataEntryForm()
{

	$( '#OVCHouseHoldAssessmentDataEntryFormDiv' ).html('');
	
	//$( '#saveButton' ).removeAttr( 'disabled' );

	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ovcId = $( '#ovcId' ).val();
	var programInstanceId = $( '#programInstanceId' ).val();
	
	//alert( programInstanceId );
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#OVCHouseHoldAssessmentDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    //jQuery('#loaderDiv').show();
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#OVCHouseHoldAssessmentDataEntryFormDiv').load('loadOVCHouseHoldAssessmentDataEntryForm.action',
			{
				id:ovcId,
				//selectedPeriodId:selectedPeriodId,
				programInstanceId:programInstanceId
			}, function()
			{
				showById('OVCHouseHoldAssessmentDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
				//jQuery('#loaderDiv').hide();
			});
		//hideLoader();
	}
}

function saveOVCHouseHoldAssessmentData()
{
	$.ajax({
      type: "POST",
      url: 'saveOVCHouseHoldAssessmentData.action',
      data: getOVCParamsForDiv('OVCHouseHoldAssessmentDataEntryForm'),
      success:validateHouseHoldDataAdd 
      
      /*
      success: function( json ) {
		//window.location.href = "getSubmittedList.action";
		//window.location.href = "getApprovedList.action";
		validateHouseHoldDataAdd(messageElement);
      }
		*/
     });
}


function validateHouseHoldDataAdd(messageElement) 
{	
	var type = jQuery(messageElement).find('message').attr('type');
	var message = jQuery(messageElement).find('message').text();
	
	if ( type == 'success' ) 
	{
		//window.location.href = "getSubmittedList.action";
		window.location.href = "selectSubmittedOVC.action";
	} 
	else if ( type == 'input' ) 
	{
		showWarningMessage( message );
	}
}


function updateOVCHouseHoldAssessmentData()
{
	$.ajax({
      type: "POST",
      url: 'updateOVCHouseHoldAssessmentData.action',
      data: getOVCParamsForDiv('updateOVCHouseHoldAssessmentDataEntryForm'),
      
      /*
      success: function( json ) {
		//window.location.href = "getSubmittedList.action";
		//window.location.href = "getApprovedList.action";
		validateHouseHoldDataUpdate(messageElement);
      }
	 */
	 success:validateHouseHoldDataUpdate 
      
     });
}


function validateHouseHoldDataUpdate(messageElement) 
{	
	
	/*
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	*/
	
	var type = jQuery(messageElement).find('message').attr('type');
	var message = jQuery(messageElement).find('message').text();
	
	if ( type == 'success' ) 
	{
		//alert( "inside success" );
		//window.location.href = "getApprovedList.action";
		loadApprovedOVCList();
	} 
	else if ( type == 'input' ) 
	{
		//alert( "inside input" );
		showWarningMessage( message );
		//alert( message );
	}
}



function getOVCParamsForDiv( formDataDiv )
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



function changeWaterSource()
{
	var waterSourceObject = document.getElementById('deps745');
	var waterSourceValue = waterSourceObject.options[ waterSourceObject.selectedIndex ].value;
	
	if( waterSourceValue == "Other(Specify)" )
	{
		showById('td746');
	} 
	else
	{
		document.getElementById( "deps746" ).value = "";
		hideById('td746');
	}  
}

function changeWaterSafe()
{
	var waterSafeObject = document.getElementById('deps747');
	var waterSafeValue = waterSafeObject.options[ waterSafeObject.selectedIndex ].value;
	
	if( waterSafeValue == "Treatment(Specify)" )
	{
		showById('td748');
		document.getElementById( "deps749" ).value = "";
		hideById('td749');
	} 
	
	else if( waterSafeValue == "Other(Specify)" )
	{
		showById('td749');
		document.getElementById( "deps748" ).value = "";
		hideById('td748');
	} 	
	
	else
	{
		document.getElementById( "deps748" ).value = "";
		document.getElementById( "deps749" ).value = "";
		hideById('td748');
		hideById('td749');
	}  
}

function changeWaterDisposal()
{
	var waterDisposalObject = document.getElementById('deps750');
	var waterDisposalValue = waterDisposalObject.options[ waterDisposalObject.selectedIndex ].value;
	
	if( waterDisposalValue == "Other(Specify)" )
	{
		showById('td751');
	} 
	else
	{
		document.getElementById( "deps751" ).value = "";
		hideById('td751');
	}  
}

function changeFoodSource()
{
	var foodSourceObject = document.getElementById('deps769');
	var foodSourceValue = foodSourceObject.options[ foodSourceObject.selectedIndex ].value;
	
	if( foodSourceValue == "Other (Specify)" )
	{
		showById('td770');
	} 
	else
	{
		document.getElementById( "deps770" ).value = "";
		hideById('td770');
	}  
}

// household main source of income
function changeSourceOfIncome()
{
	var foodSourceObject = document.getElementById('deps771');
	var foodSourceValue = foodSourceObject.options[ foodSourceObject.selectedIndex ].value;
	
	if( foodSourceValue == "Other (Specify)" )
	{
		showById('td772');
	} 
	else
	{
		document.getElementById( "deps772" ).value = "";
		hideById('td772');
	}  
}
// Livestock
function liveStock()
{
	if( document.getElementById("deps774").checked==true )
	{
		showById('td786');
	}    
	else
	{
		document.getElementById( "deps786" ).value = "";
		hideById('td786');
	}
}

//Land Size
function landSize()
{
	if( document.getElementById("deps780").checked==true )
	{
		showById('td787');
	}    
	else
	{
		document.getElementById( "deps787" ).value = "";
		hideById('td787');
	}
}

//Cellphone
/*
function cellphone()
{
	if( document.getElementById("deps775").checked==true )
	{
		showById('tdAAA');
	}    
	else
	{
		document.getElementById( "deps772" ).value = "";
		hideById('tdAAA');
	}
}
*/

//Other
function other()
{
	if( document.getElementById("deps785").checked==true )
	{
		showById('td788');
	}    
	else
	{
		document.getElementById( "deps788" ).value = "";
		hideById('td788');
	}
}

//Source of Cooking Fuel
function changeSourceOfCookingFuel()
{
	var cookingFuelObject = document.getElementById('deps789');
	var cookingFuelValue = cookingFuelObject.options[ cookingFuelObject.selectedIndex ].value;
	
	if( cookingFuelValue == "Other(Specify)" )
	{
		showById('td790');
	} 
	else
	{
		document.getElementById( "deps790" ).value = "";
		hideById('td790');
	}  
}


//House Hold Income
function changeHouseHoldIncome()
{
	var houseHoldIncomeObject = document.getElementById('deps793');
	var houseHoldIncomeValue = houseHoldIncomeObject.options[ houseHoldIncomeObject.selectedIndex ].value;
	
	if( houseHoldIncomeValue == "Other(Specify)" )
	{
		showById('td794');
	} 
	else
	{
		document.getElementById( "deps794" ).value = "";
		hideById('td794');
	}  
}

//House Hold Document
function changeHouseHoldDocument()
{
	var houseHoldDocumentObject = document.getElementById('deps796');
	var houseHoldDocumentValue = houseHoldDocumentObject.options[ houseHoldDocumentObject.selectedIndex ].value;
	
	if( houseHoldDocumentValue == "Others(Specify)" )
	{
		showById('td797');
	} 
	else
	{
		document.getElementById( "deps797" ).value = "";
		hideById('td797');
	}  
}

//Health Service
function changeHealthService()
{
	var healthServiceObject = document.getElementById('deps798');
	var healthServiceValue = healthServiceObject.options[ healthServiceObject.selectedIndex ].value;
	
	if( healthServiceValue == "Others (Specify)" )
	{
		showById('td799');
	} 
	else
	{
		document.getElementById( "deps799" ).value = "";
		hideById('td799');
	}  
}


//Health Isurance
function changeHealthiIsurance()
{
	var healthiIsuranceObject = document.getElementById('deps801');
	var healthiIsuranceValue = healthiIsuranceObject.options[ healthiIsuranceObject.selectedIndex ].value;
	
	if( healthiIsuranceValue == "Other (Specify)" )
	{
		showById('td802');
	} 
	else
	{
		document.getElementById( "deps802" ).value = "";
		hideById('td802');
	}  
}


//Legal Protection
function changeLegalProtection()
{
	var legalProtectionObject = document.getElementById('deps808');
	var legalProtectionValue = legalProtectionObject.options[ legalProtectionObject.selectedIndex ].value;
	
	if( legalProtectionValue == "Other (Specify)" )
	{
		showById('td809');
	} 
	else
	{
		document.getElementById( "deps809" ).value = "";
		hideById('td809');
	}  
}

//Receiving Support
function changeReceivingSupport()
{
	var receivingSupportObject = document.getElementById('deps810');
	var receivingSupportValue = receivingSupportObject.options[ receivingSupportObject.selectedIndex ].value;
	
	if( receivingSupportValue == "" )
	{
		document.getElementById( "deps811" ).value = "";
		hideById('td811');
	} 
	else
	{
		showById('td811');
	}  
}
