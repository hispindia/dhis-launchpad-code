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
# Author: Gaurav(gaurav08021@gmail.com)

require(RODBC)
require(reshape)
require(ggplot2)
require(scales)
require(grid)
require(zoo)
require(xts)
require(lubridate)
require(graphics)
library(data.table)

#Requires an ODBC channel to be defined with this name
#If we cannot get this connection, abort.

tryCatch({
  channel<<-odbcConnect("fsnis")
},error= function(ex) { print(ex) ; stop() ; } )

cmd_args<-commandArgs()

reportDate<-as.Date(Sys.Date(), format='%Y-%m-%d')

#reportDate<-as.Date('2013-03-30')

print(paste("SYS_DATE:",reportDate))

if( is.na(reportDate) || !is.Date(as.Date(reportDate)) ) 
{ 
  stop("A valid date is required as the first paramater")
}

targetPath<-"/var/www/charts/"
#targetPath<-"/home/gaurav/dhis/home/fpmureports/portalcharts/"
#targetPath<-cmd_args[7]

createNoDataImage<-function(chartName){}

ReportGraphNoShape<-function(x,title_text,ylabel_text, breakSize)
{
  p<- ggplot(x, aes(x=startdate,y=value, colour=variable))
  p<-p+geom_step(size=1)
  p<-p+scale_x_date(labels = date_format("%d-%b-%y"),breaks=date_breaks(breakSize),  limits = c(floor_date(min(x$startdate)), ceiling_date(as.Date(max(x$startdate))+2)))
  p<-p+labs(x = "Date", y = ylabel_text)
  p<-p + theme_bw()
  p<-p + theme(legend.key.size=unit(2,"line"))
  p<-p + theme(legend.text = element_text(colour = 'black', angle = 0, size = 13, hjust = 3, vjust = 3, face = 'bold'))
  p<-p + theme(legend.background = element_rect(size = 3))
  p<-p + theme(legend.direction = "horizontal", legend.position = "bottom", legend.box = "vertical", legend.title.align = 0)  
  p<-p + theme(legend.key = element_blank())
  p<-p + theme(axis.text.x  = element_text(size=18,angle=45,colour="black",vjust=1,hjust=1))
  p<-p + theme(axis.title.x = element_blank())
  p<-p + theme(axis.text.y  = element_text(size=18,angle=0,colour="black"))
  p<-p + scale_colour_hue(name  = "" )
  p<-p + theme(axis.line = element_line())
  p<-p + theme(plot.margin = unit(c(1, 1, 0.5, 0.5), "lines"))
  
  p<-p+theme(axis.title.y = element_text(size = 18))
  
  return(p)
}

