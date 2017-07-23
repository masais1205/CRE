package cre.algorithm.cdt;

import cre.algorithm.*;
import cre.algorithm.Validation.StratifiedSampleHelper;
import cre.Config.OtherConfig;
import cre.view.ResizablePanel;
import cre.view.tree.TreePanel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by HanYizhao on 2017/4/7.
 * <p>There are more instructions in {@link AbstractAlgorithm}</p>
 */
public class CDTAlgorithm extends AbstractAlgorithm {

    public CDTConfig config;
    private boolean shouldStop = false;

    public CDTAlgorithm(File filePath, CDTConfig oldConfig) {
        super(filePath);
        if (oldConfig == null) {
            config = new CDTConfig();
            config.setHeight(5);
            config.setPruned(true);
        } else {
            config = oldConfig;
        }
    }

    public CDTAlgorithm(File filePath) {
        this(filePath, null);
    }


    @Override
    public String getName() {
        return "CDT";
    }

    @Override
    public String getIntroduction() {
        return "NAME\n" +
                "cre.algorithm.CDT\n" +
                "\n" +
                "SYNOPSIS\n" +
                "Class for generating a causal decision tree. For more information, see\n" +
                "\n" +
                "J. Li, S. Ma, T. Le, L. Liu, J. Liu (2015). CDT: Programs for Causal Decision Tree (Coded by S. Ma). .\n";
    }

    @Override
    public Cloneable getConfiguration() {
        return config;
    }

    @Override
    public AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile) {
        return new CDTAlgorithm(newFile, this.config);
    }

    @Override
    public List<ResizablePanel> doAlgorithm(CanShowOutput canShowOutput, CanShowStatus canShowStatus, OtherConfig otherConfig) {
        setShouldStop(false);
        String fileName = filePath.getAbsolutePath();
        String[] attributes = null;
        int instancesCount = 0;
        canShowOutput.showOutputString("Scheme: " + config.toString());
        canShowOutput.showOutputString("File Name: " + fileName + "\n");
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
            String temp = br.readLine();
            attributes = temp.split(",");
            while ((temp = br.readLine()) != null) {
                if (temp.split(",").length == attributes.length) {
                    instancesCount++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        canShowOutput.showOutputString("Attributes:");
        for (String i : attributes) {
            canShowOutput.showOutputString("\t" + i);
        }

        canShowOutput.showOutputString("==== full training set ===");

        canShowStatus.showStatus("Building...");
        CDT nCDT = new CDT(config, fileName, canShowOutput, null,
                -1, null, null, false);
        try {
            nCDT.createDecisionTree();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        List<ResizablePanel> result = new ArrayList<>();
        try {
            if (nCDT.rootYizhao != null) {
                result.add(new TreePanel(nCDT.rootYizhao));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<CDTValidationStatistic> statistics = null;
        switch (otherConfig.getValidation()) {
            case CROSS_VALIDATION: {
                canShowOutput.showOutputString("\n========Cross Validation(folds: "
                        + otherConfig.getCrossValidationFolds() + ", repeat: "
                        + otherConfig.getValidationRepeatTimes() + ")=======\n");
                try {
                    int folds = otherConfig.getCrossValidationFolds();
                    int repeat = otherConfig.getValidationRepeatTimes();
                    StratifiedSampleHelper helper =
                            new StratifiedSampleHelper(filePath.getAbsolutePath(),
                                    ",", attributes.length - 1, true, folds, attributes.length, canShowOutput);
                    statistics = new ArrayList<>();
                    outer:
                    for (int i = 0; i < repeat; i++) {
                        List<String> real = new ArrayList<>();
                        List<String> test = new ArrayList<>();
                        int[] group = helper.nextLines();
                        for (int l = 0; l < folds; l++) {
                            if (isShouldStop()) {
                                statistics = null;
                                break outer;
                            }
                            canShowStatus.showStatus("times: " + (i + 1) + "; fold: " + (l + 1));
                            CDT tempCDT = new CDT(config, fileName, canShowOutput, group,
                                    l, real, test, true);
                            try {
                                tempCDT.createDecisionTree();
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                        CDTValidationStatistic statistic = new CDTValidationStatistic(real, test);
                        statistics.add(statistic);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    canShowOutput.showOutputString("ERROR: " + e.getMessage());
                }
            }
            break;
            case VALIDATION: {
                canShowOutput.showOutputString("\n========Validation(testing: "
                        + otherConfig.getTest() + "%, repeat: "
                        + otherConfig.getValidationRepeatTimes() + ")=======\n");
                try {
                    int testing = otherConfig.getTest();
                    int repeat = otherConfig.getValidationRepeatTimes();
                    StratifiedSampleHelper helper =
                            new StratifiedSampleHelper(filePath.getAbsolutePath(),
                                    ",", attributes.length - 1, false, (double) testing / 100, attributes.length, canShowOutput);
                    statistics = new ArrayList<>();
                    for (int i = 0; i < repeat; i++) {
                        if (isShouldStop()) {
                            statistics = null;
                            break;
                        }
                        canShowStatus.showStatus("times: " + (i + 1));
                        List<String> real = new ArrayList<>();
                        List<String> test = new ArrayList<>();

                        int[] group = helper.nextLines();
                        CDT tempCDT = new CDT(config, fileName, canShowOutput, group,
                                0, real, test, true);
                        try {
                            tempCDT.createDecisionTree();
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                        CDTValidationStatistic statistic = new CDTValidationStatistic(real, test);
                        statistics.add(statistic);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    canShowOutput.showOutputString("ERROR: " + e.getMessage());
                }
            }
            break;
        }
        if (statistics != null) {
            CDTValidationStatistic cdt = CDTValidationStatistic.average(statistics);
            canShowOutput.showOutputString(cdt.toString());
            canShowOutput.showOutputString("");
        }
        //new CDT(config, fileName, outPutArea);
        return result;
    }

    private synchronized void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }

    public synchronized boolean isShouldStop() {
        return shouldStop;
    }

    @Override
    public synchronized void setShouldStop() {
        shouldStop = true;
    }

    @Override
    public Object clone() {
        CDTAlgorithm algorithm = null;
        try {
            algorithm = (CDTAlgorithm) super.clone();
            if (this.config != null) {
                algorithm.config = (CDTConfig) this.config.clone();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return algorithm;
    }
}
