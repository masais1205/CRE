package cre.algorithm.cdt;

import cre.algorithm.CanShowOutput;

import java.io.*;
import java.util.*;


public class CDT {
    int numAttributes;              // The number of attributes including the output attribute
    String[] attributeNames;        // The names of all attributes.  It is an array of dimension numAttributes.  The last attribute is the output attribute
    private int atributoClase;
    /* Possible values for each attribute is stored in a vector.  domains is an array of dimension numAttributes.
            Each element of this array is a vector that contains values for the corresponding attribute
            domains[0] is a vector containing the values of the 0-th attribute, etc..
            The last attribute is the output attribute
    */
    Vector[] domains;


    String fileName;
    int maxData = 0;
    double minsup = 0.01;
    double thresholdPA = 3.84;
    double lastBestPA = 0;
    int lastDecompositionAttribute = -1;
    int hmax = 50;
    int matching;
    String matchingMethod = "";
    int pathTmpVar[];           // output unpruned path and its PA value
    int pathTmpVarVal[];
    double pathTmpVarPA[];
    double pathNewVarPA[];
    int pathTmpVarInd = -1;
    int pathTmpNum = 0;

    ArrayList<int[]> pathVar = new ArrayList<int[]>();           // output pruned path
    int pathVarRecord[];
    ArrayList<int[]> pathVarVal = new ArrayList<int[]>();
    int pathVarValRecord[];
    int pathVarInd = -1;

    int pruning = 0;              // pruning (merging the paths in same branch with same label) causal decision trees after creation
    int improving = 0;            // if yes: 1) when the child node can improve the PA value, then add this child into tree
    //         2) when the child node cannot improve the PA value, then stop
    int h = 0;
    boolean justReturn = false;
    int[] assList = new int[numAttributes];
    List<String> leaf = new ArrayList<String>();
    List<Integer> leafVal = new ArrayList<Integer>();
    FileWriter fWrite;
    BufferedWriter fp;
    int rightChild = -1;
    int lastClassLabel = -1;
    List<Integer> deSide = new ArrayList<Integer>(); // which side (left child or right child)


    /*  The class to represent a data point consisting of numAttributes values of attributes  */
    class DataPoint {

        /* The values of all attributes stored in this array.  i-th element in this array
           is the index to the element in the vector domains representing the symbolic value of
           the attribute.  For example, if attributes[2] is 1, then the actual value of the
           2-nd attribute is obtained by domains[2].elementAt(1).  This representation makes
           comparing values of attributes easier - it involves only integer comparison and
           no string comparison.
           The last attribute is the output attribute
        */
        public int[] attributes;

        public DataPoint(int numattributes) {
            attributes = new int[numattributes];
        }
    }

    ;


    /* The class to represent a node in the decomposition tree.
    */
    class TreeNode {
        public double entropy;                  // The entropy of data points if this node is a leaf node
        public Vector data;                     // The set of data points if this is a leaf node
        public int[][] dataArray;
        public int decompositionAttribute;      // If this is not a leaf node, the attribute that is used to divide the set of data points
        public int decompositionValue;          // the attribute-value that is used to divide the parent node
        public TreeNode[] children;             // If this is not a leaf node, references to the children nodes
        public TreeNode parent;                 // The parent to this node.  The root has parent == null
        public double PA;

        public TreeNode() {
            data = new Vector();
        }

    }

    ;

    /*  The root of the decomposition tree  */
    TreeNode root = new TreeNode();


    /*  This function returns an integer corresponding to the symbolic value of the attribute.
            If the symbol does not exist in the domain, the symbol is added to the domain of the attribute
    */
    public int getSymbolValue(int attribute, String symbol) {
        int index = domains[attribute].indexOf(symbol);
        if (index < 0) {
            domains[attribute].addElement(symbol);
            return domains[attribute].size() - 1;
        }
        return index;
    }


