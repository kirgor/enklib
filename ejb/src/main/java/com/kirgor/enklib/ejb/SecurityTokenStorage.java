package com.kirgor.enklib.ejb;

/**
 * Defines security token storage, which associates security tokens with user principals and used for cookie based authentication.
 */
public interface SecurityTokenStorage {
    /**
     * Adds token/principal pair.
     *
     * @param token     Security token.
     * @param principal User principal.
     */
    void add(String token, Object principal);

    /**
     * Gets user principal by security token (may return null).
     */
    Object get(String token);

    /**
     * Removes security token/principal pair from the storage.
     */
    void remove(String token);
}
