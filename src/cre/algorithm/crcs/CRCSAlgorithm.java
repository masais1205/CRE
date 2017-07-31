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
            throw new Exception("Please choose a names file");
        }
        config = new CRCSConfig(filePath);
        config.init();
    }

    @Override
    public String getName() {
        return "crcs";
    }

    @Override
    public String getIntroduction() {
        return "This is a causal association rule discovery tool.\n" +
                "This program was authored by Prof. Jiuyong Li (www.unisanet.unisa.edu.au/staff/homepage.asp?name=jiuyong.li).\n" +
                "Contact jiuyong@unisa.edu.au to obtain a manual";
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
        CRCS crcs = new CRCS(6, input2, ret, p, v, canShowOutput);

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
