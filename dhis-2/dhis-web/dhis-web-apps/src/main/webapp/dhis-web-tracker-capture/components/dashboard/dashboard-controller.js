//Controller for dashboard
trackerCapture.controller('DashboardController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                $timeout,
                storage,
                TEIService, 
                TEService,
                OptionSetService,
                EnrollmentService,
                ProgramFactory,
                CurrentSelection) {
    //dashboard items   
    $rootScope.biggerDashboardWidgets = [];
    $rootScope.smallerDashboardWidgets = [];
    $rootScope.enrollmentWidget = {title: 'enrollment', view: "components/enrollment/enrollment.html", show: true, expand: true};
    $rootScope.dataentryWidget = {title: 'dataentry', view: "components/dataentry/dataentry.html", show: true, expand: true};
    $rootScope.reportWidget = {title: 'report', view: "components/report/tei-report.html", show: true, expand: true};
    $rootScope.selectedWidget = {title: 'current_selections', view: "components/selected/selected.html", show: false, expand: true};
    $rootScope.profileWidget = {title: 'profile', view: "components/profile/profile.html", show: true, expand: true};
    $rootScope.relationshipWidget = {title: 'relationships', view: "components/relationship/relationship.html", show: true, expand: true};
    $rootScope.notesWidget = {title: 'notes', view: "components/notes/notes.html", show: true, expand: true};    
   
    $rootScope.biggerDashboardWidgets.push($rootScope.enrollmentWidget);
    $rootScope.biggerDashboardWidgets.push($rootScope.dataentryWidget);
    $rootScope.biggerDashboardWidgets.push($rootScope.reportWidget);
    $rootScope.smallerDashboardWidgets.push($rootScope.selectedWidget);
    $rootScope.smallerDashboardWidgets.push($rootScope.profileWidget);
    $rootScope.smallerDashboardWidgets.push($rootScope.relationshipWidget);
    $rootScope.smallerDashboardWidgets.push($rootScope.notesWidget);
    
    //selections  
    $scope.selectedTeiId = ($location.search()).tei; 
    $scope.selectedProgramId = ($location.search()).program; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.sortedTeiIds = CurrentSelection.getSortedTeiIds();    
    
    $scope.previousTeiExists = false;
    $scope.nextTeiExists = false;
    
    if($scope.sortedTeiIds && $scope.sortedTeiIds.length > 0){
        var current = $scope.sortedTeiIds.indexOf($scope.selectedTeiId);
        
        if(current !== -1){
            if($scope.sortedTeiIds.length-1 > current){
                $scope.nextTeiExists = true;
            }
            
            if(current > 0){
                $scope.previousTeiExists = true;
            }
        }
    }

    $scope.selectedProgram;    
    $scope.selectedTei;    
    
    if($scope.selectedTeiId){
        
        //get option sets
        $scope.optionSets = [];
        OptionSetService.getAll().then(function(optionSets){
            
            angular.forEach(optionSets, function(optionSet){                            
                $scope.optionSets[optionSet.id] = optionSet;
            });
        
            //Fetch the selected entity
            TEIService.get($scope.selectedTeiId, $scope.optionSets).then(function(response){
                $scope.selectedTei = response.data;

                //get the entity type
                TEService.get($scope.selectedTei.trackedEntity).then(function(te){                    
                    $scope.trackedEntity = te;

                    //get enrollments for the selected tei
                    EnrollmentService.getByEntity($scope.selectedTeiId).then(function(response){                    
						var enrollments = angular.isObject(response) && response.enrollments ? response.enrollments : [];
                        var selectedEnrollment = null;
                        if(enrollments.length === 1 && enrollments[0].status === 'ACTIVE'){
                            selectedEnrollment = response.enrollments[0];
                        }
                        
                        ProgramFactory.getAll().then(function(programs){
                            $scope.programs = [];

                            //get programs valid for the selected ou and tei
                            angular.forEach(programs, function(program){
                                if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id) &&
                                   program.trackedEntity.id === $scope.selectedTei.trackedEntity){
                                    $scope.programs.push(program);
                                }

                                if($scope.selectedProgramId && program.id === $scope.selectedProgramId || selectedEnrollment && selectedEnrollment.program === program.id){
                                    $scope.selectedProgram = program;
                                }
                            }); 

                            //broadcast selected items for dashboard controllers
                            CurrentSelection.set({tei: $scope.selectedTei, te: $scope.trackedEntity, prs: $scope.programs, pr: $scope.selectedProgram, enrollments: enrollments, selectedEnrollment: selectedEnrollment, optionSets: $scope.optionSets});
                            $scope.broadCastSelections();                        
                        });
                    });
                });            
            });    
        });
    }    
    
    //listen for any change to program selection
    //it is possible that such could happen during enrollment.
    $scope.$on('mainDashboard', function(event, args) { 
        var selections = CurrentSelection.get();
        $scope.selectedProgram = null;
        angular.forEach($scope.programs, function(pr){
            if(pr.id === selections.pr){
                $scope.selectedProgram = pr;
            }
        });
        $scope.broadCastSelections(); 
    }); 
    
    $scope.broadCastSelections = function(){
        
        var selections = CurrentSelection.get();
        $scope.selectedTei = selections.tei;
        $scope.trackedEntity = selections.te;
        $scope.optionSets = selections.optionSets;
      
        CurrentSelection.set({tei: $scope.selectedTei, te: $scope.trackedEntity, prs: $scope.programs, pr: $scope.selectedProgram, enrollments: selections.enrollments, selectedEnrollment: null, optionSets: $scope.optionSets});
        $timeout(function() { 
            $rootScope.$broadcast('selectedItems', {programExists: $scope.programs.length > 0});            
        }, 100); 
    };     
    
    $scope.back = function(){
        $location.path('/').search({program: $scope.selectedProgramId});                   
    };
    
    $scope.displayEnrollment = false;
    $scope.showEnrollment = function(){
        $scope.displayEnrollment = true;
    };
    
    $scope.removeWidget = function(widget){        
        widget.show = false;
    };
    
    $scope.expandCollapse = function(widget){
        widget.expand = !widget.expand;
    };
    
    $scope.showHideWidgets = function(){
        var modalInstance = $modal.open({
            templateUrl: "components/dashboard/dashboard-widgets.html",
            controller: "DashboardWidgetsController"
        });

        modalInstance.result.then(function () {
        });
    };
    
    $scope.fetchTei = function(mode){
        var current = $scope.sortedTeiIds.indexOf($scope.selectedTeiId);
        var pr = ($location.search()).program;
        var tei = null;
        if(mode === 'NEXT'){            
            tei = $scope.sortedTeiIds[current+1];
        }
        else{            
            tei = $scope.sortedTeiIds[current-1];
        }        
        $location.path('/dashboard').search({tei: tei, program: pr ? pr: null});
    };
});
