
// Disable caching for ajax requests in general 




$( document ).ready( function() {
	$.ajaxSetup ({
    	cache: false
	});
} );


function addEventForASHAForm( divname )
{
	jQuery("#" + divname + " [id=checkDuplicateBtn]").click(function() {
		checkDuplicate( divname );
	});
	
	jQuery("#" + divname + " [id=dobType]").change(function() {
		dobTypeOnChange( divname );
	});
}

//----------------------------------------------------------------
//	Common function in ASHA
//----------------------------------------------------------------


function dobTypeOnChange( container ){

	var type = jQuery('#' + container + ' [id=dobType]').val();
	if(type == 'V' || type == 'D')
	{
		jQuery('#' + container + ' [id=age]').rules("remove");
		jQuery('#' + container + ' [id=age]').css("display","none");
		
		jQuery('#' + container + ' [id=birthDate]').rules("add",{required:true});
		datePickerValid( container + ' [id=birthDate]' );
		jQuery('#' + container + ' [id=birthDate]').css("display","");
	}
	else if(type == 'A')
	{
		jQuery('#' + container + ' [id=age]').rules("add",{required:true, number: true});
		jQuery('#' + container + ' [id=age]').css("display","");
		
		jQuery('#' + container + ' [id=birthDate]').rules("remove","required");
		$('#' + container+ ' [id=birthDate]').datepicker("destroy");
		jQuery('#' + container + ' [id=birthDate]').css("display","none");
	}
	else 
	{
		jQuery('#' + container + ' [id=age]').rules("remove");
		jQuery('#' + container + ' [id=age]').css("display","");
		
		jQuery('#' + container + ' [id=birthDate]').rules("remove","required");
		$('#' + container+ ' [id=birthDate]').datepicker("destroy");
		jQuery('#' + container + ' [id=birthDate]').css("display","none");
	}
}


//-----------------------------------------------------------------------------
//check duplicate patient
//-----------------------------------------------------------------------------

function checkDuplicate( divname )
{
	$.postUTF8( 'validateASHA.action', 
		{
			fullName: jQuery( '#' + divname + ' [id=fullName]' ).val(),
			dobType: jQuery( '#' + divname + ' [id=dobType]' ).val(),
			gender: jQuery( '#' + divname + ' [id=gender]' ).val(),
			birthDate: jQuery( '#' + divname + ' [id=birthDate]' ).val(),        
			age: jQuery( '#' + divname + ' [id=age]' ).val()
		}, function( xmlObject, divname )
		{
			checkDuplicateCompleted( xmlObject, divname );
		});
}

function checkDuplicateCompleted( messageElement, divname )
{
	 checkedDuplicate = true;    
	 var type = jQuery(messageElement).find('message').attr('type');
	 var message = jQuery(messageElement).find('message').text();
	 
	 if( type == 'success')
	 {
	 	showSuccessMessage( i18n_no_duplicate_found );
	 }
	 if ( type == 'input' )
	 {
	     showWarningMessage(message);
	 }
	 else if( type == 'duplicate' )
	 {
		 showListASHADuplicate( messageElement, true );
	 }
}

