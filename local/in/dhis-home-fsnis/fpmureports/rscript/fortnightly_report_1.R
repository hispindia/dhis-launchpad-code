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
# Author: Jason P. Pickering

require(RODBC)
require(reshape)
require(ggplot2)
require(scales)
require(grid)
require(zoo)
require(xts)
require(lubridate)
require(graphics)
require(plyr)
#Requires an ODBC channel to be defined with this name
#If we cannot get this connection, abort.
tryCatch({
  channel<<-odbcConnect("fsnis")
  },error= function(ex) { print(ex) ; stop() ; } )
#Get this from the command line
cmd_args<-commandArgs();
#TODO. Validation
reportDate<-as.Date(cmd_args[6])
if( is.na(reportDate) || !is.Date(as.Date(reportDate)) ) { stop("A valid date is required as the first paramater") }
#Set the working directory
#targetPath<-"c:\\users\\jason\\documents\\foodsecurity\\pngs\\"
#targetPath<-"/home/fsnispro/dhis_home/Routput/"
targetPath<-cmd_args[7]

if( is.na(targetPath) ) { stop("A valid output directory is required as the second paramater") }

chartOption<-cmd_args[8]
#Global functions
ReportGraph<-function(x,title_text,ylabel_text){
dateLabels<-GetAxisLabelsRightAligned(x$startdate)
p<- ggplot(x, aes(x=startdate,y=value, colour=variable))
p<-p+geom_step(size=1)
p<-p+scale_x_date(breaks=dateLabels)
p<-p+labs(x = "Date", y = ylabel_text)                                         
p<-p+ theme(legend.position = "bottom",legend.text = element_text(size = 13.5,hjust=.5),legend.direction="horizontal",legend.key.size=unit(.1,"cm"),legend.key.width=unit(1.5,"cm"))
p<-p + theme(axis.text.x  = element_text(size=17,angle=45,colour="black",vjust=1,hjust=1))
p<-p + theme(axis.text.y  = element_text(size=20,angle=0,colour="black"))
p<-p + theme(axis.title.x = element_blank())
p<-p + scale_colour_hue(name  = "" )
p<-p + theme(plot.title = element_blank())
p<-p + theme(plot.margin = unit(rep(0, 4), "lines"))
p<-p + theme(panel.grid.minor = element_blank())
p<-p + theme(panel.grid.major = element_blank())
p<-p + theme(panel.background = element_blank())
p<-p + theme(axis.line = element_line())
p<-p + theme(plot.margin = unit(c(1, 1, 0.5, 0.5), "lines"))
return(p)
}

Fig8Graph<-function(x,title_text,ylabel_text, breakSize){
  dateLabels<-GetAxisLabelsRightAligned(x$startdate)
  p<- ggplot(x, aes(x=startdate,y=value, colour=variable))
  p<-p+geom_step(size=1)
  p<-p+scale_x_date(breaks=date_breaks(breakSize))
  p<-p+labs(x = "Date", y = ylabel_text)                                         
  p<-p+ theme(legend.position = "bottom",legend.text = element_text(size = 13.5,hjust=.5),legend.direction="horizontal",legend.key.size=unit(.1,"cm"),legend.key.width=unit(1.5,"cm"))
  p<-p + theme(axis.text.x  = element_text(size=12,angle=45,colour="black",vjust=1,hjust=1))
  p<-p + theme(axis.text.y  = element_text(size=20,angle=0,colour="black"))
  p<-p + theme(axis.title.x = element_blank())
  p<-p + scale_colour_hue(name  = "" )
  p<-p + theme(plot.title = element_blank())
  p<-p + theme(plot.margin = unit(rep(0, 4), "lines"))
  p<-p + theme(panel.grid.minor = element_blank())
  p<-p + theme(panel.grid.major = element_blank())
  p<-p + theme(panel.background = element_blank())
  p<-p + theme(axis.line = element_line())
  p<-p + theme(plot.margin = unit(c(1, 1, 0.5, 0.5), "lines"))
  return(p)
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
    paste(targetPath,chartName,reportDate,'.png',sep=""),
    p,
    width = 10.3 ,
    height =  4.9,
    units = "in",
    dpi = 300
  ) 
}

#Get data values and average them. Used for data elements with multiple category options
#Convert the data series to a zoo object, and fill in NAs
#according to the "last observed carried forward" method.
GetAverageDataValues<-function(dataelementid,organisationunitid, na.locf=TRUE){
sql<-"SELECT  p.startdate, avg(dv.value)  as value FROM datavalue dv
INNER JOIN period p on dv.periodid = p.periodid
where dv.dataelementid = %dataelementid and dv.sourceid = %organisationunitid
GROUP BY  p.startdate
ORDER BY p.startdate DESC"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
values<-sqlQuery(channel,sql)
foo<-zoo(values$value,values$startdate)
if(na.locf){
tmp<-xts(,seq(start(foo),end(foo),"days"))
foo<-na.locf(merge(tmp,foo))
colnames(foo)<-"value" }
return(foo)
}