    // add by mss, association test
    public int[] assTest(TreeNode node) {
        double[] contValue = new double[4];
        double upValue = 0;
        double downValue = 0;
        double curChiSquare = 0;
        double tmpChiSquare = 0;
        int[] assTestList = new int[numAttributes];
        int assListInd = 0;
        int tmpAss = 0;
        double[] ChiSquareValue = new double[numAttributes];
        int numinputattributes = numAttributes - 1;
        for (int i = 0; i < numinputattributes; i++) {
            if (atributoClase == i)
                continue;
            contValue = contingencyTable(node.data, i);
            upValue = (contValue[0] * contValue[3] - contValue[1] * contValue[2]) /
                    (contValue[0] + contValue[1] + contValue[2] + contValue[3]);
            downValue = ((contValue[0] + contValue[1]) * (contValue[2] + contValue[3]) *
                    (contValue[0] + contValue[2]) * (contValue[1] + contValue[3])) /
                    (Math.pow((contValue[0] + contValue[1] + contValue[2] + contValue[3]), 2) *
                            (contValue[0] + contValue[1] + contValue[2] + contValue[3] - 1));

            if (downValue != 0)
                curChiSquare = Math.pow((Math.abs(upValue) - 0.5), 2) / downValue;
            else curChiSquare = 0;

            if (curChiSquare >= thresholdPA) {
                assTestList[assListInd] = i;
                ChiSquareValue[assListInd++] = curChiSquare;
            }
        }
        assTestList = trimlength(assTestList, assListInd);

        for (int i = 0; i < assTestList.length - 1; i++)
            for (int j = i + 1; j < assTestList.length; j++)
                if (ChiSquareValue[i] < ChiSquareValue[j]) {
                    tmpChiSquare = ChiSquareValue[i];
                    tmpAss = assTestList[i];
                    ChiSquareValue[i] = ChiSquareValue[j];
                    assTestList[i] = assTestList[j];
                    ChiSquareValue[j] = tmpChiSquare;
                    assTestList[j] = tmpAss;
                }

        return assTestList;
    }
//    
//    
//    // add by mss, association test
//    public List<Integer> assRule(TreeNode node) {
//    	List<Integer> nonExposures = new ArrayList<Integer>();
//    	double[] contValue = new double[4];
//    	double upValue = 0;
//    	double downValue = 0;
//    	double curChiSquare = 0;
//    	int numinputattributes = numAttributes-1;
//    	for (int i=0; i< numinputattributes; i++) {
//            if (atributoClase == i)
//            	continue;
//            contValue = contingencyTable(node.data, i);
//            upValue = (contValue[0]*contValue[3] - contValue[1]*contValue[2]) /
//        		(contValue[0]+contValue[1]+contValue[2]+contValue[3]);
//            downValue = ((contValue[0]+contValue[1]) * (contValue[2]+contValue[3]) *
//        		(contValue[0]+contValue[2]) * (contValue[1]+contValue[3])) /
//        		(Math.pow((contValue[0]+contValue[1]+contValue[2]+contValue[3]),2) *
//        		(contValue[0]+contValue[1]+contValue[2]+contValue[3]-1));
//
//            if(downValue != 0)
//            	curChiSquare = Math.pow((Math.abs(upValue)-0.5), 2)/downValue;
//            else curChiSquare = 0;
//        
//            if(curChiSquare < thresholdPA)
//            	nonExposures.add(i);
//    	}
//    	
//    	for(int i=0; i<numAttributes-1; i++)
//    		if(!nonExposures.contains(i))
//    			exposures.add(i);
//        
//    	return exposures;
//    }


    /*  Returns all the values of the specified attribute in the data set  */
    public int[] getAllValues(Vector data, int attribute) {
        Vector values = new Vector();
        int num = data.size();
        for (int i = 0; i < num; i++) {
            DataPoint point = (DataPoint) data.elementAt(i);
            String symbol = (String) domains[attribute].elementAt(point.attributes[attribute]);
            int index = values.indexOf(symbol);
            if (index < 0) {
                values.addElement(symbol);
            }
        }

        int[] array = new int[values.size()];
        for (int i = 0; i < array.length; i++) {
            String symbol = (String) values.elementAt(i);
            array[i] = domains[attribute].indexOf(symbol);
        }
        values = null;
        return array;
    }

    /*  Returns the most probable value of the specified attribute in the data set  */
    public int getMostValues(Vector data, int attribute) {
        Vector values = new Vector();
        int[] frequency = new int[domains[attribute].size()];
        int num = data.size();
        for (int i = 0; i < num; i++) {
            DataPoint point = (DataPoint) data.elementAt(i);
            String symbol = (String) domains[attribute].elementAt(point.attributes[attribute]);
//                    int symbolInt = Integer.parseInt(symbol);
            int index = values.indexOf(symbol);
            if (index < 0) {
                values.addElement(symbol);
                frequency[values.size() - 1]++;
            } else
                frequency[index]++;
        }

        int[] array = new int[values.size()];
        if (values.size() == 1) {
            array[0] = Integer.parseInt((String) values.elementAt(0));
            return array[0];
        } else
            for (int i = 0; i < array.length; i++) {
                String symbol = (String) values.elementAt(i);
                array[i] = domains[attribute].indexOf(symbol);
            }
//            System.out.println(array.length+" "+frequency.length);
        int mostProb = 0;
        mostProb = array[0];
        for (int i = 1; i < array.length; i++)
            if (frequency[i] >= frequency[i - 1])
                mostProb = array[i];
        values = null;
        return mostProb;
    }


    public int[] getCorValues(Vector data, int attribute) {
        Vector values = new Vector();
        int num = data.size();

        int[] label = new int[1];
        return label;
    }

    /*  Returns a subset of data, in which the value of the specfied attribute of all data points is the specified value  */
    public Vector getSubset(Vector data, int attribute, int value) {
        Vector subset = new Vector();

        int num = data.size();
        for (int i = 0; i < num; i++) {
            DataPoint point = (DataPoint) data.elementAt(i);
            if (point.attributes[attribute] == value) subset.addElement(point);
        }
        return subset;

    }

