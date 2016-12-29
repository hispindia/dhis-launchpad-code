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
#targetPath<-"/home/fsnispro/dhis_home/Routput/"
#targetPath<-"D:\\source_codes\\dhis\\conf\\fpmureports\\chartoutput\\"
targetPath<-cmd_args[7]
setwd(targetPath)
#zz <- file("all.Rout", open="wt")
#sink(zz, type = c("output", "message"))

#paste(targetPath,"quarterlychart1_",quarter_period,".png",sep="")
 
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
#1900-01-01 dummy date only used for merging.....needs refactoring #todo
sql<-"SELECT  '1900-01-01' AS startdate, sum(dv.value)  as value FROM datavalue dv 
INNER JOIN period p on dv.periodid = p.periodid 
where dv.dataelementid in( %dataelementid) and dv.sourceid = %organisationunitid AND p.startdate BETWEEN '%startdate' AND '%enddate'"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%startdate",startdate,sql)
sql<-gsub("%enddate",enddate,sql)

values<-sqlQuery(channel,sql)
return (values)
}



getMovingAverage<-function(dataelementid,organisationunitid,CPIDE,enddate){

sql<-"SELECT ((dv.value/dv1.value*(SELECT dv.value
			FROM datavalue dv
			INNER JOIN period p ON dv.periodid = p.periodid 
			WHERE dv.dataelementid IN( %CPIDE) AND dv.sourceid =  %organisationunitid
			AND MONTH(p.startdate) = MONTH('%enddate')
			AND YEAR(p.startdate) = YEAR('%enddate')))/
				AVG(dv.value/dv1.value*(	SELECT dv.value
				FROM datavalue dv
				INNER JOIN period p ON dv.periodid = p.periodid 
				WHERE dv.dataelementid IN( %CPIDE) AND dv.sourceid = %organisationunitid
				AND MONTH(p.startdate) = MONTH('%enddate')
				AND YEAR(p.startdate) = YEAR('%enddate'))))  AS value 
FROM datavalue dv 
INNER JOIN period p ON dv.periodid = p.periodid 
INNER JOIN datavalue dv1 ON dv.periodid = dv1.periodid AND dv1.dataelementid=%CPIDE
where dv.dataelementid in( %dataelementid) and dv.sourceid = %organisationunitid AND p.startdate
BETWEEN DATE_SUB('%enddate', INTERVAL 6 MONTH) AND DATE_ADD('%enddate',INTERVAL 6 MONTH);"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%CPIDE",CPIDE,sql)
sql<-gsub("%enddate",enddate,sql)
values<-sqlQuery(channel,sql)

return (values)
}


getAvgFourYearPrices<-function(dataelementid,organisationunitid,CPIDE,currMonthCPI,enddate,inBetweenMonths){

sql<-"SELECT MONTH(p.startdate),  AVG(dv.value/dv1.value*%currMonthCPI)  AS value FROM datavalue dv 
INNER JOIN period p ON dv.periodid = p.periodid 
INNER JOIN datavalue dv1 ON dv.periodid = dv1.periodid AND dv1.dataelementid=%CPIDE
WHERE dv.dataelementid = %dataelementid AND dv.sourceid = %organisationunitid AND p.startdate BETWEEN DATE_SUB('%enddate', INTERVAL 48 MONTH) AND '%enddate'
AND MONTH(p.startdate) IN (%inBetweenMonths)
GROUP BY MONTH(p.startdate) ORDER BY month(p.startdate) DESC"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%CPIDE",CPIDE,sql)
sql<-gsub("%currMonthCPI",currMonthCPI,sql)
sql<-gsub("%enddate",enddate,sql)
sql<-gsub("%inBetweenMonths",inBetweenMonths,sql)

values<-sqlQuery(channel,sql)
return (values)
}

getPastYearCPI<-function(dataelementid,organisationunitid,date1){

sql<-"SELECT  p.startdate, AVG(dv.value)  AS  value
FROM datavalue dv 
INNER JOIN period p ON dv.periodid = p.periodid 
WHERE dv.dataelementid = %dataelementid AND dv.sourceid = %organisationunitid AND p.startdate BETWEEN DATE_SUB('%date1',INTERVAL 13 MONTH) AND DATE_SUB('%date1',INTERVAL 1 YEAR)
AND MONTH(p.startdate) = MONTH('%date1')
GROUP BY YEAR(p.startdate),MONTH(p.startdate) ORDER BY p.startdate DESC;
"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%date1",date1,sql)

values<-sqlQuery(channel,sql)

return (values)
}

getSeasonalFactor<-function(currMonthPrice,dataelementid,CPIDE,organisationunitid,enddate){

movingAverageMonth1<-getMovingAverage(dataelementid,organisationunitid,CPIDE,as.Date(enddate)-years(1))
movingAverageMonth2<-getMovingAverage(dataelementid,organisationunitid,CPIDE,as.Date(enddate)- years(2))
movingAverageMonth3<-getMovingAverage(dataelementid,organisationunitid,CPIDE,as.Date(enddate)- years(3))
movingAverageMonth4<-getMovingAverage(dataelementid,organisationunitid,CPIDE,as.Date(enddate)- years(4))
movingAverageMonth5<-getMovingAverage(dataelementid,organisationunitid,CPIDE,as.Date(enddate)- years(5))
movingAverageMonth6<-getMovingAverage(dataelementid,organisationunitid,CPIDE,as.Date(enddate)- years(6))
movingAverageMonth7<-getMovingAverage(dataelementid,organisationunitid,CPIDE,as.Date(enddate)- years(7))

movingAverages<-rbind(movingAverageMonth1,movingAverageMonth2,movingAverageMonth3,movingAverageMonth4,movingAverageMonth5,movingAverageMonth6,movingAverageMonth7)
#print("moving averages")
#print(movingAverages)

#ratioMovingAverageToCurrentPrice=currMonthPrice/movingAverages

seasonalFactor<-mean(movingAverages$value)
#print(seasonalFactor)
return(seasonalFactor)
}

