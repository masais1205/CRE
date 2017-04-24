package cre.algorithm.cdt;

import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;

import javax.swing.*;
import java.io.File;
import java.util.Collection;

/**
 * Created by HanYizhao on 2017/4/7.
 */
public class CDTAlgorithm extends AbstractAlgorithm {

    public CDTConfig config;
    public Boolean shouldStop = false;

    public CDTAlgorithm(File filePath, CDTConfig oldConfig) {
        super(filePath);
        if (oldConfig == null) {
            config = new CDTConfig();
            config.setHeight(5);
            config.setPruned(true);
            config.setTest_improve_PA(false);
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
        return "Class for generating a causal decision tree.";
    }

    @Override
    public Collection<String> getSupportLowerFileExtension() {
        Collection<String> s = super.getSupportLowerFileExtension();
        s.add("csv");
        return s;
    }

    @Override
    public Cloneable getConfiguration() {
        return config;
    }

    @Override
    public AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile) {
        CDTAlgorithm a = new CDTAlgorithm(newFile, this.config);
        return a;
    }

    @Override
    public void doAlgorithm(CanShowOutput outPutArea, CanShowStatus canShowStatus) {
        shouldStop = false;
        String fileName = filePath.getAbsolutePath();
        if (fileName.toLowerCase().endsWith(".csv")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        new CDT(config, fileName, outPutArea);
    }

    @Override
    public void setShouldStop() {
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
