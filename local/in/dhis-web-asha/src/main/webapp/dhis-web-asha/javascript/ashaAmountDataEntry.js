
function loadASHAAmountDataEntryForm()
{
	
	$( '#ASHAAmountDataEntryFormDiv' ).html('');
	
	$( '#saveButton' ).removeAttr( 'disabled' );

	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ashaId = $( '#ashaId' ).val();
	var programInstanceId = $( '#programInstanceId' ).val();
	
	//alert( programInstanceId );
	
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ASHAAmountDataEntryFormDiv' ).html('');
		document.getElementById( "saveButton" ).disabled = true;
		return false;
	}
	
	else
	{
	    //jQuery('#loaderDiv').show();
	    document.getElementById('overlay').style.visibility = 'visible';
		jQuery('#ASHAAmountDataEntryFormDiv').load('loadASHAAmountDetailsForm.action',
			{
				id:ashaId,
				selectedPeriodId:selectedPeriodId,
				programInstanceId:programInstanceId
			}, function()
			{
				showById('ASHAAmountDataEntryFormDiv');
				document.getElementById('overlay').style.visibility = 'hidden';
				//jQuery('#loaderDiv').hide();
			});
		//hideLoader();
	}
}

function saveASHAIncentiveAmountDetails()
{
	$.ajax({
      type: "POST",
      url: 'saveASHAIncentiveAmounts.action',
      data: getASHAParamsForDiv('ASHAAmountDataEntryForm'),
      success: function( json ) {
		callAction( 'getRegisteredASHAList' );
      }
     });
}

function closeAmountDetails()
{
	
	$.ajax({
	      type: "POST",
	      url: 'saveASHAIncentiveAmounts.action',
	      data: getASHAParamsForDiv('amountDetails'),
	      success: function( json ) {
	    	  jQuery('#amountDetails').dialog('close');
	      }
	     });
	
}
function getASHAParamsForDiv( formDataDiv )
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


