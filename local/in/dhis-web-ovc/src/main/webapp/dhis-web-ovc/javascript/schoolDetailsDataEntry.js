
function loadSchoolDetailsDataEntryForm()
{

	$( '#schoolDetailsDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var organisationUnitId = $( '#orgUnitId' ).val();
	var schoolId = $( '#schoolId' ).val();
	
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#schoolDetailsDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    //jQuery('#loaderDiv').show();
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#schoolDetailsDataEntryFormDiv').load('loadSchoolDetailsDataEntryForm.action',
			{
				id:schoolId,
				selectedPeriodId:selectedPeriodId,
			}, function()
			{
				showById('schoolDetailsDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
				//jQuery('#loaderDiv').hide();
			});
		//hideLoader();
	}
}

function saveSchoolDetailsDataEntryForm()
{
	$.ajax({
      type: "POST",
      url: 'saveSchoolDetailsData.action',
      data: getOVCParamsForDiv('schoolDetailsDataEntryForm'),
      success: function( json ) {
    	  window.location.href = "getSchoolList.action";
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

//next and pre periods
function getAvailablePeriods( availablePeriodsId, selectedPeriodsId, year )
{	
	$( '#schoolDetailsDataEntryFormDiv' ).html( '' );

	var availableList = document.getElementById( availablePeriodsId );
	var selectedList = document.getElementById( selectedPeriodsId );
	
	clearList( selectedList );
	
	addOptionToList( selectedList, '-1', '[Please Select]' );
	
	$.getJSON( "getAvailableNextPrePeriods.action", {
		"year": year },
		function( json ) {
			
			for ( i in json.periods ) {
	    		//addOptionToList( list, i, json.periods[i].name );
	    		addOptionToList( selectedList, json.periods[i].externalId, json.periods[i].name );
	    	}
			
		} );
}