#Used to get data values which are entered across multiple districts and must be summed.
GetSumMultiOrgDataValues<-function(dataelementid,startdate,enddate){
sql<-"SELECT   sum(dv.value)  as value FROM datavalue dv
INNER JOIN period p on dv.periodid = p.periodid
where dv.dataelementid = %dataelementid
and p.startdate >= '%startdate'
and p.enddate <= '%enddate'"
sql<-gsub("%startdate",startdate,sql)
sql<-gsub("%enddate",enddate,sql)
sql<-gsub("%dataelementid",dataelementid,sql)
value<-sqlQuery(channel,sql)
ifelse(is.null(value) || is.na(value),0,value)[[1]]
}

GetSumSingleOrgDataValues<-function(dataelementid,organisationunitid,startdate,enddate){
sql<-"SELECT   sum(dv.value)  as value FROM datavalue dv
INNER JOIN period p on dv.periodid = p.periodid
where dv.dataelementid = %dataelementid
and dv.sourceid = %organisationunitid
and p.startdate >= '%startdate'
and p.enddate <= '%enddate'"
sql<-gsub("%startdate",startdate,sql)
sql<-gsub("%enddate",enddate,sql)
sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
value<-sqlQuery(channel,sql)
ifelse(is.null(value) || is.na(value),0,value)[[1]]
}

#Various functions to deal with getting the different periods from the report date
#Weeks are calculated as starting on a Thursday
GetCurrentThursday<-function(reportDate) {
tempDate<-as.Date(reportDate)
tempDate<-tempDate+days(5)-wday(tempDate)
  if (wday(reportDate)<5){
  tempDate<-tempDate-weeks(1)
  }
  return(tempDate)
}

GetThursdayOneFortnightAgo<-function(reportDate) {
tempDate<-as.Date(reportDate)
 
tempDate<-tempDate+days(5)-wday(tempDate)-weeks(2)
 if (wday(reportDate)<5){
  tempDate<-tempDate-weeks(1)
  }
   return(tempDate)
}

GetThursdayOneYearAgo<-function(reportDate) {
tempDate<-as.Date(reportDate)
tempDate<-tempDate+days(5)-wday(tempDate)-years(1)
 if (wday(reportDate)<5){
  tempDate<-tempDate-weeks(1)
  }
   return(tempDate)
}

GetCurrentFiscalYearStartDate<-function(reportDate){
reportDate<-as.Date(reportDate)
fiscalYear<-year(reportDate)
if ( month(reportDate) <= 6 ) {fiscalYear<-year(reportDate) - 1}
as.Date(paste(as.character(fiscalYear),'-07-01',sep=""))
}

previousFriday<-function(reportDate) {
  reportDate<-as.Date(reportDate)
  if(wday(reportDate)>5 || wday(reportDate)<2)
  {
    reportDate-wday(reportDate)+days(6)
  }
  else
  {
    reportDate-wday(reportDate)-days(1) 
  }
}

previousFridayAYearAgo<-function(reportDate) {
  reportDate<-as.Date(reportDate)
  if(wday(reportDate)>5 || wday(reportDate)<2)
  {
    reportDate-wday(reportDate)+days(6)-years(1)
  }
  else
  {
    reportDate-wday(reportDate)-days(1)-years(1) 
  }
}

#Time manipulation functions
ApplyTimeWindow<-function(z,startdate,enddate){window(z, start = startdate, end = enddate)}

#Axis labeling
GetAxisLabelsRightAligned<-function(x,interval="-3 weeks"){
maxDate<-max(x)
minDate<-min(x)
rev(seq(from=maxDate,to=minDate,by=interval))}


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
  paste(targetPath,chartName,reportDate,'.png',sep=""),
  p,
  width = 10.3 ,
  height =  4.9,
  units = "in",
  dpi = 300
) 
}



#Figure 1
#SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market wholesale price

fig1.generate<-function(){

fig1.wholesale<-GetAverageDataValues(197,10)/100.0
#SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
fig1.retail<-GetAverageDataValues(198,10)
startDate<-GetThursdayOneYearAgo(reportDate)
endDate<-GetCurrentThursday(reportDate)
fig1.wholesale<-ApplyTimeWindow(fig1.wholesale,startDate,endDate)
fig1.retail<-ApplyTimeWindow(fig1.retail,startDate,endDate)
#Figure 1 Data processing
fig1.data<-as.data.frame(merge(fig1.retail,fig1.wholesale))
colnames(fig1.data)<-c("Retail","Wholesale")
fig1.data$startdate<-as.Date(rownames(fig1.data))
fig1.data <- melt(fig1.data, id.vars="startdate")
#Figure 1 graph production
fig1<-ReportGraph(fig1.data,"Figure 1. Changes in Coarse Rice (Dhaka)","Taka per kg")

ggsave(
  paste(targetPath,"fortnightlychart1_",reportDate,'.png',sep=""),
  fig1,
  width = 8.26 ,
  height =  4.13,
  units = "in",
  dpi = 300
) }

#End Figure 1

#Figure2

