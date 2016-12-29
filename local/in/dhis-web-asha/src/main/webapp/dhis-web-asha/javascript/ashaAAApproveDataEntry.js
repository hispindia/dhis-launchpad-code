
/*
function loadASHAAAApproveDataEntryForm()
{
	$( '#ASHAAAApprovePaymentDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ashaId = $( '#ashaId' ).val();
	
	//alert( programInstanceId );
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ASHAAAApprovePaymentDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    //jQuery('#loaderDiv').show();
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#ASHAAAApprovePaymentDataEntryFormDiv').load('loadASHAAAApproveDataEntryForm.action',
			{
				id:ashaId,
				selectedPeriodId:selectedPeriodId,
			}, function()
			{
				showById('ASHAAAApprovePaymentDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
				//jQuery('#loaderDiv').hide();
			});
		//hideLoader();
	}
}

*/

//save ASHA AA Approve Details
function saveASHAAAApproveDetails()
{
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ashaId = $( '#ashaId' ).val();
	
	$.ajax({
      type: "POST",
      url: 'saveASHAAAApproveDetails.action',
      data: getASHAAAApproveDetailsParamsForDiv('ASHAAAApproveForm'),
      success: function( json ) {
		
      callAction( 'selectASHA' );
      
      //loadASHABeneficiaryAndActivityAmountDetailsForm( selectedPeriodId, organisationUnitId, ashaId );
      
      //alert( selectedPeriodId + "--" + organisationUnitId + " -- " + ashaId );
		//window.location.href='getASHAFacilitatorList.action';
		//loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId )
      }
     });
}


function getASHAAAApproveDetailsParamsForDiv( formDataDiv )
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


function calculateAAApproveAmount( radioButtonId ,amount )
{	
	var moStatusList = document.getElementById( radioButtonId );
    var moStatusIndex = moStatusList.selectedIndex;
   
    var aaStatus = moStatusList.options[moStatusIndex].value;
	
	if( aaStatus == "3" )
	{			
		document.getElementById( "deps505" ).value = parseInt( document.getElementById( "deps505" ).value ) + parseInt( amount );
	}		

	else
	{
		document.getElementById( "deps505" ).value = parseInt( document.getElementById( "deps505" ).value ) - parseInt( amount );
	}
	
	calculateAAApproveTotal();
	
}


function calculateAAPendingApproveAmount( radioButtonId ,amount )
{	
	var moStatusList = document.getElementById( radioButtonId );
    var moStatusIndex = moStatusList.selectedIndex;
   
    var aaStatus = moStatusList.options[moStatusIndex].value;
	
	
	if( aaStatus == "3" )
	{			
		document.getElementById( "deps506" ).value = parseInt( document.getElementById( "deps506" ).value ) + parseInt( amount );
	}		

	else
	{
		document.getElementById( "deps506" ).value = parseInt( document.getElementById( "deps506" ).value ) - parseInt( amount );
	}
	
	calculateAAApproveTotal();
	
}

function calculateAAApproveTotal()
{
	var gTotal = 0;
	var tempTotal = 0;
	
	
	if( isInt( document.getElementById( "deps505" ).value ) )
	{
		var temp = document.getElementById( "deps505" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	
	if( isInt( document.getElementById( "deps506" ).value ) )
	{
		var temp = document.getElementById( "deps506" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	document.getElementById("deps507").value = gTotal;
	
}



