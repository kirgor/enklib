package com.kirgor.enklib.ejb;

import java.util.HashMap;

/**
 * Default implementation of {@link SecurityTokenStorage}, which uses {@link HashMap} to store token/principal pairs.
 * Obviously, user sessions will not be persistent between service restarts.
 */
public class HashMapSecurityTokenStorage implements SecurityTokenStorage {
    private final HashMap<String, Object> hashMap = new HashMap<String, Object>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(String token, Object principal) {
        hashMap.put(token, principal);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(String token) {
        return hashMap.get(token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String token) {
        hashMap.remove(token);
    }
}
