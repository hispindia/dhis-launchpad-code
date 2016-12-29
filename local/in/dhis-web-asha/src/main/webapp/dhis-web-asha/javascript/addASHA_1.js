
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




