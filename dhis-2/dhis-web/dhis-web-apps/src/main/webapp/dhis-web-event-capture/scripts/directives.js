'use strict';

/* Directives */

var eventCaptureDirectives = angular.module('eventCaptureDirectives', [])

.directive('inputValidator', function() {
    
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, ctrl) {  

            ctrl.$parsers.push(function (value) {
                return parseFloat(value || '');
            });
        }
    };   
})

.directive('selectedOrgUnit', function($timeout, storage) {        

    return {        
        restrict: 'A',        
        link: function(scope, element, attrs){
            
            //when tree has loaded, get selected orgunit - if there is any - and inform angular           
            /*$(function() {                 
                
                var adapters = [];
                var partial_adapters = [];

                if( dhis2.ou.memoryOnly ) {
                    adapters = [ dhis2.storage.InMemoryAdapter ];
                    partial_adapters = [ dhis2.storage.InMemoryAdapter ];
                } else {
                    adapters = [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomLocalStorageAdapter, dhis2.storage.InMemoryAdapter ];
                    partial_adapters = [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter ];
                }

                dhis2.ou.store = new dhis2.storage.Store({
                    name: OU_STORE_NAME,
                    objectStores: [
                        {
                            name: OU_KEY,
                            adapters: adapters
                        },
                        {
                            name: OU_PARTIAL_KEY,
                            adapters: partial_adapters
                        }
                    ]
                });

                dhis2.ou.store.open().done( function() {
                    selection.load();
                    $( "#orgUnitTree" ).one( "ouwtLoaded", function() {
                        var selected = selection.getSelected()[0];
                        selection.getOrganisationUnit(selected).done(function(data){                            
                            if( data ){
                                scope.selectedOrgUnit = {id: selected, name: data[selected].n, programs: []};
                                scope.$apply();                                                              
                            }                        
                        });
                    });
                    
                });
            });
            
            //listen to user selection, and inform angular         
            selection.setListenerFunction( organisationUnitSelected );            
            selection.responseReceived();
            
            function organisationUnitSelected( orgUnits, orgUnitNames ) {
                scope.selectedOrgUnit = {id: orgUnits[0], name: orgUnitNames[0], programs: []};    
                scope.$apply();                
            }*/
            
            //reloadtree, incase not loaded
            $(function() {                
                dhis2.ou.store.open().done( function() {
                    selection.load();
                    $( "#orgUnitTree" ).one( "ouwtLoaded", function() {
                        var selected = selection.getSelected()[0];
                        selection.getOrganisationUnit(selected).done(function(data){
                            if( data ){
                                $timeout(function() {
                                    scope.selectedOrgUnit = {id: selected, name: data[selected].n, programs: []};
                                    scope.$apply();
                                });
                            }                        
                        });
                    });                    
                });
            });
            
            //listen to user selection, and inform angular         
            selection.responseReceived();
            selection.setListenerFunction( organisationUnitSelected );
            
            function organisationUnitSelected( orgUnits, orgUnitNames ) {
                var ou = {id: orgUnits[0], name: orgUnitNames[0]};
                var selectedOld = storage.get('SELECTED_OU');
                if(!selectedOld || ou.id !== selectedOld.id){
                    $timeout(function() {
                        scope.selectedOrgUnit = ou;
                        scope.$apply();
                    });
                }
            }
        }  
    };
})

.directive('d2CustomForm', function($compile, $parse, CustomFormService) {
    return{ 
        restrict: 'E',
        link: function(scope, elm, attrs){   
            
            var customFormType = attrs.customFormType;
            var customFormObject = $parse(attrs.customFormObject)(scope);
            
            if(customFormType === 'PROGRAM_STAGE'){                
                var customForm = CustomFormService.getForProgramStage(customFormObject);                
                elm.html(customForm ? customForm : '');
                $compile(elm.contents())(scope);                
            }
        }
    };
})

.directive('d2ContextMenu', function(ContextMenuSelectedItem) {
        
    return {        
        restrict: 'A',
        link: function(scope, element, attrs){
            var contextMenu = $("#contextMenu");                   
            
            element.click(function (e) {
                var selectedItem = $.parseJSON(attrs.selectedItem);
                ContextMenuSelectedItem.setSelectedItem(selectedItem);
                
                var menuHeight = contextMenu.height();
                var menuWidth = contextMenu.width();
                var winHeight = $(window).height();
                var winWidth = $(window).width();

                var pageX = e.pageX;
                var pageY = e.pageY;

                contextMenu.show();

                if( (menuWidth + pageX) > winWidth ) {
                  pageX -= menuWidth;
                }

                if( (menuHeight + pageY) > winHeight ) {
                  pageY -= menuHeight;

                  if( pageY < 0 ) {
                      pageY = e.pageY;
                  }
                }
                
                contextMenu.css({
                    left: pageX,
                    top: pageY
                });

                return false;
            });
            
            contextMenu.on("click", "a", function () {                    
                contextMenu.hide();
            });

            $(document).click(function () {                                        
                contextMenu.hide();
            });
        }     
    };
})

