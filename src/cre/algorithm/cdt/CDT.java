package cre.algorithm.cdt;

import cre.algorithm.CanShowOutput;
import cre.view.tree.Children;
import cre.view.tree.Node;
import sun.reflect.generics.tree.Tree;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by HanYizhao on 2017/7/7.
 */
public class CDT {

    /**
     * Yizhao Han
     * This attribute is used to store the treeview.
     */
    public Node rootYizhao;

    /**
     * Yizhao Han
     */
    private boolean doTest;

    /**
     * Yizhao Han
     * Split data to several group.
     * For each line, there will be a group ID.
     */
    private int[] lineGroup;

    /**
     * Yizhao Han
     * The group ID used to do test.
     */
    private int testingGroupId;

    /**
     * Yizhao Han
     * Data used to do test.
     */
    private List<int[]> testingData = new ArrayList<>();

    /**
     * Yizhao Han
     * the real data for the testing set.
     */
    private List<String> realResult;

    /**
     * Yizhao Han
     * the classifier result for the testing set.
     */
    private List<String> testResult;

    private CanShowOutput canShowOutput;

    private int hmax;

    private boolean m_pruned;

    private int m_numControl = 15;

    /**
     * The threshold of PA Value
     */
    private double thresholdPA = 3.84;

    private boolean m_subclass = false;

    /**
     * all Data
     */
    private List<int[]> instanceData = new ArrayList<>();

    private String[] attributeNames;

    private int classPosition;

    private TreeNode root;

    public CDT(CDTConfig config, String fileName, CanShowOutput showArea,
               int[] lineGroup, int testGroupNumber,
               List<String> realResult, List<String> testResult, boolean doTest) {

        this.realResult = realResult;
        this.testResult = testResult;
        this.doTest = doTest;
        this.lineGroup = lineGroup;
        this.testingGroupId = testGroupNumber;
        this.canShowOutput = showArea;
        hmax = config.getHeight();
        m_pruned = config.isPruned();

        try {
            readData(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            canShowOutput.showOutputString("Unable to open data file: " + fileName + "\n" + e);
        }
    }

    public void createDecisionTree() throws Exception {
        root = new TreeNode();
        makeTree(instanceData, 0, root);
        if (m_pruned) {
            int heigth = root.getHeight();
            for (int i = 0; i < heigth - 1; i++) {
                prune(root);
            }
        }
        if (!doTest) {
            outputTree(root);
        } else {
            validation(root);
        }
    }

    private void validation(TreeNode root) {
        for (int[] line : testingData) {
            realResult.add(line[classPosition] + "");
            testResult.add(getValidationForOneLine(root, line));
        }
    }

    private String getValidationForOneLine(TreeNode node, int[] data) {
        if (node.children == null) {
            return node.m_ClassValue + "";
        } else {
            int nowDividerValue = data[node.decompositionAttribute];
            if (node.children.get(0).decompositionValue == nowDividerValue) {
                return getValidationForOneLine(node.children.get(0), data);
            } else {
                return getValidationForOneLine(node.children.get(1), data);
            }
        }
    }

    /*  This function prints the decision tree in the form of rules.
            The action part of the rule is of the form
                    outputAttribute = "symbolicValue"
            or
                    outputAttribute = { "Value1", "Value2", ..  }
            The second form is printed if the node cannot be decomposed any further into an homogenous set
    */
    private void outputTree(TreeNode node) {
        try {
            if (node.decompositionAttribute != -1) {
                printTreeYizhao(node, "");
                Dimension treeSize = getTreeSize(node);
                canShowOutput.showOutputString("\nTree size: "
                        + treeSize.width + "\tleaf size: " + treeSize.height + "\n");
                rootYizhao = new Node(attributeNames[node.decompositionAttribute],
                        "ROOT", null);
                getTreeYizhao(node, rootYizhao);
            } else if (node.m_ClassValue != -1) {
                String result = attributeNames[classPosition] + " = " + node.m_ClassValue;
                canShowOutput.showOutputString(result);
                rootYizhao = new Node(result, null, null);
            } else {
                canShowOutput.showOutputString("No tree");
            }
        } catch (Exception e) {
            e.printStackTrace();
            canShowOutput.showOutputString(e.getMessage());
        }
    }

    private Dimension getTreeSize(TreeNode root) {
        Dimension dimension = new Dimension();
        LinkedList<TreeNode> queue = new LinkedList<>();
        int size = 0, leafSize = 0;
        queue.push(root);
        while (!queue.isEmpty()) {
            TreeNode temp = queue.poll();
            size++;
            if (temp.children != null && temp.children.size() > 0) {
                queue.addAll(temp.children);
            } else {
                leafSize++;
            }
        }
        return new Dimension(size, leafSize);
    }

    private void getTreeYizhao(TreeNode treeNode, Node node) {
        if (treeNode.children != null && treeNode.children.size() == 2) {
            node.children = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                if (treeNode.children.get(i).children == null || treeNode.children.get(i).children.size() == 0) {
                    Node n = new Node(attributeNames[classPosition] + "=" + treeNode.children.get(i).m_ClassValue,
                            null, node);
                    node.children.add(new Children(n, " = " +
                            treeNode.children.get(i).decompositionValue));
                } else {
                    Node n = new Node(attributeNames[treeNode.children.get(i).decompositionAttribute], null, node);
                    node.children.add(new Children(n, " = " +
                            treeNode.children.get(i).decompositionValue));
                    getTreeYizhao(treeNode.children.get(i), node.children.get(i).getValue());
                }
            }
        }
    }

