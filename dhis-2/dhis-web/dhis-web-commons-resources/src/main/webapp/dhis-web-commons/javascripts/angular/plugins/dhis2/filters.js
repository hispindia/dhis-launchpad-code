'use strict';

/* Filters */

var d2Filters = angular.module('d2Filters', [])

.filter('gridFilter', function($filter, CalendarService){    
    
    return function(data, filters, filterTypes){

        if(!data ){
            return;
        }
        
        if(!filters){
            return data;
        }        
        else{            
            
            var dateFilter = {}, 
                textFilter = {}, 
                numberFilter = {},
                filteredData = data;
            
            for(var key in filters){
                
                if(filterTypes[key] === 'date'){
                    if(filters[key].start || filters[key].end){
                        dateFilter[key] = filters[key];
                    }
                }
                else if(filterTypes[key] === 'int'){
                    if(filters[key].start || filters[key].end){
                        numberFilter[key] = filters[key];
                    }
                }
                else{
                    textFilter[key] = filters[key];
                }
            }
            
            filteredData = $filter('filter')(filteredData, textFilter); 
            filteredData = $filter('filter')(filteredData, dateFilter, dateComparator);            
            filteredData = $filter('filter')(filteredData, numberFilter, numberComparator);
                        
            return filteredData;
        } 
    }; 
    
    function dateComparator(data,filter){
    	var calendarSetting = CalendarService.getSetting(); 
        var start = moment(filter.start, calendarSetting.momentFormat);
        var end = moment(filter.end, calendarSetting.momentFormat);  
        var date = moment(data, calendarSetting.momentFormat); 
        
        if(filter.start && filter.end){
            return ( Date.parse(date) <= Date.parse(end) ) && (Date.parse(date) >= Date.parse(start));
        }        
        return ( Date.parse(date) <= Date.parse(end) ) || (Date.parse(date) >= Date.parse(start));
    }
    
    function numberComparator(data,filter){
        var start = filter.start;
        var end = filter.end;
        
        if(filter.start && filter.end){
            return ( data <= end ) && ( data >= start );
        }        
        return ( data <= end ) || ( data >= start );
    }
})

.filter('paginate', function(Paginator) {
    return function(input, rowsPerPage) {
        if (!input) {
            return input;
        }

        if (rowsPerPage) {
            Paginator.rowsPerPage = rowsPerPage;
        }       
        
        Paginator.itemCount = input.length;

        return input.slice(parseInt(Paginator.page * Paginator.rowsPerPage), parseInt((Paginator.page + 1) * Paginator.rowsPerPage + 1) - 1);
    };
})

.filter('forLoop', function() {
    return function(input, start, end) {
        input = new Array(end - start);
        for (var i = 0; start < end; start++, i++) {
            input[i] = start;
        }
        return input;
    };
});