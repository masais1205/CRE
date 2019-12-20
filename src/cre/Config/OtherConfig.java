package cre.Config;

/**
 * Created by HanYizhao on 2017/5/15.
 * Test options
 */
public class OtherConfig {

    public enum Validation {
        VALIDATION, CROSS_VALIDATION, SUPPLIED_TEST_DATA, NONE
    }

    private int crossValidationFolds;
    private Validation validation;
    private String testFile;
    private String groundTruthFile;
    private int validationRepeatTimes;
    private int test;

    public OtherConfig(Validation validation, int validationRepeatTimes, String testFile, int test, int fold, String groundTruthFile) throws Exception {
        if (validation == null) {
            throw new Exception("OtherConfig need a validation type.");
        }
        if (validationRepeatTimes > 100 || validationRepeatTimes < 1) {
            throw new Exception("Repeat times must be between 1 and 100");
        }
        if (test > 99 || test < 0) {
            throw new Exception("'Test' must be between 1 and 99");
        }
        if (fold > 10 || fold < 2) {
            throw new Exception("'Fold' must be between 2 and 10");
        }
        this.validation = validation;
        this.testFile = testFile;
        this.groundTruthFile = groundTruthFile;
        this.validationRepeatTimes = validationRepeatTimes;
        this.test = test;
        this.crossValidationFolds = fold;
    }

    public String getTestFile() {return testFile;}

    public String getGroundTruthFile() {return groundTruthFile;}

    public int getCrossValidationFolds() {
        return crossValidationFolds;
    }

    public Validation getValidation() {
        return validation;
    }

    public int getValidationRepeatTimes() {
        return validationRepeatTimes;
    }

    public int getTest() {
        return test;
    }
}
