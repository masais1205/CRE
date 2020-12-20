package cre.algorithm.tool;

import cre.algorithm.CalculatingException;
import cre.algorithm.CanShowOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HanYizhao on 2017/5/16.
 */
public class FileTool {

    /**
     * Get the median value of an attribute in a file.
     * This file must be a csv file and can be read by calling {@link BufferedReader#readLine()}
     *
     * @param fileName             Name of file
     * @param delimiter            Delimiter of attributes
     * @param position             The position of this attribute
     * @param attributeLength      The num of all attributes
     * @param crossValidationGroup The group ID of each line
     * @param nowFold              Now Fold
     * @return The double value, if the value of this attribute is numeric.
     * Null, if the value of this attribute is 1 or 0 or when exception happened.
     */
    public static Double getMedianOfAttribute(String fileName,
                                              String delimiter,
                                              int position,
                                              int attributeLength,
                                              int[] crossValidationGroup,
                                              int nowFold) throws CalculatingException {
        List<Double> yValueList = new ArrayList<>();
        String tempS;
        BufferedReader brPre = null;
        boolean simpleTrueFalse = true;
        int count = 2;
        try {
            brPre = new BufferedReader(new FileReader(fileName));
            brPre.readLine();
            while ((tempS = brPre.readLine()) != null) {
                if (crossValidationGroup == null || crossValidationGroup[count - 2] != nowFold) {
                    String[] tempSS = tempS.split(delimiter);
                    if (tempSS.length == attributeLength) {
                        if (!tempSS[position].equals("1")
                                && !tempSS[position].equals("0")
                                && simpleTrueFalse) {
                            simpleTrueFalse = false;
                        }
                        yValueList.add(Double.parseDouble(tempSS[position]));
                    } else {
                        String message = "Line value ERROR: (line:" + count + ") " + tempS;
                        throw new CalculatingException(message);
                    }
                }
                count++;
            }
            if (!simpleTrueFalse) {
                Collections.sort(yValueList);
                int length = yValueList.size();
                if (length % 2 != 0) {
                    return yValueList.get(length / 2);
                } else {
                    return (yValueList.get(length / 2) + yValueList.get(length / 2 - 1)) / 2;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (brPre != null) {
                try {
                    brPre.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    public static List<String> getFileContent(File file) {
        List<String> result = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String temp;
            while ((temp = br.readLine()) != null) {
                result.add(temp);
            }
        } catch (Exception e) {
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
        return result;
    }
}
