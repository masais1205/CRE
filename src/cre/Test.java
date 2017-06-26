package cre;

import java.util.*;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class Test {
    public static void main(String[] args) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (ClassNotFoundException
//                | InstantiationException
//                | IllegalAccessException
//                | UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }
        ConfigSample configSample = new ConfigSample();
        try {
            ConfigSetter.show(null, configSample, "123", "456");
        } catch (ConfigSetter.ConfigException e) {
            e.printStackTrace();
        }

    }
}
