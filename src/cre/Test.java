package cre;

import cre.algorithm.CalculatingException;
import cre.algorithm.cdt.CDTAlgorithm;
import cre.algorithm.cdt.CDTConfig;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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
            try {
                BufferedReader br = new BufferedReader(new FileReader("D:/ValveUnhandledExceptionFilter.txt"));
                throw new CalculatingException("1");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println(1);
            }
        }catch (Exception e2){
            e2.printStackTrace();
            System.out.println(2);
        }
    }
}
