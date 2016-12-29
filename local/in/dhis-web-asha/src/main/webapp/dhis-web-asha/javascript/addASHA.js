
var dateOfBirthOfASHA = "";

function getDOBFromAge( age )
{	
	//var age = document.getElementById( "age" ).value;
	
	$.post("getDOBFromAgeReport.action",
		{
			age : age
		},
		function (data)
		{
			dobRecieved(data);
		},'xml');
}

function dobRecieved(dob)
{	
	//byId('bobOfASHA').value = dob.getElementsByTagName( 'dateOfBirth' )[0].firstChild.nodeValue;
	dateOfBirthOfASHA = dob.getElementsByTagName( 'dateOfBirth' )[0].firstChild.nodeValue;
	
	//alert( dob.getElementsByTagName( 'dateOfBirth' )[0].firstChild.nodeValue );
	//alert( dateOfBirthOfASHA );
}


function ageValidation()
{
	var age = document.getElementById( "age" ).value;
	
	if( age != "" )
	{
		getDOBFromAge( age );
	}
	
	//var dateOfBirthOfASHA = dateOfBirthOfASHA;
	
	//alert( dateOfBirthOfASHA );
	
	/*
	var now = new Date();
	
	var year = now.getFullYear();
	
	var month = now.getMonth();
	
	var finalMonth = month + 1;
	
	var date = now.getDate(); 
	
	alert( year + "--" + month + " -- " +  date );
	
	var finalYear = year - age;
	
	alert( finalYear + "--" + month + " -- " +  date );
	*/
	
	
	if( age < 18 )
	{
		showWarningMessage( i18n_age_less_than_18_year );
		document.getElementById( "age" ).value = "";
	}
	
}

function dobValidation()
{
	var dateOfBirth = document.getElementById( "birthDate" ).value;
	
	var dateOfBirthOfASHA = dateOfBirth;
	
	//alert( dateOfBirthOfASHA );
	
	var birthday = new Date( dateOfBirth );
	
	var now = new Date();

	var age = now.getTime() - birthday.getTime();

	var currentAge = Math.round( age/31536000000 );

	
	if( currentAge < 18 )
	{
		showWarningMessage( i18n_age_less_than_18_year );
		document.getElementById( "birthDate" ).value = "";
	}	
}	


function validateDateOfJoining()
{
	var dateOfJoining = document.getElementById( "attr15" ).value;
	
	var dobOfASHA = dateOfBirthOfASHA;
	
	//alert( dateOfBirthOfASHA );
	
	var dateOfJoin = new Date( dateOfJoining );
	
	var ashaDOB = new Date( dobOfASHA );
	
	//var now = new Date();

	var yearDiff = dateOfJoin.getTime() - ashaDOB.getTime();

	var numberOfYear = Math.round( yearDiff/31536000000 );
	
	//alert( numberOfYear );
	
	if( numberOfYear < 18 )
	{
		showWarningMessage( i18n_date_of_joining_less_than_18_year );
		document.getElementById( "attr15" ).value = "";
	}
}	



//RTGS Status

/*
function rtgsStatus()
{
	var attrRtgsStatusObject = document.getElementById('attr30');
	var attrRtgsStatusValue = attrRtgsStatusObject.options[ attrRtgsStatusObject.selectedIndex ].value;
	
	if( attrRtgsStatusValue == "true" || attrRtgsStatusValue == "Yes" )
	{
		document.getElementById( "attr186" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr186" ).value = "";
	    document.getElementById( "attr186" ).disabled = true;
	}  
	
}

*/

//Bank Account Status
function bankAccountStatus()
{
	var attrBankAccountStatusObject = document.getElementById('attr26');
	var attrBankAccountStatusValue = attrBankAccountStatusObject.options[ attrBankAccountStatusObject.selectedIndex ].value;
	
	if( attrBankAccountStatusValue == "true" || attrBankAccountStatusValue == "Yes" )
	{
		document.getElementById( "attr27" ).disabled = false;
		document.getElementById( "attr28" ).disabled = false;
		document.getElementById( "attr29" ).disabled = false;
		document.getElementById( "attr186" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr27" ).value = "";
	    document.getElementById( "attr27" ).disabled = true;
	    
	    document.getElementById( "attr28" ).value = "";
	    document.getElementById( "attr28" ).disabled = true;
	    
	    document.getElementById( "attr29" ).value = "";
	    document.getElementById( "attr29" ).disabled = true;
	    
	    document.getElementById( "attr186" ).value = "";
	    document.getElementById( "attr186" ).disabled = true;
	}  
	
}


