package cre.ui.custom;

import cre.ui.Tool;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HanYizhao on 2017/7/31.
 * This frame will have a icon.
 */
public class MyIconFrame extends JFrame {

    private static List<Image> iconList;

    public static List<Image> getIconList() {
        if (iconList == null) {
            BufferedImage bufferedImage;
            try {
                bufferedImage = ImageIO.read(MyIconFrame.class.getResource("/image/small logo.png"));
                List<Image> result = new ArrayList<>();
                int[] size = {16, 32, 64, 128};
                for (int s : size) {
                    result.add(bufferedImage.getScaledInstance(s, s, Image.SCALE_SMOOTH));
                }
                iconList = result;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return iconList;
    }

    public MyIconFrame() throws HeadlessException {
        this.setIconImages(getIconList());
    }
}
