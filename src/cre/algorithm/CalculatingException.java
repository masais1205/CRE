package cre.algorithm;

/**
 * Created by HanYizhao on 2017/5/16.
 */
public class CalculatingException extends Exception {
    public CalculatingException() {
    }

    public CalculatingException(String message) {
        super(message);
    }

    public CalculatingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CalculatingException(Throwable cause) {
        super(cause);
    }

    public CalculatingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
