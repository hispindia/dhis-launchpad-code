

function formValidations()
{
    //alert(" Inside validation");
    
    var startOrgUnitList = document.getElementById("selectedOrgUnitId");
    var startOrgUnitIndex = startOrgUnitList.selectedIndex;
    
    
    //alert( startOrgUnitList.options[startOrgUnitIndex].value );
    if( startOrgUnitList.options[startOrgUnitIndex].value == null || startOrgUnitList.options[startOrgUnitIndex].value == "-1" ) 
    { 
        showWarningMessage( "Please Select District" );
        //alert( "Please Select District" );
        //alert("Please Select Period"); 
        return false;
    }
    
    return true;
}


function transferPatientData()
{
	//alert( "inside transfer ");
	
	if( formValidations() )
	{        
		//alert( "inside transfer ");
		
		$( '#transferPatientDataValueDiv' ).html( ' ' );
		
		var selectedOrgUnitId = $( '#selectedOrgUnitId' ).val();
		
		jQuery('#loaderDiv').show();
		document.getElementById( "transfer" ).disabled = true;
		
		jQuery('#transferPatientDataValueDiv').load('transferPatientDataValue.action',
			{
				selectedOrgUnitId:selectedOrgUnitId
				
			}, function()
			{
				showById('transferPatientDataValueDiv');
				document.getElementById( "transfer" ).disabled = false;
				jQuery('#loaderDiv').hide();
			});
	}
}	
