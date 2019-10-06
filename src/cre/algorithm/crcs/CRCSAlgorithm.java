package cre.algorithm.crcs;

import cre.Config.OtherConfig;
import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.view.ResizablePanel;

import java.io.File;
import java.util.List;

/**
 * Created by HanYizhao on 2017/7/14.
 * <p>There are more instructions in {@link AbstractAlgorithm}</p>
 */
public class CRCSAlgorithm extends AbstractAlgorithm {

    private CRCSConfig config;

    public CRCSAlgorithm(File filePath) {
        super(filePath);
    }

    @Override
    public void init() throws Exception {

        if (!filePath.getAbsolutePath().endsWith(".names")) {
            throw new Exception("Current data file: " + filePath.getAbsolutePath() + "\n" + "For CR-CS, only C4.5 format file is permitted.");
        }
        config = new CRCSConfig(filePath);
        config.init();
    }

    @Override
    public String getName() {
        return "CR-CS (Causal Rule mining with Corhort Study)";
    }

    @Override
    public String getIntroduction() {
        return "A tool for finding causal rules based on corhort study and association rule mining.\n\nReferences\n" +
                "[1] Jiuyong Li, Thuc Duy Le, Lin Liu, Jixue Liu, Zhou Jin, Bingyu Sun, " +
                "Saisai Ma. From Observational Studies to Causal Rule Mining, " +
                "ACM Transactions on Intelligent Systems and Technology, 7 (2): Article 14.\n"
                + "[2] Jiuyong Li, Lin Liu, Thuc Le. Practical approaches to causal relationship exploration. Springer, 2015.\n\n";
    }

    @Override
    public Cloneable getConfiguration() {
        return config;
    }

    @Override
    public AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile) throws Exception {
        CRCSAlgorithm a = new CRCSAlgorithm(newFile);
        a.init();
        a.config.setOddsRatioOrChiSquare(this.config.getOddsRatioOrChiSquare());
        a.config.setMinSupport(this.config.getMinSupport());
        a.config.setMaxRules(this.config.getMaxRules());
        a.config.setRecommend(this.config.getRecommend());
        return a;
    }

    @Override
    public List<ResizablePanel> doAlgorithm(CanShowOutput canShowOutput, CanShowStatus canShowStatus, OtherConfig otherConfig) {
        CRCSConfig.P p = config.toP();
        CRCSConfig.Values v = config.toValues();
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
        if (p.oddsRatioOrChiSquare) {
            if (p.oddsratio == 1) {
                wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-h" + " " + "-z";
            } else {
                wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-h" + " " + "-t" + " " + "-z";
            }
        } else {
            wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-h" + " " + "-z" + " " + "-b";
        }
        char[] input2 = wholeInput.toCharArray();
        try {
            CRCS crcs = new CRCS(6, input2, ret, p, v, canShowOutput);
        } catch (Exception e) {
            e.printStackTrace();
            canShowOutput.showOutputString("ERROR. See log for more details");
        }

        return null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CRCSAlgorithm algorithm = new CRCSAlgorithm(super.filePath);
        if (this.config != null) {
            algorithm.config = (CRCSConfig) this.config.clone();
        }
        return algorithm;
    }
}
