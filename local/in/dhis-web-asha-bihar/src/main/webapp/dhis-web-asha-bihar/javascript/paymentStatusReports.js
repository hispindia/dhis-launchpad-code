function formValidations()
{                       
    var periodList = document.getElementById("selectedPeriodId");
    var periodIndex = periodList.selectedIndex;
    
    //alert("validation");
    //alert(periodList.options[periodIndex].text);
    //alert(periodList.options[periodIndex].value);
    var ouId = document.getElementById("orgUnitId");
    var orgunitIdValue = ouId.value;

    if( periodList.options[periodIndex].value == "-1" ) 
    {
    	showWarningMessage( "Please Select Period" );
    	//alert("Please Select Period");
        return false;
    }                       
    else if( orgunitIdValue == null || orgunitIdValue == "" || orgunitIdValue == " " ) 
    {
    	showWarningMessage( "Please Select Period" );
    	//alert("Please Select OrganisationUnit"); 
        return false;
    }       
     
    return true;
}           