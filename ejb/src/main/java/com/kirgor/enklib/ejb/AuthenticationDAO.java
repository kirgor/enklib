package com.kirgor.enklib.ejb;

import com.kirgor.enklib.sql.Session;
import com.kirgor.enklib.sql.proxy.StoredProcedureProxyFactory;

/**
 * Defines methods, which access database in order to manipulate users.
 * Also, defines methods to retrieve password hash and salt from the entity.
 * <p/>
 * Implementation of this interface is used by {@link Bean} to support login and register.
 * Configuration field "authenticationDAOClassName" defines which implementation to use.
 */
public interface AuthenticationDAO {
    /**
     * Adds new user to the database.
     *
     * @param session                     {@link Session} instance, which is connected to the database.
     * @param storedProcedureProxyFactory Factory, which can be used to generate stored procedure proxy for convenience.
     * @param principal                   User's principal, which was used to register (e.g. ID or password).
     * @param passwordHash                Hashed user password.
     * @param passwordSalt                Salt, which was used to hash the password.
     * @param extraData                   Some object, which contains extra data associated with creting user
     *                                    (e.g. name, last name and other fields).
     * @throws Exception
     */
    void addNewUser(Session session, StoredProcedureProxyFactory storedProcedureProxyFactory,
                    Object principal, String passwordHash, String passwordSalt, Object extraData) throws Exception;

    /**
     * Gets user by his principal from the database.
     *
     * @param session                     {@link Session} instance, which is connected to the database.
     * @param storedProcedureProxyFactory Factory, which can be used to generate stored procedure proxy for convenience.
     * @param principal                   User's principal, which was used to login (e.g. ID or password).
     * @return Entity, which represents user.
     * @throws Exception
     */
    Object getUserByPrincipal(Session session, StoredProcedureProxyFactory storedProcedureProxyFactory,
                              Object principal) throws Exception;

    /**
     * Retrieves password hash hex string from the user entity.
     *
     * @param user Instance of user entity class, which was just fetched from the database.
     */
    String getUserPasswordHash(Object user);

    /**
     * Retrieves password salt hex string from the user entity.
     *
     * @param user Instance of user entity class, which was just fetched from the database.
     */
    String getUserPasswordSalt(Object user);
}
