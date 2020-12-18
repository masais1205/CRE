package cre;

import java.util.List;
import com.github.jankroken.commandline.*;
import com.github.jankroken.commandline.annotations.*;


public class DEEPStandalone {
    public static final void main(String[] args) {
        try {
            DEEPConfig dbConfig = CommandLineParser.parse(DEEPConfig.class, args,
                    OptionStyle.LONG_OR_COMPACT);

            String trainFile = dbConfig.getTrainFile();
            System.out.println(trainFile==null);
            if (trainFile == null) {
                System.out.println("NO train data provided!!!");
                throw new Exception();
            }
            String testFile = dbConfig.getTestFile();
            if (testFile == null) {
                System.out.println("NO test data provided!!!");
                throw new Exception();
            }

        } catch (Exception e) {
        }
    }
}