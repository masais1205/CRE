package cre.algorithm.test.ce;

import java.util.Locale;

/**
 * Created by HanYizhao on 2017/3/9.
 */
public class TrueFalseCE extends AbstractCE {

    private int[] statisticValue;

    public TrueFalseCE(char[] buffer) {
        super(buffer);
        statisticValue = new int[4];
    }

    @Override
    public AbstractCE mergeInstance(AbstractCE c, int[] position, char positionChar, CEValue preferredValue, double zc) {
        TrueFalseCE c2 = (TrueFalseCE) c;
        TrueFalseCE result = new TrueFalseCE(this.value);
        for (int i : position) {
            result.value[i] = positionChar;
        }
        System.arraycopy(this.statisticValue, 0, result.statisticValue, 0, 4);
        for (int i = 0; i < 4; i++) {
            result.statisticValue[i] += c2.statisticValue[i];
        }
        if (preferredValue != null) {
            result.cEValue = preferredValue;
        } else {
            result.updateCEValue(zc);
        }
        return result;
    }


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
        double z = (Math.abs(p1 - p2) - temp / 2)
                / Math.sqrt(p_av * (1.0 - p_av) * temp);

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

    @Override
    public int getInstanceNumber() {
        return statisticValue[0] + statisticValue[1]
                + statisticValue[2] + statisticValue[3];
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
        sb.append(String.format(Locale.CHINA, "%.4f", p1 - p2));
        return sb.toString();
    }
}
