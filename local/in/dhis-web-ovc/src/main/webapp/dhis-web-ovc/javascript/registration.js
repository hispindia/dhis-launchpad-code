
// ----------------------------------------------------------------
// organization Unit Selected
// ----------------------------------------------------------------
/*
function organisationUnitSelected( orgUnits, orgUnitNames )
{	
	document.getElementById('selectedOrgunitID').value = orgUnits;
	setFieldValue("selectedOrgunitText", orgUnitNames[0]);
	setFieldValue("orgunitName", orgUnitNames[0]);
}

selection.setListenerFunction( organisationUnitSelected );


function registrationForm()
{
	var orgObject = document.getElementById('selectedOrgunitID');
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
		
	if( orgUnitId == "" || orgObject == null )
	{		
		showWarningMessage( i18n_please_select_orgUnit );
	}
	else
	{
		window.location.href = "getRegistrationForm.action?orgUnitId=" + orgUnitId;;
	}
	
}
*/

function validateOVCRegistration()
{
	var firstNameObject = document.getElementById('firstName');
	var firstName = document.getElementById('firstName').value;
	
	var middleNameObject = document.getElementById('middleName');
	var middleName = document.getElementById('middleName').value;
	
	var lastNameObject = document.getElementById('lastName');
	var lastName = document.getElementById('lastName').value;
	
	
	var genderObject = document.getElementById('gender');
	var genderValue = genderObject.options[ genderObject.selectedIndex ].value;
	
	
	var dateOfBirthObject = document.getElementById('dateOfBirth');
	var dateOfBirth = document.getElementById('dateOfBirth').value;
	
	
		$.post("validateOVCRegistration.action",
			{
				firstName : firstName,
	 			middleName : middleName,
	 			lastName : lastName,
	 			gender : genderValue,
	 			dateOfBirth : dateOfBirth
			},
			function (data)
			{
				validationCompleted(data);
			},'xml');

		return false;	
}

function validationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		document.forms['registrationForm'].submit();
		//return true;
	} 
	else if ( type == 'input' ) 
	{
		alert( messageElement.firstChild.nodeValue );
	}
}

// remove Submitted OVC
function removeSubmittedOVC( patientId, fullName )
{
	removeItem( patientId, fullName, i18n_confirm_delete, 'removeOVC.action' );
	//callAction( 'selectASHA' );
}

//remove Rejected OVC
function removeRejectedOVC( patientId, fullName )
{
	removeItem( patientId, fullName, i18n_confirm_delete, 'removeOVC.action' );
	//callAction( 'selectASHA' );
}



function showOVCRegistrationDetails( ovcId )
{
	$('#detailsInfo').load("getOVCRegistrationDetails.action", 
		{
			ovcId:ovcId
		}
		, function( ){
			
		}).dialog({
			title: i18n_ovcRegistration_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 900,
			height: 600
		});
}

function exportOVCRegistrationHistory( patientId, type )
{
	var url = "getOVCRegistrationHistory.action?patientId=" + patientId + "&type=" + type;
	window.location.href = url;
}


function changeSchool()
{	
	var schoolIdValue = $( '#attr51' ).val();
	var orgUnitId = $( '#orgUnitId' ).val();
	
	var schoolIdObject = document.getElementById('attr51');
	var schoolName = schoolIdObject.options[ schoolIdObject.selectedIndex ].value;
	
	//alert( schoolIdObject + "-- " + schoolName + "--" + schoolIdValue );
	
	if ( schoolIdObject == null || schoolName == null || schoolIdValue == "" || orgUnitId == null || orgUnitId == "")
	{
		document.getElementById( "attr44" ).value = "";
		document.getElementById( "attr50" ).value = "";
		var list = document.getElementById("attr50");
		clearList( list );
		return false;
	}
	
	
	$.post("getSchoolDetail.action",
		{
			schoolName:schoolName,
			orgUnitId:orgUnitId
		},
		function (data)
		{
			schoolDetailsRecieved(data);
		},'xml');
}