get12MonthCumulativeSeasonalFactor<-function(currMonthPrice,dataelementid,CPIDE,organisationunitid,enddate){

seasonalFactor1<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,enddate)
seasonalFactor2<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(1))
seasonalFactor3<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(2))
seasonalFactor4<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(3))
seasonalFactor5<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(4))
seasonalFactor6<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(5))
seasonalFactor7<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(6))
seasonalFactor8<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(7))
seasonalFactor9<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(8))
seasonalFactor10<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(9))
seasonalFactor11<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(10))
seasonalFactor12<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(11))

cumulativeSeasonalFactor<-seasonalFactor1+seasonalFactor2+seasonalFactor3+seasonalFactor4+seasonalFactor5+seasonalFactor6+
seasonalFactor7+seasonalFactor8+seasonalFactor9+seasonalFactor10+seasonalFactor11+seasonalFactor12

return(cumulativeSeasonalFactor)

}

getNormalPastPriceSeasonalFactors<-function(currMonthPrice,dataelementid,CPIDE,organisationunitid,enddate){

seasonalFactor1<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,enddate)
seasonalFactor2<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(1))
seasonalFactor3<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(2))
seasonalFactor4<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(3))
seasonalFactor5<-getSeasonalFactor(currMonthPrice,dataelementid,CPIDE,organisationunitid,as.Date(enddate) - months(4))

normalPastPriceSeasonalFactors<-rbind(seasonalFactor1,seasonalFactor2,seasonalFactor3,seasonalFactor4,seasonalFactor5)

return(normalPastPriceSeasonalFactors)
}

