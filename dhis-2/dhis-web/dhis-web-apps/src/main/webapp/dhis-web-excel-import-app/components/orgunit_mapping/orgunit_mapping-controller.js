/* global excelUpload, angular */

//Controller for managing templates
excelUpload.controller('OrgUnitMappingController',
        function($rootScope,
                $scope,
                $timeout,
                $route,
                $filter,
                ExcelMappingService,
                ValidationRuleService,
                CurrentSelection,
                ExcelReaderService,
                MetaDataFactory,
                orderByFilter,
                OrgUnitService,
                DialogService) {
					
	
	//needed variables
	$scope.orgUnits = {};
	$scope.orgMappings = {};
	
	//retrieving all the needed things
	//**************************************************************************************************************
	
	//orgMappings
	$("#templateProgress").html("Retrieving settings...");
	ExcelMappingService.get('Excel-import-app-orgunit-mapping').then(function(tem){
		if(!jQuery.isEmptyObject(tem))
			$scope.orgMappings = tem;
		else
			$scope.orgMappings = { omapping : [] };
		
		console.log( $scope.orgMappings );
		
		//org units
		$("#templateProgress").html("Fetching all organisation units...");
		$.get('../api/organisationUnits.json?paging=false', function(ou){
			console.log( ou );
			$scope.orgUnits = ou.organisationUnits;
			$scope.printLabels();
			$("#loader").hide();
		}).
		fail(function(jqXHR, textStatus, errorThrown){
			$("#templateProgress").html("Failed to fetch organisation unit groups ( " + errorThrown + " )");
		});
	});
	//**************************************************************************************************************
	
	$scope.printLabels = function(){
		setTimeout(
			function()
			{
				if( $("#selOrgId").val() != "" )
				{
					var isFound = false;
					var htmlString = '<tr><td colspan="2" align="center"> Labels of ' + $("#selOrgName").val() + '</td></tr>';
					$.each( $scope.orgMappings.omapping, function( i, m){
						
						if( m.orgUnit == $("#selOrgId").val() )
						{
							isFound = true;
							htmlString += '<tr>';
							htmlString += '<td>' + m.label + '</td>';
							var ev = "removeLabel('" + m.label + "')";
							htmlString += '<td align="right"> <input type="button" style="padding: 0 10px;" class="btn btn-danger" value="X" onclick="'+ ev +'"/> </td>';
							htmlString += '</tr>';
						}
					});
					
					if( !isFound )
						htmlString = '<tr><td colspan="2" align="center"> No labels found for ' + $("#selOrgName").val() + '</td></tr>';
					$("#lblTbl").html( htmlString );
					
					$("#errMsg").html("");
					$("#errMsg").slideUp();
					$("#infoMsg").html("");
					$("#infoMsg").slideUp();
				}
			} 
		, 500 );
	};

	$scope.uploadJSON = function () {
		$("#fileUpload").click();
		$("#errMsg").html("");
		$("#errMsg").slideUp();
		$("#infoMsg").html("");
		$("#infoMsg").slideUp();
		$("#fileUpload").change(function(event){
			var uploadedFile = event.target.files[0];
			console.info(event.target.files[0]);
			if(uploadedFile.type != "text/javascript" && uploadedFile.type != "application/x-javascript" && uploadedFile.type != "application/javascript") {
				$('#modalHeader').removeClass().addClass('alert alert-danger').addClass('modal-header');
				$("#msgIcon").removeClass().addClass('glyphicon glyphicon glyphicon-exclamation-sign');
				$("#msgTitle").html("&nbsp;&nbsp;Error Message");
				$("#msgTitle").css('color','darkred');
				$("#errorContent").html("");
				$("#errorContent").append("Wrong file type " + uploadedFile.type + ".</br>" + " Please select .js format file.");
				var fileUpload = $("#fileUpload");
				fileUpload.replaceWith( fileUpload.val('').clone( true ) );
				$("#errorModal").modal("show");
				return false;
			}

			if (uploadedFile) {
				try {
					var readFile = new FileReader();
					readFile.onload = function (e) {
						var contents = e.target.result;
						var json = JSON.parse(contents);
						var alreadyMappedOrgUnits = [];
						var hasAlreadyMappedOrgUnits = false;

						$.each(json.omapping, function(i,item){
							var found = $scope.isMappingFound(item.label);
							if( !found )
							{
								var newMapping = {};
								newMapping.label = item.label;
								newMapping.orgUnit = item.orgUnit;
								$scope.orgMappings.omapping.push( newMapping );
								$scope.saveMapping();
							}
							else
							{
								alreadyMappedOrgUnits.push([found,item.label]);
								hasAlreadyMappedOrgUnits = true;
							}
						});

						if(hasAlreadyMappedOrgUnits) {
							var errorStr = "";
							errorStr += "Upload completed with following issues:" + "</br>";
							errorStr += "Following organisation unit's mapping was already done" + "</br></br>";
							errorStr += "<center><table cellspacing='6' cellpadding='4'>";
							errorStr += "<tr style='text-align: center;'><td><strong>Organisation Unit</strong></td><td><strong>Label</strong></td></tr>";
							$.each(alreadyMappedOrgUnits, function (i) {
								errorStr += "<tr style='text-align: center;'><td>" + alreadyMappedOrgUnits[i][0] + "</td><td>" + alreadyMappedOrgUnits[i][1] + "</td></tr>";
							});
							errorStr += "</table></center>";

							$('#modalHeader').removeClass().addClass('alert alert-warning').addClass('modal-header');
							$("#msgIcon").removeClass().addClass('glyphicon glyphicon-exclamation-sign');
							$("#msgTitle").html("&nbsp;&nbsp;Warning Message");
							$("#msgTitle").css('color','darkgoldenrod');
							$("#errorContent").html("");
							$("#errorContent").append(errorStr);
							$("#errorModal").modal("show");
							$scope.printLabels();
						}
						else{
							$('#modalHeader').removeClass().addClass('alert alert-success').addClass('modal-header');
							$("#msgIcon").removeClass().addClass('glyphicon glyphicon-ok-circle');
							$("#msgTitle").html("&nbsp;&nbsp;Message");
							$("#msgTitle").css('color','darkgreen');
							$("#errorContent").html("");
							$("#errorContent").append('Upload Successful');
							$("#errorModal").modal("show");
							$scope.printLabels();
						}
						var fileUpload = $("#fileUpload");
						fileUpload.replaceWith( fileUpload.val('').clone( true ) );
					};
					readFile.readAsText(uploadedFile);
				}
				catch(err){
					$("#errMsg").html(err.message);
					$("#errMsg").slideDown();
				}
			} else {
				$("#errMsg").html("Failed to load file");
				$("#errMsg").slideDown();
			}
		});
	}

	$scope.addMapping = function(){
		var found = $scope.isMappingFound($("#newLabel").val());
		
		if( $("#newLabel").val() == "" )
		{
			$("#errMsg").html("Label cannot be empty");
			$("#errMsg").slideDown();
		}
		else if( !found )
		{
			var newMapping = {};
			newMapping.label = $("#newLabel").val();
			newMapping.orgUnit = $("#selOrgId").val();
			$("#errMsg").html("");
			$("#errMsg").slideUp();
			$("#infoMsg").html("");
			$("#infoMsg").slideUp();
			$("#newLabel").val("");
			
			$scope.orgMappings.omapping.push( newMapping );
			$scope.printLabels();
			$scope.saveMapping();
		}
		else
		{
			$("#errMsg").html("This label is already taken under " + found );
			$("#errMsg").slideDown();
		}
	};
	
	$scope.isMappingFound = function( label ){
		var found = false;
		
		$.each( $scope.orgMappings.omapping, function( i, m){
			if( m.label == label )
			{
				$.each($scope.orgUnits, function( i, o){
					if( m.orgUnit == o.id )
						found = o.name;
				});
			}
		});
		
		console.log( found );
		return found;		
	};
	
	$scope.removeLabel= function( label ){
		var removeIndex = -1;
		
		$.each( $scope.orgMappings.omapping, function( i, m){
			if( m.label == label )
				removeIndex = i;
		});
		
		if( removeIndex >= 0 )
		{
			$scope.orgMappings.omapping.splice( removeIndex , 1 );
			$scope.saveMapping();
			$scope.printLabels();
		}
	};
	
	$scope.saveMapping = function(){
		ExcelMappingService.save('Excel-import-app-orgunit-mapping',$scope.orgMappings ).then(function(r){
			//console.log(r);
		});
	};
});