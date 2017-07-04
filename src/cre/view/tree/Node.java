package cre.view.tree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import cre.view.tree.LineBreakerTool.*;

/**
 * Created by 16502 on 2017/6/22.
 */
public class Node {
    public String name;
    public String content;
    public Node parent;
    public List<Children> children;

    public Node(String name, String content, Node parent) {
        this.name = name;
        this.content = content;
        this.parent = parent;
        children = new ArrayList<>();
    }

    public float selfX;
    public float selfY;
    public float selfWidth;
    public float selfHeight;
    public List<TextLayoutAndContent> nameLayout;
    public List<TextLayoutAndContent> contentLayout;
    public List<Point2D.Float> namePoint;
    public List<Point2D.Float> contentPoint;
    public float divisionLocation;

    public boolean hasContent() {
        return contentLayout != null && contentPoint != null;
    }


}
