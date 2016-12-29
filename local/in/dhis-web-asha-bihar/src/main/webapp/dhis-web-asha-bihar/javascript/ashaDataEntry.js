function loadASHADataEntryForm()
{
	$( '#ashaDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var orgUnitId = $( '#orgUnitId' ).val();
	var ashaId = $( '#id' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ashaDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#ashaDataEntryFormDiv').load('loadASHADataEntryForm.action',
			{
				id:ashaId,
				selectedPeriodId:selectedPeriodId,
				dataSetId:dataSetId
			}, function()
			{
				showById('ashaDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
			});
	}
}

function saveASHADataEntryForm()
{
	//alert("save");
	$.ajax({
      type: "POST",
      url: 'saveASHADataValue.action',
      data: getASHADataEntryParamsForDiv('ashaDataEntryForm'),
      success: function( json ) {
		callAction( 'getRegisteredASHAList' );
      }
     });
}

function getASHADataEntryParamsForDiv( formDataDiv )
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