    public int[][] VectortoArray(Vector data) {
        int num = data.size();
        int[][] dataArray = new int[num][numAttributes];
        for (int i = 0; i < num; i++)
            dataArray[i] = ((DataPoint) data.elementAt(i)).attributes;
        return dataArray;
    }


    /*  Calculates the entropy of the set of data points.
            The entropy is calculated using the values of the output attribute which is the last element in the array attribtues
    */
    public double calculateEntropy(Vector data) {

        int numdata = data.size();
        if (numdata == 0) return 0;

        int attribute = atributoClase;
        int numvalues = domains[attribute].size();
        double sum = 0;
        for (int i = 0; i < numvalues; i++) {
            int count = 0;
            for (int j = 0; j < numdata; j++) {
                DataPoint point = (DataPoint) data.elementAt(j);
                if (point.attributes[attribute] == i) count++;
            }
            double probability = 1. * count / numdata;
            if (count > 0) sum += -probability * Math.log(probability);
        }
        return sum;

    }


    //  Calculates the Contingency Table of the set of data points.
    public double[] contingencyTable(Vector data, int attribute) {
        double[] contValue = new double[4];
        int contValueInd = 0;
        int numvalues = domains[attribute].size();

        for (int i = 0; i < numvalues; i++) {
            Vector subset = getSubset(data, attribute, i);
            if (subset.size() == 0) continue;
            int attrClass = atributoClase;
            int numClass = domains[attrClass].size();
            for (int j = 0; j < numClass; j++) {
                int count = 0;
                for (int k = 0; k < subset.size(); k++) {
                    DataPoint point = (DataPoint) subset.elementAt(k);
                    if (point.attributes[attrClass] == j) count++;
                }
                contValue[contValueInd++] = count;
            }
        }
        /* swap to get contingency table
         *       Class=1     Class=0
         * A=1  V[0](n11)   V[1](n12)
         * A=0  V[2](n21)   V[3](n22)
         */
        double tmpValue = contValue[0];
        contValue[0] = contValue[1];
        contValue[1] = tmpValue;
        tmpValue = contValue[2];
        contValue[2] = contValue[3];
        contValue[3] = tmpValue;

        return contValue;
    }


    //  Calculates the Contingency Table of the set of data points.
    public double[] contingencyTable(Vector data, int att, int attAssTested) {
        double[] contValue = new double[4];
        int contValueInd = 0;
        int numvalues = domains[att].size();

        for (int i = 0; i < numvalues; i++) {
            Vector subset = getSubset(data, att, i);
            if (subset.size() == 0) continue;
            int numClass = domains[attAssTested].size();
            for (int j = 0; j < numClass; j++) {
                int count = 0;
                for (int k = 0; k < subset.size(); k++) {
                    DataPoint point = (DataPoint) subset.elementAt(k);
                    if (point.attributes[attAssTested] == j) count++;
                }
                contValue[contValueInd++] = count;
            }
        }
        /* swap to get contingency table
         *       attTs=1     attTs=0
         * A=1  V[0](n11)   V[1](n12)
         * A=0  V[2](n21)   V[3](n22)
         */
        double tmpValue = contValue[0];
        contValue[0] = contValue[3];
        contValue[3] = tmpValue;
        tmpValue = contValue[1];
        contValue[1] = contValue[2];
        contValue[2] = tmpValue;

        return contValue;
    }


    /*  This function checks if the specified attribute is used to decompose the data set
            in any of the parents of the specfied node in the decomposition tree.
            Recursively checks the specified node as well as all parents
    */
    public boolean alreadyUsedToDecompose(TreeNode node, int attribute) {
        if (node.children != null) {
            if (node.decompositionAttribute == attribute)
                return true;
        }
        if (node.parent == null) return false;
        return alreadyUsedToDecompose(node.parent, attribute);
    }


    public void decompose(TreeNode node) {
        try {
            decomposeNodePA(node);
            fp.close();
            fWrite.close();
        } catch (Exception e) {
            canShowOutput.showOutputString(e.getMessage());
            e.printStackTrace();
        }
    }

