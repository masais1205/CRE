package cre;

import cre.algorithm.crcs.CRCSConfig;

import java.io.File;
import java.io.FileNotFoundException;

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
        //System.out.println(new Integer("5sfesfsf"));

        try {
            CRCSConfig crcsConfig = new CRCSConfig(new File("C:/fffsfsesfs"));
            crcsConfig.init();
        } catch (FileNotFoundException e) {
            System.out.println("in");
            e.printStackTrace();
        } catch (Exception e){
            System.out.println("22n");
            e.printStackTrace();
        }

    }
}
