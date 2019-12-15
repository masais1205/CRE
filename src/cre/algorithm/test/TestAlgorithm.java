package cre.algorithm.test;

import cre.Config.OtherConfig;
import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.StratifiedSampleHelper;
import cre.algorithm.tool.OtherTool;
import cre.view.ResizablePanel;

import java.io.File;
import java.util.*;

/**
 * Created by HanYizhao on 2017/4/24.
 * <p>There are more instructions in {@link AbstractAlgorithm}</p>
 */
public class TestAlgorithm extends AbstractAlgorithm {

    private TestConfig config;

    @Override
    public Object clone() {
        TestAlgorithm algorithm = new TestAlgorithm(super.filePath);
        if (this.config != null) {
            algorithm.config = (TestConfig) this.config.clone();
        }
        return algorithm;
    }

    public TestAlgorithm(File filePath) {
        super(filePath);
    }

    @Override
    public void init() throws Exception {
        if (!filePath.getAbsolutePath().toLowerCase().endsWith(".csv")) {
            throw new Exception("Current data file: " + filePath.getAbsolutePath() + "\n" + "For Test, only CSV format file is permitted.");
        }
        config = new TestConfig(filePath.getAbsolutePath());
        config.init();
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
    public AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile) throws Exception {
        TestAlgorithm a = new TestAlgorithm(newFile);
        a.init();
        a.config.setZC(this.config.getZC());
        a.config.setMergeDepth(this.config.getMergeDepth());
        a.config.setOddsRatio(this.config.getOddsRatio());
        return a;
    }