createNoDataImage<-function(chartName){

temp1 <- data.frame(x=1:1, val=1:100, nodata='x')

x<- temp1
 
lbl<-c("No Data        ")

# Prepare plot
p <- ggplot(x, aes(x=x, y=val, group=nodata))
 
# Plot.  Add legend and color by function.  Add title.
p<-p + geom_line(aes(color=nodata)) 
p<-p+geom_text(aes(label=c("No Data        "),y=50,x=1),size=12)

ggsave(
  paste(targetPath,chartName,quarter_period,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
) 
}


myprint<-function(...){
data<-list(...)
  if(length(data)==0){
  print("")
  }else{
  combinedData<-""
    for(i in 1:length(data)){
	  combinedData<-paste(combinedData,data[i],sep="")
    }
  print(combinedData)
  }
}

commonGraphAttributes<-function(p){
p<-p + theme(legend.title = element_blank())
p<-p+ theme(legend.position = "bottom",legend.text = element_text(size = 25,hjust=.5),legend.direction="horizontal",legend.key.size=unit(.1,"cm"),legend.key.width=unit(1.5,"cm"))
p<-p + theme(axis.text.x  = element_text(size=20,angle=45,colour="black",vjust=1,hjust=1))
p<-p + theme(axis.text.y  = element_text(size=20,angle=0,colour="black"))
p<-p + theme(axis.title.x  = element_text(size=15,angle=0,colour="black",face="bold"),axis.title.y  = element_text(size=15,angle=90,colour="black",face="bold"))
p<-p + theme(plot.title= element_text(size=20,angle=0,colour="black",face="bold",hjust=.5,vjust=.5))
p<-p + theme(plot.background = element_rect(fill = "#CADF7B",color="black",linetype = "solid"))
p<-p + theme(panel.background = element_rect(fill = "white"))

return(p)
}

getChartQuarterMonths<-function(reportDate){
n<-ceiling(month(as.Date(reportDate))/4)
lastMonth<-month(as.Date(reportDate))
oldMonth<-month(as.Date(reportDate)-months(2))
paste(month.name[oldMonth],"-",month.name[lastMonth],"_",year(as.Date(reportDate)),sep="") }

quarter_period<-getChartQuarterMonths(reportDate)


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

#Fig 1graph production%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
print("chart1-----------------------------------------")
startdate<-paste(year(startdatecurr)-1,"-07-01",sep="")
enddate<-paste(year(startdatecurr),"-06-30",sep="")
print(startdate)
print(enddate)


fig1.aus<-getSumDataValues(paste(882,883,884,sep=","),2,startdate,enddate)
fig1.aus$year<-year(fig1.aus$startdate)
fig1.aus<-subset(fig1.aus,select=c("year","value"))
colnames(fig1.aus)[2]<-"Aus"
print(fig1.aus)

fig1.aman<-getSumDataValues(paste(886,887,885,1535,sep=","),2,startdate,enddate)
fig1.aman$year<-year(fig1.aman$startdate)
fig1.aman<-subset(fig1.aman,select=c("year","value"))
colnames(fig1.aman)[2]<-"Aman"
print(fig1.aman)

fig1.boro<-getSumDataValues(paste(888,889,890,sep=","),2,startdate,enddate)
fig1.boro$year<-year(fig1.boro$startdate)
fig1.boro<-subset(fig1.boro,select=c("year","value"))
colnames(fig1.boro)[2]<-"Boro"
print(fig1.boro)

fig1.wheat<-getSumDataValues(891,2,startdate,enddate)
fig1.wheat$year<-year(fig1.wheat$startdate)
fig1.wheat<-subset(fig1.wheat,select=c("year","value"))
colnames(fig1.wheat)[2]<-"Wheat"
print(fig1.wheat)

foo<-as.data.frame(merge(fig1.aus,fig1.aman,by="year"))
foo1<-as.data.frame(merge(fig1.boro,fig1.wheat,by="year"))
fee<-as.data.frame(merge(foo,foo1,by="year"))
tryCatch({
fee<-melt(fee,id.vars="year")
fee<-subset(fee,select=c("variable","value"))


fee$value<-fee$value/10
fee$value<-round(fee$value,2)
print(fee)
png(filename=paste(targetPath,"quarterlychart1_",quarter_period,".png",sep=""),width=600,height=600)
plotTitle<-paste(format(as.Date(startdate),"%Y"),"/",format(as.Date(enddate),"%y"), " (Actual)",sep="")

plot.new()
par(bg="#CADF7B")
color<-c("#C05057","#4F81BD","#8064A2","#E46C0A")

pie(fee$value,labels=fee$value,col=color,cex=3)
mtext(plotTitle,side=1,adj=1.0,cex=3)
legend(x="topright",legend=fee$variable,cex=1.7,fill=color,bty="n")

#dev.off()
},error=function(e){
print("an error occurred-----")
dev.off()
createNoDataImage("quarterlychart1_")

})

#end of fig 1%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#Fig 2graph production%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
print("chart2-----------------------------------------")
png(filename=paste(targetPath,"quarterlychart2_",quarter_period,".png",sep=""),width=600,height=600)
#1 july x-1 - 30 june x
startdate<-paste(year(startdatecurr),"-07-01",sep="")
enddate<-paste(year(startdatecurr)+1,"-06-30",sep="")
print(startdate)
print(enddate)
fig1.aus<-getSumDataValues(paste(910,911,912,sep=","),2,startdate,enddate)
#print(fig1.aus)
fig1.aus$year<-year(fig1.aus$startdate)

fig1.aus<-subset(fig1.aus,select=c("year","value"))
colnames(fig1.aus)[2]<-"Aus"
print(fig1.aus)

fig1.aman<-getSumDataValues(paste(913,914,915,1536,sep=","),2,startdate,enddate)
fig1.aman$year<-year(fig1.aman$startdate)
fig1.aman<-subset(fig1.aman,select=c("year","value"))
colnames(fig1.aman)[2]<-"Aman"
print(fig1.aman)

fig1.boro<-getSumDataValues(paste(916,917,918,sep=","),2,startdate,enddate)
fig1.boro$year<-year(fig1.boro$startdate)
fig1.boro<-subset(fig1.boro,select=c("year","value"))
colnames(fig1.boro)[2]<-"Boro"
print(fig1.boro)

fig1.wheat<-getSumDataValues(919,2,startdate,enddate)
fig1.wheat$year<-year(fig1.wheat$startdate)
fig1.wheat<-subset(fig1.wheat,select=c("year","value"))
colnames(fig1.wheat)[2]<-"Wheat"
print(fig1.wheat)

foo<-as.data.frame(merge(fig1.aus,fig1.aman,by="year"))
foo1<-as.data.frame(merge(fig1.boro,fig1.wheat,by="year"))
fee<-as.data.frame(merge(foo,foo1,by="year"))

tryCatch({
fee<-melt(fee,id.vars="year")

fee<-subset(fee,select=c("variable","value"))
print(fee)
fee$value<-fee$value/10
fee$value<-round(fee$value,2)

plot.new()
plotTitle<-paste(format(as.Date(startdate),"%Y"),"/",format(as.Date(enddate),"%y"), " (Target)",sep="")
par(bg="#CADF7B")
color<-c("#C05057","#4F81BD","#8064A2","#E46C0A")

pie(fee$value,labels=fee$value,col=color,cex=3)
mtext(plotTitle,side=1,adj=1.0,cex=3)
legend(x="topright",legend=fee$variable,cex=1.7,fill=color,bty="n")

dev.off()

},error=function(e){
print("an error occurred")
print(e)
dev.off()
createNoDataImage("quarterlychart2_")
})

#plot(p)
#end of fig 2%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#Figure 3 graph production%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
print("chart3--------------------------")


fig2.rice<-getAverageDataValues(1000,2,startdateprev,enddateprev)
fig2.rice$month<-month(fig2.rice$startdate)
tempName<-paste("Rice",(year(reportDate)-1),sep="")
colnames(fig2.rice)[2]<-tempName
fig2.rice<-subset(fig2.rice,select=c("month",tempName))

fig22.rice<-getAverageDataValues(1000,2,startdatecurr,enddatecurr)
fig22.rice$month<-month(fig22.rice$startdate)
tempName<-paste("Rice",(year(reportDate)),sep="")
colnames(fig22.rice)[2]<-tempName
fig22.rice<-subset(fig22.rice,select=c("month",tempName))


fig2.wheat<-getAverageDataValues(1001,2,startdateprev,enddateprev)
fig2.wheat$month<-month(fig2.wheat$startdate)
tempName<-paste("Wheat",(year(reportDate)-1),sep="")
colnames(fig2.wheat)[2]<-tempName
fig2.wheat<-subset(fig2.wheat,select=c("month",tempName))

fig22.wheat<-getAverageDataValues(1001,2,startdatecurr,enddatecurr)
fig22.wheat$month<-month(fig22.wheat$startdate)
tempName<-paste("Wheat",(year(reportDate)),sep="")
colnames(fig22.wheat)[2]<-tempName
fig22.wheat<-subset(fig22.wheat,select=c("month",tempName))


foo<-as.data.frame(merge(fig2.rice,fig22.rice,by="month",all=TRUE))
foo1<-as.data.frame(merge(fig2.wheat,fig22.wheat,by="month",all=TRUE))


foo3<-as.data.frame(merge(foo,foo1,all=TRUE))
foo3<-melt(foo3,id.vars="month")
foo3$value<-foo3$value*100
p<-qplot(data=foo3,factor(month,label=month.abb),value,color=variable,geom=c("point","line"),lwd=I(3),group=variable,main=paste("National wholesale price, ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+labs(x = "Month", y = "Taka per quintal")
p<-commonGraphAttributes(p)

ggsave(
  paste(targetPath,"quarterlychart3_",quarter_period,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
) 
#End Figure 3%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#Figure 4 graph production%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

fig2.rice<-getAverageDataValues(1003,2,startdateprev,enddateprev)
fig2.rice$month<-month(fig2.rice$startdate)
tempName<-paste("Rice",(year(reportDate)-1),sep="")
colnames(fig2.rice)[2]<-tempName
fig2.rice<-subset(fig2.rice,select=c("month",tempName))

fig22.rice<-getAverageDataValues(1003,2,startdatecurr,enddatecurr)
fig22.rice$month<-month(fig22.rice$startdate)
tempName<-paste("Rice",(year(reportDate)),sep="")
colnames(fig22.rice)[2]<-tempName
fig22.rice<-subset(fig22.rice,select=c("month",tempName))
fig2.Atta<-getAverageDataValues(1005,2,startdateprev,enddateprev)
fig2.Atta$month<-month(fig2.Atta$startdate)
tempName<-paste("Atta",(year(reportDate)-1),sep="")
colnames(fig2.Atta)[2]<-tempName
fig2.Atta<-subset(fig2.Atta,select=c("month",tempName))
fig22.Atta<-getAverageDataValues(1005,2,startdatecurr,enddatecurr)
fig22.Atta$month<-month(fig22.Atta$startdate)
tempName<-paste("Atta",(year(reportDate)),sep="")
colnames(fig22.Atta)[2]<-tempName
fig22.Atta<-subset(fig22.Atta,select=c("month",tempName))
foo<-as.data.frame(merge(fig2.rice,fig22.rice,by="month",all=TRUE))
foo1<-as.data.frame(merge(fig2.Atta,fig22.Atta,by="month",all=TRUE))

foo3<-as.data.frame(merge(foo,foo1,all=TRUE))
foo3<-melt(foo3,id.vars="month")

foo3$value<-foo3$value
p<-qplot(data=foo3,factor(month,label=month.abb),value,color=variable,geom=c("point","line"),lwd=I(3),group=variable,main=paste("National retail price, ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+labs(x = "Month", y = "Taka per Kg")
p<-commonGraphAttributes(p)

ggsave(
  paste(targetPath,"quarterlychart4_",quarter_period,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
) 
#End Figure 4 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



#Fig 5 graph production%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
print("chart5-----------------------------------------")

startdate<-as.Date(enddatecurr)-years(1)
enddate<-enddatecurr
fig5.retail<-getAverageDataValuesAsc(1003,2,as.Date(enddatecurr)-years(1),enddatecurr)
fig5.retail$month<-month(fig5.retail$startdate)
tempName<-"Retail Price"
print(tempName)
colnames(fig5.retail)[2]<-tempName
fig5.retail<-subset(fig5.retail,select=c("month",tempName))
if(length(fig5.retail$month)>12){
fig5.retail<-fig5.retail[-1,]
}
tempenddate<-as.Date(paste(year(enddate),"-",month(enddate),"-25",sep=""))
tempstartdate<-as.Date(paste(year(startdate),"-",month(startdate),"-01",sep=""))
tempstartdate<-as.Date(tempstartdate)+months(1)
figDates<-as.data.frame(seq(as.Date(tempstartdate), as.Date(tempenddate), "months"))
colnames(figDates)<-c("dates")
figDates$month<-month(figDates$dates)
print(figDates)
fig5.retail<-merge(fig5.retail,figDates,by="month",all.y=TRUE,sort=F)
#fig5.retail<-subset(fig5.retail,select=c("month",tempName))
print(fig5.retail)
fig5TempName<-tempName


fig5.wholesale<-getAverageDataValuesAsc(1000,2,as.Date(enddatecurr)-years(1),enddatecurr)
print(fig5.wholesale)
fig5.wholesale$month<-month(fig5.wholesale$startdate)
tempName<-"Wholesale price"
#print(tempName)
colnames(fig5.wholesale)[2]<-tempName
fig5.wholesale<-subset(fig5.wholesale,select=c("month",tempName))
if(length(fig5.wholesale$month)>12){
fig5.wholesale<-fig5.wholesale[-1,]
}
fig5.wholesale<-merge(fig5.wholesale,figDates,by="month",all.y=TRUE,sort=F)
fig5.wholesale<-subset(fig5.wholesale,select=c("month",tempName))

print(fig5.wholesale)
print("qasqwe")
foo<-as.data.frame(merge(fig5.retail,fig5.wholesale,sort=F,by="month",all=TRUE))
print(foo)
margin<-((foo[2]-foo[4])/foo[4])*100
marginmonth<-foo[1]

colnames(margin)<-"Percentage margin"
marginName<-"Percentage margin"
print("asd")
margin<-cbind(marginmonth,margin)
foo1<-as.data.frame(merge(foo,margin,sort=F))

#foo$margin<-margin
print(foo1)
#print("qwwwwwwww")
fee<-melt(foo1,id.vars=c("month","dates"))
print("qwwwwwwww")
fee$monthNames<-month.abb[fee$month]

print(fee$monthNames)
lbl<-round(fee$value,1)
lblnumeric<-lbl
print((length(lbl)-length(margin$month)))
lbl[1:(length(lbl)-length(margin$month))]=""

print(fee)

tryCatch({
p<-ggplot(data=fee,aes(y=value,x=dates,group=variable,color=variable))
p<-p+geom_line(subset=.(variable==fig5TempName),lwd=1.2,show_guide=TRUE)
p<-p+geom_point(subset=.(variable==fig5TempName),show_guide=FALSE)
p<-p+geom_line(subset=.(variable==tempName),lwd=1.2,show_guide=FALSE)
p<-p+geom_point(subset=.(variable==tempName),show_guide=FALSE)
p<-p+geom_bar(aes(width=15),subset=.(variable==marginName),stat="identity",fill="#E199CC",show_guide=FALSE)
p<-p+geom_text(aes(label=lbl,y=(lblnumeric+2.5)),show_guide=FALSE,size=8,color="black")
p<-p+ scale_color_manual(values=c("#E199CC","#C05057","#4F81BD"))
p<-p+labs(x = "Months", y = "Taka per Kg",title=paste("Fig 4 Retail vs. wholesale Rice prices and margin(%) in Dhaka city: ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+scale_x_date(labels = date_format("%b-%y"),breaks=date_breaks("1 month"))
p<-commonGraphAttributes(p)


ggsave(
  paste(targetPath,"quarterlychart5_",quarter_period,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
) 
} 
,error=function(e){
print("an error occurred")


createNoDataImage("quarterlychart5_")
})

#End of Fig 5 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#Fig 6 graph production %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
print("chart6-----------------------------------------")

startdate<-as.Date(enddatecurr)-years(1)
enddate<-enddatecurr

fig5.retail<-getAverageDataValuesAsc(1005,2,as.Date(enddatecurr)-years(1),enddatecurr)
fig5.retail$month<-month(fig5.retail$startdate)
tempName<-"Retail Price"
print(tempName)
colnames(fig5.retail)[2]<-tempName
fig5.retail<-subset(fig5.retail,select=c("month",tempName))
if(length(fig5.retail$month)>12){
fig5.retail<-fig5.retail[-1,]
}
tempenddate<-as.Date(paste(year(enddate),"-",month(enddate),"-25",sep=""))
tempstartdate<-as.Date(paste(year(startdate),"-",month(startdate),"-01",sep=""))
tempstartdate<-as.Date(tempstartdate)+months(1)
figDates<-as.data.frame(seq(as.Date(tempstartdate), as.Date(tempenddate), "months"))
colnames(figDates)<-c("dates")
figDates$month<-month(figDates$dates)
print(figDates)
fig5.retail<-merge(fig5.retail,figDates,by="month",all.y=TRUE,sort=F)
#fig5.retail<-subset(fig5.retail,select=c("month",tempName))
print(fig5.retail)
fig5TempName<-tempName


fig5.wholesale<-getAverageDataValuesAsc(1002,2,as.Date(enddatecurr)-years(1),enddatecurr)
print(fig5.wholesale)
fig5.wholesale$month<-month(fig5.wholesale$startdate)
tempName<-"Wholesale price"
#print(tempName)
colnames(fig5.wholesale)[2]<-tempName
fig5.wholesale<-subset(fig5.wholesale,select=c("month",tempName))
if(length(fig5.wholesale$month)>12){
fig5.wholesale<-fig5.wholesale[-1,]
}
fig5.wholesale<-merge(fig5.wholesale,figDates,by="month",all.y=TRUE,sort=F)
fig5.wholesale<-subset(fig5.wholesale,select=c("month",tempName))

print(fig5.wholesale)
print("qasqwe")
foo<-as.data.frame(merge(fig5.retail,fig5.wholesale,sort=F,by="month",all=TRUE))
print(foo)
margin<-((foo[2]-foo[4])/foo[4])*100
marginmonth<-foo[1]

colnames(margin)<-"Percentage margin"
marginName<-"Percentage margin"
print("asd")
margin<-cbind(marginmonth,margin)
foo1<-as.data.frame(merge(foo,margin,sort=F))

#foo$margin<-margin
print(foo1)
#print("qwwwwwwww")
fee<-melt(foo1,id.vars=c("month","dates"))
print("qwwwwwwww")
fee$monthNames<-month.abb[fee$month]

print(fee$monthNames)
lbl<-round(fee$value,1)
lblnumeric<-lbl
print((length(lbl)-length(margin$month)))
lbl[1:(length(lbl)-length(margin$month))]=""
print(fee)
tryCatch({
p<-ggplot(data=fee,aes(y=value,x=dates,group=variable,color=variable))
p<-p+geom_line(subset=.(variable==fig5TempName),lwd=1.2,show_guide=TRUE)
p<-p+geom_point(subset=.(variable==fig5TempName),show_guide=FALSE)
p<-p+geom_line(subset=.(variable==tempName),lwd=1.2,show_guide=FALSE)
p<-p+geom_point(subset=.(variable==tempName),show_guide=FALSE)
p<-p+geom_bar(aes(width=15),subset=.(variable==marginName),stat="identity",fill="#E199CC",show_guide=FALSE)
p<-p+geom_text(aes(label=lbl,y=(lblnumeric+2.5)),show_guide=FALSE,size=8,color="black")
p<-p+ scale_color_manual(values=c("#E199CC","#C05057","#4F81BD"))
p<-p+labs(x = "Months", y = "Taka per Kg",title=paste("Fig 5 Retail vs. wholesale Atta prices and margin(%) in Dhaka city: ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+scale_x_date(labels = date_format("%b-%y"),breaks=date_breaks("1 month"))
p<-commonGraphAttributes(p)

ggsave(
  paste(targetPath,"quarterlychart6_",quarter_period,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
)
} 
,error=function(e){
print("an error occurred")

createNoDataImage("quarterlychart6_")
})

#End of Fig 6 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



#Fig 7 graph production %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
print("chart7----------------------------")


enddate=enddatecurr
startdate=as.Date(enddate-months(4))
tempdate<-paste(year(startdate),"-",month(startdate),"-01",sep="")
#print(tempdate)
startdate<-tempdate
tryCatch({
print(startdate)
print(enddate)
retailPricedataelementid=1000
cpidataelementid=983
fig6.retailPrice<-getAverageDataValues(retailPricedataelementid,2,startdate,enddate)
print(fig6.retailPrice)
fig6.cpi<-getAverageDataValues(983,2,startdate,enddate)
retailPrices<-fig6.retailPrice$value
cpiPrices<-fig6.cpi$value
colnames(fig6.retailPrice)<-c("month","Market Price")
fig6.retailPrice$month<-month(fig6.retailPrice$month)
print(fig6.retailPrice)
print(fig6.cpi)

currentMonthCPI<-cpiPrices[1]
realPrices<-(retailPrices*(currentMonthCPI))
print(realPrices)
realPrices<-realPrices/cpiPrices
print(realPrices)

currMonthPrice<-retailPrices[1]
CPIDE=983
print(currMonthPrice)

seasonalFactor<-getSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate))
cumulativeSeasonalFactor<-get12MonthCumulativeSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate)+months(3))
normalPastPriceSeasonalFactor<-getNormalPastPriceSeasonalFactors(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate))
print(normalPastPriceSeasonalFactor)
inBetweenMonths<-month(seq(as.Date(startdate), as.Date(enddate), "months"))
inBetweenMonths<-paste(inBetweenMonths,collapse=",")
print(inBetweenMonths)

fig6.retailPricesof4Years<-getAvgFourYearPrices(retailPricedataelementid,2,CPIDE,currentMonthCPI,enddate,inBetweenMonths)
print(fig6.retailPricesof4Years)

retailPricesof4Years<-fig6.retailPricesof4Years$value
print("retail price of 4 years")
print(retailPricesof4Years)
averageRealPrices<-retailPricesof4Years*(cpiPrices/currentMonthCPI)
print(currentMonthCPI)
print(cpiPrices)
print("--------------")

print(averageRealPrices*normalPastPriceSeasonalFactor)
print(seasonalFactor)
averageRealPrices<-(averageRealPrices*normalPastPriceSeasonalFactor*12)/cumulativeSeasonalFactor

print(averageRealPrices)

temp<-cpiPrices/currentMonthCPI
print(temp)
normalPastPricesMonthly<-averageRealPrices
tempMonth<-fig6.retailPrice$month
normalPastPricesMonthly<-cbind(tempMonth,normalPastPricesMonthly)
colnames(normalPastPricesMonthly)<-c("month","Normal Past Price")
print(normalPastPricesMonthly)
print(fig6.retailPrice)
foo<-as.data.frame(merge(normalPastPricesMonthly,fig6.retailPrice,all.y=TRUE,all.x=TRUE))
print(foo)
inflationFactor<-cpiPrices[1]

pastYearCPI<-getPastYearCPI(983,2,as.Date(enddate))
pastYearCPI<-pastYearCPI$value

print(inflationFactor)
print(pastYearCPI)
inflationFactorLow<-(inflationFactor/pastYearCPI)^(1/12)
inflationFactorHigh<-(inflationFactor/pastYearCPI)^(1/4)
print(inflationFactor/pastYearCPI)
print(inflationFactor)
inflationFactorMed<-(inflationFactor/pastYearCPI)^(1/6)
print("111111111")	
#print(inflationFactor)
#print(currMonthPrice)

seasonalFactor<-seasonalFactor*12/cumulativeSeasonalFactor

seasonalFactorLow<-getSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate)+months(1))
seasonalFactorLow<-seasonalFactorLow*12/cumulativeSeasonalFactor
seasonalFactorLow<-seasonalFactorLow/seasonalFactor

seasonalFactorMed<-getSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate)+months(2))
seasonalFactorMed<-seasonalFactorMed*12/cumulativeSeasonalFactor
seasonalFactorMed<-seasonalFactorMed/seasonalFactor

seasonalFactorHigh<-getSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate)+months(3))
seasonalFactorHigh<-seasonalFactorHigh*12/cumulativeSeasonalFactor
seasonalFactorHigh<-seasonalFactorHigh/seasonalFactor

benchMarkPriceLow<-c(seasonalFactorLow*currMonthPrice*inflationFactorLow*(1.0-.04),seasonalFactorMed*currMonthPrice*inflationFactorMed*(1.0-.05),seasonalFactorHigh*currMonthPrice*inflationFactorHigh*(1.0-.06))

#print(benchMarkPriceLow)
benchMarkPriceMed<-c((seasonalFactorLow)*currMonthPrice*inflationFactorLow,seasonalFactorMed*currMonthPrice*inflationFactorMed,seasonalFactorHigh*currMonthPrice*inflationFactorHigh)
print(seasonalFactor)
print(seasonalFactorMed)
benchMarkPriceHigh<-c(seasonalFactorLow*currMonthPrice*inflationFactorLow*(1.0+.04),seasonalFactorMed*currMonthPrice*inflationFactorMed*(1.0+.05),seasonalFactorHigh*currMonthPrice*inflationFactorHigh*(1+.06))
print(enddate)

tempenddate<-as.Date(paste(year(enddate),"-",month(enddate),"-25",sep=""))
futureMonths<-month(seq(as.Date(tempenddate)+months(1), as.Date(tempenddate)+months(3), "months"))

myprint("future months = ",futureMonths)
monthNames<-(c(rev(fig6.retailPrice$month),futureMonths))
myprint("$$$$$monthNames = ",monthNames)
allMonths<-c(1:12)
monthNamesNumeric<-monthNames
#print(monthNames)
monthNames1<-month.abb[monthNames]
print(monthNames1)
#print(fig6.retailPrice$month)

#print(futureMonths)
#print(benchMarkPriceLow)
benchMarkPriceLow<-cbind(futureMonths,benchMarkPriceLow)
benchMarkPriceMed<-cbind(futureMonths,benchMarkPriceMed)
benchMarkPriceHigh<-cbind(futureMonths,benchMarkPriceHigh)
print(benchMarkPriceLow)

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
fee$monthNames<-month.abb[fee$month]
print(fee$monthNames)
fee$value<-fee$value*100
print(fee)
lbl<-round(fee$value)
p<-qplot(factor(monthNames),value,color=variable,data=fee,geom=c("point","line"),group=variable,,lwd=I(1.5),main=paste("Fig 6 Domestic Rice price forecast, ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+labs(x = "", y = "Taka per quintal")
p<-p+geom_text(aes(label=lbl,y=lbl-lbl/100),show_guide=FALSE)
p<-p+scale_x_discrete(labels=monthNames1,limits=monthNames1)
p<-commonGraphAttributes(p)
p<-p+theme(legend.text = element_text(size = 13,hjust=1,vjust=.5))
ggsave(
  paste(targetPath,"quarterlychart7_",quarter_period,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
) 
} 
         ,error=function(e){
           print("an error occurred")
           
           createNoDataImage("quarterlychart7_")
})
#End of fig 7 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%




#Fig 8 graph production %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
print("chart8-----------------------------------------")


enddate=enddatecurr
startdate=as.Date(enddate-months(4))
tempdate<-paste(year(startdate),"-",month(startdate),"-01",sep="")
#print(tempdate)
startdate<-tempdate
tryCatch({
print(startdate)
print(enddate)
retailPricedataelementid=1001
CPIDE=983
fig6.retailPrice<-getAverageDataValues(retailPricedataelementid,2,startdate,enddate)
print(fig6.retailPrice)
fig6.cpi<-getAverageDataValues(CPIDE,2,startdate,enddate)
retailPrices<-fig6.retailPrice$value
cpiPrices<-fig6.cpi$value
colnames(fig6.retailPrice)<-c("month","Market Price")
fig6.retailPrice$month<-month(fig6.retailPrice$month)
print(fig6.retailPrice)
print(fig6.cpi)

currentMonthCPI<-cpiPrices[1]
realPrices<-(retailPrices*(currentMonthCPI))
print(realPrices)
realPrices<-realPrices/cpiPrices
print(realPrices)

currMonthPrice<-retailPrices[1]

print(currMonthPrice)


seasonalFactor<-getSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate))
cumulativeSeasonalFactor<-get12MonthCumulativeSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate)+months(3))
print(cumulativeSeasonalFactor)
normalPastPriceSeasonalFactor<-getNormalPastPriceSeasonalFactors(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate))

inBetweenMonths<-month(seq(as.Date(startdate), as.Date(enddate), "months"))
inBetweenMonths<-paste(inBetweenMonths,collapse=",")
print(inBetweenMonths)

fig6.retailPricesof4Years<-getAvgFourYearPrices(retailPricedataelementid,2,CPIDE,currentMonthCPI,enddate,inBetweenMonths)
print(fig6.retailPricesof4Years)

retailPricesof4Years<-fig6.retailPricesof4Years$value
print("retail price of 4 years")
print(retailPricesof4Years)
averageRealPrices<-retailPricesof4Years*(cpiPrices/currentMonthCPI)
print(currentMonthCPI)
print(cpiPrices)
print("--------------")

print(averageRealPrices)
print(seasonalFactor)
averageRealPrices<-(averageRealPrices*normalPastPriceSeasonalFactor*12)/cumulativeSeasonalFactor

print(averageRealPrices)

temp<-cpiPrices/currentMonthCPI
print(temp)
normalPastPricesMonthly<-averageRealPrices
tempMonth<-fig6.retailPrice$month
normalPastPricesMonthly<-cbind(tempMonth,normalPastPricesMonthly)
colnames(normalPastPricesMonthly)<-c("month","Normal Past Price")
print(normalPastPricesMonthly)
print(fig6.retailPrice)
foo<-as.data.frame(merge(normalPastPricesMonthly,fig6.retailPrice,all.y=TRUE,all.x=TRUE))
print(foo)
inflationFactor<-cpiPrices[1]

pastYearCPI<-getPastYearCPI(983,2,as.Date(enddate))
pastYearCPI<-pastYearCPI$value

print(inflationFactor)
print(pastYearCPI)
inflationFactorLow<-(inflationFactor/pastYearCPI)^(1/12)
inflationFactorHigh<-(inflationFactor/pastYearCPI)^(1/4)
print(inflationFactor/pastYearCPI)
print(inflationFactor)
inflationFactorMed<-(inflationFactor/pastYearCPI)^(1/6)
print("111111111")	
#print(inflationFactor)
#print(currMonthPrice)

seasonalFactor<-seasonalFactor*12/cumulativeSeasonalFactor

seasonalFactorLow<-getSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate)+months(1))
seasonalFactorLow<-seasonalFactorLow*12/cumulativeSeasonalFactor
seasonalFactorLow<-seasonalFactorLow/seasonalFactor

