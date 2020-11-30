require(pcalg)
require(MASS)
require(randomForest)

adjustment <- function(fileName, w, y, xArray, alpha, featureSelection) {
  data = read.csv(fileName)
  cols = colnames(data)
  
  # in R, index starts from 1, so we have w+1, y+1 and xArray+1
  w = w+1
  y = y+1
  xArray = xArray+1
  # return(xArray)
  xCols = cols[xArray]
  
  ### select based on PC
  # rf <- randomForest(as.formula(paste0(cols[y], '~', paste0(xCols,collapse = "+"))), 
  #                    data=data, ntree=1000,
  #                    keep.forest=FALSE, importance=TRUE)
  # import = importance(rf, type = 1)
  # xCols = xCols[which(import >= max(import)*.1)]
  yResults = pcSelect(data[,cols[y]], data[,xCols], alpha)
  yPCs = xCols[which(yResults$G)]
  wResults = pcSelect(data[,cols[w]], data[,xCols], alpha)
  wPCs = xCols[which(wResults$G)]
  # wPCs = yPCs
  # p_value = lapply(nonPCs, function(x) chisq.test(table(data[,w],data[,x]))$p.value)
  # nonPCs = nonPCs[which(p_value <= .5)]
  
  # return to java (minus 1)
  # Z are elements before -1, and C are the elements after -1
  intersectPCs = intersect(wPCs, yPCs)
  unionPCs = union(wPCs, yPCs)
  if(featureSelection=='true')
    nonPCs = unionPCs[which(! unionPCs %in% intersectPCs)]
  else
    nonPCs = xCols[which(! xCols %in% intersectPCs)]
  
  xArray.new = c()
  for(i in 1:length(intersectPCs))
    xArray.new = c(xArray.new, which(cols==intersectPCs[i]))
  xArray.new = c(xArray.new, -1)
  for(i in 1:length(nonPCs))
    xArray.new = c(xArray.new, which(cols==nonPCs[i]))
  xArray.new = xArray.new-1
  
  return(xArray.new)
}

