package cre.algorithm.test.ce;

import org.omg.CORBA.INTERNAL;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by HanYizhao on 2017/5/17.
 */
public class CESearchTool {

    private AbstractCE[] mergeResult;


    public CESearchTool(final List<AbstractCE> mergeResult) {
        this.mergeResult = new AbstractCE[mergeResult.size()];
        mergeResult.toArray(this.mergeResult);
    }

    private boolean compareFromPatternToArray(final char[] pattern, final char[] array) {
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

    public char[] getCharValue(char[] buffer) {
        for (AbstractCE i : mergeResult) {
            char[] now = i.value;
            if (now.length == buffer.length) {
                if (compareFromPatternToArray(now, buffer)) {
                    return now;
                }
            } else {
                break;
            }
        }
        return null;
    }

    public AbstractCE getNearestFreqPatt(char[] buffer) {
        int minDist = Integer.MAX_VALUE;
        List<TrueFalseCE> ceList = new ArrayList<>();
        List<Integer> ceListSize = new ArrayList<>();
        for (AbstractCE i : mergeResult) {
            char[] now = i.value;
            if (now.length == buffer.length) {
                int dist = compareDistFromPatternToArray(now, buffer);
                if (dist == -1)
                    continue;
                if (dist < minDist) {
                    minDist = dist;
                    ceList = new ArrayList<>();
                    ceList.add((TrueFalseCE) i);
                    ceListSize = new ArrayList<>();
                    ceListSize.add(i.getInstanceNumber());
                }
                else if (dist == minDist) {
                    ceList.add((TrueFalseCE) i);
                    ceListSize.add(i.getInstanceNumber());
                }
            }
        }
        if (minDist == Integer.MAX_VALUE)
            return null;

        int maxSize = Integer.MIN_VALUE;
        TrueFalseCE patt = new TrueFalseCE(buffer);
        boolean flag = false;
        for (int j=0; j<ceList.size(); j++) {
            if (ceListSize.get(j) > maxSize) {
                maxSize = ceListSize.get(j);
                patt = ceList.get(j);
                flag = true;
            }
        }
        if (flag)
            return patt;
        else
            return null;
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

    private int[] compareDistFromPatternToArray(final char[] pattern, final char[] array, int[] PCMemebrs) {
        int[] distNpccnt = new int[2];
        int dist = 0, PCCnt = 0;
        for (int i = 0; i < array.length; i++) {
            if (pattern[i] != array[i]) {
                if (pattern[i] != CEAlgorithm.char_Star && pattern[i] != CEAlgorithm.char_QUESTION) {
                    dist = array.length*10;
                    break;
                }
                dist++;
            }
            if (Arrays.asList(PCMemebrs).contains(i))
                PCCnt++;
        }
        distNpccnt[0] = dist;
        distNpccnt[1] = PCCnt;
        return distNpccnt;
    }

    private int compareDistFromPatternToArray(final char[] pattern, final char[] array) {
        int dist = 0;
        for (int i = 0; i < array.length; i++) {
            if (pattern[i] != array[i]) {
                if (pattern[i] != CEAlgorithm.char_Star && pattern[i] != CEAlgorithm.char_QUESTION) {
                    return -1;
                }
                dist++;
            }
        }
        return dist;
    }

    public char[] getNearestCharValue(char[] buffer) {
        int minDist = buffer.length*10;
        char[] arr = new char[buffer.length];
        for (AbstractCE i : mergeResult) {
            char[] now = i.value;
            if (now.length == buffer.length) {
                int dist = compareDistFromPatternToArray(now, buffer);
                if (dist < minDist) {
                    minDist = dist;
                    arr = now;
                }
            }
        }
        if (minDist <= buffer.length)
            return arr;
        else
            return null;
    }

    public char getSign(double num) {
        if (num > 0) {
//            if (num < .05)
//                return '0';
            return '+';
        }
        else if (num == 0)
            return '0';
        else {
//            if (num > -.05)
//                return '0';
            return '-';
        }
    }

    public char getCESign(char[] buffer) {
        int minDist = Integer.MAX_VALUE;
        char ceSign = '?';
        for (AbstractCE i : mergeResult) {
            char[] now = i.value;
            if (now.length == buffer.length) {
                int dist = compareDistFromPatternToArray(now, buffer);
                if (dist < minDist) {
                    minDist = dist;
                    ceSign = getSign(i.statistics[4]);
                }
            }
        }
        return ceSign;
    }

    public double getNearestFreqCE(char[] buffer) {
        int minDist = Integer.MAX_VALUE;
        List<Double> ceList = new ArrayList<>();
        List<Integer> ceListSize = new ArrayList<>();
        for (AbstractCE i : mergeResult) {
            char[] now = i.value;
            if (now.length == buffer.length) {
                int dist = compareDistFromPatternToArray(now, buffer);
                if (dist == -1)
                    continue;
                if (dist < minDist) {
                    minDist = dist;
                    ceList = new ArrayList<>();
                    ceList.add(i.statistics[4]);
                    ceListSize = new ArrayList<>();
                    ceListSize.add(i.getInstanceNumber());
                }
                else if (dist == minDist) {
                    ceList.add(i.statistics[4]);
                    ceListSize.add(i.getInstanceNumber());
                }
            }
        }
        if (minDist == Integer.MAX_VALUE)
            throw new java.lang.Error("Not matched to any pattern!!!");

        int maxSize = Integer.MIN_VALUE;
        double ce = 0;
        boolean flag = false;
        for (int j=0; j<ceList.size(); j++) {
            if (ceListSize.get(j) > maxSize) {
                maxSize = ceListSize.get(j);
                ce = ceList.get(j);
                flag = true;
            }
        }
        return ce;
    }

    public char getNearestFreqCESign(char[] buffer) {
        int minDist = Integer.MAX_VALUE;
        List<Double> ceList = new ArrayList<>();
        List<Integer> ceListSize = new ArrayList<>();
        for (AbstractCE i : mergeResult) {
            char[] now = i.value;
            if (now.length == buffer.length) {
                int dist = compareDistFromPatternToArray(now, buffer);
                if (dist < minDist) {
                    minDist = dist;
                    ceList = new ArrayList<>();
                    ceList.add(i.statistics[4]);
                    ceListSize = new ArrayList<>();
                    ceListSize.add(i.getInstanceNumber());
                }
                else if (dist == minDist) {
                    ceList.add(i.statistics[4]);
                    ceListSize.add(i.getInstanceNumber());
                }
            }
        }
        int maxSize = Integer.MIN_VALUE;
        double ce = 0;
        boolean flag = false;
        for (int j=0; j<ceList.size(); j++) {
            if (ceListSize.get(j) > maxSize) {
                maxSize = ceListSize.get(j);
                ce = ceList.get(j);
                flag = true;
            }
        }
        if (flag)
            return getSign(ce);
        else
            return '?';
    }

    public char getNearestPCCESign(char[] buffer, int[] PCMembers) {
        int minDist = buffer.length*10;
        List<Double> ceList = new ArrayList<>();
        List<Integer> ceListSize = new ArrayList<>();
        List<Integer> pcCntList = new ArrayList<>();
        for (AbstractCE i : mergeResult) {
            char[] now = i.value;
            if (now.length == buffer.length) {
                int[] distNpccnt = compareDistFromPatternToArray(now, buffer, PCMembers);
                int dist = distNpccnt[0];
                int PCCnt = distNpccnt[1];
                if (dist < minDist) {
                    minDist = dist;
                    ceList = new ArrayList<>();
                    ceList.add(i.statistics[4]);
                    ceListSize = new ArrayList<>();
                    ceListSize.add(i.getInstanceNumber());
                    pcCntList = new ArrayList<>();
                    pcCntList.add(PCCnt);
                }
                else if (dist == minDist) {
                    ceList.add(i.statistics[4]);
                    ceListSize.add(i.getInstanceNumber());
                    pcCntList.add(PCCnt);
                }
            }
        }
        double ce = 0, cnt = 0;
        if (PCMembers.length > 0) {
            double maxCnt = 0;
            for (int j = 0; j < ceList.size(); j++) {
                if (pcCntList.get(j) > maxCnt) {
                    maxCnt = pcCntList.get(j);
                    ce = ceList.get(j) * ceListSize.get(j);
                    cnt = ceListSize.get(j);
                } else if (pcCntList.get(j) == maxCnt) {
                    ce += ceList.get(j) * ceListSize.get(j);
                    cnt += ceListSize.get(j);
                }
            }
        }
        else {
            double maxSize = 0;
            for (int j=0; j<ceList.size(); j++) {
                if (ceListSize.get(j) > maxSize) {
                    maxSize = ceListSize.get(j);
                    ce = ceList.get(j);
                    cnt = 1;
                }
                else if (ceListSize.get(j) == maxSize) {
                    ce += ceList.get(j);
                    cnt++;
                }
            }
        }
        if (cnt > 0)
            return getSign(ce/cnt);
        else
            return '?';
    }

    public char getNearestAvgCESign(char[] buffer) {
        int minDist = buffer.length*10;
        List<Double> ceList = new ArrayList<>();
        List<Integer> ceListSize = new ArrayList<>();
        for (AbstractCE i : mergeResult) {
            char[] now = i.value;
            if (now.length == buffer.length) {
                int dist = compareDistFromPatternToArray(now, buffer);
                if (dist < minDist) {
                    minDist = dist;
                    ceList = new ArrayList<>();
                    ceList.add(i.statistics[4]);
                    ceListSize = new ArrayList<>();
                    ceListSize.add(i.getInstanceNumber());
                }
                else if (dist == minDist) {
                    ceList.add(i.statistics[4]);
                    ceListSize.add(i.getInstanceNumber());
                }
            }
        }
        double ce = 0, cnt = 0;
        for (int j=0; j<ceList.size(); j++) {
             ce += ceList.get(j) * ceListSize.get(j);
             cnt += ceListSize.get(j);
        }
        if (cnt > 0)
            return getSign(ce/cnt);
        else
            return '?';
    }
}
