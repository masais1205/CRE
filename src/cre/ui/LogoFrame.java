package cre.ui;

import cre.ui.custom.MyIconFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Created by HanYizhao on 2017/7/31.
 */
public class LogoFrame extends JDialog {
    public LogoFrame() throws HeadlessException {
        this.setIconImages(MyIconFrame.getIconList());
        this.setUndecorated(true);
        final BufferedImage image;
        try {
            image = ImageIO.read(getClass().getResource("/image/logo.png"));
            final int width = Tool.HighResolution(600);
            final int height = Math.round((float) width * image.getHeight() / image.getWidth());
            JPanel panel = new JPanel() {
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
                }
            };
            panel.setBackground(new Color(240, 240, 240));
            this.add(panel);
            this.setSize(width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setAlwaysOnTop(true);
        Tool.moveToCenter(this, true);
    }
}
