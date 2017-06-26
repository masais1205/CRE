package cre.algorithm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * K均值聚类算法
 */
public class KMeans {
    private int k;// 分成多少簇
    private int m;// 迭代次数
    private int dataSetLength;// 数据集元素个数，即数据集的长度
    private ArrayList<double[]> dataSet;// 数据集链表
    private ArrayList<double[]> center;// 中心链表
    private ArrayList<ArrayList<double[]>> cluster; // 簇
    private ArrayList<Double> jc;// 误差平方和，k越接近dataSetLength，误差越小
    private Random random;
    private int dimLength;//一个点的维度


    /**
     * 构造函数，传入需要分成的簇数量
     *
     * @param k 簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
     */
    public KMeans(int k, ArrayList<double[]> dataSet) {
        if (k <= 0) {
            k = 1;
        }
        this.k = k;
        if (dataSet == null || dataSet.size() == 0) {
            throw new IllegalArgumentException("There is no data");
        }
        this.dimLength = dataSet.get(0).length;
        for (double[] i : dataSet) {
            if (i.length != dimLength) {
                throw new IllegalArgumentException("Dim difference");
            }
        }
        this.dataSet = dataSet;
    }

    /**
     * 初始化
     */
    private void init() {
        m = 0;
        random = new Random(1);
        dataSetLength = dataSet.size();
        if (k > dataSetLength) {
            k = dataSetLength;
        }
        center = initCenters();
        cluster = initCluster();
        jc = new ArrayList<>();
    }

    /**
     * 初始化中心数据链表，分成多少簇就有多少个中心点
     *
     * @return 中心点集
     */
    private ArrayList<double[]> initCenters() {
        ArrayList<double[]> center = new ArrayList<>();
        int nowStart = 0;
        for (int i = 0; i < k; i++) {
            int end = (int) (((double) dataSetLength) * (i + 1) / k);
            center.add(dataSet.get((nowStart + end) / 2));
            nowStart = end;
        }
        return center;
    }

    /**
     * 初始化簇集合
     *
     * @return 一个分为k簇的空数据的簇集合
     */
    private ArrayList<ArrayList<double[]>> initCluster() {
        ArrayList<ArrayList<double[]>> cluster = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            cluster.add(new ArrayList<double[]>());
        }

        return cluster;
    }

    /**
     * 获取距离集合中最小距离的位置
     *
     * @param distance 距离数组
     * @return 最小距离在距离数组中的位置
     */
    private int minDistance(double[] distance) {
        double minDistance = distance[0];
        int minLocation = 0;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i] < minDistance) {
                minDistance = distance[i];
                minLocation = i;
            } else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
            {
                if (random.nextInt(10) < 5) {
                    minLocation = i;
                }
            }
        }

        return minLocation;
    }

    /**
     * 核心，将当前元素放到最小距离中心相关的簇中
     */
    private void clusterSet() {
        double[] distance = new double[k];
        for (int i = 0; i < dataSetLength; i++) {
            for (int j = 0; j < k; j++) {
                distance[j] = errorSquare(dataSet.get(i), center.get(j));
                // System.out.println("test2:"+"dataSet["+i+"],center["+j+"],distance="+distance[j]);

            }
            int minLocation = minDistance(distance);
            // System.out.println("test3:"+"dataSet["+i+"],minLocation="+minLocation);
            // System.out.println();

            cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中

        }
    }

    /**
     * 求两点误差平方的方法
     *
     * @param element 点1
     * @param center  点2
     * @return 误差平方
     */
    private double errorSquare(double[] element, double[] center) {
        double sum = 0;
        for (int i = 0; i < element.length; i++) {
            sum += Math.pow(element[i] - center[i], 2);
        }
        return sum;
    }

    /**
     * 计算误差平方和准则函数方法
     */
    private void countRule() {
        double jcF = 0;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                jcF += errorSquare(cluster.get(i).get(j), center.get(i));
            }
        }
        jc.add(jcF);
    }

    /**
     * 设置新的簇中心方法
     */
    private void setNewCenter() {
        for (int i = 0; i < k; i++) {
            int n = cluster.get(i).size();
            if (n != 0) {
                double[] newCenter = new double[dimLength];
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < dimLength; k++) {
                        newCenter[k] += cluster.get(i).get(j)[k];
                    }
                }
                // 设置一个平均值
                for (int k = 0; k < dimLength; k++) {
                    newCenter[k] = newCenter[k] / n;
                }
                center.set(i, newCenter);
            }
        }
    }

    /**
     * 打印数据，测试用
     *
     * @param dataArray     数据集
     * @param dataArrayName 数据集名称
     */
    public void printDataArray(ArrayList<double[]> dataArray,
                               String dataArrayName) {
        for (int i = 0; i < dataArray.size(); i++) {
            System.out.println("print:" + dataArrayName + "[" + i + "]={"
                    + Arrays.toString(dataArray.get(i)) + "}");
        }
        System.out.println("===================================");
    }

    /**
     * Kmeans算法核心过程方法
     */
    private void kmeans() {
        init();
        // printDataArray(dataSet,"initDataSet");
        // printDataArray(center,"initCenter");

        // 循环分组，直到误差不变为止
        while (true) {
            clusterSet();
            // for(int i=0;i<cluster.size();i++)
            // {
            // printDataArray(cluster.get(i),"cluster["+i+"]");
            // }

            countRule();

            // System.out.println("count:"+"jc["+m+"]="+jc.get(m));

            // System.out.println();
            // 误差不变了，分组完成
            if (m != 0) {
                if (jc.get(m) - jc.get(m - 1) == 0) {
                    break;
                }
            }

            setNewCenter();
            // printDataArray(center,"newCenter");
            m++;
            cluster.clear();
            cluster = initCluster();
        }

        System.out.println("note:the times of repeat:m=" + m);//输出迭代次数
    }

    /**
     * 执行算法
     */
    public ArrayList<ArrayList<double[]>> execute() {
        long startTime = System.currentTimeMillis();
        System.out.println("kmeans begins");
        kmeans();
        long endTime = System.currentTimeMillis();
        System.out.println("kmeans running time=" + (endTime - startTime)
                + "ms");
        System.out.println("kmeans ends");
        System.out.println();
        return cluster;
    }
}

