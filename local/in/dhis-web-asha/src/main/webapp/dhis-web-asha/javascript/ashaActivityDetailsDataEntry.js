
//Mobilization of Community for RI
function selectMobilization( mobilization1, sessionDate, sessionPlace, noOfChild )
{	
	
	if( document.getElementById( mobilization1 ).checked )
	{
		
		document.getElementById( sessionDate ).disabled = false;
		document.getElementById( sessionPlace ).disabled = false;
		document.getElementById( noOfChild ).disabled = false;
		
		//jQuery("#attr404").addClass('required',true);
		
		jQuery("#" + sessionDate + "").addClass('required',true);
		
		
	} 
	else
	{
		document.getElementById( sessionDate ).value = "";
		document.getElementById( sessionDate ).disabled = true;
		
		document.getElementById( sessionPlace ).value = "";
		document.getElementById( sessionPlace ).disabled = true;
		
		document.getElementById( noOfChild ).value = "";
		document.getElementById( noOfChild ).disabled = true;
		
		jQuery("#" + sessionDate + "").removeClass();
		
	}  
	
}


//Other Session
function selectOtherSession( otherSessionCheckBox, sessionDate, purpose, sessionPlace, noOfChild )
{	
	
	if( document.getElementById( otherSessionCheckBox ).checked )
	{
		
		document.getElementById( sessionDate ).disabled = false;
		document.getElementById( purpose ).disabled = false;
		document.getElementById( sessionPlace ).disabled = false;
		document.getElementById( noOfChild ).disabled = false;
		
		//jQuery("#attr404").addClass('required',true);
		
		jQuery("#" + sessionDate + "").addClass('required',true);
		
		
	} 
	else
	{
		document.getElementById( sessionDate ).value = "";
		document.getElementById( sessionDate ).disabled = true;
		
		document.getElementById( purpose ).value = "";
		document.getElementById( purpose ).disabled = true;
		
		document.getElementById( sessionPlace ).value = "";
		document.getElementById( sessionPlace ).disabled = true;
		
		document.getElementById( noOfChild ).value = "";
		document.getElementById( noOfChild ).disabled = true;
		
		jQuery("#" + sessionDate + "").removeClass();
		
	}  
	
}


//TA/DA for training programmes
function selectTADAForTraining( tADACheckBox, sessionDate, sessionPlace, noOfDays )
{	
	if( document.getElementById( tADACheckBox ).checked )
	{
		
		document.getElementById( sessionDate ).disabled = false;
		document.getElementById( sessionPlace ).disabled = false;
		
		document.getElementById( noOfDays ).disabled = false;
		
		jQuery("#" + sessionDate + "").addClass('required',true);
		
	} 
	else
	{
		document.getElementById( sessionDate ).value = "";
		document.getElementById( sessionDate ).disabled = true;
		
		document.getElementById( sessionPlace ).value = "";
		document.getElementById( sessionPlace ).disabled = true;
		
		document.getElementById( noOfDays ).value = "";
		document.getElementById( noOfDays ).disabled = true;
		
		jQuery("#" + sessionDate + "").removeClass();
	}  
	
}


// Validate No Of Days In Training Programmes
function validateNoOfDaysInTrainingProgrammes( tADACheckBox )
{	
	if( document.getElementById( tADACheckBox ).checked )
	{
		var noOfDaysInTraining = $( '#deps437' ).val();
		
		if( noOfDaysInTraining > 10 )
		{
			showWarningMessage( "Number of days in training should not exceed more than 10 days" );
			document.getElementById( "deps437" ).value = 0;
		}
		
	} 
	else
	{
		
	}  
	
}


//Community Mobilization for Vasectomy and Community Mobilization for Tubectomy
function selectCommunityMobilization( mobilizationCheckBox, sessionDate, sessionPlace )
{	
	
	if( document.getElementById( mobilizationCheckBox ).checked )
	{
		
		document.getElementById( sessionDate ).disabled = false;
		document.getElementById( sessionPlace ).disabled = false;
		
		jQuery("#" + sessionDate + "").addClass('required',true);
		
	} 
	else
	{
		document.getElementById( sessionDate ).value = "";
		document.getElementById( sessionDate ).disabled = true;
		
		document.getElementById( sessionPlace ).value = "";
		document.getElementById( sessionPlace ).disabled = true;
		
		jQuery("#" + sessionDate + "").removeClass();
	}  
	
}




