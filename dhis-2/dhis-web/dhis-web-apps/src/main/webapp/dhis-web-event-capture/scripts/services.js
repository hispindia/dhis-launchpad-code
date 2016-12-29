'use strict';

/* Services */

var eventCaptureServices = angular.module('eventCaptureServices', ['ngResource'])


.factory('StorageService', function(){
    var store = new dhis2.storage.Store({
        name: 'dhis2ec',
        adapters: [dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
        objectStores: ['ecPrograms', 'programStages', 'geoJsons', 'optionSets', 'events']
    });
    return{
        currentStore: store
    };
})

.factory('OfflineStorageService', function($http, $q, $rootScope, StorageService){
    return {        
        hasLocalData: function() {
            var def = $q.defer();
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getKeys('events').done(function(events){
                    $rootScope.$apply(function(){
                        def.resolve( events.length > 0 );
                    });                    
                });
            });            
            return def.promise;
        },
        getLocalData: function(){
            var def = $q.defer();            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('events').done(function(events){
                    $rootScope.$apply(function(){
                        def.resolve({events: events});
                    });                    
                });
            });            
            return def.promise;
        },
        uploadLocalData: function(){            
            var def = $q.defer();
            this.getLocalData().then(function(localData){                
                var evs = {events: []};
                angular.forEach(localData.events, function(ev){
                    ev.event = ev.id;
                    delete ev.id;
                    evs.events.push(ev);
                });

                $http.post('../api/events', evs).then(function(evResponse){                            
                    def.resolve();
                });                      
            });
            return def.promise;
        }
    };
})

.service('DateUtils', function($filter, CalendarService){
    
    return {
        getDate: function(dateValue){
            if(!dateValue){
                return;
            }            
            var calendarSetting = CalendarService.getSetting();
            dateValue = moment(dateValue, calendarSetting.momentFormat)._d;
            return Date.parse(dateValue);
        },
        format: function(dateValue) {            
            if(!dateValue){
                return;
            }            
            var calendarSetting = CalendarService.getSetting();
            dateValue = $filter('date')(dateValue, calendarSetting.keyDateFormat);            
            return dateValue;
        },
        formatToHrsMins: function(dateValue) {
            var calendarSetting = CalendarService.getSetting();
            var dateFormat = 'YYYY-MM-DD @ hh:mm A';
            if(calendarSetting.keyDateFormat === 'dd-MM-yyyy'){
                dateFormat = 'DD-MM-YYYY @ hh:mm A';
            }            
            return moment(dateValue).format(dateFormat);
        },
        getToday: function(){  
            var calendarSetting = CalendarService.getSetting();
            var tdy = $.calendars.instance(calendarSetting.keyCalendar).newDate();            
            var today = moment(tdy._year + '-' + tdy._month + '-' + tdy._day, 'YYYY-MM-DD')._d;            
            today = Date.parse(today);     
            today = $filter('date')(today,  calendarSetting.keyDateFormat);
            return today;
        },
        formatFromUserToApi: function(dateValue){            
            if(!dateValue){
                return;
            }
            var calendarSetting = CalendarService.getSetting();            
            dateValue = moment(dateValue, calendarSetting.momentFormat)._d;
            dateValue = Date.parse(dateValue);     
            dateValue = $filter('date')(dateValue, 'yyyy-MM-dd'); 
            return dateValue;            
        },
        formatFromApiToUser: function(dateValue){            
            if(!dateValue){
                return;
            }            
            var calendarSetting = CalendarService.getSetting();
            dateValue = moment(dateValue, 'YYYY-MM-DD')._d;
            dateValue = Date.parse(dateValue);     
            dateValue = $filter('date')(dateValue, calendarSetting.keyDateFormat); 
            return dateValue;
        }
    };
})

/* factory for loading logged in user profiles from DHIS2 */
.factory('CurrentUserProfile', function($http) { 
           
    var profile, promise;
    return {
        get: function() {
            if( !promise ){
                promise = $http.get('../api/me/profile').then(function(response){
                   profile = response.data;
                   return profile;
                });
            }
            return promise;         
        }
    };  
})


/* factory for fetching selected orgunit's coordinate */
.factory('OrgUnitService', function($http) { 
           
    var orgUnitId, promise;
    return {
        get: function(id) {
            if( !promise && id !== orgUnitId){
                promise = $http.get('../api/me/profile').then(function(response){
                   orgUnitId = id; 
                   return response.data;;
                });
            }
            return promise;         
        }
    };  
})