function schoolDetailsRecieved(data)
{	
	var maxClass = "";
	document.getElementById( "school_level_44" ).value = data.getElementsByTagName( 'schoolLevel' )[0].firstChild.nodeValue;
	document.getElementById( "attr44" ).value = data.getElementsByTagName( 'schoolLevel' )[0].firstChild.nodeValue;
	document.getElementById( "attr50" ).value = data.getElementsByTagName( 'classLevel' )[0].firstChild.nodeValue;
	
	maxClass = data.getElementsByTagName( 'classLevel' )[0].firstChild.nodeValue;
	
	
	var attr50 = document.getElementById("attr50");
	clearList( attr50 );
	
	
	attr50.options[0] = new Option( "Please Select ", "" , false, false);
	
	for ( var i = 1; i <= maxClass; i++ )
	{
		var option = document.createElement("option");
		option.value = i;
		option.text = i;
		option.title = i;
		
		if( selectedClassValue == i )
		{
			option.selected = true;
		}
		
		attr50.add(option, null);
	} 
}


// some another validation

function schoolLevelValidation()
{
    var schoolLevelObject = document.getElementById('attr44');
	var schoolLevelValue = schoolLevelObject.options[ schoolLevelObject.selectedIndex ].value;
	
	//alert( schoolLevelValue );
	
	if( schoolLevelValue == 49 )
	{
		document.getElementById( "attr50" ).value = "";
	    document.getElementById( "attr50" ).disabled = true;
	    document.getElementById( "attr51" ).value = "";
	    document.getElementById( "attr51" ).disabled = true;
	} 
	else
	{
	    document.getElementById( "attr50" ).disabled = false;
	    document.getElementById( "attr51" ).disabled = false;
	}  
	
}


function hivStatusValidation()
{
    var hivStatusObject = document.getElementById('attr52');
	var hivStatuslValue = hivStatusObject.options[ hivStatusObject.selectedIndex ].value;
	
	//alert( schoolLevelValue );
	
	if( hivStatuslValue == 54 || hivStatuslValue == 55 )
	{
		document.getElementById( "attr56" ).value = "";
	    document.getElementById( "attr56" ).disabled = true;
	    document.getElementById( "attr60" ).value = "";
	    document.getElementById( "attr60" ).disabled = true;
	} 
	else
	{
	    document.getElementById( "attr56" ).disabled = false;
	    document.getElementById( "attr60" ).disabled = false;
	}  
	
}

/*
function checkPriority( selectedId, selectedValue )
{
	//alert( selectedId + " -- " + selectedValue );
	
	if( selectedId == "attr583" )
	{
		//var selectedOptionIndex1 = document.getElementById("attr590").selectedIndex;
		//var selectedOptionIndex2 = document.getElementById("attr597").selectedIndex;
		
		var obj1 = document.getElementById('attr590');
		var obj2 = document.getElementById('attr597');
		
		//var hivStatuslValue = obj1.options[ obj1.selectedIndex ].value;
		
		
    	if( obj1.options[ obj1.selectedIndex ].text = selectedValue )
    	{	
    		//alert( " value is ppppp " + obj1.options[ obj1.selectedIndex ].text );
    		//alert( "Priotiry Set for attr590 " );
    		document.getElementById( selectedId ).options[ 0 ].selected = true; 
    		return false;
    	}
    	else if( obj2.options[ obj2.selectedIndex ].value = selectedValue )
    	{
    		//alert( "Priotiry Set for attr597 " );
    		document.getElementById( selectedId ).options[ 0 ].selected = true;
    		return false;
    	}
    }
    else if( selectedId == "attr590")
    {
    	
    	//var selectedOptionIndex1 = document.getElementById("attr583").selectedIndex;
		//var selectedOptionIndex2 = document.getElementById("attr597").selectedIndex;
    	
		var obj1 = document.getElementById('attr583');
		var obj2 = document.getElementById('attr597');
		
    	
    	if( obj1.options[ obj1.selectedIndex ].value = selectedValue )
    	{
    		alert( "Priotiry Set for attr583 " );
    		document.getElementById( selectedId ).options[ 0 ].selected = true; 
    		return false;
    	}
    	else if( obj2.options[ obj2.selectedIndex ].value = selectedValue )
    	{
    		alert( "Priotiry Set for attr597 " );
    		document.getElementById( selectedId ).options[ 0 ].selected = true;
    		return false;
    	}

    }
    else if( selectedId == "attr597" )
    {
    	//var selectedOptionIndex1 = document.getElementById("attr583").selectedIndex;
		//var selectedOptionIndex2 = document.getElementById("attr590").selectedIndex;
    	
    	var obj1 = document.getElementById('attr583');
		var obj2 = document.getElementById('attr597');
    	
    	if( obj1.options[ obj1.selectedIndex ].value = selectedValue )
    	{
    		alert( "Priotiry Set for attr583 " );
    		document.getElementById( selectedId ).options[ 0 ].selected = true; 
    		return false;
    	}
    	else if( obj2.options[ obj2.selectedIndex ].value = selectedValue )
    	{
    		alert( "Priotiry Set for attr590 " );
    		document.getElementById( selectedId ).options[ 0 ].selected = true;
    		return false;
    	}

    }
    
    	
}
*/


















