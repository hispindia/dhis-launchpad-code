
function ashaFacilitatorFormat1Report()
{
	var facilitatorId = $( '#facilitatorId' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	var orgUnitId = $( '#orgUnitId' ).val();
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if( selectedPeriodId == "-1" ) 
    {
		showWarningMessage( i18n_select_period );
        //return false;
    }   	
	else
	{
		var url = "facilitatorFormat1Report.action?id=" + facilitatorId + "&selectedPeriodId=" + selectedPeriodId + "&dataSetId=" + dataSetId + "&orgUnitId=" + orgUnitId;
		window.location.href = url;
	}
}


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
		showWarningMessage( i18n_select_period );
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
		//loadFacilitatorASHAList( facilitatorId, selectedPeriodId )
		
		loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId )
	}
	
	var selectedASHAId = $( '#selectedASHAId' ).val();
	
	if ( selectedPeriodId == "-1" || selectedASHAId == "-1" )
	{
		$( '#ashaFacilitatorDataEntryFormDiv' ).html('');
		showWarningMessage( i18n_please_select_asha );
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
		
		//loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId );
		
		loadFacilitatorASHAList();
		
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



//Load ASHA  List
function loadFacilitatorASHAList()
{
	
	var orgUnitId = $( '#orgUnitId' ).val();
	var facilitatorId = $( '#facilitatorId' ).val();
	var dataSetId = $( '#dataSetId' ).val();
	
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ashaPerFormanceListDiv' ).html('');
		showWarningMessage( i18n_select_period );
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	
	else
	{
		$( '#ashaFacilitatorDataEntryFormDiv' ).html('');
		loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId )
		
		$.post("getFacilitatorASHAList.action",
				{
					facilitatorId:facilitatorId,
					selectedPeriodId:selectedPeriodId
				},
				function(data)
				{
					populateFacilitatorASHAList( data );
					
				},'xml');
	}
	
}


function populateFacilitatorASHAList( data )
{
	var selectedASHAId = document.getElementById("selectedASHAId");
	clearList( selectedASHAId );
	
		
	var ashaList = data.getElementsByTagName("asha");
	
	selectedASHAId.options[0] = new Option( i18n_please_select, "-1" , false, false);
	
	for ( var i = 0; i < ashaList.length; i++ )
	{
		var id = ashaList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = ashaList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		selectedASHAId.add(option, null);
	} 
	
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