    /*  add by mss to decompose node using Partial Association (PA)
             This function decomposes the specified node according to the id3 algorithm.
            Recursively divides all children nodes until it is not possible to divide any further
            I have changed this code from my earlier version. I believe that the code
            in my earlier version prevents useless decomposition and results in a better decision tree!
            This is a more faithful implementation of the standard id3 algorithm
    */
    public void decomposeNodePA(TreeNode node) {

        boolean selected = false;
        int selectedAttribute = 0;
        int indexnonAss = 0;

        h++;
        pathTmpVarInd++;
        int numroot = root.data.size();
        int numdata = node.data.size();
        int numinputattributes = numAttributes - 1;
//            node.entropy = calculateEntropy(node.data);  // calc entropy of class attr
//            if (node.entropy == 0) {
//    			h--;
//    			pathNewVarPA = trimzeros(pathTmpVarPA);
//    			fprintf(fp, "Path," + ++pathTmpNum + "\n");
//    			for(int i=0; i<pathNewVarPA.length; i++) {
//    				fprintf(fp, attributeNames[pathTmpVar[i]] + ",\t=" + pathTmpVarVal[i] + ",\t" + pathTmpVarPA[i] + "\n");
//    			}
////    			pathNewVarPA = new double[hmax];
//        		if(pathTmpVarInd>0) {
//        			pathTmpVarPA[pathTmpVarInd--] = 0;
//        			fprintf(fp, "\n");
//        		}
//            	return;
//            }

        double upValue = 0.0;
        double downValue = 0.0;
        double curPA = 0.0;
        double bestPA = 0;
            /*  In the following loop, the best attribute is located which
                    has the highest partial association
            */
        for (int i = 0; i < numinputattributes; i++) {

            if (atributoClase == i) {
                continue;
            }

            int numvalues = domains[i].size();
            if (alreadyUsedToDecompose(node, i)) continue;

            assList = assTest(node);

            int[] tmpAssList = assList.clone();
            Arrays.sort(tmpAssList);
            if (Arrays.binarySearch(tmpAssList, i) < 0)
                continue;


            // PA test
            curPA = PAMH(node, i, assList);
//System.out.println(attributeNames[i] + ": " + curPA);
            if (selected == false) {
                selected = true;
                bestPA = curPA;
                selectedAttribute = i;
            } else {
                if (curPA > bestPA) {
                    selected = true;
                    bestPA = curPA;
                    selectedAttribute = i;
                }
            }

        }

        // if improving is yes: 1) when the child node can improve the PA value, then add this child into tree
        //         2) when the child node cannot improve the PA value, then stop
        if (improving == 1) {
            if (node.parent != null &&
                    lastDecompositionAttribute == node.parent.decompositionAttribute && lastBestPA > bestPA)
                return;
            lastBestPA = bestPA;
            lastDecompositionAttribute = selectedAttribute;
        }


        if (selected == false || bestPA < thresholdPA) {
            h--;
            pathNewVarPA = trimzeros(pathTmpVarPA);
            fprintf(fp, "Path," + ++pathTmpNum + "\n");
            for (int i = 0; i < pathNewVarPA.length; i++) {
                fprintf(fp, attributeNames[pathTmpVar[i]] + ",\t=" + pathTmpVarVal[i] + ",\t" + pathTmpVarPA[i] + "\n");
            }
            if (pathTmpVarInd > 0) {
                pathTmpVarPA[pathTmpVarInd--] = 0;
                fprintf(fp, "\n");
            }
            return;
        }
//            System.out.println(bestPA);


        // Now divide the dataset using the selected attribute
        int numvalues = domains[selectedAttribute].size();
        node.decompositionAttribute = selectedAttribute;
        node.PA = bestPA;
        node.children = new TreeNode[numvalues];
        for (int j = 0; j < numvalues; j++) {
            node.children[j] = new TreeNode();
            node.children[j].parent = node;
            node.children[j].data = getSubset(node.data, selectedAttribute, j);
            node.children[j].dataArray = VectortoArray(node.children[j].data);
            node.children[j].decompositionValue = j;          // x.decompositionValue is the value of x.parent.decompositionValue
            // x.decompositionValue is not a real value, it need to be converted from domains[]
        }


        // Recursively divides children nodes
        for (int j = 0; j < numvalues; j++) {
            pathTmpVar[pathTmpVarInd] = node.decompositionAttribute;
            // error here, if String data
            pathTmpVarVal[pathTmpVarInd] = Integer.parseInt((String) domains[node.decompositionAttribute].elementAt(j));
            pathTmpVarPA[pathTmpVarInd] = bestPA;

            if (h < hmax - 1) {
                decomposeNodePA(node.children[j]);
            } else {
                h--;
                fprintf(fp, "Path," + ++pathTmpNum + "\n");
                pathNewVarPA = trimzeros(pathTmpVarPA);
                for (int i = 0; i < pathNewVarPA.length; i++) {
                    fprintf(fp, attributeNames[pathTmpVar[i]] + ",\t=" + pathTmpVarVal[i] + ",\t" + pathTmpVarPA[i] + "\n");
                }
                if (pathTmpVarInd > 0) {
                    pathTmpVarPA[pathTmpVarInd--] = 0;
                    fprintf(fp, "\n");
                }
                return;
            }
        }

        h--;
        pathNewVarPA = trimzeros(pathTmpVarPA);
        for (int i = 0; i < pathNewVarPA.length; i++) {
//				System.out.print(attributeNames[pathTmpVar[i]] + "=" + pathTmpVarVal[i] + " " + pathTmpVarPA[i] + ";\t");
        }
        if (pathTmpVarInd > 0) {
            pathTmpVarPA[pathTmpVarInd--] = 0;
//        		System.out.println();
        }

        // There is no more any need to keep the original vector.  Release this memory
//            node.data = null;               // Let the garbage collector recover this memory

    }