function childHIVPositive()
{
    var childHIVPositiveValue = document.getElementById('attr618').value;
	
    //alert( childHIVPositiveValue ); // on always
    
    //alert( document.getElementById("attr618").checked );
    
	if( document.getElementById("attr618").checked==true )
	{
		 document.getElementById( "attr619" ).disabled = false;
		
	}    
	else
	{
		document.getElementById( "attr619" ).value = "";
	    document.getElementById( "attr619" ).disabled = true;
	}
}


// Goverment Name

function specifyGovernmentName()
{
    
	if( document.getElementById("attr620").checked==true )
	{
		 document.getElementById( "attr621" ).disabled = false;
		
	}    
	else
	{
		document.getElementById( "attr621" ).value = "";
	    document.getElementById( "attr621" ).disabled = true;
	}
}

//NGO Name

function specifyNGOName()
{
    
	if( document.getElementById("attr622").checked==true )
	{
		 document.getElementById( "attr623" ).disabled = false;
		
	}    
	else
	{
		document.getElementById( "attr623" ).value = "";
	    document.getElementById( "attr623" ).disabled = true;
	}
}

//CBO Name
function specifyCBOName()
{
    
	if( document.getElementById("attr624").checked==true )
	{
		 document.getElementById( "attr625" ).disabled = false;
		
	}    
	else
	{
		document.getElementById( "attr625" ).value = "";
	    document.getElementById( "attr625" ).disabled = true;
	}
}


//Others Name
function specifyOthersName()
{
    
	if( document.getElementById("attr627").checked==true )
	{
		 document.getElementById( "attr628" ).disabled = false;
		
	}    
	else
	{
		document.getElementById( "attr628" ).value = "";
	    document.getElementById( "attr628" ).disabled = true;
	}
}


function calculateAge()
{
	var dateOfBirth = document.getElementById( "dateOfBirth" ).value;
    
	//alert( dateOfBirth );
	
	var birthday = new Date( dateOfBirth );
	var now = new Date();
	
	var age = now.getTime() - birthday.getTime();
	
	// 1000 * 60 * 60 * 24 * 365 = 31536000000
	
	var currentAge = Math.round( age/31536000000 );

	//var yearDiff = now.getFullYear() - birthday.getFullYear();

	//alert( yearDiff );
	
	if ( currentAge >= 18 ) 
	{ 	
		//alert( "More than 18 year " + currentAge )
		showWarningMessage( i18n_age_more_than_18_year );
		document.getElementById( "dateOfBirth" ).value = "";
		
	} 
	
	/*
	if ( age < ( 1000 * 60 * 60 * 24 * 7 * 365 * 18 ) ) 
	{ 	
		alert( "less than 18 year " + age )
		// number of milliseconds in 18 years
	   document.write('not over 18');
	} 
	else 
	{
		alert( "More than 18 year " + age )
		document.write('over 18');
	}
	*/
		
	/*
	var date1 = getDateFromFormat("1999-10-10", "YYYY-MM-DD");
	var date2 = getDateFromFormat("2012-10-10", "YYYY-MM-DD");
	
	var millisecondsPerSecond = 1000;
	var millisecondsPerMinute = millisecondsPerSecond * 60;
	var millisecondsPerHour = millisecondsPerMinute = 60;
	var millisecondsPerDay = millisecondsPerHour * 24;
	var millisecondsPerYear = millisecondsPerDay * 365.26;

	var years = Math.round((date2 - date1) / millisecondsPerYear);
	*/
	
	
}


