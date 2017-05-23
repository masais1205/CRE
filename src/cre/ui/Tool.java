package cre.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by HanYizhao on 2017/4/7.
 */
public class Tool {
    public static void moveToCenter(Window window) {
        moveToCenter(window, false);
    }

    public static void moveToCenter(Window window, boolean force) {
        if (force) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            window.setLocation((d.width - window.getWidth()) / 2,
                    (d.height - window.getHeight()) / 2);
        } else {
            window.setLocationByPlatform(true);
        }
    }

    public static int HighResolution(int original) {
        double a = Toolkit.getDefaultToolkit().getScreenSize().getWidth() * original / 1366;
        return (int) Math.round(a);
    }
}
