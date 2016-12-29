

function loadASHABeneficiaryAddForm()
{
	
	$( '#AddASHABeneficiaryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ashaId = $( '#ashaId' ).val();
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#AddASHABeneficiaryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#AddASHABeneficiaryFormDiv').load('loadASHABeneficiaryAddForm.action',
			{
				id:ashaId,
				selectedPeriodId:selectedPeriodId
			}, function()
			{
				showById('AddASHABeneficiaryFormDiv');
				loadASHABeneficiaryList( ashaId, selectedPeriodId );
				document.getElementById('overlay').style.visibility = 'hidden';
								
			});
		
		//loadASHABeneficiaryList( ashaId, selectedPeriodId );
	}
}


function loadASHABeneficiaryList( ashaId,selectedPeriodId )
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


function validateAddBeneficiary() 
{	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	var dataElementId = $( '#dataElementId' ).val();
	
	var identifier = document.getElementById('identifier').value;
	
	
	$.post("validateBeneficiary.action",
		{
			identifier : identifier,
			selectedPeriodId : selectedPeriodId,
			dataElementId : dataElementId
		},
		function (data)
		{
			addBeneficiaryValidationCompleted(data);
		},'xml');

	return false;
}

function addBeneficiaryValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		addASHABeneficiary();
	} 
	else if ( type == 'input' ) 
	{
		//setMessage( messageElement.firstChild.nodeValue );
		showWarningMessage( messageElement.firstChild.nodeValue );
	}
}



function addASHABeneficiary()
{
	var flag = true;

	var selServiceList = document.getElementById( 'selecteddataElementsList' );
    var selServiceListLength = selServiceList.options.length;
	
	if( selServiceList.options.length <= 0 )
	{
		showWarningMessage( "Select Services" );
		return false;
		
	}
	
	else
	{
		var ashaId = $( '#ashaId' ).val();
		
		//alert( "save" );
		
		//$("#selecteddataElementsList").find("option").attr("selected", "selected");
		
		//alert( "save 2" );
		
		$.ajax({
	      type: "POST",
	      url: 'addASHABeneficiary.action',
	      //data: getASHAParamsForDiv('AddASHABeneficiaryForm'),
	      data: getASHABenficiryParams(),
	      success: function( json ) {
			
			showAddASHABeneficiaryForm( ashaId );
			//callAction( 'getRegisteredASHAList' );
	      }
	     });
	}
}
	
	

function getASHABenficiryParams()
{
	
	$("#selecteddataElementsList").find("option").attr("selected", "selected");
	
	var params = "";
	
	var ashaId = getFieldValue('ashaId');
	
	params += '&ashaId=' + ashaId;
	
	var selectedPeriodId = getFieldValue('selectedPeriodId');
	
	params += '&selectedPeriodId=' + selectedPeriodId;
	
	var name = getFieldValue('name');
	
	params += '&name=' + name;
	
	var fatherName = getFieldValue('fatherName');
	
	params += '&fatherName=' + fatherName;
	
	var gender = getFieldValue('gender');
	
	params += '&gender=' + gender;
	
	var village = getFieldValue('village');
	
	params += '&village=' + village;
	
	var identifier = getFieldValue('identifier');
	
	params += '&identifier=' + identifier;
	
	var registrationDate = getFieldValue('registrationDate');
	
	params += '&registrationDate=' + registrationDate;
	
	var serviceGivenDate = getFieldValue('serviceGivenDate');
	
	params += '&serviceGivenDate=' + serviceGivenDate;
	
	
	$("#selecteddataElementsList").find("option").attr("selected", "selected");
	
	
	params += "&" + getParamsStringBySelected( 'selecteddataElementsList', 'selecteddataElementsList' );
	
	//alert( params );
	
	return params;
}



function getASHAParamsForDiv( formDataDiv )
{
	var params = '';
	var dateOperator = '';
	
	$("#selecteddataElementsList").find("option").attr("selected", "selected");
	
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
		
	alert( params );
	
	params += "&" + getParamsStringBySelected( 'selecteddataElementsList', 'selecteddataElementsList' );
	
	alert( params );
	
	return params;
}




function getParamsStringBySelected( elementId, param )
{
	var result = "";
	var list = jQuery( "#" + elementId ).children( ":selected" );
	
	list.each( function( i, item ){
		
		//result += param + "=" + item.value + "&";
		result += param + "=" + item.value;
		result += ( i < list.length - 1 ) ? "&" : "";
		
	});
	
	//result = result.substring( 0, list.length - 1 );
	//alert( result );
	return result;
}





