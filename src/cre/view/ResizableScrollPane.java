package cre.view;

import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.beans.EventHandler;

/**
 * Created by HanYizhao on 2017/6/8.
 * This class is used to show the {@link ResizablePanel}.
 */
public class ResizableScrollPane extends JScrollPane {

    private ResizablePanel panel;

    public ResizableScrollPane(final ResizablePanel panel) {
        super(panel);
        this.panel = panel;
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                KeyEvent keyEvent = (KeyEvent) event;
                if (keyEvent.isControlDown()) {
                    ResizableScrollPane.this.setWheelScrollingEnabled(false);
                } else {
                    ResizableScrollPane.this.setWheelScrollingEnabled(true);
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    zoom(e.getPreciseWheelRotation() < 0, e.getPoint());
                }
            }
        });
    }

    /**
     * Try to zoom the panel.
     *
     * @param larger     Larger or not.
     * @param mousePoint The focus of user, may be null.
     */
    public void zoom(boolean larger, @Nullable Point mousePoint) {
        double rate = panel.getOriginalWidth() / panel.getOriginalHeight();
        float change = larger ? 1.5f : 0.67f;
        Dimension oldDimension = panel.getSize();
        Dimension newDimension = new Dimension(Math.round(oldDimension.width * change), Math.round(oldDimension.height * change));
        if (newDimension.width < 1) {
            newDimension.width = 1;
        }
        if (newDimension.height < 1) {
            newDimension.height = 1;
        }
        if (change > 1) {
            if (rate > 1) {
                newDimension.height = (int) Math.round(newDimension.width / rate);
            } else {
                newDimension.width = (int) Math.round(newDimension.height * rate);
            }
        }
        if (!larger || newDimension.width * newDimension.height < 10000000) {
            final Rectangle viewRect = ResizableScrollPane.this.getViewport().getViewRect();
            final Point point;
            if (mousePoint != null) {
                point = mousePoint;
            } else {
                point = new Point((int) Math.round(viewRect.getWidth() / 2), (int) Math.round(viewRect.getHeight() / 2));
            }
            final Point2D.Double mouseLocation = panel.getPointTruePositionFromShownPoint(
                    new Point(point.x + viewRect.x, point.y + viewRect.y));
            panel.setPreferredSize(newDimension);
            panel.revalidate();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Point p = panel.getShowPointFromTruePosition(mouseLocation);
                    Point newP = new Point(p.x - point.x, p.y - point.y);
                    if (newP.x < 0) {
                        newP.x = 0;
                    }
                    if (newP.y < 0) {
                        newP.y = 0;
                    }
                    ResizableScrollPane.this.getViewport().setViewPosition(
                            newP);

                }
            });
        }
    }


}
