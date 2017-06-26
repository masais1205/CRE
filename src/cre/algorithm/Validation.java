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
 * Created by HanYizhao on 2017/5/16.
 */
public class Validation {

    private static class Simple implements Comparable<Simple> {
        public Double value;
        public int position;

        public Simple(Double value, int position) {
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

    public static class StratifiedSampleHelper {

        private List<Point> needShuffle = new ArrayList<>();
        private List<Integer> orderedPositions = new ArrayList<>();
        private List<Integer> samplePositions = new ArrayList<>();
        private Random random = new Random(1);

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

    public static class SliceLinesHelper {
        public int[] nextLines(Integer preferredFold) {
            Collections.shuffle(nowResult, random);
            int[] result = new int[nowResult.size()];
            for (int i = 0; i < nowResult.size(); i++) {
                result[i] = nowResult.get(i);
            }
            if (preferredFold != null) {
                int test = preferredFold;
                int train = -preferredFold;
                if (preferredFold == 0) {
                    train = -1;
                }
                for (int i = 0; i < result.length; i++) {
                    result[i] = result[i] == 1 ? test : train;
                }
            }
            return result;
        }


        public SliceLinesHelper(String fileName, String delimiter, int position,
                                int testingRatio, int attributeLength,
                                CanShowOutput canShowOutput) throws CalculatingException {
            BufferedReader br = null;
            List<Simple> buffer = new ArrayList<>();
            try {
                br = new BufferedReader(new FileReader(fileName));
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
            nowResult = new ArrayList<>(buffer.size());
            int[] tempResult = new int[buffer.size()];
            for (int i = 0; i < buffer.size(); i++) {
                Simple s = buffer.get(i);
                tempResult[s.position] = i % 100 < testingRatio ? 1 : 0;
            }
            for (int aTempResult : tempResult) {
                nowResult.add(aTempResult);
            }
        }

        private List<Integer> nowResult;
        private Random random = new Random(1);

    }

    /**
     * This function is used to classify the lines.
     * For example, the data file has 100 lines except the first line.
     * In order to do cross-validation, we need to slice these lines into
     * several subsets (the number of fold).
     * If fold is 5, these 100 lines are divided to 5 stratified parts.
     * The principle of stratification depends on values of attribute in position 'position'.
     *
     * @param fileName        Name of data file
     * @param delimiter       Delimiter of attributes
     * @param position        The position of attribute (from 0)
     * @param fold            The fold
     * @param attributeLength The number of attributes
     * @param canShowOutput   Used to show messages
     * @return Just as example above, the returned value will be the group id of each line,
     * like 1,2,3,4,0,2,3,4,0,1...
     */
    public static int[] sliceLines(String fileName, String delimiter, int position,
                                   int fold, int attributeLength,
                                   CanShowOutput canShowOutput) throws CalculatingException {
        BufferedReader br = null;
        List<Simple> buffer = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(fileName));
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
        int[] result = new int[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            Simple s = buffer.get(i);
            result[s.position] = i % fold;
        }
        return result;
    }
}
