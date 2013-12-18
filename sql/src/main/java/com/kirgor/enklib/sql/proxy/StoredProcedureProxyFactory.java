package com.kirgor.enklib.sql.proxy;

import com.kirgor.enklib.sql.Session;
import com.kirgor.enklib.sql.dialect.Dialect;

import java.util.HashMap;

/**
 * Creates instances of stored procedure proxies, while dealing
 * with creating classes for them and caching for future use.
 */
@SuppressWarnings("unchecked")
public class StoredProcedureProxyFactory {
    private Dialect dialect;
    private HashMap<Class, Class> proxyClassesByInterface = new HashMap<Class, Class>();

    /**
     * Creates factory, which will generate proxies for specified {@link Dialect}.
     *
     * @param dialect {@link Dialect}, which will be used in generated proxies.
     */
    public StoredProcedureProxyFactory(Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * Creates ready-to-use proxy instance for specified interface and {@link Session}.
     * Please, use Sessions with {@link Dialect} compatible to {@link Dialect} used in factory.
     *
     * @param interfaceClass Interface class, which contains proxy declaration.
     * @param session        {@link Session}, which generated proxy will wrap.
     * @param <T>            Type of interface class, which contains proxy declaration.
     * @throws Exception
     */
    public <T> T getProxy(Class<T> interfaceClass, Session session) throws Exception {
        Class proxyClass = proxyClassesByInterface.get(interfaceClass);
        if (proxyClass == null) {
            proxyClass = StoredProcedureProxy.createProxyClass(interfaceClass, dialect);
            proxyClassesByInterface.put(interfaceClass, proxyClass);
        }

        StoredProcedureProxy result = (StoredProcedureProxy) proxyClass.newInstance();
        result.setSession(session);
        return (T) result;
    }
}
