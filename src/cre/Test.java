package cre;

import com.sun.deploy.panel.TreeRenderers;
import cre.view.tree.*;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.PolygonsSet;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
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
        //System.out.println(new Integer("5sfesfsf"));
        outer:
        for (int i = 0; i < 10; i++) {
            System.out.println("i:" + i);
            for (int l = 0; l < 10; l++) {
                if (l == 1 && i == 1) {
                    break outer;
                }
            }
            System.out.println("i:" + i);
        }

    }
}
