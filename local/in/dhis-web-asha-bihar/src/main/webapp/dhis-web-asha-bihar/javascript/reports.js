
// Load ChildrenOrgUnit List
function loadChildrenOrgUnit()
{
	var orgUnitId = $( '#orgUnitId' ).val();

	if ( orgUnitId == "-1" )
	{
		showWarningMessage( "Please Select District" );
		
		document.getElementById( "blockId" ).disabled = true;

		return false;
	}
	
	else
	{
		enable('blockId');

		$.post("getChildrenOrgUnitList.action",
				{
					orgUnitId:orgUnitId
				},
				function(data)
				{
					
					populateChildrenOrgUnitList( data );
									
				},'xml');
	}
	
}


function populateChildrenOrgUnitList( data )
{
	var blockId = document.getElementById("blockId");
	clearList( blockId );
	
	var orgUnitList = data.getElementsByTagName("orgUnit");
	
	blockId.options[0] = new Option( "Select", "-1" , false, false);
	
	for ( var i = 0; i < orgUnitList.length; i++ )
	{
		var id = orgUnitList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = orgUnitList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		blockId.add(option, null);
	} 
}