/*
var ds='2002-09-23';
var today_date = new Date();
alert(today_date);

Date.prototype.yyyymmdd = function() 
{
	var mm = (this.getMonth()+1).toString(); // getMonth() is zero-based
	var dd  = this.getDate().toString();
	var dt = yyyy + "-" +( mm[1]? mm: "0" + mm[0]) + "-" + (dd[1]?dd:"0"+dd[0]);// padding
	
	var num_years = diff_date/31536000000;
	alert(num_years);
	if (num_years>18)
	{
		alert (num_years);
	}
	else
	{
		alert ("i m not 18");
    }

}
*/

function changeStatus()
{
	//var updateStatus = document.getElementById( "update" ).value;
	//alert( updatePrivilege );
	
	if( updatePrivilege == "yes" )
	{
		var rejectReasonObject = document.getElementById('attr301');
		var rejectReasonValue = rejectReasonObject.options[ rejectReasonObject.selectedIndex ].value;
		
		if( rejectReasonValue == 304 )
		{
			showById('trattr404');
			jQuery("#attr404").addClass('required',true);
		} 
		else
		{
			document.getElementById( "attr404" ).value = "";
			jQuery("#attr404").removeClass();
			hideById('trattr404');
			
		}  
	}
}

// show Update OVC Registration Form 

function showUpdateOVCRegistrationForm( ovcId )
{
	//alert( ovcId );
	
	//hideById('areaInformationDiv');
	hideById('searchDiv');
	hideById('advanced-search');
	hideById('listOVCSubmittedDiv');
	
	setInnerHTML('updateOVCRegistrationFormDiv', '');
	jQuery('#loaderDiv').show();
	jQuery('#updateOVCRegistrationFormDiv').load('showUpdateOVCRegistrationForm.action',
		{
			id:ovcId
		}, 
		function()
		{
			jQuery('#loaderDiv').hide();
			showById('updateOVCRegistrationFormDiv');
		});
	
	//window.location.href = "showUpdateRegistrationForm.action?id=" + ovcId;
}


function validateUpdateOVCRegistration()
{
	var firstNameObject = document.getElementById('firstName');
	var firstName = document.getElementById('firstName').value;
	
	var middleNameObject = document.getElementById('middleName');
	var middleName = document.getElementById('middleName').value;
	
	var lastNameObject = document.getElementById('lastName');
	var lastName = document.getElementById('lastName').value;
	
	
	var genderObject = document.getElementById('gender');
	var genderValue = genderObject.options[ genderObject.selectedIndex ].value;
	
	
	var dateOfBirthObject = document.getElementById('dateOfBirth');
	var dateOfBirth = document.getElementById('dateOfBirth').value;
	
	
		$.post("validateOVCRegistration.action",
			{
				ovcId : byId('ovcId').value,
				firstName : firstName,
	 			middleName : middleName,
	 			lastName : lastName,
	 			gender : genderValue,
	 			dateOfBirth : dateOfBirth
			},
			function (data)
			{
				updateValidationCompleted(data);
			},'xml');

		return false;	
}

function updateValidationCompleted(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		updateOVCRegistrationData();
	} 
	else if ( type == 'input' ) 
	{
		alert( messageElement.firstChild.nodeValue );
	}
}

function updateOVCRegistrationData()
{
	$.ajax({
      type: "POST",
      url: 'updateOVCRegistrationData.action',
      data: getParamsForDiv('updateOVCRegistrationFormDiv'),
      success: function( json ) {
		
		loadSubmittedOVCList();
		//listAllSubmittedOVC();
		//window.location.href = "getSubmittedList.action";
      }
     });
}





function getParamsForDiv( formDataDiv )
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


//for Rejection


