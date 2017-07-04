package cre;

import com.sun.deploy.panel.TreeRenderers;
import cre.view.tree.*;

import java.awt.*;
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

        Font font = new Font("Serif", Font.PLAIN, 12);

        Node G = new Node("G", "FSEFESf,sfseFE,sfsefes,", null);
        Node H = new Node("H", null, null);
        Node E = new Node("E", null, null);
        Node A = new Node("A", null, null);
        Node F = new Node("F", null, null);
        Node C = new Node("C", null, null);
        Node D = new Node("D", null, null);
        Node B = new Node("B", null, null);
        G.parent = E;
        H.parent = E;
        E.children.add(new Children(G, ""));
        E.children.add(new Children(H, ""));
        E.parent = C;
        F.parent = C;
        C.children.add(new Children(E, ""));
        C.children.add(new Children(F, ""));
        C.parent = A;
        B.parent = A;
        D.parent = B;
        A.children.add(new Children(B, ""));
        A.children.add(new Children(C, ""));
        B.children.add(new Children(D, ""));
        TreeRefresher.refreshTree(A, new TreeConfig(font, font, 100,
                new Margin(10, 10, 10, 10),
                new Margin(20, 20, 20, 20), 50, 50));

        System.out.println();
    }
}
