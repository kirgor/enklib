package com.kirgor.tools.ejb;

import com.kirgor.tools.sql.Session;
import com.kirgor.tools.sql.proxy.StoredProcedureProxyFactory;

public interface AuthenticationDAO {
    void addNewUser(Session session, StoredProcedureProxyFactory storedProcedureProxyFactory,
                    Object principal, String passwordHash, String passwordSalt, Object extraData) throws Exception;

    Object getUserByPrincipal(Session session, StoredProcedureProxyFactory storedProcedureProxyFactory,
                              Object principal) throws Exception;

    String getUserPasswordHash(Object user);

    String getUserPasswordSalt(Object user);
}
