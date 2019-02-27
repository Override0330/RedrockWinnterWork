package com.redrockwork.overrdie.firstdemo.bihu;

public class UnCurrentUserException extends Exception {
    public UnCurrentUserException() {
        super();
    }

    public UnCurrentUserException(String message) {
        super(message);
    }

    public UnCurrentUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnCurrentUserException(Throwable cause) {
        super(cause);
    }

    protected UnCurrentUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
