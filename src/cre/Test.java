package cre;

import cre.algorithm.crcs.CRCSConfig;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import java.io.*;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class Test {
    public static void main(String[] args) {


        Percentile percentile = new Percentile();
        System.out.println(percentile.evaluate(new double[]{1, 2, 3, 4, 5, 6}, 50));

    }
}
