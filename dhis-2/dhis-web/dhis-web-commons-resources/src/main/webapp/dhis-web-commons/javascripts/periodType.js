function PeriodType()
{
    var dateFormat = 'yyyy-MM-dd';

    var periodTypes = [];
    periodTypes['Daily'] = new DailyPeriodType( dateFormat );
    periodTypes['Weekly'] = new WeeklyPeriodType( dateFormat );
    periodTypes['Monthly'] = new MonthlyPeriodType( dateFormat );
    periodTypes['BiMonthly'] = new BiMonthlyPeriodType( dateFormat );
    periodTypes['Quarterly'] = new QuarterlyPeriodType( dateFormat );
    periodTypes['SixMonthly'] = new SixMonthlyPeriodType( dateFormat );
    periodTypes['Yearly'] = new YearlyPeriodType( dateFormat );
    periodTypes['FinancialOct'] = new FinancialOctoberPeriodType( dateFormat );
    periodTypes['FinancialJuly'] = new FinancialJulyPeriodType( dateFormat );
    periodTypes['FinancialApril'] = new FinancialAprilPeriodType( dateFormat );
	periodTypes['Forteen'] = new ForteenPeriodType( dateFormat );

    this.get = function( key )
    {
        return periodTypes[key];
    };

    this.reverse = function( array )
    {
        var reversed = [];
        var i = 0;

        for ( var j = array.length - 1; j >= 0; j-- )
        {
            reversed[i++] = array[j];
        }

        return reversed;
    };

    this.filterFuturePeriods = function( periods )
    {
        var array = [];
        var i = 0;

        var now = new Date().getTime();

        for ( var j = 0; j < periods.length; j++ )
        {
            if ( $.date( periods[j]['startDate'], dateFormat ).date().getTime() < now )
            {
                array[i++] = periods[j];
            }
        }

        return array;
    };
}

function daysInMonth(iMonth, iYear)
{
	return 32 - new Date(iYear, iMonth, 32).getDate();
}

function ForteenPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
		var date = new Date();
        var year = date.getFullYear() + offset;
		var month = date.getMonth() + 1;
		if( date.getFullYear() >= year  )
		{
			month = "12";
		}
        var day = date.getDate();
        var i = 0;
		
		var date = "";
        if( day <=15){
			date = "1";
			var endDate = $.date( year + '-' + month + '-15', dateFormat );
		}
		else
        {
            date = "16";
			var endDate = $.date( year + '-' + month + '-' + daysInMonth(month-1,year), dateFormat );
        }
		var startDate = $.date( year + '-' + month + '-' + date , dateFormat );
		
		while ( startDate.date().getFullYear() == year && month >=1 )
		{
			var period = [];
			period['startDate'] = startDate.format( dateFormat );
			period['endDate'] = endDate.format( dateFormat );
			period['name'] = startDate.format( dateFormat ) + " - " + endDate.format( dateFormat );
			period['id'] = 'Forteen_' + period['startDate'] + "_" + period['endDate'];
			period['iso'] = year + 'D' + ( i + 1 );
			periods[i] = period;

			month = startDate.date().getMonth() + 1;
			if( startDate.date().getDate() == 1 ){
				month--;
				if(month >=1 )
				{
					startDate = $.date( year + '-' + month + '-16' , dateFormat );
					endDate = $.date( year + '-' + month + '-' + daysInMonth(month-1,year), dateFormat );
				}
			}
			else{
				startDate = $.date( year + '-' + month + '-1' , dateFormat );
				endDate = $.date( year + '-' + month + '-15', dateFormat );
			}
			i++;
		}

		var resultPeriod  = [];
		var index = 0;
		for( i = periods.length - 1; i>=0; i-- )
		{
			resultPeriod[index] = periods[i];
			index++;
		}
        return resultPeriod;
    };
}


function DailyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-01-01', dateFormat );
        var i = 0;

        while ( startDate.date().getFullYear() <= year )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['name'] = startDate.format( dateFormat );
            period['id'] = 'Daily_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'D', +1 );
            i++;
        }

        return periods;
    };
}

function WeeklyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-01-01', dateFormat );
        var day = startDate.date().getDay();
        var i = 0;

        if ( day == 0 ) // Sunday, forward to Monday
        {
            startDate.adjust( 'D', +1 );
        }
        else if ( day <= 4 ) // Monday - Thursday, rewind to Monday
        {
            startDate.adjust( 'D', ( ( day - 1 ) * -1 ) );
        }
        else
        // Friday - Saturday, forward to Monday
        {
            startDate.adjust( 'D', ( 8 - day ) );
        }

        while ( startDate.date().getFullYear() <= year )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['name'] = 'W' + ( i + 1 ) + ' - ' + startDate.format( dateFormat );
            period['id'] = 'Weekly_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'D', +7 );
            i++;
        }

        return periods;
    };
}

function MonthlyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-01-01', dateFormat );
        var i = 0;

        while ( startDate.date().getFullYear() == year )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['name'] = monthNames[i] + ' ' + year;
            period['id'] = 'Monthly_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'M', +1 );
            i++;
        }

        return periods;
    };
}

function BiMonthlyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-01-01', dateFormat );
        var i = 0;
        var j = 0;

        while ( startDate.date().getFullYear() == year )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['name'] = monthNames[i] + ' - ' + monthNames[i + 1] + ' ' + year;
            period['id'] = 'BiMonthly_' + period['startDate'];
            periods[j] = period;

            startDate.adjust( 'M', +2 );
            i += 2;
            j++;
        }

        return periods;
    };
}

function QuarterlyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-01-01', dateFormat );
        var i = 0;
        var j = 0;

        while ( startDate.date().getFullYear() == year )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['name'] = monthNames[i] + ' - ' + monthNames[i + 2] + ' ' + year;
            period['id'] = 'Quarterly_' + period['startDate'];
            periods[j] = period;

            startDate.adjust( 'M', +3 );
            i += 3;
            j++;
        }

        return periods;
    };
}

function SixMonthlyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;

        var period = [];
        period['startDate'] = year + '-01-01';
        period['name'] = monthNames[0] + ' - ' + monthNames[5] + ' ' + year;
        period['id'] = 'SixMonthly_' + period['startDate'];
        periods[0] = period;

        period = [];
        period['startDate'] = year + '-06-01';
        period['name'] = monthNames[6] + ' - ' + monthNames[11] + ' ' + year;
        period['id'] = 'SixMonthly_' + period['startDate'];
        periods[1] = period;

        return periods;
    };
}

function YearlyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-01-01', dateFormat ).adjust( 'Y', -5 );

        for ( var i = 0; i < 11; i++ )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['name'] = startDate.date().getFullYear();
            period['id'] = 'Yearly_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'Y', +1 );
        }

        return periods;
    };
}

function FinancialOctoberPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-10-01', dateFormat ).adjust( 'Y', -5 );

        for ( var i = 0; i < 11; i++ )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['name'] =  monthNames[9] + ' ' +  startDate.date().getFullYear() + '-' + monthNames[8] + ' ' + (startDate.date().getFullYear() +1 );
            period['id'] = 'FinancialOct_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'Y', +1 );
        }

        return periods;
    };
}

function FinancialJulyPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-07-01', dateFormat ).adjust( 'Y', -5 );

        for ( var i = 0; i < 11; i++ )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['name'] =  monthNames[6] + ' ' +  startDate.date().getFullYear() + '-' + monthNames[5] + ' ' + (startDate.date().getFullYear() +1 );
            period['id'] = 'FinancialJuly_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'Y', +1 );
        }

        return periods;
    };
}

function FinancialAprilPeriodType( dateFormat )
{
    this.generatePeriods = function( offset )
    {
        var periods = [];
        var year = new Date().getFullYear() + offset;
        var startDate = $.date( year + '-04-01', dateFormat ).adjust( 'Y', -5 );

        for ( var i = 0; i < 11; i++ )
        {
            var period = [];
            period['startDate'] = startDate.format( dateFormat );
            period['name'] =  monthNames[3] + ' ' +  startDate.date().getFullYear() + '-' + monthNames[2] + ' ' + (startDate.date().getFullYear() +1 );
            period['id'] = 'FinancialApril_' + period['startDate'];
            periods[i] = period;

            startDate.adjust( 'Y', +1 );
        }

        return periods;
    };
}