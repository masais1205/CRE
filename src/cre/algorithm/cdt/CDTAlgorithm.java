package cre.algorithm.cdt;

import cre.algorithm.AbstractAlgorithm;

import javax.swing.*;

/**
 * Created by HanYizhao on 2017/4/7.
 */
public class CDTAlgorithm extends AbstractAlgorithm {

    CDTConfig config = new CDTConfig();
    Boolean shouldStop = false;

    @Override
    public String getName() {
        return "CDT";
    }

    @Override
    public String getIntroduction() {
        return "Class for generating a causal decision tree.";
    }

    @Override
    public Object getConfiguration() {
        return config;
    }

    @Override
    public void doAlgorithm(Object config, JTextArea outPutArea) {
        shouldStop = false;
    }

    @Override
    public void setShouldStop() {
        shouldStop = true;
    }
}
