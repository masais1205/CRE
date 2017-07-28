package cre.algorithm.crcs;


import cre.algorithm.CanShowOutput;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class CRCS {

    final int FALSE = 0;
    final int TRUE = 1;
    final int LOCAL = 0;
    final int GLOBAL = 1;
    final int ALL = 0;
    final int CYCLE = 10;
    final int Nil = 0; /* null pointer */
    final int IGNORE = 1; /* special attribute status: do not use */
    final int DISCRETE = 2; /* Discreet: collect values as data read */
    final int CONTINUOUS = 3; /* continuous attribute */
    final double PURITY = 0.99; /* the number is considered as 1 */
    final int MAXTARGET = 4; /* the number of the maximum targets */
    final double MAXCOVERAGE = 0.95;

    int maxClass;
    int maxDiscrVal;
    double total_runtime;
    String fileName;
    File f;                  //hs, for delete the .data and .names file in outputfile
    String[] className;
    String[] attName;
    int[] maxAttVal;
    String[] specialStatus;
    AttributeCode[] attCode;
    ItemRecord[] itemRecord;
    int[][] attValue; // to store attribute values under an attribute
    int[] attribute; // to store attribute name an item belongs
    int maxAtt;
    int realAtt;
    int maxData;
    int maxDataBak;
    double[][] rawDataSpace;
    int[][] dataSpace;
    int[][] dataSpaceBak;
    column_object[] dataSpacestat;
    int maxItem;
    int treeSize;
    int singleRule;
    int multiRule;
    int toughCov;
    int looseCov;
    int opt;
    int ass;
    int Non;
    int maxLayer;
    int maxControl;
    int fast;
    int discard;
    int complete;
    int excl;
    int sub;
    int causal;
    int[][] controlSingleVar;  // record controlled variables of single cause
    int statisticTest = 0;     // 0: low bound (leftend); 1: odd ratio threshold
    int confidenceTest = 0;
    int chooseMethod;
    double Assconfidencelevel = 1.96;  // confidence level, 1.645(90%), 1.96(95%), 2.33(98%), 2.58(99%)
    double Causalconfidencelevel = 1.96;  // confidence level, 1.645(90%), 1.96(95%), 2.33(98%), 2.58(99%)
    double ChisquareValue;  //hs, ChisquareValue is for x^2 ,the value is 3.84(0.95) or 2.71(0.90) or 6.64(0.99)
    double staThreshold;
    double PaValue; //hs, PaValue is for PA , the value is 3.84(0.95) or 2.71(0.90) or 6.64(0.99)

    // modified by mss
    int controlAttribute = 0;
    ////
    int hashcoding = 0;
    long zrange = 0x7fffffffL;
    long zzrange = 0x7fffffffffffffffL;
    Random r = new Random();
    long[] z;
    long zz;
    ////
    SetGroupStru setGroup;

    int[][] counter;
    int gMinSup;
    int[] lMinSup;
    double minConf;
    double minImp;
    double[] dist;
    int pruning;
    PrefixTree allSet;
    int maxTarget;
    double maxConf;
    int maxRuleAllowed;
    int heuristic;
    int[] ChosenTest;//hs.add for user choose test attribute
    int[] ChosenControl;//hs.add for user choose control attribute
    int Controlmethod;//hs.add for user choose control method(recommended or Forced)

    RuleSet ruleSet;
    RuleSet ruleSet1;
    RuleSet singleList;
    RuleSet singleList1;
    RuleSet singleListBak;

    double weight_n;
    double weight_p;

    double counter1;
    double counter2;
    double derivableRule;

    char delimiter;
    int item_Id_Ceiling = 50;

    //beajy003
    String storeName = "";
    File nf;
    Scanner scan;
    String scannedLine;
    double gsup;
    int[] itemsToOutput;//to out put the dataset to pc
    //int[] lowlsupItems=new int[maxItem];
    //int   llsIndex=0;

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

        int nodeID;
        int token; /* Normal = 0, rule formed = 1, Terminate = 2 */
        int len;
        double value;
        int[] set;
        double gSup;
        double[] lSup;
        double[] subSup;
        PrefixTree[] subNode;
        int target[] = new int[MAXTARGET];
        int numOfSon;
        int numOfSon1;
        int memForSon; /* Record the memory space for sons */
        int reserve; /* this is for recording the place of node when counting */
        double acc;
        double conf;
        PrefixTree[] sonList;
        PrefixTree[] sonList1;
        PrefixTree father;
        boolean iscausal;
        int issupport;
    }

    public class SubRule {
        public SubRule(String namebase, double gsup) {
            int i, j, k, count;
            int item = 0;
            RuleStru rulecur;

            Scanner ui = new Scanner(System.in);

            backupData();
            rulecur = singleListBak.ruleHead;
            if (rulecur != null)
                printf("No subrules \n ");
            count = 0;

            while (rulecur != null) {
                if (count++ > 10)
                    break;
                // Prepare data
                item = rulecur.antecedent[0];

                printf("prepare sub rules for %s = %s (item %d, No. of records = %d) \n", itemRecord[item].attName, itemRecord[item].attr, item, maxData);
                flushDataSpace(rulecur);
                gMinSup = (int) (maxData * gsup + 0.5);
                // Find rules
                freeTree(allSet);
                freeAllRules(ruleSet);
                freeAllRules(ruleSet1);
                freeAllRules(singleList);
                freeAllRules(singleList1);
                initialCount();
                determineParameter(gsup);
                initWholeTree(allSet);
                k = 2;

                while (candidateGen(allSet, k) != 0) {
                    verification(allSet, k);
                    ruleSelectAndPruning(allSet, k);
                    if (++k > 3)
                        break;
                }

                // write output for all optimal rules
                sprintf(fileName, "%s_%s_%s", namebase, itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + "_opt.report";
                writeReport(fileName, minConf, gsup);

                sprintf(fileName, "%s_%s_%s", namebase, itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + "_opt.csv";
                outputtoCSVfile(fileName, minConf, gsup);

                // choose k-optimal rules
                chooseKcompleterule();

                // write output for k-optimal rules
                sprintf(fileName, "%s_%s_%s", namebase, itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + ".report";
                writeReport(fileName, minConf, gsup);

                sprintf(fileName, "%s_%s_%s", namebase, itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + ".csv";
                outputtoCSVfile(fileName, minConf, gsup);

                // point to the next rules
                rulecur = rulecur.nextRule;
            }

            // The following part gets an input from the keyboard, and find
            // subrules for the item

            printf("\n The following are codes for all attribute-value pairs \n");

            for (i = 1; i < maxItem + 1; i++)
                printf("%d  \t %s =  %s\n", i, itemRecord[i].attName, itemRecord[i].attr);

            printf("\n***Please enter a number for mining sub rules. Enter 0 to terminate the program***\n");
            // scanf("%d", item);
            item = ui.nextInt();
            if (item < 0 || item > maxItem) {
                printf("Invalid item code. Exit \n");
                System.exit(0);
            }

            while (item > 0) {

                printf("prepare sub rules for %s = %s (item %d, No. of records = %d) \n", itemRecord[item].attName, itemRecord[item].attr, item, maxData);
                // rulecur store a pseudo rule, information is not correct
                rulecur.antecedent[0] = item;
                flushDataSpace(rulecur);
                gMinSup = (int) (maxData * gsup + 0.5);
                // Find rules
                freeTree(allSet);
                freeAllRules(ruleSet);
                freeAllRules(ruleSet1);
                freeAllRules(singleList);
                freeAllRules(singleList1);
                initialCount();
                determineParameter(gsup);
                initWholeTree(allSet);
                k = 2;

                while (candidateGen(allSet, k) != 0) {
                    verification(allSet, k);
                    ruleSelectAndPruning(allSet, k);
                    if (++k > 3)
                        break;
                }

                // write output for all optimal rules
                sprintf(fileName, "%s_%s_%s", namebase, itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + "_opt.csv";
                outputtoCSVfile(fileName, minConf, gsup);

                sprintf(fileName, "%s_%s_%s", namebase, itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + "_opt.report";
                writeReport(fileName, minConf, gsup);

                // choose k-optimal rules
                chooseKcompleterule();

                // write output for k-optimal rules
                sprintf(fileName, "%s_%s_%s", namebase, itemRecord[item].attName, itemRecord[item].attr);
                fileName = fileName + ".report";
                writeReport(fileName, minConf, gsup);

                sprintf(fileName, "%s_%s_%s", namebase, itemRecord[item].attName, itemRecord[item].attr);
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
                for (j = 0; j < realAtt; j++)
                    dataSpaceBak[i][j] = dataSpace[i][j];
            }

            tmpruleset = new RuleSet();
            tmpruleset.numOfRule = 0;
            tmpruleset.ruleHead = null;

            singleListBak = singleList;
            singleList = tmpruleset;
            singleList1 = tmpruleset;

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
                    for (j = 0; j < realAtt; j++)
                        dataSpace[line][j] = row[j];
                    line++;
                }
            }

            // clear the remaining data space
            for (i = maxData; i < maxDataBak; i++)
                for (j = 0; j < realAtt; j++)
                    dataSpace[i][j] = 0;
        }

        public void freeAllRules(RuleSet ruleset) {
            RuleStru cur, last, next;

            ruleset.numOfRule = 0;
            if (ruleset.ruleHead == null)
                return;

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

    public CRCS() {
        super();
        //System.out.println("!! beajy003 - Beajy003Cluster - Beajy003Cluster() - Begin");
    }

    private CanShowOutput canShowOutput;

    public CRCS(int argc, char[] argv,
                PreprocessingLogic.retclass preprocessRet,
                CRCSConfig.P CRCHPUI_Parameters,
                CRCSConfig.Values userControl_values,
                CanShowOutput canShowOutput) {
        this.canShowOutput = canShowOutput;
        int o;
        int i, j, k, flag, max, min;
        // TODO sort out Time and Clock
        // TODO sort out ctime method in print statements
        //Time begin_t, end_t;
        //Clock begin_c, end_c;
        double cdura1, cdura2, cdura3;

        double t1, t2, t3 = 0;
        String namebase = "";
        String tmp;
        long startTime = System.currentTimeMillis();//hs.add

        attName = preprocessRet.attName;
        //attValue = ret.attValue;

        attribute = preprocessRet.attribute;
        className = preprocessRet.className;
        dataSpace = preprocessRet.dataSpace;

        fileName = preprocessRet.fileName;
        //itemRecord = ret.itemRecord;

        item_Id_Ceiling = preprocessRet.item_Id_Ceiling;
        //System.out.println("item_Id_Ceiling ="+ ret.item_Id_Ceiling);
        //hs. item_ID_Ceiling is depend on the sum of items
        //System.out.println(item_Id_Ceiling);

        itemRecord = new ItemRecord[item_Id_Ceiling];
        for (int m = 0; m < item_Id_Ceiling; m++) {
            itemRecord[m] = addItemRecord("", "");
        }

        for (int m = 0; (m < item_Id_Ceiling && preprocessRet.itemRecord[m] != null); m++) {
            itemRecord[m].attName = preprocessRet.itemRecord[m].attName;
            itemRecord[m].attr = preprocessRet.itemRecord[m].attr;
        }

        //maxAtt = ret.maxAtt;
        //maxAttVal = ret.maxAttVal;

        maxAtt = preprocessRet.maxAtt;
        maxAttVal = preprocessRet.maxAttVal;

        maxClass = preprocessRet.maxClass;
        maxData = preprocessRet.maxData;
        maxItem = preprocessRet.maxItem;
        namebase = preprocessRet.namebase;
        //nf = ret.nf;
        nf = preprocessRet.nf;
        //rawDataSpace = ret.rawDataSpace;
        realAtt = preprocessRet.realAtt;
        //scan = ret.scan;
        //scannedLine = ret.scannedLine;
        //specialStatus = ret.specialStatus;

        scan = preprocessRet.scan;
        scannedLine = preprocessRet.scannedLine;
        specialStatus = preprocessRet.specialStatus;

        // beajy003 changed number of arguments to match the removal of the file
        // name for execution. From 3 to 2.
        if (argc < 2) {
            printf("\t This is a causal association rule discovery tool.\n\t This program was authored by Prof. Jiuyong Li (www.unisanet.unisa.edu.au/staff/homepage.asp?name=jiuyong.li). \n\t Contact jiuyong@unisa.edu.au to obtain a manual \n");
            printf("\n Simple usage:\n");

            printf("\t ./rule -f fileName (without extension) \n" + "\t -s Local Support (default 0.05) \n	" + " -l maximum length of rules (default 4)  \n " + "\t -r 1 redundant rules (default no)  \n " + "\t -m 1 find subrules for some attribute-value pairs (default no) \n"
                    + "\t This program focuses only on the first class in two-class data.  \n " + "\t Please put the focused class first  \n" + "\t the automatic report is in fileName.report \n \n");
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
        Non = 0;
        sub = 0;
        //maxLayer = 4;
        maxLayer = CRCHPUI_Parameters.num_combinedvariables;
        staThreshold = CRCHPUI_Parameters.oddsratio;
        ChisquareValue = CRCHPUI_Parameters.ChisquareValue; //hs.
        ChosenTest = userControl_values.ChosenTest;//hs.add for user choose test attribute
        ChosenControl = userControl_values.ChosenControl;//hs.add for user choose control attribute
        Controlmethod = userControl_values.Controlmethod;
        //PaValue = p.PaValue; //hs.
        fast = 0;
        //maxControl=10;
        //gsup = 0.05;
        gsup = CRCHPUI_Parameters.gsup;
        // the minimum confidence has not been used, and the minimum odds ratio
        // is set to 1.5
        minConf = 0.6;
        minImp = 0.01;
        discard = 0;
        complete = 1;
        maxRuleAllowed = 10000;
        // These two have to be on
        excl = 1;
        heuristic = 1;

        fileName = "";
        //namebase = ret.namebase;

        //begin_t = new Time(0);
        //printf("\t\t\t\t\t %s \n ", begin_t);

        printf("\t This is a causal association rule discovery tool.\n\t This program was authored by Prof. Jiuyong Li (www.unisanet.unisa.edu.au/staff/homepage.asp?name=jiuyong.li).\n\t Contact jiuyong@unisa.edu.au to obtain a manual \n\n\n");

		/* Process options */

        // Beajy003 Code for accepting input

        for (int n = 0; n < argv.length; n++) {
            //  System.out.println("argv:"+argv[n]);
            if (argv[n] == '-') {
                if (n != 0) {
                    if (argv[n - 1] != ' ') break;
                }
                switch (argv[n + 1]) {
                    case 'f':
                        fileName = "";
                        int fileNameCharacter = n + 3;
                        while (fileNameCharacter < argv.length && argv[fileNameCharacter] != ' ') {
                            fileName += argv[fileNameCharacter];
                            fileNameCharacter++;
                        }
                        printf("\t File Name: %s\n", fileName);
                        break;

                    case 's':
                        String globalSupport = "";
                        int globalSupportCharacter = n + 3;
                        while (globalSupportCharacter < argv.length && argv[globalSupportCharacter] != ' ') {
                            globalSupport += argv[globalSupportCharacter];
                            globalSupportCharacter++;
                        }
                        gsup = Double.parseDouble(globalSupport);

                        printf("\t Global Support = %.4f \n", gsup);
                        break;

                    case 'c':
                        confidenceTest = 1;
                        String minimumConfidence = "";
                        int minimumConfidenceCharacter = n + 3;
                        while (minimumConfidenceCharacter < argv.length && argv[minimumConfidenceCharacter] != ' ') {
                            minimumConfidence += argv[minimumConfidenceCharacter];
                            minimumConfidenceCharacter++;
                        }
                        minConf = Double.parseDouble(minimumConfidence);

                        printf("\t Global Confidence = %.4f \n", minConf);
                        break;

                    case 'l':
                        String maximumLayer = "";
                        int maximumLayerCharacter = n + 3;
                        while (maximumLayerCharacter < argv.length && argv[maximumLayerCharacter] != ' ') {
                            maximumLayer += argv[maximumLayerCharacter];
                            maximumLayerCharacter++;
                        }
                        maxLayer = Integer.parseInt(maximumLayer);

                        printf("\t Max Layer = % d \n", maxLayer);
                        break;
                    /*
                //control number of control variables
				case 'z':
					String maximumControl = "";
					int maximumControlCharacter = n + 3;
					while (maximumControlCharacter < argv.length && argv[maximumControlCharacter] != ' ') {
						maximumControl += argv[maximumControlCharacter];
						maximumControlCharacter++;
					}
					maxControl = Integer.parseInt(maximumControl);

					printf("\t Maximum number of controlled variables = % d \n", maxControl);
					break;
				*/
                    case 'i':
                        String minimumImp = "";
                        int minimumImpCharacter = n + 3;
                        while (minimumImpCharacter < argv.length && argv[minimumImpCharacter] != ' ') {
                            minimumImp += argv[minimumImpCharacter];
                            minimumImpCharacter++;
                        }
                        minImp = Double.parseDouble(minimumImp);

                        printf("\t MinImp = % f \n", minImp);
                        break;

                    case 'o':
                        opt = 1;
                        printf("\t Mining Optimal Rule Set \n");
                        break;

                    case 'a':
                        ass = 1;
                        printf("\t Mining Complete Rule Set \n");
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
                        while (completeSolutionCharacter < argv.length && argv[completeSolutionCharacter] != ' ') {
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

                    //Thuc put causal
                    case 'x':

                        causal = 1;
                        printf("\t Mining causal rules \n");
                        break;

                    //mss set causal option
                    case 't':
                        statisticTest = 1;
//                                    String staThr = "";
//                                    int staCharacter = n + 3;
//                                    while (staCharacter < argv.length && argv[staCharacter] != ' ') {
//                                            staThr += argv[staCharacter];
//                                            staCharacter++;
//                                    }
//                                    staThreshold = Double.parseDouble(staThr);

                        printf("\t Statistic Threshold (odds ratio) = %.4f \n", staThreshold);
                        break;
                    case 'b':


                        chooseMethod = 1;

                        break;


                    case 'n':
                        Non = 1;
                        opt = 1;
                        printf("\t Mining Non-redundant rules \n");
                        break;

                    // modified by mss
                    case 'k':
                        controlAttribute = 1;
                        printf("\t Control attribute level \n");
                        break;

                    case 'h':
                        hashcoding = 1;
                        printf("\t Hash function is employed \n");
                        break;

                    case 'z':
                        fast = 1;
                        printf("\t Using CR-CS fast implementation \n");
                        break;
                    ////

                    case '?':
                        printf("## ERROR - Rule - Rule() - Dash given without an option - Character no:" + n + ": proceding letter:" + argv[n + 1] + ":");
                        System.exit(1);
                }
                n = n + 2;
            }
        }

        //begin_c = new Clock();
        //begin_t = new Time(0);

        //namebase = fileName;

        //getNames();


        // modified by mss
        //for(i=0; i<realAtt; i++)
        //	attValue[i] = trimzeros(attValue[i]);
        ////

//		System.out.println("\n\n");
//		for(i=0; i<maxClass; i++) {
//			System.out.format("claas %d = %s \n", i, className[i]);
//		}
//                for(i=0; i<maxAtt; i++)	{
//                    System.out.format("Attname %d = %s\n", i, attName[i]);
//                }
//                for(i=1; i<maxItem+1; i++) {
//                    if(itemRecord[i] != null) {
//                            System.out.format("item%d,  \t\t real: %s,  \t\t code %s\n", i, itemRecord[i].attr, itemRecord[i].attName);
//                    } else {
//                            //System.out.println("!! Rule - Rule() - attempted to print itemRecord[i] i:"+i+":");
//                    }
//                }
//
//                for(i=0; i<maxAtt; i++)	{
//                    if(specialStatus != null) {
//    //	    		System.out.format("%d = %s  ", i, specialStatus[i]);
//                    } else {
//                            //System.out.println("!! Rule - Rule() - attempted to print specialStatus[i] i:"+i+":");
//                    }
//                 }
//	    System.out.println("\n");

//print out the data

//		printf("MaxClass=%d\t MaxAtt = %d\t  RealAtt=%d\t MaxItem=%d\n", maxClass, maxAtt, realAtt, maxItem);
    /*
        for(i=0; i<realAtt; i++){
			System.out.format("\n");
			for(j=0; j<maxAttVal[i]; j++) {
				System.out.format("%d, ", attValue[i][j]);
			}
		}



		max = 0;
		min = 1000;
		for (i = 0; i < realAtt; i++) {
			if (maxAttVal[i] > max)
				max = maxAttVal[i];
			if (maxAttVal[i] < min)
				min = maxAttVal[i];
		}

		k = 0;


		for(i=1; i<maxItem+2; i++){
			if(k++ %10 == 0) {
				System.out.format("\n");
			}
			System.out.format("%d, ", attribute[i]);
		}
*/


        //read data
        //getData(".data");


//                int[][] test;
//                test=tobinary(dataSpace);
//                for(i=0; i<maxData; i++) {
//			System.out.format("%d: ", i+1);
//			for(j=0; j<maxItem+1; j++) {
//				System.out.format("%d,", test[i][j]);
//			}
//			System.out.print("\n");
//		}

//                writebinarytoCSV(test);
        //sort data
        //  sortArray(dataSpace);
        gMinSup = (int) (maxData * gsup + 0.5);
        //System.out.println("!! beajy003 - Rule - Rule() - gMinSup:"+gMinSup+":");

        // beajy003 - display all data collected
//		for(i=0; i<maxData; i++) {
//			System.out.format("%d: ", i+1);
//			for(j=0; j<realAtt+1; j++) {
//				System.out.format("%d,", dataSpace[i][j]);
//			}
//			System.out.print("\n");
//		}
//

		/* beajy003 - display the data values that the data contains
        for(i=0; i<maxItem; i++) {
			System.out.format("item %d  attribute: %s range: %s\n ", i, itemRecord[i].attName, itemRecord[i].attr);
		}
		*/


        //      System.out.println("All stats after getData: realAtt: "+realAtt+"; maxItem: "+maxItem);
        //	System.out.format("MaxData = %d \n", maxData);
        //      System.out.println("Dimension of dataspace: "+dataSpace.length+" and "+dataSpace[realAtt].length);


        //long startTime = System.currentTimeMillis();
        if (ChosenTest.length == 0) {
            generateFirstLevelRules();




		/*

         //       displayTree(allSet);

    //For Pc test
//                System.out.println("number of nodes:"+allSet.numOfSon);
                itemsToOutput=new int[allSet.numOfSon];
                for(int m=0; m<allSet.numOfSon; m++){

                  // System.out.format("node.lsup:, %f%n",allSet.sonList[m].lSup[0]);
                 //  System.out.println("local support: "+gMinSup);
               //    if(allSet.sonList[m].lSup[0]>gMinSup) itemsToOutput[m]=allSet.sonList[m].nodeID;
             //      if(allSet.sonList[m].lSup[1]>gMinSup) itemsToOutput[m]=allSet.sonList[m].nodeID;
                    if(allSet.sonList[m].target!=null) itemsToOutput[m]=allSet.sonList[m].nodeID;
                }
                itemsToOutput=trimzeros(itemsToOutput);
               // printRecord(itemsToOutput);
//
                int[][] test;
                test=tobinary(dataSpace);
//                //extract the items satisfy the support
                int[][] toOutput;
                toOutput=tobinarywithsupport(test);
                writebinarytoCSVwithSupport(toOutput);
             //   System.exit(1);



*/

            //put the myLocalMap here


            if (causal != 0) {
                ////
                if (hashcoding == 1) {
                    initialHash();
//                		for(i=0; i<z.length; i++)
//                			System.out.print(z[i]+" ");
//            			System.out.println();
                }
                ////
                causalTest(allSet);

                //causalTest(ruleSet, 2);
//                displayAbsractRule(0);
            }
            long endTime = System.currentTimeMillis();
            total_runtime = (endTime - startTime);

            printf("\n Report of %d layer \n", 1);
            report();

            System.out.println("Layer 1 took: " + (endTime - startTime) + " ms");

            //   displayTree(allSet);
            //       int[] testcombinenoncfd;
            //       int[] test=new int[2];
            //      test[0]=3;
            //       test[1]=7;

         /*
             //   System.out.println("TESTomg:"+Arrays.asList(dataSpace[1]).contains(test[0]));
                testcombinenoncfd=findnonConfounders(test);
                for(int x=0; x<testcombinenoncfd.length; x++){
                    System.out.println("testnoncfd: "+testcombinenoncfd[x]);
                }
        /*
                ArrayList<int[]> fairdts;
                fairdts=generateFairDataset(32);
                int indfair=1;
                for(int[] x:fairdts){
                    System.out.format("%d: ", indfair++);
                    for(int y=0; y<x.length; y++){
                        System.out.format("%d, ", x[y]);

                    }
                    System.out.print("\n");
                }
       */
            //       ArrayList<int[]> fairdts;
            //        int[] test=new int[2];
            //        test[0]=3;
            //        test[1]=7;
            //         fairdts=generateFairDataset(3);

            //      System.out.println("causalRule? "+causalRule(3,0));

            //print fairdataset
           /*
                for(int x=0; x<fairdts.length; x++){
                    System.out.format("%d: ", x+1);
                    for(int y=0; y<fairdts[x].length; y++){
                        System.out.format("%d, ", fairdts[x][y]);
                    }
                    System.out.print("\n");
                }

                //find nonconfounders

                int[] listcfd;
            //    listcfd=findnonConfounders(5);
            //    System.out.println("hehe"+Arrays.asList(listcfd).contains((listcfd)));
                //validate the rule on fair dataset.

                //print
           //     for (int q=0;q< listcfd.length; q++){
           //         System.out.println("item in noncfd list: "+listcfd[q]);
           //     }
         /*
                int[] tes =new int[3];
                tes[0]=45;
                tes[1]=46;
                tes[2]=47;
                friends(46, tes);




               // for(int x=1;x<maxItem+1; x++){
               //     System.out.println("attributes["+x+"]="+itemRecord[x].attName);
               // }
               //remove items with the same attribute with confounder items



                //non-confounders=singleList-listcfdrelated


          /*    for each item in the singleList do
           *        find confounder(item) return a list
           *        generate the fair data set for item
           *        validatecausalrule


            */
            k = 2;
            // printf("\n This is the %d th layer \n", k-1);
            // displayTree(allSet);

            // coverageCount(allSet, dataSpace, maxData);
            //candidateGen(allSet,2);
            //displayTree(allSet);
            //	System.out.println("End of testing candidatea");
            while (candidateGen(allSet, k) > 0) {
                displayTree(allSet);
                if (k > maxLayer)
                    break;

//                    long startTime1 = System.currentTimeMillis();
                // printf("\n This is the %d th layer, before pruning \n ", k);

                verification(allSet, k);
                // displayTree(allSet);

                //                System.out.println("not select rules and pruning yet");
                ruleSelectAndPruning(allSet, k);
                for (int a = 0; a < MAXTARGET; a++) {
                    if (allSet.target[a] != -1) {
                        RuleStru cur1;
                        cur1 = ruleSet.ruleHead;
                        while (cur1 != null) {
                            if (cur1.len == k) {
                                int[] temp3 = new int[k];
                                for (int v = 0; v < k; v++) {
                                    temp3[v] = cur1.antecedent[v];
                                }

                                System.out.print("temp3: ");
                                printRecord(temp3);
                            }
                            cur1 = cur1.nextRule;
                        }
                    }
                }
                //  printf("\n This is the %d th layer, After pruning \n ", k);
                displayTree(allSet);
                //  report();
                //causal test
                if (causal != 0) {

                    causalTest(allSet, k);
                }
                long endTime1 = System.currentTimeMillis();
                total_runtime = (endTime1 - startTime);

                //   printf("\n This is the %d th layer, After causaltest \n ", k);
                //   displayAbsractRule(0);

                // displayTree(allSet);

                // CoverageCount(AllSet, DataSpace, MaxData);

                printf("\n Report of %d layers \n", k);

                // if(!Opt && !Ass) { if (Report ()) break; }
                // else Report();

                report();
                System.out.println("Layer:" + k + " took " + (endTime1 - startTime) + " ms");

                if (++k > maxLayer)
                    break;
            }

            // int[] test=new int[4];
            //test[0]=3;
            // test[1]=5;
            // test[2]=7;
            // test[3]=46;
            // causalRule(test,0);

            //  System.out.println("number of rules="+ruleSet.numOfRule);

            //end_c = new Clock();
            //end_t = new Time(0);

            // / This is to remove the default prediction

            // beajy003 set in class instantiation.
            // RuleSet.defaultValue = -1;

//		 displayTree(allSet);

            // DisplayAbsractRule (0);

            fileName = namebase;
            fileName = fileName + "_opt.report";
            //writeReport(fileName, minConf, gsup);

            fileName = namebase;
            fileName = fileName + "_opt.csv";
            outputtoCSVfile(fileName, minConf, gsup);

            outputToCanShowOutput(minConf, gsup);


            f = new File("current.data");
            f.delete();

            f = new File("current.names");
            f.delete();
        } else if (ChosenTest.length == 1) {
            generateFirstLevelRules();

            if (causal != 0) {
                ////
                if (hashcoding == 1) {
                    initialHash();
//                		for(i=0; i<z.length; i++)
//                			System.out.print(z[i]+" ");
//            			System.out.println();
                }
                ////
                causalTest_One(allSet);

                //causalTest(ruleSet, 2);
//                displayAbsractRule(0);
            }
            long endTime = System.currentTimeMillis();
            total_runtime = (endTime - startTime);

            printf("\n Report of %d layer \n", 1);
            report();

            System.out.println("Layer 1 took: " + (endTime - startTime) + " ms");

            fileName = namebase;
            fileName = fileName + "_opt.report";
            //writeReport(fileName, minConf, gsup);

            fileName = namebase;
            fileName = fileName + "_opt.csv";
            outputtoCSVfile(fileName, minConf, gsup);

            fileName = namebase;
            outputToCanShowOutput(minConf, gsup);


            f = new File("current.data");
            f.delete();

            f = new File("current.names");
            f.delete();
            return;

        } else if (ChosenTest.length >= 2) {
            generateFirstLevelRules();
            if (causal != 0) {
                ////
                if (hashcoding == 1) {
                    initialHash();
//                		for(i=0; i<z.length; i++)
//                			System.out.print(z[i]+" ");
//            			System.out.println();
                }
                ////
                causalTest_One(allSet);

                //causalTest(ruleSet, 2);
//                displayAbsractRule(0);
            }

            long endTime = System.currentTimeMillis();
            total_runtime = (endTime - startTime);

            printf("\n Report of %d layer \n", 1);
            report();

            System.out.println("Layer 1 took: " + (endTime - startTime) + " ms");

            if (maxLayer == 1) {
                fileName = namebase;
                fileName = fileName + "_opt.report";
                //writeReport(fileName, minConf, gsup);

                fileName = namebase;
                fileName = fileName + "_opt.csv";
                outputtoCSVfile(fileName, minConf, gsup);

                fileName = namebase;
                outputToCanShowOutput(minConf, gsup);


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
                    if((ruleSet.ruleHead!=null)){
                        ruleSet.ruleHead.isCausalRule = true;
                    }
                    */

            //System.out.println("candidateGen_Two(allSet, k)="+candidateGen_Two(allSet, k));
            while (candidateGen_Two(allSet, k) > 0) {
                displayTree(allSet);
                if (k > maxLayer)
                    break;

//                    long startTime1 = System.currentTimeMillis();
                // printf("\n This is the %d th layer, before pruning \n ", k);

                verification_Two(allSet, k);
                // displayTree(allSet);

                //                System.out.println("not select rules and pruning yet");
                ruleSelectAndPruning_Two(allSet, k);
                for (int a = 0; a < MAXTARGET; a++) {
                    if (allSet.target[a] != -1) {
                        RuleStru cur1;
                        cur1 = ruleSet.ruleHead;
                        while (cur1 != null) {
                            if (cur1.len == k) {
                                int[] temp3 = new int[k];
                                for (int v = 0; v < k; v++) {
                                    temp3[v] = cur1.antecedent[v];
                                }

                                System.out.print("temp3: ");
                                printRecord(temp3);
                            }
                            cur1 = cur1.nextRule;
                        }
                    }
                }
                //  printf("\n This is the %d th layer, After pruning \n ", k);
                displayTree(allSet);
                //  report();
                //causal test
                if (causal != 0) {

                    causalTest(allSet, k);
                }
                long endTime1 = System.currentTimeMillis();
                total_runtime = (endTime1 - startTime);

                //   printf("\n This is the %d th layer, After causaltest \n ", k);
                //   displayAbsractRule(0);

                // displayTree(allSet);

                // CoverageCount(AllSet, DataSpace, MaxData);

                printf("\n Report of %d layers \n", k);

                // if(!Opt && !Ass) { if (Report ()) break; }
                // else Report();

                report();
                System.out.println("Layer:" + k + " took " + (endTime1 - startTime) + " ms");

                if (++k > maxLayer)
                    break;
            }

            // int[] test=new int[4];
            //test[0]=3;
            // test[1]=5;
            // test[2]=7;
            // test[3]=46;
            // causalRule(test,0);

            //  System.out.println("number of rules="+ruleSet.numOfRule);

            //end_c = new Clock();
            //end_t = new Time(0);

            // / This is to remove the default prediction

            // beajy003 set in class instantiation.
            // RuleSet.defaultValue = -1;

//		 displayTree(allSet);

            // DisplayAbsractRule (0);

            fileName = namebase;
            fileName = fileName + "_opt.report";
            //writeReport(fileName, minConf, gsup);

            fileName = namebase;
            fileName = fileName + "_opt.csv";
            outputtoCSVfile(fileName, minConf, gsup);

            fileName = namebase;
            outputToCanShowOutput(minConf, gsup);


            f = new File("current.data");
            f.delete();

            f = new File("current.names");
            f.delete();
        }

        // Test ();

        //t1 = end_t.getTime() - begin_t.getTime();
        // cdura1 = (double) (end_c - begin_c)/CLOCKS_PER_SEC;
        // printf("the cpu time for rule forming is %f \n", cdura1);

        if (ass != 0)
            System.exit(0);

        // TestAndSetDefult();

        //begin_c = new Clock();
        //begin_t = new Time(0);

        if (opt == 0)
            chooseKcompleterule();

        //end_c = new Clock();
        //end_t = new Time(0);

        //t2 = end_t.getTime() - begin_t.getTime();
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
        //printf("\n the runing time is %.0f", (t1 + t2 + t3));
        //printf("\n \t\t finish time is %s ", end_t);

        // Search for subrules
        if (sub == 0) {
            System.out.println("here");
            return;

            //System.exit(0);
        }
        fileName = namebase;
        fileName = fileName + "_SingleRuleList.report";
        //writeReport(fileName, minConf, gsup);

        fileName = namebase;
        fileName = fileName + "_SingleRuleList.csv";
        outputtoCSVfile(fileName, minConf, gsup);

        f = new File("current.data");
        f.delete();

        f = new File("current.names");
        f.delete();

        new SubRule(namebase, gsup);


    }

    //write data to file with corresponding support
    public void writebinarytoCSVwithSupport(int[][] data) {
        try {
            FileWriter writer = new FileWriter("Binarydatawithsupport.csv");
//                //header
            for (int i = 0; i < (itemsToOutput.length); i++) {
                int index = itemsToOutput[i];
                if (itemRecord[index] != null) {
                    writer.append(itemRecord[index].attName + ": " + itemRecord[index].attr);
                    writer.append(',');
                    //  System.out.format("item%d,  \t\t real: %s,  \t\t code %s\n", i, itemRecord[i].attr, itemRecord[i].attName);
                }

            }
            writer.append(className[0] + "/" + className[1]);
            writer.append('\n');
            writer.flush();


            int j;
            for (int i = 0; i < data.length; i++) {
                for (j = 0; j < (data[1].length - 1); j++) {
                    writer.append(String.valueOf(data[i][j]));
                    writer.append(',');
                }
                writer.append(String.valueOf(data[i][j]));
                writer.append('\n');
                writer.flush();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //write the data back to csv
    public void writebinarytoCSV(int[][] data) {
        try {
            FileWriter writer = new FileWriter("Binarydata.csv");
            //header
            for (int i = 1; i < maxItem + 1; i++) {
                if (itemRecord[i] != null) {
                    writer.append(itemRecord[i].attName + ": " + itemRecord[i].attr);
                    writer.append(',');
                    //  System.out.format("item%d,  \t\t real: %s,  \t\t code %s\n", i, itemRecord[i].attr, itemRecord[i].attName);
                }

            }
            writer.append(className[0] + "/" + className[1]);
            writer.append('\n');
            writer.flush();


            int j;
            for (int i = 0; i < data.length; i++) {
                for (j = 0; j < (data[1].length - 1); j++) {
                    writer.append(String.valueOf(data[i][j]));
                    writer.append(',');
                }
                writer.append(String.valueOf(data[i][j]));
                writer.append('\n');
                writer.flush();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //return the binary dataset for running in the PC algorithm
    public int[][] tobinarywithsupport(int[][] data) {
        int[][] result = new int[maxData][itemsToOutput.length + 1];
        //extract only the columns in the itemsToOutput

        for (int i = 0; i < maxData; i++) {
            for (int j = 0; j < itemsToOutput.length; j++) {
                result[i][j] = data[i][itemsToOutput[j] - 1];
            }
            result[i][itemsToOutput.length] = data[i][maxItem];
        }

        return result;

    }

    //convert to categorical data
//        public int[][] tocategory(int[][] data){
//            int[][] categorydata =new int[maxData][realAtt+1];
//            for(int i=0; i<maxData; i++){
//                for (int j=0; j<realAtt; j++){
//
//                }
//            }
//
//            return categorydata;
//        }
    //convert data to binary
    public int[][] tobinary(int[][] data) {
//            int rows=data.length;
//            int cols=data[1].length;
        int[][] binarydata = new int[maxData][maxItem + 1];
        for (int i = 0; i < maxData; i++) {
            for (int j = 0; j < realAtt; j++) {
                int item = data[i][j];
                if (item != 0) binarydata[i][item - 1] = 1;
            }
            //class: change 0 to 1 and 1 to 0
            if (data[i][realAtt] == 0) binarydata[i][maxItem] = 1;
            else binarydata[i][maxItem] = 0;
        }

        return binarydata;
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

        initialCount();

        determineParameter(gsup);

        //Print out the counter

//                for (int x=0; x<counter.length; x++){
//                    for (int y=0; y<counter[x].length; y++){
//                       System.out.println("counter["+x+"]["+y+"]="+counter[x][y]);
//                    }
//                }


        initRuleSet();

        //hs,add
        initCOtable();

        initWholeTree(allSet);


//		System.out.print(" Display rules in singleList - ");
//                displayAbsractRule (1);
//                System.out.print(" Display rules in ruleSet - ");
//		displayAbsractRule (0);


        // report();

    }

    public ArrayList<int[]> generateFairDataset(int[] item) {
        int[] listnoncfd;
        int[] listnoncfdbefore;
        int ChosenControlsumItemnumber = 0;
        if (ChosenControl.length != 0) {
            for (int m = 0; m < ChosenControl.length; m++) {
                for (int i = dataSpacestat[ChosenControl[m] - 1].min; i <= dataSpacestat[ChosenControl[m] - 1].max; i++) {
                    ChosenControlsumItemnumber++;
                }

            }

        }
        System.out.println("ChosenControlsumItemnumber" + ChosenControlsumItemnumber);
        int[] listcontrol = new int[ChosenControlsumItemnumber];//hs.add

        ArrayList<int[]> fairDataset = new ArrayList<int[]>();
        ArrayList<int[]> itemhalf = new ArrayList<int[]>();
        ArrayList<int[]> friendhalf = new ArrayList<int[]>();
         /*
           int[] allItem=new int[maxItem];
           for(int x=0; x<maxItem;x++){
               allItem[x]=x+1;
           }
          */


        //         int[][] fairDataset=new int[maxData][realAtt+1];
        if (ChosenControl.length == 0) {
            System.out.println("hushu1");
            System.out.println("hushusingleList1.numofRule=" + singleList1.numOfRule);
            System.out.println("hushusingleList.numofRule=" + singleList.numOfRule);
            if (ChosenTest.length == 0) {
                listnoncfd = findnonConfounders(item);
            } else {
                listnoncfd = findnonConfounders_singleList1(item);
            }
            printRecord(listnoncfd);
            for (int i = 0; i < item.length; i++) {
                System.out.println("Find nonconfounders for " + item[i]);
            }
            System.out.println("length=" + listnoncfd.length);
        } else if (Controlmethod == 0) {
            System.out.println("hushu2");
            if (ChosenTest.length == 0) {
                listnoncfdbefore = findnonConfounders(item);
            } else {
                listnoncfdbefore = findnonConfounders_singleList1(item);
            }
            for (int i = 0; i < item.length; i++) {
                System.out.println("Find nonconfounders for " + item[i]);
            }
            printRecord(listnoncfdbefore);
            int listcontrolnumber = 0;
            for (int m = 0; m < ChosenControl.length; m++) {
                for (int i = dataSpacestat[ChosenControl[m] - 1].min; i <= dataSpacestat[ChosenControl[m] - 1].max; i++) {
                    for (int j = 0; j < listnoncfdbefore.length; j++) {
                        if (i == listnoncfdbefore[j]) {
                            listcontrol[listcontrolnumber++] = i;
                        }
                    }
                }
            }
            listnoncfd = listcontrol;
            System.out.println("length=" + listnoncfdbefore.length);

        } else {
            System.out.println("hushu3");
            int listcontrolnumber = 0;
            for (int m = 0; m < ChosenControl.length; m++) {
                for (int i = dataSpacestat[ChosenControl[m] - 1].min; i <= dataSpacestat[ChosenControl[m] - 1].max; i++) {
                    listcontrol[listcontrolnumber++] = i;
                }
            }
            listnoncfd = listcontrol;
            System.out.println("length=" + listnoncfd.length);
        }

        //System.out.println("listnoncfd for combine");
        //		printRecord(item);
        //System.out.println("before");
        //printRecord(listnoncfd);
        //Fast version, use 10 items in the control
        if (fast == 1) {
            maxControl = 10;
            //Thuc add to set number of control variables
            if (listnoncfd.length > maxControl) {
                int[] temp = listnoncfd;
                listnoncfd = new int[maxControl];
                System.arraycopy(temp, 0, listnoncfd, 0, maxControl);
            }
        }
        //printRecord(listnoncfd);

        //System.out.println("after");
        //printRecord(listnoncfd);
        for (int x = 0; x < maxData; x++) {
            //    System.out.println("Incoming data record: "+(x+1));
            //trim the class value. if nonconfounder is 1 and we donot trim we got the problem
            int[] recordcopy = new int[realAtt];
            for (int w = 0; w < realAtt; w++) {
                recordcopy[w] = dataSpace[x][w];
            }
            //    printRecord(dataSpace[x]);
            //    printRecord(recordcopy);
            //    System.out.println("listnoncfd: ");
            //   printRecord(listnoncfd);
            //   int valid=1;
            // printRecord(recordcopy);
            //       int[] temp=new int[listnoncfd.length];
            //  temp[0]=item;
            //     int tempind=0;
               /*
                for(int y=0; y<listnoncfd.length; y++){

                    //for each item in noncfd list, check if the current record contains this item
                //    if(Arrays.binarySearch(dataSpace[x], listnoncfd[y])>=0){
                 //
                //        temp[tempind++]=listnoncfd[y];
                //    }
                    if(Arrays.binarySearch(recordcopy, listnoncfd[y])<0){
                         //   System.out.println("listnoncfd member: "+listnoncfd[y]);
                            //if the record does not contains the item, need to check if it contains item's friends
                            valid=0;
                            int[] friendsy;
                            friendsy=friends(listnoncfd[y], listnoncfd);

                            for(int v=0; v<friendsy.length; v++){
                                if(Arrays.binarySearch(recordcopy, friendsy[v])>=0) {
                             //       printRecord(recordcopy);
                            //      System.out.println("last chance thank to "+friendsy[v]);
                                    valid=1;
                                }
                            }
                            if (valid==0){
                             //   System.out.println("invalid record, need to be removed from the fairdataset, record"+(x+1));
                                break;
                            }

                    }
                }


              */

            // if(valid==1) {
            int search = 0;
            for (int g = 0; g < item.length; g++) {
                if (Arrays.binarySearch(recordcopy, item[g]) >= 0) search = search + 1;
            }
            if (search == item.length) {
                itemhalf.add(dataSpace[x]);
                //     fairDataset.add(dataSpace[x]);
            } else {
                friendhalf.add(dataSpace[x]);
                //consider 37.  30, 07, 00 are not valid. 4&0/7/8 or 3/4/0&8 is ok.
                       /*
                        int fr=0;
                        for(int p=0;p<item.length;p++){
                           if(friends(item[p],recordcopy).length>0) fr=fr+1;
                        }
                        if(fr!=0){
                        friendhalf.add(dataSpace[x]);
                //        fairDataset.add(dataSpace[x]);
                         }
                        */
            }
            //       System.out.println("This record is valid: ");
            //       printRecord(dataSpace[x]);
            //}
            //Arrays.asList(dataSpace[x]).containsAll(Arrays.asList(temp));
        }
        //check if record in itemhalf intersects with listnoncfd = record in friendhalf intersects with listnoncfd
        //   System.out.println("Print itemhalf");
        //   printArrayList(itemhalf);

        //   System.out.println("Print friendhalf");
        //   printArrayList(friendhalf);
        //    ArrayList<int[]> copyfairDataset=new ArrayList<int[]>();
        //  copyfairDataset=fairDataset;

        ////
        if (hashcoding == 1 && listnoncfd.length > 10) {
            System.out.println("I do");
            ArrayList<Integer> itemHash = new ArrayList<Integer>();
            ArrayList<Integer> friendHash = new ArrayList<Integer>();

            for (int[] itemrecord : itemhalf)
                itemHash.add(hash(intersection(itemrecord, listnoncfd, item)));
            for (int[] friendrecord : friendhalf)
                friendHash.add(hash(intersection(friendrecord, listnoncfd, item)));

//             	for(int k=0; k<z.length; k++)
//             		System.out.print(z[k]+" ");
//             	System.out.println();

            while (itemHash.size() > 0) {
                ArrayList<int[]> arrayitemtemp = new ArrayList<int[]>();
                ArrayList<Integer> arrayitemHash = new ArrayList<Integer>();
                ArrayList<int[]> arrayfriendtemp = new ArrayList<int[]>();
                ArrayList<Integer> arrayfriendHash = new ArrayList<Integer>();
                ArrayList<int[]> itemequiclass = new ArrayList<int[]>();
                int[] itemequiclassNum = new int[100];

                int[] controltemp = intersection(itemhalf.get(0), listnoncfd, item);
                int controlhash = itemHash.get(0);
                int[] itemequiclassSize = new int[1000000];
                int itemequiclassSizeInd = 0;
                int[] friendequiclassSize = new int[1000000];
                int friendequiclassSizeInd = 0;

                itemequiclass.add(controltemp);
                itemequiclassNum[0]++;

// 		        	for(int k=0; k<itemHash.size(); k++)
// 		        		if(controlhash == itemHash.get(k)) {
// 		        			arrayitemtemp.add(itemhalf.get(k));
// 		        			arrayitemHash.add(itemHash.get(k));
// 		        		}


                for (int k = 1; k < itemHash.size(); k++) {
                    if (controlhash == itemHash.get(k)) {
                        int flag = 0;
                        for (int l = 0; l < itemequiclass.size(); l++)
                            if (Arrays.equals(itemequiclass.get(0), intersection(itemhalf.get(k), listnoncfd, item))) {
                                itemequiclassNum[l]++;
                                flag = 1;
                            }
                        if (flag == 0) {
                            itemequiclass.add(intersection(itemhalf.get(k), listnoncfd, item));
                            itemequiclassNum[itemequiclass.size() - 1]++;


                        }
// 		        			if(!Arrays.equals(controltemp, intersection(itemhalf.get(k), listnoncfd, item))) {
// 								System.out.println("two hash value: "+hash(intersection(itemhalf.get(0), listnoncfd, item))+", "+hash(intersection(itemhalf.get(k), listnoncfd, item)));
// 								System.out.println("two hash value: "+controlhash+", "+itemHash.get(k));
// 								printRecord(intersection(controltemp, listnoncfd, item));
// 								printRecord(intersection(itemhalf.get(k), listnoncfd, item));
// 		        			}
                    }
                }

                int maxcolumn = 0;
                for (int l = 0; l < itemequiclass.size(); l++)
                    if (itemequiclassNum[l] > maxcolumn)
                        maxcolumn = l;
                for (int k = 0; k < itemHash.size(); k++) {
                    if (Arrays.equals(itemequiclass.get(maxcolumn), intersection(itemhalf.get(k), listnoncfd, item))) {
                        arrayitemtemp.add(itemhalf.get(k));
                        arrayitemHash.add(itemHash.get(k));
                        itemequiclassSize[itemequiclassSizeInd++] = k;
                    }
                }

                // add by mss
                for (int l = 0; l < itemequiclassSizeInd; l++) {
// 		        		printRecord(intersection(itemhalf.get(itemequiclassSize[l]-l), listnoncfd, item));
                    itemhalf.remove(itemequiclassSize[l] - l);
// 		        		itemhalf.subList(itemequiclassSize[l]-l, itemequiclassSize[l]-l+1).clear();
                    itemHash.remove(itemequiclassSize[l] - l);
// 		        		itemHash.subList(itemequiclassSize[l]-l, itemequiclassSize[l]-l+1).clear();
                }
// 		               itemhalf.removeAll(arrayitemtemp);
// 		               itemHash.removeAll(arrayitemHash);


// 			        for(int k=0; k<friendhalf.size(); k++)
// 	                   if(controlhash == friedndHash.get(k)) {
// 	                       arrayfriendtemp.add(friendhalf.get(k));
// 	                       arrayfriendHash.add(friedndHash.get(k));
// 	                   }


                for (int k = 0; k < friendhalf.size(); k++) {
                    if (controlhash == friendHash.get(k)
                            && Arrays.equals(itemequiclass.get(maxcolumn), intersection(friendhalf.get(k), listnoncfd, item))) {
                        arrayfriendtemp.add(friendhalf.get(k));
                        arrayfriendHash.add(friendHash.get(k));
                        friendequiclassSize[friendequiclassSizeInd++] = k;
                    }
                }
                // add by mss
                for (int l = 0; l < friendequiclassSizeInd; l++) {
//		        		System.out.println(friendequiclassSize[l]);
                    friendhalf.remove(friendequiclassSize[l] - l);
// 		        		friendhalf.subList(friendequiclassSize[l]-l, friendequiclassSize[l]-l+1).clear();
                    friendHash.remove(friendequiclassSize[l] - l);
//                   		friedndHash.subList(friendequiclassSize[l]-l, friendequiclassSize[l]-l+1).clear();
                }
// 		               friendhalf.removeAll(arrayfriendtemp);
// 		               friedndHash.removeAll(arrayfriendHash);


                if (arrayitemtemp.size() > arrayfriendtemp.size()) {
                    int remainder = arrayitemtemp.size() - arrayfriendtemp.size();
                    for (int t = 0; t < remainder; t++) {
                        Random rand = new Random();
                        int pick = rand.nextInt(arrayitemtemp.size());
                        arrayitemtemp.remove(arrayitemtemp.get(pick));
                    }
                } else {
                    if (arrayitemtemp.size() < arrayfriendtemp.size()) {
                        int remainder = arrayfriendtemp.size() - arrayitemtemp.size();
                        for (int t = 0; t < remainder; t++) {
                            Random rand = new Random();
                            int pick = rand.nextInt(arrayfriendtemp.size());
                            arrayfriendtemp.remove(arrayfriendtemp.get(pick));
                        }
                    }
                }


                for (int i = 0; i < arrayitemtemp.size(); i++) {
                    // System.out.println(arrayfriendtemp.size());
                    // for(int t=0; t<arrayfriendtemp.size(); t++){
                    Random rand = new Random();
                    int pick = rand.nextInt(arrayfriendtemp.size());
                    //add into fair dataset
                    fairDataset.add(arrayitemtemp.get(i));
                    fairDataset.add(arrayfriendtemp.get(pick));
                    arrayfriendtemp.remove(arrayfriendtemp.get(pick));
                    //}
                }
            }
        } else
            ////


            while (itemhalf.size() > 0) {
                //System.out.println("I will do");

                //     System.out.println("itemsize="+itemhalf.size());

                int[] controltemp = intersection(itemhalf.get(0), listnoncfd, item);
                //   printRecord(controltemp);

                ArrayList<int[]> arrayitemtemp = new ArrayList<int[]>();
                ArrayList<int[]> arrayfriendtemp = new ArrayList<int[]>();
                int[] itemequiclassSize = new int[1000000];
                int itemequiclassSizeInd = 0;
                int k = 0;
                //add block of same control list to arrayitemtemp and remove them from itemhalf
                for (int[] itemrecord : itemhalf) {
                    if (Arrays.equals(controltemp, intersection(itemrecord, listnoncfd, item))) {
                        arrayitemtemp.add(itemrecord);
                        itemequiclassSize[itemequiclassSizeInd++] = k++;
                        //   itemhalf.remove(itemrecord);
                    }
                }
                for (int l = 0; l < itemequiclassSizeInd; l++)
                    itemhalf.remove(itemequiclassSize[l] - l);
                //      System.out.println("itemhalf temp size="+arrayitemtemp.size());
                // printArrayList(arrayitemtemp);
                int[] friendequiclassSize = new int[1000000];
                int friendequiclassSizeInd = 0;
                k = 0;

                //check the corresponding in friendhalf and add all to arrayfriendtemp remove them from friendhalf
                for (int[] friendrecord : friendhalf) {
                    if (Arrays.equals(controltemp, intersection(friendrecord, listnoncfd, item))) {
                        arrayfriendtemp.add(friendrecord);
                        friendequiclassSize[friendequiclassSizeInd++] = k++;
                        //  friendhalf.remove(friendrecord);
                    }
                }
                for (int l = 0; l < friendequiclassSizeInd; l++)
                    friendhalf.remove(friendequiclassSize[l] - l);
                //     System.out.println("friendhalf temp size="+arrayfriendtemp.size());
                // printArrayList(arrayfriendtemp);
//               itemhalf.removeAll(arrayitemtemp);
//              friendhalf.removeAll(arrayfriendtemp);
                //itemhalf.subList(0, (arrayitemtemp.size())).clear();
                //friendhalf.subList(0, (arrayfriendtemp.size())).clear();


                //check which block have the bigger size and remove the remainder

                if (arrayitemtemp.size() > arrayfriendtemp.size()) {
                    int remainder = arrayitemtemp.size() - arrayfriendtemp.size();
                    for (int t = 0; t < remainder; t++) {
                        Random rand = new Random();
                        int pick = rand.nextInt(arrayitemtemp.size());
                        arrayitemtemp.remove(arrayitemtemp.get(pick));
                    }
                } else {
                    if (arrayitemtemp.size() < arrayfriendtemp.size()) {
                        int remainder = arrayfriendtemp.size() - arrayitemtemp.size();
                        for (int t = 0; t < remainder; t++) {
                            Random rand = new Random();
                            int pick = rand.nextInt(arrayfriendtemp.size());
                            arrayfriendtemp.remove(arrayfriendtemp.get(pick));
                        }
                    }
                }

                //add 2 block with the same size to the fairdataset

                //fairDataset.addAll(arrayitemtemp);
                //fairDataset.addAll(arrayfriendtemp);

                //pair up. See single item for details.
                for (int i = 0; i < arrayitemtemp.size(); i++) {
                    // System.out.println(arrayfriendtemp.size());
                    // for(int t=0; t<arrayfriendtemp.size(); t++){
                    Random rand = new Random();
                    int pick = rand.nextInt(arrayfriendtemp.size());
                    //add into fair dataset
                    fairDataset.add(arrayitemtemp.get(i));
                    fairDataset.add(arrayfriendtemp.get(pick));
                    arrayfriendtemp.remove(arrayfriendtemp.get(pick));
                    //}
                }

            }


//            if(itemhalf.size()<=friendhalf.size()){
//             for(int[] record:itemhalf){
//
//                //if record belongs to itemhalf then do this
//
//                //    int valid=0;
//                    ArrayList<int[]> temp=new ArrayList<int[]>();
//
//                    for(int[] recordfriendhalf:friendhalf){
//                        /*
//                        System.out.print("intersection friendhalf: ");
//                            for(int z=0; z<intersection(recordfriendhalf,listnoncfd, item).length; z++){
//                                System.out.print(intersection(recordfriendhalf,listnoncfd,item)[z]);
//                            }
//                        System.out.print("\n");
//                        */
//                        //store all the corespondences into temp, randomly pick 1, adding that pair to
//                        //fair dataset then delete them out of itemhalf and friendhalf
//                        if(Arrays.equals(intersection(record, listnoncfd,item), intersection(recordfriendhalf, listnoncfd,item))){
//                //            printRecord(listnoncfd);
//                //            System.out.println("this pair is found");
//                //            printRecord(record);
//                //            printRecord(recordfriendhalf);
//                            temp.add(recordfriendhalf);
//                     //       valid=1;
//                         //   break;
//                        }
//                    }
//                    //randomly pick 1 and adding pair to fairdataset
//                    if(!temp.isEmpty()){
//                 //       System.out.println("print temp candidate");
//                 //       printArrayList(temp);
//
//                        Random rand=new Random();
//                        int pick=rand.nextInt(temp.size());
//               //         System.out.println("random number:"+pick);
//               //         System.out.println("record selected:");
//               //         printRecord(temp.get(pick));
//
//                        fairDataset.add(record);
//                        fairDataset.add(temp.get(pick));
//                        friendhalf.remove(temp.get(pick));
//                    }
//                  //  if(valid==0) fairDataset.remove(record);
//            }
//           }
//
//             if(itemhalf.size()>friendhalf.size()){
//             for(int[] record:friendhalf){
//
//                //if record belongs to itemhalf then do this
//
//                //    int valid=0;
//                    ArrayList<int[]> temp=new ArrayList<int[]>();
//
//                    for(int[] recorditemhalf:itemhalf){
//                        /*
//                        System.out.print("intersection friendhalf: ");
//                            for(int z=0; z<intersection(recordfriendhalf,listnoncfd, item).length; z++){
//                                System.out.print(intersection(recordfriendhalf,listnoncfd,item)[z]);
//                            }
//                        System.out.print("\n");
//                        */
//                        //store all the corespondences into temp, randomly pick 1, adding that pair to
//                        //fair dataset then delete them out of itemhalf and friendhalf
//                        if(Arrays.equals(intersection(record, listnoncfd,item), intersection(recorditemhalf, listnoncfd,item))){
//                    //        printRecord(listnoncfd);
//                    //        System.out.println("this pair is found");
//                    //        printRecord(record);
//                    //        printRecord(recorditemhalf);
//                            temp.add(recorditemhalf);
//                     //       valid=1;
//                         //   break;
//                        }
//                    }
//                    //randomly pick 1 and adding pair to fairdataset
//                    if(!temp.isEmpty()){
//                     //   System.out.println("print temp candidate");
//                     //   printArrayList(temp);
//
//                        Random rand=new Random();
//                        int pick=rand.nextInt(temp.size());
//                     //   System.out.println("random number:"+pick);
//                     //   System.out.println("record selected:");
//                     //   printRecord(temp.get(pick));
//
//                        fairDataset.add(record);
//                        fairDataset.add(temp.get(pick));
//                        friendhalf.remove(temp.get(pick));
//                    }
//                  //  if(valid==0) fairDataset.remove(record);
//            }
//           }

            /* Donot need this one anymore.
                //if record belongs to friendhalf then do this.
              for(int[] record:friendhalf){
                  int valid=0;
                    for(int[] recorditemhalf:itemhalf){
                        if(Arrays.equals(intersection(record,listnoncfd, item), intersection(recorditemhalf,listnoncfd, item))){
                            System.out.println("this pair is found");
                            printRecord(record);
                            printRecord(recorditemhalf);
                            valid=1;
                            break;
                        }
                    }
                    if(valid==0) fairDataset.remove(record);

              }

              */
        //System.out.println("Print fairdataset");
        //printArrayList(fairDataset);

        return fairDataset;

    }

    //create a dataspace contains the item A, and a dataspace contains its friends B
    //if a record of A doesnot contain a noncfd item, skip this record
    //if a record of A contains a noncfd item, check if B also contains this. If yes for all noncfd items add to fairdataset.
    public ArrayList<int[]> generateFairDataset(int item) {
        //variables for calculating the odds ratio based on the new formula
        //  int n12=0;
        // int n21=0;

        //System.out.println("generateFairDataset - Generating fairdataset for item: "+item);
        int[] listnoncfd;
        int[] listnoncfdbefore;
        int ChosenControlsumItemnumber = 0;
        if (ChosenControl.length != 0) {
            for (int m = 0; m < ChosenControl.length; m++) {
                for (int i = dataSpacestat[ChosenControl[m] - 1].min; i <= dataSpacestat[ChosenControl[m] - 1].max; i++) {
                    ChosenControlsumItemnumber++;
                }

            }

        }
        System.out.println("ChosenControlsumItemnumber" + ChosenControlsumItemnumber);
        int[] listcontrol = new int[ChosenControlsumItemnumber];//hs.add


        ArrayList<int[]> fairDataset = new ArrayList<int[]>();
        ArrayList<int[]> itemhalf = new ArrayList<int[]>();
        ArrayList<int[]> friendhalf = new ArrayList<int[]>();


        // long startTime=System.currentTimeMillis();
        //System.out.println("Find nonconfounders for "+item);
        if (ChosenControl.length == 0) {
            System.out.println("hushu1");
            System.out.println("hushusingleList1.numOfRule=" + singleList1.numOfRule);
            System.out.println("hushusingleList.numOfRule=" + singleList.numOfRule);
            if (ChosenTest.length == 0) {
                System.out.println("do realAtt");
                listnoncfd = findnonConfounders(item);
            } else {
                System.out.println("do not realatt");
                listnoncfd = findnonConfounders_singleList1(item);
            }
            //listnoncfd=findnonConfounders_singleList1(item);
            //listnoncfd=findnonConfounders(item);
            printRecord(listnoncfd);
            System.out.println("Find nonconfounders for " + item);
            System.out.println("length=" + listnoncfd.length);
        } else if (Controlmethod == 0) {
            System.out.println("hushu2");
            //listnoncfd=findnonConfounders(item);
            //listnoncfdbefore=findnonConfounders_singleList1(item);
            if (ChosenTest.length == 0) {
                System.out.println("do realAtt");
                listnoncfdbefore = findnonConfounders(item);
            } else {
                System.out.println("do not realatt");
                listnoncfdbefore = findnonConfounders_singleList1(item);
            }

            System.out.println("Find nonconfounders for " + item);
            printRecord(listnoncfdbefore);
            int listcontrolnumber = 0;
            for (int m = 0; m < ChosenControl.length; m++) {
                for (int i = dataSpacestat[ChosenControl[m] - 1].min; i <= dataSpacestat[ChosenControl[m] - 1].max; i++) {
                    for (int j = 0; j < listnoncfdbefore.length; j++) {
                        if (i == listnoncfdbefore[j]) {
                            listcontrol[listcontrolnumber++] = i;
                        }
                    }
                }
            }
            listnoncfd = listcontrol;
            System.out.println("length=" + listnoncfdbefore.length);
        } else {
            System.out.println("hushu3");
            int listcontrolnumber = 0;
            for (int m = 0; m < ChosenControl.length; m++) {
                for (int i = dataSpacestat[ChosenControl[m] - 1].min; i <= dataSpacestat[ChosenControl[m] - 1].max; i++) {
                    listcontrol[listcontrolnumber++] = i;
                }
            }
            listnoncfd = listcontrol;
            System.out.println("length=" + listnoncfd.length);
        }
        //System.out.println("before");
        //printRecord(listnoncfd);
        //System.out.println("hushu");
        // add by mss
        controlSingleVar[item][0] = item;
        for (int i = 0; i < listnoncfd.length; i++) {
            controlSingleVar[item][i + 1] = listnoncfd[i];
        }
        controlSingleVar[item] = trimzeros(controlSingleVar[item]);

        if (fast == 1) {
            //Thuc add to set number of control variables
            maxControl = 10;
            if (listnoncfd.length > maxControl) {
                int[] temp = listnoncfd;
                listnoncfd = new int[maxControl];
                System.arraycopy(temp, 0, listnoncfd, 0, maxControl);
            }
        }
        //System.out.println("after");
        printRecord(listnoncfd);
        //  long endTime=System.currentTimeMillis();
        //  System.out.println("find nonconfounder took: "+(endTime-startTime));
//           if(item==57||item==58){
//               System.out.println("Control list for item: "+item);
//
//                printRecord(listnoncfd);
//           }
        // long startTime1=System.currentTimeMillis();

        // System.out.println("Start finding fair dataset for item "+item +" ---Check each if contains all control attributes and divide to 2 halfs");
        for (int x = 0; x < maxData; x++) {
            // System.out.println("Incoming data record: "+(x+1));
            //trim the class value. if nonconfounder is 1 and we donot trim we got the problem
               /*
                int[] recordcopy=new int[realAtt];
               for(int w=0; w<realAtt; w++){
                   recordcopy[w]=dataSpace[x][w];
               }

                int valid=1;

         //Check if a record contains the control items, if not this is invalid record and should be removed
         //If this is a valid record check if it contains the item. Yes, move to P haft. No, move to not P half.
                for(int y=0; y<listnoncfd.length; y++){

                    if(Arrays.binarySearch(recordcopy, listnoncfd[y])<0){

                        //if the record does not contains the item, need to check if it contains item's friends in listnoncfd
                        valid=0;
                        int[] friendsy;
                        friendsy=friends(listnoncfd[y], listnoncfd);
                        for(int v=0; v<friendsy.length; v++){
                            if(Arrays.binarySearch(recordcopy, friendsy[v])>=0) {
                              //  temp[tempind++]=friendsy[v];
                                valid=1;
                            }
                        }
                        if (valid==0){
                       //     System.out.println("invalid record, need to be removed from the fairdataset, record"+(x+1));
                             break;
                        }

                    }
                }

                */
            //This is valid record, if it contains item move to P haft, otherwise move to not P haft.

            // if(valid==1) {

            if (item == 1) {
                if (dataSpace[x][1] == 1) {
                    itemhalf.add(dataSpace[x]);
                } else friendhalf.add(dataSpace[x]);

            } else {
                if (Arrays.binarySearch(dataSpace[x], item) >= 0) {
                    itemhalf.add(dataSpace[x]);
                    //  fairDataset.add(dataSpace[x]);
                } else
                    friendhalf.add(dataSpace[x]);
                //  fairDataset.add(dataSpace[x]);

            }
//                    System.out.println("This record is valid: ");
//                    printRecord(dataSpace[x]);
            //}

        }
//System.out.println("itemhalf size: "+itemhalf.size());
//System.out.println("friendhalf size: "+friendhalf.size());
        //check if record in itemhalf intersects with listnoncfd = record in friendhalf intersects with listnoncfd
//            System.out.println("Print itemhalf");
//            printArrayList(itemhalf);
//
//            System.out.println("Print friendhalf");
//            printArrayList(friendhalf);


        //    ArrayList<int[]> copyfairDataset=new ArrayList<int[]>();
        //  copyfairDataset=fairDataset;

        //long endTime1=System.currentTimeMillis();
        // System.out.println("remove invalid record took: "+(endTime1-startTime1));

        ////
        //System.out.println("Start matching");
        //System.out.println("listnoncfd.length="+listnoncfd.length);
        if (hashcoding == 1 && listnoncfd.length > 10) {
            //System.out.println("ok");
            //if(hashcoding == 1) {
            ArrayList<Integer> itemHash = new ArrayList<Integer>();
            ArrayList<Integer> friendHash = new ArrayList<Integer>();

            for (int[] itemrecord : itemhalf)
                itemHash.add(hash(intersection(itemrecord, listnoncfd, item)));
            for (int[] friendrecord : friendhalf)
                friendHash.add(hash(intersection(friendrecord, listnoncfd, item)));

//             	for(int k=0; k<z.length; k++)
//             		System.out.print(z[k]+" ");
//             	System.out.println();

            while (itemHash.size() > 0) {
                ArrayList<int[]> arrayitemtemp = new ArrayList<int[]>();
                ArrayList<Integer> arrayitemHash = new ArrayList<Integer>();
                ArrayList<int[]> arrayfriendtemp = new ArrayList<int[]>();
                ArrayList<Integer> arrayfriendHash = new ArrayList<Integer>();
                ArrayList<int[]> itemequiclass = new ArrayList<int[]>();
                int[] itemequiclassNum = new int[100];
                int[] itemequiclassSize = new int[1000000];
                int itemequiclassSizeInd = 0;
                int[] friendequiclassSize = new int[1000000];
                int friendequiclassSizeInd = 0;

                int[] controltemp = intersection(itemhalf.get(0), listnoncfd, item);
                int controlhash = itemHash.get(0);

                itemequiclass.add(controltemp);
                itemequiclassNum[0]++;

//  		        	for(int k=0; k<itemHash.size(); k++)
//  		        		if(controlhash == itemHash.get(k)) {
//  		        			arrayitemtemp.add(itemhalf.get(k));
//  		        			arrayitemHash.add(itemHash.get(k));
//  		        		}

                for (int k = 1; k < itemHash.size(); k++) {
                    if (controlhash == itemHash.get(k)) {
                        int flag = 0;
                        for (int l = 0; l < itemequiclass.size(); l++)
                            if (Arrays.equals(itemequiclass.get(0), intersection(itemhalf.get(k), listnoncfd, item))) {
                                itemequiclassNum[l]++;
                                flag = 1;
                            }
                        if (flag == 0) {
                            itemequiclass.add(intersection(itemhalf.get(k), listnoncfd, item));
                            itemequiclassNum[itemequiclass.size() - 1]++;
                        }
// 		        			if(!Arrays.equals(controltemp, intersection(itemhalf.get(k), listnoncfd, item))) {
// 								System.out.println("two hash value: "+hash(intersection(itemhalf.get(0), listnoncfd, item))+", "+hash(intersection(itemhalf.get(k), listnoncfd, item)));
// 								System.out.println("two hash value: "+controlhash+", "+itemHash.get(k));
// 								printRecord(intersection(controltemp, listnoncfd, item));
// 								printRecord(intersection(itemhalf.get(k), listnoncfd, item));
// 		        			}
                    }
                }

                int maxcolumn = 0;
                for (int l = 0; l < itemequiclass.size(); l++)
                    if (itemequiclassNum[l] > maxcolumn)
                        maxcolumn = l;
                for (int k = 0; k < itemHash.size(); k++) {
                    if (Arrays.equals(itemequiclass.get(maxcolumn), intersection(itemhalf.get(k), listnoncfd, item))) {
                        arrayitemtemp.add(itemhalf.get(k));
                        arrayitemHash.add(itemHash.get(k));
                        itemequiclassSize[itemequiclassSizeInd++] = k;
// 		        			System.out.println("array"+arrayitemHash.size());
// 		        			System.out.println(itemequiclassSizeInd);
                    }
                }

                // add by mss
                for (int l = 0; l < itemequiclassSizeInd; l++) {
// 		        		printRecord(intersection(itemhalf.get(itemequiclassSize[l]-l), listnoncfd, item));
                    itemhalf.remove(itemequiclassSize[l] - l);
// 		        		itemhalf.subList(itemequiclassSize[l]-l, itemequiclassSize[l]-l+1).clear();
                    itemHash.remove(itemequiclassSize[l] - l);
// 		        		itemHash.subList(itemequiclassSize[l]-l, itemequiclassSize[l]-l+1).clear();
                }
// 		               itemhalf.removeAll(arrayitemtemp);
//                               itemhalf.subList(0, (arrayitemtemp.size())).clear();
// 		               itemHash.removeAll(arrayitemHash);
//                               itemHash.subList(0, (arrayitemHash.size())).clear();
                itemequiclassSizeInd = 0;

//  			        for(int k=0; k<friendhalf.size(); k++)
//  	                   if(controlhash == friedndHash.get(k)) {
//  	                       arrayfriendtemp.add(friendhalf.get(k));
//  	                       arrayfriendHash.add(friedndHash.get(k));
//  	                   }


                for (int k = 0; k < friendhalf.size(); k++) {
                    if (controlhash == friendHash.get(k)
                            && Arrays.equals(itemequiclass.get(maxcolumn), intersection(friendhalf.get(k), listnoncfd, item))) {
                        arrayfriendtemp.add(friendhalf.get(k));
                        arrayfriendHash.add(friendHash.get(k));
                        friendequiclassSize[friendequiclassSizeInd++] = k;
                    }
                }
                // add by mss
                for (int l = 0; l < friendequiclassSizeInd; l++) {
//        		        		System.out.println(friendequiclassSize[l]);
                    friendhalf.remove(friendequiclassSize[l] - l);
//         		        		friendhalf.subList(friendequiclassSize[l]-l, friendequiclassSize[l]-l+1).clear();
                    friendHash.remove(friendequiclassSize[l] - l);
//                           	friedndHash.subList(friendequiclassSize[l]-l, friendequiclassSize[l]-l+1).clear();
                }

// 		               friendhalf.removeAll(arrayfriendtemp);
//                               	friendhalf.subList(0, (arrayfriendtemp.size())).clear();
// 		               friedndHash.removeAll(arrayfriendHash);
//                               	friedndHash.subList(0, (arrayfriendHash.size())).clear();
                friendequiclassSizeInd = 0;


                if (arrayitemtemp.size() > arrayfriendtemp.size()) {
                    int remainder = arrayitemtemp.size() - arrayfriendtemp.size();
                    for (int t = 0; t < remainder; t++) {
                        Random rand = new Random();
                        int pick = rand.nextInt(arrayitemtemp.size());
                        arrayitemtemp.remove(arrayitemtemp.get(pick));
                    }
                } else {
                    if (arrayitemtemp.size() < arrayfriendtemp.size()) {
                        int remainder = arrayfriendtemp.size() - arrayitemtemp.size();
                        for (int t = 0; t < remainder; t++) {
                            Random rand = new Random();
                            int pick = rand.nextInt(arrayfriendtemp.size());
                            arrayfriendtemp.remove(arrayfriendtemp.get(pick));
                        }
                    }
                }


                for (int i = 0; i < arrayitemtemp.size(); i++) {
                    // System.out.println(arrayfriendtemp.size());
                    // for(int t=0; t<arrayfriendtemp.size(); t++){
                    Random rand = new Random();
                    int pick = rand.nextInt(arrayfriendtemp.size());
                    //add into fair dataset
                    fairDataset.add(arrayitemtemp.get(i));
                    fairDataset.add(arrayfriendtemp.get(pick));
                    arrayfriendtemp.remove(arrayfriendtemp.get(pick));
                    //}
                }
            }
        } else {
            //System.out.println("itemsize="+itemhalf.size());
            ////
            while (itemhalf.size() > 0) {
                //System.out.println("ok,I will");

                //    System.out.println("itemsize="+itemhalf.size());
                //System.out.println("hushu");
                int[] controltemp = intersection(itemhalf.get(0), listnoncfd, item);
                //   printRecord(controltemp);
                // printRecord(itemhalf.get(0));

                ArrayList<int[]> arrayitemtemp = new ArrayList<int[]>();
                ArrayList<int[]> arrayfriendtemp = new ArrayList<int[]>();
                int[] itemequiclassSize = new int[1000000];
                int itemequiclassSizeInd = 0;
                int k = 0;
                //add block of same control list to arrayitemtemp and remove them from itemhalf later
                for (int[] itemrecord : itemhalf) {
                    // printRecord(itemrecord);
                    if (Arrays.equals(controltemp, intersection(itemrecord, listnoncfd, item))) {
                        arrayitemtemp.add(itemrecord);
                        itemequiclassSize[itemequiclassSizeInd++] = k++;
                        //   itemhalf.remove(itemrecord);
                    }
                }
                for (int l = 0; l < itemequiclassSizeInd; l++)

                    itemhalf.remove(itemequiclassSize[l] - l);


                //System.out.println("itemhalf temp size="+arrayitemtemp.size());
                // printArrayList(arrayitemtemp);

                int[] friendequiclassSize = new int[1000000];
                int friendequiclassSizeInd = 0;
                k = 0;
                //check the corresponding in friendhalf and add all to arrayfriendtemp remove them from friendhalf later
                for (int[] friendrecord : friendhalf) {
                    if (Arrays.equals(controltemp, intersection(friendrecord, listnoncfd, item))) {
                        arrayfriendtemp.add(friendrecord);
                        friendequiclassSize[friendequiclassSizeInd++] = k++;
                        //  friendhalf.remove(friendrecord);
                    }
                }
                for (int l = 0; l < friendequiclassSizeInd; l++)
                    friendhalf.remove(friendequiclassSize[l] - l);
                //System.out.println("friendhalf temp size="+arrayfriendtemp.size());
                //printArrayList(arrayfriendtemp);
                //printArrayList(arrayitemtemp);
//               itemhalf.removeAll(arrayitemtemp);
//               friendhalf.removeAll(arrayfriendtemp);
                //itemhalf.subList(0, (arrayitemtemp.size())).clear();
                //friendhalf.subList(0, (arrayfriendtemp.size())).clear();
                //friendhalf.removeRange(1,(arrayfriendtemp.size()+1));

                //check which block have the bigger size and remove the remainder
                //System.out.println("begin");

                if (arrayitemtemp.size() > arrayfriendtemp.size()) {
                    int remainder = arrayitemtemp.size() - arrayfriendtemp.size();
                    for (int t = 0; t < remainder; t++) {
                        Random rand = new Random();
                        int pick = rand.nextInt(arrayitemtemp.size());
                        //System.out.println("pick"+pick);
                        arrayitemtemp.remove(arrayitemtemp.get(pick));
                    }
                } else {
                    if (arrayitemtemp.size() < arrayfriendtemp.size()) {
                        int remainder = arrayfriendtemp.size() - arrayitemtemp.size();
                        for (int t = 0; t < remainder; t++) {
                            Random rand = new Random();
                            int pick = rand.nextInt(arrayfriendtemp.size());
                            arrayfriendtemp.remove(arrayfriendtemp.get(pick));
                        }
                    }
                }
                //System.out.println("begin1");
                //System.out.println("itemhalf temp size="+arrayitemtemp.size());
                // printArrayList(arrayitemtemp);
                //System.out.println("friendhalf temp size="+arrayfriendtemp.size());
                // printArrayList(arrayfriendtemp);

                //add 2 block with the same size to the fairdataset by randomly pairing up

                // fairDataset.addAll(arrayitemtemp);
                // fairDataset.addAll(arrayfriendtemp);

                //calculate the running odds ratio based on the new formula in the paper
                //if the ith arrayitemtemp contains "z=target" and the correspoding pair in arrayfriendtemp
                //contains not z. Then the n12=n12+1. npz and pnz for n21.
                //for each record in first haft, randomly pick a record in the second haft to pair up.
                for (int i = 0; i < arrayitemtemp.size(); i++) {
                    // System.out.println(arrayfriendtemp.size());
                    // for(int t=0; t<arrayfriendtemp.size(); t++){
                    Random rand = new Random();
                    int pick = rand.nextInt(arrayfriendtemp.size());
                    //add into fair dataset
                    fairDataset.add(arrayitemtemp.get(i));
                    fairDataset.add(arrayfriendtemp.get(pick));
                    arrayfriendtemp.remove(arrayfriendtemp.get(pick));
                    //}
                }
            }
        }


//
//           long startTime2=System.currentTimeMillis();
//        //Search through the smaller array to get the random correspondence in the other array.
//            if(itemhalf.size()<=friendhalf.size()){
//             for(int[] record:itemhalf){
//
//                //if record belongs to itemhalf then do this
//
//                //    int valid=0;
//                    ArrayList<int[]> temp=new ArrayList<int[]>();
//
//                    for(int[] recordfriendhalf:friendhalf){
//                        /*
//                        System.out.print("intersection friendhalf: ");
//                            for(int z=0; z<intersection(recordfriendhalf,listnoncfd, item).length; z++){
//                                System.out.print(intersection(recordfriendhalf,listnoncfd,item)[z]);
//                            }
//                        System.out.print("\n");
//                        */
//                        //store all the corespondences into temp, randomly pick 1, adding that pair to
//                        //fair dataset then delete them out of itemhalf and friendhalf
//                        if(Arrays.equals(intersection(record, listnoncfd,item), intersection(recordfriendhalf, listnoncfd,item))){
//                        //    printRecord(listnoncfd);
//                        //    System.out.println("this pair is found");
//                       //     printRecord(record);
//                       //     printRecord(recordfriendhalf);
//                            temp.add(recordfriendhalf);
//                     //       valid=1;
//                         //   break;
//                        }
//                    }
//                    //randomly pick 1 and adding pair to fairdataset
//                    if(!temp.isEmpty()){
//                      //  System.out.println("print temp candidate");
//                      //  printArrayList(temp);
//
//                        Random rand=new Random();
//                        int pick=rand.nextInt(temp.size());
//                     //   System.out.println("random number:"+pick);
//                     //   System.out.println("record selected:");
//                     //   printRecord(temp.get(pick));
//
//                        fairDataset.add(record);
//                        fairDataset.add(temp.get(pick));
//                        friendhalf.remove(temp.get(pick));
//                    }
//                  //  if(valid==0) fairDataset.remove(record);
//            }
//           }
//
//             if(itemhalf.size()>friendhalf.size()){
//             for(int[] record:friendhalf){
//
//                //if record belongs to itemhalf then do this
//
//                //    int valid=0;
//                    ArrayList<int[]> temp=new ArrayList<int[]>();
//                    long startTime3=System.currentTimeMillis();
//                    for(int[] recorditemhalf:itemhalf){
//                        /*
//                        System.out.print("intersection friendhalf: ");
//                            for(int z=0; z<intersection(recordfriendhalf,listnoncfd, item).length; z++){
//                                System.out.print(intersection(recordfriendhalf,listnoncfd,item)[z]);
//                            }
//                        System.out.print("\n");
//                        */
//                        //store all the corespondences into temp, randomly pick 1, adding that pair to
//                        //fair dataset then delete them out of itemhalf and friendhalf
//
//                        if(Arrays.equals(intersection(record, listnoncfd,item), intersection(recorditemhalf, listnoncfd,item))){
//
//                            //    printRecord(listnoncfd);
//                       //     System.out.println("this pair is found");
//                       //     printRecord(record);
//                       //     printRecord(recorditemhalf);
//                            temp.add(recorditemhalf);
//                     //       valid=1;
//                         //   break;
//                        }
//
//                    }
//                    //randomly pick 1 and adding pair to fairdataset
//                    if(!temp.isEmpty()){
//                     //   System.out.println("print temp candidate");
//                     //   printArrayList(temp);
//
//                        Random rand=new Random();
//                        int pick=rand.nextInt(temp.size());
//                     //   System.out.println("random number:"+pick);
//                    //    System.out.println("record selected:");
//                    //    printRecord(temp.get(pick));
//
//                        fairDataset.add(record);
//                        fairDataset.add(temp.get(pick));
//                        friendhalf.remove(temp.get(pick));
//                    }
//                    long endTime3=System.currentTimeMillis();
//                        System.out.println("check 1 pair with randomise: "+(endTime3-startTime3));
//                  //  if(valid==0) fairDataset.remove(record);
//            }
//           }
//             long endTime2=System.currentTimeMillis();
//             System.out.println("Create the symetric dataset took: "+(endTime2-startTime2));
//



     /*

            //replace this
            for(int[] record:itemhalf){

                //if record belongs to itemhalf then do this

                    int valid=0;

                    for(int[] recordfriendhalf:friendhalf){
                        /*
                        System.out.print("intersection friendhalf: ");
                            for(int z=0; z<intersection(recordfriendhalf,listnoncfd, item).length; z++){
                                System.out.print(intersection(recordfriendhalf,listnoncfd,item)[z]);
                            }
                        System.out.print("\n");
                        */

             /*
                        if(Arrays.equals(intersection(record, listnoncfd,item), intersection(recordfriendhalf, listnoncfd,item))){
                            printRecord(listnoncfd);
                            System.out.println("this pair is found");
                            printRecord(record);
                            printRecord(recordfriendhalf);
                            valid=1;
                            break;
                        }
                    }
                    if(valid==0) fairDataset.remove(record);
            }


                //if record belongs to friendhalf then do this.
              for(int[] record:friendhalf){
                  int valid=0;
                    for(int[] recorditemhalf:itemhalf){
                        if(Arrays.equals(intersection(record,listnoncfd, item), intersection(recorditemhalf,listnoncfd, item))){
                            System.out.println("this pair is found");
                            printRecord(record);
                            printRecord(recorditemhalf);
                            valid=1;
                            break;
                        }
                    }
                    if(valid==0) fairDataset.remove(record);

              }
            */
//            System.out.println("Print fairdataset for item: "+item);
        //System.out.println("Print fairdataset ");
        //printArrayList(fairDataset);
        return fairDataset;

    }

    public double logfactorial(int n) {
        double logfact = 0;
        for (int i = 1; i <= n; i++) {
            logfact = logfact + Math.log(i);
        }

        return logfact;
    }

    //  ArrayList<int[]> causalRules=new ArrayList<int[]>();
    public boolean causalRule(int item, int target) {
        boolean result;
        //variables for calculating the odds ratio based on the new formula
        double n12 = 0;
        double n21 = 0;
        int a, b, c, d;
        a = b = c = d = 0;
        // System.out.println("causalRule - Testing rule: "+item+" -> "+target);

        //Generate the fair dataset for item
        //  System.out.println("generate fairdataset");
        //  long startTime = System.currentTimeMillis();
        ArrayList<int[]> fairdataset = generateFairDataset(item);
        //System.out.println("hushu1");
//          System.out.println("Fair dataset");
//          printArrayList(fairdataset);
        //  long endTime = System.currentTimeMillis();
        //  System.out.println("Took "+(endTime-startTime));
        //Count and calculate odds ratio
        //calculate the running odds ratio based on the new formula in the paper
        //if the ith arrayitemtemp contains "z=target" and the correspoding pair in arrayfriendtemp
        //contains not z. Then the n12=n12+1. npz and pnz for n21.

        for (int i = 0; i < (fairdataset.size() / 2); i++) {
            //fairdataset.get(2i) will be the item record
            //fairdataset.get(2*i+1) will be the not item record.
            if ((fairdataset.get(2 * i)[realAtt] == target) & (fairdataset.get(2 * i + 1)[realAtt] != target)) {
                n12 = n12 + 1;
            } else {
                if ((fairdataset.get(2 * i)[realAtt] != target) & (fairdataset.get(2 * i + 1)[realAtt] == target)) {
                    n21 = n21 + 1;
                }
            }
            //Fisher's exact test parameters
            if (fairdataset.get(2 * i)[realAtt] == target) a = a + 1;
            else b = b + 1;
            if (fairdataset.get(2 * i + 1)[realAtt] == target) c = c + 1;
            else d = d + 1;

        }
        //Fisher's exact test calculation
        //  System.out.println("a="+a+" b="+b+" c="+c+" d="+d);
//          double logpvalue=0;
//
//
//          logpvalue=logfactorial(a+b)+logfactorial(c+d)+logfactorial(a+c)+logfactorial(b+d)
//                  -logfactorial(a)-logfactorial(b)-logfactorial(c)-logfactorial(d)-logfactorial(a+b+c+d);
//          double pvalue=Math.exp(logpvalue);
        // System.out.println("pvalue="+pvalue);


        // System.out.println("n12: "+n12+" and n21: "+n21);
        //calculate the odds ratio
        double oddsratio;
        if (n12 == 0) n12 = 1;
        if (n21 == 0) n21 = 1;
        oddsratio = n12 / n21;
        // System.out.println("odds ratio: "+oddsratio);
        //calculate the confidence interval
        double leftend;
        double rightend;

        leftend = Math.exp(Math.log(oddsratio) - Causalconfidencelevel * Math.sqrt(1 / n12 + 1 / n21));
        rightend = Math.exp(Math.log(oddsratio) + Causalconfidencelevel * Math.sqrt(1 / n12 + 1 / n21));
        // System.out.println("leftend:"+leftend);

        //switch two methods between low bound and odds ratio. modified by mss
        if (statisticTest == 1) {
            if (oddsratio < staThreshold)
                result = false;
            else
                result = true;
        } else {
            if (leftend > 1) result = true;
            else result = false;
        }

         /*
          double p=0;
          double pc=0;
          double pnc=0;
          double npc=0;
          double npnc=0;
          int targetvalue;
          int dataitem;
          int[][] counterfair = new int[maxClass+1][];
		for (int k = 0; k < maxClass+1; k++) {
			counterfair[k] = new int[2];
		}
          for (int[] x:fairdataset) {
                    //reading the class and count

			targetvalue = x[realAtt];
			counterfair[targetvalue][0]++;

                    //count the local and global freq of items

			for (int j = 0; j < realAtt; j++) {
			//	 printf(" %d, ", dataSpace[i][j]);
				dataitem = x[j];
				if (dataitem != item)
					continue;
				counterfair[targetvalue][1]++;
				counterfair[maxClass][1]++;
			}
		}

          p=counterfair[maxClass][1];
          pc=counterfair[target][1];
          pnc=p-pc;
          npc=counterfair[target][0]-pc;
          npnc=fairdataset.size()-p-npc;

         // double oddsratio= (pc*npnc)/(npc*pnc);
       //   System.out.println("pc: "+pc+" pnc: "+pnc+" npc: "+npc+" npnc: "+npnc);
          if(pc==0) pc=1; if(pnc==0) pnc=1; if(npc==0) npc=1; if(npnc==0) npnc=1;

          //odds ratio
          double oddsratio= (pc*npnc)/(npc*pnc);
        //  System.out.println("pc="+pc+"pnc"+pnc+"npc"+npc+"npnc"+npnc+"oddsratio"+oddsratio);
          //Add to causalRules
          if(oddsratio>=1.5){
        //      System.out.println("Yes, "+item+" -> "+target+" is a causal rule");
              result= true;
          }
          else{
        //      System.out.println("No, "+item+" -> "+target+" is not a causal rule as oddsratio="+oddsratio);
              result= false;
          */


        return result;


    }

    public boolean causalRule(int[] item, int target) {
//          System.out.print("Testing rule:");
//          printRecord(item); System.out.println("-> "+target);
        boolean result;
        //Generate the fair dataset for item
        ArrayList<int[]> fairdataset = generateFairDataset(item);

        double n12 = 0.0;
        double n21 = 0.0;
        int a, b, c, d;
        a = b = c = d = 0;


        // System.out.println("Print fairdataset for item: ");
        // printArrayList(fairdataset);
        //Count and calculate odds ratio
        for (int i = 0; i < (fairdataset.size() / 2); i++) {
            //fairdataset.get(2i) will be the item record
            //fairdataset.get(2*i+1) will be the not item record.
            if ((fairdataset.get(2 * i)[realAtt] == target) & (fairdataset.get(2 * i + 1)[realAtt] != target)) {
                n12 = n12 + 1;
            } else {
                if ((fairdataset.get(2 * i)[realAtt] != target) & (fairdataset.get(2 * i + 1)[realAtt] == target)) {
                    n21 = n21 + 1;
                }
            }
            //Fisher's exact test parameters
            if (fairdataset.get(2 * i)[realAtt] == target) a = a + 1;
            else b = b + 1;
            if (fairdataset.get(2 * i + 1)[realAtt] == target) c = c + 1;
            else d = d + 1;
        }

        //Fisher's exact test calculation
        // System.out.println("a="+a+" b="+b+" c="+c+" d="+d);
        double logpvalue = 0;


        logpvalue = logfactorial(a + b) + logfactorial(c + d) + logfactorial(a + c) + logfactorial(b + d)
                - logfactorial(a) - logfactorial(b) - logfactorial(c) - logfactorial(d) - logfactorial(a + b + c + d);
        double pvalue = Math.exp(logpvalue);
        //  System.out.println("pvalue="+pvalue);
        //  System.out.println("n12: "+n12+" and n21: "+n21);
        //calculate the odds ratio
        double oddsratio;
        if (n12 == 0) n12 = 1;
        if (n21 == 0) n21 = 1;
        oddsratio = n12 / n21;
        //  System.out.println("odds ratio: "+oddsratio);
        //calculate the confidence interval
        double leftend;
        double rightend;

        leftend = Math.exp(Math.log(oddsratio) - Causalconfidencelevel * Math.sqrt(1 / n12 + 1 / n21));
        rightend = Math.exp(Math.log(oddsratio) + Causalconfidencelevel * Math.sqrt(1 / n12 + 1 / n21));
        // System.out.println("leftend:"+leftend);
        if (statisticTest == 1) {
            if (oddsratio < staThreshold)
                result = false;
            else
                result = true;
        } else {
            if (leftend > 1) result = true;
            else result = false;
        }

          /*


          double p=0;
          double pc=0;
          double pnc=0;
          double npc=0;
          double npnc=0;
          int targetvalue;
          int dataitem;
          int[][] counterfair = new int[maxClass+1][];
		for (int k = 0; k < maxClass+1; k++) {
			counterfair[k] = new int[2];
		}
          for (int[] x:fairdataset) {
                    //reading the class and count

			targetvalue = x[realAtt];
			counterfair[targetvalue][0]++;

                    //count the local and global freq of items
                        int search=0;
			for (int j = 0; j < realAtt; j++) {
			//	 printf(" %d, ", dataSpace[i][j]);
				dataitem = x[j];

                                for(int w=0; w<item.length; w++){
                                    if(dataitem==item[w]) search=search+1;
                                }

			}
                        if (search==item.length){

				counterfair[targetvalue][1]++;
				counterfair[maxClass][1]++;
                        }
		}

          p=counterfair[maxClass][1];
          pc=counterfair[target][1];
          pnc=p-pc;
          npc=counterfair[target][0]-pc;
          npnc=fairdataset.size()-p-npc;

         // double oddsratio= (pc*npnc)/(npc*pnc);
       //   System.out.println("pc: "+pc+" pnc: "+pnc+" npc: "+npc+" npnc: "+npnc);
          if(pc==0) pc=1; if(pnc==0) pnc=1; if(npc==0) npc=1; if(npnc==0) npnc=1;

          //odds ratio
          double oddsratio= (pc*npnc)/(npc*pnc);
        //  System.out.println("pc="+pc+"pnc"+pnc+"npc"+npc+"npnc"+npnc+"oddsratio"+oddsratio);
          //Add to causalRules
          if(oddsratio>=1.5){
//         //     System.out.print("Yes, ");
//              printRecord(item);
//              System.out.print(" -> "+target+" is a causal rule");
              result= true;
          }
          else{
            //  System.out.println("No,item -> "+target+" is not a causal rule as oddsratio="+oddsratio);
              result= false;
          }


        */


        return result;


    }

    ArrayList<RuleStru> singleCausalRules = new ArrayList<RuleStru>();
    ArrayList<RuleStru> secondlevelCausalRules = new ArrayList<RuleStru>();

    //this is to test the causal rules for all rules in ruleset
    public void causalTest(PrefixTree tree) {
        //System.out.print("number of son: "+tree.numOfSon);
        System.out.println("ruleSet.numOfRulebefore=" + ruleSet.numOfRule);
        for (int i = 0; i < tree.numOfSon; i++) {

            for (int x = 0; x < MAXTARGET; x++) {
                if (tree.sonList[i].target[x] != -1) {
                    //System.out.println("nodeID-target "+tree.sonList[i].nodeID+ " -> "+tree.sonList[i].target[x]);
                    boolean results = causalRule(tree.sonList[i].nodeID, tree.sonList[i].target[x]);
                    //  System.out.println("Finish causalRule, How long?");
                    if (results) {
                        //To skip this node when generating the next level and set iscausal to true
                        tree.sonList[i].token = 4;
                        tree.sonList[i].iscausal = true;
                        //note to ruleset that this rule iscausalrule

                        RuleStru cur;
                        cur = ruleSet.ruleHead;
                        System.out.println("ruleSet.numOfRule=" + ruleSet.numOfRule);


                        while (cur != null) {
                            if (cur.len == 1) {
                                if ((cur.antecedent[0] == tree.sonList[i].nodeID) && (cur.target[0] == tree.sonList[i].target[0])) {
                                    cur.isCausalRule = true;
                                }

                            }

                            cur = cur.nextRule;
                        }

                    } else {
                        tree.sonList[i].iscausal = false;

                        RuleStru cur;
                        cur = ruleSet.ruleHead;


                        while (cur != null) {
                            if (cur.len == 1) {
                                if ((cur.antecedent[0] == tree.sonList[i].nodeID) && (cur.target[0] == tree.sonList[i].target[0])) {
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
          RuleStru cur;
          cur = ruleset.ruleHead;

          //generate first level causal rules
          while(cur!=null){
              if(cur.len==1){
                  cur.isCausalRule=causalRule(cur.antecedent[0], cur.target[0]);
                  if(cur.isCausalRule){
                      cur.token=4;
                      singleCausalRules.add(cur);
                  }

              }

               cur=cur.nextRule;
           }

          System.out.println("Single causal rules: "+singleCausalRules.size());
           //store the rule name


          //check if the combine include a single causal rule
          //if yes, ofcource causal rule. If no, test causal rule and note these rules
          //the combine rules which includes non-causal components may be of interest


       //3rd level



       //4th level

        */


    }

    //hs.start for user test 1 attribute
    public void causalTest_One(PrefixTree tree) {
        System.out.print("number of son: " + tree.numOfSon);
        System.out.println("ruleSet1.numOfRulebefore=" + ruleSet1.numOfRule);
        for (int m = 0; m < ChosenTest.length; m++) {
            for (int i = dataSpacestat[ChosenTest[m] - 1].min; i <= dataSpacestat[ChosenTest[m] - 1].max; i++) {

                for (int x = 0; x < MAXTARGET; x++) {
                    if (tree.sonList1[i - 1].target[x] != -1) {
                        //System.out.println("nodeID-target "+tree.sonList[i].nodeID+ " -> "+tree.sonList[i].target[x]);
                        boolean results = causalRule(tree.sonList1[i - 1].nodeID, tree.sonList1[i - 1].target[x]);
                        System.out.println("Finish causalRule, How long?");
                        if (results) {
                            //To skip this node when generating the next level and set iscausal to true
                            tree.sonList1[i - 1].token = 4;
                            tree.sonList1[i - 1].iscausal = true;
                            //note to ruleset that this rule iscausalrule

                            RuleStru cur;
                            cur = ruleSet.ruleHead;
                            System.out.println("ruleSet1.numOfRule=" + ruleSet.numOfRule);


                            while (cur != null) {
                                if (cur.len == 1) {
                                    if ((cur.antecedent[0] == tree.sonList1[i - 1].nodeID)
                                            && (cur.target[0] == tree.sonList1[i - 1].target[0])) {
                                        cur.isCausalRule = true;
                                    }

                                }

                                cur = cur.nextRule;
                            }

                        } else {
                            tree.sonList1[i - 1].iscausal = false;

                            RuleStru cur;
                            cur = ruleSet.ruleHead;


                            while (cur != null) {
                                if (cur.len == 1) {
                                    if ((cur.antecedent[0] == tree.sonList1[i - 1].nodeID) && (cur.target[0] == tree.sonList1[i - 1].target[0])) {
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
          RuleStru cur;
          cur = ruleset.ruleHead;

          //generate first level causal rules
          while(cur!=null){
              if(cur.len==1){
                  cur.isCausalRule=causalRule(cur.antecedent[0], cur.target[0]);
                  if(cur.isCausalRule){
                      cur.token=4;
                      singleCausalRules.add(cur);
                  }

              }

               cur=cur.nextRule;
           }

          System.out.println("Single causal rules: "+singleCausalRules.size());
           //store the rule name


          //check if the combine include a single causal rule
          //if yes, ofcource causal rule. If no, test causal rule and note these rules
          //the combine rules which includes non-causal components may be of interest


       //3rd level



       //4th level

        */


    }

    //hs.end
    public void causalTest(PrefixTree tree, int layer) {
        //System.out.print("number of son: "+tree.numOfSon+"tree.len"+tree.len+"layer"+layer);
        System.out.println("tree.len = " + tree.len);
        System.out.println("tree.nodeID = " + tree.nodeID);
        if (tree.len == layer) {
            int[] temp = new int[layer];
            for (int j = 0; j < tree.len; j++) {
                System.out.println("hushu");
                printf("%d ", tree.set[j]);
                temp[j] = tree.set[j];
            }
            //System.out.println("testing");
            //printRecord(temp);
            for (int i = 0; i < MAXTARGET; i++) {
                if (tree.target[i] != -1) {
                      /*
                        RuleStru cur1;
                        cur1 = ruleSet.ruleHead;
                        while(cur1!=null){
                             if(cur1.len==layer){
                              int[] temp3=new int[layer];
                              for(int v=0; v<layer; v++){
                                  temp3[v]=cur1.antecedent[v];
                              }

                              System.out.print("temp3: ");
                              printRecord(temp3);
                             }
                             cur1=cur1.nextRule;
                        }
                        */


                    //   printf("-> %d ", tree.target[i]);
                    //System.out.println("hushu");
                    boolean results = causalRule(temp, tree.target[i]);
                    System.out.println("results of causal test =" + results);
                    if (results) {

                        //to skip this node when go to next level
                        // tree.token=4;
                        tree.iscausal = true;
                        System.out.println("hs0");
                        //notify ruleSet
                        RuleStru cur;
                        cur = ruleSet.ruleHead;
                        System.out.println("hs1");
                        while (cur != null) {
                            if (cur.len == layer) {
                                int[] temp2 = new int[layer];
                                for (int v = 0; v < layer; v++) {
                                    temp2[v] = cur.antecedent[v];
                                }

                                System.out.print("temp2: ");
                                printRecord(temp2);
                                System.out.print("target: ");
                                printRecord(cur.target);
                                if (Arrays.equals(temp2, temp) && (cur.target[0] == tree.target[i])) {
                                    cur.isCausalRule = true;
                                    System.out.println("Yes, set this to true");
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
//                              System.out.print("temp2: ");
//                              printRecord(temp2);
//                              System.out.print("target: ");
                                //    printRecord(cur.target);
                                if (Arrays.equals(temp2, temp) && (cur.target[0] == tree.target[i])) {
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


        for (int i = 0; i < tree.numOfSon; i++) {


            causalTest(tree.sonList[i], layer);

            System.out.println("hushutree.sonList1[i]" + tree.sonList[i].nodeID);

            System.out.println("hushutree.sonList1[i]" + tree.sonList[i].len);

            if (tree.sonList[i].iscausal) {
                System.out.println("hushu is causal");
                tree.sonList[i].token = 4;


            }


        }
         /*
          for (int i = 0; i < tree.numOfSon; i++) {

            for(int x=0; x<MAXTARGET; x++){
                if(tree.sonList[i].target[x]!=-1){
                    System.out.println("nodeID-target "+tree.sonList[i].len+ " -> "+tree.sonList[i].target[x]);
                    boolean results = causalRule(tree.sonList[i].set,tree.sonList[i].target[x]);
                    if(results){
                       //To skip this node when generating the next level and set iscausal to true
                        tree.sonList[i].token=4;
                        tree.sonList[i].iscausal=true;
                    }
                    else{
                        tree.sonList[i].iscausal=false;
                    }
                }

            }
          }
         */
    }


    /*
      public void causalTest(RuleSet ruleset, int level){
          RuleStru cur;
          cur=ruleset.ruleHead;

          int[] singlecausals=new int[singleCausalRules.size()];
          int index=0;
          for(RuleStru rs:singleCausalRules){
              //System.out.println(rs.antecedent[0]);
              singlecausals[index++]=rs.antecedent[0];
          }
          System.out.println("single rules:");
          printRecord(singlecausals);

          while(cur!=null){
              //everything includes a causal single will be causal rules.

             // if(cur.antecedent.length==2){
                //  for(int j=0; j<cur.len; j++){
                      if(Arrays.binarySearch(singlecausals, cur.antecedent[0])>=0 ||Arrays.binarySearch(singlecausals, cur.antecedent[1])>=0 ){
                          cur.isCausalRule=true;
                     //     break;
                      }
              //    }
                  //otherwise
                  if(cur.isCausalRule==null){
                    boolean  results=causalRule(cur.antecedent,cur.target[0]);
                    if(results){
                      secondlevelCausalRules.add(cur);
                    }
                    cur.isCausalRule=results;
                  }




               //cur.antecedent[0];
               cur=cur.nextRule;
           }

          for(RuleStru rs:singleCausalRules){
              System.out.print(", "+ rs.antecedent[0]);
          }
          System.out.println("Second level:");
          for(RuleStru rs:secondlevelCausalRules){
              printRecord(rs.antecedent);
          }

      }
     */
    public void printList(List<int[]> list) {
        for (int[] record : list) {
            System.out.format("%d, ", record);

        }
        System.out.print("\n");
    }

    //find intersection between the record and the list of noncfd.
    //First remove the class value
    //As the noncfd does not contain the item, hence the intersection will not contain
    //in this version we donot need to remove the item/friends
    public int[] intersection(int[] list1org, int[] list2, int[] item) {
        //   int[] itemfriends=friends(item, list2);
        int[] list1 = new int[list1org.length - 1];
        //similar to recordcopy
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
            } else list2index++;

        }
        list = trimzeros(list);
        return list;
    }


    //the intersection is designed for checking the intersection a record and a list of nonconfounders.
    //list1org is the record to pass on. list2 is the list of noncfd
    //need to remove class and item/friends in list1org.
    public int[] intersection(int[] list1org, int[] list2, int item) {
        int[] itemfriends = friends(item, list2);
        int[] list1 = new int[list1org.length - 1];

        for (int x = 0; x < list1.length; x++) {
            if (list1org[x] == item) continue;
            int flag = 0;
            for (int y = 0; y < itemfriends.length; y++) {
                if (list1org[x] == itemfriends[y]) flag = 1;
            }
            if (flag == 1) continue;
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
            } else list2index++;

        }
        list = trimzeros(list);
        return list;
    }


    public <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();
        for (T t : list1) {
            if (list2.contains(t)) list.add(t);
        }
        return list;
    }


    public int[] trimzeros(int[] array) {
        int len = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != 0)
                len++;
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
        public int[][] trimzeros(int[][] array){
            int len = 0;
            for (int i=0; i<array.length; i++){
                for(int v=0;v<array[i].length;v++){
                   if (array[i][v] != 0)
                len++;
                }

            }
            int [] newArray = new int[len];
            for (int i=0, j=0; i<array.length; i++){
                 if (array[i] != 0) {
                    newArray[j] = array[i];
                    j++;
                }
            }
            return newArray;
        }
        */
    //hs.start
    public int[] findnonConfounders_singleList1(int[] itemclass) {
        int[] ignoreList;
        int[] confounderList;
        int[] listcfdrelated;
        int[] nonconfounderList;
        // listcfdrelated =new int[singleList.numOfRule];
        // modified by mss
        listcfdrelated = new int[maxItem * 2];
        confounderList = new int[maxItem * 2];
        nonconfounderList = new int[maxItem * 2];
        ignoreList = new int[maxItem * 2];
        ////
        int k;
        int[][] countercfd;
        int[] lMinSupcfd;
        double[] distcfd;
        //initial the counter
        countercfd = new int[4][];
        for (k = 0; k < 4; k++) {
            countercfd[k] = new int[maxItem + 2];
        }
        //initial parameter
        int ignoreind = 0;
        int i, j, item;
        int targetvalue = 0;
        lMinSupcfd = new int[4];
        distcfd = new double[4];
        RuleStru cur;
        RuleSet ruleset = new RuleSet();
        int[] rulesinsingleList;
        ruleset = singleList1;
        cur = ruleset.ruleHead;

        //Take the rule list from singleList

        rulesinsingleList = new int[singleList1.numOfRule];
        int z = 0;
        double[] ruleOddsRatios;
        ruleOddsRatios = new double[singleList1.numOfRule];
        Map<Integer, Double> myLocalMap = new HashMap<Integer, Double>();

        while (cur != null) {

            rulesinsingleList[z] = cur.antecedent[0];
            ruleOddsRatios[z] = -cur.oddsRatio; //put negative to use Arrays.sort and get descending order
            z = z + 1;
            cur = cur.nextRule;
        }

        //test
        //   for(int m=0; m<rulesinsingleList.length; m++){
        //     System.out.println("rules in singleList: "+rulesinsingleList[m]);
        // }

        //create a map with keys are attributes, values are odds ratios
        //items in the same binary attribute will have the same odds ratio in this program
        for (int m = 0; m < rulesinsingleList.length; m++) {
            //int att;
            //if((rulesinsingleList[m] % 2)==0) att=rulesinsingleList[m]/2;
            //else att=rulesinsingleList[m]/2 +1;
            //hs.add
            int att = 0;
            for (i = 0; i < realAtt; i++) {

                //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
                //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

                if (dataSpacestat[i].min <= rulesinsingleList[m] && rulesinsingleList[m] <= dataSpacestat[i].max) {
                    // column index in dataSpace corresponding to node.nodeID in binary matrix.
                    att = i + 1;
                    //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
                    //System.out.println("dataSpacestat["+i+"].min="+dataSpacestat[i].min);
                    //System.out.println("dataSpacestat["+i+"].max="+dataSpacestat[i].max);
                }

            }
            //myLocalMap.put(att, ruleOddsRatios[m]);
            myLocalMap.put(Integer.valueOf(att), Double.valueOf(ruleOddsRatios[m]));
            // System.out.println("Original rules in singleList: "+rulesinsingleList[m]);
            // System.out.println("Corresponding  Odds ratio: "+ruleOddsRatios[m]);
        }

        //System.out.println("Keys of tree map: " + myLocalMap.keySet());
        //System.out.println("Values of tree map: " + myLocalMap.values());


        // Reading and count the co-occurence between other items and class item
        for (i = 0; i < maxData; i++) {
            int search = 0;
            for (int x = 0; x < realAtt; x++) {
                for (int y = 0; y < itemclass.length; y++) {
                    if (dataSpace[i][x] == itemclass[y]) search = search + 1;
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
                //   System.out.println("item:"+item);

                for (int x = 0; x < singleList1.numOfRule; x++) {

                    if (rulesinsingleList[x] == item) {
                        countercfd[targetvalue][item]++;
                        countercfd[2][item]++;
                        //      System.out.println("countercfd["+targetvalue+"]["+item+"]="+countercfd[targetvalue][item]);
                        //       System.out.println("countercfd[2]["+item+"]="+countercfd[2][item]);
                    }
                }

            }
            targetvalue = 0;
        }


        //test print out countercfd
        /*
           for (int x=0; x<countercfd.length; x++){
                    for (int y=0; y<countercfd[x].length; y++){
                       System.out.println("countercfd["+x+"]["+y+"]="+countercfd[x][y]);
                    }
            }
          */
        k = 0;
        //find the associations
        //1. find the local min support. if an item local min support<< the ignore

        int[] lmsupport;
        lmsupport = new int[2];
        lmsupport[0] = (int) (countercfd[0][0] * gsup + 0.5);
        lmsupport[1] = (int) (countercfd[1][0] * gsup + 0.5);


        //   System.out.println("lmsupport[0]"+lmsupport[0]+"lmsupport[1]"+lmsupport[1]);
        for (int x = 1; x < maxItem + 2; x++) {

            if (countercfd[2][x] == 0) continue;

            //exclude exclusiveness
            int pq = countercfd[1][x];
            int npq = countercfd[0][x];
            int pnq = (countercfd[1][0] - countercfd[1][x]);
            int epsilon0 = lmsupport[0];
            int epsilon1 = lmsupport[1];

            //1. sup(npq)<epsilon
            if (npq < epsilon0) {
                ignoreList[ignoreind++] = x;
                //  System.out.println("Exclude " + x +" as sup(npq)="+countercfd[0][x]+", but epsilon=lsup= "+lmsupport[0] );
                continue;
            }
            //2. sup(pq)<epsilon
            if (pq < epsilon1) {
                ignoreList[ignoreind++] = x;
                //   System.out.println("Exclude " + x +" as sup(pq)="+countercfd[1][x]+", but epsilon=lsup= "+lmsupport[1] );
                continue;
            }
            //3. sup(pnq)<epsilon

            if (pnq < epsilon1) {
                ignoreList[ignoreind++] = x;
                //   System.out.println("Exclude " + x +" as sup(pnq)="+pnq+", but epsilon=lsup= "+lmsupport[1] );
                continue;
            }


            //ignore itself
            for (int q = 0; q < itemclass.length; q++) {
                if (x == itemclass[q]) ignoreList[ignoreind++] = x;

            }


            //////////////////////////////

            //Do not use confounders

			   /*
               //if (countercfd[2][x]<gMinSup) continue;
               //calculate the odds ratio
               double pc, pnc, p, npc, npnc, np, leftend, rightend;
               double oddsratio;
               p=countercfd[2][x];
               pc=countercfd[1][x];
               pnc=countercfd[0][x];
               npc=countercfd[1][0]-countercfd[1][x];
               npnc=maxData-countercfd[2][x]+countercfd[1][x]-countercfd[1][0];
              //if the value==0 assign 1
               if(pnc==0) pnc=1;
               if(npc==0) npc=1;
               if(npnc==0) npnc=1;
         //      System.out.println("item:"+x+" pc:"+pc+" pnc:"+pnc+" npc:"+npc+"npnc:"+npnc);
               oddsratio=(pc*npnc)/(pnc*npc);
               leftend=Math.exp(Math.log(oddsratio)-1.96*Math.sqrt((1/pc) + (1/npnc)+(1/npc) +(1/pnc)));
               rightend=Math.exp(Math.log(oddsratio)+1.96*Math.sqrt((1/pc) + (1/npnc)+(1/npc)+(1/pnc)));

               //if pass local supports and oddsratio>1.5 then ->confounderList
               //we need confounderList as items with the same attribute with item in confounderList will be
               //removed out of the control list.

               //switch two methods between low bound and odds ratio. modified by mss
//               if(statisticTest==1)
//              	  if(oddsratio < staThreshold) continue;
//                else
              	  if (leftend<=1) continue;
//               if(leftend<=1) continue;
              // if(oddsratio<1.5) continue;
            //       System.out.println("item:"+x+"oddsratio="+oddsratio);

               confounderList[k++]=x;

			   */
        }


           /*
           // modified by mss
           // find all confounder and ignore items in attribute level
           // i.e. if one item is found in confounderlist or ignorelist, then remove all items belonging the same attribute.
           if(controlAttribute == 1) {
	           int confounderListInd = 0;
	           for(i=0; i<confounderList.length; i++)
	        	   if(confounderList[i]==0) {
	        		   confounderListInd = i;
	        		   break;
	        	   }
	           int[] cfdClone = confounderList.clone();
	           for(i=0; i<confounderListInd-1; i++)
	        	   for(j=i+1; j<confounderListInd; j++)
	        		   if(itemRecord[confounderList[i]].attName.equals(itemRecord[confounderList[j]].attName))
	        			   cfdClone[j] = 0;
	           confounderList = cfdClone.clone();
	           cfdClone=trimzeros(cfdClone);
	           Arrays.sort(cfdClone);

	           int[] friendsOfcfd = new int[1000];
	           int friendsOfcfdInd = 0;
	           for (i=0; i<cfdClone.length; i++) {
	        	   if(cfdClone[i] == 0)
	        		   continue;
	        	   for(j=0; j<realAtt; j++)
	        		   if(Arrays.binarySearch(attValue[j], cfdClone[i]) >= 0) {
	        			   for(int l=0; l<attValue[j].length; l++) {
	        				   if(Arrays.binarySearch(cfdClone, attValue[j][l]) < 0 && attValue[j][l] != 0)
	        					   friendsOfcfd[friendsOfcfdInd++] = attValue[j][l];
	        			   }
	        			   break;
	        		   }
	           }
	           friendsOfcfd=trimzeros(friendsOfcfd);
	           for (i=0; i<friendsOfcfd.length; i++) {
	        	   confounderList[confounderListInd++]=friendsOfcfd[i];
	           }
	           confounderList=trimzeros(confounderList);
	           Arrays.sort(confounderList);
	           listcfdrelated = confounderList.clone();

	           int[] ignoreClone = ignoreList.clone();
	           int ignoreListInd = 0;
	           for(i=0; i<ignoreList.length; i++)
	        	   if(ignoreList[i]==0) {
	        		   ignoreListInd = i;
	        		   break;
	        	   }
	           int ignoreTempInd = ignoreListInd;
	           for(i=0; i<ignoreListInd-1; i++) {
	        	   for(j=i+1; j<ignoreListInd; j++)
	        		   if(itemRecord[ignoreList[i]].attName.equals(itemRecord[ignoreList[j]].attName)) {
	        			   ignoreClone[j] = 0;
	        			   break;
	        		   }
	           }
	           for(i=0; i<ignoreListInd; i++) {
	    		   int flag = 0;
	    		   if(ignoreClone[i] == 0)
	    			   continue;
	        	   for(j=0; j<confounderList.length; j++)
	        		   if(itemRecord[ignoreClone[i]].attName.equals(itemRecord[confounderList[j]].attName)) {
	        			   ignoreClone[i] = 0;
	        			   flag = 1;
	        			   break;
	        		   }
	        	   if(flag == 0)
	            	   for(j=0; j<realAtt; j++)
	            		   if(Arrays.binarySearch(attValue[j], ignoreClone[i]) >= 0) {
	            			   for(int l=0; l<attValue[j].length; l++) {
	            				   if(attValue[j][l] != ignoreClone[i])
	            					   ignoreClone[ignoreTempInd++] = attValue[j][l];
	            			   }
	            			   break;
	            		   }
	           }
	           ignoreList = ignoreClone.clone();
	           ignoreList=trimzeros(ignoreList);
	           Arrays.sort(ignoreList);

//	           int noncfdInd=0;
//        	   for(i=0; i<realAtt; i++)
//    			   for(j=0; j<attValue[i].length; j++)
//    				   if(Arrays.binarySearch(confounderList, attValue[i][j]) < 0)
//    					   nonconfounderList[noncfdInd++] = attValue[i][j];
//        	   for(i=0; i<itemclass.length; i++)
//        		   for(j=0; j<noncfdInd; j++) {
//        			   if(itemclass[i] == nonconfounderList[j])
//        				   nonconfounderList[j] = 0;
//            		   if(Arrays.binarySearch(ignoreList, nonconfounderList[j]) >= 0)
//            			   nonconfounderList[j] = 0;
//            	   }
           }
           else {
           ////

           //find confounderrelated list
           int indcfd=0;
           for (int x=0; x<rulesinsingleList.length; x++){
                  for(int y=0;y<confounderList.length;y++){
                   if(confounderList[y]!=0 && itemRecord[(confounderList[y])].attName.equals(
                       itemRecord[rulesinsingleList[x]].attName)){
//                      int check=0;
//                       //check if the new comer already exist
//                       for(int v=0; v<indcfd;v++){
//                           if(listcfdrelated[v]==rulesinsingleList[x]){
//                               check=1;
//                               System.out.println("don't add "+rulesinsingleList[x]+" as already exist");
//                               break;
//                           }
//                       }
//                       if(check==0){
                       //check if the rule is already in listcfdrelated if yes do not add.
                       //Arrays.sort(listcfdrelated);
                      //if(Arrays.binarySearch(listcfdrelated, rulesinsingleList[x])<0){

                       boolean found=false;
                       for(int p=0; p<listcfdrelated.length; p++){
                           if(listcfdrelated[p]==rulesinsingleList[x]){
                                found=true;
                                break;
                           }
                       }
                       if(!found){

                       listcfdrelated[indcfd++]=rulesinsingleList[x];

                            //System.out.println("listcfdrelated["+(indcfd-1)+"]="+listcfdrelated[indcfd-1]+" as confounder:"+confounderList[y]);
                      }
                   }
                  }

            }
           }
//           Arrays.sort(listcfdrelated);
//           listcfdrelated=trimzeros(listcfdrelated);
           //System.out.println("listcfdrelated");
           //printRecord(listcfdrelated);
        //   nonconfounderList= Arrays.asList(rulesinsingleList).retainAll(listcfdrelated);
           //find the nonconfounder list=(ruleinsingleList \ listcfdrelated)\ ignoreList
           int noncfdind=0;

           for(int x=0; x<rulesinsingleList.length; x++){
             //  if(rulesinsingleList[x]==itemclass) continue;
               int flag=0;
               //remove listcfdrelated
               for (int y=0; y<listcfdrelated.length; y++){
                   if(rulesinsingleList[x]==listcfdrelated[y]){
                       flag=1;
                       break;
                   }

               }
               //remove ignorelist
               for (int y=0; y<ignoreList.length; y++){
                   if(rulesinsingleList[x]==ignoreList[y]){
                       flag=1;
                       break;
                   }

               }

               if(flag==0)    nonconfounderList[noncfdind++]=rulesinsingleList[x];


           }

           // modified by mss
           // For one attribute, if none of its items are confounded to the exposure,
           // then keep all the items within this attribute (including irrelevant items with the target).
           if(controlAttribute == 1) {
	           int nonconfounderListInd = 0;
	           for(i=0; i<nonconfounderList.length; i++)
	        	   if(nonconfounderList[i]==0) {
	        		   nonconfounderListInd = i;
	        		   break;
	        	   }
	           int[] noncfdClone = nonconfounderList.clone();
	           for(i=0; i<nonconfounderListInd-1; i++)
	        	   for(j=i+1; j<nonconfounderListInd; j++)
	        		   if(itemRecord[nonconfounderList[i]].attName.equals(itemRecord[nonconfounderList[j]].attName))
	        			   noncfdClone[j] = 0;
	           nonconfounderList = noncfdClone.clone();
	           noncfdClone=trimzeros(noncfdClone);
	           Arrays.sort(noncfdClone);

	           int[] friendsOfnoncfd = new int[1000];
	           int friendsOfnoncfdInd = 0;
	           for (i=0; i<noncfdClone.length; i++) {
	        	   if(noncfdClone[i] == 0)
	        		   continue;
	        	   for(j=0; j<realAtt; j++)
	        		   if(Arrays.binarySearch(attValue[j], noncfdClone[i]) >= 0) {
	        			   for(int l=0; l<attValue[j].length; l++) {
	        				   if(Arrays.binarySearch(noncfdClone, attValue[j][l]) < 0)
	        					   friendsOfnoncfd[friendsOfnoncfdInd++] = attValue[j][l];
	        			   }
	        			   break;
	        		   }
	           }
	           friendsOfnoncfd=trimzeros(friendsOfnoncfd);
	           for (i=0; i<friendsOfnoncfd.length; i++) {
	        	   nonconfounderList[nonconfounderListInd++]=friendsOfnoncfd[i];
	           }
           }
           ////


		   */

        //  System.out.println("Exclusive List");
        ignoreList = trimzeros(ignoreList);
        //printRecord(ignoreList);
        //Removing exclusives
        if (ignoreList.length > 0) {
            for (int ind = 0; ind < ignoreList.length; ind++) {
                //convert ignore item to attribute
                //int att;
                //if((ignoreList[ind] % 2)==0) att=ignoreList[ind]/2;
                //else att=ignoreList[ind]/2 +1;
                //hs.add
                int att = 0;
                for (i = 0; i < realAtt; i++) {

                    //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
                    //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

                    if (dataSpacestat[i].min <= ignoreList[ind] && ignoreList[ind] <= dataSpacestat[i].max) {
                        // column index in dataSpace corresponding to node.nodeID in binary matrix.
                        att = i + 1;
                        //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
                        //System.out.println("dataSpacestat["+i+"].min="+dataSpacestat[i].min);
                        //System.out.println("dataSpacestat["+i+"].max="+dataSpacestat[i].max);
                    }

                }
                //remove the attribute from map

                myLocalMap.remove(att);
            }

        }
        //get remaining attributes after removing ignoreList
        Set<Integer> keyset = myLocalMap.keySet();
        int[] arr = new int[keyset.size()];
        int index = 0;
        for (Integer attr : keyset) {
            arr[index++] = attr; //note the autounboxing here
        }
        //printRecord(arr);


        //get corresponding oddsratios
        double[] oddsarray = new double[arr.length];
        for (int v = 0; v < arr.length; v++) {
            oddsarray[v] = myLocalMap.get(arr[v]);
        }


        //create a new map with odds are key attributes are values
        //odds need to be unique. This is to sort the attributes based on odds ratios
        Map<Double, Integer> myLocalMapReverse = new HashMap<Double, Integer>();
        for (int m = 0; m < arr.length; m++) {
            myLocalMapReverse.put(Double.valueOf(oddsarray[m]), Integer.valueOf(arr[m]));
        }

        //System.out.println("Keys of tree map: " + myLocalMapReverse.keySet());
        //System.out.println("Values of tree map: " + myLocalMapReverse.values());

        //sort the odds values
        Arrays.sort(oddsarray);


        for (int indx = 0; indx < arr.length; indx++) {
            int temp = myLocalMapReverse.get(oddsarray[indx]).intValue();
            //	System.out.println("Keys of tree map: " +temp);
            //convert the attribute to 2 binary items. This only works for binary data sets.
            //nonconfounderList[2*indx]=temp*2-1;
            //nonconfounderList[2*indx+1]=temp*2;
            for (i = 0, j = dataSpacestat[temp - 1].min; i < (dataSpacestat[temp - 1].max - dataSpacestat[temp - 1].min + 1); i++) {
                nonconfounderList[j] = (dataSpacestat[temp - 1].min + i);
                j++;
            }

        }

        nonconfounderList = trimzeros(nonconfounderList);

        //System.out.println("Control List");
        //printRecord(nonconfounderList);
        return nonconfounderList;

    }


    //hs.end

    public int[] findnonConfounders(int[] itemclass) {
        int[] ignoreList;
        int[] confounderList;
        int[] listcfdrelated;
        int[] nonconfounderList;
        // listcfdrelated =new int[singleList.numOfRule];
        // modified by mss
        listcfdrelated = new int[maxItem * 2];
        confounderList = new int[maxItem * 2];
        nonconfounderList = new int[maxItem * 2];
        ignoreList = new int[maxItem * 2];
        ////
        int k;
        int[][] countercfd;
        int[] lMinSupcfd;
        double[] distcfd;
        //initial the counter
        countercfd = new int[4][];
        for (k = 0; k < 4; k++) {
            countercfd[k] = new int[maxItem + 2];
        }
        //initial parameter
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

        //Take the rule list from singleList

        rulesinsingleList = new int[singleList.numOfRule];
        int z = 0;
        double[] ruleOddsRatios;
        ruleOddsRatios = new double[singleList.numOfRule];
        Map<Integer, Double> myLocalMap = new HashMap<Integer, Double>();

        while (cur != null) {

            rulesinsingleList[z] = cur.antecedent[0];
            ruleOddsRatios[z] = -cur.oddsRatio; //put negative to use Arrays.sort and get descending order
            z = z + 1;
            cur = cur.nextRule;
        }

        //test
        //   for(int m=0; m<rulesinsingleList.length; m++){
        //     System.out.println("rules in singleList: "+rulesinsingleList[m]);
        // }

        //create a map with keys are attributes, values are odds ratios
        //items in the same binary attribute will have the same odds ratio in this program
        for (int m = 0; m < rulesinsingleList.length; m++) {
            //int att;
            //if((rulesinsingleList[m] % 2)==0) att=rulesinsingleList[m]/2;
            //else att=rulesinsingleList[m]/2 +1;
            //hs.add
            int att = 0;
            for (i = 0; i < realAtt; i++) {

                //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
                //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

                if (dataSpacestat[i].min <= rulesinsingleList[m] && rulesinsingleList[m] <= dataSpacestat[i].max) {
                    // column index in dataSpace corresponding to node.nodeID in binary matrix.
                    att = i + 1;
                    //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
                    //System.out.println("dataSpacestat["+i+"].min="+dataSpacestat[i].min);
                    //System.out.println("dataSpacestat["+i+"].max="+dataSpacestat[i].max);
                }

            }
            //myLocalMap.put(att, ruleOddsRatios[m]);
            myLocalMap.put(Integer.valueOf(att), Double.valueOf(ruleOddsRatios[m]));
            // System.out.println("Original rules in singleList: "+rulesinsingleList[m]);
            // System.out.println("Corresponding  Odds ratio: "+ruleOddsRatios[m]);
        }

        //System.out.println("Keys of tree map: " + myLocalMap.keySet());
        //System.out.println("Values of tree map: " + myLocalMap.values());


        // Reading and count the co-occurence between other items and class item
        for (i = 0; i < maxData; i++) {
            int search = 0;
            for (int x = 0; x < realAtt; x++) {
                for (int y = 0; y < itemclass.length; y++) {
                    if (dataSpace[i][x] == itemclass[y]) search = search + 1;
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
                //   System.out.println("item:"+item);

                for (int x = 0; x < singleList.numOfRule; x++) {

                    if (rulesinsingleList[x] == item) {
                        countercfd[targetvalue][item]++;
                        countercfd[2][item]++;
                        //      System.out.println("countercfd["+targetvalue+"]["+item+"]="+countercfd[targetvalue][item]);
                        //       System.out.println("countercfd[2]["+item+"]="+countercfd[2][item]);
                    }
                }

            }
            targetvalue = 0;
        }


        //test print out countercfd
        /*
           for (int x=0; x<countercfd.length; x++){
                    for (int y=0; y<countercfd[x].length; y++){
                       System.out.println("countercfd["+x+"]["+y+"]="+countercfd[x][y]);
                    }
            }
          */
        k = 0;
        //find the associations
        //1. find the local min support. if an item local min support<< the ignore

        int[] lmsupport;
        lmsupport = new int[2];
        lmsupport[0] = (int) (countercfd[0][0] * gsup + 0.5);
        lmsupport[1] = (int) (countercfd[1][0] * gsup + 0.5);


        //   System.out.println("lmsupport[0]"+lmsupport[0]+"lmsupport[1]"+lmsupport[1]);
        for (int x = 1; x < maxItem + 2; x++) {

            if (countercfd[2][x] == 0) continue;

            //exclude exclusiveness
            int pq = countercfd[1][x];
            int npq = countercfd[0][x];
            int pnq = (countercfd[1][0] - countercfd[1][x]);
            int epsilon0 = lmsupport[0];
            int epsilon1 = lmsupport[1];

            //1. sup(npq)<epsilon
            if (npq < epsilon0) {
                ignoreList[ignoreind++] = x;
                //  System.out.println("Exclude " + x +" as sup(npq)="+countercfd[0][x]+", but epsilon=lsup= "+lmsupport[0] );
                continue;
            }
            //2. sup(pq)<epsilon
            if (pq < epsilon1) {
                ignoreList[ignoreind++] = x;
                //   System.out.println("Exclude " + x +" as sup(pq)="+countercfd[1][x]+", but epsilon=lsup= "+lmsupport[1] );
                continue;
            }
            //3. sup(pnq)<epsilon

            if (pnq < epsilon1) {
                ignoreList[ignoreind++] = x;
                //   System.out.println("Exclude " + x +" as sup(pnq)="+pnq+", but epsilon=lsup= "+lmsupport[1] );
                continue;
            }


            //ignore itself
            for (int q = 0; q < itemclass.length; q++) {
                if (x == itemclass[q]) ignoreList[ignoreind++] = x;

            }


            //////////////////////////////

            //Do not use confounders

			   /*
               //if (countercfd[2][x]<gMinSup) continue;
               //calculate the odds ratio
               double pc, pnc, p, npc, npnc, np, leftend, rightend;
               double oddsratio;
               p=countercfd[2][x];
               pc=countercfd[1][x];
               pnc=countercfd[0][x];
               npc=countercfd[1][0]-countercfd[1][x];
               npnc=maxData-countercfd[2][x]+countercfd[1][x]-countercfd[1][0];
              //if the value==0 assign 1
               if(pnc==0) pnc=1;
               if(npc==0) npc=1;
               if(npnc==0) npnc=1;
         //      System.out.println("item:"+x+" pc:"+pc+" pnc:"+pnc+" npc:"+npc+"npnc:"+npnc);
               oddsratio=(pc*npnc)/(pnc*npc);
               leftend=Math.exp(Math.log(oddsratio)-1.96*Math.sqrt((1/pc) + (1/npnc)+(1/npc) +(1/pnc)));
               rightend=Math.exp(Math.log(oddsratio)+1.96*Math.sqrt((1/pc) + (1/npnc)+(1/npc)+(1/pnc)));

               //if pass local supports and oddsratio>1.5 then ->confounderList
               //we need confounderList as items with the same attribute with item in confounderList will be
               //removed out of the control list.

               //switch two methods between low bound and odds ratio. modified by mss
//               if(statisticTest==1)
//              	  if(oddsratio < staThreshold) continue;
//                else
              	  if (leftend<=1) continue;
//               if(leftend<=1) continue;
              // if(oddsratio<1.5) continue;
            //       System.out.println("item:"+x+"oddsratio="+oddsratio);

               confounderList[k++]=x;

			   */
        }


           /*
           // modified by mss
           // find all confounder and ignore items in attribute level
           // i.e. if one item is found in confounderlist or ignorelist, then remove all items belonging the same attribute.
           if(controlAttribute == 1) {
	           int confounderListInd = 0;
	           for(i=0; i<confounderList.length; i++)
	        	   if(confounderList[i]==0) {
	        		   confounderListInd = i;
	        		   break;
	        	   }
	           int[] cfdClone = confounderList.clone();
	           for(i=0; i<confounderListInd-1; i++)
	        	   for(j=i+1; j<confounderListInd; j++)
	        		   if(itemRecord[confounderList[i]].attName.equals(itemRecord[confounderList[j]].attName))
	        			   cfdClone[j] = 0;
	           confounderList = cfdClone.clone();
	           cfdClone=trimzeros(cfdClone);
	           Arrays.sort(cfdClone);

	           int[] friendsOfcfd = new int[1000];
	           int friendsOfcfdInd = 0;
	           for (i=0; i<cfdClone.length; i++) {
	        	   if(cfdClone[i] == 0)
	        		   continue;
	        	   for(j=0; j<realAtt; j++)
	        		   if(Arrays.binarySearch(attValue[j], cfdClone[i]) >= 0) {
	        			   for(int l=0; l<attValue[j].length; l++) {
	        				   if(Arrays.binarySearch(cfdClone, attValue[j][l]) < 0 && attValue[j][l] != 0)
	        					   friendsOfcfd[friendsOfcfdInd++] = attValue[j][l];
	        			   }
	        			   break;
	        		   }
	           }
	           friendsOfcfd=trimzeros(friendsOfcfd);
	           for (i=0; i<friendsOfcfd.length; i++) {
	        	   confounderList[confounderListInd++]=friendsOfcfd[i];
	           }
	           confounderList=trimzeros(confounderList);
	           Arrays.sort(confounderList);
	           listcfdrelated = confounderList.clone();

	           int[] ignoreClone = ignoreList.clone();
	           int ignoreListInd = 0;
	           for(i=0; i<ignoreList.length; i++)
	        	   if(ignoreList[i]==0) {
	        		   ignoreListInd = i;
	        		   break;
	        	   }
	           int ignoreTempInd = ignoreListInd;
	           for(i=0; i<ignoreListInd-1; i++) {
	        	   for(j=i+1; j<ignoreListInd; j++)
	        		   if(itemRecord[ignoreList[i]].attName.equals(itemRecord[ignoreList[j]].attName)) {
	        			   ignoreClone[j] = 0;
	        			   break;
	        		   }
	           }
	           for(i=0; i<ignoreListInd; i++) {
	    		   int flag = 0;
	    		   if(ignoreClone[i] == 0)
	    			   continue;
	        	   for(j=0; j<confounderList.length; j++)
	        		   if(itemRecord[ignoreClone[i]].attName.equals(itemRecord[confounderList[j]].attName)) {
	        			   ignoreClone[i] = 0;
	        			   flag = 1;
	        			   break;
	        		   }
	        	   if(flag == 0)
	            	   for(j=0; j<realAtt; j++)
	            		   if(Arrays.binarySearch(attValue[j], ignoreClone[i]) >= 0) {
	            			   for(int l=0; l<attValue[j].length; l++) {
	            				   if(attValue[j][l] != ignoreClone[i])
	            					   ignoreClone[ignoreTempInd++] = attValue[j][l];
	            			   }
	            			   break;
	            		   }
	           }
	           ignoreList = ignoreClone.clone();
	           ignoreList=trimzeros(ignoreList);
	           Arrays.sort(ignoreList);

//	           int noncfdInd=0;
//        	   for(i=0; i<realAtt; i++)
//    			   for(j=0; j<attValue[i].length; j++)
//    				   if(Arrays.binarySearch(confounderList, attValue[i][j]) < 0)
//    					   nonconfounderList[noncfdInd++] = attValue[i][j];
//        	   for(i=0; i<itemclass.length; i++)
//        		   for(j=0; j<noncfdInd; j++) {
//        			   if(itemclass[i] == nonconfounderList[j])
//        				   nonconfounderList[j] = 0;
//            		   if(Arrays.binarySearch(ignoreList, nonconfounderList[j]) >= 0)
//            			   nonconfounderList[j] = 0;
//            	   }
           }
           else {
           ////

           //find confounderrelated list
           int indcfd=0;
           for (int x=0; x<rulesinsingleList.length; x++){
                  for(int y=0;y<confounderList.length;y++){
                   if(confounderList[y]!=0 && itemRecord[(confounderList[y])].attName.equals(
                       itemRecord[rulesinsingleList[x]].attName)){
//                      int check=0;
//                       //check if the new comer already exist
//                       for(int v=0; v<indcfd;v++){
//                           if(listcfdrelated[v]==rulesinsingleList[x]){
//                               check=1;
//                               System.out.println("don't add "+rulesinsingleList[x]+" as already exist");
//                               break;
//                           }
//                       }
//                       if(check==0){
                       //check if the rule is already in listcfdrelated if yes do not add.
                       //Arrays.sort(listcfdrelated);
                      //if(Arrays.binarySearch(listcfdrelated, rulesinsingleList[x])<0){

                       boolean found=false;
                       for(int p=0; p<listcfdrelated.length; p++){
                           if(listcfdrelated[p]==rulesinsingleList[x]){
                                found=true;
                                break;
                           }
                       }
                       if(!found){

                       listcfdrelated[indcfd++]=rulesinsingleList[x];

                            //System.out.println("listcfdrelated["+(indcfd-1)+"]="+listcfdrelated[indcfd-1]+" as confounder:"+confounderList[y]);
                      }
                   }
                  }

            }
           }
//           Arrays.sort(listcfdrelated);
//           listcfdrelated=trimzeros(listcfdrelated);
           //System.out.println("listcfdrelated");
           //printRecord(listcfdrelated);
        //   nonconfounderList= Arrays.asList(rulesinsingleList).retainAll(listcfdrelated);
           //find the nonconfounder list=(ruleinsingleList \ listcfdrelated)\ ignoreList
           int noncfdind=0;

           for(int x=0; x<rulesinsingleList.length; x++){
             //  if(rulesinsingleList[x]==itemclass) continue;
               int flag=0;
               //remove listcfdrelated
               for (int y=0; y<listcfdrelated.length; y++){
                   if(rulesinsingleList[x]==listcfdrelated[y]){
                       flag=1;
                       break;
                   }

               }
               //remove ignorelist
               for (int y=0; y<ignoreList.length; y++){
                   if(rulesinsingleList[x]==ignoreList[y]){
                       flag=1;
                       break;
                   }

               }

               if(flag==0)    nonconfounderList[noncfdind++]=rulesinsingleList[x];


           }

           // modified by mss
           // For one attribute, if none of its items are confounded to the exposure,
           // then keep all the items within this attribute (including irrelevant items with the target).
           if(controlAttribute == 1) {
	           int nonconfounderListInd = 0;
	           for(i=0; i<nonconfounderList.length; i++)
	        	   if(nonconfounderList[i]==0) {
	        		   nonconfounderListInd = i;
	        		   break;
	        	   }
	           int[] noncfdClone = nonconfounderList.clone();
	           for(i=0; i<nonconfounderListInd-1; i++)
	        	   for(j=i+1; j<nonconfounderListInd; j++)
	        		   if(itemRecord[nonconfounderList[i]].attName.equals(itemRecord[nonconfounderList[j]].attName))
	        			   noncfdClone[j] = 0;
	           nonconfounderList = noncfdClone.clone();
	           noncfdClone=trimzeros(noncfdClone);
	           Arrays.sort(noncfdClone);

	           int[] friendsOfnoncfd = new int[1000];
	           int friendsOfnoncfdInd = 0;
	           for (i=0; i<noncfdClone.length; i++) {
	        	   if(noncfdClone[i] == 0)
	        		   continue;
	        	   for(j=0; j<realAtt; j++)
	        		   if(Arrays.binarySearch(attValue[j], noncfdClone[i]) >= 0) {
	        			   for(int l=0; l<attValue[j].length; l++) {
	        				   if(Arrays.binarySearch(noncfdClone, attValue[j][l]) < 0)
	        					   friendsOfnoncfd[friendsOfnoncfdInd++] = attValue[j][l];
	        			   }
	        			   break;
	        		   }
	           }
	           friendsOfnoncfd=trimzeros(friendsOfnoncfd);
	           for (i=0; i<friendsOfnoncfd.length; i++) {
	        	   nonconfounderList[nonconfounderListInd++]=friendsOfnoncfd[i];
	           }
           }
           ////


		   */

        //  System.out.println("Exclusive List");
        ignoreList = trimzeros(ignoreList);
        //printRecord(ignoreList);
        //Removing exclusives
        if (ignoreList.length > 0) {
            for (int ind = 0; ind < ignoreList.length; ind++) {
                //convert ignore item to attribute
                //int att;
                //if((ignoreList[ind] % 2)==0) att=ignoreList[ind]/2;
                //else att=ignoreList[ind]/2 +1;
                //hs.add
                int att = 0;
                for (i = 0; i < realAtt; i++) {

                    //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
                    //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

                    if (dataSpacestat[i].min <= ignoreList[ind] && ignoreList[ind] <= dataSpacestat[i].max) {
                        // column index in dataSpace corresponding to node.nodeID in binary matrix.
                        att = i + 1;
                        //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
                        //System.out.println("dataSpacestat["+i+"].min="+dataSpacestat[i].min);
                        //System.out.println("dataSpacestat["+i+"].max="+dataSpacestat[i].max);
                    }

                }
                //remove the attribute from map

                myLocalMap.remove(att);
            }

        }
        //get remaining attributes after removing ignoreList
        Set<Integer> keyset = myLocalMap.keySet();
        int[] arr = new int[keyset.size()];
        int index = 0;
        for (Integer attr : keyset) {
            arr[index++] = attr; //note the autounboxing here
        }
        //printRecord(arr);


        //get corresponding oddsratios
        double[] oddsarray = new double[arr.length];
        for (int v = 0; v < arr.length; v++) {
            oddsarray[v] = myLocalMap.get(arr[v]);
        }


        //create a new map with odds are key attributes are values
        //odds need to be unique. This is to sort the attributes based on odds ratios
        Map<Double, Integer> myLocalMapReverse = new HashMap<Double, Integer>();
        for (int m = 0; m < arr.length; m++) {
            myLocalMapReverse.put(Double.valueOf(oddsarray[m]), Integer.valueOf(arr[m]));
        }

        //System.out.println("Keys of tree map: " + myLocalMapReverse.keySet());
        //System.out.println("Values of tree map: " + myLocalMapReverse.values());

        //sort the odds values
        Arrays.sort(oddsarray);


        for (int indx = 0; indx < arr.length; indx++) {
            int temp = myLocalMapReverse.get(oddsarray[indx]).intValue();
            //	System.out.println("Keys of tree map: " +temp);
            //convert the attribute to 2 binary items. This only works for binary data sets.
            //nonconfounderList[2*indx]=temp*2-1;
            //nonconfounderList[2*indx+1]=temp*2;
            for (i = 0, j = dataSpacestat[temp - 1].min; i < (dataSpacestat[temp - 1].max - dataSpacestat[temp - 1].min + 1); i++) {
                nonconfounderList[j] = (dataSpacestat[temp - 1].min + i);
                j++;
            }

        }

        nonconfounderList = trimzeros(nonconfounderList);

        //System.out.println("Control List");
        //printRecord(nonconfounderList);
        return nonconfounderList;

    }


    //find confounder variables of the item
    public int[] findnonConfounders(int itemclass) {
        System.out.println("hushutest0");
        int[] ignoreList;
        int[] confounderList;
        int[] listcfdrelated;
        int[] nonconfounderList;
        // modified by mss
        listcfdrelated = new int[maxItem * 2];
        confounderList = new int[maxItem * 2];
        nonconfounderList = new int[maxItem * 2];
        ignoreList = new int[maxItem * 2];
        ////
        int k;
        int[][] countercfd;
        int[] lMinSupcfd;
        double[] distcfd;
        //initial the counter
        countercfd = new int[3][];
        for (k = 0; k < 3; k++) {
            countercfd[k] = new int[maxItem + 2];
        }
        //initial parameter
        int ignoreind = 0;
        int i, j, item;
        int targetvalue = 0;
        lMinSupcfd = new int[4];
        distcfd = new double[4];
        double[] ruleOddsRatios;
        ruleOddsRatios = new double[singleList.numOfRule];
        System.out.println("singleList.numOfRule" + singleList.numOfRule);
        int z = 0;
        Map<Integer, Double> myLocalMap = new HashMap<Integer, Double>();


        System.out.println("hushutest1");

        RuleStru cur;
        RuleSet ruleset = new RuleSet();
        int[] rulesinsingleList;
        ruleset = singleList;
        cur = ruleset.ruleHead;

        //Take the rule list from singleList
        rulesinsingleList = new int[singleList.numOfRule];

        while (cur != null) {
            //System.out.println("cur.antecedent[0]"+cur.antecedent[0]);

            rulesinsingleList[z] = cur.antecedent[0];
            ruleOddsRatios[z] = -cur.oddsRatio; //put negative to use Arrays.sort and get descending order
            z = z + 1;
            cur = cur.nextRule;
        }

        //create a map with keys are attributes, values are odds ratios
        //items in the same binary attribute will have the same odds ratio in this program
        //System.out.println("rulesinsingleList.length"+rulesinsingleList.length);
        //int att;
        for (int m = 0; m < rulesinsingleList.length; m++) {
            int att = 0;
            //int att;
            //if((rulesinsingleList[m] % 2)==0) att=rulesinsingleList[m]/2;
            //else att=rulesinsingleList[m]/2 +1;
            //hs.add
            for (i = 0; i < realAtt; i++) {

                //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
                //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

                if (dataSpacestat[i].min <= rulesinsingleList[m] && rulesinsingleList[m] <= dataSpacestat[i].max) {
                    // column index in dataSpace corresponding to node.nodeID in binary matrix.
                    att = i + 1;
                    //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
                    //System.out.println("dataSpacestat["+i+"].min="+dataSpacestat[i].min);
                    //System.out.println("dataSpacestat["+i+"].max="+dataSpacestat[i].max);
                }

            }
            //myLocalMap.put(att, ruleOddsRatios[m]);
            //System.out.println("att="+att);
            myLocalMap.put(Integer.valueOf(att), Double.valueOf(ruleOddsRatios[m]));
            //System.out.println("Original rules in singleList: "+rulesinsingleList[m]);
            //System.out.println("Corresponding  Odds ratio: "+ruleOddsRatios[m]);
        }

        //System.out.println("Keys of tree map: " + myLocalMap.keySet());
        //System.out.println("Values of tree map: " + myLocalMap.values());

        //System.out.println("itemclass"+itemclass);
        // Reading and count the co-occurence between other items and class item
        for (i = 0; i < maxData; i++) {
            for (j = 0; j < realAtt; j++) {
                //identify and count class item
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
                //   System.out.println("item:"+item);

                for (int x = 0; x < singleList.numOfRule; x++) {

                    if (rulesinsingleList[x] != itemclass && rulesinsingleList[x] == item) {
                        countercfd[targetvalue][item]++;
                        countercfd[2][item]++;
                        //System.out.println("countercfd["+targetvalue+"]["+item+"]="+countercfd[targetvalue][item]);
                        //System.out.println("countercfd[2]["+item+"]="+countercfd[2][item]);
                    }
                }

            }
            targetvalue = 0;
        }

        System.out.println("hushutest2");


        //test print out countercfd

        //  for (int x=0; x<countercfd.length; x++){
        //           for (int y=0; y<countercfd[x].length; y++){
        //              System.out.println("countercfd["+x+"]["+y+"]="+countercfd[x][y]);
        //           }
        //    }


        k = 0;
        //find the associations
        //1. find the local min support. if an item local min support<< the ignore

        int[] lmsupport;
        lmsupport = new int[2];
        lmsupport[0] = (int) (countercfd[0][0] * gsup + 0.5);
        lmsupport[1] = (int) (countercfd[1][0] * gsup + 0.5);
        //     System.out.println("lmsupport[0]"+lmsupport[0]+"lmsupport[1]"+lmsupport[1]);
        for (int x = 1; x < maxItem + 2; x++) {
            //ignore itself
            if (x == itemclass) {
                ignoreList[ignoreind++] = x;
                continue;
            }
            //skip items not in association rule list.


            //global. We donot need global check as countercfd[2][x]=counter[2][x], and x already pass global.
            if (countercfd[2][x] == 0) continue;
            //local
            //exclude exclusiveness
            int pq = countercfd[1][x];
            int npq = countercfd[0][x];
            int pnq = (countercfd[1][0] - countercfd[1][x]);
            int epsilon0 = lmsupport[0];
            int epsilon1 = lmsupport[1];
            //sup(npq)<epsilon
            if (npq < epsilon0) {
                ignoreList[ignoreind++] = x;
                //  System.out.println("Exclude " + x +" as sup(npq)="+countercfd[0][x]+", but epsilon=lsup= "+lmsupport[0] );
                continue;
            }
            //sup(pq)<epsilon
            if (pq < epsilon1) {
                ignoreList[ignoreind++] = x;
                //   System.out.println("Exclude " + x +" as sup(pq)="+countercfd[1][x]+", but epsilon=lsup= "+lmsupport[1] );
                continue;
            }
            //sup(pnq)<epsilon

            if (pnq < epsilon1) {
                ignoreList[ignoreind++] = x;
                //   System.out.println("Exclude " + x +" as sup(pnq)="+pnq+", but epsilon=lsup= "+lmsupport[1] );
                continue;
            }

            //Do not use confounders

			   /*
               //if (countercfd[2][x]<gMinSup) continue;
               //calculate the odds ratio
               double pc, pnc, p, npc, npnc, np, leftend, rightend;
               double oddsratio;
               p=countercfd[2][x];
               pc=countercfd[1][x];
               pnc=countercfd[0][x];
               npc=countercfd[1][0]-countercfd[1][x];
               npnc=maxData-countercfd[2][x]+countercfd[1][x]-countercfd[1][0];
              //if the value==0 assign 1
               if(pnc==0) pnc=1;
               if(npc==0) npc=1;
               if(npnc==0) npnc=1;
     // if(itemclass==57||itemclass==58)         System.out.println("item:"+x+" pc:"+pc+" pnc:"+pnc+" npc:"+npc+"npnc:"+npnc);
               oddsratio=(pc*npnc)/(pnc*npc);
               leftend=Math.exp(Math.log(oddsratio)-1.96*Math.sqrt((1/pc) + (1/npnc)+(1/npc) +(1/pnc)));
               rightend=Math.exp(Math.log(oddsratio)+1.96*Math.sqrt((1/pc) + (1/npnc)+(1/npc)+(1/pnc)));

               //if pass local supports and oddsratio>1.5 then ->confounderList
               //we need confounderList as items with the same attribute with item in confounderList will be
               //removed out of the control list.

               //switch two methods between low bound and odds ratio. modified by mss
//               if(statisticTest==1)
//             	  if(oddsratio < staThreshold) continue;
//               else
             	  if (leftend<=1) continue;
//               if(leftend<=1) continue;
              // if(oddsratio<1.5) continue;
        // if(itemclass==57||itemclass==58)          System.out.println("item:"+x+"oddsratio="+oddsratio);

               confounderList[k++]=x;

			   */

        }
        System.out.println("hushutest3");
        //System.out.println("ignoreList or exclusive variables");
        //printRecord(ignoreList);
           /*


        //  System.out.println("ignoreList or exclusive variables");
		//	printRecord(ignoreList);
           // modified by mss
           // find all confounder and ignore items in attribute level
           // i.e. if one item is found in confounderlist or ignorelist, then remove all items belonging the same attribute.
           if(controlAttribute == 1) {
	           int confounderListInd = 0;
	           for(i=0; i<confounderList.length; i++)
	        	   if(confounderList[i]==0) {
	        		   confounderListInd = i;
	        		   break;
	        	   }
	           int[] cfdClone = confounderList.clone();
	           for(i=0; i<confounderListInd-1; i++)
	        	   for(j=i+1; j<confounderListInd; j++)
	        		   if(itemRecord[confounderList[i]].attName.equals(itemRecord[confounderList[j]].attName))
	        			   cfdClone[j] = 0;
	           confounderList = cfdClone.clone();
	           cfdClone=trimzeros(cfdClone);
	           Arrays.sort(cfdClone);

	           int[] friendsOfcfd = new int[1000];
	           int friendsOfcfdInd = 0;
	           for (i=0; i<cfdClone.length; i++) {
	        	   if(cfdClone[i] == 0)
	        		   continue;
	        	   for(j=0; j<realAtt; j++)
	        		   if(Arrays.binarySearch(attValue[j], cfdClone[i]) >= 0) {
	        			   for(int l=0; l<attValue[j].length; l++) {
	        				   if(Arrays.binarySearch(cfdClone, attValue[j][l]) < 0 && attValue[j][l] != 0)
	        					   friendsOfcfd[friendsOfcfdInd++] = attValue[j][l];
	        			   }
	        			   break;
	        		   }
	           }
	           friendsOfcfd=trimzeros(friendsOfcfd);
	           for (i=0; i<friendsOfcfd.length; i++) {
	        	   confounderList[confounderListInd++]=friendsOfcfd[i];
	           }
	           confounderList=trimzeros(confounderList);
	           Arrays.sort(confounderList);
	           listcfdrelated = confounderList.clone();

	           int[] ignoreClone = ignoreList.clone();
	           int ignoreListInd = 0;
	           for(i=0; i<ignoreList.length; i++)
	        	   if(ignoreList[i]==0) {
	        		   ignoreListInd = i;
	        		   break;
	        	   }
	           int ignoreTempInd = ignoreListInd;
	           for(i=0; i<ignoreListInd-1; i++) {
	        	   for(j=i+1; j<ignoreListInd; j++)
	        		   if(itemRecord[ignoreList[i]].attName.equals(itemRecord[ignoreList[j]].attName)) {
	        			   ignoreClone[j] = 0;
	        			   break;
	        		   }
	           }
	           for(i=0; i<ignoreListInd; i++) {
	    		   int flag = 0;
	    		   if(ignoreClone[i] == 0)
	    			   continue;
	        	   for(j=0; j<confounderList.length; j++)
	        		   if(itemRecord[ignoreClone[i]].attName.equals(itemRecord[confounderList[j]].attName)) {
	        			   ignoreClone[i] = 0;
	        			   flag = 1;
	        			   break;
	        		   }
	        	   if(flag == 0)
	            	   for(j=0; j<realAtt; j++)
	            		   if(Arrays.binarySearch(attValue[j], ignoreClone[i]) >= 0) {
	            			   for(int l=0; l<attValue[j].length; l++) {
	            				   if(attValue[j][l] != ignoreClone[i])
	            					   ignoreClone[ignoreTempInd++] = attValue[j][l];
	            			   }
	            			   break;
	            		   }
	           }
	           ignoreList = ignoreClone.clone();
	           ignoreList=trimzeros(ignoreList);
	           Arrays.sort(ignoreList);

//	           int noncfdInd=0;
//        	   for(i=0; i<realAtt; i++)
//    			   for(j=0; j<attValue[i].length; j++)
//    				   if(Arrays.binarySearch(confounderList, attValue[i][j]) < 0)
//    					   nonconfounderList[noncfdInd++] = attValue[i][j];
//        	   for(i=0; i<noncfdInd; i++) {
//        		   if(itemRecord[nonconfounderList[i]].attName.equals(itemRecord[itemclass].attName))
//        			   nonconfounderList[i] = 0;
//        		   if(Arrays.binarySearch(ignoreList, nonconfounderList[i]) >= 0)
//        			   nonconfounderList[i] = 0;
//        	   }
           }
           else {
           ////

           //print the confounder list, all the rule list
//           System.out.println("All rules");
//           printRecord(rulesinsingleList);
//           System.out.println("Confounders:");
//           printRecord(confounderList);
//           System.out.println("Ignores:");
//           printRecord(ignoreList);
           //find confounderrelated list
           int indcfd=0;

           for (int x=0; x<rulesinsingleList.length; x++){
                  for(int y=0;y<confounderList.length;y++){
                   if(confounderList[y]!=0 && itemRecord[(confounderList[y])].attName.equals(
                       itemRecord[rulesinsingleList[x]].attName)){
                       //sort the current listcfdrelated. This is needed for the Arrays.binarysearch holds
                     // Arrays.sort(listcfdrelated);
                     //System.out.println("listcfdrelated before:");
                     //printRecord(listcfdrelated);
                       //check if the new item is already in this list
                       boolean found=false;
                       for(int p=0; p<listcfdrelated.length; p++){
                           if(listcfdrelated[p]==rulesinsingleList[x]){
                               found=true;
                               break;
                           }

                       }
                       if(!found){
                            listcfdrelated[indcfd++]=rulesinsingleList[x];
                          //  System.out.println("listcfdrelated["+(indcfd-1)+"]="+listcfdrelated[indcfd-1]+" as confounder:"+confounderList[y]);
                       }

                   }
                  }

            }
           }

          // Arrays.sort(listcfdrelated);

          // listcfdrelated=trimzeros(listcfdrelated);
          //  System.out.println("listcfdrelated");
         //   printRecord(listcfdrelated);
        //   nonconfounderList= Arrays.asList(rulesinsingleList).retainAll(listcfdrelated);
           //find the nonconfounder list=(ruleinsingleList \ listcfdrelated)\ ignoreList



		   */

        // System.out.println("Exclusive List");
        ignoreList = trimzeros(ignoreList);
        //System.out.println("ignoreList");
        //printRecord(ignoreList);
        //Removing exclusives
        if (ignoreList.length > 0) {
            for (int ind = 0; ind < ignoreList.length; ind++) {
                //convert ignore item to attribute
                //int att;
                //if((ignoreList[ind] % 2)==0) att=ignoreList[ind]/2;
                //else att=ignoreList[ind]/2 +1;

                //hs.add
                int att = 0;
                for (i = 0; i < realAtt; i++) {

                    //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
                    //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

                    if (dataSpacestat[i].min <= ignoreList[ind] && ignoreList[ind] <= dataSpacestat[i].max) {
                        // column index in dataSpace corresponding to node.nodeID in binary matrix.
                        att = i + 1;
                        //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
                        //System.out.println("dataSpacestat["+i+"].min="+dataSpacestat[i].min);
                        //System.out.println("dataSpacestat["+i+"].max="+dataSpacestat[i].max);
                    }

                }


                //remove the attribute from map

                myLocalMap.remove(att);
            }

        }
        System.out.println("hushutest4");

		/*
          int noncfdind=0;

           for(int x=0; x<rulesinsingleList.length; x++){
               if(rulesinsingleList[x]==itemclass) continue;
               int flag=0;
               //remove listcfdrelated
               for (int y=0; y<listcfdrelated.length; y++){
                   if(rulesinsingleList[x]==listcfdrelated[y]){
                       flag=1;
                       break;
                   }

               }
               //remove ignorelist
               for (int y=0; y<ignoreList.length; y++){
                   if(rulesinsingleList[x]==ignoreList[y]){
                       flag=1;
                       break;
                   }

               }

               if(flag==0)    nonconfounderList[noncfdind++]=rulesinsingleList[x];


           }
           */
        // modified by mss
        // For one attribute, if none of its items are confounded to the exposure,
        // then keep all the items within this attribute (including irrelevant items with the target).


		   /*
           if(controlAttribute == 1) {
	           int nonconfounderListInd = 0;
	           for(i=0; i<nonconfounderList.length; i++)
	        	   if(nonconfounderList[i]==0) {
	        		   nonconfounderListInd = i;
	        		   break;
	        	   }
	           int[] noncfdClone = nonconfounderList.clone();
	           for(i=0; i<nonconfounderListInd-1; i++)
	        	   for(j=i+1; j<nonconfounderListInd; j++)
	        		   if(itemRecord[nonconfounderList[i]].attName.equals(itemRecord[nonconfounderList[j]].attName))
	        			   noncfdClone[j] = 0;
	           nonconfounderList = noncfdClone.clone();
	           noncfdClone=trimzeros(noncfdClone);
	           Arrays.sort(noncfdClone);

	           int[] friendsOfnoncfd = new int[1000];
	           int friendsOfnoncfdInd = 0;
	           for (i=0; i<noncfdClone.length; i++) {
	        	   if(noncfdClone[i] == 0)
	        		   continue;
	        	   for(j=0; j<realAtt; j++)
	        		   if(Arrays.binarySearch(attValue[j], noncfdClone[i]) >= 0) {
	        			   for(int l=0; l<attValue[j].length; l++) {
	        				   if(Arrays.binarySearch(noncfdClone, attValue[j][l]) < 0)
	        					   friendsOfnoncfd[friendsOfnoncfdInd++] = attValue[j][l];
	        			   }
	        			   break;
	        		   }
	           }
	           friendsOfnoncfd=trimzeros(friendsOfnoncfd);
	           for (i=0; i<friendsOfnoncfd.length; i++) {
	        	   nonconfounderList[nonconfounderListInd++]=friendsOfnoncfd[i];
	           }
           }

           ////
           //Remove sorting to get the controlist with decreasing odds ratio w.r.t target
           //Arrays.sort(nonconfounderList);

		   */
        //Rank all attributes left based on odds ratio
        // Arrays.sort(ruleOddsRatios);

        //get remaining attributes after removing ignoreList
        Set<Integer> keyset = myLocalMap.keySet();
        int[] arr = new int[keyset.size()];
        int index = 0;
        for (Integer attr : keyset) {
            arr[index++] = attr; //note the autounboxing here
        }
        //printRecord(arr);


        //get corresponding oddsratios
        double[] oddsarray = new double[arr.length];
        for (int v = 0; v < arr.length; v++) {
            oddsarray[v] = myLocalMap.get(arr[v]);
        }


        //create a new map with odds are key attributes are values
        //odds need to be unique. This is to sort the attributes based on odds ratios
        Map<Double, Integer> myLocalMapReverse = new HashMap<Double, Integer>();
        for (int m = 0; m < arr.length; m++) {
            myLocalMapReverse.put(Double.valueOf(oddsarray[m]), Integer.valueOf(arr[m]));
        }

        //System.out.println("Keys of tree map: " + myLocalMapReverse.keySet());
        //System.out.println("Values of tree map: " + myLocalMapReverse.values());

        //sort the odds values
        Arrays.sort(oddsarray);

        //System.out.println("arr.length"+arr.length);
        for (int indx = 0; indx < arr.length; indx++) {
            int temp = myLocalMapReverse.get(oddsarray[indx]).intValue();
            //System.out.println("oddsarray[indx]: " +oddsarray[indx]);
            //System.out.println("Keys of tree map: " +temp);
            //System.out.println("Control Attribute: " +temp);
            //convert the attribute to 2 binary items. This only works for binary data sets.
            //nonconfounderList[2*indx]=temp*2-1;
            //nonconfounderList[2*indx+1]=temp*2;
            //System.out.println("dataSpacestat[temp-1].min="+dataSpacestat[temp-1].min);
            //System.out.println("dataSpacestat[temp-1].max="+dataSpacestat[temp-1].max);
            for (i = 0, j = dataSpacestat[temp - 1].min; i < (dataSpacestat[temp - 1].max - dataSpacestat[temp - 1].min + 1); i++) {
                nonconfounderList[j] = (dataSpacestat[temp - 1].min + i);
                j++;
            }

        }

        nonconfounderList = trimzeros(nonconfounderList);
        System.out.println("hushutest5");

        //System.out.println("Control List");
        //printRecord(nonconfounderList);

        return nonconfounderList;

    }

    //hs.add for user control
    public int[] findnonConfounders_singleList1(int itemclass) {
        System.out.println("hushutest0");
        int[] ignoreList;
        int[] confounderList;
        int[] listcfdrelated;
        int[] nonconfounderList;
        // modified by mss
        listcfdrelated = new int[maxItem * 2];
        confounderList = new int[maxItem * 2];
        nonconfounderList = new int[maxItem * 2];
        ignoreList = new int[maxItem * 2];
        ////
        int k;
        int[][] countercfd;
        int[] lMinSupcfd;
        double[] distcfd;
        //initial the counter
        countercfd = new int[3][];
        for (k = 0; k < 3; k++) {
            countercfd[k] = new int[maxItem + 2];
        }
        //initial parameter
        int ignoreind = 0;
        int i, j, item;
        int targetvalue = 0;
        lMinSupcfd = new int[4];
        distcfd = new double[4];
        double[] ruleOddsRatios;
        ruleOddsRatios = new double[singleList1.numOfRule];
        System.out.println("singleList1.numOfRule" + singleList1.numOfRule);
        int z = 0;
        Map<Integer, Double> myLocalMap = new HashMap<Integer, Double>();


        System.out.println("hushutest1");

        RuleStru cur;
        RuleSet ruleset = new RuleSet();
        int[] rulesinsingleList;
        ruleset = singleList1;
        cur = ruleset.ruleHead;

        //Take the rule list from singleList
        rulesinsingleList = new int[singleList1.numOfRule];

        while (cur != null) {
            //System.out.println("cur.antecedent[0]"+cur.antecedent[0]);

            rulesinsingleList[z] = cur.antecedent[0];
            ruleOddsRatios[z] = -cur.oddsRatio; //put negative to use Arrays.sort and get descending order
            z = z + 1;
            cur = cur.nextRule;
        }

        //create a map with keys are attributes, values are odds ratios
        //items in the same binary attribute will have the same odds ratio in this program
        //System.out.println("rulesinsingleList.length"+rulesinsingleList.length);
        //int att;
        for (int m = 0; m < rulesinsingleList.length; m++) {
            int att = 0;
            //int att;
            //if((rulesinsingleList[m] % 2)==0) att=rulesinsingleList[m]/2;
            //else att=rulesinsingleList[m]/2 +1;
            //hs.add
            for (i = 0; i < realAtt; i++) {

                //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
                //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

                if (dataSpacestat[i].min <= rulesinsingleList[m] && rulesinsingleList[m] <= dataSpacestat[i].max) {
                    // column index in dataSpace corresponding to node.nodeID in binary matrix.
                    att = i + 1;
                    //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
                    //System.out.println("dataSpacestat["+i+"].min="+dataSpacestat[i].min);
                    //System.out.println("dataSpacestat["+i+"].max="+dataSpacestat[i].max);
                }

            }
            //myLocalMap.put(att, ruleOddsRatios[m]);
            //System.out.println("att="+att);
            myLocalMap.put(Integer.valueOf(att), Double.valueOf(ruleOddsRatios[m]));
            //System.out.println("Original rules in singleList: "+rulesinsingleList[m]);
            //System.out.println("Corresponding  Odds ratio: "+ruleOddsRatios[m]);
        }

        //System.out.println("Keys of tree map: " + myLocalMap.keySet());
        //System.out.println("Values of tree map: " + myLocalMap.values());

        //System.out.println("itemclass"+itemclass);
        // Reading and count the co-occurence between other items and class item
        for (i = 0; i < maxData; i++) {
            for (j = 0; j < realAtt; j++) {
                //identify and count class item
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
                //   System.out.println("item:"+item);

                for (int x = 0; x < singleList1.numOfRule; x++) {

                    if (rulesinsingleList[x] != itemclass && rulesinsingleList[x] == item) {
                        countercfd[targetvalue][item]++;
                        countercfd[2][item]++;
                        //System.out.println("countercfd["+targetvalue+"]["+item+"]="+countercfd[targetvalue][item]);
                        //System.out.println("countercfd[2]["+item+"]="+countercfd[2][item]);
                    }
                }

            }
            targetvalue = 0;
        }

        System.out.println("hushutest2");


        //test print out countercfd

        //  for (int x=0; x<countercfd.length; x++){
        //           for (int y=0; y<countercfd[x].length; y++){
        //              System.out.println("countercfd["+x+"]["+y+"]="+countercfd[x][y]);
        //           }
        //    }


        k = 0;
        //find the associations
        //1. find the local min support. if an item local min support<< the ignore

        int[] lmsupport;
        lmsupport = new int[2];
        lmsupport[0] = (int) (countercfd[0][0] * gsup + 0.5);
        lmsupport[1] = (int) (countercfd[1][0] * gsup + 0.5);
        //     System.out.println("lmsupport[0]"+lmsupport[0]+"lmsupport[1]"+lmsupport[1]);
        for (int x = 1; x < maxItem + 2; x++) {
            //ignore itself
            if (x == itemclass) {
                ignoreList[ignoreind++] = x;
                continue;
            }
            //skip items not in association rule list.


            //global. We donot need global check as countercfd[2][x]=counter[2][x], and x already pass global.
            if (countercfd[2][x] == 0) continue;
            //local
            //exclude exclusiveness
            int pq = countercfd[1][x];
            int npq = countercfd[0][x];
            int pnq = (countercfd[1][0] - countercfd[1][x]);
            int epsilon0 = lmsupport[0];
            int epsilon1 = lmsupport[1];
            //sup(npq)<epsilon
            if (npq < epsilon0) {
                ignoreList[ignoreind++] = x;
                //  System.out.println("Exclude " + x +" as sup(npq)="+countercfd[0][x]+", but epsilon=lsup= "+lmsupport[0] );
                continue;
            }
            //sup(pq)<epsilon
            if (pq < epsilon1) {
                ignoreList[ignoreind++] = x;
                //   System.out.println("Exclude " + x +" as sup(pq)="+countercfd[1][x]+", but epsilon=lsup= "+lmsupport[1] );
                continue;
            }
            //sup(pnq)<epsilon

            if (pnq < epsilon1) {
                ignoreList[ignoreind++] = x;
                //   System.out.println("Exclude " + x +" as sup(pnq)="+pnq+", but epsilon=lsup= "+lmsupport[1] );
                continue;
            }

            //Do not use confounders

			   /*
               //if (countercfd[2][x]<gMinSup) continue;
               //calculate the odds ratio
               double pc, pnc, p, npc, npnc, np, leftend, rightend;
               double oddsratio;
               p=countercfd[2][x];
               pc=countercfd[1][x];
               pnc=countercfd[0][x];
               npc=countercfd[1][0]-countercfd[1][x];
               npnc=maxData-countercfd[2][x]+countercfd[1][x]-countercfd[1][0];
              //if the value==0 assign 1
               if(pnc==0) pnc=1;
               if(npc==0) npc=1;
               if(npnc==0) npnc=1;
     // if(itemclass==57||itemclass==58)         System.out.println("item:"+x+" pc:"+pc+" pnc:"+pnc+" npc:"+npc+"npnc:"+npnc);
               oddsratio=(pc*npnc)/(pnc*npc);
               leftend=Math.exp(Math.log(oddsratio)-1.96*Math.sqrt((1/pc) + (1/npnc)+(1/npc) +(1/pnc)));
               rightend=Math.exp(Math.log(oddsratio)+1.96*Math.sqrt((1/pc) + (1/npnc)+(1/npc)+(1/pnc)));

               //if pass local supports and oddsratio>1.5 then ->confounderList
               //we need confounderList as items with the same attribute with item in confounderList will be
               //removed out of the control list.

               //switch two methods between low bound and odds ratio. modified by mss
//               if(statisticTest==1)
//             	  if(oddsratio < staThreshold) continue;
//               else
             	  if (leftend<=1) continue;
//               if(leftend<=1) continue;
              // if(oddsratio<1.5) continue;
        // if(itemclass==57||itemclass==58)          System.out.println("item:"+x+"oddsratio="+oddsratio);

               confounderList[k++]=x;

			   */

        }
        System.out.println("hushutest3");
        //System.out.println("ignoreList or exclusive variables");
        //printRecord(ignoreList);
           /*


        //  System.out.println("ignoreList or exclusive variables");
		//	printRecord(ignoreList);
           // modified by mss
           // find all confounder and ignore items in attribute level
           // i.e. if one item is found in confounderlist or ignorelist, then remove all items belonging the same attribute.
           if(controlAttribute == 1) {
	           int confounderListInd = 0;
	           for(i=0; i<confounderList.length; i++)
	        	   if(confounderList[i]==0) {
	        		   confounderListInd = i;
	        		   break;
	        	   }
	           int[] cfdClone = confounderList.clone();
	           for(i=0; i<confounderListInd-1; i++)
	        	   for(j=i+1; j<confounderListInd; j++)
	        		   if(itemRecord[confounderList[i]].attName.equals(itemRecord[confounderList[j]].attName))
	        			   cfdClone[j] = 0;
	           confounderList = cfdClone.clone();
	           cfdClone=trimzeros(cfdClone);
	           Arrays.sort(cfdClone);

	           int[] friendsOfcfd = new int[1000];
	           int friendsOfcfdInd = 0;
	           for (i=0; i<cfdClone.length; i++) {
	        	   if(cfdClone[i] == 0)
	        		   continue;
	        	   for(j=0; j<realAtt; j++)
	        		   if(Arrays.binarySearch(attValue[j], cfdClone[i]) >= 0) {
	        			   for(int l=0; l<attValue[j].length; l++) {
	        				   if(Arrays.binarySearch(cfdClone, attValue[j][l]) < 0 && attValue[j][l] != 0)
	        					   friendsOfcfd[friendsOfcfdInd++] = attValue[j][l];
	        			   }
	        			   break;
	        		   }
	           }
	           friendsOfcfd=trimzeros(friendsOfcfd);
	           for (i=0; i<friendsOfcfd.length; i++) {
	        	   confounderList[confounderListInd++]=friendsOfcfd[i];
	           }
	           confounderList=trimzeros(confounderList);
	           Arrays.sort(confounderList);
	           listcfdrelated = confounderList.clone();

	           int[] ignoreClone = ignoreList.clone();
	           int ignoreListInd = 0;
	           for(i=0; i<ignoreList.length; i++)
	        	   if(ignoreList[i]==0) {
	        		   ignoreListInd = i;
	        		   break;
	        	   }
	           int ignoreTempInd = ignoreListInd;
	           for(i=0; i<ignoreListInd-1; i++) {
	        	   for(j=i+1; j<ignoreListInd; j++)
	        		   if(itemRecord[ignoreList[i]].attName.equals(itemRecord[ignoreList[j]].attName)) {
	        			   ignoreClone[j] = 0;
	        			   break;
	        		   }
	           }
	           for(i=0; i<ignoreListInd; i++) {
	    		   int flag = 0;
	    		   if(ignoreClone[i] == 0)
	    			   continue;
	        	   for(j=0; j<confounderList.length; j++)
	        		   if(itemRecord[ignoreClone[i]].attName.equals(itemRecord[confounderList[j]].attName)) {
	        			   ignoreClone[i] = 0;
	        			   flag = 1;
	        			   break;
	        		   }
	        	   if(flag == 0)
	            	   for(j=0; j<realAtt; j++)
	            		   if(Arrays.binarySearch(attValue[j], ignoreClone[i]) >= 0) {
	            			   for(int l=0; l<attValue[j].length; l++) {
	            				   if(attValue[j][l] != ignoreClone[i])
	            					   ignoreClone[ignoreTempInd++] = attValue[j][l];
	            			   }
	            			   break;
	            		   }
	           }
	           ignoreList = ignoreClone.clone();
	           ignoreList=trimzeros(ignoreList);
	           Arrays.sort(ignoreList);

//	           int noncfdInd=0;
//        	   for(i=0; i<realAtt; i++)
//    			   for(j=0; j<attValue[i].length; j++)
//    				   if(Arrays.binarySearch(confounderList, attValue[i][j]) < 0)
//    					   nonconfounderList[noncfdInd++] = attValue[i][j];
//        	   for(i=0; i<noncfdInd; i++) {
//        		   if(itemRecord[nonconfounderList[i]].attName.equals(itemRecord[itemclass].attName))
//        			   nonconfounderList[i] = 0;
//        		   if(Arrays.binarySearch(ignoreList, nonconfounderList[i]) >= 0)
//        			   nonconfounderList[i] = 0;
//        	   }
           }
           else {
           ////

           //print the confounder list, all the rule list
//           System.out.println("All rules");
//           printRecord(rulesinsingleList);
//           System.out.println("Confounders:");
//           printRecord(confounderList);
//           System.out.println("Ignores:");
//           printRecord(ignoreList);
           //find confounderrelated list
           int indcfd=0;

           for (int x=0; x<rulesinsingleList.length; x++){
                  for(int y=0;y<confounderList.length;y++){
                   if(confounderList[y]!=0 && itemRecord[(confounderList[y])].attName.equals(
                       itemRecord[rulesinsingleList[x]].attName)){
                       //sort the current listcfdrelated. This is needed for the Arrays.binarysearch holds
                     // Arrays.sort(listcfdrelated);
                     //System.out.println("listcfdrelated before:");
                     //printRecord(listcfdrelated);
                       //check if the new item is already in this list
                       boolean found=false;
                       for(int p=0; p<listcfdrelated.length; p++){
                           if(listcfdrelated[p]==rulesinsingleList[x]){
                               found=true;
                               break;
                           }

                       }
                       if(!found){
                            listcfdrelated[indcfd++]=rulesinsingleList[x];
                          //  System.out.println("listcfdrelated["+(indcfd-1)+"]="+listcfdrelated[indcfd-1]+" as confounder:"+confounderList[y]);
                       }

                   }
                  }

            }
           }

          // Arrays.sort(listcfdrelated);

          // listcfdrelated=trimzeros(listcfdrelated);
          //  System.out.println("listcfdrelated");
         //   printRecord(listcfdrelated);
        //   nonconfounderList= Arrays.asList(rulesinsingleList).retainAll(listcfdrelated);
           //find the nonconfounder list=(ruleinsingleList \ listcfdrelated)\ ignoreList



		   */

        // System.out.println("Exclusive List");
        ignoreList = trimzeros(ignoreList);
        //System.out.println("ignoreList");
        //printRecord(ignoreList);
        //Removing exclusives
        if (ignoreList.length > 0) {
            for (int ind = 0; ind < ignoreList.length; ind++) {
                //convert ignore item to attribute
                //int att;
                //if((ignoreList[ind] % 2)==0) att=ignoreList[ind]/2;
                //else att=ignoreList[ind]/2 +1;

                //hs.add
                int att = 0;
                for (i = 0; i < realAtt; i++) {

                    //System.out.println("dataSpacestat["+i+"].min"+dataSpacestat[i].min);
                    //System.out.println("dataSpacestat["+i+"].max"+dataSpacestat[i].max);

                    if (dataSpacestat[i].min <= ignoreList[ind] && ignoreList[ind] <= dataSpacestat[i].max) {
                        // column index in dataSpace corresponding to node.nodeID in binary matrix.
                        att = i + 1;
                        //System.out.println("exposureID="+exposure+" "+"node.nodeID="+node.nodeID);
                        //System.out.println("dataSpacestat["+i+"].min="+dataSpacestat[i].min);
                        //System.out.println("dataSpacestat["+i+"].max="+dataSpacestat[i].max);
                    }

                }


                //remove the attribute from map

                myLocalMap.remove(att);
            }

        }
        System.out.println("hushutest4");

		/*
          int noncfdind=0;

           for(int x=0; x<rulesinsingleList.length; x++){
               if(rulesinsingleList[x]==itemclass) continue;
               int flag=0;
               //remove listcfdrelated
               for (int y=0; y<listcfdrelated.length; y++){
                   if(rulesinsingleList[x]==listcfdrelated[y]){
                       flag=1;
                       break;
                   }

               }
               //remove ignorelist
               for (int y=0; y<ignoreList.length; y++){
                   if(rulesinsingleList[x]==ignoreList[y]){
                       flag=1;
                       break;
                   }

               }

               if(flag==0)    nonconfounderList[noncfdind++]=rulesinsingleList[x];


           }
           */
        // modified by mss
        // For one attribute, if none of its items are confounded to the exposure,
        // then keep all the items within this attribute (including irrelevant items with the target).


		   /*
           if(controlAttribute == 1) {
	           int nonconfounderListInd = 0;
	           for(i=0; i<nonconfounderList.length; i++)
	        	   if(nonconfounderList[i]==0) {
	        		   nonconfounderListInd = i;
	        		   break;
	        	   }
	           int[] noncfdClone = nonconfounderList.clone();
	           for(i=0; i<nonconfounderListInd-1; i++)
	        	   for(j=i+1; j<nonconfounderListInd; j++)
	        		   if(itemRecord[nonconfounderList[i]].attName.equals(itemRecord[nonconfounderList[j]].attName))
	        			   noncfdClone[j] = 0;
	           nonconfounderList = noncfdClone.clone();
	           noncfdClone=trimzeros(noncfdClone);
	           Arrays.sort(noncfdClone);

	           int[] friendsOfnoncfd = new int[1000];
	           int friendsOfnoncfdInd = 0;
	           for (i=0; i<noncfdClone.length; i++) {
	        	   if(noncfdClone[i] == 0)
	        		   continue;
	        	   for(j=0; j<realAtt; j++)
	        		   if(Arrays.binarySearch(attValue[j], noncfdClone[i]) >= 0) {
	        			   for(int l=0; l<attValue[j].length; l++) {
	        				   if(Arrays.binarySearch(noncfdClone, attValue[j][l]) < 0)
	        					   friendsOfnoncfd[friendsOfnoncfdInd++] = attValue[j][l];
	        			   }
	        			   break;
	        		   }
	           }
	           friendsOfnoncfd=trimzeros(friendsOfnoncfd);
	           for (i=0; i<friendsOfnoncfd.length; i++) {
	        	   nonconfounderList[nonconfounderListInd++]=friendsOfnoncfd[i];
	           }
           }

           ////
           //Remove sorting to get the controlist with decreasing odds ratio w.r.t target
           //Arrays.sort(nonconfounderList);

		   */
        //Rank all attributes left based on odds ratio
        // Arrays.sort(ruleOddsRatios);

        //get remaining attributes after removing ignoreList
        Set<Integer> keyset = myLocalMap.keySet();
        int[] arr = new int[keyset.size()];
        int index = 0;
        for (Integer attr : keyset) {
            arr[index++] = attr; //note the autounboxing here
        }
        //printRecord(arr);


        //get corresponding oddsratios
        double[] oddsarray = new double[arr.length];
        for (int v = 0; v < arr.length; v++) {
            oddsarray[v] = myLocalMap.get(arr[v]);
        }


        //create a new map with odds are key attributes are values
        //odds need to be unique. This is to sort the attributes based on odds ratios
        Map<Double, Integer> myLocalMapReverse = new HashMap<Double, Integer>();
        for (int m = 0; m < arr.length; m++) {
            myLocalMapReverse.put(Double.valueOf(oddsarray[m]), Integer.valueOf(arr[m]));
        }

        //System.out.println("Keys of tree map: " + myLocalMapReverse.keySet());
        //System.out.println("Values of tree map: " + myLocalMapReverse.values());

        //sort the odds values
        Arrays.sort(oddsarray);

        //System.out.println("arr.length"+arr.length);
        for (int indx = 0; indx < arr.length; indx++) {
            int temp = myLocalMapReverse.get(oddsarray[indx]).intValue();
            //System.out.println("oddsarray[indx]: " +oddsarray[indx]);
            //System.out.println("Keys of tree map: " +temp);
            //System.out.println("Control Attribute: " +temp);
            //convert the attribute to 2 binary items. This only works for binary data sets.
            //nonconfounderList[2*indx]=temp*2-1;
            //nonconfounderList[2*indx+1]=temp*2;
            //System.out.println("dataSpacestat[temp-1].min="+dataSpacestat[temp-1].min);
            //System.out.println("dataSpacestat[temp-1].max="+dataSpacestat[temp-1].max);
            for (i = 0, j = dataSpacestat[temp - 1].min; i < (dataSpacestat[temp - 1].max - dataSpacestat[temp - 1].min + 1); i++) {
                nonconfounderList[j] = (dataSpacestat[temp - 1].min + i);
                j++;
            }

        }

        nonconfounderList = trimzeros(nonconfounderList);
        System.out.println("hushutest5");

        //System.out.println("Control List");
        //printRecord(nonconfounderList);

        return nonconfounderList;

    }


    //hs.end

    public int[] friends(int item, int[] itemlist) {
        int[] friendslist;
        int frind = 0;
        friendslist = new int[itemlist.length];
        for (int x = 0; x < itemlist.length; x++) {
            //The same attribute name, different, and this item is in itemlist
            if (itemRecord[itemlist[x]].attName.equals(itemRecord[item].attName) && itemlist[x] != item)
                friendslist[frind++] = itemlist[x];
        }

        //trim 0s
        friendslist = trimzeros(friendslist);

            /*
            for(int y=0; y<friendslist.length; y++){
             System.out.println("friends of "+item+" is "+friendslist[y]);
                    }
            */
        return friendslist;
    }

    //Sort the array (dataspace)

    public static void sortArray(int myArray[][]) {
        Arrays.sort(myArray, new Comparator<int[]>() {

            @Override
            public int compare(int[] o1, int[] o2) {
                int r = 0;
                while (r < o1.length - 1) {
                    if (Integer.valueOf(o1[r]).compareTo(Integer.valueOf(o2[r])) == 0)
                        r = r + 1;
                    else return Integer.valueOf(o1[r]).compareTo(Integer.valueOf(o2[r]));
                }

                return Integer.valueOf(o1[r]).compareTo(Integer.valueOf(o2[r]));
            }

        });
    }

    public double log_2(int x) {
        return ((x) <= 0 ? 0.0 : Math.log(x) / Math.log(2));
    }

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

    // SHOULD NOT NEED THESE... SHOULD BE ABLE TO GET THESE FROM RULE
    /*
     * extern RULESET * RuleSet; extern long GMinSup; extern float MinConf;
	 * extern ITEMSET * CurTranSet; extern int Ass; extern int Deriv; extern
	 * TREENODE *AllSet; extern int Perfectrule;
	 */

    /* this is for initial the Node Count and Weight count */
    public void initialCount() {
        int i;
        counter = new int[maxClass + 2][];
        for (i = 0; i < maxClass + 2; i++) {
            counter[i] = new int[maxItem + 2];
        }
        // add by mss, initial control variables
        controlSingleVar = new int[maxItem + 1][maxItem + 1];
    }

    /* this is free the memory the Count hold */
    public void freeCount() {
        counter = new int[0][0];
    }

    // beajy003 - return type changed to void as there are no return statements
    // in the function
    public void determineParameter(double suprate) {
        //System.out.println("!! beajy003 - Rule - determineParameter() - sup:"+suprate+": realAtt:"+realAtt+": ");
        int i, j, targetvalue, item;

        maxTarget = 1;

        initParameter();

        // Firstly, reading the gobal frequency and local frequency
        // counter[0][item] stores the frequency of the item;
        // counter[target][item] stores the frequent of the item co-occuring
        // with the target
        // counter[target][0] stores the the frequency of the target

        for (i = 0; i < maxData; i++) {
            //reading the class and count
            targetvalue = dataSpace[i][realAtt];
            counter[targetvalue][0]++;

            //count the local and global freq of items
            for (j = 0; j < realAtt; j++) {
                //	 printf(" %d, ", dataSpace[i][j]);
                item = dataSpace[i][j];
                if (item == 0)
                    continue;
                counter[targetvalue][item]++;
                counter[maxClass][item]++;
            }
        }

        // Secondly, decide the parameter of conf and support

        for (i = 0; i < maxClass; i++) {

            lMinSup[i] = (int) (counter[i][0] * suprate + 0.5);
            dist[i] = counter[i][0];
            //                     System.out.println("\n!! - determinParameter() - class "+i+": dis["+i+"]=counter["+i+"][0]= "+counter[i][0]);
            //                     System.out.println("\n!! - determineParemeter() - lMinSup[class "+i+"]="+lMinSup[i]);
        }
        //   System.out.println("hypothyroid=Counter[0][0]="+counter[0][0]+"; negative=counter[1][0]="+counter[1][0]);
//    System.out.println("lMinSup, hypothyroid ")
        // verify correctness of suport and confidence

        //System.out.println("\n!! beajy003 - Rule - determinParameter() - maxData:"+maxData+": maxClass:"+maxClass+": gMinSup:"+gMinSup+": min0:"+lMinSup[0]+": min1:"+lMinSup[1]+":");

        gMinSup = maxData;
        for (i = 0; i < maxClass; i++) {
            if (lMinSup[i] < gMinSup)
                gMinSup = lMinSup[i];

        }
        printf("GMinSup = %d \n", gMinSup);

        for (i = 0; i < maxClass; i++) {
            printf(" Distf[%d] = %f \n", i, (double) dist[i] / maxData);
        }

    }

    public int initParameter() {
        lMinSup = new int[maxClass + 2];
        dist = new double[maxClass + 2];
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

        for (i = 0; i < maxItem + 1; i++) {
            tree.sonList[i] = null;
            tree.sonList1[i] = null;

        }

        // first layer tree

        tree.numOfSon = 0;
        tree.numOfSon1 = 0;

        //System.out.println("!! beajy003 - Rule - initWholeTree() - max:"+maxItem+": gMinSup:"+gMinSup+":");
        for (i = 1; i < maxItem + 1; i++) {

            tree.sonList1[tree.numOfSon1++] = newNode(tree, i);
            if (counter[maxClass][i] > gMinSup) {
                //         System.out.println("include item "+i+" as counter[maxClass]["+i+"]="+counter[maxClass][i]);
                tree.sonList[tree.numOfSon++] = newNode(tree, i);
                tree.sonList1[tree.numOfSon1 - 1].issupport = 1;
                System.out.println("frequent2:" + i);
                //         System.out.println("number of Son:"+tree.numOfSon);
            } else {
                System.out.println(tree.sonList1[tree.numOfSon1 - 1].nodeID);
                tree.sonList1[tree.numOfSon1 - 1].issupport = 0;
            }
        }

        //hs,this is for user test 1 attribute
        if (ChosenTest.length == 1) {
            System.out.println("test1");
            System.out.println("ChosenTest[0]=" + ChosenTest[0]);
            System.out.println("min=" + dataSpacestat[ChosenTest[0] - 1].min);
            System.out.println("max=" + dataSpacestat[ChosenTest[0] - 1].max);
            for (i = dataSpacestat[ChosenTest[0] - 1].min; i <= dataSpacestat[ChosenTest[0] - 1].max; i++) {
                if (tree.sonList1[i - 1].issupport == 1) {
                    cur = tree.sonList1[i - 1];
                    System.out.println("number of Son" + tree.numOfSon1);
                    System.out.println("curnode: " + cur.nodeID);
                    if (excl != 0) {
                        //                        System.out.println("excl= "+excl);
                        //System.out.println("curnode: "+cur.nodeID);
                        if (chooseMethod == 1) {
                            ruleTestWrite_CRPA(cur);
                            //System.out.println("chooseone");
                        } else {
                            ruleTestWrite(cur);
                            //System.out.println("choosetwo");
                        }

                        // if rule is formed, then write to SingleList
                        // Single list keeps rules with single attribute
                        // for finding sub rules (when Sub flag is on)
                        // there rules have been written in RuleSet already.
                        if (cur.token > 0) {
                            //System.out.println("hushu");
                            System.out.println("candidate for causal test node.nodeID=" + cur.nodeID);
                            //System.out.println("cur.token="+cur.token);
                            writeToRuleSet(cur, 1);
                        }
                    }
                    // This procedure is for confidence and accuracy
                    else {
                        if (formingRule(cur) != 0)
                            writeToRuleSet(cur, 0);
                    }
                }
            }


            for (i = 0; i < tree.numOfSon; i++) {
                cur1 = tree.sonList[i];
                //System.out.println("number of Son"+tree.numOfSon);
                //System.out.println("curnode: "+cur.nodeID);
                // FormingRule(cur);
                // This procedure is for exclusiveness
                if (excl != 0) {
                    //                        System.out.println("excl= "+excl);
                    //System.out.println("curnode: "+cur.nodeID);
                    if (chooseMethod == 1) {
                        ruleTestWrite_CRPA_ruleSet1(cur1);
                        //System.out.println("chooseone");
                    } else {
                        ruleTestWrite_ruleSet1(cur1);
                        //System.out.println("choosetwo");
                    }

                    // if rule is formed, then write to SingleList
                    // Single list keeps rules with single attribute
                    // for finding sub rules (when Sub flag is on)
                    // there rules have been written in RuleSet already.
                    if (cur1.token > 0) {
                        //System.out.println("hushu");
                        System.out.println("candidate for causal test node.nodeID=" + cur1.nodeID);
                        //System.out.println("cur.token="+cur.token);
                        writeToRuleSet_singleList1(cur1, 1);
                    }
                }
                // This procedure is for confidence and accuracy
                else {
                    if (formingRule(cur1) != 0)
                        writeToRuleSet_singleList1(cur1, 0);
                }
            }
            System.out.println("singleList.numOfRule=" + singleList.numOfRule);
            System.out.println("singleList1.numOfRule=" + singleList1.numOfRule);
            System.out.println("ruleSet.numOfRule=" + ruleSet.numOfRule);
            System.out.println("ruleSet1.numOfRule=" + ruleSet1.numOfRule);


        } else if (ChosenTest.length >= 2) {
            System.out.println("test2");
            System.out.println("ChosenTest[0]=" + ChosenTest[0]);
            System.out.println("min=" + dataSpacestat[ChosenTest[0] - 1].min);
            System.out.println("max=" + dataSpacestat[ChosenTest[0] - 1].max);
            for (m = 0; m < ChosenTest.length; m++) {
                for (i = dataSpacestat[ChosenTest[m] - 1].min; i <= dataSpacestat[ChosenTest[m] - 1].max; i++) {
                    if (tree.sonList1[i - 1].issupport == 1) {
                        cur1 = tree.sonList1[i - 1];
                        System.out.println("number of Son" + tree.numOfSon1);
                        System.out.println("curnode: " + cur1.nodeID);
                        if (excl != 0) {
                            //                        System.out.println("excl= "+excl);
                            //System.out.println("curnode: "+cur.nodeID);
                            if (chooseMethod == 1) {
                                ruleTestWrite_CRPA(cur1);
                                //System.out.println("chooseone");
                            } else {
                                ruleTestWrite(cur1);
                                //System.out.println("choosetwo");
                            }

                            // if rule is formed, then write to SingleList
                            // Single list keeps rules with single attribute
                            // for finding sub rules (when Sub flag is on)
                            // there rules have been written in RuleSet already.
                            if (cur1.token > 0) {
                                //System.out.println("hushu");
                                System.out.println("candidate for causal test node.nodeID=" + cur1.nodeID);
                                //System.out.println("cur.token="+cur.token);
                                writeToRuleSet(cur1, 1);
                            }
                        }
                        // This procedure is for confidence and accuracy
                        else {
                            if (formingRule(cur1) != 0)
                                writeToRuleSet(cur1, 0);
                        }
                    }
                }
            }


            for (i = 0; i < tree.numOfSon; i++) {
                cur1 = tree.sonList[i];
                //System.out.println("number of Son"+tree.numOfSon);
                //System.out.println("curnode: "+cur.nodeID);
                // FormingRule(cur);
                // This procedure is for exclusiveness
                if (excl != 0) {
                    //                        System.out.println("excl= "+excl);
                    //System.out.println("curnode: "+cur.nodeID);
                    if (chooseMethod == 1) {
                        ruleTestWrite_CRPA_ruleSet1(cur1);
                        //System.out.println("chooseone");
                    } else {
                        ruleTestWrite_ruleSet1(cur1);
                        //System.out.println("choosetwo");
                    }

                    // if rule is formed, then write to SingleList
                    // Single list keeps rules with single attribute
                    // for finding sub rules (when Sub flag is on)
                    // there rules have been written in RuleSet already.
                    if (cur1.token > 0) {
                        //System.out.println("hushu");
                        System.out.println("candidate for causal test node.nodeID=" + cur1.nodeID);
                        //System.out.println("cur.token="+cur.token);
                        writeToRuleSet_singleList1(cur1, 1);
                    }
                }
                // This procedure is for confidence and accuracy
                else {
                    if (formingRule(cur1) != 0)
                        writeToRuleSet_singleList1(cur1, 0);
                }
            }
            System.out.println("singleList.numOfRule=" + singleList.numOfRule);
            System.out.println("singleList1.numOfRule=" + singleList1.numOfRule);
            System.out.println("ruleSet.numOfRule=" + ruleSet.numOfRule);
            System.out.println("ruleSet1.numOfRule=" + ruleSet1.numOfRule);

                    /*
                    System.out.println("ChosenTest[1]="+ChosenTest[1]);
                    System.out.println("min="+dataSpacestat[ChosenTest[1]-1].min);
                    System.out.println("max="+dataSpacestat[ChosenTest[1]-1].max);
                    for(i=dataSpacestat[ChosenTest[1]-1].min;i<=dataSpacestat[ChosenTest[1]-1].max;i++){
                        cur2 = tree.sonList1[i-1];
                        System.out.println("number of Son"+tree.numOfSon1);
                        System.out.println("curnode: "+cur2.nodeID);
                    if (excl != 0) {
    //                        System.out.println("excl= "+excl);
                            //System.out.println("curnode: "+cur.nodeID);
                            if(chooseMethod == 1){
                                ruleTestWrite_CRPA(cur2);
                                //System.out.println("chooseone");
                            }else{
                                ruleTestWrite(cur2);
                                //System.out.println("choosetwo");
                            }

				// if rule is formed, then write to SingleList
				// Single list keeps rules with single attribute
				// for finding sub rules (when Sub flag is on)
				// there rules have been written in RuleSet already.
				if (cur2.token > 0) {
                                    //System.out.println("hushu");
                                    System.out.println("candidate for causal test node.nodeID="+cur2.nodeID);
                                    //System.out.println("cur.token="+cur.token);
					writeToRuleSet(cur2, 1);
				}
			}
			// This procedure is for confidence and accuracy
			else {
				if (formingRule(cur2) != 0)
					writeToRuleSet(cur2, 0);
			}
                    }
                    */


        } else if (ChosenTest.length == 0) {
            for (i = 0; i < tree.numOfSon; i++) {
                cur = tree.sonList[i];
                //System.out.println("number of Son"+tree.numOfSon);
                //System.out.println("curnode: "+cur.nodeID);
                // FormingRule(cur);
                // This procedure is for exclusiveness
                if (excl != 0) {
                    //                        System.out.println("excl= "+excl);
                    //System.out.println("curnode: "+cur.nodeID);
                    if (chooseMethod == 1) {
                        ruleTestWrite_CRPA(cur);
                        //System.out.println("chooseone");
                    } else {
                        ruleTestWrite(cur);
                        //System.out.println("choosetwo");
                    }

                    // if rule is formed, then write to SingleList
                    // Single list keeps rules with single attribute
                    // for finding sub rules (when Sub flag is on)
                    // there rules have been written in RuleSet already.
                    if (cur.token > 0) {
                        //System.out.println("hushu");
                        System.out.println("candidate for causal test node.nodeID=" + cur.nodeID);
                        //System.out.println("cur.token="+cur.token);
                        writeToRuleSet(cur, 1);
                    }
                }
                // This procedure is for confidence and accuracy
                else {
                    if (formingRule(cur) != 0)
                        writeToRuleSet(cur, 0);
                }
            }
            System.out.println("singleList.numOfRule=" + singleList.numOfRule);
            System.out.println("ruleSet.numOfRule=" + ruleSet.numOfRule);
        }
        //            System.out.println("hi");
        //          System.out.println("rules in ruleSet: "+ruleSet.numOfRule);
        //        System.out.println("rules in singleList: "+singleList.numOfRule);

        freeCount();

    }

    //hs, add, getting the maximum value
    public static int getMaxValue(int[] array) {
        int maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];

            }
        }

        return maxValue;
    }

    // hs, add, getting the miniumum value
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

    // hs, add, init Column Object table
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

    public int candidateGen(PrefixTree tree, int layer) {
        int i, j, k, l, p, q, flag, numofsonbak, att, sum = 10;
        int[] settmp = new int[100];
        int[] jointset = new int[100];
        double rate;
        PrefixTree cur, tmp1, tmp2;
        PrefixTree[] subnodeptr = new PrefixTree[100];

        flag = 0;
        for (i = 0; i < tree.numOfSon; i++) {
            cur = tree.sonList[i];
            System.out.println("current:" + cur.set[0]);


        }
        if (tree == null)
            return flag;
        if (ruleSet.numOfRule > maxRuleAllowed)
            return flag;
        //     System.out.println("current tree.len="+tree.len);
        //allSet.len=tree.len=0; layer=2 to pass on
        if (tree.len == (layer - 2)) {
            //        System.out.println("sum="+sum);
            numofsonbak = tree.numOfSon;
            //            System.out.println("numofsonbak="+numofsonbak);
            for (i = 0; i < numofsonbak - 1; i++) {
                tmp1 = tree.sonList[i];
                System.out.println("hahatmp1.nodeID=" + tmp1.nodeID);
                //                System.out.println("tmp1.len="+ tmp1.len);
                if (tmp1 == null) {
                    //                     System.out.println("temp1 is null");
                    continue;
                }

                if (tmp1.token >= 2) {
                    //                    System.out.println("token:"+tmp1.token);
                    continue;
                }

                tmp1.numOfSon = 0;
//System.out.println("temp len="+tmp1.len);
                for (k = 0; k < tmp1.len; k++) {

                    settmp[k] = tmp1.set[k];
                    System.out.println("settmp[" + k + "]=" + tmp1.set[k]);
                }


                for (j = i + 1; j < numofsonbak; j++) {
                    tmp2 = tree.sonList[j];
                    System.out.println("hahatmp2.nodeID=" + tmp2.nodeID);
                    if (tmp2 == null) {
                        //                                 System.out.println("the item is null");
                        continue;
                    }

                    if (tmp2.token >= 2) {
                        //                                  System.out.println("tmp2.token="+tmp2.token);
                        continue;
                    }


                    settmp[tmp1.len] = tmp2.nodeID;
                    System.out.println("settmp[" + tmp1.len + "]=" + settmp[tmp1.len]);
                    //       System.out.println("next layer: "+tmp2.nodeID);

                    //ok so far
                    for (k = 0; k < maxClass; k++) {
                        if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
                            jointset[k] = 1;
                            //                                             System.out.println("Yes. jointset["+k+"]="+jointset[k]);
                        } else {
                            jointset[k] = 0;
                            //                                             System.out.println("No. jointset["+k+"]="+jointset[k]);
                        }
                    }

                    for (k = 0; k < maxClass; k++) {
                        sum += jointset[k];
                    }
                    //                                 System.out.println("sum="+sum);
                    if (sum == 0) {

                        //                                       System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
                        continue;
                    }


                    if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
                        //                                    System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
                        continue;
                    }


                    tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                    System.out.println("father: " + tmp1.nodeID + " - Children: " + tmp1.sonList[tmp1.numOfSon - 1].nodeID);
                    System.out.append("\n");
                    flag = 1;
                    //transfer Thuc: backup, create the new one, put the backup back
                    PrefixTree[] transfer;
                    transfer = tmp1.sonList;

                    if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
//                                            System.out.println("the number of son is big, current:"+tmp1.memForSon);
                        tmp1.memForSon += CYCLE;
                        //                                          System.out.println("after adding: "+tmp1.memForSon);
                        tmp1.sonList = new PrefixTree[tmp1.memForSon];
                        //putback
                        System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                    }
                    //   System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                }

            }
            for (i = 0; i < tree.numOfSon; i++) {
                cur = tree.sonList[i];
                System.out.println("current:" + cur.set[0]);


            }
            //             System.out.println("tree.len="+tree.len);
            return (flag);

        }
//System.out.println("skip tree.len=layer-2");
        if (tree.len == layer - 1)
            return flag;
        if (tree.len == layer)
            return flag;
//layer>2
        for (i = 0; i < tree.numOfSon; i++) {
            cur = tree.sonList[i];
            System.out.println("current:" + cur.nodeID);
            flag += candidateGen(cur, layer);


        }
        return (flag);
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
        for (i = 0; i < tree.numOfSon1; i++) {
            cur = tree.sonList1[i];
            System.out.println("current:" + cur.set[0]);


        }


        if (tree == null)
            return flag;
        if (ruleSet.numOfRule > maxRuleAllowed)
            return flag;
        //     System.out.println("current tree.len="+tree.len);
        //allSet.len=tree.len=0; layer=2 to pass on
        if (tree.len == (layer - 2)) {

            //        System.out.println("sum="+sum);
            if (layer <= 2) {
                numofsonbak = tree.numOfSon1;


                //            System.out.println("numofsonbak="+numofsonbak);
                for (int m = 0; m < ChosenTest.length; m++) {
                    System.out.println("dataSpacestat[ChosenTest[ChosenTest.length]-1].max-1=" + (dataSpacestat[ChosenTest[ChosenTest.length - 1] - 1].max - 1));
                    for (i = dataSpacestat[ChosenTest[m] - 1].min; (i <= dataSpacestat[ChosenTest[m] - 1].max); i++) {
                        if (i <= (dataSpacestat[ChosenTest[ChosenTest.length - 1] - 1].max - 1)) {
                            System.out.println("i=" + i);
                            tmp1 = tree.sonList1[i - 1];
                            //                System.out.println("tmp1.len="+ tmp1.len);
                            System.out.println("hahatmp1.nodeID=" + tmp1.nodeID);
                            if (tmp1 == null) {
                                //                     System.out.println("temp1 is null");
                                continue;
                            }
                            if (tree.sonList1[i - 1].issupport == 0) {
                                continue;
                            }

                            if (tmp1.token >= 2) {
                                //                    System.out.println("token:"+tmp1.token);
                                continue;
                            }


                            //tmp1.numOfSon = 0;
                            //hs1
                            tmp1.numOfSon = 0;
                            //hs1
//System.out.println("temp len="+tmp1.len);
                            for (k = 0; k < tmp1.len; k++) {

                                settmp[k] = tmp1.set[k];
                                System.out.println("settmp[" + k + "]=" + tmp1.set[k]);
                            }

                            //for(int n=m+1;n<ChosenTest.length;n++){
                            //System.out.println("m="+m);
                            //System.out.println("n="+n);
                            if (tmp1 != tree.sonList1[dataSpacestat[ChosenTest[m] - 1].max - 1]) {
                                for (j = i + 1; j <= dataSpacestat[ChosenTest[m] - 1].max; j++) {
                                    System.out.println("j1=" + j);
                                    if (tree.sonList1[j - 1].issupport == 1) {
                                        tmp2 = tree.sonList1[j - 1];

                                        if (tmp2 == null) {
                                            //                                 System.out.println("the item is null");
                                            continue;
                                        }

                                        if (tmp2.token >= 2) {
                                            //                                  System.out.println("tmp2.token="+tmp2.token);
                                            continue;
                                        }
                                        System.out.println("hahatmp2.nodeID=" + tmp2.nodeID);

                                        settmp[tmp1.len] = tmp2.nodeID;
                                        //       System.out.println("next layer: "+tmp2.nodeID);

                                        //ok so far
                                        for (k = 0; k < maxClass; k++) {
                                            if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
                                                jointset[k] = 1;
                                                //                                             System.out.println("Yes. jointset["+k+"]="+jointset[k]);
                                            } else {
                                                jointset[k] = 0;
                                                //                                             System.out.println("No. jointset["+k+"]="+jointset[k]);
                                            }
                                        }

                                        for (k = 0; k < maxClass; k++) {
                                            sum += jointset[k];
                                        }
                                        //                                 System.out.println("sum="+sum);
                                        if (sum == 0) {

                                            //                                       System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
                                            continue;
                                        }


                                        if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
                                            //                                    System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
                                            continue;
                                        }

                                        //hs2
                                        tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                                        System.out.println("father: " + tmp1.nodeID + " - Children: " + tmp1.sonList[tmp1.numOfSon - 1].nodeID);
                                        System.out.append("\n");
                                        tree.sonList[i - 1] = tmp1;//hs.add
                                        flag = 1;
                                        //transfer Thuc: backup, create the new one, put the backup back
                                        PrefixTree[] transfer;
                                        transfer = tmp1.sonList;

                                        if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
                                            //                                            System.out.println("the number of son is big, current:"+tmp1.memForSon);
                                            tmp1.memForSon += CYCLE;
                                            //                                          System.out.println("after adding: "+tmp1.memForSon);
                                            tmp1.sonList = new PrefixTree[tmp1.memForSon];
                                            //putback
                                            System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                                        }
                                        //hs2
                                        //   System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                                    }

                                }
                                for (int n = m + 1; n < ChosenTest.length; n++) {
                                    for (j = dataSpacestat[ChosenTest[n] - 1].min; j <= dataSpacestat[ChosenTest[n] - 1].max; j++) {
                                        System.out.println("j2=" + j);
                                        if (tree.sonList1[j - 1].issupport == 1) {
                                            tmp2 = tree.sonList1[j - 1];

                                            if (tmp2 == null) {
                                                //                                 System.out.println("the item is null");
                                                continue;
                                            }

                                            if (tmp2.token >= 2) {
                                                //                                  System.out.println("tmp2.token="+tmp2.token);
                                                continue;
                                            }
                                            System.out.println("hahatmp2.nodeID=" + tmp2.nodeID);


                                            settmp[tmp1.len] = tmp2.nodeID;
                                            //       System.out.println("next layer: "+tmp2.nodeID);

                                            //ok so far
                                            for (k = 0; k < maxClass; k++) {
                                                if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
                                                    jointset[k] = 1;
                                                    //                                             System.out.println("Yes. jointset["+k+"]="+jointset[k]);
                                                } else {
                                                    jointset[k] = 0;
                                                    //                                             System.out.println("No. jointset["+k+"]="+jointset[k]);
                                                }
                                            }

                                            for (k = 0; k < maxClass; k++) {
                                                sum += jointset[k];
                                            }
                                            //                                 System.out.println("sum="+sum);
                                            if (sum == 0) {

                                                //                                       System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
                                                continue;
                                            }


                                            if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
                                                //                                    System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
                                                continue;
                                            }

                                            //hs3
                                            tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                                            System.out.println("father: " + tmp1.nodeID + " - Children: " + tmp1.sonList[tmp1.numOfSon - 1].nodeID);
                                            System.out.append("\n");
                                            tree.sonList[i - 1] = tmp1;//hs.add
                                            flag = 1;
                                            //transfer Thuc: backup, create the new one, put the backup back
                                            PrefixTree[] transfer;
                                            transfer = tmp1.sonList;

                                            if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
//                                            System.out.println("the number of son is big, current:"+tmp1.memForSon);
                                                tmp1.memForSon += CYCLE;
                                                //                                          System.out.println("after adding: "+tmp1.memForSon);
                                                tmp1.sonList = new PrefixTree[tmp1.memForSon];
                                                //putback
                                                System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                                            }
                                            //hs3
                                            //   System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                                        }
                                    }
                                }

                            } else {
                                for (int n = m + 1; n < ChosenTest.length; n++) {
                                    for (j = dataSpacestat[ChosenTest[n] - 1].min; j <= dataSpacestat[ChosenTest[n] - 1].max; j++) {
                                        System.out.println("j2=" + j);
                                        if (tree.sonList1[j - 1].issupport == 1) {
                                            tmp2 = tree.sonList1[j - 1];

                                            if (tmp2 == null) {
                                                //                                 System.out.println("the item is null");
                                                continue;
                                            }

                                            if (tmp2.token >= 2) {
                                                //                                  System.out.println("tmp2.token="+tmp2.token);
                                                continue;
                                            }
                                            System.out.println("hahatmp2.nodeID=" + tmp2.nodeID);

                                            settmp[tmp1.len] = tmp2.nodeID;
                                            //       System.out.println("next layer: "+tmp2.nodeID);

                                            //ok so far
                                            for (k = 0; k < maxClass; k++) {
                                                if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
                                                    jointset[k] = 1;
                                                    //                                             System.out.println("Yes. jointset["+k+"]="+jointset[k]);
                                                } else {
                                                    jointset[k] = 0;
                                                    //                                             System.out.println("No. jointset["+k+"]="+jointset[k]);
                                                }
                                            }

                                            for (k = 0; k < maxClass; k++) {
                                                sum += jointset[k];
                                            }
                                            //                                 System.out.println("sum="+sum);
                                            if (sum == 0) {

                                                //                                       System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
                                                continue;
                                            }


                                            if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
                                                //                                    System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
                                                continue;
                                            }

                                            //hs4
                                            tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                                            System.out.println("father: " + tmp1.nodeID + " - Children: " + tmp1.sonList[tmp1.numOfSon - 1].nodeID);
                                            System.out.append("\n");
                                            tree.sonList[i - 1] = tmp1;//hs.add
                                            flag = 1;
                                            //transfer Thuc: backup, create the new one, put the backup back
                                            PrefixTree[] transfer;
                                            transfer = tmp1.sonList;

                                            if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
//                                            System.out.println("the number of son is big, current:"+tmp1.memForSon);
                                                tmp1.memForSon += CYCLE;
                                                //                                          System.out.println("after adding: "+tmp1.memForSon);
                                                tmp1.sonList = new PrefixTree[tmp1.memForSon];
                                                //putback
                                                System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                                            }
                                            //hs4
                                            //   System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            } else {
                numofsonbak = tree.numOfSon;
                //            System.out.println("numofsonbak="+numofsonbak);
                for (i = 0; i < numofsonbak - 1; i++) {
                    tmp1 = tree.sonList[i];
                    //                System.out.println("tmp1.len="+ tmp1.len);
                    if (tmp1 == null) {
                        //                     System.out.println("temp1 is null");
                        continue;
                    }

                    if (tmp1.token >= 2) {
                        //                    System.out.println("token:"+tmp1.token);
                        continue;
                    }

                    tmp1.numOfSon = 0;
//System.out.println("temp len="+tmp1.len);
                    for (k = 0; k < tmp1.len; k++) {

                        settmp[k] = tmp1.set[k];
                        //                          System.out.println("settmp["+k+"]="+tmp1.set[k]);
                    }


                    for (j = i + 1; j < numofsonbak; j++) {
                        tmp2 = tree.sonList[j];

                        if (tmp2 == null) {
                            //                                 System.out.println("the item is null");
                            continue;
                        }

                        if (tmp2.token >= 2) {
                            //                                  System.out.println("tmp2.token="+tmp2.token);
                            continue;
                        }


                        settmp[tmp1.len] = tmp2.nodeID;
                        //       System.out.println("next layer: "+tmp2.nodeID);

                        //ok so far
                        for (k = 0; k < maxClass; k++) {
                            if (tmp1.lSup[k] > lMinSup[k] && tmp2.lSup[k] > lMinSup[k]) {
                                jointset[k] = 1;
                                //                                             System.out.println("Yes. jointset["+k+"]="+jointset[k]);
                            } else {
                                jointset[k] = 0;
                                //                                             System.out.println("No. jointset["+k+"]="+jointset[k]);
                            }
                        }

                        for (k = 0; k < maxClass; k++) {
                            sum += jointset[k];
                        }
                        //                                 System.out.println("sum="+sum);
                        if (sum == 0) {

                            //                                       System.out.println("not frequent as sum=0: "+settmp[0]+"-"+settmp[1]);
                            continue;
                        }


                        if (frequentSubSet(settmp, tmp1.len + 1, jointset, subnodeptr) == 0) {
                            //                                    System.out.println("not frequent: "+settmp[0]+"-"+settmp[1]);
                            continue;
                        }


                        tmp1.sonList[tmp1.numOfSon++] = addSon(tmp1, tmp2, subnodeptr);
                        //           System.out.println("father: "+tmp1.nodeID +" - Children: "+tmp1.sonList[tmp1.numOfSon-1].nodeID);
                        //         System.out.append("\n");
                        flag = 1;
                        //transfer Thuc: backup, create the new one, put the backup back
                        PrefixTree[] transfer;
                        transfer = tmp1.sonList;

                        if (tmp1.numOfSon >= (tmp1.memForSon - 1)) {
//                                            System.out.println("the number of son is big, current:"+tmp1.memForSon);
                            tmp1.memForSon += CYCLE;
                            //                                          System.out.println("after adding: "+tmp1.memForSon);
                            tmp1.sonList = new PrefixTree[tmp1.memForSon];
                            //putback
                            System.arraycopy(transfer, 0, tmp1.sonList, 0, transfer.length);

                        }
                        //   System.out.println("tmp1.sonList["+i+"]="+tmp1.sonList[i].nodeID);
                    }

                }

            }
            for (i = 0; i < tree.numOfSon1; i++) {
                cur = tree.sonList1[i];
                System.out.println("current:" + cur.set[0]);


            }
            //             System.out.println("tree.len="+tree.len);
            return (flag);

        }
//System.out.println("skip tree.len=layer-2");
        if (tree.len == layer - 1)
            return flag;
        if (tree.len == layer)
            return flag;
//layer>2
        for (i = 0; i < tree.numOfSon; i++) {
            cur = tree.sonList[i];
            System.out.println("current:" + cur.nodeID);
            flag += candidateGen_Two(cur, layer);


        }
        return (flag);
    }


    //hs.end

    public int frequentSubSet(int[] itemset, int itemsetlen, int[] joint, PrefixTree[] subnodeptr) {
        int i, j, k, l, place, flag;
        int[] subset = new int[100];
        PrefixTree subnode;

        if (itemsetlen <= 2)
            return TRUE;

        // The following part is to generate a subset of itemset
        l = itemsetlen;
        place = 0;
        for (i = 0; i < l - 2; i++) {
            k = 0;
            for (j = 0; j < l; j++)
                if (j != place)
                    subset[k++] = itemset[j];

            // / cannot find it
            if ((subnode = searchSubSet(subset, k)) == null) {
//                            System.out.println("subnode=null, cannot find it, subset[k]="+subset[k-1]);
                return (FALSE);
            }
            //add to debug
            if (subnode.token >= 2) {
                return (FALSE);
            }
            subnodeptr[place] = subnode;

            // / test if they share the common frequent sets

            flag = 0;
            for (k = 0; k < maxClass; k++) {
                if (joint[k] != 0) {
                    if (subnode.lSup[k] > lMinSup[k])
                        flag = 1;
                    else
                        joint[k] = 0;
                }
            }

            if (flag == 0)
                return (FALSE);

            place++;
        }

        return (TRUE);
    }

    public int ruleSelectAndPruning(PrefixTree tree, int layer) {

        int i, j, k, l, flag, numofsonbak;
        int[] itemset = new int[100];
        int[] jointset = new int[100];
        PrefixTree cur, tmp1, tmp2;

        flag = 0;
        if (tree == null)
            return flag;
        System.out.println("hushutree.len=" + tree.len);
        System.out.println("hushutree.nodeID=" + tree.nodeID);
        if (tree.len == layer - 1) {
//                    System.out.println("Yes get into ruleSelect and pruning");
            numofsonbak = tree.numOfSon;
            //                   System.out.println("numofsonbak="+numofsonbak);
            for (j = 0; j < numofsonbak; j++) {

                tmp1 = tree.sonList[j];
                System.out.println("hushutmp1.nodeID=" + tmp1.nodeID);
                System.out.println("tmp1.len=" + tmp1.len);
//			System.out.println("Yay, tmp1 now is: "+tmp1.nodeID);
                if (tmp1 == null)
                    continue;
                System.out.println("tmp1.gSup=" + tmp1.gSup);

                // / test the overall support

                if (tmp1.gSup < gMinSup) {
                    tree.sonList[j] = deleteNode(tmp1);
                    //                                      System.out.println("Delete as gSup<gMinSup");
                    continue;
                }

                // / test the individual support

                if (individualFrequent(tmp1) == 0) {
                    tree.sonList[j] = deleteNode(tmp1);
                    //                                    System.out.println("Delete as not frequent");
                    continue;
                }

                // / Compare with its all (k-1) order general rules, decide if
                // this node is prunable
                // / Or this rule is discardable (maybe its general rule

                if (ass == 0) {
                    if (isPrunable(tmp1) != 0) {
                        tree.sonList[j] = deleteNode(tmp1);
                        //                                          System.out.println("delete as prunable");
                        continue;
                    }
                }

                // / Decide the rule target if possible
                // / if a rule is formed, then to seee if the improvement is
                // signiicant enough

                if (excl != 0) {
                    // This is the procedure of forming rules by exclusiveness
                    //ruleTestWrite(tmp1);
                    if (chooseMethod == 1) {
                        ruleTestWrite_CRPA(tmp1);
                        //System.out.println("chooseone");
                    } else {
                        ruleTestWrite(tmp1);
                        //System.out.println("choosetwo");
                    }
                    //                                System.out.println("After ruleTestWrite");
                } else {
                    // This is the procedure of forming rules by confidence and
                    // accuracy
                    if (formingRule(tmp1) != 0)
                        significantTest(tmp1);
                    writeToRuleSet(tmp1, 0);
                }
                System.out.println("secondtmp1.len=" + tmp1.len);

            }
            flag = reOrderSon(tree);
            System.out.println("flag=" + flag);
            displayTree(tree);
            return flag;
        }

        if (tree.len == layer)
            return flag;

        for (i = 0; i < tree.numOfSon; i++) {
            cur = tree.sonList[i];
            System.out.println("hushucur.nodeID" + cur.nodeID);
            flag += ruleSelectAndPruning(cur, layer);
        }
        return (flag);

    }

    //hs.start
    public int ruleSelectAndPruning_Two(PrefixTree tree, int layer) {

        int i, j, k, l, flag, numofsonbak;
        int[] itemset = new int[100];
        int[] jointset = new int[100];
        PrefixTree cur, tmp1, tmp2;

        flag = 0;
        if (tree == null)
            return flag;

        System.out.println("hushutree.len=" + tree.len);
        System.out.println("hushutree.nodeID=" + tree.nodeID);

        if (tree.len == layer - 1) {
//                    System.out.println("Yes get into ruleSelect and pruning");
            numofsonbak = tree.numOfSon;
            //                   System.out.println("numofsonbak="+numofsonbak);
            for (j = 0; j < numofsonbak; j++) {

                tmp1 = tree.sonList[j];
                System.out.println("hushutmp1.nodeID=" + tmp1.nodeID);
                System.out.println("tmp1.len=" + tmp1.len);
//			System.out.println("Yay, tmp1 now is: "+tmp1.nodeID);
                if (tmp1 == null)
                    continue;

                // / test the overall support
                System.out.println("tmp1.gSup=" + tmp1.gSup);

                if (tmp1.gSup < gMinSup) {
                    tree.sonList[j] = deleteNode(tmp1);
                    //                                      System.out.println("Delete as gSup<gMinSup");
                    continue;
                }

                // / test the individual support

                if (individualFrequent(tmp1) == 0) {
                    tree.sonList[j] = deleteNode(tmp1);
                    //                                    System.out.println("Delete as not frequent");
                    continue;
                }

                // / Compare with its all (k-1) order general rules, decide if
                // this node is prunable
                // / Or this rule is discardable (maybe its general rule

                if (ass == 0) {
                    if (isPrunable(tmp1) != 0) {
                        tree.sonList[j] = deleteNode(tmp1);
                        //                                          System.out.println("delete as prunable");
                        continue;
                    }
                }

                // / Decide the rule target if possible
                // / if a rule is formed, then to seee if the improvement is
                // signiicant enough

                if (excl != 0) {
                    // This is the procedure of forming rules by exclusiveness
                    //ruleTestWrite(tmp1);
                    if (chooseMethod == 1) {
                        ruleTestWrite_CRPA(tmp1);
                        //System.out.println("chooseone");
                    } else {
                        ruleTestWrite(tmp1);
                        //System.out.println("choosetwo");
                    }
                    //                                System.out.println("After ruleTestWrite");
                } else {
                    // This is the procedure of forming rules by confidence and
                    // accuracy
                    if (formingRule(tmp1) != 0)
                        significantTest(tmp1);
                    writeToRuleSet(tmp1, 0);
                }
                System.out.println("secondtmp1.len=" + tmp1.len);

            }
            flag = reOrderSon(tree);
            System.out.println("flag=" + flag);
            displayTree(tree);
            return flag;
        }

        if (tree.len == layer)
            return flag;

        if (layer <= 2) {
            for (i = 0; i < tree.numOfSon1; i++) {
                for (int m = 0; m < ChosenTest.length; m++) {
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

    public int individualFrequent(PrefixTree node) {
        int i, j, flag;

        for (i = 0; i < maxClass; i++)
            if (node.lSup[i] > lMinSup[i])
                return 1;

        return 0;
    }

    public int reOrderSon(PrefixTree tree) {
        int i, k;

        k = 0;
        //      System.out.print("tree.memForSon="+tree.memForSon);
        for (i = 0; i < tree.memForSon; i++) {
            if (tree.sonList[i] != null) {

                tree.sonList[k++] = tree.sonList[i];
                //                    System.out.println("This son is not null: "+tree.sonList[k-1].nodeID);
            }
            //  System.out.println(tree.sonList[k].nodeID);
            if (k >= tree.numOfSon)
                break;
        }
        //print tree.sonlist after reoder
        //  System.out.println("tree.sonlist length="+tree.sonList.length);


        //System.out.println("k after reoder:"+ k);

        tree.memForSon = tree.numOfSon + 1;
        //System.out.println("tree.memForson="+tree.memForSon);
        //Thuc add
        PrefixTree[] tmp;
        tmp = tree.sonList;

        tree.sonList = new PrefixTree[tree.memForSon];
        int newind = 0;
        for (int x = 0; x < tree.memForSon; x++) {
            if (tmp[x] != null) tree.sonList[newind++] = tmp[x];
        }
        //end Thuc add. Original is only the tree.sonList=new PrefixTree[tree.memForSon]

        if (k > 1)
            return (1);
        else
            return 0;
    }

    // This is the procedure for forming rules by confidence and accuracy
    public int formingRule(PrefixTree node) {
        int i, j = 0, k = 0, flag;
        int[] targetset = new int[2];
        double conf = 0, conf1, conf2, conf3, acc, maxconf, base;
//System.out.print("Start formingRule(node)");
        flag = 0;
        if (node.gSup <= gMinSup)
            return (0);
        for (i = 0; i < maxClass; i++) {
            if (node.lSup[i] < lMinSup[i])
                continue;
            conf = (double) node.lSup[i] / node.gSup;
            if (conf < minConf)
                continue;
            if (conf > 1) {
                printf("confidence > 1 \n");
                System.exit(0);
            }
            if (node.gSup > 30)
                node.acc = conf - Assconfidencelevel * Math.sqrt((conf) * (1 - conf) / node.gSup);
            else
                node.acc = (float) (node.lSup[i] + 1) / (node.gSup + maxClass);
            // base = (float)Dist[i]/MaxData;
            // if( node->Acc < base ) {node->Acc = 0; break; }
            node.conf = conf;
            node.target[0] = i;

            if (conf > PURITY)
                node.token = 2;
            else
                node.token = 1;

            if (ass != 0 || opt != 0)
                node.token = 1;

            flag = 1;
            singleRule++;
            break;
        }

        if (flag == 1 || maxTarget < 2)
            return (flag);

        flag = 0;
        maxconf = 0;
        for (i = 0; i < maxClass - 1; i++) {
            if (node.lSup[i] < lMinSup[i])
                continue;
            conf1 = (double) node.lSup[i] / node.gSup;
            base = (double) dist[i] / maxData;
            if (conf1 < 2 * base)
                continue;
            for (j = i + 1; j < maxClass; j++) {
                if (node.lSup[j] < lMinSup[j])
                    continue;
                conf2 = (double) node.lSup[j] / node.gSup;
                base = (double) dist[j] / maxData;
                if (conf2 < 2 * base)
                    continue;
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
            if (node.gSup > 30)
                node.acc = conf - Assconfidencelevel * Math.sqrt((maxconf) * (1 - maxconf) / node.gSup);
            else
                node.acc = (double) (node.lSup[i] + node.lSup[j] + 1) / (node.gSup + maxClass);
            node.conf = maxconf;
        }

        if (flag == 1 || maxTarget < 3)
            return (flag);

        flag = 0;
        maxconf = 0;
        for (i = 0; i < maxClass - 1; i++) {
            if (node.lSup[i] < lMinSup[i])
                continue;
            // if( node->LSup[i] < GMinSup) continue;
            conf1 = (double) node.lSup[i] / node.gSup;
            base = (double) dist[i] / maxData;
            if (conf1 < 3 * base)
                continue;
            for (j = i + 1; j < maxClass; j++) {
                if (node.lSup[j] < lMinSup[j])
                    continue;
                // if( node->LSup[j] < GMinSup) continue;
                conf2 = (double) node.lSup[j] / node.gSup;
                base = (double) dist[j] / maxData;
                if (conf2 < 3 * base)
                    continue;
                for (k = j + 1; k < maxClass; k++) {
                    if (node.lSup[k] < lMinSup[k])
                        continue;
                    // if( node->LSup[k] < GMinSup) continue;
                    conf3 = (double) node.lSup[k] / node.gSup;
                    base = (double) dist[k] / maxData;
                    if (conf3 < 3 * base)
                        continue;
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
            if (node.gSup > 30)
                node.acc = conf - Assconfidencelevel * Math.sqrt((maxconf) * (1 - maxconf) / node.gSup);
            else
                node.acc = (double) (node.lSup[i] + node.lSup[j] + node.lSup[k] + 1) / (node.gSup + maxClass);
            node.conf = maxconf;
        }
        return (flag);

    }

    //hs.add for choose the Chisquare rule
    public int ruleTestWrite_CRPA(PrefixTree node) {

        int i, j, k;
        double leftend, rightend;
        double sum, lsuprate, excl, conf, lift, pc, npc, npnc, pnc, p, np, oddsratio, relativerisk;

        double x, nx, cAZ, cA, cZ, c, tAZ, tA, tZ, t, value;

        if (node.gSup <= gMinSup) {
            //node.token = 0;
            //continue;

            return (0);
        }
        //return (0);


        // sum carries the local support in ratio
        sum = 0;
        for (i = 0; i < maxClass; i++) {
            if (dist[i] < 0.000001)
                //node.token = 0;
                continue;

            //return 1;
            //continue;

            sum += (float) node.lSup[i] / dist[i];

        }


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


        for (i = 0; i < maxClass; i++) {
            if (node.lSup[i] < lMinSup[i]) {
                //                            lowlsupItems[llsIndex++]=node.nodeID;
                //continue;
                //node.token = 0;
                continue;

                //return 1;
            }
            // Here begin to caculate exclusiveness
            if (dist[i] < 0.000001) {
                //continue;
                //node.token = 0;
                continue;
            }

            //return 1;


            if (node.lSup[i] < 0.0001)
                node.lSup[i] = 0.5;
            lsuprate = (double) node.lSup[i] / dist[i];
            excl = lsuprate / sum;
            if (dist[i] < 0.000001) {
                //continue;
                //node.token = 0;
                continue;
            }

            //return 1;


            // for each frequent predictive variable, to generate its own contingency table.
            x = node.gSup;          // node.gSup = counter[maxClass][item]

            nx = maxData - x;       // maxData is the total amount of data set

            cAZ = node.lSup[i];     // zx, node.lSup[1] = counter[1][item]
            cZ = dist[i] - cAZ;     // zx, dist[1] = counter[1][0]

            //System.out.println("dist[0]="+dist[0]+"dist[1]="+dist[1]);

            //cAZ = node.lSup[i];
            //cZ = dist[i] - cAZ;
            cA = x - cAZ;

            //c = maxData - x - dist[i] + cAZ;
            c = maxData - x - dist[i] + cAZ;

//        System.out.println("x="+x+" "+"nx="+nx+" "+"cAZ="+cAZ+", cA="+cA+", cZ="+cZ+", c="+c);

            // any whole row or column zero will not contribute to Chi-square
            // value on the corresponding contingency table.
            if ((cAZ + cA) * (cAZ + cZ) * (cA + c) * (cZ + c) == 0) {

                cAZ = 0;
                cA = 0;
                cZ = 0;
                c = 0;

                //node.ispa = 0;
                // this node will be pruned
                //node.token = 0;
                continue;

                //return 1;
            }

            // to obtain reliable Chi-square estimation for three or more
            // variables, the count value in each cell has to be 5 or larger.
            if (cAZ < 5) {
                cAZ = 0;
                cA = 0;
                cZ = 0;
                c = 0;

                //node.ispa = 0;
                // this node will be pruned
                //node.token = 0;
                continue;

                //return 1;
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
//        System.out.println("node.nodeID="+node.nodeID);
//        System.out.println("value="+value);
//        System.out.println(node.value);
//        System.out.println("--------------------------------");


//        if(cAZ >= tAZ && c >= t)
//        {
//            // Chi-square value is 3.84, when p-value is 0.05.
//            if(value >= ChisquareValue)
//            //if(value >= 3.84)
//            {
//                //System.out.println("ChisquareValue="+ChisquareValue);
//                //System.out.println("node.nodeID="+node.nodeID);
////                System.out.println("positive node");
//                // this rule is positive association
//                //node.ispa = 1;
//
//                //node.target[0] = 1;
//                //node.target[0] = i;
//                /*
//                if(node.len == 2)
//                {
//                    System.out.println("node:"+node.nodeID+" -> target:"+node.target[0]);
//
//                    System.out.println("node set:");
//                    for(i = 0; i < node.len; i++)
//                    {
//                        System.out.println(node.set[i]);
//                    }
//
//                }
//                */
//
//                // this node will be processed further.
//                //node.token = 1;
//                conf = (double) node.lSup[i] / node.gSup;
//                if (conf > PURITY)
//				node.token = 2;
//                        else
//				node.token = 1;
//			if (ass != 0 || opt != 0)
//				node.token = 1;
//
//			node.acc = excl;
//			node.conf = conf;
//			node.target[0] = i;
//			 //if(SimpleSignificantTest(node)) WriteToRuleSet(node, 0);
//
//       //System.out.println("RuleTestWrite- before significantTest(node)");
//       //System.out.println("Odds ratio="+oddsratio+" relativerisk="+relativerisk+"node token="+node.token);
//			significantTest(node);
//
//                singleRule++;
//
//            }
//            else
//            {
//                //System.out.println("negative node");
//                // this rule is not positive association which is candidate of combined causal association rule.
//                //node.ispa = 2;
//                // this node will be processed further.
//                //node.token = 0;
//               continue;
//                //break;
//            }
//            //return 1;
//
//        }


            //return 1;
            if (cAZ < tAZ || c < t) {
                node.token = 0;
                //node.target[0] = 0;
                continue;
            } else {
                if (value < ChisquareValue) {
                    node.token = 0;
                    //node.target[0] = 0;
                    continue;
                } else {
//            }else{
//                 conf = (double) node.lSup[i] / node.gSup;
//                        if (conf > PURITY)
//				node.token = 2;
//                        else
//				node.token = 1;
//			if (ass != 0 || opt != 0)
//				node.token = 1;
//
//			node.acc = excl;
//			node.conf = conf;
//			node.target[0] = i;
//			 //if(SimpleSignificantTest(node)) WriteToRuleSet(node, 0);
//
//       //System.out.println("RuleTestWrite- before significantTest(node)");
//       //System.out.println("Odds ratio="+oddsratio+" relativerisk="+relativerisk+"node token="+node.token);
//			significantTest(node);
//
//                        singleRule++;
//
//            }


                    conf = (double) node.lSup[i] / node.gSup;
                    if (conf > PURITY)
                        node.token = 2;
                    else
                        node.token = 1;
                    if (ass != 0 || opt != 0)
                        node.token = 1;

                    node.acc = excl;
                    node.conf = conf;
                    node.target[0] = i;
                    //if(SimpleSignificantTest(node)) WriteToRuleSet(node, 0);

                    //System.out.println("RuleTestWrite- before significantTest(node)");
                    //System.out.println("Odds ratio="+oddsratio+" relativerisk="+relativerisk+"node token="+node.token);
                    significantTest(node);

                    singleRule++;
                }


            }
        }

        return 1;


    }

    //hushu.start
    public int ruleTestWrite_CRPA_ruleSet1(PrefixTree node) {

        int i, j, k;
        double leftend, rightend;
        double sum, lsuprate, excl, conf, lift, pc, npc, npnc, pnc, p, np, oddsratio, relativerisk;

        double x, nx, cAZ, cA, cZ, c, tAZ, tA, tZ, t, value;

        if (node.gSup <= gMinSup) {
            //node.token = 0;
            //continue;

            return (0);
        }
        //return (0);


        // sum carries the local support in ratio
        sum = 0;
        for (i = 0; i < maxClass; i++) {
            if (dist[i] < 0.000001)
                //node.token = 0;
                continue;

            //return 1;
            //continue;

            sum += (float) node.lSup[i] / dist[i];

        }


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


        for (i = 0; i < maxClass; i++) {
            if (node.lSup[i] < lMinSup[i]) {
                //                            lowlsupItems[llsIndex++]=node.nodeID;
                //continue;
                //node.token = 0;
                continue;

                //return 1;
            }
            // Here begin to caculate exclusiveness
            if (dist[i] < 0.000001) {
                //continue;
                //node.token = 0;
                continue;
            }

            //return 1;


            if (node.lSup[i] < 0.0001)
                node.lSup[i] = 0.5;
            lsuprate = (double) node.lSup[i] / dist[i];
            excl = lsuprate / sum;
            if (dist[i] < 0.000001) {
                //continue;
                //node.token = 0;
                continue;
            }

            //return 1;


            // for each frequent predictive variable, to generate its own contingency table.
            x = node.gSup;          // node.gSup = counter[maxClass][item]

            nx = maxData - x;       // maxData is the total amount of data set

            cAZ = node.lSup[i];     // zx, node.lSup[1] = counter[1][item]
            cZ = dist[i] - cAZ;     // zx, dist[1] = counter[1][0]

            //System.out.println("dist[0]="+dist[0]+"dist[1]="+dist[1]);

            //cAZ = node.lSup[i];
            //cZ = dist[i] - cAZ;
            cA = x - cAZ;

            //c = maxData - x - dist[i] + cAZ;
            c = maxData - x - dist[i] + cAZ;

//        System.out.println("x="+x+" "+"nx="+nx+" "+"cAZ="+cAZ+", cA="+cA+", cZ="+cZ+", c="+c);

            // any whole row or column zero will not contribute to Chi-square
            // value on the corresponding contingency table.
            if ((cAZ + cA) * (cAZ + cZ) * (cA + c) * (cZ + c) == 0) {

                cAZ = 0;
                cA = 0;
                cZ = 0;
                c = 0;

                //node.ispa = 0;
                // this node will be pruned
                //node.token = 0;
                continue;

                //return 1;
            }

            // to obtain reliable Chi-square estimation for three or more
            // variables, the count value in each cell has to be 5 or larger.
            if (cAZ < 5) {
                cAZ = 0;
                cA = 0;
                cZ = 0;
                c = 0;

                //node.ispa = 0;
                // this node will be pruned
                //node.token = 0;
                continue;

                //return 1;
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
//        System.out.println("node.nodeID="+node.nodeID);
//        System.out.println("value="+value);
//        System.out.println(node.value);
//        System.out.println("--------------------------------");


//        if(cAZ >= tAZ && c >= t)
//        {
//            // Chi-square value is 3.84, when p-value is 0.05.
//            if(value >= ChisquareValue)
//            //if(value >= 3.84)
//            {
//                //System.out.println("ChisquareValue="+ChisquareValue);
//                //System.out.println("node.nodeID="+node.nodeID);
////                System.out.println("positive node");
//                // this rule is positive association
//                //node.ispa = 1;
//
//                //node.target[0] = 1;
//                //node.target[0] = i;
//                /*
//                if(node.len == 2)
//                {
//                    System.out.println("node:"+node.nodeID+" -> target:"+node.target[0]);
//
//                    System.out.println("node set:");
//                    for(i = 0; i < node.len; i++)
//                    {
//                        System.out.println(node.set[i]);
//                    }
//
//                }
//                */
//
//                // this node will be processed further.
//                //node.token = 1;
//                conf = (double) node.lSup[i] / node.gSup;
//                if (conf > PURITY)
//				node.token = 2;
//                        else
//				node.token = 1;
//			if (ass != 0 || opt != 0)
//				node.token = 1;
//
//			node.acc = excl;
//			node.conf = conf;
//			node.target[0] = i;
//			 //if(SimpleSignificantTest(node)) WriteToRuleSet(node, 0);
//
//       //System.out.println("RuleTestWrite- before significantTest(node)");
//       //System.out.println("Odds ratio="+oddsratio+" relativerisk="+relativerisk+"node token="+node.token);
//			significantTest(node);
//
//                singleRule++;
//
//            }
//            else
//            {
//                //System.out.println("negative node");
//                // this rule is not positive association which is candidate of combined causal association rule.
//                //node.ispa = 2;
//                // this node will be processed further.
//                //node.token = 0;
//               continue;
//                //break;
//            }
//            //return 1;
//
//        }


            //return 1;
            if (cAZ < tAZ || c < t) {
                node.token = 0;
                //node.target[0] = 0;
                continue;
            } else {
                if (value < ChisquareValue) {
                    node.token = 0;
                    //node.target[0] = 0;
                    continue;
                } else {
//            }else{
//                 conf = (double) node.lSup[i] / node.gSup;
//                        if (conf > PURITY)
//				node.token = 2;
//                        else
//				node.token = 1;
//			if (ass != 0 || opt != 0)
//				node.token = 1;
//
//			node.acc = excl;
//			node.conf = conf;
//			node.target[0] = i;
//			 //if(SimpleSignificantTest(node)) WriteToRuleSet(node, 0);
//
//       //System.out.println("RuleTestWrite- before significantTest(node)");
//       //System.out.println("Odds ratio="+oddsratio+" relativerisk="+relativerisk+"node token="+node.token);
//			significantTest(node);
//
//                        singleRule++;
//
//            }


                    conf = (double) node.lSup[i] / node.gSup;
                    if (conf > PURITY)
                        node.token = 2;
                    else
                        node.token = 1;
                    if (ass != 0 || opt != 0)
                        node.token = 1;

                    node.acc = excl;
                    node.conf = conf;
                    node.target[0] = i;
                    //if(SimpleSignificantTest(node)) WriteToRuleSet(node, 0);

                    //System.out.println("RuleTestWrite- before significantTest(node)");
                    //System.out.println("Odds ratio="+oddsratio+" relativerisk="+relativerisk+"node token="+node.token);
                    significantTest_ruleSet1(node);

                    singleRule++;
                }


            }
        }

        return 1;


    }


    public int ruleTestWrite_ruleSet1(PrefixTree node) {

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
            leftend = Math.exp(Math.log(oddsratio) - Assconfidencelevel * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));
            rightend = Math.exp(Math.log(oddsratio) + Assconfidencelevel * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));

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
                    if (oddsratio < staThreshold) continue;
                } else {
                    //System.out.println("leftend="+leftend);
                    if (leftend <= 1) continue;
                }
            }
            //System.out.println("hushu");
            //System.out.println("node.nodeID="+node.nodeID);
//	  if (relativerisk < 1.5) continue;

            // We keep these for the pruning
            // if node.token>=2 we will not generate further layers
            conf = (double) node.lSup[i] / node.gSup;
            //System.out.println("conf"+conf);
            if (conf > PURITY)
                node.token = 2;
            else
                node.token = 1;
            if (ass != 0 || opt != 0)
                node.token = 1;


            // We set the Acc for the sack of ordering rules
            // node->Acc = (oddsratio-1)/(oddsratio+1);
            node.acc = excl;
            node.conf = conf;
            node.target[0] = i;
            // if(SimpleSignificantTest(node)) WriteToRuleSet(node, 0);

            //System.out.println("RuleTestWrite- before significantTest(node)");
            //System.out.println("Odds ratio="+oddsratio+" relativerisk="+relativerisk+"node token="+node.token);
            System.out.println("do significanttest");
            significantTest_ruleSet1(node);
            System.out.println("done significanttest");
            singleRule++;
            //  break;
        }
        return 1;
    }


    public int significantTest_ruleSet1(PrefixTree node) {
        int i, j, k;
        double acctmp;


        if (ass == 1 || Non == 1) {

            writeToRuleSet_singleList1(node, 0);

            return (1);
        }
//System.out.println("Start SignificantTest(node)- node passed on: ");
//              displayTree(node);

        // if the rule is multi-targets, then return.

        // if(node->Target[1] != -1) { WriteToRuleSet (node, 0); return (1); }
        if (ass != 0) {

            writeToRuleSet_singleList1(node, 0);
            return (1);
        }
        //System.out.println("node token "+node.token);
        if (node.token == 2) {

            //System.out.println("Start writting as token=2");
            writeToRuleSet_singleList1(node, 0);

            //System.out.println("after writting, number of rules:"+ruleSet.numOfRule);
            return (1);
        }

        // Search for all tree to see if there is better rule

        acctmp = findMaxConfidence(allSet, node);

        // printf("acctmp \n %f ", acctmp);

        // if no rule found
        if (acctmp < 0.001) {
            System.out.println("write ruleSet 3");
//                     System.out.println("acctmp<0.001, now writting");
            writeToRuleSet_singleList1(node, 0);
            return (1);
        }

        if (opt == 0) {
            if (node.acc <= acctmp + minImp) {
                // I comment this since we do not get rules from the tree.
                // node -> Token = 0;
                if (node.target[1] == -1)
                    singleRule--;
                else
                    multiRule--;
            } else {

                writeToRuleSet_singleList1(node, 0);

            }
        } else {
            if (node.acc <= acctmp) {
                node.token = 0;
                if (node.target[1] == -1)
                    singleRule--;
                else
                    multiRule--;
            } else {

                writeToRuleSet_singleList1(node, 0);

            }
        }

        return 1;

    }


    //hushu.end

    //hs.end

    // This is the procedure of forming rules by exclusiveness
    public int ruleTestWrite(PrefixTree node) {

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
            leftend = Math.exp(Math.log(oddsratio) - Assconfidencelevel * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));
            rightend = Math.exp(Math.log(oddsratio) + Assconfidencelevel * Math.sqrt((1 / pc) + (1 / npnc) + (1 / npc) + (1 / pnc)));

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
                    if (oddsratio < staThreshold) continue;
                } else {
                    //System.out.println("leftend="+leftend);
                    if (leftend <= 1) continue;
                }
            }
            //System.out.println("hushu");
            //System.out.println("node.nodeID="+node.nodeID);
//	  if (relativerisk < 1.5) continue;

            // We keep these for the pruning
            // if node.token>=2 we will not generate further layers
            conf = (double) node.lSup[i] / node.gSup;
            //System.out.println("conf"+conf);
            if (conf > PURITY)
                node.token = 2;
            else
                node.token = 1;
            if (ass != 0 || opt != 0)
                node.token = 1;


            // We set the Acc for the sack of ordering rules
            // node->Acc = (oddsratio-1)/(oddsratio+1);
            node.acc = excl;
            node.conf = conf;
            node.target[0] = i;
            // if(SimpleSignificantTest(node)) WriteToRuleSet(node, 0);

            //System.out.println("RuleTestWrite- before significantTest(node)");
            //System.out.println("Odds ratio="+oddsratio+" relativerisk="+relativerisk+"node token="+node.token);
            System.out.println("do significanttest");
            significantTest(node);
            System.out.println("done significanttest");
            singleRule++;
            //  break;
        }
        return 1;
    }

    // This have not been used in this program
    public int significantTest(PrefixTree node) {
        int i, j, k;
        double acctmp;


        if (ass == 1 || Non == 1) {

            writeToRuleSet(node, 0);

            return (1);
        }
//System.out.println("Start SignificantTest(node)- node passed on: ");
        //              displayTree(node);

        // if the rule is multi-targets, then return.

        // if(node->Target[1] != -1) { WriteToRuleSet (node, 0); return (1); }
        if (ass != 0) {

            writeToRuleSet(node, 0);
            return (1);
        }
        //System.out.println("node token "+node.token);
        if (node.token == 2) {

            //System.out.println("Start writting as token=2");
            writeToRuleSet(node, 0);

            //System.out.println("after writting, number of rules:"+ruleSet.numOfRule);
            return (1);
        }

        // Search for all tree to see if there is better rule

        acctmp = findMaxConfidence(allSet, node);

        // printf("acctmp \n %f ", acctmp);

        // if no rule found
        if (acctmp < 0.001) {
            System.out.println("write ruleSet 3");
            //                     System.out.println("acctmp<0.001, now writting");
            writeToRuleSet(node, 0);
            return (1);
        }

        if (opt == 0) {
            if (node.acc <= acctmp + minImp) {
                // I comment this since we do not get rules from the tree.
                // node -> Token = 0;
                if (node.target[1] == -1)
                    singleRule--;
                else
                    multiRule--;
            } else {

                writeToRuleSet(node, 0);

            }
        } else {
            if (node.acc <= acctmp) {
                node.token = 0;
                if (node.target[1] == -1)
                    singleRule--;
                else
                    multiRule--;
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

        if (node.len == 1)
            return TRUE;

        for (i = 0; i < node.len; i++) {
            if (node.acc < node.subNode[i].acc)
                return FALSE;
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

        if (tree == null)
            return maxacc;
//thuc changed 1 to cycle
        index = new PrefixTree[CYCLE];
        indexnum = CYCLE;

		/* this is for first layer count */
        tree.reserve = 0;
        //System.out.println("rulenode.len"+rulenode.len);
        //System.out.println("tree.numOfSon"+tree.numOfSon);

        for (i = 0; i < rulenode.len; i++) {

            item = rulenode.set[i];
            //System.out.println("item"+item);

            for (j = tree.reserve; j < tree.numOfSon; j++) {
                tmptree = tree.sonList[j];
                if (tmptree.nodeID > item)
                    break;
                if ((tmptree.nodeID) == item) {

                    // here begins test
                    if (sameTarget(tmptree, rulenode) != 0) {

                        if (tmptree.acc > maxacc)
                            maxacc = tmptree.acc;
                        if (tmptree.conf > maxconf)
                            maxconf = tmptree.conf;
                    }

                    // / The last node
                    if (i == rulenode.len - 1)
                        break;

                    tree.reserve = j + 1;
                    index[count++] = tmptree;
                    tmptree.reserve = 0;
                    if (count > indexnum - 1) {
                        indexnum += CYCLE;
                        //Thuc add
                        PrefixTree[] tm = index;
                        index = new PrefixTree[indexnum];
                        System.arraycopy(tm, 0, index, 0, tm.length);
                    }
                    break;
                }
            }
            //System.out.println("count"+count);
            tmp = count;
            for (j = 0; j < tmp - 1; j++) {
                tmptree = index[j];
                //    if(tmptree==null) System.out.println("j="+j+"temttree=null");
                for (k = tmptree.reserve; k < tmptree.numOfSon; k++) {
                    if (tmptree.sonList[k].nodeID > item)
                        break;
                    if (tmptree.sonList[k].nodeID == item) {
                        // here begins test
                        if (sameTarget(tmptree.sonList[k], rulenode) != 0) {
                            if (tmptree.sonList[k].acc > maxacc)
                                maxacc = tmptree.sonList[k].acc;
                            if (tmptree.sonList[k].conf > maxconf)
                                maxconf = tmptree.sonList[k].conf;
                        }

                        tmptree.reserve = k + 1;
                        index[count++] = tmptree.sonList[k];
                        tmptree.sonList[k].reserve = 0;
                        if (count > indexnum - 1) {
                            indexnum += CYCLE;
                            //thuc add
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
        if (opt != 0)
            return (maxacc);
        else
            return (maxacc);
    }

    public int sameTarget(PrefixTree general, PrefixTree specific) {

        if (general == specific)
            return (0);
        if (general.token <= 0)
            return (0);
        if (general.target[1] != -1)
            return (0);
        if (general.target[0] != specific.target[0])
            return (0);
        return (1);

    }

    public int isPrunable(PrefixTree node) {
        int i, j, k, tt, place, l;
        int[] settmp = new int[100];
        int[] subset = new int[100];
        PrefixTree subnode;

        for (i = 0; i < node.len; i++) {
            // if satisfiying the lemma
            if (pruningTesting(node.subNode[i], node) != 0)
                return TRUE;
        }

        if (heuristic == 0)
            return FALSE;

        for (i = 0; i < node.len; i++) {
            if (higherConfidence(node.subNode[i], node) != 0)
                return FALSE;
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
                if (tmp.sonList[j].nodeID < anitem)
                    continue;
                if (tmp.sonList[j].nodeID == anitem) {
                    tmp = tmp.sonList[j];
                    break;
                }
                return null;
            }
            if (j == num)
                return null;
        }

        if (tmp.len != itemsetlen)
            return null;
        if (tmp.token == -2)
            return null;

        // for (i=0; i<tmp->Len; i++)
        // printf("    %d ", tmp->Set[i]);

        return tmp;
    }

    public int pruningTesting(PrefixTree upper, PrefixTree low) {
        int i, j, k, flag = 0, target;

        if (upper == null || low == null)
            return 0;

        if (upper.gSup == low.gSup)
            return (1);

        if (upper.gSup == 0)
            return (0);

        if (upper.token == -2)
            return (0);

        if (upper.token == 2)
            return (1);

        // / If there are targets, then only test target, actually, only one
        // target is possible
        if (Non == 1) {
            if ((upper.gSup) == (low.gSup)) flag = 1;
            return flag;
        }
        if (low.token >= 1) {
            flag = 1;
            target = low.target[0];
            if (upper.lSup[target] > lMinSup[target])
                if ((upper.gSup - upper.lSup[target]) != (low.gSup - low.lSup[target]))
                    flag = 0;
            return flag;
        }

        // otherwise test all possible consequences

        flag = 1;
        for (i = 0; i < maxClass; i++) {
            if (upper.lSup[i] > lMinSup[i] && low.lSup[i] > lMinSup[i])
                if ((upper.gSup - upper.lSup[i]) != (low.gSup - low.lSup[i])) {
                    flag = 0;
                    break;
                }
        }
        return (flag);
    }

    public int higherConfidence(PrefixTree upper, PrefixTree low) {
        int i, j, k, flag, target;
        double conf1, conf2;

        if (upper == null || low == null)
            return 0;

        // / If there are targets, then only test target, actually, only one
        // target is possible
        if (low.token >= 1) {
            target = low.target[0];
            if (low.gSup > 0)
                conf1 = (double) low.lSup[target] / low.gSup;
            else
                conf1 = 0;
            if (upper.gSup > 0)
                conf2 = (double) upper.lSup[target] / upper.gSup;
            else
                conf2 = 0;
            if (conf1 > conf2)
                return TRUE;
            return FALSE;
        }

        // otherwise test all possible consequences

        flag = 1;
        for (i = 0; i < maxClass; i++) {
            if (low.gSup > 0)
                conf1 = (float) low.lSup[i] / low.gSup;
            else
                conf1 = 0;
            if (upper.gSup > 0)
                conf2 = (float) upper.lSup[i] / upper.gSup;
            else
                conf2 = 0;
            if (conf1 > conf2)
                return TRUE;
        }
        return (FALSE);
    }

    public PrefixTree deleteNode(PrefixTree node) {

        if (node == null)
            return null;
        if (node.father != null)
            node.father.numOfSon--;
        if (node.set != null)
            free(node.set);
        if (node.sonList != null)
            free(node.sonList);
        if (node.lSup != null)
            free(node.lSup);
        free(node);
        treeSize--;

        return null;

    }

    /* this is to add a item to a node */
    public PrefixTree addSon(PrefixTree node, PrefixTree sibling, PrefixTree[] subnodeptr) {
        int i;
        PrefixTree tmp;

        tmp = new PrefixTree();
        tmp.set = new int[node.len + 2];
        tmp.lSup = new double[maxClass + 2];
        tmp.sonList = new PrefixTree[CYCLE];
        tmp.subNode = new PrefixTree[node.len + 2];

        tmp.len = node.len + 1;
        for (i = 0; i < node.len; i++)
            tmp.set[i] = node.set[i];
        tmp.set[i] = sibling.nodeID;
        tmp.father = node;
        tmp.memForSon = CYCLE;
        tmp.numOfSon = 0;
        tmp.token = 0;
        tmp.reserve = 0;
        tmp.conf = 0;
        tmp.acc = 0;
        for (i = 0; i < CYCLE; i++)
            tmp.sonList[i] = null;
        tmp.nodeID = sibling.nodeID;

        for (i = 0; i < MAXTARGET; i++)
            tmp.target[i] = -1;
        for (i = 0; i < maxClass; i++)
            tmp.lSup[i] = 0;

        for (i = 0; i < tmp.len - 2; i++)
            tmp.subNode[i] = subnodeptr[i];
        tmp.subNode[i] = node;
        i++;
        tmp.subNode[i] = sibling;

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
        tmp.len = 1;
        tmp.set[0] = item;
        tmp.father = tree;
        tmp.numOfSon = 0;
        tmp.token = 0;
        tmp.reserve = 0;
        tmp.acc = 0;
        tmp.conf = 0;
        for (i = 0; i < CYCLE; i++)
            tmp.sonList[i] = null;
        tmp.nodeID = item;
        tmp.gSup = counter[maxClass][item];
        for (i = 0; i < maxClass; i++)
            tmp.lSup[i] = counter[i][item];

        for (i = 0; i < MAXTARGET; i++)
            tmp.target[i] = -1;

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
        for (i = 0; i < tree.len; i++)
            tmp.set[i] = tree.set[i];
        tmp.set[i] = item;

        tmp.father = tree;
        tmp.numOfSon = 0;
        tmp.token = -2;
        tmp.gSup = 0;
        tmp.reserve = 0;

        for (i = 0; i < maxItem; i++)
            tmp.sonList[i] = null;
        tmp.nodeID = item;

        // for(i=0; i<MAXTARGET; i++) tmp->Target[i] = -1;

        treeSize++;
        return tmp;
    }

    /* dis play all frequent itemsets in the tree */
    public void displayTree(PrefixTree tree) {
        int i, j, k;
        double conf;
//System.out.println("tree.len="+ tree.len);
        if (tree == null)
            return;
        if (tree.len > 4 || tree.len < 0) {
            printf(" here, I got it");
            for (j = 0; j < 3; j++)
                printf("%d, ", tree.set[j]);
            printf("\n %d", tree.len);
            return;
        }
        if (tree.len > 0) {
            for (j = 0; j < tree.len; j++)
                printf(" %d ", tree.set[j]);

            printf("\t  %d", tree.numOfSon);

            if (tree.gSup > 0) {
                System.out.print("\t " + tree.gSup);
                //		printf("\tsum[%d], ", tree.gSup);
                for (k = 0; k < maxClass; k++)
                    printf("[" + k + "]" + tree.lSup[k] + ", ");
            }
            if (tree.token >= 1) {
                printf("\t");
                for (i = 0; i < MAXTARGET; i++)
                    if (tree.target[i] != -1)
                        printf("\t %d ", tree.target[i]);
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

        if (tree == null)
            return;

        if (tree.len == layer) {
            for (j = 0; j < tree.len; j++)
                printf("%d ", tree.set[j]);

            printf("\t : %d", tree.numOfSon);

            if (tree.gSup > 0) {
                System.out.println("tree.gSup: " + tree.gSup);
                //printf("\tsum[%d], ", tree.gSup);
                for (k = 0; k < maxClass; k++)
                    printf("[%d]%d, ", k, tree.lSup[k]);
            }
            if (tree.token >= 1) {
                printf("\t");
                for (i = 0; i < MAXTARGET; i++)
                    if (tree.target[i] != -1)
                        printf("%d ", tree.target[i]);
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
        while (cur != null) {
            if (cur.isCausalRule == null) break;
            if (cur.isCausalRule) count++;

            cur = cur.nextRule;
        }
        return count;

    }

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

    //hs.add
    public int verification_Two(PrefixTree tree, int layer) {
        int i;
        for (i = 0; i < maxData; i++) {
            countbyTree_Two(tree, dataSpace[i], layer);
        }
        return 1;
    }
    //hs.end

    public int countbyTree(PrefixTree tree, int[] transet, int layer) {
        int i, j, k, count, item, tmp, indexnum, targetvalue;
        PrefixTree[] index;
        PrefixTree tmptree;

        count = 0;

        if (tree == null)
            return 0;

        index = new PrefixTree[CYCLE];

        indexnum = CYCLE;

		/* this is for first layer count */
        tree.reserve = 0;
        for (i = 0; i < realAtt; i++) {
            item = transet[i]; //transet is the dataSpace row
            // if(NodeCount[item]<MinSup) continue;
            // I comment this line just because I intend to let all items be
            // frequent items
            for (j = tree.reserve; j < tree.numOfSon; j++) {
                tmptree = tree.sonList[j];
                if (tmptree.nodeID > item)
                    break;
                if ((tmptree.nodeID) == item) {
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

            tmp = count;
            for (j = 0; j < tmp - 1; j++) {
                //System.out.println("!! beajy003 - Rule - countByTree() - j:"+j+": len:"+index.length+": item:"+index[j]+":");
                tmptree = index[j];
                try {
                    for (k = tmptree.reserve; k < tmptree.numOfSon; k++) {
                        //System.out.println("!! beajy003 - Rule - countByTree() - k:"+k+": len:"+tmptree.sonList.length+": item:"+tmptree.sonList[k]+":");

                        if (tmptree.sonList[k].nodeID > item)
                            break;
                        if (tmptree.sonList[k].nodeID == item) {
                            // here begins acturally counting
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
                                //thuc add arraycopy
                                PrefixTree[] tInd = index;
                                index = new PrefixTree[indexnum];
                                System.arraycopy(tInd, 0, index, 0, tInd.length);

                            }

                            break;
                        }
                    }
                } catch (Exception e) {
                    //beajy003 - throws exceptions here because the tree is not constructed correctly
                }

            }
        }
        free(index);
        return (1);
    }

    //hs.add
    public int countbyTree_Two(PrefixTree tree, int[] transet, int layer) {
        int i, j, k, count, item, tmp, indexnum, targetvalue;
        PrefixTree[] index;
        PrefixTree tmptree;

        count = 0;

        if (tree == null)
            return 0;

        index = new PrefixTree[CYCLE];

        indexnum = CYCLE;

		/* this is for first layer count */
        tree.reserve = 0;
        for (i = 0; i < realAtt; i++) {
            item = transet[i]; //transet is the dataSpace row
            // if(NodeCount[item]<MinSup) continue;
            // I comment this line just because I intend to let all items be
            // frequent items
            for (j = tree.reserve; j < tree.numOfSon; j++) {
                tmptree = tree.sonList1[j];
                if (tmptree.nodeID > item)
                    break;
                if ((tmptree.nodeID) == item) {
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

            tmp = count;
            for (j = 0; j < tmp - 1; j++) {
                //System.out.println("!! beajy003 - Rule - countByTree() - j:"+j+": len:"+index.length+": item:"+index[j]+":");
                tmptree = index[j];
                try {
                    for (k = tmptree.reserve; k < tmptree.numOfSon; k++) {
                        //System.out.println("!! beajy003 - Rule - countByTree() - k:"+k+": len:"+tmptree.sonList.length+": item:"+tmptree.sonList[k]+":");

                        if (tmptree.sonList[k].nodeID > item)
                            break;
                        if (tmptree.sonList[k].nodeID == item) {
                            // here begins acturally counting
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
                                //thuc add arraycopy
                                PrefixTree[] tInd = index;
                                index = new PrefixTree[indexnum];
                                System.arraycopy(tInd, 0, index, 0, tInd.length);

                            }

                            break;
                        }
                    }
                } catch (Exception e) {
                    //beajy003 - throws exceptions here because the tree is not constructed correctly
                }

            }
        }
        free(index);
        return (1);
    }

    //hs.end

    public int coverageCount(PrefixTree tree, int[][] data, int num) {
        int i, l;

        toughCov = 0;
        looseCov = 0;

        for (i = 0; i < num; i++)
            countEntry(tree, data[i]);
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

        if (tree == null)
            return 0;

        index = new PrefixTree[1];
        indexnum = CYCLE;

		/* this is for first layer count */
        tree.reserve = 0;
        for (i = 0; i < realAtt; i++) {
            item = entry[i];
            // if(NodeCount[item]<GMinSup) continue;
            for (j = tree.reserve; j < tree.numOfSon; j++) {
                tmptree = tree.sonList[j];
                if (tmptree.nodeID > item)
                    break;
                if (tmptree.nodeID == item) {

                    if (tmptree.token >= 1) {
                        if (looseflag == 0 && tmptree.target[1] != -1) {
                            looseCov++;
                            looseflag = 1;
                            printf("\n **");
                            for (l = 0; l < tmptree.len; l++) {
                                printf("%d,", tmptree.set[l]);
                            }
                            printf("Target[0] = %d, Target[1] = %d", tmptree.target[0], tmptree.target[1]);
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
                    if (tmptree.sonList[k].nodeID > item)
                        break;
                    if (tmptree.sonList[k].nodeID == item) {

                        if (tmptree.sonList[k].token >= 1) {
                            if (looseflag == 0 && tmptree.sonList[k].target[1] != -1) {
                                looseCov++;
                                looseflag = 1;
                                printf("\n **");
                                for (l = 0; l < tmptree.sonList[k].len; l++)
                                    printf("%d,", tmptree.sonList[k].set[l]);
                                printf("Target[0] = %d, Target[1] = %d", tmptree.sonList[k].target[0], tmptree.sonList[k].target[1]);

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

        ruleSet1 = new RuleSet();
        ruleSet1.numOfRule = 0;
        ruleSet1.ruleHead = null;

        // SignleList is for rules with single condition

        singleList = new RuleSet();
        singleList.numOfRule = 0;
        singleList.ruleHead = null;

        singleList1 = new RuleSet();
        singleList1.numOfRule = 0;
        singleList1.ruleHead = null;

    }

    //hs.add
    public int writeToRuleSet_singleList1(PrefixTree node, int choice) {

        int flag, i, j, k, targettmp, targetnum;
        RuleStru rulecur = null, ruleahead, tmp;
        double conf, pc, npc, npnc, pnc, p, np;
//System.out.println("Start WritetoRuleSet(node, choice), ruleSet.numOfRule: "+ruleSet.numOfRule+"choice: "+choice);
        tmp = new RuleStru();

        tmp.len = node.len;
        tmp.token = 0;

        tmp.antecedent = new int[tmp.len + 1];

        tmp.lSup = new double[maxClass + 1];

        for (i = 0; i < tmp.len; i++)
            tmp.antecedent[i] = node.set[i];
        for (i = 0; i < maxClass; i++)
            tmp.lSup[i] = node.lSup[i];

        k = 0;

        tmp.target[0] = node.target[0];
        targetnum = node.target[0];
//System.out.println("node.acc="+node.acc+"node.conf="+node.conf+"node.lsup[targetnum]="+node.lSup[targetnum]+"node.gSup="+node.gSup);
        tmp.accuracy = node.acc;
        tmp.confidence = node.conf;
        tmp.support = node.lSup[targetnum];
        tmp.attSupport = node.gSup;

        //causal rule is a node got token=4
        //    if(node.token==4) tmp.isCausalRule=true;

        // We calculate Relative Risk and Odds Ratio
        // We only calculate class[0]. changed Thuc&Jiuyong change to 2 sided calculation

        p = node.gSup;
        np = maxData - p;

        pc = node.lSup[node.target[0]];
        npc = dist[node.target[0]] - pc;
        pnc = p - pc;
        npnc = maxData + pc - p - dist[node.target[0]];

        // if(npc<0.0001 || pnc<0.0001) tmp -> OddsRatio = 10000;
        // else tmp -> OddsRatio = (pc*npnc)/(npc*pnc);
        if (pc < 0.0001)
            pc = 0.5;
        if (npc < 0.0001)
            npc = 0.5;
        if (pnc < 0.0001)
            pnc = 0.5;
        if (p < 0.0001)
            p = 0.5;
        tmp.oddsRatio = (pc * npnc) / (npc * pnc);

        // if(npc<0.0001 || p<0.0001) tmp -> RelativeRisk = 10000;
        // else tmp -> RelativeRisk = (pc*np)/(npc*p);
        tmp.relativeRisk = (pc * np) / (npc * p);
//System.out.println("tmp.oddsRatio="+tmp.oddsRatio+"tmp.relativeRisk="+tmp.relativeRisk);
//System.out.println("choice="+choice);

        if (choice == 0)
            rulecur = ruleSet1.ruleHead;
        else if (choice == 1) {
            rulecur = singleList1.ruleHead;
        }


        // if associaiton rules or non-redudant rules, no ranking is necessary
        ruleahead = null;
        if (ass == 1 || Non == 1) {
            while (rulecur != null) {
                ruleahead = rulecur;
                rulecur = rulecur.nextRule;
            }
            addRuleTail_singleList1(ruleahead, tmp, choice);
            return (1);
        }

        ruleahead = null;
//if (rulecur!=null) System.out.println("rulecur!=null, so start while command");
//else System.out.println("rulecur=null, so skip while, start addRuleTail");
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
                addRuleAhead_singleList1(rulecur, tmp, choice);
                return (1);
            }
        }
        if (rulecur == null) {
            addRuleTail_singleList1(ruleahead, tmp, choice);
        }
//System.out.println("end of WriteToruleSet, ruleSet.numOfRule:  "+ruleSet.numOfRule);
        return (1);

    }


    //hs.end

	/*
     * if choice == 0, then RuleSet, if choice == 1, then SingleList
	 */

    public int writeToRuleSet(PrefixTree node, int choice) {

        int flag, i, j, k, targettmp, targetnum;
        RuleStru rulecur = null, ruleahead, tmp;
        double conf, pc, npc, npnc, pnc, p, np;
//System.out.println("Start WritetoRuleSet(node, choice), ruleSet.numOfRule: "+ruleSet.numOfRule+"choice: "+choice);
        tmp = new RuleStru();

        tmp.len = node.len;
        tmp.token = 0;

        tmp.antecedent = new int[tmp.len + 1];

        tmp.lSup = new double[maxClass + 1];

        for (i = 0; i < tmp.len; i++)
            tmp.antecedent[i] = node.set[i];
        for (i = 0; i < maxClass; i++)
            tmp.lSup[i] = node.lSup[i];

        k = 0;

        tmp.target[0] = node.target[0];
        targetnum = node.target[0];
//System.out.println("node.acc="+node.acc+"node.conf="+node.conf+"node.lsup[targetnum]="+node.lSup[targetnum]+"node.gSup="+node.gSup);
        tmp.accuracy = node.acc;
        tmp.confidence = node.conf;
        tmp.support = node.lSup[targetnum];
        tmp.attSupport = node.gSup;

        //causal rule is a node got token=4
        //    if(node.token==4) tmp.isCausalRule=true;

        // We calculate Relative Risk and Odds Ratio
        // We only calculate class[0]. changed Thuc&Jiuyong change to 2 sided calculation

        p = node.gSup;
        np = maxData - p;

        pc = node.lSup[node.target[0]];
        npc = dist[node.target[0]] - pc;
        pnc = p - pc;
        npnc = maxData + pc - p - dist[node.target[0]];

        // if(npc<0.0001 || pnc<0.0001) tmp -> OddsRatio = 10000;
        // else tmp -> OddsRatio = (pc*npnc)/(npc*pnc);
        if (pc < 0.0001)
            pc = 0.5;
        if (npc < 0.0001)
            npc = 0.5;
        if (pnc < 0.0001)
            pnc = 0.5;
        if (p < 0.0001)
            p = 0.5;
        tmp.oddsRatio = (pc * npnc) / (npc * pnc);

        // if(npc<0.0001 || p<0.0001) tmp -> RelativeRisk = 10000;
        // else tmp -> RelativeRisk = (pc*np)/(npc*p);
        tmp.relativeRisk = (pc * np) / (npc * p);
//System.out.println("tmp.oddsRatio="+tmp.oddsRatio+"tmp.relativeRisk="+tmp.relativeRisk);
//System.out.println("choice="+choice);

        if (choice == 0)
            rulecur = ruleSet.ruleHead;
        else if (choice == 1) {
            rulecur = singleList.ruleHead;
        }


        // if associaiton rules or non-redudant rules, no ranking is necessary
        ruleahead = null;
        if (ass == 1 || Non == 1) {
            while (rulecur != null) {
                ruleahead = rulecur;
                rulecur = rulecur.nextRule;
            }
            addRuleTail(ruleahead, tmp, choice);
            return (1);
        }

        ruleahead = null;
//if (rulecur!=null) System.out.println("rulecur!=null, so start while command");
//else System.out.println("rulecur=null, so skip while, start addRuleTail");
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
//System.out.println("end of WriteToruleSet, ruleSet.numOfRule:  "+ruleSet.numOfRule);
        return (1);

    }

    public boolean precede(RuleStru rule1, RuleStru rule2) {

        if (rule1.accuracy > rule2.accuracy)
            return true;

        if (rule1.accuracy == rule2.accuracy && rule1.support > rule2.support)
            return true;

        if (rule1.accuracy == rule2.accuracy && rule1.support == rule2.support && rule1.len < rule2.len)
            return true;

        return false;

    }

	/*
     * //The following tow functions are old ones used with the Significant Test
	 * //Only write to rule set by order, not remove the weak rule, which is
	 * //implemented in Function SignificantTest
	 *
	 * void WriteToRuleSet (TREENODE *node) { int flag, i, k, targettmp; RULE
	 * *rulecur, *ruleahead, *tmp; float conf;
	 *
	 *
	 * tmp = (RULE *) calloc(1, sizeof(RULE)); if(!tmp) ErrorOut (1);
	 *
	 * tmp->Len = node->Len;
	 *
	 * tmp->Antecedent = (int *) calloc (tmp->Len+1, sizeof(int));
	 *
	 * if(!tmp->Antecedent) ErrorOut (1);
	 *
	 * for(i=0; i<tmp->Len; i++) tmp->Antecedent[i] = node->Set[i];
	 *
	 * k = 0; for (i=0; i<MAXTARGET; i++){ tmp->Target[i] = node -> Target[i];
	 * if(node->Target[i] != -1) { k ++; targettmp = node->Target[i]; tmp ->
	 * Support += node->LSup[targettmp]; } }
	 *
	 * tmp -> NumOfTarget = k; tmp -> Accuracy = node -> Acc; tmp -> Confidence
	 * = node -> Conf; tmp -> AttSupport = node ->GSup; //
	 * printf("attsupport = %d", tmp->AttSupport);
	 *
	 * rulecur = RuleSet->RuleHead; if(!rulecur) { AddRuleTail(rulecur, tmp);
	 * return; }
	 *
	 *
	 *
	 * while(rulecur){ if(Precede(rulecur, tmp)) { ruleahead = rulecur; rulecur
	 * = rulecur ->NextRule; continue; } else {AddRuleAhead (rulecur, tmp);
	 * break;} } if(!rulecur) AddRuleTail(ruleahead, tmp);
	 *
	 * return; }
	 *
	 *
	 * Boolean Precede (RULE* rule1, RULE * rule2) {
	 *
	 * if(rule1->NumOfTarget < rule2->NumOfTarget) return TRUE;
	 *
	 * if(rule1->NumOfTarget == rule2->NumOfTarget && rule1->Confidence >
	 * rule2->Confidence) return (TRUE);
	 *
	 * if(rule1->NumOfTarget == rule2->NumOfTarget && rule1->Confidence ==
	 * rule2->Confidence && rule1->Support > rule2->Support ) return (TRUE);
	 *
	 * if(rule1->NumOfTarget == rule2->NumOfTarget && rule1->Confidence ==
	 * rule2->Confidence && rule1->Support == rule2->Support && rule1->Len <
	 * rule2->Len ) return (TRUE);
	 *
	 * return (FALSE);
	 *
	 * }
	 *
	 * // Please read the notes before
	 */

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
                if (item1 > item2)
                    return (0);
            }
        }

        if (flag == len2)
            return (1);
        else
            return (0);

    }

	/*
     * if choice = 0, RuleSet if choice = 1, SingleList
	 */

    public int addRuleAhead(RuleStru oldrule, RuleStru newrule, int choice) {

        RuleSet ruleset = new RuleSet();

        if (choice == 0)
            ruleset = ruleSet;
        if (choice == 1)
            ruleset = singleList;

        ruleset.numOfRule++;

        // //the old rule is the first rule

        if (oldrule.aheadRule == null) {
            ruleset.ruleHead = newrule;
            newrule.aheadRule = null;
        }

        // / Normal cases

        else {
            oldrule.aheadRule.nextRule = newrule;
            newrule.aheadRule = oldrule.aheadRule;
        }

        newrule.nextRule = oldrule;
        oldrule.aheadRule = newrule;

        return 1;
    }

    public int addRuleAhead_singleList1(RuleStru oldrule, RuleStru newrule, int choice) {

        RuleSet ruleset = new RuleSet();

        if (choice == 0)
            ruleset = ruleSet1;
        if (choice == 1)
            ruleset = singleList1;

        ruleset.numOfRule++;

        // //the old rule is the first rule

        if (oldrule.aheadRule == null) {
            ruleset.ruleHead = newrule;
            newrule.aheadRule = null;
        }

        // / Normal cases

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
        if (choice == 1)
            ruleset = singleList;
        //Thuc add the following if
        if (choice == 0)
            ruleset = ruleSet;

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


    public int addRuleTail_singleList1(RuleStru oldrule, RuleStru newrule, int choice) {

        RuleSet ruleset = new RuleSet();
        if (choice == 1)
            ruleset = singleList1;
        //Thuc add the following if
        if (choice == 0)
            ruleset = ruleSet1;

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
        if (choice == 1)
            ruleset = singleList;
        //Thuc added the following
        if (choice == 0) ruleset = ruleSet;

        printf("\nThe number of rules is %d", ruleset.numOfRule);

        if (ruleset.numOfRule > 10000) {
            printf("\n The rule set is large, skip printf");
            return (1);
        }

        cur = ruleset.ruleHead;
        while (cur != null) {
            printf("\n");
            for (i = 0; i < cur.len; i++)
                printf("%d ", cur.antecedent[i]);

            // System.out.println("cur.support="+cur.support);
            printf("\t Sup = %f", cur.support);
            printf("\t Acc = %f", cur.accuracy);
            printf("\t Conf = %f", cur.confidence);
            printf("\t Oddsratio = %f", cur.oddsRatio);
            printf("\t Target = %d", cur.target[0]);
            System.out.print("\t isCausalRule: " + cur.isCausalRule);
            //     printf("\t relativeRisk = %f", cur.relativeRisk);
            cur = cur.nextRule;
        }
        printf("\n");

        return (1);

    }

    public void writeReport(String fn, double conf, double lsup) {

        FileWriter fWrite;
        BufferedWriter fp;
        int i, j, num, classnum, item;
        int[] rulenum = new int[5];
        double rate, lift, tmp, oRerror, rRerror, n11, n12, n21, n22, n1x, n2x, nx1, nx2;
        RuleStru cur;
        //Time curtime;

        try {
            fWrite = new FileWriter(fn);
            fp = new BufferedWriter(fWrite);

            //curtime = new Time(0);

            //fprintf(fp, fn + "\t\t" + curtime + "\n\n");
            fprintf(fp, "This report is automatically generated by OCAR (Optimal Class Association Rule set generator).\n");
            fprintf(fp, "OCAR is a multipurpose rule discovery tool.\n");
            fprintf(fp, "OCAR is authored by Dr Jiuyong Li (www.unisanet.unisa.edu.au/staff/homepage.asp?name=jiuyong.li).\n");
            fprintf(fp, "Contact: jiuyong@unisa.edu.au.  \n\n\n");

            fprintf(fp, "The MINIMUM SUPPORT = %.2f\n \n", lsup);
            fprintf(fp, "The number of data = %d,\n", maxData);

            for (i = 0; i < maxClass; i++) {
                //System.out.println("!! beajy003 - Rule - writeReport() - printing:"+dist[i]+": class:"+dist[i]+":");
                fprintf(fp, "\t %f in class %s \n", dist[i], className[i]);
            }
            fprintf(fp, "The number of rules = %d, and they are listed as follow. \n\n", ruleSet.numOfRule);

            // fprintf(fp,
            // "\n\n Rules sorted by an interestingness metric in individual classes \n ");

            // We list risk patterns first
            fprintf(fp, "\nRisk patterns for %s \n\n", className[0]);
            rulenum[0] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];
                if (classnum != 0) {
                    cur = cur.nextRule;
                    continue;
                }
                fprintf(fp, "Pattern %d: \t Length = %d  \n", ++num, cur.len);

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
                if (n1x < 1)
                    n1x = 0.5;
                if (n2x < 1)
                    n2x = 0.5;
                if (nx1 < 1)
                    nx1 = 0.5;
                if (nx2 < 1)
                    nx2 = 0.5;
                rRerror = (n11 * n22 - n12 * n21) / Math.sqrt(n1x * n2x * nx1 * nx2);

                // if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);

                if (n11 < 1)
                    n11 = 0.5;
                if (n12 < 1)
                    n12 = 0.5;
                if (n21 < 1)
                    n21 = 0.5;
                if (n22 < 1)
                    n22 = 0.5;

                oRerror = cur.oddsRatio * Math.sqrt(1 / n11 + 1 / n12 + 1 / n21 + 1 / n22);

                fprintf(fp, " \t \t OR = %.4f (%.4f) \t RR = %.4f \n\n", cur.oddsRatio, oRerror, cur.relativeRisk);
                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    fprintf(fp, "\t\t %s = %s \n", itemRecord[item].attName, itemRecord[item].attr);
                }
                //        System.out.println("\n\t\t Cohort size = "+cur.attSupport+" Percentage = "+(double) cur.attSupport / maxData * 100);
                //	fprintf(fp, "\n\t\t Cohort size = %d, Percentage = %.2f \n", cur.attSupport, (double) cur.attSupport / maxData * 100);
                fprintf(fp, "\t\t Contingency table \n");
				/*
				 * for(i=0; i<MaxClass; i++){ if (Dist[i]<0.0001) rate = 0; else
				 * rate = (float)cur->LSup[i]/Dist[i]; tmp =
				 * (float)cur->AttSupport/MaxData; // lift = rate/tmp;
				 * fprintf(fp, "\t\t %s: \t %d  \t%d) \t \n", ClassName[i],
				 * cur->LSup[i], cur->AttSupport-cur->LSup); }
				 */

                fprintf(fp, "\t\t             \t%s  \t%s \n", className[0], className[1]);
                fprintf(fp, "\t\t pattern     \t%.0f  \t%.0f \n", n11, n12);
                fprintf(fp, "\t\t non-pattern \t%.0f  \t%.0f \n", n21, n22);

                fprintf(fp, "\n");
                cur = cur.nextRule;
                rulenum[0] = num;
            }

            // We then pretective patterns
            fprintf(fp, "\nPreventive patterns for %s \n\n", className[0]);
            rulenum[1] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];
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
                if (n1x < 1)
                    n1x = 0.5;
                if (n2x < 1)
                    n2x = 0.5;
                if (nx1 < 1)
                    nx1 = 0.5;
                if (nx2 < 1)
                    nx2 = 0.5;
                rRerror = (n11 * n22 - n12 * n21) / Math.sqrt(n1x * n2x * nx1 * nx2);
                // if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);
                if (n11 < 1)
                    n11 = 0.5;
                if (n12 < 1)
                    n12 = 0.5;
                if (n21 < 1)
                    n21 = 0.5;
                if (n22 < 1)
                    n22 = 0.5;
                oRerror = cur.oddsRatio * Math.sqrt(1 / n11 + 1 / n12 + 1 / n21 + 1 / n22);

                fprintf(fp, " \t \t OR = %.4f (%.4f) \t RR = %.4f \n\n", cur.oddsRatio, oRerror, cur.relativeRisk);

                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    fprintf(fp, "\t\t %s = %s \n", itemRecord[item].attName, itemRecord[item].attr);
                }

                //		fprintf(fp, "\n\t\t Cohort size = %d, Percentage = %.2f%\n", cur.attSupport, (double) cur.attSupport / maxData * 100);
                fprintf(fp, "\t\t Distribution  \n");

				/*
				 * for(i=0; i<MaxClass; i++){ if (Dist[i]<0.0001) rate = 0; else
				 * rate = (float)cur->LSup[i]/Dist[i]; tmp =
				 * (float)cur->AttSupport/MaxData; // lift = rate/tmp;
				 * fprintf(fp, "\t\t %s: \t %d (%.2f\%) \n", ClassName[i],
				 * cur->LSup[i], rate*100); }
				 */

                fprintf(fp, "\t\t             \t%s  \t%s \n", className[0], className[1]);
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
        FileWriter fWrite;
        BufferedWriter fp;
        int i, j, num, classnum, item;
        int[] rulenum = new int[5];
        double rate, lift, tmp, ORerror, RRerror, n11, n12, n21, n22, N1x, N2x, Nx1, Nx2;
        RuleStru cur;
        //Time curtime;

        try {
            fWrite = new FileWriter(fn);
            fp = new BufferedWriter(fWrite);

            //curtime = new Time(0);

            //fprintf(fp, "#%s\t\t%s\n\n", fn, curtime);
            fprintf(fp, "#This report is automatically generated by CR-CS.\n");
            fprintf(fp, "#CR-CS is a causal association rule discovery tool.\n");
            fprintf(fp, "#Paper: Mining causal association rules,(ICDM workshops 2013).\n");
            fprintf(fp, "#Total running time is %f milliseconds.\n", total_runtime);

            fprintf(fp, "The MINIMUM SUPPORT = %.2f\n \n", lsup);
            fprintf(fp, "The number of data = %d,\n", maxData);

            for (i = 0; i < maxClass; i++)
                fprintf(fp, "\t %f in class %s \n", dist[i], className[i]);

            fprintf(fp, "The number of rules = %d, and they are listed as follow. \n\n", ruleSet.numOfRule);

            // fprintf(fp,
            // "\n\n Rules sorted by an interestingness metric in individual classes \n ");

            if (confidenceTest == 1)
                fprintf(fp, "#Pattern number, length, Causal Rule, Odds Ratio, Risk Ratio, confidence, Cohort size, class 0 size, class 1 size, field name, field value, field name, field value, ...");
            else
                fprintf(fp, "#Pattern number, length, Causal Rule, Odds Ratio, Risk Ratio, Cohort size, class 0 size, class 1 size, field name, field value, field name, field value, ...");

            // We list risk patterns first
            fprintf(fp, "\n\n#Risk patterns for %s \n\n", className[0]);
            rulenum[0] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];
                if (classnum != 0) {
                    cur = cur.nextRule;
                    continue;
                }
                fprintf(fp, "%d, %d, ", ++num, cur.len);

                fprintf(fp, "%s,", " " + cur.isCausalRule);
                n11 = cur.lSup[0];
                n12 = cur.attSupport - cur.lSup[0];
                n21 = dist[0] - cur.lSup[0];
                n22 = dist[1] - cur.attSupport + cur.lSup[0];

                N1x = n11 + n12;
                N2x = n21 + n22;
                Nx1 = n11 + n21;
                Nx2 = n12 + n22;

                // if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (N1x < 1)
                    N1x = 0.5;
                if (N2x < 1)
                    N2x = 0.5;
                if (Nx1 < 1)
                    Nx1 = 0.5;
                if (Nx2 < 1)
                    Nx2 = 0.5;
                RRerror = (n11 * n22 - n12 * n21) / Math.sqrt(N1x * N2x * Nx1 * Nx2);

                // if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);

                fprintf(fp, " %.4f, %.4f, ", cur.oddsRatio, cur.relativeRisk);
                if (confidenceTest == 1)
                    fprintf(fp, " %.4f,", cur.confidence);
                // CSIRO require the 0 to be normal and 1 to be abnormal, hence
                // the
                // following printout n12 first
                fprintf(fp, " %f, %.0f, %.0f,", cur.attSupport, n12, n11);
                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    fprintf(fp, "%s, %s, ", itemRecord[item].attName, itemRecord[item].attr);
                    // add by mss
                    if (cur.len == 1) {
                        fprintf(fp, ",,,,,,,");
                        for (j = 1; j < controlSingleVar[item].length; j++) {
                            fprintf(fp, "%s, %s, ", itemRecord[controlSingleVar[item][j]].attName, itemRecord[controlSingleVar[item][j]].attr);
                        }
                    }
                }

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
                if (classnum != 1) {
                    cur = cur.nextRule;
                    continue;
                }
                fprintf(fp, "%d, %d, ", ++num, cur.len);

                fprintf(fp, "%s,", " " + cur.isCausalRule);
                n11 = cur.lSup[0];
                n12 = cur.attSupport - cur.lSup[0];
                n21 = dist[0] - cur.lSup[0];
                n22 = dist[1] - cur.attSupport + cur.lSup[0];

                N1x = n11 + n12;
                N2x = n21 + n22;
                Nx1 = n11 + n21;
                Nx2 = n12 + n22;

                // if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (N1x < 1)
                    N1x = 0.5;
                if (N2x < 1)
                    N2x = 0.5;
                if (Nx1 < 1)
                    Nx1 = 0.5;
                if (Nx2 < 1)
                    Nx2 = 0.5;
                RRerror = (n11 * n22 - n12 * n21) / Math.sqrt(N1x * N2x * Nx1 * Nx2);

                // if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);
                if (n11 < 1)
                    n11 = 0.5;
                if (n12 < 1)
                    n12 = 0.5;
                if (n21 < 1)
                    n21 = 0.5;
                if (n22 < 1)
                    n22 = 0.5;
                ORerror = cur.oddsRatio * Math.sqrt(1 / n11 + 1 / n12 + 1 / n21 + 1 / n22);

                fprintf(fp, " %.4f, %.4f, ", cur.oddsRatio, cur.relativeRisk);
                if (confidenceTest == 1)
                    fprintf(fp, " %.4f,", cur.confidence);
                fprintf(fp, " %f, %.0f, %.0f,", cur.attSupport, n12, n11);

                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    fprintf(fp, "%s, %s,", itemRecord[item].attName, itemRecord[item].attr);
                    // add by mss
                    if (cur.len == 1) {
                        fprintf(fp, ",,,,,,,");
                        for (j = 1; j < controlSingleVar[item].length; j++) {
                            fprintf(fp, "%s, %s, ", itemRecord[controlSingleVar[item][j]].attName, itemRecord[controlSingleVar[item][j]].attr);
                        }
                    }
                }

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

    public void outputToCanShowOutput(double conf, double lsup) {
        int i, j, num, classnum, item;
        int[] rulenum = new int[5];
        double rate, lift, tmp, ORerror, RRerror, n11, n12, n21, n22, N1x, N2x, Nx1, Nx2;
        RuleStru cur;
        //Time curtime;
        StringBuilder sb = new StringBuilder();

        try {
//            fWrite = new FileWriter(fn);
//            fp = new BufferedWriter(fWrite);

            //curtime = new Time(0);

            //fprintf(fp, "#%s\t\t%s\n\n", fn, curtime);
            fprintf(sb, "#This report is automatically generated by CR-CS.\n");
            fprintf(sb, "#CR-CS is a causal association rule discovery tool.\n");
            fprintf(sb, "#Paper: Mining causal association rules,(ICDM workshops 2013).\n");
            //fprintf(fp,  "Total running time is %f milliseconds.\n", total_runtime);

            fprintf(sb, "The MINIMUM SUPPORT = %.2f\n \n", lsup);
            fprintf(sb, "The number of data = %d,\n", maxData);

            for (i = 0; i < maxClass; i++)
                fprintf(sb, "\t %f in class %s \n", dist[i], className[i]);

            fprintf(sb, "Total running time is %f milliseconds.\n", total_runtime);

            fprintf(sb, "The number of rules = %d, and they are listed as follow. \n\n", ruleSet.numOfRule);

            // fprintf(fp,
            // "\n\n Rules sorted by an interestingness metric in individual classes \n ");

            //if(confidenceTest == 1)
            //	fprintf(fp, "#Pattern number, length, Causal Rule, Odds Ratio, Risk Ratio, confidence, Cohort size, class 0 size, class 1 size, field name, field value, field name, field value, ...");
            //else
            //	fprintf(fp, "#Pattern number, length, Causal Rule, Odds Ratio, Risk Ratio, Cohort size, class 0 size, class 1 size, field name, field value, field name, field value, ...");

            // We list risk patterns first
            //fprintf(fp, "\n\n#Risk patterns for %s \n\n", className[0]);
            fprintf(sb, "\n\nRules with target Z= %s \n\n", className[0]);
            rulenum[0] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];
                if (classnum != 0) {
                    cur = cur.nextRule;
                    continue;
                }
                //fprintf(fp, "%d, %d, ", ++num, cur.len);

                //fprintf(fp, "%s,", " "+cur.isCausalRule);
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

                N1x = n11 + n12;
                N2x = n21 + n22;
                Nx1 = n11 + n21;
                Nx2 = n12 + n22;

                // if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (N1x < 1)
                    N1x = 0.5;
                if (N2x < 1)
                    N2x = 0.5;
                if (Nx1 < 1)
                    Nx1 = 0.5;
                if (Nx2 < 1)
                    Nx2 = 0.5;
                RRerror = (n11 * n22 - n12 * n21) / Math.sqrt(N1x * N2x * Nx1 * Nx2);

                // if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);

                //fprintf(fp, " %.4f, %.4f, ", cur.oddsRatio, cur.relativeRisk);
                //if(confidenceTest == 1)
                //	fprintf(fp, " %.4f,", cur.confidence);
                // CSIRO require the 0 to be normal and 1 to be abnormal, hence
                // the
                // following printout n12 first
                //fprintf(fp, " %f, %.0f, %.0f,", cur.attSupport, n12, n11);
                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    //fprintf(fp, "%s, %s, ", itemRecord[item].attName, itemRecord[item].attr);
                    fprintf(sb, " %s%s%s ", itemRecord[item].attName, "=",
                            itemRecord[item].attr);
                }
                fprintf(sb, " %s %s%s,", "->", "Z=", className[0]);
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
            fprintf(sb, "\nRules with target Z=  %s \n\n", className[1]);
            rulenum[1] = 0;
            num = 0;
            cur = ruleSet.ruleHead;
            while (cur != null) {
                classnum = cur.target[0];
                if (classnum != 1) {
                    cur = cur.nextRule;
                    continue;
                }
                //fprintf(fp, "%d, %d, ", ++num, cur.len);

                //fprintf(fp, "%s,", " "+cur.isCausalRule);
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

                N1x = n11 + n12;
                N2x = n21 + n22;
                Nx1 = n11 + n21;
                Nx2 = n12 + n22;

                // if(N1x<1 || N2x<1 || Nx1<1 ||Nx2<1) RRerror = 10000;
                // else RRerror = (n11*n22-n12*n21)/sqrt(N1x*N2x*Nx1*Nx2);
                if (N1x < 1)
                    N1x = 0.5;
                if (N2x < 1)
                    N2x = 0.5;
                if (Nx1 < 1)
                    Nx1 = 0.5;
                if (Nx2 < 1)
                    Nx2 = 0.5;
                RRerror = (n11 * n22 - n12 * n21) / Math.sqrt(N1x * N2x * Nx1 * Nx2);

                // if(n11<1 || n12<1 || n21 <1 || n22<1) ORerror = 10000;
                // else ORerror = cur->OddsRatio*sqrt(1/n11+1/n12+1/n21+1/n22);
                if (n11 < 1)
                    n11 = 0.5;
                if (n12 < 1)
                    n12 = 0.5;
                if (n21 < 1)
                    n21 = 0.5;
                if (n22 < 1)
                    n22 = 0.5;
                ORerror = cur.oddsRatio * Math.sqrt(1 / n11 + 1 / n12 + 1 / n21 + 1 / n22);

                //fprintf(fp, " %.4f, %.4f, ", cur.oddsRatio, cur.relativeRisk);
                //if(confidenceTest == 1)
                //	fprintf(fp, " %.4f,", cur.confidence);
                //fprintf(fp, " %f, %.0f, %.0f,", cur.attSupport, n12, n11);

                for (i = 0; i < cur.len; i++) {
                    item = cur.antecedent[i];
                    //fprintf(fp, "%s, %s,", itemRecord[item].attName, itemRecord[item].attr);
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
        printf("The rule list for class %s (num = %d),  Number of rules %d, Coverage: %d / %d = %.1f %% \n", className[classnum], setGroup.totalRecord, setGroup.numofSet, setGroup.coverNum, setGroup.totalRecord, ((double) setGroup.coverNum / setGroup.totalRecord) * 100);
        count = 0;
        setptr = setGroup.supSetList.setHead;
        err = cov = 0;
        if (setptr == null)
            return;
        while (setptr != null) {
            printf("rule %d: ", ++count);
            printf("If   ");
            for (k = 0; k < setptr.numofItems; k++) {
                if (k != 0)
                    printf("AND ");
                num = setptr.itemList[k];
                printf("%s is %s ", itemRecord[num].attName, itemRecord[num].attr);
				/*
				 * if(EquiAttr[num]){ l=0; while(EquiAttr[num][l]){
				 * printf("OR "); printf("%s is %s ",
				 * ItemRecord[EquiAttr[num][l]]->AttName,
				 * ItemRecord[EquiAttr[num][l]]->Attr); l++; } }
				 */
            }
            printf("  THEN class %s\n", className[classnum]);
            printf("\t (%d/%d) hold in class, and (%d/%d) outside class\n", setptr.localSupport, setGroup.totalRecord, (setptr.globalSupport - setptr.localSupport), (maxData - setGroup.totalRecord));
            err += (setptr.globalSupport - setptr.localSupport);
            cov += setptr.localSupport;
            setptr = setptr.nextSet;
        }
        printf("\n average accuracy = %f    average coverage = %f \n", (1.0 - (float) err / cov) * 100, (double) cov / setGroup.numofSet);

    }

    int QuickSortInteger(int[] data, int len) {
        int i, j, k;
        int temp;

        for (i = 0; i < len; i++) {
            k = i;
            for (j = i + 1; j < len; j++) {
                if (data[j] < data[k])
                    k = j;
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
                fprintf(fp, "%d %f %f %ld %d %ld ", cur.len, cur.accuracy, cur.confidence, cur.support, cur.numOfTarget, cur.attSupport);
                for (i = 0; i < maxClass; i++)
                    fprintf(fp, "%ld ", cur.lSup[i]);
                for (i = 0; i < cur.len; i++)
                    fprintf(fp, "%d ", cur.antecedent[i]);
                for (i = 0; i < MAXTARGET; i++)
                    fprintf(fp, "%d ", cur.target[i]);
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

            printf("\n MinLocalSupport = %.4f, MinConfidence = %.2f, MaxLayer = %d, MinImprovement = %f", lsup, conf, l, imp);

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
                } else
                    ruleSet.numofError++;
                break;
            }

            if (rulecur == null) {
                if (ruleSet.defaultValue == -1)
                    continue;
                if (row[realAtt] == ruleSet.defaultValue)
                    ruleSet.numofCorrect++;
                else
                    ruleSet.numofError++;
            }

        }
        // printf("\n accuracy = %.2f %%",
        // (float)(RuleSet->SumCorrect)/MaxData*100);
        printf("\n accuracy = %.2f %%", (double) (ruleSet.numofCorrect) / maxData * 100);
        printf("\n (for rules only) accuracy = %.2f %%", (double) ruleSet.numofCorrect / (ruleSet.numofCorrect + ruleSet.numofError) * 100);
        printf("\n untouched = %.2f %%", (double) (maxData - ruleSet.numofCorrect - ruleSet.numofError) / maxData * 100);
        printf("\n");
    }

    public int matchTest(int[] datptr, RuleStru rule) {
        int i, j, k, item;

        k = 0;
        for (i = 0; i < rule.len; i++) {
            item = rule.antecedent[i];
            for (j = k; j < realAtt; j++) {
                if (datptr[j] > item)
                    return (-1);
                if (datptr[j] == item) {
                    k = j + 1;
                    break;
                }
            }
            if (j == realAtt)
                return (-1);
        }

        for (i = 0; i < MAXTARGET; i++) {
            if (rule.target[i] == -1)
                break;
            if (datptr[realAtt] == rule.target[i])
                return (1);
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

        for (i = 0; i < maxClass; i++)
            distnum[i] = (int) dist[i];

        fillInTable(table);

        k = 0;
        count = 0;
        i = 0;
        while (readRuleLabel(table, rulelabel, k) != 0) {
            if (rulelabel.incorrect == 0)
                conf = 1;
            else
                conf = (float) rulelabel.correct / (rulelabel.correct + rulelabel.incorrect);
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

        if (discard != 0)
            discardRestRule(k);
        ruleSet.defaultValue = currentDefault(distnum);

        free(rulelabel);
        for (i = 0; i < ruleSet.numOfRule; i++)
            free(table[i]);
        free(table);

        return 1;
    }

    // int ReadRuleLabel (char ** table, RULELABEL * rulelabel, int begin)
    public int readRuleLabel(int[][] table, RuleLabelStr rulelabel, int begin) {
        int i, j, errortmp, correcttmp, correct = 0, error, label = 0;

        error = maxData;
        for (i = begin; i < ruleSet.numOfRule; i++) {
            if (table[i][maxData + 1] != 0)
                continue;
            errortmp = 0;
            correcttmp = 0;
            for (j = 0; j < maxData; j++) {
                if (table[i][j] == -1)
                    errortmp++;
                if (table[i][j] == 1)
                    correcttmp++;
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
        } else
            return 0;
    }

    // int UpdateTable(char **table, int * distnum, int where)
    public int updateTable(int[][] table, int[] distnum, int where) {
        int i, j, k, targetnum;

        for (i = 0; i < maxData; i++) {
            if (table[where][i] == 0)
                continue;
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
            //System.out.println("!! Rule - currentDefault() - return value not set before return - default to 0");
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
            if (distnum[i] > max)
                max = distnum[i];
        }

        if (sum == 0)
            return 1;
        rate = (float) max / sum;

        return rate;
    }

    public int swapRule(int index1, int index2) {
        int i, j, k;
        RuleStru rule1 = null, rule2 = null, tmp, cur;

        if (index1 == index2)
            return 1;

        cur = ruleSet.ruleHead;
        k = 0;
        j = 0;
        while (cur != null) {
            if (k == index1) {
                rule1 = cur;
                if (++j >= 2)
                    break;
            }
            if (k == index2) {
                rule2 = cur;
                if (++j >= 2)
                    break;
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
            if (rule1.aheadRule != null)
                rule1.aheadRule.nextRule = rule2;
            else
                ruleSet.ruleHead = rule2;

            if (rule1.nextRule != null)
                rule1.nextRule.aheadRule = rule2;

            if (rule2.aheadRule != null)
                rule2.aheadRule.nextRule = rule1;
            if (rule2.nextRule != null)
                rule2.nextRule.aheadRule = rule1;

            tmp = rule1.aheadRule;
            rule1.aheadRule = rule2.aheadRule;
            rule2.aheadRule = tmp;

            tmp = rule1.nextRule;
            rule1.nextRule = rule2.nextRule;
            rule2.nextRule = tmp;
        }

        // / if rule1 and rule2 are adjacent
        else {
            if (rule1.aheadRule != null)
                rule1.aheadRule.nextRule = rule2;
            else
                ruleSet.ruleHead = rule2;

            if (rule2.nextRule != null)
                rule2.nextRule.aheadRule = rule1;

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

        if (index1 == index2)
            return 1;

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
                } else if (dat != 0)
                    table[k][i] = 1;
                else
                    table[k][i] = -1;
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

        if (index == ruleSet.numOfRule)
            return 1;

        ruleSet.numOfRule = index;
        cur = ruleSet.ruleHead;
        k = 0;

        while (cur != null) {
            if (k == index)
                break;
            cur = cur.nextRule;
            k++;
        }

        if (cur != null) {
            tail = cur.aheadRule;
            tail.nextRule = null;
        } else
            return 0;

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
            for (j = 0; j < maxData; j++)
                printf("%d, ", table[i][j]);
        }
    }

    public void testAndSetDefult() {
        int i, j = 0, k, dat, max;
        int[] classArray = new int[100];
        RuleStru rulecur;
        int[] row;

        ruleSet.numofCorrect = 0;
        ruleSet.numofError = 0;
        for (i = 0; i < maxClass; i++)
            classArray[i] = 0;
        ruleSet.defaultValue = -1;

        for (i = 1; i < maxData; i++) {
            row = dataSpace[i];
            rulecur = ruleSet.ruleHead;
            while (rulecur != null) {
                if ((dat = matchTest(row, rulecur)) == -1) {
                    rulecur = rulecur.nextRule;
                    continue;
                } else if (dat != 0)
                    ruleSet.numofCorrect++;
                else
                    ruleSet.numofError++;
                break;
            }
            if (rulecur == null)
                classArray[row[realAtt]]++;
        }

        max = -1;
        for (i = 0; i < maxClass; i++)
            if (classArray[i] > max) {
                max = classArray[i];
                j = i;
            }
        ruleSet.defaultValue = j;

        ruleSet.numofCorrect += max;
        ruleSet.numofError = maxData - ruleSet.numofCorrect;
        printf("\n accuracy = %.2f %%", (double) ruleSet.numofCorrect / maxData * 100);
        printf("\n unaccurcy = %.2f %%", (double) ruleSet.numofError / maxData * 100);

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
                    if (k == complete)
                        break;
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
            if (rulecur.token == 0)
                rulecur = deleteRule(rulecur);
            else
                rulecur = rulecur.nextRule;
        }

    }

    public RuleStru deleteRule(RuleStru rule) {

        RuleStru tmp;

        if (rule.aheadRule == null)
            ruleSet.ruleHead = rule.nextRule;
        else
            rule.aheadRule.nextRule = rule.nextRule;

        if (rule.nextRule != null)
            rule.nextRule.aheadRule = rule.aheadRule;

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
        for (i = 1; i <= rule.len; i++)
            set[i] = attribute[rule.antecedent[i]];

        return (1);

    }

    public int overLapAttribute(RuleStru rule, int[] set) {
        int i, j, k, l, num, att;
        int[] flagset = new int[20];

        for (i = 0; i < 20; i++)
            flagset[i] = 0;

        num = 0;
        l = set[0];
        k = 0;
        for (i = 1; i <= l; i++) {
            att = set[i];
            for (j = k; j < rule.len; j++) {
                if (attribute[rule.antecedent[j]] < att)
                    continue;
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
            if (flagset[i] != 0)
                set[k++] = set[i];
        }

        set[0] = num;

        return num;

    }

    public boolean readName() {
        try {
            if (scan.hasNextLine()) {
                scannedLine = scan.nextLine();
                //skip the blank lines, comment lines

                while ((scannedLine.length() == 0) || (scannedLine.length() > 0 & scannedLine.charAt(0) == '|') || (scannedLine.length() > 0 & scannedLine.charAt(0) == ' ')) {
                    scannedLine = scan.nextLine();
                }


                storeName = "";


				/* Skip to first non-space character*/
//                                if(scannedLine.length()==0){
//                                    while(scannedLine.length()==0) scannedLine=scan.nextLine();
//                                }
//				if(scannedLine.length() > 0) {
//					while (scannedLine.charAt(0) == '|' || scannedLine.charAt(0) == ' ') {
//						scannedLine = scan.nextLine();
//					}
//				}
//
//
				/* Read in characters up to the next delimiter */
                int i = 0;
                // while (c != ':' && c != ',' && c != '\n' && c != '|' && c != EOF)
                // {
                for (; i < scannedLine.length() && scannedLine.charAt(i) != ':' && scannedLine.charAt(i) != ',' && scannedLine.charAt(i) != '\n' && scannedLine.charAt(i) != '|'; i++) {
                    //System.out.println("\t!! Rule - readName() - charAt:" + line.charAt(i) + ": equal?:" + (line.charAt(i) == ',') + ":");
                    storeName += scannedLine.charAt(i);
                    //System.out.println("!! Rule - readName() - compiling name:" + storeName);
                }
                //System.out.println("!! Rule - readName() - NAME COMPLETE:" + storeName + ":");
                if (i < scannedLine.length()) {
                    delimiter = scannedLine.charAt(i);
                    //  System.out.println("delimiter: "+delimiter);

                    //if after , is a space then skip the space
                    if (scannedLine.charAt(i + 1) != ' ')
                        scannedLine = scannedLine.substring(storeName.length() + 1);
                    else scannedLine = scannedLine.substring(storeName.length() + 2);

                    //   System.out.println("scannedLine: "+scannedLine);
                    return true;
                } else {
                    delimiter = ' ';
                    return false;
                }
            } else {
                //System.out.println("!! Rule - readName() - END OF FILE FOUND");
                return false;
            }
        } catch (Exception e) {
            System.out.println("## ERROR -  Rule - readName() - error reading names from file");
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Method by beajy003 to read the data values from the data file. This was
     * origionally done by the readName() method in c. However it is easier if
     * I create a new method.
     *
     * @return True if there was data read
     * @author beajy003
     */
    public boolean readData() {
        //System.out.println("!! Rule - readData() - Begin - line:"+scannedLine+": name:"+storeName+": del:"+delimiter+":");
        //remove comments - Thuc
        if (delimiter == '|') {
            delimiter = '.';
            scannedLine = scan.nextLine();
        }

        if (delimiter == '.') { // beajy003 read next line
            char firstChar = '|';
            while (firstChar == '|') {
                if (!scan.hasNextLine()) {
                    return false;
                }
                scannedLine = scan.nextLine();
                try {
                    firstChar = scannedLine.charAt(0);
                } catch (Exception e) {
                    firstChar = '|';
                }
            }
        }
        storeName = "";
        int i = 0;
        for (; i < scannedLine.length() && scannedLine.charAt(i) != ',' && !(scannedLine.charAt(i) == '.' && (i + 1 == scannedLine.length())); i++) {
            //System.out.println("\t!! Rule - readName() - charAt:" + line.charAt(i) + ": equal?:" + (line.charAt(i) == ',') + ":");
            storeName += scannedLine.charAt(i);
            //	System.out.println("!! Rule - readName() - compiling name:" + storeName);
        }
        try {
            delimiter = scannedLine.charAt(i);
            //if there is a space after the , then skip the space
            if (scannedLine.charAt(i + 1) == ' ')
                scannedLine = scannedLine.substring(storeName.length() + 2);
            else
                scannedLine = scannedLine.substring(storeName.length() + 1);
        } catch (Exception ex) {
            delimiter = '.';
        }
        //System.out.println("!! Rule - readData() - END - line:"+scannedLine+": name:"+storeName+": del:"+delimiter+":");
        return true;
    }

    public void getNames()

	/* --------- */ {
        try {

            String fn = "";
            int v, k;
            int att_ceiling = 5000, class_ceiling = 50, att_val_ceiling = 100;
            AttributeCode attptr;

			/* Open names file */

            fn = fileName;
            fn = fn + ".names";
            nf = new File(fn);
            scan = new Scanner(nf);

			/* Get class names from names file */

            className = new String[class_ceiling];
            maxItem = 0;
			/* Item=0 is for missing attribute */
            maxClass = -1;

            readName();
            // System.out.println("Test readName "+storeName);
            if (++maxClass >= class_ceiling) {
                class_ceiling += 50;
                String[] tmpString = new String[class_ceiling];
                for (int i = 0; i < className.length; i++) {
                    tmpString[i] = className[i];
                }
                className = tmpString;

            }
            className[maxClass] = storeName;

            while (delimiter == ',') {
                storeName = "";
                int i = 0;
                for (; i < scannedLine.length() && scannedLine.charAt(i) != '.' && scannedLine.charAt(i) != ':' && scannedLine.charAt(i) != ',' && scannedLine.charAt(i) != '\n' && scannedLine.charAt(i) != '|'; i++) {
                    //System.out.println("\t!! Rule - readName() - charAt:" + line.charAt(i) + ": equal?:" + (line.charAt(i) == ',') + ":");
                    storeName += scannedLine.charAt(i);
                    //System.out.println("!! Rule - readName() - compiling name:" + storeName+"i="+i );
                }

                if (++maxClass >= class_ceiling) {
                    class_ceiling += 50;
                    String[] tmpString = new String[class_ceiling];
                    for (int j = 0; j < className.length; j++) {
                        tmpString[j] = className[j];
                    }
                    className = tmpString;

                }
                delimiter = scannedLine.charAt(i);
                //  System.out.println("delimiter:"+delimiter);
                className[maxClass] = storeName;
            }
            //beajy003 - remove blank line if there is a blank line
            //Thuc: this has been done in readNames


            //scannedLine = scan.nextLine();

			/* Get attribute and attribute value names from names file */

            attName = new String[att_ceiling];
            specialStatus = new String[att_ceiling];
            attCode = new AttributeCode[att_ceiling];
            itemRecord = new ItemRecord[item_Id_Ceiling];
            if (attName == null || specialStatus == null || attCode == null || itemRecord == null)
                errorOut(1);

            attribute = new int[item_Id_Ceiling];

            attValue = new int[att_ceiling][];

            maxAttVal = new int[att_ceiling];

            maxAtt = -1;
            realAtt = -1;
			/* unknow attribute put in AttCode[0] */
            itemRecord[0] = addItemRecord("?", "unknown attribute");
            // AttCode[++MaxAtt] = AddAttCode("?", ++MaxItem);
            while (readName()) {

                if (delimiter != ':')
                    error(1, storeName, "");

                if (++maxAtt >= att_ceiling - 1) {
                    att_ceiling += 50;
                    //copy
                    String[] temp = new String[attName.length];
                    System.arraycopy(attName, 0, temp, 0, attName.length);


                    attName = new String[att_ceiling];
                    System.arraycopy(temp, 0, attName, 0, temp.length);

                    String[] tempspesta = new String[specialStatus.length];
                    System.arraycopy(specialStatus, 0, tempspesta, 0, specialStatus.length);
                    specialStatus = new String[att_ceiling];
                    System.arraycopy(tempspesta, 0, specialStatus, 0, tempspesta.length);

                    AttributeCode[] tempattcode = new AttributeCode[attCode.length];
                    System.arraycopy(attCode, 0, tempattcode, 0, attCode.length);
                    attCode = new AttributeCode[att_ceiling];
                    System.arraycopy(tempattcode, 0, attCode, 0, tempattcode.length);

                    if (attName == null || specialStatus == null || attCode == null)
                        errorOut(1);

                    attValue = new int[att_ceiling][];

                    maxAttVal = new int[att_ceiling];
                }
//read the attribute
                attName[maxAtt] = storeName;
                //   System.out.println("attName store:"+attName[maxAtt]);
                specialStatus[maxAtt] = "";
                attptr = attCode[maxAtt] = null;
                realAtt++;

                maxAttVal[realAtt] = 0;

                attValue[realAtt] = new int[att_val_ceiling];
//read the item values
                //      for(int h=0; h<attName.length; h++) System.out.println(attName[h]);
                do {

                    storeName = "";
                    int i = 0;
                    for (; i < scannedLine.length() && !(scannedLine.charAt(i) == '.' && scannedLine.length() == i) && scannedLine.charAt(i) != ':' && scannedLine.charAt(i) != ',' && scannedLine.charAt(i) != '|'; i++) {
                        storeName += scannedLine.charAt(i);
                    }
                    //    System.out.println("storename"+storeName);
                    if (i == scannedLine.length()) {
                        delimiter = ' ';
                    } else {
                        delimiter = scannedLine.charAt(i);
                    }

                    try {
                        //Thuc add to handle ,or , or.
                        if (scannedLine.charAt(i + 1) == ' ')
                            scannedLine = scannedLine.substring(storeName.length() + 2);
                        else scannedLine = scannedLine.substring(storeName.length() + 1);
                    } catch (Exception ex) {
                        storeName = storeName.substring(0, storeName.length() - 1);
                        delimiter = '.';
                    }

                    if (attptr == null) {
                        if (storeName.compareTo("continuous") == 0) {
                            specialStatus[maxAtt] = "" + CONTINUOUS;
                            printf("\n I do not process continuous attribute, discretize it first \n");
                            System.exit(0);
                        }
                        if (storeName.compareTo("discrete") == 0) {
                            specialStatus[maxAtt] = "" + DISCRETE;
                            printf("I find the discrete value in %s.naame\n", fileName);
                            System.exit(0);
                        }
                        if (storeName.compareTo("ignore") == 0 || storeName.compareTo("ignore. ") == 0 || storeName.compareTo("ignore ") == 0) {
                            specialStatus[maxAtt] = "" + IGNORE;
                            free(attValue[realAtt]);
                            realAtt--;
                            break;
                        }

                        attCode[maxAtt] = addAttCode(storeName, ++maxItem);
                        attptr = attCode[maxAtt];
                    } else {
                        attptr.next = addAttCode(storeName, ++maxItem);
                        attptr = attptr.next;
                    }

                    k = maxAttVal[realAtt];
                    maxAttVal[realAtt]++;
                    attValue[realAtt][k] = maxItem;
                    attribute[maxItem] = realAtt;

                    // printf(" %s: RealAtt = %d, MaxAttVal = %d, MaxItem = %d \n",
                    // buffer, RealAtt, MaxAttVal[RealAtt], MaxItem);

                    if (maxAttVal[realAtt] >= att_val_ceiling - 1) {
                        att_val_ceiling += 10;
                        int[] tmpInt = new int[att_val_ceiling];
                        for (int j = 0; j < attValue[realAtt].length; j++) {
                            tmpInt[j] = attValue[realAtt][j];
                        }
                        attValue[realAtt] = tmpInt;
                    }

                    if (maxItem >= item_Id_Ceiling - 1) {
                        item_Id_Ceiling += 50;

                        ItemRecord[] tmpStore = new ItemRecord[item_Id_Ceiling];
                        for (int j = 0; j < itemRecord.length; j++) {
                            tmpStore[j] = itemRecord[j];
                        }
                        itemRecord = tmpStore;

                        int[] tmpInt = new int[item_Id_Ceiling];
                        for (int j = 0; j < attribute.length; j++) {
                            tmpInt[j] = attribute[j];
                        }
                        attribute = tmpInt;
                    }
                    itemRecord[maxItem] = addItemRecord(attName[maxAtt], storeName);
                    specialStatus[maxAtt] = "" + 0;
                } while (delimiter == ',');

            }
            //test
            // for(int h=0; h<attName.length; h++) System.out.println(attName[h]);
            maxClass++;
            maxAtt++;
            realAtt++;
            //maxItem ++;
            scan.close();
        } catch (Exception e) {
            System.out.println("## ERROR - Rule - getNames() - Error reading names from file");
            e.printStackTrace();
        }
    }

    public AttributeCode addAttCode(String att, int id) {
        AttributeCode tmp;

        tmp = new AttributeCode();
        tmp.itemID = id;
        tmp.attr = "";
        tmp.attr = att;
        tmp.next = null;
        return tmp;
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

    // beajy003 - return type changed to void as there are no return statements.
    // From int to void.
    public void error(int n, String s1, String s2) {
        int messages = 0;

        printf("\nERROR:  ");
        switch (n) {
            case 0:
                printf("cannot open file %s%s\n", s1, s2);
                System.exit(1);

            case 1:
                printf("colon expected after attribute name %s\n", s1);
                break;

            case 2:
                printf("unexpected eof while reading attribute %s\n", s1);
                break;

            case 3:
                printf("attribute %s has only one value\n", s1);
                break;

            case 4:
                printf("case %d's value of '%s' for attribute %s is illegal\n", maxData + 1, s2, s1);
                break;

            case 5:
                printf("case %d's class of '%s' is illegal\n", maxData + 1, s2);
        }

        if (++messages > 10) {
            printf("Error limit exceeded\n");
            System.exit(1);
        }
    }

    /*************************************************************************/
	/*									 */
	/* Read raw case descriptions from file with given extension. */
	/* On completion, the description store in the int **item */
	/* item :{val1, val2.......valn, object} */
	/*									 */
	/* Read a raw case description from file df. */
	/*									 */
	/* missing value (?) or the unknown value put in AttCode[0] */
	/*									 */

    /*************************************************************************/

    public int getData(String extension) {
        String fn = "";
        try {
            int att = 0, i, k, dataceil = 100, dat;
            int count;

			/* Open data file */

            fn = fileName;
            fn = fn + extension;
            nf = new File(fn);
            scan = new Scanner(nf);

            rawDataSpace = new double[dataceil][];
            maxData = 0;
            att = 0;
            k = -1;
            rawDataSpace[maxData] = new double[realAtt + 2];

            count = 0;

            while (readData()) {

                //   System.out.println("Dataread: "+storeName+" - current count: "+count+" - maxAtt:"+maxAtt);

                //This is to read the class
                if ((1 + count++) % (maxAtt + 1) == 0) { // beajy003 exclude 0 case
                    //&& count != 1
                    //beajy003 - get the class name out of the data line
                    //int it = scannedLine.length() - 1;

                    //String tmpStore = "", storeName = "";

                    //for (; it >= 0 && scannedLine.charAt(it) != ','; it--) { tmpStore += scannedLine.charAt(it); }
                    //   System.out.println(tmpStore);
                    //for(int j = 2; j < tmpStore.length(); j++) { storeName += tmpStore.charAt(tmpStore.length() - j);}
                    //   System.out.println(storeName);
                    //beajy003 - class name found in storeName
                    //System.out.println("!! Rule - getData() - looking for class:"+storeName+": count:"+count+": max:"+maxAtt+":");
                    //System.out.println("Get to here");
                    dat = getNameCode(storeName);
                    //        System.out.println(dat);
                    if (dat == -1) {
                        System.out.println("## ERROR - Rule - getData() - class name not found :" + className + ":");
                    }

                    rawDataSpace[maxData][att] = dat;
                    //System.out.println("maxData: "+maxData+"att: "+att+"rawDataSpace[maxData][att]="+rawDataSpace[maxData][att]);
                    att = 0;
                    k = -1;
                    if (++maxData > dataceil - 1) {
                        dataceil += 100;
                        double[][] tmpSpare = new double[dataceil][];
                        for (int j = 0; j < rawDataSpace.length; j++) {
                            tmpSpare[j] = rawDataSpace[j];
                        }
                        rawDataSpace = tmpSpare;
                    }
                    rawDataSpace[maxData] = new double[realAtt + 2];
                    continue;
                }


                k++;
                if (specialStatus[k] == "" + IGNORE) {
                    //            System.out.println("Skip the ignore ones");
                    continue;
                }

                if (specialStatus[k] == "" + CONTINUOUS) {
                    if (storeName.compareTo("?") == 0)
                        rawDataSpace[maxData][att++] = -1;
                    else
                        rawDataSpace[maxData][att++] = Double.parseDouble(storeName);
                    continue;
                }
                //  System.out.println("storeName"+storeName);
                dat = getAttCode(storeName, k);
                // System.out.println(dat);
                if (dat == -1) {
                    for (i = k + 1; i < maxAtt + 1; i++) {
                        readName();
                        count++;
                    }
                    att = 0;
                    k = -1;
                    maxData = -1;
                    continue;
                }
                rawDataSpace[maxData][att++] = dat;
                //  System.out.println("rawDataSpace for att: "+rawDataSpace[maxData][att-1]);
            }
            //Thuc delete maxData++ Why needs maxData++?
            //maxData++;
            //System.out.println(maxData);
            scan.close();

            // This does not allow continuous attribute
            //Thuc print out rawDataSpace
            //    for(int m=0;m<maxData;m++){
            //        for(int n=0; n<realAtt+2;n++)
            //           System.out.println(rawDataSpace[m][n]);

            //   }
            convertDataSpace();
            //System.out.println("hushu");
            return 1;
        } catch (Exception e) {
            System.out.println("## ERROR - Rule - getData() - Error reading data from file :" + fn + ": \n\n## ERROR - Program will exit.\n");
            e.printStackTrace();
            System.exit(1);
            return (0);
        }


    }

    public int getNameCode(String nm) {
        int i;

        for (i = 0; i < maxClass; i++) {
            if (nm.compareTo(className[i]) == 0)
                return i;
        }
        return -1;
    }

    /* unknow code put in Item = 0 */
    public int getAttCode(String nm, int attnum) {
        AttributeCode tmp;

        tmp = attCode[attnum];
        while (tmp != null) {
            if (nm.compareTo(tmp.attr) == 0) {
                return tmp.itemID;
            }
            tmp = tmp.next;
        }
        return 0;
    }

    /* This is the discretion by the clustering */
    public int discretion() {
        double interval, minval, min, average_interval, mid, contrast, m1, m2;
        double[] datatmp, cut;
        ContinuousValue value, cur;
        int i, j, k, kk, num, n1, n2, flag, record = 0, realdata;
        String buffer = "", strtmp = "";

        datatmp = new double[maxData + 1];
        // value = AddContValue(NULL);

        for (i = 0; i < realAtt; i++) {
            // value = AddContValue(NULL);
            if (specialStatus[i] == "" + CONTINUOUS)
                continue;
            realdata = 0;
            value = addContValue(null);
            for (j = 0; j < maxData; j++) {
                if (rawDataSpace[j][i] == -1)
                    continue;
                datatmp[realdata++] = rawDataSpace[j][i];
            }
            quickSort(datatmp, realdata);
            value.center = datatmp[0];
            value.lower = datatmp[0];
            value.contrast = 0;
            cur = value;
            k = 0;
            num = 1;
            for (j = 1; j < realdata; j++) {
                if (datatmp[j] == datatmp[j - 1])
                    num++;
                else {
                    cur.upper = (datatmp[j] + datatmp[j - 1]) / 2;
                    cur.number = num;
                    cur.next = addContValue(cur);
                    cur = cur.next;
                    cur.center = datatmp[j];
                    cur.lower = (datatmp[j] + datatmp[j - 1]) / 2;
                    cur.contrast = 0;
                    k++;
                    num = 1;
                }
            }
            average_interval = (datatmp[realdata - 1] - datatmp[0]) / k;
            cur.upper = datatmp[realdata - 1] + average_interval / 2;
            cur.number = num;
            cur.next = null;

            while (true) {
                flag = 0;
                k = -1;
                cur = value;
                min = 9999999;
                while (cur.next != null) {
                    k++;
                    n1 = cur.number;
                    n2 = cur.next.number;
                    if (n1 > gMinSup && n2 > gMinSup) {
                        cur = cur.next;
                        continue;
                    }
                    m1 = cur.center;
                    m2 = cur.next.center;
                    if ((m2 - m1) > (3 * average_interval)) {
                        cur = cur.next;
                        continue;
                    }
                    if (cur.contrast > 0.00001 && cur.next.contrast > 0.00001) {
                        if (cur.contrast < min)
                            min = cur.contrast;
                        record = k;
                        flag++;
                        cur = cur.next;
                        continue;
                    }
                    contrast = (Math.abs(m2 - m1)) * n1 * n2 / (n1 + n2);
                    // contrast = (m1-m2)*n1*n2/(MaxData*MaxData);
                    if (contrast < min) {
                        min = contrast;
                        record = k;
                    }
                    flag++;
                    cur.contrast = contrast;
                    cur = cur.next;
                }

                if (flag == 0)
                    break;

                cur = value;
                for (j = 0; j < record; j++)
                    cur = cur.next;
                n1 = cur.number;
                n2 = cur.next.number;
                m1 = cur.center;
                m2 = cur.next.center;
                mid = ((double) n1 * m1 + n2 * m2) / (n1 + n2);
                // mid = ((float)n1+n2)/2;
                cur.number = n1 + n2;
                cur.upper = cur.next.upper;
                cur.center = mid;
                deleteContValue(cur.next);
            }

            cur = value;
            // //// datatmp[0] is for the lowest data in the attribute
            kk = 1;
            record = maxItem;
            while (cur != null) {
                datatmp[kk++] = cur.upper;
                sprintf(buffer, "[%.2f, %.2f]", cur.lower, cur.upper);
                if (maxItem >= item_Id_Ceiling - 1) {
                    item_Id_Ceiling += 50;
                    itemRecord = new ItemRecord[item_Id_Ceiling];
                }
                itemRecord[maxItem++] = addItemRecord(attName[i], buffer);
                cur = cur.next;
            }
            // ////datatmp[kk] is for largest vakue of attribute
            datatmp[kk++] = datatmp[maxData - 1];

            for (j = 0; j < maxData; j++) {
                if (rawDataSpace[j][i] == -1) {
                    rawDataSpace[j][i] = 0;
                    continue;
                }
                for (k = 0; k < kk; k++)
                    if (rawDataSpace[j][i] >= datatmp[k] && rawDataSpace[j][i] < datatmp[k + 1]) {
                        rawDataSpace[j][i] = record + k;
                        break;
                    }
            }

            cur = value;
            while (cur != null)
                cur = deleteContValue(cur);

        }
        free(datatmp);
        convertDataSpace();
        return (1);
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
                if (data[j] < data[k])
                    k = j;
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
        if (ahead != null)
            ahead.next = next;
        if (next != null)
            next.ahead = ahead;
        free(ptr);
        return next;
    }

	/*
	 * // This old function is for convert dataspace after discretization // It
	 * cannot ussed in a data base there is missing value int ConvertDataSpace()
	 * { int i, j, k, temp[1000], index[1000], tmp;
	 *
	 * DataSpace = (int **) calloc(MaxData, sizeof(int*)); if(!RawDataSpace)
	 * ErrorOut(1);
	 *
	 * /// To sort the cord in the order from small to big /// the index the
	 * sequence for geting the code
	 *
	 * for(i=0; i<RealAtt; i++) temp[i] = RawDataSpace[0][i]; for(i=0; i<1000;
	 * i++) index[i] = i;
	 *
	 * for(i=0; i<RealAtt; i++){ k=i; for(j=i+1; j<RealAtt; j++){
	 * if(temp[j]<temp[k]) k=j; } if(k!=i){ // swap tmp = temp[k]; temp[k] =
	 * temp[i]; temp[i] = tmp; tmp = index[k]; index[k] = index[i]; index[i] =
	 * tmp; } }
	 *
	 *
	 * for(i=0; i<MaxData; i++){ DataSpace[i] = (int *)calloc(RealAtt+1,
	 * sizeof(int)); if(!DataSpace[i]) ErrorOut (1); for(j=0; j<RealAtt+1; j++)
	 * DataSpace[i][j] = (int)RawDataSpace[i][index[j]]; free(RawDataSpace[i]);
	 * } free(RawDataSpace); return(1); }
	 */

    public int convertDataSpace() {
        int i, j, k, tmp;
        int[] temp = new int[1000];
        int[] index = new int[1000];

        dataSpace = new int[maxData][];

        for (i = 0; i < maxData; i++) {
            dataSpace[i] = new int[realAtt + 1];
            for (j = 0; j < realAtt + 1; j++)
                dataSpace[i][j] = (int) rawDataSpace[i][j];
            free(rawDataSpace[i]);
        }
        free(rawDataSpace);
        return (1);
    }

    public void printf(String s) {
        System.out.format(s);
    }

    public void printf(String s, Object... a) {
        System.out.format(s, a);
    }


    public void sprintf(String s, String t, Object a, Object b) {
        System.out.format(s + t, a, b);
    }

    public void sprintf(String s, String t, Object a, Object b, Object c) {
        System.out.format(s + t, a, b, c);
    }

    public void fprintf(StringBuilder sb, String s, Object... a) {
        sb.append(String.format(s, a));
    }

    public void fprintf(BufferedWriter out, String s, Object... a) {
        String output = String.format(s, a);
        try {
            out.write(output);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void free(double d) {
        d = 0;
    }

    public void free(Object o) {
        o = null;
    }

    public String globalInfo() {
        String ret = "";
        ret += "This is a causal association rule discovery tool.\n\t This program was authored Prof. Jiuyong Li (www.unisanet.unisa.edu.au/staff/homepage.asp?name=jiuyong.li). \n\t Contact jiuyong@unisa.edu.au to obtain a manual \n";
        ret += "\n Simple usage:\n";

        ret += "\t ./rule -f fileName (without extension) \n" + "\t -s Local Support (default 0.05) \n	" + " -l maximum length of rules (default 4)  \n " + "\t -r 1 redundant rules (default no)  \n " + "\t -m 1 find subrules for some attribute-value pairs (default no) \n"
                + "\t This program focuses only on the first class in two-class data.  \n " + "\t Please put the focused class first  \n" + "\t the automatic report is in fileName.report \n \n";
        return ret;
    }


    ////
    public void initialHash() {
        z = new long[maxItem];
        zz = (long) (r.nextDouble() * zzrange);
        double rand = r.nextDouble();
        for (int i = 0; i < z.length; i++) {
            z[i] = (long) (rand * zrange);
            rand += r.nextDouble();
        }
        Arrays.sort(z);
    }

    public int hash(int[] keys) {
        long[] h = new long[keys.length];
        for (int i = 0; i < keys.length; i++)
            h[i] = keys[i] & ((1L << 32) - 1);

        long hashvalue = 0;
        for (int i = 0; i < keys.length; i++)
            hashvalue += z[i] * h[i];
        hashvalue = (hashvalue * zz) >>> 32;

//		System.out.println("hashvalue: " + hashvalue);

        return (int) (hashvalue);
    }
    ////

}