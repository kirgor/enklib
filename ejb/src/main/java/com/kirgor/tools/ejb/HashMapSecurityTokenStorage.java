package com.kirgor.tools.ejb;

import java.util.HashMap;

public class HashMapSecurityTokenStorage implements SecurityTokenStorage {
    private final HashMap<String, Object> hashMap = new HashMap<String, Object>();

    @Override
    public void add(String token, Object principal) {
        hashMap.put(token, principal);
    }

    @Override
    public Object get(String token) {
        return hashMap.get(token);
    }

    @Override
    public void remove(String token) {
        hashMap.remove(token);
    }
}
