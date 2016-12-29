
function getOUDeatilsForDEAnalysis( orgUnitIds )
{
	jQuery.postJSON("getOrgUnitName.action",{
  	  id : orgUnitIds[0]
   }, function( json ){

	    var orgUnitId = json.organisationUnit.id;
	    var orgUnitName = json.organisationUnit.name;
	    setFieldValue( "ouNameTB",json.organisationUnit.name );
   });
}

//filter available data elements list
function filterAvailableDataElements()
{
	var filter = document.getElementById( 'availableDataElementsFilter' ).value;
    var list = document.getElementById( 'availableDataElements' );
    
    list.options.length = 0;
    
    var selDeListId = document.getElementById( 'selectedDataElements' );
    var selDeLength = selDeListId.options.length;
    
    for ( var id in availableDataElements )
    {
        var value = availableDataElements[id];
        
        var flag = 1;
        for( var i =0 ; i<selDeLength; i++ )
        {
        	if( id == selDeListId.options[i].value )
        		{
        		flag =2;
        		//alert("aaaa");
        		break;
        		}
        }
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 && ( flag == 1 ) )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function generateAnalysisResult()
{
	formValidationsDataElementAnalysis();
}
//DataElement Analysis Form Validations
function formValidationsDataElementAnalysis()
{
	var selOuId = document.dataElementAnalysisForm.ouIDTB.value;
	var selDEListSize  = document.dataElementAnalysisForm.selectedDataElements.options.length;//alert(selDEListSize);
	
	var orgUnitListCB = document.getElementById("orgUnitListCB");
	var selectedDataElements = document.getElementById("selectedDataElements");
	
	var orgUnitGroupCB = document.getElementById("orgUnitGroupList");
	
	var selOUGroupSetListLength = document.dataElementAnalysisForm.orgUnitGroupSetListCB.options.length;
	
	var startDateValue = document.dataElementAnalysisForm.startDate.value;
    var endDateValue = document.dataElementAnalysisForm.endDate.value;
	
    var k = 0;
    
    if( selOuId == null || selOuId == "") 
    {
    	alert("Please Select OrganisationUnit");
    	return false;
    }
    else if( selDEListSize <= 0 ) 
	{
    	alert( "Please Select DataElement(s)" );
    	return false;
	}
    else if( selOUGroupSetListLength <= 0 ) 
	{
        alert( "Please Select OrganisationUnit Set" );
        return false;
	}
    else if( startDateValue == null || startDateValue== "" ) 
    { 
	   alert("Please Select Start Date"); 
	   return false; 
    }
    else if( endDateValue == null || endDateValue=="" ) 
    { 
	   alert("Please Select End Date"); 
	   return false; 
    }    
    
    return true;
  	//generateDataElementAnalysisResult();
} 

function generateDataElementAnalysisResult()
{

	var url = "generateDataElementAnalysisResult.action?" + getParamString( 'selectedDataElements', 'selectedDataElements' ); 
	
	jQuery( "#contentDiv" ).load( url,
	{
		orgUnitGroupSet : getFieldValue( 'orgUnitGroupSetListCB' ),
		startDate : getFieldValue( 'startDate' ),
		endDate : getFieldValue( 'endDate' ),
		ouIDTB : getFieldValue( 'ouIDTB' ),
	} ).dialog( {
		title: 'Data Element Wise Analysis',
		maximize: true, 
		closable: true,
		modal:true,
		overlay:{ background:'#000000', opacity:0.1 },
		width: 1000,
		height: 1000
	} );
}

