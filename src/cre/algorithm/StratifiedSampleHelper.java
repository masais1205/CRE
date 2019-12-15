package cre.algorithm;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by HanYizhao.
 * <p>
 * This class is used to do stratified validation.
 * Now it can only handle csv file.
 * <p>
 * 1. Sort the lines according to the specific attribute (stratified attribute).
 * <p>
 * 2. Divided these lines into 10 groups using K-means. Thus, tuple in each group will have approximate value.
 * The groups are sorted from smaller to bigger.
 * <p>
 * 3. When call {@link #nextLines()}, shuffle in each group.
 * <p>
 * 4. If 10 folds cross-validation, distribute group-ID using 0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,...
 * If simple validation, distribute group-ID using stable 0,1,0,0,1,1,...
 * The number of 0 and 1 depends on the percent of testing data. This sequence has been shuffled.
 */
public class StratifiedSampleHelper {

    private static class Simple implements Comparable<Simple> {
        public Double value;
        public int position;

        Simple(Double value, int position) {
            this.value = value;
            this.position = position;
        }

        @Override
        public int compareTo(Simple o) {
            if (this.value < o.value) {
                return -1;
            } else if (this.value > o.value) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private List<Point> needShuffle = new ArrayList<>();
    private List<Integer> orderedPositions = new ArrayList<>();
    private List<Integer> samplePositions = new ArrayList<>();
    private Random random = new Random(1);

    /**
     * If cross-validation, the lines of ths csv file will be divided to several groups(folds).
     * Each line has a group Id, from 0 to (folds - 1). The result will be different if call this function repeatedly.
     * <p>
     * If simple validation, the lines of the csv file will be divided to two group, group 0 and group 1.
     * Group 0 means it will be used to do testing. Group 1 means it will be used to do training.
     * Each line has a group Id. The result will be different if call this function repeatedly.
     *
     * @return The group Id of every line.
     */
    public int[] nextLines() {
        int[] result = new int[orderedPositions.size()];
        for (Point i : needShuffle) {
            Collections.shuffle(orderedPositions.subList(i.x, i.y), random);
        }
        for (int i = 0; i < orderedPositions.size(); i++) {
            result[orderedPositions.get(i)] = samplePositions.get(i);
        }
        return result;
    }


    /**
     * @param csvFileName     The csv file.
     * @param testCSVFileName The test csv file.
     * @param delimiter       Delimiter of the csv file.
     * @param position        The position of attribute which is used to stratify.
     * @param value           1/repeatTimes, randomly select 1/repeatTimes data as training.
     * @param attributeLength The number of attributes.
     * @param canShowOutput   Used to show output.
     * @throws CalculatingException May cause some error.
     */
    public StratifiedSampleHelper(String csvFileName, String testCSVFileName, String delimiter, int position,
                                  double value, int attributeLength, CanShowOutput canShowOutput) throws CalculatingException {
        BufferedReader br = null;
        List<Simple> buffer = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(csvFileName));
            br.readLine();
            String temp;
            int count = 2;
            while ((temp = br.readLine()) != null) {
                String[] strings = temp.split(delimiter);
                if (strings.length == attributeLength) {
                    buffer.add(new Simple(Double.parseDouble(strings[position]),
                            count - 2));
                } else {
                    String message = "Line value ERROR: (line:" + count + ") " + temp;
                    if (canShowOutput != null) {
                        canShowOutput.showOutputString(message);
                    }
                    throw new CalculatingException(message);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(buffer);

        BufferedReader testBr = null;
        List<Simple> testBuffer = new ArrayList<>();
        try {
            testBr = new BufferedReader(new FileReader(testCSVFileName));
            br.readLine();
            String temp;
            int count = 2;
            while ((temp = testBr.readLine()) != null) {
                String[] strings = temp.split(delimiter);
                if (strings.length == attributeLength) {
                    testBuffer.add(new Simple(Double.parseDouble(strings[position]),
                            count - 2));
                } else {
                    String message = "Line value ERROR: (line:" + count + ") " + temp;
                    if (canShowOutput != null) {
                        canShowOutput.showOutputString(message);
                    }
                    throw new CalculatingException(message);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (testBr != null) {
                try {
                    testBr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(testBuffer);

        ArrayList<double[]> kMeansData = new ArrayList<>();
        for (Simple i : buffer) {
            kMeansData.add(new double[]{i.value});
        }
        KMeans kmeans = new KMeans(10, kMeansData);
        ArrayList<ArrayList<double[]>> cluster = kmeans.execute();
        List<Simple> clusterStatistic = new ArrayList<>();
        for (ArrayList<double[]> i : cluster) {
            double min = Double.MAX_VALUE;
            for (double[] k : i) {
                if (k[0] < min) {
                    min = k[0];
                }
            }
            clusterStatistic.add(new Simple(min, i.size()));
        }
        Collections.sort(clusterStatistic);
        int start = 0;
        for (Simple i : clusterStatistic) {
            System.out.println(i.value + " " + i.position);
            needShuffle.add(new Point(start, start + i.position));
            start += i.position;
        }
        for (Simple i : buffer) {
            orderedPositions.add(i.position);
        }

        int fold = (int) Math.round(value);
        if (fold < 2) {
            fold = 2;
        }
        if (fold > 100) {
            fold = 100;
        }
        for (int i = 0; i < buffer.size(); i++) {
            samplePositions.add(i % fold);
        }
    }


    /**
     * @param csvFileName     The csv file.
     * @param delimiter       Delimiter of the csv file.
     * @param position        The position of attribute which is used to stratify.
     * @param crossValidation True, cross-validation; false, simple validation.
     * @param value           If cross-validation, the value is fold. If simple validation, the value is percents of testing data, from 0 to 1.
     * @param attributeLength The number of attributes.
     * @param canShowOutput   Used to show output.
     * @throws CalculatingException May cause some error.
     */
    public StratifiedSampleHelper(String csvFileName, String delimiter, int position,
                                  boolean crossValidation, double value, int attributeLength,
                                  CanShowOutput canShowOutput) throws CalculatingException {
        BufferedReader br = null;
        List<Simple> buffer = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(csvFileName));
            br.readLine();
            String temp;
            int count = 2;
            while ((temp = br.readLine()) != null) {
                String[] strings = temp.split(delimiter);
                if (strings.length == attributeLength) {
                    buffer.add(new Simple(Double.parseDouble(strings[position]),
                            count - 2));
                } else {
                    String message = "Line value ERROR: (line:" + count + ") " + temp;
                    if (canShowOutput != null) {
                        canShowOutput.showOutputString(message);
                    }
                    throw new CalculatingException(message);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Collections.sort(buffer);
        ArrayList<double[]> kMeansData = new ArrayList<>();
        for (Simple i : buffer) {
            kMeansData.add(new double[]{i.value});
        }
        KMeans kmeans = new KMeans(10, kMeansData);
        ArrayList<ArrayList<double[]>> cluster = kmeans.execute();
        List<Simple> clusterStatistic = new ArrayList<>();
        for (ArrayList<double[]> i : cluster) {
            double min = Double.MAX_VALUE;
            for (double[] k : i) {
                if (k[0] < min) {
                    min = k[0];
                }
            }
            clusterStatistic.add(new Simple(min, i.size()));
        }
        Collections.sort(clusterStatistic);
        int start = 0;
        for (Simple i : clusterStatistic) {
            needShuffle.add(new Point(start, start + i.position));
            start += i.position;
        }
        for (Simple i : buffer) {
            orderedPositions.add(i.position);
        }
        if (crossValidation) {
            int fold = (int) Math.round(value);
            if (fold < 2) {
                fold = 2;
            }
            if (fold > 100) {
                fold = 100;
            }
            for (int i = 0; i < buffer.size(); i++) {
                samplePositions.add(i % fold);
            }
        } else {
            if (value > 1) {
                value = 0.5;
            }
            if (value < 0) {
                value = 0.5;
            }
            int zeroSize = (int) Math.round(buffer.size() * value);
            int zeroCount = 0;
            int oneSize = buffer.size() - zeroSize;
            int oneCount = 0;
            for (int i = 0; i < buffer.size(); i++) {
                boolean preferZero = random.nextInt(buffer.size()) < zeroSize;
                if (preferZero) {
                    if (zeroCount < zeroSize) {
                        samplePositions.add(0);
                        zeroCount++;
                    } else {
                        samplePositions.add(1);
                        oneCount++;
                    }
                } else {
                    if (oneCount < oneSize) {
                        samplePositions.add(1);
                        oneCount++;
                    } else {
                        samplePositions.add(0);
                        zeroCount++;
                    }
                }
            }
        }
    }
}