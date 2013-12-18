package com.kirgor.enklib.ejb;

public interface SecurityTokenStorage {
    void add(String token, Object principal);

    Object get(String token);

    void remove(String token);
}
