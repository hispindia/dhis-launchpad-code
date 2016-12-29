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
where dv.dataelementid in( %dataelementid) and dv.sourceid = %organisationunitid AND p.startdate BETWEEN '%startdate' AND '%enddate'"

sql<-gsub("%dataelementid",dataelementid,sql)
sql<-gsub("%organisationunitid",organisationunitid,sql)
sql<-gsub("%startdate",startdate,sql)
sql<-gsub("%enddate",enddate,sql)
print(sql)
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
print("chart3--------------------------")
png(filename=paste(target_path,"quarterlychart3_",quarter_period,".png",sep=""), width=800,heigh=400)

fig2.rice<-getAverageDataValues(1003,2,startdateprev,enddateprev)
fig2.rice$month<-month(fig2.rice$startdate)
tempName<-paste("Rice",(year(reportDate)-1),sep="")
#print(tempName)
colnames(fig2.rice)[2]<-tempName
fig2.rice<-subset(fig2.rice,select=c("month",tempName))

fig22.rice<-getAverageDataValues(1003,2,startdatecurr,enddatecurr)
fig22.rice$month<-month(fig22.rice$startdate)
tempName<-paste("Rice",(year(reportDate)),sep="")
#print(tempName)
colnames(fig22.rice)[2]<-tempName
fig22.rice<-subset(fig22.rice,select=c("month",tempName))

#print(fig2.rice)
#print(fig22.rice)

fig2.wheat<-getAverageDataValues(1004,2,startdateprev,enddateprev)
fig2.wheat$month<-month(fig2.wheat$startdate)
tempName<-paste("Wheat",(year(reportDate)-1),sep="")
#print(tempName)
colnames(fig2.wheat)[2]<-tempName
fig2.wheat<-subset(fig2.wheat,select=c("month",tempName))

#print(fig2.wheat)

fig22.wheat<-getAverageDataValues(1004,2,startdatecurr,enddatecurr)
fig22.wheat$month<-month(fig22.wheat$startdate)
tempName<-paste("Wheat",(year(reportDate)),sep="")
colnames(fig22.wheat)[2]<-tempName
fig22.wheat<-subset(fig22.wheat,select=c("month",tempName))
#print(fig22.wheat)


foo<-as.data.frame(merge(fig2.rice,fig22.rice,by="month",all=TRUE))
foo1<-as.data.frame(merge(fig2.wheat,fig22.wheat,by="month",all=TRUE))

