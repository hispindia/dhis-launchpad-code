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

require(XLConnect)
require(RODBC)
require(scales)
require(grid)
require(zoo)
require(xts)
require(lubridate)
require(graphics)
require(plyr)
options ( java.parameters = "-Xmx1048m ")
cmd_args<-commandArgs();
inputFile<-cmd_args[6]
print(inputFile)
if( is.na(inputFile)  ) { stop("A input file is required as the first paramater.") }
targetPath<-cmd_args[7]
if( is.na(targetPath) ) { stop("A valid output directory is required as the second paramater") }
#Function for filling missing data
fillMissingRowData<-function(x){
goodIdx<-!is.na(x)
goodVals <- c(NA, x[goodIdx])
fillIdx <- cumsum(goodIdx)+1
goodVals[fillIdx] }

#DailyOffTake
DailyOffTake.wb<-loadWorkbook(inputFile, create = TRUE)
#Attempt to parse the entire sheet
DailyOffTake.sheet<-readWorksheet(DailyOffTake.wb,sheet="Daily Offtake")
#Determine the start and end of each block
rowStarts<-grep("Channel",DailyOffTake.sheet[,1])
rowEnds<-grep("GRAND TOTAL",DailyOffTake.sheet[,1])
#Get the commodities
commodities<-DailyOffTake.sheet[rowStarts-2,1]
commodities<-gsub("Commodity :","",commodities)
commodities<-gsub(" ","",commodities)
#Get the time period
#TODO Ugly code. Needs to be improved.
timePeriod<-DailyOffTake.sheet[grepl("DATE:",DailyOffTake.sheet)]
timePeriod<-timePeriod[,1]
timePeriod<-timePeriod[grepl("DATE:",timePeriod)]
timePeriod<-gsub("DATE:","",timePeriod)
timePeriod<-gsub(" ","",timePeriod)
timePeriod<-gsub("/","",timePeriod)
timePeriod<-gsub("to","_",timePeriod)
#Get the first one only
#TODO Need a default date in case nothing works
timePeriod<-timePeriod[[1]]

#Just seed a data frame
x<- data.frame(Col1=character())

#Construct the data frames
for (i in 1:length(commodities) ) {
foo<-DailyOffTake.sheet[rowStarts[i]:rowEnds[i],1:20]
#Remove the third row. It is pointless
foo<-foo[-3,]
#Fill the location row with data to get around the merged cells
if( i==1 ) {
foo[1,]<-fillMissingRowData(stack(foo[1,])$values)
foo[1,1]<-"Location"
foo[2,1]<-"Channel" }
else { foo<-foo[-c(1,2),] }
#Get rid of the totals
foo<- foo[!grepl("TOTAL",foo[,1]),]
#Add a commodities column
foo$commodity<-commodities[i]
#Reformat the channel names, first case is different
if (i == 1) { startIdx<-3 } else { startIdx<-1 }
channelNames<-foo[startIdx:nrow(foo),1]
channelNames<-paste(channelNames,"(",commodities[i],")",sep="")
foo[startIdx:nrow(foo),1]<-channelNames
#Finally, bind the individual data frames together
x<-rbind(x,foo)}
#Save file as CSV. 
fileName<-paste(targetPath,"/DailyOfftake_",timePeriod,".csv",sep="")
write.csv(x,paste(fileName,sep=""),na="NULL")




