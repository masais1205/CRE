package cre.view.tree;

import java.awt.*;

/**
 * Created by HanYizhao on 2017/6/30.
 */
public class TreeConfig {
    public Font nameFont, contentFont;
    public float width;
    public Margin nameMargin, contentMargin;
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
