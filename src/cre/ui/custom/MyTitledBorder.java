package cre.ui.custom;

import cre.ui.TextInformationFrame;
import cre.ui.Tool;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.net.URL;

/**
 * Created by HanYizhao on 2017/6/27.
 * <p>This TitleBorder will show a image on right of the title.</p>
 */
public class MyTitledBorder extends TitledBorder implements MouseListener, MouseMotionListener {

    private JLabel label;
    private URL image;
    private URL activeImage;
    private String toolTip;
    private String clickTitle;
    private Document clickMessage;
    private Rectangle imageLocation;
    private Component parent;
    private boolean nowMouseIsIn = false;

    /**
     * If call this function, there will be a image right of the title.
     *
     * @param image        the image
     * @param activeImage  the image when mouse is in.
     * @param toolTip      The message when mouse is in.
     * @param clickTitle   The title of shown dialog when user clicks image.
     * @param clickMessage The content of shown dialog when user clicks image.
     * @return this
     */
    public MyTitledBorder setOtherInfo(URL image, URL activeImage, String toolTip, String clickTitle, Document clickMessage) {
        this.image = image;
        this.activeImage = activeImage;
        this.toolTip = toolTip;
        this.clickTitle = clickTitle;
        this.clickMessage = clickMessage;
        return this;
    }

    /**
     * Yizhao Han
     *
     * @param c
     * @return
     */
    private Dimension getMyLabelDimension(Component c) {
        Font f = getFont(c);
        FontMetrics fontMetrics = c.getFontMetrics(f);
        int width = fontMetrics.getAscent() + fontMetrics.getDescent();
        return new Dimension(width, width);
    }

