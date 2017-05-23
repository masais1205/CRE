package cre.algorithm.test;

import cre.Config.OtherConfig;
import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.CrossValidation;
import cre.algorithm.CrossValidation.SliceLinesHelper;
import cre.algorithm.tool.OtherTool;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by HanYizhao on 2017/4/24.
 */
public class TestAlgorithm extends AbstractAlgorithm {

    private TestConfig config;

    @Override
    public Object clone() {
        TestAlgorithm algorithm = null;
        try {
            algorithm = (TestAlgorithm) super.clone();
            if (this.config != null) {
                algorithm.config = (TestConfig) this.config.clone();
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
    public AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile) {
        TestAlgorithm a = new TestAlgorithm(newFile);
        a.config.setZC(this.config.getZC());
        return a;
    }

    @Override
    public void doAlgorithm(CanShowOutput canShowOutput, CanShowStatus canShowStatus, OtherConfig otherConfig) {
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
            switch (otherConfig.getValidation()) {
                case VALIDATION: {
                    SliceLinesHelper helper = new SliceLinesHelper(fileName, ",", YP,
                            otherConfig.getTest(), configAttributeNames.length, canShowOutput);
                    for (int i = 0; i < otherConfig.getValidationRepeatTimes(); i++) {
                        canShowStatus.showStatus("Times " + i);
                        canShowOutput.showOutputString("\nTimes " + i);
                        int[] group = helper.nextLines(i);
                        TestOldAlgorithm.do_it(fileName,
                                config.getZC(), WP, YP,
                                XPArray, group, i, canShowStatus, canShowOutput);
                    }
                }
                break;
                case CROSS_VALIDATION: {
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
                }
                break;
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
