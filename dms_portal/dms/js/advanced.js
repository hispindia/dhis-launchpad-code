/**
 * Created by mission on 6/15/14.
 */

var baseDataURL = 'http://fsnis.nfpcsp.org/';

var dataElementMap = new Object();

var categoryComboMap = new Object();

var glDataElementID = new String("");

var glLocations = new String("");

var glCategoryComboID = new String("");

var dataPoints = new String("");



var updateLocationFilterList = function () {
    var locationListURL = baseDataURL + "getFilterLocations?callback=groupsets&frequency="
        + $('#frequencySelect').val()
        + "&groupSetID="
        + $('#groupSelect').val()
        + "&groupID="
        + $('#subGroupSelect').val();

    //alert(locationListURL);
    $('#locationFilter').empty();

    var locationGroupList = new Object();

    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: locationListURL,
        success: function (data) {

            for (var item in data) {


                //$('#locationFilter').append($('<option>')
                //    .text(data[item].name)
                //    .attr('value', data[item].organisationunitid));


                var location_point = new Object();

                location_point.name = data[item].name;
                location_point.id = data[item].organisationunitid;

                if (locationGroupList[data[item].location_group] == null) {
                    var location_series = {
                        locations: []
                    };
                    location_series.name = data[item].location_group;
                    location_series.locations.push(location_point);
                    locationGroupList[data[item].location_group] = location_series;
                }
                else {
                    locationGroupList[data[item].location_group].locations.push(location_point);
                }

            }

            for(var locationGroup in locationGroupList) {
                $('#locationFilter').append("<optgroup label=\'"+locationGroupList[locationGroup].name+"\'>");
                for(var location in locationGroupList[locationGroup].locations)
                {
                    $('#locationFilter').append($('<option>')
                        .text(locationGroupList[locationGroup].locations[location].name)
                        .attr('value', locationGroupList[locationGroup].locations[location].id));
                }
                $('#locationFilter').append("</optgroup>");
            }

            $('#locationFilter').multiselect('refresh');
            $('#locationFilter').multiselect('rebuild');
        }
    });
}


var updateFrequencyList = function () {

    $('#frequencySelect').empty();

    $('#frequencySelect').append('<option value="" selected="selected">'
        + 'Select Frequency'
        + '</option>');

    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: baseDataURL + "getFrequencyList?callback=groupsets",
        success: function (data) {
            for (var item in data) {
                $('#frequencySelect').append($('<option>')
                    .text(data[item].value)
                    .attr('value', data[item].value));
            }
            $('#frequencySelect').selectpicker('refresh', {
                width: 'auto'
            });
        }
    });
    
}


var updateGroupSetList = function () {
    //-------------Load Item Groups------------------//

    //alert('updateGroupSetList');

    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: baseDataURL + "groupSets?callback=groupsets",
        success: function (data) {
            for (var item in data) {
                $('#groupSelect').append($('<option>')
                    .text(data[item].name)
                    .attr('value', data[item].dataelementgroupsetid));
            }
            $('#groupSelect').selectpicker('refresh', {
                width: 'auto'
            });
        }
    });

}

var updateGroupList = function () {
    //-------------Load Item Sub Groups------------------//

    //alert('updateGroupList');

    $('#subGroupSelect').empty();

    $('#subGroupSelect').append('<option value="" selected="selected">'
        + 'Select Subgroup'
        + '</option>');

    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: baseDataURL + "getSubGroups?callback=groupsets"
            + "&groupSetID="
            + $('#groupSelect').val()
            + "&groupID="
            + $('#subGroupSelect').val(),
        success: function (data) {

            for (var item in data) {
                $('#subGroupSelect').append($('<option>')
                    .text(data[item].name)
                    .attr('value', data[item].dataelementgroupid));
            }
            $('#subGroupSelect').selectpicker('refresh', {
                width: 'auto'
            });
        }
    });

}


var setItem = function (itemName) {
    $('#typeahead').val(itemName);
    glDataElementID = dataElementMap[itemName];
    glCategoryComboID = categoryComboMap[itemName];
    updateDates();
}


