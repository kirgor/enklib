package com.kirgor.enklib.rest;

import java.util.Map;

/**
 * Represents response to request.
 */
public class Response {
    private int code;
    private Map<String, String> headers;

    /**
     * Creates {@link Response} instance.
     *
     * @param code    HTTP response code.
     * @param headers Map of HTTP response headers.
     */
    public Response(int code, Map<String, String> headers) {
        this.code = code;
        this.headers = headers;
    }

    /**
     * Gets HTTP response code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets map of HTTP response headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets HTTP response header value.
     *
     * @param name Header name.
     * @return Header value or null.
     */
    public String getHeaderValue(String name) {
        if (headers == null) {
            return null;
        }
        return headers.get(name);
    }
}
