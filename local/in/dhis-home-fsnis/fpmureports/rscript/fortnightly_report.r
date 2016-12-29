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
#Requires an ODBC channel to be defined with this name
channel<-odbcConnect("fsnis")
#Get this from the command line
cmd_args<-commandArgs();
#TODO. Seems weak. 
reportDate<-as.Date(cmd_args[6])
#Set the working directory
#TODO..this needs to be paramaterized
#target_path<-"/home/fsnispro/dhis_home/Routput/"
target_path<-"/home/hisp/dhis2.9/fpmureports/chartoutput/"
setwd(target_path)

reportGraph<-function(x,title_text,ylabel_text){
p<-qplot(startdate,value,data=x,geom="step",colour=variable,main=title_text)
p<-p+scale_x_date(labels = date_format("%d-%b-%y"),breaks=date_breaks("28 days"))
p<-p+labs(x = "Date", y = ylabel_text)
p<-p + theme(legend.title = element_text(size=10))
p<-p + theme(legend.position = "right")
p<-p + theme(axis.text.x  = element_text(size=10,angle=45,colour="black",vjust=1,hjust=1))
p<-p + theme(axis.title.x = element_blank())
p<-p + theme(axis.text.y  = element_text(size=10,angle=0,colour="black"))
p<-p + scale_colour_hue(name  = "" )
p<-p + theme(plot.title = element_text(size = 12))
p<-p + theme(plot.margin = unit(rep(0, 4), "lines"))
p<-p + theme(panel.grid.minor = element_blank())
p<-p + theme(panel.grid.major = element_blank())
p<-p + theme(panel.background = element_blank())
p<-p + theme(axis.line = element_line())
p<-p + theme(plot.margin = unit(c(1, 1, 0.5, 0.5), "lines"))
return(p)
}



#Convenience functions
getAverageDataValues<-function(dataelementid,organisationunitid){
sql<-"SELECT  p.startdate, avg(dv.value)  as value FROM datavalue dv
INNER JOIN period p on dv.periodid = p.periodid
where dv.dataelementid = %dataelementid and dv.sourceid = %organisationunitid
GROUP BY  p.startdate
ORDER BY p.startdate DESC"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
values<-sqlQuery(channel,sql)
foo<-zoo(values$value,values$startdate)
tmp<-xts(,seq(start(foo),end(foo),"days"))
values<-na.locf(merge(tmp,foo))
colnames(values)<-"value"
return(as.zoo(values))
}

