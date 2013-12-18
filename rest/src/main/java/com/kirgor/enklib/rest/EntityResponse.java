package com.kirgor.enklib.rest;

import java.util.Map;

public class EntityResponse<T> extends Response {
    private T entity;

    public EntityResponse(int code, Map<String, String> headers, T entity) {
        super(code, headers);
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }
}
