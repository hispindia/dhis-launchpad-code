

// save ASHA Activity Details
function saveASHABeneficiaryAndActivityAmountDetailsForm()
{
	var selectedPeriodId = $( '#selectedPeriodId' ).val();
	
	var organisationUnitId = $( '#organisationUnitId' ).val();
	var ashaId = $( '#ashaId' ).val();
	
	
	var result = window.confirm( "Editing Not Possible After Submission. Want to Submit" );
	
	if ( result )
	{
		$.ajax({
		      type: "POST",
		      url: 'saveASHABeneficiaryAndActivityAmountDetails.action',
		      data: getASHABeneficiaryAndActivityAmountDetailsParamsForDiv('ASHABeneficiaryAndActivityAmountDetailsForm'),
		      success: function( json ) {
				
		      callAction( 'selectASHA' );
		      
		      //loadASHABeneficiaryAndActivityAmountDetailsForm( selectedPeriodId, organisationUnitId, ashaId );
		      
		      //alert( selectedPeriodId + "--" + organisationUnitId + " -- " + ashaId );
				//window.location.href='getASHAFacilitatorList.action';
				//loadASHAPerFormanceList( facilitatorId, dataSetId, selectedPeriodId )
		      }
		     });
			
	}	

}


function getASHABeneficiaryAndActivityAmountDetailsParamsForDiv( formDataDiv )
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



var countryIncentiveTotal = 0;

var stateIncentiveTotal = 0;

// Amount Calculation functions
function calculateStateAndTotalAmount88()
{
	
	if( document.getElementById("mobilization1").checked )
	{
		var countryAmountDE20 = $( '#de20' ).val();
		var stateAmountDEPS88 = Math.round(countryAmountDE20/2);
		
		document.getElementById( "deps88" ).value = stateAmountDEPS88;
		//alert( countryIncentiveTotal);
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE20 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS88 );
		//alert( countryIncentiveTotal);
		
		var totalAmount88 = parseInt( countryAmountDE20 ) + parseInt( stateAmountDEPS88  );
		
		document.getElementById( "deps484" ).value = totalAmount88;
		
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}


/*
function calculateStateAndTotalAmount98()
{
	if( document.getElementById("mobilization2").checked )
	{
		var countryAmountDE65 = $( '#de65' ).val();
		var stateAmountDEPS98 = parseInt(countryAmountDE65/2);
		
		document.getElementById( "deps98" ).value = stateAmountDEPS98;
		
		var totalAmount98 = parseInt( countryAmountDE65 ) + parseInt( stateAmountDEPS98  );
		
		document.getElementById( "depsamount98" ).value = totalAmount98;
		
		//alert( countryIncentiveTotal);
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE65 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS98 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
		
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}

function calculateStateAndTotalAmount97()
{
	if( document.getElementById("mobilization3").checked )
	{
		var countryAmountDE64 = $( '#de64' ).val();
		var stateAmountDEPS97 = parseInt( countryAmountDE64/2);
		
		document.getElementById( "deps97" ).value = stateAmountDEPS97;
		
		var totalAmount97 = parseInt( countryAmountDE64 ) + parseInt( stateAmountDEPS97  );
		
		document.getElementById( "depsamount97" ).value = totalAmount97;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE64 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS97 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}

*/

//calculate OtherSession1 Amount
function calculateStateAndTotalAmountOtherSession1()
{
	
	if( document.getElementById("otherSession1").checked )
	{
		var countryAmountDEOtherSession1 = $( '#de479' ).val();
		var stateAmountDEPSOtherSession1 = Math.round(countryAmountDEOtherSession1/2);
		
		document.getElementById( "depsOtherSession1" ).value = stateAmountDEPSOtherSession1;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDEOtherSession1 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPSOtherSession1 );
		
		
		var totalAmountOtherSession1 = parseInt( countryAmountDEOtherSession1 ) + parseInt( stateAmountDEPSOtherSession1  );
		
		document.getElementById( "deps485" ).value = totalAmountOtherSession1;
		
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	calculateGrandTotal();
}


//calculate OtherSession2 Amount
function calculateStateAndTotalAmountOtherSession2()
{
	if( document.getElementById("otherSession2").checked )
	{
		var countryAmountDEOtherSession2 = $( '#de479' ).val();
		var stateAmountDEPSOtherSession2 = Math.round(countryAmountDEOtherSession2/2);
		
		document.getElementById( "depsOtherSession2" ).value = stateAmountDEPSOtherSession2;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDEOtherSession2 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPSOtherSession2 );
		
		
		var totalAmountOtherSession2 = parseInt( countryAmountDEOtherSession2 ) + parseInt( stateAmountDEPSOtherSession2  );
		
		document.getElementById( "deps486" ).value = totalAmountOtherSession2;
		
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	calculateGrandTotal();
}

