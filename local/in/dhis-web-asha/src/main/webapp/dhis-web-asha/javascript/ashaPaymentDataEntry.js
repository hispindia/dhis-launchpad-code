function loadASHAPaymentDataEntryForm()
{
	$( '#ASHAPaymentDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ashaId = $( '#ashaId' ).val();
	var programInstanceId = $( '#programInstanceId' ).val();
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ASHAPaymentDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#ASHAPaymentDataEntryFormDiv').load('loadASHAPaymentDetailsForm.action',
			{
				id:ashaId,
				selectedPeriodId:selectedPeriodId,
				programInstanceId:programInstanceId
			}, function()
			{
				showById('ASHAPaymentDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
			});
	}
}


function saveASHAPaymentDetails()
{
	$.ajax({
      type: "POST",
      url: 'saveASHAPaymentDetails.action',
      data: getASHAPaymentParamsForDiv('ASHAPaymentDataEntryForm'),
      success: function( json ) {
		callAction( 'selectASHA' );
      }
     });
}

function getASHAPaymentParamsForDiv( formDataDiv )
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



function calculateTotalPayable()
{
	var totalPayable = 0;
	var paymentDue = 0;
	var payableAmount = 0;
	
	if( isInt( document.getElementById( "paymentDue" ).value ) )
	{
		var temp = document.getElementById( "paymentDue" ).value;
		paymentDue = parseInt( temp );
	}
	
	if( isInt( document.getElementById( "deps160" ).value ) )
	{
		var temp = document.getElementById( "deps160" ).value;
		payableAmount = parseInt( temp );
	}	
	
	totalPayable = paymentDue + payableAmount;
	document.getElementById("totalPayableAmount").value = totalPayable;
	
}

function calculateDueAmountForNextMonth()
{
	var dueForNextMonth = 0;
	var totalAmount = 0;
	var amountPaid = 0;
	
	if( isInt( document.getElementById( "totalPayableAmount" ).value ) )
	{
		var temp = document.getElementById( "totalPayableAmount" ).value;
		totalAmount = parseInt( temp );
	}
	
	if( isInt( document.getElementById( "deps161" ).value ) )
	{
		var temp = document.getElementById( "deps161" ).value;
		amountPaid = parseInt( temp );
	}
	
	dueForNextMonth = totalAmount - amountPaid;
	
	document.getElementById("deps159").value = dueForNextMonth;
}
