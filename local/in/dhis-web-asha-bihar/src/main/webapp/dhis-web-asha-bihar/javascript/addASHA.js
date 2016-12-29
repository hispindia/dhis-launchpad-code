
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


function validateEducationLevel()
{
	var attrEducationObject = document.getElementById('attr12');
	var arrrEducationValue = attrEducationObject.options[ attrEducationObject.selectedIndex ].value;
	
	if( arrrEducationValue == "194" )
	{
		document.getElementById( "attr202" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr202" ).value = "";
	    document.getElementById( "attr202" ).disabled = true;
	}  
	
}

function validateMiscellaneousReason()
{
	var attrMiscellaneousReasonObject = document.getElementById('attr185');
	var arrrMiscellaneousReasonValue = attrMiscellaneousReasonObject.options[ attrMiscellaneousReasonObject.selectedIndex ].value;
	
	if( arrrMiscellaneousReasonValue == "213" )
	{
		document.getElementById( "attr207" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr207" ).value = "";
	    document.getElementById( "attr207" ).disabled = true;
	}  
	
}
