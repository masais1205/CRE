package cre.algorithm;

import java.util.List;

public class ToolFunctions {
    public static final char char_Star = '*';
    public static final char char_QUESTION = '×';

    public static boolean isSamePatternGroup(char[] attrValue, char[] refValue, List<Integer> positions) {
        for (int i=0; i<attrValue.length; i++) {
            if (! positions.contains(i))
                if (attrValue[i]!=refValue[i] && attrValue[i]!=char_QUESTION && refValue[i]!=char_QUESTION)
                    return false;
        }
        return true;
    }


    public static boolean isSamePatternGroup(char[] attrValue, char[] refValue) {
        for (int i=0; i<attrValue.length; i++) {
            if (attrValue[i]!=refValue[i] && attrValue[i]!=char_QUESTION && refValue[i]!=char_QUESTION)
                return false;
        }
        return true;
    }
}
