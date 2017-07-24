package cre.view;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HanYizhao on 2017/6/8.
 * <p>
 * ResizablePanel is a kind of panel which can draw itself when size is modified by {@linkplain ResizableScrollPane}.
 * <p>
 * To implement a ResizablePanel, you need to implement
 * {@linkplain #getOriginalWidth()}, {@linkplain #getOriginalHeight()}
 * and {@linkplain #doMyPaint(Graphics2D, int, int, double)}
 * <p>
 * Because we are drawing vector diagram, the measurement unit of {@link #getOriginalWidth()} or {@link #getOriginalHeight()} is relative, just a number.
 * When user scroll the panel, this panel will repaint. So in order to reduce repainting, we use {@link #bufferedImage} as a buffer.
 */
public abstract class ResizablePanel extends JPanel {

    protected static JFileChooser fileChooser;
    protected static FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "SVG, PDF, PNG, JPEG (*.svg, *.pdf, *.png, *.jpeg, *.jpg)", "svg", "pdf", "png", "jpg", "jpeg");

    public enum FileType {
        SVG, PDF, PNG, JPEG
    }


    static {
        fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setMultiSelectionEnabled(false);

        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
    }

    /**
     * buffer
     */
    protected BufferedImage bufferedImage;
    /**
     * We can attach some information by using this field.
     */
    private Object tag;
    /**
     * The drawing area is not the same as this panel. The drawing area is smaller.
     * So, these fields indicate where the drawing area is.
     */
    protected int top, left;
    /**
     * Because the measurement unit of original is relative, we use this field to know how big the real image is.
     * The pixel height of image is scale * {@link #getOriginalHeight()}. The pixel width of image is scale * {@link #getOriginalWidth()}.
     */
    protected double scale;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    /**
     * Get the relative location of one point.
     * This function and {@link #getShowPointFromTruePosition(Point2D.Double)}
     * are now used together to make sure that the focus points are the same point when user resize the panel.
     * <p>
     * It is good if you do not modify these two functions. Or you understand what you are doing.
     *
     * @param old the point in this panel.
     * @return The relative location of this point, like (0.5, 0.5), center of diagram.
     */
    protected Point2D.Double getPointTruePositionFromShownPoint(Point old) {
        double x = ((double) (old.x - left)) / (getOriginalWidth() * scale);
        double y = ((double) (old.y - top)) / (getOriginalHeight() * scale);
        if (x < 0) {
            x = 0;
        }
        if (x > 1) {
            x = 1;
        }
        if (y < 0) {
            y = 0;
        }
        if (y > 1) {
            y = 1;
        }
        return new Point2D.Double(x, y);
    }

    /**
     * Get the absolute location from a relative position.
     *
     * @param old the relative location of one point. (0-1)
     * @return the absolute location of this point.
     */
    protected Point getShowPointFromTruePosition(Point2D.Double old) {
        return new Point((int) Math.round(left + old.x * getOriginalWidth() * scale),
                (int) (Math.round(top + old.y * getOriginalHeight() * scale)));
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension dimension = this.getSize();
        if (bufferedImage != null
                && bufferedImage.getWidth() == dimension.width
                && bufferedImage.getHeight() == dimension.height) {
            drawImage(g);
        } else {
            bufferedImage = ((Graphics2D) g).getDeviceConfiguration()
                    .createCompatibleImage(dimension.width, dimension.height);
            double oldHeight = getOriginalHeight();
            double oldWidth = getOriginalWidth();
            //Graphics2D g2 = (Graphics2D) g;
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.white);
            g2.fill(new Rectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
            int panelWidth = dimension.width - 10;
            int panelHeight = dimension.height - 10;
            double scale = Math.min(((double) panelHeight) / oldHeight, ((double) panelWidth) / oldWidth);
            int left = 0, top = 0;
            if ((double) panelWidth / panelHeight > oldWidth / oldHeight) {
                left = (int) Math.round(((double) panelWidth - scale * oldWidth) / 2);
            } else {
                top = (int) Math.round(((double) panelHeight - scale * oldHeight) / 2);
            }
            left += 5;
            top += 5;
            this.left = left;
            this.top = top;
            this.scale = scale;
            doMyPaint(g2, left, top, scale);
            g2.dispose();
            drawImage(g);
        }
    }

    protected void drawImage(Graphics g) {
        g.drawImage(bufferedImage, 0, 0, null);
    }

    protected abstract double getOriginalWidth();

    protected abstract double getOriginalHeight();

    protected abstract void doMyPaint(Graphics2D g2, int left, int top, double scale);

    /**
     * It is called when user wants to save this diagram.
     */
    public void doSaveAs() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSSZ");
        String fileName = sdf.format(new Date()) + ".svg";
        fileChooser.setSelectedFile(new File(fileName));
        int ch = fileChooser.showDialog(this, null);
        if (ch == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            String lowerName = f.getName().toLowerCase();
            FileType fileType;
            if (lowerName.endsWith(".svg")) {
                fileType = FileType.SVG;
            } else if (lowerName.endsWith(".pdf")) {
                fileType = FileType.PDF;
            } else if (lowerName.endsWith(".png")) {
                fileType = FileType.PNG;
            } else if (lowerName.endsWith(".jpeg") || lowerName.endsWith(".jpg")) {
                fileType = FileType.JPEG;
            } else {
                f = new File(f.getParent(), f.getName() + ".svg");
                fileType = FileType.SVG;
            }
            if (f.exists()) {
                int a = JOptionPane.showConfirmDialog(this,
                        "Do you confirm to overwrite this file?\n" + f.getAbsolutePath());
                if (a != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            try {
                switch (fileType) {
                    case SVG: {
                        saveAsSVG(f);
                    }
                    break;
                    case PDF: {
                        saveAsPDF(f);
                    }
                    break;
                    case PNG: {
                        saveAsPNG(f);
                    }
                    break;
                    case JPEG: {
                        saveAsJPEG(f);
                    }
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "ERROR", JOptionPane.ERROR_MESSAGE);
            }


        }
    }

    private void saveAsSVG(File f) throws Exception {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgURI = "http://www.w3.org/2000/svg";
        Document d = domImpl.createDocument(svgURI, "svg", null);
        SVGGraphics2D g2 = new SVGGraphics2D(d);
        doMyPaint(g2, 0, 0, 1);
        g2.setSVGCanvasSize(new Dimension((int) Math.ceil(getOriginalWidth()), (int) Math.ceil(getOriginalHeight())));
        Writer writer = new OutputStreamWriter(new FileOutputStream(f, false), "UTF-8");
        g2.stream(writer);
        g2.dispose();
        writer.close();
    }

    private void saveAsPDF(File f) throws Exception {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgURI = "http://www.w3.org/2000/svg";
        Document d = domImpl.createDocument(svgURI, "svg", null);
        SVGGraphics2D g2 = new SVGGraphics2D(d);
        doMyPaint(g2, 0, 0, 1);
        g2.setSVGCanvasSize(new Dimension((int) Math.ceil(getOriginalWidth()), (int) Math.ceil(getOriginalHeight())));
        ByteOutputStream byteOutputStream = new ByteOutputStream();
        Writer out = new OutputStreamWriter(byteOutputStream, "UTF-8");
        g2.stream(out, false);
        ByteInputStream byteInputStream = new ByteInputStream(byteOutputStream.getBytes(), byteOutputStream.getCount());
        Transcoder transcoder = new PDFTranscoder();
        TranscoderInput input = new TranscoderInput(byteInputStream);
        FileOutputStream fos = new FileOutputStream(f, false);
        TranscoderOutput output = new TranscoderOutput(fos);
        transcoder.transcode(input, output);
        fos.close();
        g2.dispose();
    }

    private void saveAsPNG(File f) throws Exception {
        ImageIO.write(bufferedImage, "PNG", f);
    }

    private void saveAsJPEG(File f) throws Exception {
        ImageIO.write(bufferedImage, "JPEG", f);
    }
}
