package cre.algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by HanYizhao on 2017/5/16.
 */
public class CrossValidation {

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