fig2.generate<-function(){
#Wheat Flour Open White market wholesale price daily
fig2.wholesale<-GetAverageDataValues(245,10)/100.0
fig2.retail<-GetAverageDataValues(246,10)
#Figure 2  Data processing
startDate<-GetThursdayOneYearAgo(reportDate)
endDate<-GetCurrentThursday(reportDate)
fig2.wholesale<-ApplyTimeWindow(fig2.wholesale,startDate,endDate)
fig2.retail<-ApplyTimeWindow(fig2.retail,startDate,endDate)
fig2.data<-as.data.frame(merge(fig2.retail,fig2.wholesale))
colnames(fig2.data)<-c("Retail","Wholesale")
fig2.data$startdate<-as.Date(rownames(fig2.data))
fig2.data <- melt(fig2.data, id.vars="startdate")
#Figure 2 Graph production
fig2<-ReportGraph(fig2.data,"Figure 2: Change in price of atta Dhaka city ","Taka per kg")
ggsave(
  paste(targetPath,"fortnightlychart2_",reportDate,'.png',sep=""),
  fig2,
  width = 8.26 ,
  height = 4.13,
  units = "in",
  dpi = 300
)   }
#End Figure2 

#Figure 3
fig3.generate<-function(){
startDate<-GetThursdayOneYearAgo(reportDate)
endDate<-GetCurrentThursday(reportDate)
#Rice wholesale price
#Dhaka City Coarse Rice wholesale DEID: [197 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
#(DEID:363 AVG(CCID 8,9) average of buy and selling price)
fig3.coarserice<-GetAverageDataValues(197,10)
#Get the exchange rate
fig3.xrate<-GetAverageDataValues(363,1)
#Merge these two and calculate the final values
fig3.coarserice<-fig3.coarserice/fig3.xrate*10
fig3.kolkata<-GetAverageDataValues(710,1)
fig3.xrate2<-GetAverageDataValues(711,1)
fig3.kolkata<-fig3.kolkata/fig3.xrate2*10

#Kolkata Rice  Price: data is from website http://fcainfoweb.nic.in/pms/interface3web.aspx 
#Which is INR/QTL need to be converted to USD [Rice Wholesale Kolkata price (New DE) / Exchange rate of INR to USD (DEID: 366 AVG(CCID 8,9)) *10]
#DENAME: Rice wholesale price 5% Thailand weekly DEID: 713
fig3.thai5<-GetAverageDataValues(713,1)
#DENAME: Rice wholesale price 15% Vietnam weekly DEID: 714
fig3.vn15<-GetAverageDataValues(714,1)
#DENAME: Rice wholesale price 5% parboiled Pakistan weekly DEID: 715
fig3.pak5<-GetAverageDataValues(715,1)
#DENAME: Rice wholesale price 5% parboiled India weekly DEID: 716
fig3.ind5<-GetAverageDataValues(716,1)
#Apply Time window on all series
startdate<-previousFridayAYearAgo(reportDate)
enddate<-previousFriday(reportDate)
print(paste("ST:",startdate,"  ","ET:",enddate))
fig3.coarserice<-ApplyTimeWindow(fig3.coarserice,startdate,enddate)
fig3.kolkata<-ApplyTimeWindow(fig3.kolkata,startdate,enddate)
fig3.thai5<-ApplyTimeWindow(fig3.thai5,startdate,enddate)
fig3.vn15<-ApplyTimeWindow(fig3.vn15,startdate,enddate)
fig3.pak5<-ApplyTimeWindow(fig3.pak5,startdate,enddate)
fig3.ind5<-ApplyTimeWindow(fig3.ind5,startdate,enddate)
#Merge everything into a single data frame and rearrange
foo<-NULL
foo<-as.data.frame(merge(fig3.coarserice, fig3.kolkata, fig3.thai5,fig3.vn15,fig3.pak5,fig3.ind5))
foo$startdate<-as.Date(rownames(foo))
#foo<-subset(foo,startdate>=startDate)
#foo<-subset(foo,startdate<=endDate)
colnames(foo)<-c("Dhaka city Wholesale PB",
"Kolkata Wholesale PB","Thai 5% Parboiled",
"Vietnam 15% White","Pakistan 5% Parboiled",
"India 5% Parboiled","startdate")
fig3.data<-melt(foo,id.vars="startdate")
fig3<-ReportGraph(fig3.data,"Rice wholesale price in Dhaka, Kolkata and FOB Prices in relevant international markets",
"USD per MT")
fig3<-fig3 + theme(legend.position = "bottom")
fig3<-fig3 + guides(col = guide_legend(nrow = 2))
#Figure 3 Graph production
ggsave(
  paste(targetPath,"fortnightlychart3_",reportDate,'.png',sep=""),
  fig3,
  width = 8.26 ,
  height = 4.13,
  units = "in",
  dpi = 300
)}