//calculate OtherSession3 Amount
function calculateStateAndTotalAmountOtherSession3()
{
	if( document.getElementById("otherSession3").checked )
	{
		var countryAmountDEOtherSession3 = $( '#de479' ).val();
		var stateAmountDEPSOtherSession3 = Math.round(countryAmountDEOtherSession3/2);
		
		document.getElementById( "depsOtherSession3" ).value = stateAmountDEPSOtherSession3;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDEOtherSession3 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPSOtherSession3 );
		
		
		var totalAmountOtherSession3 = parseInt( countryAmountDEOtherSession3 ) + parseInt( stateAmountDEPSOtherSession3  );
		
		document.getElementById( "deps487" ).value = totalAmountOtherSession3;
		
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	calculateGrandTotal();
}

//calculate OtherSession4 Amount
function calculateStateAndTotalAmountOtherSession4()
{
	if( document.getElementById("otherSession4").checked )
	{
		var countryAmountDEOtherSession4 = $( '#de479' ).val();
		var stateAmountDEPSOtherSession4 = Math.round(countryAmountDEOtherSession4/2);
		
		document.getElementById( "depsOtherSession4" ).value = stateAmountDEPSOtherSession4;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDEOtherSession4 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPSOtherSession4 );
		
		
		var totalAmountOtherSession4 = parseInt( countryAmountDEOtherSession4 ) + parseInt( stateAmountDEPSOtherSession4  );
		
		document.getElementById( "deps488" ).value = totalAmountOtherSession4;
		
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	calculateGrandTotal();
}













