
// -----------------------------------------------------------------------------
// List && Search Submitted OVC
// -----------------------------------------------------------------------------

function listAllSubmittedOVC()
{
	hideById('listOVCSubmittedDiv');
	hideById('advanced-search');
	//hideById('updateOVCRegistrationFormDiv');
	
	jQuery('#loaderDiv').show();
	contentDiv = 'listOVCSubmittedDiv';
	
	jQuery('#listOVCSubmittedDiv').load('getSubmittedOVCList.action',{
		listAll:true
	},
	function(){
		
		showById('listOVCSubmittedDiv');
		jQuery('#loaderDiv').hide();
	});

	hideLoader();
}

// -----------------------------------------------------------------------------
// Advanced search
// -----------------------------------------------------------------------------

function addAttributeOption()
{
	jQuery('#advancedSearchTB [name=clearSearchBtn]').attr('disabled', false);
	var rowId = 'advSearchBox' + jQuery('#advancedSearchTB select[name=searchObjectId]').length + 1;
	var contend  = '<td>' + getInnerHTML('searchingAttributeIdTD') + '</td>';
		contend += '<td>' + searchTextBox ;
		contend += '&nbsp;<input type="button" name="clearSearchBtn" class="normal-button" value="' + i18n_clear + '" onclick="removeAttributeOption(' + "'" + rowId + "'" + ');"></td>';
		contend = '<tr id="' + rowId + '">' + contend + '</tr>';

	jQuery('#advancedSearchTB').append( contend );
	var rowspan = eval( jQuery('[name=addAndSearchBtn]').attr('rowspan') );
	jQuery('[name=addAndSearchBtn]').attr('rowspan', rowspan + 1);
}	

function removeAttributeOption( rowId )
{
	jQuery( '#' + rowId ).remove();
	if( jQuery( '#advancedSearchTB tr' ).length == 2 ){
		jQuery('#advancedSearchTB [name=clearSearchBtn]').attr('disabled', true);
	}
	var rowspan = eval( jQuery('[name=addAndSearchBtn]').attr('rowspan') );
	jQuery('[name=addAndSearchBtn]').attr('rowspan', rowspan - 1);
}	



//------------------------------------------------------------------------------
//Search patients by selected attribute
//------------------------------------------------------------------------------

function searchObjectOnChange( this_ )
{	
	var container = jQuery(this_).parent().parent().attr('id');
	var attributeId = jQuery('#' + container + ' [id=searchObjectId]').val(); 
	var element = jQuery('#' + container + ' [name=searchText]');
	var valueType = jQuery('#' + container+ ' [id=searchObjectId] option:selected').attr('valueType');
	
	jQuery('#searchText_' + container).removeAttr('readonly', false);
	jQuery('#dateOperator_' + container).remove();
	jQuery('#searchText_' + container).val("");
	if( attributeId == 'fixedAttr_birthDate' || valueType=='date')
	{
		element.replaceWith( getDateField( container ) );
		datePickerValid( 'searchText_' + container );
		return;
	}
	
	$( '#searchText_' + container ).datepicker("destroy");
	$('#' + container + ' [id=dateOperator]').replaceWith("");
	
	if( attributeId == 'prg' )
	{
		element.replaceWith( programComboBox );
	}
	else if ( attributeId=='fixedAttr_gender' )
	{
		element.replaceWith( getGenderSelector() );
	}
	else if ( attributeId=='fixedAttr_age' )
	{
		element.replaceWith( getAgeTextBox() );
	}
	else if ( valueType=='bool' )
	{
		element.replaceWith( getTrueFalseBox() );
	}
	else
	{
		element.replaceWith( searchTextBox );
	}
}

function getTrueFalseBox()
{
	var trueFalseBox  = '<select id="searchText" name="searchText">';
	trueFalseBox += '<option value="true">' + i18n_yes + '</option>';
	trueFalseBox += '<option value="false">' + i18n_no + '</option>';
	trueFalseBox += '</select>';
	return trueFalseBox;
}
	
function getGenderSelector()
{
	var genderSelector = '<select id="searchText" name="searchText" style="width:200px;">';
		genderSelector += '<option value="M">' + i18n_male + '</option>';
		genderSelector += '<option value="F">' + i18n_female + '</option>';
		//genderSelector += '<option value="T">' + i18n_transgender + '</option>';
		genderSelector += '</select>';
	return genderSelector;
}

function getAgeTextBox( container )
{
	var ageField = '<select id="dateOperator" name="dateOperator" style="width:40px"><option value=">"> > </option><option value=">="> >= </option><option value="="> = </option><option value="<"> < </option><option value="<="> <= </option></select>';
	ageField += '<input type="text" id="searchText_' + container + '" name="searchText" style="width:160px;">';
	return ageField;
}