function showUpdateOVCRegistrationFormInApprove( ovcId )
{
	
	//hideById('ovcManagementDiv');
	hideById('searchDiv');
	hideById('advanced-search');
	hideById('detailsInfo');
	
	
	hideById('listOVCSubmittedDiv');
	hideById('listOVCRejectedDiv');
	hideById('listOVCApprovedDiv');
	hideById('listOVCExitRequestedDiv');
	hideById('listOVCInActivedDiv');
	
	setInnerHTML('updateOVCRegistrationFormInApproveDiv', '');
	
	jQuery('#updateOVCRegistrationFormInApproveDiv').load('showUpdateOVCRegistrationInApproveForm.action',
		{
			id:ovcId
		}, 
		function()
		{
			showById('updateOVCRegistrationFormInApproveDiv');
		});
	
	//window.location.href = "showUpdateRegistrationForm.action?id=" + ovcId;
}




function validateUpdateOVCRegistrationInApprove()
{
	var firstNameObject = document.getElementById('firstName');
	var firstName = document.getElementById('firstName').value;
	
	var middleNameObject = document.getElementById('middleName');
	var middleName = document.getElementById('middleName').value;
	
	var lastNameObject = document.getElementById('lastName');
	var lastName = document.getElementById('lastName').value;
	
	
	var genderObject = document.getElementById('gender');
	var genderValue = genderObject.options[ genderObject.selectedIndex ].value;
	
	
	var dateOfBirthObject = document.getElementById('dateOfBirth');
	var dateOfBirth = document.getElementById('dateOfBirth').value;
	
	
		$.post("validateOVCRegistration.action",
			{
				ovcId : byId('ovcId').value,
				firstName : firstName,
	 			middleName : middleName,
	 			lastName : lastName,
	 			gender : genderValue,
	 			dateOfBirth : dateOfBirth
			},
			function (data)
			{
				updateValidationCompletedInApprove(data);
			},'xml');

		return false;	
}

function updateValidationCompletedInApprove(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		updateOVCRegistrationDataInApprove();
	} 
	else if ( type == 'input' ) 
	{
		alert( messageElement.firstChild.nodeValue );
	}
}


function updateOVCRegistrationDataInApprove()
{
	$.ajax({
      type: "POST",
      url: 'updateOVCRegistrationData.action',
      data: getParamsForDiv('updateOVCRegistrationFormInApproveDiv'),
      success: function( json ) {
		loadApprovedOVCList();
		//window.location.href = "getApprovedList.action";
      }
     });
}


function showOVCRegistrationDetailsInActive( ovcId )
{
	$('#detailsInfo').load("getOVCRegistrationDetailsInActive.action", 
		{
			ovcId:ovcId
		}
		, function( ){
			
		}).dialog({
			title: i18n_ovcRegistration_details,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 900,
			height: 600
		});
}

// OVC EXIT FORM

function showOVCExitForm( ovcId )
{	
	
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('detailsInfo');
	
	hideById('listOVCSubmittedDiv');
	hideById('listOVCRejectedDiv');
	hideById('listOVCApprovedDiv');
	hideById('listOVCExitRequestedDiv');
	hideById('updateOVCRegistrationFormInApproveDiv');
	hideById('listOVCInActivedDiv');
	
	setInnerHTML('ovcExitFormDiv', '');
	
	jQuery('#ovcExitFormDiv').load('showOVCExitForm.action',
	{
		id:ovcId
	}, 
	function()
	{
		showById('ovcExitFormDiv');
	});	
}

function validateOVCExitForm() 
{	
	var result = window.confirm( "You want to Save Exit Form" );
	
	if ( result ) 
	{
		saveOVCExitFormData();
	} 
}


function saveOVCExitFormData()
{
	$.ajax({
      type: "POST",
      url: 'saveOVCExitFormData.action',
      data: getParamsForDiv('ovcExitFormDiv'),
      success: function( json ) {
		//window.location.href = "getApprovedList.action";
		loadApprovedOVCList();
      }
     });
}


