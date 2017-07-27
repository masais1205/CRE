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
 * <p>This class indicates some features what an algorithm should have.</p>
 * First of all, {@linkplain #AbstractAlgorithm(File)} is called to create an algorithm.
 * <p>
 * Then {@linkplain #init()} is called to init the algorithm. This function may throw exception and in that case,
 * this algorithm should not work because some errors occur.
 * <p>
 * When user wants to run this algorithm, {@linkplain #doAlgorithm(CanShowOutput, CanShowStatus, OtherConfig)} is called.
 * <p>
 * When user wants to stop the algorithm, {@linkplain #setShouldStop()} is called.
 * <p>
 * When user wants to run this algorithm, an cloned algorithm will do the specific work. So an algorithm must implement {@linkplain Cloneable}.
 * <p>
 * When user change the file, {@linkplain #getCloneBecauseChangeOfFile(File)} will be called.
 * <p>
 * In order to create the view of configuration, {@linkplain #getConfiguration()} will be called.
 * And when user modify the view, the object returned from {@linkplain #getConfiguration()} will be modified.
 */
public abstract class AbstractAlgorithm implements Cloneable {

    protected File filePath;

    public AbstractAlgorithm(File filePath) {
        this.filePath = filePath;
    }

    /**
     * If when init the algorithm, there may be some exceptions,
     * please implement this function and throw exception when this algorithm cannot handle this file.
     *
     * @throws Exception
     */
    public void init() throws Exception {
    }

    /**
     * Get name of this algorithm.
     *
     * @return name
     */
    public abstract String getName();

    /**
     * Get introduction. The returned value will be used to create documentation.
     *
     * @return
     */
    public abstract String getIntroduction();

    public abstract Cloneable getConfiguration();

    public abstract AbstractAlgorithm getCloneBecauseChangeOfFile(File newFile) throws Exception;

    public final List<ResizablePanel> doMyAlgorithm(CanShowOutput canShowOutput, CanShowStatus canShowStatus, OtherConfig otherConfig) {
        setShouldStop(false);
        return doAlgorithm(canShowOutput, canShowStatus, otherConfig);
    }

    /**
     * There will be a thread which is not main thread.
     * The thread will call this function.
     *
     * @param canShowOutput Used to show output.
     * @param canShowStatus Used to show some message in status bar.
     * @param otherConfig   Validation Options
     * @return Some figures or null
     */
    public abstract List<ResizablePanel> doAlgorithm(CanShowOutput canShowOutput, CanShowStatus canShowStatus, OtherConfig otherConfig);

    @Override
    public abstract Object clone() throws CloneNotSupportedException;

    @Override
    public String toString() {
        Cloneable config = getConfiguration();
        return this.getName() + " " + (config == null ? " no Config" : config);
    }


    protected boolean shouldStop = false;

    protected final synchronized void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }

    public final synchronized boolean isShouldStop() {
        return shouldStop;
    }

    public final synchronized void setShouldStop() {
        shouldStop = true;
    }
}
