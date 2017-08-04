package cre;

import cre.ui.LogoFrame;
import cre.ui.MainFrame;
import cre.ui.Tool;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import java.awt.*;
import java.util.Map;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        for (Map.Entry<Object, Object> i : UIManager.getDefaults().entrySet()) {
            if (i.getKey() instanceof String) {
                {
                    Object key = i.getKey();
                    Object value = UIManager.get(key);
                    if (value != null) {
                        if (!(value instanceof Color) && !(value instanceof Font) && !(((String) key).contains("Icon"))) {
                            if (value instanceof InsetsUIResource) {
                                InsetsUIResource oldUi = (InsetsUIResource) value;
                                InsetsUIResource newUi = new InsetsUIResource(Tool.HighResolution(oldUi.top),
                                        Tool.HighResolution(oldUi.left),
                                        Tool.HighResolution(oldUi.bottom),
                                        Tool.HighResolution(oldUi.right));
                                UIManager.put(i.getKey(), newUi);
                            }
                        }
                    }
                }
                if (((String) i.getKey()).endsWith(".font")) {
                    Object value = UIManager.get(i.getKey());
                    if (value instanceof FontUIResource) {
                        FontUIResource fontValue = (FontUIResource) value;
                        if (fontValue.getSize() < 15) {
                            FontUIResource newFont = new FontUIResource(fontValue.getName(), fontValue.getStyle(), Tool.HighResolution(fontValue.getSize()));
                            UIManager.put(i.getKey(), newFont);
                        }
                    }
                }
            }
        }
        UIDefaults defaults = UIManager.getDefaults();
        defaults.remove("SplitPane.border");
        defaults.remove("ScrollPane.border");
        //defaults.remove("SplitPaneDivider.border");


        try {
            final LogoFrame logoFrame = new LogoFrame();
            logoFrame.setVisible(true);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            logoFrame.dispose();
                            MainFrame mainFrame = new MainFrame();
                            mainFrame.setVisible(true);
                        }
                    });
                }
            });
            t.start();

        } catch (HeadlessException e) {
            System.out.println("This environment does not support a keyboard, display or mouse");
        }
    }
}
