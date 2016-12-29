
function loadOVCCareGiverDataEntryForm()
{

	$( '#OVCCareGiverDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ovcId = $( '#ovcId' ).val();
	var programInstanceId = $( '#programInstanceId' ).val();
	
	//alert( programInstanceId );
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#OVCCareGiverDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    //jQuery('#loaderDiv').show();
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#OVCCareGiverDataEntryFormDiv').load('loadOVCCareGiverDataEntryForm.action',
			{
				id:ovcId,
				selectedPeriodId:selectedPeriodId,
				programInstanceId:programInstanceId
			}, function()
			{
				showById('OVCCareGiverDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
				//jQuery('#loaderDiv').hide();
			});
		//hideLoader();
	}
}

function saveOVCCareGiverData()
{
	$.ajax({
      type: "POST",
      url: 'saveOVCCareGiverData.action',
      data: getOVCParamsForDiv('OVCCareGiverDataEntryForm'),
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