#End Figure 3
#Figure 4
#Data processing
fig4.generate<-function() {
startDate<-GetThursdayOneYearAgo(reportDate)
endDate<-GetCurrentThursday(reportDate)
fig4.xrate<-GetAverageDataValues(363,1)
fig4.dhaka<-GetAverageDataValues(243,10)/fig4.xrate*10
fig4.USSRW<-GetAverageDataValues(356,1)
fig4.ukraine<-GetAverageDataValues(353,1)
fig4.russia<-GetAverageDataValues(355,1)
#Apply Time window on all series
startdate<-previousFridayAYearAgo(reportDate)
enddate<-previousFriday(reportDate)
print(paste("ST:",startdate,"  ","ET:",enddate))
fig4.dhaka<-ApplyTimeWindow(fig4.dhaka,startdate,enddate)
fig4.USSRW<-ApplyTimeWindow(fig4.USSRW,startdate,enddate)
fig4.ukraine<-ApplyTimeWindow(fig4.ukraine,startdate,enddate)
fig4.russia<-ApplyTimeWindow(fig4.russia,startdate,enddate)
foo<-NULL
foo<-as.data.frame(merge(fig4.dhaka, fig4.USSRW, fig4.ukraine,fig4.russia))
foo$startdate<-as.Date(rownames(foo))
colnames(foo)<-c("Dhaka city Wholesale","US SRW, US Gulf Port","Ukraine, deep-sea Port","Russia, deep-sea Port","startdate")
fig4.data<-melt(foo,id.vars="startdate")
fig4<-ReportGraph(fig4.data,"Wheat wholesale price in Dhaka and FOB Prices in relevant international markets","USD per MT")
fig4<-fig4 + theme(legend.position = "bottom")
fig4<-fig4 + guides(col = guide_legend(nrow = 2))
#Figure 3 Graph production
ggsave(
  paste(targetPath,"fortnightlychart4_",reportDate,'.png',sep=""),
  fig4,
  width = 8.26 ,
  height = 4.13,
  units = "in",
  dpi = 300
)             }
#End Fig4

#Figure5
#The data for this is coming from MISM,
#the data element id for Government 
#Rice import is (261+262) for private 
#import the DEID us 263. The data is 
#cummulative as on the reporting
#date which is compared to last
#fortnightly and last year of reporting date
#(Note need to check if paddy need 
#to be included in Rice import which then will be 65% of paddy)

fig5.plot<-function(x){
  #Explicitly define the colors.
  foo.colors<-c("#B7DEE8","#FFC000")
  #We need to get the midpoints of the bars to figure out where to put the text
  x<-ddply(x , .(time.char), transform, pos = cumsum(value) - 0.5*value)
  foo <- ggplot(x, aes(x=time.char,y=value, fill=variable)) + geom_bar(stat="identity", colour="black")
  foo<- foo + scale_x_discrete(limits = rev(x$time.char))
  foo<- foo + scale_fill_manual(values=foo.colors)
  foo<-foo + geom_text(aes(x=time.char,label = value,y=pos),size = 3)
  foo<-foo + theme(legend.position = "bottom")
  foo<-foo + ylab("thousand MT")
  foo<-foo + theme(axis.title.x = element_blank())
  foo<-foo + theme(legend.title=element_blank())
  foo<-foo + theme(panel.grid.minor = element_blank())
  foo<-foo + theme(panel.grid.major = element_blank())
  foo<-foo + theme(panel.background = element_blank())
  foo<-foo + theme(axis.line = element_line())
  foo<-foo + theme(legend.position = "bottom",legend.text = element_text(size = 18,hjust=.5),legend.direction="horizontal",legend.key.size=unit(.1,"cm"),legend.key.width=unit(1.5,"cm"))
  foo<-foo + theme(axis.text.x  = element_text(size=16,angle=15,colour="black",vjust=1,hjust=1))
  foo<-foo + theme(axis.text.y  = element_text(size=17,angle=0,colour="black"))
}
 
fig5.generate<-function(){
current.Public.Rice<-GetSumSingleOrgDataValues(261,2,GetCurrentFiscalYearStartDate(reportDate),GetCurrentThursday(reportDate)) +
GetSumSingleOrgDataValues(262,2,GetCurrentFiscalYearStartDate(reportDate),GetCurrentThursday(reportDate))

current.Private.Rice<-GetSumSingleOrgDataValues(263,2,GetCurrentFiscalYearStartDate(reportDate),GetCurrentThursday(reportDate))

previousFortnight.Public.Rice<-GetSumSingleOrgDataValues(261,2,GetCurrentFiscalYearStartDate(reportDate),GetThursdayOneFortnightAgo(reportDate)) +
GetSumSingleOrgDataValues(262,2,GetCurrentFiscalYearStartDate(reportDate),GetThursdayOneFortnightAgo(reportDate))

previousFortnight.Private.Rice<-GetSumSingleOrgDataValues(263,2,GetCurrentFiscalYearStartDate(reportDate),GetThursdayOneFortnightAgo(reportDate))

previousYear.Public.Rice<-GetSumSingleOrgDataValues(261,2,GetCurrentFiscalYearStartDate(reportDate)-years(1),GetThursdayOneYearAgo(reportDate)) +
GetSumSingleOrgDataValues(262,2,GetCurrentFiscalYearStartDate(reportDate)-years(1),GetThursdayOneYearAgo(reportDate))

previousYear.Private.Rice<-GetSumSingleOrgDataValues(263,2,GetCurrentFiscalYearStartDate(reportDate)-years(1),GetThursdayOneYearAgo(reportDate))

#Construct the data frame
time.char<-list(c(as.character(GetCurrentThursday(reportDate)),as.character(GetThursdayOneFortnightAgo(reportDate)),as.character(GetThursdayOneYearAgo(reportDate))))
Private<-list(c(current.Private.Rice,previousFortnight.Private.Rice,previousYear.Private.Rice))
Government<-list(c(current.Public.Rice,previousFortnight.Public.Rice,previousYear.Public.Rice))

fig5.data<-data.frame(time.char,Private,Government,stringsAsFactors=FALSE)
colnames(fig5.data)<-c("time.char","Private","Government")
#fig5.data$time.char<-paste(fig5.data$time.char)
fig5.data<-melt(fig5.data,id.vars="time.char")
fig5.data$value<-fig5.data$value/1000
fig5.data$value<-round(fig5.data$value,1)
fig5.data<-arrange(fig5.data,time.char,desc(variable))
#Generic graph for figures 5 & 6

fig5<-fig5.plot(fig5.data)
ggsave(
  paste(targetPath,"fortnightlychart5_",reportDate,'.png',sep=""),
  fig5,
  width = 5.2 ,
  height = 3.5,
  units = "in",
  dpi = 300
)          }



