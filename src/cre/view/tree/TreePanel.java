package cre.view.tree;

import cre.view.ResizablePanel;
import cre.view.tree.LineBreakerTool.TextLayoutAndContent;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Created by HanYizhao on 2017/7/3.
 */
public class TreePanel extends ResizablePanel {

    private Node root;

    private static Font font = new Font(null, Font.BOLD, 12);
    private static Font contentFont = new Font(null, Font.PLAIN, 10);
    private static Font conditionFont = new Font(null, Font.PLAIN, 11);
    private static Color borderColor = Color.black;
    private static Color fontColor = Color.black;
    private static Color contentFontColor = Color.black;
    private static Color conditionFontColor = Color.blue;
    private float allWidth;
    private float allHeight;

    public TreePanel(Node root) throws Exception {
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
                        double tolerance = 1.0e-10;
                        condition = condition.trim();
                        FontMetrics fm = g2.getFontMetrics(conditionFont);
                        float leading = (float) (fm.getAscent() + fm.getDescent()) / 4;
                        float stringWidth = fm.stringWidth(condition) + 2 * leading;
                        float stringHeight = leading + fm.getAscent() + fm.getDescent() + leading;
                        Point2D.Float center = new Point2D.Float((start.x + end.x) / 2, (start.y + end.y) / 2);
                        Rectangle2D.Float rectangle = new Rectangle2D.Float(center.x - stringWidth / 2,
                                center.y - stringHeight / 2, stringWidth, stringHeight);
                        Vector2D end1 = getLineIntersectRectangle(rectangle, new Point2D.Float(start.x, start.y),
                                new Point2D.Float(center.x, center.y), tolerance);
                        if (end1 != null) {
                            g2.setColor(borderColor);
                            g2.draw(new Line2D.Float(start.x, start.y, (float) end1.getX(), (float) end1.getY()));
                        }
                        end1 = getLineIntersectRectangle(rectangle, new Point2D.Float(end.x, end.y),
                                new Point2D.Float(center.x, center.y), tolerance);
                        if (end1 != null) {
                            g2.setColor(borderColor);
                            g2.draw(new Line2D.Float((float) end1.getX(), (float) end1.getY(), end.x, end.y));
                        }

                        g2.setFont(conditionFont);
                        g2.setColor(conditionFontColor);
                        g2.drawString(condition, rectangle.x + leading, rectangle.y + leading + fm.getAscent());
                    }
                    paintNode(ch, g2);
                }
            }
        }
    }

    private Vector2D getLineIntersectRectangle(Rectangle2D rectangle,
                                               Point2D start, Point2D end, double tolerance) {
        Line line = new Line(new Vector2D(start.getX(), start.getY()), new Vector2D(end.getX(), end.getY()), tolerance);
        double leftTopX = rectangle.getX(), leftTopY = rectangle.getY();
        double leftBottomX = rectangle.getX(), leftBottomY = rectangle.getY() + rectangle.getHeight();
        double rightTopX = rectangle.getX() + rectangle.getWidth(), rightTopY = rectangle.getY();
        double rightBottomX = rectangle.getX() + rectangle.getWidth(),
                rightBottomY = rectangle.getY() + rectangle.getHeight();
        double[][] lines = new double[4][4];
        lines[0][0] = leftTopX;
        lines[0][1] = leftTopY;
        lines[0][2] = rightTopX;
        lines[0][3] = rightTopY;

        lines[1][0] = leftBottomX;
        lines[1][1] = leftBottomY;
        lines[1][2] = rightBottomX;
        lines[1][3] = rightBottomY;

        lines[2][0] = leftTopX;
        lines[2][1] = leftTopY;
        lines[2][2] = leftBottomX;
        lines[2][3] = leftBottomY;

        lines[3][0] = rightTopX;
        lines[3][1] = rightTopY;
        lines[3][2] = rightBottomX;
        lines[3][3] = rightBottomY;
        Line another = null;
        for (int i = 0; i < 4; i++) {
            if (Line2D.linesIntersect(lines[i][0], lines[i][1], lines[i][2], lines[i][3],
                    start.getX(), start.getY(), end.getX(), end.getY())) {
                another = new Line(new Vector2D(lines[i][0], lines[i][1]),
                        new Vector2D(lines[i][2], lines[i][3]), tolerance);
                break;
            }
        }
        if (another != null) {
            return line.intersection(another);
        }
        return null;
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