function exitReasonChange()
{	
	var exirReasonObject = document.getElementById('attr450');
	var exitReasonValue = exirReasonObject.options[ exirReasonObject.selectedIndex ].value;
	
	//alert( exitReasonValue );
	
	if( exitReasonValue == 452 )
	{
		showById('trattr456');
		hideById('trattr605');
		jQuery("#attr456").addClass('required',true);
		jQuery("#attr605").removeClass();
	} 	
	else if( exitReasonValue == 455 )
	{
		hideById('trattr456');
		showById('trattr605');
		jQuery("#attr605").addClass('required',true);
		jQuery("#attr456").removeClass();
		jQuery("#attr606").removeClass();
		hideById('trattr606');
	}	
	else
	{
		document.getElementById( "attr456" ).value = "";
		document.getElementById( "attr605" ).value = "";
		document.getElementById( "attr606" ).value = "";
		
		hideById('trattr456');
		hideById('trattr605');
		hideById('trattr606');
		
		jQuery("#attr456").removeClass();
		jQuery("#attr605").removeClass();
		jQuery("#attr606").removeClass();		
		
		//jQuery("#attr404").removeClass();
		//hideById('trattr404');		
	} 	
}

function otherReasonDisengagement()
{
	var disengagementReasonObject = document.getElementById('attr456');
	var disengagementReasonValue = disengagementReasonObject.options[ disengagementReasonObject.selectedIndex ].value;
	
	if( disengagementReasonValue == 467 )
	{
		showById('trattr606');
		//hideById('trattr456');
		hideById('trattr605');
		
		document.getElementById( "attr605" ).value = "";
		
		jQuery("#attr606").addClass('required',true);
		jQuery("#attr605").removeClass();
	}	
	else
	{
		//document.getElementById( "attr456" ).value = "";
		//document.getElementById( "attr605" ).value = "";
		document.getElementById( "attr606" ).value = "";
		
		hideById('trattr606');
		//hideById('trattr456');
		//showById('trattr605');
		
		//jQuery("#attr605").addClass('required',true);
		jQuery("#attr606").removeClass();		
	}	
}


// Exit Pending Requested Related Functions

function showOVCExitPendingDetails( ovcId )
{
	$('#detailsInfo').load("getOVCExitPendingDetails.action", 
		{
			ovcId:ovcId
		}
		, function( ){
			
		}).dialog({
			title: i18n_ovc_exit_pending_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 900,
			height: 600
		});
}


// Update Exit Form

function showUpdateOVCExitForm( ovcId )
{
	hideById('searchDiv');
	hideById('advanced-search');
	hideById('listOVCExitRequestedDiv');
	
	setInnerHTML('updateOVCExitFormDiv', '');
	
	jQuery('#updateOVCExitFormDiv').load('showUpdateOVCExitForm.action',
		{
			id:ovcId
		}, 
		function()
		{
			showById('updateOVCExitFormDiv');
		});
	
}

function validateUpdateOVCExitForm() 
{	
	var result = window.confirm( "You want to Save Exit Form" );
	
	if ( result ) 
	{
		updateOVCExitFormData();
	} 
}

function updateOVCExitFormData()
{
	$.ajax({
      type: "POST",
      url: 'updateOVCExitForm.action',
      data: getParamsForDiv('updateOVCExitFormDiv'),
      success: function( json ) {
		//window.location.href = "getExitPendingList.action";
		loadExitPendingOVCList();
      }
     });
}


function showOVCInActiveDetails( ovcId )
{
	$('#detailsInfo').load("getOVCInActiveDetails.action", 
		{
			ovcId:ovcId
		}
		, function( ){
			
		}).dialog({
			title: i18n_ovc_in_active_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 900,
			height: 600
		});
}

// for Rejection


function showUpdateOVCRegistrationFormInRejection( ovcId )
{
	//alert( ovcId );
	
	//hideById('ovcManagementDiv');
	
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listOVCSubmittedDiv');
	hideById('listOVCRejectedDiv');
	hideById('listOVCApprovedDiv');
	hideById('listOVCExitRequestedDiv');
	hideById('listOVCInActivedDiv');
	
	setInnerHTML('updateOVCRegistrationFormInRejectionDiv', '');
	
	jQuery('#updateOVCRegistrationFormInRejectionDiv').load('showUpdateOVCRegistrationInRejectionForm.action',
		{
			id:ovcId
		}, 
		function()
		{
			showById('updateOVCRegistrationFormInRejectionDiv');
		});
	
}


