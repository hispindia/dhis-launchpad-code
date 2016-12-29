/*
function validationTraining( arrrValue, fieldID )
{
	
	if( arrrValue == "true" || arrrValue == "Yes" )
	{
		document.getElementById( fieldID ).disabled = false;
	} 
	else
	{
		document.getElementById( fieldID ).value = "";
	    document.getElementById( fieldID ).disabled = true;
	}  
	
}
*/

//Module 1
function validationTrainingMod1()
{
	var attrMod1Object = document.getElementById('attr36');
	var arrrMod1Value = attrMod1Object.options[ attrMod1Object.selectedIndex ].value;
	
	if( arrrMod1Value == "true" || arrrMod1Value == "Yes" )
	{
		document.getElementById( "attr37" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr37" ).value = "";
	    document.getElementById( "attr37" ).disabled = true;
	}  
	
}
//Module 2 -4
function validationTrainingMod2()
{
	var attrMod2Object = document.getElementById('attr38');
	var arrrMod2Value = attrMod2Object.options[ attrMod2Object.selectedIndex ].value;
	
	if( arrrMod2Value == "true" || arrrMod2Value == "Yes" )
	{
		document.getElementById( "attr105" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr105" ).value = "";
	    document.getElementById( "attr105" ).disabled = true;
	}  
	
}


//Module 5
function validationTrainingMod5()
{
	var attrMod5Object = document.getElementById('attr39');
	var arrrMod5Value = attrMod5Object.options[ attrMod5Object.selectedIndex ].value;
	
	if( arrrMod5Value == "true" || arrrMod5Value == "Yes" )
	{
		document.getElementById( "attr106" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr106" ).value = "";
	    document.getElementById( "attr106" ).disabled = true;
	}  
	
}
//Module 1 to 5
function validationTrainingMod1To5()
{
	var attrMod1To5Object = document.getElementById('attr175');
	var arrrMod1To5Value = attrMod1To5Object.options[ attrMod1To5Object.selectedIndex ].value;
	
	if( arrrMod1To5Value == "true" || arrrMod1To5Value == "Yes" )
	{
		document.getElementById( "attr187" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr187" ).value = "";
	    document.getElementById( "attr187" ).disabled = true;
	}  
	
}

//Module 6
function validationTrainingMod6()
{
	var attrMod6Object = document.getElementById('attr40');
	var arrrMod6Value = attrMod6Object.options[ attrMod6Object.selectedIndex ].value;
	
	if( arrrMod6Value == "true" || arrrMod6Value == "Yes" )
	{
		document.getElementById( "attr107" ).disabled = false;
		document.getElementById( "attr193" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr107" ).value = "";
	    document.getElementById( "attr107" ).disabled = true;
	    document.getElementById( "attr193" ).value = "";
	    document.getElementById( "attr193" ).disabled = true;
	}  
	
}

//Module 7
/*
function validationTrainingMod7()
{
	var attrMod7Object = document.getElementById('attr41');
	var arrrMod7Value = attrMod7Object.options[ attrMod7Object.selectedIndex ].value;
	
	if( arrrMod7Value == "true" || arrrMod7Value == "Yes" )
	{
		document.getElementById( "attr108" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr108" ).value = "";
	    document.getElementById( "attr108" ).disabled = true;
	}  
	
}
*/

// module 8
function validationTrainingMod8()
{
	var attrMod7Object = document.getElementById('attr198');
	var arrrMod7Value = attrMod7Object.options[ attrMod7Object.selectedIndex ].value;
	
	if( arrrMod7Value == "true" || arrrMod7Value == "Yes" )
	{
		document.getElementById( "attr199" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr199" ).value = "";
	    document.getElementById( "attr199" ).disabled = true;
	}  
	
}




//HBPNC Round I
function validationTrainingHBPNC1()
{
	var attrHBPNC1Object = document.getElementById('attr42');
	var arrrHBPNC1Value = attrHBPNC1Object.options[ attrHBPNC1Object.selectedIndex ].value;
	
	if( arrrHBPNC1Value == "true" || arrrHBPNC1Value == "Yes" )
	{
		document.getElementById( "attr110" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr110" ).value = "";
	    document.getElementById( "attr110" ).disabled = true;
	}  
	
}

//HBPNC Round 2
function validationTrainingHBPNC2()
{
	var attrHBPNC2Object = document.getElementById('attr43');
	var arrrHBPNC2Value = attrHBPNC2Object.options[ attrHBPNC2Object.selectedIndex ].value;
	
	if( arrrHBPNC2Value == "true" || arrrHBPNC2Value == "Yes" )
	{
		document.getElementById( "attr111" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr111" ).value = "";
	    document.getElementById( "attr111" ).disabled = true;
	}  
	
}


// HIV/AIDS
function validationTrainingHIVAIDS()
{
	var attrHIVAIDSObject = document.getElementById('attr44');
	var arrrHIVAIDSValue = attrHIVAIDSObject.options[ attrHIVAIDSObject.selectedIndex ].value;
	
	if( arrrHIVAIDSValue == "true" || arrrHIVAIDSValue == "Yes" )
	{
		document.getElementById( "attr112" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr112" ).value = "";
	    document.getElementById( "attr112" ).disabled = true;
	}  
	
}

// Menstrual Hygiene
function validationTrainingMenstrualHygiene()
{
	var attrMenstrualHygieneObject = document.getElementById('attr45');
	var arrrMenstrualHygieneValue = attrMenstrualHygieneObject.options[ attrMenstrualHygieneObject.selectedIndex ].value;
	
	if( arrrMenstrualHygieneValue == "true" || arrrMenstrualHygieneValue == "Yes" )
	{
		document.getElementById( "attr113" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr113" ).value = "";
	    document.getElementById( "attr113" ).disabled = true;
	}  
	
}


// STI RTI
function validationTrainingSTIRTI()
{
	var attrSTIRTIObject = document.getElementById('attr46');
	var arrrSTIRTIValue = attrSTIRTIObject.options[ attrSTIRTIObject.selectedIndex ].value;
	
	if( arrrSTIRTIValue == "true" || arrrSTIRTIValue == "Yes" )
	{
		document.getElementById( "attr114" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr114" ).value = "";
	    document.getElementById( "attr114" ).disabled = true;
	}  
	
}



//RNTCP
function validationTrainingRNTCP()
{
	var attrRNTCPObject = document.getElementById('attr47');
	var arrrRNTCPValue = attrRNTCPObject.options[ attrRNTCPObject.selectedIndex ].value;
	
	if( arrrRNTCPValue == "true" || arrrRNTCPValue == "Yes" )
	{
		document.getElementById( "attr115" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr115" ).value = "";
	    document.getElementById( "attr115" ).disabled = true;
	}  
	
}



//Family Planning
function validationTrainingFamilyPlanning()
{
	var attrFamilyPlanningObject = document.getElementById('attr48');
	var arrrFamilyPlanningValue = attrFamilyPlanningObject.options[ attrFamilyPlanningObject.selectedIndex ].value;
	
	if( arrrFamilyPlanningValue == "true" || arrrFamilyPlanningValue == "Yes" )
	{
		document.getElementById( "attr116" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr116" ).value = "";
	    document.getElementById( "attr116" ).disabled = true;
	}  
	
}

//Pulse Polio
function validationTrainingPulsePolio()
{
	var attrPulsePolioObject = document.getElementById('attr49');
	var arrrPulsePolioValue = attrPulsePolioObject.options[ attrPulsePolioObject.selectedIndex ].value;
	
	if( arrrPulsePolioValue == "true" || arrrPulsePolioValue == "Yes" )
	{
		document.getElementById( "attr117" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr117" ).value = "";
	    document.getElementById( "attr117" ).disabled = true;
	}  
	
}

//Counselling
/*
function validationTrainingCounselling()
{
	var attrCounsellingObject = document.getElementById('attr50');
	var arrrCounsellingValue = attrCounsellingObject.options[ attrCounsellingObject.selectedIndex ].value;
	
	if( arrrCounsellingValue == "true" || arrrCounsellingValue == "Yes" )
	{
		document.getElementById( "attr118" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr118" ).value = "";
	    document.getElementById( "attr118" ).disabled = true;
	}  
	
}
*/
// Any Other Training
function validationAnyOtherTraining()
{
	var attrObject = document.getElementById('attr51');
	var arrrValue = attrObject.options[ attrObject.selectedIndex ].value;
	
	
	if( arrrValue == "true" || arrrValue == "Yes" )
	{
		document.getElementById( "attr120" ).disabled = false;
		document.getElementById( "attr119" ).disabled = false;
	} 
	else
	{
		document.getElementById( "attr120" ).value = "";
	    document.getElementById( "attr120" ).disabled = true;
	    document.getElementById( "attr119" ).value = "";
	    document.getElementById( "attr119" ).disabled = true;
	}  
	
}