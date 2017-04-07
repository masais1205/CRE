package cre.algorithm;

import javax.swing.*;

/**
 * Created by HanYizhao on 2017/4/7.
 */
public abstract class AbstractAlgorithm {

    public abstract String getName();

    public abstract String getIntroduction();

    public abstract Object getConfiguration();

    public abstract void doAlgorithm(Object config, JTextArea outPutArea);

    public abstract void setShouldStop();

}
