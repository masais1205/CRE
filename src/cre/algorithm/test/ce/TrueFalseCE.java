package cre.algorithm.test.ce;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Locale;
import java.util.stream.IntStream;

import cre.ui.AlgorithmPanel;
import cre.ui.MainFrameEventHandler;

/**
 * Created by HanYizhao on 2017/3/9.
 */
public class TrueFalseCE extends AbstractCE {

    public TrueFalseCE(char[] buffer) {
        super(buffer);
//        groundTruthValue = 0;
    }

    @Override
    public AbstractCE mergeInstance(AbstractCE c, int GT, List<Integer> position, int[] PCMembers, char positionChar,
                                    CEValue preferredValue, double zc) {
        TrueFalseCE c2 = (TrueFalseCE) c;
        TrueFalseCE result = new TrueFalseCE(this.value);
        result.statistics = this.statistics.clone();
        result.groundTruthValue = this.groundTruthValue;

//        System.out.println(String.valueOf(this.value)+" "+this.getInstanceNumber()+"\t"+String.valueOf(c2.value)+" "+c2.getInstanceNumber());
        boolean hasPC = false;
        for (int i : position) {
            if (!hasPC)
                hasPC = IntStream.of(PCMembers).anyMatch(x -> x == i);
            result.value[i] = positionChar;
        }
        result.updateStatistics(c2, GT, hasPC);
        result.updateItem(c2.statistics);

//        if (preferredValue != null) {
//            result.cEValue = preferredValue;
//        } else {
//            result.updateCEValue(zc);
//        }
//        System.out.println(Boolean.toString(hasPC)+" "+String.valueOf(result.value)+" "+result.getInstanceNumber());
//        System.out.println(Character.toString(positionChar) + String.valueOf(result.value));
        return result;
    }

    @Override
    public AbstractCE mergeInstanceList(Collection<AbstractCE> cList, int GT, List<Integer> position, int[] PCMembers, char positionChar,
                                    CEValue preferredValue, double zc) {
        TrueFalseCE result = new TrueFalseCE(this.value);
        result.statistics = this.statistics.clone();
        result.groundTruthValue = this.groundTruthValue;
        for (AbstractCE c : cList) {
            TrueFalseCE c2 = (TrueFalseCE) c;
//            System.out.println(String.valueOf(this.value)+" "+this.getInstanceNumber()+"\t"+String.valueOf(c2.value)+" "+c2.getInstanceNumber());
            boolean hasPC = false;
            for (int i : position) {
                if (!hasPC)
                    hasPC = IntStream.of(PCMembers).anyMatch(x -> x == i);
                result.value[i] = positionChar;
            }
            result.updateStatistics(c2, GT, hasPC);
            result.updateItem(c2.statistics);

//            if (preferredValue != null) {
//                result.cEValue = preferredValue;
//            } else {
//                result.updateCEValue(zc);
//            }
//            System.out.println(Boolean.toString(hasPC)+" "+String.valueOf(result.value)+" "+result.getInstanceNumber());
        }
        return result;
    }

    /**
     * The interface in which there are functions MainFrame provide.
     */
    private MainFrameEventHandler mainFrame;

    @Override
    public void updateCEValue(double zc) {
        double[] tempStatisticValue = new double[4];
        for (int i = 0; i < 4; i++) {
            tempStatisticValue[i] = statistics[i] == 0 ? 0.5 : statistics[i];
        }
        double WAll0 = tempStatisticValue[2] + tempStatisticValue[3];
        double WAll1 = tempStatisticValue[0] + tempStatisticValue[1];

        double p1 = tempStatisticValue[0] / WAll1;
        double p2 = tempStatisticValue[2] / WAll0;

        double p_av = (tempStatisticValue[0] + tempStatisticValue[2]) /
                (WAll1 + WAll0);
        double temp = (WAll0 + WAll1) / WAll0 / WAll1;
//        double z = (Math.abs(p1 - p2) - temp / 2)
//                / Math.sqrt(p_av * (1.0 - p_av) * temp);
        double z = (p1 - p2)
                / Math.sqrt(p_av * (1.0 - p_av) * temp);

        // modified by mss, commented z > zc
//        double thre = 0;
//        if (p1-p2 < 0-thre) {
//            cEValue = CEValue.MINUS;
//        }
//        else if (p1-p2 > thre) {
//            cEValue = CEValue.PLUS;
//        }
//        else {
//            cEValue = CEValue.QUESTION;
//        }
        // mss
        if (p1 < p2) {
            //System.out.println(z + "\t" + this);
            if (z > zc) {
                cEValue = CEValue.MINUS;
            } else {
                cEValue = CEValue.QUESTION;
            }
        } else {
            if (z > zc) {
                cEValue = CEValue.PLUS;
            } else {
                cEValue = CEValue.QUESTION;
            }
        }
    }

//    @Override
    public void updateGTValue(double GTValue) {
        double tmpValue = groundTruthValue * getInstanceNumber();
        tmpValue += GTValue;
        groundTruthValue = tmpValue / (getInstanceNumber() + 1);
    }

