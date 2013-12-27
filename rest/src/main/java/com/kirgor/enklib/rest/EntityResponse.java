package com.kirgor.enklib.rest;

import java.util.Map;

/**
 * Represents response to request, which has enclosed entity.
 *
 * @param <T> Type of response entity.
 */
public class EntityResponse<T> extends Response {
    private T entity;

    /**
     * Creates {@link EntityResponse} instance.
     *
     * @param code    HTTP response code.
     * @param headers Map of HTTP response headers.
     * @param entity  Enclosed entity.
     */
    public EntityResponse(int code, Map<String, String> headers, T entity) {
        super(code, headers);
        this.entity = entity;
    }

    /**
     * Gets enclosed entity or null.
     */
    public T getEntity() {
        return entity;
    }
}
