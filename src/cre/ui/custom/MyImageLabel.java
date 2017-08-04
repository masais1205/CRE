package cre.ui.custom;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

/**
 * Created by HanYizhao on 2017/8/3.
 */
public class MyImageLabel extends JLabel {
    private ImageIcon normalIcon;
    private ImageIcon activeIcon;

    public MyImageLabel(@NotNull final URL normalImage, @Nullable final URL activeImage, Dimension size) {
        normalIcon = new ImageIcon(new ImageIcon(normalImage).getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH));
        if (activeImage != null) {
            activeIcon = new ImageIcon(new ImageIcon(activeImage).getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH));
        }
        this.setIcon(normalIcon);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (activeIcon != null) {
                    MyImageLabel.this.setIcon(activeIcon);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (activeIcon != null) {
                    MyImageLabel.this.setIcon(normalIcon);
                }
            }
        });
    }


}