var updateItemBox = function () {

    var itemURL = baseDataURL + "getItemList?callback=groupsets&frequency="
        + $('#frequencySelect').val()
        + "&groupSetID="
        + $('#groupSelect').val()
        + "&groupID="
        + $('#subGroupSelect').val()
        + "&locations="
        + $('#locationFilter').val();

    $('#filteredList').empty();
    $('itemCount').empty();
    $('#listBox').hide();
    $('#listBox').mCustomScrollbar("destroy");

    var itemsCount = 0;

    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: itemURL,
        success: function (data) {

            var dataelments = [];
            itemsCount = data.length;

            for (var item in data) {
                dataelments.push(data[item].name);
                $('#filteredList').append('<li><a href=\"javascript:setItem(\'' + data[item].name + '\')\">' + data[item].name + '</a></li>');
                dataElementMap[data[item].name] = data[item].dataelementid;
                categoryComboMap[data[item].name] = data[item].categoryoptionid;
            }
            ;


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
            ).on('typeahead:selected', function ($e) {
                    glDataElementID = dataElementMap[$('#bloodhound #typeahead').typeahead('val')];
                    glCategoryComboID = categoryComboMap[$('#bloodhound #typeahead').typeahead('val')];
                    updateDates();
                });

            if (itemsCount > 0) {

                $('#itemCount').text("# of Items: "+itemsCount);
                $('#listBox').show();
                $("#listBox").mCustomScrollbar({
                    advanced: {
                        updateOnContentResize: true
                    },
                    scrollbarPosition: "inside",
                    autoHideScrollbar: true,
                    autoExpandScrollbar: true,
                    theme: "rounded-dark"
                });
            }
            else {
                $('#itemCount').text("# of Items: "+itemsCount);
                $('#listBox').show();
                $('#listBox').hide();
            }
        }

    });


};