function getDataElementList()
{
	var dataElementGroupList = document.getElementById('dataElementGroupId');
	var dataElementGroupId = dataElementGroupList.options[dataElementGroupList.selectedIndex].value;
	var dataElementList = document.getElementById('dataElementId');

	if ( dataElementGroupId != "" ) 
	{
		//document.getElementById('price').value = "";
		
		//document.getElementById('amount').value = "";
		
		$.post("getDataElementList.action",
			{
				dataElementGroupId : dataElementGroupId
			},
			function (data)
			{
				getdataElementListReceived(data);
			},'xml');
	} 
	else 
	{
		clearList(dataElementList);
		document.getElementById('price').value = "";
		document.getElementById('amount').value = "";
	}
}


function getdataElementListReceived(xmlObject)
{
	var availableDataElementsList = document.getElementById("availableDataElementsList");

	clearList(availableDataElementsList);
	
	/*
	var defaultValue = "";
	var defaultText = "[ Please Select ]";
	
	$("#availableDataElementsList").append("<option value='"+ defaultValue +"'>" + defaultText + "</option>");
	*/
	
	title=""
		
	var dataElements = xmlObject.getElementsByTagName("dataElement");

	for ( var i = 0; i < dataElements.length; i++ ) 
	{
		var id = dataElements[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var dataElementName = dataElements[i].getElementsByTagName("name")[0].firstChild.nodeValue;

		$("#availableDataElementsList").append("<option value='"+ id +"' title='"+ dataElementName + "'>" + dataElementName + "</option>");
	}
}

function getServicePrice()
{
	var dataElementist = document.getElementById('dataElementId');
	var dataElementId = dataElementist.options[dataElementist.selectedIndex].value;
	
	var price = document.getElementById('price').value;

	if ( dataElementId != "" ) 
	{
		$.post("getServicePrice.action",
			{
				dataElementId : dataElementId
			},
			function (data)
			{
				getPriceReceived(data);
			},'xml');
	} 
	else 
	{
		document.getElementById('price').value = "";
		document.getElementById('amount').value = "";
	}
}

function getPriceReceived(xmlObject)
{
    var prices = xmlObject.getElementsByTagName("price");

    for ( var i = 0; i < prices.length; i++ )
    {
        var amount = prices[ i ].getElementsByTagName("amount")[0].firstChild.nodeValue;
		
        document.getElementById('price').value = amount;
        
        document.getElementById('amount').value = amount;
    }    		
}


function loadAmountDetails()
{
	var ashaId = $( '#ashaId' ).val();
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	$( '#amountDetails' ).html('');
	
	//jQuery('#amountDetails').dialog('close');
	//jQuery('#amountDetails').dialog('destroy').remove();
	
	if( selectedPeriodId == "-1" ) 
    {
    	showWarningMessage( "Please Select Period" );
    	$( '#amountDetails' ).html('');
        //return false;
    }      
	
	else
	{
		document.getElementById('overlay').style.visibility = 'visible';
		$('#amountDetails').load("loadASHAAmountDetailsForm.action", 
				{
					ashaId : ashaId,
					selectedPeriodId : selectedPeriodId
				}
				, function( ){
					
				}).dialog({
					title: "Amount Details",
					maximize: true, 
					closable: true,
					modal:false,
					overlay:{background:'#000000', opacity:0.1},
					width: 1000,
					height: 800
				});
		document.getElementById('overlay').style.visibility = 'hidden';
	}

}


function ashaBeneficiaryReport()
{
	var ashaId = $( '#ashaId' ).val();
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if( selectedPeriodId == "-1" ) 
    {
    	showWarningMessage( "Please Select Period" );
        //return false;
    }   	
	else
	{
		var url = "ashaBeneficiaryReport.action?id=" + ashaId + "&selectedPeriodId=" + selectedPeriodId;
		window.location.href = url;
	}

	/*
	$('#beneficiaryReportDiv').load("ashaBeneficiaryReport.action", 
			{
				id : ashaId,
				selectedPeriodId : selectedPeriodId
			}
			, function( ){
				
			}).dialog({
				title: "Beneficiary Details",
				maximize: true, 
				closable: true,
				modal:false,
				overlay:{background:'#000000', opacity:0.1},
				width: 800,
				height: 800
			});
	*/		

}