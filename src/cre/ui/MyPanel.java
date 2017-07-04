package cre.ui;

import javax.swing.*;

/**
 * Created by HanYizhao on 2017/4/24.
 */
public abstract class MyPanel extends JPanel {

    /**
     * The only thread to do calculation using specific algorithm.
     */
    protected Thread runningThread;

    public boolean canCloseSafely() {
        return runningThread == null;
    }

    public void doForceClose() {
        if (runningThread != null) {
            runningThread.stop();
        }
    }
}