/* Factory to fetch geojsons */
.factory('GeoJsonFactory', function($q, $rootScope, StorageService) { 
    return {
        getAll: function(){

            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('geoJsons').done(function(geoJsons){
                    $rootScope.$apply(function(){
                        def.resolve(geoJsons);
                    });                    
                });
            });
            
            return def.promise;            
        },
        get: function(level){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('geoJsons', level).done(function(geoJson){                    
                    $rootScope.$apply(function(){
                        def.resolve(geoJson);
                    });
                });
            });                        
            return def.promise;            
        }
    };
})

/* Factory to fetch optioSets */
.factory('OptionSetService', function($q, $rootScope, StorageService) { 
    return {
        getAll: function(){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('optionSets').done(function(optionSets){
                    $rootScope.$apply(function(){
                        def.resolve(optionSets);
                    });                    
                });
            });            
            
            return def.promise;            
        },
        get: function(uid){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('optionSets', uid).done(function(optionSet){                    
                    $rootScope.$apply(function(){
                        def.resolve(optionSet);
                    });
                });
            });                        
            return def.promise;            
        },
        getNameOrCode: function(options, key){
            var val = key;            

            if(options){
                for(var i=0; i<options.length; i++){
                    if( key === options[i].name){
                        val = options[i].code;
                        break;
                    }
                    if( key === options[i].code){
                        val = options[i].name;
                        break;
                    }
                }
            }            
            return val;
        }
    };
})

/* Factory to fetch programs */
.factory('ProgramFactory', function($q, $rootScope, StorageService) {  
        
    return {
        
        getAll: function(){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.getAll('ecPrograms').done(function(programs){                    
                    $rootScope.$apply(function(){
                        def.resolve(programs);
                    });                    
                });
            });
            return def.promise;
        }        
    };
})

/* Factory to fetch programStages */
.factory('ProgramStageFactory', function($q, $rootScope, StorageService) {  
    
    return {        
        get: function(uid){
            
            var def = $q.defer();
            
            StorageService.currentStore.open().done(function(){
                StorageService.currentStore.get('programStages', uid).done(function(pst){                    
                    $rootScope.$apply(function(){
                        def.resolve(pst);
                    });
                });
            });                        
            return def.promise;            
        }        
    };        
})

/* factory for handling events */
.factory('DHIS2EventFactory', function($http, $q, StorageService, $rootScope) {   
    
    return {
        getByStage: function(orgUnit, programStage, pager){
        	var pgSize = pager ? pager.pageSize : 50;
        	var pg = pager ? pager.page : 1;
            var url = '../api/events.json?' + 'orgUnit=' + orgUnit + '&programStage=' + programStage + '&pageSize=' + pgSize + '&page=' + pg;
            
            var promise = $http.get( url ).then(function(response){                    
                return response.data;        
            }, function(){     
                //return dhis2.ec.storageManager.getEvents(orgUnit, programStage);                

                var def = $q.defer();
                StorageService.currentStore.open().done(function(){
                    StorageService.currentStore.getAll('events').done(function(evs){
                        var result = {events: [], pager: {pageSize: '', page: 1, toolBarDisplay: 5, pageCount: 1}};
                        angular.forEach(evs, function(ev){                            
                            if(ev.programStage === programStage && ev.orgUnit === orgUnit){
                                ev.event = ev.id;
                                result.events.push(ev);
                            }
                        }); 
                        $rootScope.$apply(function(){
                            def.resolve( result );
                        });                    
                    });
                });            
                return def.promise;
            });            
            
            return promise;
        },        
        get: function(eventUid){            
            var promise = $http.get('../api/events/' + eventUid + '.json').then(function(response){               
                return response.data;                
            }, function(){
                //return dhis2.ec.storageManager.getEvent(eventUid);
                var p = dhis2.ec.store.get('events', eventUid).then(function(ev){
                    ev.event = eventUid;
                    return ev;
                });
                return p;
            });            
            return promise;
        },        
        create: function(dhis2Event){
            var promise = $http.post('../api/events.json', dhis2Event).then(function(response){
                return response.data;
            }, function(){            
                dhis2Event.id = dhis2.util.uid();  
                dhis2Event.event = dhis2Event.id;
                dhis2.ec.store.set( 'events', dhis2Event );                
                return {importSummaries: [{status: 'SUCCESS', reference: dhis2Event.id}]};
            });
            return promise;            
        },        
        delete: function(dhis2Event){
            var promise = $http.delete('../api/events/' + dhis2Event.event).then(function(response){
                return response.data;
            }, function(){
                dhis2.ec.store.remove( 'events', dhis2Event.event );
            });
            return promise;           
        },    
        update: function(dhis2Event){
            var promise = $http.put('../api/events/' + dhis2Event.event, dhis2Event).then(function(response){              
                return response.data;
            }, function(){
                dhis2.ec.store.remove('events', dhis2Event.event);
                dhis2Event.id = dhis2Event.event;
                dhis2.ec.store.set('events', dhis2Event);
            });
            return promise;
        },        
        updateForSingleValue: function(singleValue, fullValue){        
            var promise = $http.put('../api/events/' + singleValue.event + '/' + singleValue.dataValues[0].dataElement, singleValue ).then(function(response){
                 return response.data;
            }, function(){
                dhis2.ec.store.remove('events', fullValue.event);
                fullValue.id = fullValue.event;
                dhis2.ec.store.set('events', fullValue);
            });
            return promise;
        }
    };    
})

