package io.github.pantomath.location.exception;

public class LookupException extends  Exception{
    public LookupException() {
    }

    public LookupException(String message) {
        super(message);
    }

    public LookupException(String message, Throwable cause) {
        super(message, cause);
    }

    public LookupException(Throwable cause) {
        super(cause);
    }

    public LookupException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
