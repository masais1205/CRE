package cre.ui;

import cre.algorithm.CanShowStatus;

import java.awt.*;
import java.io.File;

/**
 * Created by HanYizhao on 2017/4/21.
 * Provide limit functions. Avoid other access to MainFrame
 */
public interface MainFrameEventHandler {

    /**
     * This function is called when user choose a new File.
     *
     * @param file the new file
     */
    void selectANewFile(File file);

    /**
     * In order to show a dialog, we need to supply a owner. The owner is the MainFrame.
     *
     * @return a Frame (Mostly MainFrame)
     */
    Frame getFrame();

    /**
     * When a component of a panel wants to show status in the status bar.
     * It needs a provider who has ability to show status.
     *
     * @return a provider (Mostly MainFrame)
     */
    CanShowStatus getCanShowStatus();
}
