

//-----------------------------------------------------------------------------
//Remove Report
//-----------------------------------------------------------------------------

function removeNameBasedReport( reportId, reportName )
{
	removeItem( reportId, reportName, i18n_confirm_to_delete_report, 'removeNameBasedReport.action' );
}


/*
jQuery( document ).ready( function()
{
	validation( 'addReportForm', function( form ){ 
		form.submit();
	}, function(){
		$("#selectedPersonProperties").find("option").attr("selected", "selected");
		$("#selectedPatientAttributes").find("option").attr("selected", "selected");
		
	});
	
		
});
*/

$(function(){
	jQuery( '#reportNameAndSortOrderDesignDiv' ).dialog({
		title: 'Add Report Name',
		maximize: true,
		closable: true,
		modal:true,
		autoOpen: false,
		overlay:{background:'#000000', opacity:0.1},
		width: 400,
		height: 400
	});	
});


$(function(){
	jQuery( '#reportPreviewDiv' ).dialog({
		title: 'Report Preview Parameters',
		maximize: true,
		closable: true,
		modal:true,
		autoOpen: false,
		overlay:{background:'#000000', opacity:0.1},
		width: 600,
		height: 600
	});	
});

$(function(){
	jQuery( '#reportPreviewResultDiv' ).dialog({
		title: 'Report Preview Result',
		maximize: true,
		closable: true,
		modal:true,
		autoOpen: false,
		overlay:{background:'#000000', opacity:0.1},
		width: 1000,
		height: 800
	});	
});

$(function(){
	jQuery( '#savedReportParamDiv' ).dialog({
		title: 'Select Parameter',
		maximize: true,
		closable: true,
		modal:true,
		autoOpen: false,
		overlay:{background:'#000000', opacity:0.1},
		width: 400,
		height: 400
	});	
});


function getParams()
{
	var programIds = "";
	var params = "";
	
	$("#selectedPersonProperties").find("option").attr("selected", "selected");
	$("#selectedPatientAttributes").find("option").attr("selected", "selected");
	
	$("#displaysSlectedPersonPropertiesAndAttributes").find("option").attr("selected", "selected");
	
	$("#displaysSlectedPersonPropertiesAndAttributesForPreview").find("option").attr("selected", "selected");
	
	
	
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
							//p += dateOperator + "_" + "'" +  item.value.toLowerCase() + "'";
							p += dateOperator + "'" +  item.value.toLowerCase() + "'";
							
						}
						else{
							p += htmlEncode( item.value.toLowerCase().replace(/^\s*/, "").replace(/\s*$/, "") );
						}
					}
					else {
						p = "";
					}
				}
			}
		});
		
		params += p;
	});
	
	
	params += "&" + getParamsStringBySelected( 'selectedPersonProperties', 'selectedPersonProperties' );
	params += "&" + getParamsStringBySelected( 'selectedPatientAttributes', 'selectedPatientAttributes' );
	params += "&" + getParamsStringBySelected( 'displaysSlectedPersonPropertiesAndAttributes', 'displaysSlectedPersonPropertiesAndAttributes' );
	params += "&" + getParamsStringBySelected( 'displaysSlectedPersonPropertiesAndAttributesForPreview', 'displaysSlectedPersonPropertiesAndAttributesForPreview' );
	
	var reportAvailabeForAll = byId('reportAvailabeForAll').checked;
	params += '&reportAvailabeForAll=' + reportAvailabeForAll;
	
	var reportName = getFieldValue('reportName');
	
	params += '&reportName=' + reportName;
	
	var startDate = getFieldValue('startDate');
	var endDate = getFieldValue('endDate');
	
	params += '&startDate=' + startDate;
	
	params += '&endDate=' + endDate;
	
	//endDate
	/*
	params += '&selectedPersonProperties=' + selectedPersonProperties;
	params += '&selectedPatientAttributes=' + !selectedPatientAttributes;
	*/
	
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


window.onload=function(){
	jQuery('#orgUnitPeriodtDiv').dialog({autoOpen: false});
	jQuery('#reportNameAndSortOrderDesignDiv').dialog({autoOpen: false});
	jQuery('#reportPreviewDiv').dialog({autoOpen: false});
	jQuery('#reportPreviewResultDiv').dialog({autoOpen: false});
	
}


function moveElements()
{
	var displayListId = document.getElementById( 'displaysSlectedPersonPropertiesAttributes' );
	
	var displayLength = displayListId.options.length;
	
	var selListId = document.getElementById( 'selectedPatientAttributes' );
    var selLength = selListId.options.length;
    
    for( var i =0 ; i<selLength; i++ )
    {
    	var id = selListId.options[i].value;
    	var text = selListId.options[i].text;
    	var flag = 1;
    	for( var j =0 ; j<displayLength; j++ )
    	{
    		if( id == displayListId.options[j].value )
    		{
    			flag =2;
    			//alert( id + " == " +  displayListId.options[j].value );
    			//return;
    		}
    	}
    	if ( flag == 1 )
    	{
    		$("#displaysSlectedPersonPropertiesAttributes").append("<option value='"+ id +"' title='" + text + "'>" + text + "</option>");
    	}
    	
    }
}


