require(pcalg)
require(MASS)

adjustment <- function(fileName, w, y, xArray, alpha) {
  data = read.csv(fileName)
  
  # in R, index starts from 1, so we have w+1, y+1 and xArray+1
  w = w+1
  y = y+1
  xArray = xArray+1
  # print(colnames(data)[w])
  # print(colnames(data)[y])
  # print(colnames(data)[xArray])
  results = pcSelect(data[,y], data[,(xArray)], alpha)
  # print(results)
  xArray.new = which(results$G == T) # new xArray
  # print(xArray.new)
  p_value = lapply(xArray.new, function(x) chisq.test(table(data[,w],data[,x]))$p.value )
  
  xArray.new = c(xArray.new[which(p_value <= alpha)], -1, xArray.new[which(p_value > alpha)])
  # print(xArray.new)
  for(i in 1:length(xArray.new)) {
    if(xArray.new[i] >= w)
      xArray.new[i] = xArray.new[i]+1
    if(xArray.new[i] >= y)
      xArray.new[i] = xArray.new[i]+1
  }
  # return to java (minus 1)
  # Z are elements before -1, and C are the elements after -1
  xArray.new = xArray.new-1
  # print(xArray.new)
  
  return(xArray.new)
}

# fileName = "C:/MyWorks/Contributions/personalised paper/SoftwareData_PKDD/CollegeDistanceData-binary.csv"
# xArray = adjustment(fileName, 2, 11, c(0:1,3:10,12:13), 0.005)

# fileName = "C:/MyWorks/Contributions/personalised paper/SoftwareData_PKDD/adult_dataset-binary.csv"
# xArray = adjustment(fileName, 4, 9, c(0:3,5:8), 0.005)

# fileName = "C:/MyWorks/Contributions/personalised paper/SoftwareData_PKDD/census-binary.csv"
# xArray = adjustment(fileName, 4, 9, c(0:3,5:8), 0.005)
# print(xArray)
