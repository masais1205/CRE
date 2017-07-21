/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package cre.algorithm.crpa;

/**
 * @author turinglife
 */


import cre.algorithm.CanShowOutput;
import cre.algorithm.crcs.PreprocessingLogic;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.lang.Math;
import java.lang.System;


/*

The core workflow of CR-PA algorithm.

Step 1: Figure out frequent predictive variables in binary data set table.

Step 2: For each frequent predictive variables, to generate its corresponding contingency table.

Step 3: Calculate Chi-square value on its contingency table for each predictive
        variables to judge whether this specified predictive variable is positively
        associated with target(outcome) variable.

Setp 4: Calculate partial association on positive association set.

*/
public class CRPA {

    final int FALSE = 0;
    final int TRUE = 1;
    final int LOCAL = 0;
    final int GLOBAL = 1;
    final int ALL = 0;
    final int CYCLE = 10;
    final int Nil = 0; /* null pointer */

    //final int IGNORE = 1; /* special attribute status: do not use */

    //final int DISCRETE = 2; /* Discreet: collect values as data read */

    final int CONTINUOUS = 3; /* continuous attribute */

    final double PURITY = 0.99; /* the number is considered as 1 */

    final int MAXTARGET = 4; /* the number of the maximum targets */

    final double MAXCOVERAGE = 0.95;

    int maxClass;             // zx, this variable specifies the total amount of class in the names file.
    int maxDiscrVal;
    String fileName;
    File f;                   //hs, for delete the .data and .names file in outputfile
    String[] className;
    String[] attName;
    int[] maxAttVal;
    //int[] allexposure;
    String[] specialStatus;
    //AttributeCode[] attCode;
    ItemRecord[] itemRecord;
    //int[][] attValue; // to store attribute values under an attribute
    int[] attribute; // to store attribute name an item belongs
    int maxAtt;
    int realAtt;              // zx, the amount of real attributes, which means it doesn't contain the ignored attributes.
    int maxData;              // zx, max line number of data files
    int maxControlNumber;     // hs, this number is for control number and less than 20.
    int maxDataBak;
    double[][] rawDataSpace;
    int[][] dataSpace;     // zx, dataSpace is a matrix(maxData * (realAtt+1)) which is not same as rawDataSpace.
    int[][] dataSpaceBak;
    column_object[] dataSpacestat;
    int maxItem;         // zx, index of each attribute value which numbers from 1.
    int treeSize;        // zx, total number of nodes in prefix tree except for root node.
    int singleRule;      // zx, total number of association rules with single predictive variable.
    int multiRule;
    int toughCov;
    int looseCov;
    int opt;
    int ass;
    int maxLayer;
    int discard;
    int complete;
    int excl;
    int method;
    int sub;
    int causal;
    int statisticTest = 0;     // 0: low bound (leftend); 1: odd ratio threshold
    int confidenceTest = 0;
    int chooseMethod;
    double staThreshold;
    SetGroupStru setGroup;

    int[][] counter;     // zx, the Node Count and Weight count.
    double total_runtime;  // unit of measure is seconds.

    // zx, the first column of counter matrix contains counts of the specific target value.

    // zx, CR-PA, start
    double t = 0.02; // this is a constant value for judging whether they are frequent.
    double s = 0.95; // support threshold, for calculating whether they are positive association.
    double e = 3.84; // a chi-square value conditioned on p = 0.05, for judging whether there exists nonzero partial association.

    // zx, CR-PA, end

    int gMinSup;       // zx, global minimum support
    int[] lMinSup;     // zx, local minimum support
    double minConf;    // zx, minimum confidence
    double minImp;
    double[] dist;     // zx, distribution
    double[] seconddist;
    int pruning;
    PrefixTree allSet;
    int maxTarget;
    double maxConf;
    int maxRuleAllowed;
    int heuristic;
    double ChisquareValue;  //hs, ChisquareValue is for x^2 ,the value is 3.84(0.95) or 2.71(0.90) or 6.64(0.99)
    double PaValue; //hs, PaValue is for PA , the value is 3.84(0.95) or 2.71(0.90) or 6.64(0.99)
    int[] ChosenTest;
    int[] ChosenControl;
    int Controlmethod;

    RuleSet ruleSet;
    RuleSet singleList;
    RuleSet singleListBak;

    double weight_n;
    double weight_p;

    double counter1;
    double counter2;
    double derivableRule;

    //char delimiter;
    int item_Id_Ceiling = 50;

    // beajy003
    //String storeName = "";
    File nf;
    Scanner scan;
    String scannedLine;
    double gsup;

    public class column_object {
        int min;        // min value in the specified column
        int max;        // max value in the specified column
        int numbers;    // number of values in the specified column
    }

    public class AttributeCode {

        String attr;
        int itemID;
        AttributeCode next;
    }

    public class ItemRecord {

        String attr;
        String attName;
    }

    public class RuleLabelStr {

        int index;
        int correct;
        int incorrect;
    }

    public class ContinuousValue {

        double lower;
        double upper;
        double center;
        int number;
        double contrast;
        ContinuousValue ahead;
        ContinuousValue next;
    }

    public class ItemSetStru {

        int localSupport;
        int globalSupport;
        int numofItems;
        int[] itemList;
        ItemSetStru aheadSet;
        ItemSetStru nextSet;
    }

    public class SupSetStru {

        int numofSets;
        ItemSetStru setHead;
    }

    public class SetGroupStru {

        int numofSet;
        int totalRecord;
        int coverNum;
        SupSetStru supSetList;
    }

    public class RuleStru {

        int len;
        int[] antecedent;
        int numOfTarget;
        int target[] = new int[MAXTARGET];
        double confidence;
        double support;
        double attSupport;
        double[] lSup;
        double accuracy;
        double relativeRisk;
        double oddsRatio;
        int token;
        RuleStru aheadRule;
        RuleStru nextRule;
        Boolean isCausalRule;
    }

    public class RuleSet {

        int numOfRule;
        int numofCorrect;
        double sumCorrect;
        int numofError;
        int defaultValue = -1;
        RuleStru ruleHead;
    }

    public class PrefixTree {
        int nodeID;             // zx, serial number of predictive variables. start from 1.
        int token;              /* Normal = 0, rule formed = 1, Terminate = 2, causal = 4*/

        double value;
        int len;                // zx, the level of node in prefix tree starting from 0 for root node.
        int[] set;              // zx, a maximal common set contained by this node.
        double gSup;            // zx, global support
        double[] lSup;          // zx, local support for each of type of target variable.(two categories)
        double[] subSup;
        PrefixTree[] subNode;
        int target[] = new int[MAXTARGET]; // zx, value of target variable corresponding to this node.(zero or one)
        int numOfSon;           // zx, count of children of this node.
        int numOfSon1;
        int memForSon;          /* Record the memory space for sons */

        int reserve;            /* this is for recording the place of node when counting */
        int index_in_dataspace; // zx, index number in dataspace corresponding to the node.nodeID.

        double acc;
        double conf;
        PrefixTree[] sonList;   // zx, list of children node
        PrefixTree[] sonList1;
        PrefixTree father;      // zx, father node
        boolean iscausal;
        int issupport;
        int ispa;               // 0 for nothing association, 1 for positive association(pa), 2 for negative association.
        boolean isfrequent;     // frequent
        boolean isMultivalue;   // zx, The node.ID does not correspond to single column in dataspace.
    }

    // CR-PA definition start

    private int[] indexvector;
    private int number_choosen_attributes;
    private int number_records;

    // CR-PA definition end

    public class SubRule {

        public SubRule(String namebase, double gsup) {
            int i, j, k, count;
            int item = 0;
            RuleStru rulecur;

            Scanner ui = new Scanner(System.in);

            backupData();
            rulecur = singleListBak.ruleHead;
            if (rulecur != null) {
                printf("No subrules \n ");
            }
            count = 0;

            while (rulecur != null) {
                if (count++ > 10) {
                    break;
                }
                // Prepare data
                item = rulecur.antecedent[0];

                printf("prepare sub rules for %s = %s (item %d, No. of records = %d) \n",
                        itemRecord[item].attName, itemRecord[item].attr, item,
                        maxData);
                flushDataSpace(rulecur);
                gMinSup = (int) (maxData * gsup + 0.5);
                // Find rules
                freeTree(allSet);
                freeAllRules(ruleSet);
                freeAllRules(singleList);
                initialCount();
                determineParameter(gsup);
                initWholeTree(allSet);
                k = 2;

                while (candidateGen(allSet, k) != 0) {
                    verification(allSet, k);
                    ruleSelectAndPruning(allSet, k);
                    if (++k > 3) {
                        break;
                    }
                }

                // write output for all optimal rules
                sprintf(fileName, "%s_%s_%s", namebase,
                        itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + "_opt.report";
                writeReport(fileName, minConf, gsup);

                sprintf(fileName, "%s_%s_%s", namebase,
                        itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + "_opt.csv";
                outputtoCSVfile(fileName, minConf, gsup);

                // choose k-optimal rules
                chooseKcompleterule();

                // write output for k-optimal rules
                sprintf(fileName, "%s_%s_%s", namebase,
                        itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + ".report";
                writeReport(fileName, minConf, gsup);

                sprintf(fileName, "%s_%s_%s", namebase,
                        itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + ".csv";
                outputtoCSVfile(fileName, minConf, gsup);

                // point to the next rules
                rulecur = rulecur.nextRule;
            }

            // The following part gets an input from the keyboard, and find
            // subrules for the item
            printf("\n The following are codes for all attribute-value pairs \n");

            for (i = 1; i < maxItem + 1; i++) {
                printf("%d  \t %s =  %s\n", i, itemRecord[i].attName,
                        itemRecord[i].attr);
            }

            printf("\n***Please enter a number for mining sub rules. Enter 0 to terminate the program***\n");
            // scanf("%d", item);
            item = ui.nextInt();
            if (item < 0 || item > maxItem) {
                printf("Invalid item code. Exit \n");
                System.exit(0);
            }

            while (item > 0) {

                printf("prepare sub rules for %s = %s (item %d, No. of records = %d) \n",
                        itemRecord[item].attName, itemRecord[item].attr, item,
                        maxData);
                // rulecur store a pseudo rule, information is not correct
                rulecur.antecedent[0] = item;
                flushDataSpace(rulecur);
                gMinSup = (int) (maxData * gsup + 0.5);
                // Find rules
                freeTree(allSet);
                freeAllRules(ruleSet);
                freeAllRules(singleList);
                initialCount();
                determineParameter(gsup);
                initWholeTree(allSet);
                k = 2;

                while (candidateGen(allSet, k) != 0) {
                    verification(allSet, k);
                    ruleSelectAndPruning(allSet, k);
                    if (++k > 3) {
                        break;
                    }
                }

                // write output for all optimal rules
                sprintf(fileName, "%s_%s_%s", namebase,
                        itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + "_opt.csv";
                outputtoCSVfile(fileName, minConf, gsup);

                sprintf(fileName, "%s_%s_%s", namebase,
                        itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + "_opt.report";
                writeReport(fileName, minConf, gsup);

                // choose k-optimal rules
                chooseKcompleterule();

                // write output for k-optimal rules
                sprintf(fileName, "%s_%s_%s", namebase,
                        itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + ".report";
                writeReport(fileName, minConf, gsup);

                sprintf(fileName, "%s_%s_%s", namebase,
                        itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + ".csv";
                outputtoCSVfile(fileName, minConf, gsup);

                printf("\n***Please enter a number for mining sub rules. Enter 0 to terminate the program***\n");
                // scanf("%d", item);
                item = ui.nextInt();
                if (item < 0 || item > maxItem) {
                    printf("Invalid item code. Exit \n");
                    System.exit(0);
                }
            }

            printf("Finish \n");
        }

        public void backupData() {
            int i, j;
            RuleSet tmpruleset;

            maxDataBak = maxData;

            dataSpaceBak = new int[maxDataBak][];

            for (i = 0; i < maxData; i++) {
                dataSpaceBak[i] = new int[1];
                for (j = 0; j < realAtt; j++) {
                    dataSpaceBak[i][j] = dataSpace[i][j];
                }
            }

            tmpruleset = new RuleSet();
            tmpruleset.numOfRule = 0;
            tmpruleset.ruleHead = null;

            singleListBak = singleList;
            singleList = tmpruleset;

        }

        public void flushDataSpace(RuleStru rule) {
            int i, j, line;
            int[] row;

            maxData = 0;
            line = 0;
            for (i = 0; i < maxDataBak; i++) {
                row = dataSpaceBak[i];
                // if matching
                if (matchTest(row, rule) >= 0) {
                    maxData++;
                    for (j = 0; j < realAtt; j++) {
                        dataSpace[line][j] = row[j];
                    }
                    line++;
                }
            }

            // clear the remaining data space
            for (i = maxData; i < maxDataBak; i++) {
                for (j = 0; j < realAtt; j++) {
                    dataSpace[i][j] = 0;
                }
            }
        }

        public void freeAllRules(RuleSet ruleset) {
            RuleStru cur, last, next;

            ruleset.numOfRule = 0;
            if (ruleset.ruleHead == null) {
                return;
            }

            cur = ruleset.ruleHead;
            next = cur;

            while (next != null) {
                cur = next;
                next = cur.nextRule;
            }

            last = cur;
            cur = last.aheadRule;
            free(last.antecedent);
            free(last.lSup);
            free(last);

            while (cur != null) {
                last = cur;
                cur = last.aheadRule;
                free(last.antecedent);
                free(last.lSup);
                free(last);
            }

            ruleset.numOfRule = 0;
            ruleset.ruleHead = null;

        }
    }

    public CRPA() {
        super();
        // System.out.println("!! beajy003 - Beajy003Cluster - Beajy003Cluster() - Begin");
    }

    private CanShowOutput canShowOutput;

    public CRPA(int argc, char[] argv,
                PreprocessingLogic.retclass ret,
                CRPAConfig.P p,
                CRPAConfig.Values pp, CanShowOutput canShowOutput) {
        this.canShowOutput = canShowOutput;
        int o;
        int i, j, k, flag, max, min;
        double cdura1, cdura2, cdura3;
        //int[] allexposure = new int[realAtt];

        double t1, t2, t3 = 0;
        String namebase = "";
        String tmp;

        // stat run time
        long startTime = System.currentTimeMillis();

        attName = ret.attName;
        //attValue = ret.attValue;

        attribute = ret.attribute;
        className = ret.className;
        dataSpace = ret.dataSpace;

        fileName = ret.fileName;
        //itemRecord = ret.itemRecord;

        item_Id_Ceiling = ret.item_Id_Ceiling;
        //System.out.println("item_Id_Ceiling ="+ ret.item_Id_Ceiling);
        //hs. item_ID_Ceiling is depend on the sum of items
        //System.out.println(item_Id_Ceiling);

        itemRecord = new ItemRecord[item_Id_Ceiling];
        for (int m = 0; m < item_Id_Ceiling; m++) {
            itemRecord[m] = addItemRecord("", "");
        }

        //System.out.println("ret.itemRecord.length");
        //System.out.println(ret.itemRecord.length);

        for (int m = 0; (m < item_Id_Ceiling && ret.itemRecord[m] != null); m++) {
            itemRecord[m].attName = ret.itemRecord[m].attName;
            itemRecord[m].attr = ret.itemRecord[m].attr;
        }

        //maxAtt = ret.maxAtt;
        //maxAttVal = ret.maxAttVal;

        maxAtt = ret.maxAtt;
        maxAttVal = ret.maxAttVal;

        maxClass = ret.maxClass;
        maxData = ret.maxData;

//        if((int)(Math.log(maxData)/Math.log(2))<20){
//            maxControlNumber = (int)(Math.log(maxData)/Math.log(2));
//        }else{
//            maxControlNumber = 20;
//        }
//        System.out.println("maxControlNumber="+maxControlNumber);

        maxItem = ret.maxItem;
        System.out.println("maxItem" + maxItem);
        namebase = ret.namebase;
        //nf = ret.nf;
        nf = ret.nf;
        //rawDataSpace = ret.rawDataSpace;
        realAtt = ret.realAtt;
        //scan = ret.scan;
        //scannedLine = ret.scannedLine;
        //specialStatus = ret.specialStatus;

        scan = ret.scan;
        scannedLine = ret.scannedLine;
        specialStatus = ret.specialStatus;

        //dataSpace is a matrix(maxData * (realAtt+1))

//        System.out.println("dataSpace");
//        for(i = 0; i < maxData; i++)
//        {
//            for(j = 0; j < realAtt + 1; j++)
//            {
//                System.out.print(dataSpace[i][j] + " ");
//            }
//
//            System.out.println();
//        }


        // name for execution. From 3 to 2.
        if (argc < 2) {
            printf("\n Simple usage:\n");

            printf("\t ./rule -f fileName (without extension) \n"
                    + "\t -s Local Support (default 0.05) \n	"
                    + " -l maximum length of rules (default 4)  \n "
                    + "\t -r 1 redundant rules (default no)  \n "
                    + "\t -m 1 find subrules for some attribute-value pairs (default no) \n"
                    + "\t This program focuses only on the first class in two-class data.  \n "
                    + "\t Please put the focused class first  \n"
                    + "\t the automatic report is in fileName.report \n \n");
            System.exit(0);
        }
        /*
         * printf(
         * "\t ./rule -f File name (without extension) \n	-s Local Support (default 0.1) \n	-c Confidence (default 0.8), which carries exclusiveness when -e 1 is set (suggested >= 1/Maxclasses)\n	-e Using exclusiveness as interestingness \n	-l MaxLayer (default 4) \n  -i MinImp (default 0.01) \n 	-a Mining complete rule set (default no) \n	-o Mining optimal rule set (default no) \n 	-r Robust rule mining (default 1) \n 	-d discard low confidence rules (default no) \n \n"
         * ); return(0); }
         *
         * Opt = 0; Ass = 0; Excl = 0; MaxLayer = 6; gsup = 0.01; MinConf = 0.8;
         * MinImp = 0.00; Discard = 0; Complete = 1;
         */

        opt = 0;
        ass = 0;
        sub = 0;
        PaValue = p.PaValue; //hs.
        ChosenTest = pp.ChosenTest;
        ChosenControl = pp.ChosenControl;
        Controlmethod = pp.Controlmethod;
        System.out.println("hushustart" + Controlmethod);
        System.out.println("PaValue=" + PaValue);
        //System.out.println("maxItem="+maxItem);
        ChisquareValue = p.ChisquareValue; //hs.
        System.out.println("ChisquareValue=" + ChisquareValue);
        maxLayer = p.num_combinedvariables;
        //maxLayer = 2;
        gsup = p.gsup;
        staThreshold = p.oddsratio;
        // the minimum confidence has not been used, and the minimum odds ratio
        // is set to 1.5
        minConf = 0.6;
        minImp = 0.01;
        discard = 0;
        complete = 1;
        maxRuleAllowed = 10000;
        // These two have to be on
        //excl = 1;
        method = 3;  //zx, 1 for exclusiveness, 2 for tranditional, 3 for CR-PA
        heuristic = 1;

        fileName = "";

        // begin_t = new Time(0);
        // printf("\t\t\t\t\t %s \n ", begin_t);
        // printf("\t This is a multipurpose rule discovery tool.\n\t This program was authored by Dr Jiuyong Li (www.sci.usq.edu.au/staff/jiuyong).\n\t Contact jiuyong@usq.edu.au to obtain a manual \n\n\n");

        /* Process options */
        // Beajy003 Code for accepting input
        for (int n = 0; n < argv.length; n++) {
            if (argv[n] == '-') {
                if (n != 0) {
                    if (argv[n - 1] != ' ') {
                        break;
                    }
                }
                switch (argv[n + 1]) {
                    case 'f':
                        fileName = "";
                        int fileNameCharacter = n + 3;
                        while (fileNameCharacter < argv.length
                                && argv[fileNameCharacter] != ' ') {
                            fileName += argv[fileNameCharacter];
                            fileNameCharacter++;
                        }
                        printf("\t File Name: %s\n", fileName);
                        break;

                    case 's':
                        String globalSupport = "";
                        int globalSupportCharacter = n + 3;
                        while (globalSupportCharacter < argv.length
                                && argv[globalSupportCharacter] != ' ') {
                            globalSupport += argv[globalSupportCharacter];
                            globalSupportCharacter++;
                        }
                        gsup = Double.parseDouble(globalSupport);

                        printf("\t Global Support = %.4f \n", gsup);
                        break;

                    case 'c':
                        String minimumConfidence = "";
                        int minimumConfidenceCharacter = n + 3;
                        while (minimumConfidenceCharacter < argv.length
                                && argv[minimumConfidenceCharacter] != ' ') {
                            minimumConfidence += argv[minimumConfidenceCharacter];
                            minimumConfidenceCharacter++;
                        }
                        minConf = Double.parseDouble(minimumConfidence);

                        printf("\t Global Confidence = %.4f \n", minConf);
                        //test1

                        break;

                    case 'l':
                        String maximumLayer = "";
                        int maximumLayerCharacter = n + 3;
                        while (maximumLayerCharacter < argv.length
                                && argv[maximumLayerCharacter] != ' ') {
                            maximumLayer += argv[maximumLayerCharacter];
                            maximumLayerCharacter++;
                        }
                        maxLayer = Integer.parseInt(maximumLayer);

                        printf("\t Max Layer = % d \n", maxLayer);
                        break;

                    case 'i':
                        String minimumImp = "";
                        int minimumImpCharacter = n + 3;
                        while (minimumImpCharacter < argv.length
                                && argv[minimumImpCharacter] != ' ') {
                            minimumImp += argv[minimumImpCharacter];
                            minimumImpCharacter++;
                        }
                        minImp = Double.parseDouble(minimumImp);

                        printf("\t MinImp = % f \n", minImp);
                        break;
                    case 't':
                        statisticTest = 1;
//                            String staThr = "";
//                            int staCharacter = n + 3;
//                            while (staCharacter < argv.length && argv[staCharacter] != ' ') {
//                                    staThr += argv[staCharacter];
//                                    staCharacter++;
//                            }
//                            staThreshold = Double.parseDouble(staThr);

                        printf("\t Statistic Threshold (odds ratio) = %.4f \n", staThreshold);
                        break;
                    case 'h':
                        chooseMethod = 1;

                        break;

                    case 'o':
                        opt = 1;
                        printf("\t Mining Optimal Rule Set \n");
                        break;

                    case 'a':
                        ass = 1;
                        printf("\t Mining Complete Rule Set \n");
                        // test2
                        break;

                    case 'd':
                        discard = 1;
                        printf("\t Discard the remaining rules \n");
                        break;

                    case 'e':
                        excl = 1;
                        printf("\t Using exclusiveness = %.4f \n", minConf);
                        break;

                    case 'r':
                        String completeSolution = "";
                        int completeSolutionCharacter = n + 3;
                        while (completeSolutionCharacter < argv.length
                                && argv[completeSolutionCharacter] != ' ') {
                            completeSolution += argv[completeSolutionCharacter];
                            completeSolutionCharacter++;
                        }
                        complete = Integer.parseInt(completeSolution);

                        printf("\t Mining one complete rule set \n");
                        break;

                    case 'm':
                        sub = 1;
                        printf("\t Find sub rules for some attribute-value pairs\n");
                        break;

                    // Thuc set causal option
                    case 'x':
                        causal = 1;
                        printf("\t Mining causal rules \n");
                        break;

                    // Xin implements CR-PA
                    case 'p':
                        causal = 1;
                        System.out.println("Mining causal rules using partial association");

                    case '?':
                        printf("## ERROR - Rule - Rule() - Dash given without an option - Character no:"
                                + n + ": proceding letter:" + argv[n + 1] + ":");
                        System.exit(1);
                }
                n = n + 2;
            }
        }


        /*
        for(i = 0; i < dataSpace.length; i++)
        {
            for(j = 0; j < dataSpace[0].length; j++)
            {
                System.out.print(dataSpace[i][j] + " ");
            }

            System.out.println();
        }
        */

        // sort data
        // sortArray(dataSpace);
        // zx, maxData: max line number of data files
        //hs. System.out.println("gMinSup="+gMinSup); gMinSup==0
        //hs. System.out.println("maxData="+maxData); maxData==5000
        //hs. System.out.println("gsup="+gsup); gsup==0.05
        gMinSup = (int) (maxData * gsup + 0.5);
        System.out.println("gsup=" + gsup);
        //hs. System.out.println("gMinSup="+gMinSup);
        // System.out.println("!! beajy003 - Rule - Rule() - gMinSup:"+gMinSup+":");

        // Find association rules
        // zx, here is the code for generating first level's rules on prefix tree.
        //System.out.println("ChosenTest.length="+ChosenTest.length);
        if (ChosenTest.length == 0) {
            //System.out.println("Choose0");
            //System.out.println("beforeruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
            generateFirstLevelRules();
            System.out.println("afterruleset.ruleHead.isCausalRule=" + ruleSet.ruleHead.isCausalRule);
            //int[] allexposure = new int[realAtt];

            // Test if a rule is causal
            if (causal != 0) {
                causalTest_CRPA(allSet);
                // causalTest(ruleSet, 2);
                // displayAbsractRule(0);
            }

            printf("\n Report of %d layer \n", 1);
            report();

            long endTime = System.currentTimeMillis();

            total_runtime = (endTime - startTime);

            System.out.println("total_runtime=" + total_runtime);


            if (maxLayer == 1) {
                // calculate the total run time whose unit of measurement is millisecond
                //long endTime = System.currentTimeMillis();

                //total_runtime = (endTime - startTime);

                //System.out.println("total_runtime="+total_runtime);

                fileName = namebase;
                fileName = fileName + "_opt.report";
                System.out.println("fileName = " + fileName);

                //writeReport(fileName, minConf, gsup);

                fileName = namebase;
                fileName = fileName + "_opt.csv";
                outputtoCSVfile(fileName, minConf, gsup);

                fileName = namebase;
                outputToCanshowOutput(minConf, gsup);

                f = new File("current.data");
                f.delete();

                f = new File("current.names");
                f.delete();

                return;
            }
            //System.out.println("maxData =" + maxData);
            //System.out.println("realAtt = " + realAtt);



        /*
         * for each item in the singleList do find confounder(item) return a
         * list generate the fair data set for item validate causal rule
         */
            // zx, k representing k-th level. Since this algorithm currently only consider
            // two predictive variables as a combined variable, k equals to 2.
            // zx, the first level has been generated above. here are the codes for
            // handling the combined variable with one targer variable.


            k = 2;

            //System.out.println(candidateGen(allSet, k));
            //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
            while (candidateGen(allSet, k) > 0) {
                //printf("\n This is the %d th layer, before pruning \n ", k);
                displayTree(allSet);
                //printf("The number of tree nodes %d \n", treeSize);
                //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                verification(allSet, k);
                //displayTree(allSet);

                //printf("2 The number of tree nodes %d \n", treeSize);

                //System.out.println("not select rules and pruning yet");
                //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                ruleSelectAndPruning(allSet, k);

                //printf("\n This is the %d th layer, After pruning \n ", k);
                displayTree(allSet);
                // report();

                //printf("3 The number of tree nodes %d \n", treeSize);

                // causal test
                if (causal != 0) {
                    //causalTest(allSet, k);
                    int[] allexposure = new int[realAtt];
                    int[] allexposure_item = new int[allSet.numOfSon + 1];
//                System.out.println("hushu");
//                for(i=0;i<allSet.numOfSon;i++){
//                    System.out.println("hushu1");
//                    System.out.println(allSet.sonList[i].len);
//                    if(allSet.sonList[i].len == k){
//                        System.out.println("hushu2");
//                        for( j = 0;j < allSet.sonList[i].len;j++){
//                            System.out.println("hushu3");
//                            if(allSet.sonList[i].ispa==1){
//                                System.out.println("hushu4");
//                                for(int h=0,f=0;h<realAtt;h++,f++){
//                                    System.out.println("hushu5");
//                                    if(dataSpacestat[h].min <=  allSet.sonList[i].set[j] &&  allSet.sonList[i].set[j] <= dataSpacestat[h].max){
//                                        System.out.println("hushu6");
//                                        allexposure[f] = 1;
//                                        System.out.println(f);
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//
//                }
//                System.out.println("hushu7");
                    //causalTestsmall_CRPA(allSet, k,allexposure);
                    causalTest_CRPA(allSet, allexposure, allexposure_item);
                    //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                    causalTest_CRPA(allSet, k, allexposure, allexposure_item, allSet);
                    //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                    //System.out.println("ruleSet.ruleHead.len="+ruleSet.ruleHead.len);
                }
                // printf("\n This is the %d th layer, After causaltest \n ", k);
                // displayAbsractRule(0);

                // displayTree(allSet);
                // CoverageCount(AllSet, DataSpace, MaxData);
                printf("\n Report of %d layers \n", k);

                // if(!Opt && !Ass) { if (Report ()) break; }
                // else Report();
                report();
                long endTime1 = System.currentTimeMillis();
                total_runtime = (endTime1 - startTime);

                System.out.println("total_runtime=" + total_runtime);


                if (++k > maxLayer) {
                    break;
                }
            }

            // calculate the total run time whose unit of measurement is millisecond

            //long endTime1 = System.currentTimeMillis();
            //total_runtime = (endTime1 - startTime);

            //System.out.println("total_runtime="+total_runtime);

            fileName = namebase;
            fileName = fileName + "_opt.report";
            System.out.println("fileName = " + fileName);

            //writeReport(fileName, minConf, gsup);

            fileName = namebase;
            fileName = fileName + "_opt.csv";
            outputtoCSVfile(fileName, minConf, gsup);

            fileName = namebase;
            outputToCanshowOutput(minConf, gsup);

            f = new File("current.data");
            f.delete();

            f = new File("current.names");
            f.delete();
        } else if (ChosenTest.length == 1) {
            generateFirstLevelRules();
            //int[] allexposure = new int[realAtt];

            // Test if a rule is causal
            if (causal != 0) {
                causalTest_CRPA_One(allSet);
                // causalTest(ruleSet, 2);
                // displayAbsractRule(0);
            }

            printf("\n Report of %d layer \n", 1);
            report();

            long endTime = System.currentTimeMillis();

            total_runtime = (endTime - startTime);

            System.out.println("total_runtime=" + total_runtime);

            // calculate the total run time whose unit of measurement is millisecond
            //long endTime = System.currentTimeMillis();

            //total_runtime = (endTime - startTime);

            //System.out.println("total_runtime="+total_runtime);

            fileName = namebase;
            fileName = fileName + "_opt.report";
            System.out.println("fileName = " + fileName);

            //writeReport(fileName, minConf, gsup);

            fileName = namebase;
            fileName = fileName + "_opt.csv";
            outputtoCSVfile(fileName, minConf, gsup);

            fileName = namebase;
            outputToCanshowOutput(minConf, gsup);

            f = new File("current.data");
            f.delete();

            f = new File("current.names");
            f.delete();

            return;


        } else if (ChosenTest.length >= 2) {
            //System.out.println("Choose 2");
            //System.out.println("beforeruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
            System.out.println("hushu level=3");
            generateFirstLevelRules();
            if (causal != 0) {
                causalTest_CRPA_One(allSet);
                //causalTest(ruleSet, 2);
                // displayAbsractRule(0);
            }
            printf("\n Report of %d layer \n", 1);
            report();

            long endTime = System.currentTimeMillis();

            total_runtime = (endTime - startTime);

            System.out.println("total_runtime=" + total_runtime);


            if (maxLayer == 1) {
                // calculate the total run time whose unit of measurement is millisecond
                //long endTime = System.currentTimeMillis();

                //total_runtime = (endTime - startTime);

                //System.out.println("total_runtime="+total_runtime);

                fileName = namebase;
                fileName = fileName + "_opt.report";
                System.out.println("fileName = " + fileName);

                //writeReport(fileName, minConf, gsup);

                fileName = namebase;
                fileName = fileName + "_opt.csv";
                outputtoCSVfile(fileName, minConf, gsup);

                fileName = namebase;
                outputToCanshowOutput(minConf, gsup);

                f = new File("current.data");
                f.delete();

                f = new File("current.names");
                f.delete();

                return;
            }

            k = 2;
            //hs.add may have problem.check later
            /*
            while(ruleSet.ruleHead!=null){
                System.out.println("ruleSet.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                ruleSet.ruleHead=ruleSet.ruleHead.nextRule;

            }
            //hs.add end
            //System.out.println("hereruleSet.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
            if((ruleSet.ruleHead!=null)){
            ruleSet.ruleHead.isCausalRule = true;
            }
            */
            //System.out.println("ruleSet.ruleHead.numberofRule="+ruleSet.numOfRule);

            //System.out.println("ruleSet.ruleHead.len="+ruleSet.ruleHead.len);
            //System.out.println("afterruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
            while (candidateGen_Two(allSet, k) > 0) {
                displayTree(allSet);
                //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                //ruleSet.ruleHead.isCausalRule = true;
                verification_Two(allSet, k);
                //displayTree(allSet);
                //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                ruleSelectAndPruning_Two(allSet, k);
                displayTree(allSet);
//                if((ruleSet.ruleHead==null)){
//                ruleSet.ruleHead.isCausalRule = true;
//                }
                //ruleSet.ruleHead.isCausalRule = true;
                if (causal != 0) {
                    //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                    int[] allexposure = new int[realAtt];
                    int[] allexposure_item = new int[allSet.numOfSon + 1];

                    causalTest_CRPA_Two(allSet, allexposure, allexposure_item);
                    causalTest_CRPA_Two(allSet, k, allexposure, allexposure_item, allSet);
                    //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                    // causalTest(ruleSet, 2);
                    // displayAbsractRule(0);

                }


                printf("\n Report of %d layers \n", k);

                // if(!Opt && !Ass) { if (Report ()) break; }
                // else Report();
                //System.out.println("hushuruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
                //ruleSet.ruleHead.isCausalRule = true;
                //report_Two();
                report();

                long endTime1 = System.currentTimeMillis();
                total_runtime = (endTime1 - startTime);

                System.out.println("total_runtime=" + total_runtime);

                if (++k > maxLayer) {
                    break;
                }

            }


            fileName = namebase;
            fileName = fileName + "_opt.report";
            System.out.println("fileName = " + fileName);

            //writeReport(fileName, minConf, gsup);

            fileName = namebase;
            fileName = fileName + "_opt.csv";
            outputtoCSVfile(fileName, minConf, gsup);

            fileName = namebase;
            outputToCanshowOutput(minConf, gsup);

            f = new File("current.data");
            f.delete();

            f = new File("current.names");
            f.delete();
            //return;
        }


        // Test ();
        // t1 = end_t.getTime() - begin_t.getTime();
        // cdura1 = (double) (end_c - begin_c)/CLOCKS_PER_SEC;
        // printf("the cpu time for rule forming is %f \n", cdura1);
        if (ass != 0) {
            System.exit(0);
        }

        // TestAndSetDefult();
        // begin_c = new Clock();
        // begin_t = new Time(0);
        if (opt == 0) {
            chooseKcompleterule();
        }


        // end_c = new Clock();
        // end_t = new Time(0);
        // t2 = end_t.getTime() - begin_t.getTime();
        // cdura2 = (double)(end_c - begin_c)/CLOCKS_PER_SEC;
        // printf("\n the cpu time for selecting is %f \n", cdura2);
        // DisplayAbsractRule (0);
        // Test();

        /*
         * begin_c = clock(); begin_t = time(NULL);
         *
         * OrderRuleAndSetDefault();
         *
         * end_c = clock(); end_t = time(NULL); t3 = difftime(end_t, begin_t);
         * cdura3 = (double) (end_c - begin_c)/CLOCKS_PER_SEC;
         * printf("the cpu time for rule ordering is %f \n", cdura3);
         *
         *
         * printf("\n After seting default \n"); Test ();
         */
        if (ass == 0) {

            // remove the the prefix in the file name if there is
            tmp = namebase;
            namebase = tmp + 1;

            /*
             * strcpy(FileName, namebase); strcat(FileName, ".robust");
             * WriteRule(FileName, MinConf, gsup);
             */
            fileName = namebase;
            fileName = fileName + ".report";
            //writeReport(fileName, minConf, gsup);
            fileName = namebase;
            fileName = fileName + ".csv";
            //outputtoCSVfile(fileName, minConf, gsup);

        }

        // printf("the cpu time for rule forming is %.2f \n",
        // (cdura1+cdura2+cdura3) );
        // printf("\n the runing time is %.0f", (t1 + t2 + t3));
        // printf("\n \t\t finish time is %s ", end_t);
        // Search for subrules
        if (sub == 0) {
            System.out.println("here");

            return;

            //System.exit(0);
        }
        fileName = namebase;
        fileName = fileName + "_SingleRuleList.report";
        writeReport(fileName, minConf, gsup);

        fileName = namebase;
        fileName = fileName + "_SingleRuleList.csv";
        outputtoCSVfile(fileName, minConf, gsup);

        System.out.println("before");

//        new SubRule(namebase, gsup);

        System.out.println("zxzxzxzzxzx");

    }

    public void printRecord(int[] record) {

        for (int x = 0; x < record.length; x++) {
            System.out.format("%d, ", record[x]);
        }
        System.out.print("\n");
    }

    public void printArrayList(ArrayList<int[]> fairdts) {
        int indfair = 1;
        for (int[] x : fairdts) {
            System.out.format("%d: ", indfair++);
            for (int y = 0; y < x.length; y++) {
                System.out.format("%d, ", x[y]);

            }
            System.out.print("\n");
        }
    }

    public void generateFirstLevelRules() {

        allSet = new PrefixTree();
        //hs.test System.out.println("hs");

        // zx, initialize the Node Count and Weight count
        // zx, generate a new matrix for counting.
        initialCount();

        // zx, gsup = 0.05
        determineParameter(gsup);
        //hs.test System.out.println("hs");

        // Print out the counter
        // for (int x=0; x<counter.length; x++){
        // for (int y=0; y<counter[x].length; y++){
        // System.out.println("counter["+x+"]["+y+"]="+counter[x][y]);
        // }
        // }
        initRuleSet();

        // zx, add
        initCOtable();

        initWholeTree(allSet);
        // System.out.print(" Display rules in singleList - ");
        // displayAbsractRule (1);
        // System.out.print(" Display rules in ruleSet - ");
        // displayAbsractRule (0);
        // report();
    }


    public double logfactorial(int n) {
        double logfact = 0;
        for (int i = 1; i <= n; i++) {
            logfact = logfact + Math.log(i);
        }

        return logfact;
    }

    // zx, add for sorting the dataSpace
    public static void sort(int[][] ob, final int[] order) {
        Arrays.sort(ob, new Comparator<Object>() {

            @Override
            public int compare(Object o1, Object o2) {
                int[] one = (int[]) o1;
                int[] two = (int[]) o2;
                for (int i = 0; i < order.length; i++) {
                    int k = order[i];

                    //System.out.println("k = " + k);

                    //System.out.println("one[k] = " + one[k] + ", two[k] = " + two[k]);

                    // descending order
                    if (one[k] > two[k]) {
                        return 1;
                    } else if (one[k] < two[k]) {
                        // first argument is less than the second
                        return -1;
                    } else {
                        // the first object is equal to the second one
                        // when the first condition are equivalent, then compare the second condition.
                    }
                }

                return 0;
            }
        });
    }

    /*

    reduced_matrix is a new matrix which contains rows of amount_of_unique_rows and
    columns of (realAtt + 2).

    the last column represents the amount of the rows.
    the last but one represents the target value.

    */
    public int[][] unique(int[][] original_matrix) {
        int i, j, amount_of_unique_rows = 0, unique_index = 0;
        int[] array_of_unique_index = new int[maxData];
        int[] array_of_unique_rows_amount = new int[maxData];
        int[] unique_row = new int[realAtt + 1];
        boolean found_unique_row = true;


//        System.out.println("maxData = " + maxData);
//        System.out.println("realAtt = " + realAtt);

        unique_row = original_matrix[0].clone();
        array_of_unique_index[amount_of_unique_rows] = 0;
        amount_of_unique_rows++;


        for (i = 0; i < maxData; i++) {
            if ((i + 1) < maxData) {
                if (Arrays.equals(unique_row, original_matrix[i + 1])) {
                    //System.out.println("same rows: " + i + "->" + (i + 1));

                    found_unique_row = false;
                } else {
                    //System.out.println("different rows: " + i + "->" + (i + 1));

                    found_unique_row = true;
                }

                if (found_unique_row) {

                    unique_row = original_matrix[i + 1].clone();
                    array_of_unique_index[amount_of_unique_rows] = i + 1;
                    amount_of_unique_rows++;
                }
            }

        }

        //for(i = 0; i < array_of_unique_index.length; i++)
        //    System.out.println(array_of_unique_index[i]);

        //System.out.println("array_of_unique_index.length = " + array_of_unique_index.length);
        //System.out.println("amount_of_unique_rows = " + amount_of_unique_rows);

        // define a new matrix containing the compressed matrix.
        int[][] reduced_matrix = new int[amount_of_unique_rows][realAtt + 2];

        /*
        System.out.println("initial");
        for(i = 0; i < amount_of_unique_rows; i++)
        {
            for(j = 0; j < realAtt + 2; j++)
                System.out.print(reduced_matrix[i][j] + " ");

            //System.out.prCR-PA is a causal rule discovery algorithmintln();
        }
        */


        for (i = 0; i < amount_of_unique_rows; i++) {
            for (j = 0; j < realAtt + 1; j++)
                reduced_matrix[i][j] = original_matrix[array_of_unique_index[i]][j];

            //for(j = 0; j < realAtt + 2; j++)
            //    System.out.print(reduced_matrix[i][j] + " ");

            if ((i + 1) != amount_of_unique_rows) {
                array_of_unique_rows_amount[i] = array_of_unique_index[i + 1] - array_of_unique_index[i];
            } else {
                array_of_unique_rows_amount[i] = maxData - array_of_unique_index[i];
            }

            reduced_matrix[i][realAtt + 1] = array_of_unique_rows_amount[i];
        }

        /*
        System.out.println("new");
        for(i = 0; i < amount_of_unique_rows; i++)
        {
            for(j = 0; j < realAtt + 2; j++)
                System.out.print(reduced_matrix[i][j] + " ");

            System.out.println();
        }
        */

        /*
        System.out.println("--------reduced matrix-----");
        for(i = 0; i < amount_of_unique_rows; i++)
            for(j = 0; j < realAtt + 2; j++)
                System.out.println(reduced_matrix[i][j] + " ");
            System.out.println();
        */

        return reduced_matrix;
    }


    public int[][] equivalence() {
        int[][] matrix;
        int i, j;
        int[][] reduced_matrix;
        int[] order = new int[dataSpace[0].length];
        //System.out.println("dataSpace[0].length="+dataSpace[0].length);

        /*
        System.out.println("-----------before sorting dataSpace------------");
        for(i = 0; i < maxData; i++)
        {
            for(j = 0; j < realAtt + 1; j++)
            {
                System.out.print(dataSpace[i][j] + " ");
            }

            System.out.println();
        }
        */


//        System.out.println("before dataSpace[0].length = " + dataSpace[0].length);

        for (i = 0; i < dataSpace[0].length; i++) {
            order[i] = i;
        }


        //sort(dataSpace, new int[] {0, 1, 2, 3, 4, 5, 6});
        sort(dataSpace, order);

//        System.out.println("after dataSpace[0].length = " + dataSpace[0].length);

        // clone a new matrix for finding equivalent class.
        matrix = dataSpace.clone();


//        System.out.println("-----------after sorting matrix------------");
//        for(i = 0; i < maxData; i++)
//        {
//            for(j = 0; j < realAtt + 1; j++)
//            {
//                System.out.print(matrix[i][j] + " ");
//            }
//
//            System.out.println();
//        }
//


        reduced_matrix = unique(matrix);

//        System.out.println("-----------after unique------------");
//        for(i = 0; i < reduced_matrix.length; i++)
//        {
//            for(j = 0; j < reduced_matrix[0].length; j++)
//            {
//                System.out.print(reduced_matrix[i][j] + " ");
//            }
//
//            System.out.println();
//        }

        return reduced_matrix;
    }

    //hs.start
    public void determineParameter(double suprate, int exposure, int[] allexposure, int[] allexposure_item, int nodeID) {
        int i, j, k, targetvalue, item;
        maxTarget = 1;
        initSecondParameter();
        for (i = 0; i < maxData; i++) {
            targetvalue = dataSpace[i][exposure];
            //System.out.println("hushutargetvalue"+targetvalue);
            //System.out.println("hushunodeID"+nodeID);

            //System.out.println("hushu");
            if (nodeID == targetvalue) {
                targetvalue = 1;
                counter[targetvalue][0]++;
            } else {
                targetvalue = 0;
                counter[targetvalue][0]++;
            }
            //counter[targetvalue][0]++;

            for (j = 0; j < allexposure.length; j++) {
                if ((allexposure[j] == 1) && (j != exposure)) {
                    item = dataSpace[i][j];
                    //System.out.println("hushuitem"+item);
                    if (item == 0) {
                        continue;
                    }
                    for (k = 0; k < allexposure_item.length; k++) {
                        if ((allexposure_item[k] == 1) && (k != nodeID)) {
                            if (k == item) {
                                counter[targetvalue][item]++;
                                counter[maxClass][item]++;
                                //System.out.println("hushuj_item "+targetvalue+" "+counter[targetvalue][item]);
                            }
//                            else{
//                                System.out.println("hushuj_item 0k"+k);
//                                counter[0][item]++;
//                                System.out.println("hushuj_item 0"+counter[0][item]);
//                            }
                            //counter[maxClass][item]++;
                        }

                    }

                    //counter[targetvalue][item]++;

                    //counter[maxClass][item]++;


                }

            }

        }
        for (i = 0; i < maxClass; i++) {
            lMinSup[i] = (int) (counter[i][0] * suprate + 0.5);
            seconddist[i] = counter[i][0];
            //System.out.println("dist["+i+"]:"+seconddist[i]);
        }

        /*
         for (i = 0; i < allexposure_item.length; i++) {
            System.out.print(counter[0][i]+" ");
        }
         System.out.println();
         for (i = 0; i < allexposure_item.length; i++) {
            System.out.print(counter[1][i]+" ");
        }
         System.out.println();
         for (i = 0; i < allexposure_item.length; i++) {
            System.out.print(counter[2][i]+" ");
        }
        System.out.println();
         */

        gMinSup = maxData;
        for (i = 0; i < maxClass; i++) {
            if (lMinSup[i] < gMinSup) {
                gMinSup = lMinSup[i];
            }

        }
    }


    public void initSecondWholeTree(PrefixTree tree, int[] allexposure_item, int nodeID, int[] secondexposure) {
        int i, j, k, num, item, f;
        PrefixTree cur;
        treeSize = 0;
        singleRule = 0;
        multiRule = 0;

        tree.numOfSon = 0;
        tree.father = null;
        tree.nodeID = -1;
        tree.len = 0;
        tree.gSup = maxData;
        tree.conf = 0;
        tree.acc = 0;

        tree.sonList = new PrefixTree[maxItem + 1];
        tree.memForSon = maxItem + 1;

        for (i = 0; i < maxItem + 1; i++) {
            tree.sonList[i] = null;
        }

        tree.numOfSon = 0;

        for (i = 1; i < maxItem + 1; i++) {
            // zx, original version, if (counter[maxClass][i] > gMinSup) {
            // zx, CR-PA, test if each individual predictive variable is frequent.

            //System.out.println("counter[maxClass][i] / maxData = " + (double)counter[maxClass][i] / (double)maxData);
            //if (((double)counter[maxClass][i] / (double)maxData) >= t) {

            //System.out.println("include item "+i+" as counter[maxClass]["+i+"]="+counter[maxClass][i]);
            tree.sonList[tree.numOfSon++] = newNode(tree, i);

            //System.out.println("number of Son:"+tree.numOfSon);
            //System.out.println("frequent:"+i);
            //}
            //else
            //{
            //tree.numOfSon++;
            //System.out.println(i);
            //}
        }
        //System.out.println("hushu1");
        for (j = 0, f = 0; j < allexposure_item.length; j++, f++) {
            if ((allexposure_item[j] == 1) && (j != nodeID)) {
                //System.out.println("j"+j);
                //System.out.println("hushu2");
                cur = tree.sonList[j - 1];
                //System.out.println("hushu3");
                //System.out.println("tree.sonList[j-1].nodeID"+cur.nodeID);
                ruleSecondTestWrite_CRPA(cur);
                //System.out.println("hushu4");
                if (cur.ispa != 1) {
                    secondexposure[f] = 1;
                }

            }

        }
        freeCount();

    }


    //hs.end
    // ArrayList<int[]> causalRules=new ArrayList<int[]>();
    public boolean causalRule_CRPA(PrefixTree node, int[] allexposure, int[] allexposure_item, PrefixTree tree) {

        double upvalue = 0, downvalue = 0, partialvalue;
        double Ma = 0, Mb = 0, Mc = 0, Md = 0;
        int i, j, k, m, f, g, count;
        int exposure = 0;    // zx, start from 1 not 0.
        int[][] reduced_matrix;
        //allSet = new PrefixTree();
        PrefixTree cur;
        PrefixTree allItem;
        allItem = new PrefixTree();
        cur = new PrefixTree();


        //System.out.println("realAtt="+realAtt);

        int[] rest = new int[realAtt];
        //int[] condition = new int[realAtt - 1];
        //int[] current = new int[realAtt - 1];
        int[] condition = new int[realAtt];
        int[] current = new int[realAtt];
        int[] secondexposure = new int[allexposure_item.length + 1];
        int[] secondtenexposure = new int[allexposure_item.length + 1];
        double[] secondValueexposure = new double[allexposure_item.length + 1];
        double[] secondrest = new double[allexposure_item.length + 1];
        double[] secondstoreValue = new double[10];

        int row, col;

        // aggregating equivalent class.
        reduced_matrix = equivalence();



        /*
        -----------reduced_matrix------------
        reduced_matrix.length = 6
        reduced_matrix[0].length = 10
        1 3 6 8  9 14 15 19 0 4
        1 3 6 8 10 14 15 19 0 1
        1 3 6 8 10 14 16 19 0 2
        1 3 6 8 11 14 16 19 0 1
        1 3 6 8 12 14 18 19 1 6
        2 4 6 8 12 14 18 19 1 1

        The last column is total count of the specified same rows.
        The last but one column is target variable.
        */

        //System.out.println("-----------reduced_matrix------------");
        //System.out.println("reduced_matrix.length = " + reduced_matrix.length);
        //System.out.println("reduced_matrix[0].length = " + reduced_matrix[0].length);


        row = reduced_matrix.length;
        col = reduced_matrix[0].length;

        /*
        System.out.println("after equivalence");
        for(i = 0; i < row; i++)
        {
            for(j = 0; j < col; j++)
                System.out.print(reduced_matrix[i][j] + " ");

            System.out.println();
        }
        */

        /*
        for(i = 0; i < reduced_matrix.length; i++)
        {
            for(j = 0; j < reduced_matrix[0].length; j++)
            {
                System.out.print(reduced_matrix[i][j] + " ");
            }

            System.out.println();
        }
        */


        // find exposure index in dataSpace matrix
        for (i = 0; i < realAtt; i++) {
            //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
            //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

            if (dataSpacestat[i].min <= node.nodeID && node.nodeID <= dataSpacestat[i].max) {
                // column index in dataSpace corresponding to node.nodeID in binary matrix.
                exposure = i;
                //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
                //System.out.println("dataSpacestat["+i+"].min="+dataSpacestat[i].min);
                //System.out.println("dataSpacestat["+i+"].max="+dataSpacestat[i].max);
            }

        }
        //hs.start

        initialCount();
        determineParameter(gsup, exposure, allexposure, allexposure_item, node.nodeID);
        //initWholeTree(allSet);
        //System.out.println("hushu");
        initSecondWholeTree(allItem, allexposure_item, node.nodeID, secondexposure);
        /*
        for(j = 0,f=0; j < allexposure_item.length;j++,f++){
            if((allexposure_item[j]==1)&&(j!=node.nodeID)){
                System.out.println("j"+j);
                cur =  allItem.sonList[j-1] ;
                System.out.println("tree.sonList[j-1].nodeID"+cur.nodeID);
                ruleTestWrite_CRPA(cur);
                if(cur.ispa!=1){
                    secondexposure[f]=1;
                }
            }

        }
        */
        /*
        System.out.println("exclusive");
        for(f=0;f<secondexposure.length;f++){
            if(secondexposure[f]==1){
                System.out.println("f"+f);

            }

        }
        */
        if (ChosenTest.length == 0) {
            for (f = 0, j = 0; f < secondexposure.length; f++) {
                if (secondexposure[f] == 1) {
                    secondValueexposure[f] = tree.sonList[f - 1].value;
                    secondrest[j] = tree.sonList[f - 1].value;
                    j++;

                }

            }
        } else {
            for (f = 0, j = 0; f < secondexposure.length; f++) {
                if (secondexposure[f] == 1) {
                    secondValueexposure[f] = tree.sonList1[f - 1].value;
                    secondrest[j] = tree.sonList1[f - 1].value;
                    j++;

                }

            }
        }
//        for(f=0;f<secondrest.length;f++){
//
//                System.out.print(secondrest[f]+" ");
//
//        }
//        System.out.println();
        Arrays.sort(secondrest);
//        System.out.println("new");
//        for(f=0;f<secondrest.length;f++){
//
//                System.out.print(secondrest[f]+" ");
//
//        }

        for (i = secondrest.length - 1, j = 0; (i != 0) && j < 10; i--) {

            secondstoreValue[j] = secondrest[i];
            j++;


        }
//        System.out.println();
//        System.out.println("secondstoreValue");
//         for(f=0;f<secondstoreValue.length;f++){
//
//                System.out.print(secondstoreValue[f]+" ");
//
//
//
//        }

        for (j = 0; j < 10; j++) {
            for (f = 0; f < secondValueexposure.length; f++) {
                if (secondstoreValue[j] != 0 && secondValueexposure[f] == secondstoreValue[j]) {
                    secondtenexposure[f] = 1;
                }
            }
        }
//        System.out.println();
//        for(f=0;f<secondtenexposure.length;f++){
//            if(secondtenexposure[f]==1){
//                System.out.println("f"+f);
//
//            }
//
//        }
//


        int[] secondAttexposure = new int[realAtt];
        //for(g=0;g<secondAttexposure.length;g++){
        for (f = 0; f < secondtenexposure.length; f++) {
            if (secondtenexposure[f] == 1) {
                for (i = 0, g = 0; i < realAtt; i++, g++) {
                    if (dataSpacestat[i].min <= f && f <= dataSpacestat[i].max) {
                        secondAttexposure[g] = 1;
                    }

                }

            }

        }

        //}

//        for(g=0;g<secondAttexposure.length;g++){
//            if(secondAttexposure[g]==1){
//
//                System.out.print("g"+g);
//
//            }
//
//        }

        //System.out.println();
        freeCount();
        //freeWholeTree();

        //System.out.println("hsend");
//        for(int f=0;f<allexposure.length;f++){
//            if((allexposure[f]==1)&&(f!=exposure)){
//
//
//            }
//
//        }


        //hs.end
        //System.out.println("exposureID="+exposure);
        if (node.isMultivalue == false) {
            if (ChosenControl.length == 0) {
                // find the rest indexes in dataSpace matrix
                for (i = 0, j = 0; i < realAtt && j < realAtt - 1; i++) {
                    if (exposure != i) {
                        rest[j] = i;
                        j++;
                    }
                }
            } else if (Controlmethod == 0) {
                for (i = 0, j = 0; i < realAtt && j < realAtt - 1; i++) {
                    for (int h = 0; h < ChosenControl.length; h++) {
                        if (exposure != i && i == (ChosenControl[h] - 1)) {
                            rest[j] = i;
                            j++;
                        }
                    }
                }
            } else {
                for (i = 0, j = 0; i < realAtt && j < realAtt - 1; i++) {
                    for (int h = 0; h < ChosenControl.length; h++) {
                        if (i == (ChosenControl[h] - 1)) {
                            rest[j] = i;
                            j++;
                        }
                    }
                }
            }
        } else {

            //hs,add
            if (ChosenControl.length == 0) {
                System.out.println("hushu1");
                for (i = 0, j = 0, m = 0, g = 0; i < realAtt && j < allexposure.length - 1 && m < allexposure.length && g < secondAttexposure.length; i++) {

                    if ((exposure != i) && (allexposure[m] == 1) && m == i && secondAttexposure[g] == 1 && g == i) {   //hs.add  for sort without the items from the same attribute
                        rest[j] = i;
                        //System.out.print(rest[j]+" ");
                        m++;
                        g++;
                        j++;
                        //System.out.print(rest[j]+" ");
                    } else {
                        m++;
                        g++;
                    }


                }
            } else if (Controlmethod == 0) {
                System.out.println("hushu2");
                for (i = 0, j = 0, m = 0, g = 0; i < realAtt && j < allexposure.length - 1 && m < allexposure.length && g < secondAttexposure.length; i++) {
                    if ((exposure != i) && (allexposure[m] == 1) && m == i && secondAttexposure[g] == 1 && g == i) {   //hs.add  for sort without the items from the same attribute
                        for (int h = 0; h < ChosenControl.length; h++) {
                            if (i == (ChosenControl[h] - 1)) {
                                rest[j] = i;
                                //System.out.print(rest[j]+" ");
                                m++;
                                g++;
                                j++;
                                //System.out.print(rest[j]+" ");
                            }
                        }
                    } else {
                        m++;
                        g++;
                    }


                }
            } else {
                System.out.println("hushu3");
                //for(i = 0,j = 0,m=0,g=0; i < realAtt && j < allexposure.length-1&&m < allexposure.length&&g<secondAttexposure.length; i++)
                for (i = 0, j = 0, m = 0, g = 0; i < realAtt; i++) {
                    for (int h = 0; h < ChosenControl.length; h++) {
                        if (i == (ChosenControl[h] - 1)) {   //hs.add  for sort without the items from the same attribute
                            rest[j] = i;
                            //System.out.print(rest[j]+" ");
                            m++;
                            g++;
                            j++;
                            //System.out.print(rest[j]+" ");
                        } else {
                            m++;
                            g++;
                        }
                    }


                }

            }
        }
        /*
        System.out.println("allexposure");
        for(m = 0;m < allexposure.length; m++){
            if(allexposure[m]==1){
            System.out.print(m);
            }
        }

        System.out.println();
        */

        System.out.println("rest");
        for (i = 0; i < realAtt; i++)
            System.out.print(rest[i] + " ");

        System.out.println();



        /*
         System.out.println("before reduced_matrix");
        for(i = 0; i < row; i++)
        {
            for(j = 0; j < col; j++)
                System.out.print(reduced_matrix[i][j] + " ");

            System.out.println();
        }
        */


        // sort reduced matrix according to columns containing in rest.
        sort(reduced_matrix, rest);

        // find condition, because of the exclusiveness of value
        for (i = 0, j = 0, m = 0, g = 0; i < realAtt && j < realAtt && m < realAtt && g < secondAttexposure.length; i++)
        //for(i = 0, j = 0; i < realAtt && j < realAtt - 1; i++)
        {
            if (exposure != i && allexposure[m] == 1 && m == i && secondAttexposure[g] == 1 && g == i) {
                condition[j] = reduced_matrix[0][i];
                m++;
                g++;
                j++;
            } else {
                m++;
                g++;
            }
            //else
            //{
            //    condition[j] = node.nodeID;
            //    j++;
            //}
        }
        /*
        System.out.println("after reduced_matrix");
        for(i = 0; i < row; i++)
        {
            for(j = 0; j < col; j++)
                System.out.print(reduced_matrix[i][j] + " ");

            System.out.println();
        }
        */

        //System.out.println("exposure = " + exposure);
        /*
        System.out.println("condition");

        for(i = 0; i < condition.length; i++)
            System.out.print(condition[i] + " ");

        System.out.println();
        */

        //    -    +
        //  - Ma   Mb
        //  + Mc   Md

        Ma = 0;
        Mb = 0;
        Mc = 0;
        Md = 0;
        upvalue = 0;
        downvalue = 0;


        k = 0;
        //hs.test System.out.println("row="+row); row=5000.
        //System.out.println("row="+row);
        while (k < (row + 1)) {
            /*
            System.out.println((k+1)+"lines, "+"before current vector");

            for(i = 0; i < realAtt; i++)
            //for(i = 0; i < realAtt - 1; i++)
                System.out.print(current[i] + " ");

            System.out.println();
            */


            for (i = 0, j = 0, m = 0, g = 0; i < realAtt && j < realAtt && m < realAtt && g < secondAttexposure.length; i++) {
                if (k != row) {
                    if (exposure != i && allexposure[m] == 1 && m == i && secondAttexposure[g] == 1 && g == i) {
                        current[j] = reduced_matrix[k][i];
                        m++;
                        g++;
                        //System.out.println(current[i]);
                        j++;
                    } else {
                        m++;
                        g++;
                    }


                } else {
                    current[j] = 0;
                    j++;
                }

            }


            /*
            for(i=0,j=0;i<realAtt&&j<realAtt;i++){
                if(exposure!=i&&k != row){
                    current[j]=reduced_matrix[k][i];
                    j++;
                }


            }
            */




            /*
            System.out.println((k+1)+"lines, "+"after current vector");

            for(i = 0; i < realAtt; i++)
                for(i = 0; i < realAtt ; i++)
                System.out.print(current[i] + " ");

            System.out.println();
            */


            //    -    +
            //  - Ma   Mb
            //  + Mc   Md
            boolean equal = Arrays.equals(current, condition);
            if (equal == true && k != row) {
                //System.out.println("-----------true-----------");
                // measure table for variables
                //System.out.println("reduced_matrix[k][col - 2]="+reduced_matrix[k][col - 2]);
                if (reduced_matrix[k][col - 2] == 1) {
                    // the value of target variable is one
                    if (reduced_matrix[k][exposure] == node.nodeID) {
                        Md = Md + reduced_matrix[k][col - 1];        // 1-1
                        //System.out.println("Md = "+Md);
                    } else {
                        //System.out.println("-----------2-----------");
                        Mb = Mb + reduced_matrix[k][col - 1];        // 0-1
                        //System.out.println("Mb = "+Mb);
                    }
                } else {
                    // the value of target variable is zero
                    if (reduced_matrix[k][exposure] == node.nodeID) {
                        //System.out.println("-----------3-----------");
                        Mc = Mc + reduced_matrix[k][col - 1];        // 1-0
                        //System.out.println("Mc = "+Mc);
                    } else {
                        //System.out.println("-----------4-----------");
                        Ma = Ma + reduced_matrix[k][col - 1];        // 0-0
                        //System.out.println("Ma = "+Ma);
                    }

                }

                //hs.add
                //hs.end
                //System.out.println("hushu"+"Ma="+Ma+", Mb="+Mb+", Mc="+Mc+", Md="+Md);

            } else {
                // operate
                //if(Ma==0)
                //Ma = 0.1;
                //if(Mb==0)
                // Mb = 0.1;
                //if(Mc==0)
                // Mc = 0.1;
                //if(Md==0)
                //Md = 0.1;

                //[Ma Mb Mc Md]

                //    -    +
                //  - Ma   Mb
                //  + Mc   Md

                //System.out.println("Ma="+Ma+", Mb="+Mb+", Mc="+Mc+", Md="+Md);

                if ((Ma + Mb) * (Ma + Mc) * (Mc + Md) * (Mb + Md) != 0) {
                    if ((Md + Mc) >= 1) {
                        //System.out.println("Ma="+Ma+", Mb="+Mb+", Mc="+Mc+", Md="+Md);
                        //System.out.println("Ma*Md="+Ma*Md);
                        //System.out.println("Mc*Mb="+Mc*Mb);
                        //System.out.println("Ma+Mb+Mc+Md="+(Ma+Mb+Mc+Md));
                        //System.out.println("(Ma*Md-Mc*Mb)/(Ma+Mb+Mc+Md)="+((Ma*Md-Mc*Mb)/(Ma+Mb+Mc+Md)));
                        upvalue = upvalue + (Ma * Md - Mc * Mb) / (Ma + Mb + Mc + Md);
                        //System.out.println("upvalue="+upvalue);
                        downvalue = downvalue + (Ma + Mb) * (Ma + Mc) * (Mc + Md) * (Mb + Md) / Math.pow((Ma + Mb + Mc + Md), 2) / ((Ma + Mb + Mc + Md) - 1);
                    }
                    //Ma = 0; Mb = 0; Mc = 0; Md = 0;
                }

                Ma = 0;
                Mb = 0;
                Mc = 0;
                Md = 0;

                if (k != row) {
                    for (i = 0, j = 0, m = 0, g = 0; i < realAtt && j < realAtt && m < realAtt && g < secondAttexposure.length; i++) {
                        if (exposure != i && allexposure[m] == 1 && m == i && secondAttexposure[g] == 1 && g == i) {
                            condition[j] = reduced_matrix[k][i];
                            m++;
                            g++;
                            j++;
                        } else {
                            m++;
                            g++;
                        }

                    }
                } else {
                    break;
                }

                /*
                System.out.println("new condition");
                for(i = 0; i < realAtt; i++)
                    System.out.print(condition[i] + " ");
                System.out.println();
                */

                k = k - 1;
            }
            k = k + 1;
        }


        //System.out.println("upvalue = "+upvalue+", downvalue = "+downvalue);


        if (downvalue != 0) {
            partialvalue = Math.pow((Math.abs(upvalue) - 0.5), 2) / downvalue;
        } else
            partialvalue = 0;
        //System.out.println("upvalue="+upvalue);
        //System.out.println("downvalue="+downvalue);

        //System.out.println("partialvalue = " + partialvalue);

        if (partialvalue > PaValue) {
            //System.out.println("3.84");
            //System.out.println("PaValue="+PaValue);
            return true;
        } else {
            return false;
        }


    }

    //hs.start

    public void determineParameter(double suprate, int[] curr, int[] allexposure, int[] allexposure_item, int[] Seconditem) {
        int i, j, k, targetvalue, item;
        int[] item_targetvalue = new int[Seconditem.length];
        maxTarget = 1;
        initSecondParameter();
        //System.out.println("hushuhaha");


        for (i = 0; i < maxData; i++) {
            for (j = 0; j < Seconditem.length; j++) {
                item_targetvalue[j] = dataSpace[i][curr[j]];
                //System.out.println(item_targetvalue[j]);

                //System.out.println(Seconditem[j]);
            }

            if ((Arrays.equals(Seconditem, item_targetvalue)) == true) {
                targetvalue = 1;
                counter[targetvalue][0]++;
            } else {
                targetvalue = 0;
                counter[targetvalue][0]++;
            }

            for (j = 0; j < allexposure.length; j++) {
                if (allexposure[j] == 1) {
                    item = dataSpace[i][j];
                    if (item == 0) {
                        continue;
                    }
                    for (k = 0; k < allexposure_item.length; k++) {
                        if (allexposure_item[k] == 1) {
                            if (k == item) {
                                counter[targetvalue][item]++;
                                counter[maxClass][item]++;
                                //System.out.println("hushuj_item "+targetvalue+" "+counter[targetvalue][item]);
                            }

                        }
                    }
                    //counter[targetvalue][item]++;

                    //counter[maxClass][item]++;
                }
            }


        }
        for (i = 0; i < maxClass; i++) {
            lMinSup[i] = (int) (counter[i][0] * suprate + 0.5);
            seconddist[i] = counter[i][0];
            //System.out.println("dist["+i+"]:"+seconddist[i]);
        }

        /*
        for (i = 0; i < allexposure_item.length; i++) {
            System.out.print(counter[0][i]+" ");
        }
        System.out.println();
        for (i = 0; i < allexposure_item.length; i++) {
            System.out.print(counter[1][i]+" ");
        }
        System.out.println();
        for (i = 0; i < allexposure_item.length; i++) {
            System.out.print(counter[2][i]+" ");
        }
        System.out.println();
        */

        gMinSup = maxData;
        for (i = 0; i < maxClass; i++) {
            if (lMinSup[i] < gMinSup) {
                gMinSup = lMinSup[i];
            }

        }


    }

    public void initSecondWholeTree(PrefixTree tree, int[] allexposure_item, int[] secondexposure) {
        int i, j, k, num, item, f;
        PrefixTree cur;
        treeSize = 0;
        singleRule = 0;
        multiRule = 0;

        tree.numOfSon = 0;
        tree.father = null;
        tree.nodeID = -1;
        tree.len = 0;
        tree.gSup = maxData;
        tree.conf = 0;
        tree.acc = 0;

        tree.sonList = new PrefixTree[maxItem + 1];
        tree.memForSon = maxItem + 1;

        for (i = 0; i < maxItem + 1; i++) {
            tree.sonList[i] = null;
        }

        tree.numOfSon = 0;

        for (i = 1; i < maxItem + 1; i++) {
            // zx, original version, if (counter[maxClass][i] > gMinSup) {
            // zx, CR-PA, test if each individual predictive variable is frequent.

            //System.out.println("counter[maxClass][i] / maxData = " + (double)counter[maxClass][i] / (double)maxData);
            //if (((double)counter[maxClass][i] / (double)maxData) >= t) {

            //System.out.println("include item "+i+" as counter[maxClass]["+i+"]="+counter[maxClass][i]);
            tree.sonList[tree.numOfSon++] = newNode(tree, i);

            //System.out.println("number of Son:"+tree.numOfSon);
            //System.out.println("frequent:"+i);
            //}
            //else
            //{
            //tree.numOfSon++;
            //System.out.println(i);
            //}
        }
        //System.out.println("hushu1");
        for (j = 0, f = 0; j < allexposure_item.length; j++, f++) {
            if (allexposure_item[j] == 1) {
                //System.out.println("j"+j);
                //System.out.println("hushu2");
                cur = tree.sonList[j - 1];
                //System.out.println("hushu3");
                //System.out.println("tree.sonList[j-1].nodeID"+cur.nodeID);
                ruleSecondTestWrite_CRPA(cur);
                //System.out.println("hushu4");
                if (cur.ispa != 1) {
                    secondexposure[f] = 1;
                }

            }

        }
        freeCount();

    }


    //hs.end


    public boolean causalRule_CRPA(int[] item, int target, int[] allexposure, int[] allexposure_item, PrefixTree alltree) {
        //System.out.println("------------causalRule_CRPA more than one level -----------");


        //System.out.println("item[0]" + item[0] + "item[1]" + item[1]);


        double upvalue = 0, downvalue = 0, partialvalue;
        double Ma = 0, Mb = 0, Mc = 0, Md = 0;
        int i, j, k, f, m, g, h = 0, s = 0, count;
        int exposure = 0;
        int[][] reduced_matrix;
        int[] rest = new int[realAtt];
        //int[] condition = new int[realAtt - 1];
        int[] condition = new int[realAtt];
        //int[] start = new int[realAtt - 1];
        int[] start = new int[realAtt];
        int row, col;

        int[] secondexposure = new int[allexposure_item.length + 1];
        int[] secondtenexposure = new int[allexposure_item.length + 1];
        double[] secondValueexposure = new double[allexposure_item.length + 1];
        double[] secondrest = new double[allexposure_item.length + 1];
        double[] secondstoreValue = new double[10];

        PrefixTree allItem;
        allItem = new PrefixTree();


        /*
        System.out.println("item.length = "+item.length);
        for(i = 0; i < item.length; i++)
        {
            if(item[i] != 0)
            {
                System.out.print(item[i] + " ");
            }
        }
        */


        // aggregating equivalent class.
        reduced_matrix = equivalence();

        /*
        -----------reduced_matrix------------
        reduced_matrix.length = 6
        reduced_matrix[0].length = 10
        1 3 6 8  9 14 15 19 0 4
        1 3 6 8 10 14 15 19 0 1
        1 3 6 8 10 14 16 19 0 2
        1 3 6 8 11 14 16 19 0 1
        1 3 6 8 12 14 18 19 1 6
        2 4 6 8 12 14 18 19 1 1

        The last column is total count of the specified same rows.
        The last but one column is target variable.
        */

        //System.out.println("-----------reduced_matrix------------");

        //System.out.println("reduced_matrix.length = " + reduced_matrix.length);
        //System.out.println("reduced_matrix[0].length = " + reduced_matrix[0].length);


        row = reduced_matrix.length;
        col = reduced_matrix[0].length;

        /*
        for(i = 0; i < reduced_matrix.length; i++)
        {
            for(j = 0; j < reduced_matrix[0].length; j++)
            {
                System.out.print(reduced_matrix[i][j] + " ");
            }

            System.out.println();
        }
        */

        int[] curr = new int[item.length + 1];
        boolean[] curr_state = new boolean[item.length];

        for (f = 0; f < item.length; f++) {
            // find exposure index in dataSpace matrix
            for (i = 0; i < realAtt; i++) {
                if (dataSpacestat[i].min <= item[f] && item[f] <= dataSpacestat[i].max) {
                    // column index in dataSpace corresponding to node.nodeID in binary matrix.
                    //System.out.println("item["+f+"]"+item[f]);
                    curr[f] = i;
                    //System.out.println("curr["+f+"]"+curr[f]);
                    if (dataSpacestat[i].max - dataSpacestat[i].min != 0) {
                        // It is multi value
                        curr_state[f] = true;
                    }
                }
            }

        }

        //hs.start
        initialCount();
        determineParameter(gsup, curr, allexposure, allexposure_item, item);
        //System.out.println("hushu");
        initSecondWholeTree(allItem, allexposure_item, secondexposure);

//        for(f=0;f<secondexposure.length;f++){
//            if(secondexposure[f]==1){
//                System.out.println("f"+f);
//
//            }
//
//        }
        if (ChosenTest.length == 0) {
            for (f = 0, j = 0; f < secondexposure.length; f++) {
                if (secondexposure[f] == 1) {
                    secondValueexposure[f] = alltree.sonList[f - 1].value;
                    secondrest[j] = alltree.sonList[f - 1].value;
                    j++;

                }

            }
        } else {
            for (f = 0, j = 0; f < secondexposure.length; f++) {
                if (secondexposure[f] == 1) {
                    secondValueexposure[f] = alltree.sonList1[f - 1].value;
                    secondrest[j] = alltree.sonList1[f - 1].value;
                    j++;

                }

            }

        }

//        for(f=0;f<secondrest.length;f++){
//
//                System.out.print(secondrest[f]+" ");
//
//        }
//        System.out.println();
        Arrays.sort(secondrest);
        //System.out.println("new");
//        for(f=0;f<secondrest.length;f++){
//
//                System.out.print(secondrest[f]+" ");
//
//        }

        for (i = secondrest.length - 1, j = 0; (i != 0) && j < 10; i--) {
            secondstoreValue[j] = secondrest[i];
            j++;


        }
//        System.out.println();
//        System.out.println("secondstoreValue");
//         for(f=0;f<secondstoreValue.length;f++){
//
//                System.out.print(secondstoreValue[f]+" ");
//
//
//
//        }

        for (j = 0; j < 10; j++) {
            for (f = 0; f < secondValueexposure.length; f++) {
                if (secondstoreValue[j] != 0 && secondValueexposure[f] == secondstoreValue[j]) {
                    secondtenexposure[f] = 1;
                }
            }
        }
//        System.out.println();
//        for(f=0;f<secondtenexposure.length;f++){
//            if(secondtenexposure[f]==1){
//                System.out.println("f"+f);
//
//            }
//
//        }


        int[] secondAttexposure = new int[realAtt];
        //for(g=0;g<secondAttexposure.length;g++){
        for (f = 0; f < secondtenexposure.length; f++) {
            if (secondtenexposure[f] == 1) {
                for (i = 0, g = 0; i < realAtt; i++, g++) {
                    if (dataSpacestat[i].min <= f && f <= dataSpacestat[i].max) {
                        secondAttexposure[g] = 1;
                        //System.out.print("g"+g);
                    }

                }

            }

        }

        //}
//        System.out.println();
//        for(g=0;g<secondAttexposure.length;g++){
//            if(secondAttexposure[g]==1){
//
//                System.out.print("g"+g);
//
//            }
//
//        }


        freeCount();


        //hs.end

        /*
        f = 0;

        for(i = 0, j = 0; i < realAtt && j < realAtt; i++)
        {
            // item is an ascending array.
            if(f < item.length)
            {

                if(i != curr[f])
                {
                    rest[j] = i;
                    j++;
                }
                else if(i == curr[f] && curr_state[f] == false)
                {
                    // it is single value
                    //i++;
                    f++;
                }
                else if(i == curr[f] && curr_state[f] == true)
                {
                    rest[j] = i;
                    j++;
                    f++; // Judge the next item value.
                }
            }
        }
        */
        /*
        System.out.println("rest");
        for(i = 0; i < realAtt; i++)
            System.out.print(rest[i]+" ");

        System.out.println();
        */


        // zx, multivalue
        /*
        for(f = 0; f < item.length; f++){
            //System.out.println("curr["+f+"]"+curr[f]);

        }
        for(i = 0; i < realAtt; i++)
        {
            rest[i] = i;
        }
        */


        // hs,add
        if (ChosenControl.length == 0) {
            System.out.println("hushu1");
            for (i = 0, j = 0, f = 0, m = 0, g = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1 && m < allexposure.length && g < secondAttexposure.length; i++) {
                if (curr[f] != i && (allexposure[m] == 1) && m == i && secondAttexposure[g] == 1 && g == i) {   //hs.add  for sort without the items from the same attribute

                    rest[j] = i;

                    j++;
                    m++;
                    g++;

                } else if (curr[f] == i) {
                    f++;
                    m++;
                    g++;
                } else {
                    m++;
                    g++;
                }

            }
        } else if (Controlmethod == 0) {
            System.out.println("hushu2");
            for (i = 0, j = 0, f = 0, m = 0, g = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1 && m < allexposure.length && g < secondAttexposure.length; i++) {
                if (curr[f] != i && (allexposure[m] == 1) && m == i && secondAttexposure[g] == 1 && g == i) {   //hs.add  for sort without the items from the same attribute
                    for (int l = 0; l < ChosenControl.length; l++) {
                        if (i == (ChosenControl[l] - 1)) {
                            rest[j] = i;

                            j++;
                            m++;
                            g++;
                        }
                    }


                } else if (curr[f] == i) {
                    f++;
                    m++;
                    g++;
                } else {
                    m++;
                    g++;
                }

            }

        } else {
            System.out.println("hushu3");
            for (i = 0, j = 0, f = 0, m = 0, g = 0; i < realAtt; i++) {
                for (int l = 0; l < ChosenControl.length; l++) {
                    //if(curr[f] != i&&(allexposure[m]==1) && m==i && secondAttexposure[g]==1&&g==i ){   //hs.add  for sort without the items from the same attribute
                    if (curr[f] != i && i == (ChosenControl[l] - 1)) {
                        rest[j] = i;

                        j++;
                        m++;
                        g++;

                    } else if (curr[f] == i) {
                        f++;
                        m++;
                        g++;
                    } else {
                        m++;
                        g++;
                    }
                }

            }
        }
//        for(i = 0,j = 0;i < realAtt&& j < realAtt - item.length;i++){
//
//                for(f = 0;f < item.length;f++){
//                     if(curr[f] != i ){
//
//                         h=1;
//
//                     }
//                     else{
//                         h=0;
//                     }
//
//                }
//                for(m=0;m < allexposure.length;){
//                    if((allexposure[m]==1) && m==i){
//                       s=1;
//                       m++;
//
//                    }
//                    else{
//                        s=0;
//                        m++;
//                    }
//
//                }
//                if(h==1&&s==1){
//                     rest[j] = i;
//
//                    j++;
//                }
//
//
//        }
        //hs.end
        /*
        System.out.println("allexposure");
        for(m = 0;m < allexposure.length; m++){
            if(allexposure[m]==1){
            System.out.print(m);
            }
        }

        System.out.println();
        */
        System.out.println("rest");
        for (i = 0; i < realAtt; i++)
            System.out.print(rest[i] + " ");

        System.out.println();


        // sort reduced matrix according to columns containing in rest.
        sort(reduced_matrix, rest);

        // find condition
        //for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
        /*
        for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length; i++)
        {
            if(curr[f] != i)
            {
                condition[j] = reduced_matrix[0][i];
                j++;
            }
            else
            {
                f++;
            }
        }

        */

        /*
        for(i = 0; i < realAtt; i++)
        {

            condition[i] = reduced_matrix[0][i];
        }
        */
        //hs.add
        for (i = 0, j = 0, f = 0, m = 0, g = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1 && m < allexposure.length && g < secondAttexposure.length; i++) {
            if (curr[f] != i && (allexposure[m] == 1) && m == i && secondAttexposure[g] == 1 && g == i) {
                condition[j] = reduced_matrix[0][i];

                j++;
                m++;
                g++;
            } else if (curr[f] == i) {
                f++;
                m++;
                g++;

            } else {
                m++;
                g++;
            }


        }
        //hs.end
        /*

        System.out.println("condition");

        for(i = 0; i < condition.length; i++)
            System.out.print(condition[i] + " ");

        System.out.println();
        */

        //    -    +
        //  - Ma   Mb
        //  + Mc   Md

        Ma = 0;
        Mb = 0;
        Mc = 0;
        Md = 0;
        upvalue = 0;
        downvalue = 0;


        //k = 0;
        //while(k < row)

        k = 0;
        while (k < (row + 1)) {
            /*
            for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
            {
                if(curr[f] != i)
                {
                    start[j] = reduced_matrix[k][i];
                    j++;
                }
                else
                {
                    f++;
                }
            }
            */

            /*
            //for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
            for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length; i++)
            {
                if(k != row)
                {
                    if(curr[f] != i)
                    {
                        start[j] = reduced_matrix[k][i];
                        j++;
                    }
                    else
                    {
                        f++;
                    }
                }
                else
                {
                     start[j] = 0;
                     j++;
                }
            }

            */

            /*
            for(i = 0; i < realAtt; i++)
            {
                if(k != row)
                {
                    start[i] = reduced_matrix[k][i];
                }
                else
                {
                    start[i] = 0;
                }
            }
            */
            //hs.add
            for (i = 0, j = 0, f = 0, m = 0, g = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1 && m < allexposure.length && g < secondAttexposure.length; i++) {
                if (k != row) {
                    if (curr[f] != i && (allexposure[m] == 1) && m == i && secondAttexposure[g] == 1 && g == i) {
                        start[j] = reduced_matrix[k][i];
                        j++;
                        m++;
                        g++;
                    } else if (curr[f] == i) {
                        f++;
                        m++;
                        g++;
                    } else {
                        m++;
                        g++;
                    }
                } else {
                    start[j] = 0;
                    j++;
                }
            }
            //hs.end


            boolean equal = Arrays.equals(start, condition);

            //for(i = 0; i < start.length; i++)
            //{
            //    System.out.print(start[i]+" ");
            //    System.out.print(condition[i]+" ");

            //}

            //System.out.println("equal="+equal);

            if (equal == true && k != row) {
                // measure table for variables

                if (reduced_matrix[k][col - 2] == 1) {
                    // the value of target variable is one
                    int sum1 = 0, sum2 = 0;
                    for (f = 0; f < item.length; f++) {
                        sum1 += item[f];
                        sum2 += reduced_matrix[k][curr[f]];
                    }


                    if (sum1 == sum2 && reduced_matrix[k][curr[0]] == item[0]) {
                        Md = Md + reduced_matrix[k][col - 1];        // 1-1
                    } else {
                        Mb = Mb + reduced_matrix[k][col - 1];        // 0-1
                    }
                } else {
                    int sum1 = 0, sum2 = 0;
                    for (f = 0; f < item.length; f++) {
                        sum1 += item[f];
                        sum2 += reduced_matrix[k][curr[f]];
                    }

                    // the value of target variable is zero

                    if (sum1 == sum2 && reduced_matrix[k][curr[0]] == item[0]) {
                        Mc = Mc + reduced_matrix[k][col - 1];        // 1-0
                    } else {
                        Ma = Ma + reduced_matrix[k][col - 1];        // 0-0
                    }

                }
            } else {
                // operate
                //if(Ma==0)
                //Ma = 0.1;
                //if(Mb==0)
                //Mb = 0.1;
                //if(Mc==0)
                // Mc = 0.1;
                //if(Md==0)
                //Md = 0.1;

                //[Ma Mb Mc Md]

                //    -    +
                //  - Ma   Mb
                //  + Mc   Md

                if ((Ma + Mb) * (Ma + Mc) * (Mc + Md) * (Mb + Md) != 0) {
                    if ((Md + Mc) >= 1) {
                        upvalue = upvalue + (Ma * Md - Mc * Mb) / (Ma + Mb + Mc + Md);
                        downvalue = downvalue + (Ma + Mb) * (Ma + Mc) * (Mc + Md) * (Mb + Md) / Math.pow((Ma + Mb + Mc + Md), 2) / ((Ma + Mb + Mc + Md) - 1);
                    }
                }

                Ma = 0;
                Mb = 0;
                Mc = 0;
                Md = 0;

                /*
                for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
                {
                    if(curr[f] != i)
                    {
                        condition[j] = reduced_matrix[k][i];
                        j++;
                    }
                    else
                    {
                        f++;
                    }
                }

                */


                /*
                //for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
                for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length; i++)
                {
                    if(k != row)
                    {
                        if(curr[f] != i)
                        {
                            condition[j] = reduced_matrix[k][i];
                            j++;
                        }
                        else
                        {
                            f++;
                        }

                    }
                    else
                    {
                        break;
                    }
                }

                */

                /*
                if(k != row)
                {
                    for(i = 0; i < realAtt; i++)
                    {
                        condition[i] = reduced_matrix[k][i];
                    }
                }
                else
                {
                    break;
                }
                */
                //hs.add
                if (k != row) {
                    for (i = 0, j = 0, f = 0, m = 0, g = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1 && m < allexposure.length && g < secondAttexposure.length; i++) {
                        if (curr[f] != i && (allexposure[m] == 1) && m == i && secondAttexposure[g] == 1 && g == i) {
                            condition[j] = reduced_matrix[k][i];
                            j++;
                            m++;
                            g++;
                        } else if (curr[f] == i) {
                            f++;
                            m++;
                            g++;
                        } else {
                            m++;
                            g++;
                        }
                    }
                } else {
                    break;
                }
                //hs.end


                k = k - 1;
            }

            k = k + 1;
        }

        //System.out.println("item.length = "+item.length);
        for (i = 0; i < item.length; i++) {
            if (item[i] != 0) {
                //System.out.print(item[i] + " ");
            }
        }
        //System.out.println();

        //System.out.println("upvalue = "+upvalue+", downvalue = "+downvalue);

        if (downvalue != 0)
            partialvalue = Math.pow((Math.abs(upvalue) - 0.5), 2) / downvalue;
        else
            partialvalue = 0;

        //System.out.println("partialvalue = " + partialvalue);

        if (partialvalue > PaValue) {
            //System.out.println("3.84");
            //System.out.println("partialvalue = " + partialvalue);
            return true;
        } else {
            return false;
        }


        //return true;

    }


    ArrayList<RuleStru> singleCausalRules = new ArrayList<RuleStru>();
    ArrayList<RuleStru> secondlevelCausalRules = new ArrayList<RuleStru>();

    public void causalTest_CRPA(PrefixTree tree, int[] allexposure, int[] allexposure_item) {
        for (int i = 0, g = 1; i < tree.numOfSon; i++, g++) {
            if (tree.sonList[i].ispa == 1) {
                allexposure_item[g] = 1;
                for (int j = 0, f = 0; j < realAtt; j++, f++) {
                    if (dataSpacestat[j].min <= tree.sonList[i].nodeID && tree.sonList[i].nodeID <= dataSpacestat[j].max) {
                        //count++;
                        //if(count<=10){
                        allexposure[f] = 1;
                        //System.out.println(f);
                        //}

                    }
                }
            }
        }


        for (int f = 0; f < allexposure.length; f++) {
            if (allexposure[f] == 1) {
                System.out.print(f + " ");
            }

        }

        System.out.println();
        for (int g = 0; g < allexposure_item.length; g++) {
            if (allexposure_item[g] == 1) {
                System.out.print(g + " ");
            }

        }

        System.out.println();


    }

    //hs.start
    public void causalTest_CRPA_Two(PrefixTree tree, int[] allexposure, int[] allexposure_item) {
        /*
        for( int i = 0,g = 1; i < tree.numOfSon; i++,g++){
            if(tree.sonList[i].ispa == 1){
                allexposure_item[g] = 1;
                for( int j = 0,f = 0;j < realAtt; j++,f++){
                    if(dataSpacestat[j].min <= tree.sonList[i].nodeID && tree.sonList[i].nodeID <= dataSpacestat[j].max){
                        //count++;
                        //if(count<=10){
                            allexposure[f] = 1;
                            //System.out.println(f);
                        //}

                    }
                }
            }
        }
        */

        for (int m = 0; m < ChosenTest.length; m++) {
            int n = dataSpacestat[ChosenTest[m] - 1].min - 1;
            //for( int i = 0,g=1; i < tree.numOfSon1; i++,g++){
            for (int i = dataSpacestat[ChosenTest[m] - 1].min; i <= dataSpacestat[ChosenTest[m] - 1].max; i++) {
                System.out.println("min=" + dataSpacestat[ChosenTest[m] - 1].min);
                System.out.println("max=" + dataSpacestat[ChosenTest[m] - 1].max);
                System.out.println("the pa of i" + tree.sonList1[i - 1].ispa);
                if (tree.sonList1[i - 1].ispa == 1) {
                    System.out.println("tree.sonList1[i-1].nodeID=" + tree.sonList1[i - 1].nodeID);
                    System.out.println("woshi=" + (i - 1));
                    //allexposure_item[n] = 1;
                    allexposure_item[tree.sonList1[i - 1].nodeID] = 1;

                    for (int j = 0, f = 0; j < realAtt; j++, f++) {
                        if (dataSpacestat[j].min <= tree.sonList1[i - 1].nodeID && tree.sonList1[i - 1].nodeID <= dataSpacestat[j].max) {
                            //count++;
                            //if(count<=10){
                            allexposure[f] = 1;
                            //System.out.println(f);
                            //}
                        }
                    }
                }
                if (tree.sonList1[i - 1].issupport == 1) {
                    //n++;
                }

            }
        }


        for (int f = 0; f < allexposure.length; f++) {
            if (allexposure[f] == 1) {
                System.out.print(f + " ");
            }

        }

        System.out.println();

        for (int g = 0; g < allexposure_item.length; g++) {
            if (allexposure_item[g] == 1) {
                System.out.print(g + " ");
            }

        }

        System.out.println();


    }
    //hs.end


    //hs.start
    public void causalTest_CRPA_One(PrefixTree tree) {
        System.out.println("number of son: " + tree.numOfSon);

        //System.out.println("enter single causalTest_CRPA");
        //hs.add
        int[] allexposure = new int[realAtt];
        int[] allexposure_item = new int[tree.numOfSon1 + 1];
        //int n=dataSpacestat[ChosenTest[0]-1].min;
        //ArrayList allexposure=new ArrayList();
        //int[] allexcept = new int[realAtt];
        //int[] allexposure;
        for (int m = 0; m < ChosenTest.length; m++) {
            int n = dataSpacestat[ChosenTest[m] - 1].min - 1;
            //for( int i = 0,g=1; i < tree.numOfSon1; i++,g++){
            for (int i = dataSpacestat[ChosenTest[m] - 1].min; i <= dataSpacestat[ChosenTest[m] - 1].max; i++) {
                System.out.println("min=" + dataSpacestat[ChosenTest[m] - 1].min);
                System.out.println("max=" + dataSpacestat[ChosenTest[m] - 1].max);
                System.out.println("the pa of i" + tree.sonList1[i - 1].ispa);
                if (tree.sonList1[i - 1].ispa == 1) {
                    System.out.println("tree.sonList1[i-1].nodeID=" + tree.sonList1[i - 1].nodeID);
                    System.out.println("woshi=" + (i - 1));
                    //allexposure_item[n] = 1;
                    allexposure_item[tree.sonList1[i - 1].nodeID] = 1;
                    for (int j = 0, f = 0; j < realAtt; j++, f++) {
                        if (dataSpacestat[j].min <= tree.sonList1[i - 1].nodeID && tree.sonList1[i - 1].nodeID <= dataSpacestat[j].max) {
                            //count++;
                            //if(count<=10){
                            allexposure[f] = 1;
                            //System.out.println(f);
                            //}
                        }
                    }
                }
                if (tree.sonList1[i - 1].issupport == 1) {
                    //n++;
                }

            }
        }
        //System.out.println("allexposureattribute");
        for (int f = 0; f < allexposure.length; f++) {
            if (allexposure[f] == 1) {
                System.out.print(f);
            }

        }
        System.out.println();
        for (int g = 0; g < allexposure_item.length; g++) {
            if (allexposure_item[g] == 1) {
                System.out.print(g);
            }

        }
        for (int m = 0; m < ChosenTest.length; m++) {
            for (int i = dataSpacestat[ChosenTest[m] - 1].min; i <= dataSpacestat[ChosenTest[m] - 1].max; i++) {
                // zx, there is probably none or only one target. Hence, for loop is redundant.
                for (int x = 0; x < MAXTARGET; x++) {
                    // (tree.sonList[i].ispa == 1) represents rule is positive association.
                    // causal testing is only conducted on positive associations.
                    if (tree.sonList1[i - 1].target[x] != -1 && tree.sonList1[i - 1].ispa == 1) {
                        //if (tree.sonList[i].target[x] == 1) {

                        //System.out.println("nodeID-target "+tree.sonList[i].nodeID+
                        //" -> "+tree.sonList[i].target[x]);
                        //boolean results = causalRule_CRPA(tree.sonList1[i-1]);
                        boolean results = causalRule_CRPA(tree.sonList1[i - 1], allexposure, allexposure_item, tree);

                        //System.out.println("Node ID = " + tree.sonList1[i-1].nodeID + "Causality = " + results);

                        // System.out.println("Finish causalRule, How long?");
                        if (results) {

                            //System.out.println("enter results branch");
                            // To skip this node when generating the next level and
                            // set iscausal to true
                            tree.sonList1[i - 1].token = 4;
                            tree.sonList1[i - 1].iscausal = true;
                            // note to ruleset that this rule is causal rule

                            RuleStru cur;
                            cur = ruleSet.ruleHead;

                            while (cur != null) {
                                if (cur.len == 1) {


                                    //System.out.println("cur.antecedent[0] = "+cur.antecedent[0]);
                                    //System.out.println("tree.sonList[i].nodeID = "+tree.sonList[i].nodeID);
                                    //System.out.println("cur.target[0] = "+cur.target[0]);
                                    //System.out.println("tree.sonList[i].target[0] = "+tree.sonList[i].target[0]);


                                    if ((cur.antecedent[0] == tree.sonList1[i - 1].nodeID)
                                            && (cur.target[0] == tree.sonList1[i - 1].target[0])) {

                                        // zx, firstly, try to find the rule with the same exposure and target value as node.nodeID and node.target[0].
                                        // then, update the rule from association rule to causal association rule.
                                        cur.isCausalRule = true;
                                    }
                                }

                                cur = cur.nextRule;
                            }

                        } else {
                            //System.out.println("enter else branch");

                            tree.sonList1[i - 1].iscausal = false;

                            RuleStru cur;
                            cur = ruleSet.ruleHead;

                            while (cur != null) {
                                if (cur.len == 1) {

                                    //System.out.println("cur.antecedent[0] = "+cur.antecedent[0]);
                                    //System.out.println("tree.sonList[i].nodeID = "+tree.sonList[i].nodeID);
                                    //System.out.println("cur.target[0] = "+cur.target[0]);
                                    //System.out.println("tree.sonList[i].target[0] = "+tree.sonList[i].target[0]);


                                    if ((cur.antecedent[0] == tree.sonList1[i - 1].nodeID)
                                            && (cur.target[0] == tree.sonList1[i - 1].target[0])) {
                                        cur.isCausalRule = false;

                                    }

                                }

                                cur = cur.nextRule;
                            }

                        }
                    }

                }

            }
        }

        /*
         * RuleStru cur; cur = ruleset.ruleHead;
         *
         * //generate first level causal rules while(cur!=null){ if(cur.len==1){
         * cur.isCausalRule=causalRule(cur.antecedent[0], cur.target[0]);
         * if(cur.isCausalRule){ cur.token=4; singleCausalRules.add(cur); }
         *
         * }
         *
         * cur=cur.nextRule; }
         *
         * System.out.println("Single causal rules: "+singleCausalRules.size());
         * //store the rule name
         *
         *
         * //check if the combine include a single causal rule //if yes,
         * ofcource causal rule. If no, test causal rule and note these rules
         * //the combine rules which includes non-causal components may be of
         * interest
         *
         *
         * //3rd level
         *
         *
         *
         * //4th level
         */
    }

    public boolean causalRule_CRPA(PrefixTree node) {

        double upvalue = 0, downvalue = 0, partialvalue;
        double Ma = 0, Mb = 0, Mc = 0, Md = 0;
        int i, j, k;
        int exposure = 0;    // zx, start from 1 not 0.
        int[][] reduced_matrix;

        //System.out.println("realAtt="+realAtt);

        int[] rest = new int[realAtt];
        //int[] condition = new int[realAtt - 1];
        //int[] current = new int[realAtt - 1];
        int[] condition = new int[realAtt];
        int[] current = new int[realAtt];
        int row, col;

        // aggregating equivalent class.
        reduced_matrix = equivalence();



        /*
        -----------reduced_matrix------------
        reduced_matrix.length = 6
        reduced_matrix[0].length = 10
        1 3 6 8  9 14 15 19 0 4
        1 3 6 8 10 14 15 19 0 1
        1 3 6 8 10 14 16 19 0 2
        1 3 6 8 11 14 16 19 0 1
        1 3 6 8 12 14 18 19 1 6
        2 4 6 8 12 14 18 19 1 1

        The last column is total count of the specified same rows.
        The last but one column is target variable.
        */

        //System.out.println("-----------reduced_matrix------------");
        //System.out.println("reduced_matrix.length = " + reduced_matrix.length);
        //System.out.println("reduced_matrix[0].length = " + reduced_matrix[0].length);


        row = reduced_matrix.length;
        col = reduced_matrix[0].length;

        /*
        System.out.println("after equivalence");
        for(i = 0; i < row; i++)
        {
            for(j = 0; j < col; j++)
                System.out.print(reduced_matrix[i][j] + " ");

            System.out.println();
        }
        */

        /*
        for(i = 0; i < reduced_matrix.length; i++)
        {
            for(j = 0; j < reduced_matrix[0].length; j++)
            {
                System.out.print(reduced_matrix[i][j] + " ");
            }

            System.out.println();
        }
        */


        // find exposure index in dataSpace matrix
        for (i = 0; i < realAtt; i++) {
            //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
            //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

            if (dataSpacestat[i].min <= node.nodeID && node.nodeID <= dataSpacestat[i].max) {
                // column index in dataSpace corresponding to node.nodeID in binary matrix.
                exposure = i;
                //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
            }
        }

        if (node.isMultivalue == false) {
            // find the rest indexes in dataSpace matrix
            if (ChosenControl.length == 0) {
                for (i = 0, j = 0; i < realAtt && j < realAtt - 1; i++) {

                    if (exposure != i) {
                        rest[j] = i;
                        j++;
                    }

                }
            } else {
                for (i = 0, j = 0; i < realAtt && j < realAtt - 1; i++) {
                    for (int m = 0; m < ChosenControl.length; m++) {

                        if (exposure != i && i == (ChosenControl[m] - 1)) {
                            rest[j] = i;
                            j++;
                        }
                    }

                }

            }
        } else {
            //hs,add
//            for(i = 0,j = 0; i < realAtt; i++)
//            {
//                if(exposure != i){   //hs.add  for sort without the items from the same attribute
//                     rest[j] = i;
//                     j++;
//                }
//            }
            if (ChosenControl.length == 0) {
                for (i = 0, j = 0; i < realAtt && j < realAtt - 1; i++) {

                    if (exposure != i) {
                        rest[j] = i;
                        j++;
                    }

                }
            } else {
                for (i = 0, j = 0; i < realAtt && j < realAtt - 1; i++) {
                    for (int m = 0; m < ChosenControl.length; m++) {

                        if (exposure != i && i == (ChosenControl[m] - 1)) {
                            rest[j] = i;
                            j++;
                        }
                    }

                }

            }
        }

        System.out.println("rest" + realAtt);

        for (i = 0; i < realAtt; i++)
            System.out.print(rest[i] + " ");

        System.out.println();



        /*
         System.out.println("before reduced_matrix");
        for(i = 0; i < row; i++)
        {
            for(j = 0; j < col; j++)
                System.out.print(reduced_matrix[i][j] + " ");

            System.out.println();
        }
        */

        // sort reduced matrix according to columns containing in rest.
        sort(reduced_matrix, rest);

        // find condition, because of the exclusiveness of value
        for (i = 0, j = 0; i < realAtt && j < realAtt; i++)
        //for(i = 0, j = 0; i < realAtt && j < realAtt - 1; i++)
        {
            if (exposure != i) {
                condition[j] = reduced_matrix[0][i];
                j++;
            }
            //else
            //{
            //    condition[j] = node.nodeID;
            //    j++;
            //}
        }
        /*
        System.out.println("after reduced_matrix");
        for(i = 0; i < row; i++)
        {
            for(j = 0; j < col; j++)
                System.out.print(reduced_matrix[i][j] + " ");

            System.out.println();
        }
        */
        //System.out.println("exposure = " + exposure);
        /*
        System.out.println("condition");

        for(i = 0; i < condition.length; i++)
            System.out.print(condition[i] + " ");

        System.out.println();
        */

        //    -    +
        //  - Ma   Mb
        //  + Mc   Md

        Ma = 0;
        Mb = 0;
        Mc = 0;
        Md = 0;
        upvalue = 0;
        downvalue = 0;


        k = 0;
        //hs.test System.out.println("row="+row); row=5000.
        //System.out.println("row="+row);
        while (k < (row + 1)) {
            /*
            System.out.println((k+1)+"lines, "+"before current vector");

            for(i = 0; i < realAtt; i++)
            //for(i = 0; i < realAtt - 1; i++)
                System.out.print(current[i] + " ");

            System.out.println();
            */

            for (i = 0, j = 0; i < realAtt && j < realAtt; i++) {
                if (k != row) {
                    if (exposure != i) {
                        current[j] = reduced_matrix[k][i];
                        //System.out.println(current[i]);
                        j++;
                    }

                } else {
                    current[j] = 0;
                    j++;

                }


            }


            /*
            for(i=0,j=0;i<realAtt&&j<realAtt;i++){
                if(exposure!=i&&k != row){
                    current[j]=reduced_matrix[k][i];
                    j++;
                }


            }
            */



            /*
            System.out.println((k+1)+"lines, "+"after current vector");

            for(i = 0; i < realAtt; i++)
                for(i = 0; i < realAtt ; i++)
                System.out.print(current[i] + " ");

            System.out.println();
            */


            //    -    +
            //  - Ma   Mb
            //  + Mc   Md
            boolean equal = Arrays.equals(current, condition);
            if (equal == true) {
                //System.out.println("-----------true-----------");
                // measure table for variables
                if (reduced_matrix[k][col - 2] == 1) {
                    // the value of target variable is one
                    if (reduced_matrix[k][exposure] == node.nodeID) {
                        Md = Md + reduced_matrix[k][col - 1];        // 1-1
                        //System.out.println("Md = "+Md);
                    } else {
                        //System.out.println("-----------2-----------");
                        Mb = Mb + reduced_matrix[k][col - 1];        // 0-1
                        //System.out.println("Mb = "+Mb);
                    }
                } else {
                    // the value of target variable is zero
                    if (reduced_matrix[k][exposure] == node.nodeID) {
                        //System.out.println("-----------3-----------");
                        Mc = Mc + reduced_matrix[k][col - 1];        // 1-0
                        //System.out.println("Mc = "+Mc);
                    } else {
                        //System.out.println("-----------4-----------");
                        Ma = Ma + reduced_matrix[k][col - 1];        // 0-0
                        //System.out.println("Ma = "+Ma);
                    }

                }

                //hs.add
                //hs.end
                //System.out.println("hushu"+"Ma="+Ma+", Mb="+Mb+", Mc="+Mc+", Md="+Md);

            } else {
                // operate
                if (Ma == 0)
                    Ma = 0.1;
                if (Mb == 0)
                    Mb = 0.1;
                if (Mc == 0)
                    Mc = 0.1;
                if (Md == 0)
                    Md = 0.1;

                //[Ma Mb Mc Md]

                //    -    +
                //  - Ma   Mb
                //  + Mc   Md

                //System.out.println("Ma="+Ma+", Mb="+Mb+", Mc="+Mc+", Md="+Md);

                if ((Ma + Mb) * (Ma + Mc) * (Mc + Md) * (Mb + Md) != 0) {
                    if ((Md + Mc) >= 1) {
                        //System.out.println("Ma="+Ma+", Mb="+Mb+", Mc="+Mc+", Md="+Md);
                        //System.out.println("Ma*Md="+Ma*Md);
                        //System.out.println("Mc*Mb="+Mc*Mb);
                        //System.out.println("Ma+Mb+Mc+Md="+(Ma+Mb+Mc+Md));
                        //System.out.println("(Ma*Md-Mc*Mb)/(Ma+Mb+Mc+Md)="+((Ma*Md-Mc*Mb)/(Ma+Mb+Mc+Md)));
                        upvalue = upvalue + (Ma * Md - Mc * Mb) / (Ma + Mb + Mc + Md);
                        //System.out.println("upvalue="+upvalue);
                        downvalue = downvalue + (Ma + Mb) * (Ma + Mc) * (Mc + Md) * (Mb + Md) / Math.pow((Ma + Mb + Mc + Md), 2) / ((Ma + Mb + Mc + Md) - 1);
                    }
                    //Ma = 0; Mb = 0; Mc = 0; Md = 0;
                }

                Ma = 0;
                Mb = 0;
                Mc = 0;
                Md = 0;

                if (k != row) {
                    for (i = 0, j = 0; i < realAtt && j < realAtt; i++) {
                        if (exposure != i) {
                            condition[j] = reduced_matrix[k][i];
                            j++;
                        }
                    }
                } else {
                    break;
                }

                /*
                System.out.println("new condition");
                for(i = 0; i < realAtt; i++)
                    System.out.print(condition[i] + " ");
                System.out.println();
                */

                k = k - 1;
            }
            k = k + 1;
        }


        //System.out.println("upvalue = "+upvalue+", downvalue = "+downvalue);


        if (downvalue != 0) {
            partialvalue = Math.pow((Math.abs(upvalue) - 0.5), 2) / downvalue;
        } else
            partialvalue = 0;
        //System.out.println("upvalue="+upvalue);
        //System.out.println("downvalue="+downvalue);

        //System.out.println("partialvalue = " + partialvalue);

        if (partialvalue > PaValue) {
            //System.out.println("3.84");
            //System.out.println("PaValue="+PaValue);
            return true;
        } else {
            return false;
        }


    }


    public void causalTest_CRPA_Two(PrefixTree tree, int layer, int[] allexposure, int[] allexposure_item, PrefixTree alltree) {
        // System.out.print("number of son: "+tree.numOfSon+"tree.len"+tree.len+"layer"+layer);

        //System.out.println("enter multi causalRule_CRPA");
        System.out.println("tree.len = " + tree.len);
        System.out.println("tree.nodeID = " + tree.nodeID);
        //System.out.println("tree.sonList[i-1].sonList[0]"+tree.sonList[0].nodeID);
        if ((tree.len) == layer) {

            //System.out.println("current layer = "+layer);

            int[] temp = new int[layer];
            for (int j = 0; j < (tree.len); j++) {
                printf("%d ", tree.set[j]);
                temp[j] = tree.set[j];
            }

            // System.out.println("testing");
            // printRecord(temp);
            for (int i = 0; i < MAXTARGET; i++) {
                // causal testing is only conducted on positive associations.
                if (tree.target[i] != -1 && tree.ispa == 1) {
                    //if (tree.target[i] != -1) {
                    // printf("-> %d ", tree.target[i]);
                    //System.out.println("ok");

                    boolean results = causalRule_CRPA(temp, tree.target[i], allexposure, allexposure_item, alltree);

                    //System.out.println("causalRule_CRPA results="+results);

                    // System.out.println("results of causal test ="+results);
                    if (results) {

                        // to skip this node when go to next level
                        // tree.token=4;
                        //System.out.println("true");
                        tree.iscausal = true;

                        // notify ruleSet
                        RuleStru cur;
                        cur = ruleSet.ruleHead;
                        while (cur != null) {
                            //System.out.println("hushuruleSet.ruleHead.len="+cur.len);
                            if (cur.len == layer) {
                                int[] temp2 = new int[layer];
                                for (int v = 0; v < layer; v++) {
                                    temp2[v] = cur.antecedent[v];
                                }
                                System.out.print("temp2: ");
                                printRecord(temp2);
                                System.out.print("target: ");
                                printRecord(cur.target);
                                if (Arrays.equals(temp2, temp)
                                        && (cur.target[0] == tree.target[i])) {
                                    cur.isCausalRule = true;
                                    //System.out.println("ruleSet.ruleHead.len="+cur.len);
                                    //System.out.println("ruleset.ruleHead.isCausalRule="+cur.isCausalRule);
                                    System.out.println("Yes, set this to true");
                                }

                            }

                            cur = cur.nextRule;
                        }

                    } else {
                        //System.out.println("ready go");

                        tree.iscausal = false;
                        RuleStru cur;
                        cur = ruleSet.ruleHead;
                        while (cur != null) {
                            if (cur.len == layer) {
                                int[] temp2 = new int[layer];
                                for (int v = 0; v < layer; v++) {
                                    temp2[v] = cur.antecedent[v];
                                }
                                // System.out.print("temp2: ");
                                // printRecord(temp2);
                                // System.out.print("target: ");
                                // printRecord(cur.target);
                                if (Arrays.equals(temp2, temp)
                                        && (cur.target[0] == tree.target[i])) {
                                    cur.isCausalRule = false;
                                    System.out.println("NO, set this to false");
                                }

                            }

                            cur = cur.nextRule;
                        }

                    }
                }

            }

        }
        //for(int j=0;j<maxItem;j++){
        for (int i = 0; i < tree.numOfSon; i++) {
            //for (int i = 0; i < maxItem; i++) {
            //if((i>=(dataSpacestat[ChosenTest[0]-1].min-1)&&i<=(dataSpacestat[ChosenTest[0]-1].max-1))||(i>=(dataSpacestat[ChosenTest[1]-1].min-1)&&i<=(dataSpacestat[ChosenTest[1]-1].max-1))){
            //System.out.println("hushu="+i);
            //for (int j = 0; j < tree.numOfSon; j++) {
            //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
            causalTest_CRPA_Two(tree.sonList[i], layer, allexposure, allexposure_item, allSet);


            System.out.println("hushutree.sonList1[i]" + tree.sonList[i].nodeID);

            System.out.println("hushutree.sonList1[i]" + tree.sonList[i].len);

            if (tree.sonList[i].iscausal) {
                System.out.println("hushu is causal");
                tree.sonList[i].token = 4;

            }
            //}
            //}
            //}
        }
        /*
         * for (int i = 0; i < tree.numOfSon; i++) {
         *
         * for(int x=0; x<MAXTARGET; x++){ if(tree.sonList[i].target[x]!=-1){
         * System.out.println("nodeID-target "+tree.sonList[i].len+
         * " -> "+tree.sonList[i].target[x]); boolean results =
         * causalRule(tree.sonList[i].set,tree.sonList[i].target[x]);
         * if(results){ //To skip this node when generating the next level and
         * set iscausal to true tree.sonList[i].token=4;
         * tree.sonList[i].iscausal=true; } else{
         * tree.sonList[i].iscausal=false; } }
         *
         * } }
         */
    }

    public boolean causalRule_CRPA(int[] item, int target) {
        //System.out.println("------------causalRule_CRPA more than one level -----------");


        //System.out.println("item[0]" + item[0] + "item[1]" + item[1]);


        double upvalue = 0, downvalue = 0, partialvalue;
        double Ma = 0, Mb = 0, Mc = 0, Md = 0;
        int i, j, k, f;
        int exposure = 0;
        int[][] reduced_matrix;
        int[] rest = new int[realAtt];
        //int[] condition = new int[realAtt - 1];
        int[] condition = new int[realAtt];
        //int[] start = new int[realAtt - 1];
        int[] start = new int[realAtt];
        int row, col;


        /*
        System.out.println("item.length = "+item.length);
        for(i = 0; i < item.length; i++)
        {
            if(item[i] != 0)
            {
                System.out.print(item[i] + " ");
            }
        }
        */


        // aggregating equivalent class.
        reduced_matrix = equivalence();

        /*
        -----------reduced_matrix------------
        reduced_matrix.length = 6
        reduced_matrix[0].length = 10
        1 3 6 8  9 14 15 19 0 4
        1 3 6 8 10 14 15 19 0 1
        1 3 6 8 10 14 16 19 0 2
        1 3 6 8 11 14 16 19 0 1
        1 3 6 8 12 14 18 19 1 6
        2 4 6 8 12 14 18 19 1 1

        The last column is total count of the specified same rows.
        The last but one column is target variable.
        */

        //System.out.println("-----------reduced_matrix------------");

        //System.out.println("reduced_matrix.length = " + reduced_matrix.length);
        //System.out.println("reduced_matrix[0].length = " + reduced_matrix[0].length);


        row = reduced_matrix.length;
        col = reduced_matrix[0].length;

        /*
        for(i = 0; i < reduced_matrix.length; i++)
        {
            for(j = 0; j < reduced_matrix[0].length; j++)
            {
                System.out.print(reduced_matrix[i][j] + " ");
            }

            System.out.println();
        }
        */

        int[] curr = new int[item.length + 1];
        boolean[] curr_state = new boolean[item.length];

        for (f = 0; f < item.length; f++) {
            // find exposure index in dataSpace matrix
            for (i = 0; i < realAtt; i++) {
                if (dataSpacestat[i].min <= item[f] && item[f] <= dataSpacestat[i].max) {
                    // column index in dataSpace corresponding to node.nodeID in binary matrix.
                    curr[f] = i;
                    //System.out.println("curr["+f+"]"+curr[f]);
                    if (dataSpacestat[i].max - dataSpacestat[i].min != 0) {
                        // It is multi value
                        curr_state[f] = true;
                    }
                }
            }

        }

        /*
        f = 0;

        for(i = 0, j = 0; i < realAtt && j < realAtt; i++)
        {
            // item is an ascending array.
            if(f < item.length)
            {

                if(i != curr[f])
                {
                    rest[j] = i;
                    j++;
                }
                else if(i == curr[f] && curr_state[f] == false)
                {
                    // it is single value
                    //i++;
                    f++;
                }
                else if(i == curr[f] && curr_state[f] == true)
                {
                    rest[j] = i;
                    j++;
                    f++; // Judge the next item value.
                }
            }
        }
        */
        /*
        System.out.println("rest");
        for(i = 0; i < realAtt; i++)
            System.out.print(rest[i]+" ");

        System.out.println();
        */


        // zx, multivalue
        /*
        for(f = 0; f < item.length; f++){
            //System.out.println("curr["+f+"]"+curr[f]);

        }
        for(i = 0; i < realAtt; i++)
        {
            rest[i] = i;
        }
        */


        // hs,add
        if (ChosenControl.length == 0) {
            for (i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1; i++) {
                if (curr[f] != i) {   //hs.add  for sort without the items from the same attribute
                    rest[j] = i;
                    j++;
                } else {
                    f++;
                }
            }
        } else {
            for (i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1; i++) {
                //for(int m=0;m<ChosenControl.length;m++){
                //System.out.println("ChosenControl["+m+"]"+ChosenControl[m]);
                if (curr[f] != i) { //hs.add  for sort without the items from the same attribute
                    for (int m = 0; m < ChosenControl.length; m++) {
                        if (i == (ChosenControl[m] - 1)) {
                            rest[j] = i;
                            j++;
                        }
                    }
                } else {
                    f++;
                }
                //}
            }
        }
        //hs.end
        /*
         System.out.println("rest");
        for(i = 0; i < realAtt; i++)
            System.out.print(rest[i]+" ");

        System.out.println();
        */


        // sort reduced matrix according to columns containing in rest.
        sort(reduced_matrix, rest);

        // find condition
        //for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
        /*
        for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length; i++)
        {
            if(curr[f] != i)
            {
                condition[j] = reduced_matrix[0][i];
                j++;
            }
            else
            {
                f++;
            }
        }

        */

        /*
        for(i = 0; i < realAtt; i++)
        {

            condition[i] = reduced_matrix[0][i];
        }
        */
        //hs.add
        for (i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1; i++) {
            if (curr[f] != i) {
                condition[j] = reduced_matrix[0][i];
                j++;
            } else {
                f++;
            }


        }
        //hs.end


        //    -    +
        //  - Ma   Mb
        //  + Mc   Md

        Ma = 0;
        Mb = 0;
        Mc = 0;
        Md = 0;
        upvalue = 0;
        downvalue = 0;


        //k = 0;
        //while(k < row)

        k = 0;
        while (k < (row + 1)) {
            /*
            for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
            {
                if(curr[f] != i)
                {
                    start[j] = reduced_matrix[k][i];
                    j++;
                }
                else
                {
                    f++;
                }
            }
            */

            /*
            //for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
            for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length; i++)
            {
                if(k != row)
                {
                    if(curr[f] != i)
                    {
                        start[j] = reduced_matrix[k][i];
                        j++;
                    }
                    else
                    {
                        f++;
                    }
                }
                else
                {
                     start[j] = 0;
                     j++;
                }
            }

            */

            /*
            for(i = 0; i < realAtt; i++)
            {
                if(k != row)
                {
                    start[i] = reduced_matrix[k][i];
                }
                else
                {
                    start[i] = 0;
                }
            }
            */
            //hs.add
            for (i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1; i++) {
                if (k != row) {
                    if (curr[f] != i) {
                        start[j] = reduced_matrix[k][i];
                        j++;
                    } else {
                        f++;
                    }
                } else {
                    start[j] = 0;
                    j++;
                }
            }
            //hs.end


            boolean equal = Arrays.equals(start, condition);

            //for(i = 0; i < start.length; i++)
            //{
            //    System.out.print(start[i]+" ");
            //    System.out.print(condition[i]+" ");

            //}

            //System.out.println("equal="+equal);

            if (equal == true) {
                // measure table for variables

                if (reduced_matrix[k][col - 2] == 1) {
                    // the value of target variable is one
                    int sum1 = 0, sum2 = 0;
                    for (f = 0; f < item.length; f++) {
                        sum1 += item[f];
                        sum2 += reduced_matrix[k][curr[f]];
                    }


                    if (sum1 == sum2 && reduced_matrix[k][curr[0]] == item[0]) {
                        Md = Md + reduced_matrix[k][col - 1];        // 1-1
                    } else {
                        Mb = Mb + reduced_matrix[k][col - 1];        // 0-1
                    }
                } else {
                    int sum1 = 0, sum2 = 0;
                    for (f = 0; f < item.length; f++) {
                        sum1 += item[f];
                        sum2 += reduced_matrix[k][curr[f]];
                    }

                    // the value of target variable is zero

                    if (sum1 == sum2 && reduced_matrix[k][curr[0]] == item[0]) {
                        Mc = Mc + reduced_matrix[k][col - 1];        // 1-0
                    } else {
                        Ma = Ma + reduced_matrix[k][col - 1];        // 0-0
                    }

                }
            } else {
                // operate
                if (Ma == 0)
                    Ma = 0.1;
                if (Mb == 0)
                    Mb = 0.1;
                if (Mc == 0)
                    Mc = 0.1;
                if (Md == 0)
                    Md = 0.1;

                //[Ma Mb Mc Md]

                //    -    +
                //  - Ma   Mb
                //  + Mc   Md

                if ((Ma + Mb) * (Ma + Mc) * (Mc + Md) * (Mb + Md) != 0) {
                    if ((Md + Mc) >= 1) {
                        upvalue = upvalue + (Ma * Md - Mc * Mb) / (Ma + Mb + Mc + Md);
                        downvalue = downvalue + (Ma + Mb) * (Ma + Mc) * (Mc + Md) * (Mb + Md) / Math.pow((Ma + Mb + Mc + Md), 2) / ((Ma + Mb + Mc + Md) - 1);
                    }
                }

                Ma = 0;
                Mb = 0;
                Mc = 0;
                Md = 0;

                /*
                for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
                {
                    if(curr[f] != i)
                    {
                        condition[j] = reduced_matrix[k][i];
                        j++;
                    }
                    else
                    {
                        f++;
                    }
                }

                */


                /*
                //for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - 1 && f < item.length; i++)
                for(i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length; i++)
                {
                    if(k != row)
                    {
                        if(curr[f] != i)
                        {
                            condition[j] = reduced_matrix[k][i];
                            j++;
                        }
                        else
                        {
                            f++;
                        }

                    }
                    else
                    {
                        break;
                    }
                }

                */

                /*
                if(k != row)
                {
                    for(i = 0; i < realAtt; i++)
                    {
                        condition[i] = reduced_matrix[k][i];
                    }
                }
                else
                {
                    break;
                }
                */
                //hs.add
                if (k != row) {
                    for (i = 0, j = 0, f = 0; i < realAtt && j < realAtt - item.length && f < item.length + 1; i++) {
                        if (curr[f] != i) {
                            condition[j] = reduced_matrix[k][i];
                            j++;
                        } else {
                            f++;
                        }
                    }
                } else {
                    break;
                }
                //hs.end


                k = k - 1;
            }

            k = k + 1;
        }

        //System.out.println("item.length = "+item.length);
        for (i = 0; i < item.length; i++) {
            if (item[i] != 0) {
                //System.out.print(item[i] + " ");
            }
        }
        //System.out.println();

        //System.out.println("upvalue = "+upvalue+", downvalue = "+downvalue);

        if (downvalue != 0)
            partialvalue = Math.pow((Math.abs(upvalue) - 0.5), 2) / downvalue;
        else
            partialvalue = 0;

        //System.out.println("partialvalue = " + partialvalue);

        if (partialvalue > PaValue) {
            //System.out.println("3.84");

            return true;
        } else {
            return false;
        }


        //return true;

    }


    //hs.end

    // this is to test the causal rules for all rules in ruleset
    public void causalTest_CRPA(PrefixTree tree) {
        System.out.println("number of son: " + tree.numOfSon);
        //hs.add
        int[] allexposure = new int[realAtt];
        int[] allexposure_item = new int[tree.numOfSon + 1];
        //ArrayList allexposure=new ArrayList();
        //int[] allexcept = new int[realAtt];
        //int[] allexposure;
        for (int i = 0, g = 1; i < tree.numOfSon; i++, g++) {
            System.out.println("the pa of i" + tree.sonList[i].ispa);
            if (tree.sonList[i].ispa == 1) {
                System.out.println("tree.sonList[i].nodeID=" + tree.sonList[i].nodeID);
                allexposure_item[g] = 1;
                for (int j = 0, f = 0; j < realAtt; j++, f++) {
                    if (dataSpacestat[j].min <= tree.sonList[i].nodeID && tree.sonList[i].nodeID <= dataSpacestat[j].max) {
                        //count++;
                        //if(count<=10){
                        allexposure[f] = 1;
                        //System.out.println(f);
                        //}
                    }
                }
            }
        }
        //System.out.println("allexposureattribute");
        for (int f = 0; f < allexposure.length; f++) {
            if (allexposure[f] == 1) {
                System.out.print(f);
            }

        }
        System.out.println();
        for (int g = 0; g < allexposure_item.length; g++) {
            if (allexposure_item[g] == 1) {
                System.out.print(g);
            }

        }
//        for(int j=0;j<realAtt;j++){
//            for(int m=0,i=0;m<realAtt;m++,i++){
//                if(j!=allexposure[m]){
//                    allexcept[i] = j;
//                    System.out.println(allexposure[i]);
//                }
//
//            }
//
//        }

        //hs.end

        //System.out.println("enter single causalTest_CRPA");

        for (int i = 0; i < tree.numOfSon; i++) {
            // zx, there is probably none or only one target. Hence, for loop is redundant.
            for (int x = 0; x < MAXTARGET; x++) {
                // (tree.sonList[i].ispa == 1) represents rule is positive association.
                // causal testing is only conducted on positive associations.
                if (tree.sonList[i].target[x] != -1 && tree.sonList[i].ispa == 1) {
                    //if (tree.sonList[i].target[x] == 1) {

                    //System.out.println("nodeID-target "+i+" "+tree.sonList[i].nodeID+
                    //" -> "+tree.sonList[i].target[x]);
                    boolean results = causalRule_CRPA(tree.sonList[i], allexposure, allexposure_item, tree);

                    System.out.println("Node ID = " + tree.sonList[i].nodeID + "Causality = " + results);

                    // System.out.println("Finish causalRule, How long?");
                    if (results) {

                        //System.out.println("enter results branch");
                        // To skip this node when generating the next level and
                        // set iscausal to true
                        tree.sonList[i].token = 4;
                        tree.sonList[i].iscausal = true;
                        // note to ruleset that this rule is causal rule

                        RuleStru cur;
                        cur = ruleSet.ruleHead;

                        while (cur != null) {
                            if (cur.len == 1) {


                                //System.out.println("cur.antecedent[0] = "+cur.antecedent[0]);
                                //System.out.println("tree.sonList[i].nodeID = "+tree.sonList[i].nodeID);
                                //System.out.println("cur.target[0] = "+cur.target[0]);
                                //System.out.println("tree.sonList[i].target[0] = "+tree.sonList[i].target[0]);


                                if ((cur.antecedent[0] == tree.sonList[i].nodeID)
                                        && (cur.target[0] == tree.sonList[i].target[0])) {

                                    // zx, firstly, try to find the rule with the same exposure and target value as node.nodeID and node.target[0].
                                    // then, update the rule from association rule to causal association rule.
                                    cur.isCausalRule = true;
                                }
                            }

                            cur = cur.nextRule;
                        }

                    } else {
                        //System.out.println("enter else branch");

                        tree.sonList[i].iscausal = false;

                        RuleStru cur;
                        cur = ruleSet.ruleHead;

                        while (cur != null) {
                            if (cur.len == 1) {

                                //System.out.println("cur.antecedent[0] = "+cur.antecedent[0]);
                                //System.out.println("tree.sonList[i].nodeID = "+tree.sonList[i].nodeID);
                                //System.out.println("cur.target[0] = "+cur.target[0]);
                                //System.out.println("tree.sonList[i].target[0] = "+tree.sonList[i].target[0]);


                                if ((cur.antecedent[0] == tree.sonList[i].nodeID)
                                        && (cur.target[0] == tree.sonList[i].target[0])) {
                                    cur.isCausalRule = false;

                                }

                            }

                            cur = cur.nextRule;
                        }

                    }
                }

            }

        }

        /*
         * RuleStru cur; cur = ruleset.ruleHead;
         *
         * //generate first level causal rules while(cur!=null){ if(cur.len==1){
         * cur.isCausalRule=causalRule(cur.antecedent[0], cur.target[0]);
         * if(cur.isCausalRule){ cur.token=4; singleCausalRules.add(cur); }
         *
         * }
         *
         * cur=cur.nextRule; }
         *
         * System.out.println("Single causal rules: "+singleCausalRules.size());
         * //store the rule name
         *
         *
         * //check if the combine include a single causal rule //if yes,
         * ofcource causal rule. If no, test causal rule and note these rules
         * //the combine rules which includes non-causal components may be of
         * interest
         *
         *
         * //3rd level
         *
         *
         *
         * //4th level
         */
    }

    public void causalTestsmall_CRPA(PrefixTree tree, int layer, int[] allexposure) {
        //int[] allexposure = new int[realAtt];
        if (tree.len == layer) {
            for (int j = 0; j < tree.len; j++) {
                if (tree.ispa == 1) {
                    for (int h = 0, f = 0; h < realAtt; h++, f++) {
                        if (dataSpacestat[h].min <= tree.set[j] && tree.set[j] <= dataSpacestat[h].max) {
                            allexposure[f] = 1;
                            System.out.println(f);

                        }
                    }

                }


            }

        }
        for (int i = 0; i < tree.numOfSon; i++) {

            //System.out.println(tree.sonList[i].len);
            causalTestsmall_CRPA(tree.sonList[i], layer, allexposure);


        }

    }

    public void causalTest_CRPA(PrefixTree tree, int layer, int[] allexposure, int[] allexposure_item, PrefixTree alltree) {
        //System.out.print("number of son: "+tree.numOfSon+" "+"tree.len"+tree.len+" "+"layer"+layer+" ");
        //System.out.println("enter multi causalRule_CRPA");
        //int[] allexposure = new int[realAtt];
//        if (tree.len == layer){
//            for(int j = 0;j < tree.len; j++){
//                if(tree.ispa == 1){
//                    for(int i = 0,f = 0;i < realAtt;i++,f++){
//                        if(dataSpacestat[i].min <=  tree.set[j] &&  tree.set[j] <= dataSpacestat[i].max){
//                            allexposure[f] = 1;
//                            System.out.println(f);
//                        }
//                    }
//
//                }
//
//            }
//        }
//        for(int f=0;f< allexposure.length;f++){
//            if(allexposure[f] == 1){
//            System.out.print(f);
//            }
//
//        }
//
        //System.out.println("tree.len"+tree.len);
        //System.out.println("tree.nodeID = "+tree.nodeID);
        if (tree.len == layer) {

            //System.out.println("current layer = "+layer);
            //int[] allexposure = new int[realAtt];
            int[] temp = new int[layer];
            for (int j = 0; j < tree.len; j++) {
                printf("%d ", tree.set[j]);
                temp[j] = tree.set[j];
                //System.out.print("temp"+temp[j]);
            }

            // System.out.println("testing");
            // printRecord(temp);
            for (int i = 0; i < MAXTARGET; i++) {
                // causal testing is only conducted on positive associations.
                if (tree.target[i] != -1 && tree.ispa == 1) {
                    // printf("-> %d ", tree.target[i]);

                    boolean results = causalRule_CRPA(temp, tree.target[i], allexposure, allexposure_item, alltree);

                    //System.out.println("causalRule_CRPA results="+results);

                    // System.out.println("results of causal test ="+results);
                    if (results) {

                        // to skip this node when go to next level
                        // tree.token=4;
                        tree.iscausal = true;

                        // notify ruleSet
                        RuleStru cur;
                        cur = ruleSet.ruleHead;
                        while (cur != null) {
                            if (cur.len == layer) {
                                int[] temp2 = new int[layer];
                                for (int v = 0; v < layer; v++) {
                                    temp2[v] = cur.antecedent[v];
                                }
//                                 System.out.print("temp2: ");
//                                 printRecord(temp2);
//                                 System.out.print("target: ");
//                                 printRecord(cur.target);
                                if (Arrays.equals(temp2, temp)
                                        && (cur.target[0] == tree.target[i])) {
                                    cur.isCausalRule = true;
                                    //System.out.println("ruleset.ruleHead.isCausalRule="+cur.isCausalRule);
                                    //System.out.println("ruleSet.ruleHead.len="+cur.len);
                                    // System.out.println("Yes, set this to true");
                                }

                            }

                            cur = cur.nextRule;
                        }

                    } else {
                        tree.iscausal = false;
                        RuleStru cur;
                        cur = ruleSet.ruleHead;
                        while (cur != null) {
                            if (cur.len == layer) {
                                int[] temp2 = new int[layer];
                                for (int v = 0; v < layer; v++) {
                                    temp2[v] = cur.antecedent[v];
                                }
                                // System.out.print("temp2: ");
                                // printRecord(temp2);
                                // System.out.print("target: ");
                                // printRecord(cur.target);
                                if (Arrays.equals(temp2, temp)
                                        && (cur.target[0] == tree.target[i])) {
                                    cur.isCausalRule = false;
                                    // System.out.println("NO, set this to false");
                                }

                            }

                            cur = cur.nextRule;
                        }

                    }
                }

            }

        }

        for (int i = 0; i < tree.numOfSon; i++) {

            //System.out.println(tree.sonList[i].len);
            //System.out.println("ruleset.ruleHead.isCausalRule="+ruleSet.ruleHead.isCausalRule);
            causalTest_CRPA(tree.sonList[i], layer, allexposure, allexposure_item, allSet);
            //System.out.println("hushutree.sonList1[i]"+tree.sonList[i].nodeID);
            System.out.println("hushutree.sonList1[i]" + tree.sonList[i].nodeID);
            System.out.println("hushutree.sonList1[i]" + tree.sonList[i].len);
            if (tree.sonList[i].iscausal) {
                System.out.println("hushu is causal");
                tree.sonList[i].token = 4;

            }
        }
        /*
         * for (int i = 0; i < tree.numOfSon; i++) {
         *
         * for(int x=0; x<MAXTARGET; x++){ if(tree.sonList[i].target[x]!=-1){
         * System.out.println("nodeID-target "+tree.sonList[i].len+
         * " -> "+tree.sonList[i].target[x]); boolean results =
         * causalRule(tree.sonList[i].set,tree.sonList[i].target[x]);
         * if(results){ //To skip this node when generating the next level and
         * set iscausal to true tree.sonList[i].token=4;
         * tree.sonList[i].iscausal=true; } else{
         * tree.sonList[i].iscausal=false; } }
         *
         * } }
         */
    }


//    public void causalTest(PrefixTree tree, int layer) {
//        // System.out.print("number of son: "+tree.numOfSon+"tree.len"+tree.len+"layer"+layer);
//
//        if (tree.len == layer) {
//            int[] temp = new int[layer];
//            for (int j = 0; j < tree.len; j++) {
//                // printf("%d ", tree.set[j]);
//                temp[j] = tree.set[j];
//            }
//			// System.out.println("testing");
//            // printRecord(temp);
//            for (int i = 0; i < MAXTARGET; i++) {
//                if (tree.target[i] != -1) {
//                    // printf("-> %d ", tree.target[i]);
//                    boolean results = causalRule_CRPA(temp, tree.target[i]);
//                    // System.out.println("results of causal test ="+results);
//                    if (results) {
//
//						// to skip this node when go to next level
//                        // tree.token=4;
//                        tree.iscausal = true;
//
//                        // notify ruleSet
//                        RuleStru cur;
//                        cur = ruleSet.ruleHead;
//                        while (cur != null) {
//                            if (cur.len == layer) {
//                                int[] temp2 = new int[layer];
//                                for (int v = 0; v < layer; v++) {
//                                    temp2[v] = cur.antecedent[v];
//                                }
//								// System.out.print("temp2: ");
//                                // printRecord(temp2);
//                                // System.out.print("target: ");
//                                // printRecord(cur.target);
//                                if (Arrays.equals(temp2, temp)
//                                        && (cur.target[0] == tree.target[i])) {
//                                    cur.isCausalRule = true;
//                                    // System.out.println("Yes, set this to true");
//                                }
//
//                            }
//
//                            cur = cur.nextRule;
//                        }
//
//                    } else {
//                        tree.iscausal = false;
//                        RuleStru cur;
//                        cur = ruleSet.ruleHead;
//                        while (cur != null) {
//                            if (cur.len == layer) {
//                                int[] temp2 = new int[layer];
//                                for (int v = 0; v < layer; v++) {
//                                    temp2[v] = cur.antecedent[v];
//                                }
//								// System.out.print("temp2: ");
//                                // printRecord(temp2);
//                                // System.out.print("target: ");
//                                // printRecord(cur.target);
//                                if (Arrays.equals(temp2, temp)
//                                        && (cur.target[0] == tree.target[i])) {
//                                    cur.isCausalRule = false;
//                                    // System.out.println("NO, set this to false");
//                                }
//
//                            }
//
//                            cur = cur.nextRule;
//                        }
//
//                    }
//                }
//
//            }
//
//        }
//
//        for (int i = 0; i < tree.numOfSon; i++) {
//
//            causalTest(tree.sonList[i], layer);
//
//            if (tree.sonList[i].iscausal) {
//                tree.sonList[i].token = 4;
//
//            }
//        }
//        /*
//         * for (int i = 0; i < tree.numOfSon; i++) {
//         *
//         * for(int x=0; x<MAXTARGET; x++){ if(tree.sonList[i].target[x]!=-1){
//         * System.out.println("nodeID-target "+tree.sonList[i].len+
//         * " -> "+tree.sonList[i].target[x]); boolean results =
//         * causalRule(tree.sonList[i].set,tree.sonList[i].target[x]);
//         * if(results){ //To skip this node when generating the next level and
//         * set iscausal to true tree.sonList[i].token=4;
//         * tree.sonList[i].iscausal=true; } else{
//         * tree.sonList[i].iscausal=false; } }
//         *
//         * } }
//         */
//    }

    /*
     * public void causalTest(RuleSet ruleset, int level){ RuleStru cur;
     * cur=ruleset.ruleHead;
     *
     * int[] singlecausals=new int[singleCausalRules.size()]; int index=0;
     * for(RuleStru rs:singleCausalRules){
     * //System.out.println(rs.antecedent[0]);
     * singlecausals[index++]=rs.antecedent[0]; }
     * System.out.println("single rules:"); printRecord(singlecausals);
     *
     * while(cur!=null){ //everything includes a causal single will be causal
     * rules.
     *
     * // if(cur.antecedent.length==2){ // for(int j=0; j<cur.len; j++){
     * if(Arrays.binarySearch(singlecausals, cur.antecedent[0])>=0
     * ||Arrays.binarySearch(singlecausals, cur.antecedent[1])>=0 ){
     * cur.isCausalRule=true; // break; } // } //otherwise
     * if(cur.isCausalRule==null){ boolean
     * results=causalRule(cur.antecedent,cur.target[0]); if(results){
     * secondlevelCausalRules.add(cur); } cur.isCausalRule=results; }
     *
     *
     *
     *
     * //cur.antecedent[0]; cur=cur.nextRule; }
     *
     * for(RuleStru rs:singleCausalRules){ System.out.print(", "+
     * rs.antecedent[0]); } System.out.println("Second level:"); for(RuleStru
     * rs:secondlevelCausalRules){ printRecord(rs.antecedent); }
     *
     * }
     */
    public void printList(List<int[]> list) {
        for (int[] record : list) {
            System.out.format("%d, ", record);

        }
        System.out.print("\n");
    }

    // find intersection between the record and the list of noncfd.
    // First remove the class value
    // As the noncfd does not contain the item, hence the intersection will not
    // contain
    // in this version we donot need to remove the item/friends
    public int[] intersection(int[] list1org, int[] list2, int[] item) {
        // int[] itemfriends=friends(item, list2);
        int[] list1 = new int[list1org.length - 1];
        // similar to recordcopy
        for (int x = 0; x < list1.length; x++) {

            list1[x] = list1org[x];
        }

        Arrays.sort(list1);
        Arrays.sort(list2);
        int[] list = new int[list1.length];
        int listindex = 0;
        int list1index = 0;
        int list2index = 0;
        while (list1index < list1.length && list2index < list2.length) {
            if (list1[list1index] == list2[list2index]) {
                list[listindex++] = list1[list1index];
                list1index++;
                list2index++;
            } else if (list1[list1index] < list2[list2index]) {
                list1index++;
            } else {
                list2index++;
            }

        }
        list = trimzeros(list);
        return list;
    }

    // the intersection is designed for checking the intersection a record and a
    // list of nonconfounders.
    // list1org is the record to pass on. list2 is the list of noncfd
    // need to remove class and item/friends in list1org.
    public int[] intersection(int[] list1org, int[] list2, int item) {
        int[] itemfriends = friends(item, list2);
        int[] list1 = new int[list1org.length - 1];

        for (int x = 0; x < list1.length; x++) {
            if (list1org[x] == item) {
                continue;
            }
            int flag = 0;
            for (int y = 0; y < itemfriends.length; y++) {
                if (list1org[x] == itemfriends[y]) {
                    flag = 1;
                }
            }
            if (flag == 1) {
                continue;
            }
            list1[x] = list1org[x];
        }

        Arrays.sort(list1);
        Arrays.sort(list2);
        int[] list = new int[list1.length];
        int listindex = 0;
        int list1index = 0;
        int list2index = 0;
        while (list1index < list1.length && list2index < list2.length) {
            if (list1[list1index] == list2[list2index]) {
                list[listindex++] = list1[list1index];
                list1index++;
                list2index++;
            } else if (list1[list1index] < list2[list2index]) {
                list1index++;
            } else {
                list2index++;
            }

        }
        list = trimzeros(list);
        return list;
    }

    public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();
        for (T t : list1) {
            if (list2.contains(t)) {
                list.add(t);
            }
        }
        return list;
    }

    public int[] trimzeros(int[] array) {
        int len = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0) {
                len++;
            }
        }
        int[] newArray = new int[len];
        for (int i = 0, j = 0; i < array.length; i++) {
            if (array[i] != 0) {
                newArray[j] = array[i];
                j++;
            }
        }
        return newArray;
    }

    /*
     * public int[][] trimzeros(int[][] array){ int len = 0; for (int i=0;
     * i<array.length; i++){ for(int v=0;v<array[i].length;v++){ if (array[i][v]
     * != 0) len++; }
     *
     * } int [] newArray = new int[len]; for (int i=0, j=0; i<array.length;
     * i++){ if (array[i] != 0) { newArray[j] = array[i]; j++; } } return
     * newArray; }
     */
    public int[] findnonConfounders(int[] itemclass) {
        int[] ignoreList;
        int[] confounderList;
        int[] listcfdrelated;
        int[] nonconfounderList;
        // listcfdrelated =new int[singleList.numOfRule];

        listcfdrelated = new int[maxItem];
        confounderList = new int[singleList.numOfRule];
        nonconfounderList = new int[singleList.numOfRule];
        ignoreList = new int[singleList.numOfRule];
        int k;
        int[][] countercfd;
        int[] lMinSupcfd;
        double[] distcfd;
        // initial the counter
        countercfd = new int[4][];
        for (k = 0; k < 4; k++) {
            countercfd[k] = new int[maxItem + 2];
        }
        // initial parameter
        int ignoreind = 0;
        int i, j, item;
        int targetvalue = 0;
        lMinSupcfd = new int[4];
        distcfd = new double[4];
        RuleStru cur;
        RuleSet ruleset = new RuleSet();
        int[] rulesinsingleList;
        ruleset = singleList;
        cur = ruleset.ruleHead;

        // Take the rule list from singleList
        rulesinsingleList = new int[singleList.numOfRule];
        int z = 0;
        while (cur != null) {

            rulesinsingleList[z++] = cur.antecedent[0];
            cur = cur.nextRule;
        }

        // test
        // for(int m=0; m<rulesinsingleList.length; m++){
        // System.out.println("rules in singleList: "+rulesinsingleList[m]);
        // }
        // Reading and count the co-occurence between other items and class item
        for (i = 0; i < maxData; i++) {
            int search = 0;
            for (int x = 0; x < realAtt; x++) {
                for (int y = 0; y < itemclass.length; y++) {
                    if (dataSpace[i][x] == itemclass[y]) {
                        search = search + 1;
                    }
                }
            }
            if (search == itemclass.length) {

                countercfd[1][0]++;
                targetvalue = 1;
                // continue;
            }

            if (targetvalue == 0) {
                countercfd[0][0]++;
            }

            for (int l = 0; l < realAtt; l++) {
                item = dataSpace[i][l];
                // System.out.println("item:"+item);

                for (int x = 0; x < singleList.numOfRule; x++) {

                    if (rulesinsingleList[x] == item) {
                        countercfd[targetvalue][item]++;
                        countercfd[2][item]++;
                        // System.out.println("countercfd["+targetvalue+"]["+item+"]="+countercfd[targetvalue][item]);
                        // System.out.println("countercfd[2]["+item+"]="+countercfd[2][item]);
                    }
                }

            }
            targetvalue = 0;
        }

        // test print out countercfd
        /*
         * for (int x=0; x<countercfd.length; x++){ for (int y=0;
         * y<countercfd[x].length; y++){
         * System.out.println("countercfd["+x+"]["+y+"]="+countercfd[x][y]); } }
         */
        k = 0;
        // find the associations
        // 1. find the local min support. if an item local min support<< the
        // ignore

        int[] lmsupport;
        lmsupport = new int[2];
        lmsupport[0] = (int) (countercfd[0][0] * gsup + 0.5);
        lmsupport[1] = (int) (countercfd[1][0] * gsup + 0.5);
        // System.out.println("lmsupport[0]"+lmsupport[0]+"lmsupport[1]"+lmsupport[1]);
        for (int x = 1; x < maxItem + 2; x++) {
            // if(x==itemclass) continue;
            // global. We donot need global check as
            // countercfd[2][x]=counter[2][x], and x already pass global.
            if (countercfd[2][x] == 0) {
                continue;
            }
            // local
            if (countercfd[0][x] < lmsupport[0]) {
                ignoreList[ignoreind++] = x;
                continue;
            }
            if (countercfd[1][x] < lmsupport[1]) {
                ignoreList[ignoreind++] = x;
                continue;
            }
            // ignore A1, A2 in the combine variable A1A2. Do not break, as I
            // want to put A1, A2
            // into confounder list as well. This helps remove the item related
            // to A1, A2 out of the
            // non-confounder list. E.g if A1* is friend of A1 and we put A1*
            // into the control list
            // of A1A2 we will not find any record include both A1A2 and A1*.
            for (int q = 0; q < itemclass.length; q++) {
                if (x == itemclass[q]) {
                    ignoreList[ignoreind++] = x;
                }

            }
            // if (countercfd[2][x]<gMinSup) continue;
            // calculate the odds ratio
            double pc, pnc, p, npc, npnc, np, leftend, rightend;
            double oddsratio;
            p = countercfd[2][x];
            pc = countercfd[1][x];
            pnc = countercfd[0][x];
            npc = countercfd[1][0] - countercfd[1][x];
            npnc = maxData - countercfd[2][x] + countercfd[1][x]
                    - countercfd[1][0];
            // if the value==0 assign 1
            if (pnc == 0) {
                pnc = 1;
            }
            if (npc == 0) {
                npc = 1;
            }
            if (npnc == 0) {
                npnc = 1;
            }
            // System.out.println("item:"+x+" pc:"+pc+" pnc:"+pnc+" npc:"+npc+"npnc:"+npnc);
            oddsratio = (pc * npnc) / (pnc * npc);
            leftend = Math.exp(Math.log(oddsratio) - 1.96
                    * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));
            rightend = Math.exp(Math.log(oddsratio) + 1.96
                    * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));

            // if pass local supports and oddsratio>1.5 then ->confounderList
            // we need confounderList as items with the same attribute with item
            // in confounderList will be
            // removed out of the control list.
            if (leftend <= 1) {
                continue;
            }
            // if(oddsratio<1.5) continue;
            // System.out.println("item:"+x+"oddsratio="+oddsratio);

            confounderList[k++] = x;

        }

        // find confounderrelated list
        int indcfd = 0;
        for (int x = 0; x < rulesinsingleList.length; x++) {
            for (int y = 0; y < confounderList.length; y++) {
                if (confounderList[y] != 0
                        && itemRecord[(confounderList[y])].attName
                        .equals(itemRecord[rulesinsingleList[x]].attName)) {
                    // int check=0;
                    // //check if the new comer already exist
                    // for(int v=0; v<indcfd;v++){
                    // if(listcfdrelated[v]==rulesinsingleList[x]){
                    // check=1;
                    // System.out.println("don't add "+rulesinsingleList[x]+" as already exist");
                    // break;
                    // }
                    // }
                    // if(check==0){
                    // check if the rule is already in listcfdrelated if yes do
                    // not add.
                    // Arrays.sort(listcfdrelated);
                    // if(Arrays.binarySearch(listcfdrelated,
                    // rulesinsingleList[x])<0){

                    boolean found = false;
                    for (int p = 0; p < listcfdrelated.length; p++) {
                        if (listcfdrelated[p] == rulesinsingleList[x]) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {

                        listcfdrelated[indcfd++] = rulesinsingleList[x];

                        // System.out.println("listcfdrelated["+(indcfd-1)+"]="+listcfdrelated[indcfd-1]+" as confounder:"+confounderList[y]);
                    }
                }
            }

        }
        Arrays.sort(listcfdrelated);
        listcfdrelated = trimzeros(listcfdrelated);
        // System.out.println("listcfdrelated");
        // printRecord(listcfdrelated);
        // nonconfounderList=
        // Arrays.asList(rulesinsingleList).retainAll(listcfdrelated);
        // find the nonconfounder list=(ruleinsingleList \ listcfdrelated)\
        // ignoreList
        int noncfdind = 0;

        for (int x = 0; x < rulesinsingleList.length; x++) {
            // if(rulesinsingleList[x]==itemclass) continue;
            int flag = 0;
            // remove listcfdrelated
            for (int y = 0; y < listcfdrelated.length; y++) {
                if (rulesinsingleList[x] == listcfdrelated[y]) {
                    flag = 1;
                    break;
                }

            }
            // remove ignorelist
            for (int y = 0; y < ignoreList.length; y++) {
                if (rulesinsingleList[x] == ignoreList[y]) {
                    flag = 1;
                    break;
                }

            }

            if (flag == 0) {
                nonconfounderList[noncfdind++] = rulesinsingleList[x];
            }

        }
        nonconfounderList = trimzeros(nonconfounderList);

        // print
        // System.out.println("IgnoreList, cfdrealtedList, and controlList");
        // printRecord(trimzeros(ignoreList));
        // printRecord(trimzeros(listcfdrelated));
        // printRecord(nonconfounderList);
        /*
         * for (int x=0; x<nonconfounderList.length; x++){
         * System.out.println("nonconfounderList["+x+"]="+nonconfounderList[x]);
         * }
         */
        return nonconfounderList;

    }

    // find confounder variables of the item
    public int[] findnonConfounders(int itemclass) {
        int[] ignoreList;
        int[] confounderList;
        int[] listcfdrelated;
        int[] nonconfounderList;
        listcfdrelated = new int[maxItem];
        confounderList = new int[singleList.numOfRule];
        nonconfounderList = new int[singleList.numOfRule];
        ignoreList = new int[singleList.numOfRule];
        int k;
        int[][] countercfd;
        int[] lMinSupcfd;
        double[] distcfd;

        // initial the counter
        countercfd = new int[3][];
        for (k = 0; k < 3; k++) {
            countercfd[k] = new int[maxItem + 2];
        }

        // initial parameter
        int ignoreind = 0;
        int i, j, item;
        int targetvalue = 0;
        lMinSupcfd = new int[4];
        distcfd = new double[4];
        RuleStru cur;
        RuleSet ruleset = new RuleSet();
        int[] rulesinsingleList;
        ruleset = singleList;
        cur = ruleset.ruleHead;

        // Take the rule list from singleList
        rulesinsingleList = new int[singleList.numOfRule];
        int z = 0;
        while (cur != null) {

            rulesinsingleList[z++] = cur.antecedent[0];
            cur = cur.nextRule;
        }

        // test - use the comment button in netbean to turn on
        // for(int m=0; m<rulesinsingleList.length; m++){
        // System.out.println("findnonconfounders - rules in singleList: "+rulesinsingleList[m]);
        // }
        // Reading and count the co-occurence between other items and class item
        for (i = 0; i < maxData; i++) {
            for (j = 0; j < realAtt; j++) {
                // identify and count class item
                if (dataSpace[i][j] == itemclass) {
                    countercfd[1][0]++;
                    targetvalue = 1;
                    break;
                }
            }

            if (targetvalue == 0) {
                countercfd[0][0]++;
            }

            for (int l = 0; l < realAtt; l++) {
                item = dataSpace[i][l];
                // System.out.println("item:"+item);

                for (int x = 0; x < singleList.numOfRule; x++) {

                    if (rulesinsingleList[x] != itemclass
                            && rulesinsingleList[x] == item) {
                        countercfd[targetvalue][item]++;
                        countercfd[2][item]++;
                        // System.out.println("countercfd["+targetvalue+"]["+item+"]="+countercfd[targetvalue][item]);
                        // System.out.println("countercfd[2]["+item+"]="+countercfd[2][item]);
                    }
                }

            }
            targetvalue = 0;
        }

        // test print out countercfd
        // for (int x=0; x<countercfd.length; x++){
        // for (int y=0; y<countercfd[x].length; y++){
        // System.out.println("countercfd["+x+"]["+y+"]="+countercfd[x][y]);
        // }
        // }
        k = 0;
        // find the associations
        // 1. find the local min support. if an item local min support<< the
        // ignore

        int[] lmsupport;
        lmsupport = new int[2];
        lmsupport[0] = (int) (countercfd[0][0] * gsup + 0.5);
        lmsupport[1] = (int) (countercfd[1][0] * gsup + 0.5);
        // System.out.println("lmsupport[0]"+lmsupport[0]+"lmsupport[1]"+lmsupport[1]);
        for (int x = 1; x < maxItem + 2; x++) {
            if (x == itemclass) {
                continue;
            }
            // global. We donot need global check as
            // countercfd[2][x]=counter[2][x], and x already pass global.
            if (countercfd[2][x] == 0) {
                continue;
            }
            // local
            if (countercfd[0][x] < lmsupport[0]) {
                ignoreList[ignoreind++] = x;
                continue;
            }
            if (countercfd[1][x] < lmsupport[1]) {
                ignoreList[ignoreind++] = x;
                continue;
            }
            // if (countercfd[2][x]<gMinSup) continue;
            // calculate the odds ratio
            double pc, pnc, p, npc, npnc, np, leftend, rightend;
            double oddsratio;
            p = countercfd[2][x];
            pc = countercfd[1][x];
            pnc = countercfd[0][x];
            npc = countercfd[1][0] - countercfd[1][x];
            npnc = maxData - countercfd[2][x] + countercfd[1][x]
                    - countercfd[1][0];
            // if the value==0 assign 1
            if (pnc == 0) {
                pnc = 1;
            }
            if (npc == 0) {
                npc = 1;
            }
            if (npnc == 0) {
                npnc = 1;
            }
            // if(itemclass==57||itemclass==58)
            // System.out.println("item:"+x+" pc:"+pc+" pnc:"+pnc+" npc:"+npc+"npnc:"+npnc);
            oddsratio = (pc * npnc) / (pnc * npc);
            leftend = Math.exp(Math.log(oddsratio) - 1.96
                    * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));
            rightend = Math.exp(Math.log(oddsratio) + 1.96
                    * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));

            // if pass local supports and oddsratio>1.5 then ->confounderList
            // we need confounderList as items with the same attribute with item
            // in confounderList will be
            // removed out of the control list.
            if (leftend <= 1) {
                continue;
            }
            // if(oddsratio<1.5) continue;
            // if(itemclass==57||itemclass==58)
            // System.out.println("item:"+x+"oddsratio="+oddsratio);

            confounderList[k++] = x;

        }
        confounderList = trimzeros(confounderList);
        ignoreList = trimzeros(ignoreList);
        // print the confounder list, all the rule list
        // System.out.println("All rules");
        // printRecord(rulesinsingleList);
        // System.out.println("Confounders:");
        // printRecord(confounderList);
        // System.out.println("Ignores:");
        // printRecord(ignoreList);
        // find confounderrelated list
        int indcfd = 0;

        for (int x = 0; x < rulesinsingleList.length; x++) {
            for (int y = 0; y < confounderList.length; y++) {
                if (confounderList[y] != 0
                        && itemRecord[(confounderList[y])].attName
                        .equals(itemRecord[rulesinsingleList[x]].attName)) {
                    // sort the current listcfdrelated. This is needed for the
                    // Arrays.binarysearch holds
                    // Arrays.sort(listcfdrelated);
                    // System.out.println("listcfdrelated before:");
                    // printRecord(listcfdrelated);
                    // check if the new item is already in this list
                    boolean found = false;
                    for (int p = 0; p < listcfdrelated.length; p++) {
                        if (listcfdrelated[p] == rulesinsingleList[x]) {
                            found = true;
                            break;
                        }

                    }
                    if (!found) {
                        listcfdrelated[indcfd++] = rulesinsingleList[x];
                        // System.out.println("listcfdrelated["+(indcfd-1)+"]="+listcfdrelated[indcfd-1]+" as confounder:"+confounderList[y]);
                    }

                }
            }

        }

        // Arrays.sort(listcfdrelated);
        // listcfdrelated=trimzeros(listcfdrelated);
        // System.out.println("listcfdrelated");
        // printRecord(listcfdrelated);
        // nonconfounderList=
        // Arrays.asList(rulesinsingleList).retainAll(listcfdrelated);
        // find the nonconfounder list=(ruleinsingleList \ listcfdrelated)\
        // ignoreList
        int noncfdind = 0;

        for (int x = 0; x < rulesinsingleList.length; x++) {
            if (rulesinsingleList[x] == itemclass) {
                continue;
            }
            int flag = 0;
            // remove listcfdrelated
            for (int y = 0; y < listcfdrelated.length; y++) {
                if (rulesinsingleList[x] == listcfdrelated[y]) {
                    flag = 1;
                    break;
                }

            }
            // remove ignorelist
            for (int y = 0; y < ignoreList.length; y++) {
                if (rulesinsingleList[x] == ignoreList[y]) {
                    flag = 1;
                    break;
                }

            }

            if (flag == 0) {
                nonconfounderList[noncfdind++] = rulesinsingleList[x];
            }

        }
        nonconfounderList = trimzeros(nonconfounderList);
        // System.out.println("nonconfounderList");
        // printRecord(nonconfounderList);
        // //print
        // printRecord(trimzeros(ignoreList));
        // printRecord(trimzeros(listcfdrelated));
        // for (int x=0; x<nonconfounderList.length; x++){
        // System.out.println("nonconfounderList["+x+"]="+nonconfounderList[x]);
        // }

        return nonconfounderList;

    }

    public int[] friends(int item, int[] itemlist) {
        int[] friendslist;
        int frind = 0;
        friendslist = new int[itemlist.length];
        for (int x = 0; x < itemlist.length; x++) {
            // The same attribute name, different, and this item is in itemlist
            if (itemRecord[itemlist[x]].attName
                    .equals(itemRecord[item].attName) && itemlist[x] != item) {
                friendslist[frind++] = itemlist[x];
            }
        }

        // trim 0s
        friendslist = trimzeros(friendslist);

        /*
         * for(int y=0; y<friendslist.length; y++){
         * System.out.println("friends of "+item+" is "+friendslist[y]); }
         */
        return friendslist;
    }

    // Sort the array (dataspace)
    public static void sortArray(int myArray[][]) {
        Arrays.sort(myArray, new Comparator<int[]>() {

            @Override
            public int compare(int[] o1, int[] o2) {
                int r = 0;
                while (r < o1.length - 1) {
                    if (Integer.valueOf(o1[r])
                            .compareTo(Integer.valueOf(o2[r])) == 0) {
                        r = r + 1;
                    } else {
                        return Integer.valueOf(o1[r]).compareTo(
                                Integer.valueOf(o2[r]));
                    }
                }

                return Integer.valueOf(o1[r]).compareTo(Integer.valueOf(o2[r]));
            }

        });
    }

    public double log_2(int x) {
        return ((x) <= 0 ? 0.0 : Math.log(x) / Math.log(2));
    }

    /*
    void errorOut(int errnum) {
        switch (errnum) {

            case 0:
                printf("Can not Open File\n");
                break;

            case 1:
                printf("Not enough memory\n");
                break;

            case 11:
                printf("error in Filling the buff\n");
                break;

            case 12:
                printf("error in Reading Next Transaation\n");
                break;

        }
    }

    */

    // SHOULD NOT NEED THESE... SHOULD BE ABLE TO GET THESE FROM RULE
    /*
     * extern RULESET * RuleSet; extern long GMinSup; extern float MinConf;
     * extern ITEMSET * CurTranSet; extern int Ass; extern int Deriv; extern
     * TREENODE *AllSet; extern int Perfectrule;
     */

    /* this is for initial the Node Count and Weight count */
    public void initialCount() {
        int i;

        // zx, counter is a matrix(rows: (maxClass + 2), columns: (maxItem + 2))
        counter = new int[maxClass + 2][];

        for (i = 0; i < maxClass + 2; i++) {
            counter[i] = new int[maxItem + 2];
        }
        //System.out.println("maxItem"+maxItem);
    }

    /* this is free the memory the Count hold */
    public void freeCount() {
        counter = new int[0][0];
    }

    // beajy003 - return type changed to void as there are no return statements
    // in the function
    public void determineParameter(double suprate) {
        // System.out.println("!! beajy003 - Rule - determineParameter() - sup:"+suprate+": realAtt:"+realAtt+": ");
        int i, j, targetvalue, item;

        //System.out.println("enter determineParameter");

        maxTarget = 1;

        // zx, init parameter
        initParameter();

        //System.out.println("after determineParameter::initParamter()");


        // Firstly, reading the gobal frequency and local frequency
        // counter[0][item] stores the frequency of the item;
        // counter[target][item] stores the frequent of the item co-occuring
        // with the target
        // counter[target][0] stores the the frequency of the target

        // zx, dataSpace is a matrix(maxData * (realAtt+1))
        // zx, dataSpace[i][realAtt] corresponds to the value of target variable.
        // zx, counter is a matrix((maxClass + 2) * (maxItem + 2)).
        //hs.test System.out.println("realAtt="+realAtt); realAtt=49
        //System.out.println("realAtt="+realAtt);
        //System.out.println("maxData="+maxData);
        for (i = 0; i < maxData; i++) {
            // reading the class and count
            // zx, this is the value of target variable
            targetvalue = dataSpace[i][realAtt];
            // zx, count the total number of the specific type of target variable
            counter[targetvalue][0]++;

            // count the local and global freq of items
            for (j = 0; j < realAtt; j++) {
                //printf(" %d, ", dataSpace[i][j]);
                item = dataSpace[i][j];
                if (item == 0) {
                    continue;
                }

                // zx, counts of item specific target type
                // zx, I think this is a local frequncy of items.
                counter[targetvalue][item]++;

                // zx, total counts of global frequency of items.
                counter[maxClass][item]++;
            }
        }

        //System.out.println("first for completed");


        // Secondly, decide the parameter of conf and support
        //hs.test System.out.println("maxClass="+maxClass);  macClss==2
        for (i = 0; i < maxClass; i++) {

            lMinSup[i] = (int) (counter[i][0] * suprate + 0.5);
            dist[i] = counter[i][0];
            //System.out.println("dist["+i+"]:"+dist[i]);
            //System.out.println("\n!! - determinParameter() - class "+i+": dis["+i+"]=counter["+i+"][0]= "+counter[i][0]);
            //System.out.println("\n!! - determineParemeter() - lMinSup[class "+i+"]="+lMinSup[i]);
        }

        //System.out.println("second for completed");


        // System.out.println("hypothyroid=Counter[0][0]="+counter[0][0]+"; negative=counter[1][0]="+counter[1][0]);
        // System.out.println("lMinSup, hypothyroid ")
        // verify correctness of suport and confidence

        // System.out.println("\n!! beajy003 - Rule - determinParameter() - maxData:"+maxData+": maxClass:"+maxClass+": gMinSup:"+gMinSup+": min0:"+lMinSup[0]+": min1:"+lMinSup[1]+":");
        gMinSup = maxData;
        //printf("GMinSup = %d \n", gMinSup);
        for (i = 0; i < maxClass; i++) {
            if (lMinSup[i] < gMinSup) {
                gMinSup = lMinSup[i];
            }

        }
        printf("GMinSup = %d \n", gMinSup);
        //System.out.println("maxData="+maxData);


        for (i = 0; i < maxClass; i++) {
            //System.out.println("dist[i]="+(double) dist[i] );
            printf(" Distf[%d] = %f \n", i, (double) dist[i] / maxData);
        }

        //System.out.println("third, fourth for completed");
    }

    public int initParameter() {
        lMinSup = new int[maxClass + 2];
        dist = new double[maxClass + 2];

        return 1;
    }

    public int initSecondParameter() {
        lMinSup = new int[maxClass + 2];
        seconddist = new double[maxClass + 2];

        return 1;
    }

    // Init the candidate tree, a whole tree
    // beajy003 - return type changed to void as there are no return statements
    // in the function
    public void initWholeTree(PrefixTree tree) {
        int i, j, k, m, num, item;
        PrefixTree cur;
        PrefixTree cur1;
        PrefixTree cur2;

        treeSize = 0;
        singleRule = 0;
        multiRule = 0;

        // zx, here is the root node of prefix tree
        tree.numOfSon = 0;
        tree.father = null;
        tree.nodeID = -1;
        tree.len = 0;
        tree.gSup = maxData;
        tree.conf = 0;
        tree.acc = 0;

        tree.sonList = new PrefixTree[maxItem + 1];
        tree.sonList1 = new PrefixTree[maxItem + 1];
        tree.memForSon = maxItem + 1;

        // zx, initialize each of sonList as null.
        for (i = 0; i < maxItem + 1; i++) {
            tree.sonList[i] = null;
            tree.sonList1[i] = null;

        }

        // first layer tree
        tree.numOfSon = 0;
        tree.numOfSon1 = 0;
        //System.out.println("non frequent set is:");
        // System.out.println("!! beajy003 - Rule - initWholeTree() - max:"+maxItem+": gMinSup:"+gMinSup+":");
        for (i = 1; i < maxItem + 1; i++) {
            // zx, original version, if (counter[maxClass][i] > gMinSup) {
            // zx, CR-PA, test if each individual predictive variable is frequent.

            //System.out.println("counter[maxClass][i] / maxData = " + (double)counter[maxClass][i] / (double)maxData);
            tree.sonList1[tree.numOfSon1++] = newNode(tree, i);
            //System.out.println("frequent1:"+i);
            if (((double) counter[maxClass][i] / (double) maxData) >= t) {

                // System.out.println("include item "+i+" as counter[maxClass]["+i+"]="+counter[maxClass][i]);
                //tree.sonList1[tree.numOfSon1++] = newNode(tree, i);
                tree.sonList[tree.numOfSon++] = newNode(tree, i);
                //tree.sonList1[tree.numOfSon1++] = newNode(tree, i);
                tree.sonList1[tree.numOfSon1 - 1].issupport = 1;
                // System.out.println("number of Son:"+tree.numOfSon);
                System.out.println("frequent2:" + i);
            } else {
                //tree.sonList1[tree.numOfSon1++] = newNode(tree, i);
                System.out.println(tree.sonList1[tree.numOfSon1 - 1]);
                tree.sonList1[tree.numOfSon1 - 1].issupport = 0;
                //tree.sonList1[tree.numOfSon1-1]=null;

                //System.out.println(i);
            }
        }


        //System.out.println("tree.numOfSon = " + tree.numOfSon);


        // zx, tree.numOfSon dose not include root node.
        //hs, this is for user control
        if (ChosenTest.length == 1) {
            //System.out.println("ChosenTest[0]="+ChosenTest[0]);
            System.out.println("min=" + dataSpacestat[ChosenTest[0] - 1].min);
            System.out.println("max=" + dataSpacestat[ChosenTest[0] - 1].max);
            for (i = dataSpacestat[ChosenTest[0] - 1].min; i <= dataSpacestat[ChosenTest[0] - 1].max; i++) {
                if (tree.sonList1[i - 1].issupport == 1) {
                    cur1 = tree.sonList1[i - 1];
                    System.out.println("number of Son" + tree.numOfSon1);
                    System.out.println("curnode: " + cur1.nodeID);


                    if (method == 3) {
                        // this function will judge if the node is positively associated with target variable.
                        if (chooseMethod == 1) {
                            ruleTestWriteforAssociation_CRPA(cur1);

                        } else {
                            ruleTestWrite_CRPA(cur1);

                        }


                        //System.out.println("cur.nodeID = " + cur.nodeID + ", cur.isfrequent = " + cur.isfrequent);
                        //System.out.println("cur.ispa = " + cur.ispa);
                        //System.out.println("cur.gSup = " + cur.gSup + "cur.lSup[1] = " + cur.lSup[1]);


                        // if rule is formed, then write to SingleList
                        // Single list keeps rules with sirulesngle attribute
                        // for finding sub rules (when Sub flag is on)
                        // there rules have been written in RuleSet already.

                        //System.out.println("ID:"+cur.nodeID+" "+"state:"+cur.ispa+" "+"token:"+cur.token);

                        //hs. if is for output in UI.
                        if (cur1.token > 0) {
                            //System.out.println("hushu");
                            //System.out.println("candidates for causal rule node.nodeID="+cur.nodeID);
                            //writeToRuleSet_CRPA(cur, 1);
                            writeToRuleSet_CRPA(cur1, 0);
                            writeToRuleSet_CRPA(cur1, 1);
                        }

                    }

                } else {
                    tree.sonList1[i - 1].ispa = 0;
                }
            }

        } else if (ChosenTest.length >= 2) {
            System.out.println("ChosenTest[0]=" + ChosenTest[0]);
            System.out.println("min=" + dataSpacestat[ChosenTest[0] - 1].min);
            System.out.println("max=" + dataSpacestat[ChosenTest[0] - 1].max);

            for (m = 0; m < ChosenTest.length; m++) {
                //System.out.println("do3");
                //for(i=dataSpacestat[ChosenTest[0]-1].min;i<=dataSpacestat[ChosenTest[0]-1].max;i++){
                for (i = dataSpacestat[ChosenTest[m] - 1].min; i <= dataSpacestat[ChosenTest[m] - 1].max; i++) {
                    if (tree.sonList1[i - 1].issupport == 1) {
                        cur1 = tree.sonList1[i - 1];
                        System.out.println("number of Son" + tree.numOfSon1);
                        System.out.println("curnode: " + cur1.nodeID);

                        if (method == 3) {
                            // this function will judge if the node is positively associated with target variable.
                            if (chooseMethod == 1) {
                                ruleTestWriteforAssociation_CRPA(cur1);

                            } else {
                                ruleTestWrite_CRPA(cur1);


                            }


                            //System.out.println("cur.nodeID = " + cur.nodeID + ", cur.isfrequent = " + cur.isfrequent);
                            //System.out.println("cur.ispa = " + cur.ispa);
                            //System.out.println("cur.gSup = " + cur.gSup + "cur.lSup[1] = " + cur.lSup[1]);


                            // if rule is formed, then write to SingleList
                            // Single list keeps rules with sirulesngle attribute
                            // for finding sub rules (when Sub flag is on)
                            // there rules have been written in RuleSet already.

                            //System.out.println("ID:"+cur.nodeID+" "+"state:"+cur.ispa+" "+"token:"+cur.token);

                            //hs. if is for output in UI.
                            if (cur1.token > 0) {
                                //System.out.println("hushu");
                                //System.out.println("candidates for causal rule node.nodeID="+cur.nodeID);
                                //writeToRuleSet_CRPA(cur, 1);
                                writeToRuleSet_CRPA(cur1, 0);
                                writeToRuleSet_CRPA(cur1, 1);
                            }


                        }

                    } else {
                        tree.sonList1[i - 1].ispa = 0;
                    }
                }
            }
            /*
            System.out.println("ChosenTest[1]="+ChosenTest[1]);
            System.out.println("min="+dataSpacestat[ChosenTest[1]-1].min);
            System.out.println("max="+dataSpacestat[ChosenTest[1]-1].max);
            for(i=dataSpacestat[ChosenTest[1]-1].min;i<=dataSpacestat[ChosenTest[1]-1].max;i++){
                cur2 = tree.sonList1[i-1];
                System.out.println("number of Son"+tree.numOfSon1);
                System.out.println("curnode: "+cur2.nodeID);

            if(method == 3)
            {
                // this function will judge if the node is positively associated with target variable.
                if(chooseMethod==1){
                    ruleTestWriteforAssociation_CRPA(cur2);

                }else{
                    ruleTestWrite_CRPA(cur2);


                }


                //System.out.println("cur.nodeID = " + cur.nodeID + ", cur.isfrequent = " + cur.isfrequent);
                //System.out.println("cur.ispa = " + cur.ispa);
                //System.out.println("cur.gSup = " + cur.gSup + "cur.lSup[1] = " + cur.lSup[1]);


                // if rule is formed, then write to SingleList
                // Single list keeps rules with sirulesngle attribute
                // for finding sub rules (when Sub flag is on)
                // there rules have been written in RuleSet already.

                //System.out.println("ID:"+cur.nodeID+" "+"state:"+cur.ispa+" "+"token:"+cur.token);

                //hs. if is for output in UI.
                if (cur2.token > 0) {
                        //System.out.println("hushu");
                        //System.out.println("candidates for causal rule node.nodeID="+cur.nodeID);
                    //writeToRuleSet_CRPA(cur, 1);
                    writeToRuleSet_CRPA(cur2, 0);
                    writeToRuleSet_CRPA(cur2, 1);
                }


            }

            }
            */


        } else if (ChosenTest.length == 0) {
            for (i = 0; i < tree.numOfSon; i++) {
                cur = tree.sonList[i];
                System.out.println("number of Son" + tree.numOfSon);
                System.out.println("curnode: " + cur.nodeID);
                // FormingRule(cur);

                // This procedure is for exclusiveness
                //zx, original version, if (excl != 0) {
                if (method == 1) {
                    // System.out.println("excl= "+excl);
                    //ruleTestWrite(cur);
                    // if rule is formed, then write to SingleList
                    // Single list keeps rules with single attribute
                    // for finding sub rules (when Sub flag is on)
                    // there rules have been written in RuleSet already.

                    if (cur.token > 0) {
                        writeToRuleSet(cur, 1);
                    }
                } // This procedure is for confidence and accuracy
                // zx, orignial version, else {
                else if (method == 2) {
                    // zx, this is a tranditional method for judging if it is an association rule.
                    // zx, Traditional association rules are deﬁned by support and conﬁdence
                    if (formingRule(cur) != 0) {
                        writeToRuleSet(cur, 0);
                    }
                } else if (method == 3) {
                    // this function will judge if the node is positively associated with target variable.
                    if (chooseMethod == 1) {
                        ruleTestWriteforAssociation_CRPA(cur);

                    } else {
                        ruleTestWrite_CRPA(cur);

                    }


                    //System.out.println("cur.nodeID = " + cur.nodeID + ", cur.isfrequent = " + cur.isfrequent);
                    //System.out.println("cur.ispa = " + cur.ispa);
                    //System.out.println("cur.gSup = " + cur.gSup + "cur.lSup[1] = " + cur.lSup[1]);


                    // if rule is formed, then write to SingleList
                    // Single list keeps rules with sirulesngle attribute
                    // for finding sub rules (when Sub flag is on)
                    // there rules have been written in RuleSet already.

                    //System.out.println("ID:"+cur.nodeID+" "+"state:"+cur.ispa+" "+"token:"+cur.token);

                    //hs. if is for output in UI.
                    if (cur.token > 0) {
                        //System.out.println("hushu");
                        //System.out.println("candidates for causal rule node.nodeID="+cur.nodeID);
                        //writeToRuleSet_CRPA(cur, 1);
                        writeToRuleSet_CRPA(cur, 0);
                        writeToRuleSet_CRPA(cur, 1);
                    }

                } else {


                }

            }
        }
        // System.out.println("hi");
        // System.out.println("rules in ruleSet: "+ruleSet.numOfRule);
        // System.out.println("rules in singleList: "+singleList.numOfRule);

        freeCount();

    }

    // zx, add, getting the maximum value
    public static int getMaxValue(int[] array) {
        int maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];

            }
        }

        return maxValue;
    }

    // zx, add, getting the miniumum value
    public static int getMinValue(int[] array) {
        int i;
        int minValue = array[0];

        for (i = 1; i < array.length; i++) {
            if (array[i] != 0) {
                // set the first non-zero value as minimal value
                minValue = array[i];
                break;
            }
        }

        for (i = 1; i < array.length; i++) {
            if (array[i] < minValue && array[i] != 0) {
                // There is a condition that the specific column and row space does not contain any value.
                // So, zero shoule be removed and can not be minimum value.
                minValue = array[i];
            }
        }

        return minValue;
    }

    public column_object add_column_object(int min, int max, int numbers) {
        column_object co = new column_object();

        co.min = min;           // minimal value
        co.max = max;           // maximal value
        co.numbers = numbers;   // total number of candidate values

        return co;
    }

    // zx, add, init Column Object table
    public void initCOtable() {
        int i, j, max, min;
        int[] specified_column = new int[maxData];
        //System.out.println("maxData"+maxData);

        dataSpacestat = new column_object[realAtt];

        /*
        System.out.println("---------dataSpace in initDBtable --------");
        for(i = 0; i < maxData; i++)
        {
            for(j = 0; j < realAtt; j++)
            {
                System.out.print(dataSpace[i][j] + " ");
            }

            System.out.println();
        }
        */


        //System.out.println("---------statistic --------");
        //System.out.println("min  max  number");
        for (j = 0; j < realAtt; j++) {
            for (i = 0; i < maxData; i++) {
                specified_column[i] = dataSpace[i][j];
                //hs. System.out.println("specified_column["+i+"]="+specified_column[i]);
            }

            min = getMinValue(specified_column);

            max = getMaxValue(specified_column);


            //dataSpacestat[j].min = min;
            //dataSpacestat[j].max = max;
            //dataSpacestat[j].numbers = (max - min + 1);

            dataSpacestat[j] = add_column_object(min, max, (max - min + 1));

            //System.out.print(dataSpacestat[j].min + " " + dataSpacestat[j].max + " " + dataSpacestat[j].numbers);


            //System.out.println();
        }

    }
    //hs.start

    public int candidateGen_Two(PrefixTree tree, int layer) {
        int i, j, k, l, p, q, flag, numofsonbak, att, sum = 10;
        int[] settmp = new int[100];
        int[] jointset = new int[100];
        double rate;
        PrefixTree cur, tmp1, tmp2;
        PrefixTree[] subnodeptr = new PrefixTree[100];

        flag = 0;
        if (tree == null) {
            return flag;
        }

        if (ruleSet.numOfRule > maxRuleAllowed) {
            return flag;
        }

        //System.out.println("current tree.len="+tree.len);
        // allSet.len=tree.len=0; layer=2 to pass on
        //displayTree(tree);
        if (tree.len == (layer - 2)) {
            // System.out.println("sum="+sum);
            //numofsonbak = tree.numOfSon1;
            //System.out.println("length="+tree.sonList1.length);
            //System.out.println("numofsonbak="+numofsonbak); numofsonbak==the number of node in first level
            //hs. the follow is to choose the the ID of state==2.
            if (layer <= 2) {
                numofsonbak = tree.numOfSon1;
                for (int m = 0; m < ChosenTest.length; m++) {
                    System.out.println("m_inistall=" + m);
                    for (i = dataSpacestat[ChosenTest[m] - 1].min; i <= dataSpacestat[ChosenTest[m] - 1].max; i++) {
                        if (i <= (dataSpacestat[ChosenTest[ChosenTest.length - 1] - 1].max - 1)) {

                            //System.out.println("tree.sonList1="+tree.len);
                            //System.out.println("level3="+tree.sonList1[i-1]);
                            tmp1 = tree.sonList1[i - 1];


                            System.out.println("hahatmp1.nodeID=" + tmp1.nodeID);
                            //System.out.println("i="+i);

                            //System.out.println("tmp1.len="+ tmp1.len);
                            if (tmp1 == null) {
                                //System.out.println("temp1 is null");
                                continue;
                            }

                            if (tree.sonList1[i - 1].issupport == 0) {
                                continue;
                            }

                            // zx, the following code section is redundant.
                            if (tmp1.token >= 2) {
                                // zx, we won't consider
                                //System.out.println("token:"+tmp1.token);
                                continue;
                            }

                            // only consider those node which are not positive association.
                            //hs.add
                            //System.out.println(tmp1.ispa);


//                if(tmp1.ispa != 2)
//                {
//                    continue;
//                }


                            //hs.end


                            tmp1.numOfSon = 0;
                            //System.out.println("temp len="+tmp1.len);
                            for (k = 0; k < tmp1.len; k++) {

                                settmp[k] = tmp1.set[k];
                                //System.out.println("settmp["+k+"]="+tmp1.set[k]);
                                //hs. the result is display the ID of state==2
                            }
                            if (tmp1 != tree.sonList1[dataSpacestat[ChosenTest[m] - 1].max - 1]) {
                                for (j = i + 1; j <= dataSpacestat[ChosenTest[m] - 1].max; j++) {
                                    if (tree.sonList1[j - 1].issupport == 1) {
                                        tmp2 = tree.sonList1[j - 1];
                                        //System.out.println("hahatmp2.nodeID="+tmp2.nodeID);
                                        //System.out.println("tmp2.len="+ tmp2.len);
                                        //System.out.println("j="+j);

                                        if (tmp2 == null) {
                                            // System.out.println("the item is null");
                                            continue;
                                        }

                                        // zx, the following code section is redundant.
                                        if (tmp2.token >= 2) {
                                            // System.out.println("tmp2.token="+tmp2.token);
                                            continue;
                                        }

                                        // only consider those node which are not positive association.
                                        //hs.add

//                    if(tmp2.ispa != 2)
//                    {
//                        continue;
//                    }


                                        //hs.end

                                        settmp[tmp1.len] = tmp2.nodeID;
                                        //System.out.println("next layer: "+tmp2.nodeID);
                                        //System.out.println("settmp["+k+"]="+tmp2.nodeID);

                                        // added by zx, start
                                        int subgroup_length = 0;

                                        for (l = 0; l < settmp.length; l++) {
                                            if (settmp[l] != 0) {
                                                subgroup_length++;
                                            }
                                        }

                                        //System.out.println("subgroup_length="+subgroup_length);

                                        int[] index_in_dataspace = new int[subgroup_length];

                                        for (l = 0; l < subgroup_length; l++) {
                                            for (k = 0; k < realAtt; k++) {
                                                if (dataSpacestat[k].min <= settmp[l] && dataSpacestat[k].max >= settmp[l]) {
                                                    index_in_dataspace[l] = k;

                                                    break;
                                                }
                                            }
                                        }

                                        int count = 0;  // number of exposure variables of value of 1.
                                        for (l = 0; l < maxData; l++) {
                                            int num_exposure_v = 0;

                                            for (k = 0; k < subgroup_length; k++) {
                                                if (dataSpace[l][index_in_dataspace[k]] == settmp[k]) {
                                                    num_exposure_v++;
                                                }
                                            }

                                            if (subgroup_length == num_exposure_v) {
                                                count++;
                                            }
                                        }

//                    for(k = 0; k < subgroup_length; k++)
//                    {
//                        System.out.print(settmp[k]+" ");
//                    }
//                    System.out.println();
//
//                    System.out.println("count="+count);
//                    System.out.println("maxData="+maxData);


                                        if ((double) count / (double) maxData < t) {
                                            continue;
                                        }


                                        // added by zx, end


//                    // ok so far
//                    for (k = 0; k < maxClass; k++) {
//
//
//                        if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
//                            jointset[k] = 1;
//                            // System.out.println("Yes. jointset["+k+"]="+jointset[k]);
//                        } else {
//                            jointset[k] = 0;
//                            // System.out.println("No. jointset["+k+"]="+jointset[k]);
//                        }
//
//
//                        //jointset[k] = 1;
//                    }
//
//                    for (k = 0; k < maxClass; k++) {
//                        sum += jointset[k];
//                    }
//                    // System.out.println("sum="+sum);
//                    if (sum == 0) {
//
//                        // System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

//                    // zx comment for CRPA
//                    if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
//                        // System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

                                        tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                                        System.out.println("father: " + tmp1.nodeID
                                                + " - Children: " + tmp1.sonList[tmp1.numOfSon - 1].nodeID);
                                        System.out.append("\n");
                                        //System.out.println("I will display");
                                        //System.out.println("i="+i);
                                        //System.out.println( tree.sonList[i-2].nodeID);
                                        tree.sonList[i - 1] = tmp1;
                                        //System.out.println("2"+ tree.sonList[i-2].nodeID);
                                        //displayTree(tree);
                                        flag = 1;
                                        // transfer Thuc: backup, create the new one, put the backup
                                        // back
                                        PrefixTree[] transfer;
                                        transfer = tmp1.sonList;


                                        if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
                                            //System.out.println("the number of son is big, current:"+tmp1.memForSon);
                                            tmp1.memForSon += CYCLE;
                                            //System.out.println("after adding: "+tmp1.memForSon);
                                            tmp1.sonList = new PrefixTree[tmp1.memForSon];
                                            // putback
                                            System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                                        }

                                        //System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                                    }
                                }
                                for (int n = m + 1; n < ChosenTest.length; n++) {
                                    System.out.println("m=" + m);
                                    System.out.println("n=" + n);
                                    for (j = dataSpacestat[ChosenTest[n] - 1].min; j <= dataSpacestat[ChosenTest[n] - 1].max; j++) {
                                        if (tree.sonList1[j - 1].issupport == 1) {
                                            tmp2 = tree.sonList1[j - 1];
                                            //System.out.println("hahatmp2.nodeID="+tmp2.nodeID);
                                            //System.out.println("tmp2.len="+ tmp2.len);
                                            //System.out.println("j="+j);

                                            if (tmp2 == null) {
                                                // System.out.println("the item is null");
                                                continue;
                                            }

                                            // zx, the following code section is redundant.
                                            if (tmp2.token >= 2) {
                                                // System.out.println("tmp2.token="+tmp2.token);
                                                continue;
                                            }

                                            // only consider those node which are not positive association.
                                            //hs.add

//                    if(tmp2.ispa != 2)
//                    {
//                        continue;
//                    }


                                            //hs.end

                                            settmp[tmp1.len] = tmp2.nodeID;
                                            //System.out.println("next layer: "+tmp2.nodeID);
                                            //System.out.println("settmp["+k+"]="+tmp2.nodeID);

                                            // added by zx, start
                                            int subgroup_length = 0;

                                            for (l = 0; l < settmp.length; l++) {
                                                if (settmp[l] != 0) {
                                                    subgroup_length++;
                                                }
                                            }

                                            //System.out.println("subgroup_length="+subgroup_length);

                                            int[] index_in_dataspace = new int[subgroup_length];

                                            for (l = 0; l < subgroup_length; l++) {
                                                for (k = 0; k < realAtt; k++) {
                                                    if (dataSpacestat[k].min <= settmp[l] && dataSpacestat[k].max >= settmp[l]) {
                                                        index_in_dataspace[l] = k;

                                                        break;
                                                    }
                                                }
                                            }

                                            int count = 0;  // number of exposure variables of value of 1.
                                            for (l = 0; l < maxData; l++) {
                                                int num_exposure_v = 0;

                                                for (k = 0; k < subgroup_length; k++) {
                                                    if (dataSpace[l][index_in_dataspace[k]] == settmp[k]) {
                                                        num_exposure_v++;
                                                    }
                                                }

                                                if (subgroup_length == num_exposure_v) {
                                                    count++;
                                                }
                                            }

//                    for(k = 0; k < subgroup_length; k++)
//                    {
//                        System.out.print(settmp[k]+" ");
//                    }
//                    System.out.println();
//
//                    System.out.println("count="+count);
//                    System.out.println("maxData="+maxData);


                                            if ((double) count / (double) maxData < t) {
                                                continue;
                                            }


                                            // added by zx, end


//                    // ok so far
//                    for (k = 0; k < maxClass; k++) {
//
//
//                        if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
//                            jointset[k] = 1;
//                            // System.out.println("Yes. jointset["+k+"]="+jointset[k]);
//                        } else {
//                            jointset[k] = 0;
//                            // System.out.println("No. jointset["+k+"]="+jointset[k]);
//                        }
//
//
//                        //jointset[k] = 1;
//                    }
//
//                    for (k = 0; k < maxClass; k++) {
//                        sum += jointset[k];
//                    }
//                    // System.out.println("sum="+sum);
//                    if (sum == 0) {
//
//                        // System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

//                    // zx comment for CRPA
//                    if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
//                        // System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

                                            tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                                            System.out.println("father: " + tmp1.nodeID
                                                    + " - Children: " + tmp1.sonList[tmp1.numOfSon - 1].nodeID);
                                            System.out.append("\n");
                                            //System.out.println("I will display");
                                            //System.out.println("i="+i);
                                            //System.out.println( tree.sonList[i-2].nodeID);
                                            tree.sonList[i - 1] = tmp1;
                                            //System.out.println("2"+ tree.sonList[i-2].nodeID);
                                            //displayTree(tree);
                                            flag = 1;
                                            // transfer Thuc: backup, create the new one, put the backup
                                            // back
                                            PrefixTree[] transfer;
                                            transfer = tmp1.sonList;


                                            if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
                                                //System.out.println("the number of son is big, current:"+tmp1.memForSon);
                                                tmp1.memForSon += CYCLE;
                                                //System.out.println("after adding: "+tmp1.memForSon);
                                                tmp1.sonList = new PrefixTree[tmp1.memForSon];
                                                // putback
                                                System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                                            }

                                            //System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                                        }
                                    }

                                }


                            } else {
                                for (int n = m + 1; n < ChosenTest.length; n++) {
                                    System.out.println("m=" + m);
                                    System.out.println("n=" + n);
                                    for (j = dataSpacestat[ChosenTest[n] - 1].min; j <= dataSpacestat[ChosenTest[n] - 1].max; j++) {
                                        if (tree.sonList1[j - 1].issupport == 1) {
                                            tmp2 = tree.sonList1[j - 1];
                                            //System.out.println("hahatmp2.nodeID="+tmp2.nodeID);
                                            //System.out.println("tmp2.len="+ tmp2.len);
                                            //System.out.println("j="+j);

                                            if (tmp2 == null) {
                                                // System.out.println("the item is null");
                                                continue;
                                            }

                                            // zx, the following code section is redundant.
                                            if (tmp2.token >= 2) {
                                                // System.out.println("tmp2.token="+tmp2.token);
                                                continue;
                                            }

                                            // only consider those node which are not positive association.
                                            //hs.add

//                    if(tmp2.ispa != 2)
//                    {
//                        continue;
//                    }


                                            //hs.end

                                            settmp[tmp1.len] = tmp2.nodeID;
                                            //System.out.println("next layer: "+tmp2.nodeID);
                                            //System.out.println("settmp["+k+"]="+tmp2.nodeID);

                                            // added by zx, start
                                            int subgroup_length = 0;

                                            for (l = 0; l < settmp.length; l++) {
                                                if (settmp[l] != 0) {
                                                    subgroup_length++;
                                                }
                                            }

                                            //System.out.println("subgroup_length="+subgroup_length);

                                            int[] index_in_dataspace = new int[subgroup_length];

                                            for (l = 0; l < subgroup_length; l++) {
                                                for (k = 0; k < realAtt; k++) {
                                                    if (dataSpacestat[k].min <= settmp[l] && dataSpacestat[k].max >= settmp[l]) {
                                                        index_in_dataspace[l] = k;

                                                        break;
                                                    }
                                                }
                                            }

                                            int count = 0;  // number of exposure variables of value of 1.
                                            for (l = 0; l < maxData; l++) {
                                                int num_exposure_v = 0;

                                                for (k = 0; k < subgroup_length; k++) {
                                                    if (dataSpace[l][index_in_dataspace[k]] == settmp[k]) {
                                                        num_exposure_v++;
                                                    }
                                                }

                                                if (subgroup_length == num_exposure_v) {
                                                    count++;
                                                }
                                            }

//                    for(k = 0; k < subgroup_length; k++)
//                    {
//                        System.out.print(settmp[k]+" ");
//                    }
//                    System.out.println();
//
//                    System.out.println("count="+count);
//                    System.out.println("maxData="+maxData);


                                            if ((double) count / (double) maxData < t) {
                                                continue;
                                            }


                                            // added by zx, end


//                    // ok so far
//                    for (k = 0; k < maxClass; k++) {
//
//
//                        if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
//                            jointset[k] = 1;
//                            // System.out.println("Yes. jointset["+k+"]="+jointset[k]);
//                        } else {
//                            jointset[k] = 0;
//                            // System.out.println("No. jointset["+k+"]="+jointset[k]);
//                        }
//
//
//                        //jointset[k] = 1;
//                    }
//
//                    for (k = 0; k < maxClass; k++) {
//                        sum += jointset[k];
//                    }
//                    // System.out.println("sum="+sum);
//                    if (sum == 0) {
//
//                        // System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

//                    // zx comment for CRPA
//                    if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
//                        // System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

                                            tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                                            System.out.println("father: " + tmp1.nodeID
                                                    + " - Children: " + tmp1.sonList[tmp1.numOfSon - 1].nodeID);
                                            System.out.append("\n");
                                            //System.out.println("I will display");
                                            //System.out.println("i="+i);
                                            //System.out.println( tree.sonList[i-2].nodeID);
                                            tree.sonList[i - 1] = tmp1;
                                            //System.out.println("2"+ tree.sonList[i-2].nodeID);
                                            //displayTree(tree);
                                            flag = 1;
                                            // transfer Thuc: backup, create the new one, put the backup
                                            // back
                                            PrefixTree[] transfer;
                                            transfer = tmp1.sonList;


                                            if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
                                                //System.out.println("the number of son is big, current:"+tmp1.memForSon);
                                                tmp1.memForSon += CYCLE;
                                                //System.out.println("after adding: "+tmp1.memForSon);
                                                tmp1.sonList = new PrefixTree[tmp1.memForSon];
                                                // putback
                                                System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                                            }

                                            //System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                                        }
                                    }
                                }
                            }


                        }
                    }
                }
            } else {
                numofsonbak = tree.numOfSon;
                System.out.println("hushu");
                for (i = 0; i < numofsonbak - 1; i++) {
                    tmp1 = tree.sonList[i];
                    System.out.println("tmp1.nodeID=" + tmp1.nodeID);

                    //System.out.println("i="+i);

                    //System.out.println("tmp1.len="+ tmp1.len);
                    if (tmp1 == null) {
                        //System.out.println("temp1 is null");
                        continue;
                    }

                    // zx, the following code section is redundant.
                    if (tmp1.token >= 2) {
                        // zx, we won't consider
                        //System.out.println("token:"+tmp1.token);
                        continue;
                    }

                    // only consider those node which are not positive association.
                    //hs.add
                    //System.out.println(tmp1.ispa);


//                if(tmp1.ispa != 2)
//                {
//                    continue;
//                }


                    //hs.end


                    tmp1.numOfSon = 0;
                    //System.out.println("temp len="+tmp1.len);
                    for (k = 0; k < tmp1.len; k++) {

                        settmp[k] = tmp1.set[k];
                        System.out.println("settmp[" + k + "]=" + tmp1.set[k]);
                        //hs. the result is display the ID of state==2
                    }
                    for (j = i + 1; j < numofsonbak; j++) {

                        tmp2 = tree.sonList[j];
                        System.out.println("tmp2.nodeID=" + tmp2.nodeID);
                        //System.out.println("tmp2.len="+ tmp2.len);
                        //System.out.println("j="+j);

                        if (tmp2 == null) {
                            // System.out.println("the item is null");
                            continue;
                        }

                        // zx, the following code section is redundant.
                        if (tmp2.token >= 2) {
                            // System.out.println("tmp2.token="+tmp2.token);
                            continue;
                        }

                        // only consider those node which are not positive association.
                        //hs.add

//                    if(tmp2.ispa != 2)
//                    {
//                        continue;
//                    }


                        //hs.end

                        settmp[tmp1.len] = tmp2.nodeID;
                        //System.out.println("next layer: "+tmp2.nodeID);
                        //System.out.println("settmp["+k+"]="+tmp2.nodeID);

                        // added by zx, start
                        int subgroup_length = 0;

                        for (l = 0; l < settmp.length; l++) {
                            if (settmp[l] != 0) {
                                subgroup_length++;
                            }
                        }

                        //System.out.println("subgroup_length="+subgroup_length);

                        int[] index_in_dataspace = new int[subgroup_length];

                        for (l = 0; l < subgroup_length; l++) {
                            for (k = 0; k < realAtt; k++) {
                                if (dataSpacestat[k].min <= settmp[l] && dataSpacestat[k].max >= settmp[l]) {
                                    index_in_dataspace[l] = k;

                                    break;
                                }
                            }
                        }

                        int count = 0;  // number of exposure variables of value of 1.
                        for (l = 0; l < maxData; l++) {
                            int num_exposure_v = 0;

                            for (k = 0; k < subgroup_length; k++) {
                                if (dataSpace[l][index_in_dataspace[k]] == settmp[k]) {
                                    num_exposure_v++;
                                }
                            }

                            if (subgroup_length == num_exposure_v) {
                                count++;
                            }
                        }

//                    for(k = 0; k < subgroup_length; k++)
//                    {
//                        System.out.print(settmp[k]+" ");
//                    }
//                    System.out.println();
//
//                    System.out.println("count="+count);
//                    System.out.println("maxData="+maxData);


                        if ((double) count / (double) maxData < t) {
                            continue;
                        }


                        // added by zx, end


//                    // ok so far
//                    for (k = 0; k < maxClass; k++) {
//
//
//                        if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
//                            jointset[k] = 1;
//                            // System.out.println("Yes. jointset["+k+"]="+jointset[k]);
//                        } else {
//                            jointset[k] = 0;
//                            // System.out.println("No. jointset["+k+"]="+jointset[k]);
//                        }
//
//
//                        //jointset[k] = 1;
//                    }
//
//                    for (k = 0; k < maxClass; k++) {
//                        sum += jointset[k];
//                    }
//                    // System.out.println("sum="+sum);
//                    if (sum == 0) {
//
//                        // System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

//                    // zx comment for CRPA
//                    if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
//                        // System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

                        tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                        System.out.println("father: " + tmp1.nodeID
                                + " - Children: " + tmp1.sonList[tmp1.numOfSon - 1].nodeID);
                        System.out.append("\n");
                        //System.out.println("I will display");
                        //displayTree(tree);
                        flag = 1;
                        // transfer Thuc: backup, create the new one, put the backup
                        // back
                        PrefixTree[] transfer;
                        transfer = tmp1.sonList;

                        if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
                            //System.out.println("the number of son is big, current:"+tmp1.memForSon);
                            tmp1.memForSon += CYCLE;
                            //System.out.println("after adding: "+tmp1.memForSon);
                            tmp1.sonList = new PrefixTree[tmp1.memForSon];
                            // putback
                            System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                        }
                        //System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                    }

                }


            }
            //System.out.println("tree.len="+tree.len);
            return (flag);

        }

        // System.out.println("skip tree.len=layer-2");
        if (tree.len == layer - 1) {
            return flag;
        }

        if (tree.len == layer) {
            return flag;
        }
        // layer>2
        for (i = 0; i < tree.numOfSon; i++) {
            cur = tree.sonList[i];
            System.out.println("current:" + cur.nodeID);
            flag += candidateGen_Two(cur, layer);

        }
        //System.out.println("hushu6");
        return (flag);
    }


    //hs.end

    public int candidateGen(PrefixTree tree, int layer) {
        int i, j, k, l, p, q, flag, numofsonbak, att, sum = 10;
        int[] settmp = new int[100];
        int[] jointset = new int[100];
        double rate;
        PrefixTree cur, tmp1, tmp2;
        PrefixTree[] subnodeptr = new PrefixTree[100];

        flag = 0;
        if (tree == null) {
            return flag;
        }

        if (ruleSet.numOfRule > maxRuleAllowed) {
            return flag;
        }

        //System.out.println("current tree.len="+tree.len);
        // allSet.len=tree.len=0; layer=2 to pass on
        //displayTree(tree);
        if (tree.len == (layer - 2)) {
            // System.out.println("sum="+sum);
            numofsonbak = tree.numOfSon;
            //System.out.println("length="+tree.sonList.length);
            //System.out.println("numofsonbak="+numofsonbak); numofsonbak==the number of node in first level
            //hs. the follow is to choose the the ID of state==2.
            for (i = 0; i < numofsonbak - 1; i++) {
                tmp1 = tree.sonList[i];
                System.out.println("tmp1.nodeID=" + tmp1.nodeID);

                //System.out.println("i="+i);

                //System.out.println("tmp1.len="+ tmp1.len);
                if (tmp1 == null) {
                    //System.out.println("temp1 is null");
                    continue;
                }

                // zx, the following code section is redundant.
                if (tmp1.token >= 2) {
                    // zx, we won't consider
                    //System.out.println("token:"+tmp1.token);
                    continue;
                }

                // only consider those node which are not positive association.
                //hs.add
                //System.out.println(tmp1.ispa);


//                if(tmp1.ispa != 2)
//                {
//                    continue;
//                }


                //hs.end


                tmp1.numOfSon = 0;
                //System.out.println("temp len="+tmp1.len);
                for (k = 0; k < tmp1.len; k++) {

                    settmp[k] = tmp1.set[k];
                    System.out.println("settmp[" + k + "]=" + tmp1.set[k]);
                    //hs. the result is display the ID of state==2
                }
                for (j = i + 1; j < numofsonbak; j++) {

                    tmp2 = tree.sonList[j];
                    System.out.println("tmp2.nodeID=" + tmp2.nodeID);
                    //System.out.println("tmp2.len="+ tmp2.len);
                    //System.out.println("j="+j);

                    if (tmp2 == null) {
                        // System.out.println("the item is null");
                        continue;
                    }

                    // zx, the following code section is redundant.
                    if (tmp2.token >= 2) {
                        // System.out.println("tmp2.token="+tmp2.token);
                        continue;
                    }

                    // only consider those node which are not positive association.
                    //hs.add

//                    if(tmp2.ispa != 2)
//                    {
//                        continue;
//                    }


                    //hs.end

                    settmp[tmp1.len] = tmp2.nodeID;
                    //System.out.println("next layer: "+tmp2.nodeID);
                    //System.out.println("settmp["+k+"]="+tmp2.nodeID);

                    // added by zx, start
                    int subgroup_length = 0;

                    for (l = 0; l < settmp.length; l++) {
                        if (settmp[l] != 0) {
                            subgroup_length++;
                        }
                    }

                    //System.out.println("subgroup_length="+subgroup_length);

                    int[] index_in_dataspace = new int[subgroup_length];

                    for (l = 0; l < subgroup_length; l++) {
                        for (k = 0; k < realAtt; k++) {
                            if (dataSpacestat[k].min <= settmp[l] && dataSpacestat[k].max >= settmp[l]) {
                                index_in_dataspace[l] = k;

                                break;
                            }
                        }
                    }

                    int count = 0;  // number of exposure variables of value of 1.
                    for (l = 0; l < maxData; l++) {
                        int num_exposure_v = 0;

                        for (k = 0; k < subgroup_length; k++) {
                            if (dataSpace[l][index_in_dataspace[k]] == settmp[k]) {
                                num_exposure_v++;
                            }
                        }

                        if (subgroup_length == num_exposure_v) {
                            count++;
                        }
                    }

//                    for(k = 0; k < subgroup_length; k++)
//                    {
//                        System.out.print(settmp[k]+" ");
//                    }
//                    System.out.println();
//
//                    System.out.println("count="+count);
//                    System.out.println("maxData="+maxData);


                    if ((double) count / (double) maxData < t) {
                        continue;
                    }


                    // added by zx, end


//                    // ok so far
//                    for (k = 0; k < maxClass; k++) {
//
//
//                        if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
//                            jointset[k] = 1;
//                            // System.out.println("Yes. jointset["+k+"]="+jointset[k]);
//                        } else {
//                            jointset[k] = 0;
//                            // System.out.println("No. jointset["+k+"]="+jointset[k]);
//                        }
//
//
//                        //jointset[k] = 1;
//                    }
//
//                    for (k = 0; k < maxClass; k++) {
//                        sum += jointset[k];
//                    }
//                    // System.out.println("sum="+sum);
//                    if (sum == 0) {
//
//                        // System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

//                    // zx comment for CRPA
//                    if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
//                        // System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
//                        continue;
//                    }

                    tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                    System.out.println("father: " + tmp1.nodeID
                            + " - Children: " + tmp1.sonList[tmp1.numOfSon - 1].nodeID);
                    System.out.append("\n");
                    //System.out.println("I will display");
                    //displayTree(tree);
                    flag = 1;
                    // transfer Thuc: backup, create the new one, put the backup
                    // back
                    PrefixTree[] transfer;
                    transfer = tmp1.sonList;

                    if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
                        //System.out.println("the number of son is big, current:"+tmp1.memForSon);
                        tmp1.memForSon += CYCLE;
                        //System.out.println("after adding: "+tmp1.memForSon);
                        tmp1.sonList = new PrefixTree[tmp1.memForSon];
                        // putback
                        System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                    }
                    //System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                }

            }
            //System.out.println("tree.len="+tree.len);
            return (flag);

        }

        // System.out.println("skip tree.len=layer-2");
        if (tree.len == layer - 1) {
            return flag;
        }

        if (tree.len == layer) {
            return flag;
        }
        // layer>2
        for (i = 0; i < tree.numOfSon; i++) {
            cur = tree.sonList[i];
            //System.out.println("current:"+cur.nodeID);
            flag += candidateGen(cur, layer);

        }
        //System.out.println("hushu6");
        return (flag);
    }

    public int frequentSubSet(int[] itemset, int itemsetlen, int[] joint,
                              PrefixTree[] subnodeptr) {
        int i, j, k, l, place, flag;
        int[] subset = new int[100];
        PrefixTree subnode;

        if (itemsetlen <= 2) {
            return TRUE;
        }

        // The following part is to generate a subset of itemset
        l = itemsetlen;
        place = 0;
        for (i = 0; i < l - 2; i++) {
            k = 0;
            for (j = 0; j < l; j++) {
                if (j != place) {
                    subset[k++] = itemset[j];
                }
            }

            // / cannot find it
            if ((subnode = searchSubSet(subset, k)) == null) {
                // System.out.println("subnode=null, cannot find it, subset[k]="+subset[k-1]);
                return (FALSE);
            }
            // add to debug
            if (subnode.token >= 2) {
                return (FALSE);
            }
            subnodeptr[place] = subnode;

            // / test if they share the common frequent sets
            flag = 0;
            for (k = 0; k < maxClass; k++) {
                if (joint[k] != 0) {
                    if (subnode.lSup[k] > lMinSup[k]) {
                        flag = 1;
                    } else {
                        joint[k] = 0;
                    }
                }
            }

            if (flag == 0) {
                return (FALSE);
            }

            place++;
        }

        return (TRUE);
    }

    //hs.start
    public int ruleSelectAndPruning_Two(PrefixTree tree, int layer) {

        int i, j, k, l, flag, numofsonbak;
        int[] itemset = new int[100];
        int[] jointset = new int[100];
        PrefixTree cur, tmp1, tmp2;

        flag = 0;
        if (tree == null) {
            return flag;
        }
        //hs.test System.out.println("layer="+layer);
        System.out.println("hushutree.len=" + tree.len);
        System.out.println("hushutree.nodeID=" + tree.nodeID);
        if (tree.len == (layer - 1)) {

            // System.out.println("Yes get into ruleSelect and pruning");
            numofsonbak = tree.numOfSon;


            //System.out.println("numofsonbak="+numofsonbak);
            for (j = 0; j < numofsonbak; j++) {


                tmp1 = tree.sonList[j];
                System.out.println("hushutmp1.nodeID=" + tmp1.nodeID);
                System.out.println("tmp1.len=" + tmp1.len);
                //System.out.println("Yay, tmp1 now is: "+tmp1.nodeID);
                if (tmp1 == null) {
                    continue;
                }
                System.out.println("tmp1.gSup=" + tmp1.gSup);

                // / test the overall support
                if (tmp1.gSup < gMinSup) {
                    tree.sonList[j] = deleteNode(tmp1);
                    // System.out.println("Delete as gSup<gMinSup");
                    continue;
                }

                // / test the individual support
                // zx, It is very possible that tree.sonList[j] has been deleted
                // above the "overall support testing".

                if (individualFrequent(tmp1) == 0) {
                    tree.sonList[j] = deleteNode(tmp1);
                    // System.out.println("Delete as not frequent");
                    continue;
                }

                // / Compare with its all (k-1) order general rules, decide if
                // this node is prunable
                // / Or this rule is disdable (maybe its general rule
                if (ass == 0) {
                    if (isPrunable(tmp1) != 0) {
                        tree.sonList[j] = deleteNode(tmp1);
                        // System.out.println("delete as prunable");
                        continue;
                    }
                }

                // / Decide the rule target if possible
                // / if a rule is formed, then to seee if the improvement is
                // signiicant enough
                if (method != 0) {


                    // This is the procedure of forming rules by exclusiveness
                    //ruleTestWrite_CRPA(tmp1);
                    if (chooseMethod == 1) {
                        ruleTestWriteforAssociation_CRPA(tmp1);
                        //System.out.println("chooseme");

                    } else {
                        ruleTestWrite_CRPA(tmp1);

                    }

                    if (tmp1.token > 0) {
                        System.out.println("Iwill");
                        writeToRuleSet_CRPA(tmp1, 0);
                        writeToRuleSet_CRPA(tmp1, 1);
                    }

                    // System.out.println("After ruleTestWrite");
                } else {
                    // This is the procedure of forming rules by confidence and
                    // accuracy
                    //{
                    if (formingRule(tmp1) != 0) {
                        significantTest(tmp1);
                    }
                    writeToRuleSet(tmp1, 0);

                }
                System.out.println("secondtmp1.len=" + tmp1.len);

            }
            flag = reOrderSon(tree);
            System.out.println("flag=" + flag);
            displayTree(tree);
            return flag;
        }

        if (tree.len == layer) {
            return flag;
        }

        if (layer <= 2) {
            for (i = 0; i < tree.numOfSon1; i++) {
                for (int m = 0; m < ChosenTest.length; m++) {
                    //if((i>=(dataSpacestat[ChosenTest[0]-1].min-1)&&i<=(dataSpacestat[ChosenTest[0]-1].max-1))||(i>=(dataSpacestat[ChosenTest[1]-1].min-1)&&i<=(dataSpacestat[ChosenTest[1]-1].max-1))){
                    if ((i >= (dataSpacestat[ChosenTest[m] - 1].min - 1) && i <= (dataSpacestat[ChosenTest[m] - 1].max - 1))) {
                        cur = tree.sonList1[i];
                        System.out.println("hushucur.nodeID" + cur.nodeID);
                        flag += ruleSelectAndPruning_Two(cur, layer);
                    }
                }
            }
        } else {
            for (i = 0; i < tree.numOfSon; i++) {
                cur = tree.sonList[i];
                flag += ruleSelectAndPruning_Two(cur, layer);
            }
        }


        return (flag);

    }


    //hs.end

    public int ruleSelectAndPruning(PrefixTree tree, int layer) {

        int i, j, k, l, flag, numofsonbak;
        int[] itemset = new int[100];
        int[] jointset = new int[100];
        PrefixTree cur, tmp1, tmp2;

        flag = 0;
        if (tree == null) {
            return flag;
        }
        //hs.test System.out.println("layer="+layer);
        System.out.println("hstree.len=" + tree.len);
        if (tree.len == layer - 1) {
            // System.out.println("Yes get into ruleSelect and pruning");
            numofsonbak = tree.numOfSon;
            //System.out.println("numofsonbak="+numofsonbak);
            for (j = 0; j < numofsonbak; j++) {

                tmp1 = tree.sonList[j];
                System.out.println("tmp1.len=" + tmp1.len);
                //System.out.println("Yay, tmp1 now is: "+tmp1.nodeID);
                if (tmp1 == null) {
                    continue;
                }
                System.out.println("tmp1.gSup=" + tmp1.gSup);

                // / test the overall support
                if (tmp1.gSup < gMinSup) {
                    tree.sonList[j] = deleteNode(tmp1);
                    // System.out.println("Delete as gSup<gMinSup");
                    continue;
                }

                // / test the individual support
                // zx, It is very possible that tree.sonList[j] has been deleted
                // above the "overall support testing".

                if (individualFrequent(tmp1) == 0) {
                    tree.sonList[j] = deleteNode(tmp1);
                    // System.out.println("Delete as not frequent");
                    continue;
                }

                // / Compare with its all (k-1) order general rules, decide if
                // this node is prunable
                // / Or this rule is disdable (maybe its general rule
                if (ass == 0) {
                    if (isPrunable(tmp1) != 0) {
                        tree.sonList[j] = deleteNode(tmp1);
                        // System.out.println("delete as prunable");
                        continue;
                    }
                }

                // / Decide the rule target if possible
                // / if a rule is formed, then to seee if the improvement is
                // signiicant enough
                if (method != 0) {


                    // This is the procedure of forming rules by exclusiveness
                    //ruleTestWrite_CRPA(tmp1);
                    if (chooseMethod == 1) {
                        ruleTestWriteforAssociation_CRPA(tmp1);
                        //System.out.println("chooseme");

                    } else {
                        ruleTestWrite_CRPA(tmp1);

                    }

                    if (tmp1.token > 0) {
                        System.out.println("Iwill");
                        writeToRuleSet_CRPA(tmp1, 0);
                        writeToRuleSet_CRPA(tmp1, 1);
                    }

                    // System.out.println("After ruleTestWrite");
                } else {
                    // This is the procedure of forming rules by confidence and
                    // accuracy
                    if (formingRule(tmp1) != 0) {
                        significantTest(tmp1);
                    }
                    writeToRuleSet(tmp1, 0);
                }

            }
            flag = reOrderSon(tree);
            System.out.println("flag=" + flag);
            displayTree(tree);
            return flag;
        }

        if (tree.len == layer) {
            return flag;
        }

        for (i = 0; i < tree.numOfSon; i++) {
            cur = tree.sonList[i];
            flag += ruleSelectAndPruning(cur, layer);
        }
        return (flag);

    }

    public int individualFrequent(PrefixTree node) {
        int i, j, flag;

        for (i = 0; i < maxClass; i++) {
            if (node.lSup[i] > lMinSup[i]) {
                return 1;
            }
        }

        return 0;
    }

    public int reOrderSon(PrefixTree tree) {
        int i, k;

        k = 0;
        //System.out.print("tree.memForSon="+tree.memForSon);
        for (i = 0; i < tree.memForSon; i++) {
            if (tree.sonList[i] != null) {

                tree.sonList[k++] = tree.sonList[i];
                System.out.println("This son is not null: " + tree.sonList[k - 1].nodeID);
            }
            // System.out.println(tree.sonList[k].nodeID);
            if (k >= tree.numOfSon) {
                break;
            }
        }
        // print tree.sonlist after reoder
        // System.out.println("tree.sonlist length="+tree.sonList.length);

        // System.out.println("k after reoder:"+ k);
        tree.memForSon = tree.numOfSon + 1;
        // System.out.println("tree.memForson="+tree.memForSon);
        // Thuc add
        PrefixTree[] tmp;
        tmp = tree.sonList;

        tree.sonList = new PrefixTree[tree.memForSon];
        int newind = 0;
        for (int x = 0; x < tree.memForSon; x++) {
            if (tmp[x] != null) {
                tree.sonList[newind++] = tmp[x];
                //System.out.println("This son is not null: "+tree.sonList[newind-1].nodeID);
            }
        }
        // end Thuc add. Original is only the tree.sonList=new
        // PrefixTree[tree.memForSon]

        if (k > 1) {
            return (1);
        } else {
            return 0;
        }
    }

    // This is the procedure for forming rules by confidence and accuracy
    public int formingRule(PrefixTree node) {
        int i, j = 0, k = 0, flag;
        int[] targetset = new int[2];
        double conf = 0, conf1, conf2, conf3, acc, maxconf, base;
        // System.out.print("Start formingRule(node)");
        flag = 0;
        if (node.gSup <= gMinSup) {
            return (0);
        }
        for (i = 0; i < maxClass; i++) {
            if (node.lSup[i] < lMinSup[i]) {
                continue;
            }
            conf = (double) node.lSup[i] / node.gSup;
            if (conf < minConf) {
                continue;
            }
            if (conf > 1) {
                printf("confidence > 1 \n");
                System.exit(0);
            }
            if (node.gSup > 30) {
                node.acc = conf - 1.96
                        * Math.sqrt((conf) * (1 - conf) / node.gSup);
            } else {
                node.acc = (float) (node.lSup[i] + 1) / (node.gSup + maxClass);
            }
            // base = (float)Dist[i]/MaxData;
            // if( node->Acc < base ) {node->Acc = 0; break; }
            node.conf = conf;
            node.target[0] = i;

            if (conf > PURITY) {
                node.token = 2;
            } else {
                node.token = 1;
            }

            if (ass != 0 || opt != 0) {
                node.token = 1;
            }

            flag = 1;
            singleRule++;
            break;
        }

        if (flag == 1 || maxTarget < 2) {
            return (flag);
        }

        flag = 0;
        maxconf = 0;
        for (i = 0; i < maxClass - 1; i++) {
            if (node.lSup[i] < lMinSup[i]) {
                continue;
            }
            conf1 = (double) node.lSup[i] / node.gSup;
            base = (double) dist[i] / maxData;
            if (conf1 < 2 * base) {
                continue;
            }
            for (j = i + 1; j < maxClass; j++) {
                if (node.lSup[j] < lMinSup[j]) {
                    continue;
                }
                conf2 = (double) node.lSup[j] / node.gSup;
                base = (double) dist[j] / maxData;
                if (conf2 < 2 * base) {
                    continue;
                }
                conf = conf1 + conf2;
                if (conf > minConf) {
                    if (conf > maxconf) {
                        maxconf = conf;
                        targetset[0] = i;
                        targetset[1] = j;
                        multiRule++;
                        flag = 1;
                    }
                }
            }
        }

        if (maxconf > 0.01) {
            node.token = 1;
            node.target[0] = targetset[0];
            node.target[1] = targetset[1];
            if (node.gSup > 30) {
                node.acc = conf - 1.96
                        * Math.sqrt((maxconf) * (1 - maxconf) / node.gSup);
            } else {
                node.acc = (double) (node.lSup[i] + node.lSup[j] + 1)
                        / (node.gSup + maxClass);
            }
            node.conf = maxconf;
        }

        if (flag == 1 || maxTarget < 3) {
            return (flag);
        }

        flag = 0;
        maxconf = 0;
        for (i = 0; i < maxClass - 1; i++) {
            if (node.lSup[i] < lMinSup[i]) {
                continue;
            }
            // if( node->LSup[i] < GMinSup) continue;
            conf1 = (double) node.lSup[i] / node.gSup;
            base = (double) dist[i] / maxData;
            if (conf1 < 3 * base) {
                continue;
            }
            for (j = i + 1; j < maxClass; j++) {
                if (node.lSup[j] < lMinSup[j]) {
                    continue;
                }
                // if( node->LSup[j] < GMinSup) continue;
                conf2 = (double) node.lSup[j] / node.gSup;
                base = (double) dist[j] / maxData;
                if (conf2 < 3 * base) {
                    continue;
                }
                for (k = j + 1; k < maxClass; k++) {
                    if (node.lSup[k] < lMinSup[k]) {
                        continue;
                    }
                    // if( node->LSup[k] < GMinSup) continue;
                    conf3 = (double) node.lSup[k] / node.gSup;
                    base = (double) dist[k] / maxData;
                    if (conf3 < 3 * base) {
                        continue;
                    }
                    conf = conf1 + conf2 + conf3;
                    if (conf > minConf) {
                        if (conf > maxconf) {
                            maxconf = conf;
                            targetset[0] = i;
                            targetset[1] = j;
                            targetset[3] = k;
                            flag = 1;
                            multiRule++;
                        }
                    }
                }
            }
        }

        if (maxconf > 0.01) {
            node.token = 1;
            node.target[0] = targetset[0];
            node.target[1] = targetset[1];
            node.target[2] = targetset[2];
            if (node.gSup > 30) {
                node.acc = conf - 1.96
                        * Math.sqrt((maxconf) * (1 - maxconf) / node.gSup);
            } else {
                node.acc = (double) (node.lSup[i] + node.lSup[j] + node.lSup[k] + 1)
                        / (node.gSup + maxClass);
            }
            node.conf = maxconf;
        }
        return (flag);

    }

    //hs.start

    public int ruleSecondTestWrite_CRPA(PrefixTree node) {

        int i, j, k;
        double leftend, rightend;
        double sum, lsuprate, excl, conf, lift, pc, npc, npnc, pnc, p, np, oddsratio, relativerisk;

        double x, nx, cAZ, cA, cZ, c, tAZ, tA, tZ, t, value;

        //System.out.println("enter ruleTestWrite_CRPA");

        // System.out.println("Start ruleTestWrite(node)");

        /*
        zx

        ---------------------------
        |    |    c    |    nc    |
        |    |         |          |
        ---------------------------
        | p  |supp(pc) |supp(pnc) |
        |    |         |          |
        ---------------------------
        | np |supp(npc)|supp(npnc)|
        |    |         |          |
        ---------------------------

                          supp(pc)/supp(pnc)
        oddratio(p->c) = --------------------
                          supp(npc)/supp(npnc)

                          supp(pc)*supp(npnc)
                       = --------------------
                          supp(pnc)*supp(npc)

        It is very difficult to obtain a reliable Chi-square
        estimation for three or more variables. To obtain a reliable
        Chi-square statistic, the count value in each cell has to be
        5 or larger.

        example of Matrix M:
            a     b     c     d     Z
        t1  0     1     0     1     0
        t2  1     1     0     0     1
        t3  0     1     1     0     0
        t4  0     0     1     1     1
        chi:chi-square threshold
        V:Mapping from index to variables

            Z+    Z-
        X+  cAZ   cA
        X-  cZ    c

        */

        //if(node.len == 2)
        //{
        //System.out.println("second level node, nodeID="+node.nodeID);
        //}

        /*
        if(node.isfrequent == false)
        {
            node.ispa = 0;     // nothing association
            node.token = 0;

            // only consider frequent node.
            return 0;
        }
        */


        //for (i = 0; i < maxClass; i++)
        {

            // for each frequent predictive variable, to generate its own contingency table.
            x = node.gSup;          // node.gSup = counter[maxClass][item]

            nx = maxData - x;       // maxData is the total amount of data set

            cAZ = node.lSup[1];     // zx, node.lSup[1] = counter[1][item]
            cZ = seconddist[1] - cAZ;     // zx, dist[1] = counter[1][0]

            //System.out.println("dist[0]="+dist[0]);

            //cAZ = node.lSup[i];
            //cZ = dist[i] - cAZ;
            cA = x - cAZ;

            //c = maxData - x - dist[i] + cAZ;
            c = maxData - x - seconddist[1] + cAZ;

            //System.out.println("x="+x+" "+"nx="+nx+" "+"cAZ="+cAZ+", cA="+cA+", cZ="+cZ+", c="+c);

            // any whole row or column zero will not contribute to Chi-square
            // value on the corresponding contingency table.
            if ((cAZ + cA) * (cAZ + cZ) * (cA + c) * (cZ + c) == 0) {
                cAZ = 0;
                cA = 0;
                cZ = 0;
                c = 0;

                node.ispa = 0;
                // this node will be pruned
                //node.token = 0;

                return 1;
            }

            // to obtain reliable Chi-square estimation for three or more
            // variables, the count value in each cell has to be 5 or larger.
            if (cAZ < 5) {
                cAZ = 0;
                cA = 0;
                cZ = 0;
                c = 0;

                node.ispa = 0;
                // this node will be pruned
                //node.token = 0;

                return 1;
            }

            // M is the contingency table with single predictive variable or combined ones.

            tAZ = (cAZ + cA) * (cAZ + cZ) / (cAZ + cA + cZ + c);   // E(n11) = (n1.)*(n.1)/n
            tA = (cAZ + cA) * (cA + c) / (cAZ + cA + cZ + c);      // E(n12)
            tZ = (cZ + c) * (cAZ + cZ) / (cAZ + cA + cZ + c);      // E(n21)
            t = (cA + c) * (cZ + c) / (cAZ + cA + cZ + c);         // E(n22)

            // the following equation is calculating Chi-square value on
            // contingency table M according to equation(1) of paper.
            value = Math.pow((cAZ - tAZ), 2) / tAZ + Math.pow((cA - tA), 2) / tA + Math.pow((cZ - tZ), 2) / tZ + Math.pow((c - t), 2) / t;

        /*
        if(node.len == 2)
        {
            System.out.println("second level node's value="+value);
            System.out.println("cAZ="+cAZ+"， tAZ="+tAZ+"， c="+c+", t="+c);

        }
        */

            //System.out.println("cAZ="+cAZ+"， tAZ="+tAZ+"， c="+c+", t="+t);
            //System.out.println("node.nodeID="+node.nodeID);
            //System.out.println("value="+value);
            //System.out.println("--------------------------------");


            if (cAZ >= tAZ && c >= t) {
                // Chi-square value is 3.84, when p-value is 0.05.
                //if(value >= ChisquareValue)
                //if(value >= 15.14)     //99.99%
                //if(value >= 100)
                //using Chi-square threshold does not work, as it would remove
                // a lot of variables from the control. Sometimes, need to use the value
                // up to 9000. Here, we use exclusiveness rather than chi-square
                if ((cAZ < gMinSup) || (cA < gMinSup) || (cZ < gMinSup))
                //if(value >= 10.83)
                {
                    //System.out.println("ChisquareValue="+ChisquareValue);
                    //System.out.println("node.nodeID="+node.nodeID);
                    //System.out.println("positive node");
                    // this rule is positive association
                    node.ispa = 1;

                    //node.target[0] = 1;
                    //node.target[0] = i;
                /*
                if(node.len == 2)
                {
                    System.out.println("node:"+node.nodeID+" -> target:"+node.target[0]);

                    System.out.println("node set:");
                    for(i = 0; i < node.len; i++)
                    {
                        System.out.println(node.set[i]);
                    }

                }
                */

                    // this node will be processed further.
                    //node.token = 1;

                    //singleRule++;

                } else {
                    //System.out.println("negative node");
                    // this rule is not positive association which is candidate of combined causal association rule.
                    node.ispa = 2;
                    // this node will be processed further.
                    //node.token = 0;

                }
            }


        }

        return 1;
    }

    //hs.end

    //hs.add for choose the association rule
    public int ruleTestWriteforAssociation_CRPA(PrefixTree node) {
        int i, j, k;
        double leftend, rightend;
        double sum, lsuprate, excl, conf, lift, pc, npc, npnc, pnc, p, np, oddsratio, relativerisk;

        //System.out.println("Start ruleTestWrite(node)");
        if (node.gSup <= gMinSup)
            return (0);

        // sum carries the local support in ratio
        sum = 0;
        for (i = 0; i < maxClass; i++) {
            if (dist[i] < 0.000001)
                continue;
            sum += (float) node.lSup[i] / dist[i];
        }
        //System.out.println(sum);
        for (i = 0; i < maxClass; i++) {
            if (node.lSup[i] < lMinSup[i]) {
//                            lowlsupItems[llsIndex++]=node.nodeID;
                continue;
            }
            // Here begin to caculate exclusiveness
            if (dist[i] < 0.000001)
                continue;

            if (node.lSup[i] < 0.0001)
                node.lSup[i] = 0.5;
            lsuprate = (double) node.lSup[i] / dist[i];
            excl = lsuprate / sum;
            // lift = conf/((float)Dist[i]/MaxData);
//Added this
            //	 if( excl<minConf ) continue;

            // We calculate Odds Ratio and Relative Risk

            if (dist[i] < 0.000001)
                continue;

            p = node.gSup;
            np = maxData - p;

            pc = node.lSup[i];
            npc = dist[i] - pc;
            pnc = p - pc;
            npnc = maxData + pc - p - dist[i];

            // if(npc<0.0001 || pnc<0.0001) oddsratio = 10000;
            // else oddsratio = (pc*npnc)/(npc*pnc);

            if (pc < 0.0001)
                pc = 0.5;
            //        pc=1.0;
            if (npc < 0.0001)
                npc = 0.5;
            //   npc=1.0;
            if (pnc < 0.0001)
                pnc = 0.5;
            //   pnc=1.0;
            if (p < 0.0001)
                p = 0.5;
            //  p=1.0;
            oddsratio = (pc * npnc) / (npc * pnc);
            //   Confidence interval for odds ratio
            leftend = Math.exp(Math.log(oddsratio) - 1.96 * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));
            rightend = Math.exp(Math.log(oddsratio) + 1.96 * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));

            // if(npc<0.0001 || p<0.0001) relativerisk = 10000;
            // else relativerisk = (pc*np)/(npc*p);
            relativerisk = (pc * np) / (npc * p);

            // To make relative in one direction
            if (i == 1 && relativerisk < 0.00001)
                relativerisk = 10000;
            else if (i == 1)
                relativerisk = 1 / relativerisk;
//System.out.println("odds ratio:"+oddsratio+" pc:"+pc+" npnc"+npnc+" pnc"+pnc+" npc"+npc+ "leftend:"+leftend);

            // switch two methods between low bound and odds ratio. modified by mss
            //use confidence interval
            // use odds ratio threshold
            if (confidenceTest == 1) {
                conf = (double) node.lSup[i] / node.gSup;
                if (conf <= minConf) continue;
            } else {
                if (statisticTest == 1) {
                    //if(oddsratio < staThreshold) continue;
                    //if(oddsratio < staThreshold) {
                    if (oddsratio < staThreshold) {
                        node.ispa = 2;
                        // this node will be processed further.
                        node.token = 0;
                    } else {
                        node.token = 1;
                        node.ispa = 1;

                        node.target[0] = 1;
                    }
                } else {

                    //if(leftend<=1) continue;
                    if (leftend <= 1) {
                        node.ispa = 2;
                        // this node will be processed further.
                        node.token = 0;
                    } else {
                        //System.out.println("node.nodeIDhsuhu="+node.nodeID);
                        node.token = 1;
                        node.ispa = 1;

                        node.target[0] = 1;
                        singleRule++;
                    }
                }
            }
//                        System.out.println("hushu");
//                        System.out.println("node.nodeID="+node.nodeID);
//	  if (relativerisk < 1.5) continue;

            // We keep these for the pruning
            // if node.token>=2 we will not generate further layers
//			conf = (double) node.lSup[i] / node.gSup;
//                        //System.out.println("conf"+conf);
//			if (conf > PURITY)
//				node.token = 2;
//                        else{
//				node.token = 1;
//                                node.ispa = 1;
//
//                                node.target[0] = 1;
//                        }
//			if (ass != 0 || opt != 0)
//				node.token = 1;


            // We set the Acc for the sack of ordering rules
            // node->Acc = (oddsratio-1)/(oddsratio+1);
//			node.acc = excl;
//			node.conf = conf;
//			node.target[0] = i;
            // if(SimpleSignificantTest(node)) WriteToRuleSet(node, 0);

            //System.out.println("RuleTestWrite- before significantTest(node)");
            //System.out.println("Odds ratio="+oddsratio+" relativerisk="+relativerisk+"node token="+node.token);
            //significantTest(node);
            //singleRule++;
            //  break;
        }
        return 1;
    }

    //hs.end
    // This is the procedure of forming rules by exclusiveness
    public int ruleTestWrite_CRPA(PrefixTree node) {

        int i, j, k;
        double leftend, rightend;
        double sum, lsuprate, excl, conf, lift, pc, npc, npnc, pnc, p, np, oddsratio, relativerisk;

        double x, nx, cAZ, cA, cZ, c, tAZ, tA, tZ, t, value;

        //System.out.println("enter ruleTestWrite_CRPA");

        // System.out.println("Start ruleTestWrite(node)");

        /*
        zx

        ---------------------------
        |    |    c    |    nc    |
        |    |         |          |
        ---------------------------
        | p  |supp(pc) |supp(pnc) |
        |    |         |          |
        ---------------------------
        | np |supp(npc)|supp(npnc)|
        |    |         |          |
        ---------------------------

                          supp(pc)/supp(pnc)
        oddratio(p->c) = --------------------
                          supp(npc)/supp(npnc)

                          supp(pc)*supp(npnc)
                       = --------------------
                          supp(pnc)*supp(npc)

        It is very difficult to obtain a reliable Chi-square
        estimation for three or more variables. To obtain a reliable
        Chi-square statistic, the count value in each cell has to be
        5 or larger.

        example of Matrix M:
            a     b     c     d     Z
        t1  0     1     0     1     0
        t2  1     1     0     0     1
        t3  0     1     1     0     0
        t4  0     0     1     1     1
        chi:chi-square threshold
        V:Mapping from index to variables

            Z+    Z-
        X+  cAZ   cA
        X-  cZ    c

        */

        //if(node.len == 2)
        //{
        //System.out.println("second level node, nodeID="+node.nodeID);
        //}

        /*
        if(node.isfrequent == false)
        {
            node.ispa = 0;     // nothing association
            node.token = 0;

            // only consider frequent node.
            return 0;
        }
        */


        //for (i = 0; i < maxClass; i++)
        {

            // for each frequent predictive variable, to generate its own contingency table.
            //System.out.println("node.gSup"+node.gSup);
            x = node.gSup;          // node.gSup = counter[maxClass][item]

            nx = maxData - x;       // maxData is the total amount of data set

            cAZ = node.lSup[1];     // zx, node.lSup[1] = counter[1][item]
            cZ = dist[1] - cAZ;     // zx, dist[1] = counter[1][0]

            //System.out.println("dist[0]="+dist[0]+"dist[1]="+dist[1]);

            //cAZ = node.lSup[i];
            //cZ = dist[i] - cAZ;
            cA = x - cAZ;

            //c = maxData - x - dist[i] + cAZ;
            c = maxData - x - dist[1] + cAZ;

            //System.out.println("x="+x+" "+"nx="+nx+" "+"cAZ="+cAZ+", cA="+cA+", cZ="+cZ+", c="+c);

            // any whole row or column zero will not contribute to Chi-square
            // value on the corresponding contingency table.
            if ((cAZ + cA) * (cAZ + cZ) * (cA + c) * (cZ + c) == 0) {
                cAZ = 0;
                cA = 0;
                cZ = 0;
                c = 0;

                node.ispa = 0;
                // this node will be pruned
                node.token = 0;

                return 1;
            }

            // to obtain reliable Chi-square estimation for three or more
            // variables, the count value in each cell has to be 5 or larger.
            if (cAZ < 5) {
                cAZ = 0;
                cA = 0;
                cZ = 0;
                c = 0;

                node.ispa = 0;
                // this node will be pruned
                node.token = 0;

                return 1;
            }

            // M is the contingency table with single predictive variable or combined ones.

            tAZ = (cAZ + cA) * (cAZ + cZ) / (cAZ + cA + cZ + c);   // E(n11) = (n1.)*(n.1)/n
            tA = (cAZ + cA) * (cA + c) / (cAZ + cA + cZ + c);      // E(n12)
            tZ = (cZ + c) * (cAZ + cZ) / (cAZ + cA + cZ + c);      // E(n21)
            t = (cA + c) * (cZ + c) / (cAZ + cA + cZ + c);         // E(n22)

            // the following equation is calculating Chi-square value on
            // contingency table M according to equation(1) of paper.
            value = Math.pow((cAZ - tAZ), 2) / tAZ + Math.pow((cA - tA), 2) / tA + Math.pow((cZ - tZ), 2) / tZ + Math.pow((c - t), 2) / t;
            node.value = value;
        /*
        if(node.len == 2)
        {
            System.out.println("second level node's value="+value);
            System.out.println("cAZ="+cAZ+"， tAZ="+tAZ+"， c="+c+", t="+c);

        }
        */

            //System.out.println("cAZ="+cAZ+"， tAZ="+tAZ+"， c="+c+", t="+t);
            //System.out.println("node.nodeID="+node.nodeID);
            //System.out.println("value="+value);
            //System.out.println(node.value);
            //System.out.println("--------------------------------");


            if (cAZ >= tAZ && c >= t) {
                // Chi-square value is 3.84, when p-value is 0.05.
                if (value >= ChisquareValue) {
                    //System.out.println("ChisquareValue="+ChisquareValue);
                    //System.out.println("node.nodeID="+node.nodeID);
                    //System.out.println("positive node");
                    // this rule is positive association
                    node.ispa = 1;

                    node.target[0] = 1;
                    //node.target[0] = i;
                /*
                if(node.len == 2)
                {
                    System.out.println("node:"+node.nodeID+" -> target:"+node.target[0]);

                    System.out.println("node set:");
                    for(i = 0; i < node.len; i++)
                    {
                        System.out.println(node.set[i]);
                    }

                }
                */

                    // this node will be processed further.
                    node.token = 1;

                    singleRule++;

                } else {
                    //System.out.println("negative node");
                    // this rule is not positive association which is candidate of combined causal association rule.
                    node.ispa = 2;
                    // this node will be processed further.
                    node.token = 0;

                }
            }


        }

        return 1;
    }


    // This have not been used in this program
    public int significantTest(PrefixTree node) {
        int i, j, k;
        double acctmp;

        // System.out.println("Start SignificantTest(node)- node passed on: ");
        // displayTree(node);
        // if the rule is multi-targets, then return.
        // if(node->Target[1] != -1) { WriteToRuleSet (node, 0); return (1); }
        if (ass != 0) {
            writeToRuleSet(node, 0);
            return (1);
        }
        // System.out.println("node token "+node.token);
        if (node.token == 2) {
            // System.out.println("Start writting as token=2");
            writeToRuleSet(node, 0);
            // System.out.println("after writting, number of rules:"+ruleSet.numOfRule);
            return (1);
        }

        // Search for all tree to see if there is better rule
        acctmp = findMaxConfidence(allSet, node);

        // printf("acctmp \n %f ", acctmp);
        // if no rule found
        if (acctmp < 0.001) {
            // System.out.println("acctmp<0.001, now writting");
            writeToRuleSet(node, 0);
            return (1);
        }

        if (opt == 0) {
            if (node.acc <= acctmp + minImp) {
                // I comment this since we do not get rules from the tree.
                // node -> Token = 0;
                if (node.target[1] == -1) {
                    singleRule--;
                } else {
                    multiRule--;
                }
            } else {
                writeToRuleSet(node, 0);
            }
        } else {
            if (node.acc <= acctmp) {
                node.token = 0;
                if (node.target[1] == -1) {
                    singleRule--;
                } else {
                    multiRule--;
                }
            } else {
                writeToRuleSet(node, 0);
            }
        }

        return 1;

    }

    public int simpleSignificantTest(PrefixTree node) {
        int i, j, k, tt, place, l;
        int[] settmp = new int[100];
        int[] subset = new int[100];
        PrefixTree subnode;

        if (node.len == 1) {
            return TRUE;
        }

        for (i = 0; i < node.len; i++) {
            if (node.acc < node.subNode[i].acc) {
                return FALSE;
            }
        }

        return TRUE;

    }

    public double findMaxConfidence(PrefixTree tree, PrefixTree rulenode) {
        int i, j, k, count, item, tmp, indexnum, targetvalue;
        PrefixTree[] index;
        PrefixTree tmptree;
        double maxacc, maxconf;

        maxacc = 0;
        maxconf = 0;

        count = 0;

        if (tree == null) {
            return maxacc;
        }
        // thuc changed 1 to cycle
        index = new PrefixTree[CYCLE];
        indexnum = CYCLE;

        /* this is for first layer count */
        tree.reserve = 0;
        for (i = 0; i < rulenode.len; i++) {

            item = rulenode.set[i];

            for (j = tree.reserve; j < tree.numOfSon; j++) {
                tmptree = tree.sonList[j];
                if (tmptree.nodeID > item) {
                    break;
                }
                if ((tmptree.nodeID) == item) {

                    // here begins test
                    if (sameTarget(tmptree, rulenode) != 0) {

                        if (tmptree.acc > maxacc) {
                            maxacc = tmptree.acc;
                        }
                        if (tmptree.conf > maxconf) {
                            maxconf = tmptree.conf;
                        }
                    }

                    // / The last node
                    if (i == rulenode.len - 1) {
                        break;
                    }

                    tree.reserve = j + 1;
                    index[count++] = tmptree;
                    tmptree.reserve = 0;
                    if (count > indexnum - 1) {
                        indexnum += CYCLE;
                        // Thuc add
                        PrefixTree[] tm = index;
                        index = new PrefixTree[indexnum];
                        System.arraycopy(tm, 0, index, 0, tm.length);
                    }
                    break;
                }
            }

            tmp = count;
            for (j = 0; j < tmp - 1; j++) {
                tmptree = index[j];
                // if(tmptree==null) System.out.println("j="+j+"temttree=null");
                for (k = tmptree.reserve; k < tmptree.numOfSon; k++) {
                    if (tmptree.sonList[k].nodeID > item) {
                        break;
                    }
                    if (tmptree.sonList[k].nodeID == item) {
                        // here begins test
                        if (sameTarget(tmptree.sonList[k], rulenode) != 0) {
                            if (tmptree.sonList[k].acc > maxacc) {
                                maxacc = tmptree.sonList[k].acc;
                            }
                            if (tmptree.sonList[k].conf > maxconf) {
                                maxconf = tmptree.sonList[k].conf;
                            }
                        }

                        tmptree.reserve = k + 1;
                        index[count++] = tmptree.sonList[k];
                        tmptree.sonList[k].reserve = 0;
                        if (count > indexnum - 1) {
                            indexnum += CYCLE;
                            // thuc add
                            PrefixTree[] tmm = index;
                            index = new PrefixTree[indexnum];
                            System.arraycopy(tmm, 0, index, 0, tmm.length);
                        }

                        break;
                    }
                }
            }
        }
        free(index);

        // if(Opt) return (maxconf);
        if (opt != 0) {
            return (maxacc);
        } else {
            return (maxacc);
        }
    }

    public int sameTarget(PrefixTree general, PrefixTree specific) {

        if (general == specific) {
            return (0);
        }
        if (general.token <= 0) {
            return (0);
        }
        if (general.target[1] != -1) {
            return (0);
        }
        if (general.target[0] != specific.target[0]) {
            return (0);
        }
        return (1);

    }

    public int isPrunable(PrefixTree node) {
        int i, j, k, tt, place, l;
        int[] settmp = new int[100];
        int[] subset = new int[100];
        PrefixTree subnode;

        for (i = 0; i < node.len; i++) {
            // if satisfiying the lemma
            if (pruningTesting(node.subNode[i], node) != 0) {
                return TRUE;
            }
        }

        if (heuristic == 0) {
            return FALSE;
        }

        for (i = 0; i < node.len; i++) {
            if (higherConfidence(node.subNode[i], node) != 0) {
                return FALSE;
            }
        }

        // if confidence is lower than all
        return TRUE;

    }

    public PrefixTree searchSubSet(int[] itemset, int itemsetlen) {
        int i, j, anitem, num;
        PrefixTree tmp;

        tmp = allSet;
        for (i = 0; i < itemsetlen; i++) {
            anitem = itemset[i];
            num = tmp.numOfSon;
            for (j = 0; j < num; j++) {
                if (tmp.sonList[j].nodeID < anitem) {
                    continue;
                }
                if (tmp.sonList[j].nodeID == anitem) {
                    tmp = tmp.sonList[j];
                    break;
                }
                return null;
            }
            if (j == num) {
                return null;
            }
        }

        if (tmp.len != itemsetlen) {
            return null;
        }
        if (tmp.token == -2) {
            return null;
        }

        // for (i=0; i<tmp->Len; i++)
        // printf("    %d ", tmp->Set[i]);
        return tmp;
    }

    public int pruningTesting(PrefixTree upper, PrefixTree low) {
        int i, j, k, flag, target;

        if (upper == null || low == null) {
            return 0;
        }

        if (upper.gSup == low.gSup) {
            return (1);
        }

        if (upper.gSup == 0) {
            return (0);
        }

        if (upper.token == -2) {
            return (0);
        }

        if (upper.token == 2) {
            return (1);
        }

        // / If there are targets, then only test target, actually, only one
        // target is possible
        if (low.token >= 1) {
            flag = 1;
            target = low.target[0];
            if (upper.lSup[target] > lMinSup[target]) {
                if ((upper.gSup - upper.lSup[target]) != (low.gSup - low.lSup[target])) {
                    flag = 0;
                }
            }
            return flag;
        }

        // otherwise test all possible consequences
        flag = 1;
        for (i = 0; i < maxClass; i++) {
            if (upper.lSup[i] > lMinSup[i] && low.lSup[i] > lMinSup[i]) {
                if ((upper.gSup - upper.lSup[i]) != (low.gSup - low.lSup[i])) {
                    flag = 0;
                    break;
                }
            }
        }
        return (flag);
    }

    public int higherConfidence(PrefixTree upper, PrefixTree low) {
        int i, j, k, flag, target;
        double conf1, conf2;

        if (upper == null || low == null) {
            return 0;
        }

        // / If there are targets, then only test target, actually, only one
        // target is possible
        if (low.token >= 1) {
            target = low.target[0];
            if (low.gSup > 0) {
                conf1 = (double) low.lSup[target] / low.gSup;
            } else {
                conf1 = 0;
            }
            if (upper.gSup > 0) {
                conf2 = (double) upper.lSup[target] / upper.gSup;
            } else {
                conf2 = 0;
            }
            if (conf1 > conf2) {
                return TRUE;
            }
            return FALSE;
        }

        // otherwise test all possible consequences
        flag = 1;
        for (i = 0; i < maxClass; i++) {
            if (low.gSup > 0) {
                conf1 = (float) low.lSup[i] / low.gSup;
            } else {
                conf1 = 0;
            }
            if (upper.gSup > 0) {
                conf2 = (float) upper.lSup[i] / upper.gSup;
            } else {
                conf2 = 0;
            }
            if (conf1 > conf2) {
                return TRUE;
            }
        }
        return (FALSE);
    }

    public PrefixTree deleteNode(PrefixTree node) {

        if (node == null) {
            return null;
        }
        if (node.father != null) {
            node.father.numOfSon--;
        }
        if (node.set != null) {
            free(node.set);
        }
        if (node.sonList != null) {
            free(node.sonList);
        }
        if (node.lSup != null) {
            free(node.lSup);
        }
        free(node);
        treeSize--;

        return null;

    }

    /* this is to add a item to a node */
    public PrefixTree addSon(PrefixTree node, PrefixTree sibling,
                             PrefixTree[] subnodeptr) {
        int i;
        PrefixTree tmp;

        tmp = new PrefixTree();
        tmp.set = new int[node.len + 2];
        tmp.lSup = new double[maxClass + 2];
        tmp.sonList = new PrefixTree[CYCLE];
        tmp.subNode = new PrefixTree[node.len + 2];

        tmp.len = node.len + 1;
        for (i = 0; i < node.len; i++) {
            tmp.set[i] = node.set[i];
        }
        tmp.set[i] = sibling.nodeID;
        tmp.father = node;
        tmp.memForSon = CYCLE;
        tmp.numOfSon = 0;
        tmp.token = 0;
        tmp.reserve = 0;
        tmp.conf = 0;
        tmp.acc = 0;
        for (i = 0; i < CYCLE; i++) {
            tmp.sonList[i] = null;
        }
        tmp.nodeID = sibling.nodeID;

        for (i = 0; i < MAXTARGET; i++) {
            tmp.target[i] = -1;
        }
        for (i = 0; i < maxClass; i++) {
            tmp.lSup[i] = 0;
        }

        for (i = 0; i < tmp.len - 2; i++) {
            tmp.subNode[i] = subnodeptr[i];
        }
        tmp.subNode[i] = node;
        i++;
        tmp.subNode[i] = sibling;


        // added by zx
        tmp.isfrequent = true;
        tmp.ispa = 0;

        treeSize++;
        return tmp;
    }

    /* this is add new node (single item) to a node */
    public PrefixTree newNode(PrefixTree tree, int item) {

        int i;
        PrefixTree tmp;
        double rate;

        tmp = new PrefixTree();
        tmp.set = new int[2];
        tmp.lSup = new double[maxClass + 2];
        tmp.sonList = new PrefixTree[CYCLE];

        tmp.memForSon = CYCLE;
        tmp.len = 1;                // zx, root is 0 level, this node is in 1 level.
        tmp.set[0] = item;
        tmp.father = tree;
        tmp.numOfSon = 0;
        tmp.token = 0;
        tmp.reserve = 0;
        tmp.acc = 0;
        tmp.conf = 0;

        for (i = 0; i < CYCLE; i++) {
            tmp.sonList[i] = null;
        }
        tmp.nodeID = item;
        tmp.gSup = counter[maxClass][item];

        for (i = 0; i < maxClass; i++) {
            tmp.lSup[i] = counter[i][item];
        }

        for (i = 0; i < MAXTARGET; i++) {
            tmp.target[i] = -1;
        }

        // zx, for CR-PA, start.
        //if (((double)counter[maxClass][item] / (double)maxData) >= t)
        //{
        tmp.isfrequent = true;
        //}
        //else
        //{
        //    tmp.isfrequent = false;
        //}
        // zx, for CR-PA, end.

        // There is currently no any type of associations on this node.
        tmp.ispa = 0;

        //tmp.index_in_dataspace
        //tmp.isMultivalue

        // zx, add for CR-PA
        for (i = 0; i < realAtt; i++) {
            if (dataSpacestat[i].min <= item && item <= dataSpacestat[i].max) {
                // Judge if the node corresponds to multi-value column in dataSpace.
                if ((dataSpacestat[i].max - dataSpacestat[i].min) != 0)
                    tmp.isMultivalue = true;
                else
                    tmp.isMultivalue = false;

                // Record the column index in dataSpace corresponding to item.
                tmp.index_in_dataspace = i;
            }
        }


        treeSize++;

        return tmp;
    }

    /* this is add new node (single item) to a tree */
    public PrefixTree newEmptyNode(PrefixTree tree, int item) {

        int i;
        PrefixTree tmp;
        double rate;

        tmp = new PrefixTree();
        tmp.set = new int[tree.len];
        tmp.sonList = new PrefixTree[maxItem];

        tmp.memForSon = maxItem;

        tmp.len = tree.len + 1;
        for (i = 0; i < tree.len; i++) {
            tmp.set[i] = tree.set[i];
        }
        tmp.set[i] = item;

        tmp.father = tree;
        tmp.numOfSon = 0;
        tmp.token = -2;
        tmp.gSup = 0;
        tmp.reserve = 0;

        for (i = 0; i < maxItem; i++) {
            tmp.sonList[i] = null;
        }
        tmp.nodeID = item;

        // for(i=0; i<MAXTARGET; i++) tmp->Target[i] = -1;
        treeSize++;
        return tmp;
    }

    /* dis play all frequent itemsets in the tree */
    public void displayTree(PrefixTree tree) {
        int i, j, k;
        double conf;
        // System.out.println("tree.len="+ tree.len);
        if (tree == null) {
            return;
        }
        if (tree.len > 4 || tree.len < 0) {
            printf(" here, I got it");
            for (j = 0; j < 3; j++) {
                printf("%d, ", tree.set[j]);
            }
            printf("\n %d", tree.len);
            return;
        }
        if (tree.len > 0) {
            for (j = 0; j < tree.len; j++) {
                printf(" %d  ", tree.set[j]);
            }

            printf("\t  %d", tree.numOfSon);

            if (tree.gSup > 0) {
                System.out.print("\t " + tree.gSup);
                // printf("\tsum[%d], ", tree.gSup);
                for (k = 0; k < maxClass; k++) {
                    printf("[" + k + "]" + tree.lSup[k] + ", ");
                }
            }
            if (tree.token >= 1) {
                printf("\t");
                for (i = 0; i < MAXTARGET; i++) {
                    if (tree.target[i] != -1) {
                        printf("\t %d ", tree.target[i]);
                    }
                }
                printf("\t acc= %.3f", tree.acc);
                printf("\t conf=%.3f", tree.conf);
                System.out.print("\t iscausal: " + tree.iscausal);
            }
            printf("\n");
        }
        for (i = 0; i < tree.numOfSon; i++) {
            displayTree(tree.sonList[i]);
        }
        return;
    }

    /* dis play all frequent itemsets in the tree */
    public void displayTreeByLayer(PrefixTree tree, int layer) {
        int i, j, k;
        double conf;

        if (tree == null) {
            return;
        }

        if (tree.len == layer) {
            for (j = 0; j < tree.len; j++) {
                printf("%d ", tree.set[j]);
            }

            printf("\t : %d", tree.numOfSon);

            if (tree.gSup > 0) {
                System.out.println("tree.gSup: " + tree.gSup);
                // printf("\tsum[%d], ", tree.gSup);
                for (k = 0; k < maxClass; k++) {
                    printf("[%d]%d, ", k, tree.lSup[k]);
                }
            }
            if (tree.token >= 1) {
                printf("\t");
                for (i = 0; i < MAXTARGET; i++) {
                    if (tree.target[i] != -1) {
                        printf("%d ", tree.target[i]);
                    }
                }
                printf("\t acc= %.3f", tree.acc);
                printf("\t conf=%.3f", tree.conf);
            }
            printf("\n");
        }

        for (i = 0; i < tree.numOfSon; i++) {
            displayTreeByLayer(tree.sonList[i], layer);
        }
        return;
    }

    public int countCausalRule(RuleSet ruleset) {
        RuleStru cur;
        int count = 0;
        cur = ruleset.ruleHead;
        //System.out.println(" haharuleset.ruleHead.len="+ cur.len);
        while (cur != null) {
            //if(cur.isCausalRule != null){
            if (cur.isCausalRule == null) {
                //System.out.println("count=null");
                break;
            }
            if (cur.isCausalRule) {
                //System.out.println("haharuleset.ruleHead.len="+cur.len);
                //if(cur.len==2){
                count++;
                //}
                //System.out.println("count="+count);
            }
            //}

            cur = cur.nextRule;
        }
        //System.out.println("allcount="+count);
        return count;

    }


    //start by shu
    public int countCausalRule_Two(RuleSet ruleset) {
        RuleStru cur;
        int count = 0;
        cur = ruleset.ruleHead;
        //System.out.println(" haharuleset.ruleHead.len="+ cur.len);
        //System.out.println(" haharuleset.ruleHead.isCausalRule="+ cur.isCausalRule);
        while (cur != null) {
            //if(cur.isCausalRule != null){
            if (cur.isCausalRule == null) {
                System.out.println("count=null");
                break;
            }
            if (cur.isCausalRule) {
                //System.out.println("haharuleset.ruleHead.len="+cur.len);
                if (cur.len == 2) {
                    count++;
                }
                //System.out.println("count="+count);
            }
            //}

            cur = cur.nextRule;
        }
        //System.out.println("allcount="+count);
        return count;

    }
    //end by shu

    public int report() {
        printf("The number of tree nodes %d \n", treeSize);
        printf("The number of single target rules %d \n", ruleSet.numOfRule);
        if (causal != 0) {

            printf("The number of causal rules %d \n", countCausalRule(ruleSet));
        }
        // printf ("The number of mutiple target rules %d \n", MultiRule);
        // printf ("The loose coverage %f \n", (float) (ToughCov +
        // LooseCov)/MaxData);
        // printf ("The tough coverage %f \n", (float) ToughCov/MaxData);

        // if ((float)ToughCov/MaxData > MAXCOVERAGE) return 1;
        return 1;
    }

    // start by hushu
    public int report_Two() {
        printf("The number of tree nodes %d \n", treeSize);
        printf("The number of single target rules %d \n", ruleSet.numOfRule);
        if (causal != 0) {

            printf("The number of causal rules %d \n", countCausalRule_Two(ruleSet));
        }
        // printf ("The number of mutiple target rules %d \n", MultiRule);
        // printf ("The loose coverage %f \n", (float) (ToughCov +
        // LooseCov)/MaxData);
        // printf ("The tough coverage %f \n", (float) ToughCov/MaxData);

        // if ((float)ToughCov/MaxData > MAXCOVERAGE) return 1;
        return 1;
    }


    //end by hushu


    /* to free the memory a tree hold */
    public void freeTree(PrefixTree tree) {
    }

    public int verification(PrefixTree tree, int layer) {
        int i;
        for (i = 0; i < maxData; i++) {
            countbyTree(tree, dataSpace[i], layer);
        }
        return 1;
    }

    public int verification_Two(PrefixTree tree, int layer) {
        int i;
        for (i = 0; i < maxData; i++) {
            countbyTree_Two(tree, dataSpace[i], layer);
        }
        return 1;
    }

    public int countbyTree(PrefixTree tree, int[] transet, int layer) {
        int i, j, k, count, item, tmp, indexnum, targetvalue;
        PrefixTree[] index;
        PrefixTree tmptree;

        count = 0;

        if (tree == null) {
            return 0;
        }

        index = new PrefixTree[CYCLE];

        indexnum = CYCLE;

        /* this is for first layer count */
        tree.reserve = 0;
        for (i = 0; i < realAtt; i++) {
            item = transet[i]; // transet is the dataSpace row
            // if(NodeCount[item]<MinSup) continue;
            // I comment this line just because I intend to let all items be
            // frequent items
            for (j = tree.reserve; j < tree.numOfSon; j++) {
                tmptree = tree.sonList[j];
                if (tmptree.nodeID > item) {
                    break;
                }
                if ((tmptree.nodeID) == item) {
                    //System.out.println("tmptree.nodeID="+tmptree.nodeID);
                    tree.reserve = j + 1;
                    index[count++] = tmptree;
                    tmptree.reserve = 0;
                    if (count > indexnum - 1) {
                        indexnum += CYCLE;
                        PrefixTree[] tmpIndex = index;
                        index = new PrefixTree[indexnum];
                        System.arraycopy(tmpIndex, 0, index, 0, tmpIndex.length);

                    }
                    break;
                }
            }
            //System.out.println("one");
            tmp = count;
            for (j = 0; j < tmp - 1; j++) {
                //System.out.println("!! beajy003 - Rule - countByTree() - j:"+j+": len:"+index.length+": item:"+index[j]+":");
                tmptree = index[j];
                try {
                    //System.out.println("tmptree.reserve="+tmptree.reserve);
                    //System.out.println("tmptree.numOfSon="+tmptree.numOfSon);
                    for (k = tmptree.reserve; k < tmptree.numOfSon; k++) {
                        // System.out.println("!! beajy003 - Rule - countByTree() - k:"+k+": len:"+tmptree.sonList.length+": item:"+tmptree.sonList[k]+":");

                        if (tmptree.sonList[k].nodeID > item) {
                            break;
                        }

                        if (tmptree.sonList[k].nodeID == item) {
                            // here begins acturally counting
                            //System.out.println("tmptree.sonList[k].nodeID="+tmptree.sonList[k].nodeID);
                            if (tmptree.len == layer - 1) {
                                tmptree.sonList[k].gSup++;
                                targetvalue = transet[realAtt];
                                tmptree.sonList[k].lSup[targetvalue]++;
                            }

                            tmptree.reserve = k + 1;
                            index[count++] = tmptree.sonList[k];
                            tmptree.sonList[k].reserve = 0;
                            if (count > indexnum - 1) {
                                indexnum += CYCLE;
                                // thuc add arraycopy
                                PrefixTree[] tInd = index;
                                index = new PrefixTree[indexnum];
                                System.arraycopy(tInd, 0, index, 0, tInd.length);

                            }

                            break;
                        }
                    }
                } catch (Exception e) {
                    // beajy003 - throws exceptions here because the tree is not
                    // constructed correctly
                }

            }
        }
        free(index);
        return (1);
    }

    public int countbyTree_Two(PrefixTree tree, int[] transet, int layer) {
        int i, j, k, count, item, tmp, indexnum, targetvalue;
        PrefixTree[] index;
        PrefixTree tmptree;

        count = 0;

        if (tree == null) {
            return 0;
        }

        index = new PrefixTree[CYCLE];

        indexnum = CYCLE;

        /* this is for first layer count */
        tree.reserve = 0;
        for (i = 0; i < realAtt; i++) {
            item = transet[i]; // transet is the dataSpace row
            // if(NodeCount[item]<MinSup) continue;
            // I comment this line just because I intend to let all items be
            // frequent items
            for (j = tree.reserve; j < tree.numOfSon; j++) {
                tmptree = tree.sonList1[j];
                if (tmptree.nodeID > item) {
                    break;
                }
                if ((tmptree.nodeID) == item) {
                    //System.out.println("tmptree.nodeID="+tmptree.nodeID);
                    tree.reserve = j + 1;
                    index[count++] = tmptree;
                    tmptree.reserve = 0;
                    if (count > indexnum - 1) {
                        indexnum += CYCLE;
                        PrefixTree[] tmpIndex = index;
                        index = new PrefixTree[indexnum];
                        System.arraycopy(tmpIndex, 0, index, 0, tmpIndex.length);

                    }
                    break;
                }
            }
            //System.out.println("one");
            tmp = count;
            for (j = 0; j < tmp - 1; j++) {
                //System.out.println("!! beajy003 - Rule - countByTree() - j:"+j+": len:"+index.length+": item:"+index[j]+":");
                tmptree = index[j];
                try {
                    //System.out.println("tmptree.reserve="+tmptree.reserve);
                    //System.out.println("tmptree.numOfSon="+tmptree.numOfSon);
                    for (k = tmptree.reserve; k < tmptree.numOfSon; k++) {
                        // System.out.println("!! beajy003 - Rule - countByTree() - k:"+k+": len:"+tmptree.sonList.length+": item:"+tmptree.sonList[k]+":");

                        if (tmptree.sonList[k].nodeID > item) {
                            break;
                        }

                        if (tmptree.sonList[k].nodeID == item) {
                            // here begins acturally counting
                            //System.out.println("tmptree.sonList[k].nodeID="+tmptree.sonList[k].nodeID);
                            if (tmptree.len == layer - 1) {
                                tmptree.sonList[k].gSup++;
                                targetvalue = transet[realAtt];
                                tmptree.sonList[k].lSup[targetvalue]++;
                            }

                            tmptree.reserve = k + 1;
                            index[count++] = tmptree.sonList[k];
                            tmptree.sonList[k].reserve = 0;
                            if (count > indexnum - 1) {
                                indexnum += CYCLE;
                                // thuc add arraycopy
                                PrefixTree[] tInd = index;
                                index = new PrefixTree[indexnum];
                                System.arraycopy(tInd, 0, index, 0, tInd.length);

                            }

                            break;
                        }
                    }
                } catch (Exception e) {
                    // beajy003 - throws exceptions here because the tree is not
                    // constructed correctly
                }

            }
        }
        free(index);
        return (1);
    }

    public int coverageCount(PrefixTree tree, int[][] data, int num) {
        int i, l;

        toughCov = 0;
        looseCov = 0;

        for (i = 0; i < num; i++) {
            countEntry(tree, data[i]);
        }
        return 1;
    }

    /* This is to count the coverage in while database */
    public int countEntry(PrefixTree tree, int[] entry) {
        int i, j, k, l, count, item, tmp, indexnum, looseflag, toughflag;
        PrefixTree[] index;
        PrefixTree tmptree;

        count = 0;
        looseflag = 0;
        toughflag = 0;

        if (tree == null) {
            return 0;
        }

        index = new PrefixTree[1];
        indexnum = CYCLE;

        /* this is for first layer count */
        tree.reserve = 0;
        for (i = 0; i < realAtt; i++) {
            item = entry[i];
            // if(NodeCount[item]<GMinSup) continue;
            for (j = tree.reserve; j < tree.numOfSon; j++) {
                tmptree = tree.sonList[j];
                if (tmptree.nodeID > item) {
                    break;
                }
                if (tmptree.nodeID == item) {

                    if (tmptree.token >= 1) {
                        if (looseflag == 0 && tmptree.target[1] != -1) {
                            looseCov++;
                            looseflag = 1;
                            printf("\n **");
                            for (l = 0; l < tmptree.len; l++) {
                                printf("%d,", tmptree.set[l]);
                            }
                            printf("Target[0] = %d, Target[1] = %d",
                                    tmptree.target[0], tmptree.target[1]);
                        }
                        if (toughflag == 0 && tmptree.target[1] == -1) {
                            toughCov++;
                            toughflag = 1;
                        }
                        if (looseflag != 0 && toughflag != 0) {
                            free(index);
                            return (1);
                        }
                    }

                    tree.reserve = j + 1;
                    index[count++] = tmptree;
                    tmptree.reserve = 0;
                    if (count > indexnum - 1) {
                        indexnum += CYCLE;
                        index = new PrefixTree[indexnum];
                    }
                    break;
                }
            }

            tmp = count;
            for (j = 0; j < tmp - 1; j++) {
                tmptree = index[j];
                for (k = tmptree.reserve; k < tmptree.numOfSon; k++) {
                    if (tmptree.sonList[k].nodeID > item) {
                        break;
                    }
                    if (tmptree.sonList[k].nodeID == item) {

                        if (tmptree.sonList[k].token >= 1) {
                            if (looseflag == 0
                                    && tmptree.sonList[k].target[1] != -1) {
                                looseCov++;
                                looseflag = 1;
                                printf("\n **");
                                for (l = 0; l < tmptree.sonList[k].len; l++) {
                                    printf("%d,", tmptree.sonList[k].set[l]);
                                }
                                printf("Target[0] = %d, Target[1] = %d",
                                        tmptree.sonList[k].target[0],
                                        tmptree.sonList[k].target[1]);

                            }
                            if (toughflag == 0 && tmptree.target[1] == -1) {
                                toughCov++;
                                toughflag = 1;
                            }
                            if (looseflag != 0 && toughflag != 0) {
                                free(index);
                                return (1);
                            }
                        }

                        tmptree.reserve = k + 1;
                        index[count++] = tmptree.sonList[k];
                        tmptree.sonList[k].reserve = 0;
                        if (count > indexnum - 1) {
                            indexnum += CYCLE;
                            index = new PrefixTree[indexnum];
                        }
                        break;
                    }
                }
            }
        }
        free(index);
        return (0);

    }

    public void initRuleSet() {
        // RuleSet is for all rules
        ruleSet = new RuleSet();
        ruleSet.numOfRule = 0;
        ruleSet.ruleHead = null;

        // SignleList is for rules with single condition
        singleList = new RuleSet();
        singleList.numOfRule = 0;
        singleList.ruleHead = null;

    }


    /*
     * if choice == 0, then RuleSet, if choice == 1, then SingleList
     */
    public int writeToRuleSet_CRPA(PrefixTree node, int choice) {

        int flag, i, j, k, targettmp, targetnum;
        RuleStru rulecur = null, ruleahead, tmp;
        double conf, pc, npc, npnc, pnc, p, np;

        //System.out.println("enter writeToRuleSet_CRPA");
        //System.out.println("Start WritetoRuleSet(node, choice), ruleSet.numOfRule: "+ruleSet.numOfRule+"choice: "+choice);
        tmp = new RuleStru();

        tmp.len = node.len;
        tmp.token = 0;

        //System.out.println("enter 1111");

        tmp.antecedent = new int[tmp.len + 1];

        tmp.lSup = new double[maxClass + 1];

        for (i = 0; i < tmp.len; i++) {
            tmp.antecedent[i] = node.set[i];
        }
        //System.out.println("enter writeToRuleSet_CRPA:tmp.antecedent[i] = node.set[i];"+node.set[i]);

        for (i = 0; i < maxClass; i++) {
            tmp.lSup[i] = node.lSup[i];
        }
        //System.out.println("writeToRuleSet_CRPA:tmp.lSup[i] = node.lSup[i]:"+node.lSup[i]);


        k = 0;

        tmp.target[0] = node.target[0];
        targetnum = node.target[0];
        //System.out.println("node.acc="+node.acc+"node.conf="+node.conf+"node.lsup[targetnum]="+node.lSup[targetnum]+"node.gSup="+node.gSup);
        //tmp.accuracy = node.acc;
        //tmp.confidence = node.conf;

        //System.out.println("targetnum = "+targetnum);

        //for(i = 0; i < node.lSup.length; i++)
        //    System.out.print(node.lSup[i] + " ");

        tmp.support = node.lSup[targetnum];
        tmp.attSupport = node.gSup;

        //System.out.println("assignment has been finished");

        // causal rule is a node got token=4
        // if(node.token==4) tmp.isCausalRule=true;
        // We calculate Relative Risk and Odds Ratio
        // We only calculate class[0]. changed Thuc&Jiuyong change to 2 sided
        // calculation
//        p = node.gSup;
//        np = maxData - p;
//
//        pc = node.lSup[node.target[0]];
//        npc = dist[node.target[0]] - pc;
//        pnc = p - pc;
//        npnc = maxData + pc - p - dist[node.target[0]];

        // if(npc<0.0001 || pnc<0.0001) tmp -> OddsRatio = 10000;
        // else tmp -> OddsRatio = (pc*npnc)/(npc*pnc);
//        if (pc < 0.0001) {
//            pc = 0.5;
//        }
//        if (npc < 0.0001) {
//            npc = 0.5;
//        }
//        if (pnc < 0.0001) {
//            pnc = 0.5;
//        }
//        if (p < 0.0001) {
//            p = 0.5;
//        }
//        tmp.oddsRatio = (pc * npnc) / (npc * pnc);
//
//		// if(npc<0.0001 || p<0.0001) tmp -> RelativeRisk = 10000;
//        // else tmp -> RelativeRisk = (pc*np)/(npc*p);
//        tmp.relativeRisk = (pc * np) / (npc * p);
        // System.out.println("tmp.oddsRatio="+tmp.oddsRatio+"tmp.relativeRisk="+tmp.relativeRisk);
        // System.out.println("choice="+choice);

        if (choice == 0) {
            rulecur = ruleSet.ruleHead;
        } else if (choice == 1) {
            rulecur = singleList.ruleHead;
        }

        ruleahead = null;
        // if (rulecur!=null)
        // System.out.println("rulecur!=null, so start while command");
        // else
        // System.out.println("rulecur=null, so skip while, start addRuleTail");

        //System.out.println("writeToRuleSet_CRPA: while (rulecur != null)");
        while (rulecur != null) {
            if (precede(rulecur, tmp)) {

                if (ass == 0) {

                    // if(tmp->Target[0]==rulecur->Target[0] && tmp->Len >
                    // rulecur->Len)
                    // if(Contain(tmp->Antecedent, tmp->Len,
                    // rulecur->Antecedent, rulecur->Len))
                    // return (0);
                }

                ruleahead = rulecur;
                rulecur = rulecur.nextRule;
                continue;
            } else {
                addRuleAhead(rulecur, tmp, choice);
                return (1);
            }
        }
        if (rulecur == null) {
            //System.out.println("I am null");
            addRuleTail(ruleahead, tmp, choice);
        }
        //System.out.println("end of WriteToruleSet, ruleSet.numOfRule:  "+ruleSet.numOfRule);

        //System.out.println("exit writeToRuleSet_CRPA");

        return (1);

    }

    /*
     * if choice == 0, then RuleSet, if choice == 1, then SingleList
     */
    public int writeToRuleSet(PrefixTree node, int choice) {

        int flag, i, j, k, targettmp, targetnum;
        RuleStru rulecur = null, ruleahead, tmp;
        double conf, pc, npc, npnc, pnc, p, np;
        // System.out.println("Start WritetoRuleSet(node, choice), ruleSet.numOfRule: "+ruleSet.numOfRule+"choice: "+choice);
        tmp = new RuleStru();

        tmp.len = node.len;
        tmp.token = 0;

        tmp.antecedent = new int[tmp.len + 1];

        tmp.lSup = new double[maxClass + 1];

        for (i = 0; i < tmp.len; i++) {
            tmp.antecedent[i] = node.set[i];
        }
        for (i = 0; i < maxClass; i++) {
            tmp.lSup[i] = node.lSup[i];
        }

        k = 0;

        tmp.target[0] = node.target[0];
        targetnum = node.target[0];
        //System.out.println("node.acc="+node.acc+"node.conf="+node.conf+"node.lsup[targetnum]="+node.lSup[targetnum]+"node.gSup="+node.gSup);
        tmp.accuracy = node.acc;
        tmp.confidence = node.conf;
        tmp.support = node.lSup[targetnum];
        tmp.attSupport = node.gSup;

        // causal rule is a node got token=4
        // if(node.token==4) tmp.isCausalRule=true;
        // We calculate Relative Risk and Odds Ratio
        // We only calculate class[0]. changed Thuc&Jiuyong change to 2 sided
        // calculation
        p = node.gSup;
        np = maxData - p;

        pc = node.lSup[node.target[0]];
        npc = dist[node.target[0]] - pc;
        pnc = p - pc;
        npnc = maxData + pc - p - dist[node.target[0]];

        // if(npc<0.0001 || pnc<0.0001) tmp -> OddsRatio = 10000;
        // else tmp -> OddsRatio = (pc*npnc)/(npc*pnc);
        if (pc < 0.0001) {
            pc = 0.5;
        }
        if (npc < 0.0001) {
            npc = 0.5;
        }
        if (pnc < 0.0001) {
            pnc = 0.5;
        }
        if (p < 0.0001) {
            p = 0.5;
        }
        tmp.oddsRatio = (pc * npnc) / (npc * pnc);

        // if(npc<0.0001 || p<0.0001) tmp -> RelativeRisk = 10000;
        // else tmp -> RelativeRisk = (pc*np)/(npc*p);
        tmp.relativeRisk = (pc * np) / (npc * p);
        // System.out.println("tmp.oddsRatio="+tmp.oddsRatio+"tmp.relativeRisk="+tmp.relativeRisk);
        // System.out.println("choice="+choice);

        if (choice == 0) {
            rulecur = ruleSet.ruleHead;
        } else if (choice == 1) {
            rulecur = singleList.ruleHead;
        }

        ruleahead = null;
        // if (rulecur!=null)
        // System.out.println("rulecur!=null, so start while command");
        // else
        // System.out.println("rulecur=null, so skip while, start addRuleTail");
        while (rulecur != null) {
            if (precede(rulecur, tmp)) {

                if (ass == 0) {

                    // if(tmp->Target[0]==rulecur->Target[0] && tmp->Len >
                    // rulecur->Len)
                    // if(Contain(tmp->Antecedent, tmp->Len,
                    // rulecur->Antecedent, rulecur->Len))
                    // return (0);
                }

                ruleahead = rulecur;
                rulecur = rulecur.nextRule;
                continue;
            } else {
                addRuleAhead(rulecur, tmp, choice);
                return (1);
            }
        }
        if (rulecur == null) {
            addRuleTail(ruleahead, tmp, choice);
        }
        // System.out.println("end of WriteToruleSet, ruleSet.numOfRule:  "+ruleSet.numOfRule);
        return (1);

    }

    public boolean precede(RuleStru rule1, RuleStru rule2) {

        if (rule1.accuracy > rule2.accuracy) {
            return true;
        }

        if (rule1.accuracy == rule2.accuracy && rule1.support > rule2.support) {
            return true;
        }

        if (rule1.accuracy == rule2.accuracy && rule1.support == rule2.support
                && rule1.len < rule2.len) {
            return true;
        }

        return false;

    }

    public int contain(int[] set1, int len1, int[] set2, int len2) {
        int i, j, k, item1, item2, flag;

        k = 0;
        flag = 0;
        for (i = 0; i < len2; i++) {
            item2 = set2[i];
            for (j = k; j < len1; j++) {
                item1 = set1[j];
                if (item1 == item2) {
                    k = j + 1;
                    flag++;
                    break;
                }
                if (item1 > item2) {
                    return (0);
                }
            }
        }

        if (flag == len2) {
            return (1);
        } else {
            return (0);
        }

    }

    /*
     * if choice = 0, RuleSet if choice = 1, SingleList
     */
    public int addRuleAhead(RuleStru oldrule, RuleStru newrule, int choice) {

        RuleSet ruleset = new RuleSet();

        if (choice == 0) {
            ruleset = ruleSet;
        }
        if (choice == 1) {
            ruleset = singleList;
        }

        ruleset.numOfRule++;

        // //the old rule is the first rule
        if (oldrule.aheadRule == null) {
            ruleset.ruleHead = newrule;
            newrule.aheadRule = null;
        } // / Normal cases
        else {
            oldrule.aheadRule.nextRule = newrule;
            newrule.aheadRule = oldrule.aheadRule;
        }

        newrule.nextRule = oldrule;
        oldrule.aheadRule = newrule;

        return 1;
    }

    /*
     * if choice = 0, RuleSet if choice = 1, SingleList
     */
    public int addRuleTail(RuleStru oldrule, RuleStru newrule, int choice) {

        RuleSet ruleset = new RuleSet();
        if (choice == 1) {
            ruleset = singleList;
        }
        // Thuc add the following if
        if (choice == 0) {
            ruleset = ruleSet;
        }

        ruleset.numOfRule++;

        if (oldrule == null) {
            ruleset.ruleHead = newrule;
            newrule.aheadRule = null;
        } else {
            oldrule.nextRule = newrule;
            newrule.aheadRule = oldrule;
        }

        newrule.nextRule = null;

        return 1;

    }

    /*
     * if choice = 0, RuleSet if choice = 1, SingleList
     */
    public int displayAbsractRule(int choice) {
        int i, j, k;
        RuleStru cur;
        RuleSet ruleset = new RuleSet();
        if (choice == 1) {
            ruleset = singleList;
        }
        // Thuc added the following
        if (choice == 0) {
            ruleset = ruleSet;
        }

        printf("\nThe number of rules is %d", ruleset.numOfRule);

        if (ruleset.numOfRule > 10000) {
            printf("\n The rule set is large, skip printf");
            return (1);
        }

        cur = ruleset.ruleHead;
        while (cur != null) {
            printf("\n");
            for (i = 0; i < cur.len; i++) {
                printf("%d ", cur.antecedent[i]);
            }

            // System.out.println("cur.support="+cur.support);
            printf("\t Sup = %f", cur.support);
            printf("\t Acc = %f", cur.accuracy);
            printf("\t Conf = %f", cur.confidence);
            printf("\t Oddsratio = %f", cur.oddsRatio);
            printf("\t Target = %d", cur.target[0]);
            System.out.print("\t isCausalRule: " + cur.isCausalRule);
            // printf("\t relativeRisk = %f", cur.relativeRisk);
            cur = cur.nextRule;
        }
        printf("\n");

        return (1);

    }

    public void writeReport(String fn, double conf, double lsup) {

        System.out.println("-----------enter writeReport------------");

        FileWriter fWrite;
        BufferedWriter fp;
        int i, j, num, classnum, item;
        int[] rulenum = new int[5];
        double rate, lift, tmp, oRerror, rRerror, n11, n12, n21, n22, n1x, n2x, nx1, nx2;
        RuleStru cur;
        // Time curtime;

        try {
            fWrite = new FileWriter(fn);
            fp = new BufferedWriter(fWrite);

            // curtime = new Time(0);
            // fprintf(fp, fn + "\t\t" + curtime + "\n\n");
            fprintf(fp, "This report is automatically generated by CR-PA Algorithm.\n");
            fprintf(fp, "CR-PA is a causal association rule discovery tool.\n");
            fprintf(fp, "Paper: Discovery of Causal Rules Using Partial Association, (ICDM 2012)\n");

            fprintf(fp, "Total running time is %f milliseconds.\n", total_runtime);

            fprintf(fp, "The MINIMUM SUPPORT = %.2f\n \n", lsup);
            fprintf(fp, "The number of data = %d,\n", maxData);

            for (i = 0; i < maxClass; i++) {
                // System.out.println("!! beajy003 - Rule - writeReport() - printing:"+dist[i]+": class:"+dist[i]+":");
                fprintf(fp, "\t %f in class %s \n", dist[i], className[i]);
            }
            fprintf(fp,
                    "The number of rules = %d, and they are listed as follow. \n\n",
                    ruleSet.numOfRule);

            // fprintf(fp,
            // "\n\n Rules sorted by an interestingness metric in individual classes \n ");
            // We list risk patterns first
            fprintf(fp, "\nRisk patterns for %s \n\n", className[0]);
            rulenum[0] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];

                //System.out.println("classnum = " + classnum);

                if (classnum != 0) {
                    cur = cur.nextRule;
                    continue;
                }
                fprintf(fp, "Pattern %d: \t Length = %d  \n", ++num, cur.len);

                fprintf(fp, "\t \t Causal rule: %s \n", " " + cur.isCausalRule);

                /*

                n11 = cur.lSup[0];
                n12 = cur.attSupport - cur.lSup[0];
                n21 = dist[0] - cur.lSup[0];
                n22 = dist[1] - cur.attSupport + cur.lSup[0];

                n1x = n11 + n12;
                n2x = n21 + n22;
                nx1 = n11 + n21;
                nx2 = n12 + n22;

				// if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (n1x < 1) {
                    n1x = 0.5;
                }
                if (n2x < 1) {
                    n2x = 0.5;
                }
                if (nx1 < 1) {
                    nx1 = 0.5;
                }
                if (nx2 < 1) {
                    nx2 = 0.5;
                }
                rRerror = (n11 * n22 - n12 * n21)
                        / Math.sqrt(n1x * n2x * nx1 * nx2);

				// if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);
                if (n11 < 1) {
                    n11 = 0.5;
                }
                if (n12 < 1) {
                    n12 = 0.5;
                }
                if (n21 < 1) {
                    n21 = 0.5;
                }
                if (n22 < 1) {
                    n22 = 0.5;
                }

                oRerror = cur.oddsRatio
                        * Math.sqrt(1 / n11 + 1 / n12 + 1 / n21 + 1 / n22);

                fprintf(fp, " \t \t OR = %.4f (%.4f) \t RR = %.4f \n\n",
                        cur.oddsRatio, oRerror, cur.relativeRisk);

                */


                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    fprintf(fp, "\t\t %s = %s \n", itemRecord[item].attName,
                            itemRecord[item].attr);
                }
                // System.out.println("\n\t\t Cohort size = "+cur.attSupport+" Percentage = "+(double)
                // cur.attSupport / maxData * 100);
                // fprintf(fp, "\n\t\t Cohort size = %d, Percentage = %.2f \n",
                // cur.attSupport, (double) cur.attSupport / maxData * 100);
                fprintf(fp, "\t\t Contingency table \n");
                /*
                 * for(i=0; i<MaxClass; i++){ if (Dist[i]<0.0001) rate = 0; else
                 * rate = (float)cur->LSup[i]/Dist[i]; tmp =
                 * (float)cur->AttSupport/MaxData; // lift = rate/tmp;
                 * fprintf(fp, "\t\t %s: \t %d  \t%d) \t \n", ClassName[i],
                 * cur->LSup[i], cur->AttSupport-cur->LSup); }
                 */

                fprintf(fp, "\t\t             \t%s  \t%s \n", className[0],
                        className[1]);
                //fprintf(fp, "\t\t pattern     \t%.0f  \t%.0f \n", n11, n12);
                //fprintf(fp, "\t\t non-pattern \t%.0f  \t%.0f \n", n21, n22);

                fprintf(fp, "\n");
                cur = cur.nextRule;
                rulenum[0] = num;
            }

            // We then pretective patterns
            fprintf(fp, "\n Preventive patterns for %s \n\n", className[0]);
            rulenum[1] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];

                //System.out.println("classnum = " + classnum);


                if (classnum != 1) {
                    cur = cur.nextRule;
                    continue;
                }
                fprintf(fp, "Pattern %d: \t Length = %d \n", ++num, cur.len);

                fprintf(fp, "\t \t Causal rule: %s \n", " " + cur.isCausalRule);
                n11 = cur.lSup[0];
                n12 = cur.attSupport - cur.lSup[0];
                n21 = dist[0] - cur.lSup[0];
                n22 = dist[1] - cur.attSupport + cur.lSup[0];

                n1x = n11 + n12;
                n2x = n21 + n22;
                nx1 = n11 + n21;
                nx2 = n12 + n22;

                // if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (n1x < 1) {
                    n1x = 0.5;
                }
                if (n2x < 1) {
                    n2x = 0.5;
                }
                if (nx1 < 1) {
                    nx1 = 0.5;
                }
                if (nx2 < 1) {
                    nx2 = 0.5;
                }
                rRerror = (n11 * n22 - n12 * n21)
                        / Math.sqrt(n1x * n2x * nx1 * nx2);
                // if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);
                if (n11 < 1) {
                    n11 = 0.5;
                }
                if (n12 < 1) {
                    n12 = 0.5;
                }
                if (n21 < 1) {
                    n21 = 0.5;
                }
                if (n22 < 1) {
                    n22 = 0.5;
                }
                oRerror = cur.oddsRatio
                        * Math.sqrt(1 / n11 + 1 / n12 + 1 / n21 + 1 / n22);

                fprintf(fp, " \t \t OR = %.4f (%.4f) \t RR = %.4f \n\n",
                        cur.oddsRatio, oRerror, cur.relativeRisk);

                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    fprintf(fp, "\t\t %s = %s \n", itemRecord[item].attName,
                            itemRecord[item].attr);
                }

                // fprintf(fp, "\n\t\t Cohort size = %d, Percentage = %.2f%\n",
                // cur.attSupport, (double) cur.attSupport / maxData * 100);
                fprintf(fp, "\t\t Distribution  \n");

                /*
                 * for(i=0; i<MaxClass; i++){ if (Dist[i]<0.0001) rate = 0; else
                 * rate = (float)cur->LSup[i]/Dist[i]; tmp =
                 * (float)cur->AttSupport/MaxData; // lift = rate/tmp;
                 * fprintf(fp, "\t\t %s: \t %d (%.2f\%) \n", ClassName[i],
                 * cur->LSup[i], rate*100); }
                 */
                fprintf(fp, "\t\t             \t%s  \t%s \n", className[0],
                        className[1]);
                fprintf(fp, "\t\t pattern     \t%.0f  \t%.0f \n", n11, n12);
                fprintf(fp, "\t\t non-pattern \t%.0f  \t%.0f \n", n21, n22);

                fprintf(fp, "\n");
                cur = cur.nextRule;
                rulenum[1] = num;
            }

            /*
             * //We need not this in this program since we are interested in
             * class 2 only fprintf(fp, "\n\n Summary \n");
             *
             * for(i=0; i<MaxClass; i++){ fprintf(fp,
             * "\t %d rules in class %s\n", rulenum[i], ClassName[i]); }
             *
             * fprintf(fp, "\n");
             */

            /*
             * fprintf(fp,
             * "\n\nRules sorted by interestingness over all classes \n");
             *
             * for(i=0; i<MaxClass; i++){ fprintf(fp,
             * "\t %d rules in class %s\n", rulenum[i], ClassName[i]); }
             *
             * fprintf(fp, "\n");
             *
             * num = 0; cur = RuleSet->RuleHead; while (cur){ fprintf(fp,
             * "Rule %d: \t Interestingness rate = %f \t Length = %d \n", ++num,
             * cur->Confidence, cur->Len); classnum = cur->Target[0];
             * fprintf(fp, "\t Class = %s \n", ClassName[classnum]); for(i=0;
             * i<cur->Len; i++) { item = cur->Antecedent[i]; fprintf(fp,
             * "\t\t %s = %s \n", ItemRecord[item]->AttName,
             * ItemRecord[item]->Attr); }
             *
             * fprintf(fp,
             * "\t Distribution among classes (class name: number in the class, occurrence probability in the class \n"
             * ); for(i=0; i<MaxClass; i++){ if (Dist[i]<0.0001) rate = 0; else
             * rate = (float)cur->LSup[i]/Dist[i]; fprintf(fp,
             * "\t\t %s: \t %d \t %.4f \t %.4f\n", ClassName[i], cur->LSup[i],
             * rate, cur->Accuracy); }
             *
             * fprintf(fp, "\n"); cur = cur->NextRule; }
             */
            fprintf(fp, "The end of the report \n");

            fp.close();
            fWrite.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void outputtoCSVfile(String fn, double conf, double lsup) {

        if (fn != null) {
            return;
        }
        System.out.println("-----------enter outputtoCSVfile------------");


        FileWriter fWrite;
        BufferedWriter fp;
        int i, j, num, classnum, item;
        int[] rulenum = new int[5];
        double rate, lift, tmp, ORerror, RRerror, n11, n12, n21, n22, N1x, N2x, Nx1, Nx2;
        RuleStru cur;
        // Time curtime;

        try {
            fWrite = new FileWriter(fn);
            fp = new BufferedWriter(fWrite);

            // curtime = new Time(0);
            // fprintf(fp, "#%s\t\t%s\n\n", fn, curtime);
            fprintf(fp, "#This report is automatically generated by CR-PA Algorithm.\n");
            fprintf(fp, "#CR-PA is a causal association rule discovery tool.\n");
            fprintf(fp, "#Paper: Discovery of Causal Rules Using Partial Association, (ICDM 2012)\n");

            fprintf(fp, "#Total running time is %f milliseconds.\n", total_runtime);

            fprintf(fp, "The MINIMUM SUPPORT = %.2f\n \n", lsup);
            fprintf(fp, "The number of data = %d,\n", maxData);

            for (i = 0; i < maxClass; i++) {
                fprintf(fp, "\t %f in class %s \n", dist[i], className[i]);
            }

            fprintf(fp,
                    "The number of rules = %d, and they are listed as follow. \n\n",
                    ruleSet.numOfRule);

            // fprintf(fp,
            // "\n\n Rules sorted by an interestingness metric in individual classes \n ");
            fprintf(fp,
                    "#Pattern number, length, Causal Rule, Cohort size, class 0 size, class 1 size, field name, field value, field name, field value, ...");

            // We list risk patterns first
            fprintf(fp, "\n\n#Risk patterns for %s \n\n", className[0]);
            rulenum[0] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];

                //System.out.println("classnum = " + classnum);

                if (classnum != 0) {
                    cur = cur.nextRule;
                    continue;
                }

                fprintf(fp, "%d, %d, ", ++num, cur.len);
                fprintf(fp, "%s,", " " + cur.isCausalRule);

//                if(cur.isCausalRule==true){
//                    fprintf(fp, "%s%d, %s, ", "Rule",++num, "Causal");
//                }
//                else{
//                    //fprintf(fp, "%s%d, %s, ", "Rule",++num, cur.isCausalRule);
//                    fprintf(fp, "%s%d, %s, ", "Rule",++num, "Noncausal");
//                }

                fprintf(fp, "%s,", " " + cur.isCausalRule);
//                fprintf(fp, "%s %d,", "level" ,cur.len);


                n11 = cur.lSup[0];
                n12 = cur.attSupport - cur.lSup[0];


//                n21 = dist[0] - cur.lSup[0];
//                n22 = dist[1] - cur.attSupport + cur.lSup[0];
                /*
                N1x = n11 + n12;
                N2x = n21 + n22;
                Nx1 = n11 + n21;
                Nx2 = n12 + n22;

				// if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (N1x < 1) {
                    N1x = 0.5;
                }
                if (N2x < 1) {
                    N2x = 0.5;
                }
                if (Nx1 < 1) {
                    Nx1 = 0.5;
                }
                if (Nx2 < 1) {
                    Nx2 = 0.5;
                }
                RRerror = (n11 * n22 - n12 * n21)
                        / Math.sqrt(N1x * N2x * Nx1 * Nx2);

				// if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);
                fprintf(fp, " %.4f, %.4f, ", cur.oddsRatio, cur.relativeRisk);

                */

                // CSIRO require the 0 to be normal and 1 to be abnormal, hence
                // the
                // following printout n12 first
                fprintf(fp, " %f, %.0f, %.0f,", cur.attSupport, n12, n11);
                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    fprintf(fp, "%s, %s, ", itemRecord[item].attName,
                            itemRecord[item].attr);
//                    fprintf(fp, " %s%s%s ", itemRecord[item].attName,"=",
//                            itemRecord[item].attr);
                }
//                fprintf(fp, " %s %s%s,","->","target=",className[1]);
//                fprintf(fp, " %s%.0f,%s%.0f,%s%.0f,%s%.0f,", "n11=",n12,"n12=",n11,"n21=",n22,"n22=",n21);

                /*
                 * fprintf(fp, "\t\t             \t%s  \t%s \n", ClassName[0],
                 * ClassName[1]); fprintf(fp,
                 * "\t\t pattern     \t%.0f  \t%.0f \n", n11, n12); fprintf(fp,
                 * "\t\t non-pattern \t%.0f  \t%.0f \n", n21, n22);
                 */
                fprintf(fp, "\n");
                cur = cur.nextRule;
                rulenum[0] = num;
            }

            // We then pretective patterns
            fprintf(fp, "\n#Preventive patterns for %s \n\n", className[0]);
            rulenum[1] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];

                //System.out.println("classnum = " + classnum);

                if (classnum != 1) {
                    cur = cur.nextRule;
                    continue;
                }
                //fprintf(fp, "%s%d, %d, ", "Rule",++num, cur.len);
                fprintf(fp, "%d, %d, ", ++num, cur.len);
                fprintf(fp, "%s,", " " + cur.isCausalRule);
//                if(cur.isCausalRule==true){
//                    fprintf(fp, "%s%d, %s, ", "Rule",++num, "Causal");
//                }
//                else{
//                    //fprintf(fp, "%s%d, %s, ", "Rule",++num, cur.isCausalRule);
//                    fprintf(fp, "%s%d, %s, ", "Rule",++num, "Noncausal");
//                }

                fprintf(fp, "%s,", " " + cur.isCausalRule);
//                fprintf(fp, "%s %d,", "level" ,cur.len);
                n11 = cur.lSup[0];
                n12 = cur.attSupport - cur.lSup[0];


//                n21 = dist[0] - cur.lSup[0];
//                n22 = dist[1] - cur.attSupport + cur.lSup[0];
                /*
                N1x = n11 + n12;
                N2x = n21 + n22;
                Nx1 = n11 + n21;
                Nx2 = n12 + n22;

				// if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (N1x < 1) {
                    N1x = 0.5;
                }
                if (N2x < 1) {
                    N2x = 0.5;
                }
                if (Nx1 < 1) {
                    Nx1 = 0.5;
                }
                if (Nx2 < 1) {
                    Nx2 = 0.5;
                }
                RRerror = (n11 * n22 - n12 * n21)
                        / Math.sqrt(N1x * N2x * Nx1 * Nx2);

				// if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);
                if (n11 < 1) {
                    n11 = 0.5;
                }
                if (n12 < 1) {
                    n12 = 0.5;
                }
                if (n21 < 1) {
                    n21 = 0.5;
                }
                if (n22 < 1) {
                    n22 = 0.5;
                }
                ORerror = cur.oddsRatio
                        * Math.sqrt(1 / n11 + 1 / n12 + 1 / n21 + 1 / n22);

                fprintf(fp, " %.4f, %.4f, ", cur.oddsRatio, cur.relativeRisk);

                */

                fprintf(fp, " %f, %.0f, %.0f,", cur.attSupport, n12, n11);
                //fprintf(fp, " %s%.0f, %s%.0f,%s%.0f,%s%.0f,", "n11=",n11,"n12=",n12,"n21=",n21,"n22=",n22);

                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    fprintf(fp, "%s, %s, ", itemRecord[item].attName,
                            itemRecord[item].attr);
//                    fprintf(fp, " %s%s%s ", itemRecord[item].attName,"=",
//                            itemRecord[item].attr);
                }
//                fprintf(fp, " %s %s%s,","->","target=",className[1]);
//                fprintf(fp, " %s%.0f,%s%.0f,%s%.0f,%s%.0f,", "n11=",n12,"n12=",n11,"n21=",n22,"n22=",n21);


                // fprintf(fp, "\t\t Distribution  \n");

                /*
                 * fprintf(fp, "\t\t             \t%s  \t%s \n", ClassName[0],
                 * ClassName[1]); fprintf(fp,
                 * "\t\t pattern     \t%.0f  \t%.0f \n", n11, n12); fprintf(fp,
                 * "\t\t non-pattern \t%.0f  \t%.0f \n", n21, n22);
                 */
                fprintf(fp, "\n");
                cur = cur.nextRule;
                rulenum[1] = num;
            }

            fprintf(fp, "#The end of the report \n");

            fp.close();
            fWrite.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void outputToCanshowOutput(double conf, double lsup) {

        System.out.println("-----------enter outputtoCSVfile------------");

        int i, j, num, classnum, item;
        int[] rulenum = new int[5];
        double rate, lift, tmp, ORerror, RRerror, n11, n12, n21, n22, N1x, N2x, Nx1, Nx2;
        RuleStru cur;
        // Time curtime;

        StringBuilder sb = new StringBuilder();

        try {

            // curtime = new Time(0);
            // fprintf(fp, "#%s\t\t%s\n\n", fn, curtime);
            fprintf(sb, "#This report is automatically generated by CR-PA Algorithm.\n");
            fprintf(sb, "#CR-PA is a causal association rule discovery tool.\n");
            fprintf(sb, "#Paper: Discovery of Causal Rules Using Partial Association, (ICDM 2012)\n");

            //fprintf(fp, "Total running time is %f milliseconds.\n", total_runtime);

            fprintf(sb, "The MINIMUM SUPPORT = %.2f\n \n", lsup);
            fprintf(sb, "The number of data = %d,\n", maxData);

            for (i = 0; i < maxClass; i++) {
                fprintf(sb, "\t %f in class %s \n", dist[i], className[i]);
            }

            fprintf(sb, "Total running time is %f milliseconds.\n", total_runtime);

            fprintf(sb,
                    "The number of rules = %d, and they are listed as follow. \n\n",
                    ruleSet.numOfRule);

            // fprintf(fp,
            // "\n\n Rules sorted by an interestingness metric in individual classes \n ");
            //fprintf(fp,
            //        "#Pattern number, length, Causal Rule, Cohort size, class 0 size, class 1 size, field name, field value, field name, field value, ...");

            // We list risk patterns first
            //fprintf(fp, "\n\n#Risk patterns for %s \n\n", className[0]);
            //fprintf(fp, "\nRules with target Z= %s \n\n", className[0]);
            rulenum[0] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];

                //System.out.println("classnum = " + classnum);

                if (classnum != 0) {
                    cur = cur.nextRule;
                    continue;
                }

                //fprintf(fp, "%d, %d, ", ++num, cur.len);
                //fprintf(fp, "%s,", " " + cur.isCausalRule);

                if (cur.isCausalRule == true) {
                    fprintf(sb, "%s %d, %s, ", "Rule", ++num, "Causal");
                } else {
                    //fprintf(fp, "%s%d, %s, ", "Rule",++num, cur.isCausalRule);
                    fprintf(sb, "%s %d, %s, ", "Rule", ++num, "Noncausal");
                }

                //fprintf(fp, "%s,", " " + cur.isCausalRule);
                fprintf(sb, "%s %d,", "level", cur.len);


                n11 = cur.lSup[0];
                n12 = cur.attSupport - cur.lSup[0];


                n21 = dist[0] - cur.lSup[0];
                n22 = dist[1] - cur.attSupport + cur.lSup[0];
                /*
                N1x = n11 + n12;
                N2x = n21 + n22;
                Nx1 = n11 + n21;
                Nx2 = n12 + n22;

				// if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (N1x < 1) {
                    N1x = 0.5;
                }
                if (N2x < 1) {
                    N2x = 0.5;
                }
                if (Nx1 < 1) {
                    Nx1 = 0.5;
                }
                if (Nx2 < 1) {
                    Nx2 = 0.5;
                }
                RRerror = (n11 * n22 - n12 * n21)
                        / Math.sqrt(N1x * N2x * Nx1 * Nx2);

				// if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);
                fprintf(fp, " %.4f, %.4f, ", cur.oddsRatio, cur.relativeRisk);

                */

                // CSIRO require the 0 to be normal and 1 to be abnormal, hence
                // the
                // following printout n12 first
                //fprintf(fp, " %f, %.0f, %.0f,", cur.attSupport, n12, n11);
                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    //fprintf(fp, "%s, %s, ", itemRecord[item].attName,
                    //        itemRecord[item].attr);
                    fprintf(sb, " %s%s%s ", itemRecord[item].attName, "=",
                            itemRecord[item].attr);
                }
                fprintf(sb, " %s %s%s,", "->", "Z=", className[1]);
                fprintf(sb, " %s%.0f,%s%.0f,%s%.0f,%s%.0f,", "n11=", n12, "n12=", n11, "n21=", n22, "n22=", n21);

                /*
                 * fprintf(fp, "\t\t             \t%s  \t%s \n", ClassName[0],
                 * ClassName[1]); fprintf(fp,
                 * "\t\t pattern     \t%.0f  \t%.0f \n", n11, n12); fprintf(fp,
                 * "\t\t non-pattern \t%.0f  \t%.0f \n", n21, n22);
                 */
                fprintf(sb, "\n");
                cur = cur.nextRule;
                rulenum[0] = num;
            }

            // We then pretective patterns
            //fprintf(fp, "\n#Preventive patterns for %s \n\n", className[0]);
            fprintf(sb, "\nRules with target Z= %s \n\n", className[1]);
            rulenum[1] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];

                //System.out.println("classnum = " + classnum);

                if (classnum != 1) {
                    cur = cur.nextRule;
                    continue;
                }
                //fprintf(fp, "%s%d, %d, ", "Rule",++num, cur.len);
                //fprintf(fp, "%d, %d, ", ++num, cur.len);
                //fprintf(fp, "%s,", " " + cur.isCausalRule);
                if (cur.isCausalRule == true) {
                    fprintf(sb, "%s %d, %s, ", "Rule", ++num, "Causal");
                } else {
                    //fprintf(fp, "%s%d, %s, ", "Rule",++num, cur.isCausalRule);
                    fprintf(sb, "%s %d, %s, ", "Rule", ++num, "Noncausal");
                }

                //fprintf(fp, "%s,", " " + cur.isCausalRule);
                fprintf(sb, "%s %d,", "level", cur.len);
                n11 = cur.lSup[0];
                n12 = cur.attSupport - cur.lSup[0];


                n21 = dist[0] - cur.lSup[0];
                n22 = dist[1] - cur.attSupport + cur.lSup[0];
                /*
                N1x = n11 + n12;
                N2x = n21 + n22;
                Nx1 = n11 + n21;
                Nx2 = n12 + n22;

				// if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (N1x < 1) {
                    N1x = 0.5;
                }
                if (N2x < 1) {
                    N2x = 0.5;
                }
                if (Nx1 < 1) {
                    Nx1 = 0.5;
                }
                if (Nx2 < 1) {
                    Nx2 = 0.5;
                }
                RRerror = (n11 * n22 - n12 * n21)
                        / Math.sqrt(N1x * N2x * Nx1 * Nx2);

				// if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);
                if (n11 < 1) {
                    n11 = 0.5;
                }
                if (n12 < 1) {
                    n12 = 0.5;
                }
                if (n21 < 1) {
                    n21 = 0.5;
                }
                if (n22 < 1) {
                    n22 = 0.5;
                }
                ORerror = cur.oddsRatio
                        * Math.sqrt(1 / n11 + 1 / n12 + 1 / n21 + 1 / n22);

                fprintf(fp, " %.4f, %.4f, ", cur.oddsRatio, cur.relativeRisk);

                */

                //fprintf(fp, " %f, %.0f, %.0f,", cur.attSupport, n12, n11);
                //fprintf(fp, " %s%.0f, %s%.0f,%s%.0f,%s%.0f,", "n11=",n11,"n12=",n12,"n21=",n21,"n22=",n22);

                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    //fprintf(fp, "%s, %s, ", itemRecord[item].attName,
                    //        itemRecord[item].attr);
                    fprintf(sb, " %s%s%s ", itemRecord[item].attName, "=",
                            itemRecord[item].attr);
                }
                fprintf(sb, " %s %s%s,", "->", "Z=", className[1]);
                fprintf(sb, " %s%.0f,%s%.0f,%s%.0f,%s%.0f,", "n11=", n12, "n12=", n11, "n21=", n22, "n22=", n21);


                // fprintf(fp, "\t\t Distribution  \n");

                /*
                 * fprintf(fp, "\t\t             \t%s  \t%s \n", ClassName[0],
                 * ClassName[1]); fprintf(fp,
                 * "\t\t pattern     \t%.0f  \t%.0f \n", n11, n12); fprintf(fp,
                 * "\t\t non-pattern \t%.0f  \t%.0f \n", n21, n22);
                 */
                fprintf(sb, "\n");
                cur = cur.nextRule;
                rulenum[1] = num;
            }

            fprintf(sb, "#The end of the report \n");
            canShowOutput.showOutputString(sb.toString());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void RuleDisplay(int classnum) {
        int i, j, k, count, num, l, err, cov;
        ItemSetStru setptr;
        /*
         * printf("The rule list for class %s (num = %d),  Number of rules %d \n"
         * , ClassName[classnum],SetGroup.TotalRecord, SetGroup.NumofSet); count
         * = 0;
         *
         * setptr = SetGroup.SupSetList.SetHead; // if(!setptr) return;
         * while(setptr){ printf("rule %d: ", ++count); for(k=0; k<=j; k++){ num
         * = setptr->ItemList[k]; printf("%d ", num); } printf("\n");
         * printf("\t (%d/%d) hold in class, and (%d/%d) outside class\n",
         * setptr->LocalSupport, SetGroup.TotalRecord,
         * (setptr->GlobalSupport-setptr->LocalSupport),
         * (MaxData-SetGroup.TotalRecord)); setptr = setptr-> NextSet; }
         */
        printf("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - \n");
        printf("The rule list for class %s (num = %d),  Number of rules %d, Coverage: %d / %d = %.1f %% \n",
                className[classnum], setGroup.totalRecord, setGroup.numofSet,
                setGroup.coverNum, setGroup.totalRecord,
                ((double) setGroup.coverNum / setGroup.totalRecord) * 100);
        count = 0;
        setptr = setGroup.supSetList.setHead;
        err = cov = 0;
        if (setptr == null) {
            return;
        }
        while (setptr != null) {
            printf("rule %d: ", ++count);
            printf("If   ");
            for (k = 0; k < setptr.numofItems; k++) {
                if (k != 0) {
                    printf("AND ");
                }
                num = setptr.itemList[k];
                printf("%s is %s ", itemRecord[num].attName,
                        itemRecord[num].attr);
                /*
                 * if(EquiAttr[num]){ l=0; while(EquiAttr[num][l]){
                 * printf("OR "); printf("%s is %s ",
                 * ItemRecord[EquiAttr[num][l]]->AttName,
                 * ItemRecord[EquiAttr[num][l]]->Attr); l++; } }
                 */
            }
            printf("  THEN class %s\n", className[classnum]);
            printf("\t (%d/%d) hold in class, and (%d/%d) outside class\n",
                    setptr.localSupport, setGroup.totalRecord,
                    (setptr.globalSupport - setptr.localSupport),
                    (maxData - setGroup.totalRecord));
            err += (setptr.globalSupport - setptr.localSupport);
            cov += setptr.localSupport;
            setptr = setptr.nextSet;
        }
        printf("\n average accuracy = %f    average coverage = %f \n",
                (1.0 - (float) err / cov) * 100, (double) cov
                        / setGroup.numofSet);

    }

    int QuickSortInteger(int[] data, int len) {
        int i, j, k;
        int temp;

        for (i = 0; i < len; i++) {
            k = i;
            for (j = i + 1; j < len; j++) {
                if (data[j] < data[k]) {
                    k = j;
                }
            }
            if (k != i) { /* swap */

                temp = data[k];
                data[k] = data[i];
                data[i] = temp;
            }
        }
        return (1);
    }

    public void writeRule(String fn, double conf, double lsup) {

        FileWriter fWrite;
        BufferedWriter fp;
        int i;
        RuleStru cur;

        try {
            fWrite = new FileWriter(fn);
            fp = new BufferedWriter(fWrite);

            fWrite = new FileWriter(fn);
            fp = new BufferedWriter(fWrite);

            fprintf(fp, "%f %f %d %f ", conf, lsup, maxLayer, minImp);
            fprintf(fp, "%d %d ", ruleSet.numOfRule, ruleSet.defaultValue);

            cur = ruleSet.ruleHead;
            while (cur != null) {
                fprintf(fp, "%d %f %f %ld %d %ld ", cur.len, cur.accuracy,
                        cur.confidence, cur.support, cur.numOfTarget,
                        cur.attSupport);
                for (i = 0; i < maxClass; i++) {
                    fprintf(fp, "%ld ", cur.lSup[i]);
                }
                for (i = 0; i < cur.len; i++) {
                    fprintf(fp, "%d ", cur.antecedent[i]);
                }
                for (i = 0; i < MAXTARGET; i++) {
                    fprintf(fp, "%d ", cur.target[i]);
                }
                cur = cur.nextRule;
            }

            fp.close();
            fWrite.close();
        } catch (Exception e) {

        }

    }

    public void readRule(String fn) {

        double lsup, conf, imp; // gsup, acc
        int flag, j, l, num, defa; // i, targets
        // long lnum, sup, attsup;
        RuleStru cur, ahead = null;

        try {
            File fp = new File(fn);
            Scanner scan = new Scanner(fp);

            // fscanf(fp, "%f %f %d %f ", conf, lsup, l, imp);
            conf = scan.nextDouble();
            lsup = scan.nextDouble();
            l = scan.nextInt();
            imp = scan.nextDouble();

            printf("\n MinLocalSupport = %.4f, MinConfidence = %.2f, MaxLayer = %d, MinImprovement = %f",
                    lsup, conf, l, imp);

            // fscanf(fp, "%d %d ", num, defa);
            num = scan.nextInt();
            defa = scan.nextInt();

            ruleSet.numOfRule = num;
            ruleSet.defaultValue = defa;
            printf("\n the numOfrules = %d, default class is %d", num, defa);

            flag = 1;

            while (scan.hasNextInt()) {
                cur = new RuleStru();

                // fscanf(fp, "%d %f %f %ld %d %ld ", num, acc, conf, sup,
                // targets, attsup);
                cur.len = scan.nextInt(); // num
                cur.accuracy = scan.nextDouble(); // acc
                cur.confidence = scan.nextDouble(); // conf
                cur.support = scan.nextInt(); // sup
                cur.numOfTarget = scan.nextInt(); // targets
                cur.attSupport = scan.nextInt(); // attsup

                cur.lSup = new double[maxClass + 1];

                for (j = 0; j < maxClass; j++) {
                    // fscanf(fp, "%ld ", lnum);
                    cur.lSup[j] = scan.nextInt(); // lnum
                    System.out.format("%ld ", cur.lSup[j]);
                }

                cur.antecedent = new int[cur.len + 1];
                for (j = 0; j < cur.len; j++) {
                    // fscanf(fp, "%d ", num);
                    cur.antecedent[j] = scan.nextInt(); // num
                }

                for (j = 0; j < MAXTARGET; j++) {
                    // fscanf(fp, "%d ", num);
                    cur.target[j] = scan.nextInt(); // num

                }

                if (flag != 0) {
                    ruleSet.ruleHead = cur;
                    cur.aheadRule = null;
                    flag = 0;
                } else {
                    cur.aheadRule = ahead;
                    ahead.nextRule = cur;
                }

                cur.nextRule = null;
                ahead = cur;
            }

            scan.close();
        } catch (Exception e) {

        }
    }

    public void test() {
        int i, j, k, dat;
        RuleStru rulecur;
        int[] row;

        ruleSet.numofCorrect = 0;
        ruleSet.numofError = 0;

        printf("\n testing on %s with %d instances \n", fileName, maxData);

        for (i = 0; i < maxData; i++) {
            row = dataSpace[i];
            rulecur = ruleSet.ruleHead;
            while (rulecur != null) {
                if ((dat = matchTest(row, rulecur)) == -1) {
                    rulecur = rulecur.nextRule;
                    continue;
                } else if (dat != 0) {
                    ruleSet.numofCorrect++;
                    ruleSet.sumCorrect += (double) 1 / rulecur.numOfTarget;
                } else {
                    ruleSet.numofError++;
                }
                break;
            }

            if (rulecur == null) {
                if (ruleSet.defaultValue == -1) {
                    continue;
                }
                if (row[realAtt] == ruleSet.defaultValue) {
                    ruleSet.numofCorrect++;
                } else {
                    ruleSet.numofError++;
                }
            }

        }
        // printf("\n accuracy = %.2f %%",
        // (float)(RuleSet->SumCorrect)/MaxData*100);
        printf("\n accuracy = %.2f %%", (double) (ruleSet.numofCorrect)
                / maxData * 100);
        printf("\n (for rules only) accuracy = %.2f %%",
                (double) ruleSet.numofCorrect
                        / (ruleSet.numofCorrect + ruleSet.numofError) * 100);
        printf("\n untouched = %.2f %%", (double) (maxData
                - ruleSet.numofCorrect - ruleSet.numofError)
                / maxData * 100);
        printf("\n");
    }

    public int matchTest(int[] datptr, RuleStru rule) {
        int i, j, k, item;

        k = 0;
        for (i = 0; i < rule.len; i++) {
            item = rule.antecedent[i];
            for (j = k; j < realAtt; j++) {
                if (datptr[j] > item) {
                    return (-1);
                }
                if (datptr[j] == item) {
                    k = j + 1;
                    break;
                }
            }
            if (j == realAtt) {
                return (-1);
            }
        }

        for (i = 0; i < MAXTARGET; i++) {
            if (rule.target[i] == -1) {
                break;
            }
            if (datptr[realAtt] == rule.target[i]) {
                return (1);
            }
        }
        return (0);

    }

    // / This function is only suitable for single target now.
    public int orderRuleAndSetDefault() {
        // char ** table, lowaccrule[1000];
        int[][] table;
        int[] lowaccrule = new int[1000];
        int i, j, k, dat, max, count;
        int[] distnum = new int[100];
        RuleStru rulecur;
        int[] row;
        double conf, defa;
        RuleLabelStr rulelabel;

        // / There are two special usages in the table. table[i]{MaxData] is the
        // target
        // / and table[i][MaxData+1] is the flag
        table = new int[ruleSet.numOfRule + 1][];

        for (i = 0; i < ruleSet.numOfRule; i++) {
            table[i] = new int[maxData + 3];
        }

        rulelabel = new RuleLabelStr();

        for (i = 0; i < maxClass; i++) {
            distnum[i] = (int) dist[i];
        }

        fillInTable(table);

        k = 0;
        count = 0;
        i = 0;
        while (readRuleLabel(table, rulelabel, k) != 0) {
            if (rulelabel.incorrect == 0) {
                conf = 1;
            } else {
                conf = (float) rulelabel.correct
                        / (rulelabel.correct + rulelabel.incorrect);
            }
            // defa = CurrentDefaultRate(distnum);
            // printf(" the %d th rule, conf = %f defa = %f \n", ++i, conf,
            // defa);
            // if(conf<defa && defa>0.001) {
            if (conf < minConf) {
                lowaccrule[count++] = rulelabel.index;
                continue;
            }
            if (count != 0) {
                freeLowAccRule(table, lowaccrule, count);
                count = 0;
            }
            // printf("selected \n");

            if (k != rulelabel.index) {
                swapRule(k, rulelabel.index);
                swapRow(table, k, rulelabel.index);
            }
            // PrintTable (table);
            updateTable(table, distnum, k);
            k++;
        }

        if (discard != 0) {
            discardRestRule(k);
        }
        ruleSet.defaultValue = currentDefault(distnum);

        free(rulelabel);
        for (i = 0; i < ruleSet.numOfRule; i++) {
            free(table[i]);
        }
        free(table);

        return 1;
    }

    // int ReadRuleLabel (char ** table, RULELABEL * rulelabel, int begin)
    public int readRuleLabel(int[][] table, RuleLabelStr rulelabel, int begin) {
        int i, j, errortmp, correcttmp, correct = 0, error, label = 0;

        error = maxData;
        for (i = begin; i < ruleSet.numOfRule; i++) {
            if (table[i][maxData + 1] != 0) {
                continue;
            }
            errortmp = 0;
            correcttmp = 0;
            for (j = 0; j < maxData; j++) {
                if (table[i][j] == -1) {
                    errortmp++;
                }
                if (table[i][j] == 1) {
                    correcttmp++;
                }
            }

            if (errortmp == 0) {
                rulelabel.index = i;
                rulelabel.correct = correcttmp;
                rulelabel.incorrect = errortmp;
                table[i][maxData + 1] = 1;
                return 1;
            }

            if (errortmp < error) {
                label = i;
                correct = correcttmp;
                error = errortmp;
            }
        }

        if (error != maxData) {
            rulelabel.index = label;
            rulelabel.correct = correct;
            rulelabel.incorrect = error;
            table[label][maxData + 1] = 1;
            return 1;
        } else {
            return 0;
        }
    }

    // int UpdateTable(char **table, int * distnum, int where)
    public int updateTable(int[][] table, int[] distnum, int where) {
        int i, j, k, targetnum;

        for (i = 0; i < maxData; i++) {
            if (table[where][i] == 0) {
                continue;
            }
            if (table[where][i] == 1) {
                targetnum = table[where][maxData];
                distnum[targetnum]--;
            }
            for (j = where; j < ruleSet.numOfRule; j++) {
                table[j][i] = 0;
            }
        }

        return 1;
    }

    // int FreeLowAccRule(char ** table, char * lowaccrule, int count)
    public int freeLowAccRule(int[][] table, int[] lowaccrule, int count) {
        int i, j, k;

        for (i = 0; i < count; i++) {
            k = lowaccrule[i];
            table[k][maxData + 1] = 0;
        }

        return 1;

    }

    public int currentDefault(int[] distnum) {
        int i, dat = 0, max;
        boolean defaultSet = false;

        max = 0;
        for (i = 0; i < maxClass; i++) {
            if (distnum[i] > max) {
                max = distnum[i];
                dat = i;
                defaultSet = true;
            }
        }
        if (defaultSet) {
            return dat;
        } else {
            // System.out.println("!! Rule - currentDefault() - return value not set before return - default to 0");
            return dat;
        }

    }

    /*
     * /// use the large distrbution as default int CurrentDefault(int * distnum
     * ) { int i, dat, max, base;
     *
     * max = 0; for (i=0; i<MaxClass; i++) { base = (float)Dist[i]/MaxData;
     * if(base > max) { max = distnum[i]; dat = i; } }
     *
     * return dat; }
     */
    public double CurrentDefaultRate(int[] distnum) {
        float rate;
        int i, sum, max;

        max = 0;
        sum = 0;
        for (i = 0; i < maxClass; i++) {
            sum += distnum[i];
            if (distnum[i] > max) {
                max = distnum[i];
            }
        }

        if (sum == 0) {
            return 1;
        }
        rate = (float) max / sum;

        return rate;
    }

    public int swapRule(int index1, int index2) {
        int i, j, k;
        RuleStru rule1 = null, rule2 = null, tmp, cur;

        if (index1 == index2) {
            return 1;
        }

        cur = ruleSet.ruleHead;
        k = 0;
        j = 0;
        while (cur != null) {
            if (k == index1) {
                rule1 = cur;
                if (++j >= 2) {
                    break;
                }
            }
            if (k == index2) {
                rule2 = cur;
                if (++j >= 2) {
                    break;
                }
            }
            cur = cur.nextRule;
            k++;
        }

        // / make sure that rule1 is precede rule 2
        if (index2 < index1) {
            tmp = rule2;
            rule1 = rule2;
            rule2 = tmp;
        }

        // / if rule1 and rule2 are not adjacent
        if (rule1.nextRule != rule2) {
            if (rule1.aheadRule != null) {
                rule1.aheadRule.nextRule = rule2;
            } else {
                ruleSet.ruleHead = rule2;
            }

            if (rule1.nextRule != null) {
                rule1.nextRule.aheadRule = rule2;
            }

            if (rule2.aheadRule != null) {
                rule2.aheadRule.nextRule = rule1;
            }
            if (rule2.nextRule != null) {
                rule2.nextRule.aheadRule = rule1;
            }

            tmp = rule1.aheadRule;
            rule1.aheadRule = rule2.aheadRule;
            rule2.aheadRule = tmp;

            tmp = rule1.nextRule;
            rule1.nextRule = rule2.nextRule;
            rule2.nextRule = tmp;
        } // / if rule1 and rule2 are adjacent
        else {
            if (rule1.aheadRule != null) {
                rule1.aheadRule.nextRule = rule2;
            } else {
                ruleSet.ruleHead = rule2;
            }

            if (rule2.nextRule != null) {
                rule2.nextRule.aheadRule = rule1;
            }

            rule2.aheadRule = rule1.aheadRule;
            rule1.nextRule = rule2.nextRule;

            rule2.nextRule = rule1;
            rule1.aheadRule = rule2;
        }

        return (1);

    }

    // int SwapRow(char ** table, int index1, int index2)
    public int swapRow(int[][] table, int index1, int index2) {
        // char *tmp;
        int[] tmp;

        if (index1 == index2) {
            return 1;
        }

        tmp = table[index1];
        table[index1] = table[index2];
        table[index2] = tmp;
        return 1;
    }

    // int FillInTable (char ** table)
    public int fillInTable(int[][] table) {
        int i, j, k, dat;
        RuleStru rulecur;
        int[] row;

        k = 0;
        rulecur = ruleSet.ruleHead;
        while (rulecur != null) {
            for (i = 1; i < maxData; i++) {
                row = dataSpace[i];
                if ((dat = matchTest(row, rulecur)) == -1) {
                    table[k][i] = 0;
                } else if (dat != 0) {
                    table[k][i] = 1;
                } else {
                    table[k][i] = -1;
                }
            }

            // / Cell index[i][MaxData] is used for single target
            // / and Cell index[i][MaxData+1] is used for flag
            table[k][maxData] = rulecur.target[0];
            table[k][maxData + 1] = 0;
            k++;
            rulecur = rulecur.nextRule;
        }
        return 1;

    }

    public int discardRestRule(int index) {
        int i, j, k;
        RuleStru cur, tmp, tail;

        if (index == ruleSet.numOfRule) {
            return 1;
        }

        ruleSet.numOfRule = index;
        cur = ruleSet.ruleHead;
        k = 0;

        while (cur != null) {
            if (k == index) {
                break;
            }
            cur = cur.nextRule;
            k++;
        }

        if (cur != null) {
            tail = cur.aheadRule;
            tail.nextRule = null;
        } else {
            return 0;
        }

        while (cur != null) {
            tmp = cur.nextRule;
            if (cur.len < maxLayer) {
                tail.nextRule = cur;
                cur.aheadRule = cur;
                cur.nextRule = null;
                tail = cur;
                ruleSet.numOfRule++;
            } else {
                free(cur.antecedent);
                free(cur);
            }
            cur = tmp;
        }

        return 1;
    }

    // int PrintTable(char **table)
    // beajy003 - return type changed to void as there are no return statements
    // in the function
    public void printTable(int[][] table) {
        int i, j, k;

        for (i = 0; i < ruleSet.numOfRule; i++) {
            printf(" \n rule %d : ", i);
            for (j = 0; j < maxData; j++) {
                printf("%d, ", table[i][j]);
            }
        }
    }

    public void testAndSetDefult() {
        int i, j = 0, k, dat, max;
        int[] classArray = new int[100];
        RuleStru rulecur;
        int[] row;

        ruleSet.numofCorrect = 0;
        ruleSet.numofError = 0;
        for (i = 0; i < maxClass; i++) {
            classArray[i] = 0;
        }
        ruleSet.defaultValue = -1;

        for (i = 1; i < maxData; i++) {
            row = dataSpace[i];
            rulecur = ruleSet.ruleHead;
            while (rulecur != null) {
                if ((dat = matchTest(row, rulecur)) == -1) {
                    rulecur = rulecur.nextRule;
                    continue;
                } else if (dat != 0) {
                    ruleSet.numofCorrect++;
                } else {
                    ruleSet.numofError++;
                }
                break;
            }
            if (rulecur == null) {
                classArray[row[realAtt]]++;
            }
        }

        max = -1;
        for (i = 0; i < maxClass; i++) {
            if (classArray[i] > max) {
                max = classArray[i];
                j = i;
            }
        }
        ruleSet.defaultValue = j;

        ruleSet.numofCorrect += max;
        ruleSet.numofError = maxData - ruleSet.numofCorrect;
        printf("\n accuracy = %.2f %%", (double) ruleSet.numofCorrect / maxData
                * 100);
        printf("\n unaccurcy = %.2f %%", (double) ruleSet.numofError / maxData
                * 100);

    }

    // beajy003 - return type changed to void as there are no return statements
    // in the function
    public void chooseKcompleterule() {
        int i, j, k, oldnum = 0, newnum;
        int[] record;
        int[] leftatt = new int[20];
        RuleStru rulecur;

        for (i = 0; i < maxData; i++) {
            record = dataSpace[i];
            // We only focues on the last class, here is MaxClass-1
            // if (record[RealAtt] != MaxClass-1) continue;

            rulecur = ruleSet.ruleHead;
            k = 0;
            while (rulecur != null) {
                if (matchTest(record, rulecur) == 1) {
                    if (k == 0) {
                        k = 1;
                        rulecur.token = 1;
                        writeLeftAttribute(rulecur, leftatt);
                        oldnum = rulecur.len;
                    }
                    if (k == complete) {
                        break;
                    }
                    newnum = overLapAttribute(rulecur, leftatt);
                    if (newnum == 0) {
                        rulecur.token = 1;
                        break;
                    }
                    if (newnum < oldnum) {
                        rulecur.token = 1;
                        oldnum = newnum;
                    }
                }
                rulecur = rulecur.nextRule;
            }
        }

        rulecur = ruleSet.ruleHead;
        while (rulecur != null) {
            if (rulecur.token == 0) {
                rulecur = deleteRule(rulecur);
            } else {
                rulecur = rulecur.nextRule;
            }
        }

    }

    public RuleStru deleteRule(RuleStru rule) {

        RuleStru tmp;

        if (rule.aheadRule == null) {
            ruleSet.ruleHead = rule.nextRule;
        } else {
            rule.aheadRule.nextRule = rule.nextRule;
        }

        if (rule.nextRule != null) {
            rule.nextRule.aheadRule = rule.aheadRule;
        }

        ruleSet.numOfRule--;

        tmp = rule.nextRule;

        free(rule.antecedent);
        free(rule);
        free(rule.lSup);

        return tmp;

    }

    public int writeLeftAttribute(RuleStru rule, int[] set) {
        int i;

        set[0] = rule.len;
        for (i = 1; i <= rule.len; i++) {
            set[i] = attribute[rule.antecedent[i]];
        }

        return (1);

    }

    public int overLapAttribute(RuleStru rule, int[] set) {
        int i, j, k, l, num, att;
        int[] flagset = new int[20];

        for (i = 0; i < 20; i++) {
            flagset[i] = 0;
        }

        num = 0;
        l = set[0];
        k = 0;
        for (i = 1; i <= l; i++) {
            att = set[i];
            for (j = k; j < rule.len; j++) {
                if (attribute[rule.antecedent[j]] < att) {
                    continue;
                }
                k = j + 1;
                if (attribute[rule.antecedent[j]] == att) {
                    num++;
                    flagset[i] = 1;
                }
                break;
            }
        }

        k = 1;
        for (i = 1; i <= l; i++) {
            if (flagset[i] != 0) {
                set[k++] = set[i];
            }
        }

        set[0] = num;

        return num;

    }

    public ItemRecord addItemRecord(String attname, String att) {
        ItemRecord tmp;
        tmp = new ItemRecord();

        tmp.attName = "";
        tmp.attName = attname;

        tmp.attr = "";
        tmp.attr = att;

        return (tmp);
    }

    public ContinuousValue addContValue(ContinuousValue ptr) {
        ContinuousValue tmp = new ContinuousValue();
        tmp.ahead = ptr;
        tmp.next = null;
        tmp.contrast = 0;
        return (tmp);
    }

    public int quickSort(double[] data, int len) {
        int i, j, k;
        double temp;

        for (i = 0; i < len; i++) {
            k = i;
            for (j = i + 1; j < len; j++) {
                if (data[j] < data[k]) {
                    k = j;
                }
            }
            if (k != i) { /* swap */

                temp = data[k];
                data[k] = data[i];
                data[i] = temp;
            }
        }
        return (1);
    }

    public ContinuousValue deleteContValue(ContinuousValue ptr) {
        ContinuousValue ahead, next;

        ahead = ptr.ahead;
        next = ptr.next;
        if (ahead != null) {
            ahead.next = next;
        }
        if (next != null) {
            next.ahead = ahead;
        }
        free(ptr);
        return next;
    }

    public void printf(String s, Object... a) {
        System.out.format(s, a);
    }


    public void sprintf(String s, String t, Object... a) {
        System.out.format(s + t, a);
    }

    public void fprintf(BufferedWriter out, String s, Object... a) {
        try {
            out.write(String.format(s, a));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fprintf(StringBuilder sb, String s, Object... a) {
        sb.append(String.format(s, a));
    }


    public void free(double d) {
        d = 0;
    }

    public void free(Object o) {
        o = null;
    }

    public String globalInfo() {
        String ret = "";
        ret += "This is a multipurpose rule discovery tool.\n\t\n\t  \n";
        ret += "\n Simple usage:\n";

        ret += "\t ./rule -f fileName (without extension) \n"
                + "\t -s Local Support (default 0.05) \n	"
                + " -l maximum length of rules (default 4)  \n "
                + "\t -r 1 redundant rules (default no)  \n "
                + "\t -m 1 find subrules for some attribute-value pairs (default no) \n"
                + "\t This program focuses only on the first class in two-class data.  \n "
                + "\t Please put the focused class first  \n"
                + "\t the automatic report is in fileName.report \n \n";
        return ret;
    }

    public String getDatabase_Type() {
        return "DBTYPE";
    }

    public void setDatabase_Type() {
        // System.out.println("!! SETTING DATABASE TYPE");
    }

    public String database_TypeTipText() {
        // System.out.println("!! SETTING DATABASE TIP TEXT");
        return "TOOL TIP TEXT FOR THE TOOL TIP";
    }

    // CR-PA functions
    public void select(int number, int range_start, int range_end) {

        System.out.println("Welcome to function select");


    }


    public void succinct_set(int index, int[][] bm) {
        System.out.println("--------------------succinct_set start------------------");


        int row, column;

        // this is a constant value for judging whether they are frequent.
        double t = 0.02;

        // support threshold, for calculating whether they are positive association.
        double s = 0.95;

        // a chi-square value conditioned on p = 0.05,
        // for judging whether there exists nonzero partial association.
        double e = 3.84;


        row = bm.length;
        column = bm[0].length;

        System.out.println("row = " + row + "," + "column = " + column);


        // Sort rows in ascending order, the larger rows, the more value of 1.
        sortrows(bm);

        // unique matrix of bm and eliminate redundant rows
        //equivalence(bm);

        //for(int i = 0; i < bm.length; i++)
        //Arrays.s();


        for (int[] rows : bm)
            System.out.println(Arrays.toString(rows));


        System.out.println("--------------------succinct_set end------------------");


    }

    public void equivalence(int[][] matrix) {
        System.out.println("-------------function equivalence start-------------");
        int i, j, k, current_unique_row = 0, next_unique_row = 0;
        boolean unique = false, identical = true;

        // Create an int vector for storing numbers of unique rows in matrix.
        // int[] unique_rows = new int[matrix.length];

        //List<int> l = new ArrayList<int>();

        List<Integer> unique_rows_list = new ArrayList<Integer>();
        List<Integer> redundant_rows_list = new ArrayList<Integer>();


        for (i = 0; i < matrix.length; i++) {
            unique = true;
            current_unique_row = i;

            if (unique) {
                // insert the current unique row into list of unique rows
                unique_rows_list.add(current_unique_row);

                //for(j = 0; j < matrix[0].length; j++)
                //    System.out.print(matrix[current_unique_row][j]);
            }

            // The row follows by the current unique row
            k = current_unique_row + 1;

            while (k <= matrix.length) {
                for (j = 0; j < matrix[0].length; j++) {
                    // Compare the current unique row with the following rows.
                    // If the following rows are identical to the current unique row,
                    // skiping the following rows will be applied. Otherwise, the current
                    // rows will be identified as the current unique row.
                    if (matrix[current_unique_row][j] != matrix[k][j]) {
                        identical = false;
                    }
                }

                if (identical) {
                    redundant_rows_list.add(k);
                } else {
                    // Record the following unique row
                    if (next_unique_row == 0)
                        next_unique_row = k;

                    //for(j = 0; j < matrix[0].length; j++)
                    //    System.out.print(matrix[k][j]);
                }

                identical = true;
                k++;
            }

            i = next_unique_row;

            System.out.println();
        }


        System.out.println("-------------function equivalence end-------------");
    }

    public void sortrows(int[][] matrix) {
        for (int[] r : matrix)
            System.out.println(Arrays.toString(r));

        System.out.println("----");

        Arrays.sort(matrix, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                int sum_o1 = 0, sum_o2 = 0;
                for (int i : o1)
                    sum_o1 += i;

                for (int i : o2)
                    sum_o2 += i;

                return ((Integer) sum_o1).compareTo(sum_o2);
            }
        });

        for (int[] r : matrix)
            System.out.println(Arrays.toString(r));

        System.out.println("row = " + matrix.length + "," + "column = " + matrix[0].length);

    }

    // CR-PA functions

}
