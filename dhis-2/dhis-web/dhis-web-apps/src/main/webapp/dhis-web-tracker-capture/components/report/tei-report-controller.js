/* global trackerCapture, angular */

//conroller for tei report
trackerCapture.controller('TeiReportController',
        function($scope,
                CurrentSelection,
                storage,
                DateUtils,
                EventUtils,
                TEIService,
                ProgramStageFactory,
                EnrollmentService,
                DHIS2EventFactory) {  
    $scope.showProgramReportDetailsDiv = false;
    $scope.enrollmentsByProgram = [];

    $scope.$on('dashboardWidgets', function(event, args) {
        $scope.showProgramReportDetailsDiv = false;
        var selections = CurrentSelection.get();        
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        $scope.selectedTei = selections.tei;  
        $scope.selectedEntity = selections.te;
        $scope.selectedProgram = selections.pr;
        $scope.optionSets = selections.optionSets;
        $scope.programs = selections.prs;
        $scope.programNames = selections.prNames;  
        $scope.programStageNames = selections.prStNames;
    
        angular.forEach(selections.enrollments, function(en){            
            $scope.enrollmentsByProgram[en.program] = en;
        });
        
        if( $scope.selectedTei ){            
            $scope.getEvents();
        }
        
    });
    
    $scope.getEvents = function(){
        
        $scope.dataFetched = false;
        $scope.dataExists = false;
        var programId = null;
        
        if($scope.selectedProgram){
            programId = $scope.selectedProgram.id;
        }
        
        $scope.report = [];
        angular.forEach($scope.programs, function(pr){
            $scope.report[pr.id] = {};
        });
        
        DHIS2EventFactory.getEventsByProgram($scope.selectedTei.trackedEntityInstance, programId).then(function(eventList){
            angular.forEach(eventList, function(ev){
                if(ev.program && $scope.report[ev.program] && ev.orgUnit){       
                    ev.visited = true;
                    ev.dueDate = DateUtils.formatFromApiToUser(ev.dueDate);  
                    ev.sortingDate = ev.dueDate;
                    ev.name = $scope.programStageNames[ev.programStage].name;
                    ev.programName = $scope.programNames[ev.program].name;                    
                    if(!$scope.report[ev.program].enrollments){
                        $scope.report[ev.program] = {enrollments: {}};
                    }
                    ev.statusColor = EventUtils.getEventStatusColor(ev); 
                    
                    if(ev.eventDate){
                        ev.eventDate = DateUtils.format(ev.eventDate);
                        ev.sortingDate = ev.eventDate;
                    }
                    else{
                        ev.visited = false;
                    }                 

                    if(ev.enrollment){
                        if($scope.report[ev.program].enrollments[ev.enrollment]){
                            $scope.report[ev.program].enrollments[ev.enrollment].push(ev);
                        }
                        else{
                            $scope.report[ev.program].enrollments[ev.enrollment]= [ev];
                        }
                    }                    
                    if(!$scope.dataExists){
                        $scope.dataExists = true;
                    }
                }                
            });
            $scope.dataFetched = true;
        });
    };
    
    $scope.showProgramReportDetails = function(pr){
        
        var selections = CurrentSelection.get();
        $scope.selectedTei = selections.tei;
        
        $scope.showProgramReportDetailsDiv = !$scope.showProgramReportDetailsDiv;
        $scope.selectedProgram = pr;
        $scope.selectedReport = $scope.report[pr.id];
        
        //today as report date
        $scope.today = DateUtils.getToday();

        //process tei attributes, this is to have consistent display so that the tei 
        //contains program attributes whether it has value or not
        TEIService.processAttributes($scope.selectedTei, $scope.selectedProgram, $scope.enrollmentsByProgram[pr.id]).then(function(tei){
            $scope.tei = tei;
        });
        
        //get program stage for the selected program
        //they are needed to assign data element names for event data values
        $scope.programStages = [];  
        $scope.allowProvidedElsewhereExists = [];
        angular.forEach($scope.selectedProgram.programStages, function(st){
            ProgramStageFactory.get(st.id).then(function(stage){
                $scope.programStages[stage.id] = stage;
                var providedElsewhereExists = false;
                for(var i=0; i<stage.programStageDataElements.length && !providedElsewhereExists; i++){                
                    if(stage.programStageDataElements[i].allowProvidedElsewhere){
                        providedElsewhereExists = true;
                        $scope.allowProvidedElsewhereExists[st.id] = true;
                    }                
                }            
            });
        });
        
        //program reports come grouped in enrollment, process for each enrollment
        $scope.enrollments = [];        
        angular.forEach(Object.keys($scope.selectedReport.enrollments), function(enr){        
            //format report data values
            angular.forEach($scope.selectedReport.enrollments[enr], function(ev){

                angular.forEach(ev.notes, function(note){
                    note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
                }); 

                if(ev.dataValues){
                    angular.forEach(ev.dataValues, function(dv){
                        if(dv.dataElement){
                            ev[dv.dataElement] = dv;
                        }                    
                    });
                }
            });

            //get enrollment details
            EnrollmentService.get(enr).then(function(enrollment){
                enrollment.dateOfEnrollment = DateUtils.formatFromApiToUser(enrollment.dateOfEnrollment);
                enrollment.dateOfIncident = DateUtils.formatFromApiToUser(enrollment.dateOfIncident);            
                angular.forEach(enrollment.notes, function(note){
                    note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
                });            
                $scope.enrollments.push(enrollment);               
            });
        });    
    };
    
    $scope.close = function(){
        $scope.showProgramReportDetailsDiv = false;
    };
    
    $scope.print = function(){
        $scope.showProgramReportDetailsDiv = false;
    };
});