/**
* Show list patient duplicate  by jQuery thickbox plugin
* @param rootElement : root element of the response xml
* @param validate  :  is TRUE if this method is called from validation method  
*/
function showListASHADuplicate( rootElement, validate )
{
	var message = jQuery(rootElement).find('message').text();
	var patients = jQuery(rootElement).find('patient');
	
	var sPatient = "";
	jQuery( patients ).each( function( i, patient )
       {
		sPatient += "<hr style='margin:5px 0px;'><table>";
		sPatient += "<tr><td class='bold'>" + i18n_asha_system_id + "</td><td>" + jQuery(patient).find('systemIdentifier').text() + "</td></tr>" ;
		sPatient += "<tr><td class='bold'>" + i18n_patient_full_name + "</td><td>" + jQuery(patient).find('fullName').text() + "</td></tr>" ;
		sPatient += "<tr><td class='bold'>" + i18n_patient_gender + "</td><td>" + jQuery(patient).find('gender').text() + "</td></tr>" ;
		sPatient += "<tr><td class='bold'>" + i18n_patient_date_of_birth + "</td><td>" + jQuery(patient).find('dateOfBirth').text() + "</td></tr>" ;
		sPatient += "<tr><td class='bold'>" + i18n_patient_age + "</td><td>" + jQuery(patient).find('age').text() + "</td></tr>" ;
		sPatient += "<tr><td class='bold'>" + i18n_patient_phone_number + "</td><td>" + jQuery(patient).find('phoneNumber').text() + "</td></tr>";
   	
		var identifiers = jQuery(patient).find('identifier');
       	if( identifiers.length > 0 )
       	{
       		sPatient += "<tr><td colspan='2' class='bold'>" + i18n_patient_identifiers + "</td></tr>";

       		jQuery( identifiers ).each( function( i, identifier )
				{
       			sPatient +="<tr class='identifierRow'>"
       				+"<td class='bold'>" + jQuery(identifier).find('name').text() + "</td>"
       				+"<td>" + jQuery(identifier).find('value').text() + "</td>	"	
       				+"</tr>";
       		});
       	}
			
       	var attributes = jQuery(patient).find('attribute');
       	if( attributes.length > 0 )
       	{
       		sPatient += "<tr><td colspan='2' class='bold'>" + i18n_patient_attributes + "</td></tr>";

       		jQuery( attributes ).each( function( i, attribute )
				{
       			sPatient +="<tr class='attributeRow'>"
       				+"<td class='bold'>" + jQuery(attribute).find('name').text() + "</td>"
       				+"<td>" + jQuery(attribute).find('value').text() + "</td>	"	
       				+"</tr>";
       		});
       	}
       	//sPatient += "<tr><td colspan='2'><input type='button' id='"+ jQuery(patient).find('id').first().text() + "' value='" + i18n_edit_this_patient + "' onclick='showUpdatePatientForm(this.id)'/></td></tr>";
       	sPatient += "</table>";
		});
		
		var result = i18n_duplicate_warning;
		
		/*
		if( !validate )
		{
			result += "<input type='button' value='" + i18n_create_new_patient + "' onClick='removeDisabledIdentifier( );addPatient();'/>";
			result += "<br><hr style='margin:5px 0px;'>";
		}
		*/
		
		result += "<br>" + sPatient;
		jQuery('#resultDuplicateASHASearchDiv' ).html( result );
		jQuery('#resultDuplicateASHASearchDiv' ).dialog({
			title: i18n_duplicated_asha_list,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 400
		});
}

//----------------------------------------------------------------
//Add New ASHA
//----------------------------------------------------------------

function showAddAHSAProfileForm()
{
	var orgUnitId = document.getElementById('selectedOrgunitID').value;
	
	hideById('ashaManagementDiv');
	hideById('searchDiv');
	hideById('advanced-search');
	hideById('listASHADiv');
	
	//hide for properties registration
	hideById('ASHAregistrationSuccessDiv');
	
	
	
	jQuery('#addUpdateASHAProfileDiv').load('showAddASHAProfileForm.action',{
		orgUnitId:orgUnitId
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
		});	
}

// Add Validation
function validateAddASHA()
{	
	//alert( "inside validation");
	
	$("#addUpdateASHAProfileDiv :input").attr("disabled", true);
	
	//alert( "inside validation");
	
	$.ajax({
		type: "POST",
		url: 'validateASHA.action',
		data: getParamsForDiv('addUpdateASHAProfileDiv'),
		success:addValidationCompleted
		
    });    
}

