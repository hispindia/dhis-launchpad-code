# Copyright (c) 2012, University of Oslo
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# Redistributions of source code must retain the above copyright notice, this
# list of conditions and the following disclaimer.
# Redistributions in binary form must reproduce the above copyright notice,
# this list of conditions and the following disclaimer in the documentation
# and/or other materials provided with the distribution.
# Neither the name of the HISP project nor the names of its contributors may
# be used to endorse or promote products derived from this software without
# specific prior written permission.


require(RODBC)
require(reshape)
require(ggplot2)
require(scales)
require(grid)
require(zoo)
require(xts)
require(lubridate)
#Requires an ODBC channel to be defined with this name
channel<-odbcConnect("fsnis")
#Get this from the command line
cmd_args<-commandArgs();
#TODO. Seems weak. 
reportDate<-as.Date(cmd_args[6])
#Set the working directory
#TODO..this needs to be paramaterized
#target_path<-"/home/fsnispro/dhis_home/Routput/"
#target_path<-"D:\\source_codes\\dhis\\conf\\fpmureports\\chartoutput\\"
target_path<-cmd_args[8]

setwd(target_path)

#Set quarterly period
quarter_period<-cmd_args[7]
#paste(target_path,"quarterlychart1_",quarter_period,".png",sep="")
 
#Convenience functions
getAverageDataValues<-function(dataelementid,organisationunitid,startdate,enddate){

sql<-"SELECT  p.startdate, avg(dv.value)  as value FROM datavalue dv 
INNER JOIN period p on dv.periodid = p.periodid 
where dv.dataelementid = %dataelementid and dv.sourceid = %organisationunitid AND p.startdate BETWEEN '%startdate' AND '%enddate'
GROUP BY year(p.startdate),month(p.startdate) ORDER BY p.startdate DESC"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%startdate",startdate,sql)
sql<-gsub("%enddate",enddate,sql)
values<-sqlQuery(channel,sql)
return (values)
}

getAverageDataValuesAsc<-function(dataelementid,organisationunitid,startdate,enddate){

sql<-"SELECT  p.startdate, avg(dv.value)  as value FROM datavalue dv 
INNER JOIN period p on dv.periodid = p.periodid 
where dv.dataelementid = %dataelementid and dv.sourceid in(%organisationunitid) AND p.startdate BETWEEN '%startdate' AND '%enddate'
GROUP BY year(p.startdate),month(p.startdate) ORDER BY p.startdate ASC"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%startdate",startdate,sql)
sql<-gsub("%enddate",enddate,sql)
values<-sqlQuery(channel,sql)
return (values)
}

getSumDataValues<-function(dataelementid,organisationunitid,startdate,enddate){

sql<-"SELECT  p.startdate, sum(dv.value)  as value FROM datavalue dv 
INNER JOIN period p on dv.periodid = p.periodid 
where dv.dataelementid in( %dataelementid) and dv.sourceid = %organisationunitid AND p.startdate BETWEEN '%startdate' AND '%enddate' group by p.startdate"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%startdate",startdate,sql)
sql<-gsub("%enddate",enddate,sql)
values<-sqlQuery(channel,sql)
return (values)
}



getMovingAverage<-function(dataelementid,organisationunitid,enddate){

sql<-"SELECT avg(dv.value)  as value FROM datavalue dv 
INNER JOIN period p on dv.periodid = p.periodid 
where dv.dataelementid in( %dataelementid) and dv.sourceid = %organisationunitid AND p.startdate BETWEEN DATE_SUB('%enddate', INTERVAL 6 MONTH) AND DATE_ADD('%enddate',INTERVAL 6 MONTH)"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
#sql<-gsub("%date1",date1,sql)
sql<-gsub("%enddate",enddate,sql)
values<-sqlQuery(channel,sql)
return (values)
}


getAvgFourYearPrices<-function(dataelementid,organisationunitid,startdate,enddate,inBetweenMonths){

sql<-"SELECT MONTH(p.startdate),  AVG(dv.value)  AS value FROM datavalue dv 
INNER JOIN period p ON dv.periodid = p.periodid 
WHERE dv.dataelementid = %dataelementid AND dv.sourceid = %organisationunitid AND p.startdate BETWEEN DATE_SUB('%startdate', INTERVAL 48 MONTH) AND '%enddate'
AND MONTH(p.startdate) IN (%inBetweenMonths)
GROUP BY MONTH(p.startdate) ORDER BY p.startdate DESC"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%startdate",startdate,sql)
sql<-gsub("%enddate",enddate,sql)
sql<-gsub("%inBetweenMonths",inBetweenMonths,sql)
values<-sqlQuery(channel,sql)
return (values)
}