var updateDates = function () {

    var dataDatesURL = baseDataURL + 'getDateRange?dataElementID=' +
        glDataElementID + '&locations=' + $('#locationFilter').val();

    $('#dateInput1').val('');
    $('#dateInput2').val('');

    //-------------Load Item Groups------------------//
    $.ajax({
        type: "GET",
        dataType: "jsonp",
        contentType: "application/json",
        async: false,
        url: dataDatesURL,
        success: function (data) {
            for (var item in data) {

                var startDateObj = moment(data[item].startdate, 'YYYY-MM-DD').toDate();
                var endDateObj = moment(data[item].enddate, 'YYYY-MM-DD').toDate();

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

var populateChart = function (name, unit, seriesList) {

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
        series: [],

        exporting: {
            filename: name
        }
    };

    for (var seriesItem in seriesList) {
        //alert('T8: '+seriesList[seriesItem].data.length);

        var series = {
            data: []
        };
        series.name = seriesList[seriesItem].name;
        for (var dataItem in seriesList[seriesItem].data) {
            series.data.push(seriesList[seriesItem].data[dataItem]);
        }
        options.series.push(series);
    }

    var chart = new Highcharts.Chart(options);

}

var getData = function () {

    var startDate = $('#dateInput1').val();
    var endDate = $('#dateInput2').val();

    var startDateObj = moment(startDate, 'DD/MM/YYYY').toDate();
    var endDateObj = moment(endDate, 'DD/MM/YYYY').toDate();

    var StartDate = moment(startDateObj).format('YYYY-MM-DD');
    var EndDate = moment(endDateObj).format('YYYY-MM-DD');

    //alert("T6: "+StartDate+":"+EndDate);

    var getDataURL = baseDataURL + 'getData?' +
        'dataElementID=' + glDataElementID +
        '&locations=' + glLocations +
        '&categoryID=' + glCategoryComboID +
        '&startDate=' + StartDate +
        '&endDate=' + EndDate;

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

            var unit;

            var name;

            dataPoints = data.length;

            if (dataPoints > 0) {

                for (var item in data) {

                    unit = data[item].unit;

                    name = data[item].name;

                    var dataPoint = [];

                    var startDateObj = moment(data[item].startdate, 'YYYY-MM-DD').toDate();

                    var dataStartDate = moment(startDateObj).format('DD/MM/YYYY');

                    var year = startDateObj.getFullYear();

                    var month = startDateObj.getMonth();

                    var date = startDateObj.getDate();

                    var utcStartDate = Date.UTC(year, month, date);

                    dataPoint.push(utcStartDate, parseFloat(data[item].value));

                    if (($.inArray(data[item].location, locationList)) == (-1)) {
                        locationList.push(data[item].location)
                    }

                    if (($.inArray(data[item].startdate, dateList)) == (-1)) {
                        dateList.push(data[item].startdate)
                    }

                    if (seriesList[data[item].location] == null) {
                        var series = {
                            data: []
                        };
                        series.name = data[item].location;
                        series.data.push(dataPoint);
                        seriesList[data[item].location] = series;
                    }
                    else {
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

                $('#dataTable').tablecloth({
                    theme: "dark",
                    bordered: true,
                    condensed: true,
                    striped: true,
                    sortable: true,
                    clean: true,
                    cleanElements: "th td",
                    customClass: "my-table"
                });
            }
            else {
                //TODO
                alert("No Values Found!!");
                //Place timed alert.

            }
        }
    });

}


$(document).ready(function () {

    $('#dateInput1').val('');
    $('#dateInput2').val('');

    updateGroupSetList();
    updateGroupList();
    updateFrequencyList();
    updateLocationFilterList();
    updateItemBox();

    $('#locationFilter').multiselect({
        nonSelectedText: 'Select Locations',
        numberDisplayed: 8,
        maxHeight: 300,
        onDropdownHide: function () {
            glLocations = $('#locationFilter').val();
            updateItemBox();
        }

    });
    ;

    $('.selectpicker').selectpicker();

    $('#groupSelect').change(function (event) {
        updateGroupList();
        updateLocationFilterList();
        updateItemBox();
        $('#locationFilter').multiselect('refresh');
        glLocations = $('#locationFilter').val();
    });

    $('#frequencySelect').change(function (event) {
        updateLocationFilterList();
        updateItemBox();
        $('#locationFilter').multiselect('refresh');
        glLocations = $('#locationFilter').val();
    });

    $('#subGroupSelect').change(function (event) {
        updateLocationFilterList();
        updateItemBox();
        $('#locationFilter').multiselect('refresh');
        glLocations = $('#locationFilter').val();
    });

    $('#viewDataButton').click(function () {
        if (glDataElementID == null || glCategoryComboID == null) {
            alert('Insufficient Information, please make all the selections');
        }
        else {
            getData();
        }
    });

    $('#resetBtn').click(function (){
        updateGroupSetList();
        updateGroupList();
        updateFrequencyList();
        updateLocationFilterList();
        updateItemBox();
    });



    function exportTableToCSV($table, filename) {

        var $rows = $table.find('tr:has(td)'),

        // Temporary delimiter characters unlikely to be typed by keyboard
        // This is to avoid accidentally splitting the actual contents
            tmpColDelim = String.fromCharCode(11), // vertical tab character
            tmpRowDelim = String.fromCharCode(0), // null character

        // actual delimiter characters for CSV format
            colDelim = '","',
            rowDelim = '"\r\n"',

        // Grab text from table into CSV formatted string
            csv = '"' + $rows.map(function (i, row) {
                var $row = $(row),
                    $cols = $row.find('td');

                return $cols.map(function (j, col) {
                    var $col = $(col),
                        text = $col.text();

                    return text.replace('"', '""'); // escape double quotes

                }).get().join(tmpColDelim);

            }).get().join(tmpRowDelim)
                .split(tmpRowDelim).join(rowDelim)
                .split(tmpColDelim).join(colDelim) + '"',

        // Data URI
            csvData = 'data:application/csv;charset=utf-8,' + encodeURIComponent(csv);

        $(this)
            .attr({
                'download': filename,
                'href': csvData,
                'target': '_blank'
            });
    }

    // This must be a hyperlink
    $(".export").on('click', function (event) {
        // CSV
        exportTableToCSV.apply(this, [$('#tableContainer>table'), 'export.csv']);

        // IF CSV, don't do event.preventDefault() or return false
        // We actually need this to be a typical hyperlink
    });

});