#TODO Figure6
#Same as Rice import the data comes from MISM, The Government import is the sum of Aid and cash (269 + 270) and for private wheat import is 271. 
fig6.generate<-function(){
current.Public.Wheat<-GetSumSingleOrgDataValues(269,2,GetCurrentFiscalYearStartDate(reportDate),GetCurrentThursday(reportDate)) +
GetSumSingleOrgDataValues(270,2,GetCurrentFiscalYearStartDate(reportDate),GetCurrentThursday(reportDate))

current.Private.Wheat<-GetSumSingleOrgDataValues(271,2,GetCurrentFiscalYearStartDate(reportDate),GetCurrentThursday(reportDate))

previousFortnight.Public.Wheat<-GetSumSingleOrgDataValues(269,2,GetCurrentFiscalYearStartDate(reportDate),GetThursdayOneFortnightAgo(reportDate)) +
GetSumSingleOrgDataValues(270,2,GetCurrentFiscalYearStartDate(reportDate),GetThursdayOneFortnightAgo(reportDate))

previousFortnight.Private.Wheat<-GetSumSingleOrgDataValues(271,2,GetCurrentFiscalYearStartDate(reportDate),GetThursdayOneFortnightAgo(reportDate))

previousYear.Public.Wheat<-GetSumSingleOrgDataValues(269,2,GetCurrentFiscalYearStartDate(reportDate)-years(1),GetThursdayOneYearAgo(reportDate)) +
GetSumSingleOrgDataValues(270,2,GetCurrentFiscalYearStartDate(reportDate)-years(1),GetThursdayOneYearAgo(reportDate))

previousYear.Private.Wheat<-GetSumSingleOrgDataValues(271,2,GetCurrentFiscalYearStartDate(reportDate)-years(1),GetThursdayOneYearAgo(reportDate))


time.char<-list(c(as.character(GetCurrentThursday(reportDate)),as.character(GetThursdayOneFortnightAgo(reportDate)),as.character(GetThursdayOneYearAgo(reportDate))))
Private<-list(c(current.Private.Wheat,previousFortnight.Private.Wheat,previousYear.Private.Wheat))
Government<-list(c(current.Public.Wheat,previousFortnight.Public.Wheat,previousYear.Public.Wheat))

fig6.data<-data.frame(time.char,Private,Government)
colnames(fig6.data)<-c("time.char","Private","Government")
fig6.data<-melt(fig6.data,id.vars="time.char")
fig6.data$time.char<-paste(fig6.data$time.char)
fig6.data$value<-fig6.data$value/1000
fig6.data$value<-round(fig6.data$value,1)
fig6.data<-arrange(fig6.data,time.char,desc(variable))
#Generate the same graph as figure 6
fig6<-fig5.plot(fig6.data)


ggsave(
  paste(targetPath,"fortnightlychart6_",reportDate,'.png',sep=""),
  fig6,
  width = 5.2 ,
  height = 3.5,
  units = "in",
  dpi = 300
)     }


#Figure7
#The data for this graph is taken from Weekly MISM Report, 
#currently in DHIS2 we collect this data by division wise, 
#we need to aggregate the data at Bangladesh and then produce the chart.
#Note this graph show both rice and wheat data and there is 
#no bifurcation of monetized and non monetized. the data is in Thousand MT. 
#The red line indicate target value. We need to create data elements for these. 