    public double PAMH(TreeNode node, int attribute, int[] assList) {
        double PAValue = 0;
        int[] parentVar = new int[hmax];
        int[] parentVarVal = new int[hmax];
        int parentVarInd = 0;
        int flag = 0;

//    	System.out.println(attribute);
        TreeNode tmpNode = new TreeNode();
        if (node.parent != null) {
            int value = node.decompositionValue;
            tmpNode = node.parent;
            parentVar[parentVarInd] = tmpNode.decompositionAttribute;
//    		parentVarVal[parentVarInd++] = tmpNode.decompositionValue;
            parentVarVal[parentVarInd++] = Integer.parseInt((String) domains[tmpNode.decompositionAttribute].elementAt(value));
            flag = 1;
        }
        while (tmpNode.parent != null) {
            int value = tmpNode.decompositionValue;
            tmpNode = tmpNode.parent;
            parentVar[parentVarInd] = tmpNode.decompositionAttribute;
//    		parentVarVal[parentVarInd++] = tmpNode.decompositionValue;
            parentVarVal[parentVarInd++] = Integer.parseInt((String) domains[tmpNode.decompositionAttribute].elementAt(value));
        }
        parentVar = trimlength(parentVar, parentVarInd);
        parentVarVal = trimlength(parentVarVal, parentVarInd);
//    	System.out.println(parentVar.length);

        int[] controlList = new int[numAttributes];
        int controlListInd = 0;
        for (int i = 0; i < assList.length; i++) {
//    		if(flag == 1) 
//    			for(int j=0; j<parentVarInd; j++)
//    				if(assList[i] == parentVar[j])
//    					break;
            if (assList[i] != attribute)
                controlList[controlListInd++] = assList[i];
        }
        controlList = trimlength(controlList, controlListInd);
        String[] controlListName = new String[controlList.length];
        for (int p = 0; p < controlList.length; p++) {
            controlListName[p] = attributeNames[controlList[p]];
        }
//    	System.out.println(attributeNames[attribute]+" :");
//    	printRecord(controlListName);
//    	System.out.println();

        // aggregating equivalent class & create contingency Table
        Vector curData = (Vector) node.data.clone();
        int[][] curDataArray = VectortoArray(curData);

        // Using matching method to stratify the equivalent classes
//    	if(matching == 1) {
//        	String namebase = fileName;
//        	int[] equiclassDist = new int[3*6];
//        	double[] matchedIndDouble = new double[maxData];
//
//    		try {
//    	    	RConnection c = new RConnection();
//    	        // source the R function
//    			Path p = Paths.get(namebase);
//    			File fileSystemObtainedFile = p.toFile();
//    			String pathfile = fileSystemObtainedFile.getPath();
//    		    String path = pathfile.substring(0,pathfile.lastIndexOf("\\")+1);
//    			path=path+"propensityTest.R";
//    			path=path.replace("\\", "/");
////    			System.out.println(path);
////    			path="C:/Users/maysy020/Workspace/CDT/propensityTest.R";
//    	        c.eval("source(\""+path+"\")");
////    	        c.eval("source(\"C:/Users/maysy020/Workspace/CDT/propensityTest.R\")");
//
//    	        // call the function. Return true
//    	        c.assign("file", namebase);
//    	        c.assign("parentVar", parentVar);
//    	        c.assign("parentVarVal", parentVarVal);
////    	        System.out.println(parentVar.length);
//    	        int[] itemArray = new int[1];
//    	        itemArray[0] = attribute;
//    	        c.assign("treat", itemArray);
//    	        c.assign("nonCfd", controlList);
//    	        c.assign("method", matchingMethod);
////    	        System.out.println(attribute);
////    	        printRecord(parentVar);
////    	        printRecord(parentVarVal);
////    	        printRecord(controlList);
////    	        REXP r = c.parseAndEval("try(\"+propensityTest(file,parentVar,parentVarVal,treat,nonCfd,method)+\",silent=TRUE)");
////    	        if (r.inherits("try-error")) System.err.println("Error: "+r.asString());
//
//    	        c.assign(".tmp.", "propensityTest(file,parentVar,parentVarVal,treat,nonCfd,method)");
//    	        REXP r = c.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
//    	        if (r.inherits("try-error")) System.err.println("Error: "+r.asString());
//
//    	        REXP tmpIndex = c.eval("propensityTest(file,parentVar,parentVarVal,treat,nonCfd,method)");
//    	        matchedIndDouble = tmpIndex.asDoubles();
//
//    	    	c.close();
//
//    	      } catch (Exception e) {
//    	    	  System.out.println(e.toString());
//    	      }
//
//    		int equiclassNum=0;
//    		for(int i=0; i<matchedIndDouble.length; i++)
//    			if(matchedIndDouble[i] < 0) {
//    				equiclassNum = i/3;
//    				break;
//    			}
//	        for(int i=0; i<3*equiclassNum; i++)
//	        	equiclassDist[i] = (int) matchedIndDouble[i];
//	        equiclassDist = trimzeros(equiclassDist);
//
//	        int[] equiData = new int[matchedIndDouble.length-3*equiclassNum-1]; // the index of all matched data
//	        for(int i=0; i<equiData.length; i++)
//	        	equiData[i] = (int) (matchedIndDouble[i+3*equiclassNum+1]-1);
//
//	        int startInd = 0; int endInd = 0;
//	        int dataInd = 0;
//	        double[][] contValue = new double[equiclassNum][4];
//	        for(int i=0; i<equiclassNum; i++) {
////	        	if(equiclassDist[i*3+2]==0)
////	        		continue;
//	        	endInd = startInd+equiclassDist[i*3];
//	        	for(int j=startInd; j<endInd; j++) {
//	        		dataInd = equiData[j];
//	        		if(dataInd>curDataArray.length)
//	        			System.out.println("1"+dataInd+" "+curDataArray.length);
//	        		if(curDataArray[dataInd][atributoClase] == 1)
//	        			contValue[i][0]++; // Ma++;
//	        		else
//	        			contValue[i][1]++; // Mb++;
//	        	}
//	        	startInd = endInd;
//	        	endInd = startInd+equiclassDist[i*3+1];
//        		if(dataInd>curDataArray.length)
//        			System.out.println("2"+dataInd+" "+curDataArray.length);
//	        	for(int j=startInd; j<endInd; j++) {
//	        		dataInd = equiData[j];
//	        		if(curDataArray[dataInd][atributoClase] == 1)
//	        			contValue[i][2]++; // Mc++;
//	        		else
//	        			contValue[i][3]++; // Md++;
//	        	}
//	        	startInd = endInd;
//	        }
//
//	    	PAValue = PAMHCalc(contValue, equiclassNum);
//	    	return PAValue;
//    	}

        if (controlListInd > 15)
            controlListInd = 15;
        controlList = trimlength(controlList, controlListInd);
        // Using Exact matching to stratify the equivalent classes
        sort(curDataArray, controlList);
        int initialSize = maxData;
        double[][] contValue = new double[initialSize][4];
        int equiclassNum = 0;
        int num = 0;
        while (num < curData.size()) {
            /*        contingency table
             *       Class=1     Class=0
    		 * A=1   V[0](Ma)    V[1](Mb)
    		 * A=0   V[2](Mc)    V[3](Md)
    		 */
            int Ma = 0, Mb = 0, Mc = 0, Md = 0;
            int tmpNum = 0;
            int[] controltemp = controlColumnSelect(curDataArray[num], controlList);
//    		if(num == curData.size()-1)
//    			break;
            for (tmpNum = num; tmpNum < curData.size(); tmpNum++) {
                if (Arrays.equals(controltemp, controlColumnSelect(curDataArray[tmpNum], controlList))) {
                    if (curDataArray[tmpNum][atributoClase] == 1)
                        if (curDataArray[tmpNum][attribute] == 1)
                            contValue[equiclassNum][0]++; // Ma++;
                        else
                            contValue[equiclassNum][2]++; // Mc++;
                    else if (curDataArray[tmpNum][attribute] == 1)
                        contValue[equiclassNum][1]++; // Mb++;
                    else
                        contValue[equiclassNum][3]++; // Md++;
                } else {
                    equiclassNum++;
                    break;
                }
                if (tmpNum == curData.size() - 1)
                    equiclassNum++;
            }
            num = tmpNum;
        }

        PAValue = PAMHCalc(contValue, equiclassNum);
//		System.out.println(attribute);
//		System.out.println(PAValue);

        return PAValue;
    }