.directive('d2Date', function(DateUtils, CalendarService, storage, $parse) {
    return {
        restrict: 'A',
        require: 'ngModel',        
        link: function(scope, element, attrs, ctrl) {    
            
            var calendarSetting = CalendarService.getSetting();            
            var dateFormat = 'yyyy-mm-dd';
            if(calendarSetting.keyDateFormat === 'dd-MM-yyyy'){
                dateFormat = 'dd-mm-yyyy';
            }            
            
            var minDate = $parse(attrs.minDate)(scope), 
                maxDate = $parse(attrs.maxDate)(scope),
                calendar = $.calendars.instance(calendarSetting.keyCalendar);
            
            element.calendarsPicker({
                changeMonth: true,
                dateFormat: dateFormat,
                yearRange: '-120:+30',
                minDate: minDate,
                maxDate: maxDate,
                calendar: calendar,
                duration: "fast",
                showAnim: "",
                renderer: $.calendars.picker.themeRollerRenderer,
                onSelect: function(date) {
                    //scope.date = date;
                    ctrl.$setViewValue(date);
                    $(this).change();                    
                    scope.$apply();
                }
            })
            .change(function() {                
                var rawDate = this.value;
                var convertedDate = DateUtils.format(this.value);

                if(rawDate != convertedDate){
                    scope.invalidDate = true;
                    ctrl.$setViewValue(this.value);                                   
                    ctrl.$setValidity('foo', false);                    
                    scope.$apply();     
                }
                else{
                    scope.invalidDate = false;
                    ctrl.$setViewValue(this.value);                                   
                    ctrl.$setValidity('foo', true);                    
                    scope.$apply();     
                }
            });    
        }      
    };   
})

.directive('blurOrChange', function() {
    
    return function( scope, elem, attrs) {
        elem.calendarsPicker({
            onSelect: function() {
                scope.$apply(attrs.blurOrChange);
                $(this).change();                                        
            }
        }).change(function() {
            scope.$apply(attrs.blurOrChange);
        });
    };
})

.directive('typeaheadOpenOnFocus', function () {
  return {
        require: ['typeahead', 'ngModel'],
        link: function (scope, element, attr, ctrls) {        
            element.bind('focus', function () {
                ctrls[0].getMatchesAsync(ctrls[1].$viewValue);
                
                scope.$watch(attr.ngModel, function(value) {
                    if(value === '' || angular.isUndefined(value)){
                        ctrls[0].getMatchesAsync(ctrls[1].$viewValue);
                    }                
                });
            });
        }
    };
})

.directive('d2PopOver', function($compile, $templateCache){
    return {        
        restrict: 'EA',
        link: function(scope, element, attrs){
            var content = $templateCache.get("note.html");
            content = $compile(content)(scope);
            var options = {
                    content: content,
                    placement: 'right',
                    trigger: 'hover',
                    html: true,
                    title: scope.title               
                };            
            $(element).popover(options);
        },
        scope: {
            content: '=',
            title: '@details',
            template: "@template"
        }
    };
})