#Field Details
#Rice:
#OMS/FP = (OMS [DEID 370 (CCID 7+6)] + Fair Price [DEID 371 (CCID 7+6)]+
#4th  class emp [DEID 373 (CCID 7+6)]
#+Freedom fighter [DEID 372 (CCID 7+6)])/1000 
#OP = DEID 368 (CCID 7+6)/1000
#LEI = DEID 369 (CCID 7+6)/1000
#EP = DEID 367 (CCID 7+6)/1000
#FFW=DEID 377 (CCID 7+6)/1000
#VGD =DEID 379 (CCID 7+6)/1000
#TR=DEID 378 (CCID 7+6)/1000
#VGF=DEID 380 (CCID 7+6)/1000
#GR=DEID 381 (CCID 7+6)/1000
#Others=[DEID 376 (CCID 7+6)+DEID 382(CCID 7+6)]/1000
#Paddy: Need to ask if we need to add this to rice
#OMS/FP = (OMS [DEID 404 (CCID 7+6)] 
#+ Fair Price [DEID 405 (CCID 7+6)]+ 
#4th  class emp [DEID 407 (CCID 7+6)]
#+Freedom fighter [DEID 406 (CCID 7+6)])/1000 
#OP = DEID 402 (CCID 7+6)/1000
#LEI = DEID 403 (CCID 7+6)/1000
#EP = DEID 401 (CCID 7+6)/1000
#FFW=DEID 411 (CCID 7+6)/1000
#VGD =DEID 413 (CCID 7+6)/1000
#TR=DEID 412 (CCID 7+6)/1000
#VGF=DEID 414 (CCID 7+6)/1000
#GR=DEID 415 (CCID 7+6)/1000
#Others=[DEID 410 (CCID 7+6)+DEID 416(CCID 7+6)]/1000
fig7.generate<-function(){
startdate<-GetCurrentFiscalYearStartDate(reportDate)
enddate<-GetCurrentThursday(reportDate)

fig7.rice.omsfp<-GetSumMultiOrgDataValues(370,startdate,enddate)+
GetSumMultiOrgDataValues(371,startdate,enddate)+
GetSumMultiOrgDataValues(373,startdate,enddate)+
GetSumMultiOrgDataValues(372,startdate,enddate)+
GetSumMultiOrgDataValues(404,startdate,enddate)+
GetSumMultiOrgDataValues(405,startdate,enddate)+
GetSumMultiOrgDataValues(407,startdate,enddate)+
GetSumMultiOrgDataValues(406,startdate,enddate)

fig7.rice.op<-GetSumMultiOrgDataValues(368,startdate,enddate)+GetSumMultiOrgDataValues(402,startdate,enddate)
fig7.rice.lei<-GetSumMultiOrgDataValues(369,startdate,enddate)+GetSumMultiOrgDataValues(403,startdate,enddate)
fig7.rice.ep<-GetSumMultiOrgDataValues(367,startdate,enddate)+GetSumMultiOrgDataValues(401,startdate,enddate)
fig7.rice.ffw<-GetSumMultiOrgDataValues(377,startdate,enddate)+GetSumMultiOrgDataValues(411,startdate,enddate)
fig7.rice.vgd<-GetSumMultiOrgDataValues(379,startdate,enddate)+GetSumMultiOrgDataValues(413,startdate,enddate)
fig7.rice.tr<-GetSumMultiOrgDataValues(378,startdate,enddate)+GetSumMultiOrgDataValues(412,startdate,enddate)
fig7.rice.vgf<-GetSumMultiOrgDataValues(380,startdate,enddate)+GetSumMultiOrgDataValues(414,startdate,enddate)
fig7.rice.gr<-GetSumMultiOrgDataValues(381,startdate,enddate)+GetSumMultiOrgDataValues(415,startdate,enddate)
fig7.rice.others<-GetSumMultiOrgDataValues(376,startdate,enddate)+
GetSumMultiOrgDataValues(382,startdate,enddate)+
GetSumMultiOrgDataValues(410,startdate,enddate)+
GetSumMultiOrgDataValues(416,startdate,enddate)

fig7.rice<-data.frame(commodity=c('Rice'
),variable=c('OMS/FP','OP','LEI','EP','FFW','VGD','TR','VGF','GR','Others'
),value=c(fig7.rice.omsfp,fig7.rice.op,fig7.rice.lei,fig7.rice.ep,fig7.rice.ffw,fig7.rice.vgd,fig7.rice.tr,fig7.rice.vgf,fig7.rice.gr,fig7.rice.others))


#Wheat:
#OMS/FP= (OMS [DEID 386 (CCID 7+6)] + Fair Price [DEID 387 (CCID 7+6)]+ 
#4th  class emp [DEID 389 (CCID 7+6)]+Freedom fighter [DEID 388 (CCID 7+6)])/1000 
#OP=  DEID 384 (CCID 7+6)/1000
#LEI=  DEID 385 (CCID 7+6)/1000
#EP=  DEID 383 (CCID 7+6)/1000
#FFW=  DEID 393 (CCID 7+6)/1000
#VGD=  DEID 395 (CCID 7+6)/1000
#TR=  DEID 394 (CCID 7+6)/1000
#VGF=  DEID 396 (CCID 7+6)/1000
#SF=  DEID 400 (CCID 7+6)/1000
#Others= [DEID 392 (CCID 7+6)+DEID 398(CCID 7+6)]/1000


fig7.wheat.omsfp<-GetSumMultiOrgDataValues(386,startdate,enddate)+
GetSumMultiOrgDataValues(387,startdate,enddate)+
GetSumMultiOrgDataValues(389,startdate,enddate)+
GetSumMultiOrgDataValues(388,startdate,enddate)

fig7.wheat.op<-GetSumMultiOrgDataValues(384,startdate,enddate)
fig7.wheat.lei<-GetSumMultiOrgDataValues(385,startdate,enddate)
fig7.wheat.ep<-GetSumMultiOrgDataValues(383,startdate,enddate)
fig7.wheat.ffw<-GetSumMultiOrgDataValues(393,startdate,enddate)
fig7.wheat.vgd<-GetSumMultiOrgDataValues(395,startdate,enddate)
fig7.wheat.tr<-GetSumMultiOrgDataValues(394,startdate,enddate)
fig7.wheat.vgf<-GetSumMultiOrgDataValues(396,startdate,enddate)
fig7.wheat.sf<-GetSumMultiOrgDataValues(400,startdate,enddate)
fig7.wheat.others<-GetSumMultiOrgDataValues(392,startdate,enddate)+
GetSumMultiOrgDataValues(398,startdate,enddate)

fig7.wheat<-data.frame(commodity=c('Wheat'
),variable=c('OMFSP','OP','LEI','EP','FFW','VGD','TR','VGF','SF','Others'
),value=c(fig7.wheat.omsfp,fig7.wheat.op,fig7.wheat.lei,fig7.wheat.ep,fig7.wheat.ffw,fig7.wheat.vgd,fig7.wheat.tr,fig7.wheat.vgf,fig7.wheat.sf,fig7.wheat.others))
#Combine the wheat and rice into a single data frame
fig7.data<-rbind(fig7.rice,fig7.wheat)
#We need to get the ordering right for the bar chart
fig7.data$variable <- factor(fig7.data$variable, levels=unique(as.character(fig7.data$variable)) )

#Targets
target.deids<-c(741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,759,760)
target.commodity<-c(rep("Rice",10),rep("Wheat",10))
target.variable<-c(as.character(fig7.rice$variable),as.character(fig7.wheat$variable))
#Seed the data frame
fig7.targets.data<-as.data.frame(cbind(target.deids,target.commodity,target.variable))
foo<-data.frame(deid=integer(),name=character(),date=as.Date(character()), value=integer(), stringsAsFactors=FALSE)
for (i in 1:length(fig7.targets.data$target.deids)){
dataelementid<-target.deids[i]
sql<-"SELECT dv.dataelementid ,de.name,p.startdate,dv.value  FROM datavalue dv
INNER JOIN period p on dv.periodid = p.periodid
INNER JOIN dataelement de on dv.dataelementid = de.dataelementid
where dv.dataelementid = %dataelementid
ORDER BY startdate DESC
LIMIT 1"
sql<-gsub("%dataelementid",dataelementid,sql)
values<-sqlQuery(channel,sql)
foo<-rbind(foo,values) }

fig7.targets.data<-merge(fig7.targets.data,foo,by.x='target.deids',by.y='dataelementid',all=T)
fig7.targets.data<-fig7.targets.data[,c(2,3,6)]
colnames(fig7.targets.data)<-c("commodity","variable","target")
fig7.targets.data$variable <- factor(fig7.targets.data$variable, levels=unique(as.character(fig7.targets.data$variable)))

#Combine two data frames
fig7.combined.data<-merge(fig7.targets.data,fig7.data,by.x=c("commodity","variable"),by.y=c("commodity","variable"))
fig7.combined.data$value<-fig7.combined.data$value/1000
fig7.combined.data$acheived<-round(fig7.combined.data$value/fig7.combined.data$target*100,1)

#Figure 7
fig7<-ggplot(fig7.combined.data) 
fig7<-fig7 + geom_bar(aes(x=variable,y=value),fill="#56B4E9", colour="black")
fig7<-fig7 + geom_text(aes(x=variable,y=target, label=target),colour="black",vjust=0,size=6)
fig7<-fig7 + geom_text(aes(x=variable,y=target, label="___") , colour="red",vjust=0,cex=5) 
fig7<-fig7 + facet_grid(. ~ commodity, scale="free")
fig7<-fig7 + theme(panel.grid.minor = element_blank())
fig7<-fig7 + theme(panel.grid.major = element_blank())
fig7<-fig7 + theme(panel.background = element_blank())

fig7<-fig7 + theme(legend.position = "bottom",legend.text = element_text(size = 18,hjust=.5),legend.direction="horizontal",legend.key.size=unit(.1,"cm"),legend.key.width=unit(1.5,"cm"))
fig7<-fig7 + theme(axis.text.x  = element_text(size=17,angle=45,colour="black",vjust=1,hjust=1))
fig7<-fig7 + theme(axis.text.y  = element_text(size=20,angle=0,colour="black"))
fig7<-fig7 + theme(axis.title.x  = element_text(size=15,angle=0,colour="black",face="bold"),axis.title.y  = element_text(size=15,angle=90,colour="black",face="bold"))

fig7<-fig7 + theme(axis.line = element_line())
fig7<-fig7 + xlab("Distribution Channel")
fig7<-fig7 + ylab("Thousand MT")

ggsave(
  paste(targetPath,"fortnightlychart7_",reportDate,'.png',sep=""),
  fig7,
  width = 8 ,
  height = 4,
  units = "in",
  dpi = 300
)               }


