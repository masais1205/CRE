package cre.view.tree;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HanYizhao on 2017/6/30.
 * <p>This tool is used to break a string to several strings.
 * Thus, these strings can be shown line by line.</p>
 */
public class LineBreakerTool {

    /**
     * One line of string.
     */
    public static class TextLayoutAndContent {
        private TextLayout layout;
        private String content;

        public TextLayoutAndContent(TextLayout layout, String content) {
            this.layout = layout;
            this.content = content;
        }

        public TextLayout getLayout() {
            return layout;
        }

        public String getContent() {
            return content;
        }
    }

    /**
     * Break a long string to several strings.
     *
     * @param content The long string (maybe long).
     * @param width   Max width.
     * @param font    The font used to draw string.
     * @param g2      The painter.
     * @return Several strings.
     */
    public static List<TextLayoutAndContent> getStringLayout(@NotNull String content, @NotNull float width, Font font,
                                                             @Nullable Graphics2D g2) {
        List<TextLayoutAndContent> result = new ArrayList<>();
        String[] contents = content.split("\n");
        FontRenderContext fontRenderContext = g2 == null ?
                new FontRenderContext(null, true, false)
                : g2.getFontRenderContext();
        for (String s : contents) {
            if (s.length() > 0) {
                AttributedString as = new AttributedString(s);
                as.addAttribute(TextAttribute.FONT, font);
                as.addAttribute(TextAttribute.KERNING, TextAttribute.KERNING_ON);
                LineBreakMeasurer lineBreakMeasurer = new LineBreakMeasurer(as.getIterator(), fontRenderContext);
                while (lineBreakMeasurer.getPosition() < s.length()) {
                    int start = lineBreakMeasurer.getPosition();
                    TextLayoutAndContent c = new TextLayoutAndContent(lineBreakMeasurer.nextLayout(width),
                            s.substring(start, lineBreakMeasurer.getPosition()));
                    result.add(c);
                }
            }
        }
        return result;
    }
}
