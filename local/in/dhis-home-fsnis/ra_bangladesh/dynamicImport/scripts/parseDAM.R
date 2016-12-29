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
require(gdata)
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
  goodVals[fillIdx] 
  }

#Function for copying strings for quotes
fillQuotedText <-function(col)
{
  for(i in 1:length(col)){
    testForQuotes <- (col[i]==" ,, ")
    if(!isTRUE(testForQuotes[1]))
      {
      textToPaste <- col[i]
      }
    else
      {
      col[i] <- textToPaste
      }
  }
  print(col)
  return (col)
}

#DAM
DAM.wb<-loadWorkbook(inputFile, create = TRUE)
#Attempt to parse the entire sheet
DAM.sheet<-readWorksheet(DAM.wb,sheet="Daily",startCol=1)
#Determine the start and end of data table
rowStarts<-grep(" c‡Y¨i bvg ",DAM.sheet[,2])
rowStarts <- rowStarts+2
rowEnds<-grep("cwi`wk©Z evRvi t 1| cvBKvix evRvi t ev`vgZjx I IqvBRNvU, ‡gvnv¤§`cyi K„wl evRvi, ingZMÄ I ‡gŠjfxevRvi, ‡mvqvixNvU evRvi, k¨vgevRvi, ",DAM.sheet[,2])
rowEnds <-rowEnds-2

#Get the time period
#TODO Ugly code. Needs to be improved.
timePeriod<-DAM.sheet[grepl(" ZvwiL t",DAM.sheet)]
timePeriod<-timePeriod[,1]
timePeriod<-timePeriod[grepl("",timePeriod)]
timePeriod<-gsub(" ZvwiL t","",timePeriod)
timePeriod<-gsub(" ","",timePeriod)
timePeriod<-gsub("/","",timePeriod)
timePeriod<-gsub("Bs","",timePeriod)
timePeriod<-gsub("to","_",timePeriod)
print(timePeriod[[1]])
#Capture Table-Data
tableData<-DAM.sheet[rowStarts[1]:rowEnds[1],2:13]

#Remove unnecessary columns
tableData <- tableData[,c(-4,-6,-8,-10,-12)]

#Trim white-spaces
trim(tableData[,1],  tableData[,2], tableData[,3], recode.factor=FALSE)

print(tableData[,3])

#Replaced the Quotes with copy text
tableData[,3] <- fillQuotedText(tableData[,3])

print(tableData[,3])

#Merge first three column content to compose the commodity name
tableData[,1] <- paste(tableData[,1],  tableData[,2], tableData[,3],sep = " ", collapse = NULL) 
#Remove redundent columns
tableData <- tableData[,c(-2,-3)]

#Write to CSV 
fileName<-paste(targetPath, .Platform$file.sep, "DAM_", timePeriod[[1]], ".csv", sep="")
write.csv(tableData,paste(fileName,sep=""),na="")