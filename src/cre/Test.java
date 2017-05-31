package cre;

import cre.algorithm.test.MathListCombination;

import javax.swing.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class Test {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        ConfigSample configSample = new ConfigSample();
        try {
            ConfigSetter.show(null, configSample);
        } catch (ConfigSetter.ConfigException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 7, 24);
        calendar.add(Calendar.WEEK_OF_YEAR, 48);
        System.out.println(calendar);
    }
}