#print(foo1)
foo3<-as.data.frame(merge(foo,foo1,all=TRUE))
foo3<-melt(foo3,id.vars="month")
#foo$month<-factor(foo$month,label=month.name)
#print(foo3)
foo3$value<-foo3$value*100
p<-qplot(data=foo3,factor(month,label=month.name),value,color=variable,geom=c("point","line"),lwd=1,group=variable,main=paste("National wholesale price, ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+labs(x = "Month", y = "Taka per quintal")
p<-p + theme(legend.title = element_text(size=0,color="white"))
p<-p+ theme(legend.position = "bottom",legend.text = element_text(size = 15,hjust=1,vjust=1))
p<-p + theme(axis.text.x  = element_text(size=15,angle=45,colour="black",vjust=1,hjust=1))
p<-p + theme(axis.text.y  = element_text(size=15,angle=0,colour="black"))
p<-p + theme(axis.title.y  = element_text(size=10,angle=90,colour="black",face="bold"))
p<-p + theme(axis.title.x  = element_text(size=10,angle=0,colour="black",face="bold"),axis.title.y  = element_text(size=10,angle=90,colour="black",face="bold"))
p<-p + theme(plot.title= element_text(size=17,angle=0,colour="black",face="bold"))
p<-p + theme(plot.background = element_rect(fill = "#CADF7B",color="black",linetype = "solid"))
p<-p + theme(panel.background = element_rect(fill = "white"))
plot(p)

dev.off()
#End Figure 2.1


#Figure 2.2-------------
png(filename=paste(target_path,"quarterlychart4_",quarter_period,".png",sep=""), width=800,heigh=400)


fig2.rice<-getAverageDataValues(1000,2,startdateprev,enddateprev)
fig2.rice$month<-month(fig2.rice$startdate)
tempName<-paste("Rice",(year(reportDate)-1),sep="")
#print(tempName)
colnames(fig2.rice)[2]<-tempName
fig2.rice<-subset(fig2.rice,select=c("month",tempName))

fig22.rice<-getAverageDataValues(1003,2,startdatecurr,enddatecurr)
fig22.rice$month<-month(fig22.rice$startdate)
tempName<-paste("Rice",(year(reportDate)),sep="")
#print(tempName)
colnames(fig22.rice)[2]<-tempName
fig22.rice<-subset(fig22.rice,select=c("month",tempName))

#print(fig2.rice)
#print(fig22.rice)

fig2.Atta<-getAverageDataValues(1002,2,startdateprev,enddateprev)
fig2.Atta$month<-month(fig2.Atta$startdate)
tempName<-paste("Atta",(year(reportDate)-1),sep="")
#print(tempName)
colnames(fig2.Atta)[2]<-tempName
fig2.Atta<-subset(fig2.Atta,select=c("month",tempName))

#print(fig2.Atta)

fig22.Atta<-getAverageDataValues(1004,2,startdatecurr,enddatecurr)
fig22.Atta$month<-month(fig22.Atta$startdate)
tempName<-paste("Atta",(year(reportDate)),sep="")
colnames(fig22.Atta)[2]<-tempName
fig22.Atta<-subset(fig22.Atta,select=c("month",tempName))
#print(fig22.Atta)


foo<-as.data.frame(merge(fig2.rice,fig22.rice,by="month",all=TRUE))
foo1<-as.data.frame(merge(fig2.Atta,fig22.Atta,by="month",all=TRUE))

#print(foo1)
foo3<-as.data.frame(merge(foo,foo1,all=TRUE))
foo3<-melt(foo3,id.vars="month")
#foo$month<-factor(foo$month,label=month.name)
#print(foo3)
foo3$value<-foo3$value*100
p<-qplot(data=foo3,factor(month,label=month.name),value,color=variable,geom=c("point","line"),lwd=1,group=variable,main=paste("National wholesale price, ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+labs(x = "Month", y = "Taka per quintal")
p<-p + theme(legend.title = element_text(size=0,color="white"))
p<-p+ theme(legend.position = "bottom",legend.text = element_text(size = 15,hjust=1,vjust=1))
p<-p + theme(axis.text.x  = element_text(size=15,angle=45,colour="black",vjust=1,hjust=1))
p<-p + theme(axis.text.y  = element_text(size=15,angle=0,colour="black"))
p<-p + theme(axis.title.y  = element_text(size=10,angle=90,colour="black",face="bold"))
p<-p + theme(axis.title.x  = element_text(size=10,angle=0,colour="black",face="bold"),axis.title.y  = element_text(size=10,angle=90,colour="black",face="bold"))
p<-p + theme(plot.title= element_text(size=17,angle=0,colour="black",face="bold"))
p<-p + theme(plot.background = element_rect(fill = "#CADF7B",color="black",linetype = "solid"))
p<-p + theme(panel.background = element_rect(fill = "white"))
plot(p)

dev.off()
#End Figure 2.2


#Fig 1.2-----------------------
print("chart2-----------------------------------------")

#1 july x-1 - 30 june x
startdate<-paste(year(startdatecurr),"-07-01",sep="")
enddate<-paste(year(startdatecurr)+1,"-06-30",sep="")
print(startdate)
print(enddate)
fig1.aus<-getSumDataValues(paste(910,911,912,sep=","),2,startdate,enddate)
print(fig1.aus)
fig1.aus$year<-year(fig1.aus$startdate)

fig1.aus<-subset(fig1.aus,select=c("year","value"))
colnames(fig1.aus)[2]<-"Aus"
print(fig1.aus)

fig1.aman<-getSumDataValues(paste(913,914,915,1536,sep=","),2,startdate,enddate)
fig1.aman$year<-year(fig1.aman$startdate)
fig1.aman<-subset(fig1.aman,select=c("year","value"))
colnames(fig1.aman)[2]<-"Aman"
#print(fig1.aman)

fig1.boro<-getSumDataValues(paste(916,917,918,sep=","),2,startdate,enddate)
fig1.boro$year<-year(fig1.boro$startdate)
fig1.boro<-subset(fig1.boro,select=c("year","value"))
colnames(fig1.boro)[2]<-"Boro"
#print(fig1.boro)

fig1.wheat<-getSumDataValues(919,2,startdate,enddate)
fig1.wheat$year<-year(fig1.wheat$startdate)
fig1.wheat<-subset(fig1.wheat,select=c("year","value"))
colnames(fig1.wheat)[2]<-"Wheat"
#print(fig1.wheat)

foo<-as.data.frame(merge(fig1.aus,fig1.aman,by="year"))
foo1<-as.data.frame(merge(fig1.boro,fig1.wheat,by="year"))
fee<-as.data.frame(merge(foo,foo1,by="year"))
fee<-melt(fee,id.vars="year")
fee<-subset(fee,select=c("variable","value"))
print(fee)
value<-fee$value

position<-c(value[1]/2,value[1]+value[2]/2,value[1]+value[2]+value[3]/2,value[1]+value[2]+value[3]+value[4]/2)

position<-position/max(position)
fee$value<-fee$value/10

#print(position)
if (FALSE){
p<-ggplot(data=fee,aes(x=factor(1),weight=value,fill=factor(variable)))
p<-p+geom_bar(width = .9, position = "fill", colour = "black")
p<-p+geom_text(aes(x=1.75,y=position,label=value))
p<-p+coord_polar(theta="y")
p<-p+theme_bw()
p<-p+ opts(axis.title.x = theme_blank(), axis.title.y= theme_blank(), axis.text.x=theme_blank(), axis.text.y=theme_blank(), panel.grid.minor = theme_blank(), panel.grid.major = theme_blank(), panel.border = theme_blank(), axis.ticks=theme_blank())
p<-p+scale_fill_hue(l=70, c=150)
}
png(filename=paste("quarterlychart2_",quarter_period,".png",sep=""),height=400)

par(bg="#CADF7B")
color<-c("#C05057","#4F81BD","#8064A2","#E46C0A")
pie(fee$value,labels=fee$value,col=color,main=paste(year(reportDate),"/",year(reportDate)+1,"(Target)"))
legend(x="topright",legend=fee$variable,cex=0.8,fill=color)

dev.off()

#plot(p)
#end of fig 1.2

#Fig 1.1-----------------------
print("chart1-----------------------------------------")
#png(filename=paste(target_path,"quarterlychart1_",quarter_period,".png",sep=""), width=800,heigh=400)
startdate<-paste(year(startdatecurr)-1,"-07-01",sep="")
enddate<-paste(year(startdatecurr),"-06-30",sep="")
print(startdate)
print(enddate)


fig1.aus<-getSumDataValues(paste(882,883,884,sep=","),2,startdate,enddate)
fig1.aus$year<-year(fig1.aus$startdate)
fig1.aus<-subset(fig1.aus,select=c("year","value"))
colnames(fig1.aus)[2]<-"Aus"
#print(fig1.aus)

fig1.aman<-getSumDataValues(paste(886,887,885,1535,sep=","),2,startdate,enddate)
fig1.aman$year<-year(fig1.aman$startdate)
fig1.aman<-subset(fig1.aman,select=c("year","value"))
colnames(fig1.aman)[2]<-"Aman"
#print(fig1.aman)

fig1.boro<-getSumDataValues(paste(888,889,890,sep=","),2,startdate,enddate)
fig1.boro$year<-year(fig1.boro$startdate)
fig1.boro<-subset(fig1.boro,select=c("year","value"))
colnames(fig1.boro)[2]<-"Boro"
#print(fig1.boro)

fig1.wheat<-getSumDataValues(891,2,startdate,enddate)
fig1.wheat$year<-year(fig1.wheat$startdate)
fig1.wheat<-subset(fig1.wheat,select=c("year","value"))
colnames(fig1.wheat)[2]<-"Wheat"
#print(fig1.wheat)

foo<-as.data.frame(merge(fig1.aus,fig1.aman,by="year"))
foo1<-as.data.frame(merge(fig1.boro,fig1.wheat,by="year"))
fee<-as.data.frame(merge(foo,foo1,by="year"))
fee<-melt(fee,id.vars="year")
fee<-subset(fee,select=c("variable","value"))
print(fee)
value<-fee$value
position<-c(value[1]/2,value[1]+value[2]/2,value[1]+value[2]+value[3]/2,value[1]+value[2]+value[3]+value[4]/2)

position<-position/max(position)
fee$value<-fee$value/10

#print(position)
if (FALSE){
p<-ggplot(data=fee,aes(x=factor(1),weight=value,fill=factor(variable)))
p<-p+geom_bar(width = .9, position = "fill", colour = "black")
p<-p+geom_text(aes(x=1.75,y=position,label=value))
p<-p+coord_polar(theta="y")
p<-p+theme_bw()
p<-p+ opts(axis.title.x = theme_blank(), axis.title.y= theme_blank(), axis.text.x=theme_blank(), axis.text.y=theme_blank(), panel.grid.minor = theme_blank(), panel.grid.major = theme_blank(), panel.border = theme_blank(), axis.ticks=theme_blank())
p<-p+scale_fill_hue(l=70, c=150)
}
png(filename=paste("quarterlychart1_",quarter_period,".png",sep=""),height=400)

par(bg="#CADF7B")
color<-c("#C05057","#4F81BD","#8064A2","#E46C0A")
pie(fee$value,labels=fee$value,col=color,main=paste(year(reportDate),"/",year(reportDate)+1,"(Target)"))
legend(x="topright",legend=fee$variable,cex=0.8,fill=color)

dev.off()

#end of fig 1.1

#Fig 4--------------------
print("chart5-----------------------------------------")
png(filename=paste(target_path,"quarterlychart5_",quarter_period,".png",sep=""), width=800,heigh=400)

fig4.retail<-getAverageDataValuesAsc(1003,2,as.Date(enddatecurr)-years(1),enddatecurr)
fig4.retail$month<-month(fig4.retail$startdate)
tempName<-"Retail Price"
print(tempName)
colnames(fig4.retail)[2]<-tempName
fig4.retail<-subset(fig4.retail,select=c("month",tempName))
if(length(fig4.retail$month)>12){
fig4.retail<-fig4.retail[-1,]
}
print(fig4.retail)
fig4TempName<-tempName


fig4.wholesale<-getAverageDataValuesAsc(1000,2,as.Date(enddatecurr)-years(1),enddatecurr)
print(fig4.wholesale)
fig4.wholesale$month<-month(fig4.wholesale$startdate)
tempName<-"Wholesale price"
#print(tempName)
colnames(fig4.wholesale)[2]<-tempName
fig4.wholesale<-subset(fig4.wholesale,select=c("month",tempName))
if(length(fig4.wholesale$month)>12){
fig4.wholesale<-fig4.wholesale[-1,]
}
print(fig4.wholesale)

foo<-as.data.frame(merge(fig4.retail,fig4.wholesale,sort=F))
margin<-((foo[2]-foo[3])/foo[3])*100
marginmonth<-foo[1]

colnames(margin)<-"Percentage margin"
marginName<-"Percentage margin"

margin<-cbind(marginmonth,margin)
foo1<-as.data.frame(merge(foo,margin,sort=F))

#foo$margin<-margin
print(foo1)
print("qwwwwwwww")
fee<-melt(foo1,id.vars="month")
print("qwwwwwwww")
print(fee)

monthNames<-factor(foo$month,label=month.name)
print(monthNames)
lbl<-round(fee$value,1)
print(lbl)
if(TRUE){
p<-qplot(data=fee,factor(month,label=month.name,levels=unique(month)),value,color=variable,geom=c("point","line"),group=variable,main=paste("Fig.4 Retail vs. wholesale Rice prices and margin(%) in Dhaka city: ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+geom_bar(subset=.(variable==marginName),stat="identity",width=".1",fill="#E199CC")
p<-p+geom_text(aes(label=lbl,y=(lbl-1)))
p<-p+ scale_color_manual(values=c("blue", "cyan","white"))
p<-p+labs(x = "Months", y = "Taka per Kg")
p<-p+ theme(legend.position = "bottom")
p<-p + theme(axis.text.x  = element_text(size=15,angle=45,colour="black",vjust=1,hjust=1))
p<-p + theme(axis.text.y  = element_text(size=15,angle=0,colour="black"))
p<-p + theme(plot.title= element_text(size=17,angle=0,colour="black",face="bold"))
p<-p + theme(legend.title = element_text(size=0,color="white"),axis.title.y=element_text(size=10,face="bold"))
p<-p + theme(legend.key = element_rect(color = "white",fill = "white"))
p<-p +  theme(legend.text = element_text(size = 15, colour = "black", angle = 0))
p<-p + theme(plot.background = element_rect(fill = "#CADF7B",linetype = "solid"))
p<-p+scale_x_discrete(labels=monthNames)
p<-p + theme(panel.background = element_rect(fill = "white"))

plot(p)

dev.off()
}
#Fig 4--------------------



#Fig 5--------------------
print("chart6-----------------------------------------")
#End of Fig 5--------------------

#Fig 6------------------->>>>>>>>>>>>>>>>>>>>>>>>>
print("chart7----------------------------")


#End of fig 6 ---------------------
#if(FALSE)
{
#Fig 7------------------->>>>>>>>>>>>>>>>>>>>>>>>>
print("chart8-----------------------------------------")

#End of fig 7 ---------------------
}
if(TRUE){
#Fig8--------->>>>>>>>>>>>>>>
png(filename=paste(target_path,"quarterlychart9_",quarter_period,".png",sep=""), width=800,heigh=400)

#Constants
LCRiceDataelementid=853
thai5Dataelementid=329
sourceid="1,2"

enddate=enddatecurr
startdate=as.Date(enddate)-years(2)
print(startdate)
print(enddate)
fig8.LCRice<-getAverageDataValuesAsc(LCRiceDataelementid,sourceid,startdate,enddate)
fig8.LCRice$yearmonth<-paste(year(fig8.LCRice$startdate),month(fig8.LCRice$startdate))
fig8.LCRice<-subset(fig8.LCRice,select=c("yearmonth","value"))
colnames(fig8.LCRice)<-c("yearmonth","LC Settled")
#print(fig8.LCRice)

fig8.thai5<-getAverageDataValuesAsc(thai5Dataelementid,sourceid,startdate,enddate)
fig8.thai5$yearmonth<-paste(year(fig8.thai5$startdate),month(fig8.thai5$startdate))

fig8.thai5<-subset(fig8.thai5,select=c("yearmonth","value"))
colnames(fig8.thai5)<-c("yearmonth","Thai5% paraboiled")

#print(fig8.thai5)

foo<-as.data.frame(merge(fig8.LCRice,fig8.thai5,by=c("yearmonth"),all=TRUE,sort=F))
#print(foo)

fee<-melt(foo,id.vars=c("yearmonth"))
#print(fee)

p<-qplot(data=fee,yearmonth,value,color=variable,geom=c("line"),lwd=1,group=variable,main=paste("International rice price"))
p<-p+labs(x = "", y = "USD/MT")
p<-p + theme(legend.title = element_text(size=0,color="white"))
p<-p+ theme(legend.position = "bottom")
p<-p + theme(axis.text.x  = element_text(size=15,angle=45,colour="black",vjust=1,hjust=1))
p<-p + theme(axis.text.y  = element_text(size=15,angle=0,colour="black"))
p<-p + theme(plot.title= element_text(size=17,angle=0,colour="black",face="bold"))
p<-p + theme(plot.background = element_rect(fill = "#CADF7B",color="black",linetype = "solid"))
p<-p + theme(panel.background = element_rect(fill = "white"))
plot(p)
dev.off()
#End of fig8---------------------------

#Fig9--------->>>>>>>>>>>>>>>
png(filename=paste(target_path,"quarterlychart10_",quarter_period,".png",sep=""), width=800,heigh=400)

#Constants
SettledDataelementid=855
wheatDataelementid=356
sourceid="1,2"

enddate=enddatecurr
startdate=as.Date(enddate)-years(2)
print(startdate)
print(enddate)
fig9.Settled<-getAverageDataValuesAsc(SettledDataelementid,sourceid,startdate,enddate)
fig9.Settled$yearmonth<-paste(year(fig9.Settled$startdate),month(fig9.Settled$startdate))
fig9.Settled<-subset(fig9.Settled,select=c("yearmonth","value"))
colnames(fig9.Settled)<-c("yearmonth","LC Settled")
#print(fig9.Settled)

fig9.wheat<-getAverageDataValuesAsc(wheatDataelementid,sourceid,startdate,enddate)
fig9.wheat$yearmonth<-paste(year(fig9.wheat$startdate),month(fig9.wheat$startdate))

fig9.wheat<-subset(fig9.wheat,select=c("yearmonth","value"))
colnames(fig9.wheat)<-c("yearmonth","wheat (soft red)")

#print(fig9.wheat)

foo<-as.data.frame(merge(fig9.Settled,fig9.wheat,by=c("yearmonth"),all=TRUE,sort=F))
#print(foo)

fee<-melt(foo,id.vars=c("yearmonth"))
#print(fee)

p<-qplot(data=fee,yearmonth,value,color=variable,geom=c("line"),lwd=1,group=variable,main=paste("International wheat price"))
p<-p+labs(x = "", y = "USD/MT")
p<-p + theme(legend.title = element_text(size=0,color="white"))
p<-p+ theme(legend.position = "bottom")
p<-p + theme(axis.text.x  = element_text(size=15,angle=45,colour="black",vjust=1,hjust=1))
p<-p + theme(axis.text.y  = element_text(size=15,angle=0,colour="black"))
p<-p + theme(plot.title= element_text(size=17,angle=0,colour="black",face="bold"))
p<-p + theme(plot.background = element_rect(fill = "#CADF7B",color="black",linetype = "solid"))
p<-p + theme(panel.background = element_rect(fill = "white"))
plot(p)
dev.off()
}