HomePageGraph<-function(x,title_text,ylabel_text, breakSize)
{
  p<- ggplot(x, aes(x=startdate,y=value, colour=variable))
  p<-p+geom_step(size=2)
  p<-p+scale_x_date(labels = date_format("%d-%b-%y"),breaks=date_breaks(breakSize),  limits = c(floor_date(min(x$startdate)), ceiling_date(as.Date(max(x$startdate))+2)))
  p<-p+labs(x = "Date", y = ylabel_text)
  p<-p+theme(axis.title.y = element_text(size = 32))
  p<-p + theme_bw()
  p<-p + theme(legend.key.size=unit(2,"line"))
  p<-p + theme(legend.text = element_text(colour = 'black', angle = 0, size = 29, hjust = 3, vjust = 3, face = 'bold'))
  p<-p + theme(legend.background = element_rect(size = 3))
  p<-p + theme(legend.direction = "vertical", legend.position = "bottom")
  p<-p + theme(legend.key = element_blank())
  p<-p + theme(axis.text.x  = element_text(size=28,angle=45,colour="black",vjust=1,hjust=1))
  p<-p + theme(axis.title.x = element_blank())
  p<-p + theme(axis.title.y = element_text(size=32,angle=90,colour="black"))
  p<-p + theme(axis.text.y  = element_text(size=28,angle=0,colour="black"))
  p<-p + scale_colour_hue(name  = "" )
  p<-p + theme(axis.line = element_line())
  p<-p + theme(plot.margin = unit(c(1, 1, 0.5, 0.5), "lines"))
  return(p)
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

GetDate30DaysBefore <- function(reportDate){
  tempDate <- as.Date(reportDate)
  tempDate<-tempDate-days(30)
  return(tempDate)
}

GetDate1QuarterBefore <- function(reportDate){
  tempDate <- as.Date(reportDate)
  tempDate <- tempDate - months(3)
  return(tempDate)
}

GetDate1YearBefore <- function(reportDate){
  tempDate <- as.Date(reportDate)
  tempDate <- tempDate - years(1)
  return(tempDate)
}

#Time manipulation functions
ApplyTimeWindow<-function(z,startdate,enddate){window(z, start = startdate, end = enddate)}

fig1.generate<-function()
{  
 
  fig1.wholesale<-GetAverageDataValues(197,10)/100
  
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig1.retail<-GetAverageDataValues(198,10)
  startDate<-GetDate30DaysBefore(reportDate)
  endDate<-reportDate
  fig1.wholesale<-ApplyTimeWindow(fig1.wholesale,startDate,endDate)
  fig1.retail<-ApplyTimeWindow(fig1.retail,startDate,endDate)
  #Figure 1 Data processing
  fig1.data<-as.data.frame(merge(fig1.retail,fig1.wholesale))
  pWValue<-as.data.frame(fig1.wholesale[length(fig1.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig1.retail[length(fig1.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format="%d-%b-%y")," is ",pRValue$value," Tk/Kg )      ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate,format="%d-%b-%y")," is ",pWValue$value," Tk/Kg )      ")
  colnames(fig1.data)<-c(reatilLable,wholesaleLable)
  fig1.data$startdate<-as.Date(rownames(fig1.data))
  fig1.data <- melt(fig1.data, id.vars="startdate")
  #Figure 1 graph production
  pWValue<-as.data.frame(fig1.wholesale[length(fig1.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig1.retail[length(fig1.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  
  fig1<-ReportGraphNoShape(fig1.data,"Figure 1. Changes in Coarse Rice (Dhaka)","Taka per kg","2 days")
  #fig1<-fig1+ annotate("text",pWValue$startdate+2,pWValue$value,label=pWValue$value, size = 4)
  #fig1<-fig1+ annotate("text",pRValue$startdate+2,pRValue$value,label=pRValue$value, size = 4)
  ggsave(
    paste(targetPath,"portalchart1",'.png',sep=""),
    fig1,
    width = 12 ,
    height =  6,
    units = "in",
    dpi = 200,
    bg = "transparent"
  )
}

fig2.generate<-function()
{  
  tryCatch({
  fig2.wholesale<-GetAverageDataValues(197,10)/100
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig2.retail<-GetAverageDataValues(198,10)
  print(fig2.retail)
  startDate<-GetDate1QuarterBefore(reportDate)
  endDate<-reportDate
  fig2.wholesale<-ApplyTimeWindow(fig2.wholesale,startDate,endDate)
  fig2.retail<-ApplyTimeWindow(fig2.retail,startDate,endDate)
  #Figure 1 Data processing
  
  fig2.data<-as.data.frame(merge(fig2.retail,fig2.wholesale))
  pWValue<-as.data.frame(fig2.wholesale[length(fig2.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig2.retail[length(fig2.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format="%d-%b-%y")," is ",pRValue$value," Tk/Kg )      ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate, format="%d-%b-%y")," is ",pWValue$value," Tk/Kg )      ")
  colnames(fig2.data)<-c(reatilLable,wholesaleLable)
  fig2.data$startdate<-as.Date(rownames(fig2.data))
  fig2.data <- melt(fig2.data, id.vars="startdate")
  
  #Figure 1 graph production
  fig2<-ReportGraphNoShape(fig2.data,"Figure 1. Changes in Coarse Rice (Dhaka)","Taka per kg","10 days")
  #fig2<-fig2+ annotate("text",pWValue$startdate+4,pWValue$value,label=pWValue$value, size = 4)
  #fig2<-fig2+ annotate("text",pRValue$startdate+4,pRValue$value,label=pRValue$value, size = 4)
  ggsave(
    paste(targetPath,"portalchart2",'.png',sep=""),
    fig2,
    width = 12 ,
    height =  6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 2 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10") 
  })
}

fig3.generate<-function()
{  
  tryCatch({
  fig3.wholesale<-GetAverageDataValues(197,10)/100.0
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig3.retail<-GetAverageDataValues(198,10)
  startDate<-GetDate1YearBefore(reportDate)
  endDate<-reportDate
  fig3.wholesale<-ApplyTimeWindow(fig3.wholesale,startDate,endDate)
  fig3.retail<-ApplyTimeWindow(fig3.retail,startDate,endDate)
  #Figure 1 Data processing
  fig3.data<-as.data.frame(merge(fig3.retail,fig3.wholesale))
  pWValue<-as.data.frame(fig3.wholesale[length(fig3.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig3.retail[length(fig3.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format="%d-%b-%y")," is ",pRValue$value," Tk/Kg )       ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate, format="%d-%b-%y")," is ",pWValue$value," Tk/Kg )       ")
  colnames(fig3.data)<-c(reatilLable,wholesaleLable)
  fig3.data$startdate<-as.Date(rownames(fig3.data))
  fig3.data <- melt(fig3.data, id.vars="startdate")
  pWValue<-as.data.frame(fig3.wholesale[length(fig3.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig3.retail[length(fig3.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  
  #Figure 1 graph production
  fig3<-ReportGraphNoShape(fig3.data,"Figure 1. Changes in Coarse Rice (Dhaka)","Taka per kg","30 days")
  #fig3<-fig3+ annotate("text",pWValue$startdate+6,pWValue$value,label=pWValue$value, size = 4)
  #fig3<-fig3+ annotate("text",pRValue$startdate+6,pRValue$value,label=pRValue$value, size = 4)
  
  ggsave(
    paste(targetPath,"portalchart3",'.png',sep=""),
    fig3,
    width = 12 ,
    height =  6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 3 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")    
  })
}

## ATTA-LOOSE-FIGURES ##

fig4.generate<-function()
{ 
  tryCatch({
  fig4.wholesale<-GetAverageDataValues(245,10)/100.0
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig4.retail<-GetAverageDataValues(246,10)
  startDate<-GetDate30DaysBefore(reportDate)
  endDate<-reportDate
  fig4.wholesale<-ApplyTimeWindow(fig4.wholesale,startDate,endDate)
  fig4.retail<-ApplyTimeWindow(fig4.retail,startDate,endDate)
  #Figure 1 Data processing
  fig4.data<-as.data.frame(merge(fig4.retail,fig4.wholesale))
  pWValue<-as.data.frame(fig4.wholesale[length(fig4.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig4.retail[length(fig4.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format="%d-%b-%y")," is ",pRValue$value," Tk/Kg )    ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate, format="%d-%b-%y")," is ",pWValue$value," Tk/Kg )    ")
  colnames(fig4.data)<-c(reatilLable,wholesaleLable)
  fig4.data$startdate<-as.Date(rownames(fig4.data))
  fig4.data <- melt(fig4.data, id.vars="startdate")
  pWValue<-as.data.frame(fig4.wholesale[length(fig4.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig4.retail[length(fig4.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  #Figure 1 graph production
  fig4<-ReportGraphNoShape(fig4.data,"Figure 5. Changes in Atta-Loose (Dhaka)","Taka per kg","2 days")
  #fig4<-fig4+ annotate("text",pWValue$startdate+2,pWValue$value,label=pWValue$value, size = 4)
  #fig4<-fig4+ annotate("text",pRValue$startdate+2,pRValue$value,label=pRValue$value, size = 4)
  ggsave(
    paste(targetPath,"portalchart5",'.png',sep=""),
    fig4,
    width = 12 ,
    height =  6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 4 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

fig5.generate<-function()
{ 
  tryCatch({
  fig5.wholesale<-GetAverageDataValues(245,10)/100.0
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig5.retail<-GetAverageDataValues(246,10)
  startDate<-GetDate1QuarterBefore(reportDate)
  endDate<-reportDate
  fig5.wholesale<-ApplyTimeWindow(fig5.wholesale,startDate,endDate)
  fig5.retail<-ApplyTimeWindow(fig5.retail,startDate,endDate)
  #Figure 5 Data processing
  fig5.data<-as.data.frame(merge(fig5.retail,fig5.wholesale))
  pWValue<-as.data.frame(fig5.wholesale[length(fig5.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig5.retail[length(fig5.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format="%d-%b-%y")," is ",pRValue$value," Tk/Kg )    ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate, format="%d-%b-%y")," is ",pWValue$value," Tk/Kg )    ")
  colnames(fig5.data)<-c(reatilLable,wholesaleLable)
  fig5.data$startdate<-as.Date(rownames(fig5.data))
  fig5.data <- melt(fig5.data, id.vars="startdate")
  pWValue<-as.data.frame(fig5.wholesale[length(fig5.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig5.retail[length(fig5.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  #Figure 5 graph production
  fig5<-ReportGraphNoShape(fig5.data,"Figure 5. Changes in Atta-Loose (Dhaka)","Taka per kg","10 days")
  #fig5<-fig5+ annotate("text",pWValue$startdate+1,pWValue$value,label=pWValue$value, size = 4)
  #fig5<-fig5+ annotate("text",pRValue$startdate+1,pRValue$value,label=pRValue$value, size = 4)
  
  ggsave(
    paste(targetPath,"portalchart6",'.png',sep=""),
    fig5,
    width = 12 ,
    height =  6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 5 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

fig6.generate<-function()
{ 
  tryCatch({
  fig6.wholesale<-GetAverageDataValues(245,10)/100.0
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig6.retail<-GetAverageDataValues(246,10)
  startDate<-GetDate1YearBefore(reportDate)
  endDate<-reportDate
  fig6.wholesale<-ApplyTimeWindow(fig6.wholesale,startDate,endDate)
  fig6.retail<-ApplyTimeWindow(fig6.retail,startDate,endDate)
  #Figure 1 Data processing
  fig6.data<-as.data.frame(merge(fig6.retail,fig6.wholesale))
  pWValue<-as.data.frame(fig6.wholesale[length(fig6.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig6.retail[length(fig6.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format="%d-%b-%y")," is ",pRValue$value," Tk/Kg )    ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate, format="%d-%b-%y")," is ",pWValue$value," Tk/Kg )    ")
  colnames(fig6.data)<-c(reatilLable,wholesaleLable)
  fig6.data$startdate<-as.Date(rownames(fig6.data))
  fig6.data <- melt(fig6.data, id.vars="startdate")
  pWValue<-as.data.frame(fig6.wholesale[length(fig6.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig6.retail[length(fig6.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  #Figure 1 graph production
  fig6<-ReportGraphNoShape(fig6.data,"Figure 6. Changes in Atta-Loose (Dhaka)","Taka per kg","30 days")
  #fig6<-fig6+ annotate("text",pWValue$startdate+4,pWValue$value,label=pWValue$value, size = 4)
  #fig6<-fig6+ annotate("text",pRValue$startdate+4,pRValue$value,label=pRValue$value, size = 4)
  
  ggsave(
    paste(targetPath,"portalchart7",'.png',sep=""),
    fig6,
    width = 12 ,
    height =  6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 6 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

## LOCAL AND INTERNATIONAL PRICES - RICE ##
#Figure 7
fig7.generate<-function()
{
  tryCatch({
  startDate<-GetDate30DaysBefore(reportDate)
  endDate<-reportDate
  #Rice wholesale price
  #Dhaka City Coarse Rice wholesale DEID: [197 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
  #(DEID:363 AVG(CCID 8,9) average of buy and selling price)
  fig7.coarserice<-GetAverageDataValues(197,10)
  #Get the exchange rate
  fig7.xrate<-GetAverageDataValues(363,1)
  #Merge these two and calculate the final values
  fig7.coarserice<-fig7.coarserice/fig7.xrate*10
  fig7.kolkata<-GetAverageDataValues(710,1)
  fig7.xrate2<-GetAverageDataValues(711,1)
  fig7.kolkata<-fig7.kolkata/fig7.xrate2*10
  
  #Kolkata Rice  Price: data is from website http://fcainfoweb.nic.in/pms/interface3web.aspx 
  #Which is INR/QTL need to be converted to USD [Rice Wholesale Kolkata price (New DE) / Exchange rate of INR to USD (DEID: 366 AVG(CCID 8,9)) *10]
  #DENAME: Rice wholesale price 5% Thailand weekly DEID: 713
  fig7.thai5<-GetAverageDataValues(713,1)
  #DENAME: Rice wholesale price 5% parboiled India weekly DEID: 716
  fig7.ind5<-GetAverageDataValues(716,1)
  #Merge everything into a single data frame and rearrange
  
  #fig7.thai5<-ApplyTimeWindow(fig7.thai5,startDate,endDate)
  #fig7.coarserice<-ApplyTimeWindow(fig7.coarserice,startDate,endDate)
  #fig7.kolkata<-ApplyTimeWindow(fig7.kolkata,startDate,endDate)
  #fig7.ind5<-ApplyTimeWindow(fig7.ind5,startDate,endDate)
  
  foo<-NULL
  foo<-as.data.frame(merge(fig7.coarserice, fig7.kolkata, fig7.thai5 ,fig7.ind5))
  foo$startdate<-as.Date(rownames(foo))
  foo<-subset(foo,startdate>=startDate)
  foo<-subset(foo,startdate<=endDate)
  
  
  pCRValue<-as.data.frame(fig7.coarserice[length(fig7.coarserice)])
  pCRValue$startdate<-as.Date(rownames(pCRValue))
  pKValue<-as.data.frame(fig7.kolkata[length(fig7.kolkata)])
  pKValue$startdate<-as.Date(rownames(pKValue))
  pTValue<-as.data.frame(fig7.thai5[length(fig7.thai5)])
  pTValue$startdate<-as.Date(rownames(pTValue))
  pIValue<-as.data.frame(fig7.ind5[length(fig7.ind5)])
  pIValue$startdate<-as.Date(rownames(pIValue))
  
  dcLable = paste("Dhaka Wholesale PB ( Price  on"," ",format(pCRValue$startdate, format="%d-%b-%y")," is ",format(round(pCRValue$value,2))," USD/MT )      ")
  kLable = paste("Kolkata Wholesale PB ( Price  on"," ",format(pKValue$startdate, format="%d-%b-%y")," is ",format(round(pKValue$value,2))," USD/MT )      ")
  tLable = paste("Thai 5% PB ( Price  on"," ",format(pTValue$startdate, format="%d-%b-%y")," is ",format(round(pTValue$value,2))," USD/MT )      ")
  iLable = paste("India 5% PB ( Price  on"," ",format(pIValue$startdate, format="%d-%b-%y")," is ",format(round(pIValue$value,2))," USD/MT )      ")
  
  colnames(foo)<-c(dcLable, kLable, tLable, iLable, "startdate")
  
  fig7.data<-melt(foo,id.vars="startdate")
  
  fig7<-ReportGraphNoShape(fig7.data,"Rice wholesale price in Dhaka, Kolkata and FOB Prices in relevant international markets",
                           "USD per MT","2 days")
  fig7<-fig7 + guides(col = guide_legend(nrow = 2))
  ggsave(
    paste(targetPath,"portalchart9",'.png',sep=""),
    fig7,
    width = 12 ,
    height = 6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 7 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

#Figure 8
fig8.generate<-function()
{
  tryCatch({
    startDate<-GetDate1QuarterBefore(reportDate)
    endDate<-reportDate
    #Rice wholesale price
    #Dhaka City Coarse Rice wholesale DEID: [197 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
    #(DEID:363 AVG(CCID 8,9) average of buy and selling price)
    fig8.coarserice<-GetAverageDataValues(197,10)
    #Get the exchange rate
    fig8.xrate<-GetAverageDataValues(363,1)
    #Merge these two and calculate the final values
    fig8.coarserice<-fig8.coarserice/fig8.xrate*10
    fig8.kolkata<-GetAverageDataValues(710,1)
    fig8.xrate2<-GetAverageDataValues(711,1)
    fig8.kolkata<-fig8.kolkata/fig8.xrate2*10
    
    #Kolkata Rice  Price: data is from website http://fcainfoweb.nic.in/pms/interface3web.aspx 
    #Which is INR/QTL need to be converted to USD [Rice Wholesale Kolkata price (New DE) / Exchange rate of INR to USD (DEID: 366 AVG(CCID 8,9)) *10]
    #DENAME: Rice wholesale price 5% Thailand weekly DEID: 713
    fig8.thai5<-GetAverageDataValues(713,1)
    #DENAME: Rice wholesale price 5% parboiled India weekly DEID: 716
    fig8.ind5<-GetAverageDataValues(716,1)
    #Merge everything into a single data frame and rearrange
    
    fig8.thai5<-ApplyTimeWindow(fig8.thai5,startDate,endDate)
    fig8.coarserice<-ApplyTimeWindow(fig8.coarserice,startDate,endDate)
    fig8.kolkata<-ApplyTimeWindow(fig8.kolkata,startDate,endDate)
    fig8.ind5<-ApplyTimeWindow(fig8.ind5,startDate,endDate)
    
    foo<-NULL
    foo<-as.data.frame(merge(fig8.coarserice, fig8.kolkata, fig8.thai5 ,fig8.ind5))
    foo$startdate<-as.Date(rownames(foo))
    foo<-subset(foo,startdate>=startDate)
    foo<-subset(foo,startdate<=endDate)
    pCRValue<-as.data.frame(fig8.coarserice[length(fig8.coarserice)])
    pCRValue$startdate<-as.Date(rownames(pCRValue))
    pKValue<-as.data.frame(fig8.kolkata[length(fig8.kolkata)])
    pKValue$startdate<-as.Date(rownames(pKValue))
    pTValue<-as.data.frame(fig8.thai5[length(fig8.thai5)])
    pTValue$startdate<-as.Date(rownames(pTValue))
    pIValue<-as.data.frame(fig8.ind5[length(fig8.ind5)])
    pIValue$startdate<-as.Date(rownames(pIValue))
    
    dcLable = paste("Dhaka Wholesale PB ( Price  on"," ",format(pCRValue$startdate, format="%d-%b-%y")," is ",format(round(pCRValue$value,2))," USD/MT )      ")
    kLable = paste("Kolkata Wholesale PB ( Price  on"," ",format(pKValue$startdate, format="%d-%b-%y")," is ",format(round(pKValue$value,2))," USD/MT )      ")
    tLable = paste("Thai 5% PB ( Price  on"," ",format(pTValue$startdate, format="%d-%b-%y")," is ",format(round(pTValue$value,2))," USD/MT )      ")
    iLable = paste("India 5% PB ( Price  on"," ",format(pIValue$startdate, format="%d-%b-%y")," is ",format(round(pIValue$value,2))," USD/MT )      ")
    
    colnames(foo)<-c(dcLable, kLable, tLable, iLable, "startdate")
    fig8.data<-melt(foo,id.vars="startdate")
    
    pCRValue<-as.data.frame(fig8.coarserice[length(fig8.coarserice)])
    pCRValue$startdate<-as.Date(rownames(pCRValue))
    pKValue<-as.data.frame(fig8.kolkata[length(fig8.kolkata)])
    pKValue$startdate<-as.Date(rownames(pKValue))
    pTValue<-as.data.frame(fig8.thai5[length(fig8.thai5)])
    pTValue$startdate<-as.Date(rownames(pTValue))
    pIValue<-as.data.frame(fig8.ind5[length(fig8.ind5)])
    pIValue$startdate<-as.Date(rownames(pIValue))
    
    fig8<-ReportGraphNoShape(fig8.data,"Rice wholesale price in Dhaka, Kolkata and FOB Prices in relevant international markets",
                             "USD per MT","5 days")
    fig8<-fig8 + guides(col = guide_legend(nrow = 2))
    
    #fig8<-fig8+ annotate("text",pCRValue$startdate+4,pCRValue$value,label=format(round(pCRValue$value,2), nsmall = 2), size = 4)
    #fig8<-fig8+ annotate("text",pKValue$startdate+4,pKValue$value,label=format(round(pKValue$value,2), nsmall = 2), size = 4)
    #fig8<-fig8+ annotate("text",pTValue$startdate+4,pTValue$value,label=format(round(pTValue$value,2), nsmall = 2), size = 4)
    #fig8<-fig8+ annotate("text",pIValue$startdate+4,pIValue$value,label=format(round(pIValue$value,2), nsmall = 2), size = 4)
    
    #Figure 8 Graph production
    ggsave(
      paste(targetPath,"portalchart10",'.png',sep=""),
      fig8,
      width = 12 ,
      height = 6,
      units = "in",
      dpi = 200
    )
  },error=function(e){
    message(" Fig. 8 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

#Figure 9
fig9.generate<-function()
{
  tryCatch({
  startDate<-GetDate1YearBefore(reportDate)
  endDate<-reportDate
  #Rice wholesale price
  #Dhaka City Coarse Rice wholesale DEID: [197 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
  #(DEID:363 AVG(CCID 8,9) average of buy and selling price)
  fig9.coarserice<-GetAverageDataValues(197,10)
  #Get the exchange rate
  fig9.xrate<-GetAverageDataValues(363,1)
  #Merge these two and calculate the final values
  fig9.coarserice<-fig9.coarserice/fig9.xrate*10
  fig9.kolkata<-GetAverageDataValues(710,1)
  fig9.xrate2<-GetAverageDataValues(711,1)
  fig9.kolkata<-fig9.kolkata/fig9.xrate2*10
  
  #Kolkata Rice  Price: data is from website http://fcainfoweb.nic.in/pms/interface3web.aspx 
  #Which is INR/QTL need to be converted to USD [Rice Wholesale Kolkata price (New DE) / Exchange rate of INR to USD (DEID: 366 AVG(CCID 8,9)) *10]
  #DENAME: Rice wholesale price 5% Thailand weekly DEID: 713
  fig9.thai5<-GetAverageDataValues(713,1)
  #DENAME: Rice wholesale price 5% parboiled India weekly DEID: 716
  fig9.ind5<-GetAverageDataValues(716,1)
  #Merge everything into a single data frame and rearrange
  
  fig9.thai5<-ApplyTimeWindow(fig9.thai5,startDate,endDate)
  fig9.coarserice<-ApplyTimeWindow(fig9.coarserice,startDate,endDate)
  fig9.kolkata<-ApplyTimeWindow(fig9.kolkata,startDate,endDate)
  fig9.ind5<-ApplyTimeWindow(fig9.ind5,startDate,endDate)
  
  foo<-NULL
  foo<-as.data.frame(merge(fig9.coarserice, fig9.kolkata, fig9.thai5 ,fig9.ind5))
  foo$startdate<-as.Date(rownames(foo))
  foo<-subset(foo,startdate>=startDate)
  foo<-subset(foo,startdate<=endDate)
  
  pCRValue<-as.data.frame(fig9.coarserice[length(fig9.coarserice)])
  pCRValue$startdate<-as.Date(rownames(pCRValue))
  pKValue<-as.data.frame(fig9.kolkata[length(fig9.kolkata)])
  pKValue$startdate<-as.Date(rownames(pKValue))
  pTValue<-as.data.frame(fig9.thai5[length(fig9.thai5)])
  pTValue$startdate<-as.Date(rownames(pTValue))
  pIValue<-as.data.frame(fig9.ind5[length(fig9.ind5)])
  pIValue$startdate<-as.Date(rownames(pIValue))
  
  dcLable = paste("Dhaka Wholesale PB ( Price  on"," ",format(pCRValue$startdate, format="%d-%b-%y")," is ",format(round(pCRValue$value,2))," USD/MT )      ")
  kLable = paste("Kolkata Wholesale PB ( Price  on"," ",format(pKValue$startdate, format="%d-%b-%y")," is ",format(round(pKValue$value,2))," USD/MT )      ")
  tLable = paste("Thai 5% PB ( Price  on"," ",format(pTValue$startdate, format="%d-%b-%y")," is ",format(round(pTValue$value,2))," USD/MT )      ")
  iLable = paste("India 5% PB ( Price  on"," ",format(pIValue$startdate, format="%d-%b-%y")," is ",format(round(pIValue$value,2))," USD/MT )      ")
  
  colnames(foo)<-c(dcLable, kLable, tLable, iLable, "startdate")
  fig9.data<-melt(foo,id.vars="startdate")
  
  pCRValue<-as.data.frame(fig9.coarserice[length(fig9.coarserice)])
  pCRValue$startdate<-as.Date(rownames(pCRValue))
  pKValue<-as.data.frame(fig9.kolkata[length(fig9.kolkata)])
  pKValue$startdate<-as.Date(rownames(pKValue))
  pTValue<-as.data.frame(fig9.thai5[length(fig9.thai5)])
  pTValue$startdate<-as.Date(rownames(pTValue))
  pIValue<-as.data.frame(fig9.ind5[length(fig9.ind5)])
  pIValue$startdate<-as.Date(rownames(pIValue))
  
  fig9<-ReportGraphNoShape(fig9.data,"Rice wholesale price in Dhaka, Kolkata and FOB Prices in relevant international markets",
                           "USD per MT","30 days")
  fig9<-fig9 + guides(col = guide_legend(nrow = 2))
  
  #fig9<-fig9+ annotate("text",pCRValue$startdate+4,pCRValue$value,label=format(round(pCRValue$value,2), nsmall = 2), size = 4)
  #fig9<-fig9+ annotate("text",pKValue$startdate+4,pKValue$value,label=format(round(pKValue$value,2), nsmall = 2), size = 4)
  #fig9<-fig9+ annotate("text",pTValue$startdate+4,pTValue$value,label=format(round(pTValue$value,2), nsmall = 2), size = 4)
  #fig9<-fig9+ annotate("text",pIValue$startdate+4,pIValue$value,label=format(round(pIValue$value,2), nsmall = 2), size = 4)
  
  #Figure 9 Graph production
  ggsave(
    paste(targetPath,"portalchart11",'.png',sep=""),
    fig9,
    width = 12 ,
    height = 6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 9 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

## LOCAL AND INTERNATIONAL PRICES - WHEAT ##
#Figure 10
fig10.generate<-function()
{
  
  tryCatch({
    startDate<-GetDate30DaysBefore(reportDate)
    endDate<-reportDate
    # wholesale price
    #Dhaka City Wheat wholesale DEID: [243 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
    #(DEID:363 AVG(CCID 8,9) average of buy and selling price)
    fig10.wheatlocal<-GetAverageDataValues(243,10)
    #Get the exchange rate
    fig10.xrate<-GetAverageDataValues(363,1)
    #Merge these two and calculate the final values
    fig10.wheatlocal<-fig10.wheatlocal/fig10.xrate*10
    
    #DENAME: Wheat Soft Red Winter weekly DEID: 356
    fig10.wsoft<-GetAverageDataValues(356,1)
    #DENAME: Wheat Hard Red Winter weekly DEID: 441
    fig10.whard<-GetAverageDataValues(716,1)
    #Merge everything into a single data frame and rearrange
    #fig10.wheatlocal<-ApplyTimeWindow(fig10.wheatlocal,startDate,endDate)
    #fig10.xrate<-ApplyTimeWindow(fig10.xrate,startDate,endDate)
    #fig10.wsoft<-ApplyTimeWindow(fig10.wsoft,startDate,endDate)
    #fig10.whard<-ApplyTimeWindow(fig10.whard,startDate,endDate)
    
    foo<-NULL
    foo<-as.data.frame(merge(fig10.wheatlocal, fig10.wsoft ,fig10.whard))
    foo$startdate<-as.Date(rownames(foo))
    foo<-subset(foo,startdate>=startDate)
    foo<-subset(foo,startdate<=endDate)
    pWLValue<-as.data.frame(fig10.wheatlocal[length(fig10.wheatlocal)])
    pWLValue$startdate<-as.Date(rownames(pWLValue))
    pWSValue<-as.data.frame(fig10.wsoft[length(fig10.wsoft)])
    pWSValue$startdate<-as.Date(rownames(pWSValue))
    pWHValue<-as.data.frame(fig10.whard[length(fig10.whard)])
    pWHValue$startdate<-as.Date(rownames(pWHValue))
    
    wlLable = paste("Kolkata Wholesale PB ( Price  on"," ",format(pWLValue$startdate, format="%d-%b-%y")," is ",format(round(pWLValue$value,2))," USD/MT )      ")
    wsLable = paste("Thai 5% PB ( Price  on"," ",format(pWSValue$startdate, format="%d-%b-%y")," is ",format(round(pWSValue$value,2))," USD/MT )      ")
    whLable = paste("India 5% PB ( Price  on"," ",format(pWHValue$startdate, format="%d-%b-%y")," is ",format(round(pWHValue$value,2))," USD/MT )      ")
    
    colnames(foo)<-c(wlLable, wsLable, whLable, "startdate")
    fig10.data<-melt(foo,id.vars="startdate")
    
    pWLValue<-as.data.frame(fig10.wheatlocal[length(fig10.wheatlocal)])
    pWLValue$startdate<-as.Date(rownames(pWLValue))
    pWSValue<-as.data.frame(fig10.wsoft[length(fig10.wsoft)])
    pWSValue$startdate<-as.Date(rownames(pWSValue))
    pWHValue<-as.data.frame(fig10.whard[length(fig10.whard)])
    pWHValue$startdate<-as.Date(rownames(pWHValue))
    
    fig10<-ReportGraphNoShape(fig10.data,"Wheat wholesale price in Dhaka and  wholesale pices in relevant international markets",
                              "USD per MT","2 days")
    fig10<-fig10 + guides(col = guide_legend(nrow = 2))
    
    #fig10<-fig10+ annotate("text",pWLValue$startdate+2,pWLValue$value,label=format(round(pWLValue$value,2), nsmall = 2), size = 4)
    #fig10<-fig10+ annotate("text",pWSValue$startdate+2,pWSValue$value,label=format(round(pWSValue$value,2), nsmall = 2), size = 4)
    #fig10<-fig10+ annotate("text",pWHValue$startdate+2,pWHValue$value,label=format(round(pWHValue$value,2), nsmall = 2), size = 4)
    #Figure 10 Graph production
    ggsave(
      paste(targetPath,"portalchart13",'.png',sep=""),
      fig10,
      width = 12 ,
      height = 6,
      units = "in",
      dpi = 200
    )
  },error=function(e){
    message(" Fig. 10 Failed")
    print(e)
    #print("an error occurred-----")
  })  
}

#Figure 11
fig11.generate<-function()
{
  tryCatch({
    startDate<-GetDate1QuarterBefore(reportDate)
    endDate<-reportDate
    # wholesale price
    #Dhaka City Wheat wholesale DEID: [243 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
    #(DEID:363 AVG(CCID 8,9) average of buy and selling price)
    fig11.wheatlocal<-GetAverageDataValues(243,10)
    #Get the exchange rate
    fig11.xrate<-GetAverageDataValues(363,1)
    #Merge these two and calculate the final values
    fig11.wheatlocal<-fig11.wheatlocal/fig11.xrate*10
    
    #DENAME: Wheat Soft Red Winter weekly DEID: 356
    fig11.wsoft<-GetAverageDataValues(356,1)
    #DENAME: Wheat Hard Red Winter weekly DEID: 441
    fig11.whard<-GetAverageDataValues(716,1)
    #Merge everything into a single data frame and rearrange
    
    #fig11.wheatlocal<-ApplyTimeWindow(fig11.wheatlocal,startDate,endDate)
    #fig11.xrate<-ApplyTimeWindow(fig11.xrate,startDate,endDate)
    #fig11.wsoft<-ApplyTimeWindow(fig11.wsoft,startDate,endDate)
    #fig11.whard<-ApplyTimeWindow(fig11.whard,startDate,endDate)
    
    
    foo<-NULL
    foo<-as.data.frame(merge(fig11.wheatlocal, fig11.wsoft ,fig11.whard))
    foo$startdate<-as.Date(rownames(foo))
    foo<-subset(foo,startdate>=startDate)
    foo<-subset(foo,startdate<=endDate)
    pWLValue<-as.data.frame(fig11.wheatlocal[length(fig11.wheatlocal)])
    pWLValue$startdate<-as.Date(rownames(pWLValue))
    pWSValue<-as.data.frame(fig11.wsoft[length(fig11.wsoft)])
    pWSValue$startdate<-as.Date(rownames(pWSValue))
    pWHValue<-as.data.frame(fig11.whard[length(fig11.whard)])
    pWHValue$startdate<-as.Date(rownames(pWHValue))
    wlLable = paste("Kolkata Wholesale PB ( Price  on"," ",format(pWLValue$startdate, format="%d-%b-%y")," is ",format(round(pWLValue$value,2))," USD/MT )      ")
    wsLable = paste("Thai 5% PB ( Price  on"," ",format(pWSValue$startdate, format="%d-%b-%y")," is ",format(round(pWSValue$value,2))," USD/MT )      ")
    whLable = paste("India 5% PB ( Price  on"," ",format(pWHValue$startdate, format="%d-%b-%y")," is ",format(round(pWHValue$value,2))," USD/MT )      ")
    colnames(foo)<-c(wlLable, wsLable, whLable, "startdate")
    fig11.data<-melt(foo,id.vars="startdate")
    
    pWLValue<-as.data.frame(fig11.wheatlocal[length(fig11.wheatlocal)])
    pWLValue$startdate<-as.Date(rownames(pWLValue))
    pWSValue<-as.data.frame(fig11.wsoft[length(fig11.wsoft)])
    pWSValue$startdate<-as.Date(rownames(pWSValue))
    pWHValue<-as.data.frame(fig11.whard[length(fig11.whard)])
    pWHValue$startdate<-as.Date(rownames(pWHValue))
    
    fig11<-ReportGraphNoShape(fig11.data,"Wheat wholesale price in Dhaka and  wholesale pices in relevant international markets",
                              "USD per MT","10 days")
    fig11<-fig11 + guides(col = guide_legend(nrow = 2))
    
    #fig11<-fig11+ annotate("text",pWLValue$startdate+4,pWLValue$value,label=format(round(pWLValue$value,2), nsmall = 2), size = 4)
    #fig11<-fig11+ annotate("text",pWSValue$startdate+4,pWSValue$value,label=format(round(pWSValue$value,2), nsmall = 2), size = 4)
    #fig11<-fig11+ annotate("text",pWHValue$startdate+4,pWHValue$value,label=format(round(pWHValue$value,2), nsmall = 2), size = 4)
    
    
    #Figure 10 Graph production
    
    ggsave(
      paste(targetPath,"portalchart14",'.png',sep=""),
      fig11,
      width = 12 ,
      height = 6,
      units = "in",
      dpi = 200
    )
    
  },error=function(e){
    message(" Fig. 11 Failed")
    #print("an error occurred-----")
  }) 
}

#Figure 12
fig12.generate<-function()
{
  tryCatch({
  startDate<-GetDate1YearBefore(reportDate)
  endDate<-reportDate
  # wholesale price
  #Dhaka City Wheat wholesale DEID: [243 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
  #(DEID:363 AVG(CCID 8,9) average of buy and selling price)
  fig12.wheatlocal<-GetAverageDataValues(243,10)
  #Get the exchange rate
  fig12.xrate<-GetAverageDataValues(363,1)
  #Merge these two and calculate the final values
  fig12.wheatlocal<-fig12.wheatlocal/fig12.xrate*10
  
  #DENAME: Wheat Soft Red Winter weekly DEID: 356
  fig12.wsoft<-GetAverageDataValues(356,1)
  #DENAME: Wheat Hard Red Winter weekly DEID: 441
  fig12.whard<-GetAverageDataValues(716,1)
  #Merge everything into a single data frame and rearrange
  
  fig12.wheatlocal<-ApplyTimeWindow(fig12.wheatlocal,startDate,endDate)
  fig12.xrate<-ApplyTimeWindow(fig12.xrate,startDate,endDate)
  fig12.wsoft<-ApplyTimeWindow(fig12.wsoft,startDate,endDate)
  fig12.whard<-ApplyTimeWindow(fig12.whard,startDate,endDate)
  
  foo<-NULL
  foo<-as.data.frame(merge(fig12.wheatlocal, fig12.wsoft ,fig12.whard))
  foo$startdate<-as.Date(rownames(foo))
  foo<-subset(foo,startdate>=startDate)
  foo<-subset(foo,startdate<=endDate)
  pWLValue<-as.data.frame(fig12.wheatlocal[length(fig12.wheatlocal)])
  pWLValue$startdate<-as.Date(rownames(pWLValue))
  pWSValue<-as.data.frame(fig12.wsoft[length(fig12.wsoft)])
  pWSValue$startdate<-as.Date(rownames(pWSValue))
  pWHValue<-as.data.frame(fig12.whard[length(fig12.whard)])
  pWHValue$startdate<-as.Date(rownames(pWHValue))
  wlLable = paste("Kolkata Wholesale PB ( Price  on"," ",format(pWLValue$startdate, format="%d-%b-%y")," is ",format(round(pWLValue$value,2))," USD/MT )      ")
  wsLable = paste("Thai 5% PB ( Price  on"," ",format(pWSValue$startdate, format="%d-%b-%y")," is ",format(round(pWSValue$value,2))," USD/MT )      ")
  whLable = paste("India 5% PB ( Price  on"," ",format(pWHValue$startdate, format="%d-%b-%y")," is ",format(round(pWHValue$value,2))," USD/MT )      ")
  colnames(foo)<-c(wlLable, wsLable, whLable, "startdate")
  fig12.data<-melt(foo,id.vars="startdate")
  
  
  fig12<-ReportGraphNoShape(fig12.data,"Wheat wholesale price in Dhaka and  wholesale pices in relevant international markets",
                            "USD per MT","30 days")
  fig12<-fig12 + guides(col = guide_legend(nrow = 2))
  
  #fig12<-fig12+ annotate("text",pWLValue$startdate+4,pWLValue$value,label=format(round(pWLValue$value,2), nsmall = 2), size = 4)
  #fig12<-fig12+ annotate("text",pWSValue$startdate+4,pWSValue$value,label=format(round(pWSValue$value,2), nsmall = 2), size = 4)
  #fig12<-fig12+ annotate("text",pWHValue$startdate+4,pWHValue$value,label=format(round(pWHValue$value,2), nsmall = 2), size = 4)
  
  #Figure 10 Graph production
  ggsave(
    paste(targetPath,"portalchart15",'.png',sep=""),
    fig12,
    width = 12 ,
    height = 6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 12 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

#Figure 13
fig13.generate<-function()
{  
  tryCatch({
  fig13.wholesale<-GetAverageDataValues(197,10)/100.0
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig13.retail<-GetAverageDataValues(198,10)
  startDate<-as.Date("01/01/2008","%d/%m/%Y")
  endDate<-reportDate
  fig13.wholesale<-ApplyTimeWindow(fig13.wholesale,startDate,endDate)
  fig13.retail<-ApplyTimeWindow(fig13.retail,startDate,endDate)
  #Figure 1 Data processing
  fig13.data<-as.data.frame(merge(fig13.retail,fig13.wholesale))
  pWValue<-as.data.frame(fig13.wholesale[length(fig13.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig13.retail[length(fig13.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format='%d-%b-%y')," is ",pRValue$value," Tk/Kg )    ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate, format='%d-%b-%y')," is ",pWValue$value," Tk/Kg )    ")
  colnames(fig13.data)<-c(reatilLable,wholesaleLable)
  fig13.data$startdate<-as.Date(rownames(fig13.data))
  fig13.data <- melt(fig13.data, id.vars="startdate")
  
  pWValue<-as.data.frame(fig13.wholesale[length(fig13.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig13.retail[length(fig13.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  #Figure 1 graph production
  fig13<-ReportGraphNoShape(fig13.data,"Figure 1. Changes in Coarse Rice (Dhaka)","Taka per kg","120 days")
  #fig13<-fig13+ annotate("text",pWValue$startdate+2,pWValue$value,label=pWValue$value, size = 4)
  #fig13<-fig13+ annotate("text",pRValue$startdate+2,pRValue$value,label=pRValue$value, size = 4)
  
  ggsave(
    paste(targetPath,"portalchart4",'.png',sep=""),
    fig13,
    width = 12 ,
    height =  6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 13 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

#Figure 14
fig14.generate<-function()
{ 
  tryCatch({
  fig14.wholesale<-GetAverageDataValues(245,10)/100.0
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig14.retail<-GetAverageDataValues(246,10)
  startDate<-as.Date("01/01/2008","%d/%m/%Y")
  endDate<-reportDate
  fig14.wholesale<-ApplyTimeWindow(fig14.wholesale,startDate,endDate)
  fig14.retail<-ApplyTimeWindow(fig14.retail,startDate,endDate)
  #Figure 1 Data processing
  fig14.data<-as.data.frame(merge(fig14.retail,fig14.wholesale))
  pWValue<-as.data.frame(fig14.wholesale[length(fig14.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig14.retail[length(fig14.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format='%d-%b-%y')," is ",pRValue$value," Tk/Kg )    ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate, format='%d-%b-%y')," is ",pWValue$value," Tk/Kg )    ")
  colnames(fig14.data)<-c(reatilLable,wholesaleLable)
  fig14.data$startdate<-as.Date(rownames(fig14.data))
  fig14.data <- melt(fig14.data, id.vars="startdate")
  
  pWValue<-as.data.frame(fig14.wholesale[length(fig14.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig14.retail[length(fig14.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  
  #Figure 1 graph production
  fig14<-ReportGraphNoShape(fig14.data,"Figure 5. Changes in Atta-Loose (Dhaka)","Taka per kg","120 days")
  #fig14<-fig14+ annotate("text",pWValue$startdate+2,pWValue$value,label=pWValue$value, size = 4)
  #fig14<-fig14+ annotate("text",pRValue$startdate+2,pRValue$value,label=pRValue$value, size = 4)
  
  ggsave(
    paste(targetPath,"portalchart8",'.png',sep=""),
    fig14,
    width = 12 ,
    height =  6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 14 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

#Figure 15
fig15.generate<-function()
{
  tryCatch({
  startDate<-as.Date("01/01/2008", "%d/%m/%Y")
  endDate<-reportDate
  #Rice wholesale price
  #Dhaka City Coarse Rice wholesale DEID: [197 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
  #(DEID:363 AVG(CCID 8,9) average of buy and selling price)
  fig15.coarserice<-GetAverageDataValues(197,10)
  #Get the exchange rate
  fig15.xrate<-GetAverageDataValues(363,1)
  #Merge these two and calculate the final values
  fig15.coarserice<-fig15.coarserice/fig15.xrate*10
  fig15.kolkata<-GetAverageDataValues(710,1)
  fig15.xrate2<-GetAverageDataValues(711,1)
  fig15.kolkata<-fig15.kolkata/fig15.xrate2*10
  
  #Kolkata Rice  Price: data is from website http://fcainfoweb.nic.in/pms/interface3web.aspx 
  #Which is INR/QTL need to be converted to USD [Rice Wholesale Kolkata price (New DE) / Exchange rate of INR to USD (DEID: 366 AVG(CCID 8,9)) *10]
  #DENAME: Rice wholesale price 5% Thailand weekly DEID: 713
  fig15.thai5<-GetAverageDataValues(713,1)
  #DENAME: Rice wholesale price 5% parboiled India weekly DEID: 716
  fig15.ind5<-GetAverageDataValues(716,1)
  #Merge everything into a single data frame and rearrange
  
  fig15.thai5<-ApplyTimeWindow(fig15.thai5,startDate,endDate)
  fig15.coarserice<-ApplyTimeWindow(fig15.coarserice,startDate,endDate)
  fig15.kolkata<-ApplyTimeWindow(fig15.kolkata,startDate,endDate)
  fig15.ind5<-ApplyTimeWindow(fig15.ind5,startDate,endDate)
  
  foo<-NULL
  foo<-as.data.frame(merge(fig15.coarserice, fig15.kolkata, fig15.thai5 ,fig15.ind5))
  foo$startdate<-as.Date(rownames(foo))
  foo<-subset(foo,startdate>=startDate)
  foo<-subset(foo,startdate<=endDate)
  pCRValue<-as.data.frame(fig15.coarserice[length(fig15.coarserice)])
  pCRValue$startdate<-as.Date(rownames(pCRValue))
  pKValue<-as.data.frame(fig15.kolkata[length(fig15.kolkata)])
  pKValue$startdate<-as.Date(rownames(pKValue))
  pTValue<-as.data.frame(fig15.thai5[length(fig15.thai5)])
  pTValue$startdate<-as.Date(rownames(pTValue))
  pIValue<-as.data.frame(fig15.ind5[length(fig15.ind5)])
  pIValue$startdate<-as.Date(rownames(pIValue))
  dcLable = paste("Dhaka Wholesale PB ( Price  on"," ",format(pCRValue$startdate, format='%d-%b-%y')," is ",format(round(pCRValue$value,2))," USD/MT )      ")
  kLable = paste("Kolkata Wholesale PB ( Price  on"," ",format(pKValue$startdate, format='%d-%b-%y')," is ",format(round(pKValue$value,2))," USD/MT )      ")
  tLable = paste("Thai 5% PB ( Price  on"," ",format(pTValue$startdate, format='%d-%b-%y')," is ",format(round(pTValue$value,2))," USD/MT )      ")
  iLable = paste("India 5% PB ( Price  on"," ",format(pIValue$startdate, format='%d-%b-%y')," is ",format(round(pIValue$value,2))," USD/MT )      ")
  
  colnames(foo)<-c(dcLable, kLable, tLable, iLable, "startdate")
  fig15.data<-melt(foo,id.vars="startdate")
  
  pCRValue<-as.data.frame(fig15.coarserice[length(fig15.coarserice)])
  pCRValue$startdate<-as.Date(rownames(pCRValue))
  pKValue<-as.data.frame(fig15.kolkata[length(fig15.kolkata)])
  pKValue$startdate<-as.Date(rownames(pKValue))
  pTValue<-as.data.frame(fig15.thai5[length(fig15.thai5)])
  pTValue$startdate<-as.Date(rownames(pTValue))
  pIValue<-as.data.frame(fig15.ind5[length(fig15.ind5)])
  pIValue$startdate<-as.Date(rownames(pIValue))
  
  fig15<-ReportGraphNoShape(fig15.data,"Rice wholesale price in Dhaka, Kolkata and FOB Prices in relevant international markets",
                            "USD per MT","120 days")
  fig15<-fig15 + guides(col = guide_legend(nrow = 2))
  
  #fig15<-fig15+ annotate("text",pCRValue$startdate+2,pCRValue$value,label=format(round(pCRValue$value,2), nsmall = 2), size = 4)
  #fig15<-fig15+ annotate("text",pKValue$startdate+2,pKValue$value,label=format(round(pKValue$value,2), nsmall = 2), size = 4)
  #fig15<-fig15+ annotate("text",pTValue$startdate+2,pTValue$value,label=format(round(pTValue$value,2), nsmall = 2), size = 4)
  #fig15<-fig15+ annotate("text",pIValue$startdate+2,pIValue$value,label=format(round(pIValue$value,2), nsmall = 2), size = 4)
  
  #Figure 7 Graph production
  ggsave(
    paste(targetPath,"portalchart12",'.png',sep=""),
    fig15,
    width = 12 ,
    height = 6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 15 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}


#Figure 16
fig16.generate<-function()
{
  tryCatch({
  startDate<-as.Date("20/09/2008","%d/%m/%Y")
  endDate<-reportDate
  # wholesale price
  #Dhaka City Wheat wholesale DEID: [243 (Avg of min/max (CCID 2,3)) ] * 10 / exchange rate of taka to usd 
  #(DEID:363 AVG(CCID 8,9) average of buy and selling price)
  fig16.wheatlocal<-GetAverageDataValues(243,10)
  #Get the exchange rate
  fig16.xrate<-GetAverageDataValues(363,1)
  #Merge these two and calculate the final values
  fig16.wheatlocal<-fig16.wheatlocal/fig16.xrate*10
  
  #DENAME: Wheat Soft Red Winter weekly DEID: 356
  fig16.wsoft<-GetAverageDataValues(356,1)
  #DENAME: Wheat Hard Red Winter weekly DEID: 441
  fig16.whard<-GetAverageDataValues(716,1)
  #Merge everything into a single data frame and rearrange
  
  fig16.wheatlocal<-ApplyTimeWindow(fig16.wheatlocal,startDate,endDate)
  fig16.xrate<-ApplyTimeWindow(fig16.xrate,startDate,endDate)
  fig16.wsoft<-ApplyTimeWindow(fig16.wsoft,startDate,endDate)
  fig16.whard<-ApplyTimeWindow(fig16.whard,startDate,endDate)
  
  foo<-NULL
  foo<-as.data.frame(merge(fig16.wheatlocal, fig16.wsoft ,fig16.whard))
  foo$startdate<-as.Date(rownames(foo))
  foo<-subset(foo,startdate>=startDate)
  foo<-subset(foo,startdate<=endDate)
  pWLValue<-as.data.frame(fig16.wheatlocal[length(fig16.wheatlocal)])
  pWLValue$startdate<-as.Date(rownames(pWLValue))
  pWSValue<-as.data.frame(fig16.wsoft[length(fig16.wsoft)])
  pWSValue$startdate<-as.Date(rownames(pWSValue))
  pWHValue<-as.data.frame(fig16.whard[length(fig16.whard)])
  pWHValue$startdate<-as.Date(rownames(pWHValue))
  wlLable = paste("Kolkata Wholesale PB ( Price  on"," ",format(pWLValue$startdate, format='%d-%b-%y')," is ",format(round(pWLValue$value,2))," USD/MT )      ")
  wsLable = paste("Thai 5% PB ( Price  on"," ",format(pWSValue$startdate, format='%d-%b-%y')," is ",format(round(pWSValue$value,2))," USD/MT )      ")
  whLable = paste("India 5% PB ( Price  on"," ",format(pWHValue$startdate, format='%d-%b-%y')," is ",format(round(pWHValue$value,2))," USD/MT )      ")
  
  colnames(foo)<-c(wlLable, wsLable, whLable, "startdate")
  fig16.data<-melt(foo,id.vars="startdate")
  
  pWLValue<-as.data.frame(fig16.wheatlocal[length(fig16.wheatlocal)])
  pWLValue$startdate<-as.Date(rownames(pWLValue))
  pWSValue<-as.data.frame(fig16.wsoft[length(fig16.wsoft)])
  pWSValue$startdate<-as.Date(rownames(pWSValue))
  pWHValue<-as.data.frame(fig16.whard[length(fig16.whard)])
  pWHValue$startdate<-as.Date(rownames(pWHValue))
  
  fig16<-ReportGraphNoShape(fig16.data,"Wheat wholesale price in Dhaka and  wholesale pices in relevant international markets",
                            "USD per MT","120 days")
  fig16<-fig16 + guides(col = guide_legend(nrow = 2))
  
  #fig16<-fig16+ annotate("text",pWLValue$startdate+2,pWLValue$value,label=format(round(pWLValue$value,2), nsmall = 2), size = 4)
  #fig16<-fig16+ annotate("text",pWSValue$startdate+2,pWSValue$value,label=format(round(pWSValue$value,2), nsmall = 2), size = 4)
  #fig16<-fig16+ annotate("text",pWHValue$startdate+2,pWHValue$value,label=format(round(pWHValue$value,2), nsmall = 2), size = 4)
  
  #Figure 10 Graph production
  ggsave(
    paste(targetPath,"portalchart16",'.png',sep=""),
    fig16,
    width = 12 ,
    height = 6,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 16 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

fig17.generate<-function()
{ 
  tryCatch({
  fig17.wholesale<-GetAverageDataValues(197,10)/100.0
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig17.retail<-GetAverageDataValues(198,10)
  startDate<-GetDate30DaysBefore(reportDate)
  endDate<-reportDate
  fig17.wholesale<-ApplyTimeWindow(fig17.wholesale,startDate,endDate)
  fig17.retail<-ApplyTimeWindow(fig17.retail,startDate,endDate)
  #Figure 1 Data processing
  fig17.data<-as.data.frame(merge(fig17.retail,fig17.wholesale))
  pWValue<-as.data.frame(fig17.wholesale[length(fig17.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig17.retail[length(fig17.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format='%d-%b-%y')," is ",pRValue$value," Tk/Kg )    ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate, format='%d-%b-%y')," is ",pWValue$value," Tk/Kg )    ")
  colnames(fig17.data)<-c(reatilLable,wholesaleLable)
  fig17.data$startdate<-as.Date(rownames(fig17.data))
  fig17.data <- melt(fig17.data, id.vars="startdate")
  
  pWValue<-as.data.frame(fig17.wholesale[length(fig17.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig17.retail[length(fig17.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  
  #Figure 1 graph production
  fig17<-HomePageGraph(fig17.data,"Changes in Coarse Rice (Dhaka)","Taka per kg","4 days")
  
  #fig17<-fig17+ annotate("text",pWValue$startdate+2,pWValue$value,label=pWValue$value, size = 6)
  #fig17<-fig17+ annotate("text",pRValue$startdate+2,pRValue$value,label=pRValue$value, size = 6)
  
  
  ggsave(
    paste(targetPath,"portalchart17",'.png',sep=""),
    fig17,
    width = 12 ,
    height =  8.47,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 17 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}

fig18.generate<-function()
{ 
  tryCatch({
  fig18.wholesale<-GetAverageDataValues(245,10)/100.0
  #SQL for Rice - Coarse-BR - 8, 11, Guti, Sharna market retail price daily,
  fig18.retail<-GetAverageDataValues(246,10)
  startDate<-GetDate30DaysBefore(reportDate)
  endDate<-reportDate
  fig18.wholesale<-ApplyTimeWindow(fig18.wholesale,startDate,endDate)
  fig18.retail<-ApplyTimeWindow(fig18.retail,startDate,endDate)
  #Figure 5 Data processing
  fig18.data<-as.data.frame(merge(fig18.retail,fig18.wholesale))
  pWValue<-as.data.frame(fig18.wholesale[length(fig18.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig18.retail[length(fig18.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  reatilLable = paste("Retail ( Price  on"," ",format(pRValue$startdate, format='%d-%b-%y')," is ",pRValue$value," Tk/Kg )    ")
  wholesaleLable = paste("Wholesale ( Price  on"," ",format(pWValue$startdate, format='%d-%b-%y')," is ",pWValue$value," Tk/Kg )    ")
  colnames(fig18.data)<-c(reatilLable,wholesaleLable)
  fig18.data$startdate<-as.Date(rownames(fig18.data))
  fig18.data <- melt(fig18.data, id.vars="startdate")
  
  pWValue<-as.data.frame(fig18.wholesale[length(fig18.wholesale)])
  pWValue$startdate<-as.Date(rownames(pWValue))
  pRValue<-as.data.frame(fig18.retail[length(fig18.retail)])
  pRValue$startdate<-as.Date(rownames(pRValue))
  
  #Figure 5 graph production
  fig18<-HomePageGraph(fig18.data,"Changes in Atta-Loose (Dhaka)","Taka per kg","4 days")
  
  #fig18<-fig18+ annotate("text",pWValue$startdate+2,pWValue$value,label=pWValue$value, size = 6)
  #fig18<-fig18+ annotate("text",pRValue$startdate+2,pRValue$value,label=pRValue$value, size = 6)
  
  ggsave(
    paste(targetPath,"portalchart18",'.png',sep=""),
    fig18,
    width = 12 ,
    height =  8.47,
    units = "in",
    dpi = 200
  )
  },error=function(e){
    message(" Fig. 18 Failed")
    #print("an error occurred-----")
    #createNoDataImage("portalchart10")
  })
}


#Begin Figure generation
for (n in 1:18) {
  print(paste("Generating Figure",n))
  as.formula(paste("fig",n,".generate()",sep="")) }

#Close all ODBC handles
odbcCloseAll()