seasonalFactorMed<-getSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate)+months(2))
seasonalFactorMed<-seasonalFactorMed*12/cumulativeSeasonalFactor
seasonalFactorMed<-seasonalFactorMed/seasonalFactor

seasonalFactorHigh<-getSeasonalFactor(currMonthPrice,retailPricedataelementid,CPIDE,2,as.Date(enddate)+months(3))
seasonalFactorHigh<-seasonalFactorHigh*12/cumulativeSeasonalFactor
seasonalFactorHigh<-seasonalFactorHigh/seasonalFactor

benchMarkPriceLow<-c(seasonalFactorLow*currMonthPrice*inflationFactorLow*(1.0-.04),seasonalFactorMed*currMonthPrice*inflationFactorMed*(1.0-.05),seasonalFactorHigh*currMonthPrice*inflationFactorHigh*(1.0-.06))

#print(benchMarkPriceLow)
benchMarkPriceMed<-c((seasonalFactorLow)*currMonthPrice*inflationFactorLow,seasonalFactorMed*currMonthPrice*inflationFactorMed,seasonalFactorHigh*currMonthPrice*inflationFactorHigh)
print(seasonalFactor)
print(seasonalFactorMed)
benchMarkPriceHigh<-c(seasonalFactorLow*currMonthPrice*inflationFactorLow*(1.0+.04),seasonalFactorMed*currMonthPrice*inflationFactorMed*(1.0+.05),seasonalFactorHigh*currMonthPrice*inflationFactorHigh*(1+.06))
print(enddate)

