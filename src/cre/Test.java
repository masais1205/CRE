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
        System.out.println(Line2D.linesIntersect(1, 1, 4, 1, 3, 0, 2, 0.5));
        Line lineA = new Line(new Vector2D(1, 1), new Vector2D(2, 1));
        Line lineB = new Line(new Vector2D(3, 0), new Vector2D(3, 1));
        Area a = new Area(new Line2D.Float(-1, -1, 2, 1));
        a.subtract(new Area(new Rectangle2D.Float(0, 1, 2, 1)));
        PathIterator i = a.getPathIterator(null);
        while (!i.isDone()) {
            double[] d = new double[6];
            System.out.print(i.currentSegment(d));
            System.out.println(Arrays.toString(d));
            i.next();
        }
        System.out.println(lineA.intersection(lineB));

    }
}
