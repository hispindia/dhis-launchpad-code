function organisationUnitSelected( orgUnits ){
	window.location = "getImportingParams.action";
}
selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// IMPORT DATA FROM EXCEL FILE INTO DATABASE
// -----------------------------------------------------------------------------

function importData()
{
	if ( importItemIds && importItemIds.length > 0 )
	{
		lockScreen();
		var params = 'importData.action?';
		
		for ( var i = 0 ; i < importItemIds.length ; i ++ )
		{
			params += 'importItemIds=' + importItemIds[i];
			params += (i < importItemIds.length-1) ? "&" : "";
		}
		
		jQuery.postJSON( params,
		{
			importReportId: byId('importReportId').value,
			periodId: byId('period').value
		}, function( json ) {
			unLockScreen();
			showSuccessMessage( json.message );
		});
	}
	else showWarningMessage( i18n_choose_import_item );
}

// -----------------------------------------------------------------------------
// PREVIEW DATA FLOW
// @param isImport This is a global variable which declared in preview.js
// -----------------------------------------------------------------------------

function getPreviewImportData(){	
	
	lockScreen();
	
	isImport = true;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( previewExportReportReceived );
	request.send( "previewDataFlow.action?importReportId=" + byId("importReportId").value );
	
}

isToggled = true;

function selectAllData( _this )
{
	if ( isToggled )
	{
		jQuery( _this ).val( i18n_unselect_all );
		
		for ( var i = 0 ; i < importlist.length ; i ++ )
		{
			importlist[i].className = 'ui-widget-content ui-selected';
			
			idTemp = jQuery(importlist[i]).attr( 'id' ) + "_" + jQuery(importlist[i]).html();
			
			if ( jQuery.inArray(idTemp, importItemIds) != -1 )
			{
				importItemIds = jQuery.grep( importItemIds, function(value) {
					return value != idTemp
				});
			}
			else importItemIds.push( idTemp );
		}
	}
	else
	{
		jQuery( _this ).val( i18n_select_all );
		
		for ( var i = 0 ; i < importlist.length ; i ++ )
		{
			importlist[i].className = 'ui-widget-content ui-unselected';
		}
		
		importItemIds.length = 0;
	}
	
	isToggled = !isToggled;	
}

// --------------------------------------------------------------------
// PERIOD TYPE
// --------------------------------------------------------------------

function getPeriodsByImportReport( importReportId ) {
	
	var url = 'getPeriodsByImportReport.action';
	
	jQuery.postJSON( url, {'importReportId':importReportId}, responseListPeriodReceived );
}

function validateUploadExcelImportByJSON(){

	jQuery( "#upload" ).upload( 'validateUploadExcelImport.action',
		{ 'draft': true },
		function ( data )
		{
			if ( data.response == 'error' )
			{              
				setMessage( data.message );
			}
			else
			{
				uploadExcelImport();
			}
		}, 'json'
	);
}

function validateUploadExcelImportByXML(){

	jQuery( "#upload" ).upload( 'validateUploadExcelImport.action',
		{ 'draft': true },
		function ( data )
		{
			data = data.getElementsByTagName('message')[0];
			var type = data.getAttribute("type");
			
			if ( type == 'error' )
			{              
				setMessage( data.firstChild.nodeValue );
			}
			else
			{
				uploadExcelImport();
			}
		}, 'xml'
	);
}
	
function uploadExcelImport(){
	
	jQuery( "#upload" ).upload( 'uploadExcelImport.action',
		{ 'draft': true },
		function( data, e ) {
			try {
				window.location.reload();
			}
			catch(e) {
				alert(e);
			}
		}
	);
}