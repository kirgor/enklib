package com.kirgor.tools.ejb.exception;

public class APIException extends Exception {
    private int httpStatus;

    public APIException(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public APIException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public APIException(String message, Throwable cause, int httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public APIException(Throwable cause, int httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
