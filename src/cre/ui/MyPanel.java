package cre.ui;

import javax.swing.*;

/**
 * Created by HanYizhao on 2017/4/24.
 */
public abstract class MyPanel extends JPanel {
    public abstract boolean canCloseSafely();

    public abstract void doForceClose();
}
