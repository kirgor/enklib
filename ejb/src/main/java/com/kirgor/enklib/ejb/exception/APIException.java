package com.kirgor.enklib.ejb.exception;

/**
 * Exception, which raises when some RESTful API request failed.
 * <p/>
 * It's handled automatically by {@link com.kirgor.enklib.ejb.Bean} handleAPIException method,
 * which will return response on behalf of failed API method.
 */
public class APIException extends Exception {
    private int httpStatus;

    /**
     * Create {@link APIException} with specified HTTP status.
     *
     * @param httpStatus HTTP status of desired response.
     */
    public APIException(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    /**
     * Create {@link APIException} with specified message and HTTP status.
     *
     * @param message    Exception message,
     * @param httpStatus HTTP status of desired response.
     */
    public APIException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    /**
     * Create {@link APIException} with specified message, cause and HTTP status.
     *
     * @param message    Exception message,
     * @param cause      Exception cause.
     * @param httpStatus HTTP status of desired response.
     */
    public APIException(String message, Throwable cause, int httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    /**
     * Create {@link APIException} with specified cause and HTTP status.
     *
     * @param cause      Exception cause.
     * @param httpStatus HTTP status of desired response.
     */
    public APIException(Throwable cause, int httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    /**
     * Gets HTTP status of response, which will be generated.
     */
    public int getHttpStatus() {
        return httpStatus;
    }
}
