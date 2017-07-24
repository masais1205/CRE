package cre.view.tree;

import java.awt.*;

/**
 * Created by HanYizhao on 2017/6/30.
 * <p>The configuration of drawing a tree</p>
 */
public class TreeConfig {

    /**
     * The font of name and content
     */
    public Font nameFont, contentFont;
    /**
     * The width of a node.
     */
    public float width;
    /**
     * The margin of name and content
     */
    public Margin nameMargin, contentMargin;
    /**
     * The space between nodes in the same layer.
     * The space between the next two layers.
     */
    public float brotherSpace, layerSpace;

    public TreeConfig(Font nameFont, Font contentFont, float width,
                      Margin nameMargin, Margin contentMargin,
                      float brotherSpace, float layerSpace) {
        this.nameFont = nameFont;
        this.contentFont = contentFont;
        this.width = width;
        this.nameMargin = nameMargin;
        this.contentMargin = contentMargin;
        this.brotherSpace = brotherSpace;
        this.layerSpace = layerSpace;
    }
}