function removeElements()
{
	var displayListId = document.getElementById( 'displaysSlectedPersonPropertiesAttributes' );
	
	var displayLength = displayListId.options.length;
	
	var selListId = document.getElementById( 'selectedPatientAttributes' );
    var selLength = selListId.options.length;
    
    
    var i = 0;
    for ( i = selLength - 1; i >= 0; i--)
    {
    	if ( selListId.options[i].selected )
    	{
    		var j = 0;
    		for ( j = displayLength - 1; j >= 0; j-- )
    		{
    			if( selListId.options[i].value == displayListId.options[j].value )
    			{
    				displayListId.options[j] = null;
    			}
    		}
    	}
    }
    
}






// for Filter

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
	

	if ( attributeId=='fixedAttr_gender' )
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
	dateField += '<input type="text" id="searchText_' + container + '" name="searchText" style="width:160px;" >';
	return dateField;
}



function selectOrgUnitAndPeriod()
{
	jQuery('#orgUnitPeriodtDiv').dialog('destroy').remove();
	
	hideById('reportNameAndSortOrderDesignDiv');
	
	jQuery('<div id="orgUnitPeriodtDiv">' ).load('showOrgUnitAndPeriodSelectionForm.action').dialog({
		title: '',
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
	
	/*
	jQuery('<div id="orgUnitPeriodtDiv">' ).dialog({
		title: '',
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	*/
	
}



function closeDialog()
{
	jQuery('#orgUnitPeriodtDiv').dialog('destroy').remove();
}

function nameAndSortOrderForCustomReport()
{
	//jQuery('#reportNameAndSortOrderDesignDiv').dialog('destroy').remove();
	
	//alert( "show");
	
	var displayFinalList = document.getElementById( 'displaysSlectedPersonPropertiesAndAttributes' );
	
	clearList( displayFinalList );
	
	var selPPListId = document.getElementById( 'selectedPersonProperties' );
    var selPPLength = selPPListId.options.length;
    
    for( var i =0 ; i<selPPLength; i++ )
    {
    	var id = selPPListId.options[i].value;
    	var text = selPPListId.options[i].text;
    	
    	$("#displaysSlectedPersonPropertiesAndAttributes").append("<option value='"+ id +"' title='" + text + "'>" + text + "</option>");
    	
    }	
	
	var selPAListId = document.getElementById( 'selectedPatientAttributes' );
    var selPALength = selPAListId.options.length;
    
    for( var i =0 ; i<selPALength; i++ )
    {
    	var id = selPAListId.options[i].value;
    	var text = selPAListId.options[i].text;
    	
    	$("#displaysSlectedPersonPropertiesAndAttributes").append("<option value='"+ id +"' title='" + text + "'>" + text + "</option>");
    	
    }
    
    //jQuery("#reportName").addClass('required',true);
    
    //jQuery("#displaysSlectedPersonPropertiesAndAttributes").addClass('required',true);
    
    //alert( displaysSlectedPersonPropertiesAndAttributes );
    
	//showById('reportNameAndSortOrderDesignDiv');
	
	jQuery( '#reportNameAndSortOrderDesignDiv' ).dialog('open');
	
}

function validateAddCustomReport()
{
	var flag = true;


	var selPPAndPAList = document.getElementById( 'displaysSlectedPersonPropertiesAndAttributes' );
    var selPPAndPALength = selPPAndPAList.options.length;
	
	if( getFieldValue('reportName')=='' )
	{
		showWarningMessage( "Enter Report Name" );
		flag = false;
	} 
	else if( selPPAndPAList.options.length <= 0 )
	{
		showWarningMessage( "Select Patient Properties or Patient Attributes " );
		flag = false;
		
	}
	
	if(flag)
	{
		saveCustomReport();
	}
}


function saveCustomReport()
{
	$.ajax({
		url: 'saveReportForm.action',
		type:"POST",
		data: getParams(),
		success: function( html ){
		window.location.href='customReportManagement.action';
			}
		});
}

function closeDialogReportName()
{
	jQuery( '#reportNameAndSortOrderDesignDiv' ).dialog('close');
}


function openReportPreviewWindow()
{
	var displayPreviewList = document.getElementById( 'displaysSlectedPersonPropertiesAndAttributesForPreview' );
	
	clearList( displayPreviewList );
	
	var selPPListId = document.getElementById( 'selectedPersonProperties' );
    var selPPLength = selPPListId.options.length;
    
    for( var i =0 ; i<selPPLength; i++ )
    {
    	var id = selPPListId.options[i].value;
    	var text = selPPListId.options[i].text;
    	
    	$("#displaysSlectedPersonPropertiesAndAttributesForPreview").append("<option value='"+ id +"' title='" + text + "'>" + text + "</option>");
    	
    }	
	
	var selPAListId = document.getElementById( 'selectedPatientAttributes' );
    var selPALength = selPAListId.options.length;
    
    for( var i =0 ; i<selPALength; i++ )
    {
    	var id = selPAListId.options[i].value;
    	var text = selPAListId.options[i].text;
    	
    	$("#displaysSlectedPersonPropertiesAndAttributesForPreview").append("<option value='"+ id +"' title='" + text + "'>" + text + "</option>");
    	
    }
    
	jQuery( '#reportPreviewDiv' ).dialog('open');
	
}


function validateReportPreview()
{
	var flag = true;


	var selPreviewPPAndPAList = document.getElementById( 'displaysSlectedPersonPropertiesAndAttributesForPreview' );
    var selPreviewPPAndPALength = selPreviewPPAndPAList.options.length;
	
    if( selPreviewPPAndPAList.options.length <= 0 )
	{
		showWarningMessage( "Select Patient Properties or Patient Attributes " );
		flag = false;
		
	}
    
    else if( getFieldValue('startDate')=='' )
	{
		showWarningMessage( "Select Start date" );
		flag = false;
	} 
	
	else if( getFieldValue('endDate')=='' )
	{
		showWarningMessage( "Select End date" );
		flag = false;
	} 
	
	if(flag)
	{
		previewCustomReport();
	}
}


function previewCustomReport()
{
	/*	
	$.ajax({
		url: 'previewReportResult.action',
		type:"POST",
		data: getParams(),
		success: function( html ){
			//window.location.href='customReportManagement.action';
			jQuery('#reportPreviewDiv').dialog('close');
			jQuery( '#reportPreviewResultDiv' ).dialog('open');
			}
		});
	*/
	
	jQuery('#reportPreviewDiv').dialog('close');
	document.getElementById('overlay').style.visibility = 'visible';
	
	var params = "";
	
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
							//p += dateOperator + "_" + "'" +  item.value.toLowerCase() + "'";
							p += dateOperator + "'" +  item.value.toLowerCase() + "'";
							
						}
						else{
							p += htmlEncode( item.value.toLowerCase().replace(/^\s*/, "").replace(/\s*$/, "") );
						}
					}
					else {
						p = "";
					}
				}
			}
		});
		
		params += p;
	
	});
	
	$("#displaysSlectedPersonPropertiesAndAttributesForPreview").find("option").attr("selected", "selected");
	
	var url = "previewReportResult.action?" + getParamsStringBySelected( 'displaysSlectedPersonPropertiesAndAttributesForPreview', 'displaysSlectedPersonPropertiesAndAttributesForPreview' ) + params; 
											
	//alert( getParams() );
	
	jQuery( '#previewDataContentDiv' ).load( url,
			{
				startDate : getFieldValue( 'startDate' ),
				endDate : getFieldValue( 'endDate' ),
				orgUnitGroupId : getFieldValue( 'orgUnitGroupId' )
			}
			, function( ){
				$('#reportPreviewResultDiv').dialog('open');
				document.getElementById('overlay').style.visibility = 'hidden';
			}); 
}


