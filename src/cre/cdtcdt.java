//package cre;
//
//import cre.Config.OtherConfig;
//import cre.algorithm.CanShowOutput;
//import cre.algorithm.CanShowStatus;
//import cre.algorithm.cdt.CDTAlgorithm;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//
///**
// * Created by maysy020 on 15/09/2017.
// */
//public class cdtcdt {
//    public static void main(String[] args) {
//        CanShowOutput output = new CanShowOutput() {
//            @Override
//            public void showOutputString(String value) {
//                FileWriter fWrite;
//                BufferedWriter fp;
//                String fn = "C:\\Users\\maysy020\\Desktop\\personalisedDecision\\fourDataSet\\CDTsplitData\\Job.txt";
//
//                try {
//                    fWrite = new FileWriter(fn, true);
//                    fp = new BufferedWriter(fWrite);
//                    fprintf(fp, "\n", value);
//
//                    fp.close();
//                    fWrite.close();
//                } catch (Exception e) {
//                    System.out.println(e);
//                }
//            }
//
//            @Override
//            public void showLogString(String value) {
//                System.out.println(value);
//            }
//        };
//
//        CanShowStatus status = new CanShowStatus() {
//            @Override
//            public void showStatus(String value) {
//
//            }
//        };
//
//        String path = "C:\\Users\\maysy020\\Desktop\\personalisedDecision\\fourDataSet\\CDTsplitData\\";
//        String file = "Job-binary-";
//        for (int i = 1; i < 21; i++) {
//            String pathname = path + file + i + "a.csv";
//            System.out.println(pathname);
//            CDTAlgorithm algorithm = new CDTAlgorithm(new File(pathname));
//            algorithm.config.setHeight(50);
//            algorithm.config.setPruned(true);
//            CDTAlgorithm newAlgorithm = (CDTAlgorithm) algorithm.clone();
//            try {
//                newAlgorithm.doAlgorithm(output, status,
//                        new OtherConfig(OtherConfig.Validation.NONE, 2, 50, 10));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    public static void fprintf(BufferedWriter out, String a, String b) {
//        try {
//            out.write(a + b);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
