package cre.view.tree;

import sun.reflect.generics.tree.Tree;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by 16502 on 2017/6/22.
 */
public class TreeRefresher {

    private Font nameFont, contentFont;
    private float minWidth, maxWidth;
    private float namePadding, contentPadding;
    private float brotherSpace, layerSpace;

    public TreeRefresher(Font nameFont, Font contentFont, float minWidth, float maxWidth,
                         float namePadding, float contentPadding, float brotherSpace, float layerSpace) {
        this.nameFont = nameFont;
        this.contentFont = contentFont;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
        this.namePadding = namePadding;
        this.contentPadding = contentPadding;
        this.brotherSpace = brotherSpace;
        this.layerSpace = layerSpace;
    }

    public Rectangle2D refresh(Node root) {

        return null;
    }

    private void calculateNodeSize(Node node) {
        
    }

    public Font getNameFont() {
        return nameFont;
    }

    public void setNameFont(Font nameFont) {
        this.nameFont = nameFont;
    }

    public Font getContentFont() {
        return contentFont;
    }

    public void setContentFont(Font contentFont) {
        this.contentFont = contentFont;
    }

    public float getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    public float getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public float getNamePadding() {
        return namePadding;
    }

    public void setNamePadding(float namePadding) {
        this.namePadding = namePadding;
    }

    public float getContentPadding() {
        return contentPadding;
    }

    public void setContentPadding(float contentPadding) {
        this.contentPadding = contentPadding;
    }

    public float getBrotherSpace() {
        return brotherSpace;
    }

    public void setBrotherSpace(float brotherSpace) {
        this.brotherSpace = brotherSpace;
    }

    public float getLayerSpace() {
        return layerSpace;
    }

    public void setLayerSpace(float layerSpace) {
        this.layerSpace = layerSpace;
    }
}