/* service for dealing with events */
.service('DHIS2EventService', function(){
    return {     
        //for simplicity of grid display, events were changed from
        //event.datavalues = [{dataElement: dataElement, value: value}] to
        //event[dataElement] = value
        //now they are changed back for the purpose of storage.   
        reconstructEvent: function(event, programStageDataElements){
            var e = {};
        
            e.event         = event.event;
            e.status        = event.status;
            e.program       = event.program;
            e.programStage  = event.programStage;
            e.orgUnit       = event.orgUnit;
            e.eventDate     = event.eventDate;

            var dvs = [];
            angular.forEach(programStageDataElements, function(prStDe){
                if(event.hasOwnProperty(prStDe.dataElement.id)){
                    dvs.push({dataElement: prStDe.dataElement.id, value: event[prStDe.dataElement.id]});
                }
            });

            e.dataValues = dvs;
            
            if(event.coordinate){
                e.coordinate = {latitude: event.coordinate.latitude ? event.coordinate.latitude : '',
                                     longitude: event.coordinate.longitude ? event.coordinate.longitude : ''};
            }

            return e;
        }        
    };
})

/* service for dealing with custom form */
.service('CustomFormService', function(){
    
    return {
        getForProgramStage: function(programStage){
            
            var htmlCode = programStage.dataEntryForm ? programStage.dataEntryForm.htmlCode : null;  
            
            if(htmlCode){                
            
                var programStageDataElements = [];

                angular.forEach(programStage.programStageDataElements, function(prStDe){
                    programStageDataElements[prStDe.dataElement.id] = prStDe;
                });

                var inputRegex = /<input.*?\/>/g,
                    match,
                    inputFields = [];                

                while (match = inputRegex.exec(htmlCode)) {                
                    inputFields.push(match[0]);
                }
                
                for(var i=0; i<inputFields.length; i++){                    
                    var inputField = inputFields[i];                    
                    var inputElement = $.parseHTML( inputField );
                    var attributes = {};
                                       
                    $(inputElement[0].attributes).each(function() {
                        attributes[this.nodeName] = this.value;                       
                    });
                    
                    var deId = '', newInputField;     
                    if(attributes.hasOwnProperty('id')){
                        deId = attributes['id'].substring(4, attributes['id'].length-1).split("-")[1]; 
                        
                        //name needs to be unique so that it can be used for validation in angularjs
                        if(attributes.hasOwnProperty('name')){
                            attributes['name'] = deId;
                        }
                        
                        var maxDate = programStageDataElements[deId].allowFutureDate ? '' : 0;
                        
                        //check data element type and generate corresponding angular input field
                        if(programStageDataElements[deId].dataElement.type == "int"){
                            newInputField = '<input type="number" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '"' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"> ' + 
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'int_required\'| translate}}</span>';                                     
                        }
                        if(programStageDataElements[deId].dataElement.type == "string"){
                            if(programStageDataElements[deId].dataElement.optionSet){
                                var optionSetId = programStageDataElements[deId].dataElement.optionSet.id;
                        		newInputField = '<input type="text" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '" ' +
                                            ' ng-disabled="currentEvent[uid] == \'uid\'" ' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"' +
                                            ' typeahead="option.name as option.name for option in optionSets.'+optionSetId+'.options | filter:$viewValue | limitTo:20"' +
                                            ' typeahead-editable="false" ' +
                                            ' d2-typeahead-validation ' +
                                            ' class="typeahead" ' +
                                            ' placeholder="&#xf0d7;&nbsp;&nbsp;" ' +
                                            ' typeahead-open-on-focus ng-required="prStDes.'+deId+'.compulsory"> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'option_required\'| translate}}</span>';
                        	}
                        	else{
                        		newInputField = '<input type="text" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '" ' +
                                            ' ng-disabled="currentEvent[uid] == \'uid\'" ' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'string_required\'| translate}}</span>';                                     
                        	}
                        }
                        if(programStageDataElements[deId].dataElement.type == "bool"){
                            newInputField = '<select ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '" ' +
                                            ' ng-required="prStDes.' + deId + '.compulsory">' + 
                                            '<option value="">{{\'please_select\'| translate}}</option>' +
                                            '<option value="false">{{\'no\'| translate}}</option>' + 
                                            '<option value="true">{{\'yes\'| translate}}</option>' +
                                            '</select> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'bool_required\'| translate}}</span>';                                     
                        }
                        if(programStageDataElements[deId].dataElement.type == "date"){
                            newInputField = '<input type="text" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '"' +
                                            ' d2-date ' +
                                            ' max-date="' + maxDate + '"' + '\'' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'date_required\'| translate}}</span>'; 
                        }
                        if(programStageDataElements[deId].dataElement.type == "trueOnly"){
                            newInputField = '<input type="checkbox" ' +
                                            this.getAttributesAsString(attributes) +
                                            ' ng-model="currentEvent.' + deId + '"' +
                                            ' ng-required="prStDes.' + deId + '.compulsory"> ' +
                                            '<span ng-show="outerForm.submitted && outerForm.'+ deId +'.$invalid" class="required">{{\'required\'| translate}}</span>';
                        }

                        htmlCode = htmlCode.replace(inputField, newInputField);
                    }
                }
                return htmlCode;                
            }
            return null;
        },
        getAttributesAsString: function(attributes){
            if(attributes){
                var attributesAsString = '';                
                for(var prop in attributes){
                    if(prop != 'value'){
                        attributesAsString += prop + '="' + attributes[prop] + '" ';
                    }
                }
                return attributesAsString;
            }
            return null;
        }
    };            
})

