package cre;

import cre.algorithm.CalculatingException;
import cre.algorithm.CrossValidation;
import cre.algorithm.cdt.CDTAlgorithm;
import cre.algorithm.cdt.CDTConfig;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        try {
            CrossValidation.SliceLinesHelper helper = new CrossValidation.SliceLinesHelper(
                    "D:\\Documents\\adultAllBinary_Simple_1.csv", ",", 13, 30, 14, null);
            for (int i = 0; i < 10; i++) {
                int[] aa = helper.nextLines(3);
                System.out.println(Arrays.toString(aa));
            }
        } catch (CalculatingException e) {
            e.printStackTrace();
        }
    }
}
