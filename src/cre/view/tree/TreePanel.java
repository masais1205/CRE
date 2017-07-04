package cre.view.tree;

import cre.view.ResizablePanel;
import cre.view.tree.LineBreakerTool.TextLayoutAndContent;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.List;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by HanYizhao on 2017/7/3.
 */
public class TreePanel extends ResizablePanel {

    private Node root;

    private static Font font = new Font("Serif", Font.PLAIN, 12);
    private static Font contentFont = new Font("Serif", Font.PLAIN, 10);
    private static Font conditionFont = new Font("Serif", Font.PLAIN, 11);
    private static Color borderColor = Color.black;
    private static Color fontColor = Color.black;
    private static Color contentFontColor = Color.black;
    private static Color conditionFontColor = Color.blue;
    private float allWidth;
    private float allHeight;

    public TreePanel(Node root) {
        this.root = root;
        Dimension d = TreeRefresher.refreshTree(root, new TreeConfig(font, contentFont, 100,
                new Margin(5, 10, 5, 10),
                new Margin(5, 20, 5, 20), 50, 50));
        allWidth = (float) d.getWidth();
        allHeight = (float) d.getHeight();
    }

    @Override
    protected double getOriginalWidth() {
        return allWidth;
    }

    @Override
    protected double getOriginalHeight() {
        return allHeight;
    }

    private void paintText(Graphics2D g2, List<TextLayoutAndContent> text, List<Point2D.Float> points) {
        if (text != null && points != null && text.size() == points.size()) {
            for (int i = 0; i < text.size(); i++) {
                TextLayoutAndContent temp = text.get(i);
                Point2D.Float point = points.get(i);
                temp.getLayout().draw(g2, point.x, point.y);
            }
        }
    }

    private void paintNode(Node node, Graphics2D g2) {
        if (node != null) {
            //paint node
            g2.setColor(borderColor);
            AffineTransform old = g2.getTransform();
            g2.translate(node.selfX, node.selfY);
            g2.draw(new Rectangle2D.Float(0, 0, node.selfWidth, node.selfHeight));
            if (node.hasContent()) {
                g2.draw(new Line2D.Float(0, node.divisionLocation,
                        node.selfWidth, node.divisionLocation));
            }
            g2.setColor(fontColor);
            paintText(g2, node.nameLayout, node.namePoint);
            if (node.hasContent()) {
                g2.setColor(contentFontColor);
                paintText(g2, node.contentLayout, node.contentPoint);
            }
            g2.setTransform(old);
            //paint condition
            if (node.children != null && node.children.size() != 0) {
                for (Children i : node.children) {
                    Node ch = i.getValue();
                    String condition = i.getEdge();
                    Point2D.Float start, end;
                    start = new Point2D.Float(node.selfX + node.selfWidth / 2,
                            node.selfY + node.selfHeight);
                    end = new Point2D.Float(ch.selfX + ch.selfWidth / 2, ch.selfY);
                    if (condition == null || condition.trim().length() == 0) {
                        g2.setColor(borderColor);
                        g2.draw(new Line2D.Float(start, end));
                    } else {
                        condition = condition.trim();
                        FontMetrics fm = g2.getFontMetrics(conditionFont);
                        float stringWidth = fm.stringWidth(condition);
                        float stringHeight = fm.getLeading() + fm.getAscent() + fm.getDescent() + fm.getLeading();
                        Point2D.Float center = new Point2D.Float((start.x + end.x) / 2, (start.y + end.y) / 2);
                        Rectangle2D.Float rectangle = new Rectangle2D.Float(center.x - stringWidth / 2,
                                center.y - stringHeight / 2, stringWidth, stringHeight);
                        Vector2D end1 = new Line(new Vector2D(rectangle.x, rectangle.y),
                                new Vector2D(rectangle.x + rectangle.width, rectangle.y), 1.0e-10)
                                .intersection(new Line(new Vector2D(center.x, center.y),
                                        new Vector2D(start.x, start.y), 1.0e-10));
                        if (end1 != null) {
                            g2.setColor(borderColor);
                            g2.draw(new Line2D.Float(start.x, start.y, (float) end1.getX(), (float) end1.getY()));
                        }
                        end1 = new Line(new Vector2D(rectangle.x, rectangle.y + rectangle.height),
                                new Vector2D(rectangle.x + rectangle.width, rectangle.y + rectangle.height), 1.0e-10)
                                .intersection(new Line(new Vector2D(center.x, center.y),
                                        new Vector2D(end.x, end.y), 1.0e-10));
                        if (end1 != null) {
                            g2.setColor(borderColor);
                            g2.draw(new Line2D.Float((float) end1.getX(), (float) end1.getY(), end.x, end.y));
                        }
                        g2.setFont(conditionFont);
                        g2.setColor(conditionFontColor);
                        g2.drawString(condition, rectangle.x, rectangle.y + fm.getLeading() + fm.getAscent());
                    }
                    paintNode(ch, g2);
                }
            }
        }
    }

    @Override
    protected void doMyPaint(Graphics2D g2, int left, int top, double scale) {
        AffineTransform oldTransform = g2.getTransform();
        g2.translate(left, top);
        g2.scale(scale, scale);
        paintNode(root, g2);
        g2.setTransform(oldTransform);
    }
}
