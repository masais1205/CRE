package cre;

import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.cdt.CDT;
import cre.algorithm.cdt.CDTAlgorithm;
import cre.algorithm.cdt.CDTConfig;

import javax.swing.*;
import java.io.File;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class Test {
    public static void main(String[] args) {
        CDTConfig c = new CDTConfig();
        c.setHeight(5);
        System.out.println(c.clone());
        CDTAlgorithm a = new CDTAlgorithm(new File("C:/"));
        CDTAlgorithm b = (CDTAlgorithm) a.clone();
        a.setShouldStop();
        ((CDTConfig) a.getConfiguration()).setHeight(100);
        System.out.println(b.shouldStop);
        System.out.println(b.getConfiguration());
    }
}
