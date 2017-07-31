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
            throw new Exception("Please choose a names file");
        }
        config = new CRPAConfig(filePath);
        config.init();
    }

    @Override
    public String getName() {
        return "CR-PA";
    }

    @Override
    public String getIntroduction() {
        return "CR-PA is a causal association rule discovery tool.\nPaper: Discovery of Causal Rules Using Partial Association, (ICDM 2012).";
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
        CRPA crpa = new CRPA(3, input2, ret, p, v, canShowOutput);
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
