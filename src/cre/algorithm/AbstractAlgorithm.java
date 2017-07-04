package cre.algorithm;

import cre.Config.OtherConfig;
import cre.view.ResizablePanel;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by HanYizhao on 2017/4/7.
 */
public abstract class AbstractAlgorithm implements Cloneable {

    protected File filePath;

    public AbstractAlgorithm(File filePath) {
        this.filePath = filePath;
    }

    public abstract String getName();

    public abstract String getIntroduction();

    public Collection<String> getSupportLowerFileExtension() {
        return new ArrayList<>();
    }

    public abstract Cloneable getConfiguration();

    public abstract AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile);

    public abstract List<ResizablePanel> doAlgorithm(CanShowOutput canShowOutput, CanShowStatus canShowStatus, OtherConfig otherConfig);

    public abstract void setShouldStop();

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        Cloneable config = getConfiguration();
        return this.getName() + " " + (config == null ? " no Config" : config);
    }
}