    private void printTreeYizhao(TreeNode node, String stringNow) {
        if (node.children == null || node.children.size() == 0) {
            canShowOutput.showOutputString("End, bad ending.");
        } else if (node.children.size() == 2) {
            for (int i = 0; i < 2; i++) {
                if (node.children.get(i).children == null || node.children.get(i).children.size() == 0) {
                    canShowOutput.showOutputString(stringNow +
                            attributeNames[node.decompositionAttribute] + " = " +
                            node.children.get(i).decompositionValue +
                            " " + attributeNames[classPosition] + "=" + node.children.get(i).m_ClassValue);
                } else {
                    canShowOutput.showOutputString(stringNow +
                            attributeNames[node.decompositionAttribute] + " = " +
                            node.children.get(i).decompositionValue);
                    printTreeYizhao(node.children.get(i), stringNow + "|   ");
                }
            }
        } else {
            canShowOutput.showOutputString("This node does not has two children."
                    + attributeNames[node.decompositionAttribute]);
        }
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
    private void readData(String filename) throws Exception {
        BufferedReader bin = new BufferedReader(new FileReader(filename));
        String input = bin.readLine();
        if (input == null) {
            canShowOutput.showOutputString("Can not read first line.");
            return;
        }

        attributeNames = input.split(",");
        classPosition = attributeNames.length - 1;
        if (attributeNames.length <= 1) {
            canShowOutput.showOutputString("Read line: " + input);
            canShowOutput.showOutputString("Could not obtain the names of attributes in the line");
            canShowOutput.showOutputString("Expecting at least one input attribute and one output attribute");
            return;
        }

        int nowDataLineCount = 0;
        while ((input = bin.readLine()) != null) {
            String[] strings = input.split(",");
            if (strings.length != attributeNames.length) {
                canShowOutput.showOutputString("Line:" + (nowDataLineCount + 2));
                canShowOutput.showOutputString("Last line read: " + input);
                canShowOutput.showOutputString("Expecting " + attributeNames.length + " attributes");
                return;
            }

            int[] lineData = new int[attributeNames.length];
            for (int i = 0; i < attributeNames.length; i++) {
                lineData[i] = Integer.parseInt(strings[i]);
            }

            if (!doTest || lineGroup[nowDataLineCount] != testingGroupId) {
                instanceData.add(lineData);
                //root.data.addElement(point);
            } else {
                testingData.add(lineData);
            }
            nowDataLineCount++;
        }
        bin.close();
    }       // End of function readData


    /**
     * Method for building an CDT tree.
     *
     * @param data the training data
     * @throws Exception if decision tree can't be built successfully
     */
    private void makeTree(List<int[]> data, int h, TreeNode nowNode) throws Exception {
        h++;
        int[] numValues = new int[attributeNames.length];
        for (int i = 0; i < attributeNames.length; i++) {
            numValues[i] = numberOfValues(data, i);
        }

        // Check if no instances have reached this node.
        if (data.size() == 0) {
            nowNode.decompositionAttribute = -1;
            nowNode.m_ClassValue = -1;
            nowNode.m_DistSize = new double[2];
//            if (parentAtt.size() > 0) {
//                parentAtt.remove(parentAtt.size() - 1);
//                parentAttVal.remove(parentAttVal.size() - 1);
//            }
        } else {

            // Compute attribute with maximum PA value.
            double[] PAValue = new double[attributeNames.length];
            int[] assList;

            for (int i = 0; i < attributeNames.length; i++) {
                if (numValues[i] < 2 || i == classPosition) {
                    continue;
                }
                assList = assTest(data, i);
                PAValue[i] = PAMH(data, i, assList);
            }

            int m_Attribute = maxIndex(PAValue);
            // Make leaf if stop condition.
            // Otherwise create successors.
            if (PAValue[m_Attribute] < thresholdPA) {
                double[] m_DistSize = new double[2];
                for (int[] i : data) {
                    m_DistSize[i[classPosition]]++;
                }
                nowNode.decompositionAttribute = -1;
                nowNode.m_ClassValue = maxIndex(m_DistSize);
                nowNode.m_DistSize = m_DistSize;
            } else {
                if (h < hmax) {
                    List<List<int[]>> splitData = splitData(data, m_Attribute, numValues[m_Attribute]);
                    nowNode.children = new ArrayList<>();
                    nowNode.decompositionAttribute = m_Attribute;
                    nowNode.PA = PAValue[m_Attribute];
                    for (int i = 0; i < numValues[m_Attribute]; i++) {
                        TreeNode treeNode = new TreeNode();
                        treeNode.parent = nowNode;
                        treeNode.decompositionValue = i;
                        nowNode.children.add(treeNode);
                        makeTree(splitData.get(i), h, treeNode);
                    }
                } else {
                    double[] m_DistSize = new double[2];
                    for (int[] i : data) {
                        m_DistSize[i[classPosition]]++;
                    }
                    nowNode.decompositionAttribute = -1;
                    nowNode.m_ClassValue = maxIndex(m_DistSize);
                    nowNode.m_DistSize = m_DistSize;
                }
            }
        }
    }

    /**
     * Prunes a tree using C4.5's pruning procedure.
     *
     * @throws Exception if something goes wrong
     */
    private void prune(TreeNode now) throws Exception {
        /// m_Attribute is root, m_Successors is sontree
        if (now.children == null) {
            return;
        }
        TreeNode[] m_Successors = new TreeNode[now.children.size()];
        now.children.toArray(m_Successors);
        if (m_Successors[0].children == null && m_Successors[1].children == null) {
            if (m_Successors[0].m_ClassValue == m_Successors[1].m_ClassValue) {
                now.decompositionAttribute = -1;
                now.m_DistSize = new double[2];
                now.m_DistSize[0] = m_Successors[0].m_DistSize[0] + m_Successors[1].m_DistSize[0];
                now.m_DistSize[1] = m_Successors[0].m_DistSize[1] + m_Successors[1].m_DistSize[1];
                now.m_ClassValue = m_Successors[0].m_ClassValue;
                now.children = null;
                return;
            }
        }
        for (TreeNode m_Successor : m_Successors) {
            prune(m_Successor);
        }
    }

    /**
     * Computes PA value for an attribute.
     *
     * @param data the data for which PA value is to be computed
     * @param att  the position of attribute
     * @return the PA value for the given attribute and data
     * @throws Exception if computation fails
     */
    private int[] assTest(List<int[]> data, int att)
            throws Exception {
        double upValue;
        double downValue;
        double chiSquare;
        int assListInd = 0;

        int[] assTestList = new int[attributeNames.length];
        double[] ChiSquareValue = new double[attributeNames.length];
        for (int i = 0; i < attributeNames.length; i++) {
            if (i == att || i == classPosition) {
                continue;
            }
            double[][] contValue = contingencyTable(data, i);

            upValue = (contValue[0][0] * contValue[1][1] - contValue[0][1] * contValue[1][0]) /
                    (contValue[0][0] + contValue[0][1] + contValue[1][0] + contValue[1][1]);
            downValue = ((contValue[0][0] + contValue[0][1]) * (contValue[1][0] + contValue[1][1]) *
                    (contValue[0][0] + contValue[1][0]) * (contValue[0][1] + contValue[1][1])) /
                    (Math.pow((contValue[0][0] + contValue[0][1] + contValue[1][0] + contValue[1][1]), 2) *
                            (contValue[0][0] + contValue[0][1] + contValue[1][0] + contValue[1][1] - 1));
            if (downValue != 0)
                chiSquare = Math.pow((Math.abs(upValue) - 0.5), 2) / downValue;
            else chiSquare = 0;
            if (chiSquare >= thresholdPA) {
                assTestList[assListInd] = i;
                ChiSquareValue[assListInd++] = chiSquare;
            }

        }
        //////?????not tested.
        assTestList = Arrays.copyOf(assTestList, assListInd);
        List<Pair> pairs = new ArrayList<>();
        for (int i = 0; i < assTestList.length; i++) {
            pairs.add(new Pair(assTestList[i], ChiSquareValue[i]));
        }
        Collections.sort(pairs, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return Double.compare(o2.degree, o1.degree);
            }
        });
        for (int i = 0; i < assTestList.length; i++) {
            assTestList[i] = pairs.get(i).value;
        }
//        for (int i = 0; i < assTestList.length - 1; i++)
//            for (int j = i + 1; j < assTestList.length; j++)
//                if (ChiSquareValue[i] < ChiSquareValue[j]) {
//                    tmpChiSquare = ChiSquareValue[i];
//                    tmpAss = assTestList[i];
//                    ChiSquareValue[i] = ChiSquareValue[j];
//                    assTestList[i] = assTestList[j];
//                    ChiSquareValue[j] = tmpChiSquare;
//                    assTestList[j] = tmpAss;
//                }