getSumMultiOrgDataValues<-function(dataelementid,startdate,enddate){
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

#Various functions to deal with getting the different periods from the report date
#Weeks are calculated as starting on a Thursday
currentThursday<-function(reportDate) {
reportDate<-as.Date(reportDate)
reportDate+days(5)-wday(reportDate)
}

previousThursdayAFortnightAgo<-function(reportDate) {
reportDate<-as.Date(reportDate)
reportDate+days(5)-wday(reportDate)-weeks(2)
}

previousThursdayAYearAgo<-function(reportDate) {
reportDate<-as.Date(reportDate)
reportDate+days(5)-wday(reportDate)-years(1)
}

currentFiscalYearStart<-function(reportDate){
reportDate<-as.Date(reportDate)
fiscalYear<-year(reportDate)
if ( month(reportDate) <= 6 ) {fiscalYear<-year(reportDate) - 1}
as.Date(paste(as.character(fiscalYear),'-07-01',sep=""))
}

#Time manipulation functions
applyTimeWindow<-function(z,startdate,enddate){window(z, start = startdate, end = enddate)}

#Figure 1
#SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market wholesale price
fig1.wholesale<-getAverageDataValues(197,10)/100.0
#SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
fig1.retail<-getAverageDataValues(198,10)
startdate<-currentThursday(reportDate)-years(1)
enddate<-currentThursday(reportDate)
fig1.wholesale<-applyTimeWindow(fig1.wholesale,startdate,enddate)
fig1.retail<-applyTimeWindow(fig1.retail,startdate,enddate)
#Figure 1 Data processing
fig1.data<-as.data.frame(merge(fig1.retail,fig1.wholesale))
colnames(fig1.data)<-c("Retail","Wholesale")
fig1.data$startdate<-as.Date(rownames(fig1.data))
fig1.data <- melt(fig1.data, id.vars="startdate")
#Figure 1 graph production
#png(filename=paste(target_path,"fortnightly_fig1.png",sep=""), width=800,heigh=400)
png(filename=paste(target_path,"fortnightlychart1_",cmd_args[6],".png",sep=""), width=800,heigh=400)
plot(reportGraph(fig1.data,"Figure 1. Changes in Coarse Rice (Dhaka)","Taka per kg"))
dev.off()
#End Figure 1

#Figure2
#Wheat Flour Open White market wholesale price daily
fig2.wholesale<-getAverageDataValues(245,10)/100.0
fig2.retail<-getAverageDataValues(246,10)
#Figure 2  Data processing
fig2.wholesale<-applyTimeWindow(fig2.wholesale,startdate,enddate)
fig2.retail<-applyTimeWindow(fig2.retail,startdate,enddate)
fig2.data<-as.data.frame(merge(fig2.retail,fig2.wholesale))
colnames(fig2.data)<-c("Retail","Wholesale")
fig2.data$startdate<-as.Date(rownames(fig2.data))
fig2.data <- melt(fig2.data, id.vars="startdate")
#Figure 2 Graph production
#png(filename=paste(target_path,"fortnightly_fig2.png",sep=""), width=800,heigh=400)
png(filename=paste(target_path,"fortnightlychart2_",cmd_args[6],".png",sep=""), width=800,heigh=400)
plot(reportGraph(fig2.data,"Figure 2: Change in price of atta Dhaka city ","Taka per kg"))
dev.off()
#End Figure2 

#Figure 3
#Rice wholesale price
#Dhaka City Coarse Rice wholesale DEID: [197 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
#(DEID:363 AVG(CCID 8,9) average of buy and selling price)
fig3.coarserice<-getAverageDataValues(197,10)
#Get the exchange rate
fig3.xrate<-getAverageDataValues(363,1)
#Merge these two and calculate the final values
fig3.coarserice<-fig3.coarserice/fig3.xrate*10
fig3.kolkata<-getAverageDataValues(710,1)
fig3.xrate2<-getAverageDataValues(711,1)
fig3.kolkata<-fig3.kolkata/fig3.xrate2*10

#Kolkata Rice  Price: data is from website http://fcainfoweb.nic.in/pms/interface3web.aspx 
#Which is INR/QTL need to be converted to USD [Rice Wholesale Kolkata price (New DE) / Exchange rate of INR to USD (DEID: 366 AVG(CCID 8,9)) *10]
#DENAME: Rice wholesale price 5% Thailand weekly DEID: 713
fig3.thai5<-getAverageDataValues(713,1)
#DENAME: Rice wholesale price 15% Vietnam weekly DEID: 714
fig3.vn15<-getAverageDataValues(714,1)
#DENAME: Rice wholesale price 5% parboiled Pakistan weekly DEID: 715
fig3.pak5<-getAverageDataValues(715,1)
#DENAME: Rice wholesale price 5% parboiled India weekly DEID: 716
fig3.ind5<-getAverageDataValues(716,1)
#Merge everything into a single data frame and rearrange
foo<-as.data.frame(merge(fig3.coarserice, fig3.kolkata, fig3.thai5,fig3.vn15,fig3.pak5,fig3.ind5))
foo$startDate<-as.Date(rownames(foo))
foo<-subset(foo,startDate>=startdate)
foo<-subset(foo,startDate<=enddate)
colnames(foo)<-c("Dhaka city Wholesale PB","Kolkata Wholesale PB","Thai 5% Parboiled","Vietnam 15% White","Pakistan 5% Parboiled","India 5% Parboiled","startdate")
fig3.data<-melt(foo,id.vars="startdate")
fig3<-reportGraph(fig3.data,"Rice wholesale price in Dhaka, Kolkata and FOB Prices in relevant international markets","USD per MT")
fig3<-fig3 + theme(legend.position = "bottom")
fig3<-fig3 + guides(col = guide_legend(nrow = 2))
#Figure 3 Graph production
#png(filename=paste(target_path,"fortnightly_fig3.png",sep=""), width=800,heigh=400)
png(filename=paste(target_path,"fortnightlychart3_",cmd_args[6],".png",sep=""), width=800,heigh=400)
plot(fig3)
dev.off()
#End Figure 3

#Figure 4
#Data processing
fig4.dhaka<-getAverageDataValues(243,10)/fig3.xrate*10
fig4.USSRW<-getAverageDataValues(356,1)
fig4.ukraine<-getAverageDataValues(353,1)
fig4.russia<-getAverageDataValues(355,1)
foo<-as.data.frame(merge(fig4.dhaka, fig4.USSRW, fig4.ukraine,fig4.russia))
foo$startDate<-as.Date(rownames(foo))
foo<-subset(foo,startDate>=startdate)
foo<-subset(foo,startDate<=enddate)
colnames(foo)<-c("Dhaka city Wholesale","US SRW, US Gulf Port","Ukraine, deep-sea Port","Russia, deep-sea Port","startdate")
fig4.data<-melt(foo,id.vars="startdate")
fig4<-reportGraph(fig4.data,"Wheat wholesale price in Dhaka and FOB Prices in relevant international markets","USD per MT")
fig4<-fig4 + theme(legend.position = "bottom")
fig4<-fig4 + guides(col = guide_legend(nrow = 2))
#Figure 3 Graph production
#png(filename=paste(target_path,"fortnightly_fig4.png",sep=""), width=800,heigh=400)
png(filename=paste(target_path,"fortnightlychart4_",cmd_args[6],".png",sep=""), width=800,heigh=400)
plot(fig4)
dev.off()
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
fig5.public<-getAverageDataValues(261,2) + getAverageDataValues(262,2)
fig5.private<-getAverageDataValues(263,2)

current.Public.Rice<-sum( applyTimeWindow(fig5.public,start=currentFiscalYearStart(reportDate), end=currentThursday(reportDate)))
current.Private.Rice<-sum( applyTimeWindow(fig5.private,start=currentFiscalYearStart(reportDate), end=currentThursday(reportDate)))
previousFortnight.Public.Rice<-sum( applyTimeWindow(fig5.public,start=currentFiscalYearStart(reportDate), end=previousThursdayAFortnightAgo(reportDate)))
previousFortnight.Private.Rice<-sum( applyTimeWindow(fig5.private,start=currentFiscalYearStart(reportDate), end=previousThursdayAFortnightAgo(reportDate)))
previousYear.Public.Rice<-sum( applyTimeWindow(fig5.public,start=currentFiscalYearStart(reportDate)-years(1), end=previousThursdayAYearAgo(reportDate)))
previousYear.Private.Rice<-sum( applyTimeWindow(fig5.private,start=currentFiscalYearStart(reportDate)-years(1), end=previousThursdayAYearAgo(reportDate)))

time<-list(c(as.character(currentThursday(reportDate)),as.character(previousThursdayAFortnightAgo(reportDate)),as.character(previousThursdayAYearAgo(reportDate))))
Private<-list(c(current.Private.Rice,previousFortnight.Private.Rice,previousYear.Private.Rice))
Government<-list(c(current.Public.Rice,previousFortnight.Public.Rice,previousYear.Public.Rice))

foo<-data.frame(time,Private,Government)
colnames(foo)<-c("time","Private","Government")
foo<-melt(foo,id.vars="time")
foo$time<-paste("as on",foo$time)
foo$value<-round(foo$value/1000,1)
#We need to get the midpoints of the bars to figure out where to put the text
foo<-ddply(foo, .(time), transform, pos = cumsum(value) - 0.5*value)
p <- qplot(time, value, data = foo, geom = "bar", fill = variable)
p<-p + geom_text(aes(label = value,y=pos),size = 3)
p<-p + theme(legend.position = "bottom")
p<-p + ylab("thousand MT")
p<-p + theme(axis.title.x = element_blank())
p<-p + theme(legend.title=element_blank())

#png(filename=paste(target_path,"fortnightly_fig5.png",sep=""), width=800,heigh=400)
png(filename=paste(target_path,"fortnightlychart5_",cmd_args[6],".png",sep=""), width=800,heigh=400)
plot(p)
dev.off()


#TODO Figure6
#Same as Rice import the data comes from MISM, The Government import is the sum of Aid and cash (269 + 270) and for private wheat import is 271. 

fig6.public<-getAverageDataValues(269,2) + getAverageDataValues(270,2)
fig6.private<-getAverageDataValues(271,2)

current.Public.Wheat<-sum( applyTimeWindow(fig6.public,start=currentFiscalYearStart(reportDate), end=currentThursday(reportDate)))
current.Private.Wheat<-sum( applyTimeWindow(fig6.private,start=currentFiscalYearStart(reportDate), end=currentThursday(reportDate)))
previousFortnight.Public.Wheat<-sum( applyTimeWindow(fig6.public,start=currentFiscalYearStart(reportDate), end=previousThursdayAFortnightAgo(reportDate)))
previousFortnight.Private.Wheat<-sum( applyTimeWindow(fig6.private,start=currentFiscalYearStart(reportDate), end=previousThursdayAFortnightAgo(reportDate)))
previousYear.Public.Wheat<-sum( applyTimeWindow(fig6.public,start=currentFiscalYearStart(reportDate)-years(1), end=previousThursdayAYearAgo(reportDate)))
previousYear.Private.Wheat<-sum( applyTimeWindow(fig6.private,start=currentFiscalYearStart(reportDate)-years(1), end=previousThursdayAYearAgo(reportDate)))

time<-list(c(as.character(currentThursday(reportDate)),as.character(previousThursdayAFortnightAgo(reportDate)),as.character(previousThursdayAYearAgo(reportDate))))
Private<-list(c(current.Private.Wheat,previousFortnight.Private.Wheat,previousYear.Private.Wheat))
Government<-list(c(current.Public.Wheat,previousFortnight.Public.Wheat,previousYear.Public.Wheat))

foo<-data.frame(time,Private,Government)
colnames(foo)<-c("time","Private","Government")
foo<-melt(foo,id.vars="time")
foo$time<-paste("as on",foo$time)
foo$value<-round(foo$value/1000,1)
#We need to get the midpoints of the bars to figure out where to put the text
foo<-ddply(foo, .(time), transform, pos = cumsum(value) - 0.5*value)
fig6 <- qplot(time, value, data = foo, geom = "bar", fill = variable)
fig6<-fig6 + geom_text(aes(label = value,y=pos),size = 3)
fig6<-fig6 + theme(legend.position = "bottom")
fig6<-fig6 + ylab("thousand MT")
fig6<-fig6 + theme(axis.title.x = element_blank())
fig6<-fig6 + theme(legend.title=element_blank())

#png(filename=paste(target_path,"fortnightly_fig6.png",sep=""), width=800,heigh=400)
png(filename=paste(target_path,"fortnightlychart6_",cmd_args[6],".png",sep=""), width=800,heigh=400)
plot(fig6)
dev.off()

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

startdate<-currentFiscalYearStart(reportDate)
enddate<-currentThursday(reportDate)

fig7.rice.omsfp<-getSumMultiOrgDataValues(370,startdate,enddate)+
getSumMultiOrgDataValues(371,startdate,enddate)+
getSumMultiOrgDataValues(373,startdate,enddate)+
getSumMultiOrgDataValues(372,startdate,enddate)+
getSumMultiOrgDataValues(404,startdate,enddate)+
getSumMultiOrgDataValues(405,startdate,enddate)+
getSumMultiOrgDataValues(407,startdate,enddate)+
getSumMultiOrgDataValues(406,startdate,enddate)

fig7.rice.op<-getSumMultiOrgDataValues(368,startdate,enddate)+getSumMultiOrgDataValues(402,startdate,enddate)
fig7.rice.lei<-getSumMultiOrgDataValues(369,startdate,enddate)+getSumMultiOrgDataValues(403,startdate,enddate)
fig7.rice.ep<-getSumMultiOrgDataValues(367,startdate,enddate)+getSumMultiOrgDataValues(401,startdate,enddate)
fig7.rice.ffw<-getSumMultiOrgDataValues(377,startdate,enddate)+getSumMultiOrgDataValues(411,startdate,enddate)
fig7.rice.vgd<-getSumMultiOrgDataValues(379,startdate,enddate)+getSumMultiOrgDataValues(413,startdate,enddate)
fig7.rice.tr<-getSumMultiOrgDataValues(378,startdate,enddate)+getSumMultiOrgDataValues(412,startdate,enddate)
fig7.rice.vgf<-getSumMultiOrgDataValues(380,startdate,enddate)+getSumMultiOrgDataValues(414,startdate,enddate)
fig7.rice.gr<-getSumMultiOrgDataValues(381,startdate,enddate)+getSumMultiOrgDataValues(415,startdate,enddate)
fig7.rice.others<-getSumMultiOrgDataValues(376,startdate,enddate)+
getSumMultiOrgDataValues(382,startdate,enddate)+
getSumMultiOrgDataValues(410,startdate,enddate)+
getSumMultiOrgDataValues(416,startdate,enddate)

fig7.rice<-data.frame(commodity=c('Rice'
),variable=c('omsfp','op','lei','ep','ffw','vgd','tr','vgf','gr','others'
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


fig7.wheat.omsfp<-getSumMultiOrgDataValues(386,startdate,enddate)+
getSumMultiOrgDataValues(387,startdate,enddate)+
getSumMultiOrgDataValues(389,startdate,enddate)+
getSumMultiOrgDataValues(388,startdate,enddate)

fig7.wheat.op<-getSumMultiOrgDataValues(384,startdate,enddate)
fig7.wheat.lei<-getSumMultiOrgDataValues(385,startdate,enddate)
fig7.wheat.ep<-getSumMultiOrgDataValues(383,startdate,enddate)
fig7.wheat.ffw<-getSumMultiOrgDataValues(393,startdate,enddate)
fig7.wheat.vgd<-getSumMultiOrgDataValues(395,startdate,enddate)
fig7.wheat.tr<-getSumMultiOrgDataValues(394,startdate,enddate)
fig7.wheat.vgf<-getSumMultiOrgDataValues(396,startdate,enddate)
fig7.wheat.sf<-getSumMultiOrgDataValues(400,startdate,enddate)
fig7.wheat.others<-getSumMultiOrgDataValues(392,startdate,enddate)+
getSumMultiOrgDataValues(398,startdate,enddate)

fig7.wheat<-data.frame(commodity=c('Wheat'
),variable=c('omsfp','op','lei','ep','ffw','vgd','tr','vgf','sf','others'
),value=c(fig7.wheat.omsfp,fig7.wheat.op,fig7.wheat.lei,fig7.wheat.ep,fig7.wheat.ffw,fig7.wheat.vgd,fig7.wheat.tr,fig7.wheat.vgf,fig7.wheat.sf,fig7.wheat.others))

#Targets
target.deids<-c(741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,759,760)
target.commodity<-c(rep("rice",10),rep("wheat",10))
target.variable<-c(as.character(fig7.rice$variable),as.character(fig7.wheat$variable))
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

fig7.data<-rbind(fig7.rice,fig7.wheat)
fig7<-ggplot(fig7.data,aes(x=variable,y=value/1000)) + geom_bar() + facet_grid(. ~ commodity)
#png(filename=paste(target_path,"fortnightly_fig7.png",sep=""), width=800,heigh=400)
png(filename=paste(target_path,"fortnightlychart7_",cmd_args[6],".png",sep=""), width=800,heigh=400)
plot(fig7)
dev.off()





#Figure8

riceBoroAcheived<-getAverageDataValues(257,2)
riceBoroContracts<-getAverageDataValues(258,2)

boroTargetStartDate.sql<-"SELECT  dv.value  as value FROM datavalue dv
INNER JOIN period p on dv.periodid = p.periodid
where dv.dataelementid = 450
ORDER BY p.startdate DESC
LIMIT 1"
boroTargetStartDate<-as.character(sqlQuery(channel,boroTargetStartDate.sql)[1,1])

boroTargetEndDate.sql<-"SELECT  dv.value  as value FROM datavalue dv
INNER JOIN period p on dv.periodid = p.periodid
where dv.dataelementid = 451
ORDER BY p.startdate DESC
LIMIT 1"
boroTargetEndDate<-as.character(sqlQuery(channel,boroTargetEndDate.sql)[1,1])


boroTarget.sql<-"SELECT  dv.value  as value FROM datavalue dv
INNER JOIN period p on dv.periodid = p.periodid
where dv.dataelementid = 417
ORDER BY p.startdate DESC
LIMIT 1"
boroTarget<-as.numeric(sqlQuery(channel,boroTarget.sql)[1,1])/1000

riceBoroAcheived<-applyTimeWindow(riceBoroAcheived,boroTargetStartDate,boroTargetEndDate)
riceBoroContracts<-applyTimeWindow(riceBoroContracts,boroTargetStartDate,boroTargetEndDate)
fig8.data<-cumsum(as.data.frame(na.locf(merge(riceBoroAcheived,riceBoroContracts))))
fig8.data$stardate<-as.Date(rownames(fig8.data))
colnames(fig8.data)<-c("Acheived","Contracts","startdate")
fig8.data<-melt(fig8.data,id.vars="startdate")
fig8.data$value<-fig8.data$value/1000
#We need to figure out where to put the annotation
#TODO. This is a serious hack and need a better mehtod to get the x position
targetLabel.x<-as.Date(fig8.data$startdate[[length(fig8.data$startdate)/4]])
fig8<-reportGraph(fig8.data,"Boro Procurement 2012","Thousand MT")
fig8<-fig8+geom_hline(aes(yintercept=boroTarget))
fig8<-fig8+annotate("text",x=targetLabel.x,y=boroTarget+0.05*boroTarget,label=paste("Target:",boroTarget),colour="red")
#png(filename=paste(target_path,"fortnightly_fig8.png",sep=""), width=800,heigh=400)
png(filename=paste(target_path,"fortnightlychart8_",cmd_args[6],".png",sep=""), width=800,heigh=400)
plot(fig8)
dev.off()
