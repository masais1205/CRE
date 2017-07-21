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
 */
public class CRPAAlgorithm extends AbstractAlgorithm {

    private CRPAConfig config;

    public CRPAAlgorithm(File filePath) {
        super(filePath);
    }

    @Override
    public void init() throws Exception {
        config = new CRPAConfig(filePath);
        config.init();
    }

    @Override
    public String getName() {
        return "crpa";
    }

    @Override
    public String getIntroduction() {
        return "crpa...";
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
        a.config.setRecommend(this.config.getMaxRules());
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
        if (p.oddsratio == 1) {
            wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-h";
        } else if ((p.oddsratio != 1) && (p.oddsratio != 0)) {
            wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-t" + " " + "-h";
        } else {
            wholeInput = "-f" + " " + ret.fileName + " " + "-x";
        }
        char[] input2 = wholeInput.toCharArray();
        CRPA crpa = new CRPA(3, input2, ret, p, v, canShowOutput);
        return null;
    }

    @Override
    public void setShouldStop() {

    }
}
