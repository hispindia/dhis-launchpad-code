/**
 * Created by mission on 6/15/14.
 */

var baseDataURL = 'http://fsnis.nfpcsp.org/';

var dataElementMap = new Object();

var categoryComboMap = new Object();

var glDataElementID;

var glLocations;

var glCategoryComboID;

var dataPoints;

var updateItemBox = function () {

    var itemURL = baseDataURL + "getItemList?callback=groupsets";

    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: itemURL,
        success: function (data) {

            var dataelments = []

            for (var item in data) {
                dataelments.push(data[item].name);
                dataElementMap[data[item].name]=data[item].dataelementid;
                categoryComboMap[data[item].name]= data[item].categoryoptionid;
            };


            var engine = new Bloodhound({
                datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
                queryTokenizer: Bloodhound.tokenizers.whitespace,
                local: $.map(dataelments, function (dataelments) {
                    return { value: dataelments };
                })
            });

            engine.initialize(true);

            $('#bloodhound #typeahead').typeahead('destroy');
            $('#bloodhound #typeahead').val('');

            $('#bloodhound #typeahead').typeahead({

                    hint: true,
                    highlight: true,
                    minLength: 1,
                    mode: 'multiple'
                },
                {
                    name: 'dataelments',
                    displayKey: 'value',
                    source: engine.ttAdapter()
                }
            ).on('typeahead:selected', function ($e)
                {
                    updateLocations(
                        dataElementMap[$('#bloodhound #typeahead').typeahead('val')],
                        categoryComboMap[$('#bloodhound #typeahead').typeahead('val')]
                    );
                });
        }
    });
};

var updateLocations = function (dataElementID, categoryComboID) {

    var dataLocationURL = baseDataURL + 'getDataLocations?dataElementID='+ dataElementID;

    glDataElementID = dataElementID;

    glCategoryComboID = categoryComboID;

    //-------------Load Item Groups------------------//
    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: dataLocationURL,
        success: function (data) {
            $('option', '#locationSelect').remove();
            for (var item in data) {
                $('#locationSelect').append($('<option>')
                    .text(data[item].name)
                    .attr('value', data[item].organisationunitid));
            }
            $('#locationSelect').multiselect('refresh');
            $('#locationSelect').multiselect('rebuild');
        }
    });

}


var updateDates = function () {

    var dataDatesURL = baseDataURL + 'getDateRange?dataElementID='+
        glDataElementID +'&locations='+glLocations;


    //-------------Load Item Groups------------------//
    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: dataDatesURL,
        success: function (data) {
            for (var item in data) {

                var startDateObj = moment(data[item].startdate,'YYYY-MM-DD').toDate();
                var endDateObj = moment(data[item].enddate,'YYYY-MM-DD').toDate();

                var dataStartDate = moment(startDateObj).format('DD/MM/YYYY');
                var dataEndDate = moment(endDateObj).format('DD/MM/YYYY');

                $('#dateInput1').val('');
                $('#dateInput2').val('');

                $('#datetimepicker5').datetimepicker({

                    picktime: false,
                    format: 'DD/MM/YYYY'
                });
                $('#datetimepicker6').datetimepicker({
                    pickTime: false,
                    format: 'DD/MM/YYYY'
                });
                $('#datetimepicker5').data("DateTimePicker").setMinDate(startDateObj);
                $('#datetimepicker5').data("DateTimePicker").setMaxDate(endDateObj);
                $('#datetimepicker6').data("DateTimePicker").setMinDate(startDateObj);
                $('#datetimepicker6').data("DateTimePicker").setMaxDate(endDateObj);
                $('#datetimepicker5').data("DateTimePicker").setDate(dataStartDate);
                $('#datetimepicker6').data("DateTimePicker").setDate(dataEndDate);
            }
        }
    });

}

var populateChart = function(name, unit, seriesList){

    var options = {
        chart: {
            renderTo: 'graphcontainer',
            defaultSeriesType: 'line',
            zoomType: 'xy',
            shadow: true
        },
        credits: {
            enabled: false
        },
        title: {
            text: name
        },
        enabled: true,
        y: 20,
        align: 'right',
        verticalAlign: 'top',
        margin: 30,
        width: 200,
        borderWidth: 0,
        itemMarginTop: 15,
        itemMarginBottom: 15,
        itemStyle: {
            color: '#000',
            fontFamily: 'MuseoS500'
        },
        xAxis: {
            type: 'datetime',
            labels: {
                rotation: 0,
                overflow: 'justify',
                align: 'center'
            }
        },
        yAxis: {
            title: {
                text: unit
            }
        },
        series: []
    };

    for(var seriesItem in seriesList)
    {
        //alert('T8: '+seriesList[seriesItem].data.length);

        var series = {
            data: []
        };
        series.name = seriesList[seriesItem].name;
        for(var dataItem in seriesList[seriesItem].data)
        {
            series.data.push(seriesList[seriesItem].data[dataItem]);
        }
        options.series.push(series);
    }

    var chart = new Highcharts.Chart(options);

}