    @Override
    public List<ResizablePanel> doAlgorithm(CanShowOutput canShowOutput, CanShowStatus canShowStatus, OtherConfig otherConfig) {
        try {
            String lineSeparator = OtherTool.getLineSeparator();
            TreeMap<String, List<Integer>> configTreeMap = config.getType();
            String[] configAttributeNames = config.getTypeNames();
            String[] configAttributeClasses = config.getTypeClasses();
            if (configAttributeNames == null) {
                canShowOutput.showOutputString("Config ERROR: Need names of attributes");
                return null;
            }
            if (configTreeMap == null) {
                canShowOutput.showOutputString("Config ERROR : Need to Classify the Attributes");
                return null;
            }

            List<Integer> configTempList = configTreeMap.get(configAttributeClasses[0]);
            if (configTempList.size() != 1) {
                canShowOutput.showOutputString("Config ERROR : Need one attribute which is belong to W");
                return null;
            }
            int WP = configTempList.get(0);

            configTempList = configTreeMap.get(configAttributeClasses[2]);
            if (configTempList.size() != 1) {
                canShowOutput.showOutputString("Config ERROR : Need one attribute which is belong to Y");
                return null;
            }
            int YP = configTempList.get(0);
            int[] XPArray = OtherTool.fromIntegerListToArray(
                    configTreeMap.get(configAttributeClasses[1]));
            Arrays.sort(XPArray);

            String fileName = filePath.getAbsolutePath();
            {
                HashMap<String, String> map = new HashMap<>();
                for (String configAttributeClass : configAttributeClasses) {
                    List<Integer> l = config.getType().get(configAttributeClass);
                    for (int i : l) {
                        map.put(configAttributeNames[i], configAttributeClass);
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("File:\t").append(fileName)
                        .append(lineSeparator).append(lineSeparator)
                        .append("Attributes:").append(lineSeparator);
                for (String i : configAttributeNames) {
                    sb.append("\t");
                    sb.append(i);
                    sb.append("\t");
                    sb.append(map.get(i));
                    sb.append(lineSeparator);
                }
                canShowOutput.showOutputString(sb.toString());
            }
            canShowOutput.showOutputString("==== full training set ===");
            canShowStatus.showStatus("Building...");
            TestOldAlgorithm.do_it(fileName, otherConfig.getTestFile(),
                    config.getZC(), config.getOddsRatio(), config.getMergeDepth(),
                    WP, YP,
                    XPArray, null, -1, otherConfig,
                    canShowStatus, canShowOutput, false);


            List<Statistic> result = new ArrayList<>();
            switch (otherConfig.getValidation()) {
                case SUPPLIED_TEST_DATA: {
                    canShowOutput.showOutputString("\n========Supplied test data(test data: "
                            + otherConfig.getTestFile() + ")\n        repeat: "
                            + otherConfig.getValidationRepeatTimes() + "("
                            + 100/otherConfig.getValidationRepeatTimes() + "% of training data used " +
                            "for training each time)=======\n");

                    StratifiedSampleHelper helper = new StratifiedSampleHelper(fileName, ",", YP,
                            true, otherConfig.getValidationRepeatTimes(), configAttributeNames.length, canShowOutput);
                    for (int i = 0; i < otherConfig.getValidationRepeatTimes(); i++) {
                        if (isShouldStop()) {
                            result = null;
                            break;
                        }
                        canShowStatus.showStatus("times: " + (i + 1));
                        int[] group = helper.nextLines();
                        result.add(TestOldAlgorithm.do_it(fileName, otherConfig.getTestFile(),
                                config.getZC(), config.getOddsRatio(), config.getMergeDepth(),
                                WP, YP,
                                XPArray, group, 0, otherConfig,
                                canShowStatus, canShowOutput, true));
                    }
                }
                break;
                case VALIDATION: {
                    canShowOutput.showOutputString("\n========Validation(testing: "
                            + otherConfig.getTest() + "%, repeat: "
                            + otherConfig.getValidationRepeatTimes() + ")=======\n");

                    StratifiedSampleHelper helper = new StratifiedSampleHelper(fileName, ",", YP,
                            false, (double) otherConfig.getTest() / 100,
                            configAttributeNames.length, canShowOutput);
                    for (int i = 0; i < otherConfig.getValidationRepeatTimes(); i++) {
                        if (isShouldStop()) {
                            result = null;
                            break;
                        }
                        canShowStatus.showStatus("times: " + (i + 1));
                        int[] group = helper.nextLines();
                        result.add(TestOldAlgorithm.do_it(fileName, otherConfig.getTestFile(),
                                config.getZC(), config.getOddsRatio(), config.getMergeDepth(),
                                WP, YP,
                                XPArray, group, 0, otherConfig,
                                canShowStatus, canShowOutput, true));
                    }
                }
                break;
                case CROSS_VALIDATION: {
                    canShowOutput.showOutputString("\n========Cross Validation(folds: "
                            + otherConfig.getCrossValidationFolds() + ", repeat: "
                            + otherConfig.getValidationRepeatTimes() + ")=======\n");

                    int folds = otherConfig.getCrossValidationFolds();
                    int repeat = otherConfig.getValidationRepeatTimes();

                    StratifiedSampleHelper helper = new StratifiedSampleHelper(fileName, ",", YP,
                            true, folds, configAttributeNames.length, canShowOutput);

                    outer:
                    for (int i = 0; i < repeat; i++) {
                        int[] group = helper.nextLines();
                        for (int l = 0; l < folds; l++) {
                            if (isShouldStop()) {
                                result = null;
                                break outer;
                            }
                            canShowStatus.showStatus("times: " + (i + 1) + "; fold: " + (l + 1));
                            result.add(TestOldAlgorithm.do_it(fileName, otherConfig.getTestFile(),
                                    config.getZC(), config.getOddsRatio(), config.getMergeDepth(),
                                    WP, YP,
                                    XPArray, group, l, otherConfig,
                                    canShowStatus, canShowOutput, true));
                        }
                    }
                }
                break;
                case NONE:
                    result = null;
                    break;
            }
            if (result != null) {
                Statistic averageResult = Statistic.average(result);
                canShowOutput.showOutputString(averageResult.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            canShowOutput.showOutputString(e.getMessage());
        }
        return null;
    }
}
