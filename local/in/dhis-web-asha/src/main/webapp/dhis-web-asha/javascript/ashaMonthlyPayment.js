function loadASHAMonthlyPaymentForm()
{
	$( '#ashaMonthlyPaymentDetailsFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var orgUnitId = $( '#orgUnitId' ).val();
		
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ashaMonthlyPaymentDetailsFormDiv' ).html('');
		return false;
	}

	else
	{
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#ashaMonthlyPaymentDetailsFormDiv').load('loadASHAMonthlyPaymentForm.action',
			{
				orgUnitId:orgUnitId,
				selectedPeriodId:selectedPeriodId
			}, function()
			{
				showById('ashaMonthlyPaymentDetailsFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
			});
	}
}


function saveASHAMonthlyPaymentForm()
{
	//var orgUnitId = $( '#orgUnitId' ).val();
	//var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	var result = window.confirm( "Are you sure to Pay Selected ASHA" );
	
	if ( result )
	{
		document.getElementById('overlayPayment').style.visibility = 'visible';
		$.ajax({
		      type: "POST",
		      url: 'saveASHAMonthlyPaymentForm.action',
		      data: getASHAMonthlyPaymentParamsForDiv('ashaMonthlyPaymentForm'),
		      success: function( json ) 
		      {
		    	  window.location.href='index.action';
		    	  document.getElementById('overlayPayment').style.visibility = 'hidden';
		      }
			
		     });
	}
	

}

function getASHAMonthlyPaymentParamsForDiv( formDataDiv )
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


function selectAll(source)
{		
	var checkboxes = document.getElementsByName('option');
  	for (i=0; i < checkboxes.length; i++)
	 {
   		 checkboxes[i].checked = source.checked;
   	}
}