.directive('d2GoogleMap', function ($parse, $compile, storage) {
    return {
        restrict: 'E',
        replace: true,
        template: '<div></div>',
        link: function(scope, element, attrs){
            
            //remove angular bootstrap ui modal draggable
            $(".modal-content").draggable({ disabled: true });
            
            //get a default center
            var latCenter = 12.31, lngCenter = 51.48;            
            
            //if there is any marker already - use it as center
            if(angular.isObject(scope.location)){
                if(scope.location.lat && scope.location.lng){
                    latCenter = scope.location.lat;
                    lngCenter = scope.location.lng;
                }                
            }
            
            //default map configurations 
            var mapOptions = {
                zoom: 3,
                center: new google.maps.LatLng(latCenter, lngCenter),
                mapTypeId: google.maps.MapTypeId.ROADMAP
            },featureStyle = {
                strokeWeight: 2,
                strokeOpacity: 0.4,
                fillOpacity: 0.4,
                fillColor: 'green'
            };
            
            var geojsons = $parse(attrs.geojsons)(scope);
            var currentLayer = 0, currentGeojson = geojsons[0]; 
            
            var map = new google.maps.Map(document.getElementById(attrs.id), mapOptions);            
            var currentGeojsonFeatures = map.data.addGeoJson(currentGeojson);
            
            var marker = new google.maps.Marker({
                map: map
            });
            
            if(angular.isObject(scope.location)){
                if(scope.location.lat && scope.location.lng){                    
                    addMarker({lat: scope.location.lat, lng: scope.location.lng});                    
                }                
            }
            
            function addMarker(loc){
                var latLng = new google.maps.LatLng(loc.lat, loc.lng);
                marker.setPosition(latLng);
            }
            
            function centerMap(){
                
                if(currentGeojson && currentGeojson.features){
                    var latLngBounds = new google.maps.LatLngBounds();
                    angular.forEach(currentGeojson.features, function(feature){
                        if(feature.geometry.type === 'MultiPolygon'){
                            angular.forEach(feature.geometry.coordinates[0][0], function(coordinate){
                                latLngBounds.extend(new google.maps.LatLng(coordinate[1],coordinate[0]));
                            });
                        }
                        else if(feature.geometry.type === 'Point'){                        
                            latLngBounds.extend(new google.maps.LatLng(feature.geometry.coordinates[1],feature.geometry.coordinates[0]));
                        }
                    });
                    
                    map.fitBounds(latLngBounds);
                    map.panToBounds(latLngBounds);
                }                
            }
            
            function initializeMap(){                
                google.maps.event.addListenerOnce(map, 'idle', function(){
                    google.maps.event.trigger(map, 'resize');
                    map.data.setStyle(featureStyle);
                    centerMap();
                });
            }
            
            map.data.addListener('mouseover', function(e) {                
                $("#polygon-label").text( e.feature.k.name );
                map.data.revertStyle();
                map.data.overrideStyle(e.feature, {fillOpacity: 0.8});
            });
            
            map.data.addListener('mouseout', function() {                
                $("#polygon-label").text( '' );
                map.data.revertStyle();
            });
            
            //drill-down based on polygons assigned to orgunits
            map.data.addListener('rightclick', function(e){                
                for (var i = 0; i < currentGeojsonFeatures.length; i++){
                    map.data.remove(currentGeojsonFeatures[i]);
                }
                                
                if(currentLayer >= geojsons.length-1){
                    currentLayer = 0;
                    currentGeojson = angular.copy(geojsons[currentLayer]);                    
                }
                else{
                    currentLayer++;
                    currentGeojson = angular.copy(geojsons[currentLayer]);
                    currentGeojson.features = [];
                    var selectedFeatures = [];
                    angular.forEach(geojsons[currentLayer].features, function(feature){                    
                        if(feature.properties.parent === e.feature.B){
                            selectedFeatures.push(feature);
                        }
                    });
                    
                    if(selectedFeatures.length){
                        currentGeojson.features = selectedFeatures;
                    }                   
                }                
                currentGeojsonFeatures = map.data.addGeoJson(currentGeojson);
                centerMap();         
            });            
            
            //capturing coordinate from defined polygons
            map.data.addListener('click', function(e) {                
                scope.$apply(function(){
                    addMarker({
                       lat: e.latLng.lat(),
                       lng: e.latLng.lng()
                    });
                    $parse(attrs.location).assign(scope.$parent, {lat: e.latLng.lat(), lng: e.latLng.lng()});                    
                });                
            });
            
            //capturing coordinate from anywhere in the map - incase no polygons are defined
            google.maps.event.addListener(map, 'click', function(e){                
                scope.$apply(function(){
                    addMarker({
                       lat: e.latLng.lat(),
                       lng: e.latLng.lng()
                    });
                    $parse(attrs.location).assign(scope.$parent, {lat: e.latLng.lat(), lng: e.latLng.lng()});                    
                });                
            });
            
            initializeMap();
        }
    };
})

.directive('serversidePaginator', function factory() {
    return {
        restrict: 'E',
        controller: function ($scope, Paginator) {
            $scope.paginator = Paginator;
        },
        templateUrl: 'views/serverside-pagination.html'
    };
})

.directive('clientsidePaginator', function factory() {
    return {
        restrict: 'E',
        controller: function ($scope, Paginator) {
            $scope.paginator = Paginator;
        },
        templateUrl: 'views/clientside-pagination.html'
    };
});

