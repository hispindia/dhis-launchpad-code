trackerCapture.controller('DataEntryController',
        function($scope,
                DateUtils,
                EventUtils,
                orderByFilter,
                storage,
                ProgramStageFactory,
                DHIS2EventFactory,
                OptionSetService,
                ModalService,
                DialogService,
                CurrentSelection,
                CustomFormService) {
    
    //Data entry form
    $scope.dataEntryOuterForm = {};
    $scope.displayCustomForm = false;
    $scope.currentElement = {};
    $scope.schedulingEnabled = false;
    
    var userProfile = storage.get('USER_PROFILE');
    var storedBy = userProfile && userProfile.username ? userProfile.username : '';
    
    var today = DateUtils.getToday();
    $scope.invalidDate = false;
    
    //note
    $scope.note = '';
    
    //event color legend
    $scope.eventColors = [
                            {color: 'alert-success', description: 'completed'},
                            {color: 'alert-info', description: 'executed'},
                            {color: 'alert-warning', description: 'ontime'},
                            {color: 'alert-danger', description: 'overdue'},
                            {color: 'alert-default', description: 'skipped'}
                         ];
    $scope.showEventColors = false;
    
    //listen for the selected items
    $scope.$on('dashboardWidgets', function() {        
        $scope.showDataEntryDiv = false;
        $scope.showEventCreationDiv = false;
        $scope.showDummyEventDiv = false;        
        $scope.currentDummyEvent = null;
        $scope.currentEvent = null;
        $scope.currentStage = null;
            
        $scope.allowEventCreation = false;
        $scope.repeatableStages = [];        
        $scope.dhis2Events = null;
        
        var selections = CurrentSelection.get();          
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        $scope.selectedEntity = selections.tei;      
        $scope.selectedProgram = selections.pr;        
        $scope.selectedEnrollment = selections.selectedEnrollment;   
        $scope.optionSets = selections.optionSets;
        
        $scope.selectedProgramWithStage = [];        
        if($scope.selectedOrgUnit && $scope.selectedProgram && $scope.selectedEntity && $scope.selectedEnrollment){
            
            ProgramStageFactory.getByProgram($scope.selectedProgram).then(function(stages){
                
                angular.forEach(stages, function(stage){
                    if(stage.openAfterEnrollment){
                        $scope.currentStage = stage;
                    }
                    $scope.selectedProgramWithStage[stage.id] = stage;
                });
                
                $scope.getEvents();                
            });
        }
    });
    
    $scope.getEvents = function(){
        $scope.dhis2Events = [];
        DHIS2EventFactory.getEventsByProgram($scope.selectedEntity.trackedEntityInstance, $scope.selectedOrgUnit.id, $scope.selectedProgram.id).then(function(events){
            if(angular.isObject(events)){
                angular.forEach(events, function(dhis2Event){                    
                    if(dhis2Event.enrollment === $scope.selectedEnrollment.enrollment){
                        if(dhis2Event.notes){
                            dhis2Event.notes = orderByFilter(dhis2Event.notes, '-storedDate');            
                            angular.forEach(dhis2Event.notes, function(note){
                                note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
                            });
                        }
                    
                        var eventStage = $scope.selectedProgramWithStage[dhis2Event.programStage];
                        if(angular.isObject(eventStage)){

                            dhis2Event.name = eventStage.name; 
                            dhis2Event.reportDateDescription = eventStage.reportDateDescription;
                            dhis2Event.dueDate = DateUtils.formatFromApiToUser(dhis2Event.dueDate);
                            dhis2Event.sortingDate = dhis2Event.dueDate;

                            if(dhis2Event.eventDate){
                                dhis2Event.eventDate = DateUtils.formatFromApiToUser(dhis2Event.eventDate);
                                dhis2Event.sortingDate = dhis2Event.eventDate;
                            }                       

                            dhis2Event.statusColor = EventUtils.getEventStatusColor(dhis2Event);
                            
                            if($scope.currentStage && $scope.currentStage.id === dhis2Event.programStage){
                                $scope.currentEvent = dhis2Event;                                
                                $scope.showDataEntry($scope.currentEvent, true);
                            }
                        }
                        
                        $scope.dhis2Events.push(dhis2Event);
                    }
                });
            }
            
            $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-sortingDate');            
            $scope.dummyEvents = $scope.checkForEventCreation($scope.dhis2Events, $scope.selectedProgram);
        });          
    };
    
    $scope.checkForEventCreation = function(availableEvents, program){
        
        var dummyEvents = [];        
        if($scope.selectedEnrollment.status === 'ACTIVE'){
            if(!angular.isObject(availableEvents)){
                angular.forEach($scope.selectedProgram.programStages, function(ps){                                                        
                    var programStage = $scope.selectedProgramWithStage[ps.id];
                    var dummyEvent = EventUtils.createDummyEvent(availableEvents, programStage, $scope.selectedOrgUnit, $scope.selectedEnrollment);
                    dummyEvents.push(dummyEvent);                         
                });

                dummyEvents = orderByFilter(dummyEvents, '-eventDate');

                if(dummyEvents){
                    $scope.allowEventCreation = true;
                }

                return dummyEvents;
            }      
            else{
                var eventsPerStage = [];
                angular.forEach(availableEvents, function(event){
                    if(eventsPerStage[event.programStage]){
                        eventsPerStage[event.programStage].push(event);
                    }
                    else{
                        eventsPerStage[event.programStage] = [];
                        eventsPerStage[event.programStage].push(event);
                    }                    
                });

                angular.forEach(program.programStages, function(ps){
                    var stage = $scope.selectedProgramWithStage[ps.id];
                    if(!eventsPerStage[stage.id]){
                        $scope.allowEventCreation = true;
                        var dummyEvent = EventUtils.createDummyEvent(availableEvents, stage, $scope.selectedOrgUnit, $scope.selectedEnrollment);
                        dummyEvents.push(dummyEvent);
                    }
                    else{
                        if(stage.repeatable){
                            var stageNeedsEvent = true;
                            for(var j=0; j<eventsPerStage[stage.id].length && stageNeedsEvent; j++){
                                if(!eventsPerStage[stage.id][j].eventDate){
                                    stageNeedsEvent = false;
                                }
                            }
                            
                            if(stageNeedsEvent){
                                $scope.allowEventCreation = true;
                                var dummyEvent = EventUtils.createDummyEvent(availableEvents, stage, $scope.selectedOrgUnit, $scope.selectedEnrollment);                                
                                dummyEvents.push(dummyEvent);
                            }
                        }
                    }                    
                });
            }
        }        
        return dummyEvents;
    };
    
    $scope.showEventCreation = function(){
        $scope.showEventCreationDiv = !$scope.showEventCreationDiv;
    };
    
    $scope.enableRescheduling = function(){
        $scope.schedulingEnabled = !$scope.schedulingEnabled;
    };
    
    $scope.showDummyEventCreation = function(dummyEvent){
        
        if(dummyEvent){    
            
            if($scope.currentDummyEvent == dummyEvent){ 
                //clicked on the same stage, do toggling
                $scope.currentDummyEvent = null;
                $scope.showDummyEventDiv = !$scope.showDummyEventDiv;                
            }
            else{
                $scope.currentDummyEvent = dummyEvent;
                $scope.showDummyEventDiv = !$scope.showDummyEventDiv;
            }   
        }
    };   
    
    $scope.createEvent = function(){
        //check for form validity
        $scope.eventCreationForm.submitted = true;        
        if( $scope.eventCreationForm.$invalid ){
            return false;
        } 
        
        //form is valid, proceed to event creation
        var newEvent = 
                {
                    trackedEntityInstance: $scope.selectedEntity.trackedEntityInstance,
                    program: $scope.selectedProgram.id,
                    programStage: $scope.currentDummyEvent.programStage,
                    orgUnit: $scope.currentDummyEvent.orgUnit,
                    eventDate: DateUtils.formatFromUserToApi( $scope.currentDummyEvent.eventDate),
                    dueDate: DateUtils.formatFromUserToApi( $scope.currentDummyEvent.dueDate),
                    status: 'SCHEDULE',
                    notes: [],
                    dataValues: []
                };
                
        DHIS2EventFactory.create(newEvent).then(function(data) {
            if (data.importSummaries[0].status === 'ERROR') {
                var dialogOptions = {
                    headerText: 'event_creation_error',
                    bodyText: data.importSummaries[0].description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {
                newEvent.event = data.importSummaries[0].reference;
                newEvent.orgUnitName = $scope.currentDummyEvent.orgUnitName;
                newEvent.name = $scope.currentDummyEvent.name;
                newEvent.reportDateDescription = $scope.currentDummyEvent.reportDateDescription;
                newEvent.sortingDate = $scope.currentDummyEvent.dueDate,
                newEvent.statusColor = $scope.currentDummyEvent.statusColor;
                newEvent.eventDate = $scope.currentDummyEvent.eventDate;
                newEvent.dueDate =  $scope.currentDummyEvent.dueDate;
                newEvent.enrollmentStatus = $scope.currentDummyEvent.enrollmentStatus;
                
                if($scope.currentDummyEvent.coordinate){
                    newEvent.coordinate = {};
                }                
                
                $scope.dummyEvents = $scope.checkForEventCreation($scope.dhis2Events, $scope.selectedProgram);
                
                if(!angular.isObject($scope.dhis2Events)){
                    $scope.dhis2Events = [newEvent];
                }
                else{
                    $scope.dhis2Events.splice(0,0,newEvent);
                }
                
                $scope.showDataEntry(newEvent, false);
            }
        });
    };   
    
    $scope.showDataEntry = function(event, rightAfterEnrollment){  
        
        if(event){

            if($scope.currentEvent && !rightAfterEnrollment && $scope.currentEvent.event === event.event){
                //clicked on the same stage, do toggling
                $scope.currentEvent = null;
                $scope.currentElement = {id: '', saved: false};
                $scope.showDataEntryDiv = !$scope.showDataEntryDiv;      
            }
            else{
                $scope.currentElement = {};
                $scope.currentEvent = event;
                $scope.showDataEntryDiv = true;   
                $scope.showDummyEventDiv = false;
                $scope.showEventCreationDiv = false;

                if($scope.currentEvent.notes){
                    angular.forEach($scope.currentEvent.notes, function(note){
                        note.storedDate = DateUtils.formatToHrsMins(note.storedDate);
                    });

                    if($scope.currentEvent.notes.length > 0 ){
                        $scope.currentEvent.notes = orderByFilter($scope.currentEvent.notes, '-storedDate');
                    }
                }
                
                $scope.getDataEntryForm();
            }               
        }
    }; 
    
    $scope.switchDataEntryForm = function(){
        $scope.displayCustomForm = !$scope.displayCustomForm;
    };
    
    $scope.getDataEntryForm = function(){ 
        
        $scope.currentEvent.providedElsewhere = [];
        
        $scope.currentStage = $scope.selectedProgramWithStage[$scope.currentEvent.programStage];
        
        angular.forEach($scope.currentStage.programStageSections, function(section){
            section.open = true;
        });
        
        $scope.prStDes = [];                  
        angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){
            $scope.prStDes[prStDe.dataElement.id] = prStDe; 
        }); 

        $scope.customForm = CustomFormService.getForProgramStage($scope.currentStage);
        $scope.displayCustomForm = $scope.customForm ? true:false;

        $scope.allowProvidedElsewhereExists = false;
        angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){
            $scope.currentStage.programStageDataElements[prStDe.dataElement.id] = prStDe.dataElement;
            if(prStDe.allowProvidedElsewhere){
                $scope.allowProvidedElsewhereExists = true;                
            }
        });        
        
        if($scope.currentStage.captureCoordinates){
            $scope.currentEvent.coordinate = {latitude: $scope.currentEvent.coordinate.latitude ? $scope.currentEvent.coordinate.latitude : '',
                                     longitude: $scope.currentEvent.coordinate.longitude ? $scope.currentEvent.coordinate.longitude : ''};
        }
        
        angular.forEach($scope.currentEvent.dataValues, function(dataValue){
            var val = dataValue.value;
            var de = $scope.currentStage.programStageDataElements[dataValue.dataElement];
            if(de){                
                if(val && de.type === 'string' && de.optionSet && $scope.optionSets[de.optionSet.id].options  ){
                    val = OptionSetService.getName($scope.optionSets[de.optionSet.id].options, val);
                }
                if(val && de.type === 'date'){
                    val = DateUtils.formatFromApiToUser(val);
                }
                if(de.type === 'trueOnly'){
                    if(val === 'true'){
                        val = true;
                    }
                    else{
                        val = '';
                    }
                }
            }    
            $scope.currentEvent[dataValue.dataElement] = val;
            if(dataValue.providedElsewhere){
                $scope.currentEvent.providedElsewhere[dataValue.dataElement] = dataValue.providedElsewhere;
            }
        });

        $scope.currentEventOriginal = angular.copy($scope.currentEvent);        
    };
    
    $scope.saveDatavalue = function(prStDe){

        //check for input validity
        $scope.dataEntryOuterForm.submitted = true;        
        if( $scope.dataEntryOuterForm.$invalid ){            
            return false;
        }

        //input is valid        
        var value = $scope.currentEvent[prStDe.dataElement.id];
        
        if(!angular.isUndefined(value)){
            if(prStDe.dataElement.type === 'date'){                    
                value = DateUtils.formatFromUserToApi(value);
            }
            if(prStDe.dataElement.type === 'string'){                    
                if(prStDe.dataElement.optionSet && $scope.optionSets[prStDe.dataElement.optionSet.id] &&  $scope.optionSets[prStDe.dataElement.optionSet.id].options ) {
                    value = OptionSetService.getCode($scope.optionSets[prStDe.dataElement.optionSet.id].options, value);
                }                    
            }

            if($scope.currentEventOriginal[prStDe.dataElement.id] !== value){

                $scope.updateSuccess = false;
        
                $scope.currentElement = {id: prStDe.dataElement.id, saved: false};
        
                var ev = {  event: $scope.currentEvent.event,
                            orgUnit: $scope.currentEvent.orgUnit,
                            program: $scope.currentEvent.program,
                            programStage: $scope.currentEvent.programStage,
                            status: $scope.currentEvent.status,
                            trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                            dataValues: [
                                            {
                                                dataElement: prStDe.dataElement.id, 
                                                value: value, 
                                                providedElsewhere: $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] ? true : false
                                            }
                                        ]
                         };
                DHIS2EventFactory.updateForSingleValue(ev).then(function(response){
                    $scope.currentElement.saved = true;
                    $scope.currentEventOriginal = angular.copy($scope.currentEvent);
                });
            }
        }
    };
    
    $scope.saveDatavalueLocation = function(prStDe){
                
        $scope.updateSuccess = false;
        
        if(!angular.isUndefined($scope.currentEvent.providedElsewhere[prStDe.dataElement.id])){

            //currentEvent.providedElsewhere[prStDe.dataElement.id];
            var value = $scope.currentEvent[prStDe.dataElement.id];
            var ev = {  event: $scope.currentEvent.event,
                        orgUnit: $scope.currentEvent.orgUnit,
                        program: $scope.currentEvent.program,
                        programStage: $scope.currentEvent.programStage,
                        status: $scope.currentEvent.status,
                        trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                        dataValues: [
                                        {
                                            dataElement: prStDe.dataElement.id, 
                                            value: value, 
                                            providedElsewhere: $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] ? true : false
                                        }
                                    ]
                     };
            DHIS2EventFactory.updateForSingleValue(ev).then(function(response){
                $scope.updateSuccess = true;
            });            
        }        
    };
    
    $scope.saveEventDate = function(){
        
        $scope.eventDateSaved = false;
        if($scope.currentEvent.eventDate === ''){            
            $scope.invalidDate = true;
            return false;
        }
        
        var rawDate = angular.copy($scope.currentEvent.eventDate);
        var convertedDate = DateUtils.format($scope.currentEvent.eventDate);
        
        if(rawDate !== convertedDate){
            $scope.invalidDate = true;
            return false;
        }
        
        var e = {event: $scope.currentEvent.event,
             enrollment: $scope.currentEvent.enrollment,
             dueDate: DateUtils.formatFromUserToApi($scope.currentEvent.dueDate),
             status: $scope.currentEvent.status === 'SCHEDULE' ? 'ACTIVE' : $scope.currentEvent.status,
             program: $scope.currentEvent.program,
             programStage: $scope.currentEvent.programStage,
             orgUnit: $scope.currentEvent.orgUnit,
             eventDate: DateUtils.formatFromUserToApi($scope.currentEvent.eventDate),
             trackedEntityInstance: $scope.currentEvent.trackedEntityInstance
            };

        DHIS2EventFactory.updateForEventDate(e).then(function(data){
            $scope.currentEvent.sortingDate = $scope.currentEvent.eventDate;
            $scope.invalidDate = false;
            $scope.eventDateSaved = true;
            $scope.currentEvent.statusColor = EventUtils.getEventStatusColor($scope.currentEvent);
        });
    };
    
    $scope.saveDueDate = function(){
        $scope.dueDateSaved = false;

        if($scope.currentEvent.dueDate === ''){
            $scope.invalidDate = true;
            return false;
        }
        
        var rawDate = angular.copy($scope.currentEvent.dueDate);
        var convertedDate = DateUtils.format($scope.currentEvent.dueDate);           

        if(rawDate !== convertedDate){
            $scope.invalidDate = true;
            return false;
        } 
        
        var e = {event: $scope.currentEvent.event,
             enrollment: $scope.currentEvent.enrollment,
             dueDate: DateUtils.formatFromUserToApi($scope.currentEvent.dueDate),
             status: $scope.currentEvent.status,
             program: $scope.currentEvent.program,
             programStage: $scope.currentEvent.programStage,
             orgUnit: $scope.currentEvent.orgUnit,
             trackedEntityInstance: $scope.currentEvent.trackedEntityInstance
            };
        
        DHIS2EventFactory.update(e).then(function(data){            
            $scope.invalidDate = false;
            $scope.dueDateSaved = true;
            $scope.currentEvent.sortingDate = $scope.currentEvent.dueDate;
            $scope.currentEvent.statusColor = EventUtils.getEventStatusColor($scope.currentEvent);            
            $scope.schedulingEnabled = !$scope.schedulingEnabled;
        });
                      
    };
    
    $scope.saveCoordinate = function(type){
        
        if(type === 'LAT'){
            $scope.latitudeSaved = false;
        }
        else{
            $scope.longitudeSaved = false;
        }
        
        if( type === 'LAT' && $scope.outerForm.latitude.$invalid  || 
            type === 'LNG' && $scope.outerForm.longitude.$invalid ){//invalid coordinate            
            return;            
        }
        
        if( type === 'LAT' && $scope.currentEvent.coordinate.latitude === $scope.currentEventOriginal.coordinate.latitude  || 
            type === 'LNG' && $scope.currentEvent.coordinate.longitude === $scope.currentEventOriginal.coordinate.longitude){//no change            
            return;            
        }
        
        //valid coordinate(s), proceed with the saving
        var dhis2Event = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage, $scope.optionSets);
        
        DHIS2EventFactory.update(dhis2Event).then(function(response){            
            $scope.currentEventOriginal = angular.copy($scope.currentEvent);
            if(type === 'LAT'){
                $scope.latitudeSaved = true;
            }
            else{
                $scope.longitudeSaved = true;
            }
        });
    };
    
    $scope.addNote = function(){
        if(!angular.isUndefined($scope.note) && $scope.note !== ""){
            var newNote = {value: $scope.note};

            if(angular.isUndefined( $scope.currentEvent.notes) ){
                $scope.currentEvent.notes = [{value: $scope.note, storedDate: today, storedBy: storedBy}];
            }
            else{
                $scope.currentEvent.notes.splice(0,0,{value: $scope.note, storedDate: today, storedBy: storedBy});
            }

            var e = {event: $scope.currentEvent.event,
                     program: $scope.currentEvent.program,
                     programStage: $scope.currentEvent.programStage,
                     orgUnit: $scope.currentEvent.orgUnit,
                     trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                     notes: [newNote]
                    };

            DHIS2EventFactory.updateForNote(e).then(function(data){
                $scope.note = ''; 
            });
        }        
    };    
    
    $scope.clearNote = function(){
         $scope.note = '';           
    };
    
    $scope.getInputNotifcationClass = function(id, custom){
        if($scope.currentElement.id){
            if($scope.currentElement.saved && ($scope.currentElement.id === id)){
                if(custom){
                    return 'input-success';
                }
                return 'form-control input-success';
            }            
            if(!$scope.currentElement.saved && ($scope.currentElement.id === id)){
                if(custom){
                    return 'input-error';
                }
                return 'form-control input-error';
            }            
        }  
        if(custom){
            return '';
        }
        return 'form-control';
    };  
    
    $scope.closeEventCreation = function(){
        $scope.currentDummyEvent = null;
        $scope.showDummyEventDiv = !$scope.showDummyEventDiv;
    };
    
    $scope.completeIncompleteEvent = function(){
        var modalOptions;
        var dhis2Event = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage, $scope.optionSets);        
        if($scope.currentEvent.status === 'COMPLETED'){//activiate event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'incomplete',
                headerText: 'incomplete',
                bodyText: 'are_you_sure_to_incomplete_event'
            };
            dhis2Event.status = 'ACTIVE';        
        }
        else{//complete event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'complete',
                headerText: 'complete',
                bodyText: 'are_you_sure_to_complete_event'
            };
            dhis2Event.status = 'COMPLETED';
        }        

        ModalService.showModal({}, modalOptions).then(function(result){
            
            DHIS2EventFactory.update(dhis2Event).then(function(data){
                
                if($scope.currentEvent.status === 'COMPLETED'){//activiate event                    
                    $scope.currentEvent.status = 'ACTIVE'; 
                }
                else{//complete event                    
                    $scope.currentEvent.status = 'COMPLETED';
                }
                var statusColor = EventUtils.getEventStatusColor($scope.currentEvent);  
                var continueLoop = true;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === $scope.currentEvent.event ){
                        $scope.dhis2Events[i].statusColor = statusColor;
                        continueLoop = false;
                    }
                }           
            });
        });
    };
    
    $scope.skipUnskipEvent = function(){
        var modalOptions;
        var dhis2Event = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage, $scope.optionSets);   

        if($scope.currentEvent.status === 'SKIPPED'){//unskip event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'unskip',
                headerText: 'unskip',
                bodyText: 'are_you_sure_to_unskip_event'
            };
            dhis2Event.status = 'ACTIVE';        
        }
        else{//skip event
            modalOptions = {
                closeButtonText: 'cancel',
                actionButtonText: 'skip',
                headerText: 'skip',
                bodyText: 'are_you_sure_to_skip_event'
            };
            dhis2Event.status = 'SKIPPED';
        }        

        ModalService.showModal({}, modalOptions).then(function(result){
            
            DHIS2EventFactory.update(dhis2Event).then(function(data){
                
                if($scope.currentEvent.status === 'SKIPPED'){//activiate event                    
                    $scope.currentEvent.status = 'SCHEDULE'; 
                }
                else{//complete event                    
                    $scope.currentEvent.status = 'SKIPPED';
                }
                var statusColor = EventUtils.getEventStatusColor($scope.currentEvent);  
                var continueLoop = true;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === $scope.currentEvent.event ){
                        $scope.dhis2Events[i].statusColor = statusColor;
                        continueLoop = false;
                    }
                }           
            });
        });
    };
    
    $scope.validateEvent = function(){};    
    
    $scope.deleteEvent = function(){
        
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'delete',
            headerText: 'delete',
            bodyText: 'are_you_sure_to_delete_event'
        };

        ModalService.showModal({}, modalOptions).then(function(result){
            
            DHIS2EventFactory.delete($scope.currentEvent).then(function(data){
                
                var continueLoop = true, index = -1;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === $scope.currentEvent.event ){
                        $scope.dhis2Events[i] = $scope.currentEvent;
                        continueLoop = false;
                        index = i;
                    }
                }
                $scope.dhis2Events.splice(index,1);                
                $scope.currentEvent = null;
                if($scope.dhis2Events.length < 1){  
                    $scope.dhis2Events = '';
                    $scope.currentDummyEvent = null;
                    $scope.dummyEvents = $scope.checkForEventCreation($scope.dhis2Events, $scope.selectedProgram);
                }
            });
        });
    };
    
    $scope.toggleLegend = function(){
        $scope.showEventColors = !$scope.showEventColors;
    };
    
    $scope.getEventStyle = function(ev, dummy){
        var style = EventUtils.getEventStatusColor(ev);
        
        if(dummy){
            if($scope.currentDummyEvent && $scope.currentDummyEvent.programStage === ev.programStage){
                style = style + ' ' + 'current-stage';
            }
        }
        else{
            if($scope.currentEvent && $scope.currentEvent.event === ev.event){
                style = style + ' ' + 'current-stage';
            }
        }        
        return style;
    };
});