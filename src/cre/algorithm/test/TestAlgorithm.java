package cre.algorithm.test;

import cre.Config.OtherConfig;
import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.CrossValidation;
import cre.algorithm.tool.OtherTool;
import sun.security.x509.OtherName;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by HanYizhao on 2017/4/24.
 */
public class TestAlgorithm extends AbstractAlgorithm {

    private TestConfig config;
    private OtherConfig otherConfig;

    @Override
    public Object clone() {
        TestAlgorithm algorithm = null;
        try {
            algorithm = (TestAlgorithm) super.clone();
            if (this.config != null) {
                algorithm.config = (TestConfig) this.config.clone();
            }
            if (this.otherConfig != null) {
                algorithm.otherConfig = (OtherConfig) this.otherConfig.clone();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return algorithm;
    }

    public TestAlgorithm(File filePath) {
        super(filePath);
        config = new TestConfig(filePath.getAbsolutePath());
        config.setZC(1.28);
    }

    @Override
    public String getName() {
        return "Test";
    }

    @Override
    public String getIntroduction() {
        return "Test Introduction";
    }

    @Override
    public Cloneable getConfiguration() {
        return config;
    }

    @Override
    public OtherConfig getOtherConfiguration() {
        return otherConfig;
    }

    @Override
    public AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile) {
        TestAlgorithm a = new TestAlgorithm(newFile);
        a.config.setZC(this.config.getZC());
        return a;
    }

    @Override
    public void doAlgorithm(CanShowOutput canShowOutput, CanShowStatus canShowStatus, OtherConfig otherConfig) {
        this.otherConfig = otherConfig;
        try {
            TreeMap<String, List<Integer>> configTreeMap = config.getType();
            String[] configAttributeNames = config.getTypeNames();
            String[] configAttributeClasses = config.getTypeClasses();
            if (configAttributeNames == null) {
                canShowOutput.showOutputString("Config ERROR: Need names of attributes");
                return;
            }
            if (configTreeMap == null) {
                canShowOutput.showOutputString("Config ERROR : Need to Classify the Attributes");
                return;
            }

            List<Integer> configTempList = configTreeMap.get(configAttributeClasses[0]);
            if (configTempList.size() != 1) {
                canShowOutput.showOutputString("Config ERROR : Need one attribute which is belong to W");
                return;
            }
            int WP = configTempList.get(0);

            configTempList = configTreeMap.get(configAttributeClasses[2]);
            if (configTempList.size() != 1) {
                canShowOutput.showOutputString("Config ERROR : Need one attribute which is belong to Y");
                return;
            }
            int YP = configTempList.get(0);
            int[] XPArray = OtherTool.fromIntegerListToArray(
                    configTreeMap.get(configAttributeClasses[1]));
            Arrays.sort(XPArray);

            String fileName = filePath.getAbsolutePath();
            int[] crossValidationGroup = CrossValidation.sliceLines(fileName,
                    ",", YP, otherConfig.getCrossValidationFolds(),
                    configAttributeNames.length, canShowOutput);

            // check each fold
            for (int i = 0; i < otherConfig.getCrossValidationFolds(); i++) {
                canShowStatus.showStatus("Fold " + i);
                canShowOutput.showOutputString("\nFold " + i);
                TestOldAlgorithm.do_it(fileName,
                        config.getZC(), WP, YP,
                        XPArray, crossValidationGroup, i, canShowStatus, canShowOutput);
            }
        } catch (Exception e) {
            e.printStackTrace();
            canShowOutput.showOutputString(e.getMessage());
        }

    }

    @Override
    public void setShouldStop() {

    }
}
