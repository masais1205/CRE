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

    private int[] statisticValue;

    public TrueFalseCE(char[] buffer) {
        super(buffer);
        statisticValue = new int[4];
//        groundTruthValue = 0;
    }

    @Override
    public AbstractCE mergeInstance(AbstractCE c, int GT, List<Integer> position, int[] PCMembers, char positionChar,
                                    CEValue preferredValue, double zc) {
        TrueFalseCE c2 = (TrueFalseCE) c;
        TrueFalseCE result = this;
//        System.out.println(String.valueOf(this.value)+" "+this.getInstanceNumber()+"\t"+String.valueOf(c2.value)+" "+c2.getInstanceNumber());
        boolean hasPC = false;
        for (int i : position) {
            if (!hasPC)
                hasPC = IntStream.of(PCMembers).anyMatch(x -> x == i);
            result.value[i] = positionChar;
        }
//        if(!hasPC) {
//            if (GT > 0)
//                result.updateStatistics(c2, GT);
//            result.updateItem(c2.statisticValue);
////            for (int i = 0; i < 4; i++) {
////                result.statisticValue[i] += c2.statisticValue[i];
////                result.statistics[i] = result.statisticValue[i];
////            }
//            if (GT < 0)
//                result.updateStatistics(GT);
//        }
//        else {
            result.updateStatistics(c2, GT);
            result.updateItem(c2.statisticValue);
//            for (int i = 0; i < 4; i++) {
//                result.statisticValue[i] += c2.statisticValue[i];
//                result.statistics[i] = result.statisticValue[i];
//            }
//        }
        if (preferredValue != null) {
            result.cEValue = preferredValue;
        } else {
            result.updateCEValue(zc);
        }
//        System.out.println(Boolean.toString(hasPC)+" "+String.valueOf(result.value)+" "+result.getInstanceNumber());
        return result;
    }

    @Override
    public AbstractCE mergeInstanceList(Collection<AbstractCE> cList, int GT, List<Integer> position, int[] PCMembers, char positionChar,
                                    CEValue preferredValue, double zc) {
        TrueFalseCE result = this;
        for (AbstractCE c : cList) {
            TrueFalseCE c2 = (TrueFalseCE) c;
//            System.out.println(String.valueOf(this.value)+" "+this.getInstanceNumber()+"\t"+String.valueOf(c2.value)+" "+c2.getInstanceNumber());
            boolean hasPC = false;
            for (int i : position) {
                if (!hasPC)
                    hasPC = IntStream.of(PCMembers).anyMatch(x -> x == i);
                result.value[i] = positionChar;
            }
//            if(!hasPC) {
//                if (GT > 0)
//                    result.updateStatistics(c2, GT);
//                result.updateItem(c2.statisticValue);
////            for (int i = 0; i < 4; i++) {
////                result.statisticValue[i] += c2.statisticValue[i];
////                result.statistics[i] = result.statisticValue[i];
////            }
//                if (GT < 0)
//                    result.updateStatistics(GT);
//            }
//            else {
                result.updateStatistics(c2, GT);
                result.updateItem(c2.statisticValue);
//            for (int i = 0; i < 4; i++) {
//                result.statisticValue[i] += c2.statisticValue[i];
//                result.statistics[i] = result.statisticValue[i];
//            }
//            }
            if (preferredValue != null) {
                result.cEValue = preferredValue;
            } else {
                result.updateCEValue(zc);
            }
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
            tempStatisticValue[i] = statisticValue[i] == 0 ? 0.5 : statisticValue[i];
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
        return statisticValue[0] + statisticValue[1]
                + statisticValue[2] + statisticValue[3];
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
            tempStatisticValue[i] = statisticValue[i] == 0 ? 0.5 : statisticValue[i];
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

        isSignificant = z >= significanceLevel ? true : false;
    }

    public void updateItem(int[] stats) {
        for (int i=0; i<4; i++)
            statisticValue[i] += stats[i];
    }


    public void updateItem(double[] stats) {
        for (int i=0; i<4; i++)
            statisticValue[i] += stats[i];
    }


    public void addItem(boolean W, boolean Y) {
        if (W) {
            if (Y) {
                statisticValue[0]++;
            } else {
                statisticValue[1]++;
            }
        } else {
            if (Y) {
                statisticValue[2]++;
            } else {
                statisticValue[3]++;
            }
        }
    }

    public void updateStatistics(TrueFalseCE c2, int GT) {
        double[] s1 = new double[4];
        double[] s2 = new double[4];
        double ce;
        for (int i = 0; i < 4; i++) {
            s1[i] = this.statisticValue[i] == 0 ? 0.5 : this.statisticValue[i];
            s2[i] = c2.statisticValue[i] == 0 ? 0.5 : c2.statisticValue[i];
        }

        double c10 = s1[2] + s1[3];
        double c11 = s1[0] + s1[1];

        double c20 = s2[2] + s2[3];
        double c21 = s2[0] + s2[1];

        if (GT > 0)
            statistics[4] = (groundTruthValue * (c10+c11) + c2.groundTruthValue * (c20+c21))
                    / (c10+c11+c20+c21);
        else
            statistics[4] = ((s1[0]/c11 - s1[2]/c10) * (c10+c11) + (s2[0]/c21 - s2[2]/c20) * (c20+c21))
                    / (c10+c11+c20+c21);
        groundTruthValue = statistics[4];
    }

    @Override
    public void updateStatistics(int GT) {
        for (int i = 0; i < 4; i++) {
            statistics[i] = statisticValue[i] == 0 ? 0.5 : statisticValue[i];
        }
        double WAll0 = statistics[2] + statistics[3];
        double WAll1 = statistics[0] + statistics[1];

        double p1 = statistics[0] / WAll1;
        double p2 = statistics[2] / WAll0;
        if (GT > 0)
            statistics[4] = groundTruthValue;
        else
            statistics[4] = p1 - p2;
        groundTruthValue = statistics[4];
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i]);
            sb.append('\t');
        }
        sb.append(cEValue);
        sb.append('\t');
        for (int i = 0; i < statisticValue.length; i++) {
            if (statisticValue[i] == 0) {
                sb.append(0.5);
            } else {
                sb.append(statisticValue[i]);
            }
            sb.append('\t');
        }

        double[] tempStatisticValue = new double[4];
        for (int i = 0; i < 4; i++) {
            tempStatisticValue[i] = statisticValue[i] == 0 ? 0.5 : statisticValue[i];
        }
        double WAll0 = tempStatisticValue[2] + tempStatisticValue[3];
        double WAll1 = tempStatisticValue[0] + tempStatisticValue[1];

        double p1 = tempStatisticValue[0] / WAll1;
        double p2 = tempStatisticValue[2] / WAll0;
//        sb.append(String.format(Locale.CHINA, "%.4f", p1 - p2)); // output p1-p2
        sb.append(String.format(Locale.CHINA, "%.4f", statistics[4]));
        return sb.toString();
    }
}
