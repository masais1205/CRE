package cre.view.tree;

import cre.view.tree.LineBreakerTool.TextLayoutAndContent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by HanYizhao on 2017/6/22.
 * <p>This tool is used to calculate the details of a tree, like location and size of each node</p>
 */
public class TreeRefresher {

    private static class GetHeightResult {
        int depth;
        float height;
    }

    private static class T {
        Node node;
        int level;

        public T(Node node, int level) {
            this.node = node;
            this.level = level;
        }
    }

    private static void refreshNodeItself(Node node, TreeConfig config) {
        if (node.name == null || node.name.length() == 0) {
            node.name = " ";
        }
        float nameWidth = config.width - config.nameMargin.left - config.nameMargin.right;
        node.nameLayout = LineBreakerTool.getStringLayout(node.name, nameWidth, config.nameFont, null);
        node.namePoint = new ArrayList<>();
        float y = config.nameMargin.top;
        for (TextLayoutAndContent l : node.nameLayout) {
            y += l.getLayout().getAscent();
            node.namePoint.add(new Point2D.Float(((config.width - (float) l.getLayout().getBounds().getWidth()) / 2), y));
            y += l.getLayout().getDescent();
            if (l != node.nameLayout.get(node.nameLayout.size() - 1)) {
                y += l.getLayout().getLeading();
            }
        }
        y += config.nameMargin.bottom;
        if (node.content != null && node.content.length() > 0) {
            node.divisionLocation = y;
            float contentWidth = config.width - config.contentMargin.left - config.contentMargin.right;
            node.contentLayout = LineBreakerTool.getStringLayout(node.content, contentWidth, config.contentFont, null);
            node.contentPoint = new ArrayList<>();
            y += config.contentMargin.top;
            for (TextLayoutAndContent l : node.contentLayout) {
                y += l.getLayout().getAscent();
                node.contentPoint.add(new Point2D.Float(config.contentMargin.left, y));
                y += l.getLayout().getDescent();
                if (l != node.contentLayout.get(node.contentLayout.size() - 1)) {
                    y += l.getLayout().getLeading();
                }
            }
            y += config.contentMargin.bottom;
        }
        node.selfWidth = config.width;
        node.selfHeight = y;
        for (Children i : node.children) {
            refreshNodeItself(i.getValue(), config);
        }
    }

    private static GetHeightResult refreshTheHeight(Node root, TreeConfig config) {
        HashMap<Integer, Float> maxHeightOfEachLevel = new HashMap<>();
        Queue<T> queue = new LinkedList<>();
        queue.offer(new T(root, 0));
        while (!queue.isEmpty()) {
            T temp = queue.poll();
            float nowHeight = (float) temp.node.selfHeight;
            int nowLevel = temp.level;
            Float value = maxHeightOfEachLevel.get(nowLevel);
            if (value == null) {
                maxHeightOfEachLevel.put(nowLevel, nowHeight);
            } else {
                if (nowHeight > value) {
                    maxHeightOfEachLevel.put(nowLevel, nowHeight);
                }
            }
            for (Children i : temp.node.children) {
                queue.offer(new T(i.getValue(), nowLevel + 1));
            }
        }
        float[] xS = new float[maxHeightOfEachLevel.keySet().size()];
        xS[0] = 0;
        for (int i = 1; i < xS.length; i++) {
            xS[i] = xS[i - 1] + maxHeightOfEachLevel.get(i - 1) + config.layerSpace;
        }
        queue.offer(new T(root, 0));
        while (!queue.isEmpty()) {
            T temp = queue.poll();
            temp.node.selfY = xS[temp.level];
            for (Children i : temp.node.children) {
                queue.offer(new T(i.getValue(), temp.level + 1));
            }
        }
        GetHeightResult result = new GetHeightResult();
        result.depth = xS.length;
        result.height = xS[xS.length - 1] + maxHeightOfEachLevel.get(xS.length - 1);
        return result;
    }

    private static int moveTreeHorizontal(Node root, float space, int nowDepth) {
        Queue<T> queue = new LinkedList<>();
        int maxDepth = nowDepth;
        queue.offer(new T(root, nowDepth));
        while (!queue.isEmpty()) {
            T temp = queue.poll();
            if (temp.level > maxDepth) {
                maxDepth = temp.level;
            }
            temp.node.selfX += space;
            for (Children i : temp.node.children) {
                queue.offer(new T(i.getValue(), temp.level + 1));
            }
        }
        return maxDepth;
    }

    private static float checkXValue(Node node, Float[] maxRightNow, int nowDepth, float brotherSpace) {
        if (node.children.size() > 0) {
            float canMaxLeft = Float.MAX_VALUE;
            for (Children i : node.children) {
                float a = checkXValue(i.getValue(), maxRightNow, nowDepth + 1, brotherSpace);
                if (a < canMaxLeft) {
                    canMaxLeft = a;
                }
            }
            float xSum = 0;
            for (Children i : node.children) {
                xSum += i.getValue().selfX;
            }
            node.selfX = xSum / node.children.size();
            if (maxRightNow[nowDepth] == null) {
                maxRightNow[nowDepth] = node.selfX + node.selfWidth;
                return Float.MAX_VALUE;
            } else {
                float brotherSpaceNow = node.selfX - maxRightNow[nowDepth];
                maxRightNow[nowDepth] = node.selfX + node.selfWidth;
                float realMove = 0;
                if (brotherSpaceNow < brotherSpace) {
                    realMove = brotherSpace - brotherSpaceNow;
                } else {
                    float moveNow = Math.min(canMaxLeft, brotherSpaceNow - brotherSpace);
                    realMove = -moveNow;
                }
                if (realMove != 0) {
                    int maxDepth = moveTreeHorizontal(node, realMove, nowDepth);
                    for (int i = nowDepth; i <= maxDepth; i++) {
                        maxRightNow[i] += realMove;
                    }
                }
                return 0;
            }
        } else {
            if (maxRightNow[nowDepth] == null) {
                maxRightNow[nowDepth] = node.selfX + node.selfWidth;
                return Float.MAX_VALUE;
            } else {
                float brotherSpaceNow = node.selfX - maxRightNow[nowDepth];
                maxRightNow[nowDepth] = node.selfX + node.selfWidth;
                float realMove = brotherSpace - brotherSpaceNow;
                if (realMove != 0) {
                    node.selfX += realMove;
                    maxRightNow[nowDepth] += realMove;
                }
                return 0;
            }
        }

    }

    private static float refreshTheWidth(Node root, TreeConfig config, int depth) {
        Float[] maxRightNow = new Float[depth];
        checkXValue(root, maxRightNow, 0, config.brotherSpace);
        float max = maxRightNow[0];
        for (int i = 1; i < depth; i++) {
            if (maxRightNow[i] > max) {
                max = maxRightNow[i];
            }
        }
        return max;
    }

    /**
     * Calculate the details of a tree.
     *
     * @param root   Root of the tree.
     * @param config Configuration of the tree.
     * @return the size of this tree.
     */
    public static Dimension refreshTree(Node root, TreeConfig config) {
        Dimension d = new Dimension();
        // calculate size of each node
        refreshNodeItself(root, config);
        // calculate the maximal height of each level.
        GetHeightResult heightResult = refreshTheHeight(root, config);

        // calculate the horizontal position of each node.
        float width = refreshTheWidth(root, config, heightResult.depth);
        d.setSize(width, heightResult.height);
        System.out.println("refreshTree result size: " + d);
        return d;
    }


}
