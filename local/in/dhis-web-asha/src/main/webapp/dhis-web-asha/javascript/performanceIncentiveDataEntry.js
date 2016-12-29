
function loadPerformanceIncentiveDataEntryForm()
{
	$( '#performanceIncentiveDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var orgUnitId = $( '#orgUnitId' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#performanceIncentiveDataEntryFormDiv' ).html('');
		return false;
	}

	else
	{
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#performanceIncentiveDataEntryFormDiv').load('loadPerformanceIncentiveDataEntryForm.action',
			{
				orgUnitId:orgUnitId,
				selectedPeriodId:selectedPeriodId,
				dataSetId:dataSetId
			}, function()
			{
				showById('performanceIncentiveDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
			});
	}
}



function savePerformanceIncentiveDataEntryForm()
{
	var orgUnitId = $( '#orgUnitId' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	$.ajax({
      type: "POST",
      url: 'savePerformanceIncentiveDataValue.action',
      data: getPerformanceIncentiveParamsForDiv('performanceIncentiveDataEntryForm'),
      success: function( json ) 
      {
    	  window.location.href='index.action';
      }
	
     });
}

function getPerformanceIncentiveParamsForDiv( formDataDiv )
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