// Meeting
function calculateStateAndTotalAmount92()
{
	if( document.getElementById("meeting1").checked )
	{
		var countryAmountDE29 = $( '#de29' ).val();
		var stateAmountDEPS29 = Math.round( countryAmountDE29/2);
		
		document.getElementById( "deps92" ).value = stateAmountDEPS29;
		
		var totalAmount92 = parseInt( countryAmountDE29 ) + parseInt( stateAmountDEPS29  );
		
		document.getElementById( "deps489" ).value = totalAmount92;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE29 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS29 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}


function calculateStateAndTotalAmount93()
{
	if( document.getElementById("meeting2").checked )
	{
		var countryAmountDE30 = $( '#de30' ).val();
		var stateAmountDEPS30 = Math.round( countryAmountDE30/2);
		
		document.getElementById( "deps93" ).value = stateAmountDEPS30;
		
		var totalAmount93 = parseInt( countryAmountDE30 ) + parseInt( stateAmountDEPS30  );
		
		document.getElementById( "deps490" ).value = totalAmount93;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE30 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS30 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}

function calculateStateAndTotalAmount96()
{
	if( document.getElementById("meeting3").checked )
	{
		var countryAmountDE34 = $( '#de34' ).val();
		var stateAmountDEPS34 = Math.round( countryAmountDE34/2);
		
		document.getElementById( "deps96" ).value = stateAmountDEPS34;
		
		var totalAmount96 = parseInt( countryAmountDE34 ) + parseInt( stateAmountDEPS34  );
		
		document.getElementById( "deps491" ).value = totalAmount96;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE34 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS34 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}

function calculateStateAndTotalAmount120()
{
	if( document.getElementById("meeting4").checked )
	{
		var countryAmountDE119 = $( '#de119' ).val();
		var stateAmountDEPS119 = Math.round( countryAmountDE119/2);
		
		document.getElementById( "deps120" ).value = stateAmountDEPS119;
		
		var totalAmount120 = parseInt( countryAmountDE119 ) + parseInt( stateAmountDEPS119  );
		
		document.getElementById( "deps492" ).value = totalAmount120;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE119 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS119 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}


//calculate OtherMeeting1 Amount
function calculateStateAndTotalAmountOtherMeeting1()
{
	if( document.getElementById("otherMeeting1").checked )
	{
		var countryAmountDEOtherMeeting1 = $( '#de465' ).val();
		var stateAmountDEPSOtherMeeting1 = Math.round(countryAmountDEOtherMeeting1/2);
		
		document.getElementById( "depsOtherMeeting1" ).value = stateAmountDEPSOtherMeeting1;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDEOtherMeeting1 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPSOtherMeeting1 );
		
		
		var totalAmountOtherMeeting1 = parseInt( countryAmountDEOtherMeeting1 ) + parseInt( stateAmountDEPSOtherMeeting1  );
		
		document.getElementById( "deps493" ).value = totalAmountOtherMeeting1;
		
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	calculateGrandTotal();
}


//calculate OtherMeeting2 Amount
function calculateStateAndTotalAmountOtherMeeting2()
{
	if( document.getElementById("otherMeeting2").checked )
	{
		var countryAmountDEOtherMeeting2 = $( '#de465' ).val();
		var stateAmountDEPSOtherMeeting2 = Math.round(countryAmountDEOtherMeeting2/2);
		
		document.getElementById( "depsOtherMeeting2" ).value = stateAmountDEPSOtherMeeting2;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDEOtherMeeting2 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPSOtherMeeting2 );
		
		
		var totalAmountOtherMeeting2 = parseInt( countryAmountDEOtherMeeting2 ) + parseInt( stateAmountDEPSOtherMeeting2  );
		
		document.getElementById( "deps494" ).value = totalAmountOtherMeeting2;
		
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	calculateGrandTotal();
}


//calculate OtherMeeting3 Amount
function calculateStateAndTotalAmountOtherMeeting3()
{
	if( document.getElementById("otherMeeting3").checked )
	{
		var countryAmountDEOtherMeeting3 = $( '#de465' ).val();
		var stateAmountDEPSOtherMeeting3 = Math.round(countryAmountDEOtherMeeting3/2);
		
		document.getElementById( "depsOtherMeeting3" ).value = stateAmountDEPSOtherMeeting3;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDEOtherMeeting3 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPSOtherMeeting3 );
		
		
		var totalAmountOtherMeeting3 = parseInt( countryAmountDEOtherMeeting3 ) + parseInt( stateAmountDEPSOtherMeeting3  );
		
		document.getElementById( "deps495" ).value = totalAmountOtherMeeting3;
		
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	calculateGrandTotal();
}


//SAMPLE
function calculateStateAndTotalAmount94()
{
	if( document.getElementById("sample1").checked )
	{
		var noOfWaterSample = $( '#noOfWaterSample' ).val();
		
		var AmountDE32 = $( '#de32' ).val();
		
		var countryAmountDE32 = AmountDE32 * noOfWaterSample;
		
		document.getElementById( "de32" ).value = countryAmountDE32;
		
		var stateAmountDEPS32 = Math.round( countryAmountDE32/2);
		
		document.getElementById( "deps94" ).value = stateAmountDEPS32;
		
		var totalAmount94 = parseInt( countryAmountDE32 ) + parseInt( stateAmountDEPS32  );
		
		document.getElementById( "deps496" ).value = totalAmount94;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE32 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS32 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}

function calculateStateAndTotalAmount122()
{
	var noOfSaltSample = $( '#noOfSaltSample' ).val();
	
	if( document.getElementById("sample2").checked &&  noOfSaltSample >= 50 )
	{
		
		var countryAmountDE35 = $( '#de35' ).val();
		var stateAmountDEPS35 = Math.round( countryAmountDE35/2);
		
		document.getElementById( "deps122" ).value = stateAmountDEPS35;
		
		var totalAmount122 = parseInt( countryAmountDE35 ) + parseInt( stateAmountDEPS35  );
		
		document.getElementById( "deps497" ).value = totalAmount122;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE35 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS35 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}

//TA/DA
function calculateStateAndTotalAmount396()
{
	if( document.getElementById("taDa1").checked )
	{
		var noOfDaysInTraining = $( '#noOfDaysInTraining' ).val();
		
		var amountDE395 = $( '#de395' ).val();
		
		var countryAmountDE395 = amountDE395 * noOfDaysInTraining;
		
		document.getElementById( "de395" ).value = countryAmountDE395;
		
		var stateAmountDEPS395 = Math.round( countryAmountDE395/2);
		
		document.getElementById( "deps396" ).value = stateAmountDEPS395;
		
		var totalAmount396 = parseInt( countryAmountDE395 ) + parseInt( stateAmountDEPS395  );
		
		document.getElementById( "deps498" ).value = totalAmount396;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE395 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS395 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
		
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}

function calculateStateAndTotalAmount386()
{
	if( document.getElementById("taDa2").checked )
	{
		var countryAmountDE387 = $( '#de387' ).val();
		var stateAmountDEPS387 = Math.round( countryAmountDE387/2);
		
		document.getElementById( "deps386" ).value = stateAmountDEPS387;
		
		var totalAmount386 = parseInt( countryAmountDE387 ) + parseInt( stateAmountDEPS387  );
		
		document.getElementById( "deps499" ).value = totalAmount386;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE387 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS387 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}

// AWARD
function calculateStateAndTotalAmount124()
{
	if( document.getElementById("award1").checked )
	{
		var countryAmountDE123 = $( '#de123' ).val();
		var stateAmountDEPS123 = Math.round( countryAmountDE123/2);
		
		document.getElementById( "deps124" ).value = stateAmountDEPS123;
		
		var totalAmount124 = parseInt( countryAmountDE123 ) + parseInt( stateAmountDEPS123  );
		
		document.getElementById( "deps500" ).value = totalAmount124;
		
		countryIncentiveTotal = parseInt( countryIncentiveTotal ) + parseInt( countryAmountDE123 );
		stateIncentiveTotal = parseInt( stateIncentiveTotal ) + parseInt( stateAmountDEPS123 );
		//alert( countryIncentiveTotal);
		calculateCountryIncentiveTotal();
		calculateStateIncentiveTotal();
	}
	
	//calculateNRHMGroupTotal();
	calculateGrandTotal();
}

function calculateGrandTotal()
{
	var gTotal = 0;
	var tempTotal = 0;
	
	//var beneficiaryAmountTempTotal = 0;
	
	if( isInt( document.getElementById( "totalBeneficiaryAmount" ).value ) )
	{
		var temp = document.getElementById( "totalBeneficiaryAmount" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	if( isInt( document.getElementById( "deps484" ).value ) )
	{
		var temp = document.getElementById( "deps484" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	/*
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
	
	*/
	
	
	
	
	if( isInt( document.getElementById( "deps485" ).value ) )
	{
		var temp = document.getElementById( "deps485" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "deps486" ).value ) )
	{
		var temp = document.getElementById( "deps486" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	

	if( isInt( document.getElementById( "deps487" ).value ) )
	{
		var temp = document.getElementById( "deps487" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "deps488" ).value ) )
	{
		var temp = document.getElementById( "deps488" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	if( isInt( document.getElementById( "deps489" ).value ) )
	{
		var temp = document.getElementById( "deps489" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "deps490" ).value ) )
	{
		var temp = document.getElementById( "deps490" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		

	if( isInt( document.getElementById( "deps491" ).value ) )
	{
		var temp = document.getElementById( "deps491" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "deps492" ).value ) )
	{
		var temp = document.getElementById( "deps492" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "deps493" ).value ) )
	{
		var temp = document.getElementById( "deps493" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "deps494" ).value ) )
	{
		var temp = document.getElementById( "deps494" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "deps495" ).value ) )
	{
		var temp = document.getElementById( "deps495" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
		

	if( isInt( document.getElementById( "deps496" ).value ) )
	{
		var temp = document.getElementById( "deps496" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}		
	
	if( isInt( document.getElementById( "deps497" ).value ) )
	{
		var temp = document.getElementById( "deps497" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}	
	
	if( isInt( document.getElementById( "deps498" ).value ) )
	{
		var temp = document.getElementById( "deps498" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	if( isInt( document.getElementById( "deps499" ).value ) )
	{
		var temp = document.getElementById( "deps499" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	if( isInt( document.getElementById( "deps500" ).value ) )
	{
		var temp = document.getElementById( "deps500" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}			
	
	if( isInt( document.getElementById( "deps501" ).value ) )
	{
		var temp = document.getElementById( "deps501" ).value;
		tempTotal = parseInt( temp );
		gTotal = gTotal + tempTotal;
	}
	
	
	document.getElementById("depstotalamount").value = gTotal;
	document.getElementById("deps160").value = gTotal;

	
}
// for Country Total
function calculateCountryIncentiveTotal()
{
	var beneficiaryAmountTempTotal = 0;
	var tempTotal = 0;
	if( isInt( document.getElementById( "beneficiaryAmount" ).value ) )
	{
		var beneficiaryAmountTemp = document.getElementById( "beneficiaryAmount" ).value;
		beneficiaryAmountTempTotal = parseInt( beneficiaryAmountTemp );
		tempTotal = countryIncentiveTotal + beneficiaryAmountTempTotal;
		
	}
	
	document.getElementById("incentiveTotalAmount").value = tempTotal;
}


// for State Total
function calculateStateIncentiveTotal()
{
	var stateAmountTempTotal = 0;
	var tempTotal = 0;
	
	if( isInt( document.getElementById( "stateBeneficiaryAmount" ).value ) )
	{
		var stateAmountTemp = document.getElementById( "stateBeneficiaryAmount" ).value;
		stateAmountTempTotal = parseInt( stateAmountTemp );
		tempTotal = stateIncentiveTotal + stateAmountTempTotal;
		
	}
	
	document.getElementById("stateIncentiveAmount").value = tempTotal;
}
