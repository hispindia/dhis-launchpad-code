function loadASHAFacilitatorDataEntryForm()
{
	$( '#ashaFacilitatorDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var orgUnitId = $( '#orgUnitId' ).val();
	var facilitatorId = $( '#facilitatorId' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ashaPerFormanceListDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
		loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId )
	}
	
	var selectedASHAId = $( '#selectedASHAId' ).val();
	
	if ( selectedPeriodId == "-1" || selectedASHAId == "-1" )
	{
		$( '#ashaFacilitatorDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#ashaFacilitatorDataEntryFormDiv').load('loadASHAFacilitatorDataEntryForm.action',
			{
				id:facilitatorId,
				selectedPeriodId:selectedPeriodId,
				dataSetId:dataSetId,
				selectedASHAId:selectedASHAId
			}, function()
			{
				showById('ashaFacilitatorDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
			});
	}
}



function saveASHAFacilitatorDataEntryForm()
{
	var facilitatorId = $( '#facilitatorId' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	
	$.ajax({
      type: "POST",
      url: 'saveASHAFacilitatorDataValue.action',
      data: getASHAFacilitatorParamsForDiv('facilitatorDataEntryForm'),
      success: function( json ) {
		//window.location.href='getASHAFacilitatorList.action';
		
		loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId )
		
      }
     });
}

function getASHAFacilitatorParamsForDiv( formDataDiv )
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




function loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId )
{
	
	$( '#ashaPerFormanceListDiv' ).html('');
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ashaPerFormanceListDiv' ).html('');
		return false;
	}
	
	else
	{
		jQuery('#ashaPerFormanceListDiv').load('getASHAPerFormanceList.action',
			{
				facilitatorId:facilitatorId,
				dataSetId:dataSetId,
				selectedPeriodId:selectedPeriodId
			}, function()
			{
				showById('ashaPerFormanceListDiv');
				
			});
	}
}