// Other meeting
function selectOtherMeeting( otherMeetingCheckBox, sessionDate, purpose, sessionPlace )
{	
	
	if( document.getElementById( otherMeetingCheckBox ).checked )
	{
		
		document.getElementById( sessionDate ).disabled = false;
		document.getElementById( purpose ).disabled = false;
		document.getElementById( sessionPlace ).disabled = false;
		
		jQuery("#" + sessionDate + "").addClass('required',true);
		
	} 
	else
	{
		document.getElementById( sessionDate ).value = "";
		document.getElementById( sessionDate ).disabled = true;
		
		document.getElementById( purpose ).value = "";
		document.getElementById( purpose ).disabled = true;
		
		document.getElementById( sessionPlace ).value = "";
		document.getElementById( sessionPlace ).disabled = true;
		
		jQuery("#" + sessionDate + "").removeClass();
	}  
	
}









//Sample Test
function selectSampleTest( sampleTestCheckBox, sampleDate, sampleNumber, samplePlace )
{	
	
	if( document.getElementById( sampleTestCheckBox ).checked )
	{
		
		document.getElementById( sampleDate ).disabled = false;
		document.getElementById( sampleNumber ).disabled = false;
		document.getElementById( samplePlace ).disabled = false;
		
		jQuery("#" + sampleDate + "").addClass('required',true);
		
	} 
	else
	{
		document.getElementById( sampleDate ).value = "";
		document.getElementById( sampleDate ).disabled = true;
		
		document.getElementById( sampleNumber ).value = "";
		document.getElementById( sampleNumber ).disabled = true;
		
		document.getElementById( samplePlace ).value = "";
		document.getElementById( samplePlace ).disabled = true;
		
		jQuery("#" + sampleDate + "").removeClass();
		
	}  
	
}


//validate No Of Sample Taken
function validateNoOfSampleTaken( sampleCheckBox )
{	
	if( document.getElementById( sampleCheckBox ).checked )
	{
		var noOfSampleTaken = $( '#deps428' ).val();
		
		if( noOfSampleTaken < 50 )
		{
			showWarningMessage( "Number of samples taken less than 50, Incentive to be generated if more tnan 50 samples are taken" );
		}
		
	} 
	else
	{
		
	}  
	
}


//Award Check Box
function selectAward( awardCheckBox, award, awardDate )
{	
	if( document.getElementById( awardCheckBox ).checked )
	{
		
		document.getElementById( award ).disabled = false;
		//document.getElementById( awardAmount ).disabled = false;
		document.getElementById( awardDate ).disabled = false;
		
		jQuery("#" + award + "").addClass('required',true);
		jQuery("#" + awardDate + "").addClass('required',true);
		
	} 
	else
	{
		document.getElementById( award ).value = "";
		document.getElementById( award ).disabled = true;
		
		//document.getElementById( awardAmount ).value = "";
		//document.getElementById( awardAmount ).disabled = true;
		
		document.getElementById( awardDate ).value = "";
		document.getElementById( awardDate ).disabled = true;
		
		jQuery("#" + award + "").removeClass();
		jQuery("#" + awardDate + "").removeClass();
	}  
	
}


//Reason for inactive Check Box
function selectReasonForInActive( inactiveCheckBox, reason )
{	
	if( document.getElementById( inactiveCheckBox ).checked )
	{
		document.getElementById( reason ).disabled = false;
		
		jQuery("#" + reason + "").addClass('required',true);
	} 
	else
	{
		document.getElementById( reason ).value = "";
		document.getElementById( reason ).disabled = true;
		
		jQuery("#" + reason + "").removeClass();
	}  
	
}
