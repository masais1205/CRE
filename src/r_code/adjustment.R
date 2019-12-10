require(pcalg)
require(MASS)

adjustment <- function(fileName, w, y, xArray, alpha) {
  data = read.csv(fileName)
  
  # in R, index starts from 1, so we have w+1, y+1 and xArray+1
  w = w+1
  y = y+1
  xArray = xArray+1
  
  ### select based on PC
  results = pcSelect(data[,y], data[,(xArray)], alpha)
  xArray.new = c(which(results$G), -1, which(!results$G))
  
  #### selct based chisq p_value 
  # p_value = lapply(xArray.new, function(x) chisq.test(table(data[,w],data[,x]))$p.value )
  # xArray.new = c(xArray.new[which(p_value <= alpha)], -1, xArray.new[which(p_value > alpha)])
  
  for(i in 1:length(xArray.new)) {
    if(xArray.new[i] >= w)
      xArray.new[i] = xArray.new[i]+1
    if(xArray.new[i] >= y)
      xArray.new[i] = xArray.new[i]+1
  }
  # return to java (minus 1)
  # Z are elements before -1, and C are the elements after -1
  xArray.new = xArray.new-1
  
  return(xArray.new)
}