function getDateField( container )
{
	var dateField = '<select id="dateOperator_' + container + '" name="dateOperator" style="width:40px"><option value=">"> > </option><option value=">="> >= </option><option value="="> = </option><option value="<"> < </option><option value="<="> <= </option></select>';
	dateField += '<input type="text" id="searchText_' + container + '" name="searchText" style="width:160px;" onkeyup="searchOVCOnKeyUp( event );">';
	return dateField;
}


function validateAdvancedSearch()
{
	hideById( 'listOVCSubmittedDiv' );
	var flag = true;
	var dateOperator = '';
	
	if( getFieldValue('startDueDate')=='' && getFieldValue('endDueDate')=='' ){
		if (getFieldValue('searchByProgramStage') == "false" 
			|| ( getFieldValue('searchByProgramStage') == "true"  
				&& jQuery( '#advancedSearchTB tr' ).length > 1) ){
			jQuery("#searchDiv :input").each( function( i, item )
			{
				var elementName = $(this).attr('name');
				if( elementName=='searchText' && jQuery( item ).val() == '')
				{
					showWarningMessage( i18n_specify_search_criteria );
					flag = false;
				}
			});
		}
	}
	
	if(flag){
		contentDiv = 'listOVCSubmittedDiv';
		jQuery( "#loaderDiv" ).show();
		advancedSearch( getSearchParams() );
	}
}

function getSearchParams()
{
	var params = "";
	var programIds = "";
	var programStageId = jQuery('#programStageAddPatient').val();
	if( getFieldValue('searchByProgramStage') == "true" ){
		var statusEvent = jQuery('#programStageAddPatientTR [id=statusEvent]').val();
		var startDueDate = getFieldValue('startDueDate');
		var orgunitid = getFieldValue('orgunitId');
		if( byId('searchInAllFacility').checked ){
			orgunitid = 0;
		}
		var endDueDate = getFieldValue('endDueDate');
		params = '&searchTexts=stat_' + getFieldValue('programIdAddPatient') 
			   + '_' + startDueDate + '_' + endDueDate
			   + "_" + orgunitid
			   + '_false_' + statusEvent;
	}
	
	var flag = false;
	jQuery( '#advancedSearchTB tr' ).each( function( i, row ){
		var dateOperator = "";
		var p = "";
		jQuery( this ).find(':input').each( function( idx, item ){
			if(item.type!="button"){
				if( idx == 0){
					p = "&searchTexts=" + item.value;
					if(item.value=='prg'){
						programIds += '&programIds=';
						flag = true;
					}
				}
				else if( item.name == 'dateOperator'){
					dateOperator = item.value;
				}
				else if( item.name == 'searchText'){
					if( item.value!='')
					{
						p += "_";
						if ( dateOperator.length >0 ) {
							p += dateOperator + "'" +  item.value.toLowerCase() + "'";
						}
						else{
							p += htmlEncode( item.value.toLowerCase().replace(/^\s*/, "").replace(/\s*$/, "") );
						}
						
						if( flag ){
							programIds += item.value;
							flag = false;
						}
					}
					else {
						p = "";
					}
				}
			}
		});
		
		var searchInAllFacility = byId('searchInAllFacility').checked;
		if( getFieldValue('searchByProgramStage') == "false" && !searchInAllFacility ){
			p += "_" + getFieldValue('orgunitId');
		}
		params += p;
	});
		
	params += '&listAll=false';
	if( getFieldValue('searchByProgramStage') == "false"){
		var searchInAllFacility = byId('searchInAllFacility').checked;
		params += '&searchBySelectedOrgunit=' + !searchInAllFacility;
	}
	else
	{
		params += '&searchBySelectedOrgunit=false';
	}
	params += programIds;
	
	return params;
}


function advancedSearch( params )
{
	$.ajax({
		url: 'getSubmittedOVCList.action',
		type:"POST",
		data: params,
		success: function( html ){
				
				setInnerHTML( 'listOVCSubmittedDiv', html );
				showById('listOVCSubmittedDiv');
				
				jQuery( "#loaderDiv" ).hide();
			}
		});
}




//-----------------------------------------------------------------------------
//Search Patient
//-----------------------------------------------------------------------------

function searchOVCOnKeyUp( event )
{
	var key = getKeyCode( event );
	
	if ( key==13 )// Enter
	{
		validateAdvancedSearch();
	}
}

function getKeyCode(e)
{
	 if (window.event)
		return window.event.keyCode;
	 return (e)? e.which : null;
}

function loadSubmittedOVCList()
{
	hideById('updateOVCRegistrationFormDiv');

	showById('searchDiv');
	
	listAllSubmittedOVC();
}

function loadSubmittedOVCList1()
{
	hideById('registrationSuccessDiv');
	
	showById('searchDiv');
	
	listAllSubmittedOVC();
}
