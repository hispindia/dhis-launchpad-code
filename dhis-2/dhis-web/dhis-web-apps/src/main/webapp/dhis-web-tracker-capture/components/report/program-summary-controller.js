trackerCapture.controller('ProgramSummaryController',
        function($scope,
                $modal,
                DateUtils,
                EventUtils,
                TEIService,
                TEIGridService,
                AttributesFactory,
                ProgramFactory,
                CurrentSelection,
                OptionSetService,
                DHIS2EventFactory,
                storage) {    
    $scope.today = DateUtils.getToday();
    
    $scope.ouModes = [{name: 'SELECTED'}, {name: 'CHILDREN'}, {name: 'DESCENDANTS'}, {name: 'ACCESSIBLE'}];         
    $scope.selectedOuMode = $scope.ouModes[0];
    $scope.report = {};
    
    $scope.optionSets = CurrentSelection.getOptionSets();
    if(!$scope.optionSets){
        $scope.optionSets = [];
        OptionSetService.getAll().then(function(optionSets){
            angular.forEach(optionSets, function(optionSet){                        
                $scope.optionSets[optionSet.id] = optionSet;
            });

            CurrentSelection.setOptionSets($scope.optionSets);
        });
    }
    
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function() {      
        $scope.selectedProgram = null;
        if( angular.isObject($scope.selectedOrgUnit)){            
            storage.set('SELECTED_OU', $scope.selectedOrgUnit);            
            $scope.loadPrograms($scope.selectedOrgUnit);
        }
    });
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function(orgUnit) {        
        $scope.selectedOrgUnit = orgUnit;        
        if (angular.isObject($scope.selectedOrgUnit)){
            ProgramFactory.getProgramsByOu($scope.selectedOrgUnit, $scope.selectedProgram).then(function(response){
                $scope.programs = response.programs;
                $scope.selectedProgram = response.selectedProgram;
            });
        }        
    };
    
    //watch for selection of program
    $scope.$watch('selectedProgram', function() {   
        if( angular.isObject($scope.selectedProgram)){            
            $scope.reportStarted = false;
            $scope.dataReady = false;
        }
    });
    
    $scope.generateReport = function(program, report, ouMode){
        
        $scope.selectedProgram = program;
        $scope.report = report;
        $scope.selectedOuMode = ouMode;
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid || !$scope.selectedProgram){
            return false;
        }
        
        $scope.reportStarted = true;
        $scope.dataReady = false;
        
        $scope.programStages = [];
        angular.forEach($scope.selectedProgram.programStages, function(stage){
            $scope.programStages[stage.id] = stage;
        });
            
        AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){            
            var grid = TEIGridService.generateGridColumns(atts, $scope.selectedOuMode.name);   
            $scope.gridColumns = grid.columns;
        });  
        
        //fetch TEIs for the selected program and orgunit/mode
        TEIService.search($scope.selectedOrgUnit.id, 
                            $scope.selectedOuMode.name,
                            null,
                            'program=' + $scope.selectedProgram.id,
                            null,
                            $scope.pager,
                            false).then(function(data){                     
            
            //process tei grid
            var teis = TEIGridService.format(data,true, $scope.optionSets);     
            $scope.teiList = [];

            DHIS2EventFactory.getByOrgUnitAndProgram($scope.selectedOrgUnit.id, 
                                                    $scope.selectedOuMode.name, 
                                                    $scope.selectedProgram.id, 
                                                    DateUtils.formatFromUserToApi(report.startDate), 
                                                    DateUtils.formatFromUserToApi(report.endDate)).then(function(eventList){
                $scope.dhis2Events = [];                
                angular.forEach(eventList, function(ev){
                    if(ev.trackedEntityInstance){
                        ev.name = $scope.programStages[ev.programStage].name;
                        ev.programName = $scope.selectedProgram.name;
                        ev.statusColor = EventUtils.getEventStatusColor(ev); 
                        ev.eventDate = DateUtils.formatFromApiToUser(ev.eventDate);
                        
                        if($scope.dhis2Events[ev.trackedEntityInstance]){
                            if(teis.rows[ev.trackedEntityInstance]){
                                $scope.teiList.push(teis.rows[ev.trackedEntityInstance]);
                                delete teis.rows[ev.trackedEntityInstance];
                            }                     
                            $scope.dhis2Events[ev.trackedEntityInstance].push(ev);
                        }
                        else{
                            if(teis.rows[ev.trackedEntityInstance]){
                                $scope.teiList.push(teis.rows[ev.trackedEntityInstance]);
                                delete teis.rows[ev.trackedEntityInstance];
                            }  
                            $scope.dhis2Events[ev.trackedEntityInstance] = [ev];
                        }
                    }
                });
                $scope.reportStarted = false;
                $scope.dataReady = true;                
            });
        });
    };
    
    $scope.showEventDetails = function(dhis2Event, selectedTei){
        
        var modalInstance = $modal.open({
            templateUrl: 'components/report/event-details.html',
            controller: 'EventDetailsController',
            resolve: {
                dhis2Event: function () {
                    return dhis2Event;
                },
                gridColumns: function(){
                    return $scope.gridColumns;
                },
                selectedTei: function(){
                    return selectedTei;
                },
                entityName: function(){
                    return $scope.selectedProgram.trackedEntity.name;
                },
                reportMode: function(){
                    return 'PROGRAM';
                }
            }
        });

        modalInstance.result.then({
        });
    };    
})

//Controller for event details
.controller('EventDetailsController', 
    function($scope, 
            $modalInstance,
            orderByFilter,
            DateUtils,
            ProgramStageFactory,
            dhis2Event,
            selectedTei,
            gridColumns,
            entityName,
            reportMode){
    
    $scope.selectedTei = selectedTei;
    $scope.gridColumns = gridColumns;
    $scope.entityName = entityName;
    $scope.reportMode = reportMode;
    $scope.currentEvent = dhis2Event;
    $scope.currentEvent.providedElsewhere = [];
    
    if(!angular.isUndefined( $scope.currentEvent.notes)){
        $scope.currentEvent.notes = orderByFilter($scope.currentEvent.notes, '-storedDate');            
        angular.forEach($scope.currentEvent.notes, function(note){
            note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
        });
    }
    
    ProgramStageFactory.get($scope.currentEvent.programStage).then(function(stage){
        $scope.currentStage = stage;

        $scope.allowProvidedElsewhereExists = false;
        angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){
            $scope.currentStage.programStageDataElements[prStDe.dataElement.id] = prStDe.dataElement;
            if(prStDe.allowProvidedElsewhere){
                $scope.allowProvidedElsewhereExists = true;
                $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] = '';   
            }                
        });
        angular.forEach($scope.currentEvent.dataValues, function(dataValue){
            if(dataValue.dataElement){
                $scope.currentEvent[dataValue.dataElement] = dataValue;
            }            
        });
    });
    
    $scope.close = function () {
        $modalInstance.close();
    };
});