function addValidationCompleted( data )
{
    var type = jQuery(data).find('message').attr('type');
	var message = jQuery(data).find('message').text();
	
	//alert( "after  validation");
	
	//alert( type + " -- " + message );
	
	if ( type == 'success' )
	{
		addASHA();
		
		//document.forms['addASHAProfileForm'].submit();
		//$("#addASHAProfileForm").submit();
	}
	else
	{
		$("#addUpdateASHAProfileDiv :input").attr("disabled", true);
		if ( type == 'error' )
		{
			showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + message );
		}
		else if ( type == 'input' )
		{
			showWarningMessage( message );
		}
		else if( type == 'duplicate' )
		{
			showListASHADuplicate(data, false);
			//addASHA();
		}
			
		$("#addUpdateASHAProfileDiv :input").attr("disabled", false);
	}
}

/*
function addASHA()
{	
	
	$.ajax({
      type: "POST",
      url: 'addASHAProperties.action',
      data: getParamsForDiv('addUpdateASHAProfileDiv'),
      //success:registeredASHASuccessfully
      
      
      success: function() {
    	
    	setInnerHTML('addUpdateASHAProfileDiv', '');
    	showById('ASHAregistrationSuccessDiv');
		
      }
      
      
     });
	
    //return false;
}
*/

function registeredASHASuccessfully( data )
{
    var type = jQuery(data).find('message').attr('type');
	var message = jQuery(data).find('message').text();
	
		
	if ( type == 'success' )
	{
		setInnerHTML('addUpdateASHAProfileDiv', '');
		jQuery('#ASHAregistrationSuccessDiv' ).html( data );
	}
	
}



function addASHA()
{	
	//alert( "inside  add");
	$.ajax({
      type: "POST",
      url: 'addASHA.action',
      data: getParamsForDiv('addUpdateASHAProfileDiv'),
      success: function(json) {
		callAction( 'selectASHA' );
		//alert( "after  add");
      }
     });
	
    return false;
}



//Add/Update ASHA Profile Form
function showUpdateASHAProfileForm( ashaId )
{
	
	hideById('ashaManagementDiv');
	setInnerHTML('addUpdateASHAProfileDiv', '');
	
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listASHADiv');
	
	jQuery('#addUpdateASHAProfileDiv').load('showUpdateASHAProfileForm.action',
		{
			id:ashaId,
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
		});
}


//-----------------------------------------------------------------------------
//Update ASHA Profile
//-----------------------------------------------------------------------------

function validateUpdateASHAProfile()
{
	$("#addUpdateASHAProfileDiv :input").attr("disabled", true);
	$.ajax({
		type: "POST",
		url: 'validateASHA.action',
		data: getParamsForDiv('addUpdateASHAProfileDiv'),
		success:updateValidationCompleted
  });
}

function updateValidationCompleted( messageElement )
{
 var type = jQuery(messageElement).find('message').attr('type');
	var message = jQuery(messageElement).find('message').text();
 
 if ( type == 'success' )
 {
 	//removeDisabledIdentifier();
 	updateASHAProfile();
 }
	else
	{
		$("#addUpdateASHAProfileDiv :input").attr("disabled", true);
		if ( type == 'error' )
		{
			showErrorMessage( i18n_saving_patient_failed + ':' + '\n' + message );
		}
		else if ( type == 'input' )
		{
			showWarningMessage( message );
		}
		else if( type == 'duplicate' )
		{
			showListASHADuplicate(messageElement, true);
		}
		$("#addUpdateASHAProfileDiv :input").attr("disabled", false);
	}
}


function updateASHAProfile()
{
	$.ajax({
      type: "POST",
      url: 'updateASHAProfile.action',
      data: getParamsForDiv('addUpdateASHAProfileDiv'),
      success: function( json ) {
		callAction( 'selectASHA' );
      }
     });
}



// Add/Update ASHA Attributes

function showUpdateASHAAttributesTrainingForm( ashaId,attributeGroupName )
{
	
	hideById('ashaManagementDiv');
	setInnerHTML('addUpdateASHAProfileDiv', '');
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listASHADiv');
	
	jQuery('#addUpdateASHAProfileDiv').load('showUpdateASHAAttributesTrainingForm.action',
		{
			id:ashaId,
			attributeGroupName:attributeGroupName
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
		});	
	
}

