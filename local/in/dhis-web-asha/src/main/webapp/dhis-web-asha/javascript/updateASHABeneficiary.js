

function validateUpdateBeneficiary() 
{	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	var dataElementId = $( '#dataElementId1' ).val();
	
	var identifier = document.getElementById('identifier1').value;
	
	var beneficiaryId = document.getElementById('beneficiaryId').value;
	
	//alert( dataElementId + "---" + identifier );
	
	$.post("validateBeneficiary.action",
		{
			identifier : identifier,
			selectedPeriodId : selectedPeriodId,
			dataElementId : dataElementId,
			beneficiaryId : beneficiaryId
		},
		function (data)
		{
			updateBeneficiaryValidationCompleted(data);
		},'xml');

	return false;
}

function updateBeneficiaryValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		updateASHABeneficiary();
	} 
	else if ( type == 'input' ) 
	{
		//setMessage( messageElement.firstChild.nodeValue );
		showWarningMessage( messageElement.firstChild.nodeValue );
	}
}





function updateASHABeneficiary()
{
	var ashaId = $( '#ashaId' ).val();
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	$.ajax({
      type: "POST",
      url: 'updateASHABeneficiary.action',
      data: getASHABeneficiaryParamsForDiv('UpdateASHABeneficiaryForm'),
      success: function( json ) {
		jQuery('#updateASHABeneficiaryFormDiv').dialog('destroy').remove();
		loadASHABeneficiaryListAfterUpdate( ashaId, selectedPeriodId );
		//callAction( 'getRegisteredASHAList' );
      }
     });
}

function loadASHABeneficiaryListAfterUpdate( ashaId,selectedPeriodId )
{
	
	$( '#ASHABeneficiaryListDiv' ).html('');
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ASHABeneficiaryListDiv' ).html('');
		return false;
	}
	
	else
	{
		jQuery('#ASHABeneficiaryListDiv').load('getASHABeneficiaryList.action',
			{
				id:ashaId,
				selectedPeriodId:selectedPeriodId
			}, function()
			{
				showById('ASHABeneficiaryListDiv');
				
			});
	}
}

function getASHABeneficiaryParamsForDiv( formDataDiv )
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


function getDataElementList1()
{
	var dataElementGroupList1 = document.getElementById('dataElementGroupId1');
	var dataElementGroupId1 = dataElementGroupList1.options[dataElementGroupList1.selectedIndex].value;
	var dataElementList1 = document.getElementById('dataElementId1');

	if ( dataElementGroupId1 != "" ) 
	{
		document.getElementById('price1').value = "";
		document.getElementById('amount1').value = "";
		
		$.post("getDataElementList.action",
			{
				dataElementGroupId : dataElementGroupId1
			},
			function (data)
			{
				getdataElementListReceived1(data);
			},'xml');
	} 
	else 
	{
		clearList(dataElementList1);
		document.getElementById('price1').value = "";
		document.getElementById('amount1').value = "";
	}
}


function getdataElementListReceived1(xmlObject)
{
	var dataElementId1 = document.getElementById("dataElementId1");

	clearList(dataElementId1);
	
	var defaultValue1 = "";
	var defaultText1 = "[ Please Select ]";
	
	$("#dataElementId1").append("<option value='"+ defaultValue1 +"'>" + defaultText1 + "</option>");
	
	var dataElements1 = xmlObject.getElementsByTagName("dataElement");

	for ( var i = 0; i < dataElements1.length; i++ ) 
	{
		var id1 = dataElements1[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var dataElementName1 = dataElements1[i].getElementsByTagName("name")[0].firstChild.nodeValue;

		$("#dataElementId1").append("<option value='"+ id1 +"'>" + dataElementName1 + "</option>");
	}
}

function getServicePrice1()
{
	var dataElementist1 = document.getElementById('dataElementId1');
	var dataElementId1 = dataElementist1.options[dataElementist1.selectedIndex].value;
	
	var price = document.getElementById('price1').value;

	if ( dataElementId1 != "" ) 
	{
		$.post("getServicePrice.action",
			{
				dataElementId : dataElementId1
			},
			function (data)
			{
				getPriceReceived1(data);
			},'xml');
	} 
	else 
	{
		document.getElementById('price1').value = "";
	}
}

function getPriceReceived1(xmlObject)
{
    var prices1 = xmlObject.getElementsByTagName("price");
    
    document.getElementById('price1').value = "";
    document.getElementById('amount1').value = "";

    for ( var i = 0; i < prices1.length; i++ )
    {
        var amount = prices1[ i ].getElementsByTagName("amount")[0].firstChild.nodeValue;
		
        document.getElementById('price1').value = amount;
        
        document.getElementById('amount1').value = amount;
    }    		
}
