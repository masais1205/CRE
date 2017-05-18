package cre.algorithm.test.ce;

import java.util.List;

/**
 * Created by HanYizhao on 2017/5/17.
 */
public class CESearchTool {

    private AbstractCE[] mergeResult;


    public CESearchTool(List<AbstractCE> mergeResult) {
        this.mergeResult = new AbstractCE[mergeResult.size()];
        mergeResult.toArray(this.mergeResult);
    }

    private boolean compareFromPatternToArray(char[] pattern, char[] array) {
        boolean same = true;
        for (int i = 0; i < array.length; i++) {
            if (pattern[i] != array[i]) {
                if (pattern[i] != CEAlgorithm.char_Star && pattern[i] != CEAlgorithm.char_QUESTION) {
                    same = false;
                    break;
                }
            }
        }
        return same;
    }

    public CEValue getCEValue(char[] buffer) {
        for (AbstractCE i : mergeResult) {
            char[] now = i.value;
            if (now.length == buffer.length) {
                if (compareFromPatternToArray(now, buffer)) {
                    return i.cEValue;
                }
            } else {
                break;
            }
        }
        return null;
    }
}