function validateUpdateOVCRegistrationInRejection()
{
	var firstNameObject = document.getElementById('firstName');
	var firstName = document.getElementById('firstName').value;
	
	var middleNameObject = document.getElementById('middleName');
	var middleName = document.getElementById('middleName').value;
	
	var lastNameObject = document.getElementById('lastName');
	var lastName = document.getElementById('lastName').value;
	
	
	var genderObject = document.getElementById('gender');
	var genderValue = genderObject.options[ genderObject.selectedIndex ].value;
	
	
	var dateOfBirthObject = document.getElementById('dateOfBirth');
	var dateOfBirth = document.getElementById('dateOfBirth').value;
	
	
		$.post("validateOVCRegistration.action",
			{
				ovcId : byId('ovcId').value,
				firstName : firstName,
	 			middleName : middleName,
	 			lastName : lastName,
	 			gender : genderValue,
	 			dateOfBirth : dateOfBirth
			},
			function (data)
			{
				updateValidationCompletedInRejection(data);
			},'xml');

		return false;	
}

function updateValidationCompletedInRejection(messageElement) 
{	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if ( type == 'success' ) 
	{
		updateOVCRegistrationDataInRejection();
	} 
	else if ( type == 'input' ) 
	{
		alert( messageElement.firstChild.nodeValue );
	}
}


function updateOVCRegistrationDataInRejection()
{
	$.ajax({
      type: "POST",
      url: 'updateOVCRegistrationData.action',
      data: getParamsForDiv('updateOVCRegistrationFormInRejectionDiv'),
      success: function( json ) {
		//window.location.href = "getRejectedList.action";
		loadRejectedOVCList();
      }
     });
}


function showOVCRegistrationDetailsInRejection( ovcId )
{
	$('#detailsInfo').load("getOVCRegistrationDetailsInRejection.action", 
		{
			ovcId:ovcId
		}
		, function( ){
			
		}).dialog({
			title: i18n_ovcRegistration_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 900,
			height: 600
		});
}



function showOVCForm1A( ovcId )
{
	
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('detailsInfo');
	
	hideById('listOVCSubmittedDiv');
	hideById('listOVCRejectedDiv');
	hideById('listOVCApprovedDiv');
	hideById('listOVCExitRequestedDiv');
	hideById('updateOVCRegistrationFormInApproveDiv');
	hideById('listOVCInActivedDiv');
	hideById('ovcExitFormDiv');
	
	setInnerHTML('ovcForm1ADiv', '');

	jQuery('#ovcForm1ADiv').load('showOVCForm1A.action',
		{
			id:ovcId,
		}, 
		function()
		{
			showById('ovcForm1ADiv');
		});	
	
}


function showOVCCareGiverForm( ovcId )
{
	
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('detailsInfo');
	
	hideById('listOVCSubmittedDiv');
	hideById('listOVCRejectedDiv');
	hideById('listOVCApprovedDiv');
	hideById('listOVCExitRequestedDiv');
	hideById('updateOVCRegistrationFormInApproveDiv');
	hideById('listOVCInActivedDiv');
	hideById('ovcExitFormDiv');
	
	setInnerHTML('ovcForm1ADiv', '');

	jQuery('#ovcForm1ADiv').load('showOVCCareGiverForm.action',
		{
			id:ovcId,
		}, 
		function()
		{
			showById('ovcForm1ADiv');
		});	
	
}


function showOVCHouseHoldAssessmentForm( ovcId )
{
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('detailsInfo');
	
	hideById('listOVCSubmittedDiv');
	hideById('listOVCRejectedDiv');
	hideById('listOVCApprovedDiv');
	hideById('listOVCExitRequestedDiv');
	hideById('updateOVCRegistrationFormInApproveDiv');
	hideById('listOVCInActivedDiv');
	hideById('ovcExitFormDiv');
	hideById('ovcForm1ADiv');
	
	setInnerHTML('ovcHouseHoldAssessmentFormDiv', '');

	jQuery('#ovcHouseHoldAssessmentFormDiv').load('showOVCHouseHoldAssessmentUpdateForm.action',
		{
			id:ovcId
		}, 
		function()
		{
			showById('ovcHouseHoldAssessmentFormDiv');
		});	
	
}
