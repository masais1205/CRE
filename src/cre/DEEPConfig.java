package cre;

import com.github.jankroken.commandline.annotations.*;

import java.util.List;

public class DEEPConfig {

    private String trainFile;
    private String testFile;
    private boolean featureSelection;
    private DEEPAttributes attr;
    private String significanceLevel;

    @Option
    @LongSwitch("train")
    @ShortSwitch("r")
    @SingleArgument
    public void setTrainFile(String trainFile) {
        this.trainFile = trainFile;
    }

    @Option
    @LongSwitch("test")
    @ShortSwitch("t")
    @SingleArgument
    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    @Option
    @LongSwitch("featureSelection")
    @ShortSwitch("f")
    @Toggle(true)
    public void setFeatureSelection(boolean featureSelection) {
        this.featureSelection = featureSelection;
    }

    @Option
    @LongSwitch("attr")
    @ShortSwitch("a")
    @SubConfiguration(DEEPAttributes.class)
    public void setAttr(DEEPAttributes attr) {
        this.attr = attr;
    }

    @Option
    @LongSwitch("significance")
    @ShortSwitch("s")
    @SingleArgument
    public void setSignificanceLevel(String significanceLevel) {
        this.significanceLevel = significanceLevel;
    }


    public String getTrainFile() {
        return trainFile;
    }
    public String getTestFile() {
        return testFile;
    }
    public boolean getFeatureSelection() {
        return featureSelection;
    }
    public DEEPAttributes getAttr() { return attr; }
    public String getSignificanceLevel() {
        return significanceLevel;
    }
}