getPastYearCPI<-function(dataelementid,organisationunitid,date1){

sql<-"SELECT  p.startdate, AVG(dv.value)  AS  value
FROM datavalue dv 
INNER JOIN period p ON dv.periodid = p.periodid 
WHERE dv.dataelementid = %dataelementid AND dv.sourceid = %organisationunitid AND p.startdate BETWEEN '%date1' AND DATE_ADD('%date1',INTERVAL 1 MONTH)
AND MONTH(p.startdate) = MONTH('%date1')
GROUP BY YEAR(p.startdate),MONTH(p.startdate) ORDER BY p.startdate DESC;
"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%date1",date1,sql)
values<-sqlQuery(channel,sql)
return (values)
}

reportDate<-as.Date(reportDate)
#print(reportDate)

enddatecurr<-reportDate
tempdate<-paste(year(reportDate),"-01-01",sep="")
print(tempdate)
startdatecurr<-as.Date(tempdate)

tempdate<-as.Date(tempdate)-years(1)

print("asd")
startdateprev<-tempdate

tempdate<-paste(year(tempdate),"-12-31",sep="")
enddateprev<-as.Date(tempdate)
print("------------")
print(startdateprev)
print(enddateprev)
print(startdatecurr)
print(enddatecurr)
print("-----*******-------")

#Figure 2.1 graph production
#End Figure 2.1


#Figure 2.2-------------
#End Figure 2.2


#Fig 1.2-----------------------
print("chart2-----------------------------------------")
#end of fig 1.2

#Fig 1.1-----------------------
print("chart1-----------------------------------------")

#end of fig 1.1

#Fig 4--------------------
print("chart5-----------------------------------------")
#Fig 4--------------------



#Fig 5--------------------
print("chart6-----------------------------------------")


#End of Fig 5--------------------

#Fig 6------------------->>>>>>>>>>>>>>>>>>>>>>>>>
print("chart7----------------------------")

#End of fig 6 ---------------------
#Fig 7------------------->>>>>>>>>>>>>>>>>>>>>>>>>
print("chart8-----------------------------------------")
png(filename=paste(target_path,"quarterlychart8_",quarter_period,".png",sep=""), width=800,heigh=400)

enddate=enddatecurr
startdate=as.Date(enddate-months(4))
tempdate<-paste(year(startdate),"-",month(startdate),"-01",sep="")
print(tempdate)
startdate<-tempdate

fig7.retailPrice<-getAverageDataValues(1001,2,startdate,enddate)
fig7.cpi<-getAverageDataValues(983,2,startdate,enddate)
retailPrices<-fig7.retailPrice$value
cpiPrices<-fig7.cpi$value
colnames(fig7.retailPrice)<-c("month","Market Price")
fig7.retailPrice$month<-month(fig7.retailPrice$month)
print(fig7.retailPrice)


realPrices<-(retailPrices*(cpiPrices[length(cpiPrices)]))
realPrices<-realPrices/cpiPrices
print(realPrices)

date<-enddate
print(as.Date(date) - months(1))
movingAverageMonth1<-getMovingAverage(1001,2,as.Date(date) - months(1))
movingAverageMonth2<-getMovingAverage(1001,2,as.Date(date)- months(2))
movingAverageMonth3<-getMovingAverage(1001,2,as.Date(date)- months(3))
movingAverageMonth4<-getMovingAverage(1001,2,as.Date(date)- months(4))
movingAverageMonth5<-getMovingAverage(1001,2,as.Date(date)- months(5))
movingAverages<-c(movingAverageMonth1,movingAverageMonth2,movingAverageMonth3,movingAverageMonth4,movingAverageMonth5)
movingAverages<-rbind(movingAverageMonth1,movingAverageMonth2,movingAverageMonth3,movingAverageMonth4,movingAverageMonth5)

print(movingAverages)

inBetweenMonths=""
for (i in month(startdate):month(enddate))
{
inBetweenMonths<-paste(inBetweenMonths,i,sep=",")
}
inBetweenMonths<-substr(inBetweenMonths,2,nchar(inBetweenMonths))

fig7.retailPricesof4Years<-getAvgFourYearPrices(1001,2,startdate,enddate,inBetweenMonths)


retailPricesof4Years<-fig7.retailPricesof4Years$value
print("retail price of 4 years")
print(retailPricesof4Years)
averageRealPrices<-retailPricesof4Years*cpiPrices[length(cpiPrices)]
averageRealPrices<-averageRealPrices/cpiPrices
print("Average real price")
print(averageRealPrices)

