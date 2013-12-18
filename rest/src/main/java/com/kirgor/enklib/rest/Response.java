package com.kirgor.enklib.rest;

import java.util.Map;

public class Response {
    private int code;
    private Map<String, String> headers;

    public Response(int code, Map<String, String> headers) {
        this.code = code;
        this.headers = headers;
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
}