function showUpdateASHAAttributesSkillsForm( ashaId,attributeGroupName )
{
	
	hideById('ashaManagementDiv');
	setInnerHTML('addUpdateASHAProfileDiv', '');
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listASHADiv');
	
	jQuery('#addUpdateASHAProfileDiv').load('showUpdateASHAAttributesSkillsForm.action',
		{
			id:ashaId,
			attributeGroupName:attributeGroupName
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
		});	
	
}


function showUpdateASHAAttributesIECForm( ashaId,attributeGroupName )
{
	
	hideById('ashaManagementDiv');
	setInnerHTML('addUpdateASHAProfileDiv', '');
	
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listASHADiv');
	
	jQuery('#addUpdateASHAProfileDiv').load('showUpdateASHAAttributesIECForm.action',
		{
			id:ashaId,
			attributeGroupName:attributeGroupName
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
		});	
	
}




function updateASHAAttributes()
{	
	//alert( "inside update save");
	$.ajax({
      type: "POST",
      url: 'updateASHAAttributes.action',
      data: getParamsForDiv('addUpdateASHAProfileDiv'),
      success: function( json ) {
		callAction( 'selectASHA' );
      }
     });
}


function removeASHA( patientId, fullName )
{
	removeItem( patientId, fullName, i18n_confirm_delete, 'removeASHA.action' );
	//callAction( 'selectASHA' );
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

// show details
function showASHADetails( patientId )
{
	$('#detailsInfo').load("getASHADetails.action", 
		{
			patientId:patientId
		}
		, function( ){
			
		}).dialog({
			title: i18n_asha_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 520
		});
}

function exportASHADetails( patientId, type )
{
	var url = "getASHADetails.action?patientId=" + patientId + "&type=" + type;
	window.location.href = url;
}



// ASHA Performance Details


function showUpdateASHAAmountDetailsForm( ashaId )
{
	
	hideById('ashaManagementDiv');
	setInnerHTML('addUpdateASHAProfileDiv', '');
	
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listASHADiv');
	
	jQuery('#addUpdateASHAProfileDiv').load('showUpdateASHAAmountDetailsForm.action',
		{
			id:ashaId,
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
		});	
	
}


function showAddASHABeneficiaryForm( ashaId )
{
	
	hideById('ashaManagementDiv');
	setInnerHTML('addUpdateASHAProfileDiv', '');
	
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listASHADiv');
	
	jQuery('#addUpdateASHAProfileDiv').load('showAddASHABeneficiaryForm.action',
		{
			id:ashaId,
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
			
			
			
		});	
	
}


function showUpdateASHAPaymentForm( ashaId )
{
	
	hideById('ashaManagementDiv');
	setInnerHTML('addUpdateASHAProfileDiv', '');
	
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listASHADiv');
	
	jQuery('#addUpdateASHAProfileDiv').load('showUpdateASHAPaymentDetailsForm.action',
		{
			id:ashaId,
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
		});	
	
}



// Training Form
function showUpdateASHATrainingForm( ashaId,attributeGroupName )
{
	
	hideById('ashaManagementDiv');
	setInnerHTML('addUpdateASHAProfileDiv', '');
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listASHADiv');
	
	jQuery('#addUpdateASHAProfileDiv').load('showASHATrainingForm.action',
		{
			id:ashaId,
			attributeGroupName:attributeGroupName
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
		});	
	
}



//Drug Kit Form
function showUpdateASHADrugKitIECForm( ashaId,attributeGroupName )
{
	
	hideById('ashaManagementDiv');
	setInnerHTML('addUpdateASHAProfileDiv', '');
	hideById('searchDiv');
	hideById('advanced-search');
	
	hideById('listASHADiv');
	
	jQuery('#addUpdateASHAProfileDiv').load('showASHADrugKitIECForm.action',
		{
			id:ashaId,
			attributeGroupName:attributeGroupName
		}, 
		function()
		{
			showById('addUpdateASHAProfileDiv');
		});	
	
}



