package cre.algorithm.test.ce;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

import static java.lang.Math.abs;

/**
 * Created by Saisai MA on 5/10/2019.
 */

public class DistMeasure {
    public Table<Integer, Integer, String> xorMatrix = HashBasedTable.create();
    public Table<Integer, Integer, Integer> distanceMatrix = HashBasedTable.create();
    
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

                    Integer r1 = ((minDistLocation) o1).cntUnreliable;
                    Integer r2 = ((minDistLocation) o2).cntUnreliable;
                    sComp = r2.compareTo(r1);
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
            xorMatrix.put(j,j,new String(CEList.get(j).value));
            distanceMatrix.put(j,j,n);
        }
    }
}