    public int[] controlColumnSelect(int[] array, int[] control) {
        int[] controlList = new int[control.length];
        int controlListInd = 0;
        Arrays.sort(control);
        for (int i = 0; i < array.length; i++) {
            if (Arrays.binarySearch(control, i) >= 0)
                controlList[controlListInd++] = array[i];
        }
        return controlList;
    }


    public double PAMHCalc(double[][] contTable, int equiclassNum) {
        double PAValue = 0;
        double[] upValue = new double[equiclassNum];
        double[] downValue = new double[equiclassNum];
        double Ma, Mb, Mc, Md;
        double upValueSum = 0;
        double downValueSum = 0;
        double sum = 0;

        for (int i = 0; i < equiclassNum; i++) {

    		/*        contingency table
             *       Class=1     Class=0
    		 * A=1   V[0](Ma)    V[1](Mb)
    		 * A=0   V[2](Mc)    V[3](Md)
    		 */
            Ma = contTable[i][0];
            Mb = contTable[i][1];
            Mc = contTable[i][2];
            Md = contTable[i][3];
            sum = Ma + Mb + Mc + Md;
            if ((Ma + Mb) * (Ma + Mc) * (Mc + Md) * (Mb + Md) != 0 && (Md + Mc) >= 1) {
                upValue[i] = (Ma * Md - Mb * Mc) / sum;
                downValue[i] = ((Ma + Mb) * (Mc + Md) * (Ma + Mc) * (Mb + Md)) / (Math.pow(sum, 2) * (sum - 1));
            } else {
                upValue[i] = 0;
                downValue[i] = 0;
            }
        }

        for (int i = 0; i < equiclassNum; i++) {
            upValueSum = upValueSum + upValue[i];
            downValueSum = downValueSum + downValue[i];
        }

        if (downValueSum != 0)
            PAValue = Math.pow((Math.abs(upValueSum) - 0.5), 2) / downValueSum;
        else
            PAValue = 0;

        return PAValue;
    }


