function loadOVCForm1ADataEntryForm()
{

	$( '#OVCForm1ADataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ovcId = $( '#ovcId' ).val();
	var programInstanceId = $( '#programInstanceId' ).val();
	
	//alert( programInstanceId );
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#OVCForm1ADataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    //jQuery('#loaderDiv').show();
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#OVCForm1ADataEntryFormDiv').load('loadOVCForm1ADataEntryForm.action',
			{
				id:ovcId,
				selectedPeriodId:selectedPeriodId,
				programInstanceId:programInstanceId
			}, function()
			{
				showById('OVCForm1ADataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
				//jQuery('#loaderDiv').hide();
			});
		//hideLoader();
	}
}

function saveOVCForm1AData()
{
	$.ajax({
      type: "POST",
      url: 'saveOVCForm1AData.action',
      data: getOVCParamsForDiv('OVCForm1ADataEntryForm'),
      success: function( json ) {
		//window.location.href = "getApprovedList.action";
		
		loadApprovedOVCList();
      }
     });
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