var totalCalculatedAmount = 0;
function calCulateAmount67()
{
	var amountDE3 = $( '#de3' ).val();
	var amountDEPS67 = $( '#deps67' ).val();
	
	var totalAmount67 = amountDE3 * amountDEPS67;
	
	document.getElementById( "depsamount67" ).value = totalAmount67;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount68()
{
	var amountDE6 = $( '#de6' ).val();
	var amountDEPS68 = $( '#deps68' ).val();
	
	var totalAmount68 = amountDE6 * amountDEPS68;
	
	document.getElementById( "depsamount68" ).value = totalAmount68;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}


/*
function calCulateAmount69()
{
	var amountDE9 = $( '#de9' ).val();
	var amountDEPS69 = $( '#deps69' ).val();
	
	var totalAmount69 = amountDE9 * amountDEPS69;
	
	document.getElementById( "depsamount69" ).value = totalAmount69;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}


function calCulateAmount70()
{
	var amountDE63 = $( '#de5' ).val();
	var amountDEPS70 = $( '#deps70' ).val();
	
	var totalAmount70 = amountDE63 * amountDEPS70;
	
	document.getElementById( "depsamount70" ).value = totalAmount70;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}


*/

function calCulateAmount71()
{
	var amountDE10 = $( '#de10' ).val();
	var amountDEPS71 = $( '#deps71' ).val();
	
	var totalAmount71 = amountDE10 * amountDEPS71;
	
	document.getElementById( "depsamount71" ).value = totalAmount71;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount72()
{
	var amountDE5 = $( '#de5' ).val();
	var amountDEPS72 = $( '#deps72' ).val();
	
	var totalAmount72 = amountDE5 * amountDEPS72;
	
	document.getElementById( "depsamount72" ).value = totalAmount72;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}



function calCulateAmount75()
{
	var amountDE15 = $( '#de15' ).val();
	var amountDEPS75 = $( '#deps75' ).val();
	
	var totalAmount75 = amountDE15 * amountDEPS75;
	
	document.getElementById( "depsamount75" ).value = totalAmount75;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount76()
{
	var amountDE13 = $( '#de13' ).val();
	var amountDEPS76 = $( '#deps76' ).val();
	
	var totalAmount76 = amountDE13 * amountDEPS76;
	
	document.getElementById( "depsamount76" ).value = totalAmount76;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}


function calCulateAmount110()
{
	var amountDE109 = $( '#de109' ).val();
	var amountDEPS110 = $( '#deps110' ).val();
	
	var totalAmount110 = amountDE109 * amountDEPS110;
	
	document.getElementById( "depsamount110" ).value = totalAmount110;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount77()
{
	var amountDE14 = $( '#de14' ).val();
	var amountDEPS77 = $( '#deps77' ).val();
	
	var totalAmount77 = amountDE14 * amountDEPS77;
	
	document.getElementById( "depsamount77" ).value = totalAmount77;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount78()
{
	var amountDE12 = $( '#de12' ).val();
	var amountDEPS78 = $( '#deps78' ).val();
	
	var totalAmount78 = amountDE12 * amountDEPS78;
	
	document.getElementById( "depsamount78" ).value = totalAmount78;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount112()
{
	var amountDE111 = $( '#de111' ).val();
	var amountDEPS112 = $( '#deps112' ).val();
	
	var totalAmount112 = amountDE111 * amountDEPS112;
	
	document.getElementById( "depsamount112" ).value = totalAmount112;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount86()
{
	var amountDE8 = $( '#de8' ).val();
	var amountDEPS86 = $( '#deps86' ).val();
	
	var totalAmount86 = amountDE8 * amountDEPS86;
	
	document.getElementById( "depsamount86" ).value = totalAmount86;
	
	calculateMaternalGroupTotal();
	calculateGrandTotal();
}


function calculateMaternalGroupTotal()
{
	var group1Total = 0;
	var tempTotal = 0;
	
	if( isInt( document.getElementById( "depsamount67" ).value ) )
	{
		var temp = document.getElementById( "depsamount67" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount68" ).value ) )
	{
		var temp = document.getElementById( "depsamount68" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount71" ).value ) )
	{
		var temp = document.getElementById( "depsamount71" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount72" ).value ) )
	{
		var temp = document.getElementById( "depsamount72" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount75" ).value ) )
	{
		var temp = document.getElementById( "depsamount75" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}


	if( isInt( document.getElementById( "depsamount76" ).value ) )
	{
		var temp = document.getElementById( "depsamount76" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	
	if( isInt( document.getElementById( "depsamount110" ).value ) )
	{
		var temp = document.getElementById( "depsamount110" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
		

	if( isInt( document.getElementById( "depsamount78" ).value ) )
	{
		var temp = document.getElementById( "depsamount78" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}		
	
	if( isInt( document.getElementById( "depsamount77" ).value ) )
	{
		var temp = document.getElementById( "depsamount77" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	
	if( isInt( document.getElementById( "depsamount112" ).value ) )
	{
		var temp = document.getElementById( "depsamount112" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	
	if( isInt( document.getElementById( "depsamount86" ).value ) )
	{
		var temp = document.getElementById( "depsamount86" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}			
	
	document.getElementById("deps129").value = group1Total;
	
}

/*

function calCulateAmount73()
{
	var amountDE16 = $( '#de16' ).val();
	var amountDEPS73 = $( '#deps73' ).val();
	
	var totalAmount73 = amountDE16 * amountDEPS73;
	
	document.getElementById( "depsamount73" ).value = totalAmount73;
	
	calculateGrandTotal();
}

function calCulateAmount74()
{
	var amountDE62 = $( '#de62' ).val();
	var amountDEPS74 = $( '#deps74' ).val();
	
	var totalAmount74 = amountDE62 * amountDEPS74;
	
	document.getElementById( "depsamount74" ).value = totalAmount74;
	
	calculateGrandTotal();
}


*/

//Child Health
function calCulateAmount82()
{
	var amountDE61 = $( '#de61' ).val();
	var amountDEPS82 = $( '#deps82' ).val();
	
	var totalAmount82 = amountDE61 * amountDEPS82;
	
	document.getElementById( "depsamount82" ).value = totalAmount82;
	
	calculateChildGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount83()
{
	var amountDE4 = $( '#de4' ).val();
	var amountDEPS83 = $( '#deps83' ).val();
	
	var totalAmount83 = amountDE4 * amountDEPS83;
	
	document.getElementById( "depsamount83" ).value = totalAmount83;
	
	calculateChildGroupTotal();
	calculateGrandTotal();
}


function calCulateAmount116()
{
	var amountDE115 = $( '#de115' ).val();
	var amountDEPS116 = $( '#deps116' ).val();
	
	var totalAmount116 = amountDE115 * amountDEPS116;
	
	document.getElementById( "depsamount116" ).value = totalAmount116;
	
	calculateChildGroupTotal();
	calculateGrandTotal();
}


function calCulateAmount85()
{
	var amountDE11 = $( '#de11' ).val();
	var amountDEPS85 = $( '#deps85' ).val();
	
	var totalAmount85 = amountDE11 * amountDEPS85;
	
	document.getElementById( "depsamount85" ).value = totalAmount85;
	
	calculateChildGroupTotal();
	calculateGrandTotal();
}

function calculateChildGroupTotal()
{
	var group1Total = 0;
	var tempTotal = 0;
	
	if( isInt( document.getElementById( "depsamount82" ).value ) )
	{
		var temp = document.getElementById( "depsamount82" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount83" ).value ) )
	{
		var temp = document.getElementById( "depsamount83" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount116" ).value ) )
	{
		var temp = document.getElementById( "depsamount116" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}


	if( isInt( document.getElementById( "depsamount85" ).value ) )
	{
		var temp = document.getElementById( "depsamount85" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	document.getElementById("deps130").value = group1Total;
}


// RBSY 

function calCulateAmount114()
{
	var amountDE113 = $( '#de113' ).val();
	var amountDEPS114 = $( '#deps114' ).val();
	
	var totalAmount114 = amountDE113 * amountDEPS114;
	
	document.getElementById( "depsamount114" ).value = totalAmount114;
	
	calculateRBSYGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount108()
{
	var amountDE18 = $( '#de18' ).val();
	var amountDEPS108 = $( '#deps108' ).val();
	
	var totalAmount108 = amountDE18 * amountDEPS108;
	
	document.getElementById( "depsamount108" ).value = totalAmount108;
	
	calculateRBSYGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount118()
{
	var amountDE117 = $( '#de117' ).val();
	var amountDEPS118 = $( '#deps118' ).val();
	
	var totalAmount118 = amountDE117 * amountDEPS118;
	
	document.getElementById( "depsamount118" ).value = totalAmount118;
	
	calculateRBSYGroupTotal();
	calculateGrandTotal();
}


function calculateRBSYGroupTotal()
{
	var group1Total = 0;
	var tempTotal = 0;
	
	
	if( isInt( document.getElementById( "depsamount114" ).value ) )
	{
		var temp = document.getElementById( "depsamount114" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount108" ).value ) )
	{
		var temp = document.getElementById( "depsamount108" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount118" ).value ) )
	{
		var temp = document.getElementById( "depsamount118" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	document.getElementById("deps394").value = group1Total;
	
}


//Adolescent Health

function calCulateAmount120()
{
	var amountDE119 = $( '#de119' ).val();
	var amountDEPS120 = $( '#deps120' ).val();
	
	var totalAmount120 = amountDE119 * amountDEPS120;
	
	document.getElementById( "depsamount120" ).value = totalAmount120;
	
	calculateAdolescentGroupTotal();
	calculateGrandTotal();
}


function calculateAdolescentGroupTotal()
{
	var group1Total = 0;
	var tempTotal = 0;
	

	
	if( isInt( document.getElementById( "depsamount120" ).value ) )
	{
		var temp = document.getElementById( "depsamount120" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	document.getElementById("deps131").value = group1Total;
	
}

//NRHM Additionalities

function calCulateAmount92()
{
	var amountDE29 = $( '#de29' ).val();
	var amountDEPS92 = $( '#deps92' ).val();
	
	var totalAmount92 = amountDE29 * amountDEPS92;
	
	document.getElementById( "depsamount92" ).value = totalAmount92;
	
	calculateNRHMGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount93()
{
	var amountDE30 = $( '#de30' ).val();
	var amountDEPS93 = $( '#deps93' ).val();
	
	var totalAmount93 = amountDE30 * amountDEPS93;
	
	document.getElementById( "depsamount93" ).value = totalAmount93;
	
	calculateNRHMGroupTotal();
	calculateGrandTotal();
}


function calCulateAmount66()
{
	var amountDE31 = $( '#de31' ).val();
	var amountDEPS66 = $( '#deps66' ).val();
	
	var totalAmount66 = amountDE31 * amountDEPS66;
	
	document.getElementById( "depsamount66" ).value = totalAmount66;
	
	calculateNRHMGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount94()
{
	var amountDE32 = $( '#de32' ).val();
	var amountDEPS94 = $( '#deps94' ).val();
	
	var totalAmount94 = amountDE32 * amountDEPS94;
	
	document.getElementById( "depsamount94" ).value = totalAmount94;
	
	calculateNRHMGroupTotal();
	calculateGrandTotal();
}

function checkBoxValueChange()
{
	if( document.getElementById("checkboxdeps95").checked == true )
	{
		 document.getElementById( "deps95" ).value = 1;
		
	}    
	else
	{
		document.getElementById( "deps95" ).value = 0;
	    
	}
	calCulateAmount95();
	calculateNRHMGroupTotal();
	calculateGrandTotal();
	//alert($( '#deps95' ).val());
}


function calCulateAmount95()
{
	var amountDE43 = $( '#de43' ).val();
	var amountDEPS95 = $( '#deps95' ).val();
	
	var totalAmount95 = amountDE43 * amountDEPS95;
	
	document.getElementById( "depsamount95" ).value = totalAmount95;
	
	calculateNRHMGroupTotal();
	calculateGrandTotal();
}





function checkBoxValueChange386()
{
	if( document.getElementById("checkboxdeps386").checked == true )
	{
		 document.getElementById( "deps386" ).value = 1;
		
	}    
	else
	{
		document.getElementById( "deps386" ).value = 0;
	    
	}
	calCulateAmount386();
	calculateNRHMGroupTotal();
	calculateGrandTotal();
	//alert($( '#deps95' ).val());
}



function calCulateAmount386()
{
	var amountDE387 = $( '#de387' ).val();
	var amountDEPS386 = $( '#deps386' ).val();
	
	var totalAmount386 = amountDE387 * amountDEPS386;
	
	document.getElementById( "depsamount386" ).value = totalAmount386;
	
	calculateNRHMGroupTotal();
	calculateGrandTotal();
}


















function calCulateAmount96()
{
	var amountDE34 = $( '#de34' ).val();
	var amountDEPS96 = $( '#deps96' ).val();
	
	var totalAmount96 = amountDE34 * amountDEPS96;
	
	document.getElementById( "depsamount96" ).value = totalAmount96;
	
	calculateNRHMGroupTotal();
	calculateGrandTotal();
}


function calculateNRHMGroupTotal()
{
	var group1Total = 0;
	var tempTotal = 0;
	
	if( isInt( document.getElementById( "depsamount92" ).value ) )
	{
		var temp = document.getElementById( "depsamount92" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount93" ).value ) )
	{
		var temp = document.getElementById( "depsamount93" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount66" ).value ) )
	{
		var temp = document.getElementById( "depsamount66" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount94" ).value ) )
	{
		var temp = document.getElementById( "depsamount94" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount96" ).value ) )
	{
		var temp = document.getElementById( "depsamount96" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}


	if( isInt( document.getElementById( "depsamount95" ).value ) )
	{
		var temp = document.getElementById( "depsamount95" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	

	if( isInt( document.getElementById( "depsamount386" ).value ) )
	{
		var temp = document.getElementById( "depsamount386" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	
	
	
	document.getElementById("deps132").value = group1Total;
	
}


// JSY  National


function calCulateAmount80()
{
	var amountDE2 = $( '#de2' ).val();
	var amountDEPS80 = $( '#deps80' ).val();
	
	var totalAmount80 = amountDE2 * amountDEPS80;
	
	document.getElementById( "depsamount80" ).value = totalAmount80;
	
	calculateJSYGroupTotal();
	calculateGrandTotal();
}



function calCulateAmount393()
{
	var amountDE392 = $( '#de392' ).val();
	var amountDEPS393 = $( '#deps393' ).val();
	
	var totalAmount393 = amountDE392 * amountDEPS393;
	
	document.getElementById( "depsamount393" ).value = totalAmount393;
	
	calculateJSYGroupTotal();
	calculateGrandTotal();
}



function calculateJSYGroupTotal()
{
	var group1Total = 0;
	var tempTotal = 0;
	
	if( isInt( document.getElementById( "depsamount80" ).value ) )
	{
		var temp = document.getElementById( "depsamount80" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount393" ).value ) )
	{
		var temp = document.getElementById( "depsamount393" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	document.getElementById("deps133").value = group1Total;
	
}














// RI and FI of Children

function calCulateAmount88()
{
	var amountDE20 = $( '#de20' ).val();
	var amountDEPS88 = $( '#deps88' ).val();
	
	var totalAmount88 = amountDE20 * amountDEPS88;
	
	document.getElementById( "depsamount88" ).value = totalAmount88;
	
	calculateRIandFIGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount90()
{
	var amountDE22 = $( '#de22' ).val();
	var amountDEPS90 = $( '#deps90' ).val();
	
	var totalAmount90 = amountDE22 * amountDEPS90;
	
	document.getElementById( "depsamount90" ).value = totalAmount90;
	
	calculateRIandFIGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount91()
{
	var amountDE23 = $( '#de23' ).val();
	var amountDEPS91 = $( '#deps91' ).val();
	
	var totalAmount91 = amountDE23 * amountDEPS91;
	
	document.getElementById( "depsamount91" ).value = totalAmount91;
	
	calculateRIandFIGroupTotal();
	calculateGrandTotal();
}


function calculateRIandFIGroupTotal()
{
	var group1Total = 0;
	var tempTotal = 0;
	
	if( isInt( document.getElementById( "depsamount88" ).value ) )
	{
		var temp = document.getElementById( "depsamount88" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}

	if( isInt( document.getElementById( "depsamount90" ).value ) )
	{
		var temp = document.getElementById( "depsamount90" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount91" ).value ) )
	{
		var temp = document.getElementById( "depsamount91" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	
	document.getElementById("deps134").value = group1Total;
	
}


// Family Welfare

function calCulateAmount101()
{
	var amountDE24 = $( '#de24' ).val();
	var amountDEPS101 = $( '#deps101' ).val();
	
	var totalAmount101 = amountDE24 * amountDEPS101;
	
	document.getElementById( "depsamount101" ).value = totalAmount101;
	
	calculateFamilyWelfareGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount102()
{
	var amountDE25 = $( '#de25' ).val();
	var amountDEPS102 = $( '#deps102' ).val();
	
	var totalAmount102 = amountDE25 * amountDEPS102;
	
	document.getElementById( "depsamount102" ).value = totalAmount102;
	
	calculateFamilyWelfareGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount103()
{
	var amountDE26 = $( '#de26' ).val();
	var amountDEPS103 = $( '#deps103' ).val();
	
	var totalAmount103 = amountDE26 * amountDEPS103;
	
	document.getElementById( "depsamount103" ).value = totalAmount103;
	
	calculateFamilyWelfareGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount98()
{
	var amountDE65 = $( '#de65' ).val();
	var amountDEPS98 = $( '#deps98' ).val();
	
	var totalAmount98 = amountDE65 * amountDEPS98;
	
	document.getElementById( "depsamount98" ).value = totalAmount98;
	
	calculateFamilyWelfareGroupTotal();
	calculateGrandTotal();
}

function calCulateAmount97()
{
	var amountDE64 = $( '#de64' ).val();
	var amountDEPS97 = $( '#deps97' ).val();
	
	var totalAmount97 = amountDE64 * amountDEPS97;
	
	document.getElementById( "depsamount97" ).value = totalAmount97;
	
	calculateFamilyWelfareGroupTotal();
	calculateGrandTotal();
}



function calculateFamilyWelfareGroupTotal()
{
	var group1Total = 0;
	var tempTotal = 0;
	
	if( isInt( document.getElementById( "depsamount101" ).value ) )
	{
		var temp = document.getElementById( "depsamount101" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}

	if( isInt( document.getElementById( "depsamount102" ).value ) )
	{
		var temp = document.getElementById( "depsamount102" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount103" ).value ) )
	{
		var temp = document.getElementById( "depsamount103" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	

	
	if( isInt( document.getElementById( "depsamount98" ).value ) )
	{
		var temp = document.getElementById( "depsamount98" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount97" ).value ) )
	{
		var temp = document.getElementById( "depsamount97" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	
	document.getElementById("deps135").value = group1Total;
	
}

// Other National Health Programmes

function calCulateAmount105()
{
	var amountDE36 = $( '#de36' ).val();
	var amountDEPS105 = $( '#deps105' ).val();
	
	var totalAmount105 = amountDE36 * amountDEPS105;
	
	document.getElementById( "depsamount105" ).value = totalAmount105;
	
	calculateOtherNationalGroupTotal();
	calculateGrandTotal();
}



function calCulateAmount106()
{
	var amountDE37 = $( '#de37' ).val();
	var amountDEPS106 = $( '#deps106' ).val();
	
	var totalAmount106 = amountDE37 * amountDEPS106;
	
	document.getElementById( "depsamount106" ).value = totalAmount106;
	
	calculateOtherNationalGroupTotal();
	calculateGrandTotal();
}



function calCulateAmount388()
{
	var amountDE389 = $( '#de389' ).val();
	var amountDEPS388 = $( '#deps388' ).val();
	
	var totalAmount388 = amountDE389 * amountDEPS388;
	
	document.getElementById( "depsamount388" ).value = totalAmount388;
	
	calculateOtherNationalGroupTotal();
	calculateGrandTotal();
}



function calCulateAmount390()
{
	var amountDE391 = $( '#de391' ).val();
	var amountDEPS390 = $( '#deps390' ).val();
	
	var totalAmount390 = amountDE391 * amountDEPS390;
	
	document.getElementById( "depsamount390" ).value = totalAmount390;
	
	calculateOtherNationalGroupTotal();
	calculateGrandTotal();
}





function calCulateAmount122()
{
	var amountDE35 = $( '#de35' ).val();
	var amountDEPS122 = $( '#deps122' ).val();
	
	var totalAmount122 = amountDE35 * amountDEPS122;
	
	document.getElementById( "depsamount122" ).value = totalAmount122;
	
	calculateOtherNationalGroupTotal();
	calculateGrandTotal();
}


function calCulateAmount107()
{
	var amountDE38 = $( '#de38' ).val();
	var amountDEPS107 = $( '#deps107' ).val();
	
	var totalAmount107 = amountDE38 * amountDEPS107;
	
	document.getElementById( "depsamount107" ).value = totalAmount107;
	
	calculateOtherNationalGroupTotal();
	calculateGrandTotal();
}

function checkBoxValueChange124()
{
	if( document.getElementById("checkboxdeps124").checked == true )
	{
		 document.getElementById( "deps124" ).value = 1;
		 
		 var amountDE123 = $( '#de40' ).val();
		 
		 var totalAmount124 = amountDE123 * 1 ;
		 document.getElementById( "depsamount124" ).value = totalAmount124;
		 
		 document.getElementById( "deps126" ).value = 0;
		 document.getElementById( "deps128" ).value = 0;
		 
		 document.getElementById( "depsamount126" ).value = 0;
		 document.getElementById( "depsamount128" ).value = 0;
		 
		 document.getElementById("checkboxdeps126").checked = false;
		 document.getElementById("checkboxdeps128").checked = false;
		 
		 document.getElementById( "checkboxdeps126" ).disabled = true;
		 document.getElementById( "checkboxdeps128" ).disabled = true;
		
	}
	
	else
	{
		document.getElementById( "depsamount124" ).value = 0; 
		
		document.getElementById( "checkboxdeps126" ).disabled = false;
		document.getElementById( "checkboxdeps128" ).disabled = false;
	}
	
	
	calculateOtherNationalGroupTotal();
	calculateGrandTotal();
}


function checkBoxValueChange126()
{
	if( document.getElementById("checkboxdeps126").checked == true )
	{
		 document.getElementById( "deps126" ).value = 1;
		 
		 var amountDE125 = $( '#de41' ).val();
		 
		 var totalAmount126 = amountDE125 * 1 ;
		 document.getElementById( "depsamount126" ).value = totalAmount126;
		 
		 document.getElementById( "deps124" ).value = 0;
		 document.getElementById( "deps128" ).value = 0;
		 
		 document.getElementById( "depsamount124" ).value = 0;
		 document.getElementById( "depsamount128" ).value = 0;
		 
		 document.getElementById("checkboxdeps124").checked = false;
		 document.getElementById("checkboxdeps128").checked = false;
		 
		 document.getElementById( "checkboxdeps124" ).disabled = true;
		 document.getElementById( "checkboxdeps128" ).disabled = true;
		
	}
	else
	{
		document.getElementById( "depsamount126" ).value = 0; 
		
		document.getElementById( "checkboxdeps124" ).disabled = false;
		document.getElementById( "checkboxdeps128" ).disabled = false;
	}
	
	calculateOtherNationalGroupTotal();
	calculateGrandTotal();
}


function checkBoxValueChange128()
{
	if( document.getElementById("checkboxdeps128").checked == true )
	{
		 document.getElementById( "deps128" ).value = 1;
		 
		 var amountDE127 = $( '#de42' ).val();
		 
		 var totalAmount128 = amountDE127 * 1 ;
		 document.getElementById( "depsamount128" ).value = totalAmount128;
		 
		 document.getElementById( "deps124" ).value = 0;
		 document.getElementById( "deps126" ).value = 0;
		 
		 document.getElementById( "depsamount124" ).value = 0;
		 document.getElementById( "depsamount126" ).value = 0;
		 
		 document.getElementById("checkboxdeps124").checked = false;
		 
		 document.getElementById("checkboxdeps126").checked = false
		 
		 document.getElementById( "checkboxdeps124" ).disabled = true;
		 document.getElementById( "checkboxdeps126" ).disabled = true;
		
	}

	else
	{
		document.getElementById( "depsamount128" ).value = 0; 
		
		document.getElementById( "checkboxdeps124" ).disabled = false;
		document.getElementById( "checkboxdeps126" ).disabled = false;
	}	
	calculateOtherNationalGroupTotal();
	calculateGrandTotal();
}
















function calculateOtherNationalGroupTotal()
{
	var group1Total = 0;
	var tempTotal = 0;
	
	if( isInt( document.getElementById( "depsamount105" ).value ) )
	{
		var temp = document.getElementById( "depsamount105" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}

	if( isInt( document.getElementById( "depsamount106" ).value ) )
	{
		var temp = document.getElementById( "depsamount106" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount388" ).value ) )
	{
		var temp = document.getElementById( "depsamount388" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	
	if( isInt( document.getElementById( "depsamount390" ).value ) )
	{
		var temp = document.getElementById( "depsamount390" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	
	
	
	if( isInt( document.getElementById( "depsamount122" ).value ) )
	{
		var temp = document.getElementById( "depsamount122" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	

	
	if( isInt( document.getElementById( "depsamount107" ).value ) )
	{
		var temp = document.getElementById( "depsamount107" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}
	
	
	if( isInt( document.getElementById( "depsamount124" ).value ) )
	{
		var temp = document.getElementById( "depsamount124" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount126" ).value ) )
	{
		var temp = document.getElementById( "depsamount126" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount128" ).value ) )
	{
		var temp = document.getElementById( "depsamount128" ).value;
		tempTotal = parseInt( temp );
		group1Total = group1Total + tempTotal;
	}		
	
	document.getElementById("deps136").value = group1Total;
	
}




/*

function calCulateAmount79()
{
	var amountDE1 = $( '#de1' ).val();
	var amountDEPS79 = $( '#deps79' ).val();
	
	var totalAmount79 = amountDE1 * amountDEPS79;
	
	document.getElementById( "depsamount79" ).value = totalAmount79;
	
	calculateGrandTotal();
}




function calCulateAmount81()
{
	var amountDE60 = $( '#de60' ).val();
	var amountDEPS81 = $( '#deps81' ).val();
	
	var totalAmount81 = amountDE60 * amountDEPS81;
	
	document.getElementById( "depsamount81" ).value = totalAmount81;
	
	calculateGrandTotal();
}





function calCulateAmount84()
{
	var amountDE17 = $( '#de17' ).val();
	var amountDEPS84 = $( '#deps84' ).val();
	
	var totalAmount84 = amountDE17 * amountDEPS84;
	
	document.getElementById( "depsamount84" ).value = totalAmount84;
	
	calculateGrandTotal();
}


function calCulateAmount87()
{
	var amountDE19 = $( '#de19' ).val();
	var amountDEPS87 = $( '#deps87' ).val();
	
	var totalAmount87 = amountDE19 * amountDEPS87;
	
	document.getElementById( "depsamount87" ).value = totalAmount87;
	
	calculateGrandTotal();
}

function calCulateAmount89()
{
	var amountDE21 = $( '#de21' ).val();
	var amountDEPS89 = $( '#deps89' ).val();
	
	var totalAmount89 = amountDE21 * amountDEPS89;
	
	document.getElementById( "depsamount89" ).value = totalAmount89;
	
	calculateGrandTotal();
}


function calCulateAmount99()
{
	var amountDE28 = $( '#de28' ).val();
	var amountDEPS99 = $( '#deps99' ).val();
	
	var totalAmount99 = amountDE28 * amountDEPS99;
	
	document.getElementById( "depsamount99" ).value = totalAmount99;
	
	calculateGrandTotal();
}

function calCulateAmount100()
{
	var amountDE27 = $( '#de27' ).val();
	var amountDEPS100 = $( '#deps100' ).val();
	
	var totalAmount100 = amountDE27 * amountDEPS100;
	
	document.getElementById( "depsamount100" ).value = totalAmount100;
	
	calculateGrandTotal();
}


function calCulateAmount104()
{
	var amountDE35 = $( '#de35' ).val();
	var amountDEPS104 = $( '#deps104' ).val();
	
	amountDEPS104 =  parseInt( amountDEPS104/50 );
	
	var totalAmount104 = amountDE35 * amountDEPS104;
	
	document.getElementById( "depsamount104" ).value = totalAmount104;
	
	calculateGrandTotal();
}

*/



function calculateGrandTotal()
{
	var gTotal = 0;
	var tempTotal = 0;
	
	
	//Maternal Health
	if( isInt( document.getElementById( "depsamount67" ).value ) )
	{
		var temp = document.getElementById( "depsamount67" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount68" ).value ) )
	{
		var temp = document.getElementById( "depsamount68" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount71" ).value ) )
	{
		var temp = document.getElementById( "depsamount71" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount72" ).value ) )
	{
		var temp = document.getElementById( "depsamount72" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	
	/*
	if( isInt( document.getElementById( "depsamount69" ).value ) )
	{
		var temp = document.getElementById( "depsamount69" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount70" ).value ) )
	{
		var temp = document.getElementById( "depsamount70" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount73" ).value ) )
	{
		var temp = document.getElementById( "depsamount73" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "depsamount74" ).value ) )
	{
		var temp = document.getElementById( "depsamount74" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	*/
	
	if( isInt( document.getElementById( "depsamount75" ).value ) )
	{
		var temp = document.getElementById( "depsamount75" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "depsamount76" ).value ) )
	{
		var temp = document.getElementById( "depsamount76" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			

	if( isInt( document.getElementById( "depsamount110" ).value ) )
	{
		var temp = document.getElementById( "depsamount110" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	if( isInt( document.getElementById( "depsamount77" ).value ) )
	{
		var temp = document.getElementById( "depsamount77" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "depsamount78" ).value ) )
	{
		var temp = document.getElementById( "depsamount78" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			

	if( isInt( document.getElementById( "depsamount112" ).value ) )
	{
		var temp = document.getElementById( "depsamount112" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}				
	
	if( isInt( document.getElementById( "depsamount86" ).value ) )
	{
		var temp = document.getElementById( "depsamount86" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	/*
	if( isInt( document.getElementById( "deps129" ).value ) )
	{
		var temp = document.getElementById( "deps129" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	*/
	
	//Child Health
	
	if( isInt( document.getElementById( "depsamount82" ).value ) )
	{
		var temp = document.getElementById( "depsamount82" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	if( isInt( document.getElementById( "depsamount83" ).value ) )
	{
		var temp = document.getElementById( "depsamount83" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	
	if( isInt( document.getElementById( "depsamount116" ).value ) )
	{
		var temp = document.getElementById( "depsamount116" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}				
	
	if( isInt( document.getElementById( "depsamount85" ).value ) )
	{
		var temp = document.getElementById( "depsamount85" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	
	
	// RBSY 
	
	if( isInt( document.getElementById( "depsamount114" ).value ) )
	{
		var temp = document.getElementById( "depsamount114" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount108" ).value ) )
	{
		var temp = document.getElementById( "depsamount108" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}				
	
	if( isInt( document.getElementById( "depsamount118" ).value ) )
	{
		var temp = document.getElementById( "depsamount118" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			

	
	/*
	if( isInt( document.getElementById( "deps130" ).value ) )
	{
		var temp = document.getElementById( "deps130" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	*/
	
	//Adolescent Health

	if( isInt( document.getElementById( "depsamount120" ).value ) )
	{
		var temp = document.getElementById( "depsamount120" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}				
	
	/*
	if( isInt( document.getElementById( "deps131" ).value ) )
	{
		var temp = document.getElementById( "deps131" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	*/
	
	//NRHM
	
	if( isInt( document.getElementById( "depsamount92" ).value ) )
	{
		var temp = document.getElementById( "depsamount92" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount93" ).value ) )
	{
		var temp = document.getElementById( "depsamount93" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "depsamount66" ).value ) )
	{
		var temp = document.getElementById( "depsamount66" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "depsamount94" ).value ) )
	{
		var temp = document.getElementById( "depsamount94" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	if( isInt( document.getElementById( "depsamount95" ).value ) )
	{
		var temp = document.getElementById( "depsamount95" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "depsamount96" ).value ) )
	{
		var temp = document.getElementById( "depsamount96" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	
	if( isInt( document.getElementById( "depsamount386" ).value ) )
	{
		var temp = document.getElementById( "depsamount386" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	
	
	/*
	if( isInt( document.getElementById( "deps132" ).value ) )
	{
		var temp = document.getElementById( "deps132" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	*/			
	
	//JSY  National Programes
	
	if( isInt( document.getElementById( "depsamount80" ).value ) )
	{
		var temp = document.getElementById( "depsamount80" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	
	if( isInt( document.getElementById( "depsamount393" ).value ) )
	{
		var temp = document.getElementById( "depsamount393" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	

	/*
	if( isInt( document.getElementById( "deps133" ).value ) )
	{
		var temp = document.getElementById( "deps133" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}				
	*/
	
	// RI and FI Children
	
	if( isInt( document.getElementById( "depsamount88" ).value ) )
	{
		var temp = document.getElementById( "depsamount88" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}				

	if( isInt( document.getElementById( "depsamount90" ).value ) )
	{
		var temp = document.getElementById( "depsamount90" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "depsamount91" ).value ) )
	{
		var temp = document.getElementById( "depsamount91" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	/*
	if( isInt( document.getElementById( "deps134" ).value ) )
	{
		var temp = document.getElementById( "deps134" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	*/
	
	// Family Welfare
	
	if( isInt( document.getElementById( "depsamount101" ).value ) )
	{
		var temp = document.getElementById( "depsamount101" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}				
	
	
	if( isInt( document.getElementById( "depsamount102" ).value ) )
	{
		var temp = document.getElementById( "depsamount102" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	if( isInt( document.getElementById( "depsamount103" ).value ) )
	{
		var temp = document.getElementById( "depsamount103" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	
	if( isInt( document.getElementById( "depsamount98" ).value ) )
	{
		var temp = document.getElementById( "depsamount98" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
		
	
	if( isInt( document.getElementById( "depsamount97" ).value ) )
	{
		var temp = document.getElementById( "depsamount97" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	/*
	if( isInt( document.getElementById( "deps135" ).value ) )
	{
		var temp = document.getElementById( "deps135" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	*/
	
	// Other National
	
	if( isInt( document.getElementById( "depsamount105" ).value ) )
	{
		var temp = document.getElementById( "depsamount105" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	if( isInt( document.getElementById( "depsamount106" ).value ) )
	{
		var temp = document.getElementById( "depsamount106" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	
	if( isInt( document.getElementById( "depsamount388" ).value ) )
	{
		var temp = document.getElementById( "depsamount388" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "depsamount390" ).value ) )
	{
		var temp = document.getElementById( "depsamount390" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	
	if( isInt( document.getElementById( "depsamount122" ).value ) )
	{
		var temp = document.getElementById( "depsamount122" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	
	if( isInt( document.getElementById( "depsamount107" ).value ) )
	{
		var temp = document.getElementById( "depsamount107" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	

	if( isInt( document.getElementById( "depsamount124" ).value ) )
	{
		var temp = document.getElementById( "depsamount124" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	if( isInt( document.getElementById( "depsamount126" ).value ) )
	{
		var temp = document.getElementById( "depsamount126" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	
	if( isInt( document.getElementById( "depsamount128" ).value ) )
	{
		var temp = document.getElementById( "depsamount128" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	/*
	
	if( isInt( document.getElementById( "deps136" ).value ) )
	{
		var temp = document.getElementById( "deps136" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	*/
	
	
	
	
	/*
	
	if( isInt( document.getElementById( "depsamount79" ).value ) )
	{
		var temp = document.getElementById( "depsamount79" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			

		
	
	if( isInt( document.getElementById( "depsamount81" ).value ) )
	{
		var temp = document.getElementById( "depsamount81" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	

	
	if( isInt( document.getElementById( "depsamount84" ).value ) )
	{
		var temp = document.getElementById( "depsamount84" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}				
	
	
	if( isInt( document.getElementById( "depsamount87" ).value ) )
	{
		var temp = document.getElementById( "depsamount87" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	

	
	if( isInt( document.getElementById( "depsamount89" ).value ) )
	{
		var temp = document.getElementById( "depsamount89" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	
	if( isInt( document.getElementById( "depsamount99" ).value ) )
	{
		var temp = document.getElementById( "depsamount99" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			

	if( isInt( document.getElementById( "depsamount100" ).value ) )
	{
		var temp = document.getElementById( "depsamount100" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	

	if( isInt( document.getElementById( "depsamount104" ).value ) )
	{
		var temp = document.getElementById( "depsamount104" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	*/

	
	document.getElementById("depstotalamount").value = gTotal;
	document.getElementById("deps160").value = gTotal;
	
}




//document.getElementById( "depstotalamount" ).value = totalCalculatedAmount;






