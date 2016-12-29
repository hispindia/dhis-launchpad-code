//Drug Kit Received
function validationDrugKitReceived()
{
	var attrDrugKitReceivedObject = document.getElementById('attr75');
	var arrrDrugKitReceivedValue = attrDrugKitReceivedObject.options[ attrDrugKitReceivedObject.selectedIndex ].value;
	
	if( arrrDrugKitReceivedValue == "true" || arrrDrugKitReceivedValue == "Yes" )
	{
		document.getElementById( "attr188" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr188" ).value = "";
	    document.getElementById( "attr188" ).disabled = true;
	}  
	
}

//Drug Kit Replenished
function validationDrugKitReplenished()
{
	var attrDrugKitReplenishedObject = document.getElementById('attr77');
	var arrrDrugKitReplenishedValue = attrDrugKitReplenishedObject.options[ attrDrugKitReplenishedObject.selectedIndex ].value;
	
	if( arrrDrugKitReplenishedValue == "true" || arrrDrugKitReplenishedValue == "Yes" )
	{
		document.getElementById( "attr178" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr178" ).value = "";
	    document.getElementById( "attr178" ).disabled = true;
	}  
	
}

//Saree Received
function validationSareeReceived()
{
	var attrSareeReceivedObject = document.getElementById('attr204');
	var arrrSareeReceivedValue = attrSareeReceivedObject.options[ attrSareeReceivedObject.selectedIndex ].value;
	
	if( arrrSareeReceivedValue == "true" || arrrSareeReceivedValue == "Yes" )
	{
		document.getElementById( "attr205" ).disabled = false;
		document.getElementById( "attr206" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr205" ).value = "";
	    document.getElementById( "attr205" ).disabled = true;
	    
	    document.getElementById( "attr206" ).value = "";
	    document.getElementById( "attr206" ).disabled = true;
	}  
	
}

// validationCommunicationKitReceived
function validationCommunicationKitReceived()
{
	var attrDrugCommunictionObject = document.getElementById('attr218');
	var attrDrugKitCommunictionValue = attrDrugCommunictionObject.options[ attrDrugCommunictionObject.selectedIndex ].value;
	
	if( attrDrugKitCommunictionValue == "true" || attrDrugKitCommunictionValue == "Yes" )
	{
		document.getElementById( "attr219" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr219" ).value = "";
	    document.getElementById( "attr219" ).disabled = true;
	}  
	
}


//validationNeoKitReceived
function validationNeoKitReceived()
{
	var attrDrugNeoKitObject = document.getElementById('attr220');
	var attrNeoKitCommunictionValue = attrDrugNeoKitObject.options[ attrDrugNeoKitObject.selectedIndex ].value;
	
	if( attrNeoKitCommunictionValue == "true" || attrNeoKitCommunictionValue == "Yes" )
	{
		document.getElementById( "attr221" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr221" ).value = "";
	    document.getElementById( "attr221" ).disabled = true;
	}  
	
}

