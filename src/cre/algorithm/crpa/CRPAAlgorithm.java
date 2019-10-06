package cre.algorithm.crpa;

import cre.Config.OtherConfig;
import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.crcs.PreprocessingLogic;
import cre.view.ResizablePanel;

import java.io.File;
import java.util.List;

/**
 * Created by HanYizhao on 2017/7/14.
 * <p>There are more instructions in {@link AbstractAlgorithm}</p>
 */
public class CRPAAlgorithm extends AbstractAlgorithm {

    private CRPAConfig config;

    public CRPAAlgorithm(File filePath) {
        super(filePath);
    }

    @Override
    public void init() throws Exception {
        if (!filePath.getAbsolutePath().endsWith(".names")) {
            throw new Exception("Current data file: " + filePath.getAbsolutePath() + "\n" + "For CR-PA, only C4.5 format file is permitted.");
        }
        config = new CRPAConfig(filePath);
        config.init();
    }

    @Override
    public String getName() {
        return "CR-PA (Causal Rule-Partial Association)";
    }

    @Override
    public String getIntroduction() {
        return "A tool for discovering causal rules base on partial associations.\n\nReferences\n" +
                "[1] Zhou Jin, Jiuyong Li, Lin Liu, Thuc Le, Bingyu Sun, Rujing Wang. " +
                "Discovery of Causal Rules Using Partial Association, " +
                "in IEEE 12th International Conference on Data Mining, IEEE Press, pp. 309-318.\n"
                + "[2] Jiuyong Li, Lin Liu, Thuc Le. Practical approaches to causal relationship exploration. Springer, 2015.\n\n";
    }

    @Override
    public Cloneable getConfiguration() {
        return config;
    }

    @Override
    public AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile) throws Exception {
        CRPAAlgorithm a = new CRPAAlgorithm(newFile);
        a.init();
        a.config.setOddsRatioOrChiSquare(this.config.getOddsRatioOrChiSquare());
        a.config.setMinSupport(this.config.getMinSupport());
        a.config.setMaxRules(this.config.getMaxRules());
        a.config.setRecommend(this.config.getRecommend());
        return a;
    }

    @Override
    public List<ResizablePanel> doAlgorithm(CanShowOutput canShowOutput, CanShowStatus canShowStatus, OtherConfig otherConfig) {
        CRPAConfig.P p = config.toP();
        CRPAConfig.Values v = config.toValues();
        PreprocessingLogic.retclass ret = config.ret;
        PreprocessingLogic ppl = new PreprocessingLogic();
        String nameFileContent = config.getNewNameFileContent();
        try {
            ppl.loadData(nameFileContent, new File(ret.fileName + ".data"));
        } catch (Exception e) {
            canShowOutput.showOutputString("ERROR. See log for more details");
            e.printStackTrace();
        }
        ret = ppl.getData();

        String wholeInput;
        //Yizhao Han modify. The old version is created by HuShu, -h means no Chi Square.
        if (p.oddsRatioOrChiSquare) {
            if (p.oddsratio == 1) {
                wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-h";
            } else {
                wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-t" + " " + "-h";
            }
        } else {
            wholeInput = "-f" + " " + ret.fileName + " " + "-x";
        }
        char[] input2 = wholeInput.toCharArray();

        try {
            CRPA crpa = new CRPA(3, input2, ret, p, v, canShowOutput);
        } catch (Exception e) {
            e.printStackTrace();
            canShowOutput.showOutputString("ERROR. See log for more details");
        }
        return null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CRPAAlgorithm algorithm = new CRPAAlgorithm(super.filePath);
        if (this.config != null) {
            algorithm.config = (CRPAConfig) this.config.clone();
        }
        return algorithm;
    }
}