temp<-cpiPrices/cpiPrices[length(cpiPrices)]
print(temp)
normalPastPricesMonthly<-averageRealPrices*temp
print("normal past prices monthly")
print(normalPastPricesMonthly)
tempMonth<-fig7.retailPrice$month
print(tempMonth)
normalPastPricesMonthly<-cbind(tempMonth,normalPastPricesMonthly)
colnames(normalPastPricesMonthly)<-c("month","Normal Past Price")
print(normalPastPricesMonthly)
print("444444444")
foo<-as.data.frame(merge(normalPastPricesMonthly,fig7.retailPrice))
#print(foo)
inflationFactor<-cpiPrices[length(cpiPrices)]
currMonthPrice<-retailPrices[length(retailPrices)]
pastYearCPI<-getPastYearCPI(983,2,enddate)
pastYearCPI<-pastYearCPI$value
if(length(pastYearCPI)==0){
pastYearCPI=280
}

print(pastYearCPI)
inflationFactor<-(inflationFactor/pastYearCPI)^(1/12)
print(inflationFactor)
print(currMonthPrice)
benchMarkPriceLow<-c(currMonthPrice*inflationFactor*(1.0-.04),currMonthPrice*inflationFactor*(1-.05),currMonthPrice*inflationFactor*(1-.06))
benchMarkPriceMed<-c(currMonthPrice*inflationFactor,currMonthPrice*inflationFactor,currMonthPrice*inflationFactor)
benchMarkPriceHigh<-c(currMonthPrice*inflationFactor*(1.0+.04),currMonthPrice*inflationFactor*(1+.05),currMonthPrice*inflationFactor*(1+.06))

futureMonths<-month(seq(as.Date(enddate),by="month",length=4))
futureMonths<-futureMonths[2:length(futureMonths)]
monthNames<-(c(sort(fig7.retailPrice$month),futureMonths))
allMonths<-c(1:12)
monthNamesNumeric<-monthNames
#print(monthNames)
monthNames1<-month.name[monthNames]
#print(monthNames1)
#print(fig7.retailPrice$month)

benchMarkPriceLow<-cbind(futureMonths,benchMarkPriceLow)
benchMarkPriceMed<-cbind(futureMonths,benchMarkPriceMed)
benchMarkPriceHigh<-cbind(futureMonths,benchMarkPriceHigh)
colnames(benchMarkPriceLow)<-c("month","Bench Mark Price - Low")
colnames(benchMarkPriceMed)<-c("month","Bench Mark Price - Med")
colnames(benchMarkPriceHigh)<-c("month","Bench Mark Price - High")
#print(benchMarkPriceLow)
#print(benchMarkPriceMed)
#print(benchMarkPriceHigh)

benchMark<-as.data.frame(merge(benchMarkPriceMed,benchMarkPriceLow,by="month",all=TRUE,sort=F))
#print(benchMark)
benchMark<-as.data.frame(merge(benchMark,benchMarkPriceHigh,by="month",all=TRUE,sort=F))
#print(benchMark)
foo<-as.data.frame(merge(foo,benchMark,by="month",all=TRUE,sort=F))
#print(foo)
print("!!!!!!!!!!!!!!!!!!!!!!!!!")
print(foo)
fee<-melt(foo,id.vars="month")
print(fee)
lbl<-round(fee$value,1)
p<-qplot(data=fee,factor(rep(c(1:8),5),label=monthNames1),value,color=variable,geom=c("point","line"),group=variable,main=paste("Domestic Wheat price forecast, ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+labs(x = "", y = "Taka per quintal")
p<-p + theme(legend.title = element_text(size=0,color="white"))
p<-p+geom_text(aes(label=lbl,y=lbl-.1))
p<-p+ theme(legend.position = "bottom",legend.text = element_text(size = 14,hjust=1,vjust=1))
p<-p + theme(axis.text.x  = element_text(size=15,angle=45,colour="black",vjust=1,hjust=1))
p<-p + theme(axis.text.y  = element_text(size=15,angle=0,colour="black"))
p<-p + theme(plot.title= element_text(size=17,angle=0,colour="black",face="bold"))
p<-p + theme(plot.background = element_rect(fill = "#CADF7B",color="black",linetype = "solid"))
p<-p + theme(panel.background = element_rect(fill = "white"))
#p<-p+scale_x_discrete(labels=monthNames1,limits=monthNames1)
plot(p)

dev.off()

#End of fig 7 ---------------------

