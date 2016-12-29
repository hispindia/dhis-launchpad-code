function mychartfunction(mydata,xdata,left,title,ytitle,xtitle,render,chartid,windowid,posx)
{
	var chart;
	$(document).ready(function() {
		chart = new Highcharts.Chart({
			chart: {
				renderTo: chartid,
				borderColor: '#0000FF',
                borderWidth: 3,
                height: 340,
                width: 400,
                zoomType: 'x',
				type: 'column'
			},
			title: {
				text: title
			},
			xAxis: {
				categories: xdata,
				title: {
					text: xtitle
				}
			},
			yAxis: {
				min: 0,
				title: {
					text: ytitle
				}
			},
			
			tooltip: {
				formatter: function() {
					return ''+
						this.x +': '+ this.y;
				}
			},
			plotOptions: {
				column: {
					pointPadding: 0.2,
					borderWidth: 0
				},
				showInLegend: true
			},
				series:mydata
		});
	});
			        
	           

}

function mypiechartfunction(mydata,title,chartid,org)
{
	var chart;
    $(document).ready(function() {
        chart = new Highcharts.Chart({
        	chart: {
				renderTo: chartid,
				borderColor: '#0000FF',
                borderWidth: 3,
                height: 340,
                width: 400
			},
            title: {
                text: title
            },
            subtitle: {
                text: org
           },
            tooltip: {
                formatter: function() {
                    return '<b>'+ this.point.name +'</b>: '+ this.y ;
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
    					enabled: true,
    					color: '#000000',
    					connectorColor: '#000000',
    					formatter: function() {
    						return '<b>'+ Math.round(this.y*Math.pow(10,2))/Math.pow(10,2);
    					}
    				},
                    showInLegend: true
                }
            
            },
            series: [{
                type: 'pie',
                name: 'Indicator share',
                data:mydata
            }]
        });
    });
    
}

