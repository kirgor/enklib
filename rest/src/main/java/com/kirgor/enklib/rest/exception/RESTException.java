package com.kirgor.enklib.rest.exception;

import java.util.Map;

/**
 * Thrown when request failed due to non OK response code.
 */
public class RESTException extends Exception {
    private int code;
    private Map<String, String> headers;
    private Object entity;

    /**
     * Creates {@link RESTException} instance.
     *
     * @param code    HTTP response code.
     * @param headers Map of HTTP headers.
     * @param entity  Response entity.
     */
    public RESTException(int code, Map<String, String> headers, Object entity) {
        this.code = code;
        this.headers = headers;
        this.entity = entity;
    }

    /**
     * Gets HTTP response code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets map of response HTTP headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets value of specified HTTP response header.
     *
     * @param name HTTP header name.
     * @return Value of the header or null, if header is not present.
     */
    public String getHeaderValue(String name) {
        if (headers == null) {
            return null;
        }
        return headers.get(name);
    }

    /**
     * Gets response entity or null.
     */
    public Object getEntity() {
        return entity;
    }
}
