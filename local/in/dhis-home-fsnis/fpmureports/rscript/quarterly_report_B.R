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
png(filename=paste(target_path,"quarterlychart6_",quarter_period,".png",sep=""), width=800,heigh=400)

fig5.retail<-getAverageDataValuesAsc(1005,2,as.Date(enddatecurr)-years(1),enddatecurr)
fig5.retail$month<-month(fig5.retail$startdate)
tempName<-"Retail Price"
print(tempName)
colnames(fig5.retail)[2]<-tempName
fig5.retail<-subset(fig5.retail,select=c("month",tempName))
if(length(fig5.retail$month)>12){
fig5.retail<-fig5.retail[-1,]
}

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
print(fig5.wholesale)

foo<-as.data.frame(merge(fig5.retail,fig5.wholesale,sort=F))
margin<-((foo[2]-foo[3])/foo[3])*100
marginmonth<-foo[1]

colnames(margin)<-"Percentage margin"
marginName<-"Percentage margin"

margin<-cbind(marginmonth,margin)
foo1<-as.data.frame(merge(foo,margin,sort=F))

#foo$margin<-margin
#print(foo1)
#print("qwwwwwwww")
fee<-melt(foo1,id.vars="month")
print("qwwwwwwww")
monthNames<-factor(foo$month,label=month.name)
print(monthNames)
lbl<-round(fee$value,1)
print(lbl)
if(TRUE){
p<-qplot(data=fee,factor(month,label=month.name,levels=unique(month)),value,color=variable,geom=c("point","line"),group=variable,main=paste("Fig.4 Retail vs. wholesale Atta prices and margin(%) in Dhaka city: ",(year(reportDate)-1),"-",year(reportDate),sep=""))
p<-p+geom_bar(subset=.(variable==marginName),stat="identity",fill="#E199CC")
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
}


dev.off()

#End of Fig 5--------------------

#Fig 6------------------->>>>>>>>>>>>>>>>>>>>>>>>>
print("chart7----------------------------")

#End of fig 6 ---------------------

#Fig 7------------------->>>>>>>>>>>>>>>>>>>>>>>>>
print("chart8-----------------------------------------")


#End of fig 7 ---------------------