    public double[][] enlargeArraySize(double[][] array) {
        double[][] newArray = new double[array.length * 10][array[0].length];
        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < array[0].length; j++)
                newArray[i][j] = array[i][j];
        return newArray;
    }

    /**
     * Function to read the data file.
     * The first line of the data file should contain the names of all attributes.
     * The number of attributes is inferred from the number of words in this line.
     * The last word is taken as the name of the output attribute.
     * Each subsequent line contains the values of attributes for a data point.
     * If any line starts with // it is taken as a comment and ignored.
     * Blank lines are also ignored.
     */
    public int readData(String filename) throws Exception {

        FileInputStream in = null;

        try {
            File inputFile = new File(filename);
            in = new FileInputStream(inputFile);
        } catch (Exception e) {
            System.err.println("Unable to open data file: " + filename + "\n" + e);
            return 0;
        }

        BufferedReader bin = new BufferedReader(new InputStreamReader(in));

        String input;
        while (true) {
            input = bin.readLine();
            if (input == null) {
                System.err.println("No data found in the data file: " + filename + "\n");
                return 0;
            }
            if (input.startsWith("//")) continue;
            if (input.equals("")) continue;
            break;
        }


        StringTokenizer tokenizer = new StringTokenizer(input, ",");
        numAttributes = tokenizer.countTokens();

        if (numAttributes <= 1) {
            System.err.println("Read line: " + input);
            System.err.println("Could not obtain the names of attributes in the line");
            System.err.println("Expecting at least one input attribute and one output attribute");
            return 0;
        }

        domains = new Vector[numAttributes];
        for (int i = 0; i < numAttributes; i++) domains[i] = new Vector();
        attributeNames = new String[numAttributes];

        for (int i = 0; i < numAttributes; i++) {
            attributeNames[i] = tokenizer.nextToken();
        }

        // add by mss
        atributoClase = numAttributes - 1;
        System.out.println("The class attribute is: " + attributeNames[atributoClase]);


        while (true) {
            input = bin.readLine();
            if (input == null) break;
            if (input.startsWith("//")) continue;
            if (input.equals("")) continue;

            tokenizer = new StringTokenizer(input, ",");
            int numtokens = tokenizer.countTokens();
            if (numtokens != numAttributes) {
                System.err.println("Read " + root.data.size() + " data");
                System.err.println("Last line read: " + input);
                System.err.println("Expecting " + numAttributes + " attributes");
                return 0;
            }

            DataPoint point = new DataPoint(numAttributes);
            for (int i = 0; i < numAttributes; i++) {
                point.attributes[i] = getSymbolValue(i, tokenizer.nextToken());
            }
            root.data.addElement(point);
        }

        bin.close();

        root.dataArray = new int[root.data.size()][];
        for (int i = 0; i < root.data.size(); i++) {
            root.dataArray[i] = new int[numAttributes];
            root.dataArray[i] = ((DataPoint) root.data.elementAt(i)).attributes;
        }

        maxData = root.data.size();

        return 1;

    }       // End of function readData
    //-----------------------------------------------------------------------

    /*  This function prints the decision tree in the form of rules.
            The action part of the rule is of the form
                    outputAttribute = "symbolicValue"
            or
                    outputAttribute = { "Value1", "Value2", ..  }
            The second form is printed if the node cannot be decomposed any further into an homogenous set
    */
    public void output2Treefile(TreeNode node, String tab) {

        try {
            if (pruning == 1) {
                for (int i = 0; i < hmax - 1; i++)
                    pruneTree(node, "");
            }
            printTree(node, "", 0);

        } catch (Exception e) {
            e.printStackTrace();
            canShowOutput.showOutputString(e.getMessage());
        }
    }

    public void pruneTree(TreeNode node, String tab) {
        int outputattr = atributoClase;

        if (node.children == null) {
            int value = getMostValues(node.data, outputattr);
            value = Integer.valueOf((String) domains[outputattr].elementAt(value));
            if (rightChild == 1) {
                if (value == lastClassLabel) {
                    int childNum = deSide.get(deSide.size() - 2);
                    node.parent.parent.children[childNum] = new TreeNode();
                    node.parent.parent.children[childNum].parent = node.parent.parent;
                    node.parent.parent.children[childNum].data = getSubset(node.parent.parent.data, node.parent.parent.decompositionAttribute, childNum);
                    node.parent.parent.children[childNum].dataArray = VectortoArray(node.parent.parent.children[childNum].data);
                }
                lastClassLabel = -1;
            } else
                lastClassLabel = value;
            if (deSide.size() > 0)
                deSide.remove(deSide.size() - 1);
            return;
        }

        int numvalues = node.children.length;
        for (int i = 0; i < numvalues; i++) {
            rightChild = i;
            deSide.add(rightChild);
//			System.out.println(attributeNames[node.decompositionAttribute] + "=" + domains[node.decompositionAttribute].elementAt(i) + "\t" + rightChild);
            pruneTree(node.children[i], tab + ",");
        }
        if (deSide.size() > 0)
            deSide.remove(deSide.size() - 1);
    }


    public void printTree(TreeNode node, String tab, int depth) {

        int outputattr = atributoClase;
        pathVarInd++;

        if (node.children == null) {
            int value = getMostValues(node.data, outputattr);

            int[] tmpVar = trimlength(pathVarRecord, pathVarInd);
            int[] tmpVarVal = trimlength(pathVarValRecord, pathVarInd);
            pathVar.add(tmpVar);
            pathVarVal.add(tmpVarVal);
//            	pathVarRecord = new int[hmax];
//            	pathVarValRecord = new int[hmax];
            if (pathVarInd > 0)
                pathVarInd--;

            fprintf(fp, tab + "\t" + attributeNames[outputattr] + " = \"" + domains[outputattr].elementAt(value) + "\";\n");
//                System.out.println(attributeNames[outputattr] + " = \"" + domains[outputattr].elementAt(value));
            return;
        }

        int numvalues = node.children.length;
        for (int i = 0; i < numvalues; i++) {
            pathVarRecord[pathVarInd] = node.decompositionAttribute;
            pathVarValRecord[pathVarInd] = Integer.parseInt((String) domains[node.decompositionAttribute].elementAt(i));
            fprintf(fp, tab + "if( " + attributeNames[node.decompositionAttribute] + " == \"" +
                    domains[node.decompositionAttribute].elementAt(i) + "\") (" + node.PA + ") {\n");
            printTree(node.children[i], tab + ",", depth + 1);
            if (i != numvalues - 1) fprintf(fp, tab + "} else ");
            else fprintf(fp, tab + "}\n");
        }

        if (pathVarInd > 0)
            pathVarInd--;

    }

    /*  This function creates the decision tree and prints it in the form of rules on the console
    */
    public void createDecisionTree() {

        long startTime = System.currentTimeMillis();

//    		int[] exposures = assTest(root);
//				System.out.println("The number of associated variables: "+ exposures.length);
//				System.out.println("Ass Var: ");
//    			for(int i=0; i<exposures.size(); i++)
//    				System.out.print(exposures.get(i) + " ");
//    			System.out.println();

        decompose(root);

        long endTime = System.currentTimeMillis();

        System.out.println();
        System.out.println("Run time: " + (endTime - startTime));

        output2Treefile(root, "");

//            System.out.println("\n All leaf nodes are: ");
//            unique();
//            for(int i=0; i<leaf.size(); i++) {
//            	System.out.print(leaf.get(i)+" = ");
//            	System.out.print(leafVal.get(i)+"; \t");
//            	if(i!=0 && i%7 == 0)
//                	System.out.println();
//            }
    }

    private CanShowOutput canShowOutput;

    public CDT(CDTConfig config, String fileNameWithoutExtension, CanShowOutput showArea) {

        this.canShowOutput = showArea;
        fileName = fileNameWithoutExtension;
        hmax = config.getHeight();
        improving = config.isTest_improve_PA() ? 1 : 0;
        pruning = config.isPruned() ? 1 : 0;

        pathTmpVar = new int[hmax];
        pathTmpVarVal = new int[hmax];
        pathTmpVarPA = new double[hmax];
        pathNewVarPA = new double[hmax];

        pathVarRecord = new int[hmax];
        pathVarValRecord = new int[hmax];

        String namebase = fileName + ".csv";

        try {
            int status = readData(namebase);
            if (status <= 0) {
                return;
            }
        } catch (Exception e) {
            System.err.println("Unable to open data file: " + namebase + "\n" + e);
        }

        createDecisionTree();
    }


    public void fprintf(BufferedWriter out, String s) {
        try {
            out.write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // remove zeros in the end of the array
    public double[] trimzeros(double[] array) {
        int len = 0;
        for (int i = array.length - 1; i >= 0; i--)
            if (array[i] != 0) {
                len = i + 1;
                break;
            }

        double[] newArray = new double[len];
        for (int i = 0; i < len; i++) {
            newArray[i] = array[i];
        }
        return newArray;
    }


    public int[] trimlength(int[] array, int len) {
        int[] newArray = new int[len];
        for (int i = 0; i < len; i++) {
            newArray[i] = array[i];
        }
        return newArray;
    }

    public static void sort(int[][] ob, final int[] columns) {
        Arrays.sort(ob, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                int[] one = (int[]) o1;
                int[] two = (int[]) o2;
                for (int i = 0; i < columns.length; i++) {
                    int k = columns[i];
                    if (one[k] > two[k])
                        return 1;
                    else if (one[k] < two[k])
                        return -1;
                }

                return 0;
            }
        });
    }
}