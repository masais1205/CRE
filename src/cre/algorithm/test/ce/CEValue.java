package cre.algorithm.test.ce;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public enum CEValue {
    PLUS("+"), MINUS("-"), QUESTION("?");

    private String name;

    CEValue(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