    @Override
    public int getInstanceNumber() {
        return (int) (statistics[0] + statistics[1]
                        + statistics[2] + statistics[3]);
    }


    @Override
    public void updateReliable(double reliabilityMinSupport) {
        int num = getInstanceNumber();
        reliable = num >= reliabilityMinSupport ? true : false;
    }


    @Override
    public void updateSignificance(double significanceLevel) {
        double[] tempStatisticValue = new double[4];
        for (int i = 0; i < 4; i++) {
            tempStatisticValue[i] = statistics[i] == 0 ? 0.5 : statistics[i];
        }
        double WAll0 = tempStatisticValue[2] + tempStatisticValue[3];
        double WAll1 = tempStatisticValue[0] + tempStatisticValue[1];

        double p1 = tempStatisticValue[0] / WAll1;
        double p2 = tempStatisticValue[2] / WAll0;

        double p_av = (tempStatisticValue[0] + tempStatisticValue[2]) /
                (WAll1 + WAll0);
        double temp = (WAll0 + WAll1) / WAll0 / WAll1;
//        double z = (Math.abs(p1 - p2) - temp / 2)
//                / Math.sqrt(p_av * (1.0 - p_av) * temp);
        significance = (p1 - p2)
                / Math.sqrt(p_av * (1.0 - p_av) * temp);
        isSignificant = significance >= significanceLevel ? true : false;
    }

    public void updateItem(int[] stats) {
        for (int i=0; i<4; i++)
            statistics[i] += stats[i];
    }


    public void updateItem(double[] stats) {
        for (int i=0; i<4; i++)
            statistics[i] += stats[i];
    }


    public void addItem(boolean W, boolean Y) {
        if (W) {
            if (Y) {
                statistics[0]++;
            } else {
                statistics[1]++;
            }
        } else {
            if (Y) {
                statistics[2]++;
            } else {
                statistics[3]++;
            }
        }
    }

    public void updateStatistics(TrueFalseCE c2, int GT, boolean hasPC) {
        double[] s1 = new double[4];
        double[] s2 = new double[4];
        double ce;
        for (int i = 0; i < 4; i++) {
            s1[i] = this.statistics[i] == 0 ? 0.5 : this.statistics[i];
            s2[i] = c2.statistics[i] == 0 ? 0.5 : c2.statistics[i];
        }

        double c10 = s1[2] + s1[3];
        double c11 = s1[0] + s1[1];

        double c20 = s2[2] + s2[3];
        double c21 = s2[0] + s2[1];

        statistics[4] = (s1[0]+s2[0]) / (c11+c21) - (s1[2]+s2[2]) / (c10+c20);
        if (GT >= 0) {
            groundTruthValue = (groundTruthValue * (c10 + c11) + c2.groundTruthValue * (c20 + c21))
                    / (c10 + c11 + c20 + c21);
        }
        if (hasPC) {
            groundTruthValue = ((s1[0] / c11 - s1[2] / c10) * (c10 + c11) + (s2[0] / c21 - s2[2] / c20) * (c20 + c21))
                        / (c10 + c11 + c20 + c21);
        }
        else
            groundTruthValue = statistics[4];
    }

    @Override
    public void updateStatistics(int GT) {
        for (int i = 0; i < 4; i++) {
            statistics[i] = statistics[i] == 0 ? 0.5 : statistics[i];
        }
        double WAll0 = statistics[2] + statistics[3];
        double WAll1 = statistics[0] + statistics[1];

        double p1 = statistics[0] / WAll1;
        double p2 = statistics[2] / WAll0;

        statistics[4] = p1 - p2;
        if (GT < 0) {
            groundTruthValue = statistics[4];
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i]);
            sb.append('\t');
        }
        sb.append(isSignificant);
        sb.append('\t');
        for (int i = 0; i < 4; i++) {
            if (statistics[i] == 0) {
                sb.append(0.5);
            } else {
                sb.append(statistics[i]);
            }
            sb.append('\t');
        }

        double[] tempStatisticValue = new double[4];
        for (int i = 0; i < 4; i++) {
            tempStatisticValue[i] = statistics[i] == 0 ? 0.5 : statistics[i];
        }
        double WAll0 = tempStatisticValue[2] + tempStatisticValue[3];
        double WAll1 = tempStatisticValue[0] + tempStatisticValue[1];

        double p1 = tempStatisticValue[0] / WAll1;
        double p2 = tempStatisticValue[2] / WAll0;
//        sb.append(String.format(Locale.CHINA, "%.4f", p1 - p2)); // output p1-p2
        sb.append(String.format(Locale.CHINA, "%.4f", statistics[4]) + "\t");
        sb.append(String.format(Locale.CHINA, "%.4f", groundTruthValue));
        return sb.toString();
    }
}
