package cre;

import javax.swing.*;

/**
 * Created by HanYizhao on 2017/4/6.
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
        ConfigSample sample = new ConfigSample();
        sample.setAaaaaaaaaaaaaaaaaa(100);
        sample.setB(1.23);
        sample.setC(false);
        sample.setD("234");
        sample.setDd("history");
        try {
            ConfigSetter.show(null, sample);
        } catch (ConfigSetter.ConfigException e) {
            e.printStackTrace();
        }
        System.out.println("true");
    }
}