        return assTestList;
    }


    /**
     * generates contingency table for an attribute.
     *
     * @param data         the data for which PA value is to be computed
     * @param attAssTested attribute
     * @return the PA value for the given attribute and data
     * @throws Exception if computation fails
     */
    private double[][] contingencyTable(List<int[]> data, int attAssTested)
            throws Exception {
          /* contValue:
           * 					classValue[0]	classValue[1]
		   * attAssTested[0]		a c[0][0]		b c[0][1]
		   * attAssTested[1]		c c[1][0]		d c[1][1]
		   */

        double[][] contValue = new double[2][2];
        for (int[] i : data) {
            contValue[i[attAssTested]][i[classPosition]]++;
        }
        return contValue;
    }

    private int numberOfValues(List<int[]> data, int position) {
        Set<Integer> set = new HashSet<>();
        for (int[] i : data) {
            set.add(i[position]);
        }
        return set.size();
    }

    /**
     * Computes PA value for an attribute.
     *
     * @param data the data for which PA value is to be computed
     * @param att  the attribute
     * @return the PA value for the given attribute and data
     * @throws Exception if computation fails
     */
    private double PAMH(List<int[]> data, int att, int[] assList)
            throws Exception {

        double PAVal = 0;
        int[] controlList = new int[attributeNames.length];
        int controlInd = 0;

        // no limit about control items
        for (int anAssList : assList) {
            if (anAssList != att)
                controlList[controlInd++] = anAssList;
        }

        controlList = Arrays.copyOf(controlList, controlInd);

        double[][] data_keep = new double[data.size()][attributeNames.length];
        int data_keepIndex = 0;
        for (int[] i : data) {
            for (int k = 0; k < attributeNames.length; k++) {
                data_keep[data_keepIndex][k] = i[k];
            }
            data_keepIndex++;
        }

        if (!m_subclass) {
            if (controlInd > m_numControl)
                controlInd = m_numControl;
            controlList = Arrays.copyOf(controlList, controlInd);
            ArrayList<double[]> dataList = new ArrayList<>(Arrays.asList(data_keep));
            double[][] contValue = new double[data.size()][4];
            int contValueInd = 0;
            while (dataList.size() > 1) {
                ArrayList<double[]> eqiClass = new ArrayList<>();
                int[] eqiClassSpace = new int[dataList.size()];
                int eqiClassIndex = 0;
                int index = 0;
                double[] controltemp = controlColumnSelect(dataList.get(0), controlList);
                for (double[] itemrecord : dataList) {
                    if (Arrays.equals(controltemp, controlColumnSelect(itemrecord, controlList))) {
                        eqiClass.add(itemrecord);
                        eqiClassSpace[eqiClassIndex++] = index;
                    }
                    index++;
                }
                for (int i = 0; i < eqiClassIndex; i++)
                    dataList.remove(eqiClassSpace[i] - i);
                contValue[contValueInd++] = contingencyTable(eqiClass, att);
                //		  		System.out.println(dataList.size());
            }
            PAVal = PAMHCalc(contValue, contValueInd);
        } else {
            /*
            int[] equiclassDist = new int[3 * 6];
            double[] matchedIndDouble = new double[data.numInstances() * data.numAttributes()];
            if (fileName.isEmpty())
                fileName = m_location + "\\file";
            try {
                RConnection c = new RConnection();
                Path p = Paths.get(fileName);
                File fileSystemObtainedFile = p.toFile();
                String pathfile = fileSystemObtainedFile.getPath();
                String path = pathfile.substring(0, pathfile.lastIndexOf("\\") + 1);
//	    			System.out.println(path);
                String rFileName = path + "propensityTest_weka.R";
                rFileName = rFileName.replace("\\", "/");
                c.eval("source(\"" + rFileName + "\")");
//	    	        String dataFileName = pathfile.substring(0,pathfile.lastIndexOf("_"));
//	    	        String dataType = pathfile.substring(pathfile.lastIndexOf("."));
//	    	        dataFileName = dataFileName + dataType;
//	    	        dataFileName = dataFileName.replace("\\", "/");
//	    	        String dataFileName = pathfile.replace("\\", "/");
//	    	        c.assign("file", dataFileName);

                c.assign("data", data_keep[0]);
                for (int i = 1; i < data_keep.length; i++) {
                    c.assign("tmp", data_keep[i]);
                    c.eval("data<-rbind(data,tmp)");
                }

                int[] itemArray = new int[1];
                itemArray[0] = att.index();
                c.assign("treat", itemArray);
                int[] parentAttIndex = new int[parentAtt.size()];
                String[] parentAttValIndex = new String[parentAtt.size()];
                for (int i = 0; i < parentAtt.size(); i++) {
                    parentAttIndex[i] = parentAtt.get(i).index();
                    parentAttValIndex[i] = parentAttVal.get(i);
                }
                c.assign("parentAtt", parentAttIndex);
                c.assign("parentAttVal", parentAttValIndex);

                int[] controlIndex = new int[controlList.length];
                for (int i = 0; i < controlList.length; i++)
                    controlIndex[i] = controlList[i].index();
                c.assign("nonCfd", controlIndex);
                c.assign("method", "subclass");

                c.assign(".tmp.", "propensityTest(data,treat,nonCfd,method)");
                REXP r = c.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
                if (r.inherits("try-error")) {
                    System.err.println("Error: " + r.asString());
                    c.close();
                    return 0;
                }

                REXP tmpIndex = c.eval("propensityTest(data,treat,nonCfd,method)");
                matchedIndDouble = tmpIndex.asDoubles();

                c.close();
            } catch (Exception e) {
                System.out.println(e.toString());
            }

            int equiclassNum = 0;
            for (int i = 0; i < matchedIndDouble.length; i++)
                if (matchedIndDouble[i] < 0) {
                    equiclassNum = i / 3;
                    break;
                }
            for (int i = 0; i < 3 * equiclassNum; i++)
                equiclassDist[i] = (int) matchedIndDouble[i];
            equiclassDist = trimzeros(equiclassDist);

            int[] equiData = new int[matchedIndDouble.length - 3 * equiclassNum - 1]; // the index of all matched data
            for (int i = 0; i < equiData.length; i++)
                equiData[i] = (int) (matchedIndDouble[i + 3 * equiclassNum + 1] - 1);

            int startInd = 0;
            int endInd = 0;
            int dataInd = 0;
            double[][] contValue = new double[equiclassNum][4];
            for (int i = 0; i < equiclassNum; i++) {
                endInd = startInd + equiclassDist[i * 3];
                for (int j = startInd; j < endInd; j++) {
                    dataInd = equiData[j];
                    if (dataInd > data_keep.length)
                        System.out.println("1" + dataInd + " " + data_keep.length);
                    if (data_keep[dataInd][data.classAttribute().index()] == 1)
                        contValue[i][0]++; // Ma++;
                    else
                        contValue[i][1]++; // Mb++;
                }
                startInd = endInd;
                endInd = startInd + equiclassDist[i * 3 + 1];
                if (dataInd > data_keep.length)
                    System.out.println("2" + dataInd + " " + data_keep.length);
                for (int j = startInd; j < endInd; j++) {
                    dataInd = equiData[j];
                    if (data_keep[dataInd][data.classAttribute().index()] == 1)
                        contValue[i][2]++; // Mc++;
                    else
                        contValue[i][3]++; // Md++;
                }
                startInd = endInd;
            }

            PAVal = PAMHCalc(contValue, equiclassNum);
            */
        }


//		  m_Instances.removeElementAt(index);
//		  Instances tmpData = new Instances(data_keep, 0);

        return PAVal;
    }

    private double PAMHCalc(double[][] contTable, int equiclassNum) {
        double PAValue;
        double[] upValue = new double[equiclassNum];
        double[] downValue = new double[equiclassNum];
        double Ma, Mb, Mc, Md;
        double upValueSum = 0;
        double downValueSum = 0;
        double sum;

        for (int i = 0; i < equiclassNum; i++) {

	    		/*        contingency table
                 *       Class=0     Class=1
	    		 * A=0   V[0](Ma)    V[1](Mb)
	    		 * A=1   V[2](Mc)    V[3](Md)
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

    /**
     * generates contingency table for an attribute.
     *
     * @param att the attribute
     * @return the PA value for the given attribute and data
     * @throws Exception if computation fails
     */
    private double[] contingencyTable(ArrayList<double[]> eqiClass, int att)
            throws Exception {
          /* contValue:
           * 					classValue[0]	classValue[1]
		   * attAssTested[0]		a c[0]			b c[1]
		   * attAssTested[1]		c c[2]			d c[3]
		   */

        double[] contValue = new double[4];
        for (double[] eqiRecord : eqiClass) {
            if (eqiRecord[att] == 0 & eqiRecord[eqiRecord.length - 1] == 0)
                contValue[0]++;
            if (eqiRecord[att] == 0 & eqiRecord[eqiRecord.length - 1] == 1)
                contValue[1]++;
            if (eqiRecord[att] == 1 & eqiRecord[eqiRecord.length - 1] == 0)
                contValue[2]++;
            if (eqiRecord[att] == 1 & eqiRecord[eqiRecord.length - 1] == 1)
                contValue[3]++;
        }
        return contValue;
    }

    private double[] controlColumnSelect(double[] array, int[] controlList) {
        double[] controlColumn = new double[controlList.length];
        int[] controlIndex = new int[controlList.length];
        System.arraycopy(controlList, 0, controlIndex, 0, controlList.length);
        Arrays.sort(controlIndex);
        for (int i = 0; i < controlList.length; i++)
            controlColumn[i] = array[controlIndex[i]];
        return controlColumn;
    }

    /**
     * Splits a dataset according to the values of a nominal attribute.
     *
     * @param data the data which is to be split
     * @param att  the attribute to be used for splitting
     * @return the sets of instances produced by the split
     */
    private List<List<int[]>> splitData(List<int[]> data, int att, int numValue) {
        List<List<int[]>> splitData = new ArrayList<>();
        for (int j = 0; j < numValue; j++) {
            splitData.add(new ArrayList<int[]>());
        }
        for (int[] i : data) {
            splitData.get(i[att]).add(i);
        }
        return splitData;
    }

    /**
     * Returns index of maximum element in a given array of doubles. First maximum
     * is returned.
     *
     * @param doubles the array of doubles
     * @return the index of the maximum element
     */
    private static int maxIndex(double[] doubles) {

        double maximum = 0;
        int maxIndex = 0;

        for (int i = 0; i < doubles.length; i++) {
            if ((i == 0) || (doubles[i] > maximum)) {
                maxIndex = i;
                maximum = doubles[i];
            }
        }

        return maxIndex;
    }


    private class Pair {
        int value;
        double degree;

        Pair(int value, double degree) {
            this.value = value;
            this.degree = degree;
        }

    }

    private class TreeNode {
        List<TreeNode> children;
        TreeNode parent;
        /**
         * If this is not a leaf node, the attribute that is used to divide the set of data points
         */
        int decompositionAttribute;
        /**
         * the attribute-value that is used to divide the parent node
         */
        int decompositionValue;
        double PA;
        /**
         * Class value if node is leaf, 0 otherwise.
         */
        int m_ClassValue;

        /**
         * Class distribution size if node is leaf, null otherwise.
         */
        private double[] m_DistSize;

        int getHeight() {

            class N {
                private int height;
                private TreeNode value;

                private N(int height, TreeNode value) {
                    this.height = height;
                    this.value = value;
                }
            }
            int maxHeight = 1;
            LinkedList<N> queue = new LinkedList<>();
            queue.add(new N(1, this));
            while (!queue.isEmpty()) {
                N tempN = queue.poll();
                if (tempN.height > maxHeight) {
                    maxHeight = tempN.height;
                }
                if (tempN.value.children != null) {
                    for (TreeNode i : tempN.value.children) {
                        queue.add(new N(tempN.height + 1, i));
                    }
                }
            }
            return maxHeight;
        }


    }

}