#Figure8
fig8.generate<-function()
{
  if (chartOption!=0){
    
    if (chartOption==1){
      achievedDE=255
      contractDE=256
      targetDE=418
      startdateDE=452
      enddateDE=453
    }else if(chartOption==2){
      achievedDE=257
      contractDE=258
      targetDE=417
      startdateDE=450
      enddateDE=451
    }else if (chartOption==3){
      achievedDE=267
      contractDE=268
      targetDE=420
      startdateDE=456
      enddateDE=457
    }
    
    cropAcheived<-GetAverageDataValues(achievedDE,2,na.locf=FALSE)/1000
    cropContracts<-GetAverageDataValues(contractDE,2,na.locf=FALSE)/1000
    
    TargetStartDate.sql<-"SELECT  dv.value  as value FROM datavalue dv
    INNER JOIN period p on dv.periodid = p.periodid
    where dv.dataelementid = %startdateDE
    ORDER BY p.startdate DESC
    LIMIT 1;"
    
    TargetStartDate.sql<-gsub("%startdateDE",startdateDE,TargetStartDate.sql)
    
    TargetStartDate<-sqlQuery(channel,TargetStartDate.sql)
    TargetStartDate<-as.character(TargetStartDate[1,1])
    #print(TargetStartDate)
    TargetEndDate.sql<-"SELECT  dv.value  as value FROM datavalue dv
    INNER JOIN period p on dv.periodid = p.periodid
    where dv.dataelementid = %enddateDE
    ORDER BY p.startdate DESC
    LIMIT 1;"
    TargetEndDate.sql<-gsub("%enddateDE",enddateDE,TargetEndDate.sql)
    TargetEndDate<-sqlQuery(channel,TargetEndDate.sql)
    TargetEndDate<-as.character(TargetEndDate[1,1])
    #print(TargetEndDate)
    print(paste("TargetStartDate:",TargetStartDate,"   ","ReprotDate:",reportDate,"   ","TargetEndDate:",TargetEndDate))
    
    
    
    Target.sql<-"SELECT  dv.value  as value FROM datavalue dv
    INNER JOIN period p on dv.periodid = p.periodid
    where dv.dataelementid = %targetDE
    ORDER BY p.startdate DESC
    LIMIT 1"
    
    Target.sql<-gsub("%targetDE",targetDE,Target.sql)
    
    Target<-as.numeric(sqlQuery(channel,Target.sql))/1000
    
    cropAcheived<-ApplyTimeWindow(cropAcheived,TargetStartDate,TargetEndDate)
    cropContracts<-ApplyTimeWindow(cropContracts,TargetStartDate,TargetEndDate)
    cropAcheived<-cumsum(cropAcheived)
    cropContracts<-cumsum(cropContracts)
    #cropAcheived[[1,1]]<-0
    print("-------------------------------------------------------------------")
    print(cropAcheived)
    print("-------------------------------------------------------------------")
    print(cropContracts)
    print("-------------------------------------------------------------------")
    
    tryCatch({
      #ToDO. This blows up if the data frames are empty
      fig8.data<-as.data.frame(na.locf(merge(cropAcheived,cropContracts)))
      
      
      
      fig8.data$startdate<-as.Date(rownames(fig8.data))
      print(fig8.data)
      
      colnames(fig8.data)<-c("Acheived","Contracts","startdate")
      fig8.data<-melt(fig8.data,id.vars="startdate")
      
      fig8.data$Target<-Target
      #print(fig8.data)
      
      lastDate <- NULL
      for (i in 1:nrow(fig8.data)) {
        if(fig8.data[i,"startdate"]>=reportDate)
        {
          lastDate<-fig8.data[i,"startdate"]
          fig8.data[i,"value"]<-NA
        }
      } 
      
      fillStartDate<-as.Date(lastDate+1)
      print(paste("FillDateStart:",fillStartDate,"   ","ReprotDate:",TargetEndDate))
      
      if(length(fillStartDate)>0)
      {
        while(fillStartDate <= TargetEndDate)
        {
          #print("Looping")
          fig8.data<-rbind(fig8.data,data.frame(startdate=as.character(fillStartDate[1]), variable="Acheived",value=NA,Target=300))
          fig8.data<-rbind(fig8.data,data.frame(startdate=as.character(fillStartDate[1]), variable="Contracts",value=NA,Target=300))
          fillStartDate<-as.Date(fillStartDate+1)
        }
      }
      
      print(fig8.data)
      #print(paste("Target-Value": Target))
      
      #We need to figure out where to put the annotation
      #TODO. This is a serious hack and need a better mehtod to get the x position
      targetLabel.x<-as.Date(fig8.data$startdate[[length(fig8.data$startdate)/4]])
      if (chartOption==2)
      {
        fig8<-Fig8Graph(fig8.data," Procurement 2012","Thousand MT","6 days")
      }
      else
      {
        fig8<-Fig8Graph(fig8.data," Procurement 2012","Thousand MT","4 days") 
      }
      fig8<-fig8+geom_hline(aes(yintercept=Target))
      fig8<-fig8+annotate("text",x=targetLabel.x,y=Target+0.05*Target,label=paste("Target:",Target),colour="red")
      ggsave(
        paste(targetPath,"fortnightlychart8_",reportDate,'.png',sep=""),
        fig8,
        width = 8 ,
        height = 4,
        units = "in",
        dpi = 300
      ) 
      
    },
             
    error=function(e){
    print("an error occurred-----")
    #dev.off()
    createNoDataImage("fortnightlychart8_")    
   })
  }
  
  else {
    ggsave(
      paste(targetPath,"fortnightlychart8_",reportDate,'.png',sep=""),
      p<-ggplot(),
      width = 8 ,
      height = 4,
      units = "in",
      dpi = 300
    )
  }
}
#End Fig 8

#Begin Figure generation
for (n in 1:8) {
print(paste("Generating Figure",n))
as.formula(paste("fig",n,".generate()",sep="")) }
#Finally, close the ODBC connection
odbcClose(channel)

