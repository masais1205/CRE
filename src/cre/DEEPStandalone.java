package cre;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.github.jankroken.commandline.*;
import com.github.jankroken.commandline.annotations.*;
import cre.Config.OtherConfig;
import cre.algorithm.test.TestConfig;
import cre.algorithm.test.TestOldAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.ui.MainFrameEventHandler;


public class DEEPStandalone {
    private static String delimiter = ",";
    private TestConfig config;

    public static final void main(String[] args) {
        try {
            DEEPConfig dbConfig = CommandLineParser.parse(DEEPConfig.class, args,
                    OptionStyle.LONG_OR_COMPACT);

            String trainFile = dbConfig.getTrainFile();
            if (trainFile == null) {
                System.out.println("Config ERROR : NO train data provided!!!");
                throw new Exception();
            }
            String testFile = dbConfig.getTestFile();
            if (testFile == null) {
                System.out.println("Config ERROR : NO test data provided!!!");
                throw new Exception();
            }
            DEEPAttributes attr = dbConfig.getAttr();
            String w = attr.getW();
            String y = attr.getY();
            String gt = attr.getGt();

            try {
                BufferedReader trainBR = new BufferedReader(new FileReader(trainFile));
                String trainHeader = trainBR.readLine();
                String[] trainNames = trainHeader.split(delimiter);
                BufferedReader testBR = new BufferedReader(new FileReader(testFile));
                String testHeader = testBR.readLine();
                String[] testNames = testHeader.split(delimiter);

                // check if Attributes in train and test are consistent
                assert trainNames.length == testNames.length;
                for (String s : trainNames)
                    if (! Arrays.asList(testNames).contains(s)) {
                        System.out.println("ERROR : Attributes in train and test are not consistent!!!");
                        throw new Exception();
                    }
                for (String s : testNames)
                    if (! Arrays.asList(trainNames).contains(s)) {
                        System.out.println("ERROR : Attributes in train and test are not consistent!!!");
                        throw new Exception();
                    }

                if (! Arrays.asList(trainNames).contains(w)) {
                    System.out.println("Config ERROR : treatment variable was NOT found in train data!!!");
                    throw new Exception();
                }
                if (! Arrays.asList(trainNames).contains(y)) {
                    System.out.println("Config ERROR : outcome variable was NOT found in train data!!!");
                    throw new Exception();
                }

                int WP = indexOf(trainNames, w);
                int YP = indexOf(trainNames, y);
                int GT = indexOf(trainNames, gt);
                int[] XPArray = new int[trainNames.length];
                int XPArrayLength = 0;
                for (int idx=0; idx<trainNames.length; idx++)
                    if (idx!=WP && idx!=YP &&idx!=GT){
                        XPArray[XPArrayLength] = idx;
                        XPArrayLength++;
                    }
                XPArray = trimLength(XPArray, XPArrayLength);

                TestConfig config = new TestConfig(trainFile, dbConfig.getFeatureSelection(), dbConfig.getSignificanceLevel());
                OtherConfig otherConfig = new OtherConfig(OtherConfig.Validation.SUPPLIED_TEST_DATA, 1, testFile, 50, 10, null);

                TestOldAlgorithm.do_it(trainFile,
                        config,
                        WP, YP, XPArray, GT, null, -1, otherConfig, -1, false);
                System.out.println("done_it");

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static int[] trimLength(int[] array, int len){
        int [] newArray = new int[len];
        for (int i=0; i<len; i++){
            newArray[i] = array[i];
        }
        return newArray;
    }

    public static int indexOf(String[] arr, String str) {
        for (int i=0; i<arr.length; i++) {
            if (arr[i].equals(str))
                return i;
        }
        return -1;
    }
}