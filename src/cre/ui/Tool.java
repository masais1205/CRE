package cre.ui;

import cre.Config.ConfigBase;

import javax.swing.text.*;
import java.awt.*;
import java.util.Collection;

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

    public static Document getAlgorithmHelpDoc(String algorithmName,
                                               String algorithmIntroduction, Collection<ConfigBase> configBases) {
        Document helpDoc = new DefaultStyledDocument();
        MutableAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setFontSize(set, Tool.HighResolution(16));
        StyleConstants.setBold(set, true);
        try {
            helpDoc.insertString(helpDoc.getLength(), "\n" + algorithmName + "\n", set);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        StyleConstants.setFontSize(set, Tool.HighResolution(12));
        StyleConstants.setBold(set, false);
        try {
            if (algorithmIntroduction != null) {
                helpDoc.insertString(helpDoc.getLength(), "\n" + algorithmIntroduction + "\n", set);
            }
            if (configBases != null) {
                for (ConfigBase i : configBases) {
                    if (i.isVisible()) {
                        StyleConstants.setFontSize(set, Tool.HighResolution(14));
                        StyleConstants.setBold(set, true);
                        helpDoc.insertString(helpDoc.getLength(), "\n" + i.getShownName(), set);
                        StyleConstants.setFontSize(set, Tool.HighResolution(12));
                        StyleConstants.setBold(set, false);
                        helpDoc.insertString(helpDoc.getLength(), "    -    " + i.getComments() + "\n", set);
                    }
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return helpDoc;
    }

}
