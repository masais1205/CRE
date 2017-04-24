package cre.ui;

import javax.swing.*;
import java.awt.*;

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
        try {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        } catch (HeadlessException e) {
            System.out.println("This environment does not support a keyboard, display or mouse");
        }
    }
}