tempenddate<-as.Date(paste(year(enddate),"-",month(enddate),"-25",sep=""))
futureMonths<-month(seq(as.Date(tempenddate)+months(1), as.Date(tempenddate)+months(3), "months"))

myprint("future months = ",futureMonths)
monthNames<-(c(rev(fig6.retailPrice$month),futureMonths))
myprint("$$$$$monthNames = ",monthNames)
allMonths<-c(1:12)
monthNamesNumeric<-monthNames
#print(monthNames)
monthNames1<-month.abb[monthNames]
print(monthNames1)
#print(fig6.retailPrice$month)

#print(futureMonths)
#print(benchMarkPriceLow)
benchMarkPriceLow<-cbind(futureMonths,benchMarkPriceLow)
benchMarkPriceMed<-cbind(futureMonths,benchMarkPriceMed)
benchMarkPriceHigh<-cbind(futureMonths,benchMarkPriceHigh)
print(benchMarkPriceLow)

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
fee$monthNames<-month.abb[fee$month]
print(fee$monthNames)
fee$value<-fee$value*100
print(fee)
lbl<-round(fee$value)
p<-qplot(factor(monthNames),value,color=variable,data=fee,geom=c("point","line"),group=variable,,lwd=I(1.5),main=paste("Fig 7 Domestic Wheat price forecast, ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+labs(x = "", y = "Taka per quintal")
p<-p+geom_text(aes(label=lbl,y=lbl-lbl/100),show_guide=FALSE)
p<-p+scale_x_discrete(labels=monthNames1,limits=monthNames1)
p<-commonGraphAttributes(p)
p<-p+theme(legend.text = element_text(size = 13,hjust=1,vjust=.5))
ggsave(
  paste(targetPath,"quarterlychart8_",quarter_period,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
) 
} 
         ,error=function(e){
           print("an error occurred")
           
           createNoDataImage("quarterlychart8_")
})
#End of fig 8 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#Fig9 graph production%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#Constants
LCRiceDataelementValueID=853
LCRiceDataelementidQuantityID=358
thai5Dataelementid=329
sourceid="1,2"


enddate=enddatecurr
startdate=as.Date(enddate)-years(2)
print(startdate)
print(enddate)
tryCatch({
figDates<-as.data.frame(seq(as.Date(startdate), as.Date(enddate), "months"))
colnames(figDates)<-c("dates")

figDates$yearmonth<-paste(year(figDates$dates),month(figDates$dates))
fig9.data11<-getAverageDataValuesAsc(LCRiceDataelementValueID,sourceid,startdate,enddate)
fig9.data12<-getAverageDataValuesAsc(LCRiceDataelementidQuantityID,sourceid,startdate,enddate)

print(fig9.data11)

colnames(fig9.data12)[2] <- "quantity"

print(fig9.data12)

fig9.data13<-merge(fig9.data11,fig9.data12)
print(fig9.data13)
fig9.data1<-(data.frame((fig9.data13)[1],((fig9.data13)[2]/(fig9.data13)[3])))
fig9.data1 <- within(fig9.data1, value <- value * 10)
fig9.data1$yearmonth<-paste(year(fig9.data1$startdate),month(fig9.data1$startdate))
fig9.data1<-subset(fig9.data1,select=c("yearmonth","value"))
fig9.data1<-merge(fig9.data1,figDates,by="yearmonth",all.y=TRUE)
fig9.data1<-subset(fig9.data1,select=c("dates","value"))
colnames(fig9.data1)<-c("dates","LC Settled")

print(fig9.data1)

fig9.data2<-getAverageDataValuesAsc(thai5Dataelementid,sourceid,startdate,enddate)
fig9.data2$yearmonth<-paste(year(fig9.data2$startdate),month(fig9.data2$startdate))

fig9.data2<-subset(fig9.data2,select=c("yearmonth","value"))
fig9.data2<-merge(fig9.data2,figDates,by="yearmonth",all.y=TRUE)
fig9.data2<-subset(fig9.data2,select=c("dates","value"))
colnames(fig9.data2)<-c("dates","Thai5% paraboiled")

print(fig9.data2)

foo<-as.data.frame(merge(fig9.data1,fig9.data2,by=c("dates"),all=TRUE,sort=F))
#print(foo)

fee<-melt(foo,id.vars=c("dates"))
print(fee)
print(factor(fee$dates))
p<- ggplot(fee)
p<-p+geom_line( aes(x=dates,y=value, colour=variable),size=2)
p<-p+scale_x_date(labels = date_format("%b-%y"),breaks=date_breaks("1 month"))
p<-p+labs(x = "", y = "USD/MT",title="Fig 8 International rice price")
p<-commonGraphAttributes(p)

ggsave(
  paste(targetPath,"quarterlychart9_",quarter_period,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
)
} 
,error=function(e){
print("an error occurred")

createNoDataImage("quarterlychart9_")
}) 
#End of fig9 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


#Fig10%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
myprint("Fig 10 chart generation.........")

#Constants
SettledDataelementid=855
SettledDataelementidQuantity = 361
wheatDataelementid=356
sourceid="1,2"

enddate=enddatecurr
startdate=as.Date(enddate)-years(2)
print(startdate)
print(enddate)
tryCatch({
figDates<-as.data.frame(seq(as.Date(startdate), as.Date(enddate), "months"))
colnames(figDates)<-c("dates")

figDates$yearmonth<-paste(year(figDates$dates),month(figDates$dates))
print(figDates)
fig10.data11<-getAverageDataValuesAsc(SettledDataelementid,sourceid,startdate,enddate)
fig10.data12<-getAverageDataValuesAsc(SettledDataelementidQuantity,sourceid,startdate,enddate)

print(fig10.data11)

colnames(fig10.data12)[2] <- "quantity"

print(fig10.data12)

fig10.data13<-merge(fig10.data11,fig10.data12)
print(fig10.data13)
fig10.data1<-(data.frame((fig10.data13)[1],((fig10.data13)[2]/(fig10.data13)[3])))
fig10.data1 <- within(fig10.data1, value <- value * 10)
print("fig10-----------data1-----------")
fig10.data1$yearmonth<-paste(year(fig10.data1$startdate),month(fig10.data1$startdate))
fig10.data1<-subset(fig10.data1,select=c("yearmonth","value"))
fig10.data1<-merge(fig10.data1,figDates,by="yearmonth",all.y=TRUE)
fig10.data1<-subset(fig10.data1,select=c("dates","value"))
colnames(fig10.data1)<-c("dates","LC Settled")

print(fig10.data1)

fig10.data2<-getAverageDataValuesAsc(wheatDataelementid,sourceid,startdate,enddate)                                                                                  
fig10.data2$yearmonth<-paste(year(fig10.data2$startdate),month(fig10.data2$startdate))

fig10.data2<-subset(fig10.data2,select=c("yearmonth","value"))
fig10.data2<-merge(fig10.data2,figDates,by="yearmonth",all.y=TRUE)
fig10.data2<-subset(fig10.data2,select=c("dates","value"))
colnames(fig10.data2)<-c("dates","wheat (soft red)")                    

print(fig10.data2)

foo<-as.data.frame(merge(fig10.data1,fig10.data2,by=c("dates"),all=TRUE,sort=F))
#print(foo)

fee<-melt(foo,id.vars=c("dates"))
print(fee)
print(factor(fee$dates))
p<- ggplot(fee)
p<-p+geom_line( aes(x=dates,y=value, colour=variable),size=2)
p<-p+scale_x_date(labels = date_format("%b-%y"),breaks=date_breaks("1 month"))
p<-p+labs(x = "", y = "USD/MT",title="Fig 9 International wheat price")
p<-commonGraphAttributes(p)

ggsave(
  paste(targetPath,"quarterlychart10_",quarter_period,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
) 
} 
,error=function(e){
print("an error occurred")

createNoDataImage("quarterlychart10_")
})
#End of Fig10 %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
