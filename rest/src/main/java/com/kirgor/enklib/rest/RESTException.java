package com.kirgor.enklib.rest;

import java.util.Map;

public class RESTException extends Exception {
    private int code;
    private Map<String, String> headers;
    private Object entity;

    public RESTException(int code, Map<String, String> headers, Object entity) {
        this.code = code;
        this.headers = headers;
        this.entity = entity;
    }

    public int getCode() {
        return code;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeaderValue(String name) {
        if (headers == null) {
            return null;
        }
        return headers.get(name);
    }

    public Object getEntity() {
        return entity;
    }
}