/* Modal service for user interaction */
.service('ModalService', ['$modal', function($modal) {

        var modalDefaults = {
            backdrop: true,
            keyboard: true,
            modalFade: true,
            templateUrl: 'views/modal.html'
        };

        var modalOptions = {
            closeButtonText: 'Close',
            actionButtonText: 'OK',
            headerText: 'Proceed?',
            bodyText: 'Perform this action?'
        };

        this.showModal = function(customModalDefaults, customModalOptions) {
            if (!customModalDefaults)
                customModalDefaults = {};
            customModalDefaults.backdrop = 'static';
            return this.show(customModalDefaults, customModalOptions);
        };

        this.show = function(customModalDefaults, customModalOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempModalDefaults = {};
            var tempModalOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempModalDefaults, modalDefaults, customModalDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempModalOptions, modalOptions, customModalOptions);

            if (!tempModalDefaults.controller) {
                tempModalDefaults.controller = function($scope, $modalInstance) {
                    $scope.modalOptions = tempModalOptions;
                    $scope.modalOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };
                    $scope.modalOptions.close = function(result) {
                        $modalInstance.dismiss('cancel');
                    };
                };
            }

            return $modal.open(tempModalDefaults).result;
        };

    }])

/* Dialog service for user interaction */
.service('DialogService', ['$modal', function($modal) {

        var dialogDefaults = {
            backdrop: true,
            keyboard: true,
            backdropClick: true,
            modalFade: true,            
            templateUrl: 'views/dialog.html'
        };

        var dialogOptions = {
            closeButtonText: 'close',
            actionButtonText: 'ok',
            headerText: 'dhis2_tracker',
            bodyText: 'Perform this action?'
        };

        this.showDialog = function(customDialogDefaults, customDialogOptions) {
            if (!customDialogDefaults)
                customDialogDefaults = {};
            customDialogDefaults.backdropClick = false;
            return this.show(customDialogDefaults, customDialogOptions);
        };

        this.show = function(customDialogDefaults, customDialogOptions) {
            //Create temp objects to work with since we're in a singleton service
            var tempDialogDefaults = {};
            var tempDialogOptions = {};

            //Map angular-ui modal custom defaults to modal defaults defined in service
            angular.extend(tempDialogDefaults, dialogDefaults, customDialogDefaults);

            //Map modal.html $scope custom properties to defaults defined in service
            angular.extend(tempDialogOptions, dialogOptions, customDialogOptions);

            if (!tempDialogDefaults.controller) {
                tempDialogDefaults.controller = function($scope, $modalInstance) {
                    $scope.dialogOptions = tempDialogOptions;
                    $scope.dialogOptions.ok = function(result) {
                        $modalInstance.close(result);
                    };                           
                };
            }

            return $modal.open(tempDialogDefaults).result;
        };

    }])