function closeReportPreviewDialog()
{
	jQuery( '#reportPreviewDiv' ).dialog('close');
}


function selectSavedReportParam( reportId, reportName )
{
	savedReportId = reportId;
	//alert( savedReportId );
	jQuery( '#savedReportParamDiv' ).dialog('open');
}

function savedReportParamDialog()
{
	jQuery( '#savedReportParamDiv' ).dialog('close');
}


function validateGenerateSavedReport()
{
	var flag = true;

	if( getFieldValue('startDate')=='' )
	{
		showWarningMessage( "Select Start date" );
		flag = false;
	} 
	
	else if( getFieldValue('endDate')=='' )
	{
		showWarningMessage( "Select End date" );
		flag = false;
	} 
	
	if(flag)
	{
		generateReport();
	}
}

function generateReport()
{
	//alert( savedReportId );
	jQuery( '#savedReportParamDiv' ).dialog('close');
	//document.getElementById('overlay').style.visibility = 'visible';
	
	var url = "generateNameBasedReport.action?reportId=" + savedReportId + "&orgUnitGroupId=" + getFieldValue('orgUnitGroupId') + "&startDate=" + getFieldValue('startDate') + "&endDate=" + getFieldValue('endDate');
	window.location.href = url;
	
	//document.getElementById('overlay').style.visibility = 'hidden';
	/*
	$.ajax({
		url: 'saveReportForm.action',
		type:"POST",
		data: getParams(),
		success: function( html ){
		window.location.href='generateNameBasedReport.action?reportId=$report.id';
			}
		});
	*/
}