var getData = function (){

    var startDate = $('#dateInput1').val();
    var endDate = $('#dateInput2').val();

    var startDateObj = moment(startDate,'DD/MM/YYYY').toDate();
    var endDateObj = moment(endDate,'DD/MM/YYYY').toDate();

    var StartDate = moment(startDateObj).format('YYYY-MM-DD');
    var EndDate = moment(endDateObj).format('YYYY-MM-DD');

    //alert("T6: "+StartDate+":"+EndDate);

    var getDataURL = baseDataURL + 'getData?' +
        'dataElementID=' + glDataElementID +
        '&locations=' + glLocations +
        '&categoryID=' + glCategoryComboID +
        '&startDate=' + StartDate +
        '&endDate=' + EndDate ;
    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: getDataURL,
        success: function (data) {

            var seriesList = new Object();

            var locationList = [];

            var dateList = [];

            var name;

            var unit;

            for (var item in data) {

                unit = data[item].unit;

                name = data[item].name;

                dataPoints = data.length;

                var dataPoint = [];

                var startDateObj = moment(data[item].startdate,'YYYY-MM-DD').toDate();

                var dataStartDate = moment(startDateObj).format('DD/MM/YYYY');

                var year = startDateObj.getFullYear();

                var month = startDateObj.getMonth();

                var date = startDateObj.getDate();

                var utcStartDate = Date.UTC(year,month,date);

                dataPoint.push(utcStartDate,parseFloat(data[item].value));

                if(($.inArray(data[item].location, locationList)) == (-1)){
                    locationList.push(data[item].location)
                }

                if(($.inArray(data[item].startdate, dateList)) == (-1)){
                    dateList.push(data[item].startdate)
                }

                if(seriesList[data[item].location] == null)
                {
                    var series = {
                        data: []
                    };
                    series.name = data[item].location;
                    series.data.push(dataPoint);
                    seriesList[data[item].location] = series;
                }
                else
                {
                    seriesList[data[item].location].data.push(dataPoint);

                }
            }

            $('#metaInfoList').empty();

            $('#metaInfoList').append('<li><a href="#graphcontainer" style="font-weight: bold">'
                + 'Item:&nbsp;' + name
                + '</a></li>'
                + '</br>'
                + '<li><a href="#" style="font-weight: bold">Unit:&nbsp;' + unit
                + '</a></li>'
                + '</br>'
                + '<li>'
                + '<a href="#graphcontainer" style="font-weight: bold">Data points:&nbsp;'
                + '<span class="badge pull-right">' + dataPoints
                + '</span>'
                + '</a>'
                + '</li>');

            $('#metaInfoTab').show();

            var index = 0;

            var tableHTML = "<thead>" + "<tr>" + "<th>" + "DATE" + "</th>";

            for (var locationItem in locationList) {
                tableHTML = tableHTML + "<th>" + locationList[locationItem] + "</th>";
            }

            tableHTML = tableHTML + "</tr>" + "</thead>";

            tableHTML = tableHTML + "<tboby>";

            for (var dateItem in dateList) {

                tableHTML = tableHTML + "<tr><th>" + data[index].startdate + "</th>";
                for (var locationItem in locationList) {
                    if (data[index].startdate == dateList[dateItem] && data[index].location == locationList[locationItem]) {

                        tableHTML = tableHTML + "<th>" + data[index].value + "</th>";
                        ++index;
                    }
                    else {
                        tableHTML = tableHTML + "<th>" + " " + "</th>";
                    }
                }

                tableHTML = tableHTML + "</tr>";
            }
            tableHTML = tableHTML + "</tbody>";

            populateChart(name, unit, seriesList);

            $('#dataTable').empty();

            $('#dataTable').append(tableHTML);

            $('.table').dataTable({
                "scrollY": 220,
                "scrollX": true,
                bDestroy: true
            });
        }
    });

}

$(document).ready(function () {

    //-----Initialize preselected filter------//

    $('#dateInput1').val('');
    $('#dateInput2').val('');

    updateItemBox();

    $('.multiselect').multiselect({
        nonSelectedText: 'Select Locations',
        numberDisplayed: 10,
        onDropdownHide: function() {
            if($('.multiselect').val()!=null){
                glLocations = $('.multiselect').val();
                updateDates();
            }
        }

    });

    $('.selectpicker').selectpicker();

    $('#viewDataButton').click(function(){
        if(glDataElementID==null || glCategoryComboID==null || glLocations==null)
        {
            alert('Insufficient Information, please make all the selections');
        }
        else
        {
            getData();
        }
    });

});