/* Context menu for grid*/
.service('ContextMenuSelectedItem', function(){
    this.selectedItem = '';
    
    this.setSelectedItem = function(selectedItem){  
        this.selectedItem = selectedItem;        
    };
    
    this.getSelectedItem = function(){
        return this.selectedItem;
    };
})

/* Translation service - gets logged in user profile for the server, 
 * and apply user's locale to translation
 */
.service('TranslationService', function($translate, storage){
    
    this.translate = function(){
        var profile = storage.get('USER_PROFILE');        
        if( profile && profile.settings ){        
            $translate.uses(profile.settings.keyUiLocale);
        }
    };
})

/* Pagination service */
.service('Paginator', function () {
    this.page = 1;
    this.pageSize = 50;
    this.itemCount = 0;
    this.pageCount = 0;
    this.toolBarDisplay = 5;

    this.setPage = function (page) {
        if (page > this.getPageCount()) {
            return;
        }

        this.page = page;
    };
    
    this.getPage = function(){
        return this.page;
    };
    
    this.setPageSize = function(pageSize){
      this.pageSize = pageSize;
    };
    
    this.getPageSize = function(){
        return this.pageSize;
    };
    
    this.setItemCount = function(itemCount){
      this.itemCount = itemCount;
    };
    
    this.getItemCount = function(){
        return this.itemCount;
    };
    
    this.setPageCount = function(pageCount){
        this.pageCount = pageCount;
    };

    this.getPageCount = function () {
        return this.pageCount;
    };

    this.lowerLimit = function() { 
        var pageCountLimitPerPageDiff = this.getPageCount() - this.toolBarDisplay;

        if (pageCountLimitPerPageDiff < 0) { 
            return 0; 
        }

        if (this.getPage() > pageCountLimitPerPageDiff + 1) { 
            return pageCountLimitPerPageDiff; 
        } 

        var low = this.getPage() - (Math.ceil(this.toolBarDisplay/2) - 1); 

        return Math.max(low, 0);
    };
})

/*this is just a hack - there should be better way */
.service('ValidDate', function(){    
    var dateValidation;    
    return {
        get: function(dt) {
            dateValidation = dt;
        },
        set: function() {    
            return dateValidation;
        }
    };
            
})

/* service for getting calendar setting */
.service('CalendarService', function(storage, $rootScope){    

    return {
        getSetting: function() {
            
            var dhis2CalendarFormat = {keyDateFormat: 'yyyy-MM-dd', keyCalendar: 'gregorian', momentFormat: 'YYYY-MM-DD'};                
            var storedFormat = storage.get('CALENDAR_SETTING');
            if(angular.isObject(storedFormat) && storedFormat.keyDateFormat && storedFormat.keyCalendar){
                if(storedFormat.keyCalendar === 'iso8601'){
                    storedFormat.keyCalendar = 'gregorian';
                }

                if(storedFormat.keyDateFormat === 'dd-MM-yyyy'){
                    dhis2CalendarFormat.momentFormat = 'DD-MM-YYYY';
                }
                
                dhis2CalendarFormat.keyCalendar = storedFormat.keyCalendar;
                dhis2CalendarFormat.keyDateFormat = storedFormat.keyDateFormat;
            }
            $rootScope.dhis2CalendarFormat = dhis2CalendarFormat;
            return dhis2CalendarFormat;
        }
    };            
})

.service('GridColumnService', function(){
    return {        
        columnExists: function(cols, id) {
            var colExists = false;
            if(!angular.isObject(cols) || !id || angular.isObject(cols) && !cols.length){
                return colExists;
            }
            
            for(var i=0; i<cols.length && !colExists; i++){
                if(cols[i].id === id){
                    colExists = true;
                }
            }
            return colExists;
        }
    };
});
