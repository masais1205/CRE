package cre.algorithm.test.ce;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.sun.deploy.util.StringUtils;

import java.util.*;
import java.lang.String;

import static com.google.common.base.Strings.repeat;
import static java.lang.Math.abs;

/**
 * Created by Saisai MA on 5/10/2019.
 */

public class DistMeasure {

    public static final char char_Star = '*';
    public static final char char_QUESTION = '×';

    public static Table<Integer, Integer, String> xorMatrix = HashBasedTable.create(); // xor matrix, rowIndex, colIndex, xor
    public Table<Integer, Integer, Integer> distanceMatrix = HashBasedTable.create(); // distance matrix, rowIndex, colIndex, distance
                                                                                      // distance between Record rowIndex and Record colIndex
    
    public DistMeasure() {
    }
    
    public static class minDistLocation{
        public int rowIndex, colIndex, minDistance, cntUnreliable;
        public double diffCE;

        public minDistLocation(int rowIndex, int colIndex, int minDistance, int cntUnreliable, double diffCE) {
            this.rowIndex = rowIndex;
            this.colIndex = colIndex;
            this.minDistance = minDistance;
            this.cntUnreliable = cntUnreliable;
            this.diffCE = diffCE;
        }

        public static void sortDist(List<minDistLocation> location) {

            Collections.sort(location, new Comparator() {

                public int compare(Object o1, Object o2) {

                    Integer d1 = ((minDistLocation) o1).minDistance;
                    Integer d2 = ((minDistLocation) o2).minDistance;
                    int sComp = d1.compareTo(d2);
                    if (sComp != 0) {
                        return sComp;
                    }

                    Double e1 = ((minDistLocation) o1).diffCE;
                    Double e2 = ((minDistLocation) o2).diffCE;
                    return e1.compareTo(e2);
                }});
        }
    }

    public char[] calcXOR(char[] a, char[] b) {
        char[] xor = new char[a.length];
        for(int i=0; i<a.length; i++)
            xor[i] = a[i]==b[i] ? '0' : '1';
        return xor;
    }

    public void buildDistanceMatrix(List<AbstractCE> CEList) {
        int n = CEList.size();
        char [] xor;
        String xorString;
        int distance;

        for (int j=1; j<n; j++) {
            for (int k=0; k<j; k++) {
                xor = calcXOR(CEList.get(j).value,
                        CEList.get(k).value);
                xorString = new String(xor);
                xorMatrix.put(j,k,xorString);
                xorMatrix.put(k,j,xorString);
                distance = 0;
                for(int i=0; i<xor.length; i++)
                    if(xor[i] == '1')
                        distance += 1;
                distanceMatrix.put(j,k,distance);
                distanceMatrix.put(k,j,distance*n);
            }
        }
        for(int j=0; j<n; j++) {
            xorMatrix.put(j,j, repeat("0", CEList.get(j).value.length));
            distanceMatrix.put(j,j,n);
        }
    }

    public static void updateXorMatrix(int rowIndex, int colSize, int[] positions, char positionChar) {
        String xor;
        for (int c=0; c<colSize; c++) {
            for (int p : positions) {
                xor = xorMatrix.get(rowIndex, c);
                char[] xorChars = xor.toCharArray();
                xorChars[p] = positionChar;
                xor = String.valueOf(xorChars);
                xorMatrix.put(rowIndex, c, xor);
            }
        }
    }
}
