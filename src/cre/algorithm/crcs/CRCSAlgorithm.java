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
        config = new CRCSConfig(filePath);
        config.init();
    }

    @Override
    public String getName() {
        return "crcs";
    }

    @Override
    public String getIntroduction() {
        return "crcs...";
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
        a.config.setRecommend(this.config.getMaxRules());
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
            e.printStackTrace();
        }
        ret = ppl.getData();

        String wholeInput;
        if ((p.oddsratio == 1) && (p.ChisquareValue == 0)) {
            wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-h" + " " + "-z";
            //wholeInput = "-f" + " " + ret.fileName + " " + "-x";

        } else if ((p.oddsratio != 1) && (p.oddsratio != 0)) {
            wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-h" + " " + "-t" + " " + "-z";
            //wholeInput = "-f" + " " + ret.fileName + " " + "-x"+" "+"-h"+" "+"-t";
        } else {
            wholeInput = "-f" + " " + ret.fileName + " " + "-x" + " " + "-h" + " " + "-z" + " " + "-b";
            //wholeInput = "-f" + " " + ret.fileName + " " + "-x"+" "+"-h"+" "+"-b";
        }
        char[] input2 = wholeInput.toCharArray();
        CRCS crcs = new CRCS(6, input2, ret, p, v, canShowOutput);

        return null;
    }

    @Override
    public void setShouldStop() {

    }
}
