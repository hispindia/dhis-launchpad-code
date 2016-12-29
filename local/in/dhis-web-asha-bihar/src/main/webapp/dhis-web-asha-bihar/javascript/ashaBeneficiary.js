
window.onload=function()
{
	jQuery('#updateASHABeneficiaryFormDiv').dialog({autoOpen: false});
}


function showUpdateASHABeneficiaryForm( beneficiaryId , periodId, ashaId ,selectedPeriodId )
{
	
	jQuery('#updateASHABeneficiaryFormDiv').dialog('close');
	
	jQuery('#updateASHABeneficiaryFormDiv').dialog('destroy').remove();
	jQuery('<div id="updateASHABeneficiaryFormDiv">' ).load( 'showUpdateASHABeneficiaryForm.action?beneficiaryId='+ beneficiaryId + "&periodId=" + periodId + "&ashaId=" + ashaId + "&selectedPeriodId=" + selectedPeriodId ).dialog({
		title: "Update ASHA Beneficiary Form",
		maximize: true,
		closable: true,
		modal:true,
		overlay:{background:'#000000', opacity:0.1},
		width: 650,
		height: 500
	});
	
}


function closeUpdateBeneficiaryWindow()
{
	jQuery('#updateASHABeneficiaryFormDiv').dialog('destroy').remove();
}


function removeASHABeneficiary( beneficiaryId ,beneficiaryName, periodId, ashaId ,selectedPeriodId )
{
	removeItem( beneficiaryId, beneficiaryName, i18n_confirm_delete, 'removeASHABeneficiary.action' );
	
	loadASHABeneficiaryListAfterDelete( ashaId, selectedPeriodId );
	
}


function loadASHABeneficiaryListAfterDelete( ashaId,selectedPeriodId )
{
	
	$( '#ASHABeneficiaryListDiv' ).html('');
	
	if ( selectedPeriodId == "-1" )
	{
		$( '#ASHABeneficiaryListDiv' ).html('');
		return false;
	}
	
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