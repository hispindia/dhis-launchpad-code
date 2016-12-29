
/*
function loadASHAMOApproveDataEntryForm()
{
	$( '#ASHAMOApprovePaymentDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ashaId = $( '#ashaId' ).val();
	
	//alert( programInstanceId );
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ASHAMOApprovePaymentDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    //jQuery('#loaderDiv').show();
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#ASHAMOApprovePaymentDataEntryFormDiv').load('loadASHAMOApproveDataEntryForm.action',
			{
				id:ashaId,
				selectedPeriodId:selectedPeriodId,
			}, function()
			{
				showById('ASHAMOApprovePaymentDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
				//jQuery('#loaderDiv').hide();
			});
		//hideLoader();
	}
}
*/

//save ASHA MO Approve Details
function saveASHAMOApproveDetails()
{
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ashaId = $( '#ashaId' ).val();
	
	$.ajax({
      type: "POST",
      url: 'saveASHAMOApproveDetails.action',
      data: getASHAMOApproveDetailsParamsForDiv('ASHAMOApproveForm'),
      success: function( json ) {
		
      callAction( 'selectASHA' );
      
      //loadASHABeneficiaryAndActivityAmountDetailsForm( selectedPeriodId, organisationUnitId, ashaId );
      
      //alert( selectedPeriodId + "--" + organisationUnitId + " -- " + ashaId );
		//window.location.href='getASHAFacilitatorList.action';
		//loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId )
      }
     });
}


function getASHAMOApproveDetailsParamsForDiv( formDataDiv )
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
			/*
			else if( $(this).attr('type') == 'radio' )
			{
				var checked = jQuery(this).attr('checked') ? jQuery(this).val() : null;
				params += elementId + "=" + checked + "&";
			}
			*/
			
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
		
	//alert( params );
	return params;
}

function calculateMOApproveAmount( radioButtonId, amount, falgId )
{	
	var moStatusList = document.getElementById( radioButtonId );
    var moStatusIndex = moStatusList.selectedIndex;
   
    var moStatus = moStatusList.options[moStatusIndex].value;
	
	//var moStatus = $( "#" + radioButtonId + "" ).val();
	
	//alert( moStatus  + "--"  + amount );
	
    var flagValue = document.getElementById( falgId ).value;
    if( flagValue == "1" && moStatus != "3" )
    {
    	return;
    }
    else if( flagValue == "1" && moStatus == "3" )
    {
    	document.getElementById( falgId ).value = "2";
    }
    else if( flagValue == "2" && moStatus != "3" )
	{
		document.getElementById( falgId ).value = "1";
	}
	
	if( moStatus == "3" )
	{			
		document.getElementById( "deps503" ).value = parseInt( document.getElementById( "deps503" ).value ) + parseInt( amount );
	}		
	/*
	else if( moStatus == "" || moStatus == " " )
	{
		
	}
	*/
	
	else
	{
		document.getElementById( "deps503" ).value = parseInt( document.getElementById( "deps503" ).value ) - parseInt( amount );
	}
	
	calculateApproveTotal();
	
	//document.getElementById("deps160").value = document.getElementById( "totalMOApproveAmount" ).value;
	
	
	/*
	if( document.getElementById( radioButtonId ).checked )
	{
		alert( document.getElementById( radioButtonId ).value  + "--"  + amount );
		
		if( document.getElementById( radioButtonId ).value == 1 || document.getElementById( radioButtonId ).value == 2 )
		{			
			document.getElementById( "beneficiaryTotalAmount" ).value = parseInt( document.getElementById( "beneficiaryTotalAmount" ).value ) - parseInt( amount );
		}		
		else
		{
			document.getElementById( "beneficiaryTotalAmount" ).value = parseInt( document.getElementById( "beneficiaryTotalAmount" ).value ) + parseInt( amount );
		}		
	}
	*/
	
}


function calculateMOPendingApproveAmount( radioButtonId ,amount, falgId )
{	
	var moStatusList = document.getElementById( radioButtonId );
    var moStatusIndex = moStatusList.selectedIndex;
   
    var moStatus = moStatusList.options[moStatusIndex].value;
	
	
	var flagValue = document.getElementById( falgId ).value;
    if( flagValue == "1" && moStatus != "3" )
    {
    	return;
    }
    else if( flagValue == "1" && moStatus == "3" )
    {
    	document.getElementById( falgId ).value = "2";
    }
    else if( flagValue == "2" && moStatus != "3" )
	{
		document.getElementById( falgId ).value = "1";
	}
	
	
	//alert( moStatus  + "--"  + amount );
	
	if( moStatus == "3" )
	{			
		document.getElementById( "deps502" ).value = parseInt( document.getElementById( "deps502" ).value ) + parseInt( amount );
	}		
	/*
	else if( moStatus == "" || moStatus == " " )
	{
		
	}
	*/
	
	else
	{
		document.getElementById( "deps502" ).value = parseInt( document.getElementById( "deps502" ).value ) - parseInt( amount );
	}
	
	calculateApproveTotal();
	
}

function calculateApproveTotal()
{
	var gTotal = 0;
	var tempTotal = 0;
	
	
	if( isInt( document.getElementById( "deps503" ).value ) )
	{
		var temp = document.getElementById( "deps503" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	
	if( isInt( document.getElementById( "deps502" ).value ) )
	{
		var temp = document.getElementById( "deps502" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	document.getElementById("deps504").value = gTotal;
	
}

