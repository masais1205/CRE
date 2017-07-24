package cre.view.tree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import cre.view.tree.LineBreakerTool.*;

/**
 * Created by HanYizhao on 2017/6/22.
 * <p>
 * A tree node is constitute of border, name and content.
 * The content is just an option.
 * If there is content, there will be a separator between name and content.
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

    /**
     * The location and size of this node.
     */
    public float selfX, selfY, selfWidth, selfHeight;
    /**
     * How to draw name.
     */
    public List<TextLayoutAndContent> nameLayout;
    /**
     * How to draw content.
     */
    public List<TextLayoutAndContent> contentLayout;
    /**
     * The relative location of name.
     */
    public List<Point2D.Float> namePoint;
    /**
     * The relative location of content.
     */
    public List<Point2D.Float> contentPoint;
    /**
     * The location of separator.
     */
    public float divisionLocation;

    public boolean hasContent() {
        return contentLayout != null && contentPoint != null;
    }


}
