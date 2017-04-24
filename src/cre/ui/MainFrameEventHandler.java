package cre.ui;

import cre.algorithm.CanShowStatus;

import java.awt.*;
import java.io.File;

/**
 * Created by HanYizhao on 2017/4/21.
 */
public interface MainFrameEventHandler {
    void selectANewFile(File file);

    Frame getFrame();

    CanShowStatus getCanShowStatus();
}