    /**
     * Yizhao Han
     *
     * @param c
     * @return
     */
    private JLabel getMyImageLabel(Component c, URL image) {
        Font f = getFont(c);
        JLabel mLabel = new JLabel();
        ImageIcon ico = new ImageIcon(image);
        FontMetrics fontMetrics = c.getFontMetrics(f);
        int width = fontMetrics.getAscent() + fontMetrics.getDescent();
        Image temp = ico.getImage().getScaledInstance(width, width, Image.SCALE_SMOOTH);
        ico = new ImageIcon(temp);
        mLabel.setIcon(ico);
        if (toolTip != null) {
            mLabel.setToolTipText(toolTip);
        }
        return mLabel;

    }


    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        this.parent = c;
        Border border = getBorder();
        String title = getTitle();
        if ((title != null) && !title.isEmpty()) {
            int edge = (border instanceof TitledBorder) ? 0 : EDGE_SPACING;
            JLabel label = getLabel(c);
            Dimension size = label.getPreferredSize();
            ////////////////////////////////////
            //Yizhao Han
            int differenceWidth = 0;
            int labelGap = Tool.HighResolution(4);
            if (image != null) {
                Dimension mSize = getMyLabelDimension(c);
                differenceWidth += labelGap + mSize.width;
                size.width += differenceWidth;
            }
            ///////////////////////////////////
            Insets insets = getBorderInsets(border, c, new Insets(0, 0, 0, 0));

            int borderX = x + edge;
            int borderY = y + edge;
            int borderW = width - edge - edge;
            int borderH = height - edge - edge;

            int labelY = y;
            int labelH = size.height;
            int position = getPosition();
            switch (position) {
                case ABOVE_TOP:
                    insets.left = 0;
                    insets.right = 0;
                    borderY += labelH - edge;
                    borderH -= labelH - edge;
                    break;
                case TOP:
                    insets.top = edge + insets.top / 2 - labelH / 2;
                    if (insets.top < edge) {
                        borderY -= insets.top;
                        borderH += insets.top;
                    } else {
                        labelY += insets.top;
                    }
                    break;
                case BELOW_TOP:
                    labelY += insets.top + edge;
                    break;
                case ABOVE_BOTTOM:
                    labelY += height - labelH - insets.bottom - edge;
                    break;
                case BOTTOM:
                    labelY += height - labelH;
                    insets.bottom = edge + (insets.bottom - labelH) / 2;
                    if (insets.bottom < edge) {
                        borderH += insets.bottom;
                    } else {
                        labelY -= insets.bottom;
                    }
                    break;
                case BELOW_BOTTOM:
                    insets.left = 0;
                    insets.right = 0;
                    labelY += height - labelH;
                    borderH -= labelH - edge;
                    break;
            }
            insets.left += edge + TEXT_INSET_H;
            insets.right += edge + TEXT_INSET_H;

            int labelX = x;
            int labelW = width - insets.left - insets.right;
            if (labelW > size.width) {
                labelW = size.width;
            }
            switch (getJustification(c)) {
                case LEFT:
                    labelX += insets.left;
                    break;
                case RIGHT:
                    labelX += width - insets.right - labelW;
                    break;
                case CENTER:
                    labelX += (width - labelW) / 2;
                    break;
            }

            if (border != null) {
                if ((position != TOP) && (position != BOTTOM)) {
                    border.paintBorder(c, g, borderX, borderY, borderW, borderH);
                } else {
                    Graphics g2 = g.create();
                    if (g2 instanceof Graphics2D) {
                        Graphics2D g2d = (Graphics2D) g2;
                        Path2D path = new Path2D.Float();
                        path.append(new Rectangle(borderX, borderY, borderW, labelY - borderY), false);
                        path.append(new Rectangle(borderX, labelY, labelX - borderX - TEXT_SPACING, labelH), false);
                        path.append(new Rectangle(labelX + labelW + TEXT_SPACING, labelY, borderX - labelX + borderW - labelW - TEXT_SPACING, labelH), false);
                        path.append(new Rectangle(borderX, labelY + labelH, borderW, borderY - labelY + borderH - labelH), false);
                        g2d.clip(path);
                    }
                    border.paintBorder(c, g2, borderX, borderY, borderW, borderH);
                    g2.dispose();
                }
            }
            g.translate(labelX, labelY);
            label.setSize(labelW - differenceWidth, labelH);//Yizhao Han add ' - differenceWidth'
            label.paint(g);
            g.translate(-labelX, -labelY);
            //////////////////////////////////////
            //Yizhao Han
            if (image != null) {
                int labelWidth = label.getSize().width;
                int translateX = labelX + labelWidth + labelGap;
                g.translate(translateX, labelY);
                Dimension newSize = new Dimension(labelW - labelWidth - labelGap, labelH);
                imageLocation = new Rectangle(translateX,
                        labelY, newSize.width, newSize.height);
                JLabel mLabel;
                Point mousePosition = c.getMousePosition();
                if (mousePosition == null) {
                    mLabel = getMyImageLabel(c, image);
                    nowMouseIsIn = false;
                } else {
                    if (imageLocation.contains(mousePosition)) {
                        mLabel = getMyImageLabel(c, activeImage == null ? image : activeImage);
                        nowMouseIsIn = true;
                        if (toolTip != null && c instanceof JComponent) {
                            ((JComponent) c).setToolTipText(toolTip);
                        }
                    } else {
                        mLabel = getMyImageLabel(c, image);
                        nowMouseIsIn = false;
                        if (toolTip != null && c instanceof JComponent) {
                            ((JComponent) c).setToolTipText(null);
                        }
                    }
                }
                c.getMousePosition();
                mLabel.setSize(newSize);
                mLabel.paint(g);
                g.translate(-translateX, -labelY);
                c.removeMouseListener(this);
                c.addMouseListener(this);
                c.addMouseMotionListener(this);
            }
            //////////////////////////////////////
        } else if (border != null) {
            border.paintBorder(c, g, x, y, width, height);
        }
    }

    private void initLabel() {
        this.label = new JLabel();
        this.label.setOpaque(false);
        this.label.putClientProperty(BasicHTML.propertyKey, null);
    }

    public MyTitledBorder(String title) {
        super(title);
        initLabel();
    }

    public MyTitledBorder(Border border) {
        super(border);
        initLabel();
    }

    public MyTitledBorder(Border border, String title) {
        super(border, title);
        initLabel();
    }

    public MyTitledBorder(Border border, String title, int titleJustification, int titlePosition) {
        super(border, title, titleJustification, titlePosition);
        initLabel();
    }

    public MyTitledBorder(Border border, String title, int titleJustification, int titlePosition, Font titleFont) {
        super(border, title, titleJustification, titlePosition, titleFont);
        initLabel();
    }

    public MyTitledBorder(Border border, String title, int titleJustification, int titlePosition, Font titleFont, Color titleColor) {
        super(border, title, titleJustification, titlePosition, titleFont, titleColor);
        initLabel();
    }

    private static Insets getBorderInsets(Border border, Component c, Insets insets) {
        if (border == null) {
            insets.set(0, 0, 0, 0);
        } else if (border instanceof AbstractBorder) {
            AbstractBorder ab = (AbstractBorder) border;
            insets = ab.getBorderInsets(c, insets);
        } else {
            Insets i = border.getBorderInsets(c);
            insets.set(i.top, i.left, i.bottom, i.right);
        }
        return insets;
    }

    private JLabel getLabel(Component c) {
        this.label.setText(getTitle());
        this.label.setFont(getFont(c));
        this.label.setForeground(getColor(c));
        this.label.setComponentOrientation(c.getComponentOrientation());
        this.label.setEnabled(c.isEnabled());
        return this.label;
    }

    private Color getColor(Component c) {
        Color color = getTitleColor();
        if (color != null) {
            return color;
        }
        return (c != null)
                ? c.getForeground()
                : null;
    }

    private int getJustification(Component c) {
        int justification = getTitleJustification();
        if ((justification == LEADING) || (justification == DEFAULT_JUSTIFICATION)) {
            return c.getComponentOrientation().isLeftToRight() ? LEFT : RIGHT;
        }
        if (justification == TRAILING) {
            return c.getComponentOrientation().isLeftToRight() ? RIGHT : LEFT;
        }
        return justification;
    }

    private int getPosition() {
        int position = getTitlePosition();
        if (position != DEFAULT_POSITION) {
            return position;
        }
        Object value = UIManager.get("TitledBorder.position");
        if (value instanceof Integer) {
            int i = (Integer) value;
            if ((0 < i) && (i <= 6)) {
                return i;
            }
        } else if (value instanceof String) {
            String s = (String) value;
            if (s.equalsIgnoreCase("ABOVE_TOP")) {
                return ABOVE_TOP;
            }
            if (s.equalsIgnoreCase("TOP")) {
                return TOP;
            }
            if (s.equalsIgnoreCase("BELOW_TOP")) {
                return BELOW_TOP;
            }
            if (s.equalsIgnoreCase("ABOVE_BOTTOM")) {
                return ABOVE_BOTTOM;
            }
            if (s.equalsIgnoreCase("BOTTOM")) {
                return BOTTOM;
            }
            if (s.equalsIgnoreCase("BELOW_BOTTOM")) {
                return BELOW_BOTTOM;
            }
        }
        return TOP;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (imageLocation != null && imageLocation.contains(e.getPoint())) {
            if (clickMessage != null) {
                TextInformationFrame textInformationFrame = new TextInformationFrame(clickTitle, clickMessage);
                textInformationFrame.setVisible(true);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        checkShouldRepaintBorder(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        checkShouldRepaintBorder(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        checkShouldRepaintBorder(e);
    }

    private void checkShouldRepaintBorder(MouseEvent e) {
        if (imageLocation != null) {
            if (parent != null) {
                boolean contain = imageLocation.contains(e.getPoint());
                if (contain && !nowMouseIsIn || !contain && nowMouseIsIn) {
                    parent.validate();
                    parent.repaint();
                }
            }
        }
    }

}
